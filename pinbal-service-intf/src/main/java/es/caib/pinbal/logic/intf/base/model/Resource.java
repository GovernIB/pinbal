package es.caib.pinbal.logic.intf.base.model;

import java.io.Serializable;

/**
 * Interfície que han d'implementar tots els recursos de l'aplicació. Aquesta
 * classe només està pensada per a recursos amb clau primària simple (d'un
 * dels tipus primitius com Integer, Long, String, ...). Si la clau primària
 * del recurs és de tipus compost s'ha d'utilitzar la classe
 * CompositePkResource.
 * 
 * @param <ID> el tipus de la clau primària de l'entitat que correspon al recurs.
 *            Aquest tipus ha d'implementar Serializable.
 * 
 * @author Límit Tecnologies
 */
public interface Resource<ID extends Serializable> extends Serializable {

	/**
	 * Obté el valor del camp id.
	 * 
	 * @return el valor del camp id.
	 */
	ID getId();

	/**
	 * Estableix el valor del camp id.
	 * 
	 * @param id
	 *            el valor del camp id.
	 */
	void setId(ID id);

	/**
	 * Interfície que es pot incloure en el camp groups de des validacions
	 * de la Bean Validation API per a especificar que la validació només
	 * s'ha de tenir en compte durant <b>creació</b> d'entitats.
	 */
	interface OnCreate {}

	/**
	 * Interfície que es pot incloure en el camp groups de des validacions
	 * de la Bean Validation API per a especificar que la validació només
	 * s'ha de tenir en compte durant <b>modificació</b> d'entitats.
	 */
	interface OnUpdate {}

	/**
	 * Interfície que es pot incloure en el camp groups de des validacions
	 * de la Bean Validation API per a especificar que la validació s'ha
	 * d'ignorar.
	 */
	interface Ignore {}

}
