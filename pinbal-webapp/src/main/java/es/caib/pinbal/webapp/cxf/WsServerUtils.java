/**
 * 
 */
package es.caib.pinbal.webapp.cxf;

import javax.xml.ws.Endpoint;

import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.EndpointImpl;

/**
 * Utilitat per a configurar de manera centralitzada
 * tots els clients ws
 * 
 * @author Limit Tecnologies
 */
public class WsServerUtils {

	public static Endpoint publish(
			String address,
			Object implementor,
			boolean generateTimestamp,
			boolean logCalls) {
		Endpoint jaxwsEndpoint = Endpoint.publish(address, implementor);
		EndpointImpl jaxwsEndpointImpl = (EndpointImpl)jaxwsEndpoint;
		org.apache.cxf.endpoint.Server server = jaxwsEndpointImpl.getServer();
		org.apache.cxf.endpoint.Endpoint endpoint = server.getEndpoint();
		if (logCalls) {
			endpoint.getInInterceptors().add(new LoggingInInterceptor());
			endpoint.getOutInterceptors().add(new LoggingOutInterceptor());
		}
		/*if ("USERNAMETOKEN".equalsIgnoreCase(authType)) {
			Map<String, Object> wss4jInterceptorProps = new HashMap<String, Object>();
			if (generateTimestamp) {
				wss4jInterceptorProps.put(WSHandlerConstants.ACTION, WSHandlerConstants.TIMESTAMP + " " + WSHandlerConstants.USERNAME_TOKEN);
			} else {
				wss4jInterceptorProps.put(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
			}
			wss4jInterceptorProps.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);
			ServerPasswordCallback sp = new ServerPasswordCallback();
			sp.setUserName(wsUserName);
			sp.setPassword(wsPassword);
			wss4jInterceptorProps.put(WSHandlerConstants.PW_CALLBACK_REF, sp);
			endpoint.getOutInterceptors().add(new WSS4JOutInterceptor(wss4jInterceptorProps));
		}*/
		return jaxwsEndpoint;
	}

}

/*class ServerPasswordCallback implements CallbackHandler {

	private String userName;
	private String password;

	public void handle(Callback[] callbacks) throws IOException,
			UnsupportedCallbackException {
		WSPasswordCallback pc = (WSPasswordCallback) callbacks[0];
		if (pc.getIdentifier().equals(userName)) {
			if (!pc.getPassword().equals(password))
				throw new IOException("usuari/contrasenya incorrectes");
		}
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	public void setPassword(String password) {
		this.password = password;
	}

}*/
