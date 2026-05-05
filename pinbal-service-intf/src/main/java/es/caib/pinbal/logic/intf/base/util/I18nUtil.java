package es.caib.pinbal.logic.intf.base.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * Utilitats per a traduir missatges.
 *
 * @author Límit Tecnologies
 */
@Component
public class I18nUtil implements ApplicationContextAware {

	@Autowired
	private MessageSource messageSource;

	public String getI18nMessage(String code, Object... args) {
		return messageSource.getMessage(
				code,
				args,
				LocaleContextHolder.getLocale());
	}

	public String getI18nEnumDescription(
			Field field,
			String enumValue) {
		try {
			String i18nKey = field.getDeclaringClass().getName() + "." + field.getName() + "." + enumValue;
			return messageSource.getMessage(
					i18nKey,
					null,
					LocaleContextHolder.getLocale());
		} catch (NoSuchMessageException ex) {
			try {
				Class<?> fieldType;
				if (TypeUtil.isMultipleFieldType(field)) {
					fieldType = TypeUtil.getMultipleFieldType(field);
				} else {
					fieldType = field.getType();
				}
				String i18nKey = fieldType.getName() + "." + enumValue;
				return messageSource.getMessage(
						i18nKey,
						null,
						LocaleContextHolder.getLocale());
			} catch (NoSuchMessageException ex2) {
				return enumValue;
			}
		}
	}

	private static ApplicationContext applicationContext;
	public static I18nUtil getInstance() {
		return applicationContext.getBean(I18nUtil.class);
	}
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		I18nUtil.applicationContext = applicationContext;
	}

}
