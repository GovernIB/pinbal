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
import es.caib.pinbal.webapp.helper.MessageHelper;

/**
 * Constraint de validació que controla que no es repeteixi
 * el codi d'entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CifEntitatNoRepetitValidator implements ConstraintValidator<CifEntitatNoRepetit, Object> {

	private String campId;
	private String campCif;

	@Autowired
	private EntitatService entitatService;



	@Override
	public void initialize(final CifEntitatNoRepetit constraintAnnotation) {
		this.campId = constraintAnnotation.campId();
		this.campCif = constraintAnnotation.campCif();
	}

	@Override
	public boolean isValid(final Object value, final ConstraintValidatorContext context) {
		try {
			boolean isValid = true;
			final String id = BeanUtils.getProperty(value, campId);
			final String cif = BeanUtils.getProperty(value, campCif);
			EntitatDto entitatByCif = entitatService.findByCif(cif);
			
			if(entitatByCif == null) {
				isValid = true;
			}else {
				if(id == null) 
					isValid = false;
				else
					isValid = id.equals(entitatByCif.getId().toString());
			}
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(MessageHelper.getInstance().getMessage("entitat.cif.repetit"))
			                                .addNode("cif")
			                                .addConstraintViolation();
			return isValid;
        } catch (final Exception ex) {
        	LOGGER.error("Error en la validació de l'usuari", ex);
        }
        return false;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(CodiEntitatNoRepetitValidator.class);

}
