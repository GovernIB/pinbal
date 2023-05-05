/**
 * 
 */
package es.caib.pinbal.webapp.validation;

import es.caib.pinbal.core.dto.regles.ServeiReglaDto;
import es.caib.pinbal.core.service.ServeiService;
import es.caib.pinbal.webapp.command.ServeiReglaCommand;
import es.caib.pinbal.webapp.helper.MessageHelper;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Comprova que el codi d'entorn no estigui repetit.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ServeiReglaValidator implements ConstraintValidator<ServeiRegla, ServeiReglaCommand> {

	private ServeiRegla anotacio;
	@Autowired
	private ServeiService serveiService;

	@Override
	public void initialize(ServeiRegla anotacio) {
		this.anotacio = anotacio;
	}

	@Override
	public boolean isValid(
			ServeiReglaCommand command,
			ConstraintValidatorContext context) {
		boolean valid = true;
		// comprova que el nom sigui únic
		if (command.getNom() != null) {
			ServeiReglaDto repetit = serveiService.serveiReglaFindByNom(command.getServeiId(), command.getNom());
			if (repetit != null && (command.getId() == null || !command.getId().equals(repetit.getId()))) {
				context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage(anotacio.message() + ".nom.repetit"))
						.addNode("nom")
						.addConstraintViolation();
				valid = false;
			}
		}

		return valid;
	}

}
