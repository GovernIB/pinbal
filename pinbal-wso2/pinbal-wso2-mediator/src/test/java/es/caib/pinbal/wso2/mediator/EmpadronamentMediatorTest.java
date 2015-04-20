/**
 * 
 */
package es.caib.pinbal.wso2.mediator;

import java.io.ByteArrayInputStream;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMXMLBuilderFactory;
import org.apache.axiom.om.OMXMLParserWrapper;

/**
 * Test per l'EntitatResolver del servei d'empadronament.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EmpadronamentMediatorTest {

	/*
	private static final String XML_PETICIO = "<Peticion xmlns=\"http://www.map.es/scsp/esquemas/V2/peticion\">" +
			"<Atributos><IdPeticion>PBL0000000000282</IdPeticion><NumElementos>1</NumElementos><TimeStamp>2013-03-20T17:05:58.918+01:00</TimeStamp><CodigoCertificado>PROVAEMP</CodigoCertificado></Atributos>" +
			"<Solicitudes>" +
			"<SolicitudTransmision>" +
			"<DatosGenericos><Emisor><NifEmisor>000000003</NifEmisor><NombreEmisor>Ajuntaments</NombreEmisor></Emisor><Solicitante><IdentificadorSolicitante>B07167448</IdentificadorSolicitante><NombreSolicitante>Limit Tecnologies</NombreSolicitante><Finalidad>123#::##::#1234</Finalidad><Consentimiento>Si</Consentimiento><Funcionario><NombreCompletoFuncionario>Sion Andreu</NombreCompletoFuncionario><NifFuncionario>97669911C</NifFuncionario></Funcionario></Solicitante><Titular><TipoDocumentacion>NIF</TipoDocumentacion><Documentacion>12345678Z</Documentacion></Titular><Transmision><CodigoCertificado>PROVAEMP</CodigoCertificado><IdSolicitud>PBL0000000000282</IdSolicitud><IdTransmision>PBL0000000000282</IdTransmision><FechaGeneracion>2013-03-20T17:05:58.903+01:00</FechaGeneracion></Transmision></DatosGenericos>" +
			"<DatosEspecificos xmlns=\"http://www.map.es/scsp/esquemas/datosespecificos\">" +
			"<Solicitud><Municipio><Codigo>033</Codigo></Municipio><Tutor>SI</Tutor><Provincia><Codigo>07</Codigo></Provincia></Solicitud>" +
			"</DatosEspecificos>" +
			"</SolicitudTransmision>" +
			"</Solicitudes>" +
			"</Peticion>";
	*/
	//private static final String XML_PETICIO = "<Peticion xmlns=\"http://www.map.es/scsp/esquemas/V2/peticion\"><Atributos><IdPeticion>PINBAL0000000669</IdPeticion><NumElementos>1</NumElementos><TimeStamp>2013-04-02T08:55:03.235+02:00</TimeStamp><CodigoCertificado>pruebaPMI</CodigoCertificado></Atributos><Solicitudes><SolicitudTransmision><DatosGenericos><Emisor><NifEmisor>S0711001H</NifEmisor><NombreEmisor>CAIB</NombreEmisor></Emisor><Solicitante><IdentificadorSolicitante>G07896004</IdentificadorSolicitante><NombreSolicitante>Fundació BIT</NombreSolicitante><Finalidad>CODSVDR_GBA_20121107#::##::#1234</Finalidad><Consentimiento>Si</Consentimiento><Funcionario><NombreCompletoFuncionario>Daniel Boerner</NombreCompletoFuncionario><NifFuncionario>12345678Z</NifFuncionario></Funcionario></Solicitante><Titular><TipoDocumentacion>NIF</TipoDocumentacion><Documentacion>12345678Z</Documentacion></Titular><Transmision><CodigoCertificado>pruebaPMI</CodigoCertificado><IdSolicitud>PINBAL0000000669</IdSolicitud><IdTransmision>PINBAL0000000669</IdTransmision><FechaGeneracion>2013-04-02T08:55:03.204+02:00</FechaGeneracion></Transmision></DatosGenericos><DatosEspecificos xmlns=\"http://www.map.es/scsp/esquemas/datosespecificos\"><Solicitud><Provincia><Nombre>Illes Balears</Nombre><Codigo>07</Codigo></Provincia><Municipio><Codigo>033</Codigo><Nombre>Manacor</Nombre></Municipio><Tutor>SI</Tutor></Solicitud></DatosEspecificos></SolicitudTransmision></Solicitudes></Peticion>";
	/*private static final String XML_PETICIO = "<Peticion xmlns=\"http://intermediacion.redsara.es/scsp/esquemas/V3/peticion\">" +
			"<Atributos><IdPeticion>PINBAL00000000000000000270</IdPeticion><NumElementos>1</NumElementos><TimeStamp>2014-02-13T14:01:38.813+01:00</TimeStamp><CodigoCertificado>SCDHPAJU</CodigoCertificado></Atributos>" +
			"<Solicitudes>" +
			"<SolicitudTransmision>" +
			"<DatosGenericos><Emisor><NifEmisor>S0711001H</NifEmisor><NombreEmisor>Govern de les Illes Balears</NombreEmisor></Emisor><Solicitante><IdentificadorSolicitante>S0711001H</IdentificadorSolicitante><NombreSolicitante>Govern de les Illes Balears</NombreSolicitante><UnidadTramitadora>DGIDT</UnidadTramitadora><Procedimiento><CodProcedimiento>EC0023SOAD</CodProcedimiento><NombreProcedimiento>Procediment de proves d'escolaritzacio</NombreProcedimiento></Procedimiento><Finalidad>EC0023SOAD#::##::#Test</Finalidad><Consentimiento>Si</Consentimiento><Funcionario><NombreCompletoFuncionario>Antonio Trobat Obrador</NombreCompletoFuncionario><NifFuncionario>43120476F</NifFuncionario></Funcionario></Solicitante><Titular><TipoDocumentacion>NIF</TipoDocumentacion><Documentacion>12345678Z</Documentacion></Titular><Transmision><CodigoCertificado>SCDHPAJU</CodigoCertificado><IdSolicitud>PINBAL00000000000000000270</IdSolicitud><IdTransmision>PINBAL00000000000000000270</IdTransmision><FechaGeneracion>2014-02-13T14:01:38.777+01:00</FechaGeneracion></Transmision></DatosGenericos>" +
			"<DatosEspecificos xmlns=\"http://intermediacion.redsara.es/scsp/esquemas/datosespecificos\">" +
			"<Solicitud>" +
			"<Titular><DatosPersonales><Nombre>Mickey</Nombre><Apellido1>Mouse</Apellido1></DatosPersonales></Titular><MunicipioSolicitud>005</MunicipioSolicitud><ProvinciaSolicitud>07</ProvinciaSolicitud>" +
			"</Solicitud>" +
			"</DatosEspecificos>" +
			"</SolicitudTransmision>" +
			"</Solicitudes>" +
			"</Peticion>";
*/
	//private static final String XML_PETICIO = "<Peticion><Solicitudes><SolicitudTransmision><DatosEspecificos><Solicitud><MunicipioSolicitud>005</MunicipioSolicitud><ProvinciaSolicitud>07</ProvinciaSolicitud></Solicitud></DatosEspecificos></SolicitudTransmision></Solicitudes></Peticion>";
	private static final String XML_PETICIO = 
			"<Peticion xmlns=\"http://intermediacion.redsara.es/scsp/esquemas/V3/peticion\"><Atributos><IdPeticion>PINBAL00000000000000000273</IdPeticion><NumElementos>1</NumElementos><TimeStamp>2014-02-17T09:13:33.483+01:00</TimeStamp><CodigoCertificado>SCDHPAJU</CodigoCertificado></Atributos><Solicitudes><SolicitudTransmision><DatosGenericos><Emisor><NifEmisor>S0711001H</NifEmisor><NombreEmisor>Govern de les Illes Balears</NombreEmisor></Emisor><Solicitante><IdentificadorSolicitante>S0711001H</IdentificadorSolicitante><NombreSolicitante>Govern de les Illes Balears</NombreSolicitante><UnidadTramitadora>DGIDT</UnidadTramitadora><Procedimiento><CodProcedimiento>EC0023SOAD</CodProcedimiento><NombreProcedimiento>Procediment de proves d'escolaritzacio</NombreProcedimiento></Procedimiento><Finalidad>EC0023SOAD#::##::#Tst</Finalidad><Consentimiento>Si</Consentimiento><Funcionario><NombreCompletoFuncionario>Antonio Trobat Obrador</NombreCompletoFuncionario><NifFuncionario>43120476F</NifFuncionario></Funcionario></Solicitante><Titular><TipoDocumentacion>DNI</TipoDocumentacion><Documentacion>12345678Z</Documentacion></Titular><Transmision><CodigoCertificado>SCDHPAJU</CodigoCertificado><IdSolicitud>PINBAL00000000000000000273</IdSolicitud><IdTransmision>PINBAL00000000000000000273</IdTransmision><FechaGeneracion>2014-02-17T09:13:33.454+01:00</FechaGeneracion></Transmision></DatosGenericos>" +
			"<DatosEspecificos xmlns=\"http://intermediacion.redsara.es/scsp/esquemas/datosespecificos\"><Solicitud><Titular><DatosPersonales><Nombre>Mickey</Nombre><Apellido1>Mouse</Apellido1></DatosPersonales></Titular><MunicipioSolicitud>004</MunicipioSolicitud><ProvinciaSolicitud>07</ProvinciaSolicitud></Solicitud></DatosEspecificos>" +
			"</SolicitudTransmision></Solicitudes></Peticion>";
	public static void main(String[] args) {
		EntitatResolver er = new EmpadronamentEntitatResolver();
		OMXMLParserWrapper builder = OMXMLBuilderFactory.createOMBuilder(
				new ByteArrayInputStream(
						XML_PETICIO.getBytes()));
		OMElement documentElement = builder.getDocumentElement();
		System.out.println(documentElement.toString());
		System.out.println(">>> Entitat: " + er.resolve(documentElement));
	}

}
