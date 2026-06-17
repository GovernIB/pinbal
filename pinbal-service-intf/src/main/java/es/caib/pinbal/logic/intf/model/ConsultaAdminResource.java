package es.caib.pinbal.logic.intf.model;

import es.caib.pinbal.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.pinbal.logic.intf.base.annotation.ResourceConfig;
import es.caib.pinbal.logic.intf.base.config.BaseConfig;
import es.caib.pinbal.logic.intf.base.model.BaseResource;
import es.caib.pinbal.logic.intf.base.model.ResourceReference;
import es.caib.pinbal.logic.intf.base.permission.PermissionEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.Date;

/**
 * Recurs per a la consulta de consultes SCSP per part de l'administrador.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
        accessConstraints = @ResourceAccessConstraint(
                type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
                roles = { BaseConfig.ROLE_ADMIN },
                grantedPermissions = { PermissionEnum.READ }
        )
)
public class ConsultaAdminResource extends BaseResource<Long> {

    private String scspPeticionId;
    private Date creacioData;
    private String creacioUsuariNomCodi;
    private String funcionariNomAmbDocument;
    private String procedimentCodiNom;
    private String serveiCodiNom;
    private String estat;
    private Date dataEsperadaResposta;
    private String justificantEstat;
    private boolean recobriment;
    private boolean multiple;
    private ResourceReference<EntitatResource, Long> entitat;

}
