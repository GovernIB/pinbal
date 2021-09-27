/**
 * 
 */
package es.caib.pinbal.core.service;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.w3c.dom.Element;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfReader;

import es.caib.pinbal.client.dadesobertes.DadesObertesRespostaConsulta;
import es.caib.pinbal.core.dto.CarregaDto;
import es.caib.pinbal.core.dto.CarregaDto.CarregaDetailedCountDto;
import es.caib.pinbal.core.dto.ConsultaDto;
import es.caib.pinbal.core.dto.ConsultaDto.Consentiment;
import es.caib.pinbal.core.dto.ConsultaDto.DocumentTipus;
import es.caib.pinbal.core.dto.ConsultaFiltreDto;
import es.caib.pinbal.core.dto.EmisorDto;
import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.dto.EstadisticaDto;
import es.caib.pinbal.core.dto.EstadistiquesFiltreDto;
import es.caib.pinbal.core.dto.EstadistiquesFiltreDto.EstadistiquesAgrupacioDto;
import es.caib.pinbal.core.dto.FitxerDto;
import es.caib.pinbal.core.dto.InformeGeneralEstatDto;
import es.caib.pinbal.core.dto.IntegracioAccioTipusEnumDto;
import es.caib.pinbal.core.dto.JustificantDto;
import es.caib.pinbal.core.dto.ProcedimentDto;
import es.caib.pinbal.core.dto.RecobrimentSolicitudDto;
import es.caib.pinbal.core.helper.DtoMappingHelper;
import es.caib.pinbal.core.helper.EmailReportEstatHelper;
import es.caib.pinbal.core.helper.ExcelHelper;
import es.caib.pinbal.core.helper.IntegracioHelper;
import es.caib.pinbal.core.helper.JustificantHelper;
import es.caib.pinbal.core.helper.PermisosHelper;
import es.caib.pinbal.core.helper.PluginHelper;
import es.caib.pinbal.core.helper.PropertiesHelper;
import es.caib.pinbal.core.helper.ServeiHelper;
import es.caib.pinbal.core.helper.UsuariHelper;
import es.caib.pinbal.core.model.Consulta;
import es.caib.pinbal.core.model.Consulta.EstatTipus;
import es.caib.pinbal.core.model.Consulta.JustificantEstat;
import es.caib.pinbal.core.model.Entitat;
import es.caib.pinbal.core.model.EntitatUsuari;
import es.caib.pinbal.core.model.Procediment;
import es.caib.pinbal.core.model.ProcedimentServei;
import es.caib.pinbal.core.model.ServeiCamp;
import es.caib.pinbal.core.model.ServeiCamp.ServeiCampTipus;
import es.caib.pinbal.core.model.ServeiConfig;
import es.caib.pinbal.core.repository.ConsultaRepository;
import es.caib.pinbal.core.repository.EntitatRepository;
import es.caib.pinbal.core.repository.EntitatUsuariRepository;
import es.caib.pinbal.core.repository.ProcedimentRepository;
import es.caib.pinbal.core.repository.ProcedimentServeiRepository;
import es.caib.pinbal.core.repository.ServeiCampRepository;
import es.caib.pinbal.core.repository.ServeiConfigRepository;
import es.caib.pinbal.core.repository.UsuariRepository;
import es.caib.pinbal.core.service.exception.AccesExternException;
import es.caib.pinbal.core.service.exception.ConsultaNotFoundException;
import es.caib.pinbal.core.service.exception.ConsultaScspComunicacioException;
import es.caib.pinbal.core.service.exception.ConsultaScspEstatException;
import es.caib.pinbal.core.service.exception.ConsultaScspException;
import es.caib.pinbal.core.service.exception.ConsultaScspGeneracioException;
import es.caib.pinbal.core.service.exception.ConsultaScspRespostaException;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.JustificantGeneracioException;
import es.caib.pinbal.core.service.exception.ProcedimentNotFoundException;
import es.caib.pinbal.core.service.exception.ProcedimentServeiNotFoundException;
import es.caib.pinbal.core.service.exception.ScspException;
import es.caib.pinbal.core.service.exception.ServeiNotAllowedException;
import es.caib.pinbal.core.service.exception.ValidacioDadesPeticioException;
import es.caib.pinbal.plugins.DadesUsuari;
import es.caib.pinbal.plugins.SistemaExternException;
import es.caib.pinbal.scsp.Resposta;
import es.caib.pinbal.scsp.ResultatEnviamentPeticio;
import es.caib.pinbal.scsp.ScspHelper;
import es.caib.pinbal.scsp.Solicitud;
import es.caib.plugins.arxiu.api.Expedient;
import es.caib.plugins.arxiu.api.ExpedientEstat;
import es.scsp.common.domain.core.EmisorCertificado;
import es.scsp.common.domain.core.Servicio;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementació dels mètodes per a gestionar les consultes al SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Service
public class ConsultaServiceImpl implements ConsultaService, ApplicationContextAware, MessageSourceAware {

	@Autowired
	private ConsultaRepository consultaRepository;
	@Autowired
	private ProcedimentRepository procedimentRepository;
	@Autowired
	private ProcedimentServeiRepository procedimentServeiRepository;
	@Autowired
	private EntitatRepository entitatRepository;
	@Autowired
	private UsuariRepository usuariRepository;
	@Autowired
	private ServeiConfigRepository serveiConfigRepository;
	@Autowired
	private ServeiCampRepository serveiCampRepository;
	@Autowired
	private EntitatUsuariRepository entitatUsuariRepository;

	@Autowired
	private JustificantHelper justificantHelper;
	@Autowired
	private DtoMappingHelper dtoMappingHelper;
	/*@Autowired
	private TransaccioHelper transaccioHelper;*/
	@Autowired
	private UsuariHelper usuariHelper;
	@Autowired
	private ServeiHelper serveiHelper;
	@Autowired
	private PluginHelper pluginHelper;

	@Autowired
	private MutableAclService aclService;
	
	@Autowired
	private IntegracioHelper integracioHelper;

	@Autowired
	private PlatformTransactionManager transactionManager;
	
	@Autowired
	private ExcelHelper excelHelper;
	
	@Autowired
	private EmailReportEstatHelper emailReportEstatHelper;

	private ApplicationContext applicationContext;
	private MessageSource messageSource;
	private ScspHelper scspHelper;

	private Map<Long, Object> justificantLocks = new HashMap<Long, Object>();
	private List<CarregaDto> carreguesAny;
	private List<CarregaDto> carreguesMes;
	private List<CarregaDto> carreguesDia;
	private List<CarregaDto> carreguesHora;
	private List<CarregaDto> carreguesMinut;

	@Transactional(rollbackFor = {ProcedimentServeiNotFoundException.class, ServeiNotAllowedException.class, ConsultaScspException.class})
	@Override
	public ConsultaDto novaConsulta(
			ConsultaDto consulta) throws ProcedimentServeiNotFoundException, ServeiNotAllowedException, ConsultaScspException {
		log.debug("Executant consulta del servei (codi=" + consulta.getServeiCodi() + "): " + consulta);
		String accioDescripcio = "Consulta del servei";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("codi", consulta.getServeiCodi());
		long t0 = System.currentTimeMillis();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		copiarPropertiesToDb();
		ProcedimentServei procedimentServei = procedimentServeiRepository.findByProcedimentIdAndServei(
				consulta.getProcedimentId(),
				consulta.getServeiCodi());
		if (procedimentServei == null || !procedimentServei.isActiu()) {
			log.debug("No s'ha trobat el servei (codi=" + consulta.getServeiCodi() + ") del procediment (id=" + consulta.getProcedimentId() + ")");
			throw new ProcedimentServeiNotFoundException();
		}
		Entitat entitat = procedimentServei.getProcediment().getEntitat();
		if (!serveiHelper.isServeiPermesPerUsuari(
				entitat,
				procedimentServei.getProcediment(),
				consulta.getServeiCodi())) {
			log.debug("L'usuari no te accés al servei (codi=" + auth.getName() + ")");
			throw new ServeiNotAllowedException();
		}
		Consulta conslt = null;
		try {
			String idPeticion = getScspHelper().generarIdPeticion(consulta.getServeiCodi());
		/*} catch (Exception ex) {
			String errorDescripcio = "Error al generar identificador per a consulta (servei=" + consulta.getServeiCodi() + ", usuari=" + auth.getName() + "): " + ex.getMessage();
			// Error al generar l'identificador
			log.error(errorDescripcio, ex);
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_SERVEIS_SCSP,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new ScspException(ex.getMessage(), ex);
		}
		try {*/
			String titularDocumentTipus = null;
			if (consulta.getTitularDocumentTipus() != null) {
				titularDocumentTipus = consulta.getTitularDocumentTipus().toString();
			}
			conslt = Consulta.getBuilder(
					idPeticion,
					consulta.getFuncionariNom(),
					consulta.getFuncionariNif(),
					titularDocumentTipus,
					consulta.getTitularDocumentNum(),
					consulta.getTitularNom(),
					consulta.getTitularLlinatge1(),
					consulta.getTitularLlinatge2(),
					consulta.getTitularNomComplet(),
					consulta.getDepartamentNom(),
					procedimentServei,
					false,
					false,
					null).
					build();
			conslt.updateEstat(EstatTipus.Processant);
			processarDadesEspecifiquesSegonsCamps(
					consulta.getServeiCodi(),
					consulta.getDadesEspecifiques());
			ResultatEnviamentPeticio resultat = enviarPeticioScsp(
					entitat.getId(),
					consulta.getServeiCodi(),
					idPeticion,
					Arrays.asList(convertirEnSolicitud(consulta, procedimentServei)),
					true,
					conslt.isRecobriment());
			if (resultat.getIdsSolicituds() != null && resultat.getIdsSolicituds().length > 0) {
				conslt.updateScspSolicitudId(resultat.getIdsSolicituds()[0]);
			}
			updateEstatConsulta(conslt, resultat);
			/*if (EstatTipus.Tramitada.equals(c.getEstat())) {
				justificantHelper.generarCustodiarJustificantPendent(
						c,
						getScspHelper());
			}*/
			Consulta saved = consultaRepository.save(conslt);
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_SERVEIS_SCSP,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return dtoMappingHelper.getMapperFacade().map(
					saved,
					ConsultaDto.class);
		/*} catch (Exception ex) {
			String errorDescripcio = "Excepció al realitzar consulta SCSP (id=" + idPeticion + ", servei=" + consulta.getServeiCodi() + ", usuari=" + auth.getName() + ")" + ex.getMessage();
			// Error al realitzar la petició (no s'arriba a enviar)
			log.error(errorDescripcio, ex);
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_SERVEIS_SCSP,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new ScspException(ex.getMessage(), ex);
		}*/
		} catch (ConsultaScspException ex) {
			return processarConsultaScspException(
					ex,
					conslt,
					accioDescripcio,
					accioParams,
					t0);
		}
	}

	@Transactional(rollbackFor = {ProcedimentServeiNotFoundException.class, ServeiNotAllowedException.class, ConsultaScspException.class})
	@Override
	public ConsultaDto novaConsultaInit(
			ConsultaDto consulta) throws ProcedimentServeiNotFoundException, ServeiNotAllowedException, ConsultaScspGeneracioException {
		log.debug("Executant consulta del servei (init) (codi=" + consulta.getServeiCodi() + "): " + consulta);
		String accioDescripcio = "Consulta del servei (init)";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("codi", consulta.getServeiCodi());
		long t0 = System.currentTimeMillis();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		copiarPropertiesToDb();
		ProcedimentServei procedimentServei = procedimentServeiRepository.findByProcedimentIdAndServei(
				consulta.getProcedimentId(),
				consulta.getServeiCodi());
		if (procedimentServei == null || !procedimentServei.isActiu()) {
			log.debug("No s'ha trobat el servei (codi=" + consulta.getServeiCodi() + ") del procediment (id=" + consulta.getProcedimentId() + ")");
			throw new ProcedimentServeiNotFoundException();
		}
		if (!serveiHelper.isServeiPermesPerUsuari(
				procedimentServei.getProcediment().getEntitat(),
				procedimentServei.getProcediment(),
				consulta.getServeiCodi())) {
			log.debug("L'usuari no te accés al servei (codi=" + auth.getName() + ")");
			throw new ServeiNotAllowedException();
		}
		String idPeticion = getScspHelper().generarIdPeticion(consulta.getServeiCodi());
		integracioHelper.addAccioOk(
				IntegracioHelper.INTCODI_SERVEIS_SCSP,
				accioDescripcio,
				accioParams,
				IntegracioAccioTipusEnumDto.ENVIAMENT,
				System.currentTimeMillis() - t0);
		/*} catch (Exception ex) {
			String errorDescripcio = "Error al generar identificador per a consulta (servei=" + consulta.getServeiCodi() + ", usuari=" + auth.getName() + ")";
			// Error al generar l'identificador
			log.error(errorDescripcio, ex);
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_SERVEIS_SCSP,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new ScspException(ex.getMessage(), ex);
		}*/
		String titularDocumentTipus = null;
		if (consulta.getTitularDocumentTipus() != null)
			titularDocumentTipus = consulta.getTitularDocumentTipus().toString();
		Consulta constl = Consulta.getBuilder(
				idPeticion,
				consulta.getFuncionariNom(),
				consulta.getFuncionariNif(),
				titularDocumentTipus,
				consulta.getTitularDocumentNum(),
				consulta.getTitularNom(),
				consulta.getTitularLlinatge1(),
				consulta.getTitularLlinatge2(),
				consulta.getTitularNomComplet(),
				consulta.getDepartamentNom(),
				procedimentServei,
				false,
				false,
				null).
				build();
		constl.updateEstat(EstatTipus.Processant);
		Consulta saved = consultaRepository.save(constl);
		return dtoMappingHelper.getMapperFacade().map(
				saved,
				ConsultaDto.class);
	}

	@Transactional
	@Override
	public void novaConsultaEnviament(
			Long consultaId,
			ConsultaDto consulta) throws ProcedimentServeiNotFoundException, ConsultaNotFoundException, ConsultaScspException {
		log.debug("Executant consulta del servei (enviament) (consultaId=" + consultaId + ")");
		String accioDescripcio = "Consulta del servei (enviament)";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("consultaId", consultaId.toString());
		long t0 = System.currentTimeMillis();
		ProcedimentServei procedimentServei = procedimentServeiRepository.findByProcedimentIdAndServei(
				consulta.getProcedimentId(),
				consulta.getServeiCodi());
		if (procedimentServei == null || !procedimentServei.isActiu()) {
			log.debug("No s'ha trobat el servei (codi=" + consulta.getServeiCodi() + ") del procediment (id=" + consulta.getProcedimentId() + ")");
			throw new ProcedimentServeiNotFoundException();
		}
		Consulta conslt = consultaRepository.findOne(consultaId);
		if (conslt == null) {
			log.debug("No s'ha trobat la consulta (id=" + consultaId + ")");
			throw new ConsultaNotFoundException();
		}
		try {
			processarDadesEspecifiquesSegonsCamps(
					consulta.getServeiCodi(),
					consulta.getDadesEspecifiques());
		/*} catch (Exception ex) {
			String errorDescripcio = "Error al processar dades específiques de la consulta (consultaId=" + consultaId + ")";
			log.error(errorDescripcio, ex);
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_SERVEIS_SCSP,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new ScspException(ex.getMessage(), ex);
		}*/
			List<Solicitud> solicituds = new ArrayList<Solicitud>();
			solicituds.add(convertirEnSolicitud(consulta, procedimentServei));
			/*ResultatEnviamentPeticio resultat = */enviarPeticioScsp(
					procedimentServei.getProcediment().getEntitat().getId(),
					consulta.getServeiCodi(),
					conslt.getScspPeticionId(),
					Arrays.asList(convertirEnSolicitud(consulta, procedimentServei)),
					true,
					conslt.isRecobriment());
			/*updateEstatConsulta(conslt, resultat);
			if (resultat.isError()) {
				throw new ConsultaScspRespostaException(
						consulta.getScspPeticionId(),
						"[" + resultat.getErrorCodi() + "] " + resultat.getErrorDescripcio());
			}*/
		} catch (ConsultaScspException ex) {
			processarConsultaScspException(
					ex,
					conslt,
					accioDescripcio,
					accioParams,
					t0);
		}
	}

