/**
 * 
 */
package es.caib.pinbal.webapp.validation;

import es.caib.pinbal.core.helper.DocumentIdentitatHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Valida que el nombre de document d'identitat de tipus NIF
 * sigui vàlid.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DocumentIdentitatNifValidator implements
		ConstraintValidator<DocumentIdentitatNif, String> {

	@Override
	public void initialize(final DocumentIdentitatNif constraintAnnotation) {
	}

	@Override
	public boolean isValid(
			final String value,
			final ConstraintValidatorContext context) {
		try {
			if (value == null || value.isEmpty())
				return true;
			else
				return DocumentIdentitatHelper.validacioNif(value);
		} catch (final Exception ex) {
			LOGGER.error("Error en la validació del NIF", ex);
		}
		return false;
	}



	private static final Logger LOGGER = LoggerFactory.getLogger(DocumentIdentitatNifValidator.class);

}
