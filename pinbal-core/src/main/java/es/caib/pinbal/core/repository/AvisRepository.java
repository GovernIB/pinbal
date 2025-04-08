/**
 * 
 */
package es.caib.pinbal.core.repository;

import es.caib.pinbal.core.model.Avis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

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




	@Modifying
	@Query(value = "UPDATE PBL_AVIS " +
			"SET CREATEDBY_CODI = CASE WHEN CREATEDBY_CODI = :codiAntic THEN :codiNou ELSE CREATEDBY_CODI END, " +
			"    LASTMODIFIEDBY_CODI = CASE WHEN LASTMODIFIEDBY_CODI = :codiAntic THEN :codiNou ELSE LASTMODIFIEDBY_CODI END " +
			"WHERE CREATEDBY_CODI = :codiAntic OR LASTMODIFIEDBY_CODI = :codiAntic",
			nativeQuery = true)
	void updateUsuariAuditoria(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);

}