	@Transactional(rollbackFor = {ConsultaScspException.class})
	@Override
	public ConsultaDto novaConsultaEstat(
			Long consultaId) throws ConsultaNotFoundException, ConsultaScspException {
		log.debug("Executant consulta del servei (estat) (consultaId=" + consultaId + ")");
		String accioDescripcio = "Consulta del servei (estat)";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("consultaId", consultaId.toString());
		long t0 = System.currentTimeMillis();
		Consulta consulta = consultaRepository.findOne(consultaId);
		if (consulta == null) {
			log.debug("No s'ha trobat la consulta (id=" + consultaId + ")");
			throw new ConsultaNotFoundException();
		}
		try {
			ResultatEnviamentPeticio resultat = getScspHelper().recuperarResultatEnviamentPeticio(
					consulta.getScspPeticionId());
			if (resultat.getIdsSolicituds() != null && resultat.getIdsSolicituds().length > 0) {
				consulta.updateScspSolicitudId(resultat.getIdsSolicituds()[0]);
			}
			updateEstatConsulta(consulta, resultat);
			/*if (EstatTipus.Tramitada.equals(consulta.getEstat())) {
				justificantHelper.generarCustodiarJustificantPendent(
						consulta,
						getScspHelper());
			}*/
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_SERVEIS_SCSP,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return dtoMappingHelper.getMapperFacade().map(
					consulta,
					ConsultaDto.class);
		} catch (Exception ex) {
			String errorDescripcio = "Error al obtenir l'estat de la petició corresponent a la consulta (consultaId=" + consultaId + ")";
			log.error(errorDescripcio, ex);
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_SERVEIS_SCSP,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new ConsultaScspEstatException(consulta.getScspPeticionId(), ex);
		}
	}

	@Transactional(rollbackFor = {ProcedimentServeiNotFoundException.class, ServeiNotAllowedException.class, ConsultaScspException.class})
	@Override
	public ConsultaDto novaConsultaMultiple(
			ConsultaDto consulta) throws ValidacioDadesPeticioException, ProcedimentServeiNotFoundException, ServeiNotAllowedException, ConsultaScspException {
		log.debug("Executant consulta múltiple del servei (codi=" + consulta.getServeiCodi() + "): " + consulta);
		String accioDescripcio = "Consulta múltiple del servei";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("codi", consulta.getServeiCodi());
		long t0 = System.currentTimeMillis();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		copiarPropertiesToDb();
		ProcedimentServei procedimentServei = procedimentServeiRepository.findByProcedimentIdAndServei(
				consulta.getProcedimentId(),
				consulta.getServeiCodi());
		if (procedimentServei == null || !procedimentServei.isActiu()) {
			log.debug("No s'ha trobat el servei (codi=" + consulta.getServeiCodi() + ") del procediment (id=" + consulta.getProcedimentId() + ")");
			throw new ProcedimentServeiNotFoundException();
		}
		if (!serveiHelper.isServeiPermesPerUsuari(
				procedimentServei.getProcediment().getEntitat(),
				procedimentServei.getProcediment(),
				consulta.getServeiCodi())) {
			log.debug("L'usuari no te accés al servei (codi=" + auth.getName() + ")");
			throw new ServeiNotAllowedException();
		}
		Consulta conslt = null;
		try {
			String idPeticion = getScspHelper().generarIdPeticion(consulta.getServeiCodi());
			conslt = Consulta.getBuilder(
					idPeticion,
					consulta.getFuncionariNom(),
					consulta.getFuncionariNif(),
					null,
					null,
					null,
					null,
					null,
					null,
					consulta.getDepartamentNom(),
					procedimentServei,
					false,
					true,
					null).
					build();
			conslt.updateEstat(EstatTipus.Pendent);
			List<Solicitud> solicituds = convertirEnMultiplesSolicituds(consulta, procedimentServei);
			ResultatEnviamentPeticio resultat = enviarPeticioScsp(
					procedimentServei.getProcediment().getEntitat().getId(),
					consulta.getServeiCodi(),
					idPeticion,
					solicituds,
					false,
					conslt.isRecobriment());
			updateEstatConsulta(conslt, resultat);
			Consulta saved = consultaRepository.save(conslt);
			int solicitudIndex = 0;
			for (Solicitud solicitud: solicituds) {
				String titularDocumentTipus = null;
				if (solicitud.getTitularDocumentTipus() != null)
					titularDocumentTipus = solicitud.getTitularDocumentTipus().toString();
				Consulta cs = Consulta.getBuilder(
						idPeticion,
						consulta.getFuncionariNom(),
						consulta.getFuncionariNif(),
						titularDocumentTipus,
						solicitud.getTitularDocument(),
						solicitud.getTitularNom(),
						solicitud.getTitularLlinatge1(),
						solicitud.getTitularLlinatge2(),
						solicitud.getTitularNomComplet(),
						consulta.getDepartamentNom(),
						procedimentServei,
						false,
						false,
						conslt).
						build();
				cs.updateScspSolicitudId(resultat.getIdsSolicituds()[solicitudIndex++]);
				updateEstatConsulta(cs, resultat);
				consultaRepository.save(cs);
			}
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_SERVEIS_SCSP,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return dtoMappingHelper.getMapperFacade().map(
					saved,
					ConsultaDto.class);
		/*} catch (Exception ex) {
			// Error al realitzar la petició (no s'arriba a enviar)
			String errorDescripcio = "Excepció al realitzar consulta SCSP (id=" + idPeticion + ", servei=" + consulta.getServeiCodi() + ", usuari=" + auth.getName() + ")";
			log.error(errorDescripcio, ex);
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_SERVEIS_SCSP,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new ScspException(ex.getMessage(), ex);
		}*/
		} catch (ConsultaScspException ex) {
			return processarConsultaScspException(
					ex,
					conslt,
					accioDescripcio,
					accioParams,
					t0);
		}
	}

