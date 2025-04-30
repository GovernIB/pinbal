/**
 * 
 */
package es.caib.pinbal.core.repository;

import es.caib.pinbal.core.model.Entitat;
import es.caib.pinbal.core.model.Procediment;
import es.caib.pinbal.core.model.ProcedimentServei;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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
			"    ps.procediment.entitat.id = ?1 " +
			"and ps.procediment.codi = ?2 " +
			"and ps.servei = ?3")
	ProcedimentServei findByEntitatIdProcedimentCodiAndServeiCodi(Long entitatId, String procedimentCodi, String serveiCodi);

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
			"    ps.procediment.id = ?1 " +
			"and ps.actiu = true")
	public List<ProcedimentServei> findActiusByProcedimentId(
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
			"  and ps.procediment = :procediment ")
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
			"  and ps.procediment = :procediment ")
	public List<String> findServeisProcedimenActiustServeisIds(
			@Param("entitat") Entitat entitat,
			@Param("procediment") Procediment procediment);
	
	@Query(	"select " +
			"    ps " +
			"from " +
			"    ProcedimentServei ps " +
			"where " +
			"    ps.procediment.actiu = true " +
			"and ps.actiu = true")
	public List<ProcedimentServei> findAllActius();
	
	@Query(	"select " +
			"    ps " +
			"from " +
			"    ProcedimentServei ps " +
			"where " +
			"    ps.procediment.actiu = true " +
			"  and ps.actiu = true" +
			"  and ps.procediment.entitat.id = :entitatId" + 
			"  and (:esNullFiltreOrganGestorId = true or ps.procediment.organGestor.id = :filtreOrganGestorId)" +
			"  and (:esNullFiltreProcedimentId = true or ps.procediment.id = :filtreProcedimentId)" +
			"  and (:esNullFiltreServeiCodi = true or lower(ps.serveiScsp.codi) = lower(:filtreServeiCodi))")
	public List<ProcedimentServei> findAllActiusAmbFiltre(
			@Param("entitatId") Long entitatId,
			@Param("esNullFiltreOrganGestorId") boolean esNullFiltreOrganGestorId,
		    @Param("filtreOrganGestorId") Long filtreOrganGestorId, 
		    @Param("esNullFiltreProcedimentId") boolean esNullFiltreProcedimentId,
		    @Param("filtreProcedimentId") Long filtreProcedimentId, 
		    @Param("esNullFiltreServeiCodi") boolean esNullFiltreServeiCodi,
		    @Param("filtreServeiCodi") String filtreServeiCodi);

    List<ProcedimentServei> findByServei(String serveiCodi);




	@Modifying
	@Query(value = "UPDATE PBL_PROCEDIMENT_SERVEI " +
			"SET CREATEDBY_CODI = CASE WHEN CREATEDBY_CODI = :codiAntic THEN :codiNou ELSE CREATEDBY_CODI END, " +
			"    LASTMODIFIEDBY_CODI = CASE WHEN LASTMODIFIEDBY_CODI = :codiAntic THEN :codiNou ELSE LASTMODIFIEDBY_CODI END " +
			"WHERE CREATEDBY_CODI = :codiAntic OR LASTMODIFIEDBY_CODI = :codiAntic",
			nativeQuery = true)
	int updateUsuariAuditoria(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);
}
