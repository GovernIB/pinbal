/**
 * 
 */
package es.caib.pinbal.core.service;

import es.caib.pinbal.core.dto.dadesexternes.Municipi;
import es.caib.pinbal.core.dto.dadesexternes.Pais;
import es.caib.pinbal.core.dto.dadesexternes.Provincia;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

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
	public List<Provincia> findProvincies();

	/**
	 * Retorna el llistat dels municipis d'una província en
	 * format JSON.
	 * 
	 * @param provinciaCodi
	 *            El codi de la província.
	 * @return el llistat de municipis.
	 */
	@PreAuthorize("hasRole('ROLE_DELEG')")
	public List<Municipi> findMunicipisPerProvincia(String provinciaCodi);

	/**
	 * Retorna el llistat de tots els països en format
	 * JSON.
	 * 
	 * @return el llistat de països.
	 */
	public List<Pais> findPaisos();

}
