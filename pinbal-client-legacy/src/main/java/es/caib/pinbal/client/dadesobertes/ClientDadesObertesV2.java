/**
 * 
 */
package es.caib.pinbal.client.dadesobertes;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.caib.pinbal.client.comu.ClientBase;

/**
 * Client amb la lògica bàsica per a accedir al servei de consulta
 * d'estadístiques.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ClientDadesObertesV2 extends ClientBase {

	private static final String BASE_URL_SUFIX = "/externa/v2/";

	public ClientDadesObertesV2(
			String urlBase,
			String usuari,
			String contrasenya) {
		super(urlBase + BASE_URL_SUFIX, usuari, contrasenya);
	}

	public ClientDadesObertesV2(
			String urlBase,
			String usuari,
			String contrasenya,
			boolean basicAuth,
			Integer timeoutConnect,
			Integer timeoutRead) {
		super(urlBase + BASE_URL_SUFIX, usuari, contrasenya, basicAuth, timeoutConnect, timeoutRead);
	}

	public DadesObertesResposta opendata(
			String entitatCodi,
			Date dataInici,
			Date dataFi,
			String procedimentCodi,
			String serveiCodi,
			Integer pagina,
			Integer mida) throws Exception {
		return opendata(entitatCodi, dataInici, dataFi, procedimentCodi, serveiCodi, false, pagina, mida);
	}

	public DadesObertesResposta opendata(
			String entitatCodi,
			Date dataInici,
			Date dataFi,
			String procedimentCodi,
			String serveiCodi,
			boolean historic,
			Integer pagina,
			Integer mida) throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		if (entitatCodi != null) {
			params.put("entitatCodi", entitatCodi);
		}
		if (dataInici != null) {
			params.put("dataInici", simpleDateToString(dataInici));
		}
		if (dataFi != null) {
			params.put("dataFi", simpleDateToString(dataFi));
		}
		if (procedimentCodi != null) {
			params.put("procedimentCodi", procedimentCodi);
		}
		if (serveiCodi != null) {
			params.put("serveiCodi", serveiCodi);
		}
		if (pagina != null) {
			params.put("pagina", pagina.toString());
		}
		if (mida != null) {
			params.put("mida", mida.toString());
		}
		params.put("historic", historic? "true" : "false");
		String json = restPeticioGetString("opendata", params);
		return new ObjectMapper().readValue(
						json,
						DadesObertesResposta.class);
	}

}
