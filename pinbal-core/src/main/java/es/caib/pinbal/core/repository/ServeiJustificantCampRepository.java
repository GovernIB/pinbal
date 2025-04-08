/**
 * 
 */
package es.caib.pinbal.core.repository;

import es.caib.pinbal.core.model.ServeiJustificantCamp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa la traducció d'un camp que està emmagatzemada a 
 * dins la base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ServeiJustificantCampRepository extends JpaRepository<ServeiJustificantCamp, Long> {

	ServeiJustificantCamp findByServeiAndXpathAndLocaleIdiomaAndLocaleRegio(
			String servei,
			String xpath,
			String localeIdioma,
			String localeRegio);
			
	List<ServeiJustificantCamp> findByServeiAndLocaleIdiomaAndLocaleRegio(
			String servei,
			String localeIdioma,
			String localeRegio);




	@Modifying
	@Query(value = "UPDATE PBL_SERVEI_JUSTIF_CAMP " +
			"SET CREATEDBY_CODI = CASE WHEN CREATEDBY_CODI = :codiAntic THEN :codiNou ELSE CREATEDBY_CODI END, " +
			"    LASTMODIFIEDBY_CODI = CASE WHEN LASTMODIFIEDBY_CODI = :codiAntic THEN :codiNou ELSE LASTMODIFIEDBY_CODI END " +
			"WHERE CREATEDBY_CODI = :codiAntic OR LASTMODIFIEDBY_CODI = :codiAntic",
			nativeQuery = true)
	void updateUsuariAuditoria(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);
}
