package es.caib.pinbal.logic.intf.model;

import es.caib.pinbal.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.pinbal.logic.intf.base.annotation.ResourceConfig;
import es.caib.pinbal.logic.intf.base.model.BaseResource;
import es.caib.pinbal.logic.intf.base.permission.PermissionEnum;
import es.caib.pinbal.logic.intf.dto.EntitatDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * Informació d'una entitat
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
        descriptionField = EntitatResource.Fields.nom,
        quickFilterFields = { EntitatResource.Fields.codi, EntitatResource.Fields.nom },
        accessConstraints = @ResourceAccessConstraint(
                type = ResourceAccessConstraint.ResourceAccessConstraintType.AUTHENTICATED,
                grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE }
        )
)
public class EntitatResource extends BaseResource<Long> {

    @NotNull
    @Size(max = 64)
    @EqualsAndHashCode.Include
    private String codi;
    @NotNull
    @Size(max = 255)
    private String nom;
    @NotNull
    @Size(max = 16)
    private String cif;
    @NotNull
    @Size(max = 9)
    private String unitatArrel;
    @NotNull
    private EntitatDto.EntitatTipusDto tipus;
    private boolean activa;

    private List<EntitatServeiResource> serveis;
    private List<ServeiBusResource> serveisBus;
    private List<EntitatUsuariResource> usuaris;
    private List<ProcedimentResource> procediments;
    private List<OrganGestorResource> organGestors;

}
