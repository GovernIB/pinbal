/**
 * 
 */
package es.caib.pinbal.back.validation;

import es.caib.pinbal.back.command.UsuariCodiCommand;
import es.caib.pinbal.back.helper.MessageHelper;
import es.caib.pinbal.logic.intf.dto.UsuariDto;
import es.caib.pinbal.logic.intf.service.UsuariService;
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
			UsuariDto usuariAntic = usuariService.getDades(command.getCodiAntic());
			if (usuariAntic == null) {
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage(anotacio.message() + ".not.found"))
						.addNode("codiAntic")
						.addConstraintViolation();
				valid = false;
			}

//			UsuariDto usuariNou = usuariService.getDades(command.getCodiNou());
//			if (usuariNou != null) {
//				context.disableDefaultConstraintViolation();
//				context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage(anotacio.message() + ".exists"))
//						.addNode("codiNou")
//						.addConstraintViolation();
//				valid = false;
//			}
		}

		return valid;
	}

}
