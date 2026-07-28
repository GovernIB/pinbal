package es.caib.pinbal.api.interna.controller;

import es.caib.pinbal.client.comu.ErrorResponse;
import es.caib.pinbal.logic.intf.dto.apiresponse.ValidationErrorResponse;
import es.caib.pinbal.logic.intf.service.exception.InvalidInputException;
import es.caib.pinbal.logic.intf.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PinbalRestControllerTest {

    private final PinbalRestController controller = new PinbalRestController();

    @Test
    public void testHandleResourceNotFound() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Recurs no trobat");

        ErrorResponse response = controller.handleResourceNotFound(ex);

        assertNotNull(response);
        assertEquals("RESOURCE_NOT_FOUND", response.getErrorCode());
        assertEquals("Recurs no trobat", response.getErrorMessage());
    }

    @Test
    public void testHandleInvalidInput() {
        BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "objecte");
        bindingResult.addError(new FieldError("objecte", "camp1", "El camp és obligatori"));

        InvalidInputException ex = new InvalidInputException(bindingResult);

        ValidationErrorResponse response = controller.handleInvalidInput(ex);

        assertNotNull(response);
        assertEquals("INVALID_INPUT", response.getErrorCode());
    }
}
