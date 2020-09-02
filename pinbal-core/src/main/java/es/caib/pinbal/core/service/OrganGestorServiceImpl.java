package es.caib.pinbal.core.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.pinbal.core.dto.OrganGestorDto;
import es.caib.pinbal.core.helper.DtoMappingHelper;
import es.caib.pinbal.core.helper.PluginOrganGestorHelper;
import es.caib.pinbal.core.model.Entitat;
import es.caib.pinbal.core.model.OrganGestor;
import es.caib.pinbal.core.repository.EntitatRepository;
import es.caib.pinbal.core.repository.OrganGestorRepository;
import es.caib.pinbal.plugin.unitat.NodeDir3;

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
		List<OrganGestor> organs = organGestorRepository.findAll();
		return dtoMappingHelper.convertirList(organs, OrganGestorDto.class);
	}

	@Transactional(readOnly = true)
	public OrganGestorDto findItem(Long id) {
		OrganGestor organGestor = organGestorRepository.findOne(id);
		OrganGestorDto resposta = dtoMappingHelper.convertir(organGestor, OrganGestorDto.class);
		return resposta;
	}

	@Transactional(readOnly = true)
	public List<OrganGestorDto> findByEntitat(Long entitatId) {
		Entitat entitat = entitatRepository.findOne(entitatId);
		List<OrganGestor> organs = entitat.getOrganGestors();
		return dtoMappingHelper.convertirList(organs, OrganGestorDto.class);
	}

	@Override
	@Transactional
	public boolean syncDir3OrgansGestors(Long entitatId) throws Exception {
		Entitat entitat = entitatRepository.findOne(entitatId);
		if (entitat.getUnitatArrel() == null || entitat.getUnitatArrel().isEmpty())
		{
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
				organGestorRepository.save(organDB);
				
			} else { // update it
				organDB.setNom(o.getNom());
				organDB.setPare(organGestorRepository.findByCodiAndEntitat(o.getPareCodi(), entitat));
				organGestorRepository.flush();
				
			}
			
			organismesDIR3.add(organDB);
		}
		// Processam els organs gestors que ja no estan a dir3 i tenen instancies a la
		// bbdd
		List<OrganGestor> organismesNotInDIR3 = entitat.getOrganGestors();
		organismesNotInDIR3.removeAll(organismesDIR3);
		for (OrganGestor o : organismesNotInDIR3) {
			if (o.getProcediments() == null || o.getProcediments().size() == 0) {
				organGestorRepository.delete(o.getId());
				System.out.println("REMOVED: " + o.getNom());
			} else {
				o.setActiu(false);
				organGestorRepository.flush();
			}
		}
		return true;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<OrganGestorDto> findPageOrgansGestorsAmbFiltrePaginat(
			Long entitatId,
			String filtre,
			Pageable pageable) {
		Entitat entitat = entitatRepository.findOne(entitatId);

		Page<OrganGestor> organs = organGestorRepository.findByEntitatAndFiltre(
				entitat,
				filtre == null,
				filtre,
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
	
	private static final Logger logger = LoggerFactory.getLogger(EntitatServiceImpl.class);

}
