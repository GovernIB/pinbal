package es.caib.pinbal.core.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.pinbal.core.dto.OrganGestorDto;
import es.caib.pinbal.core.dto.OrganGestorEstatEnumDto;
import es.caib.pinbal.core.helper.DtoMappingHelper;
import es.caib.pinbal.core.helper.PluginOrganGestorHelper;
import es.caib.pinbal.core.model.Entitat;
import es.caib.pinbal.core.model.OrganGestor;
import es.caib.pinbal.core.repository.EntitatRepository;
import es.caib.pinbal.core.repository.OrganGestorRepository;
import es.caib.pinbal.plugin.unitat.NodeDir3;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrganGestorServiceImpl implements OrganGestorService {

	@Resource
	private DtoMappingHelper dtoMappingHelper;
	@Resource
	private OrganGestorRepository organGestorRepository;
	@Resource
	private EntitatRepository entitatRepository;

	@Autowired
	private PluginOrganGestorHelper pluginOrganGestorHelper;

	@Transactional(readOnly = true)
	public List<OrganGestorDto> findAll() {
		log.debug("Consulta de tots els òrgans gestors");
		List<OrganGestor> organs = organGestorRepository.findAll();
		return dtoMappingHelper.convertirList(organs, OrganGestorDto.class);
	}

	@Transactional(readOnly = true)
	public OrganGestorDto findItem(Long id) {
		log.debug("Consulta d'un òrgan gestor (id=" + id + ")");
		OrganGestor organGestor = organGestorRepository.findOne(id);
		OrganGestorDto resposta = dtoMappingHelper.convertir(organGestor, OrganGestorDto.class);
		return resposta;
	}

	@Transactional(readOnly = true)
	public List<OrganGestorDto> findByEntitat(Long entitatId) {
		log.debug("Consulta dels òrgans d'una entitat (entitatId=" + entitatId + ")");
		Entitat entitat = entitatRepository.findOne(entitatId);
		List<OrganGestor> organs = entitat.getOrganGestors();
		return dtoMappingHelper.convertirList(organs, OrganGestorDto.class);
	}

	@Transactional(readOnly = true)
	public List<OrganGestorDto> findByEntitatAmbFiltre(Long entitatId, String filtre) {
		log.debug("Consulta dels òrgans d'una entitat (entitatId=" + entitatId + ") amb codi o nom (" + filtre + ")");
		List<OrganGestor> organs = organGestorRepository.findByEntitatAndCodiNom(entitatId, filtre);
		return dtoMappingHelper.convertirList(organs, OrganGestorDto.class);
	}

	@Override
	@Transactional
	public boolean syncDir3OrgansGestors(Long entitatId) throws Exception {
		log.debug("Sincronització dels òrgans d'una entitat (entitatId=" + entitatId + ")");
		Entitat entitat = entitatRepository.findOne(entitatId);
		if (entitat.getUnitatArrel() == null || entitat.getUnitatArrel().isEmpty()) {
			throw new Exception("L'entitat actual no té cap codi DIR3 associat");
		}
		List<OrganGestor> organismesDIR3 = new ArrayList<OrganGestor>();
		List<OrganGestorDto> organismes = findOrganismesArbre(entitat.getUnitatArrel());
		for (OrganGestorDto o : organismes) {
			OrganGestor organDB = organGestorRepository.findByCodiAndEntitat(o.getCodi(), entitat);
			if (organDB == null) { // create it
				organDB = new OrganGestor();
				organDB.setCodi(o.getCodi());
				organDB.setEntitat(entitat);
				organDB.setNom(o.getNom());
				organDB.setPare(organGestorRepository.findByCodiAndEntitat(o.getPareCodi(), entitat));
				organDB.setActiu(true);
				organGestorRepository.save(organDB);
			} else { // update it
				organDB.setNom(o.getNom());
				organDB.setActiu(true);
				organDB.setPare(organGestorRepository.findByCodiAndEntitat(o.getPareCodi(), entitat));
			}
			organismesDIR3.add(organDB);
		}
		// Processam els organs gestors que ja no estan a dir3 i tenen instancies a la
		// bbdd
		List<OrganGestor> organismesNotInDIR3 = entitat.getOrganGestors();
		organismesNotInDIR3.removeAll(organismesDIR3);
		for (OrganGestor o : organismesNotInDIR3) {
			o.setActiu(false);
		}
		return true;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<OrganGestorDto> findPageOrgansGestorsAmbFiltrePaginat(
			Long entitatId,
			String filtreCodi, 
			String filtreNom, 
			OrganGestorEstatEnumDto filtreEstat, 
			Pageable pageable) {
		log.debug("Consulta pafinada i amb filtre dels òrgans d'una entitat (" +
				"entitatId=" + entitatId + ", " +
				"filtreCodi=" + filtreCodi + ", " +
				"filtreNom=" + filtreNom + ", " +
				"filtreEstat=" + filtreEstat + ", " +
				"pageable=" + pageable + ")");
		Entitat entitat = entitatRepository.findOne(entitatId);
		Page<OrganGestor> organs = organGestorRepository.findByEntitatAndFiltre(
				entitat,
				filtreCodi == null || filtreCodi.length() == 0,
				filtreCodi,
				filtreNom == null || filtreNom.length() == 0,
				filtreNom,
				filtreEstat == null,
				filtreEstat == OrganGestorEstatEnumDto.VIGENT ? true : false,
				pageable);
		return dtoMappingHelper.pageEntities2pageDto(organs, OrganGestorDto.class, pageable);
	}

	private List<OrganGestorDto> findOrganismesArbre(String codiDir3) throws Exception {
		if (codiDir3 == null || codiDir3.isEmpty()) {
			throw new Exception("Codi Dir3 incorrecte");
		}
		List<OrganGestorDto> organismes = new ArrayList<OrganGestorDto>();
		Map<String, NodeDir3> organigramaDir3 = pluginOrganGestorHelper.getOrganigramaOrganGestor(codiDir3);
		if (organigramaDir3 != null) {
			NodeDir3 arrel = organigramaDir3.get(codiDir3);
			OrganGestorDto organisme = new OrganGestorDto();
			organisme.setCodi(arrel.getCodi());
			organisme.setNom(arrel.getDenominacio());
			organisme.setPareCodi(null);
			organismes.add(organisme);
			findOrganismesFills(arrel, organismes);
		}
		return organismes;
	}

	private void findOrganismesFills(NodeDir3 root, List<OrganGestorDto> organismes) {
		for (NodeDir3 fill : root.getFills()) {
			OrganGestorDto organisme = new OrganGestorDto();
			organisme.setCodi(fill.getCodi());
			organisme.setNom(fill.getDenominacio());
			organisme.setPareCodi(root.getCodi());
			organismes.add(organisme);
			findOrganismesFills(fill, organismes);
		}
	}

}
