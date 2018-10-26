/**
 * 
 */
package es.caib.pinbal.ws.client.test;

import java.net.MalformedURLException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import es.caib.pinbal.ws.client.RecobrimentClientFactory;
import es.caib.pinbal.ws.recobriment.Atributos;
import es.caib.pinbal.ws.recobriment.Consentimiento;
import es.caib.pinbal.ws.recobriment.DatosGenericos;
import es.caib.pinbal.ws.recobriment.Funcionario;
import es.caib.pinbal.ws.recobriment.Peticion;
import es.caib.pinbal.ws.recobriment.Procedimiento;
import es.caib.pinbal.ws.recobriment.Recobriment;
import es.caib.pinbal.ws.recobriment.Respuesta;
import es.caib.pinbal.ws.recobriment.Solicitante;
import es.caib.pinbal.ws.recobriment.SolicitudTransmision;
import es.caib.pinbal.ws.recobriment.Solicitudes;
import es.caib.pinbal.ws.recobriment.Titular;

/**
 * Test per al servei de recobriment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RecobrimentClientTest {

	private static final String ENDPOINT_URL = "http://localhost:8080/pinbal/ws/recobriment";
	private static final String USERNAME = "pinbal";
	private static final String PASSWORD = "pinbal";

	private static final String CODIGO_CERTIFICADO = "VDRSFWS02";
	private static final Consentimiento CONSENTIMIENTO = Consentimiento.LEY;
	private static final String FINALIDAD = "Test recobriment";
	private static final String FUNCIONARIO_NIF = "97669911C";
	private static final String FUNCIONARIO_NOMBRE = "MPR TESTER";
	private static final String SOLICITANTE_ID = "G07896004";
	private static final String SOLICITANTE_NOMBRE = "Fundació BIT";
	private static final String PROCEDIMIENTO_CODIGO = "IBIT_20101223_PRUEBA";
	private static final String PROCEDIMIENTO_NOMBRE = "PROCEDIMIENTO DE PRUEBA FUNDACION IBIT";
	private static final String UNIDAD_CODIGO = null;
	private static final String UNIDAD_NOMBRE = "Unitat de test";
	private static final String EXPEDIENTE_ID = null;

	public static void main(String[] args) throws MalformedURLException, ParserConfigurationException {
		Recobriment client = RecobrimentClientFactory.getWsClient(
				ENDPOINT_URL,
				USERNAME,
				PASSWORD);
		Peticion peticion = new Peticion();
		Atributos atributos = new Atributos();
		peticion.setAtributos(atributos);
		atributos.setNumElementos("1");
		atributos.setCodigoCertificado(CODIGO_CERTIFICADO);
		Solicitudes solicitudes = new Solicitudes();
		SolicitudTransmision solicitudTransmision = new SolicitudTransmision();
		DatosGenericos datosGenericos = new DatosGenericos();
		Solicitante solicitante = new Solicitante();
		solicitante.setConsentimiento(CONSENTIMIENTO);
		solicitante.setFinalidad(FINALIDAD);
		Funcionario funcionario = new Funcionario();
		funcionario.setNifFuncionario(FUNCIONARIO_NIF);
		funcionario.setNombreCompletoFuncionario(FUNCIONARIO_NOMBRE);
		solicitante.setFuncionario(funcionario);
		solicitante.setIdentificadorSolicitante(SOLICITANTE_ID);
		solicitante.setNombreSolicitante(SOLICITANTE_NOMBRE);
		Procedimiento procedimiento = new Procedimiento();
		procedimiento.setCodProcedimiento(PROCEDIMIENTO_CODIGO);
		procedimiento.setNombreProcedimiento(PROCEDIMIENTO_NOMBRE);
		solicitante.setProcedimiento(procedimiento);
		solicitante.setCodigoUnidadTramitadora(UNIDAD_CODIGO);
		solicitante.setUnidadTramitadora(UNIDAD_NOMBRE);
		solicitante.setIdExpediente(EXPEDIENTE_ID);
		datosGenericos.setSolicitante(solicitante);
		Titular titular = new Titular();
		datosGenericos.setTitular(titular);
		solicitudTransmision.setDatosGenericos(datosGenericos);
		solicitudTransmision.setDatosEspecificos(generarDatosEspecificos());
		solicitudes.getSolicitudTransmision().add(solicitudTransmision);
		peticion.setSolicitudes(solicitudes);
		Respuesta respuesta = client.peticionSincrona(peticion);
		System.out.println("Petició SCSP enviada (idPeticion=" + respuesta.getAtributos().getIdPeticion() + ")");
	}

	private static Element generarDatosEspecificos() throws ParserConfigurationException {
		DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
		fac.setNamespaceAware(true);
		Document doc = fac.newDocumentBuilder().newDocument();
		Element datosEspecificos = doc.createElement("DatosEspecificos");
		Element consulta = doc.createElement("Consulta");
		Element provincia = doc.createElement("CodigoProvincia");
		provincia.setTextContent("07");
		consulta.appendChild(provincia);
		datosEspecificos.appendChild(consulta);
		doc.appendChild(datosEspecificos);
		return doc.getDocumentElement();
	}

}
