package es.caib.pinbal.back.validation.consultes;

import es.caib.pinbal.back.command.ConsultaCommand;
import es.caib.pinbal.logic.intf.dto.ServeiCampDto;
import es.caib.pinbal.logic.intf.dto.ServeiCampDto.ServeiCampDtoTipus;
import es.caib.pinbal.logic.intf.dto.ServeiCampDto.ServeiCampDtoValidacioDataTipus;
import es.caib.pinbal.logic.intf.dto.ServeiCampDto.ServeiCampDtoValidacioOperacio;
import es.caib.pinbal.logic.intf.dto.ServeiCampGrupDto;
import es.caib.pinbal.logic.intf.dto.regles.CampFormProperties;
import es.caib.pinbal.logic.intf.service.ServeiService;
import es.caib.pinbal.logic.intf.service.exception.ServeiNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests de {@link DadesConsultaSimpleValidator}: valida els valors de les dades específiques
 * d'una consulta (obligatorietat, format de regex/numèric/data) i, en un segon pas, les regles
 * dinàmiques de camps/grups que arriben via {@code ServeiService}.
 */
public class DadesConsultaSimpleValidatorTest {

    private static final String SERVEI_CODI = "SERV1";

    private ServeiService serveiService;
    private DadesConsultaSimpleValidator validator;

    @BeforeEach
    public void configurar() {
        serveiService = mock(ServeiService.class);
        validator = new DadesConsultaSimpleValidator(serveiService, SERVEI_CODI);
    }

    private ServeiCampDto camp(String path, ServeiCampDtoTipus tipus, boolean obligatori) {
        ServeiCampDto camp = new ServeiCampDto();
        camp.setPath(path);
        camp.setTipus(tipus);
        camp.setObligatori(obligatori);
        return camp;
    }

    private ConsultaCommand command(Map<String, Object> dadesEspecifiques) {
        ConsultaCommand command = new ConsultaCommand();
        command.setDadesEspecifiques(dadesEspecifiques);
        return command;
    }

    private void mockCampsIGrups(List<ServeiCampDto> camps, List<ServeiCampGrupDto> grups) throws ServeiNotFoundException {
        when(serveiService.findServeiCamps(SERVEI_CODI)).thenReturn(camps);
        when(serveiService.findServeiCampGrupsAndSubgrups(SERVEI_CODI)).thenReturn(grups);
    }

    private void mockCampsRegles(List<CampFormProperties> campsRegles) throws ServeiNotFoundException {
        when(serveiService.getCampsByserveiRegla(eq(SERVEI_CODI), any())).thenReturn(campsRegles);
        when(serveiService.getGrupsByserveiRegla(eq(SERVEI_CODI), any())).thenReturn(null);
    }

    // ------------------------- supports -------------------------

    @Test
    public void supportsConsultaCommandEsTrue() {
        assertTrue(validator.supports(ConsultaCommand.class));
    }

    @Test
    public void supportsAltraClasseEsFalse() {
        assertFalse(validator.supports(String.class));
    }

    // ------------------------- obligatorietat -------------------------

    @Test
    public void campObligatoriBuitGeneraError() throws Exception {
        ServeiCampDto camp = camp("Camp1", ServeiCampDtoTipus.TEXT, true);
        mockCampsIGrups(List.of(camp), List.of());
        mockCampsRegles(null);

        Errors errors = new BeanPropertyBindingResult(command(new HashMap<>()), "command");
        validator.validate(command(new HashMap<>()), errors);

        assertTrue(errors.hasFieldErrors("dadesEspecifiques[Camp1]"));
        assertEquals("NotEmpty", errors.getFieldError("dadesEspecifiques[Camp1]").getCode());
    }

    @Test
    public void campObligatoriAmbStringBuidaGeneraError() throws Exception {
        ServeiCampDto camp = camp("Camp1", ServeiCampDtoTipus.TEXT, true);
        mockCampsIGrups(List.of(camp), List.of());
        mockCampsRegles(null);
        Map<String, Object> dades = new HashMap<>();
        dades.put("Camp1", "");

        Errors errors = new BeanPropertyBindingResult(command(dades), "command");
        validator.validate(command(dades), errors);

        assertTrue(errors.hasFieldErrors("dadesEspecifiques[Camp1]"));
    }

