/**
 * 
 */
package es.caib.pinbal.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.pinbal.core.model.Usuari;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un usuari que està emmagatzemat a dins la base de
 * dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface UsuariRepository extends JpaRepository<Usuari, String> {

	public Usuari findByCodi(String codi);

	public Usuari findByNif(String nif);

	@Query("from Usuari " +
			"where lower(codi) like lower(concat('%', :text, '%')) " +
			"   or lower(nom) like lower(concat('%', :text, '%'))")
	public List<Usuari> findByCodiOrNom(@Param("text") String text);

}
