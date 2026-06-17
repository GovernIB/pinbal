/**
 * 
 */
package es.caib.pinbal.client.estadistiques;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import es.caib.pinbal.client.comu.EntitatEstadistiques;
import es.caib.pinbal.client.estadistica.ClientEstadistica;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test de les estadístiques de càrrega.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EstadistiquesTest {

	private static final String URL_BASE = "http://localhost:8180/pinbalapi";
	private static final String USUARI = "pblrep";
	private static final String CONTRASENYA = "pblrep";

	private ClientEstadistica client = new ClientEstadistica(URL_BASE, USUARI, CONTRASENYA, true, null, null);

	@Test
	public void carrega() throws IOException {
		client.enableLogginFilter();
		List<EntitatEstadistiques> resposta = client.carrega();
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