    @Test
    public void campOpcionalBuitNoGeneraError() throws Exception {
        ServeiCampDto camp = camp("Camp1", ServeiCampDtoTipus.TEXT, false);
        mockCampsIGrups(List.of(camp), List.of());
        mockCampsRegles(null);

        Errors errors = new BeanPropertyBindingResult(command(new HashMap<>()), "command");
        validator.validate(command(new HashMap<>()), errors);

        assertFalse(errors.hasErrors());
    }

    // ------------------------- TEXT / regex -------------------------

    @Test
    public void textAmbValorQueCompleixElRegexNoGeneraError() throws Exception {
        ServeiCampDto camp = camp("Camp1", ServeiCampDtoTipus.TEXT, false);
        camp.setValidacioRegexp("[A-Z]{3}");
        mockCampsIGrups(List.of(camp), List.of());
        mockCampsRegles(null);
        Map<String, Object> dades = Map.of("Camp1", "ABC");

        Errors errors = new BeanPropertyBindingResult(command(dades), "command");
        validator.validate(command(dades), errors);

        assertFalse(errors.hasErrors());
    }

    @Test
    public void textAmbValorQueNoCompleixElRegexGeneraError() throws Exception {
        ServeiCampDto camp = camp("Camp1", ServeiCampDtoTipus.TEXT, false);
        camp.setValidacioRegexp("[A-Z]{3}");
        mockCampsIGrups(List.of(camp), List.of());
        mockCampsRegles(null);
        Map<String, Object> dades = Map.of("Camp1", "ab");

        Errors errors = new BeanPropertyBindingResult(command(dades), "command");
        validator.validate(command(dades), errors);

        assertEquals("RegexpDontMatch", errors.getFieldError("dadesEspecifiques[Camp1]").getCode());
    }

    @Test
    public void textAmbRegexInvalidGeneraErrorDeRegexInvalid() throws Exception {
        ServeiCampDto camp = camp("Camp1", ServeiCampDtoTipus.TEXT, false);
        camp.setValidacioRegexp("[A-Z{3}"); // regex mal format
        mockCampsIGrups(List.of(camp), List.of());
        mockCampsRegles(null);
        Map<String, Object> dades = Map.of("Camp1", "abc");

        Errors errors = new BeanPropertyBindingResult(command(dades), "command");
        validator.validate(command(dades), errors);

        assertEquals("InvalidRegexp", errors.getFieldError("dadesEspecifiques[Camp1]").getCode());
    }

    // ------------------------- NUMERIC -------------------------

    @Test
    public void numericDinsDelRangAmbEnterNoGeneraError() throws Exception {
        ServeiCampDto camp = camp("Camp1", ServeiCampDtoTipus.NUMERIC, false);
        camp.setValidacioMin(1);
        camp.setValidacioMax(10);
        mockCampsIGrups(List.of(camp), List.of());
        mockCampsRegles(null);
        Map<String, Object> dades = Map.of("Camp1", 5);

        Errors errors = new BeanPropertyBindingResult(command(dades), "command");
        validator.validate(command(dades), errors);

        assertFalse(errors.hasErrors());
    }

    @Test
    public void numericDinsDelRangAmbStringNoGeneraError() throws Exception {
        ServeiCampDto camp = camp("Camp1", ServeiCampDtoTipus.NUMERIC, false);
        camp.setValidacioMin(1);
        camp.setValidacioMax(10);
        mockCampsIGrups(List.of(camp), List.of());
        mockCampsRegles(null);
        Map<String, Object> dades = Map.of("Camp1", "5");

        Errors errors = new BeanPropertyBindingResult(command(dades), "command");
        validator.validate(command(dades), errors);

        assertFalse(errors.hasErrors());
    }

    @Test
    public void numericPerSotaDelMinimGeneraError() throws Exception {
        ServeiCampDto camp = camp("Camp1", ServeiCampDtoTipus.NUMERIC, false);
        camp.setValidacioMin(10);
        mockCampsIGrups(List.of(camp), List.of());
        mockCampsRegles(null);
        Map<String, Object> dades = Map.of("Camp1", 5);

        Errors errors = new BeanPropertyBindingResult(command(dades), "command");
        validator.validate(command(dades), errors);

        assertEquals("NumericRangeExceeded", errors.getFieldError("dadesEspecifiques[Camp1]").getCode());
    }

