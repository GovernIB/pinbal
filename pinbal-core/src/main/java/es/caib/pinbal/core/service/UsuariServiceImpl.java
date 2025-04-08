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
    public void updateUsuariCodi(String codiAntic, String codiNou) {
        Usuari usuariAntic = usuariRepository.findByCodi(codiAntic);
		if (usuariAntic == null) {
			throw new NotFoundException(codiAntic, Usuari.class);
		}

		Usuari usuariNou = cloneUsuari(codiNou, usuariAntic);

		// Actualitzam la informació de auditoria de les taules:
		updateUsuariAuditoria(codiAntic, codiNou);

		// Actualitazam els permisos assignats per ACL
		updateUsuariPermisos(codiAntic, codiNou);

		// Actualitzam les referencis a l'usuari a taules:
		updateUsuariReferencies(codiAntic, codiNou);

		// Eliminam l'usuari antic
		usuariRepository.delete(usuariAntic);
	}

	private void updateUsuariPermisos(String codiAntic, String codiNou) {
		usuariRepository.updateUsuariPermis(codiAntic, codiNou);
		usuariRepository.flush();
	}

	private void updateUsuariReferencies(String codiAntic, String codiNou) {
		//		PBL_CONSULTA_HIST_LIST.USUARICODI
		llistatHistoricConsultaRepository.updateUsuariCodi(codiAntic, codiNou);
		//		PBL_CONSULTA_LIST.USUARICODI
		llistatConsultaRepository.updateUsuariCodi(codiAntic, codiNou);
		//		PBL_EXPLOT_CONSULTA_DIM.USUARI_CODI
		explotConsultaDimensioRepository.updateUsuariCodi(codiAntic, codiNou);
		//		PBL_ENTITAT_USUARI.USUARI_ID
		entitatUsuariRepository.updateUsuariCodi(codiAntic, codiNou);
	}

	private void updateUsuariAuditoria(String codiAntic, String codiNou) {
		//		PBL_AVIS
		avisRepository.updateUsuariAuditoria(codiAntic, codiNou);
		avisRepository.flush();
		//		PBL_CONFIG
		configRepository.updateUsuariAuditoria(codiAntic, codiNou);
		avisRepository.flush();
		//		PBL_CONSULTA
		consultaRepository.updateUsuariAuditoria(codiAntic, codiNou);
		avisRepository.flush();
		//		PBL_CONSULTA_HIST
		historicConsultaRepository.updateUsuariAuditoria(codiAntic, codiNou);
		avisRepository.flush();
		//		PBL_ENTITAT
		entitatRepository.updateUsuariAuditoria(codiAntic, codiNou);
		avisRepository.flush();
		//		PBL_ENTITAT_SERVEI
		entitatServeiRepository.updateUsuariAuditoria(codiAntic, codiNou);
		avisRepository.flush();
		//		PBL_ENTITAT_USUARI
		entitatUsuariRepository.updateUsuariAuditoria(codiAntic, codiNou);
		avisRepository.flush();
		//		PBL_MON_INT
		integracioAccioRepository.updateUsuariAuditoria(codiAntic, codiNou);
		avisRepository.flush();
		//		PBL_MON_INT_PARAM
		integracioAccioParamRepository.updateUsuariAuditoria(codiAntic, codiNou);
		avisRepository.flush();
		//		PBL_ORGAN_GESTOR
		organGestorRepository.updateUsuariAuditoria(codiAntic, codiNou);
		avisRepository.flush();
		//		PBL_PROCEDIMENT
		procedimentRepository.updateUsuariAuditoria(codiAntic, codiNou);
		avisRepository.flush();
		//		PBL_PROCEDIMENT_SERVEI
		procedimentServeiRepository.updateUsuariAuditoria(codiAntic, codiNou);
		avisRepository.flush();
		//		PBL_SERVEI_BUS
		serveiBusRepository.updateUsuariAuditoria(codiAntic, codiNou);
		avisRepository.flush();
		//		PBL_SERVEI_CAMP
		serveiCampRepository.updateUsuariAuditoria(codiAntic, codiNou);
		avisRepository.flush();
		//		PBL_SERVEI_CAMP_GRUP
		serveiCampGrupRepository.updateUsuariAuditoria(codiAntic, codiNou);
		avisRepository.flush();
		//		PBL_SERVEI_CONFIG
		serveiConfigRepository.updateUsuariAuditoria(codiAntic, codiNou);
		avisRepository.flush();
		//		PBL_SERVEI_JUSTIF_CAMP
		serveiJustificantCampRepository.updateUsuariAuditoria(codiAntic, codiNou);
		avisRepository.flush();
		//		PBL_SERVEI_REGLA
		serveiReglaRepository.updateUsuariAuditoria(codiAntic, codiNou);
		avisRepository.flush();
	}

	private Usuari cloneUsuari(String codiNou, Usuari usuariAntic) {
		Usuari usuariNou = Usuari.getBuilderNoInicialitzatCodi(codiNou).build();
		usuariNou.update(usuariAntic.getNom(), usuariAntic.getNif());
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
