/**
 * 
 */
package es.caib.pinbal.core.audit;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.pinbal.core.model.Usuari;

/**
 * EntityListener per a les auditories per a evitar la configuració
 * via Spring.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PinbalAuditingEntityListener extends AuditingEntityListener<Usuari> {
	
	boolean auditorAwareConfigurat = false;

	@PrePersist
	public void touchForCreate(Object target) {
		configAuditorAware();
		super.touchForCreate(target);
	}

	@PreUpdate
	public void touchForUpdate(Object target) {
		configAuditorAware();
		super.touchForUpdate(target);
	}

	private void configAuditorAware() {
		if (!auditorAwareConfigurat) {
			setAuditorAware(BasicAuditorAware.getInstance());
			auditorAwareConfigurat = true;
		}
	}

}
