/**
 *
 */
package es.caib.pinbal.api.interna.ws;

import es.caib.pinbal.client.recobriment.model.ScspAtributos;
import es.caib.pinbal.client.recobriment.model.ScspConfirmacionPeticion;
import es.caib.pinbal.client.recobriment.model.ScspDatosGenericos;
import es.caib.pinbal.client.recobriment.model.ScspEmisor;
import es.caib.pinbal.client.recobriment.model.ScspEstado;
import es.caib.pinbal.client.recobriment.model.ScspFuncionario;
import es.caib.pinbal.client.recobriment.model.ScspPeticion;
import es.caib.pinbal.client.recobriment.model.ScspProcedimiento;
import es.caib.pinbal.client.recobriment.model.ScspRespuesta;
import es.caib.pinbal.client.recobriment.model.ScspSolicitante;
import es.caib.pinbal.client.recobriment.model.ScspSolicitud;
import es.caib.pinbal.client.recobriment.model.ScspTitular;
import es.caib.pinbal.client.recobriment.model.ScspTransmision;
import es.caib.pinbal.client.recobriment.model.ScspTransmisionDatos;
import es.caib.pinbal.logic.intf.ws.model.WsAtributos;
import es.caib.pinbal.logic.intf.ws.model.WsConfirmacionPeticion;
import es.caib.pinbal.logic.intf.ws.model.WsConsentimiento;
import es.caib.pinbal.logic.intf.ws.model.WsDatosGenericos;
import es.caib.pinbal.logic.intf.ws.model.WsEmisor;
import es.caib.pinbal.logic.intf.ws.model.WsEstado;
import es.caib.pinbal.logic.intf.ws.model.WsFuncionario;
import es.caib.pinbal.logic.intf.ws.model.WsPeticion;
import es.caib.pinbal.logic.intf.ws.model.WsProcedimiento;
import es.caib.pinbal.logic.intf.ws.model.WsRespuesta;
import es.caib.pinbal.logic.intf.ws.model.WsSolicitante;
import es.caib.pinbal.logic.intf.ws.model.WsSolicitudTransmision;
import es.caib.pinbal.logic.intf.ws.model.WsTipoDocumentacion;
import es.caib.pinbal.logic.intf.ws.model.WsTitular;
import es.caib.pinbal.logic.intf.ws.model.WsTransmision;
import es.caib.pinbal.logic.intf.ws.model.WsTransmisionDatos;
import es.caib.pinbal.logic.intf.ws.model.WsTransmisiones;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Conversor entre el contracte SOAP de recobriment (el de PINBAL 1.4.x, vegeu
 * {@code es.caib.pinbal.logic.intf.ws.model}) i els DTOs de recobriment del servei.
 * <p>
 * Les dades específiques arriben com a {@code xs:anyType}: JAXB les deixa com l'element DOM
 * {@code <datosEspecificos>} amb el contingut que envia el client, i es passen al servei
 * serialitzades, igual que feia la versió 1.4.x amb l'element.
 */
class RecobrimentSoapMapper {

	ScspPeticion toScspPeticion(WsPeticion peticion) throws TransformerException {
		if (peticion == null) {
			return null;
		}
		ScspPeticion scspPeticion = new ScspPeticion();
		scspPeticion.setAtributos(toScspAtributos(peticion.getAtributos()));
		if (peticion.getSolicitudes() != null
				&& peticion.getSolicitudes().getSolicitudTransmision() != null) {
			List<ScspSolicitud> solicituds = new ArrayList<ScspSolicitud>();
			for (WsSolicitudTransmision solicitudTransmision : peticion.getSolicitudes().getSolicitudTransmision()) {
				if (solicitudTransmision == null) {
					continue;
				}
				ScspSolicitud solicitud = new ScspSolicitud();
				solicitud.setId(solicitudTransmision.getId());
				solicitud.setDatosGenericos(toScspDatosGenericos(solicitudTransmision.getDatosGenericos()));
				solicitud.setDatosEspecificos(toXmlString(solicitudTransmision.getDatosEspecificos()));
				solicituds.add(solicitud);
			}
			scspPeticion.setSolicitudes(solicituds);
		}
		return scspPeticion;
	}

