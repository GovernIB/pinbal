package es.caib.pinbal.client.procediments;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import es.caib.pinbal.client.comu.ClientBase;
import es.caib.pinbal.client.comu.LogLevel;
import es.caib.pinbal.client.comu.Page;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ProcedimentClient extends ClientBase {

    public ProcedimentClient(String urlBase, String usuari, String contrasenya, LogLevel logLevel) {
        super(urlBase, usuari, contrasenya, logLevel);
    }

    public void createProcediment(Procediment procediment) throws UniformInterfaceException, ClientHandlerException, IOException {
        ClientResponse response = restPeticioPost("/procediments", procediment, ClientResponse.class, MediaType.APPLICATION_JSON_TYPE);

        processVoidResponse(response);
//        int statusCode = response.getStatus();
//        switch(statusCode) {
//            case 201:
//                logInfo("Procediment creat amb èxit.");
//                break;
//            case 400:
//                logError("Entrada invàlida: " + response.getEntity(ErrorResponse.class).getErrorMessage());
//                throw new RuntimeException("Entrada invàlida: " + response.getEntity(ErrorResponse.class).getErrorMessage());
//            case 500:
//                logError("Resposta inesperada del servidor: " + response.getEntity(ErrorResponse.class).getErrorMessage());
//                throw new RuntimeException("Resposta inesperada del servidor: " + response.getEntity(String.class));
//        }
    }

    public void updateProcediment(Long procedimentId, Procediment procediment) throws UniformInterfaceException, ClientHandlerException, IOException {
        ClientResponse response = restPeticioPost("/procediments/" + procedimentId, procediment, ClientResponse.class, MediaType.APPLICATION_JSON_TYPE);

        processVoidResponse(response);
//        int statusCode = response.getStatus();
//        switch(statusCode) {
//            case 200:
//                logInfo("Procediment actualitzat amb èxit.");
//                break;
//            case 400:
//                logError("Entrada invàlida: " + response.getEntity(ErrorResponse.class).getErrorMessage());
//                throw new RuntimeException("Entrada invàlida: " + response.getEntity(ErrorResponse.class).getErrorMessage());
//            case 404:
//                logError("Procediment no trobat.");
//                throw new RuntimeException("Procediment no trobat.");
//            case 500:
//                logError("Resposta inesperada del servidor: " + response.getEntity(ErrorResponse.class).getErrorMessage());
//                throw new RuntimeException("Resposta inesperada del servidor: " + response.getEntity(ErrorResponse.class).getErrorMessage());
//        }
    }

//    public Procediment patchProcediment(Long procedimentId, ProcedimentPatch procedimentPatch) throws UniformInterfaceException, ClientHandlerException, IOException {
//        return restPeticioPatch("/procediments/" + procedimentId, procedimentPatch, Procediment.class, MediaType.APPLICATION_JSON_TYPE);
////        processVoidResponse(response);
//    }

    public void enableServeiToProcediment(Long procedimentId, String serveiCodi) throws UniformInterfaceException, ClientHandlerException, IOException {
        ClientResponse response = restPeticioPost("/procediments/" + procedimentId + "/serveis/" + serveiCodi + "/enable", null, ClientResponse.class, MediaType.APPLICATION_JSON_TYPE);

        processVoidResponse(response);
//        int statusCode = response.getStatus();
//        switch(statusCode) {
//            case 200:
//                logInfo("Servei habilitat per al procediment amb èxit.");
//                break;
//            case 404:
//                logError("Procediment o Servei no trobat.");
//                throw new RuntimeException("Procediment o Servei no trobat.");
//            case 500:
//                logError("Resposta inesperada del servidor: " + response.getEntity(ErrorResponse.class).getErrorMessage());
//                throw new RuntimeException("Resposta inesperada del servidor: " + response.getEntity(ErrorResponse.class).getErrorMessage());
//        }
    }

    public Page<Procediment> getProcediments(String entitatCodi, String codi, String nom, String organGestor, int page, int size, String sort)
        throws UniformInterfaceException, ClientHandlerException, IOException {
        
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("entitatCodi", entitatCodi);
        queryParams.put("codi", codi);
        queryParams.put("nom", nom);
        queryParams.put("organGestor", organGestor);
        queryParams.put("page", String.valueOf(page));
        queryParams.put("size", String.valueOf(size));
        queryParams.put("sort", sort);

        ClientResponse response = restPeticioGet("/procediments", queryParams, ClientResponse.class);

        return processPagedResponse(response, Procediment.class, page, size, sort);
//        int statusCode = response.getStatus();
//        switch(statusCode) {
//            case 204:
//                logInfo("No s'han trobat procediments");
//                return Page.<Procediment>builder().number(page).size(size).totalPages(0).totalElements(0).content(new ArrayList<Procediment>()).build();
//            case 404:
//                logError("Entitat no trobada: " + response.getEntity(String.class));
//                throw new RuntimeException("Entitat no trobada: " + response.getEntity(ErrorResponse.class).getErrorMessage());
//            case 500:
//                logError("Resposta inesperada del servidor: " + response.getEntity(ErrorResponse.class).getErrorMessage());
//                throw new RuntimeException("Resposta inesperada del servidor: " + response.getEntity(ErrorResponse.class).getErrorMessage());
//        }
//
//        String jsonOutput = response.getEntity(String.class);
//        PagedResources<Resource<Procediment>> procediments = mapper.readValue(
//                jsonOutput,
//                mapper.getTypeFactory().constructParametricType(PagedResources.class, mapper.getTypeFactory().constructParametricType(Resource.class, Procediment.class))
//        );
//
//        List<Procediment> content = new ArrayList<>();
//        for (Resource<Procediment> resource : procediments.getContent()) {
//            content.add(resource.getContent());
//        }
//
//        Page<Procediment> resultPage = new Page<>();
//        resultPage.setContent(content);
//        resultPage.setSize(procediments.getPage().getSize());
//        resultPage.setTotalElements(procediments.getPage().getTotalElements());
//        resultPage.setTotalPages(procediments.getPage().getTotalPages());
//        resultPage.setNumber(procediments.getPage().getNumber());
//
//        return resultPage;
    }

    public Procediment getProcediment(Long procedimentId) throws UniformInterfaceException, ClientHandlerException, IOException {
        ClientResponse response = restPeticioGet("/procediments/" + procedimentId, new HashMap<String, String>(), ClientResponse.class);

        return processResponse(response, Procediment.class);
//        int statusCode = response.getStatus();
//        switch(statusCode) {
//            case 204:
//                logInfo("No s'ha trobat el procediment: " + procedimentId + ". ");
//                return null;
//            case 404:
//                logError("Procediment no trobat: " + response.getEntity(String.class));
//                throw new RuntimeException("Procediment no trobat: " + response.getEntity(ErrorResponse.class).getErrorMessage());
//            case 500:
//                logError("Resposta inesperada del servidor: " + response.getEntity(ErrorResponse.class).getErrorMessage());
//                throw new RuntimeException("Resposta inesperada del servidor: " + response.getEntity(ErrorResponse.class).getErrorMessage());
//        }
//
//        String jsonOutput = response.getEntity(String.class);
//        Resource<Procediment> procediment = mapper.readValue(jsonOutput, mapper.getTypeFactory().constructParametricType(Resource.class, Procediment.class));
//
//        return procediment.getContent();
    }

    public Procediment getProcediment(String procedimentCodi, final String entitatCodi) throws UniformInterfaceException, ClientHandlerException, IOException {
        ClientResponse response = restPeticioGet("/procediments/byCodi/" + procedimentCodi, new HashMap<String, String>() {{ put("entitatCodi", entitatCodi); }}, ClientResponse.class);

        return processResponse(response, Procediment.class);
//        int statusCode = response.getStatus();
//        switch(statusCode) {
//            case 204:
//                logInfo("No s'ha trobat el procediment amb codi: " + procedimentCodi + ". ");
//                return null;
//            case 404:
//                logError("Procediment no trobat: " + response.getEntity(String.class));
//                throw new RuntimeException("Procediment no trobat: " + response.getEntity(ErrorResponse.class).getErrorMessage());
//            case 500:
//                logError("Resposta inesperada del servidor: " + response.getEntity(ErrorResponse.class).getErrorMessage());
//                throw new RuntimeException("Resposta inesperada del servidor: " + response.getEntity(ErrorResponse.class).getErrorMessage());
//        }
//
//        String jsonOutput = response.getEntity(String.class);
//        Resource<Procediment> procediment = mapper.readValue(jsonOutput, mapper.getTypeFactory().constructParametricType(Resource.class, Procediment.class));
//
//        return procediment.getContent();
    }


    private void logTrace(String message) {
        if (logLevel.isTraceEnabled()) log.trace(message);
    }
    private void logDebug(String message) {
        if (logLevel.isDebugEnabled()) log.debug(message);
    }
    private void logInfo(String message) {
        if (logLevel.isInfoEnabled()) log.info(message);
    }
    private void logWarn(String message) {
        if (logLevel.isWarnEnabled()) log.warn(message);
    }
    private void logError(String message) {
        if (logLevel.isErrorEnabled()) log.error(message);
    }
}