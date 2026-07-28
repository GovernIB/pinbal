package es.caib.pinbal.logic.helper;

import es.caib.pinbal.logic.intf.dto.ServeiCampDto;
import es.caib.pinbal.logic.intf.dto.ServeiCampGrupDto;
import es.caib.pinbal.logic.intf.dto.regles.CampFormProperties;
import es.caib.pinbal.logic.intf.service.ServeiService;
import es.caib.pinbal.logic.intf.service.exception.ServeiNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.FieldError;
import org.springframework.validation.MapBindingResult;
import org.springframework.validation.Errors;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DadesConsultaSimpleValidatorTest {

    @Mock
    private ServeiService serveiService;

    private DadesConsultaSimpleValidator validator;
    private static final String SERVEI_CODI = "SV001";

    @BeforeEach
    public void setUp() {
        validator = new DadesConsultaSimpleValidator(serveiService, SERVEI_CODI);
    }

    @Test
    public void supports_HashMapRetornaTrue() {
        assertTrue(validator.supports(HashMap.class));
    }

    @Test
    public void supports_AltreClasseRetornaFalse() {
        assertFalse(validator.supports(String.class));
    }

    @Test
    public void validate_campObligatori_buit_rejecta() throws ServeiNotFoundException {
        ServeiCampDto camp = campObligatori("NIF", "/NIF");
        when(serveiService.findServeiCamps(SERVEI_CODI)).thenReturn(Collections.singletonList(camp));
        when(serveiService.findServeiCampGrupsAndSubgrups(SERVEI_CODI)).thenReturn(Collections.emptyList());
        when(serveiService.getCampsByserveiRegla(eq(SERVEI_CODI), any())).thenReturn(Collections.emptyList());
        when(serveiService.getGrupsByserveiRegla(eq(SERVEI_CODI), any())).thenReturn(Collections.emptyList());

        Map<String, Object> dades = new HashMap<>();
        Errors errors = bindingResult(dades);

        validator.validate(dades, errors);

        assertTrue(errors.hasErrors());
        assertNotNull(errors.getFieldError("dadesEspecifiques[/NIF]"));
    }

    @Test
    public void validate_campObligatori_emplenat_noRejecta() throws ServeiNotFoundException {
        ServeiCampDto camp = campObligatori("NIF", "/NIF");
        when(serveiService.findServeiCamps(SERVEI_CODI)).thenReturn(Collections.singletonList(camp));
        when(serveiService.findServeiCampGrupsAndSubgrups(SERVEI_CODI)).thenReturn(Collections.emptyList());
        when(serveiService.getCampsByserveiRegla(eq(SERVEI_CODI), any())).thenReturn(Collections.emptyList());
        when(serveiService.getGrupsByserveiRegla(eq(SERVEI_CODI), any())).thenReturn(Collections.emptyList());

        Map<String, Object> dades = new HashMap<>();
        dades.put("/NIF", "12345678A");
        Errors errors = bindingResult(dades);

        validator.validate(dades, errors);

        assertFalse(errors.hasErrors());
    }

    @Test
    public void validate_campText_regexpInvalida_rejecta() throws ServeiNotFoundException {
        ServeiCampDto camp = campText("NIF", "/NIF");
        camp.setValidacioRegexp("[0-9]{8}[A-Z]");
        when(serveiService.findServeiCamps(SERVEI_CODI)).thenReturn(Collections.singletonList(camp));
        when(serveiService.findServeiCampGrupsAndSubgrups(SERVEI_CODI)).thenReturn(Collections.emptyList());
        when(serveiService.getCampsByserveiRegla(eq(SERVEI_CODI), any())).thenReturn(Collections.emptyList());
        when(serveiService.getGrupsByserveiRegla(eq(SERVEI_CODI), any())).thenReturn(Collections.emptyList());

        Map<String, Object> dades = new HashMap<>();
        dades.put("/NIF", "INVALID");
        Errors errors = bindingResult(dades);

        validator.validate(dades, errors);

        assertTrue(errors.hasErrors());
    }

    @Test
    public void validate_campText_regexpValida_noRejecta() throws ServeiNotFoundException {
        ServeiCampDto camp = campText("NIF", "/NIF");
        camp.setValidacioRegexp("[0-9]{8}[A-Z]");
        when(serveiService.findServeiCamps(SERVEI_CODI)).thenReturn(Collections.singletonList(camp));
        when(serveiService.findServeiCampGrupsAndSubgrups(SERVEI_CODI)).thenReturn(Collections.emptyList());
        when(serveiService.getCampsByserveiRegla(eq(SERVEI_CODI), any())).thenReturn(Collections.emptyList());
        when(serveiService.getGrupsByserveiRegla(eq(SERVEI_CODI), any())).thenReturn(Collections.emptyList());

        Map<String, Object> dades = new HashMap<>();
        dades.put("/NIF", "12345678A");
        Errors errors = bindingResult(dades);

        validator.validate(dades, errors);

        assertFalse(errors.hasErrors());
    }

    @Test
    public void validate_campNumeric_foraDeRang_rejecta() throws ServeiNotFoundException {
        ServeiCampDto camp = campNumeric("EDAT", "/EDAT", 0, 120);
        when(serveiService.findServeiCamps(SERVEI_CODI)).thenReturn(Collections.singletonList(camp));
        when(serveiService.findServeiCampGrupsAndSubgrups(SERVEI_CODI)).thenReturn(Collections.emptyList());
        when(serveiService.getCampsByserveiRegla(eq(SERVEI_CODI), any())).thenReturn(Collections.emptyList());
        when(serveiService.getGrupsByserveiRegla(eq(SERVEI_CODI), any())).thenReturn(Collections.emptyList());

        Map<String, Object> dades = new HashMap<>();
        dades.put("/EDAT", "200");
        Errors errors = bindingResult(dades);

        validator.validate(dades, errors);

        assertTrue(errors.hasErrors());
    }

    @Test
    public void validate_campNumeric_dinsDeLRang_noRejecta() throws ServeiNotFoundException {
        ServeiCampDto camp = campNumeric("EDAT", "/EDAT", 0, 120);
        when(serveiService.findServeiCamps(SERVEI_CODI)).thenReturn(Collections.singletonList(camp));
        when(serveiService.findServeiCampGrupsAndSubgrups(SERVEI_CODI)).thenReturn(Collections.emptyList());
        when(serveiService.getCampsByserveiRegla(eq(SERVEI_CODI), any())).thenReturn(Collections.emptyList());
        when(serveiService.getGrupsByserveiRegla(eq(SERVEI_CODI), any())).thenReturn(Collections.emptyList());

        Map<String, Object> dades = new HashMap<>();
        dades.put("/EDAT", "30");
        Errors errors = bindingResult(dades);

        validator.validate(dades, errors);

        assertFalse(errors.hasErrors());
    }

    @Test
    public void validate_serveiNotFound_rejecta() throws ServeiNotFoundException {
        when(serveiService.findServeiCamps(SERVEI_CODI)).thenThrow(new ServeiNotFoundException(SERVEI_CODI));

        Map<String, Object> dades = new HashMap<>();
        Errors errors = bindingResult(dades);

        validator.validate(dades, errors);

        assertTrue(errors.hasErrors());
    }

    @Test
    public void validate_campNoDefinit_rejecta() throws ServeiNotFoundException {
        ServeiCampDto camp = campText("NIF", "/NIF");
        when(serveiService.findServeiCamps(SERVEI_CODI)).thenReturn(Collections.singletonList(camp));
        when(serveiService.findServeiCampGrupsAndSubgrups(SERVEI_CODI)).thenReturn(Collections.emptyList());
        when(serveiService.getCampsByserveiRegla(eq(SERVEI_CODI), any())).thenReturn(Collections.emptyList());
        when(serveiService.getGrupsByserveiRegla(eq(SERVEI_CODI), any())).thenReturn(Collections.emptyList());

        Map<String, Object> dades = new HashMap<>();
        dades.put("/NIF", "12345678A");
        dades.put("/CAMP_INEXISTENT", "valor");
        Errors errors = bindingResult(dades);

        validator.validate(dades, errors);

        assertTrue(errors.hasErrors());
    }

    @Test
    public void parseDate_formatCatalunya_retornaData() {
        assertNotNull(DadesConsultaSimpleValidator.parseDate("25/06/2024", "dd/MM/yyyy"));
    }

    @Test
    public void parseDate_formatISO_retornaData() {
        assertNotNull(DadesConsultaSimpleValidator.parseDate("2024-06-25", "yyyy-MM-dd"));
    }

    @Test
    public void parseDate_formatInvalid_retornaNull() {
        assertNull(DadesConsultaSimpleValidator.parseDate("not-a-date", "dd/MM/yyyy"));
    }

    @Test
    public void parseDate_multipletsFormats_trobaElCorrecte() {
        assertNotNull(DadesConsultaSimpleValidator.parseDate("25-06-2024",
                "dd/MM/yyyy", "dd-MM-yyyy", "yyyy-MM-dd"));
    }

    // ---- validacions de camps de tipus DATA ----

    @Test
    public void validate_campData_formatInvalid_rejecta() throws ServeiNotFoundException {
        ServeiCampDto camp = campData("DATA", "/DATA");
        mockCampsIRegles(Collections.singletonList(camp), Collections.singletonList(campReglaNeutre()));

        Map<String, Object> dades = new HashMap<>();
        dades.put("/DATA", "no-es-una-data");
        Errors errors = bindingResult(dades);

        validator.validate(dades, errors);

        FieldError fieldError = errors.getFieldError("dadesEspecifiques[/DATA]");
        assertNotNull(fieldError);
        assertEquals("DataValida", fieldError.getCode());
        assertEquals("La data és incorrecta", fieldError.getDefaultMessage());
    }

    @Test
    public void validate_campData_validSenseComparacio_noRejecta() throws ServeiNotFoundException {
        ServeiCampDto camp = campData("DATA", "/DATA");
        mockCampsIRegles(Collections.singletonList(camp), Collections.singletonList(campReglaNeutre()));

        Map<String, Object> dades = new HashMap<>();
        dades.put("/DATA", "25/06/2024");
        Errors errors = bindingResult(dades);

        validator.validate(dades, errors);

        assertFalse(errors.hasErrors());
    }

    @Test
    public void validate_campData_referenciaInvalida_rejecta() throws ServeiNotFoundException {
        ServeiCampDto camp2 = campData("DATA2", "/DATA2");
        ServeiCampDto camp1 = campData("DATA1", "/DATA1");
        camp1.setValidacioDataCmpCamp2(camp2);
        mockCampsIRegles(Collections.singletonList(camp1), Collections.singletonList(campReglaNeutre()));

        Map<String, Object> dades = new HashMap<>();
        dades.put("/DATA1", "25/06/2024");
        Errors errors = bindingResult(dades);

        validator.validate(dades, errors);

        FieldError fieldError = errors.getFieldError("dadesEspecifiques[/DATA1]");
        assertNotNull(fieldError);
        assertEquals("DataReferencia", fieldError.getCode());
    }

    @Test
    public void validate_campData_comparacioDirectaLt_valida_noRejecta() throws ServeiNotFoundException {
        ServeiCampDto camp1 = campDataAmbComparacio("/DATA1", "/DATA2",
                ServeiCampDto.ServeiCampDtoValidacioOperacio.LT, null, null);
        ServeiCampDto camp2 = campData("DATA2", "/DATA2");
        mockCampsIRegles(Arrays.asList(camp1, camp2), Arrays.asList(campReglaNeutre(), campReglaNeutre()));

        Map<String, Object> dades = new HashMap<>();
        dades.put("/DATA1", "01/01/2024");
        dades.put("/DATA2", "02/01/2024");
        Errors errors = bindingResult(dades);

        validator.validate(dades, errors);

        assertFalse(errors.hasErrors());
    }

    @Test
    public void validate_campData_comparacioDirectaLt_invalida_rejecta() throws ServeiNotFoundException {
        ServeiCampDto camp1 = campDataAmbComparacio("/DATA1", "/DATA2",
                ServeiCampDto.ServeiCampDtoValidacioOperacio.LT, null, null);
        ServeiCampDto camp2 = campData("DATA2", "/DATA2");
        mockCampsIRegles(Arrays.asList(camp1, camp2), Arrays.asList(campReglaNeutre(), campReglaNeutre()));

        Map<String, Object> dades = new HashMap<>();
        dades.put("/DATA1", "03/01/2024");
        dades.put("/DATA2", "02/01/2024");
        Errors errors = bindingResult(dades);

        validator.validate(dades, errors);

        FieldError fieldError = errors.getFieldError("dadesEspecifiques[/DATA1]");
        assertNotNull(fieldError);
        assertEquals("DataValida", fieldError.getCode());
        assertEquals("La comparació entre dates no passa la validació", fieldError.getDefaultMessage());
    }

    @Test
    public void validate_campData_diferenciaAnys_exacta_noRejecta() throws ServeiNotFoundException {
        ServeiCampDto camp1 = campDataAmbComparacio("/DATA1", "/DATA2",
                ServeiCampDto.ServeiCampDtoValidacioOperacio.EQ, 2, ServeiCampDto.ServeiCampDtoValidacioDataTipus.ANYS);
        ServeiCampDto camp2 = campData("DATA2", "/DATA2");
        mockCampsIRegles(Arrays.asList(camp1, camp2), Arrays.asList(campReglaNeutre(), campReglaNeutre()));

        Map<String, Object> dades = new HashMap<>();
        dades.put("/DATA1", "01/01/2020");
        dades.put("/DATA2", "01/01/2022");
        Errors errors = bindingResult(dades);

        validator.validate(dades, errors);

        assertFalse(errors.hasErrors());
    }

    @Test
    public void validate_campData_diferenciaAnys_ambResidu_rejecta() throws ServeiNotFoundException {
        ServeiCampDto camp1 = campDataAmbComparacio("/DATA1", "/DATA2",
                ServeiCampDto.ServeiCampDtoValidacioOperacio.EQ, 2, ServeiCampDto.ServeiCampDtoValidacioDataTipus.ANYS);
        ServeiCampDto camp2 = campData("DATA2", "/DATA2");
        mockCampsIRegles(Arrays.asList(camp1, camp2), Arrays.asList(campReglaNeutre(), campReglaNeutre()));

        Map<String, Object> dades = new HashMap<>();
        dades.put("/DATA1", "01/01/2020");
        dades.put("/DATA2", "15/02/2022");
        Errors errors = bindingResult(dades);

        validator.validate(dades, errors);

        FieldError fieldError = errors.getFieldError("dadesEspecifiques[/DATA1]");
        assertNotNull(fieldError);
        assertEquals("DataValida", fieldError.getCode());
        assertEquals("La data és surt del rang permès", fieldError.getDefaultMessage());
    }

    @Test
    public void validate_campData_diferenciaMesos_dinsDelRang_noRejecta() throws ServeiNotFoundException {
        ServeiCampDto camp1 = campDataAmbComparacio("/DATA1", "/DATA2",
                ServeiCampDto.ServeiCampDtoValidacioOperacio.GTE, 3, ServeiCampDto.ServeiCampDtoValidacioDataTipus.MESOS);
        ServeiCampDto camp2 = campData("DATA2", "/DATA2");
        mockCampsIRegles(Arrays.asList(camp1, camp2), Arrays.asList(campReglaNeutre(), campReglaNeutre()));

        Map<String, Object> dades = new HashMap<>();
        dades.put("/DATA1", "01/01/2024");
        dades.put("/DATA2", "01/04/2024");
        Errors errors = bindingResult(dades);

        validator.validate(dades, errors);

        assertFalse(errors.hasErrors());
    }

    @Test
    public void validate_campData_diferenciaMesos_foraDelRang_rejecta() throws ServeiNotFoundException {
        ServeiCampDto camp1 = campDataAmbComparacio("/DATA1", "/DATA2",
                ServeiCampDto.ServeiCampDtoValidacioOperacio.GTE, 4, ServeiCampDto.ServeiCampDtoValidacioDataTipus.MESOS);
        ServeiCampDto camp2 = campData("DATA2", "/DATA2");
        mockCampsIRegles(Arrays.asList(camp1, camp2), Arrays.asList(campReglaNeutre(), campReglaNeutre()));

        Map<String, Object> dades = new HashMap<>();
        dades.put("/DATA1", "01/01/2024");
        dades.put("/DATA2", "01/04/2024");
        Errors errors = bindingResult(dades);

        validator.validate(dades, errors);

        FieldError fieldError = errors.getFieldError("dadesEspecifiques[/DATA1]");
        assertNotNull(fieldError);
        assertEquals("DataValida", fieldError.getCode());
    }

    @Test
    public void validate_campData_diferenciaDies_dinsDelRang_noRejecta() throws ServeiNotFoundException {
        ServeiCampDto camp1 = campDataAmbComparacio("/DATA1", "/DATA2",
                ServeiCampDto.ServeiCampDtoValidacioOperacio.GT, 5, null);
        ServeiCampDto camp2 = campData("DATA2", "/DATA2");
        mockCampsIRegles(Arrays.asList(camp1, camp2), Arrays.asList(campReglaNeutre(), campReglaNeutre()));

        Map<String, Object> dades = new HashMap<>();
        dades.put("/DATA1", "01/01/2024");
        dades.put("/DATA2", "10/01/2024");
        Errors errors = bindingResult(dades);

        validator.validate(dades, errors);

        assertFalse(errors.hasErrors());
    }

    @Test
    public void validate_campData_diferenciaDies_foraDelRang_rejecta() throws ServeiNotFoundException {
        ServeiCampDto camp1 = campDataAmbComparacio("/DATA1", "/DATA2",
                ServeiCampDto.ServeiCampDtoValidacioOperacio.GT, 15, null);
        ServeiCampDto camp2 = campData("DATA2", "/DATA2");
        mockCampsIRegles(Arrays.asList(camp1, camp2), Arrays.asList(campReglaNeutre(), campReglaNeutre()));

        Map<String, Object> dades = new HashMap<>();
        dades.put("/DATA1", "01/01/2024");
        dades.put("/DATA2", "10/01/2024");
        Errors errors = bindingResult(dades);

        validator.validate(dades, errors);

        FieldError fieldError = errors.getFieldError("dadesEspecifiques[/DATA1]");
        assertNotNull(fieldError);
        assertEquals("DataValida", fieldError.getCode());
    }

    // ---- validacions de regles de camps i grups ----

    @Test
    public void validate_campRegla_obligatoriNoModificat_rejecta() throws ServeiNotFoundException {
        ServeiCampDto camp = campText("EDAT", "/EDAT");
        mockCampsIRegles(Collections.singletonList(camp), Collections.singletonList(
                CampFormProperties.builder().obligatori(true).visible(true).editable(true)
                        .reglaObligatori("cal EDAT").build()));

        Map<String, Object> dades = new HashMap<>();
        Errors errors = bindingResult(dades);

        validator.validate(dades, errors);

        FieldError fieldError = errors.getFieldError("dadesEspecifiques[/EDAT]");
        assertNotNull(fieldError);
        assertEquals("consulta.form.camp.regla.obligatori", fieldError.getCode());
    }

    @Test
    public void validate_campRegla_noVisibleModificat_rejecta() throws ServeiNotFoundException {
        ServeiCampDto camp = campText("NOM", "/NOM");
        mockCampsIRegles(Collections.singletonList(camp), Collections.singletonList(
                CampFormProperties.builder().obligatori(false).visible(false).editable(true)
                        .reglaVisible("nomes si X").build()));

        Map<String, Object> dades = new HashMap<>();
        dades.put("/NOM", "Joan");
        Errors errors = bindingResult(dades);

        validator.validate(dades, errors);

        FieldError fieldError = errors.getFieldError("dadesEspecifiques[/NOM]");
        assertNotNull(fieldError);
        assertEquals("consulta.form.camp.regla.visible", fieldError.getCode());
    }

    @Test
    public void validate_campRegla_noEditableModificat_rejecta() throws ServeiNotFoundException {
        ServeiCampDto camp = campText("NOM", "/NOM");
        mockCampsIRegles(Collections.singletonList(camp), Collections.singletonList(
                CampFormProperties.builder().obligatori(false).visible(true).editable(false)
                        .reglaEditable("nomes lectura").build()));

        Map<String, Object> dades = new HashMap<>();
        dades.put("/NOM", "Joan");
        Errors errors = bindingResult(dades);

        validator.validate(dades, errors);

        FieldError fieldError = errors.getFieldError("dadesEspecifiques[/NOM]");
        assertNotNull(fieldError);
        assertEquals("consulta.form.camp.regla.editable", fieldError.getCode());
    }

    @Test
    public void validate_grupRegla_obligatoriNoModificat_rejecta() throws ServeiNotFoundException {
        ServeiCampGrupDto grup = ServeiCampGrupDto.builder().id(1L).nom("GRUP1").build();
        when(serveiService.findServeiCamps(SERVEI_CODI)).thenReturn(Collections.emptyList());
        when(serveiService.findServeiCampGrupsAndSubgrups(SERVEI_CODI)).thenReturn(Collections.singletonList(grup));
        when(serveiService.getCampsByserveiRegla(eq(SERVEI_CODI), any())).thenReturn(Collections.emptyList());
        when(serveiService.getGrupsByserveiRegla(eq(SERVEI_CODI), any())).thenReturn(Collections.singletonList(
                CampFormProperties.builder().varId(1L).obligatori(true).visible(true).editable(true)
                        .reglaObligatori("cal el grup").build()));

        Map<String, Object> dades = new HashMap<>();
        Errors errors = bindingResult(dades);

        validator.validate(dades, errors);

        FieldError fieldError = errors.getFieldError("dadesEspecifiques[1]");
        assertNotNull(fieldError);
        assertEquals("consulta.form.grup.regla.obligatori", fieldError.getCode());
    }

    @Test
    public void validate_grupRegla_noEditableModificat_rejecta() throws ServeiNotFoundException {
        ServeiCampGrupDto grup = ServeiCampGrupDto.builder().id(1L).nom("GRUP1").build();
        ServeiCampDto camp = campText("CAMP", "/CAMP");
        camp.setGrup(grup);

        when(serveiService.findServeiCamps(SERVEI_CODI)).thenReturn(Collections.singletonList(camp));
        when(serveiService.findServeiCampGrupsAndSubgrups(SERVEI_CODI)).thenReturn(Collections.singletonList(grup));
        when(serveiService.getCampsByserveiRegla(eq(SERVEI_CODI), any())).thenReturn(Collections.singletonList(campReglaNeutre()));
        when(serveiService.getGrupsByserveiRegla(eq(SERVEI_CODI), any())).thenReturn(Collections.singletonList(
                CampFormProperties.builder().varId(1L).editable(false).visible(true)
                        .reglaEditable("nomes lectura").build()));

        Map<String, Object> dades = new HashMap<>();
        dades.put("/CAMP", "valor");
        Errors errors = bindingResult(dades);

        validator.validate(dades, errors);

        FieldError fieldError = errors.getFieldError("dadesEspecifiques[1]");
        assertNotNull(fieldError);
        assertEquals("consulta.form.grup.regla.editable", fieldError.getCode());
    }

    @Test
    public void validate_grupRegla_noVisibleModificat_rejectaGlobal() throws ServeiNotFoundException {
        ServeiCampGrupDto grup = ServeiCampGrupDto.builder().id(1L).nom("GRUP1").build();
        ServeiCampDto camp = campText("CAMP", "/CAMP");
        camp.setGrup(grup);

        when(serveiService.findServeiCamps(SERVEI_CODI)).thenReturn(Collections.singletonList(camp));
        when(serveiService.findServeiCampGrupsAndSubgrups(SERVEI_CODI)).thenReturn(Collections.singletonList(grup));
        when(serveiService.getCampsByserveiRegla(eq(SERVEI_CODI), any())).thenReturn(Collections.singletonList(campReglaNeutre()));
        when(serveiService.getGrupsByserveiRegla(eq(SERVEI_CODI), any())).thenReturn(Collections.singletonList(
                CampFormProperties.builder().varId(1L).editable(true).visible(false)
                        .reglaVisible("nomes si Y").build()));

        Map<String, Object> dades = new HashMap<>();
        dades.put("/CAMP", "valor");
        Errors errors = bindingResult(dades);

        validator.validate(dades, errors);

        assertFalse(errors.getGlobalErrors().isEmpty());
        assertEquals("consulta.form.grup.regla.visible", errors.getGlobalError().getCode());
    }

    // ---- helpers de construcció ----

    private void mockCampsIRegles(java.util.List<ServeiCampDto> camps, java.util.List<CampFormProperties> campsRegles) throws ServeiNotFoundException {
        when(serveiService.findServeiCamps(SERVEI_CODI)).thenReturn(camps);
        when(serveiService.findServeiCampGrupsAndSubgrups(SERVEI_CODI)).thenReturn(Collections.emptyList());
        when(serveiService.getCampsByserveiRegla(eq(SERVEI_CODI), any())).thenReturn(campsRegles);
        when(serveiService.getGrupsByserveiRegla(eq(SERVEI_CODI), any())).thenReturn(Collections.emptyList());
    }

    private CampFormProperties campReglaNeutre() {
        return CampFormProperties.builder().obligatori(false).visible(true).editable(true).build();
    }

    private ServeiCampDto campData(String nom, String path) {
        ServeiCampDto c = new ServeiCampDto();
        c.setEtiqueta(nom);
        c.setPath(path);
        c.setObligatori(false);
        c.setTipus(ServeiCampDto.ServeiCampDtoTipus.DATA);
        return c;
    }

    private ServeiCampDto campDataAmbComparacio(
            String path,
            String path2,
            ServeiCampDto.ServeiCampDtoValidacioOperacio operacio,
            Integer nombre,
            ServeiCampDto.ServeiCampDtoValidacioDataTipus tipus) {
        ServeiCampDto camp2 = campData("DATA2", path2);
        ServeiCampDto camp = campData("DATA1", path);
        camp.setValidacioDataCmpCamp2(camp2);
        camp.setValidacioDataCmpOperacio(operacio);
        camp.setValidacioDataCmpNombre(nombre);
        camp.setValidacioDataCmpTipus(tipus);
        return camp;
    }

    // ---- helpers de construcció ----

    private ServeiCampDto campObligatori(String nom, String path) {
        ServeiCampDto c = new ServeiCampDto();
        c.setEtiqueta(nom);
        c.setPath(path);
        c.setObligatori(true);
        c.setTipus(ServeiCampDto.ServeiCampDtoTipus.TEXT);
        return c;
    }

    private ServeiCampDto campText(String nom, String path) {
        ServeiCampDto c = new ServeiCampDto();
        c.setEtiqueta(nom);
        c.setPath(path);
        c.setObligatori(false);
        c.setTipus(ServeiCampDto.ServeiCampDtoTipus.TEXT);
        return c;
    }

    private ServeiCampDto campNumeric(String nom, String path, Integer min, Integer max) {
        ServeiCampDto c = new ServeiCampDto();
        c.setEtiqueta(nom);
        c.setPath(path);
        c.setObligatori(false);
        c.setTipus(ServeiCampDto.ServeiCampDtoTipus.NUMERIC);
        c.setValidacioMin(min);
        c.setValidacioMax(max);
        return c;
    }

    private Errors bindingResult(Map<String, Object> target) {
        return new MapBindingResult(target, "dades");
    }
}
