package es.caib.pinbal.logic.intf.helper;

import java.util.Collection;
import java.util.Map;

/**
 * Utilitats per a implementar el mètode toString dels DTOs i de les entitats.
 *
 * Els mètodes toString només han d'imprimir els atributs bàsics: dels objectes
 * relacionats només l'identificador, de les col·leccions només la mida i dels
 * continguts grans (byte[], XML, base64, ...) només la longitud. Imprimir-los
 * sencers pot generar cadenes enormes (OutOfMemoryError).
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public final class ToStringHelper {

	private ToStringHelper() {}

	/**
	 * Retorna la mida d'una col·lecció.
	 *
	 * @param col la col·lecció.
	 * @return la mida o null si la col·lecció és null.
	 */
	public static Integer mida(Collection<?> col) {
		return col != null ? col.size() : null;
	}

	/**
	 * Retorna la mida d'un mapa.
	 *
	 * @param map el mapa.
	 * @return la mida o null si el mapa és null.
	 */
	public static Integer mida(Map<?, ?> map) {
		return map != null ? map.size() : null;
	}

	/**
	 * Retorna la longitud d'un array.
	 *
	 * @param array l'array.
	 * @return la longitud o null si l'array és null.
	 */
	public static Integer longitud(Object[] array) {
		return array != null ? array.length : null;
	}

	/**
	 * Retorna la longitud d'un contingut binari.
	 *
	 * @param contingut el contingut.
	 * @return la longitud o null si el contingut és null.
	 */
	public static Integer longitud(byte[] contingut) {
		return contingut != null ? contingut.length : null;
	}

	/**
	 * Retorna la longitud d'un contingut de text gran.
	 *
	 * @param contingut el contingut.
	 * @return la longitud o null si el contingut és null.
	 */
	public static Integer longitud(String contingut) {
		return contingut != null ? contingut.length() : null;
	}

}
