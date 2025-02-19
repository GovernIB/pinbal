/**
 * 
 */
package es.caib.pinbal.core.helper;

import es.caib.pinbal.core.dto.ConsultaDto;
import es.caib.pinbal.core.dto.ConsultaDto.Consentiment;
import es.caib.pinbal.core.dto.ConsultaDto.DocumentTipus;
import es.caib.pinbal.core.dto.JustificantDto;
import es.caib.pinbal.core.dto.RecobrimentSolicitudDto;
import es.caib.pinbal.core.dto.RespostaAtributsDto;
import es.caib.pinbal.core.model.Consulta;
import es.caib.pinbal.core.model.HistoricConsulta;
import es.caib.pinbal.core.repository.ConsultaRepository;
import es.caib.pinbal.core.repository.HistoricConsultaRepository;
import es.caib.pinbal.core.service.ConsultaService;
import es.caib.pinbal.core.service.HistoricConsultaService;
import es.caib.pinbal.core.service.exception.ConsultaNotFoundException;
import es.caib.pinbal.core.service.exception.ConsultaScspException;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.JustificantGeneracioException;
import es.caib.pinbal.core.service.exception.ProcedimentNotFoundException;
import es.caib.pinbal.core.service.exception.ProcedimentServeiNotFoundException;
import es.caib.pinbal.core.service.exception.ServeiNotAllowedException;
import es.caib.pinbal.scsp.ScspHelper;
import es.scsp.bean.common.Atributos;
import es.scsp.bean.common.ConfirmacionPeticion;
import es.scsp.bean.common.DatosGenericos;
import es.scsp.bean.common.Estado;
import es.scsp.bean.common.Peticion;
import es.scsp.bean.common.Respuesta;
import es.scsp.bean.common.Solicitante;
import es.scsp.bean.common.SolicitudTransmision;
import es.scsp.bean.common.Titular;
import es.scsp.bean.common.Transmision;
import es.scsp.bean.common.TransmisionDatos;
import es.scsp.common.exceptions.ScspException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Mètodes comuns per a les consultes al servei de recobriment fetes
 * des del serveis web SOAP i REST.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Component
public class RecobrimentHelper implements ApplicationContextAware, MessageSourceAware {

	public static final String ERROR_CODE_SCSP_VALIDATION = "0226";

	@Autowired
	private ConsultaRepository consultaRepository;
	@Autowired
	private HistoricConsultaRepository historicConsultaRepository;
	@Autowired
	private ConsultaService consultaService;
	@Autowired
	private HistoricConsultaService historicConsultaService;
	@Autowired
	private ConfigHelper configHelper;

	private ApplicationContext applicationContext;
	private MessageSource messageSource;
	private ScspHelper scspHelper;

//    @Autowired
//    private ServeiConfigRepository serveiConfigRepository;
//    @Autowired
//    private PluginHelper pluginHelper;
//    @Autowired
//    private ProcedimentRepository procedimentRepository;
//    @Autowired
//    private EntitatRepository entitatRepository;
//    @Autowired
//    private ServeiCampRepository serveiCampRepository;
//
//	private XmlHelper xmlHelper;

	public Respuesta peticionSincrona(Peticion peticion) throws ScspException {
		String codigoCertificado = peticion.getAtributos().getCodigoCertificado();
		log.debug("Processant petició síncrona al servei web del recobriment (" +
				"codCertificado=" + codigoCertificado + ")");
		List<RecobrimentSolicitudDto> solicituds = validarIObtenirSolicituds(
				peticion,
				1);
		try {
			ConsultaDto consulta = novaConsultaRecobriment(
					codigoCertificado,
					solicituds.get(0));
			if (consulta.isEstatError()) {
				log.debug("Petició SCSP realitzada amb error (" +
						"peticionId=" + consulta.getScspPeticionId() + ", " +
						"error=" + consulta.getError() + ", " +
						"estadoCodigo=" + consulta.getRespostaEstadoCodigo() + ", " +
						"estadoError=" + consulta.getRespostaEstadoError() + ")");
				if (consulta.getRespostaEstadoCodigo() != null) {
					throw getErrorValidacio(
							consulta.getRespostaEstadoCodigo(),
							consulta.getRespostaEstadoError());
				} else {
					throw getErrorValidacio(
							"0227",
							consulta.getError());
				}
			} else {
				log.debug("Petició SCSP realitzada correctament (" +
						"peticionId=" + consulta.getScspPeticionId() + ")");
			}
			log.debug("Recuperant resposta SCSP per retornar al client (" +
					"peticionId=" + consulta.getScspPeticionId() + ")");
			return recuperarRespuestaScsp(consulta.getScspPeticionId());
		} catch (EntitatNotFoundException ex) {
			throw getErrorValidacio(
					"0227",
					"No s'ha trobat l'entitat especificada");
		} catch (ProcedimentNotFoundException ex) {
			throw getErrorValidacio(
					"0227",
					"No s'ha trobat el procediment especificat");
		} catch (ProcedimentServeiNotFoundException ex) {
			throw getErrorValidacio(
					"0227",
					"No s'ha trobat el procediment-servei especificat");
		} catch (ConsultaNotFoundException ex) {
			throw getErrorValidacio(
					"0227",
					"No s'ha trobat la consulta una vegada creada");
		} catch (ServeiNotAllowedException ex) {
			throw getErrorValidacio(
					"0227",
					"L'usuari no te permisos per a realitzar aquesta consulta");
		} catch (ConsultaScspException ex) {
			throw getErrorValidacio("0227", ex.getMessage());
		} catch (ScspException ex) {
			throw ex;
		} catch (Exception ex) {
			log.error("Error en la consulta SCSP", ex);
			throw getErrorValidacio(
					"0227",
					"Error en la consulta SCSP: " + ExceptionUtils.getStackTrace(ex));
		}
	}

	public ConfirmacionPeticion peticionAsincrona(
			Peticion peticion) throws ScspException {
		log.debug("Processant petició asíncrona al servei web del recobriment");
		List<RecobrimentSolicitudDto> solicituds = validarIObtenirSolicituds(
				peticion,
				-1);
		String codigoCertificado = peticion.getAtributos().getCodigoCertificado();
		try {
			ConsultaDto consulta = consultaService.novaConsultaRecobrimentMultiple(
					codigoCertificado,
					solicituds);
			if (consulta.getError() != null) {
				throw getErrorValidacio(
						consulta.getRespostaEstadoCodigo(),
						consulta.getRespostaEstadoError());
			}
			RespostaAtributsDto atributs = consulta.getRespostaAtributs();
			ConfirmacionPeticion confirmacionPeticion = new ConfirmacionPeticion();
			Atributos atributos = new Atributos();
			atributos.setCodigoCertificado(codigoCertificado);
			Estado estado = new Estado();
			estado.setCodigoEstado(atributs.getEstatCodi());
			estado.setCodigoEstadoSecundario(atributs.getEstatCodiSecundari());
			estado.setLiteralError(atributs.getEstatErrorLiteral());
			estado.setTiempoEstimadoRespuesta(atributs.getEstatTempsEstimatResposta());
			atributos.setEstado(estado);
			atributos.setIdPeticion(atributs.getPeticioId());
			atributos.setNumElementos(atributs.getNumElements());
			atributos.setTimeStamp(atributs.getTimestamp());
			confirmacionPeticion.setAtributos(atributos);
			return confirmacionPeticion;
		} catch (EntitatNotFoundException ex) {
			throw getErrorValidacio(
					"0227",
					"No s'ha trobat l'entitat especificada");
		} catch (ProcedimentNotFoundException ex) {
			throw getErrorValidacio(
					"0227",
					"No s'ha trobat el procediment especificat");
		} catch (ProcedimentServeiNotFoundException ex) {
			throw getErrorValidacio(
					"0227",
					"No s'ha trobat el procediment-servei especificat");
		} catch (ServeiNotAllowedException ex) {
			throw getErrorValidacio(
					"0227",
					"L'usuari no te permisos per a realitzar aquesta consulta");
		} catch (ScspException ex) {
			throw ex;
		} catch (Exception ex) {
			log.error("Error en la consulta SCSP", ex);
			throw getErrorValidacio(
					"0227",
					"Error en la consulta SCSP: " + ExceptionUtils.getStackTrace(ex));
		}
	}

