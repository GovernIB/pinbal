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

import es.caib.pinbal.core.dto.ProcedimentDto;
import es.caib.pinbal.core.service.ProcedimentService;

/**
 * Constraint de validació que controla que no es repeteixi
 * el codi de procediment dins d'una determinada entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CodiProcedimentNoRepetitValidator implements ConstraintValidator<CodiProcedimentNoRepetit, Object> {

	private String campId;
	private String campEntitatId;
	private String campCodi;

	@Autowired
	private ProcedimentService procedimentService;



	@Override
	public void initialize(final CodiProcedimentNoRepetit constraintAnnotation) {
		this.campId = constraintAnnotation.campId();
		this.campEntitatId = constraintAnnotation.campEntitatId();
		this.campCodi = constraintAnnotation.campCodi();
	}

	@Override
	public boolean isValid(final Object value, final ConstraintValidatorContext context) {
		try {
			final String id = BeanUtils.getProperty(value, campId);
			final String entitatId = BeanUtils.getProperty(value, campEntitatId);
			final String codi = BeanUtils.getProperty(value, campCodi);
			ProcedimentDto procediment = procedimentService.findAmbEntitatICodi(new Long(entitatId), codi);
			if (procediment == null) {
				return true;
			} else {
				if (id == null)
					return false;
				else
					return id.equals(procediment.getId().toString());
			}
        } catch (final Exception ex) {
        	LOGGER.error("Error en la validació de l'usuari", ex);
        }
        return false;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(CodiProcedimentNoRepetitValidator.class);

}