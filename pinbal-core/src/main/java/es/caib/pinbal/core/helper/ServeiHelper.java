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

import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;

import es.caib.pinbal.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.pinbal.core.model.EntitatServei;
import es.caib.pinbal.core.model.ProcedimentServei;
import es.caib.pinbal.core.model.ServeiConfig;
import es.caib.pinbal.core.repository.EntitatServeiRepository;
import es.caib.pinbal.core.repository.ProcedimentServeiRepository;
import es.caib.pinbal.core.repository.ServeiConfigRepository;


/**
 * Helper per a operacions amb serveis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ServeiHelper {

	@Resource
	private ServeiConfigRepository serveiConfigRepository;
	@Resource
	private ProcedimentServeiRepository procedimentServeiRepository;
	@Resource
	private EntitatServeiRepository entitatServeiRepository;

	@Resource
	private MutableAclService aclService;



	public List<String> findServeisPermesosPerUsuari(
			Long entitatId,
			String procedimentCodi,
			Authentication auth) {
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
			if (serveiConfig != null)
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
			if (serveiConfig == null || serveiConfig.getRoleName() == null || serveiConfig.getRoleName().isEmpty())
				resposta.add(servei);
		}
		return resposta;
	}

}