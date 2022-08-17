/**
 * 
 */
package es.caib.pinbal.client.dadesobertes;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.caib.pinbal.client.comu.ClientBase;

/**
 * Client amb la lògica bàsica per a accedir al servei de consulta
 * d'estadístiques.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ClientDadesObertes extends ClientBase {

	private static final String BASE_URL_SUFIX = "/externa/";

	public ClientDadesObertes(
			String urlBase,
			String usuari,
			String contrasenya) {
		super(urlBase + BASE_URL_SUFIX, usuari, contrasenya);
	}

	public ClientDadesObertes(
			String urlBase,
			String usuari,
			String contrasenya,
			boolean basicAuth,
			Integer timeoutConnect,
			Integer timeoutRead) {
		super(urlBase + BASE_URL_SUFIX, usuari, contrasenya, basicAuth, timeoutConnect, timeoutRead);
	}

	public List<DadesObertesRespostaConsulta> opendata(
			String entitatCodi,
			Date dataInici,
			Date dataFi,
			String procedimentCodi,
			String serveiCodi) throws IOException {
		return opendata(entitatCodi, dataInici, dataFi, procedimentCodi, serveiCodi, false);
	}

	public List<DadesObertesRespostaConsulta> opendata(
			String entitatCodi,
			Date dataInici,
			Date dataFi,
			String procedimentCodi,
			String serveiCodi,
			boolean historic) throws IOException {
		Map<String, String> params = new HashMap<String, String>();
		if (entitatCodi != null) {
			params.put("entitatCodi", entitatCodi);
		}
		if (dataInici != null) {
			params.put("dataInici", dateToString(dataInici));
		}
		if (dataFi != null) {
			params.put("dataFi", dateToString(dataFi));
		}
		if (procedimentCodi != null) {
			params.put("procedimentCodi", procedimentCodi);
		}
		if (serveiCodi != null) {
			params.put("serveiCodi", serveiCodi);
		}
		params.put("historic", historic? "true" : "false");
		String json = restPeticioGetString("opendata", params);
		return Arrays.asList(
				new ObjectMapper().readValue(
						json,
						DadesObertesRespostaConsulta[].class));
	}

}