	@Transactional(rollbackFor = {EntitatNotFoundException.class, ProcedimentNotFoundException.class, ProcedimentNotFoundException.class, AccesExternException.class, ServeiNotAllowedException.class, ConsultaScspException.class})
	@Override
	public ConsultaDto novaConsultaRecobriment(
			String serveiCodi,
			RecobrimentSolicitudDto solicitud) throws EntitatNotFoundException, ProcedimentNotFoundException, ProcedimentServeiNotFoundException, ServeiNotAllowedException, ConsultaScspException {
		log.debug("Executant consulta del servei via recobriment (" +
				"entitatCif=" + solicitud.getEntitatCif() + ", " +
				"procedimentCodi=" + solicitud.getProcedimentCodi() + ", " +
				"serveiCodi=" + serveiCodi + ")");
		String accioDescripcio = "Consulta del servei via recobriment";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("entitatCif", solicitud.getEntitatCif());
		accioParams.put("procedimentCodi", solicitud.getProcedimentCodi());
		accioParams.put("serveiCodi", serveiCodi);
		long t0 = System.currentTimeMillis();
		copiarPropertiesToDb();
		String entitatCif = solicitud.getEntitatCif();
		String procedimentCodi = solicitud.getProcedimentCodi();
		Entitat entitat = entitatRepository.findByCif(entitatCif);
		if (entitat == null) {
			log.debug("No s'ha trobat l'entitat (entitatCif=" + entitatCif + ")");
			throw new EntitatNotFoundException();
		}
		Procediment procediment = procedimentRepository.findByEntitatAndCodi(entitat, procedimentCodi);
		if (procediment == null) {
			log.debug("No s'ha trobat el procediment (entitatCif=" + entitatCif + ", procedimentCodi=" + procedimentCodi + ")");
			throw new ProcedimentNotFoundException();
		}
		ProcedimentServei procedimentServei = procedimentServeiRepository.findByProcedimentIdAndServei(
				procediment.getId(),
				serveiCodi);
		if (procedimentServei == null || !procedimentServei.isActiu()) {
			log.debug("No s'ha trobat el servei (serveiCodi=" + serveiCodi + ") del procediment (id=" + procediment.getId() + ")");
			throw new ProcedimentServeiNotFoundException();
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		usuariHelper.init(auth.getName());
		if (!serveiHelper.isServeiPermesPerUsuari(
				entitat,
				procediment,
				serveiCodi)) {
			log.debug("L'usuari no te accés al servei (usuari=" + auth.getName() + ")");
			throw new ServeiNotAllowedException();
		}
		Consulta conslt = null;
		try {
			String idPeticion = getScspHelper().generarIdPeticion(serveiCodi);
			conslt = Consulta.getBuilder(
					idPeticion,
					solicitud.getFuncionariNom(),
					solicitud.getFuncionariNif(),
					(solicitud.getTitularDocumentTipus() != null) ? solicitud.getTitularDocumentTipus().toString() : null,
					solicitud.getTitularDocumentNum(),
					solicitud.getTitularNom(),
					solicitud.getTitularLlinatge1(),
					solicitud.getTitularLlinatge2(),
					solicitud.getTitularNomComplet(),
					solicitud.getDepartamentNom(),
					procedimentServei,
					true,
					false,
					null).
					build();
			conslt.updateEstat(EstatTipus.Pendent);
			Solicitud solicitudEnviar = convertirEnSolicitud(
					entitat,
					procediment,
					serveiCodi,
					solicitud.getFuncionariNom(),
					solicitud.getFuncionariNif(),
					solicitud.getTitularDocumentTipus(),
					solicitud.getTitularDocumentNum(),
					solicitud.getTitularNom(),
					solicitud.getTitularLlinatge1(),
					solicitud.getTitularLlinatge2(),
					solicitud.getTitularNomComplet(),
					solicitud.getFinalitat(),
					solicitud.getConsentiment(),
					solicitud.getDepartamentNom(),
					solicitud.getUnitatTramitadoraCodi(),
					solicitud.getExpedientId(),
					getScspHelper().copiarDadesEspecifiquesRecobriment(
							serveiCodi,
							solicitud.getDadesEspecifiques(),
							isGestioXsdActiva(serveiCodi)),
					procedimentServei);
			ResultatEnviamentPeticio resultat = enviarPeticioScsp(
					entitat.getId(),
					serveiCodi,
					idPeticion,
					Arrays.asList(solicitudEnviar),
					true,
					conslt.isRecobriment());
			conslt.updateEstat(EstatTipus.Processant);
			if (resultat.getIdsSolicituds() != null && resultat.getIdsSolicituds().length > 0) {
				conslt.updateScspSolicitudId(resultat.getIdsSolicituds()[0]);
			}
			updateEstatConsulta(conslt, resultat);
			Consulta saved = consultaRepository.save(conslt);
			ConsultaDto resposta = dtoMappingHelper.getMapperFacade().map(
					saved,
					ConsultaDto.class);
			if (resultat.isError()) {
				// Si la petició ha produit un error ompl els camps per permetre
				// al recobriment generar el missatge amb informació de l'error.
				resposta.setRespostaEstadoCodigo(resultat.getErrorCodi());
				resposta.setRespostaEstadoError(resultat.getErrorDescripcio());
			} else {
				// Si no hi ha error genera el justificant
				/*justificantHelper.generarCustodiarJustificantPendent(
						saved,
						getScspHelper());*/
			}
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_SERVEIS_SCSP,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return resposta;
		} catch (ConsultaScspException ex) {
			return processarConsultaScspException(
					ex,
					conslt,
					accioDescripcio,
					accioParams,
					t0);
		}
		/*} catch (Exception ex) {
			// Error al realitzar la petició (no s'arriba a enviar)
			String errorDescripcio = "Excepció al realitzar consulta SCSP (id=" + idPeticion + ", servei=" + serveiCodi + ", usuari=" + auth.getName() + ")";
			log.error(errorDescripcio, ex);
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_SERVEIS_SCSP,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new ScspException(ex.getMessage(), ex);
		}*/
	}

	@Transactional(rollbackFor = {EntitatNotFoundException.class, ProcedimentNotFoundException.class, ProcedimentNotFoundException.class, AccesExternException.class, ServeiNotAllowedException.class, ConsultaScspException.class})
	@Override
	public ConsultaDto novaConsultaRecobrimentInit(
			String serveiCodi,
			RecobrimentSolicitudDto solicitud) throws EntitatNotFoundException, ProcedimentNotFoundException, ProcedimentServeiNotFoundException, ServeiNotAllowedException, ConsultaScspException {
		log.debug("Executant consulta del servei via recobriment (init) (" +
				"entitatCif=" + solicitud.getEntitatCif() + ", " +
				"procedimentCodi=" + solicitud.getProcedimentCodi() + ", " +
				"serveiCodi=" + serveiCodi + ")");
		String accioDescripcio = "Consulta del servei via recobriment (init)";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("entitatCif", solicitud.getEntitatCif());
		accioParams.put("procedimentCodi", solicitud.getProcedimentCodi());
		accioParams.put("serveiCodi", serveiCodi);
		long t0 = System.currentTimeMillis();
		copiarPropertiesToDb();
		Entitat entitat = entitatRepository.findByCif(solicitud.getEntitatCif());
		if (entitat == null) {
			log.debug("No s'ha trobat l'entitat (entitatCif=" + solicitud.getEntitatCif() + ")");
			throw new EntitatNotFoundException();
		}
		Procediment procediment = procedimentRepository.findByEntitatAndCodi(entitat, solicitud.getProcedimentCodi());
		if (procediment == null) {
			log.debug("No s'ha trobat el procediment (entitatCif=" + solicitud.getEntitatCif() + ", procedimentCodi=" + solicitud.getProcedimentCodi() + ")");
			throw new ProcedimentNotFoundException();
		}
		ProcedimentServei procedimentServei = procedimentServeiRepository.findByProcedimentIdAndServei(
				procediment.getId(),
				serveiCodi);
		if (procedimentServei == null || !procedimentServei.isActiu()) {
			log.debug("No s'ha trobat el servei (serveiCodi=" + serveiCodi + ") del procediment (id=" + procediment.getId() + ")");
			throw new ProcedimentServeiNotFoundException();
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		usuariHelper.init(auth.getName());
		if (!serveiHelper.isServeiPermesPerUsuari(
				entitat,
				procediment,
				serveiCodi)) {
			log.debug("L'usuari no te accés al servei (usuari=" + auth.getName() + ")");
			throw new ServeiNotAllowedException();
		}
		String idPeticion = getScspHelper().generarIdPeticion(serveiCodi);
		integracioHelper.addAccioOk(
				IntegracioHelper.INTCODI_SERVEIS_SCSP,
				accioDescripcio,
				accioParams,
				IntegracioAccioTipusEnumDto.ENVIAMENT,
				System.currentTimeMillis() - t0);
		Consulta conslt = Consulta.getBuilder(
				idPeticion,
				solicitud.getFuncionariNom(),
				solicitud.getFuncionariNif(),
				(solicitud.getTitularDocumentTipus() != null) ? solicitud.getTitularDocumentTipus().toString() : null,
				solicitud.getTitularDocumentNum(),
				solicitud.getTitularNom(),
				solicitud.getTitularLlinatge1(),
				solicitud.getTitularLlinatge2(),
				solicitud.getTitularNomComplet(),
				solicitud.getDepartamentNom(),
				procedimentServei,
				true,
				false,
				null).
				build();
		conslt.updateEstat(EstatTipus.Processant);
		Consulta saved = consultaRepository.save(conslt);
		ConsultaDto resposta = dtoMappingHelper.getMapperFacade().map(
				saved,
				ConsultaDto.class);
		return resposta;
	}
	@Transactional
	@Override
	public void novaConsultaRecobrimentEnviament(
			Long consultaId,
			RecobrimentSolicitudDto solicitud) throws ConsultaNotFoundException, ConsultaScspException {
		log.debug("Executant consulta del servei via recobriment (enviament) (" +
				"consultaId=" + consultaId + ")");
		String accioDescripcio = "Consulta del servei via recobriment (enviament)";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("consultaId", consultaId.toString());
		long t0 = System.currentTimeMillis();
		Consulta consulta = consultaRepository.findOne(consultaId);
		if (consulta == null) {
			log.debug("No s'ha trobat la consulta (id=" + consultaId + ")");
			throw new ConsultaNotFoundException();
		}
		try {
			ProcedimentServei procedimentServei = consulta.getProcedimentServei();
			DocumentTipus documentTipus = (consulta.getTitularDocumentTipus() != null) ? DocumentTipus.valueOf(consulta.getTitularDocumentTipus()) : null;
			Entitat entitat = procedimentServei.getProcediment().getEntitat();
			Solicitud solicitudEnviar = convertirEnSolicitud(
					entitat,
					procedimentServei.getProcediment(),
					procedimentServei.getServei(),
					consulta.getFuncionariNom(),
					consulta.getFuncionariDocumentNum(),
					documentTipus,
					consulta.getTitularDocumentNum(),
					consulta.getTitularNom(),
					consulta.getTitularLlinatge1(),
					consulta.getTitularLlinatge2(),
					consulta.getTitularNomComplet(),
					solicitud.getFinalitat(),
					solicitud.getConsentiment(),
					solicitud.getDepartamentNom(),
					solicitud.getUnitatTramitadoraCodi(),
					solicitud.getExpedientId(),
					getScspHelper().copiarDadesEspecifiquesRecobriment(
							procedimentServei.getServei(),
							solicitud.getDadesEspecifiques(),
							isGestioXsdActiva(procedimentServei.getServei())),
					procedimentServei);
			enviarPeticioScsp(
					entitat.getId(),
					procedimentServei.getServei(),
					consulta.getScspPeticionId(),
					Arrays.asList(solicitudEnviar),
					true,
					consulta.isRecobriment());
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_SERVEIS_SCSP,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
		} catch (ConsultaScspException ex) {
			processarConsultaScspException(
					ex,
					consulta,
					accioDescripcio,
					accioParams,
					t0);
		}
	}
	@Transactional(rollbackFor = {EntitatNotFoundException.class, ProcedimentNotFoundException.class, ProcedimentNotFoundException.class, AccesExternException.class, ServeiNotAllowedException.class, ScspException.class})
	@Override
	public ConsultaDto novaConsultaRecobrimentEstat(
			Long consultaId) throws ConsultaNotFoundException, ConsultaScspException {
		log.debug("Executant consulta del servei via recobriment (estat) (" +
				"consultaId=" + consultaId + ")");
		String accioDescripcio = "Consulta del servei via recobriment (estat)";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("consultaId", consultaId.toString());
		long t0 = System.currentTimeMillis();
		Consulta consulta = consultaRepository.findOne(consultaId);
		if (consulta == null) {
			log.debug("No s'ha trobat la consulta (id=" + consultaId + ")");
			throw new ConsultaNotFoundException();
		}
		try {
			ResultatEnviamentPeticio resultat = getScspHelper().recuperarResultatEnviamentPeticio(
					consulta.getScspPeticionId());
			if (resultat.getIdsSolicituds() != null && resultat.getIdsSolicituds().length > 0) {
				consulta.updateScspSolicitudId(resultat.getIdsSolicituds()[0]);
			}
			updateEstatConsulta(consulta, resultat);
			/*if (EstatTipus.Tramitada.equals(consulta.getEstat())) {
				justificantHelper.generarCustodiarJustificantPendent(
						consulta,
						getScspHelper());
			}*/

			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_SERVEIS_SCSP,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			return dtoMappingHelper.getMapperFacade().map(
					consulta,
					ConsultaDto.class);
		/*} catch (Exception ex) {
			log.error("Error al obtenir l'estat de la petició corresponent a la consulta (consultaId=" + consultaId + ")", ex);
			throw new ScspException(ex.getMessage(), ex);
		}*/
		} catch (Exception ex) {
			String errorDescripcio = "Error al obtenir l'estat de la petició corresponent a la consulta (consultaId=" + consultaId + ")";
			log.error(errorDescripcio, ex);
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_SERVEIS_SCSP,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new ConsultaScspEstatException(consulta.getScspPeticionId(), ex);
		}
	}

	@Transactional(rollbackFor = {EntitatNotFoundException.class, ProcedimentNotFoundException.class, ProcedimentServeiNotFoundException.class, ServeiNotAllowedException.class, ConsultaScspException.class})
	@Override
	public ConsultaDto novaConsultaRecobrimentMultiple(
			String serveiCodi,
			List<RecobrimentSolicitudDto> solicituds) throws EntitatNotFoundException, ProcedimentNotFoundException, ProcedimentServeiNotFoundException, ServeiNotAllowedException, ConsultaScspException {
		log.debug("Executant consulta múltiple del servei via recobriment (" +
				"serveiCodi=" + serveiCodi + ", " +
				"solicituds=" + ((solicituds != null) ? solicituds.size() : "") + ")");
		String accioDescripcio = "Consulta múltiple del servei via recobriment";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("serveiCodi", serveiCodi);
		accioParams.put("serveiCodi", (solicituds != null) ? String.valueOf(solicituds.size()) : "");
		long t0 = System.currentTimeMillis();
		copiarPropertiesToDb();
		RecobrimentSolicitudDto primeraSolicitud = solicituds.get(0);
		String entitatCif = primeraSolicitud.getEntitatCif();
		String procedimentCodi = primeraSolicitud.getProcedimentCodi();
		String funcionariNom = primeraSolicitud.getFuncionariNom();
		String funcionariNif = primeraSolicitud.getFuncionariNif();
		String departamentNom = primeraSolicitud.getDepartamentNom();
		Entitat entitat = entitatRepository.findByCif(entitatCif);
		if (entitat == null) {
			log.debug("No s'ha trobat l'entitat (entitatCif=" + entitatCif + ")");
			throw new EntitatNotFoundException();
		}
		Procediment procediment = procedimentRepository.findByEntitatAndCodi(entitat, procedimentCodi);
		if (procediment == null) {
			log.debug("No s'ha trobat el procediment (entitatCif=" + entitatCif + ", procedimentCodi=" + procedimentCodi + ")");
			throw new ProcedimentNotFoundException();
		}
		ProcedimentServei procedimentServei = procedimentServeiRepository.findByProcedimentIdAndServei(
				procediment.getId(),
				serveiCodi);
		if (procedimentServei == null || !procedimentServei.isActiu()) {
			log.debug("No s'ha trobat el servei (serveiCodi=" + serveiCodi + ") del procediment (id=" + procediment.getId() + ")");
			throw new ProcedimentServeiNotFoundException();
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		usuariHelper.init(auth.getName());
		if (!serveiHelper.isServeiPermesPerUsuari(
				entitat,
				procediment,
				serveiCodi)) {
			log.debug("L'usuari no te accés al servei (usuari=" + auth.getName() + ")");
			throw new ServeiNotAllowedException();
		}
		Consulta conslt = null;
		try {
			String idPeticion = getScspHelper().generarIdPeticion(serveiCodi);
			conslt = Consulta.getBuilder(
					idPeticion,
					funcionariNom,
					funcionariNif,
					null,
					null,
					null,
					null,
					null,
					null,
					departamentNom,
					procedimentServei,
					true,
					true,
					null).
					build();
			conslt.updateEstat(EstatTipus.Pendent);
			List<Solicitud> solicitudsEnviar = new ArrayList<Solicitud>();
			for (RecobrimentSolicitudDto solicitud: solicituds) {
				Solicitud solicitudEnviar = convertirEnSolicitud(
						entitat,
						procediment,
						serveiCodi,
						funcionariNom,
						funcionariNif,
						solicitud.getTitularDocumentTipus(),
						solicitud.getTitularDocumentNum(),
						solicitud.getTitularNom(),
						solicitud.getTitularLlinatge1(),
						solicitud.getTitularLlinatge2(),
						solicitud.getTitularNomComplet(),
						solicitud.getFinalitat(),
						solicitud.getConsentiment(),
						departamentNom,
						solicitud.getUnitatTramitadoraCodi(),
						solicitud.getExpedientId(),
						getScspHelper().copiarDadesEspecifiquesRecobriment(
								serveiCodi,
								solicitud.getDadesEspecifiques(),
								isGestioXsdActiva(serveiCodi)),
						procedimentServei);
				solicitudsEnviar.add(solicitudEnviar);
			}
			ResultatEnviamentPeticio resultat = enviarPeticioScsp(
					entitat.getId(),
					serveiCodi,
					idPeticion,
					solicitudsEnviar,
					true,
					conslt.isRecobriment());
			updateEstatConsulta(conslt, resultat);
			Consulta saved = consultaRepository.save(conslt);
			int solicitudIndex = 0;
			for (RecobrimentSolicitudDto solicitud: solicituds) {
				String titularDocumentTipus = null;
				if (solicitud.getTitularDocumentTipus() != null)
					titularDocumentTipus = solicitud.getTitularDocumentTipus().toString();
				Consulta cs = Consulta.getBuilder(
						idPeticion,
						funcionariNom,
						funcionariNif,
						titularDocumentTipus,
						solicitud.getTitularDocumentNum(),
						solicitud.getTitularNom(),
						solicitud.getTitularLlinatge1(),
						solicitud.getTitularLlinatge2(),
						solicitud.getTitularNomComplet(),
						departamentNom,
						procedimentServei,
						true,
						false,
						conslt).
						build();
				cs.updateScspSolicitudId(resultat.getIdsSolicituds()[solicitudIndex++]);
				updateEstatConsulta(cs, resultat);
				consultaRepository.save(cs);
			}
			ConsultaDto resposta = dtoMappingHelper.getMapperFacade().map(
					saved,
					ConsultaDto.class);
			// Si la petició ha produit un error ompl els camps per permetre
			// al recobriment generar el missatge amb informació de l'error.
			if (resultat.isError()) {
				resposta.setRespostaEstadoCodigo(resultat.getErrorCodi());
				resposta.setRespostaEstadoError(resultat.getErrorDescripcio());
			}
			
			integracioHelper.addAccioOk(
					IntegracioHelper.INTCODI_SERVEIS_SCSP,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0);
			
			return resposta;
		/*} catch (Exception ex) {
			// Error al realitzar la petició (no s'arriba a enviar)
			String errorDescripcio = "Excepció al realitzar consulta SCSP (id=" + idPeticion + ", servei=" + serveiCodi + ", usuari=" + auth.getName() + ")";
			log.error(errorDescripcio, ex);
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_SERVEIS_SCSP,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					errorDescripcio,
					ex);
			throw new ScspException(ex.getMessage(), ex);
		}*/
		} catch (ConsultaScspException ex) {
			return processarConsultaScspException(
					ex,
					conslt,
					accioDescripcio,
					accioParams,
					t0);
		}
	}

	@Override
	public JustificantDto obtenirJustificant(
			Long id) throws ConsultaNotFoundException, JustificantGeneracioException {
		log.debug("Generant justificant per a la consulta (id=" + id + ")");
		Consulta consulta = consultaRepository.findOne(id);
		if (consulta == null) {
			log.error("No s'ha trobat la consulta (id=" + id + ")");
			throw new ConsultaNotFoundException();
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!auth.getName().equals(consulta.getCreatedBy().getCodi())) {
			log.error("La consulta (id=" + id + ") no pertany a aquest usuari");
			throw new ConsultaNotFoundException();
		}
		return obtenirJustificantComu(consulta, true);
	}

	@Override
	public JustificantDto obtenirJustificant(String idpeticion, String idsolicitud)
			throws ConsultaNotFoundException, JustificantGeneracioException {
		log.debug("Generant justificant per a la consulta (" +
				"idpeticion=" + idpeticion + ", " +
				"idsolicitud=" + idsolicitud + ")");
		Consulta consulta = consultaRepository.findByScspPeticionIdAndScspSolicitudId(
				idpeticion,
				idsolicitud);
		if (consulta == null) {
			log.error("No s'ha trobat la consulta (idpeticion=" + idpeticion + ", idsolicitud=" + idsolicitud + ")");
			throw new ConsultaNotFoundException();
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!auth.getName().equals(consulta.getCreatedBy().getCodi())) {
			log.error("La consulta (idpeticion=" + idpeticion + ", idsolicitud=" + idsolicitud + ") no pertany a aquest usuari");
			throw new ConsultaNotFoundException();
		}
		return obtenirJustificantComu(consulta, true);
	}

	@Override
	public JustificantDto reintentarGeneracioJustificant(
			Long id,
			boolean descarregar) throws ConsultaNotFoundException, JustificantGeneracioException {
		log.debug("Reintentant generació del justificant per a la consulta (id=" + id + ")");
		Consulta consulta = consultaRepository.findOne(id);
		if (consulta == null) {
			log.error("No s'ha trobat la consulta (id=" + id + ")");
			throw new ConsultaNotFoundException();
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (!auth.getName().equals(consulta.getCreatedBy().getCodi())) {
			log.error("La consulta (id=" + id + ") no pertany a aquest usuari");
			throw new ConsultaNotFoundException();
		}
		return obtenirJustificantComu(consulta, descarregar);
	}

	@Transactional(rollbackFor = {ConsultaNotFoundException.class, JustificantGeneracioException.class})
	@Override
	public FitxerDto obtenirJustificantMultipleConcatenat(
			Long id) throws ConsultaNotFoundException, JustificantGeneracioException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		log.debug("Generant justificant concatenat per a la consulta múltiple (id=" + id + ")");
		copiarPropertiesToDb();
		Consulta consulta = consultaRepository.findOne(id);
		if (consulta == null) {
			log.debug("No s'ha trobat la consulta (id=" + id + ")");
			throw new ConsultaNotFoundException();
		}
		if (!auth.getName().equals(consulta.getCreatedBy().getCodi())) {
			log.debug("La consulta (id=" + id + ") no pertany a aquest usuari");
			throw new ConsultaNotFoundException();
		}
		if (!consulta.isMultiple()) {
			log.debug("La consulta (id=" + id + ") no és múltiple");
			throw new ConsultaNotFoundException();
		}
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Document pdfConcatenat = new Document();
			PdfCopy copy = new PdfCopy(pdfConcatenat, baos);
			pdfConcatenat.open();
			for (Consulta solicitud: consulta.getFills()) {
				FitxerDto fitxerJustificantGenerat = justificantHelper.generar(
						solicitud,
						getScspHelper());
				PdfReader pdfReader = new PdfReader(fitxerJustificantGenerat.getContingut());
				for (int pagina = 1; pagina <= pdfReader.getNumberOfPages(); pagina++) {
					copy.addPage(
							copy.getImportedPage(pdfReader, pagina));
				}
			}
			pdfConcatenat.close();
			FitxerDto fitxer = new FitxerDto();
			fitxer.setNom("PBL_" + consulta.getScspPeticionId() + ".pdf");
			fitxer.setContingut(baos.toByteArray());
			return fitxer;
		} catch (Exception ex) {
			log.error("Error al generar justificant concatenat per a la consulta múltiple (id=" + id + ")", ex);
			throw new JustificantGeneracioException(ex.getMessage(), ex);
		}
	}

	@Transactional(rollbackFor = {ConsultaNotFoundException.class, JustificantGeneracioException.class})
	@Override
	public FitxerDto obtenirJustificantMultipleZip(
			Long id) throws ConsultaNotFoundException, JustificantGeneracioException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		log.debug("Generant justificant ZIP per a la consulta múltiple (id=" + id + ")");
		copiarPropertiesToDb();
		Consulta consulta = consultaRepository.findOne(id);
		if (consulta == null) {
			log.debug("No s'ha trobat la consulta (id=" + id + ")");
			throw new ConsultaNotFoundException();
		}
		if (!auth.getName().equals(consulta.getCreatedBy().getCodi())) {
			log.debug("La consulta (id=" + id + ") no pertany a aquest usuari");
			throw new ConsultaNotFoundException();
		}
		if (!consulta.isMultiple()) {
			log.debug("La consulta (id=" + id + ") no és múltiple");
			throw new ConsultaNotFoundException();
		}
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ZipOutputStream zos = new ZipOutputStream(baos);
			for (Consulta solicitud: consulta.getFills()) {
				FitxerDto fitxerJustificantGenerat = justificantHelper.generar(
						solicitud,
						getScspHelper());
				ZipEntry zipEntry = new ZipEntry(fitxerJustificantGenerat.getNom());
				zos.putNextEntry(zipEntry);
				zos.write(fitxerJustificantGenerat.getContingut());
				zos.closeEntry();
			}
			zos.close();
			FitxerDto fitxer = new FitxerDto();
			fitxer.setNom("PBL_" + consulta.getScspPeticionId() + ".zip");
			fitxer.setContingut(baos.toByteArray());
			return fitxer;
		} catch (Exception ex) {
			log.error("Error al generar justificant ZIP per a la consulta múltiple (id=" + id + ")", ex);
			throw new JustificantGeneracioException(ex.getMessage(), ex);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public Page<ConsultaDto> findSimplesByFiltrePaginatPerDelegat(
			Long entitatId,
			ConsultaFiltreDto filtre,
			Pageable pageable) throws EntitatNotFoundException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		log.debug("Cercant les consultes de delegat simples per a l'entitat (id=" + entitatId + ") i l'usuari (codi=" + auth.getName() + ")");
		Entitat entitat = entitatRepository.findOne(entitatId);
		if (entitat == null) {
			log.debug("No s'ha trobat l'entitat (id=" + entitatId + ")");
			throw new EntitatNotFoundException();
		}
		copiarPropertiesToDb();
		return findByEntitatIUsuariFiltrePaginat(
				entitat,
				auth.getName(),
				filtre,
				pageable,
				false,
				true,
				false,
				false);
	}

	@Transactional(readOnly = true)
	@Override
	public Page<ConsultaDto> findMultiplesByFiltrePaginatPerDelegat(
			Long entitatId,
			ConsultaFiltreDto filtre,
			Pageable pageable) throws EntitatNotFoundException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		log.debug("Cercant les consultes de delegat múltiples per a l'entitat (id=" + entitatId + ") i l'usuari (codi=" + auth.getName() + ")");
		Entitat entitat = entitatRepository.findOne(entitatId);
		if (entitat == null) {
			log.debug("No s'ha trobat l'entitat (id=" + entitatId + ")");
			throw new EntitatNotFoundException();
		}
		copiarPropertiesToDb();
		return findByEntitatIUsuariFiltrePaginat(
				entitat,
				auth.getName(),
				filtre,
				pageable,
				true,
				true,
				false,
				false);
	}

