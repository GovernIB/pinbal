/**
 * 
 */
package es.caib.pinbal.webapp.validation;

import es.caib.pinbal.core.dto.UsuariDto;
import es.caib.pinbal.core.service.UsuariService;
import es.caib.pinbal.webapp.command.UsuariCodiCommand;
import es.caib.pinbal.webapp.helper.MessageHelper;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Comprova que el codi d'entorn no estigui repetit.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class UsuariExistsValidator implements ConstraintValidator<UsuariExists, UsuariCodiCommand> {

	private UsuariExists anotacio;
	@Autowired
	private UsuariService usuariService;

	@Override
	public void initialize(UsuariExists anotacio) {
		this.anotacio = anotacio;
	}

	@Override
	public boolean isValid(
			UsuariCodiCommand command,
			ConstraintValidatorContext context) {
		boolean valid = true;
		// comprova que el nom sigui únic
		if (command.getCodiAntic() != null) {
			UsuariDto usuari = usuariService.getDades(command.getCodiAntic());
			if (usuari == null) {
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage(anotacio.message() + ".not.found"))
						.addNode("codiAntic")
						.addConstraintViolation();
				valid = false;
			}
		}

		return valid;
	}

}
