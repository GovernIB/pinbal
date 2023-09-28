/**
 * 
 */
package es.caib.pinbal.core.repository;

import es.caib.pinbal.core.model.ServeiCampGrup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un grup de camps que està emmagatzemada a dins la base de
 * dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ServeiCampGrupRepository extends JpaRepository<ServeiCampGrup, Long> {

	List<ServeiCampGrup> findByServei(String servei);
	List<ServeiCampGrup> findByServeiAndPareIsNullOrderByOrdreAsc(String servei);

	@Query(	" from ServeiCampGrup scg " +
			"where scg.servei=?1 " +
			"  and ((?2 = true and scg.pare is null) " +
			"   or (?2 = false and scg.pare = ?3)) " +
			"order by scg.ordre asc")
	List<ServeiCampGrup> findByServeiAndPareOrderByOrdreAsc(String servei, boolean esNullPare, ServeiCampGrup pare);

	@Query(	"select count(scg) " +
			"  from ServeiCampGrup scg " +
			" where scg.servei=?1 " +
			"   and ((?2 = true and scg.pare is null) " +
			"    or (?2 = false and scg.pare = ?3)) " +
			" order by scg.ordre asc")
	int countByServeiAndPareOrderByOrdreAsc(String servei, boolean esNullPare, ServeiCampGrup pare);

	@Query("from ServeiCampGrup scg where scg.servei=:serveiCodi and scg.nom = :nom")
	ServeiCampGrup findByNom(@Param("serveiCodi") String serveiCodi, @Param("nom") String nom);

	@Modifying
	void delete(ServeiCampGrup serveiCampGrup);

	@Modifying
	@Query("delete ServeiCampGrup where id=?1")
	void deleteById(Long id);

}