	public Respuesta getRespuesta(
			String idpeticion) throws ScspException {
		try {
			return recuperarRespuestaScsp(idpeticion);
		} catch (Exception ex) {
			log.error("Error en la consulta SCSP", ex);
			throw getErrorValidacio(
					"0227",
					"Error en la consulta SCSP: " + ExceptionUtils.getStackTrace(ex));
		}
	}

	public JustificantDto getJustificante(
			String idpeticion,
			String idsolicitud,
			boolean versioImprimible,
			boolean ambContingut) throws ScspException {
		try {
			Consulta consulta = consultaRepository.findByScspPeticionIdAndScspSolicitudId(idpeticion, idsolicitud);
			HistoricConsulta historicConsulta = null;
			if (consulta == null) {
				historicConsulta = historicConsultaRepository.findByScspPeticionIdAndScspSolicitudId(idpeticion, idsolicitud);
			}
			if (consulta == null && historicConsulta == null) {
				log.error("No s'ha trobat la consulta (idpeticion=" + idpeticion + ", idsolicitud=" + idsolicitud + ")");
				throw new ConsultaNotFoundException();
			}
			JustificantDto justificant;
			if (consulta != null) {
				justificant = consultaService.obtenirJustificant(
						idpeticion,
						idsolicitud,
						versioImprimible,
						ambContingut);
			} else {
				justificant = historicConsultaService.obtenirJustificant(
						idpeticion,
						idsolicitud,
						versioImprimible,
						ambContingut);
			}
			if (!justificant.isError()) {
				return justificant;
			} else {
				throw getErrorValidacio(
						"0227",
						justificant.getErrorDescripcio());
			}
		} catch (ConsultaNotFoundException ex) {
			throw getErrorValidacio(
					"0227",
					"No s'ha trobat la sol·licitud: " + ex.getMessage());
		} catch (JustificantGeneracioException ex) {
			log.error("Error en la generació del justificant", ex);
			throw getErrorValidacio(
					"0227",
					"Error en la generació del justificant: " + ExceptionUtils.getStackTrace(ex));
		}
	}

