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
	 * Retorna la informació d'un usuari donat el NIF de l'usuari.
	 *
	 * @param usuariNom
	 *            NIF de l'usuari que es vol consultar.
	 * @return la informació de l'usuari o null si no se troba.
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar les dades de l'usuari.
	 */
	public DadesUsuari consultarAmbUsuariNom(String usuariNom) throws SistemaExternException;

	/**
	 * Retorna la llista d'usuaris que el seu nom, codi o nif conté un TEXT.
	 *
	 * @param text TEXT de l'usuari que es vol consultar.
	 * @return la informació dels usuaris.
	 * @throws SistemaExternException Si es produeix un error al consultar les dades dels usuaris.
	 */
	public List<DadesUsuari> consultarAmbUsuariAny(String text) throws SistemaExternException;
	
	/**
	 * Retorna la llista d'usuaris d'un grup.
	 * 
	 * @param grupCodi
	 *            Codi del grup que es vol consultar.
	 * @return La llista d'usuaris del grup.
	 * @throws SistemaExternException
	 *            Si es produeix un error al consultar les dades de l'usuari.
	 */
	public List<DadesUsuari> findAmbGrup(String grupCodi) throws SistemaExternException;

}
