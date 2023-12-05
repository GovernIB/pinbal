/**
 * 
 */
package es.caib.pinbal.core.service;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfReader;
import es.caib.pinbal.client.dadesobertes.DadesObertesResposta;
import es.caib.pinbal.client.dadesobertes.DadesObertesRespostaConsulta;
import es.caib.pinbal.core.dto.CarregaDto;
import es.caib.pinbal.core.dto.ConsultaDto;
import es.caib.pinbal.core.dto.ConsultaDto.Consentiment;
import es.caib.pinbal.core.dto.ConsultaDto.DocumentTipus;
import es.caib.pinbal.core.dto.ConsultaFiltreDto;
import es.caib.pinbal.core.dto.ConsultaOpenDataDto;
import es.caib.pinbal.core.dto.EmisorDto;
import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.dto.EstadisticaDto;
import es.caib.pinbal.core.dto.EstadistiquesFiltreDto;
import es.caib.pinbal.core.dto.EstadistiquesFiltreDto.EstadistiquesAgrupacioDto;
import es.caib.pinbal.core.dto.FitxerDto;
import es.caib.pinbal.core.dto.InformeGeneralEstatDto;
import es.caib.pinbal.core.dto.InformeProcedimentServeiDto;
import es.caib.pinbal.core.dto.InformeRepresentantFiltreDto;
import es.caib.pinbal.core.dto.IntegracioAccioTipusEnumDto;
import es.caib.pinbal.core.dto.JustificantDto;
import es.caib.pinbal.core.dto.ProcedimentDto;
import es.caib.pinbal.core.dto.RecobrimentSolicitudDto;
import es.caib.pinbal.core.dto.arxiu.ArxiuConversions;
import es.caib.pinbal.core.dto.arxiu.ArxiuDetallDto;
import es.caib.pinbal.core.dto.arxiu.ArxiuEstatEnumDto;
import es.caib.pinbal.core.dto.arxiu.ArxiuFirmaDto;
import es.caib.pinbal.core.dto.arxiu.ArxiuFirmaPerfilEnumDto;
import es.caib.pinbal.core.dto.arxiu.ArxiuFirmaTipusEnumDto;
import es.caib.pinbal.core.helper.ConfigHelper;
import es.caib.pinbal.core.helper.DtoMappingHelper;
import es.caib.pinbal.core.helper.EmailReportEstatHelper;
import es.caib.pinbal.core.helper.ExcelHelper;
import es.caib.pinbal.core.helper.IntegracioHelper;
import es.caib.pinbal.core.helper.JustificantHelper;
import es.caib.pinbal.core.helper.PermisosHelper;
import es.caib.pinbal.core.helper.PeticioScspEstadistiquesHelper;
import es.caib.pinbal.core.helper.PeticioScspHelper;
import es.caib.pinbal.core.helper.PluginHelper;
import es.caib.pinbal.core.helper.ServeiHelper;
import es.caib.pinbal.core.helper.UsuariHelper;
import es.caib.pinbal.core.model.Consulta;
import es.caib.pinbal.core.model.Consulta.EstatTipus;
import es.caib.pinbal.core.model.Consulta.JustificantEstat;
import es.caib.pinbal.core.model.Entitat;
import es.caib.pinbal.core.model.EntitatUsuari;
import es.caib.pinbal.core.model.Procediment;
import es.caib.pinbal.core.model.ProcedimentServei;
import es.caib.pinbal.core.model.Usuari;
import es.caib.pinbal.core.repository.ConsultaRepository;
import es.caib.pinbal.core.repository.EntitatRepository;
import es.caib.pinbal.core.repository.EntitatUsuariRepository;
import es.caib.pinbal.core.repository.ProcedimentRepository;
import es.caib.pinbal.core.repository.ProcedimentServeiRepository;
import es.caib.pinbal.core.repository.TokenRepository;
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
import es.caib.pinbal.scsp.PropertiesHelper;
import es.caib.pinbal.scsp.Resposta;
import es.caib.pinbal.scsp.ResultatEnviamentPeticio;
import es.caib.pinbal.scsp.ScspHelper;
import es.caib.pinbal.scsp.Solicitud;
import es.caib.plugins.arxiu.api.DocumentEstat;
import es.caib.plugins.arxiu.api.DocumentMetadades;
import es.caib.plugins.arxiu.api.Expedient;
import es.caib.plugins.arxiu.api.ExpedientEstat;
import es.caib.plugins.arxiu.api.Firma;
import es.scsp.common.domain.core.EmisorCertificado;
import es.scsp.common.domain.core.Servicio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

import javax.xml.ws.soap.SOAPFaultException;
import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * Implementació dels mètodes per a gestionar les consultes al SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Service
public class ConsultaServiceImpl implements ConsultaService, ApplicationContextAware, MessageSourceAware {

	private static final String ROLE_ADMIN = "ROLE_ADMIN";
	private static final String ROLE_REPRES = "ROLE_REPRES";

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
	private EntitatUsuariRepository entitatUsuariRepository;
	@Autowired
	private TokenRepository tokenRepository;

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
	private PeticioScspEstadistiquesHelper peticioScspEstadistiquesHelper;
	@Autowired
	private PeticioScspHelper peticioScspHelper;

	@Autowired
	private MutableAclService aclService;

	@Autowired
	private ProcedimentService procedimentService;

	@Autowired
	private IntegracioHelper integracioHelper;

	@Autowired
	private PlatformTransactionManager transactionManager;

	@Autowired
	private ExcelHelper excelHelper;

	@Autowired
	private EmailReportEstatHelper emailReportEstatHelper;
	@Autowired
	private ConfigHelper configHelper;

	private ApplicationContext applicationContext;
	private MessageSource messageSource;
	private ScspHelper scspHelper;

	private Map<Long, Object> justificantLocks = new HashMap<Long, Object>();