    @Test
    public void numericPerSobreDelMaximGeneraError() throws Exception {
        ServeiCampDto camp = camp("Camp1", ServeiCampDtoTipus.NUMERIC, false);
        camp.setValidacioMax(10);
        mockCampsIGrups(List.of(camp), List.of());
        mockCampsRegles(null);
        Map<String, Object> dades = Map.of("Camp1", 20);

        Errors errors = new BeanPropertyBindingResult(command(dades), "command");
        validator.validate(command(dades), errors);

        assertEquals("NumericRangeExceeded", errors.getFieldError("dadesEspecifiques[Camp1]").getCode());
    }

    // ------------------------- DATA -------------------------

    @Test
    public void dataAmbFormatValidSenseSegonCampNoGeneraError() throws Exception {
        ServeiCampDto camp = camp("Camp1", ServeiCampDtoTipus.DATA, false);
        mockCampsIGrups(List.of(camp), List.of());
        mockCampsRegles(null);
        Map<String, Object> dades = Map.of("Camp1", "15/03/2024");

        Errors errors = new BeanPropertyBindingResult(command(dades), "command");
        validator.validate(command(dades), errors);

        assertFalse(errors.hasErrors());
    }

    @Test
    public void dataAmbFormatInvalidGeneraError() throws Exception {
        ServeiCampDto camp = camp("Camp1", ServeiCampDtoTipus.DATA, false);
        mockCampsIGrups(List.of(camp), List.of());
        mockCampsRegles(null);
        Map<String, Object> dades = Map.of("Camp1", "31/02/2024");

        Errors errors = new BeanPropertyBindingResult(command(dades), "command");
        validator.validate(command(dades), errors);

        assertEquals("DataValida", errors.getFieldError("dadesEspecifiques[Camp1]").getCode());
    }

    @Test
    public void dataAmbSegonCampInvalidGeneraErrorDeReferencia() throws Exception {
        ServeiCampDto camp2 = camp("Camp2", ServeiCampDtoTipus.DATA, false);
        ServeiCampDto camp1 = camp("Camp1", ServeiCampDtoTipus.DATA, false);
        camp1.setValidacioDataCmpCamp2(camp2);
        camp1.setValidacioDataCmpOperacio(ServeiCampDtoValidacioOperacio.LT);
        mockCampsIGrups(List.of(camp1, camp2), List.of());
        mockCampsRegles(null);
        Map<String, Object> dades = Map.of("Camp1", "15/03/2024", "Camp2", "no-es-una-data");

        Errors errors = new BeanPropertyBindingResult(command(dades), "command");
        validator.validate(command(dades), errors);

        assertEquals("DataReferencia", errors.getFieldError("dadesEspecifiques[Camp1]").getCode());
    }

    @Test
    public void dataComparacioLtAmbDatesEnOrdreNoGeneraError() throws Exception {
        ServeiCampDto camp2 = camp("Camp2", ServeiCampDtoTipus.DATA, false);
        ServeiCampDto camp1 = camp("Camp1", ServeiCampDtoTipus.DATA, false);
        camp1.setValidacioDataCmpCamp2(camp2);
        camp1.setValidacioDataCmpOperacio(ServeiCampDtoValidacioOperacio.LT);
        mockCampsIGrups(List.of(camp1, camp2), List.of());
        mockCampsRegles(null);
        Map<String, Object> dades = Map.of("Camp1", "01/01/2024", "Camp2", "02/01/2024");

        Errors errors = new BeanPropertyBindingResult(command(dades), "command");
        validator.validate(command(dades), errors);

        assertFalse(errors.hasErrors());
    }

    @Test
    public void dataComparacioLtAmbDatesEnOrdreInvertitGeneraError() throws Exception {
        ServeiCampDto camp2 = camp("Camp2", ServeiCampDtoTipus.DATA, false);
        ServeiCampDto camp1 = camp("Camp1", ServeiCampDtoTipus.DATA, false);
        camp1.setValidacioDataCmpCamp2(camp2);
        camp1.setValidacioDataCmpOperacio(ServeiCampDtoValidacioOperacio.LT);
        mockCampsIGrups(List.of(camp1, camp2), List.of());
        mockCampsRegles(null);
        Map<String, Object> dades = Map.of("Camp1", "02/01/2024", "Camp2", "01/01/2024");

        Errors errors = new BeanPropertyBindingResult(command(dades), "command");
        validator.validate(command(dades), errors);

        assertEquals("DataValida", errors.getFieldError("dadesEspecifiques[Camp1]").getCode());
    }

