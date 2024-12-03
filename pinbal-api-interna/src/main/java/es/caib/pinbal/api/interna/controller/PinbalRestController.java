package es.caib.pinbal.api.interna.controller;

import es.caib.pinbal.core.dto.apiresponse.ErrorResponse;
import es.caib.pinbal.core.dto.apiresponse.ValidationErrorResponse;
import es.caib.pinbal.core.service.exception.InvalidInputException;
import es.caib.pinbal.core.service.exception.ResourceNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

public class PinbalRestController {

    /**
     * Gestor d'excepció per a recursos no trobats.
     * @param ex Excepció llançada.
     * @return Missatge d'error.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public @ResponseBody ErrorResponse handleResourceNotFound(ResourceNotFoundException ex) {
        return new ErrorResponse("RESOURCE_NOT_FOUND", ex.getMessage());
    }

    /**
     * Gestor d'excepció per a entrades invàlides.
     * @param ex Excepció llançada.
     * @return Llista d'errors de validació.
     */
    @ExceptionHandler(InvalidInputException.class)
    public @ResponseBody ValidationErrorResponse handleInvalidInput(InvalidInputException ex) {
        ValidationErrorResponse errorResponse = new ValidationErrorResponse("INVALID_INPUT");

        // Iterar manualment sobre la llista d'errors de validació
        for (org.springframework.validation.FieldError fieldError : ex.getErrors().getFieldErrors()) {
            errorResponse.addValidationError(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return errorResponse;
    }

}
