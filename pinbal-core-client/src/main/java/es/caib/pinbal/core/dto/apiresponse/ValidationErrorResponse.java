package es.caib.pinbal.core.dto.apiresponse;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class ValidationErrorResponse extends ErrorResponse {
    private List<ValidationError> validationErrors = new ArrayList<>();

    public ValidationErrorResponse(String errorCode) {
        super(errorCode, "Validation failed");
    }

    public void addValidationError(String field, String error) {
        validationErrors.add(new ValidationError(field, error));
    }

    @Getter @Setter
    public static class ValidationError {
        private String field;
        private String error;

        public ValidationError(String field, String error) {
            this.field = field;
            this.error = error;
        }
    }
}