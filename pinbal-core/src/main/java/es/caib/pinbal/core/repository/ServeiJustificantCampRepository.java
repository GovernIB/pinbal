/**
 * 
 */
package es.caib.pinbal.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.pinbal.core.model.ServeiJustificantCamp;

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

}
