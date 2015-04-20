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
 * Valida que el nombre de document d'identitat de tipus
 * passaport sigui vàlid.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DocumentIdentitatPassValidator implements
		ConstraintValidator<DocumentIdentitatPass, String> {

	@Override
	public void initialize(final DocumentIdentitatPass constraintAnnotation) {
	}

	@Override
	public boolean isValid(
			final String value,
			final ConstraintValidatorContext context) {
		try {
			if (value == null || value.isEmpty())
				return true;
			else
				return DocumentIdentitatHelper.validacioPass(value);
		} catch (final Exception ex) {
			LOGGER.error("Error en la validació del passaport", ex);
		}
		return false;
	}



	private static final Logger LOGGER = LoggerFactory.getLogger(DocumentIdentitatPassValidator.class);

}
