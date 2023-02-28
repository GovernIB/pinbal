/**
 * 
 */
package es.caib.pinbal.scsp;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.apache.axis2.util.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author josepg
 *
 */
public class RecobrimentTest {

	private static final String XMLNS_DATOS_ESPECIFICOS_V2 = "http://www.map.es/scsp/esquemas/datosespecificos";
	//private static final String XMLNS_DATOS_ESPECIFICOS_V3 = "http://www.map.es/scsp/esquemas/datosespecificos";

	//private static final String RECOBRIMENT_URL = "http://localhost:8180/pinbalrec/ws";
	private static final String RECOBRIMENT_URL = "http://localhost:8080/pinbal/ws/recobriment";
	private static final String RECOBRIMENT_AUTH_USERNAME = "tomeud";
	private static final String RECOBRIMENT_AUTH_PASSWORD = "tomeud15";



	public static void main(String[] args) {
		try {
			RecobrimentTest recobrimentTest = new RecobrimentTest();
			OMElement peticion = recobrimentTest.crearPeticion(
					"VDRSFWS02",
					"G07896004",
					"FUNDACIO IBIT",
					"IBIT_20101223_PRUEBA",
					"PROCEDIMIENTO DE PRUEBA FUNDACION IBIT",
					"MPR TESTER",
					"97669911C", //"97669911C", //"12345678Z",
					"02634348C",
					"DNI");
			System.out.println(">>> Petició: " + peticion.toString());
			recobrimentTest.revisarPeticion(peticion.toString());
			OMElement respuesta = recobrimentTest.enviarPeticion(peticion);
			System.out.println(">>> Resposta: " + respuesta.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}



	private void revisarPeticion(String xml) throws Exception {
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(false); 
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));
		System.out.println(">>> CodigoCertificado: " + getNodeTextPerXpath(doc, "//Atributos/CodigoCertificado"));
		System.out.println(">>> CodProcedimiento: " + getNodeTextPerXpath(doc, "//PeticionSincrona/Solicitudes/SolicitudTransmision/DatosGenericos/Solicitante/Procedimiento/CodProcedimiento"));
		System.out.println(">>> NombreProcedimiento: " + getNodeTextPerXpath(doc, "//PeticionSincrona/Solicitudes/SolicitudTransmision/DatosGenericos/Solicitante/Procedimiento/NombreProcedimiento"));
		System.out.println(">>> NombreCompletoFuncionario: " + getNodeTextPerXpath(doc, "//PeticionSincrona/Solicitudes/SolicitudTransmision/DatosGenericos/Solicitante/Funcionario/NombreCompletoFuncionario"));
		System.out.println(">>> NifFuncionario: " + getNodeTextPerXpath(doc, "//PeticionSincrona/Solicitudes/SolicitudTransmision/DatosGenericos/Solicitante/Funcionario/NifFuncionario"));
	}

