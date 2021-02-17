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
import es.caib.pinbal.core.model.Entitat.EntitatTipus;
import es.caib.pinbal.core.model.EntitatUsuari;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a una entitat que està emmagatzemada a dins la base de
 * dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface EntitatRepository extends JpaRepository<Entitat, Long> {

	Entitat findByCodi(String codi);

	Entitat findByCif(String cif);

	@Query(	"select " +
			"    eu " +
			"from " +
			"    EntitatUsuari eu " +
			"where " +
			"    eu.entitat.id = ?1 " +
			"and eu.usuari.nif = ?2")
	EntitatUsuari findUsuariAmbNif(Long id, String nif);

	@Query(	"select " +
			"    eu.entitat " +
			"from " +
			"    EntitatUsuari eu " +
			"where " +
			"    eu.usuari.codi = ?1 " +
			"and eu.entitat.activa = true " +
			"and (eu.representant = true or eu.delegat=true or eu.auditor=true)")
	List<Entitat> findActivesAmbUsuariCodi(String usuariCodi);

	List<Entitat> findByTipusOrderByNomAsc(EntitatTipus tipus);

	@Query(	"select" +
			"    e " +
			"from" +
			"    Entitat e " +
			"where " +
			"      (:esNullCodi = true or lower(e.codi) like concat('%', lower(:codi), '%')) " +
			"  and (:esNullNom = true or lower(e.nom) like concat('%', lower(:nom), '%'))" +
			"  and (:esNullCif = true or lower(e.cif) like concat('%', lower(:cif), '%')) " +
			"  and (:esNullActiva = true or e.activa = :activa) " +
			"  and (:esNullTipus = true or e.tipus = :tipus) " +
			"  and (:esNullUnitatArrel = true or lower(e.unitatArrel) like concat('%', lower(:unitatArrel), '%')) " +
			"")
	public Page<Entitat> findByFiltre(
			@Param("esNullCodi") boolean esNullCodi,
			@Param("codi") String codi,
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNullCif") boolean esNullCif,
			@Param("cif") String cif,
			@Param("esNullActiva") boolean esNullActiva,
			@Param("activa") Boolean activa,
			@Param("esNullTipus") boolean esNullTipus,
			@Param("tipus") EntitatTipus tipus,	
			@Param("esNullUnitatArrel") boolean esNullUnitatArrel,
			@Param("unitatArrel") String unitatArrel,
			Pageable pageable);
}
