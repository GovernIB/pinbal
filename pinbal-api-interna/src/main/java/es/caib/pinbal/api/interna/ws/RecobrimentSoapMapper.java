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
import es.caib.pinbal.client.recobriment.model.ScspTitular.ScspTipoDocumentacion;
import es.caib.pinbal.client.recobriment.model.ScspTransmision;
import es.caib.pinbal.client.recobriment.model.ScspTransmisionDatos;
import es.scsp.bean.common.confirmacion.ConfirmacionPeticion;
import es.scsp.bean.common.peticion.Consentimiento;
import es.scsp.bean.common.peticion.Peticion;
import es.scsp.bean.common.peticion.SolicitudTransmision;
import es.scsp.bean.common.peticion.Solicitudes;
import es.scsp.bean.common.peticion.TipoDocumentacion;
import es.scsp.bean.common.respuesta.Respuesta;
import es.scsp.bean.common.respuesta.Transmisiones;
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
 * Conversor entre el contracte SOAP SCSP i els DTOs REST de recobriment.
 */
class RecobrimentSoapMapper {

	ScspPeticion toScspPeticion(Peticion peticion) throws TransformerException {
		if (peticion == null) {
			return null;
		}
		ScspPeticion scspPeticion = new ScspPeticion();
		scspPeticion.setAtributos(toScspAtributos(peticion.getAtributos()));
		if (peticion.getSolicitudes() != null
				&& peticion.getSolicitudes().getSolicitudTransmision() != null) {
			List<ScspSolicitud> solicituds = new ArrayList<ScspSolicitud>();
			for (SolicitudTransmision solicitudTransmision : peticion.getSolicitudes().getSolicitudTransmision()) {
				ScspSolicitud solicitud = new ScspSolicitud();
				solicitud.setDatosGenericos(toScspDatosGenericos(solicitudTransmision.getDatosGenericos()));
				solicitud.setDatosEspecificos(toXmlString(solicitudTransmision.getDatosEspecificos()));
				solicituds.add(solicitud);
			}
			scspPeticion.setSolicitudes(solicituds);
		}
		return scspPeticion;
	}

	Respuesta toRespuesta(ScspRespuesta scspRespuesta) throws ParserConfigurationException, SAXException, IOException {
		if (scspRespuesta == null) {
			return null;
		}
		Respuesta respuesta = new Respuesta();
		respuesta.setAtributos(toRespuestaAtributos(scspRespuesta.getAtributos()));
		if (scspRespuesta.getTransmisiones() != null) {
			Transmisiones transmisiones = new Transmisiones();
			for (ScspTransmisionDatos scspTransmisionDatos : scspRespuesta.getTransmisiones()) {
				es.scsp.bean.common.respuesta.TransmisionDatos transmisionDatos =
						new es.scsp.bean.common.respuesta.TransmisionDatos();
				transmisionDatos.setDatosGenericos(toRespuestaDatosGenericos(scspTransmisionDatos.getDatosGenericos()));
				transmisionDatos.setDatosEspecificos(toElement(scspTransmisionDatos.getDatosEspecificos()));
				transmisiones.getTransmisionDatos().add(transmisionDatos);
			}
			respuesta.setTransmisiones(transmisiones);
		}
		return respuesta;
	}

	ConfirmacionPeticion toConfirmacionPeticion(ScspConfirmacionPeticion scspConfirmacionPeticion) {
		if (scspConfirmacionPeticion == null) {
			return null;
		}
		ConfirmacionPeticion confirmacionPeticion = new ConfirmacionPeticion();
		confirmacionPeticion.setAtributos(toConfirmacionAtributos(scspConfirmacionPeticion.getAtributos()));
		return confirmacionPeticion;
	}

	private ScspAtributos toScspAtributos(es.scsp.bean.common.peticion.Atributos source) {
		if (source == null) {
			return null;
		}
		ScspAtributos target = new ScspAtributos();
		target.setIdPeticion(source.getIdPeticion());
		target.setNumElementos(String.valueOf(source.getNumElementos()));
		target.setTimeStamp(source.getTimeStamp());
		target.setCodigoCertificado(source.getCodigoCertificado());
		target.setEstado(toScspEstado(source.getEstado()));
		return target;
	}

