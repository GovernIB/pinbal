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
import es.caib.pinbal.core.model.OrganGestor;
import es.caib.pinbal.core.model.Procediment;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un procediment que està emmagatzemat a dins la base de
 * dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ProcedimentRepository extends JpaRepository<Procediment, Long> {

	Procediment findByCodi(String codi);

	List<Procediment> findByEntitatOrderByNomAsc(Entitat entitat);
	
	@Query(	"from " +
			"    Procediment p " +
			"where (p.entitat = :entitat)" +
			" and (:esNullFiltre = true or lower(p.codi) like lower('%'||:filtre||'%') or lower(p.nom) like lower('%'||:filtre||'%')) " +
			"order by p.nom asc")
	public List<Procediment> findByEntitatAndFiltreOrderByNomAsc(
			@Param("entitat") Entitat entitat,
			@Param("esNullFiltre") boolean esNullFiltre,
			@Param("filtre") String filtre);

	Procediment findByEntitatAndCodi(Entitat entitat, String codi);

	Procediment findByEntitatAndCodiSia(Entitat entitat, String codiSia);

	@Query("from Procediment p where p.entitat.id = ?1 and p.actiu = true order by p.nom asc")
	List<Procediment> findActiusByEntitat(Long entitatId);

	@Query("select ps.procediment from ProcedimentServei ps where ps.procediment.entitat.id = ?1 and ps.servei = ?2 and ps.procediment.actiu = true")
	List<Procediment> findActiusByEntitatAndServeid(Long entitatId, String servei);


	@Query(	"select" +
			"    p " +
			"from" +
			"    Procediment p " +
			"where " +
			"    p.entitat = :entitat " +
			"and (:esNullCodi = true or lower(p.codi) like concat('%', lower(:codi), '%')) " +
			"and (:esNullNom = true or lower(p.nom) like concat('%', lower(:nom), '%')) " +
			"and (:esNullDepartament = true or lower(p.departament) like concat('%', lower(:departament), '%')) " +
			"and (:esNullOrganGestor = true or p.organGestor = :organGestor) " +
			"and (:esNullCodiSia = true or lower(p.codiSia) like concat('%', lower(:codiSia), '%')) " +
			"and (:esNullActiu = true or p.actiu = :actiu) "
			)
	public Page<Procediment> findByFiltre(
			@Param("entitat") Entitat entitat,
			@Param("esNullCodi") boolean esNullCodi,
			@Param("codi") String codi,
			@Param("esNullNom") boolean esNullNom,
			@Param("nom") String nom,
			@Param("esNullDepartament") boolean esNullDepartament,
			@Param("departament") String departament,
			@Param("esNullOrganGestor") boolean esNullOrganGestor,
			@Param("organGestor") OrganGestor organGestor,
			@Param("esNullCodiSia") boolean esNullCodiSia,
			@Param("codiSia") String codiSia,
			@Param("esNullActiu") boolean esNullActiu,
			@Param("actiu") boolean actiu,
			Pageable pageable);
	
	@Query(	"select" +
			"    p " +
			"from" +
			"    Procediment p " +
			"order by " +
			"    p.entitat, " +
			"    p.departament")
	public List<Procediment> findAllOrderByEntitatAndDepartament();
	
	@Query("select ps.procediment from ProcedimentServei ps where ps.servei = ?1 and ps.actiu = true order by ps.procediment.entitat.nom, ps.procediment.codi asc")
	public List<Procediment> findAllByServei(String servei);
	
	@Query("select count(*) from ProcedimentServei ps where ps.servei = ?1 and ps.actiu = true")
	public long countByServei(String servei);

}
