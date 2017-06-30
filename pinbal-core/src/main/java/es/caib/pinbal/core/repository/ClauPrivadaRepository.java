/**
 * 
 */
package es.caib.pinbal.core.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.pinbal.core.model.ClauPrivada;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar
 * la informació relativa a una clau privada que està emmagatzemat
 * a dins la base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ClauPrivadaRepository extends JpaRepository<ClauPrivada, Long> {

	public List<ClauPrivada> findAll();
	
	public Page<ClauPrivada> findAll(Pageable pageable);

	public ClauPrivada findById(Long id);

}
