/**
 * 
 */
package es.caib.pinbal.core.helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import es.caib.pinbal.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.pinbal.core.model.Entitat;
import es.caib.pinbal.core.model.EntitatServei;
import es.caib.pinbal.core.model.EntitatUsuari;
import es.caib.pinbal.core.model.Procediment;
import es.caib.pinbal.core.model.ProcedimentServei;
import es.caib.pinbal.core.model.ServeiConfig;
import es.caib.pinbal.core.repository.EntitatServeiRepository;
import es.caib.pinbal.core.repository.EntitatUsuariRepository;
import es.caib.pinbal.core.repository.ProcedimentServeiRepository;
import es.caib.pinbal.core.repository.ServeiConfigRepository;


/**
 * Helper per a operacions amb serveis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class ServeiHelper {

	@Resource
	private ServeiConfigRepository serveiConfigRepository;
	@Resource
	private ProcedimentServeiRepository procedimentServeiRepository;
	@Resource
	private EntitatServeiRepository entitatServeiRepository;
	@Autowired
	private EntitatUsuariRepository entitatUsuariRepository;

	@Resource
	private MutableAclService aclService;

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
		// Valida si l'usuari és delegat i si està actiu per l'entitat
		EntitatUsuari entitatUsuari = entitatUsuariRepository.findByEntitatIdAndUsuariCodi(
				entitatId,
				auth.getName());
		if (entitatUsuari != null && entitatUsuari.isDelegat() && entitatUsuari.isActiu()) {
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