	WsRespuesta toRespuesta(ScspRespuesta scspRespuesta) throws ParserConfigurationException, SAXException, IOException {
		if (scspRespuesta == null) {
			return null;
		}
		WsRespuesta respuesta = new WsRespuesta();
		respuesta.setAtributos(toWsAtributos(scspRespuesta.getAtributos()));
		if (scspRespuesta.getTransmisiones() != null) {
			WsTransmisiones transmisiones = new WsTransmisiones();
			for (ScspTransmisionDatos scspTransmisionDatos : scspRespuesta.getTransmisiones()) {
				WsTransmisionDatos transmisionDatos = new WsTransmisionDatos();
				transmisionDatos.setId(scspTransmisionDatos.getId());
				transmisionDatos.setDatosGenericos(toWsDatosGenericos(scspTransmisionDatos.getDatosGenericos()));
				transmisionDatos.setDatosEspecificos(toElement(scspTransmisionDatos.getDatosEspecificos()));
				transmisiones.getTransmisionDatos().add(transmisionDatos);
			}
			respuesta.setTransmisiones(transmisiones);
		}
		return respuesta;
	}

	WsConfirmacionPeticion toConfirmacionPeticion(ScspConfirmacionPeticion scspConfirmacionPeticion) {
		if (scspConfirmacionPeticion == null) {
			return null;
		}
		WsConfirmacionPeticion confirmacionPeticion = new WsConfirmacionPeticion();
		confirmacionPeticion.setAtributos(toWsAtributos(scspConfirmacionPeticion.getAtributos()));
		return confirmacionPeticion;
	}

	// ---------------------------------------------------------------------------------------------
	// Petició: contracte SOAP -> DTO
	// ---------------------------------------------------------------------------------------------

	private ScspAtributos toScspAtributos(WsAtributos source) {
		if (source == null) {
			return null;
		}
		ScspAtributos target = new ScspAtributos();
		target.setIdPeticion(source.getIdPeticion());
		target.setNumElementos(source.getNumElementos());
		target.setTimeStamp(source.getTimeStamp());
		target.setCodigoCertificado(source.getCodigoCertificado());
		target.setEstado(toScspEstado(source.getEstado()));
		return target;
	}

	private ScspEstado toScspEstado(WsEstado source) {
		if (source == null) {
			return null;
		}
		ScspEstado target = new ScspEstado();
		target.setCodigoEstado(source.getCodigoEstado());
		target.setCodigoEstadoSecundario(source.getCodigoEstadoSecundario());
		target.setLiteralError(source.getLiteralError());
		target.setLiteralErrorSec(source.getLiteralErrorSec());
		target.setTiempoEstimadoRespuesta(source.getTiempoEstimadoRespuesta());
		return target;
	}

	private ScspDatosGenericos toScspDatosGenericos(WsDatosGenericos source) {
		if (source == null) {
			return null;
		}
		ScspDatosGenericos target = new ScspDatosGenericos();
		target.setEmisor(toScspEmisor(source.getEmisor()));
		target.setSolicitante(toScspSolicitante(source.getSolicitante()));
		target.setTitular(toScspTitular(source.getTitular()));
		target.setTransmision(toScspTransmision(source.getTransmision()));
		return target;
	}

	private ScspEmisor toScspEmisor(WsEmisor source) {
		if (source == null) {
			return null;
		}
		ScspEmisor target = new ScspEmisor();
		target.setNifEmisor(source.getNifEmisor());
		target.setNombreEmisor(source.getNombreEmisor());
		return target;
	}

