/**
 * 
 */
package es.caib.pinbal.plugins;


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

}
