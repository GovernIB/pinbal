/**
 * 
 */
package es.caib.pinbal.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.pinbal.core.model.Entitat;
import es.caib.pinbal.core.model.Procediment;
import es.caib.pinbal.core.model.ProcedimentServei;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a una parella procediment-servei que està emmagatzemada a
 * dins la base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ProcedimentServeiRepository extends JpaRepository<ProcedimentServei, Long> {

	@Query(	"select " +
			"    ps " +
			"from " +
			"    ProcedimentServei ps " +
			"where " +
			"    ps.procediment.id = ?1 " +
			"and ps.servei = ?2")
	public ProcedimentServei findByProcedimentIdAndServei(
			Long procedimentId,
			String servei);

	@Query(	"select " +
			"    ps " +
			"from " +
			"    ProcedimentServei ps " +
			"where " +
			"    ps.procediment.entitat.id = ?1")
	public List<ProcedimentServei> findByEntitatId(Long entitatId);

	@Query(	"select " +
			"    ps " +
			"from " +
			"    ProcedimentServei ps " +
			"where " +
			"    ps.procediment.entitat.id = ?1 " +
			"and ps.procediment.id = ?2")
	public List<ProcedimentServei> findByEntitatIdAndProcedimentId(
			Long entitatId,
			Long procedimentId);
	
	@Query(	"select " +
			"    ps " +
			"from " +
			"    ProcedimentServei ps " +
			"where " +
			"    ps.procediment.id = ?1")
	public List<ProcedimentServei> findByProcedimentId(
			Long procedimentId);

	@Query(	"select " +
			"    ps " +
			"from " +
			"    ProcedimentServei ps " +
			"where " +
			"    ps.procediment.entitat.id = ?1 " +
			"and ps.procediment.actiu = true " +
			"and ps.actiu = true")
	public List<ProcedimentServei> findActiusByEntitatId(Long entitatId);

	@Query(	"select " +
			"    ps " +
			"from " +
			"    ProcedimentServei ps " +
			"where " +
			"    ps.procediment.entitat.id = ?1 " +
			"and ps.procediment.actiu = true " +
			"and ps.actiu = true " +
			"and ps.servei = ?2")
	public List<ProcedimentServei> findActiusByEntitatIdAndServei(
			Long entitatId,
			String serveiCodi);

	@Query(	"select " +
			"    ps " +
			"from " +
			"    ProcedimentServei ps " +
			"where " +
			"    ps.procediment.entitat.id in (" +
			"        select " +
			"            eu.entitat " +
			"        from " +
			"            EntitatUsuari eu " +
			"        where " +
			"            eu.usuari.codi = ?1)")
	public List<ProcedimentServei> findByUsuariAmbAccesEntitat(String usuariCodi);
	
	@Query(	"select" +
			"    ps " +
			"from" +
			"    ProcedimentServei ps " +
			"where " +
			"      ps.procediment.entitat = :entitat " +
			"  and ps.procediment = :procediment " +
			") " +
			"")
	public List<ProcedimentServei> findServeisProcediment(
			@Param("entitat") Entitat entitat,
			@Param("procediment") Procediment procediment);

	@Query(	"select" +
			"    ps.servei " +
			"from" +
			"    ProcedimentServei ps " +
			"where " +
			"  ps.actiu = true" +
			"  and ps.procediment.entitat = :entitat " +
			"  and ps.procediment = :procediment " +
			") " +
			"")
	public List<String> findServeisProcedimenActiustServeisIds(
			@Param("entitat") Entitat entitat,
			@Param("procediment") Procediment procediment);
}
