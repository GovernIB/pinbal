package es.caib.pinbal.logic.intf.model;

import es.caib.pinbal.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.pinbal.logic.intf.base.annotation.ResourceConfig;
import es.caib.pinbal.logic.intf.base.config.BaseConfig;
import es.caib.pinbal.logic.intf.base.model.BaseResource;
import es.caib.pinbal.logic.intf.base.permission.PermissionEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

/**
 * Recurs de només lectura dels organismes cessionaris SCSP, usat com a picker de referència des
 * de {@link ClauPrivadaResource}.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
        descriptionField = OrganismeCessionariResource.Fields.nom,
        quickFilterFields = { OrganismeCessionariResource.Fields.nom, OrganismeCessionariResource.Fields.cif },
        accessConstraints = @ResourceAccessConstraint(
                type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
                roles = BaseConfig.ROLE_ADMIN,
                grantedPermissions = { PermissionEnum.READ }
        )
)
public class OrganismeCessionariResource extends BaseResource<Long> {

    private String nom;
    private String cif;
    private Boolean bloquejat;

}
