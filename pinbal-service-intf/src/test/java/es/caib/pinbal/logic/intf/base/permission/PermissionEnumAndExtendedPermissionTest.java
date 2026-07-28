package es.caib.pinbal.logic.intf.base.permission;

import es.caib.pinbal.logic.intf.base.exception.UnknownPermissionException;
import org.junit.jupiter.api.Test;
import org.springframework.security.acls.model.Permission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PermissionEnumAndExtendedPermissionTest {

    @Test
    void toPermissionIFromPermissionSonBidireccionalsPerCadaValorConegut() {
        for (PermissionEnum permissionEnum : PermissionEnum.values()) {
            if (permissionEnum == PermissionEnum.NULL) {
                continue;
            }
            Permission permission = PermissionEnum.toPermission(permissionEnum);
            assertThat(permission).as("permission per %s", permissionEnum).isNotNull();
            assertThat(PermissionEnum.fromPermission(permission)).isEqualTo(permissionEnum);
        }
    }

    @Test
    void toPermissionAmbNullEnumRetornaNull() {
        assertThat(PermissionEnum.toPermission(PermissionEnum.NULL)).isNull();
    }

    @Test
    void fromPermissionAmbPermisDesconegutRetornaNull() {
        Permission desconegut = org.springframework.security.acls.domain.BasePermission.ADMINISTRATION;
        // Es crea una instància pròpia que no coincideix (per equals) amb cap constant coneguda.
        Permission personalitzat = ExtendedPermission.fromMaskAndCode(1 << 20, 'Z');
        assertThat(PermissionEnum.fromPermission(personalitzat)).isNull();
    }

    @Test
    void fromEnumValueIGetEnumValueSonBidireccionalsPerCadaValorConegut() {
        for (PermissionEnum permissionEnum : PermissionEnum.values()) {
            if (permissionEnum == PermissionEnum.NULL) {
                continue;
            }
            Permission permission = ExtendedPermission.fromEnumValue(permissionEnum);
            assertThat(permission).as("permission per %s", permissionEnum).isNotNull();
            assertThat(ExtendedPermission.getEnumValue(permission.getMask())).isEqualTo(permissionEnum);
            assertThat(ExtendedPermission.getName(permission.getMask())).isEqualTo(permissionEnum.name());
        }
    }

    @Test
    void fromEnumValueAmbNullRetornaNull() {
        assertThat(ExtendedPermission.fromEnumValue(PermissionEnum.NULL)).isNull();
    }

    @Test
    void getEnumValueAmbMascaraDesconegudaLlancaUnknownPermissionException() {
        assertThatThrownBy(() -> ExtendedPermission.getEnumValue(1 << 25))
                .isInstanceOf(UnknownPermissionException.class);
    }

    @Test
    void fromMaskCreaUnaInstanciaAmbLaMascaraIndicada() {
        ExtendedPermission permission = ExtendedPermission.fromMask(1 << 5);
        assertThat(permission.getMask()).isEqualTo(1 << 5);
    }

    @Test
    void fromMaskAndCodeCreaUnaInstanciaAmbMascaraICodi() {
        ExtendedPermission permission = ExtendedPermission.fromMaskAndCode(1 << 6, '1');
        assertThat(permission.getMask()).isEqualTo(1 << 6);
        assertThat(permission.getPattern()).contains("1");
    }
}
