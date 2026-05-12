package es.caib.pinbal.persist.base.repository;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * Implementació del repositori base.
 *
 * @author Límit Tecnologies
 */
@NoRepositoryBean
public class BaseRepositoryImpl<E, PK extends Serializable> extends SimpleJpaRepository<E, PK> implements BaseRepository<E, PK> {

	private final EntityManager entityManager;

	public BaseRepositoryImpl(
			JpaEntityInformation<E, ?> entityInformation,
			EntityManager entityManager) {
		super(entityInformation, entityManager);
		this.entityManager = entityManager;
	}

	@Override
	public void refresh(E entity) {
		entityManager.refresh(entity);
	}

	@Override
	public void detach(E entity) {
		entityManager.detach(entity);
	}

	@Override
	public E merge(E entity) {
		return entityManager.merge(entity);
	}

	@Override
	public void clear() {
		entityManager.clear();
	}

}
