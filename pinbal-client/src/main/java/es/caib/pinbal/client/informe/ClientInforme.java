/**
 * 
 */
package es.caib.pinbal.client.informe;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.caib.pinbal.client.comu.ClientBase;
import es.caib.pinbal.client.comu.EntitatEstadistiques;
import es.caib.pinbal.client.comu.ServeiEstadistiques;

/**
 * Client amb la lògica bàsica per a accedir al servei de consulta
 * d'estadístiques.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ClientInforme extends ClientBase {

	private static final String BASE_URL_SUFIX = "/externa/reports/";

	public ClientInforme(
			String urlBase,
			String usuari,
			String contrasenya) {
		super(urlBase + BASE_URL_SUFIX, usuari, contrasenya);
	}

	public ClientInforme(
			String urlBase,
			String usuari,
			String contrasenya,
			boolean basicAuth,
			Integer timeoutConnect,
			Integer timeoutRead) {
		super(urlBase + BASE_URL_SUFIX, usuari, contrasenya, basicAuth, timeoutConnect, timeoutRead);
	}

	public List<EntitatEstadistiques> procediments() throws IOException {
		return restPeticioGetList(
				"procediments",
				null,
				EntitatEstadistiques.class);
	}

	public List<EntitatEstadistiques> usuaris() throws IOException {
		return restPeticioGetList(
				"usuaris",
				null,
				EntitatEstadistiques.class);
	}

	public List<ServeiEstadistiques> serveis() throws IOException {
		return restPeticioGetList(
				"serveis",
				null,
				ServeiEstadistiques.class);
	}

	public List<EntitatEstadistiques> general(
			Date dataInici,
			Date dataFi) throws IOException {
		return general(dataInici, dataFi, false);
	}

	public List<EntitatEstadistiques> general(
			Date dataInici,
			Date dataFi,
			boolean historic) throws IOException {
		Map<String, String> params = new HashMap<String, String>();
		if (dataInici != null) {
			params.put("dataInici", dateToString(dataInici));
		}
		if (dataFi != null) {
			params.put("dataFi", dateToString(dataFi));
		}
		params.put("historic", historic? "true" : "false");
		return restPeticioGetList(
				"general",
				params,
				EntitatEstadistiques.class);
	}

}
