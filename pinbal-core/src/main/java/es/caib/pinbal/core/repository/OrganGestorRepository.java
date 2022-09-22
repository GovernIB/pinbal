/**
 * 
 */
package es.caib.pinbal.core.repository;

import java.util.List;

import es.caib.pinbal.core.dto.OrganGestorEstatEnum;
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
	
	
	
	
	@Query(	"select og " +
			"from " +
			"    OrganGestor og " +
			"left outer join og.pare ogp " +
			"where (og.entitat = :entitat)" +
			"  and (:esNullFiltreCodi = true or lower(og.codi) like concat('%', lower(:filtreCodi), '%'))" +
			"  and (:esNullFiltreNom = true or lower(og.nom) like concat('%', lower(:filtreNom), '%')) " + 
			"  and (:esNullFiltrePareCodi = true or ogp.codi = :filtrePareCodi) " +
			"  and (:esNullFiltreEstat = true or og.estat = :estat )")
	public Page<OrganGestor> findByEntitatAndFiltre(
			@Param("entitat") Entitat entitat,
		    @Param("esNullFiltreCodi") boolean esNullFiltreCodi,
		    @Param("filtreCodi") String filtreCodi, 
		    @Param("esNullFiltreNom") boolean esNullFiltreNom,
		    @Param("filtreNom") String filtreNom,
			@Param("esNullFiltrePareCodi") boolean esNullFiltrePareCodi,
			@Param("filtrePareCodi") String filtrePareCodi,
		    @Param("esNullFiltreEstat") boolean esNullFiltreEstat,
		    @Param("estat") OrganGestorEstatEnum estat,
			Pageable paginacio);
	
	@Query(	"from " +
			"    OrganGestor og " +
			"where (og.entitat.id = :entitatId)" +
			"  and ((lower(og.codi) like concat('%', lower(:filtre), '%'))" +
			"  		or (lower(og.nom) like concat('%', lower(:filtre), '%')))")
	public List<OrganGestor> findByEntitatAndCodiNom(
			@Param("entitatId") Long entitatId,
			@Param("filtre") String filtre);
	
	@Query("from " +
    		 "    OrganGestor og " +
    		 "where og.codi in (:codis)")
	public List<OrganGestor> findByCodiDir3List(@Param("codis") List<String> codis);
	

  @Query( "select og.id " + 
      "from " +
      "    OrganGestor og " +
      "where og.codi in (:codi)")
  public List<Long> findIdsByCodiDir3List(List<String> codi);

	List<OrganGestor> findByEntitatIdAndActiuIsTrue(Long entitatId);
}

