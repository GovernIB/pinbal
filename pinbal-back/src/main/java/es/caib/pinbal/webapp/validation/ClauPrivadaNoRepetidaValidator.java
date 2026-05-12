/**
 * 
 */
package es.caib.pinbal.webapp.validation;

import es.caib.pinbal.core.dto.ClauPrivadaDto;
import es.caib.pinbal.core.service.ScspService;
import es.caib.pinbal.webapp.command.ClauPrivadaCommand;
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
public class ClauPrivadaNoRepetidaValidator implements ConstraintValidator<ClauPrivadaNoRepetida, ClauPrivadaCommand> {

	@Autowired
	private ScspService scspService;


	private ClauPrivadaNoRepetida anotacio;

	@Override
	public void initialize(final ClauPrivadaNoRepetida anotacio) {
		this.anotacio = anotacio;
	}

	@Override
	public boolean isValid(final ClauPrivadaCommand command, final ConstraintValidatorContext context) {
		try {

			boolean isValid = true;

			if (command.getNom() != null && !command.getNom().isEmpty()) {
				ClauPrivadaDto clauPrivada = scspService.findClauPrivadaByNom(command.getNom());

				if (clauPrivada != null && (command.getId() == null || !command.getId().equals(clauPrivada.getId()))) {
					context.disableDefaultConstraintViolation();
					context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("clau.privada.nom.repetit"))
							.addNode("nom")
							.addConstraintViolation();
					isValid = false;
				}
			}

			if (command.getAlies() != null && !command.getAlies().isEmpty()) {
				ClauPrivadaDto clauPrivada = scspService.findClauPrivadaByAlies(command.getAlies());

				if (clauPrivada != null && (command.getId() == null || !command.getId().equals(clauPrivada.getId()))) {
					context.disableDefaultConstraintViolation();
					context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("clau.privada.alies.repetit"))
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

	private static final Logger LOGGER = LoggerFactory.getLogger(ClauPrivadaNoRepetidaValidator.class);

}
