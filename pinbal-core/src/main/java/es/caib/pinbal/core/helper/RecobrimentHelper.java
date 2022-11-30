/**
 * 
 */
package es.caib.pinbal.core.helper;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import es.caib.pinbal.core.model.Consulta;
import es.caib.pinbal.core.model.HistoricConsulta;
import es.caib.pinbal.core.repository.ConsultaRepository;
import es.caib.pinbal.core.repository.HistoricConsultaRepository;
import es.caib.pinbal.core.service.HistoricConsultaService;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import es.caib.pinbal.core.dto.ConsultaDto;
import es.caib.pinbal.core.dto.ConsultaDto.Consentiment;
import es.caib.pinbal.core.dto.ConsultaDto.DocumentTipus;
import es.caib.pinbal.core.dto.JustificantDto;
import es.caib.pinbal.core.dto.RecobrimentSolicitudDto;
import es.caib.pinbal.core.dto.RespostaAtributsDto;
import es.caib.pinbal.core.service.ConsultaService;
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

/**
 * Mètodes comuns per a les consultes al servei de recobriment fetes
 * des del serveis web SOAP i REST.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
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

	public Respuesta peticionSincrona(Peticion peticion) throws ScspException {
		String codigoCertificado = peticion.getAtributos().getCodigoCertificado();
		LOGGER.debug("Processant petició síncrona al servei web del recobriment (" +
				"codCertificado=" + codigoCertificado + ")");
		List<RecobrimentSolicitudDto> solicituds = validarIObtenirSolicituds(
				peticion,
				1);
		try {
			ConsultaDto consulta = novaConsultaRecobriment(
					codigoCertificado,
					solicituds.get(0));
			if (consulta.isEstatError()) {
				LOGGER.debug("Petició SCSP realitzada amb error (" +
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
				LOGGER.debug("Petició SCSP realitzada correctament (" +
						"peticionId=" + consulta.getScspPeticionId() + ")");
			}
			LOGGER.debug("Recuperant resposta SCSP per retornar al client (" +
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
			LOGGER.error("Error en la consulta SCSP", ex);
			throw getErrorValidacio(
					"0227",
					"Error en la consulta SCSP: " + ExceptionUtils.getStackTrace(ex));
		}
	}

	public ConfirmacionPeticion peticionAsincrona(
			Peticion peticion) throws ScspException {
		LOGGER.debug("Processant petició asíncrona al servei web del recobriment");
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
			LOGGER.error("Error en la consulta SCSP", ex);
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
			LOGGER.error("Error en la consulta SCSP", ex);
			throw getErrorValidacio(
					"0227",
					"Error en la consulta SCSP: " + ExceptionUtils.getStackTrace(ex));
		}
	}

	public JustificantDto getJustificante(
			String idpeticion,
			String idsolicitud) throws ScspException {
		try {
			Consulta consulta = consultaRepository.findByScspPeticionIdAndScspSolicitudId(idpeticion, idsolicitud);
			HistoricConsulta historicConsulta = null;
			if (consulta == null) {
				historicConsulta = historicConsultaRepository.findByScspPeticionIdAndScspSolicitudId(idpeticion, idsolicitud);
			}
			if (consulta == null && historicConsulta == null) {
				LOGGER.error("No s'ha trobat la consulta (idpeticion=" + idpeticion + ", idsolicitud=" + idsolicitud + ")");
				throw new ConsultaNotFoundException();
			}
			JustificantDto justificant;
			if (consulta != null) {
				justificant = consultaService.obtenirJustificant(
						idpeticion,
						idsolicitud);
			} else {
				justificant = historicConsultaService.obtenirJustificant(
						idpeticion,
						idsolicitud);
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
			LOGGER.error("Error en la generació del justificant", ex);
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
			if (solicitante.getUnidadTramitadora().length() > 64)
				throw getErrorValidacio(ERROR_CODE_SCSP_VALIDATION, "Camp massa llarg. L'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.unidadTramitadora (solicitudIndex=" + index + ") no pot superar els 64 caràcters");
			solicitud.setDepartamentNom(solicitante.getUnidadTramitadora());

			if (solicitante.getCodigoUnidadTramitadora() != null) {
//				if (solicitante.getCodigoUnidadTramitadora().length() > 9)
//					throw getErrorValidacio(ERROR_CODE_SCSP_VALIDATION, "Camp massa llarg. L'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.codigoUnidadTramitadora (solicitudIndex=" + index + ") no pot superar els 9 caràcters");
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
			LOGGER.info("Iniciant petició SCSP amb optimització fase INIT (serveiCodi=" + serveiCodi + ")");
			long t0 = System.currentTimeMillis();
			ConsultaDto consultaInit = consultaService.novaConsultaRecobrimentInit(
					serveiCodi,
					solicitud);
			LOGGER.debug("\tpetició SCSP amb optimització fase INIT creada (" +
					"serveiCodi=" + serveiCodi + ", " +
					"scspId=" + consultaInit.getScspPeticionSolicitudId() + ", " +
					"temps=" + (System.currentTimeMillis() - t0) + "ms)");
			LOGGER.info("Petició SCSP amb optimització fase ENVIAMENT (" +
					"serveiCodi=" + serveiCodi + ", " +
					"scspId=" + consultaInit.getScspPeticionSolicitudId() + ")");
			t0 = System.currentTimeMillis();
			consultaService.novaConsultaRecobrimentEnviament(
					consultaInit.getId(),
					solicitud);
			LOGGER.debug("\tpetició SCSP amb optimització fase ENVIAMENT resposta (" +
					"serveiCodi=" + serveiCodi + ", " +
					"scspId=" + consultaInit.getScspPeticionSolicitudId() + ", " +
					"temps=" + (System.currentTimeMillis() - t0) + "ms)");
			LOGGER.info("Petició SCSP amb optimització fase ESTAT (" +
					"serveiCodi=" + serveiCodi + ", " +
					"scspId=" + consultaInit.getScspPeticionSolicitudId() + ")");
			t0 = System.currentTimeMillis();
			ConsultaDto resposta = consultaService.novaConsultaEstat(
					consultaInit.getId());
			LOGGER.debug("\tpetició SCSP amb optimització fase ESTAT resposta (" +
					"serveiCodi=" + serveiCodi + ", " +
					"scspId=" + consultaInit.getScspPeticionSolicitudId() + ", " +
					"temps=" + (System.currentTimeMillis() - t0) + "ms)");
			return resposta;
		} else {
			LOGGER.info("Nova petició SCSP sense optimització (" +
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

	private static final Logger LOGGER = LoggerFactory.getLogger(RecobrimentHelper.class);

}
