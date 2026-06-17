package es.caib.pinbal.client.comu;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import java.io.IOException;

/**
 * Filter that converts PATCH requests to POST with X-HTTP-Method-Override header,
 * for servers that do not support the PATCH HTTP method natively.
 */
public class PatchRequestFilter implements ClientRequestFilter {

	@Override
	public void filter(ClientRequestContext requestContext) throws IOException {
		if ("PATCH".equalsIgnoreCase(requestContext.getMethod())) {
			requestContext.setMethod("POST");
			requestContext.getHeaders().putSingle("X-HTTP-Method-Override", "PATCH");
		}
	}

}
