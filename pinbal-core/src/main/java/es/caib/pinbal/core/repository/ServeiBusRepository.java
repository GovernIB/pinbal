/**
 * 
 */
package es.caib.pinbal.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.pinbal.core.model.ServeiBus;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un serveiBus que està emmagatzemat a dins la
 * base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ServeiBusRepository extends JpaRepository<ServeiBus, Long> {

	List<ServeiBus> findByServeiOrderByIdAsc(String servei);

}
