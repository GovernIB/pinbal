/**
 * 
 */
package es.caib.pinbal.core.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.pinbal.core.model.Entitat;
import es.caib.pinbal.core.model.EntitatUsuari;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a una parella entitat-usuari que està emmagatzemada a
 * dins la base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface EntitatUsuariRepository extends JpaRepository<EntitatUsuari, Long> {

	@Query("select eu from EntitatUsuari eu where eu.entitat.id = ?1 and eu.usuari.nif = ?2")
	public EntitatUsuari findByEntitatIdAndUsuariNif(Long entitatId, String usuariNif);

	@Query("select eu from EntitatUsuari eu where eu.entitat.id = ?1 and eu.usuari.codi = ?2")
	public EntitatUsuari findByEntitatIdAndUsuariCodi(Long entitatId, String usuariCodi);

	public List<EntitatUsuari> findByEntitatId(Long entitatId);

	@Query(	"select" +
			"    eu " +
			"from" +
			"    EntitatUsuari eu " +
			"order by " +
			"    eu.entitat, " +
			"    eu.departament")
	public List<EntitatUsuari> findAllOrderByEntitatAndDepartament();

	@Query(	"select" +
			"    eu " +
			"from" +
			"    EntitatUsuari eu " +
			"where " +
			"      (:esNullEntitat = true or :entitat = eu.entitat)    " + 
			"  and (:esNullCodi = true or lower(eu.usuari.codi) like concat('%', lower(:codi), '%')) " +
			"  and (:esNullNom = true or lower(eu.usuari.nom) like concat('%', lower(:nom), '%'))" +
			"  and (:esNullNif = true or lower(eu.usuari.nif) like concat('%', lower(:nif), '%'))" +
			"  and (:esNullDepartament = true or lower(eu.departament) like concat('%', lower(:departament), '%'))" +
			"")
	public  Page<EntitatUsuari> findByFiltre(
			@Param("esNullEntitat") boolean esNullEntitat,
			@Param("entitat") Entitat entitat,
			@Param("esNullCodi") boolean esNullCodi,
			@Param("codi") String codi,
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNullNif") boolean esNullNif,
			@Param("nif") String nif,
			@Param("esNullDepartament") boolean esNullDepartament,
			@Param("departament") String descripcio,
			Pageable pageable);
}
