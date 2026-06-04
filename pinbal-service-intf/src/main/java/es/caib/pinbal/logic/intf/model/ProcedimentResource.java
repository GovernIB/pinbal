package es.caib.pinbal.logic.intf.model;

import es.caib.pinbal.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.pinbal.logic.intf.base.annotation.ResourceConfig;
import es.caib.pinbal.logic.intf.base.model.BaseResource;
import es.caib.pinbal.logic.intf.base.model.ResourceReference;
import es.caib.pinbal.logic.intf.base.permission.PermissionEnum;
import es.caib.pinbal.logic.intf.dto.ProcedimentClaseTramiteEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Informació d'un procediment administratiu.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
        descriptionField = ProcedimentResource.Fields.nom,
        quickFilterFields = { ProcedimentResource.Fields.codi, ProcedimentResource.Fields.nom },
        accessConstraints = @ResourceAccessConstraint(
                type = ResourceAccessConstraint.ResourceAccessConstraintType.AUTHENTICATED,
                grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE }
        )
)
public class ProcedimentResource extends BaseResource<Long> {

    @NotNull
    @Size(max = 20)
    private String codi;
    @NotNull
    @Size(max = 255)
    private String nom;
    @Size(max = 64)
    private String departament;
    private boolean actiu;
    @Size(max = 64)
    private String codiSia;
    private Boolean valorCampAutomatizado;
    private ProcedimentClaseTramiteEnumDto valorCampClaseTramite;

    private ResourceReference<EntitatResource, Long> entitat;
    private ResourceReference<OrganGestorResource, Long> organGestor;

}
