package es.caib.pinbal.logic.intf.model;

import es.caib.pinbal.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.pinbal.logic.intf.base.annotation.ResourceConfig;
import es.caib.pinbal.logic.intf.base.model.BaseResource;
import es.caib.pinbal.logic.intf.base.permission.PermissionEnum;
import es.caib.pinbal.logic.intf.dto.ServeiDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Informació d'un servei SCSP.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
        descriptionField = ServeiResource.Fields.descripcio,
        quickFilterFields = { ServeiResource.Fields.codi, ServeiResource.Fields.descripcio },
        accessConstraints = @ResourceAccessConstraint(
                type = ResourceAccessConstraint.ResourceAccessConstraintType.AUTHENTICATED,
                grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE }
        )
)
public class ServeiResource extends BaseResource<Long> {

    @NotNull
    @Size(max = 64)
    private String codi;
    @NotNull
    @Size(max = 512)
    private String descripcio;
    private boolean actiu;
    private ServeiDto.EntitatTipusDto pinbalEntitatTipus;
    @Size(max = 64)
    private String pinbalRoleName;
    private boolean pinbalPermesDocumentTipusDni;
    private boolean pinbalPermesDocumentTipusNif;
    private boolean pinbalPermesDocumentTipusCif;
    private boolean pinbalPermesDocumentTipusNie;
    private boolean pinbalPermesDocumentTipusPas;
    private boolean pinbalDocumentObligatori;
    private Integer maxPeticionsMinut;

}
