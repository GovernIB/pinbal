/**
 * 
 */
package es.caib.pinbal.core.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.pinbal.core.model.ParamConf;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un paràmetre de configuració que està emmagatzemat
 * a dins la base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ParamConfRepository extends JpaRepository<ParamConf, String> {

	public List<ParamConf> findAll();
	
	public Page<ParamConf> findAll(Pageable pageable);

	public ParamConf findByNom(String nom);

}
