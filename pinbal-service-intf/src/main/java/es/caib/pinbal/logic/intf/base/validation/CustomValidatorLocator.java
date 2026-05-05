package es.caib.pinbal.logic.intf.base.validation;

import es.caib.pinbal.logic.intf.base.exception.ComponentNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Localitzador de validadors personalitzats donada la classe del validador.
 * 
 * @author Límit Tecnologies
 */
@Component
public class CustomValidatorLocator implements ApplicationContextAware {

	@Autowired(required = false)
	protected List<CustomValidator<?>> validators;

	public <T> CustomValidator<T> getCustomValidatorWithClass(Class<? extends CustomValidator<T>> validatorClass) {
		CustomValidator<T> foundValidator = null;
		for (CustomValidator<?> validator: validators) {
			if (validator.getClass().equals(validatorClass)) {
				foundValidator = (CustomValidator<T>)validator;
				break;
			}
		}
		if (foundValidator != null) {
			return foundValidator;
		} else {
			throw new ComponentNotFoundException(validatorClass, "customValidator");
		}
	}

	private static ApplicationContext applicationContext;

	public static CustomValidatorLocator getInstance() {
		return applicationContext.getBean(CustomValidatorLocator.class);
	}
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		CustomValidatorLocator.applicationContext = applicationContext;
	}

}