	private ScspSolicitante toScspSolicitante(WsSolicitante source) {
		if (source == null) {
			return null;
		}
		ScspSolicitante target = new ScspSolicitante();
		target.setIdentificadorSolicitante(source.getIdentificadorSolicitante());
		target.setNombreSolicitante(source.getNombreSolicitante());
		target.setUnidadTramitadora(source.getUnidadTramitadora());
		target.setCodigoUnidadTramitadora(source.getCodigoUnidadTramitadora());
		target.setIdExpediente(source.getIdExpediente());
		target.setFinalidad(source.getFinalidad());
		target.setProcedimiento(toScspProcedimiento(source.getProcedimiento()));
		target.setFuncionario(toScspFuncionario(source.getFuncionario()));
		target.setConsentimiento(source.getConsentimiento() != null
				? ScspSolicitante.ScspConsentimiento.valueOf(source.getConsentimiento().name())
				: null);
		return target;
	}

	private ScspProcedimiento toScspProcedimiento(WsProcedimiento source) {
		if (source == null) {
			return null;
		}
		ScspProcedimiento target = new ScspProcedimiento();
		target.setCodProcedimiento(source.getCodProcedimiento());
		target.setNombreProcedimiento(source.getNombreProcedimiento());
		return target;
	}

	private ScspFuncionario toScspFuncionario(WsFuncionario source) {
		if (source == null) {
			return null;
		}
		ScspFuncionario target = new ScspFuncionario();
		target.setNifFuncionario(source.getNifFuncionario());
		target.setNombreCompletoFuncionario(source.getNombreCompletoFuncionario());
		target.setSeudonimo(source.getSeudonimo());
		return target;
	}

	private ScspTitular toScspTitular(WsTitular source) {
		if (source == null) {
			return null;
		}
		ScspTitular target = new ScspTitular();
		target.setTipoDocumentacion(source.getTipoDocumentacion() != null
				? ScspTitular.ScspTipoDocumentacion.valueOf(source.getTipoDocumentacion().name())
				: null);
		target.setDocumentacion(source.getDocumentacion());
		target.setNombreCompleto(source.getNombreCompleto());
		target.setNombre(source.getNombre());
		target.setApellido1(source.getApellido1());
		target.setApellido2(source.getApellido2());
		return target;
	}

	private ScspTransmision toScspTransmision(WsTransmision source) {
		if (source == null) {
			return null;
		}
		ScspTransmision target = new ScspTransmision();
		target.setCodigoCertificado(source.getCodigoCertificado());
		target.setIdSolicitud(source.getIdSolicitud());
		target.setIdTransmision(source.getIdTransmision());
		target.setFechaGeneracion(source.getFechaGeneracion());
		return target;
	}

	// ---------------------------------------------------------------------------------------------
	// Resposta: DTO -> contracte SOAP
	// ---------------------------------------------------------------------------------------------

	private WsAtributos toWsAtributos(ScspAtributos source) {
		if (source == null) {
			return null;
		}
		WsAtributos target = new WsAtributos();
		target.setIdPeticion(source.getIdPeticion());
		target.setNumElementos(source.getNumElementos());
		target.setTimeStamp(source.getTimeStamp());
		target.setCodigoCertificado(source.getCodigoCertificado());
		target.setEstado(toWsEstado(source.getEstado()));
		return target;
	}

	private WsEstado toWsEstado(ScspEstado source) {
		if (source == null) {
			return null;
		}
		WsEstado target = new WsEstado();
		target.setCodigoEstado(source.getCodigoEstado());
		target.setCodigoEstadoSecundario(source.getCodigoEstadoSecundario());
		target.setLiteralError(source.getLiteralError());
		target.setLiteralErrorSec(source.getLiteralErrorSec());
		target.setTiempoEstimadoRespuesta(source.getTiempoEstimadoRespuesta());
		return target;
	}

