/**
 * 
 */
package es.caib.pinbal.core.repository;

import es.caib.pinbal.core.model.Servei;
import es.caib.pinbal.core.model.ServeiRegla;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un Repro que està emmagatzemat a dins la base
 * de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ServeiReglaRepository extends JpaRepository<ServeiRegla, Long> {

//	public List<ServeiRegla> findByServei(Servei servei);

	public List<ServeiRegla> findByServeiOrderByOrdreAsc(Servei servei);

	@Query("from ServeiRegla r " +
			"where r.servei=:servei " +
			"  and r.modificat in (es.caib.pinbal.core.dto.regles.ModificatEnum.ALGUN_CAMP, es.caib.pinbal.core.dto.regles.ModificatEnum.CAMPS) " +
			"order by r.ordre asc")
	public List<ServeiRegla> findReglesCamps(@Param("servei") Servei servei);

	@Query("from ServeiRegla r " +
			"where r.servei=:servei " +
			"  and r.modificat in (es.caib.pinbal.core.dto.regles.ModificatEnum.ALGUN_GRUP, es.caib.pinbal.core.dto.regles.ModificatEnum.GRUPS) " +
			"order by r.ordre asc")
	public List<ServeiRegla> findReglesGrups(@Param("servei") Servei servei);

//	@Query("select count(sr) from ServeiRegla sr where sr.servei.id = :serveiId ")
//	public Long countByServeiId(@Param("serveiId") Long serveiId);


	@Query("select max(r.ordre) "
			+ "from ServeiRegla r "
			+ "where r.servei.id=:serveiId")
	public Integer getSeguentOrdre(@Param("serveiId") Long serveiId);

	@Query("from ServeiRegla r where r.servei.id=:serveiId and r.nom = :nom")
	ServeiRegla findByNom(@Param("serveiId") Long serveiId, @Param("nom") String nom);

	@Query("select c.id " +
			" from ServeiCamp c " +
			"where c.path in (" +
			"		select substring(rm.valor, locate('|', rm.valor) + 2) " +
			"		  from ServeiRegla r, " +
			"			   ServeiReglaCampModificat rm " +
			"		 where r.servei.codi = c.servei " +
			"		   and rm.reglaId = r.id " +
			"		   and (r.modificat = es.caib.pinbal.core.dto.regles.ModificatEnum.CAMPS or r.modificat = es.caib.pinbal.core.dto.regles.ModificatEnum.ALGUN_CAMP)) " +
			"  and c.servei = :serveiCodi")
	List<Long> findCampsRegles(@Param("serveiCodi") String serveiCodi);

	@Query("select g.id " +
			" from ServeiCampGrup g " +
			"where g.nom in (" +
			"		select rm.valor " +
			"		  from ServeiRegla r, " +
			"			   ServeiReglaCampModificat rm " +
			"		 where r.servei.codi = g.servei " +
			"		   and rm.reglaId = r.id " +
			"		   and (r.modificat = es.caib.pinbal.core.dto.regles.ModificatEnum.GRUPS or r.modificat = es.caib.pinbal.core.dto.regles.ModificatEnum.ALGUN_GRUP)) " +
			"  and g.servei = :serveiCodi")
	List<Long> findGrupsRegles(@Param("serveiCodi") String serveiCodi);




	@Modifying
	@Query(value = "UPDATE PBL_SERVEI_REGLA " +
			"SET CREATEDBY_CODI = CASE WHEN CREATEDBY_CODI = :codiAntic THEN :codiNou ELSE CREATEDBY_CODI END, " +
			"    LASTMODIFIEDBY_CODI = CASE WHEN LASTMODIFIEDBY_CODI = :codiAntic THEN :codiNou ELSE LASTMODIFIEDBY_CODI END " +
			"WHERE CREATEDBY_CODI = :codiAntic OR LASTMODIFIEDBY_CODI = :codiAntic",
			nativeQuery = true)
	int updateUsuariAuditoria(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);
}
