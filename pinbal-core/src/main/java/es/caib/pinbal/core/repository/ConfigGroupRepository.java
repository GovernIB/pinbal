package es.caib.pinbal.core.repository;

import es.caib.pinbal.core.model.ConfigGroup;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus notificacio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ConfigGroupRepository extends JpaRepository<ConfigGroup, String> {

    List<ConfigGroup> findByParentCodeIsNull(Sort sort);
}