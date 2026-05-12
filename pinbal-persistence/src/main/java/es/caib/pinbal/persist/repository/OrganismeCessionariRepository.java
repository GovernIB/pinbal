/**
 * 
 */
package es.caib.pinbal.persist.repository;

import es.caib.pinbal.persist.entity.OrganismeCessionari;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar
 * la informació relativa a l'organisme cessionari que està emmagatzemat
 * a dins la base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface OrganismeCessionariRepository extends JpaRepository<OrganismeCessionari, Long> {

	public List<OrganismeCessionari> findAll();

	public List<OrganismeCessionari> findByBloquejatFalseOrderByNomAsc();

	public Optional<OrganismeCessionari> findById(Long id);
	
	public OrganismeCessionari findByCif(String id);

}
