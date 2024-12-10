package es.caib.pinbal.api.interna.controller;

import es.caib.pinbal.client.comu.ErrorResponse;
import es.caib.pinbal.core.dto.apiresponse.ServiceExecutionException;
import es.caib.pinbal.core.service.exception.AccessDenegatException;
import es.caib.pinbal.core.service.exception.InvalidInputException;
import es.caib.pinbal.core.service.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class PinbalHalRestController {

    /**
     * Gestor d'excepció per a recursos no trobats.
     * @param ex Excepció llançada.
     * @return Missatge d'error.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse("404", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Gestor d'excepció per a entrades invàlides.
     * @param ex Excepció llançada.
     * @return Llista d'errors de validació.
     */
    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ErrorResponse> handleInvalidInput(InvalidInputException ex) {
        BindingResult errors = ex.getErrors();
        StringBuilder errorMessages = new StringBuilder();
        for (ObjectError error : errors.getGlobalErrors()) {
            if (errorMessages.length() > 0) {
                errorMessages.append(", ");
            }
            errorMessages.append(error.getDefaultMessage());
        }
        for (FieldError error : errors.getFieldErrors()) {
            if (errorMessages.length() > 0) {
                errorMessages.append(", ");
            }
            errorMessages.append(error.getField() + ": " + error.getDefaultMessage());
        }

        ErrorResponse errorResponse = new ErrorResponse("400", errorMessages.toString());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Gestor d'excepció per a excepcions inesperades.
     * @param ex Excepció llançada.
     * @return Llista d'errors de validació.
     */
    @ExceptionHandler(ServiceExecutionException.class)
    public ResponseEntity<ErrorResponse> handleService(ServiceExecutionException ex) {

        ErrorResponse errorResponse = new ErrorResponse("500", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Gestor d'excepció per a errors d'accés.
     * @param ex Excepció llançada.
     * @return Llista d'errors de validació.
     */
    @ExceptionHandler(AccessDenegatException.class)
    public ResponseEntity<ErrorResponse> handleAccesDenegat(AccessDenegatException ex) {

        ErrorResponse errorResponse = new ErrorResponse("401", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

}
