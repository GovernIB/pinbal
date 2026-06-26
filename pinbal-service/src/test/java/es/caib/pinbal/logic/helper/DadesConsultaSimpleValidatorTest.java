package es.caib.pinbal.logic.helper;

import es.caib.pinbal.logic.intf.dto.ServeiCampDto;
import es.caib.pinbal.logic.intf.dto.ServeiCampGrupDto;
import es.caib.pinbal.logic.intf.service.ServeiService;
import es.caib.pinbal.logic.intf.service.exception.ServeiNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
