package es.caib.pinbal.logic.intf.base.util;

import es.caib.pinbal.logic.intf.base.exception.ResourceFieldNotFoundException;
import es.caib.pinbal.logic.intf.base.model.Resource;
import es.caib.pinbal.logic.intf.base.model.ResourceReference;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import javax.validation.constraints.NotNull;
import java.lang.reflect.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Utilitats per a obtenir informació dels tipus Java via reflection.
 * 
 * @author Límit Tecnologies
 */
public class TypeUtil {

	public static <R> Class<R> getArgumentClassFromGenericSuperclass(
			Class<?> clazz,
			Class<?> superClass,
			int index) {
		return (Class<R>)Objects.requireNonNull(
				GenericTypeResolver.resolveTypeArguments(
						clazz,
						superClass != null ? superClass : clazz))[index];
	}
	public static <R> Class<R> getArgumentClassFromGenericSuperclass(Class<?> clazz, int index) {
		return getArgumentClassFromGenericSuperclass(clazz, null, index);
	}

	public static Class<? extends Resource<?>> getReferencedResourceClass(Field field) {
		Class<? extends Resource<?>> referencedClass = null;
		if (Collection.class.isAssignableFrom(field.getType()) && field.getGenericType() instanceof ParameterizedType) {
			ParameterizedType resourceFieldGenericType = (ParameterizedType)field.getGenericType();
			Type[] resourceFieldArgTypes = resourceFieldGenericType.getActualTypeArguments();
			ParameterizedType parameterizedType = (ParameterizedType)resourceFieldArgTypes[0];
			referencedClass = (Class<? extends Resource<?>>)parameterizedType.getActualTypeArguments()[0];
		} else {
			boolean isArrayType = field.getType().isArray();
			Class<?> fieldType = isArrayType ? field.getType().getComponentType() : field.getType();
			if (ResourceReference.class.isAssignableFrom(fieldType)) {
				ParameterizedType parameterizedType;
				if (isArrayType) {
					parameterizedType = (ParameterizedType)((GenericArrayType)field.getGenericType()).getGenericComponentType();
				} else {
					parameterizedType = (ParameterizedType)field.getGenericType();
				}
				referencedClass = (Class<? extends Resource<?>>)parameterizedType.getActualTypeArguments()[0];
			} else if (Resource.class.isAssignableFrom(fieldType)) {
				referencedClass = (Class<? extends Resource<?>>)fieldType;
			}
		}
		return referencedClass;
	}

	public static String getMethodSuffixFromFieldName(String fieldName) {
		return fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
	}

	public static String getMethodSuffixFromField(Field field) {
		return field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
	}

	public static boolean isNotNullField(Field field) {
		return field.getAnnotation(NotNull.class) != null;
	}

	public static boolean isMultipleFieldType(Field field) {
		return isArrayFieldType(field) || isCollectionFieldType(field);
	}

	public static Class<?> getMultipleFieldType(Field field) {
		if (isArrayFieldType(field)) {
			return getArrayFieldType(field);
		} else if (isCollectionFieldType(field)) {
			return getCollectionFieldType(field);
		} else {
			return null;
		}
	}

	public static Class<?> getFieldTypeMultipleAware(Field field) {
		Class<?> fieldType = field.getType();
		if (TypeUtil.isMultipleFieldType(field)) {
			fieldType = TypeUtil.getMultipleFieldType(field);
		}
		return fieldType;
	}

	public static boolean isArrayFieldType(Field field) {
		return field.getType().isArray();
	}

	public static boolean isCollectionFieldType(Field field) {
		return Collection.class.isAssignableFrom(field.getType()) && field.getGenericType() instanceof ParameterizedType;
	}

	public static Class<?> getArrayFieldType(Field field) {
		if (field.getType().isArray()) {
			//return Array.newInstance(field.getType(), 0).getClass();
			return field.getType().getComponentType();
		} else {
			return null;
		}
	}

	public static Class<?> getCollectionFieldType(Field field) {
		if (isCollectionFieldType(field)) {
			ParameterizedType resourceFieldGenericType = (ParameterizedType)field.getGenericType();
			Type[] resourceFieldArgTypes = resourceFieldGenericType.getActualTypeArguments();
			if (resourceFieldArgTypes[0] instanceof ParameterizedType) {
				ParameterizedType parameterizedType = (ParameterizedType)resourceFieldArgTypes[0];
				return (Class<?>)parameterizedType.getRawType();
			} else {
				return (Class<?>)resourceFieldArgTypes[0];
			}
		} else {
			return null;
		}
	}

