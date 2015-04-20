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

	private static final String ENDPOINT_ADDRESS = "http://localhost:8080/pinbal/ws/recobriment";
	private static final String USERNAME = "tomeud";
	private static final String PASSWORD = "tomeud";

	//private static final String XMLNS_DATOS_ESPECIFICOS_V2 = "http://www.map.es/scsp/esquemas/datosespecificos";
	//private static final String XMLNS_DATOS_ESPECIFICOS_V3 = "http://www.map.es/scsp/esquemas/datosespecificos";



	public static void main(String[] args) {
		try {
			RecobrimentTest recobrimentTest = new RecobrimentTest();
			recobrimentTest.test(
					"VDRSFWS02",
					"G07896004",
					"Govern de les Illes Balears",
					"DGIDT",
					"IBIT_20101223_PRUEBA",
					"PROCEDIMIENTO DE PRUEBA FUNDACION IBIT",
					"Tomeu Domenge",
					"51378267Q",
					TipoDocumentacion.DNI,
					"02634348C");
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
		solicitante.setFinalidad(procedimentCodi + "#::##::#" + procedimentNom);
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
		Element solicitanteDatos = doc.createElement("SolicitanteDatos");
		Element tipo = doc.createElement("Tipo");
		tipo.setTextContent("app");
		solicitanteDatos.appendChild(tipo);
		datosEspecificos.appendChild(solicitanteDatos);
		Element solicitud = doc.createElement("Solicitud");
		Element espanol = doc.createElement("Espanol");
		espanol.setTextContent("s");
		solicitud.appendChild(espanol);
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
