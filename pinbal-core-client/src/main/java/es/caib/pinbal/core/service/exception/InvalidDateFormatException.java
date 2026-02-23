package es.caib.pinbal.core.service.exception;

public class InvalidDateFormatException extends RuntimeException {

    public InvalidDateFormatException() {
        super("El format de la data és incorrecte");
    }

    public InvalidDateFormatException(String msg) {
        super(msg);
    }

    public InvalidDateFormatException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
