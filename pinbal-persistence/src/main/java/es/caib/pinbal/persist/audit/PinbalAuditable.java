/**
 * 
 */
package es.caib.pinbal.persist.audit;

import es.caib.pinbal.persist.entity.Usuari;
import org.springframework.data.jpa.domain.AbstractAuditable;

import java.io.Serializable;

/**
 * Classe basse de on extendre per a activar les auditories.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
//@EntityListeners(PinbalAuditingEntityListener.class)
public class PinbalAuditable<PK extends Serializable> extends AbstractAuditable<Usuari, PK> implements Serializable {

	private static final long serialVersionUID = 5373083799666869320L;

}
