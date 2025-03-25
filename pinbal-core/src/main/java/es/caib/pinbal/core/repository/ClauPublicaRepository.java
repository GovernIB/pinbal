/**
 * 
 */
package es.caib.pinbal.core.repository;

import es.caib.pinbal.core.model.ClauPublica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a una clau públicaque està emmagatzemada
 * a dins la base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ClauPublicaRepository extends JpaRepository<ClauPublica, Long> {

	public List<ClauPublica> findAll();

	public ClauPublica findById(Long id);

	public ClauPublica findByNom(String nom);
	public ClauPublica findByAlies(String alies);
}
