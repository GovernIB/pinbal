/**
 * 
 */
package es.caib.pinbal.core.repository;

import es.caib.pinbal.core.model.OrganismeCessionari;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

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

	public OrganismeCessionari findById(Long id);
	
	public OrganismeCessionari findByCif(String id);

}
