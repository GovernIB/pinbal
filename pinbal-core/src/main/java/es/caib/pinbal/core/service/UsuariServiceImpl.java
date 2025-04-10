/**
 * 
 */
package es.caib.pinbal.core.service;

import es.caib.pinbal.core.dto.EntitatUsuariDto;
import es.caib.pinbal.core.dto.InformeUsuariDto;
import es.caib.pinbal.core.dto.UsuariDto;
import es.caib.pinbal.core.helper.CacheHelper;
import es.caib.pinbal.core.helper.DtoMappingHelper;
import es.caib.pinbal.core.helper.PluginHelper;
import es.caib.pinbal.core.helper.UsuariHelper;
import es.caib.pinbal.core.model.Entitat;
import es.caib.pinbal.core.model.EntitatUsuari;
import es.caib.pinbal.core.model.Usuari;
import es.caib.pinbal.core.repository.AvisRepository;
import es.caib.pinbal.core.repository.ConfigRepository;
import es.caib.pinbal.core.repository.ConsultaRepository;
import es.caib.pinbal.core.repository.EntitatRepository;
import es.caib.pinbal.core.repository.EntitatServeiRepository;
import es.caib.pinbal.core.repository.EntitatUsuariRepository;
import es.caib.pinbal.core.repository.HistoricConsultaRepository;
import es.caib.pinbal.core.repository.IntegracioAccioParamRepository;
import es.caib.pinbal.core.repository.IntegracioAccioRepository;
import es.caib.pinbal.core.repository.OrganGestorRepository;
import es.caib.pinbal.core.repository.ProcedimentRepository;
import es.caib.pinbal.core.repository.ProcedimentServeiRepository;
import es.caib.pinbal.core.repository.ServeiBusRepository;
import es.caib.pinbal.core.repository.ServeiCampGrupRepository;
import es.caib.pinbal.core.repository.ServeiCampRepository;
import es.caib.pinbal.core.repository.ServeiConfigRepository;
import es.caib.pinbal.core.repository.ServeiJustificantCampRepository;
import es.caib.pinbal.core.repository.ServeiReglaRepository;
import es.caib.pinbal.core.repository.UsuariRepository;
import es.caib.pinbal.core.repository.explotacio.ExplotConsultaDimensioRepository;
import es.caib.pinbal.core.repository.llistat.LlistatConsultaRepository;
import es.caib.pinbal.core.repository.llistat.LlistatHistoricConsultaRepository;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.EntitatUsuariNotFoundException;
import es.caib.pinbal.core.service.exception.EntitatUsuariProtegitException;
import es.caib.pinbal.core.service.exception.NotFoundException;
import es.caib.pinbal.core.service.exception.UsuariExternNotFoundException;
import es.caib.pinbal.plugins.DadesUsuari;
import es.caib.pinbal.plugins.SistemaExternException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Implementació dels mètodes per al manteniment de la taula d'usuaris.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Service
public class UsuariServiceImpl implements UsuariService {

	@Resource
	private EntitatRepository entitatRepository;
	@Resource
	private UsuariRepository usuariRepository;
	@Resource
	private EntitatUsuariRepository entitatUsuariRepository;
	@Resource
	private ProcedimentServeiRepository procedimentServeiRepository;
	@Resource
	private ProcedimentRepository procedimentRepository;

	@Resource
	private DtoMappingHelper dtoMappingHelper;
	@Resource
	private UsuariHelper usuariHelper;
	@Resource
	private PluginHelper pluginHelper;

	@Resource
	private MutableAclService aclService;
    @Autowired
    private CacheHelper cacheHelper;

    @Autowired
    private AvisRepository avisRepository;
    @Autowired
    private ConfigRepository configRepository;
    @Autowired
    private ConsultaRepository consultaRepository;
    @Autowired
    private HistoricConsultaRepository historicConsultaRepository;
    @Autowired
    private EntitatServeiRepository entitatServeiRepository;
    @Autowired
    private OrganGestorRepository organGestorRepository;
    @Autowired
    private ServeiBusRepository serveiBusRepository;
    @Autowired
    private ServeiCampRepository serveiCampRepository;
    @Autowired
    private ServeiConfigRepository serveiConfigRepository;
    @Autowired
    private ServeiJustificantCampRepository serveiJustificantCampRepository;
    @Autowired
    private ServeiReglaRepository serveiReglaRepository;
    @Autowired
    private IntegracioAccioRepository integracioAccioRepository;
    @Autowired
    private IntegracioAccioParamRepository integracioAccioParamRepository;
    @Autowired
    private ServeiCampGrupRepository serveiCampGrupRepository;
    @Autowired
    private LlistatConsultaRepository llistatConsultaRepository;
    @Autowired
    private LlistatHistoricConsultaRepository llistatHistoricConsultaRepository;
    @Autowired
    private ExplotConsultaDimensioRepository explotConsultaDimensioRepository;

