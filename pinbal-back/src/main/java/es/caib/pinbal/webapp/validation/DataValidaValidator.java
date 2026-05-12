/**
 * 
 */
package es.caib.pinbal.webapp.validation;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Valida que la data introduida sigui vàlida.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DataValidaValidator implements
		ConstraintValidator<DataValida, Object> {

	private String format;

	@Override
	public void initialize(final DataValida constraintAnnotation) {
		this.format = constraintAnnotation.format();
	}

	@Override
	public boolean isValid(
			final Object value,
			final ConstraintValidatorContext context) {
		String dataOriginal = (String)value;
		if (dataOriginal == null || dataOriginal.isEmpty())
			return true;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			Date date = sdf.parse(dataOriginal);
			String dataTransformada = sdf.format(date);
			return dataTransformada.equals(dataOriginal);
		} catch (final Exception ex) {
			LOGGER.error("Error en la validació de la data", ex);
		}
		return false;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(DataValidaValidator.class);

}
