package es.caib.pinbal.logic.base.helper;

import es.caib.pinbal.logic.intf.base.model.Resource;
import es.caib.pinbal.logic.intf.base.model.ResourceReference;
import es.caib.pinbal.logic.intf.base.util.CompositePkUtil;
import es.caib.pinbal.persist.base.repository.JpaRepositoryLocator;
import org.hibernate.Hibernate;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Mètodes per a transformar referències a recursos en les seves entitats de base de dades.
 *
 * @author Límit Tecnologies
 */
@Component
public class ResourceReferenceToEntityHelper {

	public <R> Map<String, Persistable<?>> getReferencedEntitiesForResource(
			R resource,
			Class<?> entityClass) {
		Map<String, Persistable<?>> referencedEntities = new HashMap<>();
		ReflectionUtils.doWithFields(entityClass, field -> {
			// Es modifica el valor de cada camp de l'entitat que és de tipus de Persistable
			// amb la referencia especificada al resource.
			if (Persistable.class.isAssignableFrom(field.getType())) {
				Class<? extends Persistable<?>> persistableFieldType = (Class<? extends Persistable<?>>)field.getType();
				Persistable<?> referencedEntity = getReferencedEntityForResourceField(
						resource,
						field.getName(),
						persistableFieldType);
				if (referencedEntity != null) {
					referencedEntities.put(field.getName(), referencedEntity);
				}
			}
		});
		return referencedEntities;
	}

	private <R> Persistable<?> getReferencedEntityForResourceField(
			R resource,
			String fieldName,
			Class<? extends Persistable<?>> entityClass) {
		Field field = ReflectionUtils.findField(resource.getClass(), fieldName);
		Object referenceId = null;
		if (field != null) {
			field.setAccessible(true);
			if (ResourceReference.class.isAssignableFrom(field.getType())) {
				ResourceReference<?, ?> fieldValue = (ResourceReference<?, ?>)ReflectionUtils.getField(field, resource);
				if (fieldValue != null) {
					referenceId = fieldValue.getId();
				}
			} else if (Resource.class.isAssignableFrom(field.getType())) {
				Resource<?> fieldValue = (Resource<?>)ReflectionUtils.getField(field, resource);
				if (fieldValue != null) {
					referenceId = fieldValue.getId();
				}
			}
		}
		if (referenceId != null) {
			JpaRepository<?, ?> referencedRepository = JpaRepositoryLocator.getInstance().getEmbeddableRepositoryForEmbeddableEntityClass(
					entityClass);
			Class<?> pkClass = getRepositoryPkClass(referencedRepository.getClass());
			boolean isCompositePk = CompositePkUtil.getInstance().isCompositePkClass(pkClass);
			if (isCompositePk) {
				referenceId = CompositePkUtil.getInstance().getCompositePkFromSerializedId((String)referenceId, pkClass);
			}
			Method getByIdMethod = ReflectionUtils.findMethod(referencedRepository.getClass(), "getReferenceById", Object.class);
			if (getByIdMethod != null) {
				Object referencedObject = ReflectionUtils.invokeMethod(
						getByIdMethod,
						referencedRepository,
						referenceId);
				return (Persistable<?>) Hibernate.unproxy(referencedObject);
			}
		}
		return null;
	}

	private Class<?> getRepositoryPkClass(Class<?> repositoryClass) {
		return GenericTypeResolver.resolveTypeArguments(repositoryClass, JpaRepository.class)[1];
	}

}
