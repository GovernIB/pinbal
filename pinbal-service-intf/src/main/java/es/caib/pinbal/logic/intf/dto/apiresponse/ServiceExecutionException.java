package es.caib.pinbal.logic.intf.dto.apiresponse;

public class ServiceExecutionException extends RuntimeException {

    public ServiceExecutionException(String message) {
        super(message);
    }
    public ServiceExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

}