	@Transactional(rollbackFor = {ProcedimentServeiNotFoundException.class, ServeiNotAllowedException.class, ConsultaScspException.class})
	@Override
	public ConsultaDto novaConsulta(
			ConsultaDto consulta) throws ProcedimentServeiNotFoundException, ServeiNotAllowedException, ConsultaScspException {
		log.debug("Executant consulta del servei (codi=" + consulta.getServeiCodi() + "): " + consulta);
		String accioDescripcio = "Consulta del servei " + consulta.getServeiCodi();
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
		accioParams.put("procediment", procedimentServei.getProcediment() != null ? procedimentServei.getProcediment().getCodi() + " - " + procedimentServei.getProcediment().getNom() : "");
		accioParams.put("servei", procedimentServei.getServeiScsp() != null ? procedimentServei.getServeiScsp().getCodi() + " - " + procedimentServei.getServeiScsp().getDescripcio() : "");
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
							consulta.getFinalitat(),
							consulta.getConsentiment(),
							consulta.getExpedientId(),
							false,
							false,
							null).
					build();
			peticioScspHelper.processarIEmmagatzemarDadesEspecifiques(
					conslt,
					consulta.getDadesEspecifiques());
			if (peticioScspHelper.isEnviarConsultaServei(conslt, false)) {
				conslt.updateEstat(EstatTipus.Processant);
				ResultatEnviamentPeticio resultat = peticioScspHelper.enviarPeticioScsp(
						conslt,
						Arrays.asList(peticioScspHelper.convertirEnSolicitud(conslt)),
						true,
						conslt.isRecobriment(),
						getScspHelper());
				if (resultat.getIdsSolicituds() != null && resultat.getIdsSolicituds().length > 0) {
					conslt.updateScspSolicitudId(resultat.getIdsSolicituds()[0]);
				}
				peticioScspHelper.updateEstatConsulta(conslt, resultat, accioParams);
				if (resultat.isError()) {
					integracioHelper.addAccioError(
							idPeticion,
							IntegracioHelper.INTCODI_SERVEIS_SCSP,
							accioDescripcio,
							accioParams,
							IntegracioAccioTipusEnumDto.ENVIAMENT,
							System.currentTimeMillis() - t0,
							"[" + resultat.getErrorCodi() + "] " + resultat.getErrorDescripcio(),
							(Throwable)null);
				} else {
					integracioHelper.addAccioOk(
							idPeticion,
							IntegracioHelper.INTCODI_SERVEIS_SCSP,
							accioDescripcio,
							accioParams,
							IntegracioAccioTipusEnumDto.ENVIAMENT,
							System.currentTimeMillis() - t0);
				}
			}
			Consulta saved = consultaRepository.save(conslt);
			return dtoMappingHelper.getMapperFacade().map(
					saved,
					ConsultaDto.class);
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
		String accioDescripcio = "Consulta del servei "  + consulta.getServeiCodi() + " (init)";
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
		accioParams.put("procediment", procedimentServei.getProcediment() != null ? procedimentServei.getProcediment().getCodi() + " - " + procedimentServei.getProcediment().getNom() : "");
		accioParams.put("servei", procedimentServei.getServeiScsp() != null ? procedimentServei.getServeiScsp().getCodi() + " - " + procedimentServei.getServeiScsp().getDescripcio() : "");
		integracioHelper.addAccioOk(
				idPeticion,
				IntegracioHelper.INTCODI_SERVEIS_SCSP,
				accioDescripcio,
				accioParams,
				IntegracioAccioTipusEnumDto.ENVIAMENT,
				System.currentTimeMillis() - t0);
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
						consulta.getFinalitat(),
						consulta.getConsentiment(),
						consulta.getExpedientId(),
						false,
						false,
						null).
				build();
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
		String accioDescripcio = "Consulta del servei " + consulta.getServeiCodi() + " (enviament)";
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
		accioParams.put("procediment", procedimentServei.getProcediment() != null ? procedimentServei.getProcediment().getCodi() + " - " + procedimentServei.getProcediment().getNom() : "");
		accioParams.put("servei", procedimentServei.getServeiScsp() != null ? procedimentServei.getServeiScsp().getCodi() + " - " + procedimentServei.getServeiScsp().getDescripcio() : "");
		Consulta conslt = consultaRepository.findOne(consultaId);
		if (conslt == null) {
			log.debug("No s'ha trobat la consulta (id=" + consultaId + ")");
			throw new ConsultaNotFoundException();
		}
		try {
			peticioScspHelper.processarIEmmagatzemarDadesEspecifiques(
					conslt,
					consulta.getDadesEspecifiques());
			if (peticioScspHelper.isEnviarConsultaServei(conslt, false)) {
				conslt.updateEstat(EstatTipus.Processant);
				ResultatEnviamentPeticio resultat = peticioScspHelper.enviarPeticioScsp(
						conslt,
						Arrays.asList(peticioScspHelper.convertirEnSolicitud(conslt)),
						true,
						conslt.isRecobriment(),
						getScspHelper());
				accioParams.put("idPeticion", consulta.getScspPeticionId());
				accioParams.put("idSolicitud", consulta.getScspSolicitudId());
				accioParams.put("estat", "[" + resultat.getEstatCodi() + "] " + resultat.getEstatDescripcio());
				if (resultat.isError()) {
					integracioHelper.addAccioError(
							conslt.getScspPeticionId(),
							IntegracioHelper.INTCODI_SERVEIS_SCSP,
							accioDescripcio,
							accioParams,
							IntegracioAccioTipusEnumDto.ENVIAMENT,
							System.currentTimeMillis() - t0,
							"[" + resultat.getErrorCodi() + "] " + resultat.getErrorDescripcio(),
							(Throwable)null);
				} else {
					integracioHelper.addAccioOk(
							conslt.getScspPeticionId(),
							IntegracioHelper.INTCODI_SERVEIS_SCSP,
							accioDescripcio,
							accioParams,
							IntegracioAccioTipusEnumDto.ENVIAMENT,
							System.currentTimeMillis() - t0);
				}
			}
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
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("consultaId", consultaId.toString());
		long t0 = System.currentTimeMillis();
		Consulta consulta = consultaRepository.findOne(consultaId);
		if (consulta == null) {
			log.debug("No s'ha trobat la consulta (id=" + consultaId + ")");
			throw new ConsultaNotFoundException();
		}
		String accioDescripcio = "Consulta del servei " + consulta.getServeiCodi() +" (estat)";
		if (consulta.getProcedimentServei() != null) {
			ProcedimentServei procedimentServei = consulta.getProcedimentServei();
			accioParams.put("procediment", consulta.getProcedimentServei().getProcediment() != null ? procedimentServei.getProcediment().getCodi() + " - " + procedimentServei.getProcediment().getNom() : "");
			accioParams.put("servei", procedimentServei.getServeiScsp() != null ? procedimentServei.getServeiScsp().getCodi() + " - " + procedimentServei.getServeiScsp().getDescripcio() : "");
		}
		try {
			// Si l'estat de la consulta és pendent aleshores voldrà dir que la petició encara no s'ha
			// enviat i no te sentit refrescar el seu estat.
			if (consulta.getEstat() != EstatTipus.Pendent) {
				ResultatEnviamentPeticio resultat = getScspHelper().recuperarResultatEnviamentPeticio(
						consulta.getScspPeticionId());
				if (resultat.getIdsSolicituds() != null && resultat.getIdsSolicituds().length > 0) {
					consulta.updateScspSolicitudId(resultat.getIdsSolicituds()[0]);
				}
				// Si l'estat de la consulta és Error vol dir que l'error s'ha processat amb anterioritat
				// i no és necessari actualitzar l'estat. Si l'estat s'actualitza segurament el posarà com
				// a pendent i no seria l'estat correcte.
				if (consulta.getEstat() != EstatTipus.Error) {
					peticioScspHelper.updateEstatConsulta(consulta, resultat, accioParams);
				}
			}
			integracioHelper.addAccioOk(
					consulta.getScspPeticionId(),
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
					consulta.getScspPeticionId(),
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
		String accioDescripcio = "Consulta múltiple del servei " + consulta.getServeiCodi();
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
		accioParams.put("procediment", procedimentServei.getProcediment() != null ? procedimentServei.getProcediment().getCodi() + " - " + procedimentServei.getProcediment().getNom() : "");
		accioParams.put("servei", procedimentServei.getServeiScsp() != null ? procedimentServei.getServeiScsp().getCodi() + " - " + procedimentServei.getServeiScsp().getDescripcio() : "");
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
							null,
							null,
							null,
							false,
							true,
							null).
					build();
			conslt.updateEstat(EstatTipus.Pendent);
			List<Solicitud> solicituds = peticioScspHelper.convertirEnMultiplesSolicituds(consulta, procedimentServei);
			ResultatEnviamentPeticio resultat = peticioScspHelper.enviarPeticioScsp(
					conslt,
					solicituds,
					false,
					conslt.isRecobriment(),
					getScspHelper());
			peticioScspHelper.updateEstatConsulta(conslt, resultat, accioParams);
			if (resultat.isError()) {
				integracioHelper.addAccioError(
						idPeticion,
						IntegracioHelper.INTCODI_SERVEIS_SCSP,
						accioDescripcio,
						accioParams,
						IntegracioAccioTipusEnumDto.ENVIAMENT,
						System.currentTimeMillis() - t0,
						"[" + resultat.getErrorCodi() + "] " + resultat.getErrorDescripcio(),
						(Throwable)null);
			} else {
				integracioHelper.addAccioOk(
						idPeticion,
						IntegracioHelper.INTCODI_SERVEIS_SCSP,
						accioDescripcio,
						accioParams,
						IntegracioAccioTipusEnumDto.ENVIAMENT,
						System.currentTimeMillis() - t0);
			}
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
								solicitud.getFinalitat(),
								(solicitud.getConsentiment() == es.caib.pinbal.scsp.Consentiment.Llei) ? Consentiment.Llei : Consentiment.Si,
								solicitud.getExpedientId(),
								false,
								false,
								conslt).
						build();
				cs.updateScspSolicitudId(resultat.getIdsSolicituds()[solicitudIndex++]);
				peticioScspHelper.updateEstatConsulta(cs, resultat, null);
				consultaRepository.save(cs);
			}
			return dtoMappingHelper.getMapperFacade().map(
					saved,
					ConsultaDto.class);
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
		String accioDescripcio = "Consulta del servei " + serveiCodi + " via recobriment";
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
		accioParams.put("procediment", procedimentServei.getProcediment() != null ? procedimentServei.getProcediment().getCodi() + " - " + procedimentServei.getProcediment().getNom() : "");
		accioParams.put("servei", procedimentServei.getServeiScsp() != null ? procedimentServei.getServeiScsp().getCodi() + " - " + procedimentServei.getServeiScsp().getDescripcio() : "");
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
							solicitud.getFinalitat(),
							solicitud.getConsentiment(),
							solicitud.getExpedientId(),
							true,
							false,
							null).
					build();
			conslt.updateEstat(EstatTipus.Pendent);
			Solicitud solicitudEnviar = peticioScspHelper.convertirEnSolicitud(
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
					solicitud.getDadesEspecifiques(),
					procedimentServei,
					getScspHelper());
			ResultatEnviamentPeticio resultat = peticioScspHelper.enviarPeticioScsp(
					conslt,
					Arrays.asList(solicitudEnviar),
					true,
					conslt.isRecobriment(),
					getScspHelper());
			if (resultat.getIdsSolicituds() != null && resultat.getIdsSolicituds().length > 0) {
				conslt.updateScspSolicitudId(resultat.getIdsSolicituds()[0]);
			}
			peticioScspHelper.updateEstatConsulta(conslt, resultat, accioParams);
			if (resultat.isError()) {
				integracioHelper.addAccioError(
						idPeticion,
						IntegracioHelper.INTCODI_SERVEIS_SCSP,
						accioDescripcio,
						accioParams,
						IntegracioAccioTipusEnumDto.ENVIAMENT,
						System.currentTimeMillis() - t0,
						"[" + resultat.getErrorCodi() + "] " + resultat.getErrorDescripcio(),
						(Throwable)null);
			} else {
				integracioHelper.addAccioOk(
						idPeticion,
						IntegracioHelper.INTCODI_SERVEIS_SCSP,
						accioDescripcio,
						accioParams,
						IntegracioAccioTipusEnumDto.ENVIAMENT,
						System.currentTimeMillis() - t0);
			}
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
			return resposta;
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
	public ConsultaDto novaConsultaRecobrimentInit(
			String serveiCodi,
			RecobrimentSolicitudDto solicitud) throws EntitatNotFoundException, ProcedimentNotFoundException, ProcedimentServeiNotFoundException, ServeiNotAllowedException, ConsultaScspException {
		log.debug("Executant consulta del servei via recobriment (init) (" +
				"entitatCif=" + solicitud.getEntitatCif() + ", " +
				"procedimentCodi=" + solicitud.getProcedimentCodi() + ", " +
				"serveiCodi=" + serveiCodi + ")");
		String accioDescripcio = "Consulta del servei " + serveiCodi + " via recobriment (init)";
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("entitatCif", solicitud.getEntitatCif());
//		accioParams.put("procedimentCodi", solicitud.getProcedimentCodi());
//		accioParams.put("serveiCodi", serveiCodi);
		long t0 = System.currentTimeMillis();
		copiarPropertiesToDb();
		Entitat entitat = entitatRepository.findByCif(solicitud.getEntitatCif());
		if (entitat == null) {
			log.debug("No s'ha trobat l'entitat (entitatCif=" + solicitud.getEntitatCif() + ")");
			throw new EntitatNotFoundException();
		}
		Procediment procediment = procedimentRepository.findByEntitatAndCodi(
				entitat,
				solicitud.getProcedimentCodi());
		if (procediment == null) {
			procediment = procedimentRepository.findByEntitatAndCodiSia(
					entitat,
					solicitud.getProcedimentCodi());
		}
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
		accioParams.put("procediment", procedimentServei.getProcediment() != null ? procedimentServei.getProcediment().getCodi() + " - " + procedimentServei.getProcediment().getNom() : "");
		accioParams.put("servei", procedimentServei.getServeiScsp() != null ? procedimentServei.getServeiScsp().getCodi() + " - " + procedimentServei.getServeiScsp().getDescripcio() : "");
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
				idPeticion,
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
						solicitud.getFinalitat(),
						solicitud.getConsentiment(),
						solicitud.getExpedientId(),
						true,
						false,
						null).
				build();
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
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("consultaId", consultaId.toString());
		long t0 = System.currentTimeMillis();
		Consulta consulta = consultaRepository.findOne(consultaId);
		if (consulta == null) {
			log.debug("No s'ha trobat la consulta (id=" + consultaId + ")");
			throw new ConsultaNotFoundException();
		}
		String accioDescripcio = "Consulta del servei " + consulta.getServeiCodi() + " via recobriment (enviament)";
		ProcedimentServei procedimentServei = consulta.getProcedimentServei();
		if (procedimentServei != null) {
			accioParams.put("procediment", procedimentServei.getProcediment() != null ? procedimentServei.getProcediment().getCodi() + " - " + procedimentServei.getProcediment().getNom() : "");
			accioParams.put("servei", procedimentServei.getServeiScsp() != null ? procedimentServei.getServeiScsp().getCodi() + " - " + procedimentServei.getServeiScsp().getDescripcio() : "");
		}
		try {
			DocumentTipus documentTipus = (consulta.getTitularDocumentTipus() != null) ? DocumentTipus.valueOf(consulta.getTitularDocumentTipus()) : null;
			Entitat entitat = procedimentServei.getProcediment().getEntitat();
			Solicitud solicitudEnviar = peticioScspHelper.convertirEnSolicitud(
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
					solicitud.getDadesEspecifiques(),
					procedimentServei,
					getScspHelper());
			consulta.updateEstat(EstatTipus.Processant);
			ResultatEnviamentPeticio resultat = peticioScspHelper.enviarPeticioScsp(
					consulta,
					Arrays.asList(solicitudEnviar),
					true,
					consulta.isRecobriment(),
					getScspHelper());
			accioParams.put("idPeticion", consulta.getScspPeticionId());
			accioParams.put("idSolicitud", consulta.getScspSolicitudId());
			accioParams.put("estat", "[" + resultat.getEstatCodi() + "] " + resultat.getEstatDescripcio());
			if (resultat.isError()) {
				integracioHelper.addAccioError(
						consulta.getScspPeticionId(),
						IntegracioHelper.INTCODI_SERVEIS_SCSP,
						accioDescripcio,
						accioParams,
						IntegracioAccioTipusEnumDto.ENVIAMENT,
						System.currentTimeMillis() - t0,
						"[" + resultat.getErrorCodi() + "] " + resultat.getErrorDescripcio(),
						(Throwable)null);
			} else {
				integracioHelper.addAccioOk(
						consulta.getScspPeticionId(),
						IntegracioHelper.INTCODI_SERVEIS_SCSP,
						accioDescripcio,
						accioParams,
						IntegracioAccioTipusEnumDto.ENVIAMENT,
						System.currentTimeMillis() - t0);
			}
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
		Map<String, String> accioParams = new HashMap<String, String>();
		accioParams.put("consultaId", consultaId.toString());
		long t0 = System.currentTimeMillis();
		Consulta consulta = consultaRepository.findOne(consultaId);
		if (consulta == null) {
			log.debug("No s'ha trobat la consulta (id=" + consultaId + ")");
			throw new ConsultaNotFoundException();
		}
		String accioDescripcio = "Consulta del servei " + consulta.getServeiCodi() + " via recobriment (estat)";
		if (consulta.getProcedimentServei() != null) {
			ProcedimentServei procedimentServei = consulta.getProcedimentServei();
			accioParams.put("procediment", procedimentServei.getProcediment() != null ? procedimentServei.getProcediment().getCodi() + " - " + procedimentServei.getProcediment().getNom() : "");
			accioParams.put("servei", procedimentServei.getServeiScsp() != null ? procedimentServei.getServeiScsp().getCodi() + " - " + procedimentServei.getServeiScsp().getDescripcio() : "");
		}
		try {
			ResultatEnviamentPeticio resultat = getScspHelper().recuperarResultatEnviamentPeticio(
					consulta.getScspPeticionId());
			if (resultat.getIdsSolicituds() != null && resultat.getIdsSolicituds().length > 0) {
				consulta.updateScspSolicitudId(resultat.getIdsSolicituds()[0]);
			}
			// Si l'estat de la consulta és Error vol dir que l'error s'ha processat amb anterioritat
			// i no és necessari actualitzar l'estat. Si l'estat s'actualitza segurament el posarà com
			// a pendent i no seria l'estat correcte.
			if (consulta.getEstat() != EstatTipus.Error) {
				peticioScspHelper.updateEstatConsulta(consulta, resultat, accioParams);
			}
			integracioHelper.addAccioOk(
					consulta.getScspPeticionId(),
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
					consulta.getScspPeticionId(),
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
		String accioDescripcio = "Consulta múltiple del servei " + serveiCodi + " via recobriment";
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
		accioParams.put("procediment", procedimentServei.getProcediment() != null ? procedimentServei.getProcediment().getCodi() + " - " + procedimentServei.getProcediment().getNom() : "");
		accioParams.put("servei", procedimentServei.getServeiScsp() != null ? procedimentServei.getServeiScsp().getCodi() + " - " + procedimentServei.getServeiScsp().getDescripcio() : "");
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
							null,
							null,
							null,
							true,
							true,
							null).
					build();
			conslt.updateEstat(EstatTipus.Pendent);
			List<Solicitud> solicitudsEnviar = new ArrayList<Solicitud>();
			for (RecobrimentSolicitudDto solicitud: solicituds) {
				Solicitud solicitudEnviar = peticioScspHelper.convertirEnSolicitud(
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
						solicitud.getDadesEspecifiques(),
						procedimentServei,
						getScspHelper());
				solicitudsEnviar.add(solicitudEnviar);
			}
			ResultatEnviamentPeticio resultat = peticioScspHelper.enviarPeticioScsp(
					conslt,
					solicitudsEnviar,
					true,
					conslt.isRecobriment(),
					getScspHelper());
			peticioScspHelper.updateEstatConsulta(conslt, resultat, accioParams);
			if (resultat.isError()) {
				integracioHelper.addAccioError(
						idPeticion,
						IntegracioHelper.INTCODI_SERVEIS_SCSP,
						accioDescripcio,
						accioParams,
						IntegracioAccioTipusEnumDto.ENVIAMENT,
						System.currentTimeMillis() - t0,
						"[" + resultat.getErrorCodi() + "] " + resultat.getErrorDescripcio(),
						(Throwable)null);
			} else {
				integracioHelper.addAccioOk(
						idPeticion,
						IntegracioHelper.INTCODI_SERVEIS_SCSP,
						accioDescripcio,
						accioParams,
						IntegracioAccioTipusEnumDto.ENVIAMENT,
						System.currentTimeMillis() - t0);
			}
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
								solicitud.getFinalitat(),
								solicitud.getConsentiment(),
								solicitud.getExpedientId(),
								true,
								false,
								conslt).
						build();
				cs.updateScspSolicitudId(resultat.getIdsSolicituds()[solicitudIndex++]);
				peticioScspHelper.updateEstatConsulta(cs, resultat, null);
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
			return resposta;
		} catch (ConsultaScspException ex) {
			return processarConsultaScspException(ex, conslt, accioDescripcio, accioParams, t0);
		}
	}

	@Override
	public ArxiuDetallDto obtenirArxiuInfo(Long consultaId) {

		try {
			Consulta consulta = consultaRepository.findOne(consultaId);
			if (consulta == null) {
				log.error("No s'ha trobat la consulta (id=" + consultaId + ")");
				throw new ConsultaNotFoundException();
			}
			ArxiuDetallDto arxiuDetall = new ArxiuDetallDto();
			boolean noMock = !"true".equalsIgnoreCase(System.getProperty("es.caib.pinbal.arxiu.document.consultar.mock"));
			es.caib.plugins.arxiu.api.Document arxiuDocument = noMock ? pluginHelper.arxiuDocumentConsultar(
					consulta.getScspPeticionId(),
					consulta.getArxiuDocumentUuid(),
					null,
					false,
					false) : pluginHelper.arxiuDocumentConsultarMock();
			List<Firma> firmes = arxiuDocument.getFirmes();
			arxiuDetall.setIdentificador(arxiuDocument.getIdentificador());
			arxiuDetall.setNom(arxiuDocument.getNom());
			DocumentMetadades metadades = arxiuDocument.getMetadades();
			if (metadades != null) {
				arxiuDetall.setEniVersio(metadades.getVersioNti());
				arxiuDetall.setEniIdentificador(metadades.getIdentificador());
				arxiuDetall.setSerieDocumental(metadades.getSerieDocumental());
				arxiuDetall.setEniDataCaptura(metadades.getDataCaptura());

				arxiuDetall.setEniOrigen(ArxiuConversions.getOrigen(metadades.getOrigen()));

				arxiuDetall.setEniEstatElaboracio(ArxiuConversions.getEstatElaboracio(metadades.getEstatElaboracio()));

//				if (metadades.getTipusDocumental() != null) {
//					List<TipusDocumentalEntity> tipos = tipusDocumentalRepository.findByCodi(metadades.getTipusDocumental().toString());
//					if (Utils.isNotEmpty(tipos)) {
//						TipusDocumentalDto tipus = conversioTipusHelper.convertir(tipos.get(0), TipusDocumentalDto.class);
//						arxiuDetall.setEniTipusDocumental(tipus.getCodiNom());
//					} else {
//						arxiuDetall.setEniTipusDocumental(metadades.getTipusDocumental().toString());
//					}
//				}
//
//				if (metadades.getTipusDocumental() == null && metadades.getTipusDocumentalAddicional() != null) {
//					log.info("Tipus documental addicional: " + metadades.getTipusDocumentalAddicional());
//					TipusDocumentalEntity tipusDocumental = tipusDocumentalRepository.findByCodiAndEntitat(
//							metadades.getTipusDocumentalAddicional(),
//							entitat);
//
//					if (tipusDocumental != null) {
//						arxiuDetall.setEniTipusDocumentalAddicional(tipusDocumental.getNomEspanyol());
//					} else {
//						List<TipusDocumental> docsAddicionals = pluginHelper.documentTipusAddicionals();
//
//						for (TipusDocumental docAddicional : docsAddicionals) {
//							if (docAddicional.getCodi().equals(metadades.getTipusDocumentalAddicional())) {
//								arxiuDetall.setEniTipusDocumentalAddicional(docAddicional.getNom());
//							}
//						}
//					}
//
//					arxiuDetall.setEniTipusDocumentalAddicional(tipusDocumental.getNomEspanyol());
//				}

//				arxiuDetall.setEniOrgans(getOrgansAmbNoms(metadades.getOrgans()));
				if (metadades.getFormat() != null) {
					arxiuDetall.setEniFormat(metadades.getFormat().toString());
				}
				arxiuDetall.setEniDocumentOrigenId(metadades.getIdentificadorOrigen());

				final String fechaSelladoKey = "eni:fecha_sellado";
				if (metadades.getMetadadesAddicionals() != null && metadades.getMetadadesAddicionals().containsKey(fechaSelladoKey)) {
					try {
						DateFormat dfIn= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
						DateFormat dfOut = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
						Date fechaSelladoValor = dfIn.parse(metadades.getMetadadesAddicionals().get(fechaSelladoKey).toString());
						String fechaSelladoValorStr = dfOut.format(fechaSelladoValor);
						metadades.getMetadadesAddicionals().put(fechaSelladoKey, fechaSelladoValorStr);
					} catch (ParseException e) {
						log.error(e.getMessage(), e);
					}
				}
				arxiuDetall.setMetadadesAddicionals(metadades.getMetadadesAddicionals());

				if (arxiuDocument.getContingut() != null) {
					arxiuDetall.setContingutArxiuNom(arxiuDocument.getContingut().getArxiuNom());
					arxiuDetall.setContingutTipusMime(arxiuDocument.getContingut().getTipusMime());
				}
			}
			if (arxiuDocument.getEstat() != null) {
				if (DocumentEstat.ESBORRANY.equals(arxiuDocument.getEstat())) {
					arxiuDetall.setArxiuEstat(ArxiuEstatEnumDto.ESBORRANY);
				} else if (DocumentEstat.DEFINITIU.equals(arxiuDocument.getEstat())) {
					arxiuDetall.setArxiuEstat(ArxiuEstatEnumDto.DEFINITIU);
				}
			}

			// ##################### CONTINGUT ##################################
//			if (continguts != null) {
//				List<ArxiuContingutDto> detallFills = new ArrayList<ArxiuContingutDto>();
//				for (ContingutArxiu cont: continguts) {
//					ArxiuContingutDto detallFill = new ArxiuContingutDto();
//					detallFill.setIdentificador(
//							cont.getIdentificador());
//					detallFill.setNom(
//							cont.getNom());
//					if (cont.getTipus() != null) {
//						switch (cont.getTipus()) {
//							case EXPEDIENT:
//								detallFill.setTipus(ArxiuContingutTipusEnumDto.EXPEDIENT);
//								break;
//							case DOCUMENT:
//								detallFill.setTipus(ArxiuContingutTipusEnumDto.DOCUMENT);
//								break;
//							case CARPETA:
//								detallFill.setTipus(ArxiuContingutTipusEnumDto.CARPETA);
//								break;
//						}
//					}
//					detallFills.add(detallFill);
//				}
//				arxiuDetall.setFills(detallFills);
//			}
			if (firmes != null) {
				List<ArxiuFirmaDto> dtos = new ArrayList<>();
				for (Firma firma : firmes) {
					ArxiuFirmaDto dto = new ArxiuFirmaDto();

					if (firma.getTipus() != null) {
						switch (firma.getTipus()) {
							case CSV:
								dto.setTipus(ArxiuFirmaTipusEnumDto.CSV);
								break;
							case XADES_DET:
								dto.setTipus(ArxiuFirmaTipusEnumDto.XADES_DET);
								break;
							case XADES_ENV:
								dto.setTipus(ArxiuFirmaTipusEnumDto.XADES_ENV);
								break;
							case CADES_DET:
								dto.setTipus(ArxiuFirmaTipusEnumDto.CADES_DET);
								break;
							case CADES_ATT:
								dto.setTipus(ArxiuFirmaTipusEnumDto.CADES_ATT);
								break;
							case PADES:
								dto.setTipus(ArxiuFirmaTipusEnumDto.PADES);
								break;
							case SMIME:
								dto.setTipus(ArxiuFirmaTipusEnumDto.SMIME);
								break;
							case ODT:
								dto.setTipus(ArxiuFirmaTipusEnumDto.ODT);
								break;
							case OOXML:
								dto.setTipus(ArxiuFirmaTipusEnumDto.OOXML);
								break;
						}
					}
					if (firma.getPerfil() != null) {
						switch (firma.getPerfil()) {
							case BES:
								dto.setPerfil(ArxiuFirmaPerfilEnumDto.BES);
								break;
							case EPES:
								dto.setPerfil(ArxiuFirmaPerfilEnumDto.EPES);
								break;
							case LTV:
								dto.setPerfil(ArxiuFirmaPerfilEnumDto.LTV);
								break;
							case T:
								dto.setPerfil(ArxiuFirmaPerfilEnumDto.T);
								break;
							case C:
								dto.setPerfil(ArxiuFirmaPerfilEnumDto.C);
								break;
							case X:
								dto.setPerfil(ArxiuFirmaPerfilEnumDto.X);
								break;
							case XL:
								dto.setPerfil(ArxiuFirmaPerfilEnumDto.XL);
								break;
							case A:
								dto.setPerfil(ArxiuFirmaPerfilEnumDto.A);
								break;
							case BASIC:
								dto.setPerfil(ArxiuFirmaPerfilEnumDto.BASIC);
								break;
							case Basic:
								dto.setPerfil(ArxiuFirmaPerfilEnumDto.Basic);
								break;
							case BASELINE_B_LEVEL:
								dto.setPerfil(ArxiuFirmaPerfilEnumDto.BASELINE_B_LEVEL);
								break;
							case BASELINE_LTA_LEVEL:
								dto.setPerfil(ArxiuFirmaPerfilEnumDto.BASELINE_LTA_LEVEL);
								break;
							case BASELINE_LT_LEVEL:
								dto.setPerfil(ArxiuFirmaPerfilEnumDto.BASELINE_LT_LEVEL);
								break;
							case BASELINE_T:
								dto.setPerfil(ArxiuFirmaPerfilEnumDto.BASELINE_T);
								break;
							case BASELINE_T_LEVEL:
								dto.setPerfil(ArxiuFirmaPerfilEnumDto.BASELINE_T_LEVEL);
								break;
							case LTA:
								dto.setPerfil(ArxiuFirmaPerfilEnumDto.LTA);
								break;
						}
					}
					dto.setFitxerNom(firma.getFitxerNom());
					if (ArxiuFirmaTipusEnumDto.CSV.equals(dto.getTipus())) {
						dto.setContingut(firma.getContingut());
					}
					dto.setTipusMime(firma.getTipusMime());
					dto.setCsvRegulacio(firma.getCsvRegulacio());
					dtos.add(dto);
				}
				arxiuDetall.setFirmes(dtos);
			}
			return arxiuDetall;
		} catch (Exception ex) {
			log.error("Error consultat la informació del arxiu", ex);
			return new ArxiuDetallDto();
		}
	}

