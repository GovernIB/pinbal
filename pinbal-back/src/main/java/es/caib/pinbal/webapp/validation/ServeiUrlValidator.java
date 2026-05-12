/**
 * 
 */
package es.caib.pinbal.webapp.validation;

import com.google.common.base.Strings;
import es.caib.pinbal.webapp.command.ServeiCommand;
import es.caib.pinbal.webapp.helper.MessageHelper;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Comprova que el codi d'entorn no estigui repetit.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ServeiUrlValidator implements ConstraintValidator<ServeiUrl, ServeiCommand> {

	private ServeiUrl anotacio;

	@Override
	public void initialize(ServeiUrl anotacio) {
		this.anotacio = anotacio;
	}

	@Override
	public boolean isValid(
			ServeiCommand command,
			ConstraintValidatorContext context) {
		boolean valid = true;
		// comprova que al manco una url estigui definida
		if (Strings.isNullOrEmpty(command.getScspUrlSincrona()) && Strings.isNullOrEmpty(command.getScspUrlAsincrona())) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage(anotacio.message() + ".buida"))
					.addNode("scspUrlSincrona")
					.addConstraintViolation();
			context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage(anotacio.message() + ".buida"))
					.addNode("scspUrlAsincrona")
					.addConstraintViolation();
			valid = false;
		}

		return valid;
	}

}