	@Override
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}



	@SuppressWarnings("incomplete-switch")
	public List<RecobrimentSolicitudDto> validarIObtenirSolicituds(
			Peticion peticion,
			int maxSolicituds) throws ScspException {
		// Validació dels camps principals de la petició
		if (peticion == null)
			throw getErrorValidacio(ERROR_CODE_SCSP_VALIDATION, "No s'ha trobat l'element peticion");
		if (peticion.getAtributos() == null)
			throw getErrorValidacio(ERROR_CODE_SCSP_VALIDATION, "No s'ha trobat l'element peticion.atributos");
		if (peticion.getAtributos().getCodigoCertificado() == null)
			throw getErrorValidacio(ERROR_CODE_SCSP_VALIDATION, "No s'ha trobat l'element peticion.atributos.codigoCertificado");
		if (peticion.getSolicitudes() == null)
			throw getErrorValidacio(ERROR_CODE_SCSP_VALIDATION, "No s'ha trobat l'element peticion.solicitudes");
		if (peticion.getSolicitudes().getSolicitudTransmision() == null)
			throw getErrorValidacio(ERROR_CODE_SCSP_VALIDATION, "No s'ha trobat cap element peticion.solicitudes.solicitudTransmision");

		// Validació del nombre de solicituds
		if (maxSolicituds != -1) {
			int numSolicitudes = peticion.getSolicitudes().getSolicitudTransmision().size();
			if (numSolicitudes > maxSolicituds)
				throw getErrorValidacio(ERROR_CODE_SCSP_VALIDATION, "S'ha excedit el màxim nombre de sol·licituds permeses en la petició (màxim=" + maxSolicituds + ")");
		}
		List<RecobrimentSolicitudDto> solicituds = new ArrayList<RecobrimentSolicitudDto>();
		int index = 0;

		// Validació de les transmissions
		for (SolicitudTransmision st: peticion.getSolicitudes().getSolicitudTransmision()) {
			RecobrimentSolicitudDto solicitud = new RecobrimentSolicitudDto();
			DatosGenericos datosGenericos = st.getDatosGenericos();
			// Validació de les dades genèriques
			if (datosGenericos == null)
				throw getErrorValidacio(ERROR_CODE_SCSP_VALIDATION, "No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos (solicitudIndex=" + index + ")");

			// Validació de la transmissió
			if (maxSolicituds != 1) {
				Transmision transmision = datosGenericos.getTransmision();
				if (transmision == null)
					throw getErrorValidacio(ERROR_CODE_SCSP_VALIDATION, "No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos.transmision (solicitudIndex=" + index + ")");
				// Validació de l'ID de solicitud
				if (transmision.getIdSolicitud() == null)
					throw getErrorValidacio(ERROR_CODE_SCSP_VALIDATION, "No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos.transmision.idSolicitud (solicitudIndex=" + index + ")");
				if (transmision.getIdSolicitud().trim().isEmpty())
					throw getErrorValidacio(ERROR_CODE_SCSP_VALIDATION, "L'element peticion.solicitudes.solicitudTransmision.datosGenericos.transmision.idSolicitud (solicitudIndex=" + index + ") no pot ser buit");
				if (transmision.getIdSolicitud().length() > 64)
					throw getErrorValidacio(ERROR_CODE_SCSP_VALIDATION, "Camp massa llarg. L'element peticion.solicitudes.solicitudTransmision.datosGenericos.transmision.idSolicitud (solicitudIndex=" + index + ") no pot superar els 64 caràcters");
				solicitud.setId(transmision.getIdSolicitud());
			} else {
				solicitud.setId("000001");
			}

			// Validació de les dades del solicitant
			if (datosGenericos.getSolicitante() == null)
				throw getErrorValidacio(ERROR_CODE_SCSP_VALIDATION, "No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante (solicitudIndex=" + index + ")");
			Solicitante solicitante = datosGenericos.getSolicitante();
			if (solicitante.getIdentificadorSolicitante() == null)
				throw getErrorValidacio(ERROR_CODE_SCSP_VALIDATION, "No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.identificadorSolicitante (solicitudIndex=" + index + ")");
			solicitud.setEntitatCif(solicitante.getIdentificadorSolicitante());

			// Validació de la finalitat
			if (solicitante.getFinalidad() == null)
				throw getErrorValidacio(ERROR_CODE_SCSP_VALIDATION, "No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.finalidad (solicitudIndex=" + index + ")");
			if (solicitante.getFinalidad().trim().isEmpty())
				throw getErrorValidacio(ERROR_CODE_SCSP_VALIDATION, "L'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.finalidad (solicitudIndex=" + index + ") no pot ser buit");
			if (solicitante.getFinalidad().length() > 250)
				throw getErrorValidacio(ERROR_CODE_SCSP_VALIDATION, "Camp massa llarg. L'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.finalidad (solicitudIndex=" + index + ") no pot superar els 250 caràcters");
			solicitud.setFinalitat(solicitante.getFinalidad());

			// Validació del consentiment
			if (solicitante.getConsentimiento() == null)
				throw getErrorValidacio(ERROR_CODE_SCSP_VALIDATION, "No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.consentimiento (solicitudIndex=" + index + ")");
			switch (solicitante.getConsentimiento()) {
			case Si:
				solicitud.setConsentiment(Consentiment.Si);
				break;
			case Ley:
				solicitud.setConsentiment(Consentiment.Llei);
				break;
			default:
				throw getErrorValidacio(ERROR_CODE_SCSP_VALIDATION, "Valor incorrecte. Els valors possibles de l'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.consentimiento (solicitudIndex=" + index + ") son: [Si | Llei]");
			}

			// Validació del departament (unitat tramitadora)
			if (solicitante.getUnidadTramitadora() == null)
				throw getErrorValidacio(ERROR_CODE_SCSP_VALIDATION, "No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.unidadTramitadora (solicitudIndex=" + index + ")");
			if (solicitante.getUnidadTramitadora().trim().isEmpty())
				throw getErrorValidacio(ERROR_CODE_SCSP_VALIDATION, "L'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.unidadTramitadora (solicitudIndex=" + index + ") no pot ser buit");
			if (solicitante.getUnidadTramitadora().length() > 250)
				throw getErrorValidacio(ERROR_CODE_SCSP_VALIDATION, "Camp massa llarg. L'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.unidadTramitadora (solicitudIndex=" + index + ") no pot superar els 250 caràcters");
			solicitud.setDepartamentNom(solicitante.getUnidadTramitadora());

			if (solicitante.getCodigoUnidadTramitadora() != null) {
				if (solicitante.getCodigoUnidadTramitadora().length() > 9)
					throw getErrorValidacio(ERROR_CODE_SCSP_VALIDATION, "Camp massa llarg. L'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.codigoUnidadTramitadora (solicitudIndex=" + index + ") no pot superar els 9 caràcters");
//				if (!Pattern.matches("([AUIJO][0-9]|CC|G[EA]|E([AC]|[0-9])|L(A|[0-9]))[0-9]{7}", solicitante.getCodigoUnidadTramitadora()))
//					throw getErrorValidacio(ERROR_CODE_SCSP_VALIDATION, "L'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.codigoUnidadTramitadora (solicitudIndex=" + index + ") no té el format correcte");
				solicitud.setUnitatTramitadoraCodi(solicitante.getCodigoUnidadTramitadora());
			}

			// Validació de l'identificador d'expedient
			if (solicitante.getIdExpediente() != null && solicitante.getIdExpediente().length() > 25)
				throw getErrorValidacio(ERROR_CODE_SCSP_VALIDATION, "Camp massa llarg. L'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.idExpediente (solicitudIndex=" + index + ") no pot superar els 25 caràcters");
			solicitud.setExpedientId(solicitante.getIdExpediente());

			// Validació del procediment
			if (solicitante.getProcedimiento() == null)
				throw getErrorValidacio(ERROR_CODE_SCSP_VALIDATION, "No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.procedimiento (solicitudIndex=" + index + ")");
			if (solicitante.getProcedimiento().getCodProcedimiento() == null)
				throw getErrorValidacio(ERROR_CODE_SCSP_VALIDATION, "No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.procedimiento.codProcedimiento (solicitudIndex=" + index + ")");
			if (solicitante.getProcedimiento().getCodProcedimiento().trim().isEmpty())
				throw getErrorValidacio(ERROR_CODE_SCSP_VALIDATION, "L'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.procedimiento.codProcedimiento (solicitudIndex=" + index + ") no pot ser buit");
			solicitud.setProcedimentCodi(solicitante.getProcedimiento().getCodProcedimiento());

			// Validació de les dades del funcionari
			if (solicitante.getFuncionario() == null)
				throw getErrorValidacio(ERROR_CODE_SCSP_VALIDATION, "No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.funcionario (solicitudIndex=" + index + ")");
			// Validació del Nif del funcionari
			if (solicitante.getFuncionario().getNifFuncionario() == null)
				throw getErrorValidacio(ERROR_CODE_SCSP_VALIDATION, "No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.funcionario.nifFuncionario (solicitudIndex=" + index + ")");
			if (solicitante.getFuncionario().getNifFuncionario().trim().isEmpty())
				throw getErrorValidacio(ERROR_CODE_SCSP_VALIDATION, "L'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.funcionario.nifFuncionario (solicitudIndex=" + index + ") no pot ser buit)");
			if (solicitante.getFuncionario().getNifFuncionario().length() > 10)
				throw getErrorValidacio(ERROR_CODE_SCSP_VALIDATION, "Camp massa llarg. L'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.funcionario.nifFuncionario (solicitudIndex=" + index + ") no pot superar els 10 caràcters");
			solicitud.setFuncionariNif(solicitante.getFuncionario().getNifFuncionario());
			// Validació del nom complet del funcionari
			if (solicitante.getFuncionario().getNombreCompletoFuncionario() == null)
				throw getErrorValidacio(ERROR_CODE_SCSP_VALIDATION, "No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.funcionario.nombreCompletoFuncionario (solicitudIndex=" + index + ")");
			if (solicitante.getFuncionario().getNombreCompletoFuncionario().trim().isEmpty())
				throw getErrorValidacio(ERROR_CODE_SCSP_VALIDATION, "L'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.funcionario.nombreCompletoFuncionario (solicitudIndex=" + index + ") no pot ser buit");
			if (solicitante.getFuncionario().getNombreCompletoFuncionario().length() > 122)
				throw getErrorValidacio(ERROR_CODE_SCSP_VALIDATION, "Camp massa llarg. L'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.funcionario.nombreCompletoFuncionario (solicitudIndex=" + index + ") no pot superar els 122 caràcters");
			solicitud.setFuncionariNom(solicitante.getFuncionario().getNombreCompletoFuncionario());

			// Validar les dades del titular
			if (datosGenericos.getTitular() != null) {
				Titular titular = datosGenericos.getTitular();
				// Validar documentació
				if (titular.getDocumentacion() != null) {
					// Validar el número de document
					if (titular.getDocumentacion().length() > 14)
						throw getErrorValidacio(ERROR_CODE_SCSP_VALIDATION, "Camp massa llarg. L'element peticion.solicitudes.solicitudTransmision.datosGenericos.titular.documentacion (solicitudIndex=" + index + ") no pot superar els 14 caràcters");
					solicitud.setTitularDocumentNum(titular.getDocumentacion());
					// Validar el tipus de document
					if (titular.getTipoDocumentacion() == null)
						throw getErrorValidacio(ERROR_CODE_SCSP_VALIDATION, "No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos.titular.tipoDocumentacion (solicitudIndex=" + index + ")");
					switch (titular.getTipoDocumentacion()) {
					case CIF:
						solicitud.setTitularDocumentTipus(DocumentTipus.CIF);
						break;
					case DNI:
						solicitud.setTitularDocumentTipus(DocumentTipus.DNI);
						break;
					case NIE:
						solicitud.setTitularDocumentTipus(DocumentTipus.NIE);
						break;
					case NIF:
						solicitud.setTitularDocumentTipus(DocumentTipus.NIF);
						break;
					case Pasaporte:
						solicitud.setTitularDocumentTipus(DocumentTipus.Passaport);
						break;
					case NumeroIdentificacion:
						solicitud.setTitularDocumentTipus(DocumentTipus.NombreIdentificacio);
						break;
					case Otros:
						solicitud.setTitularDocumentTipus(DocumentTipus.Altres);
						break;
					default:
						throw getErrorValidacio(ERROR_CODE_SCSP_VALIDATION, "Valor incorrecte. Els valors possibles de l'element peticion.solicitudes.solicitudTransmision.datosGenericos.titular.tipoDocumentacion (solicitudIndex=" + index + ") son: [CIF | DNI | NIF | NIE | Pasaporte | NumeroIdentificacion | Otros]");
					}
				}

				// Validar el nom del titular
				if (titular.getNombre() != null && titular.getNombre().length() > 40)
					throw getErrorValidacio(ERROR_CODE_SCSP_VALIDATION, "Camp massa llarg. L'element peticion.solicitudes.solicitudTransmision.datosGenericos.titular.nombre (solicitudIndex=" + index + ") no pot superar els 40 caràcters");
				solicitud.setTitularNom(titular.getNombre());

				// Validar el primer llinatge del titular
				if (titular.getApellido1() != null && titular.getApellido1().length() > 40)
					throw getErrorValidacio(ERROR_CODE_SCSP_VALIDATION, "Camp massa llarg. L'element peticion.solicitudes.solicitudTransmision.datosGenericos.titular.apellido1 (solicitudIndex=" + index + ") no pot superar els 40 caràcters");
				solicitud.setTitularLlinatge1(titular.getApellido1());

				// Validar el segon llinatge del titular
				if (titular.getApellido2() != null && titular.getApellido2().length() > 40)
					throw getErrorValidacio(ERROR_CODE_SCSP_VALIDATION, "Camp massa llarg. L'element peticion.solicitudes.solicitudTransmision.datosGenericos.titular.apellido2 (solicitudIndex=" + index + ") no pot superar els 40 caràcters");
				solicitud.setTitularLlinatge2(titular.getApellido2());

				// Validar el nom complet del titular
				if (titular.getNombreCompleto() != null && titular.getNombreCompleto().length() > 122)
					throw getErrorValidacio(ERROR_CODE_SCSP_VALIDATION, "Camp massa llarg. L'element peticion.solicitudes.solicitudTransmision.datosGenericos.titular.nombreCompleto (solicitudIndex=" + index + ") no pot superar els 122 caràcters");
				solicitud.setTitularNomComplet(titular.getNombreCompleto());
			}
			Object datosEspecificos = st.getDatosEspecificos();
			if (datosEspecificos != null && !(datosEspecificos instanceof Element)) {
				throw getErrorValidacio(ERROR_CODE_SCSP_VALIDATION, "L'element peticion.solicitudes.solicitudTransmision.datosEspecificos (solicitudIndex=" + index + ") no és del tipus org.w3c.dom.Element");
			}
			solicitud.setDadesEspecifiques((Element)st.getDatosEspecificos());
			solicituds.add(solicitud);
			index++;
		}
		return solicituds;
	}

	private ConsultaDto novaConsultaRecobriment(
			String serveiCodi,
			RecobrimentSolicitudDto solicitud) throws EntitatNotFoundException, ProcedimentNotFoundException, ProcedimentServeiNotFoundException, ConsultaNotFoundException, ServeiNotAllowedException, ConsultaScspException {
		if (consultaService.isOptimitzarTransaccionsNovaConsulta()) {
			log.info("Iniciant petició SCSP amb optimització fase INIT (serveiCodi=" + serveiCodi + ")");
			long t0 = System.currentTimeMillis();
			ConsultaDto consultaInit = consultaService.novaConsultaRecobrimentInit(
					serveiCodi,
					solicitud);
			log.debug("\tpetició SCSP amb optimització fase INIT creada (" +
					"serveiCodi=" + serveiCodi + ", " +
					"scspId=" + consultaInit.getScspPeticionSolicitudId() + ", " +
					"temps=" + (System.currentTimeMillis() - t0) + "ms)");
			log.info("Petició SCSP amb optimització fase ENVIAMENT (" +
					"serveiCodi=" + serveiCodi + ", " +
					"scspId=" + consultaInit.getScspPeticionSolicitudId() + ")");
			t0 = System.currentTimeMillis();
			consultaService.novaConsultaRecobrimentEnviament(
					consultaInit.getId(),
					solicitud);
			log.debug("\tpetició SCSP amb optimització fase ENVIAMENT resposta (" +
					"serveiCodi=" + serveiCodi + ", " +
					"scspId=" + consultaInit.getScspPeticionSolicitudId() + ", " +
					"temps=" + (System.currentTimeMillis() - t0) + "ms)");
			log.info("Petició SCSP amb optimització fase ESTAT (" +
					"serveiCodi=" + serveiCodi + ", " +
					"scspId=" + consultaInit.getScspPeticionSolicitudId() + ")");
			t0 = System.currentTimeMillis();
			ConsultaDto resposta = consultaService.novaConsultaEstat(
					consultaInit.getId());
			log.debug("\tpetició SCSP amb optimització fase ESTAT resposta (" +
					"serveiCodi=" + serveiCodi + ", " +
					"scspId=" + consultaInit.getScspPeticionSolicitudId() + ", " +
					"temps=" + (System.currentTimeMillis() - t0) + "ms)");
			return resposta;
		} else {
			log.info("Nova petició SCSP sense optimització (" +
					"serveiCodi=" + serveiCodi + ")");
			ConsultaDto resposta = consultaService.novaConsultaRecobriment(
					serveiCodi,
					solicitud);
			return resposta;
		}
	}

	private Respuesta recuperarRespuestaScsp(
			String peticionId) throws TransformerException, ParserConfigurationException, SAXException, IOException {
		Respuesta respuesta = getScspHelper().recuperarRespuestaScsp(peticionId);
		boolean processar = getPropertyDatosEspecificosProcessar();
		if (processar) {
			processarDatosEspecificos(respuesta);
		}
		return respuesta;
	}

	private ScspException getErrorValidacio(
			String codi,
			String missatge) {
		return new ScspException(missatge, codi);
	}

	private ScspHelper getScspHelper() {
		if (scspHelper == null) {
			scspHelper = new ScspHelper(
					applicationContext,
					messageSource);
		}
		return scspHelper;
	}

	private static final String XMLNS_DATOS_ESPECIFICOS_V2 = "http://www.map.es/scsp/esquemas/datosespecificos";
	private static final String XMLNS_DATOS_ESPECIFICOS_V3 = "http://intermediacion.redsara.es/scsp/esquemas/datosespecificos";
	private void processarDatosEspecificos(
			Respuesta respuesta) throws TransformerException, ParserConfigurationException, SAXException, IOException {
		if (respuesta != null && respuesta.getTransmisiones() != null) {
			List<TransmisionDatos> transmisiones = respuesta.getTransmisiones().getTransmisionDatos();
			for (TransmisionDatos transmisionDatos: transmisiones) {
				Object datespObj = transmisionDatos.getDatosEspecificos();
				if (datespObj instanceof Element) {
					Element datosEspecificos = (Element)datespObj;
					String datosEspecificosStr = elementToString(datosEspecificos);
					String datosEspecificosSenseNs = removeXmlStringNamespaceAndPreamble(datosEspecificosStr);
					String xmlns;
					if (datosEspecificosStr.contains(XMLNS_DATOS_ESPECIFICOS_V2)) {
						xmlns = "xmlns=\"" + XMLNS_DATOS_ESPECIFICOS_V2 + "\"";
					} else if (datosEspecificosStr.contains(XMLNS_DATOS_ESPECIFICOS_V3)) {
						xmlns = "xmlns=\"" + XMLNS_DATOS_ESPECIFICOS_V3 + "\"";
					} else {
						xmlns = "";
					}
					boolean incloureNs = getPropertyDatosEspecificosIncloureNs();
					String datosEspecificosProcessat =  datosEspecificosSenseNs.substring(0, "<DatosEspecificos ".length()) +
							(incloureNs ? xmlns : "") + 
							datosEspecificosSenseNs.substring("<DatosEspecificos ".length());
					transmisionDatos.setDatosEspecificos(
							stringToElement(datosEspecificosProcessat));
				}
			}
		}
	}
	private String removeXmlStringNamespaceAndPreamble(String xml) {
		return xml.replaceAll("(<\\?[^<]*\\?>)?", "").
				replaceAll("xmlns.*?(\"|\').*?(\"|\')", "").
				replaceAll("(<)(\\w+:)(.*?>)", "$1$3").
				replaceAll("(</)(\\w+:)(.*?>)", "$1$3");
	}
	private Element stringToElement(String xml) throws TransformerException, ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		dbFactory.setNamespaceAware(true);
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		StringReader reader = new StringReader(xml);
		InputSource inputSource = new InputSource(reader);
		Document doc = dBuilder.parse(inputSource);
		return doc.getDocumentElement();
	}
	private String elementToString(Element element) throws TransformerException {
		TransformerFactory transFactory = TransformerFactory.newInstance();
		Transformer transformer = transFactory.newTransformer();
		StringWriter buffer = new StringWriter();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.transform(
				new DOMSource(element),
				new StreamResult(buffer));
		return buffer.toString();
	}

	private boolean getPropertyDatosEspecificosProcessar() {
		return configHelper.getAsBoolean("es.caib.pinbal.recobriment.datos.especificos.processar", false);
	}
	private boolean getPropertyDatosEspecificosIncloureNs() {
		return configHelper.getAsBoolean("es.caib.pinbal.recobriment.datos.especificos.incloure.ns", false);
	}


