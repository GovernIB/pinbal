/**
 * 
 */
package es.caib.pinbal.core.repository;

import es.caib.pinbal.core.model.Servei;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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
			")" +
			"  and (:esNullScspVersionEsquema = true or lower(s.scspVersionEsquema) like concat('%', lower(:scspVersionEsquema), '%'))"
			)
	public Page<Servei> findByFiltre(
			@Param("esNullCodi") boolean esNullCodi,
			@Param("codi") String codi,
			@Param("esNullDescripcio") boolean esNullDescripcio,
			@Param("descripcio") String descripcio,
			@Param("esNullEmisor") boolean esNullEmisor,
			@Param("emisor") Long emisor,
			@Param("esNullActiva") boolean esNullActiva,
			@Param("activa") Boolean activa,
			@Param("esNullScspVersionEsquema") boolean esNullScspVersionEsquema,
			@Param("scspVersionEsquema") String scspVersionEsquema,
			Pageable pageable);
	

	@Query(	"select s " +
			"  from Servei s " +
			" where s.codi in (:serveiIds) " +
			"  and (s.codi in :serveisProcedimentActiusIds) " +
			"  and (:esNullCodi = true or lower(s.codi) like concat('%', lower(:codi), '%')) " +
			"  and (:esNullDescripcio = true or lower(s.descripcio) like concat('%', lower(:descripcio), '%'))" +
			"  and (:esNullEmisor = true or s.scspEmisor.id = :emisor) " +
			"  and (:esNullActiu = true or (:actiu = true and (select count(sc.servei) from ServeiConfig sc where sc.servei = s.codi and sc.actiu = true) > 0) " +
			"							or (:actiu = false and (select count(sc.servei) from ServeiConfig sc where sc.servei = s.codi and sc.actiu = true) = 0)) "
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
			@Param("esNullActiu") boolean esNullActiu,
			@Param("actiu") Boolean actiu,
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
	
	@Query("select s " +
			" from Servei s " +
			"where (lower(s.codi) like concat('%', lower(:text), '%') " +
			"  		or lower(s.descripcio) like concat('%', lower(:text), '%'))")
	public List<Servei> findByCodiAndDescripcioLikeText(@Param("text") String text);

	@Query( "select s " +
			"  from Servei s, " +
			" 		ServeiConfig sc " +
			" where s.codi = sc.servei " +
//			"   and sc.actiu = true " +
			"	and s.codi not in (select ps.servei from ProcedimentServei ps where ps.procediment.id = :procedimentId and ps.actiu = true)" +
			"order by s.codi asc")
	List<Servei> findActiuNotInProcediment(@Param("procedimentId") Long procedimentId);


	// Serveis - Client

	@Query("select new es.caib.pinbal.client.serveis.Servei(" +
			"	s.codi, " +
			"	s.descripcio, " +
			"	sc.actiu " +
			") " +
			"  from Servei s, " +
			" 		ServeiConfig sc " +
			" where s.codi = sc.servei ")
	List<es.caib.pinbal.client.serveis.Servei> findAllServeisClient();

	@Query("select new es.caib.pinbal.client.serveis.Servei(" +
			"	s.codi, " +
			"	s.descripcio, " +
			"	sc.actiu " +
			") " +
			"  from Servei s, " +
			" 		ServeiConfig sc," +
			" 		EntitatServei es " +
			" where s.codi = sc.servei" +
			"	and es.servei = s.codi " +
			"   and es.entitat.codi = :entitatCodi")
	List<es.caib.pinbal.client.serveis.Servei> findServeisClientByEntitatCodi(@Param("entitatCodi") String entitatCodi);

	@Query("select new es.caib.pinbal.client.serveis.Servei(" +
			"	s.codi, " +
			"	s.descripcio, " +
			"	sc.actiu " +
			") " +
			"  from Servei s, " +
			" 		ServeiConfig sc," +
			" 		ProcedimentServei ps " +
			" where s.codi = sc.servei" +
			"	and ps.servei = s.codi " +
			"   and ps.procediment.codi = :procedimentCodi")
	List<es.caib.pinbal.client.serveis.Servei> findServeisClientByProcedimentCodi(@Param("procedimentCodi") String procedimentCodi);
}
