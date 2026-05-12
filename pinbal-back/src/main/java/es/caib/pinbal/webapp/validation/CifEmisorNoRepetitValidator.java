/**
 * 
 */
package es.caib.pinbal.webapp.validation;

import es.caib.pinbal.core.dto.EmissorCertDto;
import es.caib.pinbal.core.service.ScspService;
import es.caib.pinbal.webapp.command.EmissorCertCommand;
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
public class CifEmisorNoRepetitValidator implements ConstraintValidator<CifEmisorNoRepetit, EmissorCertCommand> {

	@Autowired
	private ScspService scspService;


	private CifEmisorNoRepetit anotacio;

	@Override
	public void initialize(final CifEmisorNoRepetit anotacio) {
		this.anotacio = anotacio;
	}

	@Override
	public boolean isValid(final EmissorCertCommand command, final ConstraintValidatorContext context) {
		try {

			if (command.getCif() == null)
				return true;

			EmissorCertDto emissor = scspService.findEmissorCertByCif(command.getCif());

			if(emissor == null)
				return true;

			if (command.getId() == null || !command.getId().equals(emissor.getId())) {
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("emissor.cif.repetit"))
						.addNode("cif")
						.addConstraintViolation();
				return false;
			}
			return true;
        } catch (final Exception ex) {
        	LOGGER.error("Error en la validació de l'emisor", ex);
        }
        return false;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(CifEmisorNoRepetitValidator.class);

}
