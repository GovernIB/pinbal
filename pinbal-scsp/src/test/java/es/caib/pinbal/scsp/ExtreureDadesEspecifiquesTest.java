/**
 * 
 */
package es.caib.pinbal.scsp;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Test per a extreure les dades dels DatosEspecificos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ExtreureDadesEspecifiquesTest {

	//private static final String XML_DADES_ESPECIFIQUES = "<DatosEspecificos xmlns=\"http://www.map.es/scsp/esquemas/datosespecificos\"><Solicitud><Espanol>s</Espanol><Nacimiento><Fecha>29/09/75</Fecha></Nacimiento></Solicitud><SolicitanteDatos><Tipo>app</Tipo></SolicitanteDatos></DatosEspecificos>";
	//private static final String XML_DADES_ESPECIFIQUES = "<Respuesta xmlns=\"http://www.map.es/scsp/esquemas/V2/respuesta\"><Atributos><IdPeticion>PBL0000000000405</IdPeticion><NumElementos>1</NumElementos><TimeStamp>2013-05-23T16:48:37.602+02:00</TimeStamp><Estado><CodigoEstado>0003</CodigoEstado><CodigoEstadoSecundario>2</CodigoEstadoSecundario><LiteralError>Peticion procesada correctamente.</LiteralError><TiempoEstimadoRespuesta>0</TiempoEstimadoRespuesta></Estado><CodigoCertificado>pruebaPMI</CodigoCertificado></Atributos><Transmisiones><TransmisionDatos><DatosGenericos><Emisor><NifEmisor>S0711001H</NifEmisor><NombreEmisor>CAIB</NombreEmisor></Emisor><Solicitante><IdentificadorSolicitante>B07167448</IdentificadorSolicitante><NombreSolicitante>Limit Tecnologies</NombreSolicitante><Finalidad>ProvaConcepte#::##::#1234</Finalidad><Consentimiento>Si</Consentimiento><Funcionario><NombreCompletoFuncionario>Sion Andreu</NombreCompletoFuncionario><NifFuncionario>97669911C</NifFuncionario></Funcionario></Solicitante><Titular><TipoDocumentacion>NIF</TipoDocumentacion><Documentacion>12345678Z</Documentacion></Titular><Transmision><CodigoCertificado>pruebaPMI</CodigoCertificado><IdSolicitud>PBL0000000000405</IdSolicitud><IdTransmision>T230516400</IdTransmision><FechaGeneracion>2013-05-23T16:48:37.593+02:00</FechaGeneracion></Transmision></DatosGenericos><ns:DatosEspecificos xmlns:ns=\"http://www.map.es/scsp/esquemas/datosespecificos\"><ns:Solicitud><ns:Tutor>SI</ns:Tutor><ns:Provincia><ns:Nombre>Illes Balears</ns:Nombre><ns:Codigo>07</ns:Codigo></ns:Provincia><ns:Municipio><ns:Nombre>Manacor</ns:Nombre><ns:Codigo>033</ns:Codigo></ns:Municipio></ns:Solicitud></ns:DatosEspecificos></TransmisionDatos></Transmisiones></Respuesta>";
	private static final String XML_DADES_ESPECIFIQUES = "<Respuesta xmlns=\"http://www.map.es/scsp/esquemas/V2/respuesta\"><Atributos><IdPeticion>PINBAL0000000180</IdPeticion><NumElementos>1</NumElementos><TimeStamp>2013-06-03T12:01:43.310+02:00</TimeStamp><Estado><CodigoEstado>0003</CodigoEstado><CodigoEstadoSecundario ></CodigoEstadoSecundario><LiteralError>Tramitada</LiteralError><TiempoEstimadoRespuesta>0</TiempoEstimadoRespuesta></Estado><CodigoCertificado>SVDCTITWS01</CodigoCertificado></Atributos><Transmisiones><TransmisionDatos><DatosGenericos><Emisor><NifEmisor>S2826053G</NifEmisor><NombreEmisor>Catastro</NombreEmisor></Emisor><Solicitante><IdentificadorSolicitante>S0711001H</IdentificadorSolicitante><NombreSolicitante>Govern de les Illes Balears</NombreSolicitante><Finalidad>Test</Finalidad><Consentimiento>Si</Consentimiento></Solicitante><Titular><TipoDocumentacion>NIF</TipoDocumentacion><Documentacion>02871082S</Documentacion></Titular><Transmision><CodigoCertificado>SVDCTITWS01</CodigoCertificado><IdSolicitud>PINBAL0000000180</IdSolicitud><IdTransmision>PRE0000000012593</IdTransmision><FechaGeneracion>2013-06-03T12:01:43.307+02:00</FechaGeneracion></Transmision></DatosGenericos><DatosEspecificos xmlns=\"http://www.map.es/scsp/esquemas/datosespecificos\"><DatosSalida><pdf>1234</pdf></DatosSalida><Estado><CodigoEstado>0003</CodigoEstado><CodigoEstadoSecundario ></CodigoEstadoSecundario><LiteralError>TRAMITADA</LiteralError></Estado></DatosEspecificos></TransmisionDatos></Transmisiones></Respuesta>";

	public static void main(String[] args) {
		try {
			new ExtreureDadesEspecifiquesTest().test();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void test() throws Exception {
		Map<String, String> dades = new HashMap<String, String>();
		Document doc = xmlToDocument(
				new ByteArrayInputStream(XML_DADES_ESPECIFIQUES.getBytes()));
		NodeList nl = doc.getElementsByTagNameNS("*", "DatosEspecificos");
		if (nl.getLength() > 0) {
			//NodeList nodes = doc.getChildNodes();
			//NodeList nodes = nl.item(0).getChildNodes();
			List<String> path = new ArrayList<String>();
			recorrerDocument(
					nl.item(0),
					path,
					dades);
			for (String clau: dades.keySet())
				System.out.println(">>> " + clau + ": " + dades.get(clau));
		}
	}

	public void recorrerDocument(Node node, List<String> path, Map<String, String> dades) {
		NodeList fills = node.getChildNodes();
		for (int i = 0; i < fills.getLength(); i++) {
			Node fill = fills.item(i);
			path.add(node.getNodeName());
			//path.add(node.getLocalName());
			if (fill.hasChildNodes())
				recorrerDocument(fill, path, dades);
			else
				dades.put(pathToString(path), node.getTextContent());
			path.remove(path.size() - 1);
		}
	}

	public String pathToString(List<String> path) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0 ; i < path.size(); i++) {
			sb.append("/");
			sb.append(path.get(i));
		}
		return sb.toString();
	}

	public Document xmlToDocument(InputStream is) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory
				.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = null;
		builder = factory.newDocumentBuilder();
		Document doc = builder.parse(is);
		is.close();
		return doc;
	}

}
