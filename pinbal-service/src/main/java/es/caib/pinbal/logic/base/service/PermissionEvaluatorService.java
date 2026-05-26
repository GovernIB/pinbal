package es.caib.pinbal.logic.base.service;

import es.caib.pinbal.logic.base.helper.BasePermissionHelper;
import es.caib.pinbal.logic.intf.base.exception.UnknownPermissionException;
import es.caib.pinbal.logic.intf.base.model.ResourceArtifactType;
import es.caib.pinbal.logic.intf.base.util.HttpRequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementació del servei d'avaluació de permisos.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
public class PermissionEvaluatorService implements es.caib.pinbal.logic.intf.base.service.PermissionEvaluatorService {

	@Autowired
	private BasePermissionHelper permissionHelper;

	@Override
	public boolean hasPermission(
			Authentication authentication,
			Object domainObject,
			Object permission) {
		log.debug("Comprovant permisos per a accedir a l'entitat (authentication={}, domainObject={}, permission={})",
				authentication,
				domainObject,
				permission);
		return checkResourcePermission(
				authentication,
				null,
				domainObject.getClass().getName(),
				permission);
	}

	@Override
	public boolean hasPermission(
			Authentication authentication,
			Serializable targetId,
			String targetType,
			Object permission) {
		log.debug("Comprovant permisos per a accedir al recurs (authentication={}, targetId={}, targetType={}, permission={})",
				authentication,
				targetId,
				targetType,
				permission);
		return checkResourcePermission(
				authentication,
				targetId,
				targetType,
				permission);
	}

	private boolean checkResourcePermission(
			Authentication authentication,
			@Nullable Serializable targetId,
			String targetType,
			@Nullable Object permission) {
		boolean isActionPermission = isArtifactActionPermission(permission);
		boolean isReportPermission = isArtifactReportPermission(permission);
		if (isActionPermission || isReportPermission) {
			// Si s'està verificant el permís d'un artefacte crida el mètode a posta pels artefactes
			String code = getArtifactCodeFromHttpRequest(isActionPermission ? "action" : "report");
			if (code != null) {
				try {
					return permissionHelper.checkResourceArtifactPermission(
							Class.forName(targetType),
							isActionPermission ? ResourceArtifactType.ACTION : ResourceArtifactType.REPORT,
							code);
				} catch (ClassNotFoundException ex) {
					log.warn("Permission denied for resource {}: class not found", targetType, ex);
				}
			}
			return false;
		} else {
			// Si no s'està verificant el permís d'un artefacte crida el mètode per defecte
			return permissionHelper.checkResourcePermission(
					authentication,
					targetId,
					targetType,
					toBasePermissions(permission));
		}
	}

	private boolean isArtifactActionPermission(Object objectPermission) {
		if (objectPermission instanceof RestApiOperation) {
			RestApiOperation restapiOperation = (RestApiOperation)objectPermission;
			return RestApiOperation.ACTION == restapiOperation;
		}
		return false;
	}
	private boolean isArtifactReportPermission(Object objectPermission) {
		if (objectPermission instanceof RestApiOperation) {
			RestApiOperation restapiOperation = (RestApiOperation)objectPermission;
			return RestApiOperation.REPORT == restapiOperation;
		}
		return false;
	}

	private BasePermission[] toBasePermissions(Object objectPermission) {
		if (objectPermission instanceof RestApiOperation) {
			RestApiOperation restapiOperation = (RestApiOperation)objectPermission;
			switch (restapiOperation) {
				case GET_ONE:
				case FIND:
				case EXPORT:
				case ARTIFACT:
				case REPORT:
				case FIELDDOWNLOAD:
					return new BasePermission[] { (BasePermission)BasePermission.READ };
				case UPDATE:
				case PATCH:
				case ACTION:
					return new BasePermission[] { (BasePermission)BasePermission.WRITE };
				case ONCHANGE:
				case OPTIONS:
					return new BasePermission[] { (BasePermission)BasePermission.CREATE, (BasePermission)BasePermission.WRITE };
				case CREATE:
					return new BasePermission[] { (BasePermission)BasePermission.CREATE };
				case DELETE:
					return new BasePermission[] { (BasePermission)BasePermission.DELETE };
			}
		}
		throw new UnknownPermissionException(objectPermission);
	}

	private String getArtifactCodeFromHttpRequest(String urlPart) {
		String code = null;
		Optional<HttpServletRequest> request = HttpRequestUtil.getCurrentHttpRequest();
		if (request.isPresent()) {
			String requestUri = request.get().getRequestURI();
			// Primer mira si el codi de l'informe és a "action/CODE" o "report/CODE"
			Pattern pattern = Pattern.compile("/" + urlPart + "/([^/]+)/");
			Matcher matcher = pattern.matcher(requestUri);
			if (matcher.find()) {
				code = matcher.group(1);
			} else {
				// Si no hi és mira si és al final de tot
				if (requestUri.endsWith("/")) {
					requestUri = requestUri.substring(0, requestUri.length() - 1);
				}
				int lastSlashIndex = requestUri.lastIndexOf('/');
				if (lastSlashIndex >= 0) {
					return requestUri.substring(lastSlashIndex + 1);
				}
			}
		}
		return code;
	}

}
