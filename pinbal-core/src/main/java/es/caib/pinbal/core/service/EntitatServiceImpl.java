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
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.helper.DtoMappingHelper;
import es.caib.pinbal.core.model.Entitat;
import es.caib.pinbal.core.model.Entitat.EntitatTipus;
import es.caib.pinbal.core.model.EntitatServei;
import es.caib.pinbal.core.model.OrganismeCessionari;
import es.caib.pinbal.core.model.ServeiConfig;
import es.caib.pinbal.core.repository.EntitatRepository;
import es.caib.pinbal.core.repository.EntitatServeiRepository;
import es.caib.pinbal.core.repository.OrganismeCessionariRepository;
import es.caib.pinbal.core.repository.ServeiConfigRepository;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.EntitatServeiNotFoundException;
import es.caib.pinbal.core.service.exception.ServeiNotFoundException;
import es.caib.pinbal.scsp.ScspHelper;
import es.scsp.common.domain.core.Servicio;

/**
 * Implementació de EntitatService que es comunica amb la base de dades emprant
 * un repositori proporcionat per spring-data-jpa.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class EntitatServiceImpl implements EntitatService, ApplicationContextAware, MessageSourceAware {

	@Resource
	private EntitatRepository entitatRepository;
	@Resource
	private EntitatServeiRepository entitatServeiRepository;
	@Resource
	private ServeiConfigRepository serveiConfigRepository;
	@Resource
	private OrganismeCessionariRepository organismeCessionariRepository;

	@Resource
	private DtoMappingHelper dtoMappingHelper;

	private ApplicationContext applicationContext;
	private MessageSource messageSource;
	private ScspHelper scspHelper;



	@Transactional
	@Override
	public EntitatDto create(EntitatDto creada){
		LOGGER.debug("Creant una nova entitat: " + creada);
		Entitat entitat = Entitat.getBuilder(
				creada.getCodi(),
				creada.getNom(),
				creada.getCif(),
				EntitatTipus.valueOf(creada.getTipus().toString())).build();
		getScspHelper().organismoCesionarioSave(
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
		getScspHelper().organismoCesionarioDelete(esborrada.getCif());
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
	public Page<EntitatDto> findAmbFiltrePaginat(
			String codi,
			String nom,
			String cif,
			Boolean activa,
			String tipus,
			Pageable pageable) {
		LOGGER.debug("Consulta d'entitats segons filtre (codi=" + codi + ", nom=" + nom + ""
				+ "cif=" + cif + " activa=" + activa + " tipus=" + tipus + ")");
		
		Page<Entitat> paginaEntitats = entitatRepository.findByFiltre(
				codi == null || codi.length() == 0,
				codi,
				nom == null || nom.length() == 0,
				nom,
				cif == null || cif.length() == 0,
				cif,
				activa == null,
				activa,
				tipus == null || tipus.length() == 0,
				(tipus != null && tipus.length() > 0) ? Entitat.EntitatTipus.valueOf(tipus.toString()) : null,
				pageable);
		
		return new PageImpl<EntitatDto>(
					dtoMappingHelper.getMapperFacade().mapAsList(
							paginaEntitats.getContent(),
							EntitatDto.class),
					pageable,
					paginaEntitats.getTotalElements());
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
	
	@Transactional(readOnly = true)
	@Override
	public EntitatDto findByCif(String cif) {
		LOGGER.debug("Consulta de l'entitat (codi=" + cif + ")");
		return dtoMappingHelper.getMapperFacade().map(
				entitatRepository.findByCif(cif),
				EntitatDto.class);
	}


	@Transactional(rollbackFor = EntitatNotFoundException.class)
	@Override
	public EntitatDto update(EntitatDto modificada) throws EntitatNotFoundException{
		LOGGER.debug("Actualitzant l'entitat (id=" + modificada.getId() + ") amb la informació: " + modificada);
		Entitat entitat = entitatRepository.findOne(modificada.getId());
		if (entitat == null) {
			LOGGER.debug("No s'ha trobat l'entitat (id=" + modificada.getId() + ")");
			throw new EntitatNotFoundException();
		}
		modificada.setActiva(entitat.isActiva());
		OrganismeCessionari oc = organismeCessionariRepository.findByCif(entitat.getCif());
		oc.updateEntitat(modificada.getNom(), modificada.getCif(), !modificada.isActiva());
		organismeCessionariRepository.saveAndFlush(oc);
		entitat.update(
				modificada.getCodi(),
				modificada.getNom(),
				modificada.getCif(),
				EntitatTipus.valueOf(modificada.getTipus().toString()));
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
		OrganismeCessionari oc = organismeCessionariRepository.findByCif(entitat.getCif());
		oc.setBloquejat(!activa);
		organismeCessionariRepository.saveAndFlush(oc);
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
		if (getScspHelper().getServicio(serveiCodi) == null) {
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
		Servicio servicio = getScspHelper().getServicio(serveiCodi);
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

	@Override
	public void setApplicationContext(
			ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	@Override
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	private void actualitzarServeisScspActiusEntitat(
			Entitat entitat) {
		List<EntitatServei> entitatServeis = entitatServeiRepository.findByEntitatId(
				entitat.getId());
		String[] serveisActius = new String[entitatServeis.size()];
		for (int i = 0; i < serveisActius.length; i++) {
			serveisActius[i] = entitatServeis.get(i).getServei();
		}
		getScspHelper().actualitzarServiciosActivosOrganismoCesionario(
				entitat.getCif(),
				serveisActius);
	}

	private ScspHelper getScspHelper() {
		if (scspHelper == null) {
			scspHelper = new ScspHelper(
					applicationContext,
					messageSource);
		}
		return scspHelper;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(EntitatServiceImpl.class);
}
