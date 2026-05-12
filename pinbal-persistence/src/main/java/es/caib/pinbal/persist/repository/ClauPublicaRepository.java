/**
 * 
 */
package es.caib.pinbal.persist.repository;

import es.caib.pinbal.persist.entity.ClauPublica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a una clau públicaque està emmagatzemada
 * a dins la base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ClauPublicaRepository extends JpaRepository<ClauPublica, Long> {

	public List<ClauPublica> findAll();

	public Optional<ClauPublica> findById(Long id);

	public ClauPublica findByNom(String nom);
	public ClauPublica findByAlies(String alies);
}
