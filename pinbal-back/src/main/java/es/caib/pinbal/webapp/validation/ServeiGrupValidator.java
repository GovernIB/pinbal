/**
 * 
 */
package es.caib.pinbal.webapp.validation;

import es.caib.pinbal.core.dto.ServeiCampGrupDto;
import es.caib.pinbal.core.service.ServeiService;
import es.caib.pinbal.webapp.command.ServeiCampGrupCommand;
import es.caib.pinbal.webapp.helper.MessageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Comprova que el codi d'entorn no estigui repetit.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ServeiGrupValidator implements ConstraintValidator<ServeiGrup, ServeiCampGrupCommand> {

	private ServeiGrup anotacio;
	@Autowired
	private ServeiService serveiService;

	@Override
	public void initialize(ServeiGrup anotacio) {
		this.anotacio = anotacio;
	}

	@Override
	public boolean isValid(
			ServeiCampGrupCommand command,
			ConstraintValidatorContext context) {
		boolean valid = true;
		// comprova que el nom sigui únic
		if (command.getNom() != null) {
			ServeiCampGrupDto repetit = serveiService.serveiCampGrupFindByNom(command.getServei(), command.getNom());
			if (repetit != null && (command.getId() == null || !command.getId().equals(repetit.getId()))) {
				context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage(anotacio.message() + ".nom.repetit"))
						.addNode("nom")
						.addConstraintViolation();
				valid = false;
			}
		}

		return valid;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(CodiProcedimentNoRepetitValidator.class);
}
