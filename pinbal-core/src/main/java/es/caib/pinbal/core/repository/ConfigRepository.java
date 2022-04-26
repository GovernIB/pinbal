/**
 * 
 */
package es.caib.pinbal.core.repository;

import es.caib.pinbal.core.model.Config;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades del tipus notificacio.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ConfigRepository extends JpaRepository<Config, String> {

}