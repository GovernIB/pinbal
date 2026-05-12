package es.caib.pinbal.persist.base.entity;

import java.time.LocalDateTime;

/**
 * Interfície que han d'implementar totes les entitats amb camps d'auditoria.
 * 
 * @author Límit Tecnologies
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
	public void updateCreated(String createdBy, LocalDateTime createdDate);

	/**
	 * Actualitza la informació de la darrera modificació.
	 * 
	 * @param lastModifiedBy
	 *            el codi de l'usuari que ha fet la darrera modificació.
	 * @param lastModifiedDate
	 *            la data de la darrera modificació.
	 */
	public void updateLastModified(String lastModifiedBy, LocalDateTime lastModifiedDate);

	/**
	 * Retorna l'usuari de creació.
	 * 
	 * @return l'usuari de creació.
	 */
	public String getCreatedBy();

	/**
	 * Retorna la data de creació.
	 * 
	 * @return la data de creació.
	 */
	public LocalDateTime getCreatedDate();

	/**
	 * Retorna l'usuari de modificació.
	 * 
	 * @return l'usuari de modificació.
	 */
	public String getLastModifiedBy();

	/**
	 * Retorna la data de modificació.
	 * 
	 * @return la data de modificació.
	 */
	public LocalDateTime getLastModifiedDate();

}
