package es.caib.pinbal.persist.base.entity;

import es.caib.pinbal.logic.intf.helper.ToStringHelper;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;

import java.util.Collection;
import java.util.function.Function;

/**
 * Utilitats per a implementar el mètode toString de les entitats JPA sense
 * recórrer les relacions.
 *
 * Els mètodes toString de les entitats només han d'imprimir els atributs
 * bàsics: de les relacions només l'identificador, de les col·leccions només
 * la mida i dels continguts grans (byte[], XML, @Lob, ...) només la longitud.
 * Recórrer les relacions pot generar cadenes enormes (OutOfMemoryError),
 * consultes a la base de dades o LazyInitializationException.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
public final class EntityToStringHelper {

	/** Valor que s'imprimeix si una col·lecció lazy encara no s'ha carregat. */
	public static final String NO_INICIALITZADA = "<no inicialitzada>";

	private EntityToStringHelper() {}

	/**
	 * Retorna la mida de la col·lecció sense forçar-ne la càrrega.
	 *
	 * @param col la col·lecció.
	 * @return la mida, null si la col·lecció és null o {@link #NO_INICIALITZADA}
	 *         si és una col·lecció lazy que encara no s'ha carregat.
	 */
	public static Object mida(Collection<?> col) {
		if (col == null) {
			return null;
		}
		if (!Hibernate.isInitialized(col)) {
			return NO_INICIALITZADA;
		}
		return col.size();
	}

	/**
	 * Retorna l'identificador d'una entitat relacionada sense inicialitzar-la
	 * si és un proxy de Hibernate.
	 *
	 * @param entitat l'entitat relacionada.
	 * @param idGetter funció per a obtenir l'identificador si l'entitat no és un proxy.
	 * @return l'identificador o null si l'entitat és null.
	 */
	public static <T> Object identificador(T entitat, Function<T, ?> idGetter) {
		if (entitat == null) {
			return null;
		}
		if (entitat instanceof HibernateProxy) {
			return ((HibernateProxy)entitat).getHibernateLazyInitializer().getIdentifier();
		}
		return idGetter.apply(entitat);
	}

	/**
	 * Retorna la longitud d'un contingut binari.
	 *
	 * @param contingut el contingut.
	 * @return la longitud o null si el contingut és null.
	 */
	public static Integer longitud(byte[] contingut) {
		return ToStringHelper.longitud(contingut);
	}

	/**
	 * Retorna la longitud d'un contingut de text gran.
	 *
	 * @param contingut el contingut.
	 * @return la longitud o null si el contingut és null.
	 */
	public static Integer longitud(String contingut) {
		return ToStringHelper.longitud(contingut);
	}

}
