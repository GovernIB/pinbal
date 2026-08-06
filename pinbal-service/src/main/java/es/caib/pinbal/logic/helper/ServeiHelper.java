/**
 * 
 */
package es.caib.pinbal.logic.helper;

import lombok.RequiredArgsConstructor;
import es.caib.pinbal.client.serveis.ServeiBasic;
import es.caib.pinbal.logic.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.pinbal.persist.entity.*;
import es.caib.pinbal.persist.repository.EntitatServeiRepository;
import es.caib.pinbal.persist.repository.EntitatUsuariRepository;
import es.caib.pinbal.persist.repository.ProcedimentServeiRepository;
import es.caib.pinbal.persist.repository.ServeiConfigRepository;
import es.caib.pinbal.persist.repository.ServeiRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.*;


/**
 * Helper per a operacions amb serveis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@RequiredArgsConstructor
@Component
public class ServeiHelper {

	private final ServeiConfigRepository serveiConfigRepository;
	private final ProcedimentServeiRepository procedimentServeiRepository;
	private final EntitatServeiRepository entitatServeiRepository;
	private final EntitatUsuariRepository entitatUsuariRepository;
	private final ServeiRepository serveiRepository;

	private final MutableAclService aclService;

	/**
	 * Obté la informació bàsica de tots els serveis configurats a PINBAL.
	 * <p>
	 * El resultat no depèn de l'usuari autenticat i per això es pot cachejar.
	 * Els objectes retornats formen part de la cache i no s'han de modificar:
	 * les dades que depenen de l'usuari s'han d'afegir sobre còpies.
	 *
	 * @return la llista de serveis.
	 */
	@Cacheable(value = "serveis")
	public List<ServeiBasic> findServeisClient() {
		return serveiRepository.findAllServeisClient();
	}

	/**
	 * Obté la informació bàsica dels serveis disponibles per a una entitat.
	 * <p>
	 * Els objectes retornats formen part de la cache i no s'han de modificar
	 * (veure {@link #findServeisClient()}).
	 *
	 * @param entitatCodi
	 *            codi de l'entitat.
	 * @return la llista de serveis de l'entitat.
	 */
	@Cacheable(value = "serveisEntitat", key = "#entitatCodi")
	public List<ServeiBasic> findServeisClientPerEntitat(String entitatCodi) {
		return serveiRepository.findServeisClientByEntitatCodi(entitatCodi);
	}

	/**
	 * Obté la informació bàsica dels serveis d'un procediment d'una entitat.
	 * <p>
	 * Els objectes retornats formen part de la cache i no s'han de modificar
	 * (veure {@link #findServeisClient()}).
	 *
	 * @param entitatCodi
	 *            codi de l'entitat.
	 * @param procedimentCodi
	 *            codi del procediment.
	 * @return la llista de serveis del procediment.
	 */
	@Cacheable(value = "serveisProcediment", key = "#entitatCodi + ':' + #procedimentCodi")
	public List<ServeiBasic> findServeisClientPerProcediment(String entitatCodi, String procedimentCodi) {
		return serveiRepository.findServeisClientByProcedimentCodi(procedimentCodi);
	}

	public boolean isServeiPermesPerUsuari(
			Entitat entitat,
			Procediment procediment,
			String serveiCodi) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		List<String> permesos = findServeisPermesosPerUsuari(
				entitat.getId(),
				procediment.getCodi(),
				auth);
		boolean trobat = false;
		for (String servei: permesos) {
			if (servei.equals(serveiCodi)) {
				trobat = true;
				break;
			}
		}
		return trobat;
	}

	public List<String> findServeisPermesosPerUsuari(
			Long entitatId,
			String procedimentCodi,
			Authentication auth) {
		// Valida si l'usuari és delegat o aplicació i si està actiu per l'entitat.
		EntitatUsuari entitatUsuari = entitatUsuariRepository.findByEntitatIdAndUsuariCodi(
				entitatId,
				auth.getName());
		if (entitatUsuari != null && (entitatUsuari.isDelegat() || entitatUsuari.isAplicacio()) && entitatUsuari.isActiu()) {
			List<ProcedimentServei> pss = procedimentServeiRepository.findActiusByEntitatId(entitatId);
			// Si hi ha un procedimentCodi es filtren els ProcedimentServei
			// amb aquest codi de procediment
			if (procedimentCodi != null) {
				Iterator<ProcedimentServei> it = pss.iterator();
				while (it.hasNext()) {
					ProcedimentServei ps = it.next();
					if (!ps.getProcediment().getCodi().equals(procedimentCodi))
						it.remove();
				}
			}
			// Filtra les combinacions Procediment-Servei a les quals 
			// l'usuari te permisos per accedir.
			PermisosHelper.filterGrantedAll(
					pss,
					new ObjectIdentifierExtractor<ProcedimentServei>() {
						public Long getObjectIdentifier(ProcedimentServei object) {
							return object.getId();
						}
					},
					ProcedimentServei.class,
					new Permission[] {BasePermission.READ},
					aclService,
					auth);
			// Obté tots els serveis permesos evitant duplicats.
			List<EntitatServei> serveisDisponiblesEntitat = entitatServeiRepository.findByEntitatId(entitatId);
			Set<String> serveis = new HashSet<String>();
			for (ProcedimentServei ps: pss) {
				for (EntitatServei entitatServei: serveisDisponiblesEntitat) {
					if (entitatServei.getServei().equals(ps.getServei())) {
						serveis.add(ps.getServei());
						break;
					}
				}
			}
			// Dels serveis resultants es filtren els serveis als quals
			// l'usuari te accés segons el rol configurat al ServeiConfig.
			List<ServeiConfig> serveiConfigs = new ArrayList<ServeiConfig>();
			for (String servei: serveis) {
				ServeiConfig serveiConfig = serveiConfigRepository.findByServei(servei);
				if (serveiConfig != null && serveiConfig.isActiu())
					serveiConfigs.add(serveiConfig);
			}
			PermisosHelper.filterGrantedAll(
					serveiConfigs,
					new ObjectIdentifierExtractor<ServeiConfig>() {
						public Long getObjectIdentifier(ServeiConfig object) {
							return object.getId();
						}
					},
					ServeiConfig.class,
					new Permission[] {BasePermission.READ},
					aclService,
					auth);
			// Omple la resposta amb els serveis resultants.
			List<String> resposta = new ArrayList<String>();
			for (ServeiConfig serveiConfig: serveiConfigs) {
				resposta.add(serveiConfig.getServei());
			}
			// Afegeix els serveis que no tenen restringit l'accés
			// per Rol al ServeiConfig
			for (String servei: serveis) {
				ServeiConfig serveiConfig = serveiConfigRepository.findByServei(servei);
				if ((serveiConfig == null || serveiConfig.getRoleName() == null || serveiConfig.getRoleName().isEmpty())) {
					boolean serveiActive = true;
					if (serveiConfig != null && !serveiConfig.isActiu()) {
						serveiActive = false;
					}
					if (serveiActive) {
						resposta.add(servei);
					}
				}
					
			}
			return resposta;
		} else {
			return new ArrayList<String>();
		}
	}

}
