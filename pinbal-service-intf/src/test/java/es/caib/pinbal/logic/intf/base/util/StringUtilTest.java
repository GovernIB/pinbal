package es.caib.pinbal.logic.intf.base.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StringUtilTest {

    @Test
    void capitalizeConverteixLaPrimeraLletraAMajuscula() {
        assertThat(StringUtil.capitalize("nom")).isEqualTo("Nom");
    }

    @Test
    void decapitalizeConverteixLaPrimeraLletraAMinuscula() {
        assertThat(StringUtil.decapitalize("Nom")).isEqualTo("nom");
    }

    @Test
    void removeLeadingAndTrailingCharsEliminaElsCaractersDelsExtrems() {
        assertThat(StringUtil.removeLeadingAndTrailingChars("abcdefgh", 2)).isEqualTo("cdef");
    }

    @Test
    void removeLeadingAndTrailingCharsAmbCadenaMassaCurtaRetornaBuit() {
        assertThat(StringUtil.removeLeadingAndTrailingChars("ab", 2)).isEqualTo("");
    }
}
