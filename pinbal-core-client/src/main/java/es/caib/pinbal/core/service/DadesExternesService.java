/**
 * 
 */
package es.caib.pinbal.core.service;

import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Mètodes per a obtenir dades de fonts externes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DadesExternesService {

	/**
	 * Retorna el llistat de totes les províncies en format
	 * JSON.
	 * 
	 * @return el llistat de províncies.
	 */
	@PreAuthorize("hasRole('ROLE_DELEG')")
	public byte[] findProvincies();

	/**
	 * Retorna el llistat dels municipis d'una província en
	 * format JSON.
	 * 
	 * @param provinciaCodi
	 *            El codi de la província.
	 * @return el llistat de municipis.
	 */
	@PreAuthorize("hasRole('ROLE_DELEG')")
	public byte[] findMunicipisPerProvincia(String provinciaCodi);

	/**
	 * Retorna el llistat de tots els països en format
	 * JSON.
	 * 
	 * @return el llistat de països.
	 */
	public byte[] findPaisos();

}
