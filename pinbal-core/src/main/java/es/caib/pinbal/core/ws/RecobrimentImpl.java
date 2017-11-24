/**
 * 
 */
package es.caib.pinbal.core.ws;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import es.caib.pinbal.core.dto.ConsultaDto;
import es.caib.pinbal.core.dto.ConsultaDto.Consentiment;
import es.caib.pinbal.core.dto.ConsultaDto.DocumentTipus;
import es.caib.pinbal.core.dto.RecobrimentSolicitudDto;
import es.caib.pinbal.core.dto.RespostaAtributsDto;
import es.caib.pinbal.core.service.ConsultaService;
import es.caib.pinbal.core.service.ConsultaServiceImpl;
import es.caib.pinbal.core.service.exception.ConsultaNotFoundException;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.ProcedimentNotFoundException;
import es.caib.pinbal.core.service.exception.ProcedimentServeiNotFoundException;
import es.caib.pinbal.core.service.exception.ServeiNotAllowedException;
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
 * Implementació dels mètodes per al recobriment de les peticions
 * SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
@WebService(
		name = "Recobriment",
		serviceName = "RecobrimentService",
		portName = "RecobrimentServicePort",
		endpointInterface = "es.caib.pinbal.core.ws.Recobriment",
		targetNamespace = "http://www.caib.es/pinbal/ws/recobriment")
public class RecobrimentImpl implements Recobriment {

	@Autowired
	private ConsultaService consultaService;



