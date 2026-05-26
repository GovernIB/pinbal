package es.caib.pinbal.logic.base.service;

import es.caib.pinbal.logic.base.helper.BasePermissionHelper;
import es.caib.pinbal.logic.intf.base.model.Resource;
import es.caib.pinbal.logic.intf.base.permission.ResourcePermissions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementació del servei de l'API REST.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
public class ResourceApiService implements es.caib.pinbal.logic.intf.base.service.ResourceApiService {

	@Autowired
	private BasePermissionHelper permissionHelper;

	private final Set<Class<? extends Resource<?>>> registeredResources = new HashSet<>();

	@Override
	public void resourceRegister(Class<? extends Resource<?>> resourceClass) {
		log.info("New resource registered (class={})", resourceClass);
		registeredResources.add(resourceClass);
	}

	@Override
	public List<Class<? extends Resource<?>>> resourceFindAllowed() {
		return new ArrayList<>(registeredResources).stream().
				filter(this::isResourceAllowed).
				sorted(Comparator.comparing(Class::getSimpleName)).
				collect(Collectors.toList());
	}

	@Override
	public ResourcePermissions permissionsCurrentUser(Class<?> resourceClass, Serializable resourceId) {
		boolean isReadGranted = permissionHelper.checkResourcePermission(
				resourceId,
				resourceClass.getName(),
				new BasePermission[] { (BasePermission)BasePermission.READ });
		boolean isWriteGranted = permissionHelper.checkResourcePermission(
				resourceId,
				resourceClass.getName(),
				new BasePermission[] { (BasePermission)BasePermission.WRITE });
		boolean isCreateGranted = permissionHelper.checkResourcePermission(
				resourceId,
				resourceClass.getName(),
				new BasePermission[] { (BasePermission)BasePermission.CREATE });
		boolean isDeleteGranted = permissionHelper.checkResourcePermission(
				resourceId,
				resourceClass.getName(),
				new BasePermission[] { (BasePermission)BasePermission.DELETE });
		return new ResourcePermissions(
				isReadGranted,
				isWriteGranted,
				isCreateGranted,
				isDeleteGranted);
	}

	private boolean isResourceAllowed(Class<? extends Resource<?>> resourceClass) {
		return permissionHelper.checkResourcePermission(
				null,
				resourceClass.getName(),
				null);
	}

}
