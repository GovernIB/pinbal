package es.caib.pinbal.logic.intf.base.service;

import org.springframework.security.access.PermissionEvaluator;

/**
 * Definició del servei d'avaluació de permisos.
 * 
 * @author Límit Tecnologies
 */
public interface PermissionEvaluatorService extends PermissionEvaluator {

	enum RestApiOperation {
		CREATE,
		UPDATE,
		PATCH,
		DELETE,
		GET_ONE,
		FIND,
		EXPORT,
		ONCHANGE,
		ARTIFACT,
		ACTION,
		REPORT,
		OPTIONS,
		FIELDDOWNLOAD
	}

}