    @Test
    public void dataDiferenciaEnMesosDinsDelLimitNoGeneraError() throws Exception {
        ServeiCampDto camp2 = camp("Camp2", ServeiCampDtoTipus.DATA, false);
        ServeiCampDto camp1 = camp("Camp1", ServeiCampDtoTipus.DATA, false);
        camp1.setValidacioDataCmpCamp2(camp2);
        camp1.setValidacioDataCmpOperacio(ServeiCampDtoValidacioOperacio.GTE);
        camp1.setValidacioDataCmpNombre(2);
        camp1.setValidacioDataCmpTipus(ServeiCampDtoValidacioDataTipus.MESOS);
        mockCampsIGrups(List.of(camp1, camp2), List.of());
        mockCampsRegles(null);
        // El període es calcula de Camp1 a Camp2: 3 mesos de diferència, >= 2 mesos, compleix
        Map<String, Object> dades = Map.of("Camp1", "01/01/2024", "Camp2", "01/04/2024");

        Errors errors = new BeanPropertyBindingResult(command(dades), "command");
        validator.validate(command(dades), errors);

        assertFalse(errors.hasErrors());
    }

    @Test
    public void dataDiferenciaEnMesosPerSotaDelLimitGeneraError() throws Exception {
        ServeiCampDto camp2 = camp("Camp2", ServeiCampDtoTipus.DATA, false);
        ServeiCampDto camp1 = camp("Camp1", ServeiCampDtoTipus.DATA, false);
        camp1.setValidacioDataCmpCamp2(camp2);
        camp1.setValidacioDataCmpOperacio(ServeiCampDtoValidacioOperacio.GTE);
        camp1.setValidacioDataCmpNombre(6);
        camp1.setValidacioDataCmpTipus(ServeiCampDtoValidacioDataTipus.MESOS);
        mockCampsIGrups(List.of(camp1, camp2), List.of());
        mockCampsRegles(null);
        // El període es calcula de Camp1 a Camp2: 1 mes de diferència, no arriba als 6 exigits
        Map<String, Object> dades = Map.of("Camp1", "01/01/2024", "Camp2", "01/02/2024");

        Errors errors = new BeanPropertyBindingResult(command(dades), "command");
        validator.validate(command(dades), errors);

        assertEquals("DataValida", errors.getFieldError("dadesEspecifiques[Camp1]").getCode());
    }

    @Test
    public void dataDiferenciaEnAnysAmbLimitExacteNoGeneraError() throws Exception {
        ServeiCampDto camp2 = camp("Camp2", ServeiCampDtoTipus.DATA, false);
        ServeiCampDto camp1 = camp("Camp1", ServeiCampDtoTipus.DATA, false);
        camp1.setValidacioDataCmpCamp2(camp2);
        camp1.setValidacioDataCmpOperacio(ServeiCampDtoValidacioOperacio.EQ);
        camp1.setValidacioDataCmpNombre(1);
        camp1.setValidacioDataCmpTipus(ServeiCampDtoValidacioDataTipus.ANYS);
        mockCampsIGrups(List.of(camp1, camp2), List.of());
        mockCampsRegles(null);
        Map<String, Object> dades = Map.of("Camp1", "01/01/2024", "Camp2", "01/01/2025");

        Errors errors = new BeanPropertyBindingResult(command(dades), "command");
        validator.validate(command(dades), errors);

        assertFalse(errors.hasErrors());
    }

    @Test
    public void dataDiferenciaEnDiesUsaDiferenciaDeDies() throws Exception {
        ServeiCampDto camp2 = camp("Camp2", ServeiCampDtoTipus.DATA, false);
        ServeiCampDto camp1 = camp("Camp1", ServeiCampDtoTipus.DATA, false);
        camp1.setValidacioDataCmpCamp2(camp2);
        camp1.setValidacioDataCmpOperacio(ServeiCampDtoValidacioOperacio.EQ);
        camp1.setValidacioDataCmpNombre(5);
        camp1.setValidacioDataCmpTipus(ServeiCampDtoValidacioDataTipus.DIES);
        mockCampsIGrups(List.of(camp1, camp2), List.of());
        mockCampsRegles(null);
        Map<String, Object> dades = Map.of("Camp1", "01/01/2024", "Camp2", "06/01/2024");

        Errors errors = new BeanPropertyBindingResult(command(dades), "command");
        validator.validate(command(dades), errors);

        assertFalse(errors.hasErrors());
    }

