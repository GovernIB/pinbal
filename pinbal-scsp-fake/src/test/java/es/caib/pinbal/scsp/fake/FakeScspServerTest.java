package es.caib.pinbal.scsp.fake;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.junit.Assert.assertTrue;

/**
 * Prova d'integració HTTP real contra {@link FakeScspServer}: verifica que
 * les respostes es resolen correctament per a serveis amb esquemes de
 * petició diferents (V3 amb {@code Documentacion} com a text directe, i V2
 * amb {@code Documentacion/Valor} dins de {@code DatosEspecificos}, com
 * SCDCPAJU), i que el disparador d'error simulat funciona en tots dos casos.
 */
public class FakeScspServerTest {

	private static final String HOST = "127.0.0.1";
	private static final int PORT = 18199;
	private static final String BASE_URL = "http://" + HOST + ":" + PORT;
	private static final HttpClient CLIENT = HttpClient.newBuilder()
		.connectTimeout(Duration.ofSeconds(5))
		.build();

	@BeforeClass
	public static void startServer() throws Exception {
		System.setProperty("fake.scsp.host", HOST);
		System.setProperty("fake.scsp.port", String.valueOf(PORT));
		FakeScspServer.main(new String[0]);
		waitUntilReady();
	}

	@AfterClass
	public static void clearProperties() {
		System.clearProperty("fake.scsp.host");
		System.clearProperty("fake.scsp.port");
	}

	private static void waitUntilReady() throws Exception {
		for (int i = 0; i < 20; i++) {
			try {
				HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(BASE_URL + "/__fake-scsp"))
					.timeout(Duration.ofSeconds(5))
					.GET()
					.build();
				HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
				if (response.statusCode() == 200) {
					return;
				}
			} catch (IOException e) {
				// encara no està a punt
			}
			Thread.sleep(100);
		}
		throw new IllegalStateException("El fake SCSP no ha arrencat a temps");
	}

	private static HttpResponse<String> send(String path, String body) throws Exception {
		HttpRequest request = HttpRequest.newBuilder()
			.uri(URI.create(path))
			.timeout(Duration.ofSeconds(5))
			.POST(HttpRequest.BodyPublishers.ofString(body))
			.build();
		return CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
	}

	private static String peticionV3(String codigoCertificado, String documentacio) {
		return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">"
			+ "<soapenv:Body><Peticion xmlns=\"http://intermediacion.redsara.es/scsp/esquemas/V3/peticion\">"
			+ "<Atributos><IdPeticion>TEST00001</IdPeticion><NumElementos>1</NumElementos>"
			+ "<CodigoCertificado>" + codigoCertificado + "</CodigoCertificado></Atributos>"
			+ "<Solicitudes><SolicitudTransmision><DatosGenericos>"
			+ "<Titular><TipoDocumentacion>NIF</TipoDocumentacion><Documentacion>" + documentacio + "</Documentacion></Titular>"
			+ "<Transmision><CodigoCertificado>" + codigoCertificado + "</CodigoCertificado>"
			+ "<IdSolicitud>TEST00001</IdSolicitud></Transmision>"
			+ "</DatosGenericos></SolicitudTransmision></Solicitudes></Peticion></soapenv:Body></soapenv:Envelope>";
	}

	private static String peticionScdcpaju(String documentacio) {
		return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">"
			+ "<soapenv:Body><Peticion xmlns=\"http://intermediacion.redsara.es/scsp/esquemas/V3/peticion\">"
			+ "<Atributos><IdPeticion>TEST00002</IdPeticion><NumElementos>1</NumElementos>"
			+ "<CodigoCertificado>SCDCPAJU</CodigoCertificado></Atributos>"
			+ "<Solicitudes><SolicitudTransmision><DatosGenericos>"
			+ "<Titular/>"
			+ "<Transmision><CodigoCertificado>SCDCPAJU</CodigoCertificado>"
			+ "<IdSolicitud>TEST00002</IdSolicitud></Transmision>"
			+ "</DatosGenericos>"
			+ "<DatosEspecificos xmlns=\"http://intermediacion.redsara.es/scsp/esquemas/datosespecificos\">"
			+ "<Solicitud><Titular><Documentacion><Tipo>NIF</Tipo><Valor>" + documentacio + "</Valor></Documentacion></Titular></Solicitud>"
			+ "</DatosEspecificos>"
			+ "</SolicitudTransmision></Solicitudes></Peticion></soapenv:Body></soapenv:Envelope>";
	}

	@Test
	public void shouldReturnSuccessCodeForOrdinaryRequest_v3Schema() throws Exception {
		HttpResponse<String> response = send(BASE_URL + "/servicios/fake", peticionV3("Q2827003ATGSS001", "43102532A"));
		assertTrue("s'esperava CodigoEstado 0003, resposta: " + response.body(),
			response.body().contains("<CodigoEstado>0003</CodigoEstado>"));
	}

	@Test
	public void shouldSimulateErrorWhenTitularDocMatchesTrigger_v3Schema() throws Exception {
		HttpResponse<String> response = send(BASE_URL + "/servicios/fake", peticionV3("Q2827003ATGSS001", "00000000ERR"));
		assertTrue("s'esperava CodigoEstado 9999, resposta: " + response.body(),
			response.body().contains("<CodigoEstado>9999</CodigoEstado>"));
	}

	@Test
	public void shouldReturnSuccessCodeForOrdinaryRequest_v2SchemaWithNestedDocumentacion() throws Exception {
		HttpResponse<String> response = send(BASE_URL + "/servicios/fake", peticionScdcpaju("41510455D"));
		assertTrue("s'esperava CodigoEstado 0003, resposta: " + response.body(),
			response.body().contains("<CodigoEstado>0003</CodigoEstado>"));
	}

	@Test
	public void shouldSimulateErrorWhenTitularDocMatchesTrigger_v2SchemaWithNestedDocumentacion() throws Exception {
		HttpResponse<String> response = send(BASE_URL + "/servicios/fake", peticionScdcpaju("00000000ERR"));
		assertTrue("s'esperava CodigoEstado 9999, resposta: " + response.body(),
			response.body().contains("<CodigoEstado>9999</CodigoEstado>"));
	}
}
