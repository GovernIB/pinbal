/**
 * 
 */
package es.caib.pinbal.core.service;


/**
 * Declaració dels mètodes per a obtenir els properties.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface PropertyService {

	/**
	 * Retorna el valor de la propietat especificada.
	 * 
	 * @param key
	 *            Codi de la propietat.
	 * @return el valor de la propietat.
	 */
	public String get(String key);

    String get(String key, String defaultValue);
}
