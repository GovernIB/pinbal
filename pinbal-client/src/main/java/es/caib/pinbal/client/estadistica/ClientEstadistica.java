/**
 * 
 */
package es.caib.pinbal.client.estadistica;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.caib.pinbal.client.comu.ClientBase;
import es.caib.pinbal.client.comu.Entitat;
import es.caib.pinbal.client.comu.Procediment;

/**
 * Client amb la lògica bàsica per a accedir al servei de consulta
 * d'estadístiques.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ClientEstadistica extends ClientBase {

	private static final String BASE_URL_SUFIX = "api/interna/stats/";

	public ClientEstadistica(
			String urlBase,
			String usuari,
			String contrasenya) {
		super(urlBase + BASE_URL_SUFIX, usuari, contrasenya);
	}

	public ClientEstadistica(
			String urlBase,
			String usuari,
			String contrasenya,
			boolean basicAuth,
			Integer timeoutConnect,
			Integer timeoutRead) {
		super(urlBase + BASE_URL_SUFIX, usuari, contrasenya, basicAuth, timeoutConnect, timeoutRead);
	}

	public List<Procediment> consultes(
			String entitatCodi,
			String procedimentCodi,
			String serveiCodi,
			String estat,
			Date dataInici,
			Date dataFi) throws IOException {
		Map<String, String> params = new HashMap<String, String>();
		if (entitatCodi != null) {
			params.put("entitatCodi", entitatCodi);
		}
		if (procedimentCodi != null) {
			params.put("procedimentCodi", procedimentCodi);
		}
		if (serveiCodi != null) {
			params.put("serveiCodi", serveiCodi);
		}
		if (estat != null) {
			params.put("estat", estat);
		}
		if (dataInici != null) {
			params.put("dataInici", dateToString(dataInici));
		}
		if (dataFi != null) {
			params.put("dataFi", dateToString(dataFi));
		}
		return restPeticioGetList(
				"consultes",
				params,
				Procediment.class);
	}

	public List<Entitat> carrega() throws IOException {
		return restPeticioGetList(
				"carrega",
				null,
				Entitat.class);
	}

}
