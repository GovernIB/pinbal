/**
 * 
 */
package es.caib.pinbal.webapp.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.web.multipart.MultipartFile;

/**
 * Valida que l'arxiu no estigui buit.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ArxiuNoBuitValidator implements ConstraintValidator<ArxiuNoBuit, MultipartFile> {
	
	@Override
	public void initialize(final ArxiuNoBuit constraintAnnotation) {
	}

	@Override
	public boolean isValid(
			final MultipartFile value,
			final ConstraintValidatorContext context) {
		return !(value == null || value.isEmpty());
	}

}
