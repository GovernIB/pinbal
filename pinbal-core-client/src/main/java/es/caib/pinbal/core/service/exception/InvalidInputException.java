package es.caib.pinbal.core.service.exception;

import org.springframework.validation.BindingResult;

public class InvalidInputException extends RuntimeException {
    private BindingResult errors;

    public InvalidInputException(BindingResult errors) {
        this.errors = errors;
    }

    public BindingResult getErrors() {
        return errors;
    }
}
