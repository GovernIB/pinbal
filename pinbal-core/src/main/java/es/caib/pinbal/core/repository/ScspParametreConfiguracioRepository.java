/**
 * 
 */
package es.caib.pinbal.core.repository;

import es.caib.pinbal.core.model.ScspParametreConfiguracio;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Definició dels mètodes necessaris per a gestionar una entitat de base
 * de dades de la taula SCSP CORE_PARAMETRO_CONFIGURACION.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ScspParametreConfiguracioRepository extends JpaRepository<ScspParametreConfiguracio, String> {

}
