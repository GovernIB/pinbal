/**
 * 
 */
package es.caib.pinbal.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import es.caib.pinbal.core.model.EntitatServei;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a una parella entitat-servei que està emmagatzemada a
 * dins la base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface EntitatServeiRepository extends JpaRepository<EntitatServei, Long> {

	@Query("select es from EntitatServei es where es.entitat.id = ?1")
	public List<EntitatServei> findByEntitatId(Long entitatId);

	@Query("select es from EntitatServei es where es.entitat.id = ?1 and es.servei = ?2")
	public EntitatServei findByEntitatIdAndServei(Long entitatId, String servei);

}
