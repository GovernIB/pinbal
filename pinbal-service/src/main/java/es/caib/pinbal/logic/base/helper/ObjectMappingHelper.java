package es.caib.pinbal.logic.base.helper;

import es.caib.pinbal.logic.intf.base.annotation.ResourceConfig;
import es.caib.pinbal.logic.intf.base.annotation.ResourceField;
import es.caib.pinbal.logic.intf.base.exception.ObjectMappingException;
import es.caib.pinbal.logic.intf.base.model.FileReference;
import es.caib.pinbal.logic.intf.base.model.Resource;
import es.caib.pinbal.logic.intf.base.model.ResourceReference;
import es.caib.pinbal.logic.intf.base.util.TypeUtil;
import es.caib.pinbal.persist.base.entity.ResourceEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Date;

/**
 * Mètodes per al mapeig d'objectes.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Component
public class ObjectMappingHelper {

	/**
	 * Retorna una instància de la classe especificada amb els camps amb el mateix nom mapejats.
	 *
	 * @param source
	 *            l'objecte d'orígen.
	 * @param targetClass
	 *            la classe que es vol obtenir.
	 * @return una nova instància de la classe especificada amb els camps mapejats.
	 * @param <T>
	 *            el tipus que es vol obtenir.
	 * @throws ObjectMappingException
	 *            quan es dona algun error en el mapeig.
	 */
	public <T> T newInstanceMap(
			Object source,
			Class<T> targetClass,
			String... ignoredFields) throws ObjectMappingException {
		if (source != null) {
			try {
				T target = getNewInstance(targetClass);
				if (target != null) {
					map(source, target, ignoredFields);
					return target;
				} else {
					throw new ObjectMappingException(
							source.getClass(),
							targetClass,
							"Couldn't find no args constructor or builder");
				}
			} catch (Exception ex) {
				throw new ObjectMappingException(source.getClass(), targetClass, ex);
			}
		} else {
			return null;
		}
	}

	/**
	 * Mapeja els camps entre l'objecte d'orígen i l'objecte destí que tenguin els mateixos noms.
	 *
	 * @param source
	 *            l'objecte d'orígen.
	 * @param target
	 *            l'objecte de destí.
	 * @throws ObjectMappingException
	 *            quan es dona algun error en el mapeig.
	 */
	public void map(
			Object source,
			Object target,
			String... ignoredFields) throws ObjectMappingException {
		ReflectionUtils.doWithFields(
				source.getClass(),
				sourceField -> {
					ReflectionUtils.makeAccessible(sourceField);
					Field targetField = ReflectionUtils.findField(target.getClass(), sourceField.getName());
					if (targetField != null) {
						if (targetField.getType().isPrimitive() && sourceField.get(source) == null) {
							if (targetField.getType() == boolean.class) {
								targetField.setBoolean(target, false);
							} else if (targetField.getType() == int.class) {
								targetField.setInt(target, 0);
							} else if (targetField.getType() == long.class) {
								targetField.setLong(target, 0L);
							} else if (targetField.getType() == double.class) {
								targetField.setDouble(target, 0.0);
							} else if (targetField.getType() == float.class) {
								targetField.setFloat(target, 0f);
							} else if (targetField.getType() == short.class) {
								targetField.setShort(target, (short)0);
							} else if (targetField.getType() == byte.class) {
								targetField.setByte(target, (byte)0);
							}
						} else if (isSimpleType(sourceField.getType())) {
							boolean isFileReferenceMapping =
									(sourceField.getType().equals(byte[].class) && targetField.getType().equals(FileReference.class)) ||
											(targetField.getType().equals(byte[].class) && sourceField.getType().equals(FileReference.class));
							if (!isFileReferenceMapping) {
								setFieldValue(
										target,
										targetField,
										sourceField.get(source));
							}
						} else if (ResourceEntity.class.isAssignableFrom(sourceField.getType()) && ResourceReference.class.isAssignableFrom(targetField.getType())) {
							ResourceEntity<?, ?> entity = (ResourceEntity<?, ?>)sourceField.get(source);
							ResourceReference<?, ?> resourceReference = null;
							if (entity != null) {
								resourceReference = ResourceReference.toResourceReference(
										(Serializable)entity.getId(),
										getResourceEntityDescription(entity, targetField));
							}
							setFieldValue(
									target,
									targetField,
									resourceReference);
						} else {
							setFieldValue(
									target,
									targetField,
									null);
						}
					}
				},
				field -> ignoredFields == null || !Arrays.asList(ignoredFields).contains(field.getName()));
	}

	/**
	 * Crea un clon de l'objecte per a la lògica onChange.
	 *
	 * @param object
	 *            l'objecte d'orígen.
	 * @return el recurs clonat.
	 */
	public <O> O clone(O object) throws ObjectMappingException {
		if (object != null) {
			try {
				O cln = (O)getNewInstance(object.getClass());
				if (cln != null) {
					ReflectionUtils.doWithFields(object.getClass(), field -> {
						if (!isStaticFinal(field)) {
							try {
								Object value = getFieldValue(object, field.getName());
								ReflectionUtils.makeAccessible(field);
								ReflectionUtils.setField(
										field,
										cln,
										value);
							} catch (NoSuchFieldException ignored) {
							}
						}
					});
					return cln;
				} else {
					throw new ObjectMappingException(
							object.getClass(),
							object.getClass(),
							"Couldn't find no args constructor or builder");
				}
			} catch (Exception ex) {
				throw new ObjectMappingException(
						object.getClass(),
						object.getClass(),
						ex);
			}
		} else {
			return null;
		}
	}

	/**
	 * Retorna una nova instància de la classe especificada. Intenta crear la instància
	 * amb el constructor sense arguments o amb el mètode build() del builder.
	 *
	 * @param targetClass
	 *            la classe de la qual es vol crear la instància.
	 * @return la nova instància de la classe especificada o null si no s'ha pogut crear.
	 * @param <T> el tipus de la classe que es vol instanciar
	 */
	private <T> T getNewInstance(
			Class<T> targetClass) throws InvocationTargetException, InstantiationException, IllegalAccessException {
		Constructor<T> noArgsConstructor = Arrays.stream((Constructor<T>[])targetClass.getConstructors()).
				filter(c -> c.getParameterCount() == 0).
				findFirst().
				orElse(null);
		if (noArgsConstructor != null) {
			return noArgsConstructor.newInstance();
		} else {
			Method builderMethod = ReflectionUtils.findMethod(targetClass, "builder");
			if (builderMethod != null) {
				Class<?> builderReturnType = builderMethod.getReturnType();
				Object builderInstance = ReflectionUtils.invokeMethod(builderMethod, null);
				return (T)ReflectionUtils.invokeMethod(
						ReflectionUtils.findMethod(builderReturnType, "build"),
						builderInstance);
			}
		}
		return null;
	}

	private String getResourceEntityDescription(
			ResourceEntity<?, ?> entity,
			Field targetField) {
		String descriptionFieldName = null;
		Class<? extends Resource<?>> resourceClass = TypeUtil.getArgumentClassFromGenericSuperclass(
				entity.getClass(),
				ResourceEntity.class,
				0);
		ResourceField resourceField = targetField.getAnnotation(ResourceField.class);
		if (resourceField != null && !resourceField.descriptionField().isEmpty()) {
			descriptionFieldName = resourceField.descriptionField();
		} else {
			ResourceConfig resourceConfig = resourceClass.getAnnotation(ResourceConfig.class);
			if (resourceConfig != null) {
				String descriptionField = resourceConfig.descriptionField();
				if (!descriptionField.isEmpty()) {
					descriptionFieldName = descriptionField;
				} else {
					log.warn(
							"Couldn't find description field for resource class {}: ResourceConfig.descriptionField not configured",
							resourceClass.getName());
				}
			} else {
				log.warn(
						"Couldn't find description field for resource class {}: ResourceConfig annotation not found",
						resourceClass.getName());
			}
		}
		if (descriptionFieldName != null) {
			try {
				return (String)getFieldValue(
						entity,
						descriptionFieldName);
			} catch (Exception ex) {
				log.warn(
						"Couldn't find description field {} in entity class {}",
						descriptionFieldName,
						entity.getClass().getName(),
						ex);
			}
		}
		return resourceClass.getSimpleName() + " (id=" + entity.getId() + ")";
	}

	private boolean isSimpleType(Class<?> type) {
		return type.isPrimitive() ||
				Boolean.class == type ||
				Character.class == type ||
				(Serializable.class.isAssignableFrom(type) && !ResourceReference.class.isAssignableFrom(type)) ||
				CharSequence.class.isAssignableFrom(type) ||
				Number.class.isAssignableFrom(type) ||
				Date.class.isAssignableFrom(type) ||
				LocalTime.class.isAssignableFrom(type) ||
				LocalDateTime.class.isAssignableFrom(type) ||
				Enum.class.isAssignableFrom(type);
	}

	private Object getFieldValue(
			Object object,
			String fieldName) throws NoSuchFieldException {
		String getMethodName = methodNameFromFieldName(fieldName, "get");
		Method method = ReflectionUtils.findMethod(object.getClass(), getMethodName);
		if (method != null) {
			return ReflectionUtils.invokeMethod(method, object);
		} else {
			Field field = object.getClass().getDeclaredField(fieldName);
			ReflectionUtils.makeAccessible(field);
			return ReflectionUtils.getField(field, object);
		}
	}

	private void setFieldValue(
			Object object,
			Field field,
			Object value) {
		try {
			String setMethodName = methodNameFromFieldName(field.getName(), "set");
			Method method = ReflectionUtils.findMethod(object.getClass(), setMethodName, field.getType());
			if (method != null) {
				ReflectionUtils.invokeMethod(method, object, value);
			} else {
				ReflectionUtils.makeAccessible(field);
				ReflectionUtils.setField(
					field,
					object,
					value);
			}
		} catch (IllegalArgumentException ex) {
			log.warn("Couldn't set field value (targetClass={}, fieldName={}, valueType={})",
				object.getClass().getName(),
				field.getName(),
				value != null ? value.getClass().getName() : "<null>",
				ex);
		}
	}

	private boolean isStaticFinal(Field field) {
		int modifiers = field.getModifiers();
		return Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers);
	}

	private String methodNameFromFieldName(String fieldName, String prefix) {
		return prefix + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
	}

}
