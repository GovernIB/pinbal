/**
 * 
 */
package es.caib.pinbal.webapp.validation;

import es.caib.pinbal.core.dto.ClauPublicaDto;
import es.caib.pinbal.core.service.ScspService;
import es.caib.pinbal.webapp.command.ClauPublicaCommand;
import es.caib.pinbal.webapp.helper.MessageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Constraint de validació que controla que no es repeteixi
 * el codi d'entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ClauPublicaNoRepetidaValidator implements ConstraintValidator<ClauPublicaNoRepetida, ClauPublicaCommand> {

	@Autowired
	private ScspService scspService;


	private ClauPublicaNoRepetida anotacio;

	@Override
	public void initialize(final ClauPublicaNoRepetida anotacio) {
		this.anotacio = anotacio;
	}

	@Override
	public boolean isValid(final ClauPublicaCommand command, final ConstraintValidatorContext context) {
		try {

			boolean isValid = true;

			if (command.getNom() != null && !command.getNom().isEmpty()) {
				ClauPublicaDto clauPublica = scspService.findClauPublicaByNom(command.getNom());

                if (clauPublica != null && (command.getId() == null || !command.getId().equals(clauPublica.getId()))) {
					context.disableDefaultConstraintViolation();
					context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("clau.publica.nom.repetit"))
							.addNode("nom")
							.addConstraintViolation();
					isValid = false;
				}
			}

			if (command.getAlies() != null && !command.getAlies().isEmpty()) {
				ClauPublicaDto clauPublica = scspService.findClauPublicaByAlies(command.getAlies());

				if (clauPublica != null && (command.getId() == null || !command.getId().equals(clauPublica.getId()))) {
					context.disableDefaultConstraintViolation();
					context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("clau.publica.alies.repetit"))
							.addNode("alies")
							.addConstraintViolation();
					isValid = false;
				}
			}

			return isValid;
        } catch (final Exception ex) {
        	LOGGER.error("Error en la validació de la clau privada", ex);
        }
        return false;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(ClauPublicaNoRepetidaValidator.class);

}