	private OMElement crearPeticion(
			String certificado,
			String solicitanteCif,
			String solicitanteNombre,
			String procedimentCodi,
			String procedimentNom,
			String funcionarioNombre,
			String funcionarioNif,
			String titularDocumentacion,
			String titularTipoDocumentacion) throws Exception {
		OMFactory fac = OMAbstractFactory.getOMFactory();
		//Declaración del NAMESPACE así como la asignación del PREFIJO empleado.
		OMNamespace omNs = fac.createOMNamespace(
				//"http://intermediacion.redsara.es/scsp/esquemas/ws/peticion",
				"http://www.caib.es/pinbal/ws/recobriment",
				"ws");
		//Creación del elemento <PeticionSincrona> usando en NAMESPACE y PREFIJO declarado.
		OMElement peticionSincrona = fac.createOMElement("peticionSincrona", omNs);
		//Creación del elemento <PeticionSincrona> usando en NAMESPACE y PREFIJO declarado.
		OMElement peticion = fac.createOMElement("arg0", null);
		//Creación del elemento <Atributos>.
		OMElement atributos = fac.createOMElement("Atributos", omNs);
		//Creación del elemento <CodigoCertificado>.
		OMElement codigoCertificado = fac.createOMElement("CodigoCertificado", omNs);
		//Asignación del valor 'CDISFWS01' para el elemento <CodigoCertificado>.
		codigoCertificado.setText(certificado);
		//Añade el elemento <CodigoCertificado> como nodo hijo del elemento <Atributos>.
		atributos.addChild(codigoCertificado);
		//Creación del elemento <Solicitante>.
		OMElement solicitante = fac.createOMElement("Solicitante", omNs);
		//Creación del elemento <IdentificadorSolicitante>.
		OMElement identificadorSolicitante = fac.createOMElement("IdentificadorSolicitante", omNs);
		//Asignación del valor 'S2811001C' para el elemento <IdentificadorSolicitante>.
		identificadorSolicitante.setText(solicitanteCif);
		//Creación del elemento <NombreSolicitante>.
		OMElement nombreSolicitante = fac.createOMElement("NombreSolicitante", omNs);
		//Asignación del valor 'MPR' para el elemento <NombreSolicitante>.
		nombreSolicitante.setText(solicitanteNombre);
		//Creación del elemento <Finalidad>.
		OMElement finalidad = fac.createOMElement("Finalidad", omNs);
		//Asignación del valor 'PRUEBA RECUBRIMIENTO' para el elemento <Finalidad>.
		finalidad.setText(procedimentNom);
		//Creación del elemento <Consentimiento>.
		OMElement consentimiento = fac.createOMElement("Consentimiento", omNs);
		//Asignación del valor 'Si' para el elemento <Consentimiento>.
		consentimiento.setText("Si");
		//Creación del elemento <Procedimiento>.
		OMElement procedimiento = fac.createOMElement("Procedimiento", omNs);
		OMElement codProcedimiento = fac.createOMElement("CodProcedimiento", omNs);
		codProcedimiento.setText(procedimentCodi);
		procedimiento.addChild(codProcedimiento);
		OMElement nombreProcedimiento = fac.createOMElement("NombreProcedimiento", omNs);
		nombreProcedimiento.setText(procedimentNom);
		procedimiento.addChild(nombreProcedimiento);
		//Creación del elemento <Funcionario>.
		OMElement funcionario = fac.createOMElement("Funcionario", omNs);
		OMElement nombreCompletoFuncionario = fac.createOMElement("NombreCompletoFuncionario", omNs);
		nombreCompletoFuncionario.setText(funcionarioNombre);
		funcionario.addChild(nombreCompletoFuncionario);
		OMElement nifFuncionario = fac.createOMElement("NifFuncionario", omNs);
		nifFuncionario.setText(funcionarioNif);
		funcionario.addChild(nifFuncionario);
		//Se añaden estos 6 elementos como nodos hijos del elemento <Solicitante>.
		solicitante.addChild(identificadorSolicitante);
		solicitante.addChild(nombreSolicitante);
		solicitante.addChild(finalidad);
		solicitante.addChild(consentimiento);
		solicitante.addChild(procedimiento);
		solicitante.addChild(funcionario);
		//Creación del elemento <Titular>.
		OMElement titular = fac.createOMElement("Titular", omNs);
		//Creación del elemento <TipoDocumentacion>.
		OMElement tipoDocumentacion = fac.createOMElement("TipoDocumentacion", omNs);
		//Asignación del valor 'NIF' para el elemento<TipoDocumentacion>.
		tipoDocumentacion.setText(titularTipoDocumentacion);
		//Creación del elemento <Documentacion>.
		OMElement documentacion = fac.createOMElement("Documentacion", omNs);
		//Asignación del valor '10000320N' para el elemento <Documentacion>.
		documentacion.setText(titularDocumentacion);
		//Se añaden estos 2 elementos como nodos hijos del elemento <Titular>.
		titular.addChild(tipoDocumentacion);
		titular.addChild(documentacion);
		//Creación del elemento <DatosGenericos>.
		OMElement datosGenericos = fac.createOMElement("DatosGenericos", omNs);
		//Se añaden estos 2 elementos como nodos hijos del elemento <DatosGenericos>.
		datosGenericos.addChild(solicitante);
		datosGenericos.addChild(titular);
		//Creación del elemento <SolicitudTransmision>.
		OMElement solicitudTransmision = fac.createOMElement("SolicitudTransmision", omNs);
		//Se añade el elemento <DatosGenericos> como nodo hijo <SolicitudTransmision>.
		solicitudTransmision.addChild(datosGenericos);
		//Se añade el elemento <DatosEspecificos> como nodo hijo <SolicitudTransmision>.
		Element datosEspecificos = createDatosEspecificos();
		if (datosEspecificos!=null) {
			OMElement datosEspecificosOM = XMLUtils.toOM(datosEspecificos);
			solicitudTransmision.addChild(datosEspecificosOM);
			solicitudTransmision.addChild(datosEspecificosOM);
		}
		//Creación del elemento <Solicitudes>.
		OMElement solicitudes = fac.createOMElement("Solicitudes", omNs);
		//Se añade el elemento <SolicitudTransmision> como nodo hijo <Solicitudes>.
		solicitudes.addChild(solicitudTransmision);
		//Se añaden estos 2 elementos como nodos hijos del elemento <PeticionSincrona>
		peticion.addChild(atributos);
		peticion.addChild(solicitudes);
		peticionSincrona.addChild(peticion);
		return peticionSincrona;
	}
	private Element createDatosEspecificos() throws Exception {
		DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
		fac.setNamespaceAware(true);
		Document doc = fac.newDocumentBuilder().newDocument();
		Element datosEspecificos = doc.createElement("DatosEspecificos");
		datosEspecificos.setAttribute("xmlns", XMLNS_DATOS_ESPECIFICOS_V2);

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

	private OMElement enviarPeticion(OMElement peticion) throws Exception {
		//Se instancia un objeto de la clase ServiceClient.
		ServiceClient client = new ServiceClient();
		//Se instancia un objeto de la clase Options.
		Options opts = new Options();
		//Se asigna el tiempo máximo de espera para el cliente.
		opts.setTimeOutInMilliSeconds(300000);
		//Se asigna la URL que usará el cliente WS para consumir el servicio Web.
		opts.setTo(new EndpointReference(RECOBRIMENT_URL));
		//Se asigna el parámetro SOAPACTION.
		opts.setAction("peticionSincrona");
		HttpTransportProperties.Authenticator authenticator = new HttpTransportProperties.Authenticator();
		authenticator.setUsername(RECOBRIMENT_AUTH_USERNAME);
		authenticator.setPassword(RECOBRIMENT_AUTH_PASSWORD);
		opts.setProperty(HTTPConstants.AUTHENTICATE, authenticator);
		//Se asignan las opciones configuradas.
		client.setOptions(opts);
		//Invocación del servicio Web.
		OMElement res = client.sendReceive(peticion);
		//Limpia la configuración creada para este cliente.
		client.cleanup();
		//Libera recursos usados durante la invocación del servicio Web.
		client.cleanupTransport();
		return res;
	}

	private String getNodeTextPerXpath(Document doc, String expression) throws Exception {
		XPath xpath = XPathFactory.newInstance().newXPath();
		XPathExpression expr = xpath.compile(expression);
		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		NodeList nodes = (NodeList)result;
		if (nodes.getLength() == 1)
			return nodes.item(0).getTextContent();
		else 
			return null;
	}

}
