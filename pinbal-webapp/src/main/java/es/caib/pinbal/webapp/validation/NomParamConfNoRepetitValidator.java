/**
 * 
 */
package es.caib.pinbal.webapp.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import es.caib.pinbal.core.dto.ParamConfDto;
import es.caib.pinbal.core.service.ScspService;
import es.caib.pinbal.webapp.command.ParamConfCommand;

/**
 * Constraint de validació que controla que no es repeteixi
 * el nom del paràmetre de configuració
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class NomParamConfNoRepetitValidator implements ConstraintValidator<NomParamConfNoRepetit, Object> {
	
	private String campNom;
	
	@Autowired
	private ScspService scspService;
	
	
	@Override
	public void initialize(final NomParamConfNoRepetit constraintAnnotation) {
		this.campNom = constraintAnnotation.campNom();
	}
	
	@Override
	public boolean isValid(final Object value, final ConstraintValidatorContext context) {
		try {
			ParamConfCommand command = (ParamConfCommand) value;
			final String nom = BeanUtils.getProperty(value, campNom);
			ParamConfDto dto = scspService.findByNom(nom);
			if (dto == null) {
				return true;
			} else {
				return !command.isForcreate();
			}
        } catch (final Exception ex) {
        	LOGGER.error("Error en la validació del paràmetre de configuració", ex);
        }
        return false;
	}
	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(NomParamConfNoRepetitValidator.class);

}
