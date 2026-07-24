package es.caib.pinbal.logic.intf.model;

import es.caib.pinbal.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.pinbal.logic.intf.base.annotation.ResourceArtifact;
import es.caib.pinbal.logic.intf.base.annotation.ResourceConfig;
import es.caib.pinbal.logic.intf.base.model.BaseResource;
import es.caib.pinbal.logic.intf.base.model.ResourceArtifactType;
import es.caib.pinbal.logic.intf.base.model.ResourceReference;
import es.caib.pinbal.logic.intf.base.permission.PermissionEnum;
import es.caib.pinbal.logic.intf.dto.OrganGestorEstatEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Informació d'un organ gestor.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
        descriptionField = OrganGestorResource.Fields.nom,
        quickFilterFields = { OrganGestorResource.Fields.codi, OrganGestorResource.Fields.nom },
        accessConstraints = @ResourceAccessConstraint(
                type = ResourceAccessConstraint.ResourceAccessConstraintType.AUTHENTICATED,
                grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE }
        ),
        artifacts = @ResourceArtifact(
                type = ResourceArtifactType.ACTION,
                code = "syncDir3",
                requiresId = false,
                formClass = OrganGestorSyncDir3Params.class
        )
)
public class OrganGestorResource extends BaseResource<Long> {

    @NotNull
    @Size(max = 64)
    private String codi;
    @Size(max = 1000)
    private String nom;
    private boolean actiu;
    private OrganGestorEstatEnum estat;

    private ResourceReference<EntitatResource, Long> entitat;
    private ResourceReference<OrganGestorResource, Long> pare;

}
