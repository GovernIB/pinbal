/**
 * 
 */
package es.caib.pinbal.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.pinbal.core.model.ServeiCampGrup;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un grup de camps que està emmagatzemada a dins la base de
 * dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ServeiCampGrupRepository extends JpaRepository<ServeiCampGrup, Long> {

	List<ServeiCampGrup> findByServeiOrderByOrdreAsc(String servei);

}
