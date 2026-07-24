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
 * Recurs de només lectura dels grups de propietats de configuració. El catàleg de grups
 * (i la seva jerarquia) es defineix a les dades inicials de l'aplicació, no és editable des
 * d'aquí (igual que al JSP).
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
        descriptionField = ConfigGroupResource.Fields.description,
        accessConstraints = @ResourceAccessConstraint(
                type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
                roles = BaseConfig.ROLE_ADMIN,
                grantedPermissions = { PermissionEnum.READ }
        )
)
public class ConfigGroupResource extends BaseResource<String> {

    private String descriptionKey;

    // Text ja resolt via Spring MessageSource (mateix mecanisme que <spring:message> al JSP),
    // perquè el frontend no hagi de dur el seu propi catàleg de traduccions per a un catàleg de
    // grups que es defineix a dades inicials de l'aplicació.
    private String description;

    private int position;
    private String parentCode;

}
