package es.caib.pinbal.persist.base.repository;

import es.caib.pinbal.logic.intf.base.exception.ComponentNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Localitzador de repositoris de tipus EmbeddableRepository donat una entitat.
 * 
 * @author Límit Tecnologies
 */
@Component
public class JpaRepositoryLocator implements ApplicationContextAware {

	@Autowired(required = false)
	protected List<JpaRepository<?, ?>> repositories;

	public JpaRepository<?, ?> getEmbeddableRepositoryForEmbeddableEntityClass(
			Class<? extends Persistable<?>> embeddableEntityClass) {
		JpaRepository<?, ?> foundRepository = null;
		for (JpaRepository<?, ?> repository: repositories) {
			Class<?> repositoryEntityClass = GenericTypeResolver.resolveTypeArguments(repository.getClass(), JpaRepository.class)[0];
			if (repositoryEntityClass.equals(embeddableEntityClass)) {
				foundRepository = repository;
				break;
			}
		}
		if (foundRepository != null) {
			return foundRepository;
		} else {
			throw new ComponentNotFoundException(embeddableEntityClass, "repository");
		}
	}

	public JpaRepository<?, ?> getEmbeddableRepositoryForResourceClass(
			Class<? extends Persistable<?>> resourceClass) {
		JpaRepository<?, ?> foundRepository = null;
		for (JpaRepository<?, ?> repository: repositories) {
			Class<?> repositoryEntityClass = GenericTypeResolver.resolveTypeArguments(repository.getClass(), JpaRepository.class)[0];
			Class<?> entityResourceClass = GenericTypeResolver.resolveTypeArguments(repositoryEntityClass, JpaRepository.class)[0];
			if (resourceClass.equals(entityResourceClass)) {
				foundRepository = repository;
				break;
			}
		}
		if (foundRepository != null) {
			return foundRepository;
		} else {
			throw new ComponentNotFoundException(resourceClass, "repository");
		}
	}

	private static ApplicationContext applicationContext;

	public static JpaRepositoryLocator getInstance() {
		return applicationContext.getBean(JpaRepositoryLocator.class);
	}
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		JpaRepositoryLocator.applicationContext = applicationContext;
	}

}
