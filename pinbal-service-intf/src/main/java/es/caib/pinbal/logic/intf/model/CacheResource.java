package es.caib.pinbal.logic.intf.model;

import es.caib.pinbal.logic.intf.base.annotation.ResourceAccessConstraint;
import es.caib.pinbal.logic.intf.base.annotation.ResourceArtifact;
import es.caib.pinbal.logic.intf.base.annotation.ResourceConfig;
import es.caib.pinbal.logic.intf.base.config.BaseConfig;
import es.caib.pinbal.logic.intf.base.model.BaseResource;
import es.caib.pinbal.logic.intf.base.model.ResourceArtifactType;
import es.caib.pinbal.logic.intf.base.permission.PermissionEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

/**
 * Recurs de consulta i buidatge de les caches de l'aplicació.
 * <p>
 * No hi ha CRUD real: només es pot llistar i buidar (una cache concreta o totes), per això
 * {@code create}/{@code update}/{@code delete} es bloquegen a la implementació.
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
                roles = BaseConfig.ROLE_ADMIN,
                grantedPermissions = { PermissionEnum.READ, PermissionEnum.WRITE }
        ),
        artifacts = {
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = "buidarCache",
                        requiresId = false,
                        formClass = CacheBuidarParams.class
                ),
                @ResourceArtifact(
                        type = ResourceArtifactType.ACTION,
                        code = "buidarTotesCaches",
                        requiresId = false
                )
        }
)
public class CacheResource extends BaseResource<String> {

    private String codi;
    private long localHeapSize;

}
