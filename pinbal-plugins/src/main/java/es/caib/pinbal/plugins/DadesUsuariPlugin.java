/**
 * 
 */
package es.caib.pinbal.plugins;

import java.util.List;

/**
 * Plugin per a consultar les dades d'una font d'usuaris externa.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface DadesUsuariPlugin {

	/**
	 * Retorna la informació d'un usuari donat el codi d'usuari.
	 * 
	 * @param usuariCodi
	 *            Codi de l'usuari que es vol consultar.
	 * @return la informació de l'usuari o null si no se troba.
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar les dades de l'usuari.
	 */
	public DadesUsuari consultarAmbUsuariCodi(String usuariCodi) throws SistemaExternException;

	/**
	 * Retorna la informació d'un usuari donat el NIF de l'usuari.
	 * 
	 * @param usuariNif
	 *            NIF de l'usuari que es vol consultar.
	 * @return la informació de l'usuari o null si no se troba.
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar les dades de l'usuari.
	 */
	public DadesUsuari consultarAmbUsuariNif(String usuariNif) throws SistemaExternException;
	
	/**
	 * Retorna la llista d'usuaris d'un grup.
	 * 
	 * @param usuariNif
	 *            Codi del grup que es vol consultar.
	 * @return La llista d'usuaris del grup.
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar les dades de l'usuari.
	 */
	public List<DadesUsuari> findAmbGrup(String grupCodi) throws SistemaExternException;

}
