/**
 * 
 */
package es.caib.pinbal.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.pinbal.core.model.OrganismeCessionari;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar
 * la informació relativa a l'organisme cessionari que està emmagatzemat
 * a dins la base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface OrganismeCessionariRepository extends JpaRepository<OrganismeCessionari, Long> {

	public List<OrganismeCessionari> findAll();
	
	public OrganismeCessionari findById(Long id);
	
	public OrganismeCessionari findByCif(String id);

}