	@Transactional(readOnly = true)
	@Override
	public Page<ConsultaDto> findByFiltrePaginatPerAuditor(
			Long entitatId,
			ConsultaFiltreDto filtre,
			Pageable pageable) throws EntitatNotFoundException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		log.debug("Cercant les consultes d'auditor per a l'entitat (id=" + entitatId + ")");
		Entitat entitat = entitatRepository.findOne(entitatId);
		if (entitat == null) {
			log.debug("No s'ha trobat l'entitat (id=" + entitatId + ")");
			throw new EntitatNotFoundException();
		}
		EntitatUsuari entitatUsuari = entitatUsuariRepository.findByEntitatIdAndUsuariCodi(
				entitat.getId(),
				auth.getName());
		if (entitatUsuari == null || !entitatUsuari.isAuditor()) {
			log.debug("Aquest usuari no té permisos per auditar l'entitat (id=" + entitatId + ", usuariCodi=" + auth.getName() + ")");
			throw new EntitatNotFoundException();
		}
		copiarPropertiesToDb();
		return findByEntitatIUsuariFiltrePaginat(
				entitat,
				null,
				filtre,
				pageable,
				false,
				false,
				false,
				false);
	}

	@Transactional(readOnly = true)
	@Override
	public Page<ConsultaDto> findByFiltrePaginatPerSuperauditor(
			Long entitatId,
			ConsultaFiltreDto filtre,
			Pageable pageable) throws EntitatNotFoundException {
		log.debug("Cercant les consultes de superauditor per a l'entitat (id=" + entitatId + ")");
		Entitat entitat = entitatRepository.findOne(entitatId);
		if (entitat == null) {
			log.debug("No s'ha trobat l'entitat (id=" + entitatId + ")");
			throw new EntitatNotFoundException();
		}
		copiarPropertiesToDb();
		return findByEntitatIUsuariFiltrePaginat(
				entitat,
				null,
				filtre,
				pageable,
				false,
				false,
				false,
				false);
	}

	@Transactional(readOnly = true)
	@Override
	public Page<ConsultaDto> findByFiltrePaginatPerAdmin(
			ConsultaFiltreDto filtre,
			Pageable pageable) throws EntitatNotFoundException {
		log.debug("Cercant les consultes de administrador");
		return findByFiltrePaginat(
				filtre,
				pageable,
				false,
				false,
				false,
				false);
	}

	@Transactional(readOnly = true)
	@Override
	public List<DadesObertesRespostaConsulta> findByFiltrePerOpenData(
			String entitatCodi,
			Date dataInici,
			Date dataFi,
			String procedimentCodi,
			String serveiCodi) throws EntitatNotFoundException, ProcedimentNotFoundException {
		log.debug("Consultant informació per opendata (" +
				"entitatCodi=" + entitatCodi + ", " +
				"dataInici=" + dataInici + ", " +
				"dataFi" + dataFi + ", " +
				"procedimentCodi=" + procedimentCodi + ", " +
				"serveiCodi=" + serveiCodi + ")");
		Entitat entitat = null;
		Procediment procediment = null;
		if (entitatCodi != null) {
			entitat = entitatRepository.findByCodi(entitatCodi);
			if (entitat == null) {
				log.debug("No s'ha trobat l'entitat (codi=" + entitatCodi + ")");
				throw new EntitatNotFoundException();
			}
			if (procedimentCodi != null) {
				procediment = procedimentRepository.findByEntitatAndCodi(entitat, procedimentCodi);
				if (procediment == null) {
					log.debug("No s'ha trobat el procediment (codi=" + procedimentCodi + ")");
					throw new ProcedimentNotFoundException();
				}
			}
		}
		List<DadesObertesRespostaConsulta> resposta = consultaRepository.findByOpendata(
				entitat == null,
				entitat != null ? entitat.getId() : null,
				procediment == null,
				procediment != null ? procediment.getId() : null,
				serveiCodi == null,
				serveiCodi,
				dataInici == null,
				dataInici,
				dataFi == null,
				dataFi);
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public ConsultaDto findOneDelegat(Long id) throws ConsultaNotFoundException, ScspException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		log.debug("Cercant les dades de la consulta (id=" + id + ")");
		Consulta consulta = consultaRepository.findOne(id);
		if (consulta == null) {
			log.debug("No s'ha trobat la consulta (id=" + id + ")");
			throw new ConsultaNotFoundException();
		}
		if (!auth.getName().equals(consulta.getCreatedBy().getCodi())) {
			log.debug("La consulta (id=" + id + ") no pertany a aquest usuari");
			throw new ConsultaNotFoundException();
		}
		return toConsultaDto(
				null,
				consulta);
	}

	@Transactional(readOnly = true)
	@Override
	public ConsultaDto findOneAuditor(Long id) throws ConsultaNotFoundException, ScspException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		log.debug("Cercant les dades de la consulta (id=" + id + ")");
		Consulta consulta = consultaRepository.findOne(id);
		if (consulta == null) {
			log.debug("No s'ha trobat la consulta (id=" + id + ")");
			throw new ConsultaNotFoundException();
		}
		Entitat entitat = consulta.getProcedimentServei().getProcediment().getEntitat();
		EntitatUsuari entitatUsuari = entitatUsuariRepository.findByEntitatIdAndUsuariCodi(
				entitat.getId(),
				auth.getName());
		if (entitatUsuari == null || !entitatUsuari.isAuditor()) {
			log.debug("Aquest usuari no té permisos per auditar la consulta (id=" + id + ", usuariCodi=" + auth.getName() + ")");
			throw new ConsultaNotFoundException();
		}
		return toConsultaDto(
				null,
				consulta);
	}

	@Transactional(readOnly = true)
	@Override
	public ConsultaDto findOneSuperauditor(Long id) throws ConsultaNotFoundException, ScspException {
		log.debug("Cercant les dades de la consulta (id=" + id + ")");
		Consulta consulta = consultaRepository.findOne(id);
		if (consulta == null) {
			log.debug("No s'ha trobat la consulta (id=" + id + ")");
			throw new ConsultaNotFoundException();
		}
		return toConsultaDto(
				null,
				consulta);
	}

	@Transactional(readOnly = true)
	@Override
	public ConsultaDto findOneAdmin(Long id) throws ConsultaNotFoundException, ScspException {
		log.debug("Cercant les dades de la consulta (id=" + id + ")");
		Consulta consulta = consultaRepository.findOne(id);
		if (consulta == null) {
			log.debug("No s'ha trobat la consulta (id=" + id + ")");
			throw new ConsultaNotFoundException();
		}
		return toConsultaDto(
				null,
				consulta);
	}

	@Transactional(readOnly = true)
	@Override
	public List<ConsultaDto> findAmbPare(
			Long pareId) throws ConsultaNotFoundException, ScspException {
		log.debug("Cercant les consultes amb pare (pareId=" + pareId + ")");
		Consulta pare = consultaRepository.findOne(pareId);
		if (pare == null) {
			log.debug("No s'ha trobat la consulta (id=" + pareId + ")");
			throw new ConsultaNotFoundException();
		}
		List<ConsultaDto> resposta = new ArrayList<ConsultaDto>();
		List<Consulta> filles = consultaRepository.findByPareOrderByScspSolicitudIdAsc(pare);
		for (Consulta filla: filles) {
			resposta.add(
					toConsultaDto(
							null,
							filla));
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public long countConsultesMultiplesProcessant(
			Long entitatId) throws EntitatNotFoundException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		log.debug("Contant consultes múltiples pendents (entitatId=" + entitatId + ")");
		return consultaRepository.countByEstatAndCreatedByAndMultipleTrue(
				EstatTipus.Processant,
				usuariRepository.findOne(auth.getName()));
	}

	@Transactional(readOnly = true)
	@Override
	public List<EstadisticaDto> findEstadistiquesByFiltre(EstadistiquesFiltreDto filtre) throws EntitatNotFoundException {
		log.debug("Consultant estadístiques per a l'entitat (id=" + filtre.getEntitatId() + ")");
		if (filtre.getEntitatId() != null) {
			Entitat entitat = entitatRepository.findOne(filtre.getEntitatId());
			if (entitat == null) {
				log.debug("No s'ha trobat l'entitat (id=" + filtre.getEntitatId() + ")");
				throw new EntitatNotFoundException();
			}
		}
		List<EstadisticaDto> resposta = new ArrayList<EstadisticaDto>();
		List<Object[]> resultats;
		if (EstadistiquesAgrupacioDto.PROCEDIMENT_SERVEI.equals(filtre.getAgrupacio())) {
			resultats = consultaRepository.countByProcedimentServei(
					filtre.getEntitatId() == null,
					filtre.getEntitatId(),
					filtre.getUsuariCodi() == null,
					(filtre.getUsuariCodi() != null) ? usuariRepository.findOne(filtre.getUsuariCodi()) : null,
					filtre.getProcedimentId() == null,
					filtre.getProcedimentId(),
					filtre.getServeiCodi() == null || filtre.getServeiCodi().isEmpty(),
					filtre.getServeiCodi(),
					filtre.getEstat() == null,
					(filtre.getEstat() != null) ? Consulta.EstatTipus.valueOf(filtre.getEstat().toString()) : null,
					filtre.getDataInici() == null,
					filtre.getDataInici(),
					filtre.getDataFi() == null,
					configurarDataFiPerFiltre(filtre.getDataFi()),
					EstatTipus.Tramitada,
					EstatTipus.Error);
		} else {
			resultats = consultaRepository.countByServeiProcediment(
					filtre.getEntitatId() == null,
					filtre.getEntitatId(),
					filtre.getUsuariCodi() == null,
					(filtre.getUsuariCodi() != null) ? usuariRepository.findOne(filtre.getUsuariCodi()) : null,
					filtre.getProcedimentId() == null,
					filtre.getProcedimentId(),
					filtre.getServeiCodi() == null || filtre.getServeiCodi().isEmpty(),
					filtre.getServeiCodi(),
					filtre.getEstat() == null,
					(filtre.getEstat() != null) ? Consulta.EstatTipus.valueOf(filtre.getEstat().toString()) : null,
					filtre.getDataInici() == null,
					filtre.getDataInici(),
					filtre.getDataFi() == null,
					configurarDataFiPerFiltre(filtre.getDataFi()),
					EstatTipus.Tramitada,
					EstatTipus.Error);
		}
		for (Object[] resultat: resultats) {
			Long procedimentServeiId = (Long)resultat[0];
			Long numRecobrimentOk = (Long)resultat[1];
			Long numRecobrimentError = (Long)resultat[2];
			Long numWebUIOk = (Long)resultat[4];
			Long numWebUIError = (Long)resultat[5];
			ProcedimentServei procedimentServei = procedimentServeiRepository.findOne(procedimentServeiId);
			EstadisticaDto dto = new EstadisticaDto();
			dto.setProcediment(
					dtoMappingHelper.getMapperFacade().map(
							procedimentServei.getProcediment(),
							ProcedimentDto.class));
			dto.setServeiCodi(procedimentServei.getServei());
			dto.setServeiNom(
					getScspHelper().getServicioDescripcion(
							procedimentServei.getServei()));
			dto.setNumRecobrimentOk(numRecobrimentOk);
			dto.setNumRecobrimentError(numRecobrimentError);
			dto.setNumWebUIOk(numWebUIOk);
			dto.setNumWebUIError(numWebUIError);
			resposta.add(dto);
		}
		EstadisticaDto estadisticaActual = null;
		for (EstadisticaDto estadistica: resposta) {
			boolean coincideixProcediment = estadisticaActual != null && estadisticaActual.getProcediment().getId().equals(estadistica.getProcediment().getId());
			boolean coincideixServei = estadisticaActual != null && estadisticaActual.getServeiCodi().equals(estadistica.getServeiCodi());
			if (	estadisticaActual == null || 
					(EstadistiquesAgrupacioDto.PROCEDIMENT_SERVEI.equals(filtre.getAgrupacio()) && !coincideixProcediment) ||
					(EstadistiquesAgrupacioDto.SERVEI_PROCEDIMENT.equals(filtre.getAgrupacio()) && !coincideixServei)) {
				estadisticaActual = estadistica;
				estadisticaActual.setConteSumatori(true);
			}
			estadisticaActual.setSumatoriNumRegistres(
					estadisticaActual.getSumatoriNumRegistres() + 1);
			estadisticaActual.setSumatoriNumRecobrimentOk(
					estadisticaActual.getSumatoriNumRecobrimentOk() + estadistica.getNumRecobrimentOk());
			estadisticaActual.setSumatoriNumRecobrimentError(
					estadisticaActual.getSumatoriNumRecobrimentError() + estadistica.getNumRecobrimentError());
			estadisticaActual.setSumatoriNumWebUIOk(
					estadisticaActual.getSumatoriNumWebUIOk() + estadistica.getNumWebUIOk());
			estadisticaActual.setSumatoriNumWebUIError(
					estadisticaActual.getSumatoriNumWebUIError() + estadistica.getNumWebUIError());
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public Map<EntitatDto, List<EstadisticaDto>> findEstadistiquesGlobalsByFiltre(
			EstadistiquesFiltreDto filtre) {
		log.debug("Consultant estadístiques globals");
		Map<EntitatDto, List<EstadisticaDto>> resposta = new HashMap<EntitatDto, List<EstadisticaDto>>();
		List<Object[]> resultats = consultaRepository.countByEntitat(
				filtre.getUsuariCodi() == null,
				(filtre.getUsuariCodi() != null) ? usuariRepository.findOne(filtre.getUsuariCodi()) : null,
				filtre.getProcedimentId() == null,
				filtre.getProcedimentId(),
				filtre.getServeiCodi() == null || filtre.getServeiCodi().isEmpty(),
				filtre.getServeiCodi(),
				filtre.getEstat() == null,
				(filtre.getEstat() != null) ? Consulta.EstatTipus.valueOf(filtre.getEstat().toString()) : null,
				filtre.getDataInici() == null,
				filtre.getDataInici(),
				filtre.getDataFi() == null,
				configurarDataFiPerFiltre(filtre.getDataFi()));
		// Omple les estadístiques per cada entitat
		for (Object[] resultat: resultats) {
			Long entitatId = (Long)resultat[0];
			Long numConsultes = (Long)resultat[1];
			if (numConsultes > 0) {
				filtre.setEntitatId(entitatId);
				try {
					resposta.put(
							dtoMappingHelper.getMapperFacade().map(
									entitatRepository.findOne(entitatId),
									EntitatDto.class),
							findEstadistiquesByFiltre(filtre));
				} catch (EntitatNotFoundException ex) {
					// És impossible però ho traurem pel log
					log.error("No s'ha trobat l'entitat (entitatId=" + entitatId + ")", ex);
				}
			}
		}
		// Omple l'estadística global
		filtre.setEntitatId(null);
		try {
			resposta.put(
					null,
					findEstadistiquesByFiltre(filtre));
		} catch (EntitatNotFoundException ex) {
			// És impossible però ho traurem pel log
			log.error("No s'ha trobat l'entitat (entitatId=null)", ex);
		}
		return resposta;
	}
	
	@Transactional(readOnly = true)
	@Override
	public List<CarregaDto> findEstadistiquesCarrega() {
		log.debug("Consultant estadístiques de càrrega");
		initEstadistiquesCarrega();
		List<CarregaDto> carregues = new ArrayList<CarregaDto>();
		for (CarregaDto carregaAny: carreguesAny) {
			CarregaDto carrega = new CarregaDto(
					0,
					0,
					carregaAny.getEntitatId(),
					carregaAny.getEntitatCodi(),
					carregaAny.getEntitatNom(),
					carregaAny.getEntitatCif(),
					carregaAny.getDepartamentNom(),
					carregaAny.getProcedimentServeiId(),
					carregaAny.getProcedimentCodi(),
					carregaAny.getProcedimentNom(),
					carregaAny.getServeiCodi(),
					carregaAny.getServeiDescripcio());
			long countWebMes = getCountFromCarregues(carrega, carreguesMes, true);
			long countWebDia = getCountFromCarregues(carrega, carreguesDia, true);
			long countWebHora = getCountFromCarregues(carrega, carreguesHora, true);
			long countWebMinut = getCountFromCarregues(carrega, carreguesMinut, true);
			carrega.setDetailedWebCount(
					new CarregaDetailedCountDto(
							carregaAny.getCountWeb(),
							countWebMes,
							countWebDia,
							countWebHora,
							countWebMinut));
			long countRecobrimentMes = getCountFromCarregues(carrega, carreguesMes, false);
			long countRecobrimentDia = getCountFromCarregues(carrega, carreguesDia, false);
			long countRecobrimentHora = getCountFromCarregues(carrega, carreguesHora, false);
			long countRecobrimentMinut = getCountFromCarregues(carrega, carreguesMinut, false);
			carrega.setDetailedRecobrimentCount(
					new CarregaDetailedCountDto(
							carregaAny.getCountRecobriment(),
							countRecobrimentMes,
							countRecobrimentDia,
							countRecobrimentHora,
							countRecobrimentMinut));
			carregues.add(carrega);
		}
		return carregues;
	}

	@Transactional(readOnly = true)
	@Override
	public List<Long> auditoriaGenerarAuditor(
			Long entitatId,
			Date dataInici,
			Date dataFi,
			int numConsultes) throws EntitatNotFoundException {
		log.debug("Generant auditoria per auditor (" +
				"entitatId=" + entitatId + "," +
				"dataInici=" + dataInici + "," +
				"dataFi=" + dataFi + "," +
				"numConsultes=" + numConsultes + ")");
		Entitat entitat = entitatRepository.findOne(entitatId);
		if (entitat == null) {
			log.debug("No s'ha trobat l'entitat (id=" + entitatId + ")");
			throw new EntitatNotFoundException();
		}
		List<Consulta> consultes = consultaRepository.findByEntitatAndDataIniciAndDataFi(
				entitat,
				dataInici,
				dataFi);
		List<Long> resposta = new ArrayList<Long>();
		if (consultes.size() > numConsultes) {
			int[] indexos = getRandomIndexesFromList(
					consultes,
					numConsultes);
			for (int index: indexos) {
				resposta.add(consultes.get(index).getId());
			}
		} else {
			for (Consulta consulta: consultes)
				resposta.add(consulta.getId());
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public List<ConsultaDto> auditoriaConsultarAuditor(
			Long entitatId,
			List<Long> consultaIds) throws EntitatNotFoundException, ScspException {
		log.debug("Consultant auditoria per auditor (" +
				"entitatId=" + entitatId + "," +
				"consultaIds=" + consultaIds + ")");
		Entitat entitat = entitatRepository.findOne(entitatId);
		if (entitat == null) {
			log.debug("No s'ha trobat l'entitat (id=" + entitatId + ")");
			throw new EntitatNotFoundException();
		}
		List<Consulta> consultes = consultaRepository.findByEntitatAndIds(
				entitat,
				consultaIds);
		List<ConsultaDto> resposta = new ArrayList<ConsultaDto>();
		for (Consulta consulta: consultes)
			resposta.add(toConsultaDto(entitatId, consulta));
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public List<Long> auditoriaGenerarSuperauditor(
			Date dataInici,
			Date dataFi,
			int numEntitats,
			int numConsultes) {
		log.debug("Generant auditoria per superauditor (" +
				"dataInici=" + dataInici + "," +
				"dataFi=" + dataFi + "," +
				"numConsultes=" + numConsultes + "," +
				"numEntitats=" + numEntitats + ")");
		List<Entitat> entitats = entitatRepository.findAll();
		// Filtra les entitats sense cap consulta
		List<Object[]> entitatCounts = consultaRepository.countByEntitat(
				true,
				null,
				true,
				null,
				true,
				null,
				true,
				null,
				false,
				dataInici,
				false,
				dataFi);
		Iterator<Entitat> itEntitats = entitats.iterator();
		while (itEntitats.hasNext()) {
			Entitat entitat = itEntitats.next();
			boolean trobada = false;
			for (Object[] count: entitatCounts) {
				Long entitatId = (Long)count[0];
				if (entitat.getId().equals(entitatId)) {
					trobada = true;
					break;
				}
			}
			if (!trobada)
				itEntitats.remove();
		}
		// Selecciona numEntitats d'entre les disponibles
		List<Long> seleccioEntitats = new ArrayList<Long>();
		if (entitats.size() > numEntitats) {
			int[] indexos = getRandomIndexesFromList(
					entitats,
					numEntitats);
			for (int index: indexos) {
				seleccioEntitats.add(entitats.get(index).getId());
			}
			itEntitats = entitats.iterator();
			while (itEntitats.hasNext()) {
				Entitat entitat = itEntitats.next();
				if (!seleccioEntitats.contains(entitat.getId()))
					itEntitats.remove();
			}
		}
		// Genera l'auditoria per a cada entitat
		List<Long> resposta = new ArrayList<Long>();
		for (Entitat entitat: entitats) {
			try {
				List<Long> auditoria = auditoriaGenerarAuditor(
						entitat.getId(),
						dataInici,
						dataFi,
						numConsultes);
				resposta.addAll(auditoria);
			} catch (EntitatNotFoundException e) {
				log.error("No s'ha trobat l'entitat (id=" + entitat.getId() + ")");
			}
		}
		return resposta;
	}

	@Transactional(readOnly = true)
	@Override
	public Map<EntitatDto, List<ConsultaDto>> auditoriaConsultarSuperauditor(
			List<Long> consultaIds) throws ScspException {
		log.debug("Consultant auditoria per superauditor (" +
				"consultaIds=" + consultaIds + ")");
		List<Object[]> consultes = consultaRepository.findByIds(
				consultaIds);
		Map<EntitatDto, List<ConsultaDto>> resposta = new HashMap<EntitatDto, List<ConsultaDto>>();
		List<ConsultaDto> consultesEntitat = null;
		EntitatDto entitatActual = null;
		for (Object[] consulta: consultes) {
			Consulta c = (Consulta)consulta[0];
			Long entitatId = (Long)consulta[1];
			if (entitatActual == null || !entitatActual.getId().equals(entitatId)) {
				entitatActual = dtoMappingHelper.getMapperFacade().map(
						entitatRepository.findOne(entitatId),
						EntitatDto.class);
				consultesEntitat = new ArrayList<ConsultaDto>();
				resposta.put(entitatActual, consultesEntitat);
			}
			consultesEntitat.add(toConsultaDto(entitatId, c));
		}
		return resposta;
	}

	@Transactional
	@Override
	public void autoRevisarEstatPeticionsMultiplesPendents() {
		log.debug("Iniciant revisió automàtica dels estats de les peticions múltiples pendents de forma automàtica");
		long t0 = System.currentTimeMillis();
		List<Consulta> pendents = consultaRepository.findByEstatAndMultipleTrue(EstatTipus.Processant);
		for (final Consulta pendent: pendents) {
			TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
			transactionTemplate.execute(new TransactionCallbackWithoutResult() {
				protected void doInTransactionWithoutResult(TransactionStatus status) {
					try {
						ResultatEnviamentPeticio resultat = getScspHelper().recuperarResultatEnviamentPeticio(pendent.getScspPeticionId());
						updateEstatConsulta(pendent, resultat);
						consultaRepository.saveAndFlush(pendent);
						for (Consulta fill: pendent.getFills()) {
							updateEstatConsulta(fill, resultat);
							consultaRepository.saveAndFlush(fill);
							/*if (EstatTipus.Tramitada.equals(filla.getEstat())) {
								justificantHelper.generarCustodiarJustificantPendent(
										filla,
										getScspHelper());
							}*/
						}
						if (EstatTipus.Tramitada.equals(pendent.getEstat())) {
							log.info(
									"Actualitzat l'estat de la consulta múltiple a TRAMITADA (" +
									"id=" + pendent.getId() + ", " +
									"scspPeticionId=" + pendent.getScspPeticionId() + ", " +
									"scspSolicitudId=" + pendent.getScspSolicitudId() + ", " +
									"arxiuExpedientUuid=" + pendent.getArxiuExpedientUuid() + ")");
						}
					} catch (Exception ex) {
						log.error("No s'ha pogut obtenir l'estat de la consulta SCSP (peticionId=" + pendent.getScspPeticionId() + ")", ex);
					}
				}
			});
		}
		log.debug("Finalitzada revisió automàtica dels estats de les peticions múltiples pendents (" + (System.currentTimeMillis() - t0) + "ms)");
	}

	@Override
	public void autoGenerarJustificantsPendents() {
		log.debug("Iniciant generació automàtica dels justificants pendents");
		long t0 = System.currentTimeMillis();
		List<Consulta> pendents = consultaRepository.findByEstatAndJustificantEstatAndMultipleAndArxiuExpedientTancatOrderByIdAsc(
				EstatTipus.Tramitada,
				JustificantEstat.PENDENT,
				false,
				false);
		for (Consulta pendent: pendents) {
			try {
				obtenirJustificantComu(pendent, false);
			} catch (JustificantGeneracioException ex) {
				log.error(
						"Error al generar automàticament el justificant per la consulta (" +
						"id=" + pendent.getId() + ", " +
						"scspPeticionId=" + pendent.getScspPeticionId() + ", " +
						"scspSolicitudId=" + pendent.getScspSolicitudId() + ")",
						ex);
			}
		}
		log.debug("Finalitzada generació automàtica dels justificants pendents (" + (System.currentTimeMillis() - t0) + "ms)");
	}

	@Override
	public void autoTancarExpedientsPendents() {
		log.debug("Iniciant tancament automàtic dels expedients pendents");
		long t0 = System.currentTimeMillis();
		List<Consulta> pendents = consultaRepository.findByEstatAndJustificantEstatAndMultipleAndArxiuExpedientTancatOrderByIdAsc(
				EstatTipus.Tramitada,
				JustificantEstat.OK,
				false,
				false);
		for (final Consulta pendent: pendents) {
			boolean esPotTancar = false;
			final Consulta pare = pendent.getPare();
			if (pare == null) {
				// Consultes simples
				esPotTancar = pendent.isCustodiat();
			} else {
				// Consultes múltiples
				int fillsTotals = consultaRepository.countByPare(pendent);
				int fillsCustodiats = consultaRepository.countByPareAndCustodiat(pendent, true);
				esPotTancar = fillsTotals == fillsCustodiats;
			}
			if (esPotTancar) {
				TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
				transactionTemplate.execute(new TransactionCallback<JustificantGeneracioException>() {
					@Override
					public JustificantGeneracioException doInTransaction(TransactionStatus status) {
						try {
							Expedient expedient = pluginHelper.arxiuExpedientConsultar(pendent.getArxiuExpedientUuid());
							if (!expedient.getMetadades().getEstat().equals(ExpedientEstat.TANCAT)) {
								pluginHelper.arxiuExpedientTancar(pendent.getArxiuExpedientUuid());
							}
							if (pare == null) {
								pendent.updateArxiuExpedientTancat(true);
								consultaRepository.saveAndFlush(pendent);
							} else {
								for (Consulta fill: pare.getFills()) {
									fill.updateArxiuExpedientTancat(true);
									consultaRepository.saveAndFlush(fill);
								}
							}
						} catch (SistemaExternException ex) {
							log.error(
									"Error al tancar l'expedient per la consulta (" +
									"id=" + pendent.getId() + ", " +
									"scspPeticionId=" + pendent.getScspPeticionId() + ", " +
									"scspSolicitudId=" + pendent.getScspSolicitudId() + ", " +
									"arxiuExpedientUuid=" + pendent.getArxiuExpedientUuid() + ")",
									ex);
						}
						log.info(
								"Tancat expedient de l'arxiu relacionat amb la consulta (" +
								"id=" + pendent.getId() + ", " +
								"scspPeticionId=" + pendent.getScspPeticionId() + ", " +
								"arxiuExpedientUuid=" + pendent.getArxiuExpedientUuid() + ")");
						return null;
					}
				});
			}
		}
		log.debug("Finalitzat el tancament automàtic dels expedients pendents (" + (System.currentTimeMillis() - t0) + "ms)");
	}

	@Override
	public boolean isOptimitzarTransaccionsNovaConsulta() {
		log.debug("Consultant optimització transaccions en nova consulta");
		return PropertiesHelper.getProperties().getAsBoolean("es.caib.pinbal.optimitzar.transaccions.nova.consulta");
	}

	@Transactional(readOnly = true)
	@Override
	public List<InformeGeneralEstatDto> informeGeneralEstat(Date dataInici, Date dataFi) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(dataInici);
		cal.set(Calendar.HOUR_OF_DAY,0);
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.SECOND,0);
		cal.set(Calendar.MILLISECOND,0);
		dataInici = cal.getTime();
		cal.setTime(dataFi);
		cal.set(Calendar.HOUR_OF_DAY,23);
		cal.set(Calendar.MINUTE,59);
		cal.set(Calendar.SECOND,59);
		cal.set(Calendar.MILLISECOND,999);
		dataFi = cal.getTime();
		log.debug("Obtenint informe general d'estats");
		List<InformeGeneralEstatDto> resposta = new ArrayList<InformeGeneralEstatDto>();
		List<ProcedimentServei> serveis = procedimentServeiRepository.findAll(
				new Sort(Sort.Direction.ASC, "procediment.entitat.nom", "procediment.codi", "servei"));
		List<Object[]> consultes = consultaRepository.countGroupByProcedimentServeiEstat(dataInici, dataFi);
		for (ProcedimentServei servei : serveis) {
			resposta.add(toInformeGeneralEstatDto(servei, consultes));
		}
		return resposta;
	}
	
	@Override
	public void autoGenerarEmailReportEstat() {
		Date fechaActual = new Date();
		List<InformeGeneralEstatDto> informeDades = informeGeneralEstat(fechaActual, fechaActual);
		byte[] fileReportEstatExcel = excelHelper.generarReportEstatExcel(informeDades);
		
		List<String> emailsAdministradorsList = new ArrayList<String>();
		try {
			List<DadesUsuari> dadesUsuarisAdmin = pluginHelper.dadesUsuariFindAmbGrup("PBL_ADMIN");
			for (DadesUsuari dadesUsuari : dadesUsuarisAdmin) {
				emailsAdministradorsList.add(dadesUsuari.getEmail());
			}
		} catch (SistemaExternException ex) {
			log.error("No s'han trobat usuaris amb el grup PBL_ADMIN al sistema extern");
		}

		String[] emailsAdministradors = emailsAdministradorsList.toArray(new String[emailsAdministradorsList.size()]);
		emailReportEstatHelper.sendMail(emailsAdministradors, fileReportEstatExcel);
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



	private ResultatEnviamentPeticio enviarPeticioScsp(
			Long entitatId,
			String serveiCodi,
			String idPeticion,
			List<Solicitud> solicituds,
			boolean sincrona,
			boolean recobriment) throws ConsultaScspGeneracioException, ConsultaScspComunicacioException {
		initEstadistiquesCarrega();
		if (solicituds != null && solicituds.size() > 0) {
			Solicitud solicitud = solicituds.get(0);
			afegirConsultaEstadistiquesCarrega(
					entitatId,
					solicitud.getUnitatTramitadora(),
					solicitud.getProcedimentCodi(),
					solicitud.getServeiCodi(),
					recobriment,
					carreguesAny);
			afegirConsultaEstadistiquesCarrega(
					entitatId,
					solicitud.getUnitatTramitadora(),
					solicitud.getProcedimentCodi(),
					solicitud.getServeiCodi(),
					recobriment,
					carreguesMes);
			afegirConsultaEstadistiquesCarrega(
					entitatId,
					solicitud.getUnitatTramitadora(),
					solicitud.getProcedimentCodi(),
					solicitud.getServeiCodi(),
					recobriment,
					carreguesDia);
			afegirConsultaEstadistiquesCarrega(
					entitatId,
					solicitud.getUnitatTramitadora(),
					solicitud.getProcedimentCodi(),
					solicitud.getServeiCodi(),
					recobriment,
					carreguesHora);
			afegirConsultaEstadistiquesCarrega(
					entitatId,
					solicitud.getUnitatTramitadora(),
					solicitud.getProcedimentCodi(),
					solicitud.getServeiCodi(),
					recobriment,
					carreguesMinut);
		}
		boolean gestioXsdActiva = isGestioXsdActiva(serveiCodi);
		if (sincrona) {
			return getScspHelper().enviarPeticionSincrona(
					idPeticion,
					solicituds,
					gestioXsdActiva);
		} else {
			return getScspHelper().enviarPeticionAsincrona(
					idPeticion,
					solicituds,
					gestioXsdActiva);
		}
	}

	private void initEstadistiquesCarrega() {
		if (carreguesAny == null) {
			carreguesAny = Collections.synchronizedList(
					consultaRepository.findCarrega(DateUtils.truncate(new Date(), Calendar.YEAR)));
		}
		if (carreguesMes == null) {
			carreguesMes = Collections.synchronizedList(
					consultaRepository.findCarrega(DateUtils.truncate(new Date(), Calendar.MONTH)));
		}
		if (carreguesDia == null) {
			carreguesDia = Collections.synchronizedList(
					consultaRepository.findCarrega(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH)));
		}
		if (carreguesHora == null) {
			carreguesHora = Collections.synchronizedList(
					consultaRepository.findCarrega(DateUtils.truncate(new Date(), Calendar.HOUR_OF_DAY)));
		}
		if (carreguesMinut == null) {
			carreguesMinut = Collections.synchronizedList(
					consultaRepository.findCarrega(DateUtils.truncate(new Date(), Calendar.MINUTE)));
		}
	}

	private void afegirConsultaEstadistiquesCarrega(
			Long entitatId,
			String departamentNom,
			String procedimentCodi,
			String serveiCodi,
			boolean recobriment,
			List<CarregaDto> carregues) {
		for (CarregaDto carrega: carregues) {
			if (	carrega.getEntitatId().equals(entitatId) &&
					carrega.getDepartamentNom().equals(departamentNom) &&
					carrega.getProcedimentCodi().equals(procedimentCodi) &&
					carrega.getServeiCodi().equals(serveiCodi)) {
				if (!recobriment) {
					carrega.setCountWeb(carrega.getCountWeb() + 1);
				} else {
					carrega.setCountRecobriment(carrega.getCountRecobriment() + 1);
				}
				break;
			}
		}
	}

	private InformeGeneralEstatDto toInformeGeneralEstatDto(ProcedimentServei servei, List<Object[]> consultes) {
		InformeGeneralEstatDto dto = new InformeGeneralEstatDto();
		Servicio servicio = getScspHelper().getServicio(servei.getServei());
		dto.setEntitatCodi(servei.getProcediment().getEntitat().getCodi());
		dto.setEntitatNom(servei.getProcediment().getEntitat().getNom());
		dto.setEntitatCif(servei.getProcediment().getEntitat().getCif());
		dto.setDepartament(servei.getProcediment().getDepartament());
		dto.setProcedimentCodi(servei.getProcediment().getCodi());
		dto.setProcedimentNom(servei.getProcediment().getNom());
		dto.setServeiCodi(servicio.getCodCertificado());
		dto.setServeiNom(servicio.getDescripcion());
		if (servicio.getEmisor() != null) {
			EmisorDto emisor = new EmisorDto();
			EmisorCertificado emisorCertificado = servicio.getEmisor();
			emisor.setCif(emisorCertificado.getCif());
			emisor.setNom(getScspHelper().getEmisorNombre(emisorCertificado.getCif()));
			dto.setServeiEmisor(emisor);
		}
		// Obtenim els usuaris que tenen permís sobre el procediment-servei
		List<AccessControlEntry> aces = PermisosHelper.getAclSids(
				ProcedimentServei.class,
				servei.getId(),
				aclService);
		Set<String> usuaris = new HashSet<String>();
		if (aces != null) {
			for (AccessControlEntry ace: aces) {
				if (ace.getSid() instanceof PrincipalSid)
					usuaris.add(((PrincipalSid)ace.getSid()).getPrincipal());
			}
		}
		dto.setServeiUsuaris(usuaris.size());
		// Calculam les peticions correctes i les errònees
		// consulta[0] --> Codi entitat
		// consulta[1] --> Codi procediemnt
		// consulta[2] --> Codi servei
		// consulta[3] --> Estat (Pendent, Processant, Tramitada o Error)
		// consulta[4] --> Número de peticions
		Long peticionsCorrectes = 0L;
		Long peticionsErronees = 0L;
		boolean etrobat = false;
		boolean strobat = false;
		Long entitatId = servei.getProcediment().getEntitat().getId();
		Long procedimentId = servei.getProcediment().getId();
		for (int i = 0; i < consultes.size(); i++) {
			Long entId = (Long)consultes.get(i)[0];
			Long procId = (Long)consultes.get(i)[1];
			if (entId.equals(entitatId) && procId.equals(procedimentId)) {
				etrobat = true;
				String serveiCodi = (String)consultes.get(i)[2];
				if (servei.getServei().equals(serveiCodi)) {
					strobat = true;
					EstatTipus estat = (EstatTipus)consultes.get(i)[3];
					Long count = (Long)consultes.get(i)[4];
					if (estat.equals(EstatTipus.Error))
						peticionsErronees += count;
					else
						peticionsCorrectes += count;
				} else if (strobat) {
					// Les consultes estan ordenades per servei. Per tant, al trobar un nou servei, ja no cal continuar
					strobat = false;
					break;
				}
			} else if(etrobat) {
				// Les consultes estan ordenades per entitat i procediment. Per tant, al trobar una nova entitat-procediment, ja no cal continuar
				etrobat = false;
				break;
			}
		}
		dto.setPeticionsCorrectes(peticionsCorrectes.intValue());
		dto.setPeticionsErronees(peticionsErronees.intValue());
		return dto;
	}

	private ConsultaDto toConsultaDto(
			Long entitatId,
			Consulta consulta) throws ScspException {
		ConsultaDto resposta = dtoMappingHelper.getMapperFacade().map(
				consulta,
				ConsultaDto.class);
		resposta.setEntitatId(entitatId);
		//resposta.setFuncionariNom(consulta.getCreatedBy().getNom());
		//resposta.setFuncionariNif(consulta.getCreatedBy().getNif());
		resposta.setFuncionariNom(consulta.getFuncionariNom());
		resposta.setFuncionariNif(consulta.getFuncionariDocumentNum());
		try {
			Resposta rpt = getScspHelper().recuperarResposta(
					consulta.getScspPeticionId(),
					consulta.getScspSolicitudId(),
					consulta.isMultiple());
			if (rpt != null) {
				resposta.setFinalitat(rpt.getFinalitat());
				if (rpt.getConsentiment() != null)
					resposta.setConsentiment(ConsultaDto.Consentiment.valueOf(rpt.getConsentiment().name()));
				resposta.setExpedientId(rpt.getExpedientId());
				resposta.setDepartamentNom(rpt.getUnitatTramitadora());
				resposta.setUnitatTramitadoraCodi(rpt.getUnitatTramitadoraCodi());
				if (rpt.getRespostaXml() != null) {
					resposta.setHiHaResposta(true);
					resposta.setRespostaData(rpt.getRespostaData());
					resposta.setRespostaXml(rpt.getRespostaXml());
				}
				if (rpt.getPeticioXml() != null) {
					resposta.setHiHaPeticio(true);
					resposta.setPeticioXml(rpt.getPeticioXml());
				}
				if (!consulta.isMultiple()) {
					resposta.setDadesEspecifiques(
							getScspHelper().getDadesEspecifiquesPeticio(
									consulta.getScspPeticionId(),
									consulta.getScspSolicitudId()));
				}
			}
		} catch (Exception ex) {
			log.error("No s'han pogut consultar les dades de la petició (peticionId=" + consulta.getScspPeticionId() + ", solicitudId=" + consulta.getScspSolicitudId() + ")", ex);
			throw new ScspException("No s'han pogut consultar les dades de la petició (peticionId=" + consulta.getScspPeticionId() + ", solicitudId=" + consulta.getScspSolicitudId() + ")", ex);
		}
		return resposta;
	}
 
	private Page<ConsultaDto> findByEntitatIUsuariFiltrePaginat(
			Entitat entitat,
			String usuariCodi,
			ConsultaFiltreDto filtre,
			Pageable pageable,
			boolean multiple,
			boolean nomesSensePare,
			boolean consultaHihaPeticio,
			boolean consultaTerData) throws EntitatNotFoundException {
		copiarPropertiesToDb();
		log.debug("Consulta de peticions findByEntitatIUsuariFiltrePaginat (" +
				"entitat=" + entitat.getCodi() + ", " +
				"usuariCodi=" + usuariCodi + ", " +
				((filtre != null) ? (
				"filtre.scspPeticionId=" + filtre.getScspPeticionId() + ", " +
				"filtre.procedimentId=" + filtre.getProcedimentId() + ", " +
				"filtre.serveiCodi=" + filtre.getServeiCodi() + ", " +
				"filtre.estat=" + filtre.getProcedimentId() + ", " +
				"filtre.dataInici=" + filtre.getProcedimentId() + ", " +
				"filtre.dataFi=" + filtre.getProcedimentId() + ", " +
				"filtre.titularNom=" + filtre.getTitularNom() + ", " +
				"filtre.titularDocument=" + filtre.getTitularDocument() + ", " +
				"filtre.funcionariNom=" + filtre.getFuncionariNom() + ", " +
				"filtre.funcionariDocument=" + filtre.getFuncionariDocument() + ", ") : "") +
				((pageable != null) ? (
				"paginacio.paginaNum=" + pageable.getPageNumber() + ", " +
				"paginacio.paginaTamany=" + pageable.getPageSize() + ", ") : "") +
				"multiple=" + multiple + ", " +
				"nomesSensePare=" + nomesSensePare + ")");
		long t0 = System.currentTimeMillis();
		Page<Consulta> paginaConsultes;
		if (filtre == null) {
			paginaConsultes = consultaRepository.findByProcedimentServeiProcedimentEntitatIdAndCreatedBy(
					entitat.getId(),
					usuariCodi == null,
					(usuariCodi != null) ? usuariRepository.findOne(usuariCodi) : null,
					multiple,
					nomesSensePare,
					pageable);
		} else {
			paginaConsultes = consultaRepository.findByCreatedByAndFiltrePaginat(
					entitat.getId(),
					usuariCodi == null,
					(usuariCodi != null) ? usuariRepository.findOne(usuariCodi) : null,
					filtre.getScspPeticionId() == null || filtre.getScspPeticionId().isEmpty(),
					filtre.getScspPeticionId(),
					filtre.getProcedimentId() == null,
					filtre.getProcedimentId(),
					filtre.getServeiCodi() == null || filtre.getServeiCodi().isEmpty(),
					filtre.getServeiCodi(),
					filtre.getEstat() == null,
					(filtre.getEstat() != null) ? Consulta.EstatTipus.valueOf(filtre.getEstat().toString()) : null,
					filtre.getDataInici() == null,
					filtre.getDataInici(),
					filtre.getDataFi() == null,
					configurarDataFiPerFiltre(filtre.getDataFi()),
					filtre.getTitularNom() == null || filtre.getTitularNom().isEmpty(),
					filtre.getTitularNom(),
					filtre.getTitularDocument() == null || filtre.getTitularDocument().isEmpty(),
					filtre.getTitularDocument(),
					filtre.getFuncionariNom() == null || filtre.getFuncionariNom().isEmpty(),
					filtre.getFuncionariNom(),
					filtre.getFuncionariDocument() == null || filtre.getFuncionariDocument().isEmpty(),
					filtre.getFuncionariDocument(),
					multiple,
					nomesSensePare,
					pageable);
		}
		log.trace("[S_CONS] Consulta a la base de dades (" + (System.currentTimeMillis() - t0) + " ms)");
		t0 = System.currentTimeMillis();
		Page<ConsultaDto> paginaConsultesDto = dtoMappingHelper.pageEntities2pageDto(paginaConsultes, ConsultaDto.class, pageable);
		log.trace("[S_CONS] Conversió a DTO (" + (System.currentTimeMillis() - t0) + " ms)");
		t0 = System.currentTimeMillis();
		for (ConsultaDto consulta: paginaConsultesDto.getContent()) {
			consulta.setServeiDescripcio(
					getScspHelper().getServicioDescripcion(
							consulta.getServeiCodi()));
		}
		if (consultaHihaPeticio) {
			for (ConsultaDto consulta: paginaConsultesDto.getContent()) {
				try {
					consulta.setHiHaPeticio(
							getScspHelper().isPeticionEnviada(
									consulta.getScspPeticionId()));
				} catch (es.scsp.common.exceptions.ScspException ex) {
					log.error("No s'han pogut consultar l'enviament de la petició (id=" + consulta.getScspPeticionId() + ")", ex);
					consulta.setHiHaPeticio(false);
				}
			}
		}
		if (consultaHihaPeticio) {
			for (ConsultaDto consulta: paginaConsultesDto.getContent()) {
				try {
					consulta.setTerData(getScspHelper().getTerPeticion(
										consulta.getScspPeticionId()));
				} catch (es.scsp.common.exceptions.ScspException ex) {
					log.error("No s'han pogut consultar el TER de la petició (id=" + consulta.getScspPeticionId() + ")", ex);
				}
			}
		}
		log.trace("[S_CONS] Consulta de peticions addicionals (" + (System.currentTimeMillis() - t0) + " ms)");
		return  paginaConsultesDto;
	}

	private Page<ConsultaDto> findByFiltrePaginat(
			ConsultaFiltreDto filtre,
			Pageable pageable,
			boolean multiple,
			boolean nomesSensePare,
			boolean consultaHihaPeticio,
			boolean consultaTerData) throws EntitatNotFoundException {
		copiarPropertiesToDb();
		log.debug("Consulta de peticions findByFiltrePaginat (" +
				((filtre != null) ? (
				"filtre.entitatId=" + filtre.getEntitatId() + ", " +
				"filtre.scspPeticionId=" + filtre.getScspPeticionId() + ", " +
				"filtre.procedimentId=" + filtre.getProcedimentId() + ", " +
				"filtre.serveiCodi=" + filtre.getServeiCodi() + ", " +
				"filtre.estat=" + filtre.getProcedimentId() + ", " +
				"filtre.dataInici=" + filtre.getProcedimentId() + ", " +
				"filtre.dataFi=" + filtre.getProcedimentId() + ", " +
				"filtre.titularNom=" + filtre.getTitularNom() + ", " +
				"filtre.titularDocument=" + filtre.getTitularDocument() + ", " +
				"filtre.funcionariNom=" + filtre.getFuncionariNom() + ", " +
				"filtre.funcionariDocument=" + filtre.getFuncionariDocument() + ", ") : "") +
				((pageable != null) ? (
				"paginacio.paginaNum=" + pageable.getPageNumber() + ", " +
				"paginacio.paginaTamany=" + pageable.getPageSize() + ", ") : "") +
				"multiple=" + multiple + ", " +
				"nomesSensePare=" + nomesSensePare + ")");
		long t0 = System.currentTimeMillis();
		Page<Consulta> paginaConsultes;
		if (filtre == null) {
			paginaConsultes = consultaRepository.findByProcedimentServeiProcediment(
					multiple,
					nomesSensePare,
					pageable);
		} else {
			paginaConsultes = consultaRepository.findByFiltrePaginat(
					filtre.getEntitatId() == null,
					filtre.getEntitatId(),
					filtre.getScspPeticionId() == null || filtre.getScspPeticionId().isEmpty(),
					filtre.getScspPeticionId(),
					filtre.getProcedimentId() == null,
					filtre.getProcedimentId(),
					filtre.getServeiCodi() == null || filtre.getServeiCodi().isEmpty(),
					filtre.getServeiCodi(),
					filtre.getEstat() == null,
					(filtre.getEstat() != null) ? Consulta.EstatTipus.valueOf(filtre.getEstat().toString()) : null,
					filtre.getDataInici() == null,
					filtre.getDataInici(),
					filtre.getDataFi() == null,
					configurarDataFiPerFiltre(filtre.getDataFi()),
					filtre.getTitularNom() == null || filtre.getTitularNom().isEmpty(),
					filtre.getTitularNom(),
					filtre.getTitularDocument() == null || filtre.getTitularDocument().isEmpty(),
					filtre.getTitularDocument(),
					filtre.getFuncionariNom() == null || filtre.getFuncionariNom().isEmpty(),
					filtre.getFuncionariNom(),
					filtre.getFuncionariDocument() == null || filtre.getFuncionariDocument().isEmpty(),
					filtre.getFuncionariDocument(),
					multiple,
					nomesSensePare,
					pageable);
		}
		log.trace("[S_CONS] Consulta a la base de dades (" + (System.currentTimeMillis() - t0) + " ms)");
		t0 = System.currentTimeMillis();
		Page<ConsultaDto> paginaConsultesDto = dtoMappingHelper.pageEntities2pageDto(paginaConsultes, ConsultaDto.class, pageable);
		log.trace("[S_CONS] Conversió a DTO (" + (System.currentTimeMillis() - t0) + " ms)");
		t0 = System.currentTimeMillis();
		for (ConsultaDto consulta: paginaConsultesDto.getContent()) {
			consulta.setServeiDescripcio(
					getScspHelper().getServicioDescripcion(
							consulta.getServeiCodi()));
		}
		if (consultaHihaPeticio) {
			for (ConsultaDto consulta: paginaConsultesDto.getContent()) {
				try {
					consulta.setHiHaPeticio(
							getScspHelper().isPeticionEnviada(
									consulta.getScspPeticionId()));
				} catch (es.scsp.common.exceptions.ScspException ex) {
					log.error("No s'han pogut consultar l'enviament de la petició (id=" + consulta.getScspPeticionId() + ")", ex);
					consulta.setHiHaPeticio(false);
				}
			}
		}
		if (consultaHihaPeticio) {
			for (ConsultaDto consulta: paginaConsultesDto.getContent()) {
				try {
					consulta.setTerData(getScspHelper().getTerPeticion(
										consulta.getScspPeticionId()));
				} catch (es.scsp.common.exceptions.ScspException ex) {
					log.error("No s'han pogut consultar el TER de la petició (id=" + consulta.getScspPeticionId() + ")", ex);
				}
			}
		}
		log.trace("[S_CONS] Consulta de peticions addicionals (" + (System.currentTimeMillis() - t0) + " ms)");
		return  paginaConsultesDto;
	}	
	
	private JustificantDto obtenirJustificantComu(
			final Consulta consulta,
			final boolean descarregar) throws JustificantGeneracioException {
		// Abans de continuar es comprova si l'estat de la consulta és "Tramitada"
		if (EstatTipus.Tramitada.equals(consulta.getEstat())) {
			// Amb aquest bloc sincronitzat aconseguim que només hi hagi un thread a la vegada
			// generant el justificant per a una determinada consulta. Si un altre thread intenta
			// generar el mateix justificant es quedarà bloquejat esperant.
			// Això es fa per a evitar problemes derivats de l'accés concurrent a l'arxiu digital.
			try {
				synchronized (getJustificantLockForConsulta(consulta)) {
					TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
					transactionTemplate.execute(new TransactionCallback<Object>() {
						@Override
						public Object doInTransaction(TransactionStatus status) {
							Consulta consultaRefreshed = consultaRepository.getOne(consulta.getId());
							// Si l'estat del justificant és PENDENT o ERROR intentam tornar a generar el justificant
							if (JustificantEstat.PENDENT.equals(consultaRefreshed.getJustificantEstat()) || JustificantEstat.ERROR.equals(consultaRefreshed.getJustificantEstat())) {
								justificantHelper.generarCustodiarJustificantPendent(
										consultaRefreshed,
										getScspHelper());
								consultaRepository.saveAndFlush(consultaRefreshed);
							}
							return null;
						}
					});
				}
			} finally {
				releaseJustificantLockForConsulta(consulta);
			}
			if (descarregar) {
				TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
				return transactionTemplate.execute(new TransactionCallback<JustificantDto>() {
					@Override
					public JustificantDto doInTransaction(TransactionStatus status) {
						Consulta consultaRefreshed = consultaRepository.getOne(consulta.getId());
						JustificantDto justificant = new JustificantDto();
						justificant.setEstat(
								es.caib.pinbal.core.dto.ConsultaDto.JustificantEstat.valueOf(
										consultaRefreshed.getJustificantEstat().name()));
						if (JustificantEstat.ERROR.equals(consultaRefreshed.getJustificantEstat())) {
							justificant.setError(true);
							justificant.setErrorDescripcio("La generació del justificant ha produït errors");
						}
						if (JustificantEstat.NO_DISPONIBLE.equals(consultaRefreshed.getJustificantEstat())) {
							justificant.setError(true);
							justificant.setErrorDescripcio("L'estat del justificant de la consulta és NO_DISPONIBLE");
						}
						if (!justificant.isError()) {
							try {
								FitxerDto justificantFitxer = justificantHelper.descarregarFitxerGenerat(
										consultaRefreshed,
										getScspHelper());
								justificant.setNom(justificantFitxer.getNom());
								justificant.setContentType(justificantFitxer.getContentType());
								justificant.setContingut(justificantFitxer.getContingut());
							} catch (Exception ex) {
								log.error("La descàrrega del justificant ha produït errors (" +
										"id=" + consultaRefreshed.getScspPeticionId() + ", " +
										"scspPeticionId=" + consultaRefreshed.getScspPeticionId() + ", " +
										"scspSolicitudId=" + consultaRefreshed.getScspSolicitudId() + ")",
										ex);
								justificant.setError(true);
								justificant.setErrorDescripcio("La descàrrega del justificant ha produit errors");
							}
						}
						return justificant;
					}
				});
			} else {
				return null;
			}
		} else {
			throw new JustificantGeneracioException("La consulta no està en estat TRAMITADA");
		}
	}

	private Solicitud convertirEnSolicitud(
			ConsultaDto consulta,
			ProcedimentServei procedimentServei) {
		Solicitud solicitud = new Solicitud();
		solicitud.setServeiCodi(consulta.getServeiCodi());
		Procediment procediment = procedimentRepository.findOne(procedimentServei.getProcediment().getId());
		solicitud.setProcedimentCodi(
				(procedimentServei.getProcedimentCodi() != null && !("".equalsIgnoreCase(procedimentServei.getProcedimentCodi())) ? 
						procedimentServei.getProcedimentCodi() : 
						procediment.getCodi()));
		solicitud.setProcedimentNom(procediment.getNom());
		solicitud.setProcedimentValorCampAutomatizado(procediment.getValorCampAutomatizado());
		if (procediment.getValorCampClaseTramite() != null) {
			solicitud.setProcedimentValorCampClaseTramite(procediment.getValorCampClaseTramite().getIntValue());
		}
		solicitud.setSolicitantIdentificacio(consulta.getEntitatCif());
		solicitud.setSolicitantNom(consulta.getEntitatNom());
		solicitud.setFuncionariNom(consulta.getFuncionariNom());
		solicitud.setFuncionariNif(consulta.getFuncionariNif());
		if (consulta.getTitularDocumentTipus() != null) {
			solicitud.setTitularDocumentTipus(
					es.caib.pinbal.scsp.DocumentTipus.valueOf(consulta.getTitularDocumentTipus().toString()));
		}
		solicitud.setTitularDocument(consulta.getTitularDocumentNum());
		solicitud.setTitularNom(consulta.getTitularNom());
		solicitud.setTitularLlinatge1(consulta.getTitularLlinatge1());
		solicitud.setTitularLlinatge2(consulta.getTitularLlinatge2());
		solicitud.setTitularNomComplet(consulta.getTitularNomComplet());
		solicitud.setFinalitat(consulta.getFinalitat());
		solicitud.setConsentiment(
				es.caib.pinbal.scsp.Consentiment.valueOf(
						consulta.getConsentiment().toString()));
		setUnitatTramitadoraSolicitud(solicitud, procediment, consulta.getDepartamentNom());
		solicitud.setExpedientId(consulta.getExpedientId());
		solicitud.setDadesEspecifiquesMap(consulta.getDadesEspecifiques());
		return solicitud;
	}
	
	private Solicitud convertirEnSolicitud(
			Entitat entitat,
			Procediment procediment,
			String serveiCodi,
			String funcionariNom,
			String funcionariNif,
			DocumentTipus titularDocumentTipus,
			String titularDocumentNum,
			String titularNom,
			String titularLlinatge1,
			String titularLlinatge2,
			String titularNomComplet,
			String finalitat,
			Consentiment consentiment,
			String departamentNom,
			String unitatTramitadoraCodi,
			String expedientId,
			Element dadesEspecifiques,
			ProcedimentServei procedimentServei) {
		Solicitud solicitud = new Solicitud();
		solicitud.setServeiCodi(serveiCodi);
		solicitud.setProcedimentCodi(
				(procedimentServei.getProcedimentCodi() != null && !("".equalsIgnoreCase(procedimentServei.getProcedimentCodi())) ? 
						procedimentServei.getProcedimentCodi() : 
						procediment.getCodi()));
		solicitud.setProcedimentNom(procediment.getNom());
		solicitud.setProcedimentValorCampAutomatizado(procediment.getValorCampAutomatizado());
		if (procediment.getValorCampClaseTramite() != null) {
			solicitud.setProcedimentValorCampClaseTramite(procediment.getValorCampClaseTramite().getIntValue());
		}
		solicitud.setSolicitantIdentificacio(entitat.getCif());
		solicitud.setSolicitantNom(entitat.getNom());
		solicitud.setFuncionariNom(funcionariNom);
		solicitud.setFuncionariNif(funcionariNif);
		if (titularDocumentTipus != null) {
			solicitud.setTitularDocumentTipus(
					es.caib.pinbal.scsp.DocumentTipus.valueOf(titularDocumentTipus.toString()));
		}
		solicitud.setTitularDocument(titularDocumentNum);
		solicitud.setTitularNom(titularNom);
		solicitud.setTitularLlinatge1(titularLlinatge1);
		solicitud.setTitularLlinatge2(titularLlinatge2);
		solicitud.setTitularNomComplet(titularNomComplet);
		solicitud.setFinalitat(finalitat);
		solicitud.setConsentiment(
				es.caib.pinbal.scsp.Consentiment.valueOf(consentiment.toString()));
		setUnitatTramitadoraSolicitud(solicitud, procediment, departamentNom);
		solicitud.setUnitatTramitadoraCodi(unitatTramitadoraCodi);
		solicitud.setExpedientId(expedientId);
		solicitud.setDadesEspecifiquesElement(dadesEspecifiques);
		return solicitud;
	}
	private List<Solicitud> convertirEnMultiplesSolicituds(
			ConsultaDto consulta,
			ProcedimentServei procedimentServei) throws ConsultaScspGeneracioException {
		try {
			List<ServeiCamp> serveiCamps = serveiCampRepository.findByServeiOrderByGrupOrdreAsc(
					consulta.getServeiCodi());
			List<Solicitud> solicituds = new ArrayList<Solicitud>();
			for (String[] dades: consulta.getDadesPeticioMultiple()) {
				Solicitud solicitud = new Solicitud();
				solicitud.setServeiCodi(consulta.getServeiCodi());
				Procediment procediment = procedimentRepository.findOne(consulta.getProcedimentId());
				solicitud.setProcedimentCodi(procediment.getCodi());
				solicitud.setProcedimentNom(procediment.getNom());
				solicitud.setProcedimentValorCampAutomatizado(procediment.getValorCampAutomatizado());
				if (procediment.getValorCampClaseTramite() != null) {
					solicitud.setProcedimentValorCampClaseTramite(procediment.getValorCampClaseTramite().getIntValue());
				}
				solicitud.setSolicitantIdentificacio(consulta.getEntitatCif());
				solicitud.setSolicitantNom(consulta.getEntitatNom());
				solicitud.setFuncionariNom(consulta.getFuncionariNom());
				solicitud.setFuncionariNif(consulta.getFuncionariNif());
				solicitud.setFinalitat(consulta.getFinalitat());
				solicitud.setConsentiment(
						es.caib.pinbal.scsp.Consentiment.valueOf(
								consulta.getConsentiment().toString()));
				setUnitatTramitadoraSolicitud(solicitud, procediment, consulta.getDepartamentNom());
				String titularDocumentTipus = getValorCampPeticioMultiple(
						"DatosGenericos/Titular/TipoDocumentacion",
						consulta.getCampsPeticioMultiple(),
						dades);
				if (titularDocumentTipus != null) {
					solicitud.setTitularDocumentTipus(
							es.caib.pinbal.scsp.DocumentTipus.valueOf(
									titularDocumentTipus));
				}
				solicitud.setTitularDocument(
						getValorCampPeticioMultiple(
								"DatosGenericos/Titular/Documentacion",
								consulta.getCampsPeticioMultiple(),
								dades));
				solicitud.setTitularNom(
						getValorCampPeticioMultiple(
								"DatosGenericos/Titular/Nombre",
								consulta.getCampsPeticioMultiple(),
								dades));
				solicitud.setTitularLlinatge1(
						getValorCampPeticioMultiple(
								"DatosGenericos/Titular/Apellido1",
								consulta.getCampsPeticioMultiple(),
								dades));
				solicitud.setTitularLlinatge2(
						getValorCampPeticioMultiple(
								"DatosGenericos/Titular/Apellido2",
								consulta.getCampsPeticioMultiple(),
								dades));
				solicitud.setTitularNomComplet(
						getValorCampPeticioMultiple(
								"DatosGenericos/Titular/NombreCompleto",
								consulta.getCampsPeticioMultiple(),
								dades));
				solicitud.setExpedientId(
						getValorCampPeticioMultiple(
								"DatosGenericos",
								consulta.getCampsPeticioMultiple(),
								dades));
				Map<String, Object> dadesEspecifiques = getDadesEspecifiquesPeticioMultiple(
						serveiCamps,
						consulta.getCampsPeticioMultiple(),
						dades);
				processarDadesEspecifiquesSegonsCamps(
						consulta.getServeiCodi(),
						dadesEspecifiques);
				solicitud.setDadesEspecifiquesMap(dadesEspecifiques);
				solicituds.add(solicitud);
			}
			return solicituds;
		} catch (Exception ex) {
			if (ex instanceof ConsultaScspGeneracioException) {
				throw ex;
			} else {
				throw new ConsultaScspGeneracioException(ex);
			}
		}
	}

	private void setUnitatTramitadoraSolicitud(Solicitud solicitud, Procediment procediment, String defaultUnitatTramitadora) {
		ServeiConfig serveiConfig = serveiConfigRepository.findByServei(solicitud.getServeiCodi());
		if (serveiConfig.isPinbalUnitatDir3FromEntitat()) {
			solicitud.setUnitatTramitadoraCodi(procediment.getEntitat().getUnitatArrel());
		} else if(serveiConfig.getPinbalUnitatDir3() != null && !serveiConfig.getPinbalUnitatDir3().isEmpty()) {
			solicitud.setUnitatTramitadoraCodi(serveiConfig.getPinbalUnitatDir3());
		} else if (procediment.getOrganGestor() != null) {
			solicitud.setUnitatTramitadoraCodi(procediment.getOrganGestor().getCodi());
		}
		solicitud.setUnitatTramitadora(defaultUnitatTramitadora);
	}
		

	private void processarDadesEspecifiquesSegonsCamps(
			String serveiCodi,
			Map<String, Object> dadesEspecifiques) throws ConsultaScspGeneracioException {
		try {
			SimpleDateFormat sdfComu = new SimpleDateFormat("dd/MM/yyyy");
			List<ServeiCamp> camps = serveiCampRepository.findByServeiOrderByGrupOrdreAsc(serveiCodi);
			// Conversió de format
			for (String path: dadesEspecifiques.keySet()) {
				for (ServeiCamp camp: camps) {
					if (camp.getPath().equals(path)) {
						if (camp.getTipus().equals(ServeiCampTipus.DATA)) {
							String str = (String)dadesEspecifiques.get(path);
							if (str != null && !str.isEmpty()) {
								Date data = sdfComu.parse(str);
								SimpleDateFormat sdfCamp = null;
								if (camp.getDataFormat() != null && !camp.getDataFormat().isEmpty())
									sdfCamp = new SimpleDateFormat(camp.getDataFormat());
								else
									sdfCamp = new SimpleDateFormat("ddMMyyyy");
								dadesEspecifiques.put(path, sdfCamp.format(data));
							} else {
								dadesEspecifiques.put(path, null);
							}
						} else if (camp.getTipus().equals(ServeiCampTipus.BOOLEA)) {
							String str = (String)dadesEspecifiques.get(path);
							if (str != null && !str.isEmpty()) {
								boolean valor =
										"true".equalsIgnoreCase(str) ||
										"on".equalsIgnoreCase(str) ||
										"yes".equalsIgnoreCase(str) ||
										"si".equalsIgnoreCase(str);
								dadesEspecifiques.put(path, valor ? "true" : "false");
							} else {
								dadesEspecifiques.put(path, null);
							}
						}
						break;
					}
				}
			}
			// Control de camps de tipus document
			for (ServeiCamp camp: camps) {
				if (camp.getTipus().equals(ServeiCampTipus.DOC_IDENT)) {
					// Elimina el tipus de document de les dades específiques
					// si no s'especifica el número de document
					String valor = (String)dadesEspecifiques.get(camp.getPath());
					if (valor == null || valor.isEmpty()) {
						ServeiCamp campTipusDocument = camp.getCampPare();
						if (campTipusDocument != null)
							dadesEspecifiques.remove(campTipusDocument.getPath());
					}
				}
			}
		} catch (ParseException ex) {
			throw new ConsultaScspGeneracioException(ex);
		}
	}

	private Map<String, Object> getDadesEspecifiquesPeticioMultiple(
			List<ServeiCamp> serveiCamps,
			String[] camps,
			String[] dades) {
		Map<String, Object> dadesEspecifiques = new HashMap<String, Object>();
		for (ServeiCamp serveiCamp: serveiCamps) {
			for (int i = 0; i < camps.length; i++) {
				if (camps[i].equals(serveiCamp.getPath())) {
					dadesEspecifiques.put(
							camps[i],
							dades[i]);
					break;
				}
			}
		}
		return dadesEspecifiques;
		
	}

	private String getValorCampPeticioMultiple(
			String path,
			String[] camps,
			String[] valors) {
		String valor = null;
		for (int i = 0; i < camps.length; i++) {
			if (path.equals(camps[i])) {
				valor = valors[i];
				break;
			}
		}
		return valor;
	}

	private void updateEstatConsulta(
			Consulta consulta,
			ResultatEnviamentPeticio resultat) {
		if (resultat.isError()) {
			updateEstatConsultaError(
					consulta,
					"[" + resultat.getErrorCodi() + "] " + resultat.getErrorDescripcio());
		} else if ("0001".equals(resultat.getEstatCodi())) {
			consulta.updateEstat(EstatTipus.Pendent);
		} else if ("0002".equals(resultat.getEstatCodi())) {
			consulta.updateEstat(EstatTipus.Processant);
		} else if ("0003".equals(resultat.getEstatCodi())) {
			consulta.updateEstat(EstatTipus.Tramitada);
		} else if ("0004".equals(resultat.getEstatCodi())) {
			consulta.updateEstat(EstatTipus.Processant);
		}
	}

	private void updateEstatConsultaError(
			Consulta consulta,
			String error) {
		consulta.updateEstat(EstatTipus.Error);
		consulta.updateEstatError(error);
	}

	private Date configurarDataFiPerFiltre(Date dataFi) {
		if (dataFi == null)
			return null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(dataFi);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		return cal.getTime();
	}

	private int[] getRandomIndexesFromList(
			List<?> list,
			int numIndexes) {
		int[] indexes = new int[numIndexes];
		for (int i = 0; i < numIndexes; i++) {
			int randomNum;
			boolean existeix;
			do {
				randomNum = new Random().nextInt(list.size());
				existeix = false;
				for (int j = 0; j < i; j++) {
					if (indexes[j] == randomNum)
						existeix = true;
				}
			} while (existeix);
			indexes[i] = randomNum;
		}
		Arrays.sort(indexes);
		return indexes;
	}

	private long getCountFromCarregues(
			CarregaDto carrega,
			List<CarregaDto> carregues,
			boolean web) {
		int index = carregues.indexOf(carrega);
		if (index != -1) {
			CarregaDto carregaTrobada = carregues.get(index);
			return web ? carregaTrobada.getCountWeb() : carregaTrobada.getCountRecobriment();
		} else {
			return 0;
		}
	}

	private boolean propertiesCopiades = false;
	private void copiarPropertiesToDb() {
		if (!propertiesCopiades) {
			getScspHelper().copiarPropertiesToDb(
					PropertiesHelper.getProperties());
			propertiesCopiades = true;
		}
	}

	private ScspHelper getScspHelper() {
		if (scspHelper == null) {
			scspHelper = new ScspHelper(
					applicationContext,
					messageSource);
		}
		return scspHelper;
	}

	private Object getJustificantLockForConsulta(Consulta consulta) {
		Long id = consulta.getId();
		if (!justificantLocks.containsKey(id)) {
			justificantLocks.put(id, new Object());
		}
		return justificantLocks.get(id);
	}
	private void releaseJustificantLockForConsulta(Consulta consulta) {
		Long id = consulta.getId();
		if (justificantLocks.containsKey(id)) {
			justificantLocks.remove(id);
		}
	}

	private ConsultaDto processarConsultaScspException(
			ConsultaScspException ex,
			Consulta conslt,
			String accioDescripcio,
			Map<String, String> accioParams,
			long t0) throws ConsultaScspException {
		if (conslt != null && ex instanceof ConsultaScspComunicacioException || ex instanceof ConsultaScspRespostaException) {
			String error;
			if (ex.getCause() != null && ex.getCause() instanceof es.scsp.common.exceptions.ScspException) {
				es.scsp.common.exceptions.ScspException scspException = (es.scsp.common.exceptions.ScspException)ex.getCause();
				error = "[" + scspException.getScspCode() + "] " + scspException.getMessage();
			} else {
				error = ex.getMessage();
			}
			updateEstatConsultaError(
					conslt,
					error);
			Consulta saved = consultaRepository.save(conslt);
			integracioHelper.addAccioError(
					IntegracioHelper.INTCODI_SERVEIS_SCSP,
					accioDescripcio,
					accioParams,
					IntegracioAccioTipusEnumDto.ENVIAMENT,
					System.currentTimeMillis() - t0,
					error,
					ex);
			return dtoMappingHelper.getMapperFacade().map(
					saved,
					ConsultaDto.class);
		} else {
			throw ex;
		}
	}

	private boolean isGestioXsdActiva(String serveiCodi) {
		ServeiConfig serveiConfig = serveiConfigRepository.findByServei(serveiCodi);
		return serveiConfig != null && serveiConfig.isActivaGestioXsd();
	}

}