    // ------------------------- regles de camps -------------------------

    @Test
    public void campNoModificatIObligatoriPerReglaGeneraError() throws Exception {
        ServeiCampDto camp = camp("Camp1", ServeiCampDtoTipus.TEXT, false);
        mockCampsIGrups(List.of(camp), List.of());
        CampFormProperties regla = CampFormProperties.builder()
                .obligatori(true)
                .visible(true)
                .editable(true)
                .reglaObligatori("regla obligatorietat")
                .build();
        mockCampsRegles(List.of(regla));

        Errors errors = new BeanPropertyBindingResult(command(new HashMap<>()), "command");
        validator.validate(command(new HashMap<>()), errors);

        FieldError error = errors.getFieldError("dadesEspecifiques[Camp1]");
        assertEquals("consulta.form.camp.regla.obligatori", error.getCode());
        assertEquals("regla obligatorietat", error.getArguments()[0]);
    }

    @Test
    public void campNoModificatINoObligatoriPerReglaNoGeneraError() throws Exception {
        ServeiCampDto camp = camp("Camp1", ServeiCampDtoTipus.TEXT, false);
        mockCampsIGrups(List.of(camp), List.of());
        CampFormProperties regla = CampFormProperties.builder().obligatori(false).build();
        mockCampsRegles(List.of(regla));

        Errors errors = new BeanPropertyBindingResult(command(new HashMap<>()), "command");
        validator.validate(command(new HashMap<>()), errors);

        assertFalse(errors.hasErrors());
    }

    @Test
    public void campModificatINoVisiblePerReglaGeneraErrorGlobal() throws Exception {
        ServeiCampDto camp = camp("Camp1", ServeiCampDtoTipus.TEXT, false);
        camp.setEtiqueta("Etiqueta1");
        mockCampsIGrups(List.of(camp), List.of());
        CampFormProperties regla = CampFormProperties.builder()
                .visible(false)
                .reglaVisible("regla visibilitat")
                .build();
        mockCampsRegles(List.of(regla));
        Map<String, Object> dades = Map.of("Camp1", "valor");

        Errors errors = new BeanPropertyBindingResult(command(dades), "command");
        validator.validate(command(dades), errors);

        ObjectError error = errors.getGlobalError();
        assertEquals("consulta.form.camp.regla.visible", error.getCode());
        assertEquals("Etiqueta1", error.getArguments()[0]);
    }

    @Test
    public void campModificatVisibleINoEditablePerReglaGeneraError() throws Exception {
        ServeiCampDto camp = camp("Camp1", ServeiCampDtoTipus.TEXT, false);
        mockCampsIGrups(List.of(camp), List.of());
        CampFormProperties regla = CampFormProperties.builder()
                .visible(true)
                .editable(false)
                .reglaEditable("regla editable")
                .build();
        mockCampsRegles(List.of(regla));
        Map<String, Object> dades = Map.of("Camp1", "valor");

        Errors errors = new BeanPropertyBindingResult(command(dades), "command");
        validator.validate(command(dades), errors);

        FieldError error = errors.getFieldError("dadesEspecifiques[Camp1]");
        assertEquals("consulta.form.camp.regla.editable", error.getCode());
    }

    @Test
    public void campModificatVisibleIEditableNoGeneraError() throws Exception {
        ServeiCampDto camp = camp("Camp1", ServeiCampDtoTipus.TEXT, false);
        mockCampsIGrups(List.of(camp), List.of());
        CampFormProperties regla = CampFormProperties.builder().visible(true).editable(true).build();
        mockCampsRegles(List.of(regla));
        Map<String, Object> dades = Map.of("Camp1", "valor");

        Errors errors = new BeanPropertyBindingResult(command(dades), "command");
        validator.validate(command(dades), errors);

        assertFalse(errors.hasErrors());
    }

    // ------------------------- regles de grups -------------------------