	@Transactional
	@Override
	public void inicialitzarUsuariActual() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		log.debug("Inicialitzant l'usuari (codi=" + auth.getName() + ")");
		usuariHelper.init(auth.getName());
	}

	@Transactional(readOnly = true)
	@Override
	public Page<EntitatUsuariDto> findAmbFiltrePaginat(
			Long id_entitat,
			Boolean isRepresentant,
			Boolean isDelegat,
			Boolean isAuditor,
			Boolean isAplicacio,
			String codi,
			String nom,
			String nif,
			String departament,
			Pageable pageable) {
		log.debug("Consulta d'entitats segons filtre (" +
				"codi=" + codi + ", " +
				"nom=" + nom + ", " +
				"nif=" + nif + ", " +
				"departament=" + departament + ")");
		Page<EntitatUsuari> paginaEntitats = entitatUsuariRepository.findByFiltre(
				id_entitat == null,
				entitatRepository.findOne(id_entitat),
				isRepresentant != null && isRepresentant == true,
				isDelegat != null && isDelegat == true,
				isAuditor != null && isAuditor == true,
				isAplicacio != null && isAplicacio == true,
				codi == null || codi.length() == 0,
				codi,
				nom == null || nom.length() == 0,
				nom,
				nif == null || nif.length() == 0,
				nif,
				departament == null || departament.length() == 0,
				departament,
				pageable);
		return dtoMappingHelper.pageEntities2pageDto(
				paginaEntitats,
				EntitatUsuariDto.class,
				pageable);
	}
	
	@Transactional(readOnly = true)
	@Override
	public UsuariDto getDades() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		log.debug("Consulta de les dades de l'usuari (codi=" + auth.getName() + ")");
		return dtoMappingHelper.getMapperFacade().map(
				usuariRepository.findOne(auth.getName()),
				UsuariDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public UsuariDto getDades(
			String usuariCodi) {
		log.debug("Consulta de les dades de l'usuari (codi=" + usuariCodi + ")");
		return dtoMappingHelper.getMapperFacade().map(
				usuariRepository.findOne(usuariCodi),
				UsuariDto.class);
	}

	@Transactional
	@Override
	public UsuariDto getUsuariActual() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		log.debug("Obtenint usuari actual");
		Usuari usuari = usuariRepository.findOne(auth.getName());
		if (usuari.getEmail() == null || usuari.getEmail().isEmpty()) {
			try {
				DadesUsuari dadesUsuari = pluginHelper.dadesUsuariConsultarAmbUsuariCodi(auth.getName());
				usuari.updateEmail(dadesUsuari.getEmail());
			} catch (SistemaExternException ex) {
				log.error("Error al consultar les dades de l'usuari (codi=" + auth.getName() + ") al sistema extern", ex);
			}
		}
		return toUsuariDtoAmbRols(usuari);
	}

	@Override
	public String getIdiomaUsuariActual() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		log.debug("Obtenint idioma de usuari actual");
		Usuari usuari = usuariRepository.findOne(auth.getName());
		return usuari != null ? usuari.getIdioma() : null;
	}

	@Override
	public Integer getNumElementsPaginaDefecte() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		log.debug("Obtenint numElementsPaginaDefecte de usuari actual");
		Usuari usuari = usuariRepository.findOne(auth.getName());
		return usuari != null && usuari.getNumElementsPagina() != null ? usuari.getNumElementsPagina(): 10;
	}

	@Transactional
	@Override
	public UsuariDto updateUsuariActual(UsuariDto dto, boolean updateEntitat) {
		log.debug("Actualitzant configuració de usuari actual");
		Usuari usuari = usuariRepository.findOne(dto.getCodi());
//		usuari.updateIdioma(dto.getIdioma());
		if (updateEntitat) {
			usuari.updateValorsPerDefecte(
					dto.getIdioma(),
					dto.getProcedimentId(),
					dto.getServeiCodi(),
					dto.getEntitatId(),
					dto.getDepartament(),
					dto.getFinalitat(),
					dto.getNumElementsPagina() != null ? dto.getNumElementsPagina().getElements() : null);
		} else {
			usuari.updateValorsPerDefecte(
					dto.getIdioma(),
					dto.getProcedimentId(),
					dto.getServeiCodi(),
					dto.getDepartament(),
					dto.getFinalitat(),
					dto.getNumElementsPagina() != null ? dto.getNumElementsPagina().getElements() : null);
		}
		return toUsuariDtoAmbRols(usuari);
	}

	@Override
	public List<UsuariDto> findLikeCodiONom(String text) {
		return dtoMappingHelper.getMapperFacade().mapAsList(
				usuariRepository.findByCodiOrNom(text),
				UsuariDto.class
		);
	}

	@Override
	public List<UsuariDto> findLikeCodiONomONif(String text) {
		return dtoMappingHelper.getMapperFacade().mapAsList(
				usuariRepository.findByCodiOrNomOrNif(text),
				UsuariDto.class
		);
	}

	@Override
    public EntitatUsuariDto getEntitatUsuari(Long entitatId, String usuariCodi) {
		EntitatUsuari entitatUsuari = entitatUsuariRepository.findByEntitatIdAndUsuariCodi(
				entitatId,
				usuariCodi);
		if (entitatUsuari == null) {
			throw new NotFoundException(entitatId + "-" + usuariCodi, EntitatUsuari.class);
		}
		return dtoMappingHelper.convertir(entitatUsuari, EntitatUsuariDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public UsuariDto getUsuariExtern(String codi) throws Exception {
		DadesUsuari dadesUsuari = pluginHelper.dadesUsuariConsultarAmbUsuariCodi(codi);
		return UsuariDto.builder()
				.codi(dadesUsuari.getCodi())
				.nom(dadesUsuari.getNom())
				.nif(dadesUsuari.getNif())
				.email(dadesUsuari.getEmail())
				.build();
	}

	@Override
	public List<UsuariDto> getUsuarisExterns(String text) throws Exception {
		List<UsuariDto> usuaris = new ArrayList<>();

		List<DadesUsuari> dadesUsuaris = pluginHelper.dadesUsuariLikeCodiNomOrNif(text);
		if (dadesUsuaris != null) {
			for (DadesUsuari dadesUsuari : dadesUsuaris) {
				usuaris.add(UsuariDto.builder()
						.codi(dadesUsuari.getCodi())
						.nom(dadesUsuari.getNom())
						.nif(dadesUsuari.getNif())
						.email(dadesUsuari.getEmail())
						.build());
			}
		}
		return usuaris;
	}

	@Override
	public UsuariDto getUsuariEntitat(Long entitatId, String usuariCodi) {
		EntitatUsuari entitatUsuari = entitatUsuariRepository.findByEntitatIdAndUsuariCodi(
				entitatId,
				usuariCodi);
		if (entitatUsuari == null) {
			throw new NotFoundException(entitatId + "-" + usuariCodi, EntitatUsuari.class);
		}
		return UsuariDto.builder()
				.codi(entitatUsuari.getUsuari().getCodi())
				.nom(entitatUsuari.getUsuari().getNom())
				.nif(entitatUsuari.getUsuari().getNif())
				.email(entitatUsuari.getUsuari().getEmail())
				.entitatId(entitatId)
				.build();
	}

	@Override
	public List<UsuariDto> getUsuarisEntitat(Long entitatId, String text) {
		List<UsuariDto> usuaris = new ArrayList<>();

		List<Usuari> usuarisEntitat = entitatUsuariRepository.findByEntitatIdAndUsuariLikeText(entitatId, text);
		if (usuarisEntitat != null) {
			for (Usuari usuari : usuarisEntitat) {
				usuaris.add(UsuariDto.builder()
						.codi(usuari.getCodi())
						.nom(usuari.getNom())
						.nif(usuari.getNif())
						.email(usuari.getEmail())
						.entitatId(entitatId)
						.build());
			}
		}
		return usuaris;
	}

	@Transactional(timeout = 1200)
	@Override
	public Long updateUsuariCodi(String codiAntic, String codiNou) {
		Usuari usuariAntic = usuariRepository.findByCodi(codiAntic);
		if (usuariAntic == null) {
			throw new NotFoundException(codiAntic, Usuari.class);
		}

		// Si han informat un codi d'usuari nou, actualitzem l'usuari i totes les seves referències en BBDD
		Usuari usuariNou = usuariRepository.findByCodi(codiNou);
		if (usuariNou == null) {
			usuariNou = cloneUsuari(codiNou, usuariAntic);
		}

		log.info(">>>>> UPDATE CODI USUARI: " + codiAntic + " -> " + codiNou);
		Long registresModificats = 0L;

		// Actualitzam la informació de auditoria de les taules:
		registresModificats += updateUsuariAuditoria(codiAntic, codiNou);
		// Actualitazam els permisos assignats per ACL
		registresModificats += updateUsuariPermisos(codiAntic, codiNou);
		// Actualitzam les referencis a l'usuari a taules:
		registresModificats += updateUsuariReferencies(codiAntic, codiNou);

		// Eliminam l'usuari antic
		usuariRepository.delete(usuariAntic);

		return registresModificats;
	}

	@Transactional(timeout = 1200)
    @Override
    public void updateUsuariCodi(String codiAntic, String codiNou, String nom, String nif, String email, String idioma) {
        Usuari usuariAntic = usuariRepository.findByCodi(codiAntic);
		if (usuariAntic == null) {
			throw new NotFoundException(codiAntic, Usuari.class);
		}

		String uNom = nom != null && !nom.trim().isEmpty() ? nom : usuariAntic.getNom();
		String uNif = nif != null && !nif.trim().isEmpty() ? nif : usuariAntic.getNif();
		String uEmail = email != null && !email.trim().isEmpty() ? email : usuariAntic.getEmail();
		String uIdioma = idioma != null && !idioma.trim().isEmpty() ? idioma : usuariAntic.getIdioma();

		// Si no han indicat un codi nou, únicament actualitzem els valors de l'usuari
		if (codiNou == null || codiNou.trim().isEmpty()) {
			usuariAntic.update(uNom, uNif);
			usuariAntic.updateEmail(uEmail);
			usuariAntic.updateIdioma(uIdioma);
			usuariRepository.saveAndFlush(usuariAntic);
			return;
		}

		// Si han informat un codi d'usuari nou, actualitzem l'usuari i totes les seves referències en BBDD
		Usuari usuariNou = usuariRepository.findByCodi(codiNou);
		if (usuariNou == null) {
			usuariNou = cloneUsuari(codiNou, usuariAntic, uNom, uNif, uEmail, uIdioma);
		}

		log.info(">>>>> UPDATE CODI USUARI: " + codiAntic + " -> " + codiNou);

		// Actualitzam la informació de auditoria de les taules:
		updateUsuariAuditoria(codiAntic, codiNou);
		// Actualitazam els permisos assignats per ACL
		updateUsuariPermisos(codiAntic, codiNou);
		// Actualitzam les referencis a l'usuari a taules:
		updateUsuariReferencies(codiAntic, codiNou);

		// Eliminam l'usuari antic
		usuariRepository.delete(usuariAntic);
	}

	private int updateUsuariPermisos(String codiAntic, String codiNou) {
		return usuariRepository.updateUsuariPermis(codiAntic, codiNou);
//		usuariRepository.flush();
	}

	private Long updateUsuariReferencies(String codiAntic, String codiNou) {
		Long registresModificats = 0L;

		log.info(">>> UPDATE USUARIS TAULES AMB REFERENCIES:");
		//		PBL_CONSULTA_HIST_LIST.USUARICODI
		Long t0 = System.currentTimeMillis();
		registresModificats += llistatHistoricConsultaRepository.updateUsuariCodi(codiAntic, codiNou);
		log.info("> Llista historic: " + (System.currentTimeMillis() - t0) + " ms");
		//		PBL_CONSULTA_LIST.USUARICODI
		t0 = System.currentTimeMillis();
		registresModificats += llistatConsultaRepository.updateUsuariCodi(codiAntic, codiNou);
		log.info("> Llista consulta: " + (System.currentTimeMillis() - t0) + " ms");
		//		PBL_EXPLOT_CONSULTA_DIM.USUARI_CODI
		t0 = System.currentTimeMillis();
		registresModificats += explotConsultaDimensioRepository.updateUsuariCodi(codiAntic, codiNou);
		log.info("> Dimensions: " + (System.currentTimeMillis() - t0) + " ms");
		//		PBL_ENTITAT_USUARI.USUARI_ID
		t0 = System.currentTimeMillis();
		registresModificats += entitatUsuariRepository.updateUsuariCodi(codiAntic, codiNou);
		log.info("> Entitat-Usuari: " + (System.currentTimeMillis() - t0) + " ms");

		return registresModificats;
	}

	private Long updateUsuariAuditoria(String codiAntic, String codiNou) {
		Long registresModificats = 0L;

		log.info(">>> UPDATE USUARIS AUDITORIA:");
		//		PBL_AVIS
		Long t0 = System.currentTimeMillis();
		registresModificats += avisRepository.updateUsuariAuditoria(codiAntic, codiNou);
		log.info("> Avis: " + (System.currentTimeMillis() - t0) + " ms");
		//		PBL_CONFIG
		t0 = System.currentTimeMillis();
		registresModificats += configRepository.updateUsuariAuditoria(codiAntic, codiNou);
		log.info("> Config: " + (System.currentTimeMillis() - t0) + " ms");
		//		PBL_CONSULTA
		t0 = System.currentTimeMillis();
		// Dividim l'update en 2, i cream indexos a la taula per les columnes de createdby i lastmodifiedby
		registresModificats += consultaRepository.updateCreatedByCodi(codiAntic, codiNou);
		registresModificats += consultaRepository.updateLastModifiedByCodi(codiAntic, codiNou);
//		consultaRepository.disableLogging();
//		try {
//			int updatedRows;
//			do {
//				updatedRows = consultaRepository.updateCreatedByCodiBatch(codiAntic, codiNou, 5000);
//				log.info("  - Actualitzats {} registres per CREATEDBY_CODI", updatedRows);
//			} while (updatedRows > 0);
//
//			do {
//				updatedRows = consultaRepository.updateLastModifiedByCodiBatch(codiAntic, codiNou, 5000);
//				log.info("  - Actualitzats {} registres per LASTMODIFIEDBY_CODI", updatedRows);
//			} while (updatedRows > 0);
//		} finally {
//			consultaRepository.enableLogging();
//		}
		log.info("> Consulta: " + (System.currentTimeMillis() - t0) + " ms");
		//		PBL_CONSULTA_HIST
		t0 = System.currentTimeMillis();
		// Dividim l'update en 2, i cream indexos a la taula per les columnes de createdby i lastmodifiedby
		registresModificats += historicConsultaRepository.updateCreatedByCodi(codiAntic, codiNou);
		registresModificats += historicConsultaRepository.updateLastModifiedByCodi(codiAntic, codiNou);
		log.info("> Historic consulta: " + (System.currentTimeMillis() - t0) + " ms");
		//		PBL_ENTITAT
		t0 = System.currentTimeMillis();
		registresModificats += entitatRepository.updateUsuariAuditoria(codiAntic, codiNou);
		log.info("> Entitat: " + (System.currentTimeMillis() - t0) + " ms");
		//		PBL_ENTITAT_SERVEI
		t0 = System.currentTimeMillis();
		registresModificats += entitatServeiRepository.updateUsuariAuditoria(codiAntic, codiNou);
		log.info("> Entitat-Servei: " + (System.currentTimeMillis() - t0) + " ms");
		//		PBL_ENTITAT_USUARI
		t0 = System.currentTimeMillis();
		registresModificats += entitatUsuariRepository.updateUsuariAuditoria(codiAntic, codiNou);
		log.info("> Entitat-Usuari: " + (System.currentTimeMillis() - t0) + " ms");
		//		PBL_MON_INT
		t0 = System.currentTimeMillis();
		// Dividim l'update en 2, i cream indexos a la taula per les columnes de createdby i lastmodifiedby
		registresModificats += integracioAccioRepository.updateCreatedByCodi(codiAntic, codiNou);
		registresModificats += integracioAccioRepository.updateLastModifiedByCodi(codiAntic, codiNou);
		log.info("> Monitor: " + (System.currentTimeMillis() - t0) + " ms");
		//		PBL_MON_INT_PARAM
		t0 = System.currentTimeMillis();
		// Dividim l'update en 2, i cream indexos a la taula per les columnes de createdby i lastmodifiedby
		registresModificats += integracioAccioParamRepository.updateCreatedByCodi(codiAntic, codiNou);
		registresModificats += integracioAccioParamRepository.updateLastModifiedByCodi(codiAntic, codiNou);
		log.info("> Monitor params: " + (System.currentTimeMillis() - t0) + " ms");
		//		PBL_ORGAN_GESTOR
		t0 = System.currentTimeMillis();
		registresModificats += organGestorRepository.updateUsuariAuditoria(codiAntic, codiNou);
		log.info("> Organ Gestor: " + (System.currentTimeMillis() - t0) + " ms");
		//		PBL_PROCEDIMENT
		t0 = System.currentTimeMillis();
		registresModificats += procedimentRepository.updateUsuariAuditoria(codiAntic, codiNou);
		log.info("> Procediment: " + (System.currentTimeMillis() - t0) + " ms");
		//		PBL_PROCEDIMENT_SERVEI
		t0 = System.currentTimeMillis();
		registresModificats += procedimentServeiRepository.updateUsuariAuditoria(codiAntic, codiNou);
		log.info("> Procediment-Servei: " + (System.currentTimeMillis() - t0) + " ms");
		//		PBL_SERVEI_BUS
		t0 = System.currentTimeMillis();
		registresModificats += serveiBusRepository.updateUsuariAuditoria(codiAntic, codiNou);
		log.info("> Servei-Bus: " + (System.currentTimeMillis() - t0) + " ms");
		//		PBL_SERVEI_CAMP
		t0 = System.currentTimeMillis();
		registresModificats += serveiCampRepository.updateUsuariAuditoria(codiAntic, codiNou);
		log.info("> Servei-Camp: " + (System.currentTimeMillis() - t0) + " ms");
		//		PBL_SERVEI_CAMP_GRUP
		t0 = System.currentTimeMillis();
		registresModificats += serveiCampGrupRepository.updateUsuariAuditoria(codiAntic, codiNou);
		log.info("> Servei-CampGrup: " + (System.currentTimeMillis() - t0) + " ms");
		//		PBL_SERVEI_CONFIG
		t0 = System.currentTimeMillis();
		registresModificats += serveiConfigRepository.updateUsuariAuditoria(codiAntic, codiNou);
		log.info("> Servei-Config: " + (System.currentTimeMillis() - t0) + " ms");
		//		PBL_SERVEI_JUSTIF_CAMP
		t0 = System.currentTimeMillis();
		registresModificats += serveiJustificantCampRepository.updateUsuariAuditoria(codiAntic, codiNou);
		log.info("> Servei-JustificantCamp: " + (System.currentTimeMillis() - t0) + " ms");
		//		PBL_SERVEI_REGLA
		t0 = System.currentTimeMillis();
		registresModificats += serveiReglaRepository.updateUsuariAuditoria(codiAntic, codiNou);
		log.info("> Servei-Regla: " + (System.currentTimeMillis() - t0) + " ms");

		return registresModificats;
	}

	private Usuari cloneUsuari(
			String codiNou,
			Usuari usuariAntic,
			String nom,
			String nif,
			String email,
			String idioma) {
		Usuari usuariNou = Usuari.getBuilderInicialitzat(codiNou, nom, nif).build();
		usuariNou.updateEmail(email);
		usuariNou.updateValorsPerDefecte(
				idioma,
				usuariAntic.getProcedimentId(),
				usuariAntic.getServeiCodi(),
				usuariAntic.getEntitatId(),
				usuariAntic.getDepartament(),
				usuariAntic.getFinalitat(),
				usuariAntic.getNumElementsPagina()
		);
		return usuariRepository.saveAndFlush(usuariNou);
	}

	private Usuari cloneUsuari(
			String codiNou,
			Usuari usuariAntic) {
		Usuari usuariNou = Usuari.getBuilderInicialitzat(codiNou, usuariAntic.getNom(), usuariAntic.getNif()).build();
		usuariNou.updateEmail(usuariAntic.getEmail());
		usuariNou.updateValorsPerDefecte(
				usuariAntic.getIdioma(),
				usuariAntic.getProcedimentId(),
				usuariAntic.getServeiCodi(),
				usuariAntic.getEntitatId(),
				usuariAntic.getDepartament(),
				usuariAntic.getFinalitat(),
				usuariAntic.getNumElementsPagina()
		);
		return usuariRepository.saveAndFlush(usuariNou);
	}

	@Transactional(rollbackFor = EntitatNotFoundException.class)
	@Override
	public void actualitzarDadesAdmin(
			Long id,
			String codi,
			String nif,
			String departament,
			boolean representant,
			boolean delegat,
			boolean auditor,
			boolean aplicacio,
			boolean afegir,
			boolean actiu) throws EntitatNotFoundException, UsuariExternNotFoundException {
		log.debug("Actualitzant dades no auditor de l'usuari (" +
				"codi=" + codi + ", " +
				"nif=" + nif + ") a l'entitat (" +
				"id=" + id + ")");
		Entitat entitat = entitatRepository.findOne(id);
		if (entitat == null) {
			log.debug("No s'ha trobat l'entitat (id=" + id + ")");
			throw new EntitatNotFoundException();
		}
		usuariActualitzarDades(
				entitat,
				codi,
				nif,
				departament,
				true,
				representant,
				true,
				delegat,
				true,
				auditor,
				true,
				aplicacio,
				afegir,
				actiu);
	}

	@Transactional(rollbackFor = EntitatNotFoundException.class)
	@Override
	public void actualitzarDadesRepresentant(
			Long id,
			String codi,
			String nif,
			String departament,
			boolean representant,
			boolean delegat,
			boolean aplicacio,
			boolean afegir,
			boolean actiu) throws EntitatNotFoundException, EntitatUsuariProtegitException, UsuariExternNotFoundException {
		log.debug("Actualitzant dades no auditor de l'usuari (codi=" + codi + ", nif=" + nif + ", actiu=" + actiu + ") a l'entitat (id=" + id + ")");
		Entitat entitat = entitatRepository.findOne(id);
		if (entitat == null) {
			log.debug("No s'ha trobat l'entitat (id=" + id + ")");
			throw new EntitatNotFoundException();
		}
		EntitatUsuari entitatUsuari;
		boolean idPerNif = (codi == null || codi.isEmpty());
		if (idPerNif)
			entitatUsuari = entitatUsuariRepository.findByEntitatIdAndUsuariNif(
					id,
					nif);
		else
			entitatUsuari = entitatUsuariRepository.findByEntitatIdAndUsuariCodi(
					id,
					codi);
		if (entitatUsuari != null && entitatUsuari.isPrincipal()) {
			log.debug("No es pot modificar un usuari principal (codi=" + codi + ", nif=" + nif + ") de l'entitat (id=" + id + ")");
			throw new EntitatUsuariProtegitException();
		}
		usuariActualitzarDades(
				entitat,
				codi,
				nif,
				departament,
				true,
				representant,
				true,
				delegat,
				false,
				false,
				true,
				aplicacio,
				afegir,
				actiu);
	}

	@Transactional(rollbackFor = EntitatNotFoundException.class)
	@Override
	public void actualitzarDadesAuditor(
			Long id,
			String codi,
			String nif,
			boolean auditor,
			boolean afegir) throws EntitatNotFoundException, EntitatUsuariProtegitException, UsuariExternNotFoundException {
		log.debug("Actualitzant dades auditor de l'usuari (codi=" + codi + ", nif=" + nif + ") a l'entitat (id=" + id + ")");
		Entitat entitat = entitatRepository.findOne(id);
		if (entitat == null) {
			log.debug("No s'ha trobat l'entitat (id=" + id + ")");
			throw new EntitatNotFoundException();
		}
		EntitatUsuari entitatUsuari;
		boolean idPerNif = (codi == null || codi.isEmpty());
		if (idPerNif)
			entitatUsuari = entitatUsuariRepository.findByEntitatIdAndUsuariNif(
					id,
					nif);
		else
			entitatUsuari = entitatUsuariRepository.findByEntitatIdAndUsuariCodi(
					id,
					codi);
		if (entitatUsuari != null && entitatUsuari.isPrincipal()) {
			log.debug("No es pot modificar un usuari principal (codi=" + codi + ", nif=" + nif + ") de l'entitat (id=" + id + ")");
			throw new EntitatUsuariProtegitException();
		}
		usuariActualitzarDades(
				entitat,
				codi,
				nif,
				null,
				false,
				false,
				false,
				false,
				true,
				auditor,
				false,
				false,
				afegir,
				null);
	}

	@Transactional(rollbackFor = {EntitatNotFoundException.class, EntitatUsuariNotFoundException.class})
	@Override
	public boolean establirPrincipal(
			Long id,
			String usuariCodi) throws EntitatNotFoundException, EntitatUsuariNotFoundException {
		log.debug("Marca l'usuari com a principal (codi=" + usuariCodi + ") a l'entitat (id=" + id + ")");
		Entitat entitat = entitatRepository.findOne(id);
		if (entitat == null) {
			log.debug("No s'ha trobat l'entitat (id=" + id + ")");
			throw new EntitatNotFoundException();
		}
		EntitatUsuari entitatUsuari = entitatUsuariRepository.findByEntitatIdAndUsuariCodi(id, usuariCodi);
		if (entitatUsuari == null) {
			log.debug("L'entitat (id=" + id + ") no té actiu l'usuari (codi=" + usuariCodi + ")");
			throw new EntitatUsuariNotFoundException();
		}
		return entitatUsuari.canviPrincipal();
	}

	@Transactional(rollbackFor = {EntitatNotFoundException.class, EntitatUsuariNotFoundException.class})
    @Override
    public boolean canviActiu(Long entitatId, String usuariCodi) throws EntitatNotFoundException, EntitatUsuariNotFoundException {
		log.debug("Activa l'usuari (codi=" + usuariCodi + ") a l'entitat (id=" + entitatId + ")");
		Entitat entitat = entitatRepository.findOne(entitatId);
		if (entitat == null) {
			log.debug("No s'ha trobat l'entitat (id=" + entitatId + ")");
			throw new EntitatNotFoundException();
		}
		EntitatUsuari entitatUsuari = entitatUsuariRepository.findByEntitatIdAndUsuariCodi(entitatId, usuariCodi);
		if (entitatUsuari == null) {
			log.debug("L'entitat (id=" + entitatId + ") no té configurat l'usuari (codi=" + usuariCodi + ")");
			throw new EntitatUsuariNotFoundException();
		}
		return entitatUsuari.canviActiu();
    }

    @Transactional(readOnly = true)
	@Override
	public List<InformeUsuariDto> informeUsuarisAgrupatsEntitatDepartament() {
		log.debug("Generant informe d'usuaris agrupats per entitat i departament");
		List<EntitatUsuari> usuaris = entitatUsuariRepository.findAllOrderByEntitatAndDepartament();
		Entitat entitatActual = null;
		String departamentActual = null;
		List<InformeUsuariDto> resposta = new ArrayList<InformeUsuariDto>();
		for (EntitatUsuari usuari: usuaris) {
			if (entitatActual == null)
				entitatActual = usuari.getEntitat();
			if (departamentActual == null)
				departamentActual = usuari.getDepartament();
			InformeUsuariDto informeUsuari = dtoMappingHelper.getMapperFacade().map(
					usuari,
					InformeUsuariDto.class);
			resposta.add(informeUsuari);
		}
		return resposta;
	}



	private void usuariActualitzarDades(
			Entitat entitat,
			String codi,
			String nif,
			String departament,
			boolean canviarRepresentant,
			boolean representant,
			boolean canviarDelegat,
			boolean delegat,
			boolean canviarAuditor,
			boolean auditor,
			boolean canviarAplicacio,
			boolean aplicacio,
			boolean afegir,
			Boolean actiu) throws UsuariExternNotFoundException {
		boolean idPerNif = (codi == null || codi.isEmpty());
		Usuari usuari;
		if (idPerNif) {
			usuari = usuariRepository.findByNif(nif);
		} else {
			usuari = usuariRepository.findByCodi(codi);
		}
		if (usuari == null || !usuari.isInicialitzat()) {
			// Consulta les dades de l'usuari al sistema extern
			DadesUsuari dadesUsuari = null;
			if (idPerNif) {
				try {
					dadesUsuari = pluginHelper.dadesUsuariConsultarAmbUsuariNif(nif);
				} catch (SistemaExternException ex) {
					log.warn("No s'han trobat les dades de l'usuari (nif=" + nif + ") al sistema extern");
					throw new UsuariExternNotFoundException();
				}
			} else {
				try {
					dadesUsuari = pluginHelper.dadesUsuariConsultarAmbUsuariCodi(codi);
				} catch (SistemaExternException ex) {
					log.warn("No s'han trobat les dades de l'usuari (codi=" + codi + ") al sistema extern");
					throw new UsuariExternNotFoundException();
				}
			}
			if (usuari == null) {
				// Si l'usuari no existeix el crea
				if (dadesUsuari != null) {
					usuari = usuariRepository.save(
							Usuari.getBuilderInicialitzat(
									dadesUsuari.getCodi(),
									dadesUsuari.getNom(),
									dadesUsuari.getNif()).build());
				} else {
					if (idPerNif) {
						usuari = usuariRepository.save(
								Usuari.getBuilderNoInicialitzatNif(
										nif).build());
					} else {
						usuari = usuariRepository.save(
								Usuari.getBuilderNoInicialitzatCodi(
										codi).build());
					}
				}
			} else {
				// Si l'usuari ja existeix actualitza les seves dades
				if (!usuari.isInicialitzat() && dadesUsuari != null) {
					log.debug("Actualitzant les dades de l'usuari (codi=" + usuari.getCodi() + ")");
					usuari = usuariHelper.moure(
							usuari,
							dadesUsuari,
							usuariRepository,
							procedimentServeiRepository,
							aclService);
				}
			}
		}
		// Crea un registre de tipus entitatUsuari si no existeix
		EntitatUsuari entitatUsuari;
		if (idPerNif) {
			entitatUsuari = entitatUsuariRepository.findByEntitatIdAndUsuariNif(
					entitat.getId(),
					nif);
		} else {
			entitatUsuari = entitatUsuariRepository.findByEntitatIdAndUsuariCodi(
					entitat.getId(),
					codi);
		}
		if (entitatUsuari == null) {
			entitatUsuari = EntitatUsuari.getBuilder(
					entitat,
					usuari,
					departament,
					representant,
					delegat,
					auditor,
					aplicacio,
					(actiu != null) ? actiu : true).build();
			entitatUsuariRepository.save(entitatUsuari);
		} else {
			boolean representatPerUpdate = (canviarRepresentant) ? representant : entitatUsuari.isRepresentant();
			boolean delegatPerUpdate = (canviarDelegat) ? delegat : entitatUsuari.isDelegat();
			boolean auditorPerUpdate = (canviarAuditor) ? auditor : entitatUsuari.isAuditor();
			boolean aplicacioPerUpdate = (canviarAplicacio) ? aplicacio : entitatUsuari.isAuditor();
			if (afegir) {
				representatPerUpdate = entitatUsuari.isRepresentant() || representant;
				delegatPerUpdate = entitatUsuari.isDelegat() || delegat;
				auditorPerUpdate = entitatUsuari.isAuditor() || auditor;
				aplicacioPerUpdate = entitatUsuari.isAplicacio() || aplicacio;
			}
			entitatUsuari.update(
					departament,
					representatPerUpdate,
					delegatPerUpdate,
					auditorPerUpdate,
					aplicacioPerUpdate,
					(actiu != null) ? actiu : entitatUsuari.isActiu());
		}
		cacheHelper.evictPermisosPerDelegat(usuari.getCodi());
	}

	private UsuariDto toUsuariDtoAmbRols(
			Usuari usuari) {
		UsuariDto dto = dtoMappingHelper.convertir(
				usuari,
				UsuariDto.class);
		if (usuari.getEntitats() != null && usuari.getEntitats().size() > 1) {
			Set<EntitatUsuari> entitats = usuari.getEntitats();
			int entitatsUsuariActives = 0;
			for (EntitatUsuari entitatUsuari : entitats) {
                if (entitatUsuari.getEntitat().isActiva()) {
					entitatsUsuariActives++;
					if (entitatsUsuariActives > 1) {
						dto.setHasMultiplesEntitats(entitatsUsuariActives > 1);
						break;
					}
				}
			}
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth.getAuthorities() != null) {
			String[] rols = new String[auth.getAuthorities().size()];
			int index = 0;
			for (GrantedAuthority grantedAuthority: auth.getAuthorities()) {
				switch (grantedAuthority.getAuthority()) {
				case "ROLE_ADMIN":
					rols[index++] = "PBL_ADMIN";
					break;
				case "ROLE_REPRES":
					rols[index++] = "PBL_REPRES";
					break;
				case "ROLE_DELEG":
					rols[index++] = "PBL_DELEG";
					break;
				case "ROLE_AUDIT":
					rols[index++] = "PBL_AUDIT";
					break;
				case "ROLE_SUPERAUD":
					rols[index++] = "PBL_SUPERAUD";
					break;
				case "ROLE_WS":
					rols[index++] = "PBL_WS";
					break;
				case "ROLE_REPORT":
					rols[index++] = "PBL_REPORT";
					break;
				default:
					rols[index++] = grantedAuthority.getAuthority();
				}
			}
			dto.setRols(rols);
		}
		return dto;
	}

}
