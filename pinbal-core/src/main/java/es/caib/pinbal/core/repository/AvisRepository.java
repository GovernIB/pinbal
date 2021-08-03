/**
 * 
 */
package es.caib.pinbal.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.pinbal.core.model.Avis;

/**
 * Repositori per gestionar una entitat de base de dades del tipus avís.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface AvisRepository extends JpaRepository<Avis, Long> {

	@Query(	"from " +
			"    Avis a " +
			"where " +
			"    a.actiu = true " +
			"and a.dataInici <= :currentDate " +
			"and a.dataFinal >= :currentDate")
	List<Avis> findActive(@Param("currentDate") Date currentDate);	
}