//	private List<String> getOrgansAmbNoms(List<String> organsCodis) {
//		List<String> organsCodisNoms = new ArrayList<>();
//		if (Utils.isNotEmpty(organsCodis)) {
//			for (String organCodi : organsCodis) {
//				OrganGestorEntity organ = organGestorRepository.findByCodi(organCodi);
//				if (organ != null) {
//					organsCodisNoms.add(organ.getCodiINom());
//				} else {
//					organsCodisNoms.add(organCodi);
//				}
//			}
//		}
//
//		return organsCodisNoms;
//	}

	@Override
	public JustificantDto obtenirJustificant(Long id, boolean isAdmin) throws ConsultaNotFoundException, JustificantGeneracioException {
		log.debug("Generant justificant per a la consulta (id=" + id + ")");

		Consulta consulta = consultaRepository.findOne(id);
		if (consulta == null) {
			log.error("No s'ha trobat la consulta (id=" + id + ")");
			throw new ConsultaNotFoundException();
		}
		if (!isAdmin) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (!auth.getName().equals(consulta.getCreatedBy().getCodi())) {
				log.error("La consulta (id=" + id + ") no pertany a aquest usuari");
				throw new ConsultaNotFoundException();
			}
		}
		return obtenirJustificantComu(consulta, true, true);
	}

	@Override
	public JustificantDto obtenirJustificant(
			String idpeticion,
			String idsolicitud,
			boolean versioImprimible,
			boolean ambContingut) throws ConsultaNotFoundException, JustificantGeneracioException {
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
		return obtenirJustificantComu(consulta, ambContingut, versioImprimible);
	}

	@Override
	public JustificantDto reintentarGeneracioJustificant(
			Long id,
			boolean descarregar,
			boolean isAdmin) throws ConsultaNotFoundException, JustificantGeneracioException {
		log.debug("Reintentant generació del justificant per a la consulta (id=" + id + ")");
		Consulta consulta = consultaRepository.findOne(id);
		if (consulta == null) {
			log.error("No s'ha trobat la consulta (id=" + id + ")");
			throw new ConsultaNotFoundException();
		}
		if (!isAdmin) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (!auth.getName().equals(consulta.getCreatedBy().getCodi())) {
				log.error("La consulta (id=" + id + ") no pertany a aquest usuari");
				throw new ConsultaNotFoundException();
			}
		}
		return obtenirJustificantComu(consulta, descarregar, true);
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
    public List<ConsultaDto> findByFiltrePerAuditor(Long entitatId, ConsultaFiltreDto filtre) throws EntitatNotFoundException {
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

		Page<ConsultaDto> page = findByEntitatIUsuariFiltrePaginat(
				entitat,
				null,
				filtre,
				new PageRequest(0, Integer.MAX_VALUE, new Sort(new Sort.Order(Sort.Direction.DESC, "scspPeticionId"))),
				false,
				false,
				false,
				false);

		return page.getContent();
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
		return findByFiltrePaginatAdmin(
				filtre,
				pageable,
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
			entitat = getEntitat(entitatCodi);
			if (procedimentCodi != null) {
				procediment = getProcediment(procedimentCodi, entitat);
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

	@Override
	public DadesObertesResposta findByFiltrePerOpenDataV2(ConsultaOpenDataDto consultaOpenDataDto) throws EntitatNotFoundException, ProcedimentNotFoundException {
		log.debug("Consultant informació per opendata (" + consultaOpenDataDto + ")");
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		Entitat entitat = null;
		Procediment procediment = null;
		if (consultaOpenDataDto.getEntitatCodi() != null) {
			entitat = getEntitat(consultaOpenDataDto.getEntitatCodi());
			if (consultaOpenDataDto.getProcedimentCodi() != null) {
				procediment = getProcediment(consultaOpenDataDto.getProcedimentCodi(), entitat);
			}
		}

		Integer numElements = consultaRepository.countByOpendata(
				entitat == null,
				entitat != null ? entitat.getId() : null,
				procediment == null,
				procediment != null ? procediment.getId() : null,
				isBlank(consultaOpenDataDto.getServeiCodi()),
				!isBlank(consultaOpenDataDto.getServeiCodi()) ? consultaOpenDataDto.getServeiCodi() : null,
				consultaOpenDataDto.getDataInici() == null,
				consultaOpenDataDto.getDataInici(),
				consultaOpenDataDto.getDataFi() == null,
				consultaOpenDataDto.getDataFi());
		Page<DadesObertesRespostaConsulta> dades = consultaRepository.findByOpendata(
				entitat == null,
				entitat != null ? entitat.getId() : null,
				procediment == null,
				procediment != null ? procediment.getId() : null,
				consultaOpenDataDto.getServeiCodi() == null,
				consultaOpenDataDto.getServeiCodi(),
				consultaOpenDataDto.getDataInici() == null,
				consultaOpenDataDto.getDataInici(),
				consultaOpenDataDto.getDataFi() == null,
				consultaOpenDataDto.getDataFi(),
				new PageRequest(consultaOpenDataDto.getPagina(), consultaOpenDataDto.getMida()));

		Integer totalPagines = (numElements.intValue() + consultaOpenDataDto.getMida() - 1)/consultaOpenDataDto.getMida();
		String nextUrl = null;
		if (totalPagines.intValue() > consultaOpenDataDto.getPagina().intValue() + 1) {
			nextUrl = consultaOpenDataDto.getAppPath() + "?historic=false";
			nextUrl += "&dataInici=" + sdf.format(consultaOpenDataDto.getDataInici());
			nextUrl += "&dataFi=" + sdf.format(consultaOpenDataDto.getDataFi());
			nextUrl += !isBlank(consultaOpenDataDto.getEntitatCodi()) ? "&entitatCodi=" + consultaOpenDataDto.getEntitatCodi() : "";
			nextUrl += !isBlank(consultaOpenDataDto.getProcedimentCodi()) ? "&procedimentCodi=" + consultaOpenDataDto.getProcedimentCodi() : "";
			nextUrl += !isBlank(consultaOpenDataDto.getServeiCodi()) ? "&serveiCodi=" + consultaOpenDataDto.getServeiCodi() : "";
			nextUrl += "&pagina=" + (consultaOpenDataDto.getPagina() + 1);
			nextUrl += "&mida=" + consultaOpenDataDto.getMida();
		}

		return DadesObertesResposta.builder()
				.totalElements(numElements)
				.paginaActual(consultaOpenDataDto.getPagina() + 1)
				.totalPagines(totalPagines)
				.properaPagina(nextUrl)
				.dades(dades.getContent())
				.build();
	}

	private Procediment getProcediment(String procedimentCodi, Entitat entitat) throws ProcedimentNotFoundException {
		Procediment procediment = procedimentRepository.findByEntitatAndCodi(entitat, procedimentCodi);
		if (procediment == null) {
			log.debug("No s'ha trobat el procediment (codi=" + procedimentCodi + ")");
			throw new ProcedimentNotFoundException();
		}
		return procediment;
	}

	private Entitat getEntitat(String entitatCodi) throws EntitatNotFoundException {
		Entitat entitat = entitatRepository.findByCodi(entitatCodi);
		if (entitat == null) {
			log.debug("No s'ha trobat l'entitat (codi=" + entitatCodi + ")");
			throw new EntitatNotFoundException();
		}
		return entitat;
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
			if (estadisticaActual == null ||
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
		return peticioScspEstadistiquesHelper.consultaEstadistiques();
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
		List<Consulta> pendents = consultaRepository.findByEstatAndMultipleOrderByIdAsc(EstatTipus.Processant, true);
		for (final Consulta pendent: pendents) {
			TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
			transactionTemplate.execute(new TransactionCallbackWithoutResult() {
				protected void doInTransactionWithoutResult(TransactionStatus status) {
					try {
						ResultatEnviamentPeticio resultat = getScspHelper().recuperarResultatEnviamentPeticio(pendent.getScspPeticionId());
						peticioScspHelper.updateEstatConsulta(pendent, resultat, null);
						consultaRepository.saveAndFlush(pendent);
						for (Consulta fill: pendent.getFills()) {
							peticioScspHelper.updateEstatConsulta(fill, resultat, null);
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
				obtenirJustificantComu(pendent, false, false);
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
			if (esPotTancar && pluginHelper.isPluginArxiuActiu()) {
				TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
				transactionTemplate.execute(new TransactionCallback<JustificantGeneracioException>() {
					@Override
					public JustificantGeneracioException doInTransaction(TransactionStatus status) {
						try {
							Expedient expedient = pluginHelper.arxiuExpedientConsultar(pendent.getScspPeticionId(), pendent.getArxiuExpedientUuid());
							if (!expedient.getMetadades().getEstat().equals(ExpedientEstat.TANCAT)) {
								pluginHelper.arxiuExpedientTancar(pendent.getScspPeticionId(), pendent.getArxiuExpedientUuid());
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

	@Transactional
	@Override
	public void autoEnviarPeticionsPendents() {
		log.debug("Iniciant enviament automàtic de les peticions pendents");
		long t0 = System.currentTimeMillis();
		List<Consulta> pendents = consultaRepository.findByEstatAndMultipleAndConsentimentNotNullOrderByIdAsc(EstatTipus.Pendent, false);
		int i = 0;
		for (final Consulta pendent: pendents) {
			if (peticioScspHelper.isEnviarConsultaServei(pendent, true)) {
				peticioScspHelper.enviarPeticioScspPendent(pendent.getId(), scspHelper);
				i++;
			}
		}
		log.debug("Finalitzat l'enviament automàtic de les " + i + " peticions pendents (" + (System.currentTimeMillis() - t0) + "ms)");
	}

	@Override
	public boolean isOptimitzarTransaccionsNovaConsulta() {
		log.debug("Consultant optimització transaccions en nova consulta");
		return configHelper.getAsBoolean("es.caib.pinbal.optimitzar.transaccions.nova.consulta", false);
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

	@Transactional(readOnly = true)
	@Override
	public List<InformeProcedimentServeiDto> informeUsuarisEntitatOrganProcedimentServei(Long entitatId, String rolActual, InformeRepresentantFiltreDto filtre) {
		log.debug("Obtenint informe usuaris agrupats per entitat, òrgan gestor, procediment i servei");
		List<InformeProcedimentServeiDto> resposta = new ArrayList<InformeProcedimentServeiDto>();

		List<ProcedimentServei> procedimentServeis = new ArrayList<ProcedimentServei>();
		if (ROLE_ADMIN.equals(rolActual)) {
			procedimentServeis = procedimentServeiRepository.findAllActius();
		} else if (ROLE_REPRES.equals(rolActual)) {
			procedimentServeis = procedimentServeiRepository.findAllActiusAmbFiltre(
					entitatId,
					filtre.getOrganGestorId() == null,
					filtre.getOrganGestorId(),
					filtre.getProcedimentId() == null,
					filtre.getProcedimentId(),
					filtre.getServeiCodi() == null,
					filtre.getServeiCodi());
		}
		for (ProcedimentServei procedimentServei : procedimentServeis) {
			List<String> usuarisAmbPermis = new ArrayList<String>();
			try {
				usuarisAmbPermis = procedimentService.findUsuarisAmbPermisPerServei(procedimentServei.getProcediment().getId(), procedimentServei.getServei());
			} catch (ProcedimentNotFoundException e) {
				log.error("No s'ha trobat el Procediment (id=" + procedimentServei.getProcediment().getId() + ")");
			} catch (ProcedimentServeiNotFoundException e) {
				log.error("No s'ha trobat el Servei (codi=" + procedimentServei.getServei() + ")");
			}

			for (String usuariAmbPermis: usuarisAmbPermis) {
				EntitatUsuari entitatUsuari = entitatUsuariRepository.findByEntitatIdAndUsuariCodi(procedimentServei.getProcediment().getEntitat().getId(), usuariAmbPermis);
				if (entitatUsuari != null) {
					resposta.add(toInformeProcedimentServeiDto(procedimentServei, entitatUsuari));
				}
			}
		}

		return resposta;
	}

	private InformeProcedimentServeiDto toInformeProcedimentServeiDto(ProcedimentServei procedimentServei,
																	  EntitatUsuari entitatUsuari) {
		InformeProcedimentServeiDto informeProcedimentServei = new InformeProcedimentServeiDto();
		Servicio servicio = getScspHelper().getServicio(procedimentServei.getServei());
		informeProcedimentServei.setEntitatCodi(procedimentServei.getProcediment().getEntitat().getCodi());
		informeProcedimentServei.setEntitatNom(procedimentServei.getProcediment().getEntitat().getNom());
		informeProcedimentServei.setEntitatCif(procedimentServei.getProcediment().getEntitat().getCif());
		informeProcedimentServei.setOrganGestorCodi(procedimentServei.getProcediment().getOrganGestor() != null ? procedimentServei.getProcediment().getOrganGestor().getCodi() : null);
		informeProcedimentServei.setOrganGestorNom(procedimentServei.getProcediment().getOrganGestor() != null ? procedimentServei.getProcediment().getOrganGestor().getNom() : null);
		informeProcedimentServei.setOrganGestorActiu(procedimentServei.getProcediment().getOrganGestor() != null ? procedimentServei.getProcediment().getOrganGestor().isActiu() : true);
		informeProcedimentServei.setProcedimentCodi(procedimentServei.getProcediment().getCodi());
		informeProcedimentServei.setProcedimentNom(procedimentServei.getProcediment().getNom());
		informeProcedimentServei.setServeiCodi(servicio != null ? servicio.getCodCertificado() : null);
		informeProcedimentServei.setServeiNom(servicio != null ? servicio.getDescripcion() : null);
		informeProcedimentServei.setUsuariCodi(entitatUsuari.getUsuari().getCodi());
		informeProcedimentServei.setUsuariNif(entitatUsuari.getUsuari().getNif());
		informeProcedimentServei.setUsuariNom(entitatUsuari.getUsuari().getNom());
		return informeProcedimentServei;
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

    /*private ResultatEnviamentPeticio enviarPeticioScsp(
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
	}*/

    /*private void initEstadistiquesCarrega() {
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
	}*/

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
				resposta.setRespostaData(rpt.getRespostaData());
				resposta.setRespostaXml(rpt.getRespostaXml());
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
			if (resposta.isEstatError()) {
				resposta.setRespostaXml(tokenRepository.getFaultError(consulta.getScspPeticionId()));
			}
			if (resposta.getRespostaXml() != null && !resposta.getRespostaXml().isEmpty()) {
				resposta.setHiHaResposta(true);
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
								"filtre.funcionari=" + filtre.getFuncionari() + ", " +
								"filtre.usuari=" + filtre.getUsuari() + ", ") : "") +
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
			consultaRepository.setSessionOptimizerModeToRule();
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
					filtre.getFuncionari() == null || filtre.getFuncionari().isEmpty(),
					filtre.getFuncionari(),
					filtre.getUsuari() == null || filtre.getUsuari().isEmpty(),
					filtre.getUsuari(),
					multiple,
					nomesSensePare,
					pageable);
		}
		log.debug("[S_CONS] Consulta a la base de dades (" + (System.currentTimeMillis() - t0) + " ms)");
		t0 = System.currentTimeMillis();
		Page<ConsultaDto> paginaConsultesDto = dtoMappingHelper.pageEntities2pageDto(paginaConsultes, ConsultaDto.class, pageable);
		log.debug("[S_CONS] Conversió a DTO (" + (System.currentTimeMillis() - t0) + " ms)");
        /*t0 = System.currentTimeMillis();
		for (ConsultaDto consulta: paginaConsultesDto.getContent()) {
			consulta.setServeiDescripcio(
					getScspHelper().getServicioDescripcion(
							consulta.getServeiCodi()));
		}
		log.debug("[S_CONS] Consulta de descripcions de serveis (" + (System.currentTimeMillis() - t0) + " ms)");*/
		t0 = System.currentTimeMillis();
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
		log.debug("[S_CONS] Consulta de peticions addicionals (" + (System.currentTimeMillis() - t0) + " ms)");
		return  paginaConsultesDto;
	}

	private Page<ConsultaDto> findByFiltrePaginatAdmin(
			ConsultaFiltreDto filtre,
			Pageable pageable,
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
								"filtre.usuari=" + filtre.getUsuari() + ", " +
								"filtre.funcionari=" + filtre.getFuncionari() + ", " +
								"filtre.recobriment=" + filtre.getRecobriment() + ", ") : "") +
				((pageable != null) ? (
						"paginacio.paginaNum=" + pageable.getPageNumber() + ", " +
								"paginacio.paginaTamany=" + pageable.getPageSize() + ", ") : "") + ")");
		long t0 = System.currentTimeMillis();
		Page<Consulta> paginaConsultes = consultaRepository.findByFiltrePaginatAdmin(
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
				filtre.getFuncionari() == null || filtre.getFuncionari().isEmpty(),
				filtre.getFuncionari(),
				filtre.getUsuari() == null || filtre.getUsuari().isEmpty(),
				filtre.getUsuari(),
				filtre.getRecobriment() == null,
				filtre.getRecobriment(),
				pageable);
		log.debug("[S_CONS] Consulta a la base de dades (" + (System.currentTimeMillis() - t0) + " ms)");
		t0 = System.currentTimeMillis();
		Page<ConsultaDto> paginaConsultesDto = dtoMappingHelper.pageEntities2pageDto(
				paginaConsultes,
				ConsultaDto.class,
				pageable);
		log.debug("[S_CONS] Conversió a DTO (" + (System.currentTimeMillis() - t0) + " ms)");
		t0 = System.currentTimeMillis();
        /*for (ConsultaDto consulta: paginaConsultesDto.getContent()) {
			consulta.setServeiDescripcio(
					getScspHelper().getServicioDescripcion(
							consulta.getServeiCodi()));
		}*/
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
		if (consultaTerData) {
			for (ConsultaDto consulta: paginaConsultesDto.getContent()) {
				try {
					consulta.setTerData(getScspHelper().getTerPeticion(
							consulta.getScspPeticionId()));
				} catch (es.scsp.common.exceptions.ScspException ex) {
					log.error("No s'han pogut consultar el TER de la petició (id=" + consulta.getScspPeticionId() + ")", ex);
				}
			}
		}
		log.debug("[S_CONS] Consulta de peticions addicionals (" + (System.currentTimeMillis() - t0) + " ms)");
		return paginaConsultesDto;
	}

	private JustificantDto obtenirJustificantComu(
			final Consulta consulta,
			final boolean descarregar,
			final boolean versioImprimible) throws JustificantGeneracioException {
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
										getScspHelper(),
										versioImprimible);
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
				TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
				Object[] params = transactionTemplate.execute(new TransactionCallback<Object[]>() {
					@Override
					public Object[] doInTransaction(TransactionStatus status) {
						Consulta consultaRef = consultaRepository.getOne(consulta.getId());
						Object[] params = new Object[3];
						params[0] = consultaRef.getJustificantEstat();
						params[1] = consultaRef.getArxiuDocumentUuid();
						params[2] = consultaRef.getScspPeticionId();
						return params;
					}
				});
				JustificantEstat estat = (JustificantEstat) params[0];
				String documentUuid = (String) params[1];
				String peticioId = (String) params[2];

				if (JustificantEstat.OK.equals(estat)) {
					return JustificantDto.builder().error(true).errorDescripcio("El justificant no s'ha generat, o no s'ha desat a l'arxiu").build();
				}
				if (documentUuid == null) {
					return JustificantDto.builder().error(true).errorDescripcio("El justificant no es troba a l'arxiu").build();
				}
				if (versioImprimible) {
					try {
						es.caib.plugins.arxiu.api.Document documentArxiu = pluginHelper.arxiuDocumentConsultar(
								peticioId,
								documentUuid,
								null,
								false,
								false);
						if (documentArxiu != null && documentArxiu.getMetadades() != null && documentArxiu.getMetadades().getCsv() != null) {
							return JustificantDto.builder().arxiuCsv(documentArxiu.getMetadades().getCsv()).build();
						} else {
							return JustificantDto.builder().error(true).errorDescripcio("No s'ha pogut recuperar el CSV del justificant").build();
						}
					} catch (Exception ex) {
						log.error("No ha estat possible obtenir el document de l'arxiu.", ex);
						throw new JustificantGeneracioException("No ha estat possible recuperar la informació del docuement a l'arxiu.");
					}
				} else {
					return JustificantDto.builder().arxiuUuid(documentUuid).build();
				}
			}
		} else {
			throw new JustificantGeneracioException("La consulta no està en estat TRAMITADA");
		}
	}

    /*private Solicitud convertirEnSolicitud(
			ConsultaDto consulta,
			ProcedimentServei procedimentServei) {
		Solicitud solicitud = new Solicitud();
		solicitud.setServeiCodi(consulta.getServeiCodi());
		Procediment procediment = procedimentRepository.findOne(procedimentServei.getProcediment().getId());
		solicitud.setProcedimentCodi(
				(procedimentServei.getProcedimentCodi() != null && !("".equalsIgnoreCase(procedimentServei.getProcedimentCodi())) ? 
						procedimentServei.getProcedimentCodi("[" + scspException.getScspCode() + "] " + ) :
						procediment.getCodi()));
		solicitud.setProcedimentNom(procediment.getNom());
		solicitud.setProcedimentValorCampAutomatizado(procediment.getValorCampAutomatizado());
		if (procediment.getValorCampClaseTramite() != null) {
			solicitud.setProcedimentValorCampClaseTramite(procediment.getValorCampClaseTramite().getShortValue());
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
	}*/




    /*private void updateEstatConsulta(
			Consulta consulta,
			ResultatEnviamentPeticio resultat,
			Map<String, String> accioParams) {
		String error = null;
		if (resultat.isError()) {
			error = "[" + resultat.getErrorCodi() + "] " + resultat.getErrorDescripcio();
			updateEstatConsultaError(consulta, error);
		} else if ("0001".equals(resultat.getEstatCodi())) {
			consulta.updateEstat(EstatTipus.Pendent);
		} else if ("0002".equals(resultat.getEstatCodi())) {
			consulta.updateEstat(EstatTipus.Processant);
		} else if ("0003".equals(resultat.getEstatCodi())) {
			consulta.updateEstat(EstatTipus.Tramitada);
		} else if ("0004".equals(resultat.getEstatCodi())) {
			consulta.updateEstat(EstatTipus.Processant);
		}
		if (accioParams != null) {
			accioParams.put("idPeticion", consulta.getScspPeticionId());
			accioParams.put("idSolicitud", consulta.getScspSolicitudId());
			accioParams.put("estat", "[" + resultat.getEstatCodi() + "] " + resultat.getEstatDescripcio());
			if (error != null) {
				accioParams.put("error", error);
			}
		}
	}

	private void updateEstatConsultaError(
			Consulta consulta,
			String error) {
		consulta.updateEstat(EstatTipus.Error);
		consulta.updateEstatError(error);
	}*/

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

    /*private long getCountFromCarregues(
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
	}*/

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
			Usuari usuariActual = usuariHelper.getUsuariAutenticat();
			if (usuariActual != null) {
				log.info("[CNS_ERR] L'usuari '" + usuariActual.getCodi() + "' amb idioma '" + usuariActual.getIdioma() + "' ha ralitzat una consulta que ha donat error.");
			}
			Locale locale = usuariActual != null && usuariActual.getIdioma() != null ? new Locale(usuariActual.getIdioma().toLowerCase()) : new Locale("ca");
			log.info("[CNS_ERR] S'utilitza el locale '" + locale.toString() + "' per a generar el missatge d'error.");
			String error = generateErrorMessage(ex, locale);
			peticioScspHelper.updateEstatConsultaError(
					conslt,
					error);
			Consulta saved = consultaRepository.save(conslt);
			integracioHelper.addAccioError(
					conslt.getScspPeticionId(),
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

	private String generateErrorMessage(Throwable throwable, Locale locale) {
		String error;
		if (throwable.getCause() != null && throwable.getCause() instanceof es.scsp.common.exceptions.ScspException) {
			es.scsp.common.exceptions.ScspException scspException = (es.scsp.common.exceptions.ScspException) throwable.getCause();
			String codi = scspException.getScspCode();
			String msg = scspException.getMessage();
			String detall = ERROR_SEPARADOR + scspException.getMessage();
			switch (scspException.getScspCode()) {
				case "0001":
					error = messageSource.getMessage("consulta.scsp.processar.error.0001", new Object[] {codi}, locale);
					break;
				case "0002":
					error = messageSource.getMessage("consulta.scsp.processar.error.0002", new Object[] {codi}, locale);
					break;
				case "0003":
					error = messageSource.getMessage("consulta.scsp.processar.error.0003", new Object[] {codi}, locale);
					break;
				case "0101":
				case "0102":
					error = messageSource.getMessage("consulta.scsp.processar.error.0102", new Object[] {codi}, locale) + detall;
					break;
				case "0103":
				case "0201":
				case "0202":
				case "0203":
				case "0204":
				case "0205":
				case "0206":
				case "0207":
				case "0208":
				case "0209":
				case "0210":
				case "0211":
				case "0212":
				case "0213":
				case "0214":
				case "0215":
				case "0216":
				case "0217":
				case "0218":
				case "0219":
				case "0220":
				case "0221":
				case "0222":
				case "0223":
				case "0224":
					error = messageSource.getMessage("consulta.scsp.processar.error.0224", new Object[] {codi, msg}, locale);
					break;
				case "0225":
					error = messageSource.getMessage("consulta.scsp.processar.error.0225", new Object[] {codi}, locale) + detall;
					break;
				case "0226":
					error = messageSource.getMessage("consulta.scsp.processar.error.0226", new Object[] {codi}, locale) + detall;
					break;
				case "0227":
					error = messageSource.getMessage("consulta.scsp.processar.error.0227", new Object[] {codi, getError227(msg, locale)}, locale) + detall;
					break;
				case "0228":
					error = messageSource.getMessage("consulta.scsp.processar.error.0228", new Object[] {codi}, locale) + detall;
					break;
				case "0229":
					error = messageSource.getMessage("consulta.scsp.processar.error.0229", new Object[] {codi}, locale) + detall;
					break;
				case "0230":
					error = messageSource.getMessage("consulta.scsp.processar.error.0230", new Object[] {codi}, locale) + detall;
					break;
				case "0231":
					error = messageSource.getMessage("consulta.scsp.processar.error.0231", new Object[] {codi}, locale) + detall;
					break;
				case "0232":
					error = messageSource.getMessage("consulta.scsp.processar.error.0232", new Object[] {codi}, locale) + detall;
					break;
				case "0233":
					error = messageSource.getMessage("consulta.scsp.processar.error.0233", new Object[] {codi}, locale) + detall;
					break;
				case "0234":
					error = messageSource.getMessage("consulta.scsp.processar.error.0234", new Object[] {codi}, locale) + detall;
					break;
				case "0235":
					error = messageSource.getMessage("consulta.scsp.processar.error.0235", new Object[] {codi}, locale) + detall;
					break;
				case "0236":
					error = messageSource.getMessage("consulta.scsp.processar.error.0236", new Object[] {codi}, locale) + detall;
					break;
				case "0237":
					error = messageSource.getMessage("consulta.scsp.processar.error.0237", new Object[] {codi}, locale) + detall;
					break;
				case "0238":
					error = messageSource.getMessage("consulta.scsp.processar.error.0238", new Object[] {codi}, locale) + detall;
					break;
				case "0239":
					error = messageSource.getMessage("consulta.scsp.processar.error.0239", new Object[] {codi}, locale) + detall;
					break;
				case "0240":
					error = messageSource.getMessage("consulta.scsp.processar.error.0240", new Object[] {codi}, locale) + detall;
					break;
				case "0241":
					error = messageSource.getMessage("consulta.scsp.processar.error.0241", new Object[] {codi}, locale) + detall;
					break;
				case "0242":
					error = messageSource.getMessage("consulta.scsp.processar.error.0242", new Object[] {codi, getError242(msg, locale)}, locale) + detall;
					break;
				case "0243":
					error = messageSource.getMessage("consulta.scsp.processar.error.0243", new Object[] {codi}, locale) + detall;
					break;
				case "0244":
					error = messageSource.getMessage("consulta.scsp.processar.error.0244", new Object[] {codi}, locale) + detall;
					break;
				case "0245":
					error = messageSource.getMessage("consulta.scsp.processar.error.0245", new Object[] {codi}, locale) + detall;
					break;
				case "0246":
					error = messageSource.getMessage("consulta.scsp.processar.error.0246", new Object[] {codi}, locale) + detall;
					break;
				case "0247":
					error = messageSource.getMessage("consulta.scsp.processar.error.0247", new Object[] {codi}, locale) + detall;
					break;
				case "0248":
					error = messageSource.getMessage("consulta.scsp.processar.error.0248", new Object[] {codi}, locale) + detall;
					break;
				case "0249":
					error = messageSource.getMessage("consulta.scsp.processar.error.0249", new Object[] {codi}, locale) + detall;
					break;
				case "0250":
					error = messageSource.getMessage("consulta.scsp.processar.error.0250", new Object[] {codi}, locale) + detall;
					break;
				case "0251":
					error = messageSource.getMessage("consulta.scsp.processar.error.0251", new Object[] {codi}, locale) + detall;
					break;
				case "0252":
					error = messageSource.getMessage("consulta.scsp.processar.error.0252", new Object[] {codi, getError252(msg, locale)}, locale) + detall;
					break;
				case "0253":
					error = messageSource.getMessage("consulta.scsp.processar.error.0253", new Object[] {codi}, locale) + detall;
					break;
				case "0254":
					error = messageSource.getMessage("consulta.scsp.processar.error.0254", new Object[] {codi, getError254(msg, locale)}, locale) + detall;
					break;
				case "0255":
					error = messageSource.getMessage("consulta.scsp.processar.error.0255", new Object[] {codi}, locale) + detall;
					break;
				case "0256":
					error = messageSource.getMessage("consulta.scsp.processar.error.0256", new Object[] {codi}, locale) + detall;
					break;
				case "0257":
					error = messageSource.getMessage("consulta.scsp.processar.error.0257", new Object[] {codi}, locale) + detall;
					break;
				case "0258":
					error = messageSource.getMessage("consulta.scsp.processar.error.0258", new Object[] {codi}, locale) + detall;
					break;
				case "0259":
					error = messageSource.getMessage("consulta.scsp.processar.error.0259", new Object[] {codi}, locale) + detall;
					break;
				case "0301":
					error = messageSource.getMessage("consulta.scsp.processar.error.0301", new Object[] {codi}, locale) + detall;
					break;
				case "0302":
					error = messageSource.getMessage("consulta.scsp.processar.error.0302", new Object[] {codi}, locale) + detall;
					break;
				case "0303":
					error = messageSource.getMessage("consulta.scsp.processar.error.0303", new Object[] {codi}, locale) + detall;
					break;
				case "0304":
					error = messageSource.getMessage("consulta.scsp.processar.error.0304", new Object[] {codi}, locale) + detall;
					break;
				case "0305":
					error = messageSource.getMessage("consulta.scsp.processar.error.0305", new Object[] {codi}, locale) + detall;
					break;
				case "0306":
					error = messageSource.getMessage("consulta.scsp.processar.error.0306", new Object[] {codi}, locale) + detall;
					break;
				case "0307":
					error = messageSource.getMessage("consulta.scsp.processar.error.0307", new Object[] {codi}, locale) + detall;
					break;
				case "0308":
					error = messageSource.getMessage("consulta.scsp.processar.error.0308", new Object[] {codi}, locale) + detall;
					break;
				case "0309":
					error = messageSource.getMessage("consulta.scsp.processar.error.0309", new Object[] {codi}, locale) + detall;
					break;
				case "0310":
					error = messageSource.getMessage("consulta.scsp.processar.error.0310", new Object[] {codi}, locale) + detall;
					break;
				case "0311":
					error = messageSource.getMessage("consulta.scsp.processar.error.0311", new Object[] {codi}, locale) + detall;
					break;
				case "0312":
					error = messageSource.getMessage("consulta.scsp.processar.error.0312", new Object[] {codi}, locale) + detall;
					break;
				case "0313":
					error = messageSource.getMessage("consulta.scsp.processar.error.0313", new Object[] {codi}, locale) + detall;
					break;
				case "0314":
					error = messageSource.getMessage("consulta.scsp.processar.error.0314", new Object[] {codi}, locale) + detall;
					break;
				case "0315":
					error = messageSource.getMessage("consulta.scsp.processar.error.0315", new Object[] {codi}, locale) + detall;
					break;
				case "0316":
					error = messageSource.getMessage("consulta.scsp.processar.error.0316", new Object[] {codi}, locale) + detall;
					break;
				case "0317":
					error = messageSource.getMessage("consulta.scsp.processar.error.0317", new Object[] {codi}, locale) + detall;
					break;
				case "0318":
					error = messageSource.getMessage("consulta.scsp.processar.error.0318", new Object[] {codi}, locale) + detall;
					break;
				case "0319":
					error = messageSource.getMessage("consulta.scsp.processar.error.0319", new Object[] {codi}, locale) + detall;
					break;
				case "0320":
					error = messageSource.getMessage("consulta.scsp.processar.error.0320", new Object[] {codi}, locale) + detall;
					break;
				case "0321":
					error = messageSource.getMessage("consulta.scsp.processar.error.0321", new Object[] {codi}, locale) + detall;
					break;
				case "0322":
					error = messageSource.getMessage("consulta.scsp.processar.error.0322", new Object[] {codi}, locale) + detall;
					break;
				case "0323":
					error = messageSource.getMessage("consulta.scsp.processar.error.0323", new Object[] {codi}, locale) + detall;
					break;
				case "0324":
					error = messageSource.getMessage("consulta.scsp.processar.error.0324", new Object[] {codi}, locale) + detall;
					break;
				case "0325":
					error = messageSource.getMessage("consulta.scsp.processar.error.0325", new Object[] {codi}, locale) + detall;
					break;
				case "0326":
					error = messageSource.getMessage("consulta.scsp.processar.error.0326", new Object[] {codi}, locale) + detall;
					break;
				case "0327":
					error = messageSource.getMessage("consulta.scsp.processar.error.0327", new Object[] {codi}, locale) + detall;
					break;
				case "0328":
					error = messageSource.getMessage("consulta.scsp.processar.error.0328", new Object[] {codi}, locale) + detall;
					break;
				case "0329":
					error = messageSource.getMessage("consulta.scsp.processar.error.0329", new Object[] {codi}, locale) + detall;
					break;
				case "0330":
					error = messageSource.getMessage("consulta.scsp.processar.error.0330", new Object[] {codi}, locale) + detall;
					break;
				case "0331":
					error = messageSource.getMessage("consulta.scsp.processar.error.0331", new Object[] {codi}, locale) + detall;
					break;
				case "0332":
					error = messageSource.getMessage("consulta.scsp.processar.error.0332", new Object[] {codi}, locale) + detall;
					break;
				case "0350":
					error = messageSource.getMessage("consulta.scsp.processar.error.0350", new Object[] {codi}, locale) + detall;
					break;
				case "0401":
					error = messageSource.getMessage("consulta.scsp.processar.error.0401", new Object[] {codi, getError401(msg, locale)}, locale) + detall;
					break;
				case "0402":
					error = messageSource.getMessage("consulta.scsp.processar.error.0402", new Object[] {codi, getError402(msg, locale)}, locale) + detall;
					break;
				case "0403":
					error = messageSource.getMessage("consulta.scsp.processar.error.0403", new Object[] {codi}, locale) + detall;
					break;
				case "0404":
					error = messageSource.getMessage("consulta.scsp.processar.error.0404", new Object[] {codi}, locale) + detall;
					break;
				case "0405":
					error = messageSource.getMessage("consulta.scsp.processar.error.0405", new Object[] {codi}, locale) + detall;
					break;
				case "0406":
					error = messageSource.getMessage("consulta.scsp.processar.error.0406", new Object[] {codi}, locale) + detall;
					break;
//				case "0407":
//				case "0408":
//				case "0409":
//				case "0410":
//				case "0418":
//					error = "[" + codi + "] " + msg;
//					break;
				case "0411":
					error = messageSource.getMessage("consulta.scsp.processar.error.0411", new Object[] {codi}, locale) + detall;
					break;
				case "0412":
					error = messageSource.getMessage("consulta.scsp.processar.error.0412", new Object[] {codi}, locale) + detall;
					break;
				case "0413":
					error = messageSource.getMessage("consulta.scsp.processar.error.0413", new Object[] {codi}, locale) + detall;
					break;
				case "0414":
					error = messageSource.getMessage("consulta.scsp.processar.error.0414", new Object[] {codi}, locale) + detall;
					break;
				case "0415":
					error = messageSource.getMessage("consulta.scsp.processar.error.0415", new Object[] {codi}, locale) + detall;
					break;
				case "0416":
					// TODO: posar el nombre màxim de sol·licitus per a cada servei
					error = messageSource.getMessage("consulta.scsp.processar.error.0416", new Object[] {codi}, locale) + detall;
					break;
				case "0417":
					error = messageSource.getMessage("consulta.scsp.processar.error.0417", new Object[] {codi}, locale) + detall;
					break;
				case "0419":
					error = messageSource.getMessage("consulta.scsp.processar.error.0419", new Object[] {codi}, locale) + detall;
					break;
				case "0501":
					error = messageSource.getMessage("consulta.scsp.processar.error.0501", new Object[] {codi}, locale) + detall;
					break;
				case "0502":
					error = messageSource.getMessage("consulta.scsp.processar.error.0502", new Object[] {codi}, locale) + detall;
					break;
				case "0503":
					error = messageSource.getMessage("consulta.scsp.processar.error.0503", new Object[] {codi}, locale) + detall;
					break;
				case "0504":
					error = messageSource.getMessage("consulta.scsp.processar.error.0504", new Object[] {codi}, locale) + detall;
					break;
//				case "0505":
//				case "0506":
//				case "0507":
//				case "0508":
//				case "0509":
//				case "0510":
//				case "0511":
//					error = "[" + codi + "] " + msg;
//					break;
				case "0512":
					error = messageSource.getMessage("consulta.scsp.processar.error.0512", new Object[] {codi}, locale) + detall;
					break;
				case "0513":
					error = messageSource.getMessage("consulta.scsp.processar.error.0513", new Object[] {codi}, locale) + detall;
					break;
				case "0514":
					error = messageSource.getMessage("consulta.scsp.processar.error.0514", new Object[] {codi}, locale) + detall;
					break;
				case "0901":
					error = messageSource.getMessage("consulta.scsp.processar.error.0901", new Object[] {codi}, locale) + detall;
					break;
				case "0902":
					error = messageSource.getMessage("consulta.scsp.processar.error.0902", new Object[] {codi}, locale) + detall;
					break;
				case "0903":
					error = messageSource.getMessage("consulta.scsp.processar.error.0903", new Object[] {codi}, locale) + detall;
					break;
				case "0904":
					error = messageSource.getMessage("consulta.scsp.processar.error.0904", new Object[] {codi}, locale) + detall;
					break;
				default:
					error = "[" + codi + "] " + msg;
					break;
			}

		} else if (throwable.getCause() != null && throwable.getCause() instanceof SOAPFaultException) {
			SOAPFaultException soapFaultException = (SOAPFaultException) throwable.getCause();
			error = "[" + soapFaultException.getFault().getFaultCode() + "]" + soapFaultException.getFault().getFaultString();
		} else {
			error = throwable.getMessage();
		}
		return error;
	}

	private String getError402(String message, Locale locale) {
		if (message == null) return messageSource.getMessage("consulta.scsp.processar.suberror.buid", null, locale);
		else if (message.startsWith("Falta informar campo obligatorio")) return message.length() > 32 ? message.substring(32) : messageSource.getMessage("consulta.scsp.processar.suberror.sense.informacio", null, locale);
		else return message;
	}

	private String getError401(String message, Locale locale) {
		if (message == null) return messageSource.getMessage("consulta.scsp.processar.suberror.buid", null, locale);
		else if (message.contains("cvc-")) return message.substring(message.indexOf(":"));
		else return message;
	}

	private String getError227(String message, Locale locale) {
		if (message == null) return messageSource.getMessage("consulta.scsp.processar.suberror.buid", null, locale);
		else if (message.contains("Connection refused")) return messageSource.getMessage("consulta.scsp.processar.suberror.0227.connection.refused", null, locale);
		else if (message.contains("text/html") && message.contains("j_security")) return messageSource.getMessage("consulta.scsp.processar.suberror.0227.autenticacio", null, locale);
		else if (message.contains("HTTP") &&  message.contains("404")) return messageSource.getMessage("consulta.scsp.processar.suberror.0227.404", null, locale);
		else if (message.contains("HTTP") &&  message.contains("502")) return messageSource.getMessage("consulta.scsp.processar.suberror.0227.502", null, locale);
		else if (message.contains("HTTP") &&  message.contains("503")) return messageSource.getMessage("consulta.scsp.processar.suberror.0227.503", null, locale);
		else if (message.contains("HTTP") &&  message.contains("500")) return messageSource.getMessage("consulta.scsp.processar.suberror.0227.500", null, locale);
		else if (message.contains("Host unreachable")) return messageSource.getMessage("consulta.scsp.processar.suberror.0227.host.unreachable", null, locale);
		else if (message.contains("[0254]")) return messageSource.getMessage("consulta.scsp.processar.suberror.0227.0254", null, locale);
		else if (message.contains("\"code\":233")) return messageSource.getMessage("consulta.scsp.processar.suberror.0227.0233", null, locale);
		else if (message.startsWith("El servidor ha devuelto un mensaje SOAP Fault. Error al generar la respuesta. BackofficeException:")) return message.length() > 98 ? message.substring(98) : messageSource.getMessage("consulta.scsp.processar.suberror.sense.informacio", null, locale);
		else if (message.startsWith("El servidor ha devuelto un mensaje SOAP Fault.")) return message.length() > 46 ? message.substring(46) : messageSource.getMessage("consulta.scsp.processar.suberror.sense.informacio", null, locale);
		else return message;
	}

	private String getError242(String message, Locale locale) {
		if (message == null) return messageSource.getMessage("consulta.scsp.processar.suberror.buid", null, locale);
		else if (message.contains("La estructura del fichero recibido no corresponde con el esquema")) return messageSource.getMessage("consulta.scsp.processar.suberror.0242.estructura", null, locale);
		else if (message.contains("Organismo no autorizado")) return messageSource.getMessage("consulta.scsp.processar.suberror.0242.organ.permis", null, locale);
		else if (message.contains("Tiempo de espera superado")) return messageSource.getMessage("consulta.scsp.processar.suberror.0242.timeout", null, locale);
		else if (message.startsWith("El servidor ha devuelto un mensaje SOAP Fault. Error Genérico devuelto por el BackOffice")) return message.length() > 88 ? message.substring(88) : messageSource.getMessage("consulta.scsp.processar.suberror.sense.informacio", null, locale);
		else if (message.startsWith("El servidor ha devuelto un mensaje SOAP Fault.")) return message.length() > 46 ? message.substring(46) : messageSource.getMessage("consulta.scsp.processar.suberror.sense.informacio", null, locale);
		else return message;

	}

	private String getError252(String message, Locale locale) {
		if (message == null) return messageSource.getMessage("consulta.scsp.processar.suberror.buid", null, locale);
		else if (message.startsWith("El servidor ha devuelto un mensaje SOAP Fault.")) return message.length() > 46 ? message.substring(46) : messageSource.getMessage("consulta.scsp.processar.suberror.sense.informacio", null, locale);
		else return message;
	}

	private String getError254(String message, Locale locale) {
		if (message == null) return messageSource.getMessage("consulta.scsp.processar.suberror.buid", null, locale);
		else if (message.startsWith("El servidor ha devuelto un mensaje SOAP Fault. No se ha aportado la información mínima necesaria para tramitar la petición")) return message.length() > 123 ? message.substring(123) : messageSource.getMessage("consulta.scsp.processar.suberror.sense.informacio", null, locale);
		else return message;
	}

}
