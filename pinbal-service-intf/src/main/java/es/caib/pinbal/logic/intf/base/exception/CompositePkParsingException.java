package es.caib.pinbal.logic.intf.base.exception;

import lombok.Getter;

import java.io.Serializable;

/**
 * Excepció que es llança quan es produeix un error al deserialitzar una clau
 * primària composta.
 * 
 * @author Límit Tecnologies
 */
@Getter
public class CompositePkParsingException extends RuntimeException {

	private final String id;
	private final Serializable pk;
	private final Class<?> compositePkClass;

	public CompositePkParsingException(
			String id,
			Serializable pk,
			Class<?> compositePkClass,
			String message) {
		this(id, pk, compositePkClass, message, null);
	}

	public CompositePkParsingException(
			String id,
			Serializable pk,
			Class<?> compositePkClass,
			Throwable cause) {
		this(id, pk, compositePkClass, null, cause);
	}

	public CompositePkParsingException(
			String id,
			Serializable pk,
			Class<?> compositePkClass,
			String message,
			Throwable cause) {
		super(getExceptionMessage(id, pk, compositePkClass) + ": " + message, cause);
		this.id = id;
		this.pk = pk;
		this.compositePkClass = compositePkClass;
	}

	private static String getExceptionMessage(
			String id,
			Serializable pk,
			Class<?> compositePkClass) {
		if (id != null) {
			return "Composite pk deserialization failed (id=" + id + ", compositePkClass=" + compositePkClass + ")";
		} else {
			return "Composite pk serialization failed (pk=" + pk + ", compositePkClass=" + compositePkClass + ")";
		}
	}

}
