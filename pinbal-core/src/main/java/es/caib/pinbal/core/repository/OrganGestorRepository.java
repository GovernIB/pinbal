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

/**
 * Definició dels mètodes necessaris per a gestionar un organ gestor
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface OrganGestorRepository extends JpaRepository<OrganGestor, Long> {

	public Page<OrganGestor> findByEntitat(Entitat entitat, Pageable paginacio);
	public OrganGestor findByCodiAndEntitat(String codi, Entitat entitat);
	
	@Query(	"from " +
			"    OrganGestor og " +
			"where (og.entitat = :entitat)" +
      " and (:esNullFiltre = true or lower(og.codi) like lower('%'||:filtre||'%') or lower(og.nom) like lower('%'||:filtre||'%')) ")
	public Page<OrganGestor> findByEntitatAndFiltre(
			@Param("entitat") Entitat entitat,
		    @Param("esNullFiltre") boolean esNullFiltre,
		    @Param("filtre") String filtre, 
			Pageable paginacio);
	
	@Query("from " +
    		 "    OrganGestor og " +
    		 "where og.codi in (:codis)")
	public List<OrganGestor> findByCodiDir3List(@Param("codis") List<String> codis);
	

  @Query( "select og.id " + 
      "from " +
      "    OrganGestor og " +
      "where og.codi in (:codi)")
  public List<Long> findIdsByCodiDir3List(List<String> codi);
}

