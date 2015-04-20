/**
 * 
 */
package es.caib.pinbal.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.pinbal.core.model.ServeiConfig;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un serveiConfig que està emmagatzemat a dins la
 * base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ServeiConfigRepository extends JpaRepository<ServeiConfig, Long> {

	ServeiConfig findByServei(String servei);

}
