package es.caib.pinbal.persist.base.entity;

import java.io.Serializable;

/**
 * Interfície que han d'implementar les entitats reordenables.
 * 
 * @author Límit Tecnologies
 */
public interface ReorderableEntity<PID extends Serializable> {

	Long getOrder();
	void setOrder(Long order);

	default PID getOrderParentId() {
		return null;
	}

}
