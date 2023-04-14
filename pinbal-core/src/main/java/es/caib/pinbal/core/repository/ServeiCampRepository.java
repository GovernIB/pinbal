/**
 * 
 */
package es.caib.pinbal.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import es.caib.pinbal.core.model.ServeiCamp;
import es.caib.pinbal.core.model.ServeiCampGrup;
import org.springframework.data.repository.query.Param;

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

	@Query(	"select sc.path " +
			"  from ServeiCamp sc " +
			" where sc.servei=:servei " +
			"   and sc.inicialitzar is true")
	List<String> findPathInicialitzablesByServei(@Param("servei") String servei);

}
