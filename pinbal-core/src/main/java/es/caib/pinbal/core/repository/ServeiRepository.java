/**
 * 
 */
package es.caib.pinbal.core.repository;

import java.util.List;

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
			"  and (:esNullActiva = true or (:activa = true and (select count(sc.servei) from ServeiConfig sc where sc.servei = s.codi and sc.actiu = true) > 0) " +
			" 							 or (:activa = false and (select count(sc.servei) from ServeiConfig sc where sc.servei = s.codi and sc.actiu = true) = 0) " +
			")")
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
	
//	@Query(	"select" +
//			"    s " +
//			"from" +
//			"    Servei s " +
//			"where " +
//			"    s.codi in (:serveiIds) " +
//			"  and (:esNullCodi = true or lower(s.codi) like concat('%', lower(:codi), '%')) " +
//			"  and (:esNullDescripcio = true or lower(s.descripcio) like concat('%', lower(:descripcio), '%'))" +
//			"  and (:esNullEmisor = true or s.scspEmisor.id = :emisor) " +
//			"  and (:esNullActiva = true or (:activa = true  and "+ 
//			"									(s.scspFechaBaja = null" + 
//			"				                     or current_date() <= s.scspFechaBaja))" + 
//			"							 or (:activa = false  and " + 
//			"									 (s.scspFechaBaja != null" + 
//			"				                      and current_date() > s.scspFechaBaja))" +
//			") "
//			)
//	public Page<Servei> findByFiltre(
//			@Param("serveiIds") List<String> serveiIds,	
//			@Param("esNullCodi") boolean esNullCodi,
//			@Param("codi") String codi,
//			@Param("esNullDescripcio") boolean esNullDescripcio,
//			@Param("descripcio") String descripcio,
//			@Param("esNullEmisor") boolean esNullEmisor,
//			@Param("emisor") Long emisor,
//			@Param("esNullActiva") boolean esNullActiva,
//			@Param("activa") Boolean activa,	
//			Pageable pageable);
	
	@Query(	"select" +
			"    s " +
			"from" +
			"    Servei s " +
			"where " +
			"    s.codi in (:serveiIds) " +
			"  and (:esNullCodi = true or lower(s.codi) like concat('%', lower(:codi), '%')) " +
			"  and (:esNullDescripcio = true or lower(s.descripcio) like concat('%', lower(:descripcio), '%'))" +
			"  and (:esNullEmisor = true or s.scspEmisor.id = :emisor) " +
			"  and (:esNullEmisor = true or s.scspEmisor.id = :emisor) " +
			"  and (:esNullActiva = true or (:activa = true  and s.codi in :serveisProcedimentActiusIds) " +
			"   						 or (:activa = false  and s.codi not in :serveisProcedimentActiusIds)) " +
			"  and (select count(sc.servei) from ServeiConfig sc where sc.servei = s.codi and sc.actiu = true) > 0 " + 
			") "
			)
	public Page<Servei> findByFiltre(
			@Param("serveiIds") List<String> serveiIds,
			@Param("serveisProcedimentActiusIds") List<String> serveisProcedimentActiusIds,	
			@Param("esNullCodi") boolean esNullCodi,
			@Param("codi") String codi,
			@Param("esNullDescripcio") boolean esNullDescripcio,
			@Param("descripcio") String descripcio,
			@Param("esNullEmisor") boolean esNullEmisor,
			@Param("emisor") Long emisor,
			@Param("esNullActiva") boolean esNullActiva,
			@Param("activa") Boolean activa,	
			Pageable pageable);
	
	@Query(	"select" +
			"    s " +
			"from" +
			"    Servei s " +
			"where " +
			"    s.codi = ?1 ")
	public List<Servei> findByCode(
			String codi
			);
	
}
