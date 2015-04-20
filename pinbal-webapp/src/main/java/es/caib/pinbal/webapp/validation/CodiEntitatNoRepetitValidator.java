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

import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.service.EntitatService;

/**
 * Constraint de validació que controla que no es repeteixi
 * el codi d'entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CodiEntitatNoRepetitValidator implements ConstraintValidator<CodiEntitatNoRepetit, Object> {

	private String campId;
	private String campCodi;

	@Autowired
	private EntitatService entitatService;



	@Override
	public void initialize(final CodiEntitatNoRepetit constraintAnnotation) {
		this.campId = constraintAnnotation.campId();
		this.campCodi = constraintAnnotation.campCodi();
	}

	@Override
	public boolean isValid(final Object value, final ConstraintValidatorContext context) {
		try {
			final String id = BeanUtils.getProperty(value, campId);
			final String codi = BeanUtils.getProperty(value, campCodi);
			EntitatDto entitat = entitatService.findByCodi(codi);
			if (entitat == null) {
				return true;
			} else {
				if (id == null)
					return false;
				else
					return id.equals(entitat.getId().toString());
			}
        } catch (final Exception ex) {
        	LOGGER.error("Error en la validació de l'usuari", ex);
        }
        return false;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(CodiEntitatNoRepetitValidator.class);

}
