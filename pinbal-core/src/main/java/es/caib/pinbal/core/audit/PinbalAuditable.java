/**
 * 
 */
package es.caib.pinbal.core.audit;

import java.io.Serializable;

import javax.persistence.EntityListeners;

import org.springframework.data.jpa.domain.AbstractAuditable;

import es.caib.pinbal.core.model.Usuari;

/**
 * Classe basse de on extendre per a activar les auditories.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@EntityListeners(PinbalAuditingEntityListener.class)
public class PinbalAuditable<PK extends Serializable> extends AbstractAuditable<Usuari, PK> {

	private static final long serialVersionUID = 5373083799666869320L;

}
