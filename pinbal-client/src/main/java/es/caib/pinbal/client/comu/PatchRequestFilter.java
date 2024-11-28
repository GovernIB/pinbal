package es.caib.pinbal.client.comu;

import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;

import javax.ws.rs.core.MediaType;
import java.net.URI;

public class PatchRequestFilter extends ClientFilter {
    @Override
    public ClientResponse handle(ClientRequest cr) {
        if (cr.getMethod().equalsIgnoreCase("PATCH")) {
            cr.setMethod("POST");
            cr.getHeaders().add("X-HTTP-Method-Override", "PATCH");
        }
        return getNext().handle(cr);
    }

    public static ClientRequest createPatchRequest(URI uri, Object entity, MediaType mediaType) {
        ClientRequest request = ClientRequest.create()
                .type(mediaType)
                .entity(entity)
                .build(uri, "PATCH" );
        return request;
    }
}