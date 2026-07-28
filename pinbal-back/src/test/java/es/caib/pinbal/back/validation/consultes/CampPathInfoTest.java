package es.caib.pinbal.back.validation.consultes;

import es.caib.pinbal.logic.intf.dto.ServeiCampDto.ServeiCampDtoTipus;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CampPathInfoTest {

    private CampPathInfo camp(ServeiCampDtoTipus tipus) {
        return CampPathInfo.builder().tipus(tipus).build();
    }

    @Test
    public void isValidValueAmbNullSempreEsValid() {
        assertTrue(camp(ServeiCampDtoTipus.NUMERIC).isValidValue(null));
    }

    @Test
    public void isValidValueNumericAmbEnterEsValid() {
        assertTrue(camp(ServeiCampDtoTipus.NUMERIC).isValidValue("42"));
        assertTrue(camp(ServeiCampDtoTipus.NUMERIC).isValidValue("42.0"));
        assertFalse(camp(ServeiCampDtoTipus.NUMERIC).isValidValue("abc"));
    }

    @Test
    public void isValidValueDataAmbFormatCorrecte() {
        assertTrue(camp(ServeiCampDtoTipus.DATA).isValidValue("31/01/2024"));
        assertFalse(camp(ServeiCampDtoTipus.DATA).isValidValue("31/02/2024"));
    }

    @Test
    public void isValidValueEnumComprovaValorsPermesos() {
        CampPathInfo camp = CampPathInfo.builder()
                .tipus(ServeiCampDtoTipus.ENUM)
                .valorsPermesos(List.of("A", "B"))
                .build();

        assertTrue(camp.isValidValue("A"));
        assertFalse(camp.isValidValue("C"));
    }

    @Test
    public void isValidValuePaisComprovaLongitud() {
        assertTrue(camp(ServeiCampDtoTipus.PAIS).isValidValue("ESP"));
        assertFalse(camp(ServeiCampDtoTipus.PAIS).isValidValue("ES"));
    }

    @Test
    public void isValidValueProvinciaComprovaDosDigits() {
        assertTrue(camp(ServeiCampDtoTipus.PROVINCIA).isValidValue("07"));
        assertFalse(camp(ServeiCampDtoTipus.PROVINCIA).isValidValue("7"));
        assertFalse(camp(ServeiCampDtoTipus.PROVINCIA).isValidValue("abc"));
    }

    @Test
    public void isValidValueMunicipi3ComprovaTresDigits() {
        assertTrue(camp(ServeiCampDtoTipus.MUNICIPI_3).isValidValue("123"));
        assertFalse(camp(ServeiCampDtoTipus.MUNICIPI_3).isValidValue("12"));
    }

    @Test
    public void isValidValueMunicipi5ComprovaCincDigits() {
        assertTrue(camp(ServeiCampDtoTipus.MUNICIPI_5).isValidValue("12345"));
        assertFalse(camp(ServeiCampDtoTipus.MUNICIPI_5).isValidValue("1234"));
    }

    @Test
    public void isValidValueBooleaComprovaTrueFalse() {
        assertTrue(camp(ServeiCampDtoTipus.BOOLEA).isValidValue("true"));
        assertTrue(camp(ServeiCampDtoTipus.BOOLEA).isValidValue("FALSE"));
        assertFalse(camp(ServeiCampDtoTipus.BOOLEA).isValidValue("potser"));
    }

    @Test
    public void isValidValueTextSempreEsValid() {
        assertTrue(camp(ServeiCampDtoTipus.TEXT).isValidValue("qualsevol"));
    }

    @Test
    public void getValueAmbNullRetornaNull() {
        assertEquals(null, camp(ServeiCampDtoTipus.NUMERIC).getValue(null));
    }

    @Test
    public void getValueNumericRetornaInteger() {
        assertEquals(42, camp(ServeiCampDtoTipus.NUMERIC).getValue("42"));
    }

    @Test
    public void getValueNumericAmbValorNoNumericRetornaElString() {
        assertEquals("abc", camp(ServeiCampDtoTipus.NUMERIC).getValue("abc"));
    }

    @Test
    public void getValueDataRetornaDate() {
        assertInstanceOf(Date.class, camp(ServeiCampDtoTipus.DATA).getValue("31/01/2024"));
    }

    @Test
    public void getValueDataAmbFormatInvalidRetornaElString() {
        assertEquals("no-es-una-data", camp(ServeiCampDtoTipus.DATA).getValue("no-es-una-data"));
    }

    @Test
    public void getValueBooleaRetornaBoolean() {
        assertEquals(Boolean.TRUE, camp(ServeiCampDtoTipus.BOOLEA).getValue("true"));
    }

    @Test
    public void getValueProvinciaRetornaStringFormatat() {
        assertEquals("07", camp(ServeiCampDtoTipus.PROVINCIA).getValue("07.0"));
    }

    @Test
    public void getValueDefaultRetornaElMateixValor() {
        assertEquals("text", camp(ServeiCampDtoTipus.TEXT).getValue("text"));
    }

    @Test
    public void isDadaEspecificaAmbPathQueComencaPerDatosespecificos() {
        CampPathInfo camp = CampPathInfo.builder().path("DatosEspecificos.camp1").build();

        assertTrue(camp.isDadaEspecifica());
    }

    @Test
    public void isDadaEspecificaAmbAltrePathEsFalse() {
        CampPathInfo camp = CampPathInfo.builder().path("Solicitante.nombre").build();

        assertFalse(camp.isDadaEspecifica());
    }

    @Test
    public void isDadaEspecificaAmbPathNullEsFalse() {
        assertFalse(CampPathInfo.builder().build().isDadaEspecifica());
    }
}
