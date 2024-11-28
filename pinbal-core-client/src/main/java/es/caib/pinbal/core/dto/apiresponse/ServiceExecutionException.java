package es.caib.pinbal.core.dto.apiresponse;

public class ServiceExecutionException extends RuntimeException {
    public ServiceExecutionException(String message) {
        super(message);
    }

    public ServiceExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
