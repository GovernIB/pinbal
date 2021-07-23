/**
 * 
 */
package es.caib.pinbal.client.estadistiques;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import es.caib.pinbal.client.comu.Entitat;
import es.caib.pinbal.client.comu.Servei;
import es.caib.pinbal.client.informe.ClientInforme;

/**
 * Test de les estadístiques de càrrega.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class InformeTest {

	private static final String URL_BASE = "http://localhost:8080/pinbal";
	private static final String USUARI = "pblrep";
	private static final String CONTRASENYA = "pblrep";

	private ClientInforme client = new ClientInforme(URL_BASE, USUARI, CONTRASENYA, false, null, null);

	@Test
	public void procediments() throws IOException {
		client.enableLogginFilter();
		List<Entitat> resposta = client.procediments();
		assertNotNull(resposta);
		System.out.println("-> procediments: " + objectToJsonString(resposta));
	}
	
	@Test
	public void serveis() throws IOException {
		List<Servei> resposta = client.serveis();
		assertNotNull(resposta);
		System.out.println("-> serveis: " + objectToJsonString(resposta));
	}

	@Test
	public void usuaris() throws IOException {
		List<Entitat> resposta = client.usuaris();
		assertNotNull(resposta);
		System.out.println("-> usuaris: " + objectToJsonString(resposta));
	}

	private String objectToJsonString(Object obj) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		return mapper.writeValueAsString(obj);
	}

}