	private WsDatosGenericos toWsDatosGenericos(ScspDatosGenericos source) {
		if (source == null) {
			return null;
		}
		WsDatosGenericos target = new WsDatosGenericos();
		target.setEmisor(toWsEmisor(source.getEmisor()));
		target.setSolicitante(toWsSolicitante(source.getSolicitante()));
		target.setTitular(toWsTitular(source.getTitular()));
		target.setTransmision(toWsTransmision(source.getTransmision()));
		return target;
	}

	private WsEmisor toWsEmisor(ScspEmisor source) {
		if (source == null) {
			return null;
		}
		WsEmisor target = new WsEmisor();
		target.setNifEmisor(source.getNifEmisor());
		target.setNombreEmisor(source.getNombreEmisor());
		return target;
	}

	private WsSolicitante toWsSolicitante(ScspSolicitante source) {
		if (source == null) {
			return null;
		}
		WsSolicitante target = new WsSolicitante();
		target.setIdentificadorSolicitante(source.getIdentificadorSolicitante());
		target.setNombreSolicitante(source.getNombreSolicitante());
		target.setUnidadTramitadora(source.getUnidadTramitadora());
		target.setCodigoUnidadTramitadora(source.getCodigoUnidadTramitadora());
		target.setIdExpediente(source.getIdExpediente());
		target.setFinalidad(source.getFinalidad());
		target.setProcedimiento(toWsProcedimiento(source.getProcedimiento()));
		target.setFuncionario(toWsFuncionario(source.getFuncionario()));
		target.setConsentimiento(source.getConsentimiento() != null
				? WsConsentimiento.valueOf(source.getConsentimiento().name())
				: null);
		return target;
	}

	private WsProcedimiento toWsProcedimiento(ScspProcedimiento source) {
		if (source == null) {
			return null;
		}
		WsProcedimiento target = new WsProcedimiento();
		target.setCodProcedimiento(source.getCodProcedimiento());
		target.setNombreProcedimiento(source.getNombreProcedimiento());
		return target;
	}

	private WsFuncionario toWsFuncionario(ScspFuncionario source) {
		if (source == null) {
			return null;
		}
		WsFuncionario target = new WsFuncionario();
		target.setNifFuncionario(source.getNifFuncionario());
		target.setNombreCompletoFuncionario(source.getNombreCompletoFuncionario());
		target.setSeudonimo(source.getSeudonimo());
		return target;
	}

	private WsTitular toWsTitular(ScspTitular source) {
		if (source == null) {
			return null;
		}
		WsTitular target = new WsTitular();
		target.setTipoDocumentacion(source.getTipoDocumentacion() != null
				? WsTipoDocumentacion.valueOf(source.getTipoDocumentacion().name())
				: null);
		target.setDocumentacion(source.getDocumentacion());
		target.setNombreCompleto(source.getNombreCompleto());
		target.setNombre(source.getNombre());
		target.setApellido1(source.getApellido1());
		target.setApellido2(source.getApellido2());
		return target;
	}

	private WsTransmision toWsTransmision(ScspTransmision source) {
		if (source == null) {
			return null;
		}
		WsTransmision target = new WsTransmision();
		target.setCodigoCertificado(source.getCodigoCertificado());
		target.setIdSolicitud(source.getIdSolicitud());
		target.setIdTransmision(source.getIdTransmision());
		target.setFechaGeneracion(source.getFechaGeneracion());
		return target;
	}

	// ---------------------------------------------------------------------------------------------
	// Dades específiques
	// ---------------------------------------------------------------------------------------------

	private String toXmlString(Object node) throws TransformerException {
		if (!(node instanceof Node)) {
			return null;
		}
		StringWriter writer = new StringWriter();
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.transform(new DOMSource((Node) node), new StreamResult(writer));
		return writer.toString();
	}

	private Element toElement(String xml) throws ParserConfigurationException, SAXException, IOException {
		if (xml == null || xml.isEmpty()) {
			return null;
		}
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
		return document.getDocumentElement();
	}

}
