/**
 * 
 */
package es.caib.pinbal.core.repository;

import es.caib.pinbal.core.model.Entitat;
import es.caib.pinbal.core.model.EntitatServei;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

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

	@Query("select es from EntitatServei es where es.entitat = ?1")
	public List<EntitatServei> findByEntitat(Entitat entitat);
	
	@Query("select es.servei from EntitatServei es where es.entitat.id = ?1")
	public List<String> findServeisByEntitatId(Long entitatId);
	
	@Query("select es from EntitatServei es where es.entitat.id = ?1 and es.servei = ?2")
	public EntitatServei findByEntitatIdAndServei(Long entitatId, String servei);

    List<EntitatServei> findByServei(String serveiCodi);
}
