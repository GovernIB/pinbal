/**
 * 
 */
package es.caib.pinbal.core.repository;

import es.caib.pinbal.core.model.IntegracioAccioParamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

/**
 * Definició dels mètodes necessaris per a gestionar un parametre del monitor d'integració
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface IntegracioAccioParamRepository extends JpaRepository<IntegracioAccioParamEntity, Long> {
	
	/** Esborra els paràmetres per codi el monitor. */
	@Modifying
	@Query("delete IntegracioAccioParamEntity monParam " +
			"where monParam.integracioAccio.id in " +
			"( 	select mon.id " + 
			"	from IntegracioAccioEntity mon " +
			"	where mon.codi = :codi )")
	public void deleteByIntegracioAccioCodi(
			@Param("codi") String codi);
	
	/** Esborra tots els paràmetres del monitor. */
	@Modifying
	@Query("delete IntegracioAccioParamEntity monParam")
	public void deleteAll();
	
	/** Esborra les dades anteriors a la data passada per paràmetre. */
	@Modifying
	@Query("delete IntegracioAccioParamEntity monParam " +
			"where monParam.integracioAccio.id in " +
			"( 	select mon.id " + 
			"	from IntegracioAccioEntity mon " +
			"	where mon.data < :data )")
	public void deleteDataBefore(
			@Param("data") Date data);




	@Modifying
	@Query(value = "UPDATE PBL_MON_INT_PARAM " +
			"SET CREATEDBY_CODI = CASE WHEN CREATEDBY_CODI = :codiAntic THEN :codiNou ELSE CREATEDBY_CODI END, " +
			"    LASTMODIFIEDBY_CODI = CASE WHEN LASTMODIFIEDBY_CODI = :codiAntic THEN :codiNou ELSE LASTMODIFIEDBY_CODI END " +
			"WHERE CREATEDBY_CODI = :codiAntic OR LASTMODIFIEDBY_CODI = :codiAntic",
			nativeQuery = true)
	void updateUsuariAuditoria(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);

	@Modifying
	@Query(value = "UPDATE PBL_MON_INT_PARAM SET CREATEDBY_CODI = :codiNou WHERE CREATEDBY_CODI = :codiAntic", nativeQuery = true)
	void updateCreatedByCodi(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);

	@Modifying
	@Query(value = "UPDATE PBL_MON_INT_PARAM SET LASTMODIFIEDBY_CODI = :codiNou WHERE LASTMODIFIEDBY_CODI = :codiAntic", nativeQuery = true)
	void updateLastModifiedByCodi(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);
}

