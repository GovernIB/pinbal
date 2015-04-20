/**
 * 
 */
package es.caib.pinbal.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import es.caib.pinbal.core.model.EntitatUsuari;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a una parella entitat-usuari que està emmagatzemada a
 * dins la base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface EntitatUsuariRepository extends JpaRepository<EntitatUsuari, Long> {

	@Query("select eu from EntitatUsuari eu where eu.entitat.id = ?1 and eu.usuari.nif = ?2")
	public EntitatUsuari findByEntitatIdAndUsuariNif(Long entitatId, String usuariNif);

	@Query("select eu from EntitatUsuari eu where eu.entitat.id = ?1 and eu.usuari.codi = ?2")
	public EntitatUsuari findByEntitatIdAndUsuariCodi(Long entitatId, String usuariCodi);

	public List<EntitatUsuari> findByEntitatId(Long entitatId);

	@Query(	"select" +
			"    eu " +
			"from" +
			"    EntitatUsuari eu " +
			"order by " +
			"    eu.entitat, " +
			"    eu.departament")
	public List<EntitatUsuari> findAllOrderByEntitatAndDepartament();

}
