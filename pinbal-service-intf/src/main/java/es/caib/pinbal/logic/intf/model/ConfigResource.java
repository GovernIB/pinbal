package es.caib.pinbal.logic.intf.model;

import es.caib.pinbal.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.pinbal.logic.intf.base.annotation.ResourceArtifact;
import es.caib.pinbal.logic.intf.base.annotation.ResourceConfig;
import es.caib.pinbal.logic.intf.base.config.BaseConfig;
import es.caib.pinbal.logic.intf.base.model.BaseResource;
import es.caib.pinbal.logic.intf.base.model.ResourceArtifactType;
import es.caib.pinbal.logic.intf.base.permission.PermissionEnum;
import es.caib.pinbal.logic.intf.dto.ConfigSourceEnumDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.List;

/**
 * Recurs de manteniment de les propietats de configuració de l'aplicació.
 * <p>
 * El catàleg de claus (i el seu tipus/grup) es defineix a les dades inicials de l'aplicació:
 * només el {@code value} és editable, i només si {@code sourceProperty == DATABASE} (JSP:
 * {@code Config#isEditable}) — per això {@code create}/{@code delete} es bloquegen a la
 * implementació.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@ResourceConfig(
        quickFilterFields = { "key", "value" },
        accessConstraints = @ResourceAccessConstraint(
                type = ResourceAccessConstraint.ResourceAccessConstraintType.ROLE,
                roles = BaseConfig.ROLE_ADMIN,
                grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE }
        ),
        artifacts = {
                // Sincronitza els valors amb les propietats de JBoss (JSP: "Sincronitzar amb JBoss").
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = "syncFromJBoss",
                        requiresId = false
                ),
                // Reinicia les tasques programades en segon pla (JSP: "Reiniciar tasques en segon pla").
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = "reiniciarTasques",
                        requiresId = false
                )
        }
)
public class ConfigResource extends BaseResource<String> {

    private String key;
    private String value;
    private String descriptionKey;

    // Text ja resolt via Spring MessageSource (mateix mecanisme que <spring:message> al JSP).
    private String description;

    private ConfigSourceEnumDto sourceProperty;
    private String groupCode;
    private int position;

    // Camps calculats (no editables), equivalents a Config#getTypeCode/getValidValues/isEditable.
    private String typeCode;
    private List<String> validValues;
    private boolean editable;

}