//	// V2
//	// /////////////////////////////////////////////////////////////
//
//	public void validateDadesComunes(DadesComunes dadesComunes, BindException errors) {
//		if (dadesComunes == null) {
//			errors.rejectValue("dadesComunes", "rec.val.err.dadesComunes", "No s'ha trobat l'element dadesComunes");
//			return;
//		}
//
//		// Validem cada camp de "dadesComunes"
//		validateCamp(dadesComunes.getServeiCodi(), "serveiCodi", errors);
//		ServeiConfig serveiConfig = serveiConfigRepository.findByServei(dadesComunes.getServeiCodi());
//		if (serveiConfig == null) {
//			errors.rejectValue("dadesComunes.serveiCodi", "rec.val.err.serveiCodi.notfound", "No s'ha trobat el servei amb codi " + dadesComunes.getServeiCodi());
//		}
//
//		validateCamp(dadesComunes.getEntitatCif(), "entitatCif", errors);
//		Entitat entitat = entitatRepository.findByCif(dadesComunes.getEntitatCif());
//		if (entitat == null) {
//			errors.rejectValue("dadesComunes.entitatCif", "rec.val.err.entitatCif.notfound", "No s'ha trobat l'entitat amb el CIF " + dadesComunes.getEntitatCif());
//		}
//
//		validateCamp(dadesComunes.getProcedimentCodi(), "procedimentCodi", errors);
//		es.caib.pinbal.core.model.Procediment procediment = procedimentRepository.findByCodi(dadesComunes.getProcedimentCodi());
//		if (procediment == null) {
//			errors.rejectValue("dadesComunes.procedimentCodi", "rec.val.err.procedimentCodi.notfound", "No s'ha trobat el procediment amb el codi " + dadesComunes.getProcedimentCodi());
//		}
//
//		// Velidam que l'entitat del procediment és l'enitat amb el cif informat a la petició
//		if (procediment != null && entitat != null && !procediment.getEntitat().equals(entitat)) {
//			errors.rejectValue("dadesComunes.procedimentCodi", "rec.val.procedimentCodi.entitat.differs", "L'entitat del procediment no té el CIF indicant al camp dadesComunes.entitatCif");
//		}
//
//		validateFuncionari(dadesComunes.getFuncionari(), errors);
//
//		validateCamp(dadesComunes.getDepartament(), "departament", 250, errors);
//		validateCamp(dadesComunes.getFinalitat(), "finalitat", 250, errors);
//
//		if (dadesComunes.getConsentiment() == null) {
//			errors.rejectValue("dadesComunes.consentiment", "rec.val.err.consentiment", "No s'ha trobat l'element dadesComunes.consentiment");
//		}
//	}
//
//	// Validació genèrica de camps: nul·litat i mida
//	private void validateCamp(String valor, String campNom, int maxLongitud, BindException errors) {
//		valor = valor != null ? valor.trim() : null;
//		if (valor == null || valor.isEmpty()) {
//			errors.rejectValue("dadesComunes." + campNom, "rec.val.err." + campNom, "No s'ha trobat l'element dadesComunes." + campNom);
//		} else if (valor.length() > maxLongitud) {
//			errors.rejectValue("dadesComunes." + campNom, "rec.val.err." + campNom + ".length", "Camp massa llarg. L'element dadesComunes." + campNom + " no pot superar els " + maxLongitud + " caràcters.");
//		}
//	}
//
//	// Validació genèrica de camps: nul·litat
//	private void validateCamp(String valor, String campNom, BindException errors) {
//		valor = valor != null ? valor.trim() : null;
//		if (valor == null || valor.isEmpty()) {
//			errors.rejectValue("dadesComunes." + campNom, "rec.val.err." + campNom, "No s'ha trobat l'element dadesComunes." + campNom);
//		}
//	}
//
//	// Validar la longitud d'un camp
//	private void validateCampLength(String valor, String campNom, int maxLongitud, BindException errors) {
//		if (valor != null && valor.length() > maxLongitud) {
//			errors.rejectValue(campNom, "rec.val.err." + campNom + ".length", "Camp massa llarg. L'element " + campNom + " no pot superar els " + maxLongitud + " caràcters.");
//		}
//	}
//
//	// Validació del funcionari (personalitzat segons regles complexes)
//	private void validateFuncionari(Funcionari funcionari, BindException errors) {
//		if (funcionari == null) {
//			errors.rejectValue("dadesComunes.funcionari", "rec.val.err.funcionari", "No s'ha trobat l'element dadesComunes.funcionari");
//			return;
//		}
//
//		// És obligatori informar o bé el codi o el nif.
//		// No fa falta informar els dos, però si s'informen els dos es validarà que el codi i el nif corresponguin al mateix usuari.
//		// Tampoc fa falta informar el nom del funcionari. Però si s'informa es validarà que el nom corresponguin al de l'usuari.
//		String funcionariCodi = funcionari.getCodi() != null ? funcionari.getCodi().trim() : null;
//		String funcionariNif = funcionari.getNif() != null ? funcionari.getNif().trim() : null;
//		String funcionariNom = funcionari.getNom() != null ? funcionari.getNom().trim() : null;
//
//		// Validació bàsica: codi o nif han d'estar informats
//		if ((funcionariCodi == null || funcionariCodi.isEmpty()) && (funcionariNif == null || funcionariNif.isEmpty())) {
//			errors.rejectValue("dadesComunes.funcionari", "rec.val.err.funcionari.missing", "És obligatori informar o bé el codi o el nif del funcionari.");
//			return;
//		}
//
//		DadesUsuari funcionariDades = consultarFuncionari(funcionariCodi, funcionariNif, errors);
//
//		// Validem longitud per a codi i NIF
//		validateCampLength(funcionariCodi, "dadesComunes.funcionari.codi", 16, errors);
//		validateCampLength(funcionariNif, "dadesComunes.funcionari.nif", 10, errors);
//
//		if (funcionariDades != null) {
//			if (funcionariNif != null && !funcionariNif.equals(funcionariDades.getNif())) {
//				errors.rejectValue("dadesComunes.funcionari.nif", "rec.val.err.funcionari.nif.mismatch", "El NIF del funcionari no coincideix amb el NIF del funcionari amb codi " + funcionariCodi);
//			}
//			if (funcionariNom != null && !funcionariNom.equalsIgnoreCase(funcionariDades.getNom())) {
//				errors.rejectValue("dadesComunes.funcionari.nom", "rec.val.err.funcionari.nom.mismatch", "El nom indicat no coincideix amb el nom del funcionari amb codi " + funcionariDades.getCodi());
//			}
//		}
//
//		validateCampLength(funcionariNom, "dadesComunes.funcionari.nom", 122, errors);
//	}
//
//	// Mètode que consulta el funcionari a partir del codi o nif indicat
//	private DadesUsuari consultarFuncionari(String codi, String nif, BindException errors) {
//		try {
//			if (codi != null && !codi.isEmpty()) {
//				DadesUsuari funcionari = pluginHelper.dadesUsuariConsultarAmbUsuariCodi(codi);
//				if (funcionari == null) {
//					errors.rejectValue("dadesComunes.funcionari.codi", "rec.val.err.funcionari.codi.notfound", "No s'ha trobat el funcionari amb el codi " + codi);
//				}
//				return funcionari;
//			} else if (nif != null && !nif.isEmpty()) {
//				DadesUsuari funcionari = pluginHelper.dadesUsuariConsultarAmbUsuariNif(nif);
//				if (funcionari == null) {
//					errors.rejectValue("dadesComunes.funcionari.nif", "rec.val.err.funcionari.nif.notfound", "No s'ha trobat el funcionari amb el nif " + nif);
//				}
//				return funcionari;
//			}
//		} catch (SistemaExternException e) {
//			String identificador = codi != null ? codi : nif;
//			log.error("No s'han pogut obtenir les dades del funcionari amb identificador: " + identificador, e);
//		}
//		return null;
//	}
//
//	public void validateDadesSolicitud(SolicitudSimple solicitud, String serveiCodi, BindException errors, ServeiService serveiService) {
//		if (solicitud == null) {
//			errors.rejectValue("solicitud", "rec.val.err.solicitud", "No s'ha trobat l'element solicitud");
//			return;
//		}
//
//		String solicitudId = solicitud.getId() != null ? solicitud.getId().trim() : null;
//		es.caib.pinbal.client.recobriment.v2.Titular titular = solicitud.getTitular();
//		String expedientId = solicitud.getExpedient() != null ? solicitud.getExpedient().trim() : null;
//		Map<String, String> dadesEspedifiques = solicitud.getDadesEspecifiques();
//
//		validateCampLength(solicitudId, "solicitud.solicitudId", 64, errors);
//
//		validateTitular(titular, serveiCodi, errors);
//
//		validateCampLength(expedientId, "solicitud.expedient", 25, errors);
//
//		validateDadesEspecifiques(dadesEspedifiques, serveiCodi, errors, serveiService);
//	}
//
//	private void validateTitular(es.caib.pinbal.client.recobriment.v2.Titular titular, String serveiCodi, BindException errors) {
//		ServeiConfig servei = serveiConfigRepository.findByServei(serveiCodi);
//		if (titular == null) {
//			if (servei.isDocumentObligatori())
//				errors.rejectValue("solicitud.titular", "rec.val.err.titular", "No s'ha trobat l'element solicitud.titular");
//			return;
//		}
//
//		es.caib.pinbal.client.recobriment.v2.Titular.DocumentTipus titularDocTipus = titular.getDocumentTipus();
//		String titularDocNum = titular.getDocumentNumero() != null ? titular.getDocumentNumero().trim() : null;
//		String titularNom = titular.getNom() != null ? titular.getNom().trim() : null;
//		String titularLlinatge1 = titular.getLlinatge1() != null ? titular.getLlinatge1().trim() : null;
//		String titularLlinatge2 = titular.getLlinatge2() != null ? titular.getLlinatge2().trim() : null;
//		String titularNomComplet = titular.getNomComplet() != null ? titular.getNomComplet().trim() : null;
//
//		if (servei.isDocumentObligatori() || (titularDocNum != null && !titularDocNum.isEmpty())) {
//			if (titularDocTipus == null) {
//				errors.rejectValue("dadesComunes.titular.documentTipus", "rec.val.err.titular.documentTipus", "No s'ha trobat l'element dadesComunes.titular.documentTipus");
//			}
//		}
//		if (servei.isDocumentObligatori())
//			validateCamp(titular.getDocumentNumero(), "solicitud.titular.documentNumero", errors);
//
//		validateCampLength(titularDocNum, "solicitud.titular.documentNumero", 14, errors);
//
//		if (titularDocTipus != null && titularDocNum != null && !titularDocNum.isEmpty() && servei.isComprovarDocument()) {
//			if (es.caib.pinbal.client.recobriment.v2.Titular.DocumentTipus.NIF.equals(titularDocTipus)) {
//				if (!DocumentIdentitatHelper.validacioNif(titularDocNum))
//					errors.rejectValue("dadesComunes.titular.documentNumero", "rec.val.err.titular.nif", "El valor de l'element dadesComunes.titular.documentTipus no és un NIF vàlid");
//			} if (es.caib.pinbal.client.recobriment.v2.Titular.DocumentTipus.DNI.equals(titularDocTipus)) {
//				if (!DocumentIdentitatHelper.validacioDni(titularDocNum))
//					errors.rejectValue("dadesComunes.titular.documentNumero", "rec.val.err.titular.dni", "El valor de l'element dadesComunes.titular.documentTipus no és un DNI vàlid");
//			} if (es.caib.pinbal.client.recobriment.v2.Titular.DocumentTipus.CIF.equals(titularDocTipus)) {
//				if (!DocumentIdentitatHelper.validacioCif(titularDocNum))
//					errors.rejectValue("dadesComunes.titular.documentNumero", "rec.val.err.titular.cif", "El valor de l'element dadesComunes.titular.documentTipus no és un CIF vàlid");
//			} if (es.caib.pinbal.client.recobriment.v2.Titular.DocumentTipus.NIE.equals(titularDocTipus)) {
//				if (!DocumentIdentitatHelper.validacioNie(titularDocNum))
//					errors.rejectValue("dadesComunes.titular.documentNumero", "rec.val.err.titular.nie", "El valor de l'element dadesComunes.titular.documentTipus no és un NIE vàlid");
//			} if (es.caib.pinbal.client.recobriment.v2.Titular.DocumentTipus.Pasaporte.equals(titularDocTipus)) {
//				if (!DocumentIdentitatHelper.validacioPass(titularDocNum))
//					errors.rejectValue("dadesComunes.titular.documentNumero", "rec.val.err.titular.pass", "El valor de l'element dadesComunes.titular.documentTipus no és un Passaport vàlid");
//			}
//
//		}
//
//		validateCampLength(titularNom, "solicitud.titular.nom", 40, errors);
//		validateCampLength(titularLlinatge1, "solicitud.titular.llinatge1", 40, errors);
//		validateCampLength(titularLlinatge2, "solicitud.titular.llinatge2", 40, errors);
//		validateCampLength(titularNomComplet, "solicitud.titular.nomComplet", 122, errors);
//
//	}
//
//	private void validateDadesEspecifiques(Map<String, String> dadesEspedifiques, String serveiCodi, BindException errors, ServeiService serveiService) {
//
//		Map<String, Object> dades = new HashMap<>();
//
//		DadesConsultaSimpleValidator validatorDadesEspecifiques = new DadesConsultaSimpleValidator(serveiService, serveiCodi);
//		validatorDadesEspecifiques.validate(dadesEspedifiques, errors);
//	}
//
//	public Map<String, List<String>> validateDadesSolicituds(List<SolicitudSimple> solicituds, String serveiCodi, ServeiService serveiService) {
//
//		Map<String, List<String>> errorsSolicituds = new HashMap<>();
//
//		Servicio servicio = getScspHelper().getServicio(serveiCodi);
//		int maxSolicituds = servicio.getMaxSolicitudesPeticion();
//		int numPeticions = solicituds != null ? solicituds.size() : 0;
//
//		if (numPeticions == 0) {
//			log.error("Consulta asíncrona via recobriment sense sol·licituds", "La consulta ha de tenir al manco una sol·licitud");
//			errorsSolicituds.put("GLOBAL", Collections.singletonList("La consulta excedeix el màxim de sol·licituds permeses pel servei"));
//		}
//
//		if (maxSolicituds > 0 && numPeticions > maxSolicituds) {
//			log.error("Error al processar dades de la petició múltiple via recobriment", "La consulta excedeix el màxim de sol·licituds permeses pel servei");
//			errorsSolicituds.put("GLOBAL", Collections.singletonList("La consulta excedeix el màxim de sol·licituds permeses pel servei"));
//		}
//
//		int index = 1;
//		for (SolicitudSimple solicitud : solicituds) {
//			BindException errorsSolicitud = new BindException(solicitud, "solicitud " + index);
//			validateDadesSolicitud(solicitud, serveiCodi, errorsSolicitud, serveiService);
//			Map<String, List<String>> errorsCamps = new HashMap<>();
//			String solIndex = String.format("%06d", index);
//
//			for (FieldError fieldError : errorsSolicitud.getFieldErrors()) {
//				String campNom = solIndex + ":" + fieldError.getField(); // Nom del camp
//				String campErrorMsg = fieldError.getDefaultMessage(); // Missatge d'error
//
//				// Si el camp no existeix al mapa, inicialitzar una nova llista
//				if (!errorsCamps.containsKey(campNom)) {
//					errorsCamps.put(campNom, new ArrayList<String>());
//				}
//
//				// Afegir el missatge d'error a la llista del camp
//				errorsCamps.get(campNom).add(campErrorMsg);
//			}
//			errorsSolicituds.putAll(errorsCamps);
//		}
//
//		return errorsSolicituds;
//	}
//
//	public Peticion toPeticion(PeticioSincrona peticio) throws ConsultaScspGeneracioException {
//		if (peticio == null)
//			return null;
//
//		String timeStamp = DateUtils.parseISO8601(new Date());
//		DadesComunes dadesComunes = peticio.getDadesComunes();
//		SolicitudSimple solicitud = peticio.getSolicitud();
//
//		String serveiCodi = dadesComunes.getServeiCodi();
//		ServeiConfig serveiConfig = serveiConfigRepository.findByServei(serveiCodi);
//
//		String idPeticion = getScspHelper().generarIdPeticion(serveiCodi);
//		String idSolicitud = getIdSolicitud(idPeticion, 1, 1);
//
//		// Crear objecte Peticion
//		Peticion peticion = new Peticion();
//		peticion.setAtributos(crearAtributos(idPeticion, timeStamp, serveiCodi, 1));
//
//		// Crear Solicitudes i SolicitudTransmision
//		Solicitudes solicitudes = new Solicitudes();
//		SolicitudTransmision solicitudTransmision = new SolicitudTransmision();
//		solicitudTransmision.setId(solicitud.getId());
//		solicitudTransmision.setDatosGenericos(crearDatosGenericos(dadesComunes, solicitud, serveiConfig, serveiCodi, idPeticion, idSolicitud, timeStamp));
//
//		// Dades específiques (si n'hi ha)
//		if (solicitud.getDadesEspecifiques() != null && !solicitud.getDadesEspecifiques().isEmpty()) {
//			Servicio servicio = getScspHelper().getServicio(serveiCodi);
//			List<String> pathInicialitzablesByServei = serveiCampRepository.findPathInicialitzablesByServei(serveiCodi);
//			solicitudTransmision.setDatosEspecificos(processarDadesEspecifiques(solicitud.getDadesEspecifiques(), serveiCodi, serveiConfig, servicio, pathInicialitzablesByServei));
//		}
//
//		// Afegir SolicitudTransmision a Solicitudes
//		ArrayList<SolicitudTransmision> solicitudesTransmision = new ArrayList<>();
//		solicitudesTransmision.add(solicitudTransmision);
//		solicitudes.setSolicitudTransmision(solicitudesTransmision);
//		peticion.setSolicitudes(solicitudes);
//
//		return peticion;
//
//	}
//
//	public Peticion toPeticion(PeticioAsincrona peticio) throws ConsultaScspGeneracioException {
//		if (peticio == null)
//			return null;
//
//		String timeStamp = DateUtils.parseISO8601(new Date());
//		DadesComunes dadesComunes = peticio.getDadesComunes();
//		List<SolicitudSimple> solicituds = peticio.getSolicituds();
//
//		String serveiCodi = dadesComunes.getServeiCodi();
//		ServeiConfig serveiConfig = serveiConfigRepository.findByServei(serveiCodi);
//		Servicio servicio = getScspHelper().getServicio(serveiCodi);
//		List<String> pathInicialitzablesByServei = serveiCampRepository.findPathInicialitzablesByServei(serveiCodi);
//
//		String idPeticion = getScspHelper().generarIdPeticion(serveiCodi);
//		int numSolicituds = solicituds.size();
//		int index = 1;
//
//
//		// Crear objecte Peticion
//		Peticion peticion = new Peticion();
//		peticion.setAtributos(crearAtributos(idPeticion, timeStamp, serveiCodi, numSolicituds));
//
//		// Crear Solicitudes i SolicitudTransmision
//		Solicitudes solicitudes = new Solicitudes();
//		ArrayList<SolicitudTransmision> solicitudesTransmision = new ArrayList<>();
//
//		for (SolicitudSimple solicitud : solicituds) {
//			String idSolicitud = getIdSolicitud(idPeticion, numSolicituds, index++);
//
//			SolicitudTransmision solicitudTransmision = new SolicitudTransmision();
//			solicitudTransmision.setId(solicitud.getId());
//			solicitudTransmision.setDatosGenericos(crearDatosGenericos(dadesComunes, solicitud, serveiConfig, serveiCodi, idPeticion, idSolicitud, timeStamp));
//
//			// Dades específiques (si n'hi ha)
//			if (solicitud.getDadesEspecifiques() != null && !solicitud.getDadesEspecifiques().isEmpty()) {
//				solicitudTransmision.setDatosEspecificos(processarDadesEspecifiques(solicitud.getDadesEspecifiques(), serveiCodi, serveiConfig, servicio, pathInicialitzablesByServei));
//			}
//
//			// Afegir SolicitudTransmision a Solicitudes
//			solicitudesTransmision.add(solicitudTransmision);
//		}
//		solicitudes.setSolicitudTransmision(solicitudesTransmision);
//		peticion.setSolicitudes(solicitudes);
//
//		return peticion;
//
//	}
//
//	private static Atributos crearAtributos(String idPeticion, String timeStamp, String serveiCodi, int numSolicituds) {
//		Atributos atributos = new Atributos();
//		atributos.setIdPeticion(idPeticion);
//		atributos.setNumElementos(String.valueOf(numSolicituds));
//		atributos.setTimeStamp(timeStamp);
//		atributos.setCodigoCertificado(serveiCodi);
//		return atributos;
//	}
//
//	// Crear DatosGenericos
//	private DatosGenericos crearDatosGenericos(DadesComunes dadesComunes, SolicitudSimple solicitud, ServeiConfig serveiConfig, String serveiCodi, String idPeticion, String idSolicitud, String timeStamp) {
//		DatosGenericos datosGenericos = new DatosGenericos();
//
//		// Configurar Emisor
//		datosGenericos.setEmisor(getScspHelper().getEmisor(serveiCodi));
//
//		// Configurar Solicitant
//		datosGenericos.setSolicitante(crearSolicitante(dadesComunes, solicitud, serveiConfig));
//
//		// Configurar Titular (si existeix)
//		if (solicitud.getTitular() != null) {
//			datosGenericos.setTitular(crearTitular(solicitud.getTitular()));
//		}
//
//		// Configurar Transmissió
//		Transmision transmision = new Transmision();
//		transmision.setCodigoCertificado(serveiCodi);
//		transmision.setIdSolicitud(idSolicitud);
//		transmision.setFechaGeneracion(timeStamp);
//		datosGenericos.setTransmision(transmision);
//
//		return datosGenericos;
//	}
//
//	// Crear Solicitante
//	private Solicitante crearSolicitante(DadesComunes dadesComunes, SolicitudSimple solicitud, ServeiConfig serveiConfig) {
//		Solicitante solicitante = new Solicitante();
//
//		// Procediment
//		es.caib.pinbal.core.model.Procediment procediment = procedimentRepository.findByCodi(dadesComunes.getProcedimentCodi());
//		Procedimiento procedimiento = new Procedimiento();
//		procedimiento.setCodProcedimiento(procediment.getCodi());
//		procedimiento.setNombreProcedimiento(procediment.getNom());
//		solicitante.setProcedimiento(procedimiento);
//
//		// Funcionari
//		solicitante.setFuncionario(crearFuncionario(dadesComunes.getFuncionari()));
//
//		// Unitat tramitadora
//		solicitante.setUnidadTramitadora(dadesComunes.getDepartament());
//		solicitante.setCodigoUnidadTramitadora(determinarUnitatTramitadora(serveiConfig, procediment));
//
//		// Solicitant
//		Entitat entitat = entitatRepository.findByCif(dadesComunes.getEntitatCif());
//		solicitante.setIdentificadorSolicitante(entitat.getCif());
//		solicitante.setNombreSolicitante(entitat.getNom());
//		solicitante.setIdExpediente(solicitud.getExpedient());
//		solicitante.setFinalidad(dadesComunes.getFinalitat());
//		solicitante.setConsentimiento(Consentimiento.valueOf(dadesComunes.getConsentiment().name()));
//
//		return solicitante;
//	}
//
//	private Funcionario crearFuncionario(Funcionari funcionari) {
//		if (funcionari == null) {
//			return null;
//		}
//
//		String funcionariCodi = funcionari.getCodi() != null ? funcionari.getCodi().trim() : null;
//		String funcionariNif = funcionari.getNif() != null ? funcionari.getNif().trim() : null;
//
//		DadesUsuari funcionariDades = null;
//		try {
//			funcionariDades = funcionariCodi != null && !funcionariCodi.isEmpty()
//					? pluginHelper.dadesUsuariConsultarAmbUsuariCodi(funcionariCodi)
//					: pluginHelper.dadesUsuariConsultarAmbUsuariNif(funcionariNif);
//		} catch (SistemaExternException e) {
//			// Gestionar errors, si escau
//		}
//
//		Funcionario funcionario = new Funcionario();
//		funcionario.setNombreCompletoFuncionario(funcionariDades != null ? funcionariDades.getNom() : funcionari.getNom());
//		funcionario.setNifFuncionario(funcionariDades != null ? funcionariDades.getNif() : funcionariNif);
//		return funcionario;
//	}
//
//	private String determinarUnitatTramitadora(ServeiConfig serveiConfig, es.caib.pinbal.core.model.Procediment procediment) {
//		return serveiConfig.isPinbalUnitatDir3FromEntitat()
//				? procediment.getEntitat().getUnitatArrel()
//				: serveiConfig.getPinbalUnitatDir3() != null && !serveiConfig.getPinbalUnitatDir3().isEmpty()
//				? serveiConfig.getPinbalUnitatDir3()
//				: procediment.getOrganGestor() != null
//				? procediment.getOrganGestor().getCodi()
//				: null;
//	}
//
//	private Titular crearTitular(es. caib. pinbal. client. recobriment. v2.Titular titularModel) {
//		Titular titular = new Titular();
//		if (titularModel.getDocumentTipus() != null) {
//			titular.setTipoDocumentacion(TipoDocumentacion.valueOf(titularModel.getDocumentTipus().name()));
//		}
//		titular.setDocumentacion(titularModel.getDocumentNumero());
//		titular.setNombreCompleto(titularModel.getNomComplet());
//		titular.setNombre(titularModel.getNom());
//		titular.setApellido1(titularModel.getLlinatge1());
//		titular.setApellido2(titularModel.getLlinatge2());
//		return titular;
//	}
//
//	private String getIdSolicitud(
//			String idPeticion,
//			int numSolicitudes,
//			int index) {
//		if (numSolicitudes == 1) {
//			return idPeticion;
//		} else {
//			return String.format("%06d", index);
//		}
//	}
//
//	// Processar dades específiques
//	private Element processarDadesEspecifiques(Map<String, String> dadesEspecifiques,
//											   String serveiCodi,
//											   ServeiConfig serveiConfig,
//											   Servicio servicio,
//											   List<String> pathInicialitzablesByServei) throws ConsultaScspGeneracioException {
//		Map<String, Object> dadesEspecifiquesConverted = new HashMap<>();
//		for (Map.Entry<String, String> entry : dadesEspecifiques.entrySet()) {
//			dadesEspecifiquesConverted.put(entry.getKey(), entry.getValue());
//		}
//		return getXmlHelper().crearDadesEspecifiques(
//				servicio,
//				dadesEspecifiquesConverted,
//				serveiConfig.isActivaGestioXsd(),
//				serveiConfig.isIniDadesEspecifiques(),
//				pathInicialitzablesByServei,
//				serveiConfig.isAddDadesEspecifiques()
//		);
//	}
//
//	private XmlHelper getXmlHelper() {
//		if (xmlHelper == null) {
//			xmlHelper = new XmlHelper();
//		}
//		return xmlHelper;
//	}

}
