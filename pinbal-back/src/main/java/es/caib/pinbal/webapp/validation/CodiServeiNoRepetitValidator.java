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

import es.caib.pinbal.core.service.ServeiService;
import es.caib.pinbal.core.service.exception.ServeiNotFoundException;

/**
 * Constraint de validació que controla que no es repeteixi
 * el codi de servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class CodiServeiNoRepetitValidator implements ConstraintValidator<CodiServeiNoRepetit, Object> {

	private String campCodi;
	private String campCreacio;

	@Autowired
	private ServeiService serveiService;



	@Override
	public void initialize(final CodiServeiNoRepetit constraintAnnotation) {
		this.campCodi = constraintAnnotation.campCodi();
		this.campCreacio = constraintAnnotation.campCreacio();
	}

	@Override
	public boolean isValid(final Object value, final ConstraintValidatorContext context) {
		try {
			final String codi = BeanUtils.getProperty(value, campCodi);
			final String creacio = BeanUtils.getProperty(value, campCreacio);
			if (Boolean.parseBoolean(creacio)) {
				try {
					serveiService.findAmbCodiPerAdminORepresentant(codi);
					return false;
				} catch (ServeiNotFoundException ignore) {
				}
			}
			return true;
        } catch (final Exception ex) {
        	LOGGER.error("Error en la validació del servei", ex);
        }
        return false;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(CodiServeiNoRepetitValidator.class);

}
