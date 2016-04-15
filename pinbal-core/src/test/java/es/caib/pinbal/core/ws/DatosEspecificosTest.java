/**
 * 
 */
package es.caib.pinbal.core.ws;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * Test de transformació dels datosEspecificos al recobriment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DatosEspecificosTest {

	private static final String DATOS_ESPECIFICOS = "<ns2:DatosEspecificos xmlns:ns2=\"http://intermediacion.redsara.es/scsp/esquemas/datosespecificos\"><ns2:Cabecera xmlns:ns1=\"http://intermediacion.redsara.es/scsp/esquemas/V3/respuesta\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><ns2:CodRet>1000</ns2:CodRet><ns2:DescripcionError/><ns2:Referencia>81651069052</ns2:Referencia><ns2:FechaEmision>2016-04-08</ns2:FechaEmision><ns2:Ejercicio>2013</ns2:Ejercicio><ns2:TipoRespuesta>IR</ns2:TipoRespuesta></ns2:Cabecera><ns2:irpf><ns2:CabeceraRenta><ns2:NifSolicitante>12345678Z</ns2:NifSolicitante><ns2:NombreSolicitante>COMUNITAT AUTONOMA DE LES ILLES BALEARS</ns2:NombreSolicitante><ns2:PrimerDeclarante>12345678Z PEP POMAR1</ns2:PrimerDeclarante><ns2:SegundoTitular>12345678Z PEP POMAR2</ns2:SegundoTitular><ns2:Modelo>100</ns2:Modelo><ns2:Tributacion>CONJUNTA</ns2:Tributacion><ns2:OrigenDatos>PARTIDAS: ORIGINALES CONTRIBUYENTE</ns2:OrigenDatos></ns2:CabeceraRenta><ns2:NivelRenta><ns2:NRLiteral>NIVEL DE RENTA</ns2:NRLiteral><ns2:NREnteros>12487</ns2:NREnteros><ns2:NRDecimales>39</ns2:NRDecimales></ns2:NivelRenta><ns2:DatosEconomicos><ns2:DECasilla>366</ns2:DECasilla><ns2:DEEnteros>12487</ns2:DEEnteros><ns2:DEDecimales>39</ns2:DEDecimales></ns2:DatosEconomicos><ns2:DatosEconomicos><ns2:DECasilla>374</ns2:DECasilla><ns2:DEEnteros>0</ns2:DEEnteros><ns2:DEDecimales>0</ns2:DEDecimales></ns2:DatosEconomicos><ns2:DatosEconomicos><ns2:DECasilla>411</ns2:DECasilla><ns2:DEEnteros>9087</ns2:DEEnteros><ns2:DEDecimales>39</ns2:DEDecimales></ns2:DatosEconomicos><ns2:DatosEconomicos><ns2:DECasilla>419</ns2:DECasilla><ns2:DEEnteros>0</ns2:DEEnteros><ns2:DEDecimales>0</ns2:DEDecimales></ns2:DatosEconomicos><ns2:DatosEconomicos><ns2:DECasilla>433</ns2:DECasilla><ns2:DEEnteros>19125</ns2:DEEnteros><ns2:DEDecimales>0</ns2:DEDecimales></ns2:DatosEconomicos><ns2:DatosCola><ns2:DCDatosPersonales><ns2:DCEstadoCivil><ns2:DCFecha>31-12-2013</ns2:DCFecha><ns2:DCContenido>CASADO</ns2:DCContenido></ns2:DCEstadoCivil><ns2:DCFechaNac>15-01-1974</ns2:DCFechaNac></ns2:DCDatosPersonales><ns2:DCDatosConyuge><ns2:DCFechaNac>14-05-1984</ns2:DCFechaNac></ns2:DCDatosConyuge><ns2:DCDatosHijos><ns2:DCNumHijos><ns2:DCNombreHijo>12345678Z PEP POMAR3</ns2:DCNombreHijo><ns2:DCFechaNacim>05-08-2003</ns2:DCFechaNacim></ns2:DCNumHijos><ns2:DCNumHijos><ns2:DCNombreHijo>12345678Z PEP POMAR4</ns2:DCNombreHijo><ns2:DCFechaNacim>03-07-2005</ns2:DCFechaNacim></ns2:DCNumHijos><ns2:DCNumHijos><ns2:DCNombreHijo>12345678Z PEP POMAR5</ns2:DCNombreHijo><ns2:DCFechaNacim>09-01-2008</ns2:DCFechaNacim></ns2:DCNumHijos><ns2:DCNumHijos><ns2:DCNombreHijo>12345678Z PEP POMAR6</ns2:DCNombreHijo><ns2:DCFechaNacim>30-10-2012</ns2:DCFechaNacim></ns2:DCNumHijos></ns2:DCDatosHijos><ns2:DCDatosVivienda><ns2:DCNumViviendas><ns2:DCContrib>DECLARANTE</ns2:DCContrib><ns2:DCRefCatastr>0700000000000000001DF</ns2:DCRefCatastr><ns2:DCSituacion>Territorio español excepto País Vasco y Navarra</ns2:DCSituacion><ns2:DCTitularidad>ARRENDAMIENTO</ns2:DCTitularidad></ns2:DCNumViviendas></ns2:DCDatosVivienda></ns2:DatosCola></ns2:irpf></ns2:DatosEspecificos>";
	private static final String XMLNS_DATOS_ESPECIFICOS_V2 = "http://www.map.es/scsp/esquemas/datosespecificos";
	private static final String XMLNS_DATOS_ESPECIFICOS_V3 = "http://intermediacion.redsara.es/scsp/esquemas/datosespecificos";

	public static void main(String[] args) {
		try {
			new DatosEspecificosTest().test();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void test() throws Exception {
		Element element = processarDatosEspecificos(stringToElement(DATOS_ESPECIFICOS));
		String str = elementToString(element);
		System.out.println(">>> XML: " + str);
	}

	private Element processarDatosEspecificos(Element datosEspecificos) throws TransformerException, ParserConfigurationException, SAXException, IOException {
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
		return stringToElement(
				datosEspecificosSenseNs.substring(0, "<DatosEspecificos ".length()) +
				xmlns + 
				datosEspecificosSenseNs.substring("<DatosEspecificos ".length()));
	}

	private String removeXmlStringNamespaceAndPreamble(String xml) {
		return xml.replaceAll("(<\\?[^<]*\\?>)?", ""). /* remove preamble */
				replaceAll("xmlns.*?(\"|\').*?(\"|\')", ""). /* remove xmlns declaration */
				replaceAll("(<)(\\w+:)(.*?>)", "$1$3"). /* remove opening tag prefix */
				replaceAll("(</)(\\w+:)(.*?>)", "$1$3"); /* remove closing tags prefix */
	}

	private Element stringToElement(String xml) throws TransformerException, ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		dbFactory.setNamespaceAware(false);
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

}
