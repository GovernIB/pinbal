/**
 * 
 */
package es.caib.pinbal.logic.service;

import es.caib.pinbal.client.comu.EntitatInfo;
import es.caib.pinbal.logic.helper.CacheHelper;
import es.caib.pinbal.logic.helper.DtoMappingHelper;
import es.caib.pinbal.logic.intf.dto.EntitatDto;
import es.caib.pinbal.logic.intf.dto.EntitatServeiDto;
import es.caib.pinbal.logic.intf.dto.EntitatDto.EntitatTipusDto;
import es.caib.pinbal.logic.intf.dto.OrganGestorDto;
import es.caib.pinbal.logic.intf.service.EntitatService;
import es.caib.pinbal.logic.intf.service.exception.EntitatNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.EntitatServeiNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.ServeiNotFoundException;
import es.caib.pinbal.persist.entity.ClauPrivada;
import es.caib.pinbal.persist.entity.Entitat;
import es.caib.pinbal.persist.entity.Entitat.EntitatTipus;
import es.caib.pinbal.persist.entity.EntitatServei;
import es.caib.pinbal.persist.entity.EntitatUsuari;
import es.caib.pinbal.persist.entity.OrganGestor;
import es.caib.pinbal.persist.entity.ServeiConfig;
import es.caib.pinbal.persist.entity.Usuari;
import es.caib.pinbal.persist.repository.ClauPrivadaRepository;
import es.caib.pinbal.persist.repository.EntitatRepository;
import es.caib.pinbal.persist.repository.EntitatServeiRepository;
import es.caib.pinbal.persist.repository.EntitatUsuariRepository;
import es.caib.pinbal.persist.repository.ServeiConfigRepository;
import es.caib.pinbal.persist.repository.UsuariRepository;
import es.caib.pinbal.scsp.ScspHelper;
import es.scsp.common.domain.core.Servicio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementació de EntitatService que es comunica amb la base de dades emprant
 * un repositori proporcionat per spring-data-jpa.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class EntitatServiceImpl implements EntitatService, ApplicationContextAware, MessageSourceAware {

	private final EntitatRepository entitatRepository;
	private final EntitatServeiRepository entitatServeiRepository;
	private final EntitatUsuariRepository entitatUsuariRepository;
	private final ServeiConfigRepository serveiConfigRepository;
	private final UsuariRepository usuariRepository;
	private final DtoMappingHelper dtoMappingHelper;
	private final CacheHelper cacheHelper;
	private final ClauPrivadaRepository clauPrivadaRepository;

	private ApplicationContext applicationContext;
	private MessageSource messageSource;
	private ScspHelper scspHelper;

	@Transactional
	@Override
	public EntitatDto create(EntitatDto creada){
		log.debug("Creant una nova entitat: " + creada);
		Entitat entitat = Entitat.getBuilder(
				creada.getCodi(),
				creada.getNom(),
				creada.getCif(),
				EntitatTipus.valueOf(creada.getTipus().toString())).build();
		entitat.setUnitatArrel(creada.getUnitatArrel());
		
		getScspHelper().organismoCesionarioSave(
				creada.getCif(),
				creada.getNom(),
				new Date(),
				null,
				!entitat.isActiva());
		entitat = entitatRepository.save(entitat);
		return dtoMappingHelper.getMapperFacade().map(
				entitat,
				EntitatDto.class);
	}

	@Transactional(rollbackFor = EntitatNotFoundException.class)
	@Override
	public EntitatDto delete(Long entitatId) throws EntitatNotFoundException {
		log.debug("Esborrant entitat (id=" + entitatId + ")");
		Entitat esborrada = entitatRepository.findById(entitatId).orElse(null);
		if (esborrada == null) {
			log.debug("No s'ha trobat l'entitat (id=" + entitatId + ")");
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
		log.debug("Consulta de totes les entitats");
		return dtoMappingHelper.getMapperFacade().mapAsList(
				entitatRepository.findAll(
						Sort.by(Sort.Direction.ASC, "nom")),
				EntitatDto.class);
	}

	@Transactional(readOnly = true)
    @Override
    public List<EntitatInfo> getEntitatsInfo() {
		log.debug("Consulta de totes les entitats");
		return dtoMappingHelper.getMapperFacade().mapAsList(
				entitatRepository.findAll(
						Sort.by(Sort.Direction.ASC, "nom")),
				EntitatInfo.class);
    }

    @Transactional(readOnly = true)
	@Override
	public Page<EntitatDto> findAmbFiltrePaginat(
			String codi,
			String nom,
			String cif,
			Boolean activa,
			String tipus,
			Pageable pageable, 
			String unitatArrel) {
		log.debug("Consulta d'entitats segons filtre (codi=" + codi + ", nom=" + nom + ""
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
				(tipus != null && tipus.length() > 0) ? EntitatTipus.valueOf(tipus.toString()) : null,
				unitatArrel == null || unitatArrel.length() == 0,
				unitatArrel,
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
		log.debug("Consulta de l'entitat (id=" + id + ")");
		return dtoMappingHelper.getMapperFacade().map(
				entitatRepository.findById(id).orElseThrow(),
				EntitatDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public EntitatDto findByCodi(String codi) {
		log.debug("Consulta de l'entitat (codi=" + codi + ")");
		return dtoMappingHelper.getMapperFacade().map(
				entitatRepository.findByCodi(codi),
				EntitatDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public EntitatDto findTopByTipus(EntitatTipusDto tipus) {
		log.debug("Consulta de l'entitat (tipus=" + tipus + ")");
		return dtoMappingHelper.getMapperFacade().map(
				entitatRepository.findByTipusOrderByTipusAsc(dtoMappingHelper.getMapperFacade().map(tipus, EntitatTipus.class)).get(0),
				EntitatDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public EntitatDto findByCif(String cif) {
		log.debug("Consulta de l'entitat (codi=" + cif + ")");
		return dtoMappingHelper.getMapperFacade().map(
				entitatRepository.findByCif(cif),
				EntitatDto.class);
	}

	@Transactional(rollbackFor = EntitatNotFoundException.class)
	@Override
	public EntitatDto update(EntitatDto modificada) throws EntitatNotFoundException{
		log.debug("Actualitzant l'entitat (id=" + modificada.getId() + ") amb la informació: " + modificada);
		Entitat entitat = entitatRepository.findById(modificada.getId()).orElse(null);
		if (entitat == null) {
			log.debug("No s'ha trobat l'entitat (id=" + modificada.getId() + ")");
			throw new EntitatNotFoundException();
		}
		modificada.setActiva(entitat.isActiva());
		getScspHelper().organismoCesionarioUpdate(
				entitat.getCif(),
				entitat.getNom(),
				!entitat.isActiva());
		entitat.update(
				modificada.getCodi(),
				modificada.getNom(),
				modificada.getCif(),
				EntitatTipus.valueOf(modificada.getTipus().toString()));
		entitat.setUnitatArrel(modificada.getUnitatArrel());
		actualitzarServeisScspActiusEntitat(entitat);
		return dtoMappingHelper.getMapperFacade().map(
				entitat,
				EntitatDto.class);
	}

	@Transactional(readOnly = true)
	public List<OrganGestorDto> getOrgansGestors(Long id) {
		Entitat entitat = entitatRepository.findById(id).orElse(null);
		List<OrganGestor> organs = entitat.getOrganGestors();
		return dtoMappingHelper.convertirList(organs, OrganGestorDto.class);
	}

	@Transactional(rollbackFor = EntitatNotFoundException.class)
	@Override
	public EntitatDto updateActiva(Long id, boolean activa) throws EntitatNotFoundException {
		log.debug("Desactivant l'entitat (id=" + id + ")");
		Entitat entitat = entitatRepository.findById(id).orElse(null);
		if (entitat == null) {
			log.debug("No s'ha trobat l'entitat (id=" + id + ")");
			throw new EntitatNotFoundException();
		}
		entitat.updateActiva(activa);
		getScspHelper().organismoCesionarioUpdate(
				entitat.getCif(),
				entitat.getNom(),
				!activa);
		return dtoMappingHelper.getMapperFacade().map(
				entitat,
				EntitatDto.class);
	}

	@Transactional(rollbackFor = {EntitatNotFoundException.class, ServeiNotFoundException.class})
	@Override
	public void addServei(Long id, String serveiCodi) throws EntitatNotFoundException, ServeiNotFoundException {
		log.debug("Afegint servei (codi=" + serveiCodi + ") a l'entitat (id=" + id + ")");
		Entitat entitat = entitatRepository.findById(id).orElse(null);
		if (entitat == null) {
			log.debug("No s'ha trobat l'entitat (id=" + id + ")");
			throw new EntitatNotFoundException();
		}
		if (getScspHelper().getServicio(serveiCodi) == null) {
			log.debug("No s'ha trobat el servei (codi=" + serveiCodi + ")");
			throw new ServeiNotFoundException();
		}
		EntitatServei entitatServei = EntitatServei.getBuilder(
				entitat,
				serveiCodi).build();
		entitatServeiRepository.save(entitatServei);
		actualitzarServeisScspActiusEntitat(entitat);

		cacheHelper.evictServeisEntitat(entitat.getCodi());
	}

	@Transactional(rollbackFor = {EntitatNotFoundException.class, ServeiNotFoundException.class})
	@Override
	public void removeServei(Long id, String serveiCodi) throws EntitatNotFoundException, EntitatServeiNotFoundException {
		log.debug("Esborrant servei (codi=" + serveiCodi + ") de l'entitat (id=" + id + ")");
		Entitat entitat = entitatRepository.findById(id).orElse(null);
		if (entitat == null) {
			log.debug("No s'ha trobat l'entitat (id=" + id + ")");
			throw new EntitatNotFoundException();
		}
		EntitatServei entitatServei = entitatServeiRepository.findByEntitatIdAndServei(id, serveiCodi);
		if (entitatServei == null) {
			log.debug("L'entitat (id=" + id + ") no té cap servei (codi=" + serveiCodi + ")");
			throw new EntitatServeiNotFoundException();
		}
		entitatServeiRepository.delete(entitatServei);
		actualitzarServeisScspActiusEntitat(entitat);

		cacheHelper.evictServeisEntitat(entitat.getCodi());
	}

	@Transactional(readOnly = true)
	@Override
	public List<EntitatServeiDto> findServeisAssignats(Long entitatId) {
		log.debug("Consulta dels serveis assignats a l'entitat (id=" + entitatId + ")");
		return entitatServeiRepository.findByEntitatId(entitatId).stream().
				map(this::toEntitatServeiDto).
				collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	@Override
	public EntitatServeiDto findServeiAssignatById(Long id) {
		log.debug("Consulta de l'assignació entitat-servei (id=" + id + ")");
		return entitatServeiRepository.findById(id).map(this::toEntitatServeiDto).orElse(null);
	}

	private EntitatServeiDto toEntitatServeiDto(EntitatServei entitatServei) {
		EntitatServeiDto dto = new EntitatServeiDto(entitatServei.getEntitat().getId(), entitatServei.getServei());
		dto.setId(entitatServei.getId());
		return dto;
	}

	@Transactional(readOnly = true)
	@Override
	public List<EntitatDto> findActivesAmbUsuariCodi(String usuariCodi) {
		log.debug("Consulta de les entitats actives per a l'usuari (codi=" + usuariCodi + ")");
		List<EntitatUsuari> entitatUsuaris = entitatUsuariRepository.findByUsuariCodi(usuariCodi);
		List<Entitat> entitats = entitatRepository.findActivesAmbUsuariCodi(usuariCodi);
		Iterator<Entitat> it = entitats.iterator();
		while (it.hasNext()) {
			Entitat entitat = it.next();
			boolean trobada = false;
			for (EntitatUsuari entitatUsuari: entitatUsuaris) {
				if (entitatUsuari.getEntitat().getId() == entitat.getId()) {
					if (entitatUsuari.isActiu()) {
						trobada = true;
					}
					break;
				}
			}
			if (!trobada) {
				it.remove();
			}
		}
		return dtoMappingHelper.getMapperFacade().mapAsList(
				entitats,
				EntitatDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public List<EntitatDto> findDisponiblesPerRedireccionsBus(String serveiCodi) throws ServeiNotFoundException {
		log.debug("Consulta de les entitats disponibles per a configurar les redireccions del bus (serveiCodi=" + serveiCodi + ")");
		Servicio servicio = getScspHelper().getServicio(serveiCodi);
		if (servicio == null) {
			log.debug("No s'ha trobat el servicio (codi=" + serveiCodi + ")");
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

    @Override
	@Transactional(readOnly = true)
    public Long getEntitatIdPerDefecte(String usuariCodi) {
		Usuari usuari = usuariRepository.findByCodi(usuariCodi);
        return usuari != null ? usuari.getEntitatId() : null;
    }

	@Override
	@Transactional(readOnly = true)
	public List<EntitatDto> findActives() {
		return dtoMappingHelper.getMapperFacade().mapAsList(
				entitatRepository.findByActivaTrue(),
				EntitatDto.class);
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

	private void actualitzarServeisScspActiusEntitat(Entitat entitat) {

        // Obtenir serveis actius
		List<EntitatServei> entitatServeis = entitatServeiRepository.findByEntitat(entitat);
		Set<String> serveisActius = new HashSet<>();
		for (int i = 0; i < entitatServeis.size(); i++) {
			serveisActius.add(entitatServeis.get(i).getServei());
		}

        // Obtenir clau privada de l'entitat
        ClauPrivada clauPrivada = clauPrivadaRepository.findTopByOrganismeCifAndPerEntitatTrueOrderByDataAltaDesc(entitat.getCif());
        String aliesClauFirmaEntitat = clauPrivada != null ? clauPrivada.getAlies() : null;

        // Actualitzar els serveis per òrgan SCSP
		getScspHelper().actualitzarServiciosActivosOrganismoCesionario(
				entitat.getCif(),
				serveisActius,
                aliesClauFirmaEntitat);
	}

	@Transactional
	@Override
	public void scspOrganismeCessionariAlta(String cif, String nom, boolean activa) {
		getScspHelper().organismoCesionarioSave(
				cif,
				nom,
				new Date(),
				null,
				!activa);
	}

	@Transactional
	@Override
	public void scspOrganismeCessionariActualitzacio(String cif, String nom, boolean activa) {
		getScspHelper().organismoCesionarioUpdate(
				cif,
				nom,
				!activa);
	}

	@Transactional
	@Override
	public void scspOrganismeCessionariBaixa(String cif) {
		getScspHelper().organismoCesionarioDelete(cif);
	}

	@Transactional
	@Override
	public void scspSincronitzarServeisActius(Long entitatId) {
		Entitat entitat = entitatRepository.findById(entitatId).orElse(null);
		if (entitat != null) {
			actualitzarServeisScspActiusEntitat(entitat);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public boolean scspServeiExisteix(String serveiCodi) {
		return getScspHelper().getServicio(serveiCodi) != null;
	}

	private ScspHelper getScspHelper() {
		if (scspHelper == null) {
			scspHelper = new ScspHelper(
					applicationContext,
					messageSource);
		}
		return scspHelper;
	}

}
