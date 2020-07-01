/**
 * 
 */
package es.caib.pinbal.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.pinbal.core.model.Servei;

/**
 * Mètodes per a accedir a tots els serveis.
 * 
 *  @author Limit Tecnologies <limit@limit.es>
 */
public interface ServeiRepository extends JpaRepository<Servei, Long> {
	@Query(	"select" +
			"    s " +
			"from" +
			"    Servei s " +
			"where " +
			"      (:esNullCodi = true or lower(s.codi) like concat('%', lower(:codi), '%')) " +
			"  and (:esNullDescripcio = true or lower(s.descripcio) like concat('%', lower(:descripcio), '%'))" +
			"  and (:esNullEmisor = true or s.scspEmisor.id = :emisor) " +
			"  and (:esNullActiva = true or (:activa = true  and "+ 
			"									(s.scspFechaBaja = null" + 
			"				                     or current_date() <= s.scspFechaBaja))" + 
			"							 or (:activa = false  and " + 
			"									 (s.scspFechaBaja != null" + 
			"				                      and current_date() > s.scspFechaBaja))" +
			") " +
			"")
	public Page<Servei> findByFiltre(
			@Param("esNullCodi") boolean esNullCodi,
			@Param("codi") String codi,
			@Param("esNullDescripcio") boolean esNullDescripcio,
			@Param("descripcio") String descripcio,
			@Param("esNullEmisor") boolean esNullEmisor,
			@Param("emisor") Long emisor,
			@Param("esNullActiva") boolean esNullActiva,
			@Param("activa") Boolean activa,	
			Pageable pageable);
}