	private ScspEstado toScspEstado(es.scsp.bean.common.peticion.Estado source) {
		if (source == null) {
			return null;
		}
		ScspEstado target = new ScspEstado();
		target.setCodigoEstado(source.getCodigoEstado());
		target.setCodigoEstadoSecundario(source.getCodigoEstadoSecundario());
		target.setLiteralError(source.getLiteralError());
		target.setTiempoEstimadoRespuesta(source.getTiempoEstimadoRespuesta());
		return target;
	}

	private ScspDatosGenericos toScspDatosGenericos(es.scsp.bean.common.peticion.DatosGenericos source) {
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

	private ScspEmisor toScspEmisor(es.scsp.bean.common.peticion.Emisor source) {
		if (source == null) {
			return null;
		}
		ScspEmisor target = new ScspEmisor();
		target.setNifEmisor(source.getNifEmisor());
		target.setNombreEmisor(source.getNombreEmisor());
		return target;
	}

	private ScspSolicitante toScspSolicitante(es.scsp.bean.common.peticion.Solicitante source) {
		if (source == null) {
			return null;
		}
		ScspSolicitante target = new ScspSolicitante();
		target.setProcedimiento(toScspProcedimiento(source.getProcedimiento()));
		target.setFuncionario(toScspFuncionario(source.getFuncionario()));
		target.setUnidadTramitadora(source.getUnidadTramitadora());
		target.setCodigoUnidadTramitadora(source.getCodigoUnidadTramitadora());
		target.setIdentificadorSolicitante(source.getIdentificadorSolicitante());
		target.setNombreSolicitante(source.getNombreSolicitante());
		target.setIdExpediente(source.getIdExpediente());
		target.setFinalidad(source.getFinalidad());
		target.setConsentimiento(toScspConsentimiento(source.getConsentimiento()));
		return target;
	}

	private ScspProcedimiento toScspProcedimiento(es.scsp.bean.common.peticion.Procedimiento source) {
		if (source == null) {
			return null;
		}
		ScspProcedimiento target = new ScspProcedimiento();
		target.setCodProcedimiento(source.getCodProcedimiento());
		target.setNombreProcedimiento(source.getNombreProcedimiento());
		return target;
	}

	private ScspFuncionario toScspFuncionario(es.scsp.bean.common.peticion.Funcionario source) {
		if (source == null) {
			return null;
		}
		ScspFuncionario target = new ScspFuncionario();
		target.setNombreCompletoFuncionario(source.getNombreCompletoFuncionario());
		target.setNifFuncionario(source.getNifFuncionario());
		return target;
	}

	private ScspSolicitante.ScspConsentimiento toScspConsentimiento(Consentimiento source) {
		if (Consentimiento.SI.equals(source)) {
			return ScspSolicitante.ScspConsentimiento.Si;
		}
		if (Consentimiento.LEY.equals(source)) {
			return ScspSolicitante.ScspConsentimiento.Ley;
		}
		return null;
	}

	private ScspTitular toScspTitular(es.scsp.bean.common.peticion.Titular source) {
		if (source == null) {
			return null;
		}
		ScspTitular target = new ScspTitular();
		target.setTipoDocumentacion(toScspTipoDocumentacion(source.getTipoDocumentacion()));
		target.setDocumentacion(source.getDocumentacion());
		target.setNombreCompleto(source.getNombreCompleto());
		target.setNombre(source.getNombre());
		target.setApellido1(source.getApellido1());
		target.setApellido2(source.getApellido2());
		return target;
	}

	private ScspTipoDocumentacion toScspTipoDocumentacion(TipoDocumentacion source) {
		if (TipoDocumentacion.CIF.equals(source)) {
			return ScspTipoDocumentacion.CIF;
		}
		if (TipoDocumentacion.CSV.equals(source)) {
			return ScspTipoDocumentacion.CSV;
		}
		if (TipoDocumentacion.DNI.equals(source)) {
			return ScspTipoDocumentacion.DNI;
		}
		if (TipoDocumentacion.NIE.equals(source)) {
			return ScspTipoDocumentacion.NIE;
		}
		if (TipoDocumentacion.NIF.equals(source)) {
			return ScspTipoDocumentacion.NIF;
		}
		if (TipoDocumentacion.PASAPORTE.equals(source)) {
			return ScspTipoDocumentacion.Pasaporte;
		}
		if (TipoDocumentacion.NUMERO_IDENTIFICACION.equals(source)) {
			return ScspTipoDocumentacion.NumeroIdentificacion;
		}
		if (TipoDocumentacion.OTROS.equals(source)) {
			return ScspTipoDocumentacion.Otros;
		}
		return null;
	}

	private ScspTransmision toScspTransmision(es.scsp.bean.common.peticion.Transmision source) {
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

	private es.scsp.bean.common.respuesta.Atributos toRespuestaAtributos(ScspAtributos source) {
		if (source == null) {
			return null;
		}
		es.scsp.bean.common.respuesta.Atributos target = new es.scsp.bean.common.respuesta.Atributos();
		target.setIdPeticion(source.getIdPeticion());
		target.setNumElementos(toInt(source.getNumElementos()));
		target.setTimeStamp(source.getTimeStamp());
		target.setCodigoCertificado(source.getCodigoCertificado());
		target.setEstado(toRespuestaEstado(source.getEstado()));
		return target;
	}

	private es.scsp.bean.common.respuesta.Estado toRespuestaEstado(ScspEstado source) {
		if (source == null) {
			return null;
		}
		es.scsp.bean.common.respuesta.Estado target = new es.scsp.bean.common.respuesta.Estado();
		target.setCodigoEstado(source.getCodigoEstado());
		target.setCodigoEstadoSecundario(source.getCodigoEstadoSecundario());
		target.setLiteralError(source.getLiteralError());
		target.setLiteralErrorSecundario(source.getLiteralErrorSec());
		target.setTiempoEstimadoRespuesta(source.getTiempoEstimadoRespuesta());
		return target;
	}

	private es.scsp.bean.common.respuesta.DatosGenericos toRespuestaDatosGenericos(ScspDatosGenericos source) {
		if (source == null) {
			return null;
		}
		es.scsp.bean.common.respuesta.DatosGenericos target = new es.scsp.bean.common.respuesta.DatosGenericos();
		target.setEmisor(toRespuestaEmisor(source.getEmisor()));
		target.setSolicitante(toRespuestaSolicitante(source.getSolicitante()));
		target.setTitular(toRespuestaTitular(source.getTitular()));
		target.setTransmision(toRespuestaTransmision(source.getTransmision()));
		return target;
	}

	private es.scsp.bean.common.respuesta.Emisor toRespuestaEmisor(ScspEmisor source) {
		if (source == null) {
			return null;
		}
		es.scsp.bean.common.respuesta.Emisor target = new es.scsp.bean.common.respuesta.Emisor();
		target.setNifEmisor(source.getNifEmisor());
		target.setNombreEmisor(source.getNombreEmisor());
		return target;
	}

	private es.scsp.bean.common.respuesta.Solicitante toRespuestaSolicitante(ScspSolicitante source) {
		if (source == null) {
			return null;
		}
		es.scsp.bean.common.respuesta.Solicitante target = new es.scsp.bean.common.respuesta.Solicitante();
		target.setProcedimiento(toRespuestaProcedimiento(source.getProcedimiento()));
		target.setFuncionario(toRespuestaFuncionario(source.getFuncionario()));
		target.setUnidadTramitadora(source.getUnidadTramitadora());
		target.setCodigoUnidadTramitadora(source.getCodigoUnidadTramitadora());
		target.setIdentificadorSolicitante(source.getIdentificadorSolicitante());
		target.setNombreSolicitante(source.getNombreSolicitante());
		target.setIdExpediente(source.getIdExpediente());
		target.setFinalidad(source.getFinalidad());
		target.setConsentimiento(toRespuestaConsentimiento(source.getConsentimiento()));
		return target;
	}

	private es.scsp.bean.common.respuesta.Procedimiento toRespuestaProcedimiento(ScspProcedimiento source) {
		if (source == null) {
			return null;
		}
		es.scsp.bean.common.respuesta.Procedimiento target = new es.scsp.bean.common.respuesta.Procedimiento();
		target.setCodProcedimiento(source.getCodProcedimiento());
		target.setNombreProcedimiento(source.getNombreProcedimiento());
		return target;
	}

	private es.scsp.bean.common.respuesta.Funcionario toRespuestaFuncionario(ScspFuncionario source) {
		if (source == null) {
			return null;
		}
		es.scsp.bean.common.respuesta.Funcionario target = new es.scsp.bean.common.respuesta.Funcionario();
		target.setNombreCompletoFuncionario(source.getNombreCompletoFuncionario());
		target.setNifFuncionario(source.getNifFuncionario());
		return target;
	}

	private es.scsp.bean.common.respuesta.Consentimiento toRespuestaConsentimiento(
			ScspSolicitante.ScspConsentimiento source) {
		if (ScspSolicitante.ScspConsentimiento.Si.equals(source)) {
			return es.scsp.bean.common.respuesta.Consentimiento.SI;
		}
		if (ScspSolicitante.ScspConsentimiento.Ley.equals(source)) {
			return es.scsp.bean.common.respuesta.Consentimiento.LEY;
		}
		return null;
	}

	private es.scsp.bean.common.respuesta.Titular toRespuestaTitular(ScspTitular source) {
		if (source == null) {
			return null;
		}
		es.scsp.bean.common.respuesta.Titular target = new es.scsp.bean.common.respuesta.Titular();
		target.setTipoDocumentacion(toRespuestaTipoDocumentacion(source.getTipoDocumentacion()));
		target.setDocumentacion(source.getDocumentacion());
		target.setNombreCompleto(source.getNombreCompleto());
		target.setNombre(source.getNombre());
		target.setApellido1(source.getApellido1());
		target.setApellido2(source.getApellido2());
		return target;
	}

	private es.scsp.bean.common.respuesta.TipoDocumentacion toRespuestaTipoDocumentacion(
			ScspTipoDocumentacion source) {
		if (ScspTipoDocumentacion.CIF.equals(source)) {
			return es.scsp.bean.common.respuesta.TipoDocumentacion.CIF;
		}
		if (ScspTipoDocumentacion.CSV.equals(source)) {
			return es.scsp.bean.common.respuesta.TipoDocumentacion.CSV;
		}
		if (ScspTipoDocumentacion.DNI.equals(source)) {
			return es.scsp.bean.common.respuesta.TipoDocumentacion.DNI;
		}
		if (ScspTipoDocumentacion.NIE.equals(source)) {
			return es.scsp.bean.common.respuesta.TipoDocumentacion.NIE;
		}
		if (ScspTipoDocumentacion.NIF.equals(source)) {
			return es.scsp.bean.common.respuesta.TipoDocumentacion.NIF;
		}
		if (ScspTipoDocumentacion.Pasaporte.equals(source)) {
			return es.scsp.bean.common.respuesta.TipoDocumentacion.PASAPORTE;
		}
		if (ScspTipoDocumentacion.NumeroIdentificacion.equals(source)) {
			return es.scsp.bean.common.respuesta.TipoDocumentacion.NUMERO_IDENTIFICACION;
		}
		if (ScspTipoDocumentacion.Otros.equals(source)) {
			return es.scsp.bean.common.respuesta.TipoDocumentacion.OTROS;
		}
		return null;
	}

	private es.scsp.bean.common.respuesta.Transmision toRespuestaTransmision(ScspTransmision source) {
		if (source == null) {
			return null;
		}
		es.scsp.bean.common.respuesta.Transmision target = new es.scsp.bean.common.respuesta.Transmision();
		target.setCodigoCertificado(source.getCodigoCertificado());
		target.setIdSolicitud(source.getIdSolicitud());
		target.setIdTransmision(source.getIdTransmision());
		target.setFechaGeneracion(source.getFechaGeneracion());
		return target;
	}

	private es.scsp.bean.common.confirmacion.Atributos toConfirmacionAtributos(ScspAtributos source) {
		if (source == null) {
			return null;
		}
		es.scsp.bean.common.confirmacion.Atributos target = new es.scsp.bean.common.confirmacion.Atributos();
		target.setIdPeticion(source.getIdPeticion());
		target.setNumElementos(toInt(source.getNumElementos()));
		target.setTimeStamp(source.getTimeStamp());
		target.setCodigoCertificado(source.getCodigoCertificado());
		target.setEstado(toConfirmacionEstado(source.getEstado()));
		return target;
	}

	private es.scsp.bean.common.confirmacion.Estado toConfirmacionEstado(ScspEstado source) {
		if (source == null) {
			return null;
		}
		es.scsp.bean.common.confirmacion.Estado target = new es.scsp.bean.common.confirmacion.Estado();
		target.setCodigoEstado(source.getCodigoEstado());
		target.setCodigoEstadoSecundario(source.getCodigoEstadoSecundario());
		target.setLiteralError(source.getLiteralError());
		target.setTiempoEstimadoRespuesta(source.getTiempoEstimadoRespuesta());
		return target;
	}

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

	private int toInt(String value) {
		return value != null && !value.isEmpty() ? Integer.parseInt(value) : 0;
	}

}
