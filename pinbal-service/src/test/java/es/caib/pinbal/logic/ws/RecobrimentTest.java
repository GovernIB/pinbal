/**
 * 
 */
package es.caib.pinbal.logic.ws;

import es.caib.pinbal.logic.intf.ws.Recobriment;
import es.caib.pinbal.logic.intf.ws.model.WsAtributos;
import es.caib.pinbal.logic.intf.ws.model.WsConsentimiento;
import es.caib.pinbal.logic.intf.ws.model.WsDatosGenericos;
import es.caib.pinbal.logic.intf.ws.model.WsFuncionario;
import es.caib.pinbal.logic.intf.ws.model.WsPeticion;
import es.caib.pinbal.logic.intf.ws.model.WsProcedimiento;
import es.caib.pinbal.logic.intf.ws.model.WsSolicitante;
import es.caib.pinbal.logic.intf.ws.model.WsSolicitudTransmision;
import es.caib.pinbal.logic.intf.ws.model.WsSolicitudes;
import es.caib.pinbal.logic.intf.ws.model.WsTipoDocumentacion;
import es.caib.pinbal.logic.intf.ws.model.WsTitular;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Test del servei web de recobriment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RecobrimentTest {

	private static final String ENDPOINT_ADDRESS = "http://localhost:8180/pinbal/ws/recobriment";
	private static final String USERNAME = "siona";
	private static final String PASSWORD = "siona";

	//private static final String XMLNS_DATOS_ESPECIFICOS_V2 = "http://www.map.es/scsp/esquemas/datosespecificos";
	//private static final String XMLNS_DATOS_ESPECIFICOS_V3 = "http://www.map.es/scsp/esquemas/datosespecificos";



	public static void main(String[] args) {
		try {
			RecobrimentTest recobrimentTest = new RecobrimentTest();
			recobrimentTest.test(
					"SCMCEDU",
					"B07167448",
					"Limit Tecnologies",
					"Departament d'atenció al client",
					"ProvaConcepte",
					"Prova de concepte",
					"Sion Andreu",
					"97669911C",
					WsTipoDocumentacion.NIF,
					"12345678Z");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void test(
			String codigoCertificado,
			String solicitanteId,
			String solicitanteNombre,
			String solicitanteUnidadTramitadora,
			String procedimentCodi,
			String procedimentNom,
			String funcionarioNombre,
			String funcionarioNif,
			WsTipoDocumentacion titularTipoDocumentacion,
			String titularDocumentacion) throws Exception {
		URL url = new URL(ENDPOINT_ADDRESS + "?wsdl");
		QName qname = new QName(
				"http://www.caib.es/pinbal/ws/recobriment",
				"RecobrimentService");
		Service service = Service.create(url, qname);
		Recobriment recobriment = service.getPort(RecobrimentWs.class);
		BindingProvider prov = (BindingProvider)recobriment;
		@SuppressWarnings("rawtypes")
		List<Handler> handlerChain = new ArrayList<Handler>();
		handlerChain.add(new LogMessageHandler());
		prov.getBinding().setHandlerChain(handlerChain);
		prov.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, USERNAME);
		prov.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, PASSWORD);
		WsPeticion peticion = new WsPeticion();
		WsAtributos atributos = new WsAtributos();
		atributos.setCodigoCertificado(codigoCertificado);
		peticion.setAtributos(atributos);
		WsSolicitudes solicitudes = new WsSolicitudes();
		ArrayList<WsSolicitudTransmision> solicitudesList = new ArrayList<WsSolicitudTransmision>();
		WsSolicitudTransmision solicitudTransmision = new WsSolicitudTransmision();
		WsDatosGenericos datosGenericos = new WsDatosGenericos();
		WsSolicitante solicitante = new WsSolicitante();
		solicitante.setIdentificadorSolicitante(solicitanteId);
		solicitante.setNombreSolicitante(solicitanteNombre);
		WsProcedimiento procedimiento = new WsProcedimiento();
		procedimiento.setCodProcedimiento(procedimentCodi);
		procedimiento.setNombreProcedimiento(procedimentNom);
		solicitante.setProcedimiento(procedimiento);
		WsFuncionario funcionario = new WsFuncionario();
		funcionario.setNombreCompletoFuncionario(funcionarioNombre);
		funcionario.setNifFuncionario(funcionarioNif);
		solicitante.setFuncionario(funcionario);
		solicitante.setFinalidad(procedimentNom);
		solicitante.setUnidadTramitadora(solicitanteUnidadTramitadora);
		solicitante.setConsentimiento(WsConsentimiento.Si);
		datosGenericos.setSolicitante(solicitante);
		WsTitular titular = new WsTitular();
		titular.setTipoDocumentacion(titularTipoDocumentacion);
		titular.setDocumentacion(titularDocumentacion);
		datosGenericos.setTitular(titular);
		solicitudTransmision.setDatosGenericos(datosGenericos);
		solicitudTransmision.setDatosEspecificos(createDatosEspecificos());
		solicitudesList.add(solicitudTransmision);
		solicitudes.getSolicitudTransmision().addAll(solicitudesList);
		peticion.setSolicitudes(solicitudes);
		recobriment.peticionSincrona(peticion);
	}

	private Element createDatosEspecificos() throws Exception {
		DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
		fac.setNamespaceAware(true);
		Document doc = fac.newDocumentBuilder().newDocument();
		Element datosEspecificos = doc.createElement("DatosEspecificos");
		//datosEspecificos.setAttribute("xmlns", XMLNS_DATOS_ESPECIFICOS_V2);
		Element solicitud = doc.createElement("Solicitud");
		Element fechaNacimientoTitular = doc.createElement("FechaNacimientoTitular");
		fechaNacimientoTitular.setTextContent("31/03/1978");
		solicitud.appendChild(fechaNacimientoTitular);
		Element idTutor = doc.createElement("IDTutor");
		Element tipoDocumentacion = doc.createElement("TipoDocumentacion");
		tipoDocumentacion.setTextContent("NIF");
		idTutor.appendChild(tipoDocumentacion);
		Element documentacion = doc.createElement("Documentacion");
		documentacion.setTextContent("43120476F");
		idTutor.appendChild(documentacion);
		solicitud.appendChild(idTutor);
		datosEspecificos.appendChild(solicitud);
		datosEspecificos.appendChild(solicitud);
		doc.appendChild(datosEspecificos);
		return doc.getDocumentElement();
	}

	private class LogMessageHandler implements SOAPHandler<SOAPMessageContext> {
		public boolean handleMessage(SOAPMessageContext messageContext) {
			log(messageContext);
			return true;
		}
		public Set<QName> getHeaders() {
			return Collections.emptySet();
		}
		public boolean handleFault(SOAPMessageContext messageContext) {
			log(messageContext);
			return true;
		}
		public void close(MessageContext context) {
		}
		private void log(SOAPMessageContext messageContext) {
			SOAPMessage msg = messageContext.getMessage();
			try {
				System.out.print("Missatge SOAP: ");
				msg.writeTo(System.out);
				System.out.println();
			} catch (SOAPException ex) {
				Logger.getLogger(LogMessageHandler.class.getName()).log(Level.SEVERE, null, ex);
			} catch (IOException ex) {
				Logger.getLogger(LogMessageHandler.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

}
