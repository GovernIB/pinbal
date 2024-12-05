/**
 * 
 */
package es.caib.pinbal.core.repository;

import es.caib.pinbal.core.model.Entitat;
import es.caib.pinbal.core.model.EntitatUsuari;
import es.caib.pinbal.core.model.Usuari;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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

	@Query("select eu.usuari " +
			" from EntitatUsuari eu " +
			"where eu.entitat.id = :entitatId " +
			"  and (lower(eu.usuari.codi) like concat('%', lower(:text), '%') " +
			"  		or lower(eu.usuari.nif) like concat('%', lower(:text), '%') " +
			"  		or lower(eu.usuari.nom) like concat('%', lower(:text), '%'))")
	public List<Usuari> findByEntitatIdAndUsuariLikeText(
			@Param("entitatId") Long entitatId,
			@Param("text") String text
	);

	@Query("select eu " +
			" from EntitatUsuari eu " +
			"where eu.entitat.id = :entitatId " +
			"  and (eu.usuari.codi in (:usuariCodis0) " +
			"		or eu.usuari.codi in (:usuariCodis1) " +
			"		or eu.usuari.codi in (:usuariCodis2) " +
			"		or eu.usuari.codi in (:usuariCodis3) " +
			"		or eu.usuari.codi in (:usuariCodis4)) ")
	public List<EntitatUsuari> findByEntitatIdAndUsuariCodis(
			@Param("entitatId") Long entitatId,
			@Param("usuariCodis0") List<String> usuariCodis0,
			@Param("usuariCodis1") List<String> usuariCodis1,
			@Param("usuariCodis2") List<String> usuariCodis2,
			@Param("usuariCodis3") List<String> usuariCodis3,
			@Param("usuariCodis4") List<String> usuariCodis4);

	@Query("select eu " +
			" from EntitatUsuari eu " +
			"where eu.entitat.id = :entitatId " +
			"  and (:esNullCodi = true or lower(eu.usuari.codi) like concat('%', lower(:codi), '%')) " +
			"  and (:esNullNif = true or lower(eu.usuari.nif) like concat('%', lower(:nif), '%'))" +
			"  and (:esNullNom = true or lower(eu.usuari.nom) like concat('%', lower(:nom), '%'))" +
			"  and (eu.usuari.codi in (:usuariCodis0) " +
			"		or eu.usuari.codi in (:usuariCodis1) " +
			"		or eu.usuari.codi in (:usuariCodis2) " +
			"		or eu.usuari.codi in (:usuariCodis3) " +
			"		or eu.usuari.codi in (:usuariCodis4)) ")
	public Page<EntitatUsuari> findByEntitatIdAndUsuariCodis(
			@Param("entitatId") Long entitatId,
			@Param("usuariCodis0") List<String> usuariCodis0,
			@Param("usuariCodis1") List<String> usuariCodis1,
			@Param("usuariCodis2") List<String> usuariCodis2,
			@Param("usuariCodis3") List<String> usuariCodis3,
			@Param("usuariCodis4") List<String> usuariCodis4,
			@Param("esNullCodi") boolean esNullCodi,
			@Param("codi") String codi,
			@Param("esNullNif") boolean esNullNif,
			@Param("nif") String nif,
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			Pageable pageable);

	public List<EntitatUsuari> findByEntitatId(Long entitatId);

	public List<EntitatUsuari> findByUsuariCodi(String usuariCodi);

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
			"  and (:filtreRepresentants = false or eu.representant = true) " + 
			"  and (:filtreDelegats = false or eu.delegat = true) " +
			"  and (:filtreAuditors = false or eu.auditor = true) " +
			"  and (:filtreAplicacio = false or eu.aplicacio = true) " +
			"  and (:esNullCodi = true or lower(eu.usuari.codi) like concat('%', lower(:codi), '%')) " +
			"  and (:esNullNom = true or lower(eu.usuari.nom) like concat('%', lower(:nom), '%'))" +
			"  and (:esNullNif = true or lower(eu.usuari.nif) like concat('%', lower(:nif), '%'))" +
			"  and (:esNullDepartament = true or lower(eu.departament) like concat('%', lower(:departament), '%'))" +
			"")
	public  Page<EntitatUsuari> findByFiltre(
			@Param("esNullEntitat") boolean esNullEntitat,
			@Param("entitat") Entitat entitat,
			@Param("filtreRepresentants") boolean filtreRepresentants,
			@Param("filtreDelegats") boolean filtreDelegats,
			@Param("filtreAuditors") boolean filtreAuditors,
			@Param("filtreAplicacio") boolean filtreAplicacio,
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
