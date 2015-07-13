/**
 * 
 */
package es.caib.pinbal.core.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.dto.PaginaLlistatDto;
import es.caib.pinbal.core.dto.PaginacioAmbOrdreDto;
import es.caib.pinbal.core.helper.DtoMappingHelper;
import es.caib.pinbal.core.helper.PaginacioHelper;
import es.caib.pinbal.core.model.Entitat;
import es.caib.pinbal.core.model.Entitat.EntitatTipus;
import es.caib.pinbal.core.model.EntitatServei;
import es.caib.pinbal.core.model.ServeiConfig;
import es.caib.pinbal.core.repository.EntitatRepository;
import es.caib.pinbal.core.repository.EntitatServeiRepository;
import es.caib.pinbal.core.repository.ServeiConfigRepository;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.EntitatServeiNotFoundException;
import es.caib.pinbal.core.service.exception.ServeiNotFoundException;
import es.caib.pinbal.scsp.ScspHelper;
import es.scsp.common.domain.Servicio;

/**
 * Implementació de EntitatService que es comunica amb la base de dades emprant
 * un repositori proporcionat per spring-data-jpa.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class EntitatServiceImpl implements EntitatService {

	@Resource
	private EntitatRepository entitatRepository;
	@Resource
	private EntitatServeiRepository entitatServeiRepository;
	@Resource
	private ServeiConfigRepository serveiConfigRepository;

	@Resource
	private DtoMappingHelper dtoMappingHelper;
	@Resource
	private ScspHelper scspHelper;



	@Transactional
	@Override
	public EntitatDto create(EntitatDto creada) {
		LOGGER.debug("Creant una nova entitat: " + creada);
		Entitat entitat = Entitat.getBuilder(
				creada.getCodi(),
				creada.getNom(),
				creada.getCif(),
				EntitatTipus.valueOf(creada.getTipus().toString())).build();
		scspHelper.organismoCesionarioSave(
				creada.getCif(),
				creada.getNom(),
				new Date(),
				null,
				!entitat.isActiva());
		return dtoMappingHelper.getMapperFacade().map(
				entitatRepository.save(entitat),
				EntitatDto.class);
	}

	@Transactional(rollbackFor = EntitatNotFoundException.class)
	@Override
	public EntitatDto delete(Long entitatId) throws EntitatNotFoundException {
		LOGGER.debug("Esborrant entitat (id=" + entitatId + ")");
        Entitat esborrada = entitatRepository.findOne(entitatId);
		if (esborrada == null) {
			LOGGER.debug("No s'ha trobat l'entitat (id=" + entitatId + ")");
			throw new EntitatNotFoundException();
		}
		scspHelper.organismoCesionarioDelete(esborrada.getCif());
		entitatRepository.delete(esborrada);
		return dtoMappingHelper.getMapperFacade().map(
				esborrada,
				EntitatDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public List<EntitatDto> findAll() {
		LOGGER.debug("Consulta de totes les entitats");
		return dtoMappingHelper.getMapperFacade().mapAsList(
				entitatRepository.findAll(
						new Sort(Sort.Direction.ASC, "nom")),
				EntitatDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	@SuppressWarnings("unchecked")
	public PaginaLlistatDto<EntitatDto> findAmbFiltrePaginat(
			String codi,
			String nom,
			PaginacioAmbOrdreDto paginacioAmbOrdre) {
		LOGGER.debug("Consulta d'entitats segons filtre (codi=" + codi + ", nom=" + nom + ")");
		Page<Entitat> paginaEntitats = entitatRepository.findByFiltre(
				codi == null || codi.length() == 0,
				codi,
				nom == null || nom.length() == 0,
				nom,
				PaginacioHelper.toSpringDataPageable(
						paginacioAmbOrdre,
						null));
		return PaginacioHelper.toPaginaLlistatDto(
				paginaEntitats,
				dtoMappingHelper,
				EntitatDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public EntitatDto findById(Long id) {
		LOGGER.debug("Consulta de l'entitat (id=" + id + ")");
		return dtoMappingHelper.getMapperFacade().map(
				entitatRepository.findOne(id),
				EntitatDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public EntitatDto findByCodi(String codi) {
		LOGGER.debug("Consulta de l'entitat (codi=" + codi + ")");
		return dtoMappingHelper.getMapperFacade().map(
				entitatRepository.findByCodi(codi),
				EntitatDto.class);
	}

	@Transactional(rollbackFor = EntitatNotFoundException.class)
	@Override
	public EntitatDto update(EntitatDto modificada) throws EntitatNotFoundException {
		LOGGER.debug("Actualitzant l'entitat (id=" + modificada.getId() + ") amb la informació: " + modificada);
		Entitat entitat = entitatRepository.findOne(modificada.getId());
		if (entitat == null) {
			LOGGER.debug("No s'ha trobat l'entitat (id=" + modificada.getId() + ")");
			throw new EntitatNotFoundException();
		}
		scspHelper.organismoCesionarioDelete(entitat.getCif());
		entitat.update(
				modificada.getCodi(),
				modificada.getNom(),
				modificada.getCif(),
				EntitatTipus.valueOf(modificada.getTipus().toString()));
		scspHelper.organismoCesionarioSave(
				entitat.getCif(),
				entitat.getNom(),
				entitat.getCreatedDate().toDate(),
				null,
				!modificada.isActiva());
		actualitzarServeisScspActiusEntitat(entitat);
		return dtoMappingHelper.getMapperFacade().map(
				entitat,
				EntitatDto.class);
	}

	@Transactional(rollbackFor = EntitatNotFoundException.class)
	@Override
	public EntitatDto updateActiva(Long id, boolean activa) throws EntitatNotFoundException {
		LOGGER.debug("Desactivant l'entitat (id=" + id + ")");
		Entitat entitat = entitatRepository.findOne(id);
		if (entitat == null) {
			LOGGER.debug("No s'ha trobat l'entitat (id=" + id + ")");
			throw new EntitatNotFoundException();
		}
		entitat.updateActiva(activa);
		scspHelper.organismoCesionarioSave(
				entitat.getCif(),
				entitat.getNom(),
				entitat.getCreatedDate().toDate(),
				null,
				!activa);
		return dtoMappingHelper.getMapperFacade().map(
				entitat,
				EntitatDto.class);
	}

	@Transactional(rollbackFor = {EntitatNotFoundException.class, ServeiNotFoundException.class})
	@Override
	public void addServei(Long id, String serveiCodi) throws EntitatNotFoundException, ServeiNotFoundException {
		LOGGER.debug("Afegint servei (codi=" + serveiCodi + ") a l'entitat (id=" + id + ")");
		Entitat entitat = entitatRepository.findOne(id);
		if (entitat == null) {
			LOGGER.debug("No s'ha trobat l'entitat (id=" + id + ")");
			throw new EntitatNotFoundException();
		}
		if (scspHelper.getServicio(serveiCodi) == null) {
			LOGGER.debug("No s'ha trobat el servei (codi=" + serveiCodi + ")");
			throw new ServeiNotFoundException();
		}
		EntitatServei entitatServei = EntitatServei.getBuilder(
				entitat,
				serveiCodi).build();
		entitatServeiRepository.save(entitatServei);
		actualitzarServeisScspActiusEntitat(entitat);
	}

	@Transactional(rollbackFor = {EntitatNotFoundException.class, ServeiNotFoundException.class})
	@Override
	public void removeServei(Long id, String serveiCodi) throws EntitatNotFoundException, EntitatServeiNotFoundException {
		LOGGER.debug("Esborrant servei (codi=" + serveiCodi + ") de l'entitat (id=" + id + ")");
		Entitat entitat = entitatRepository.findOne(id);
		if (entitat == null) {
			LOGGER.debug("No s'ha trobat l'entitat (id=" + id + ")");
			throw new EntitatNotFoundException();
		}
		EntitatServei entitatServei = entitatServeiRepository.findByEntitatIdAndServei(id, serveiCodi);
		if (entitatServei == null) {
			LOGGER.debug("L'entitat (id=" + id + ") no té cap servei (codi=" + serveiCodi + ")");
			throw new EntitatServeiNotFoundException();
		}
		entitatServeiRepository.delete(entitatServei);
		actualitzarServeisScspActiusEntitat(entitat);
	}

	@Transactional(readOnly = true)
	@Override
	public List<EntitatDto> findActivesAmbUsuariCodi(String usuariCodi) {
		LOGGER.debug("Consulta de les entitats actives per a l'usuari (codi=" + usuariCodi + ")");
		return dtoMappingHelper.getMapperFacade().mapAsList(
				entitatRepository.findActivesAmbUsuariCodi(usuariCodi),
				EntitatDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public List<EntitatDto> findDisponiblesPerRedireccionsBus(String serveiCodi) throws ServeiNotFoundException {
		LOGGER.debug("Consulta de les entitats disponibles per a configurar les redireccions del bus (serveiCodi=" + serveiCodi + ")");
		Servicio servicio = scspHelper.getServicio(serveiCodi);
		if (servicio == null) {
			LOGGER.debug("No s'ha trobat el servicio (codi=" + serveiCodi + ")");
			throw new ServeiNotFoundException();
		}
		ServeiConfig serveiConfig = serveiConfigRepository.findByServei(serveiCodi);
		if (serveiConfig != null && serveiConfig.getEntitatTipus() != null) {
			EntitatTipus entitatTipus = null;
			if (serveiConfig.getEntitatTipus().equals(ServeiConfig.EntitatTipus.GOVERN))
				entitatTipus = EntitatTipus.GOVERN;
			else if (serveiConfig.getEntitatTipus().equals(ServeiConfig.EntitatTipus.CONSELL))
				entitatTipus = EntitatTipus.CONSELL;
			else if (serveiConfig.getEntitatTipus().equals(ServeiConfig.EntitatTipus.AJUNTAMENT))
				entitatTipus = EntitatTipus.AJUNTAMENT;
			return dtoMappingHelper.getMapperFacade().mapAsList(
					entitatRepository.findByTipusOrderByNomAsc(entitatTipus),
					EntitatDto.class);
		}
		return new ArrayList<EntitatDto>();
	}

	/**
     * Aquest setter només s'hauria d'emprar en les proves unitàries.
     * @param entitatRepository
     */
	public void setEntitatRepository(EntitatRepository entitatRepository) {
		this.entitatRepository = entitatRepository;
	}

	/**
     * Aquest setter només s'hauria d'emprar en les proves unitàries.
     * @param entitatServeiRepository
     */
	public void setEntitatServeiRepository(EntitatServeiRepository entitatServeiRepository) {
		this.entitatServeiRepository = entitatServeiRepository;
	}



	private void actualitzarServeisScspActiusEntitat(
			Entitat entitat) {
		List<EntitatServei> entitatServeis = entitatServeiRepository.findByEntitatId(
				entitat.getId());
		String[] serveisActius = new String[entitatServeis.size()];
		for (int i = 0; i < serveisActius.length; i++) {
			serveisActius[i] = entitatServeis.get(i).getServei();
		}
		scspHelper.actualitzarServiciosActivosOrganismoCesionario(
				entitat.getCif(),
				serveisActius);
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(EntitatServiceImpl.class);

}