	@Override
	public Respuesta peticionSincrona(
			Peticion peticion) throws ScspException {
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
			if (consulta.getError() != null) {
				LOGGER.debug("Petició SCSP realitzada amb error (" +
						"peticionId=" + consulta.getScspPeticionId() + ", " +
						"error=" + consulta.getError() + ", " +
						"estadoCodigo=" + consulta.getRespostaEstadoCodigo() + ", " +
						"estadoError=" + consulta.getRespostaEstadoError() + ")");
				throw getErrorValidacio(
						consulta.getRespostaEstadoCodigo(),
						consulta.getRespostaEstadoError());
			} else {
				LOGGER.debug("Petició SCSP realitzada correctament (" +
						"peticionId=" + consulta.getScspPeticionId() + ")");
			}
			LOGGER.debug("Recuperant resposta SCSP per retornar al client (" +
					"peticionId=" + consulta.getScspPeticionId() + ")");
			Respuesta respuesta = recuperarRespuestaScsp(consulta.getScspPeticionId());
			processarDatosEspecificos(respuesta);
			return respuesta;
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
		} catch (ScspException ex) {
			throw ex;
		} catch (Exception ex) {
			LOGGER.error("Error en la consulta SCSP", ex);
			throw getErrorValidacio(
					"0227",
					"Error en la consulta SCSP: " + ExceptionUtils.getStackTrace(ex));
		}
	}

	@Override
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

	@Override
	public Respuesta getRespuesta(
			String idpeticion) throws ScspException {
		throw getErrorValidacio(
				"0227",
				"Mètode no implementat");
	}



	private List<RecobrimentSolicitudDto> validarIObtenirSolicituds(
			Peticion peticion,
			int maxSolicituds) throws ScspException {
		if (peticion == null)
			throw getErrorValidacio(
					"0226",
					"No s'ha trobat l'element peticion");
		if (peticion.getAtributos() == null)
			throw getErrorValidacio(
					"0226",
					"No s'ha trobat l'element peticion.atributos");
		if (peticion.getAtributos().getCodigoCertificado() == null)
			throw getErrorValidacio(
					"0226",
					"No s'ha trobat l'element peticion.atributos.codigoCertificado");
		if (peticion.getSolicitudes() == null)
			throw getErrorValidacio(
					"0226",
					"No s'ha trobat l'element peticion.solicitudes");
		if (peticion.getSolicitudes().getSolicitudTransmision() == null)
			throw getErrorValidacio(
					"0226",
					"No s'ha trobat cap element peticion.solicitudes.solicitudTransmision");
		if (maxSolicituds != -1) {
			int numSolicitudes = peticion.getSolicitudes().getSolicitudTransmision().size();
			if (numSolicitudes > maxSolicituds)
				throw getErrorValidacio(
						"0226",
						"S'ha excedit el màxim nombre de sol·licituds permeses en la petició (màxim=" + maxSolicituds + ")");
		}
		List<RecobrimentSolicitudDto> solicituds = new ArrayList<RecobrimentSolicitudDto>();
		int index = 0;
		for (SolicitudTransmision st: peticion.getSolicitudes().getSolicitudTransmision()) {
			RecobrimentSolicitudDto solicitud = new RecobrimentSolicitudDto();
			DatosGenericos datosGenericos = st.getDatosGenericos();
			if (datosGenericos == null)
				throw getErrorValidacio(
						"0226",
						"No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos (solicitudIndex=" + index + ")");
			if (maxSolicituds != 1) {
				Transmision transmision = datosGenericos.getTransmision();
				if (transmision == null)
					throw getErrorValidacio(
							"0226",
							"No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos.transmision (solicitudIndex=" + index + ")");
				if (transmision.getIdSolicitud() == null)
					throw getErrorValidacio(
							"0226",
							"No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos.transmision.idSolicitud (solicitudIndex=" + index + ")");
				solicitud.setId(transmision.getIdSolicitud());
			} else {
				solicitud.setId("000001");
			}
			if (datosGenericos.getSolicitante() == null)
				throw getErrorValidacio(
						"0226",
						"No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante (solicitudIndex=" + index + ")");
			Solicitante solicitante = datosGenericos.getSolicitante();
			if (solicitante.getIdentificadorSolicitante() == null)
				throw getErrorValidacio(
						"0226",
						"No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.identificadorSolicitante (solicitudIndex=" + index + ")");
			solicitud.setEntitatCif(solicitante.getIdentificadorSolicitante());
			if (solicitante.getFinalidad() == null)
				throw getErrorValidacio(
						"0226",
						"No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.finalidad (solicitudIndex=" + index + ")");
			solicitud.setFinalitat(solicitante.getFinalidad());
			if (solicitante.getConsentimiento() == null)
				throw getErrorValidacio(
						"0226",
						"No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.consentimiento (solicitudIndex=" + index + ")");
			switch (solicitante.getConsentimiento()) {
			case Si:
				solicitud.setConsentiment(Consentiment.Si);
				break;
			case Ley:
				solicitud.setConsentiment(Consentiment.Llei);
				break;
			}
			if (solicitante.getUnidadTramitadora() == null)
				throw getErrorValidacio(
						"0226",
						"No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.unidadTramitadora (solicitudIndex=" + index + ")");
			solicitud.setDepartamentNom(solicitante.getUnidadTramitadora());
			solicitud.setExpedientId(solicitante.getIdExpediente());
			if (solicitante.getProcedimiento() == null)
				throw getErrorValidacio(
						"0226",
						"No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.procedimiento (solicitudIndex=" + index + ")");
			if (solicitante.getProcedimiento().getCodProcedimiento() == null)
				throw getErrorValidacio(
						"0226",
						"No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.procedimiento.codProcedimiento (solicitudIndex=" + index + ")");
			solicitud.setProcedimentCodi(solicitante.getProcedimiento().getCodProcedimiento());
			if (solicitante.getFuncionario() == null)
				throw getErrorValidacio(
						"0226",
						"No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.funcionario (solicitudIndex=" + index + ")");
			if (solicitante.getFuncionario().getNifFuncionario() == null)
				throw getErrorValidacio(
						"0226",
						"No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.funcionario.nifFuncionario (solicitudIndex=" + index + ")");
			solicitud.setFuncionariNif(solicitante.getFuncionario().getNifFuncionario());
			if (solicitante.getFuncionario().getNombreCompletoFuncionario() == null)
				throw getErrorValidacio(
						"0226",
						"No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos.solicitante.funcionario.nombreCompletoFuncionario (solicitudIndex=" + index + ")");
			solicitud.setFuncionariNom(solicitante.getFuncionario().getNombreCompletoFuncionario());
			if (datosGenericos.getTitular() != null) {
				Titular titular = datosGenericos.getTitular();
				if (titular.getDocumentacion() != null) {
					solicitud.setTitularDocumentNum(titular.getDocumentacion());
					if (titular.getTipoDocumentacion() == null)
						throw getErrorValidacio(
								"0226",
								"No s'ha trobat l'element peticion.solicitudes.solicitudTransmision.datosGenericos.titular.tipoDocumentacion (solicitudIndex=" + index + ")");
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
					}
				}
				solicitud.setTitularNom(titular.getNombre());
				solicitud.setTitularLlinatge1(titular.getApellido1());
				solicitud.setTitularLlinatge2(titular.getApellido2());
				solicitud.setTitularNomComplet(titular.getNombreCompleto());
			}
			Object datosEspecificos = st.getDatosEspecificos();
			if (datosEspecificos != null && !(datosEspecificos instanceof Element)) {
				throw getErrorValidacio(
						"0226",
						"L'element peticion.solicitudes.solicitudTransmision (solicitudIndex=" + index + ") no és del tipus org.w3c.dom.Element");
			}
			solicitud.setDadesEspecifiques(
					(Element)st.getDatosEspecificos());
			solicituds.add(solicitud);
			index++;
		}
		return solicituds;
	}

	private ConsultaDto novaConsultaRecobriment(
			String serveiCodi,
			RecobrimentSolicitudDto solicitud) throws EntitatNotFoundException, ProcedimentNotFoundException, ProcedimentServeiNotFoundException, ConsultaNotFoundException, ServeiNotAllowedException, es.caib.pinbal.core.service.exception.ScspException {
		if (consultaService.isOptimitzarTransaccionsNovaConsulta()) {
			LOGGER.error("Nova petició SCSP amb optimitació INIT (" +
						"serveiCodi=" + serveiCodi + ")");
			ConsultaDto consultaInit = consultaService.novaConsultaRecobrimentInit(
					serveiCodi,
					solicitud);
			LOGGER.error("Nova petició SCSP amb optimitació ENVIAMENT (" +
					"serveiCodi=" + serveiCodi + ")");
			consultaService.novaConsultaRecobrimentEnviament(
					consultaInit.getId(),
					solicitud);
			LOGGER.error("Nova petició SCSP amb optimitació ESTAT (" +
					"serveiCodi=" + serveiCodi + ")");
			return consultaService.novaConsultaEstat(
					consultaInit.getId());
		} else {
			LOGGER.error("Nova petició SCSP sense oprimització (" +
					"serveiCodi=" + serveiCodi + ")");
			return consultaService.novaConsultaRecobriment(
					serveiCodi,
					solicitud);
		}
	}

	private Respuesta recuperarRespuestaScsp(String peticionId) throws ScspException {
		ConsultaServiceImpl consultaServiceImpl = (ConsultaServiceImpl)consultaService;
		return consultaServiceImpl.recuperarRespuestaScsp(peticionId);
	}
	private ScspException getErrorValidacio(
			String codi,
			String missatge) {
		return new ScspException(missatge, codi);
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
					String datosEspecificosProcessat =  datosEspecificosSenseNs.substring(0, "<DatosEspecificos ".length()) +
							xmlns + 
							datosEspecificosSenseNs.substring("<DatosEspecificos ".length());
					transmisionDatos.setDatosEspecificos(
							stringToElement(datosEspecificosProcessat));
				}
			}
		}
	}
	private String removeXmlStringNamespaceAndPreamble(String xml) {
		return xml.replaceAll("(<\\?[^<]*\\?>)?", ""). /* remove preamble */
				replaceAll("xmlns.*?(\"|\').*?(\"|\')", ""). /* remove xmlns declaration */
				replaceAll("(<)(\\w+:)(.*?>)", "$1$3"). /* remove opening tag prefix */
				replaceAll("(</)(\\w+:)(.*?>)", "$1$3"); /* remove closing tags prefix */
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

	private static final Logger LOGGER = LoggerFactory.getLogger(RecobrimentImpl.class);

}
