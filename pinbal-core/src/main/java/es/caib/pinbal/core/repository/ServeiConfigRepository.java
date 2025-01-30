/**
 * 
 */
package es.caib.pinbal.core.repository;

import es.caib.pinbal.core.model.ServeiConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un serveiConfig que està emmagatzemat a dins la
 * base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ServeiConfigRepository extends JpaRepository<ServeiConfig, Long> {

	ServeiConfig findByServei(String servei);

    @Query("select sc.servei from ServeiConfig sc where sc.actiu = false")
    List<String> findByActiuFalse();

    @Query("from ServeiConfig sc where sc.servei in (:serveisEntitat)")
    List<ServeiConfig> findByServeiIn(@Param("serveisEntitat") List<String> serveisEntitat);
}
