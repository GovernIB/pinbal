/**
 * 
 */
package es.caib.pinbal.core.repository;

import es.caib.pinbal.core.dto.XsdTipusEnumDto;
import es.caib.pinbal.core.model.ServeiXsd;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a un camp que està emmagatzemada a dins la base de
 * dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ServeiXsdRepository extends JpaRepository<ServeiXsd, Long> {

	List<ServeiXsd> findByServei(String servei);

	ServeiXsd findByServeiAndTipus(String servei, XsdTipusEnumDto tipus);

}
