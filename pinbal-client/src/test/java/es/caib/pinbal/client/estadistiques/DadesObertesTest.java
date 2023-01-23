/**
 * 
 */
package es.caib.pinbal.client.estadistiques;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import es.caib.pinbal.client.dadesobertes.ClientDadesObertes;
import es.caib.pinbal.client.dadesobertes.DadesObertesRespostaConsulta;

/**
 * Test de les estadístiques de dades obertes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DadesObertesTest {

//	private static final String URL_BASE = "https://proves.caib.es/pinbalapi";

	private static final String URL_BASE = "http://localhost:8180/pinbalapi";

	@Test
	public void opendata() throws IOException {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_YEAR, 1);
		List<DadesObertesRespostaConsulta> resposta = getClient().opendata(
				null,
				cal.getTime(),
				new Date(),
				null,
				null);
		assertNotNull(resposta);
		System.out.println("-> opendata: " + resposta.size());
		for (DadesObertesRespostaConsulta item: resposta.subList(0, 5)) {
			System.out.println("    - " + objectToJsonString(item));
		}
	}

	@Test
	public void opendata_historic() throws IOException {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_YEAR, 1);
		cal.add(Calendar.MONTH, -6);
		List<DadesObertesRespostaConsulta> resposta = getClient().opendata(
				null,
				cal.getTime(),
				new Date(),
				null,
				null,
				true);
		assertNotNull(resposta);
		System.out.println("-> opendata: " + resposta.size());
		for (DadesObertesRespostaConsulta item: resposta.subList(0, 5)) {
			System.out.println("    - " + objectToJsonString(item));
		}
	}

	private String objectToJsonString(Object obj) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		return mapper.writeValueAsString(obj);
	}

	private ClientDadesObertes getClient() {
		ClientDadesObertes client = new ClientDadesObertes(
				URL_BASE,
				null, // USUARI,
				null, // CONTRASENYA,
				true,
				null,
				null);
		//client.enableLogginFilter();
		return client;
	}

}
