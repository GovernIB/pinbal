/**
 * 
 */
package es.caib.pinbal.core.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.pinbal.core.dto.FiltreActiuEnumDto;
import es.caib.pinbal.core.dto.InformeProcedimentDto;
import es.caib.pinbal.core.dto.PaginacioAmbOrdreDto;
import es.caib.pinbal.core.dto.ProcedimentDto;
import es.caib.pinbal.core.helper.DtoMappingHelper;
import es.caib.pinbal.core.helper.PaginacioHelper;
import es.caib.pinbal.core.helper.PermisosHelper;
import es.caib.pinbal.core.helper.PermisosHelper.ObjectIdentifierExtractor;
import es.caib.pinbal.core.helper.UsuariHelper;
import es.caib.pinbal.core.model.Entitat;
import es.caib.pinbal.core.model.EntitatServei;
import es.caib.pinbal.core.model.EntitatUsuari;
import es.caib.pinbal.core.model.OrganGestor;
import es.caib.pinbal.core.model.Procediment;
import es.caib.pinbal.core.model.ProcedimentServei;
import es.caib.pinbal.core.repository.EntitatRepository;
import es.caib.pinbal.core.repository.EntitatServeiRepository;
import es.caib.pinbal.core.repository.EntitatUsuariRepository;
import es.caib.pinbal.core.repository.OrganGestorRepository;
import es.caib.pinbal.core.repository.ProcedimentRepository;
import es.caib.pinbal.core.repository.ProcedimentServeiRepository;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.EntitatUsuariNotFoundException;
import es.caib.pinbal.core.service.exception.ProcedimentNotFoundException;
import es.caib.pinbal.core.service.exception.ProcedimentServeiNotFoundException;
import es.caib.pinbal.core.service.exception.ServeiNotFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementació de ProcedimentService que es comunica amb la base de dades emprant
 * un repositori proporcionat per spring-data-jpa.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Service
public class ProcedimentServiceImpl implements ProcedimentService {

	@Resource
	private ProcedimentRepository procedimentRepository;
	@Resource
	private EntitatRepository entitatRepository;
	@Resource
	private EntitatServeiRepository entitatServeiRepository;
	@Resource
	private EntitatUsuariRepository entitatUsuariRepository;
	@Resource
	private ProcedimentServeiRepository procedimentServeiRepository;
	@Resource
	private OrganGestorRepository organGestorRepository;
	@Resource
	private DtoMappingHelper dtoMappingHelper;

	@Resource
	private MutableAclService aclService;
	@Resource
	private UsuariHelper usuariHelper;

	@Resource
	private PaginacioHelper paginacioHelper;
	

	@Transactional(rollbackFor = EntitatNotFoundException.class)
	@Override
	public ProcedimentDto create(ProcedimentDto creat) throws EntitatNotFoundException {
		log.debug("Creant un nou procediment: " + creat);
		Entitat entitat = entitatRepository.findOne(creat.getEntitatId());
		if (entitat == null)
			throw new EntitatNotFoundException();
		
		Procediment procediment = Procediment.getBuilder(
				entitat,
				creat.getCodi(),
				creat.getNom(),
				creat.getDepartament(),
				organGestorRepository.getOne(creat.getOrganGestor().getId()),
				creat.getCodiSia(),
				creat.getValorCampAutomatizado(),
				creat.getValorCampClaseTramite()).build();

		return dtoMappingHelper.getMapperFacade().map(
				procedimentRepository.save(procediment),
				ProcedimentDto.class);
	}