    @Test
    public void grupNoModificatIObligatoriPerReglaGeneraError() throws Exception {
        ServeiCampGrupDto grup = new ServeiCampGrupDto();
        grup.setId(1L);
        grup.setNom("Grup1");
        ServeiCampDto camp = camp("Camp1", ServeiCampDtoTipus.TEXT, false);
        camp.setGrup(grup);
        mockCampsIGrups(List.of(camp), List.of(grup));
        when(serveiService.getCampsByserveiRegla(eq(SERVEI_CODI), any())).thenReturn(null);
        CampFormProperties reglaGrup = CampFormProperties.builder()
                .varId(1L)
                .obligatori(true)
                .reglaObligatori("regla grup obligatori")
                .build();
        when(serveiService.getGrupsByserveiRegla(eq(SERVEI_CODI), any())).thenReturn(List.of(reglaGrup));

        Errors errors = new BeanPropertyBindingResult(command(new HashMap<>()), "command");
        validator.validate(command(new HashMap<>()), errors);

        FieldError error = errors.getFieldError("dadesEspecifiques[1]");
        assertEquals("consulta.form.grup.regla.obligatori", error.getCode());
    }

    @Test
    public void grupModificatINoEditablePerReglaGeneraError() throws Exception {
        ServeiCampGrupDto grup = new ServeiCampGrupDto();
        grup.setId(1L);
        grup.setNom("Grup1");
        ServeiCampDto camp = camp("Camp1", ServeiCampDtoTipus.TEXT, false);
        camp.setGrup(grup);
        mockCampsIGrups(List.of(camp), List.of(grup));
        when(serveiService.getCampsByserveiRegla(eq(SERVEI_CODI), any())).thenReturn(null);
        CampFormProperties reglaGrup = CampFormProperties.builder()
                .varId(1L)
                .editable(false)
                .reglaEditable("regla grup editable")
                .build();
        when(serveiService.getGrupsByserveiRegla(eq(SERVEI_CODI), any())).thenReturn(List.of(reglaGrup));
        Map<String, Object> dades = Map.of("Camp1", "valor");

        Errors errors = new BeanPropertyBindingResult(command(dades), "command");
        validator.validate(command(dades), errors);

        FieldError error = errors.getFieldError("dadesEspecifiques[1]");
        assertEquals("consulta.form.grup.regla.editable", error.getCode());
    }

    @Test
    public void grupModificatEditableINoVisiblePerReglaGeneraErrorGlobal() throws Exception {
        ServeiCampGrupDto grup = new ServeiCampGrupDto();
        grup.setId(1L);
        grup.setNom("Grup1");
        ServeiCampDto camp = camp("Camp1", ServeiCampDtoTipus.TEXT, false);
        camp.setGrup(grup);
        mockCampsIGrups(List.of(camp), List.of(grup));
        when(serveiService.getCampsByserveiRegla(eq(SERVEI_CODI), any())).thenReturn(null);
        CampFormProperties reglaGrup = CampFormProperties.builder()
                .varId(1L)
                .editable(true)
                .visible(false)
                .reglaVisible("regla grup visible")
                .build();
        when(serveiService.getGrupsByserveiRegla(eq(SERVEI_CODI), any())).thenReturn(List.of(reglaGrup));
        Map<String, Object> dades = Map.of("Camp1", "valor");

        Errors errors = new BeanPropertyBindingResult(command(dades), "command");
        validator.validate(command(dades), errors);

        ObjectError error = errors.getGlobalError();
        assertEquals("consulta.form.grup.regla.visible", error.getCode());
    }

    // ------------------------- excepcions del servei -------------------------

    @Test
    public void errorObtenintCampsLlancaRuntimeException() throws Exception {
        when(serveiService.findServeiCamps(SERVEI_CODI)).thenThrow(new ServeiNotFoundException(SERVEI_CODI));

        Errors errors = new BeanPropertyBindingResult(command(new HashMap<>()), "command");

        assertThrows(RuntimeException.class, () -> validator.validate(command(new HashMap<>()), errors));
    }

    @Test
    public void errorObtenintReglesLlancaRuntimeException() throws Exception {
        ServeiCampDto camp = camp("Camp1", ServeiCampDtoTipus.TEXT, false);
        mockCampsIGrups(List.of(camp), List.of());
        when(serveiService.getCampsByserveiRegla(eq(SERVEI_CODI), any())).thenThrow(new ServeiNotFoundException(SERVEI_CODI));

        Errors errors = new BeanPropertyBindingResult(command(new HashMap<>()), "command");

        assertThrows(RuntimeException.class, () -> validator.validate(command(new HashMap<>()), errors));
    }
}
