/**
 * 
 */
package es.caib.pinbal.webapp.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.caib.pinbal.webapp.common.DocumentIdentitatHelper;

/**
 * Valida que el nombre de document d'identitat de tipus DNI
 * sigui vàlid.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DocumentIdentitatDniValidator implements
		ConstraintValidator<DocumentIdentitatDni, String> {

	@Override
	public void initialize(final DocumentIdentitatDni constraintAnnotation) {
	}

	@Override
	public boolean isValid(
			final String value,
			final ConstraintValidatorContext context) {
		try {
			if (value == null || value.isEmpty())
				return true;
			else
				return DocumentIdentitatHelper.validacioDni(value);
		} catch (final Exception ex) {
			LOGGER.error("Error en la validació del DNI", ex);
		}
		return false;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(DocumentIdentitatDniValidator.class);

}
