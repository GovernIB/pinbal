/**
 * 
 */
package es.caib.pinbal.core.ws;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import es.scsp.bean.common.Atributos;
import es.scsp.bean.common.Consentimiento;
import es.scsp.bean.common.DatosGenericos;
import es.scsp.bean.common.Funcionario;
import es.scsp.bean.common.Peticion;
import es.scsp.bean.common.Procedimiento;
import es.scsp.bean.common.Solicitante;
import es.scsp.bean.common.SolicitudTransmision;
import es.scsp.bean.common.Solicitudes;
import es.scsp.bean.common.TipoDocumentacion;
import es.scsp.bean.common.Titular;


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
					TipoDocumentacion.NIF,
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
			TipoDocumentacion titularTipoDocumentacion,
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
		Peticion peticion = new Peticion();
		Atributos atributos = new Atributos();
		atributos.setCodigoCertificado(codigoCertificado);
		peticion.setAtributos(atributos);
		Solicitudes solicitudes = new Solicitudes();
		ArrayList<SolicitudTransmision> solicitudesList = new ArrayList<SolicitudTransmision>();
		SolicitudTransmision solicitudTransmision = new SolicitudTransmision();
		DatosGenericos datosGenericos = new DatosGenericos();
		Solicitante solicitante = new Solicitante();
		solicitante.setIdentificadorSolicitante(solicitanteId);
		solicitante.setNombreSolicitante(solicitanteNombre);
		Procedimiento procedimiento = new Procedimiento();
		procedimiento.setCodProcedimiento(procedimentCodi);
		procedimiento.setNombreProcedimiento(procedimentNom);
		solicitante.setProcedimiento(procedimiento);
		Funcionario funcionario = new Funcionario();
		funcionario.setNombreCompletoFuncionario(funcionarioNombre);
		funcionario.setNifFuncionario(funcionarioNif);
		solicitante.setFuncionario(funcionario);
		solicitante.setFinalidad(procedimentNom);
		solicitante.setUnidadTramitadora(solicitanteUnidadTramitadora);
		solicitante.setConsentimiento(Consentimiento.Si);
		datosGenericos.setSolicitante(solicitante);
		Titular titular = new Titular();
		titular.setTipoDocumentacion(titularTipoDocumentacion);
		titular.setDocumentacion(titularDocumentacion);
		datosGenericos.setTitular(titular);
		solicitudTransmision.setDatosGenericos(datosGenericos);
		solicitudTransmision.setDatosEspecificos(createDatosEspecificos());
		solicitudesList.add(solicitudTransmision);
		solicitudes.setSolicitudTransmision(solicitudesList);
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
