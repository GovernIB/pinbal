/**
 * 
 */
package es.caib.pinbal.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.pinbal.core.model.Usuari;

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

}
