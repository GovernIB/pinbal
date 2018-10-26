/**
 * 
 */
package es.caib.pinbal.ws.client;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;

import es.caib.pinbal.ws.recobriment.Recobriment;

/**
 * Utilitat per a instanciar clients per al servei de recobriment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RecobrimentClientFactory {

	public static Recobriment getWsClient(
			URL wsdlResourceUrl,
			String endpoint,
			String userName,
			String password) throws MalformedURLException {
		return new WsClientHelper<Recobriment>().generarClientWs(
				wsdlResourceUrl,
				endpoint,
				new QName(
						"http://www.caib.es/pinbal/ws/recobriment",
						"RecobrimentService"),
				userName,
				password,
				Recobriment.class);
	}

	public static Recobriment getWsClient(
			String endpoint,
			String userName,
			String password) throws MalformedURLException {
		return new WsClientHelper<Recobriment>().generarClientWs(
				endpoint,
				new QName(
						"http://www.caib.es/pinbal/ws/recobriment",
						"RecobrimentService"),
				userName,
				password,
				Recobriment.class);
	}

}