	@Transactional(rollbackFor = ProcedimentNotFoundException.class)
	@Override
	public ProcedimentDto delete(Long procedimentId) throws ProcedimentNotFoundException {
		log.debug("Esborrant procediment (id= " + procedimentId + ")");
		Procediment esborrat = procedimentRepository.findOne(procedimentId);
		if (esborrat == null) {
			log.debug("No s'ha trobat cap procediment (id= " + procedimentId + ")");
			throw new ProcedimentNotFoundException();
		}
		procedimentRepository.delete(esborrat);
		return dtoMappingHelper.getMapperFacade().map(
				esborrat,
				ProcedimentDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public List<ProcedimentDto> findAmbEntitat(Long entitatId) throws EntitatNotFoundException {
		log.debug("Cercant els procediments de l'entitat (id=" + entitatId + ")");
		Entitat entitat = entitatRepository.findOne(entitatId);
		if (entitat == null)
			throw new EntitatNotFoundException();
		return dtoMappingHelper.getMapperFacade().mapAsList(
				procedimentRepository.findByEntitatOrderByNomAsc(entitat),
				ProcedimentDto.class);
	}
	
	@Transactional(readOnly = true)
	@Override
	public List<ProcedimentDto> findAmbEntitat(Long entitatId, String filtre) throws EntitatNotFoundException {
		log.debug("Cercant els procediments de l'entitat (id=" + entitatId + ") y filtre: " + filtre);
		Entitat entitat = entitatRepository.findOne(entitatId);
		if (entitat == null)
			throw new EntitatNotFoundException();
		return dtoMappingHelper.getMapperFacade().mapAsList(
				procedimentRepository.findByEntitatAndFiltreOrderByNomAsc(entitat, 
						filtre == null || filtre.isEmpty(), 
						filtre),
				ProcedimentDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public Page<ProcedimentDto> findAmbFiltrePaginat(
			Long entitatId,
			String codi,
			String nom,
			String departament,
			Long organGestorId,
			String codiSia,
			FiltreActiuEnumDto actiu,
			PaginacioAmbOrdreDto paginacioParams) throws EntitatNotFoundException {
		log.debug("Consulta de procediments segons filtre ("
				+ "entitatId=" + entitatId + ", "
				+ "codi=" + codi + ", "
				+ "nom=" + nom + ", "
				+ "departament=" + departament + ")");
		Entitat entitat = entitatRepository.findOne(entitatId);
		if (entitat == null)
			throw new EntitatNotFoundException();
		
		boolean esActiu = false;
		if (actiu != null) {
			if (actiu == FiltreActiuEnumDto.ACTIU) {
				esActiu = true;
			} else {
				esActiu = false;
			}
		}
		
		OrganGestor organGestor = organGestorId == null ? null : organGestorRepository.findOne(organGestorId);

		Map<String, String[]> ordenacioMap = new HashMap<String, String[]>();
		ordenacioMap.put("organGestorStr", new String[] {"organGestor.codi"});
		
		Page<Procediment> paginaProcediments = procedimentRepository.findByFiltre(
					entitat,
					codi == null || codi.length() == 0,
					codi,
					nom == null || nom.length() == 0,
					nom,
					departament == null || departament.length() == 0,
					departament,
					organGestor == null,
					organGestor,
					codiSia == null || codiSia.isEmpty(),
					codiSia,
					actiu == null,
					esActiu,
					paginacioHelper.toSpringDataPageable(paginacioParams, ordenacioMap));

	
		return dtoMappingHelper.pageEntities2pageDto(
				paginaProcediments,
				ProcedimentDto.class,
				paginacioHelper.toSpringDataPageable(paginacioParams, ordenacioMap));
	}

	@Transactional(readOnly = true)
	@Override
	public ProcedimentDto findAmbEntitatICodi(Long entitatId, String codi) throws EntitatNotFoundException {
		log.debug("Cercant els procediment de l'entitat (id=" + entitatId + ") amb codi (codi=" + codi + ")");
		Entitat entitat = entitatRepository.findOne(entitatId);
		if (entitat == null)
			throw new EntitatNotFoundException();
		return dtoMappingHelper.getMapperFacade().map(
				procedimentRepository.findByEntitatAndCodi(entitat, codi),
				ProcedimentDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public ProcedimentDto findById(Long id) {
		log.debug("Cercant el procediment (id= " + id + ")");
		return dtoMappingHelper.getMapperFacade().map(
				procedimentRepository.findOne(id),
				ProcedimentDto.class);
	}

	@Transactional(rollbackFor = ProcedimentNotFoundException.class)
	@Override
	public ProcedimentDto update(ProcedimentDto modificat) throws ProcedimentNotFoundException {
		log.debug("Actualitzant el procediment (id= " + modificat.getId() + ") amb la informació: " + modificat);
		Procediment procediment = procedimentRepository.findOne(modificat.getId());
		if (procediment == null) {
			log.debug("No s'ha trobat el procediment (id= " + modificat.getId() + ")");
			throw new ProcedimentNotFoundException();
		}
		procediment.update(
				modificat.getCodi(),
				modificat.getNom(),
				modificat.getDepartament(),
				organGestorRepository.getOne(modificat.getOrganGestor().getId()),
				modificat.getCodiSia(),
				modificat.getValorCampAutomatizado(),
				modificat.getValorCampClaseTramite());
		
		return dtoMappingHelper.getMapperFacade().map(
				procediment,
				ProcedimentDto.class);
	}

	@Transactional(rollbackFor = ProcedimentNotFoundException.class)
	@Override
	public ProcedimentDto updateActiu(Long id, boolean actiu) throws ProcedimentNotFoundException {
		log.debug("Actualitzant estat actiu del procediment (id= " + id + ", actiu=" + actiu + ")");
		Procediment procediment = procedimentRepository.findOne(id);
		if (procediment == null) {
			log.debug("No s'ha trobat el procediment (id= " + id + ")");
			throw new ProcedimentNotFoundException();
		}
		procediment.updateActiu(actiu);
		return dtoMappingHelper.getMapperFacade().map(
				procediment,
				ProcedimentDto.class);
	}

	@Transactional(rollbackFor = {ProcedimentNotFoundException.class, ServeiNotFoundException.class})
	@Override
	public void serveiEnable(
			Long id,
			String serveiCodi) throws ProcedimentNotFoundException, ServeiNotFoundException {
		log.debug("Activant el servei (codi=" + serveiCodi + ") al procediment (id= " + id + ")");
		Procediment procediment = procedimentRepository.findOne(id);
		if (procediment == null) {
			log.debug("No s'ha trobat cap procediment (id= " + id + ")");
			throw new ProcedimentNotFoundException();
		}
		EntitatServei entitatServei = entitatServeiRepository.findByEntitatIdAndServei(
				procediment.getEntitat().getId(),
				serveiCodi);
		if (entitatServei == null) {
			log.debug("El servei (codi=" + serveiCodi + ") no està actiu per a l'entitat (id= " + procediment.getEntitat().getId() + ")");
			throw new ServeiNotFoundException();
		}
		ProcedimentServei procedimentServei = procedimentServeiRepository.findByProcedimentIdAndServei(id, serveiCodi);
		if (procedimentServei != null) {
			procedimentServei.updateActiu(true);
		} else {
			procedimentServei = ProcedimentServei.getBuilder(
					procediment,
					serveiCodi).build();
			procedimentServeiRepository.save(procedimentServei);
		}
	}
	
	@Transactional(rollbackFor = {ProcedimentNotFoundException.class, ServeiNotFoundException.class})
	@Override
	public boolean putProcedimentCodi(
			Long procedimentId,
			String serveiCodi,
			String procedimentCodi) throws ProcedimentNotFoundException, ServeiNotFoundException {
		log.debug("Afegint codi adicional (codi=" + serveiCodi + ") al servei (serveiCodi= " + serveiCodi + ") al procediment (procedimentId= " + procedimentId + ")");
		Procediment procediment = procedimentRepository.findOne(procedimentId);
		if (procediment == null) {
			log.debug("No s'ha trobat cap procediment (id= " + procedimentId + ")");
			throw new ProcedimentNotFoundException();
		}
		EntitatServei entitatServei = entitatServeiRepository.findByEntitatIdAndServei(
				procediment.getEntitat().getId(),
				serveiCodi);
		if (entitatServei == null) {
			log.debug("El servei (codi=" + serveiCodi + ") no està actiu per a l'entitat (id= " + procediment.getEntitat().getId() + ")");
			throw new ServeiNotFoundException();
		}
		ProcedimentServei procedimentServei = procedimentServeiRepository.findByProcedimentIdAndServei(procedimentId, serveiCodi);
		if (procedimentServei != null) {
			procedimentServei.updateProcedimentCodi(procedimentCodi);
		} else {
			procedimentServei = ProcedimentServei.getBuilder(
					procediment,
					serveiCodi).build();
			procedimentServei.updateProcedimentCodi(procedimentCodi);
			procedimentServeiRepository.save(procedimentServei);
		}
		
		return true;
	}

	@Transactional(rollbackFor = {ProcedimentNotFoundException.class, ProcedimentServeiNotFoundException.class})
	@Override
	public void serveiDisable(
			Long id,
			String serveiCodi) throws ProcedimentNotFoundException, ProcedimentServeiNotFoundException {
		log.debug("Desactivant el servei (codi=" + serveiCodi + ") del procediment (id= " + id + ")");
		Procediment procediment = procedimentRepository.findOne(id);
		if (procediment == null) {
			log.debug("No s'ha trobat cap procediment (id= " + id + ")");
			throw new ProcedimentNotFoundException();
		}
		ProcedimentServei procedimentServei = procedimentServeiRepository.findByProcedimentIdAndServei(id, serveiCodi);
		if (procedimentServei == null) {
			log.debug("El procediment (id= " + id + ") no té cap servei (codi=" + serveiCodi + ")");
			throw new ProcedimentServeiNotFoundException();
		}
		procedimentServei.updateActiu(false);
		
		// Esborrar permisos assignats al servei
		PermisosHelper.revocarPermisosServei(
				ProcedimentServei.class,
				procedimentServei.getId(),
				aclService);
//				BasePermission.READ);
	}

	@Transactional(rollbackFor = {ProcedimentNotFoundException.class, ProcedimentServeiNotFoundException.class, EntitatUsuariNotFoundException.class})
	@Override
	public void serveiPermisAllow(
			Long id,
			String serveiCodi,
			String usuariCodi) throws ProcedimentNotFoundException, ProcedimentServeiNotFoundException, EntitatUsuariNotFoundException {
		log.debug("Donant permis d'accés a un servei d'un procediment(" +
				"id=" + id + ", " +
				"serveiCodi=" + serveiCodi + ", " +
				"usuariCodi=" + usuariCodi + ")");
		Procediment procediment = procedimentRepository.findOne(id);
		if (procediment == null) {
			log.debug("No s'ha trobat cap procediment (id= " + id + ")");
			throw new ProcedimentNotFoundException();
		}
		ProcedimentServei procedimentServei = procedimentServeiRepository.findByProcedimentIdAndServei(id, serveiCodi);
		if (procedimentServei == null) {
			log.debug("El procediment (id= " + id + ") no té cap servei (codi=" + serveiCodi + ")");
			throw new ProcedimentServeiNotFoundException();
		}
		EntitatUsuari entitatUsuari = entitatUsuariRepository.findByEntitatIdAndUsuariCodi(
				procediment.getEntitat().getId(),
				usuariCodi);
		if (entitatUsuari == null) {
			log.debug("L'entitat (id=" + procediment.getEntitat().getId() + ") no té cap usuari (codi=" + usuariCodi + ")");
			throw new EntitatUsuariNotFoundException();
		}
		PermisosHelper.assignarPermisUsuari(
				usuariCodi,
				ProcedimentServei.class,
				procedimentServei.getId(),
				BasePermission.READ,
				aclService);
	}

	@Transactional(rollbackFor = {ProcedimentNotFoundException.class, ProcedimentServeiNotFoundException.class, EntitatUsuariNotFoundException.class})
	@Override
	public void serveiPermisDeny(
			Long id,
			String serveiCodi,
			String usuariCodi) throws ProcedimentNotFoundException, ProcedimentServeiNotFoundException, EntitatUsuariNotFoundException {
		log.debug("Esborrant servei (codi=" + serveiCodi + ") del procediment (id= " + id + ")");
		Procediment procediment = procedimentRepository.findOne(id);
		if (procediment == null) {
			log.debug("No s'ha trobat cap procediment (id= " + id + ")");
			throw new ProcedimentNotFoundException();
		}
		ProcedimentServei procedimentServei = procedimentServeiRepository.findByProcedimentIdAndServei(id, serveiCodi);
		if (procedimentServei == null) {
			log.debug("El procediment (id= " + id + ") no té cap servei (codi=" + serveiCodi + ")");
			throw new ProcedimentServeiNotFoundException();
		}
		EntitatUsuari entitatUsuari = entitatUsuariRepository.findByEntitatIdAndUsuariCodi(
				procediment.getEntitat().getId(),
				usuariCodi);
		if (entitatUsuari == null) {
			log.debug("L'entitat (id=" + procediment.getEntitat().getId() + ") no té cap usuari (codi=" + usuariCodi + ")");
			throw new EntitatUsuariNotFoundException();
		}
		PermisosHelper.revocarPermisUsuari(
				usuariCodi,
				ProcedimentServei.class,
				procedimentServei.getId(),
				BasePermission.READ,
				aclService);
	}
	
	@Transactional(rollbackFor = {ProcedimentNotFoundException.class, ProcedimentServeiNotFoundException.class, EntitatUsuariNotFoundException.class})
	@Override
	public void serveiPermisDenyAll(
			String usuariCodi,
			Long entitatId) throws EntitatUsuariNotFoundException {
		EntitatUsuari entitatUsuari = entitatUsuariRepository.findByEntitatIdAndUsuariCodi(
				entitatId,
				usuariCodi);
		if (entitatUsuari == null) {
			log.debug("L'entitat (id=" + entitatId + ") no té cap usuari (codi=" + usuariCodi + ")");
			throw new EntitatUsuariNotFoundException();
		}
		List<ProcedimentServei> procedimentServeis = procedimentServeiRepository.findByEntitatId(entitatId);
		PermisosHelper.filterGrantedAll(
				procedimentServeis,
				new ObjectIdentifierExtractor<ProcedimentServei>() {
					public Long getObjectIdentifier(ProcedimentServei object) {
						return object.getId();
					}
				},
				ProcedimentServei.class,
				new Permission[] {BasePermission.READ},
				aclService,
				usuariHelper.generarUsuariAutenticat(
						usuariCodi,
						false));
		if (!procedimentServeis.isEmpty()) {
			for (ProcedimentServei procedimentServei: procedimentServeis) {
				PermisosHelper.revocarPermisUsuari(
						usuariCodi, 
						ProcedimentServei.class, 
						procedimentServei.getId(), 
						BasePermission.READ, 
						aclService);
			}
		}
	}

	@Transactional(readOnly = true)
	@Override
	public List<String> findUsuarisAmbPermisPerServei(Long id, String serveiCodi) throws ProcedimentNotFoundException, ProcedimentServeiNotFoundException {
		log.debug("Cercant usuaris amb permisos per a accedir al servei (codi=" + serveiCodi + ") del procediment (id= " + id + ")");
		Procediment procediment = procedimentRepository.findOne(id);
		if (procediment == null) {
			log.debug("No s'ha trobat cap procediment (id= " + id + ")");
			throw new ProcedimentNotFoundException();
		}
		ProcedimentServei procedimentServei = procedimentServeiRepository.findByProcedimentIdAndServei(id, serveiCodi);
		if (procedimentServei == null) {
			log.debug("El procediment (id= " + id + ") no té cap servei (codi=" + serveiCodi + ")");
			throw new ProcedimentServeiNotFoundException();
		}
		List<String> resposta = new ArrayList<String>();
		List<AccessControlEntry> aces = PermisosHelper.getAclSids(
				ProcedimentServei.class,
				procedimentServei.getId(),
				aclService);
		if (aces != null) {
			for (AccessControlEntry ace: aces)
				resposta.add(((PrincipalSid)ace.getSid()).getPrincipal());
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public List<ProcedimentDto> findAmbEntitatPerDelegat(
			Long entitatId) throws EntitatNotFoundException {
		log.debug("Cercant procediments de delegat per l'entitat (id=" + entitatId + ")");
		Entitat entitat = entitatRepository.findOne(entitatId);
		if (entitat == null)
			throw new EntitatNotFoundException();
		// Filtra les combinacions Procediment-Servei a les quals 
		// l'usuari te permisos per accedir.
		List<ProcedimentServei> pss = procedimentServeiRepository.findActiusByEntitatId(entitatId);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
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
		// Omple la resposta amb tots els procediments permesos
		// evitant duplicats.
		List<Procediment> procediments = new ArrayList<Procediment>();
		for (ProcedimentServei ps : pss) {
			if (!procediments.contains(ps.getProcediment()))
				procediments.add(ps.getProcediment());
		}
		return dtoMappingHelper.getMapperFacade().mapAsList(
				procediments,
				ProcedimentDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public List<ProcedimentDto> findActiusAmbEntitatIServeiCodi(
			Long entitatId,
			String serveiCodi) throws EntitatNotFoundException {
		log.debug("Cercant procediments actius per l'entitat (" +
				"entitatId=" + entitatId + ", " +
				"serveiCodi=" + serveiCodi + ")");
		Entitat entitat = entitatRepository.findOne(entitatId);
		if (entitat == null)
			throw new EntitatNotFoundException();
		List<ProcedimentServei> procedimentServeis = procedimentServeiRepository.findActiusByEntitatIdAndServei(
				entitatId,
				serveiCodi);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		PermisosHelper.filterGrantedAll(
				procedimentServeis,
				new ObjectIdentifierExtractor<ProcedimentServei>() {
					public Long getObjectIdentifier(ProcedimentServei object) {
						return object.getId();
					}
				},
				ProcedimentServei.class,
				new Permission[] {BasePermission.READ},
				aclService,
				auth);
		// Omple la resposta amb tots els procediments permesos
		// evitant duplicats.
		List<Procediment> procediments = new ArrayList<Procediment>();
		for (ProcedimentServei ps : procedimentServeis) {
			if (!procediments.contains(ps.getProcediment()))
				procediments.add(ps.getProcediment());
		}
		return dtoMappingHelper.getMapperFacade().mapAsList(
				procediments,
				ProcedimentDto.class);
	}
	
	@Transactional(readOnly = true)
	@Override
	public List<ProcedimentDto> findAmbServeiCodi(
			String serveiCodi) {
		log.debug("Cercant procediments amb el serveiCodi=" + serveiCodi);
		List<Procediment> procediments = procedimentRepository.findAllByServei(serveiCodi);
		return dtoMappingHelper.getMapperFacade().mapAsList(
				procediments,
				ProcedimentDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public List<InformeProcedimentDto> informeProcedimentsAgrupatsEntitatDepartament() {
		log.debug("Generant informe de procediments agrupats per entitat i departament");
		List<Procediment> procediments = procedimentRepository.findAllOrderByEntitatAndDepartament();
		Entitat entitatActual = null;
		String departamentActual = null;
		List<InformeProcedimentDto> resposta = new ArrayList<InformeProcedimentDto>();
		for (Procediment procediment: procediments) {
			if (entitatActual == null)
				entitatActual = procediment.getEntitat();
			if (departamentActual == null)
				departamentActual = procediment.getDepartament();
			InformeProcedimentDto informeProcediment = dtoMappingHelper.getMapperFacade().map(
					procediment,
					InformeProcedimentDto.class);
			resposta.add(informeProcediment);
		}
		return resposta;
	}

	/**
	 * Aquest setter només s'hauria d'emprar en les proves unitàries.
	 * @param personRepository
	 */
	public void setProcedimentRepository(ProcedimentRepository procedimentRepository) {
		this.procedimentRepository = procedimentRepository;
	}

	@Transactional(readOnly = true)
	@Override
	public List<ProcedimentDto> findAll() {
		log.debug("Cercant tots els procediments");
		return dtoMappingHelper.getMapperFacade().mapAsList(
				procedimentRepository.findAll(),
				ProcedimentDto.class);
	}
}
