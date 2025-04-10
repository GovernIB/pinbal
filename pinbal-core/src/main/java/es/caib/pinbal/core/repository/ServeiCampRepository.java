/**
 * 
 */
package es.caib.pinbal.core.repository;

import es.caib.pinbal.core.model.ServeiCamp;
import es.caib.pinbal.core.model.ServeiCampGrup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un camp que està emmagatzemada a dins la base de
 * dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ServeiCampRepository extends JpaRepository<ServeiCamp, Long> {

	@Query(	"from " +
			"    ServeiCamp sc " +
			"where " +
			"    sc.servei=?1 " +
			"order by " +
			"    sc.grup asc, " +
			"    sc.ordre asc")
	List<ServeiCamp> findByServeiOrderByGrupOrdreAsc(String servei);

	@Query(	"from " +
			"    ServeiCamp sc " +
			"where " +
			"    sc.servei=?1 " +
			"and ((?2 = true and sc.grup is null) " +
			" or (?2 = false and sc.grup = ?3)) " +
			"order by " +
			"    sc.ordre asc")
	List<ServeiCamp> findByServeiAndGrupOrderByOrdreAsc(
			String servei,
			boolean esNullGrup,
			ServeiCampGrup grup);

	@Query(	"select count(sc) " +
			"from " +
			"    ServeiCamp sc " +
			"where " +
			"    sc.servei=?1 " +
			"and ((?2 = true and sc.grup is null) " +
			" or (?2 = false and sc.grup = ?3)) " +
			"order by " +
			"    sc.ordre asc")
	int countByServeiAndGrupOrderByOrdreAsc(
			String servei,
			boolean esNullGrup,
			ServeiCampGrup grup);

	@Query(	"select sc.path " +
			"  from ServeiCamp sc " +
			" where sc.servei=:servei " +
			"   and sc.inicialitzar is true")
	List<String> findPathInicialitzablesByServei(@Param("servei") String servei);

	ServeiCamp findByServeiAndPath(String servei, String path);




	@Modifying
	@Query(value = "UPDATE PBL_SERVEI_CAMP " +
			"SET CREATEDBY_CODI = CASE WHEN CREATEDBY_CODI = :codiAntic THEN :codiNou ELSE CREATEDBY_CODI END, " +
			"    LASTMODIFIEDBY_CODI = CASE WHEN LASTMODIFIEDBY_CODI = :codiAntic THEN :codiNou ELSE LASTMODIFIEDBY_CODI END " +
			"WHERE CREATEDBY_CODI = :codiAntic OR LASTMODIFIEDBY_CODI = :codiAntic",
			nativeQuery = true)
	int updateUsuariAuditoria(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);
}
