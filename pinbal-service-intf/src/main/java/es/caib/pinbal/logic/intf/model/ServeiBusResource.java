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

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Informació d'una redirecció de servei per al bus de serveis.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
        descriptionField = ServeiBusResource.Fields.serveiCodi,
        quickFilterFields = { ServeiBusResource.Fields.serveiCodi, ServeiBusResource.Fields.urlDesti },
        accessConstraints = @ResourceAccessConstraint(
                type = ResourceAccessConstraint.ResourceAccessConstraintType.AUTHENTICATED,
                grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE }
        )
)
public class ServeiBusResource extends BaseResource<Long> {

    @NotNull
    @Size(max = 64)
    private String serveiCodi;
    @NotNull
    @Size(max = 255)
    private String urlDesti;
    private ResourceReference<EntitatResource, Long> entitat;

}
