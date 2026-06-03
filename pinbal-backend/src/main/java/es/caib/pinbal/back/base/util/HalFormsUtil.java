package es.caib.pinbal.back.base.util;

import es.caib.pinbal.logic.intf.base.annotation.ResourceField;
import es.caib.pinbal.logic.intf.base.exception.ComponentNotFoundException;
import es.caib.pinbal.logic.intf.base.exception.ResourceNotCreatedException;
import es.caib.pinbal.logic.intf.base.service.MutableResourceService;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Utilitats per a HAL-FORMS.
 * 
 * @author Límit Tecnologies
 */
public class HalFormsUtil {

	public static Map<String, Object> getNewResourceValues(
			Class<?> resourceClass,
			ResourceServiceLocator resourceServiceLocator) throws ResourceNotCreatedException {
		Map<String, Object> values = new HashMap<>();
		if (resourceServiceLocator != null) {
			try {
				MutableResourceService<?, ?> mutableResourceService = resourceServiceLocator.
						getMutableEntityResourceServiceForResourceClass(resourceClass);
				Object newInstance = mutableResourceService.newResourceInstance();
				if (newInstance != null) {
					values.putAll(toMap(newInstance));
				}
			} catch (ComponentNotFoundException ex) {
				try {
					values.putAll(toMap(resourceClass.getDeclaredConstructor().newInstance()));
				} catch (InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
				}
			}
		}
		return values;
	}

	public static <T extends Annotation> T getFieldAnnotation(
			Class<?> resourceClass,
			String fieldName,
			Class<T> annotationClass) {
		try {
			return resourceClass.getDeclaredField(fieldName).getAnnotation(annotationClass);
		} catch (NoSuchFieldException e) {
			return null;
		}
	}

	public static boolean isOnChangeActive(Class<?> resourceClass, String fieldName) {
		Field field = ReflectionUtils.findField(resourceClass, fieldName);
		if (field != null) {
			ResourceField resourceField = field.getAnnotation(ResourceField.class);
			return resourceField != null && resourceField.onChangeActive();
		} else {
			return false;
		}
	}

	private static Map<String, Object> toMap(Object object) {
		Map<String, Object> map = new HashMap<>();
		Field[] fields = object.getClass().getDeclaredFields();
		for (Field field: fields) {
			field.setAccessible(true);
			try {
				Object value = field.get(object);
				if (value != null) {
					map.put(field.getName(), value);
				}
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		return map;
	}

}
