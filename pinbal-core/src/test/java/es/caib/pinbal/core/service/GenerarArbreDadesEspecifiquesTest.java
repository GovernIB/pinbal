/**
 * 
 */
package es.caib.pinbal.core.service;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.context.support.ResourceBundleMessageSource;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import es.caib.pinbal.core.helper.JustificantPlantillaHelper;
import es.caib.pinbal.scsp.JustificantArbreHelper;
import es.caib.pinbal.scsp.JustificantArbreHelper.ElementArbre;
import es.scsp.bean.common.DatosGenericos;
import es.scsp.bean.common.Transmision;
import es.scsp.bean.common.TransmisionDatos;

/**
 * Test per a extreure les dades dels DatosEspecificos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class GenerarArbreDadesEspecifiquesTest {

	//private static final String XML_DADES_ESPECIFIQUES = "<ns1:DatosEspecificos xmlns:ns1=\"http://www.map.es/scsp/esquemas/datosespecificos\"><ns1:EstadoResultado><ns1:CodigoEstado>0003</ns1:CodigoEstado><ns1:CodigoEstadoSecundario /><ns1:LiteralError>TRAMITADA</ns1:LiteralError></ns1:EstadoResultado><ns1:DatosTitular><ns1:FechaNacimiento>19/11/1983</ns1:FechaNacimiento></ns1:DatosTitular><ns1:ListaTitulos><ns1:DatosTitulacion><ns1:DatosCentro><ns1:Universidad>Universidad de Alcalá</ns1:Universidad><ns1:Centro>Escuela Politécnica Superior</ns1:Centro></ns1:DatosCentro><ns1:DatosTitulo><ns1:NombreCarrera>Ingeniero Técnico en Informática de Gestión</ns1:NombreCarrera><ns1:TipoTitulo>Ingeniero Técnico</ns1:TipoTitulo><ns1:FechaFinalizacion>01/06/1999</ns1:FechaFinalizacion><ns1:NumTitulo>2000071293</ns1:NumTitulo><ns1:RegistroUniv>22943</ns1:RegistroUniv></ns1:DatosTitulo></ns1:DatosTitulacion><ns1:DatosTitulacion><ns1:DatosCentro><ns1:Universidad>Universidad Politécnica de Madrid</ns1:Universidad><ns1:Centro>Facultad de Informática</ns1:Centro></ns1:DatosCentro><ns1:DatosTitulo><ns1:NombreCarrera>Ingeniero en Informática</ns1:NombreCarrera><ns1:TipoTitulo>Ingeniero</ns1:TipoTitulo><ns1:FechaFinalizacion>26/06/2003</ns1:FechaFinalizacion><ns1:NumTitulo>2005088985</ns1:NumTitulo><ns1:RegistroUniv>64107</ns1:RegistroUniv></ns1:DatosTitulo></ns1:DatosTitulacion></ns1:ListaTitulos></ns1:DatosEspecificos>";
	private static final String XML_DADES_ESPECIFIQUES = "<DatosEspecificos xmlns=\"http://www.map.es/scsp/esquemas/datosespecificos\"><Estado><CodigoEstado>0003</CodigoEstado><CodigoEstadoSecundario>CS_1</CodigoEstadoSecundario><LiteralError>TRAMITADA</LiteralError></Estado><Solicitud><Tutor>NO</Tutor><Provincia><Codigo>07</Codigo><Nombre>Balears</Nombre></Provincia><Municipio><Codigo>007</Codigo><Nombre>Palma</Nombre></Municipio><FechaNacimiento>12122012</FechaNacimiento><Nombre>Toni</Nombre><Apellido1>Gallard</Apellido1><Apellido2>Bauza</Apellido2></Solicitud><Resultados><Resultado><TipoDocumentacion>DNI</TipoDocumentacion><Documentacion>12345678Z</Documentacion><Nombre>Joan</Nombre><Apellido1>Bestard</Apellido1><Apellido2>Gonza</Apellido2><NombreCompleto>Joan Bestard Gonza</NombreCompleto><Domicilios><Domicilio><Desde>14032001</Desde><Hasta>14042004</Hasta><Provincia><Codigo>07</Codigo><Nombre>Balears</Nombre></Provincia><Municipio><Codigo>007</Codigo><Nombre>Palma</Nombre></Municipio><EntColectiva><Codigo>3</Codigo><Nombre>nose</Nombre></EntColectiva><EntSingular><Codigo>4</Codigo><Nombre>tampoco_nose</Nombre></EntSingular><Nucleo><Codigo>5</Codigo><Nombre>nose</Nombre></Nucleo><CodUnidadPoblacional>6</CodUnidadPoblacional><Direccion><Via><Codigo>12345</Codigo><Tipo>54321</Tipo><Nombre>Gran Via</Nombre></Via><Numero><Calificador>A</Calificador><Valor>1234</Valor></Numero><NumeroSuperior><Calificador ></Calificador><Valor ></Valor></NumeroSuperior><Kmt ></Kmt><Hmt ></Hmt><Bloque ></Bloque><Portal ></Portal><Escalera>3A</Escalera><Planta>45C</Planta><Puerta>1234</Puerta><CodPostal>07001</CodPostal></Direccion></Domicilio></Domicilios></Resultado></Resultados></DatosEspecificos>";
	private static final String CODIGO_CERTIFICADO = "testEMISOR";

	public static void main(String[] args) {
		try {
			new GenerarArbreDadesEspecifiquesTest().test();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void test() throws Exception {
		TransmisionDatos transmision = new TransmisionDatos();
		DatosGenericos datosGenericos = new DatosGenericos();
		Transmision trans = new Transmision();
		trans.setCodigoCertificado(CODIGO_CERTIFICADO);
		datosGenericos.setTransmision(trans);
		transmision.setDatosGenericos(datosGenericos);
		Document doc = xmlToDocument(
				new ByteArrayInputStream(XML_DADES_ESPECIFIQUES.getBytes()));
		NodeList nodes = doc.getChildNodes();
		transmision.setDatosEspecificos(nodes.item(0));
		JustificantArbreHelper arbreHelper = new JustificantArbreHelper();
		arbreHelper.setMessageSource(new ResourceBundleMessageSource());
		ElementArbre arbre = arbreHelper.generarArbre(
				transmision,
				"PBL0000000000236",
				new Locale("ca", "ES"));
		arbreHelper.imprimirJustificantStdout(arbre);
		JustificantPlantillaHelper justificantHelper = new JustificantPlantillaHelper();
		ResourceBundleMessageSource ms = new ResourceBundleMessageSource();
		ms.setBasename("es/caib/pinbal/core/i18n/messages");
		justificantHelper.setMessageSource(ms);
		FileOutputStream fos = new FileOutputStream("justificant.odt");
		justificantHelper.generarAmbPlantillaFreemarker(
				arbre,
				"Bon dia",
				null,
				new Locale("ca", "ES"),
				fos);
		fos.close();
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