	public static <C> C getFieldOrGetterValue(String fieldName, Object target) {
		Field field = ReflectionUtils.findField(target.getClass(), fieldName);
		if (field == null) {
			String methodSuffix = getMethodSuffixFromField(field);
			String getMethodName = "get" + methodSuffix;
			Method getMethod = ReflectionUtils.findMethod(target.getClass(), getMethodName);
			if (getMethod != null) {
				return (C) ReflectionUtils.invokeMethod(getMethod, target);
			} else {
				throw new ResourceFieldNotFoundException(target.getClass(), fieldName);
			}
		} else {
			ReflectionUtils.makeAccessible(field);
			return (C)ReflectionUtils.getField(field, target);
		}
	}

	public static <C> C getFieldOrGetterValue(Field field, Object target) {
		String methodSuffix = TypeUtil.getMethodSuffixFromField(field);
		String getMethodName = "get" + methodSuffix;
		Method getMethod = ReflectionUtils.findMethod(target.getClass(), getMethodName);
		if (getMethod != null) {
			return (C)ReflectionUtils.invokeMethod(getMethod, target);
		} else {
			ReflectionUtils.makeAccessible(field);
			return (C)ReflectionUtils.getField(field, target);
		}
	}

	public static <C> C getFieldOrGetterValue(String fieldName, Object target, Class<C> expectedType) {
		Object value = null;
		Field field = ReflectionUtils.findField(target.getClass(), fieldName);
		if (field == null) {
			String methodSuffix = getMethodSuffixFromFieldName(fieldName);
			String getMethodName = "get" + methodSuffix;
			Method getMethod = ReflectionUtils.findMethod(target.getClass(), getMethodName);
			if (getMethod != null) {
				value = ReflectionUtils.invokeMethod(getMethod, target);
				if (value != null && !expectedType.isInstance(value)) {
					throw new IllegalStateException(
							"Returned value from method " + getMethod + " is not an instance of " + expectedType.getName());
				}
			} else {
				throw new ResourceFieldNotFoundException(target.getClass(), fieldName);
			}
		} else {
			ReflectionUtils.makeAccessible(field);
			value = ReflectionUtils.getField(field, target);
			if (value != null && !expectedType.isInstance(value)) {
				throw new IllegalStateException(
						"Returned value from field " + field + " is not an instance of " + expectedType.getName());
			}
		}
		return (C)value;
	}

	public static <C> C getFieldOrGetterValue(Field field, Object target, Class<C> expectedType) {
		Object value = null;
		String methodSuffix = getMethodSuffixFromField(field);
		String getMethodName = "get" + methodSuffix;
		Method getMethod = ReflectionUtils.findMethod(target.getClass(), getMethodName);
		if (getMethod != null) {
			value = ReflectionUtils.invokeMethod(getMethod, target);
			if (value != null && !expectedType.isInstance(value)) {
				throw new IllegalStateException(
						"Returned value from method " + getMethod + " is not an instance of " + expectedType.getName());
			}
		} else {
			ReflectionUtils.makeAccessible(field);
			value = ReflectionUtils.getField(field, target);
			if (value != null && !expectedType.isInstance(value)) {
				throw new IllegalStateException(
						"Returned value from field " + field + " is not an instance of " + expectedType.getName());
			}
		}
		return (C)value;
	}

	public static void setFieldOrSetterValue(Field field, Object target, Object value) {
		String methodSuffix = TypeUtil.getMethodSuffixFromField(field);
		String setMethodName = "set" + methodSuffix;
		Method setMethod = ReflectionUtils.findMethod(target.getClass(), setMethodName);
		if (setMethod != null) {
			ReflectionUtils.invokeMethod(setMethod, target, value);
		} else {
			ReflectionUtils.makeAccessible(field);
			ReflectionUtils.setField(field, target, value);
		}
	}

	public static <V> V getFieldValue(Object target, String fieldName) {
		Field field = ReflectionUtils.findField(target.getClass(), fieldName);
		if (field != null) {
			ReflectionUtils.makeAccessible(field);
			return (V)ReflectionUtils.getField(field, target);
		} else {
			throw new ResourceFieldNotFoundException(target.getClass(), "fieldName");
		}
	}

	public static <T> Set<Class<T>> findAssignableClasses(Class<T> assignableType, String... packagesToScan) {
		ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
		provider.addIncludeFilter(new AssignableTypeFilter(assignableType));
		Set<Class<T>> ret = new HashSet<>();
		for (String pkg: packagesToScan) {
			provider.findCandidateComponents(pkg).stream().
					map(BeanDefinition::getBeanClassName).
					map(n -> {
						try {
							return ClassUtils.forName(n, null);
						} catch (ClassNotFoundException ex) {
							return null;
						}
					}).
					filter(Objects::nonNull).
					forEach(c -> ret.add(((Class<T>)c)));
		}
		return ret;
	}

}
