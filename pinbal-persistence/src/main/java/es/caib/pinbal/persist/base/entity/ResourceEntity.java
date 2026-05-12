package es.caib.pinbal.persist.base.entity;

import org.springframework.data.domain.Persistable;

/**
 * Interfície que han d'implementar totes les entitats per a poder identificar
 * el recurs al qual fan referència.
 * 
 * @author Límit Tecnologies
 */
public interface ResourceEntity<R, PK> extends Persistable<PK> {

	/**
	 * Modifica la clau primària de l'entitat.
	 *
	 * @param id
	 *            la nova clau primària.
	 */
	void setId(PK id);

}