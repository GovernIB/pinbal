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
import es.caib.pinbal.client.estadistica.ClientEstadistica;

/**
 * Test de les estadístiques de càrrega.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EstadistiquesTest {

	private static final String URL_BASE = "http://localhost:8080/pinbal";
	private static final String USUARI = "pblrep";
	private static final String CONTRASENYA = "pblrep";

	private ClientEstadistica client = new ClientEstadistica(URL_BASE, USUARI, CONTRASENYA, false, null, null);

	@Test
	public void carrega() throws IOException {
		client.enableLogginFilter();
		List<Entitat> resposta = client.carrega();
		assertNotNull(resposta);
		System.out.println("-> carrega: " + objectToJsonString(resposta));
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
