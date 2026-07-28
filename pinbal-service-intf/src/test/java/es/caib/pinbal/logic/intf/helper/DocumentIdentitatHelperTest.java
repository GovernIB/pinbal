package es.caib.pinbal.logic.intf.helper;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DocumentIdentitatHelperTest {

    // ------------------------- DNI -------------------------

    @Test
    void validacioDniAmbDniValid() {
        assertThat(DocumentIdentitatHelper.validacioDni("12345678Z")).isTrue();
    }

    @Test
    void validacioDniAmbLletraIncorrecta() {
        assertThat(DocumentIdentitatHelper.validacioDni("12345678A")).isFalse();
    }

    @Test
    void validacioDniAmbFormatIncorrecte() {
        assertThat(DocumentIdentitatHelper.validacioDni("1234567Z")).isFalse();
        assertThat(DocumentIdentitatHelper.validacioDni("abcdefghZ")).isFalse();
    }

    // ------------------------- NIE -------------------------

    @Test
    void validacioNieAmbNieValidPerCadaLletraInicial() {
        assertThat(DocumentIdentitatHelper.validacioNie("X1234567L")).isTrue();
        assertThat(DocumentIdentitatHelper.validacioNie("Y1234567X")).isTrue();
        assertThat(DocumentIdentitatHelper.validacioNie("Z1234567R")).isTrue();
    }

    @Test
    void validacioNieAmbLletraFinalIncorrecta() {
        assertThat(DocumentIdentitatHelper.validacioNie("X1234567A")).isFalse();
    }

    @Test
    void validacioNieAmbFormatIncorrecte() {
        assertThat(DocumentIdentitatHelper.validacioNie("A1234567L")).isFalse();
    }

    // ------------------------- CIF -------------------------

    @Test
    void validacioCifValidPerControlLletraIControlDigit() {
        assertThat(DocumentIdentitatHelper.validacioCif("V0000000J")).isTrue();
        assertThat(DocumentIdentitatHelper.validacioCif("V00000000")).isTrue();
    }

    @Test
    void validacioCifAmbControlIncorrecte() {
        assertThat(DocumentIdentitatHelper.validacioCif("V0000000A")).isFalse();
    }

    @Test
    void validacioCifAmbFormatIncorrecte() {
        assertThat(DocumentIdentitatHelper.validacioCif("1234567Z9")).isFalse();
    }

    // ------------------------- NIF (dispatch) -------------------------

    @Test
    void validacioNifDelegaSegonsElFormat() {
        assertThat(DocumentIdentitatHelper.validacioNif("12345678Z")).isTrue();
        assertThat(DocumentIdentitatHelper.validacioNif("X1234567L")).isTrue();
        assertThat(DocumentIdentitatHelper.validacioNif("V0000000J")).isTrue();
    }

    @Test
    void validacioNifAmbValorInvalidRetornaFals() {
        assertThat(DocumentIdentitatHelper.validacioNif("no-es-un-nif")).isFalse();
    }

    // ------------------------- passaport -------------------------

    @Test
    void validacioPassSempreRetornaCert() {
        assertThat(DocumentIdentitatHelper.validacioPass("qualsevol-valor")).isTrue();
        assertThat(DocumentIdentitatHelper.validacioPass(null)).isTrue();
    }
}
