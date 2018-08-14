package es.caib.pinbal.core.service.exception;
/**
 * Excepció que es llança quan l'objecte especificat no existeix.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class NotFoundException extends RuntimeException {

	private Object objectId;
	private Class<?> objectClass;
	
	public NotFoundException(
			Object objectId,
			Class<?> objectClass) {
		super(getObjectInfo(objectId, objectClass));
		this.objectId = objectId;
		this.objectClass = objectClass;
	}

	public Object getObjectId() {
		return objectId;
	}

	public Class<?> getObjectClass() {
		return objectClass;
	}

	private static String getObjectInfo(
			Object objectId,
			Class<?> objectClass) {
		StringBuilder sb = new StringBuilder();
		if (objectClass != null)
			sb.append(objectClass.getName());
		else
			sb.append("null");
		sb.append("#");
		if (objectId != null)
			sb.append(objectId.toString());
		else
			sb.append("null");
		return sb.toString();
	}

}
