/**
 * 
 */
package es.caib.pinbal.persist.audit;

import java.util.Date;

/**
 * Interfície que han d'implementar totes les entitats amb camps d'auditoria.
 * 
 * @author Limit Tecnologies
 */
public interface AuditableEntity {

	/**
	 * Actualitza la informació de creació.
	 * 
	 * @param createdBy
	 *            el codi de l'usuari que ha creat l'entitat.
	 * @param createdDate
	 *            la data de creació de l'entitat.
	 */
	public void updateCreated(String createdBy, Date createdDate);

	/**
	 * Actualitza la informació de la darrera modificació.
	 * 
	 * @param lastModifiedBy
	 *            el codi de l'usuari que ha fet la darrera modificació.
	 * @param lastModifiedDate
	 *            la data de la darrera modificació.
	 */
	public void updateLastModified(String lastModifiedBy, Date lastModifiedDate);

}
