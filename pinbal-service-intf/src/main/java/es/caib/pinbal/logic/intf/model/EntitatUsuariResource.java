package es.caib.pinbal.logic.intf.model;

import es.caib.pinbal.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.pinbal.logic.intf.base.annotation.ResourceConfig;
import es.caib.pinbal.logic.intf.base.model.BaseResource;
import es.caib.pinbal.logic.intf.base.model.ResourceReference;
import es.caib.pinbal.logic.intf.base.permission.PermissionEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.Size;

/**
 * Informació de la relació entre una entitat i un usuari.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
        descriptionField = EntitatUsuariResource.Fields.usuariCodi,
        quickFilterFields = { EntitatUsuariResource.Fields.usuariCodi, EntitatUsuariResource.Fields.departament },
        accessConstraints = @ResourceAccessConstraint(
                type = ResourceAccessConstraint.ResourceAccessConstraintType.AUTHENTICATED,
                grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE }
        )
)
public class EntitatUsuariResource extends BaseResource<Long> {

    private ResourceReference<EntitatResource, Long> entitat;
    private ResourceReference<UsuariResource, String> usuari;
    private String usuariCodi;
    @Size(max = 64)
    private String departament;
    private boolean principal;
    private boolean representant;
    private boolean delegat;
    private boolean auditor;
    private boolean aplicacio;
    private boolean actiu;

}
