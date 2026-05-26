package es.caib.pinbal.logic.base.helper;

import es.caib.pinbal.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.pinbal.logic.intf.base.annotation.ResourceArtifact;
import es.caib.pinbal.logic.intf.base.annotation.ResourceConfig;
import es.caib.pinbal.logic.intf.base.model.ResourceArtifactType;
import es.caib.pinbal.logic.intf.base.permission.PermissionEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Helper per a la comprovació de permisos.
 *
 * @author Límit Tecnologies
 */
@Slf4j
public abstract class BasePermissionHelper {

	@Autowired
	private AuthenticationHelper authenticationHelper;

	/**
	 * Comprova els permisos per a accedir a un recurs.
	 *
	 * @param auth
	 *            l'objecte d'autenticació que s'utilitzarà per a comprovar els permisos.
	 * @param targetId
	 *            l'id del recurs (pot ser null).
	 * @param targetType
	 *            la classe del recurs.
	 * @param permissions
	 *            la llista de permisos a comprovar (si és null voldrà dir que es comprovarà qualsevol permís).
	 * @return true si l'usuari actual te accés al recurs o false en cas contrari.
	 */
	public boolean checkResourcePermission(
			Authentication auth,
			@Nullable Serializable targetId,
			String targetType,
			@Nullable BasePermission[] permissions) {
		try {
			Class<?> targetTypeClass = Class.forName(targetType);
			ResourceConfig resourceConfig = targetTypeClass.getAnnotation(ResourceConfig.class);
			if (resourceConfig != null) {
				if (resourceConfig.accessConstraints().length > 0) {
					return checkAccessConstraints(
							auth,
							targetId,
							targetTypeClass,
							null,
							null,
							resourceConfig.accessConstraints(),
							permissions);
				} else {
					// Els recursos sense restriccions d'accés tenen l'accés permès per defecte
					return true;
				}
			} else {
				// Els recursos sense configuració tenen l'accés permès per defecte
				return true;
			}
		} catch (ClassNotFoundException ex) {
			log.warn("Permission denied for resource {}: class not found", targetType, ex);
			return false;
		}
	}

	/**
	 * Comprova els permisos per a accedir a un recurs.
	 *
	 * @param targetId
	 *            l'id del recurs (pot ser null).
	 * @param targetType
	 *            la classe del recurs.
	 * @param permissions
	 *            la llista de permisos a comprovar (si és null voldrà dir que es comprovarà qualsevol permís).
	 * @return true si l'usuari actual te accés al recurs o false en cas contrari.
	 */
	public boolean checkResourcePermission(
			@Nullable Serializable targetId,
			String targetType,
			@Nullable BasePermission[] permissions) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return checkResourcePermission(
				auth,
				targetId,
				targetType,
				permissions);
	}

	/**
	 * Comprova els permisos per a accedir a un artefacte d'un recurs.
	 *
	 * @param resourceClass
	 *            la classe del recurs.
	 * @param type
	 *            El tipus d'artefacte.
	 * @param code
	 *            El codi de l'artefacte.
	 * @return true si l'usuari actual te accés a l'artefacte o false en cas contrari.
	 */
	public boolean checkResourceArtifactPermission(
			Class<?> resourceClass,
			ResourceArtifactType type,
			String code) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		ResourceConfig resourceConfig = resourceClass.getAnnotation(ResourceConfig.class);
		if (resourceConfig != null) {
			Optional<ResourceArtifact> optionalArtifact = Arrays.stream(resourceConfig.artifacts()).
					filter(a -> a.type() == type && a.code().equals(code)).
					findFirst();
			if (optionalArtifact.isPresent()) {
				ResourceArtifact artifact = optionalArtifact.get();
				if (artifact.accessConstraints().length > 0) {
					return checkAccessConstraints(
							auth,
							null,
							resourceClass,
							type,
							code,
							artifact.accessConstraints(),
							null);
				} else {
					// Els artefactes sense restriccions d'accés comproven l'accés al recurs
					BasePermission[] permissions = new BasePermission[] {
							(BasePermission)(ResourceArtifactType.ACTION.equals(type) ?
									BasePermission.WRITE :
									BasePermission.READ)
					};
					return checkResourcePermission(
							auth,
							null,
							resourceClass.getName(),
							permissions);
				}
			}
		}
		// Els artefactes que no apareixen a l'anotació @ResourceConfig del recurs no tenen l'accés permès
		return false;
	}

	protected boolean checkAccessConstraints(
			Authentication auth,
			Serializable resourceId,
			Class<?> resourceClass,
			ResourceArtifactType type,
			String code,
			ResourceAccessConstraint[] accessConstraints,
			BasePermission[] permissions) {
		ResourceAccessConstraint allowedAccessConstraint = Arrays.stream(accessConstraints).
				filter(ac -> {
					boolean accessContraintGranted = false;
					if (ac.type() == ResourceAccessConstraint.ResourceAccessConstraintType.PERMIT_ALL) {
						accessContraintGranted = true;
					} else if (ac.type() == ResourceAccessConstraint.ResourceAccessConstraintType.AUTHENTICATED) {
						accessContraintGranted = auth.isAuthenticated();
					} else if (ac.type() == ResourceAccessConstraint.ResourceAccessConstraintType.ROLE) {
						accessContraintGranted = isCurrentUserInAnyRole(auth, ac.roles());
					} else if (ac.type() == ResourceAccessConstraint.ResourceAccessConstraintType.CUSTOM) {
						if (type == null) {
							accessContraintGranted = checkCustomResourceAccessConstraint(
									auth,
									resourceId,
									resourceClass,
									ac,
									permissions);
						} else {
							accessContraintGranted = checkCustomResourceArtifactAccessConstraint(
									auth,
									resourceClass,
									type,
									code,
									ac);
						}
					}
					if (accessContraintGranted) {
						return permissions == null || isAnyPermissionGranted(permissions, ac.grantedPermissions());
					} else {
						return false;
					}
				}).
				findFirst().orElse(null);
		return allowedAccessConstraint != null;
	}

	protected boolean isCurrentUserInAnyRole(Authentication auth, String[] roles) {
		String firstUserRole = Arrays.stream(roles).
				filter(r -> authenticationHelper.isCurrentUserInRole(auth, r)).
				findFirst().orElse(null);
		return firstUserRole != null;
	}

	protected boolean isAnyPermissionGranted(
		Permission[] permissions, // Els permisos a comprovar
		PermissionEnum[] accessConstraintGrantedPermissions) { // La llista de permisos atorgats
		List<Permission> grantedPermissions = Arrays.stream(accessConstraintGrantedPermissions).
			map(PermissionEnum::toPermission).collect(Collectors.toList());
		return !Collections.disjoint(
			Arrays.asList(permissions),
			grantedPermissions);
	}

	protected abstract boolean checkCustomResourceAccessConstraint(
			Authentication auth,
			Serializable resourceId,
			Class<?> resourceClass,
			ResourceAccessConstraint resourceAccessConstraint,
			BasePermission[] permissions);

	protected abstract boolean checkCustomResourceArtifactAccessConstraint(
			Authentication auth,
			Class<?> resourceClass,
			ResourceArtifactType type,
			String code,
			ResourceAccessConstraint resourceAccessConstraint);

}
