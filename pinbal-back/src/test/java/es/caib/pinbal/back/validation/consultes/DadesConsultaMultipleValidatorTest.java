package es.caib.pinbal.back.validation.consultes;

import es.caib.pinbal.back.command.ConsultaCommand;
import es.caib.pinbal.back.helper.MessageHelper;
import es.caib.pinbal.logic.intf.dto.ArbreDto;
import es.caib.pinbal.logic.intf.dto.ConsultaDto.DocumentTipus;
import es.caib.pinbal.logic.intf.dto.DadaEspecificaDto;
import es.caib.pinbal.logic.intf.dto.NodeDto;
import es.caib.pinbal.logic.intf.dto.ServeiCampDto;
import es.caib.pinbal.logic.intf.dto.ServeiCampDto.ServeiCampDtoTipus;
import es.caib.pinbal.logic.intf.dto.ServeiDto;
import es.caib.pinbal.logic.intf.service.ServeiService;
import es.caib.pinbal.logic.intf.service.exception.ServeiNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests de {@link DadesConsultaMultipleValidator}: parseja un fitxer de consultes múltiples
 * (capçalera de paths + files de dades) construint un {@link ConsultaCommand} per fila i, després,
 * amb {@code validate()}, aplica les mateixes regles de negoci que {@link DadesConsultaSimpleValidator}
 * més les específiques d'aquest flux (document obligatori/permès per servei).
 */
public class DadesConsultaMultipleValidatorTest {

    private static final String SERVEI_CODI = "SERV1";

    private ServeiService serveiService;
    private Errors errorsCommand;

    @BeforeEach
    public void configurar() {
        serveiService = mock(ServeiService.class);
        // Cal un ConsultaCommand real (no un Object nu) perquè rejectValue("multipleFitxer", ...)
        // pugui resoldre la propietat sobre la qual registra l'error.
        errorsCommand = new BeanPropertyBindingResult(new ConsultaCommand(), "command");
        MessageSource messageSource = mock(MessageSource.class);
        when(messageSource.getMessage(any(), any(), any())).thenAnswer(inv -> inv.getArgument(0));
        MessageHelper.INSTANCE.setMessageSource(messageSource);
    }

    private ServeiCampDto campEspecific(String path, ServeiCampDtoTipus tipus, boolean obligatori) {
        ServeiCampDto camp = new ServeiCampDto();
        camp.setPath(path);
        camp.setTipus(tipus);
        camp.setObligatori(obligatori);
        return camp;
    }

    private ServeiDto servei() {
        ServeiDto servei = new ServeiDto();
        servei.setCodi(SERVEI_CODI);
        return servei;
    }

    private void mockDelegatSenseRegles(List<ServeiCampDto> camps) throws ServeiNotFoundException {
        // DadesConsultaMultipleValidator.validate() delega en un DadesConsultaSimpleValidator nou,
        // que torna a demanar camps/grups/regles al servei per a cada línia.
        when(serveiService.findServeiCamps(SERVEI_CODI)).thenReturn(camps);
        when(serveiService.findServeiCampGrupsAndSubgrups(SERVEI_CODI)).thenReturn(List.of());
        when(serveiService.getCampsByserveiRegla(eq(SERVEI_CODI), any())).thenReturn(null);
        when(serveiService.getGrupsByserveiRegla(eq(SERVEI_CODI), any())).thenReturn(null);
    }

    private static final String[] CAPCALERA = {
            "DatosGenericos/Solicitante/IdExpediente",
            "DatosGenericos/Titular/TipoDocumentacion",
            "DatosGenericos/Titular/Documentacion",
            "DatosGenericos/Titular/Nombre",
            "DatosEspecificos/camp1"
    };

    private List<String[]> linies(String[]... files) {
        List<String[]> linies = new ArrayList<>();
        linies.add(CAPCALERA);
        for (String[] fila : files) {
            linies.add(fila);
        }
        return linies;
    }

    // ------------------------- construcció / getCampsPaths -------------------------

    @Test
    public void construccioExtreuDadesGeneriquesIEspecifiquesDeCadaLinia() throws Exception {
        ServeiCampDto camp1 = campEspecific("DatosEspecificos/camp1", ServeiCampDtoTipus.TEXT, false);
        List<String[]> linies = linies(new String[]{"EXP1", "NIF", "12345678Z", "Nom Titular", "valorEspecific1"});

        DadesConsultaMultipleValidator validator = new DadesConsultaMultipleValidator(
                serveiService, linies, List.of(camp1), servei(), errorsCommand, Locale.forLanguageTag("ca"));

        ConsultaCommand command = commandLinia(validator, 0);
        assertEquals("EXP1", command.getExpedientId());
        assertEquals(DocumentTipus.NIF, command.getTitularDocumentTipus());
        assertEquals("12345678Z", command.getTitularDocumentNum());
        assertEquals("Nom Titular", command.getTitularNom());
        assertEquals("valorEspecific1", command.getDadesEspecifiques().get("DatosEspecificos/camp1"));
        assertTrue(validator.getErrorsValidacio().isEmpty());
        assertFalse(errorsCommand.hasErrors());
    }

    @SuppressWarnings("unchecked")
    private ConsultaCommand commandLinia(DadesConsultaMultipleValidator validator, int index) throws Exception {
        var field = DadesConsultaMultipleValidator.class.getDeclaredField("consultaLinies");
        field.setAccessible(true);
        List<ConsultaLinia> linies = (List<ConsultaLinia>) field.get(validator);
        return linies.get(index).getCommandLinia();
    }

    @Test
    public void construccioAmbPathDesconegutDeDadesEspecifiquesRebutjaLaCapcalera() throws Exception {
        // El valor de la columna del path desconegut es deixa buit: si el camp no s'ha trobat el
        // seu CampPathInfo.tipus queda a null, i validar-hi un valor real (isValidValue) llançaria
        // NullPointerException en fer el switch(tipus) — comportament pre-existent, no es prova aquí.
        List<String[]> linies = linies(new String[]{"EXP1", "NIF", "12345678Z", "Nom Titular", null});

        new DadesConsultaMultipleValidator(
                serveiService, linies, List.of(), servei(), errorsCommand, Locale.forLanguageTag("ca"));

        assertTrue(errorsCommand.hasErrors());
        assertEquals("consulta.fitxer.camp.path.invalid", errorsCommand.getGlobalError().getCode());
    }

    @Test
    public void construccioAmbPathDesconegutQueNoEsDadaEspecificaNoGeneraError() throws Exception {
        String[] capcalera = {"DatosGenericos/Solicitante/IdExpediente", "CampNoDefinit"};
        List<String[]> linies = new ArrayList<>();
        linies.add(capcalera);
        linies.add(new String[]{"EXP1", "qualsevol"});

        new DadesConsultaMultipleValidator(
                serveiService, linies, List.of(), servei(), errorsCommand, Locale.forLanguageTag("ca"));

        assertFalse(errorsCommand.hasErrors());
    }

    @Test
    public void construccioAmbCampEnumTrobatOmpleValorsPermesos() throws Exception {
        ServeiCampDto campEnum = campEspecific("DatosEspecificos/camp1", ServeiCampDtoTipus.ENUM, false);
        String[] capcalera = {"DatosEspecificos/camp1"};
        List<String[]> linies = new ArrayList<>();
        linies.add(capcalera);
        linies.add(new String[]{"A"});

        DadaEspecificaDto dadaEspecifica = new DadaEspecificaDto();
        dadaEspecifica.setPath(new String[]{"DatosEspecificos"});
        dadaEspecifica.setNom("camp1");
        dadaEspecifica.setEnumeracioValors(new String[]{"A", "B"});
        NodeDto<DadaEspecificaDto> node = new NodeDto<>(dadaEspecifica);
        ArbreDto<DadaEspecificaDto> arbre = new ArbreDto<>();
        arbre.setArrel(node);
        when(serveiService.generarArbreDadesEspecifiques(SERVEI_CODI)).thenReturn(arbre);

        DadesConsultaMultipleValidator validator = new DadesConsultaMultipleValidator(
                serveiService, linies, List.of(campEnum), servei(), errorsCommand, Locale.forLanguageTag("ca"));

        // Amb valor "A" (dins l'enumeració) no ha de generar error de valor invàlid
        assertTrue(validator.getErrorsValidacio().isEmpty());
    }

    @Test
    public void construccioAmbCampEnumIValorForaDeLEnumeracioGeneraErrorDeLinia() throws Exception {
        ServeiCampDto campEnum = campEspecific("DatosEspecificos/camp1", ServeiCampDtoTipus.ENUM, false);
        String[] capcalera = {"DatosEspecificos/camp1"};
        List<String[]> linies = new ArrayList<>();
        linies.add(capcalera);
        linies.add(new String[]{"Z"});

        DadaEspecificaDto dadaEspecifica = new DadaEspecificaDto();
        dadaEspecifica.setPath(new String[]{"DatosEspecificos"});
        dadaEspecifica.setNom("camp1");
        dadaEspecifica.setEnumeracioValors(new String[]{"A", "B"});
        NodeDto<DadaEspecificaDto> node = new NodeDto<>(dadaEspecifica);
        ArbreDto<DadaEspecificaDto> arbre = new ArbreDto<>();
        arbre.setArrel(node);
        when(serveiService.generarArbreDadesEspecifiques(SERVEI_CODI)).thenReturn(arbre);

        DadesConsultaMultipleValidator validator = new DadesConsultaMultipleValidator(
                serveiService, linies, List.of(campEnum), servei(), errorsCommand, Locale.forLanguageTag("ca"));

        assertFalse(validator.getErrorsValidacio().isEmpty());
        assertTrue(validator.hasErrors());
    }

    @Test
    public void construccioAmbErrorObtenintArbreDadesEspecifiquesNoLlancaExcepcio() throws Exception {
        ServeiCampDto campEnum = campEspecific("DatosEspecificos/camp1", ServeiCampDtoTipus.ENUM, false);
        String[] capcalera = {"DatosEspecificos/camp1"};
        List<String[]> linies = new ArrayList<>();
        linies.add(capcalera);
        linies.add(new String[]{"A"});
        when(serveiService.generarArbreDadesEspecifiques(SERVEI_CODI)).thenThrow(new RuntimeException("error inesperat"));

        DadesConsultaMultipleValidator validator = new DadesConsultaMultipleValidator(
                serveiService, linies, List.of(campEnum), servei(), errorsCommand, Locale.forLanguageTag("ca"));

        // No es pot determinar l'enumeració -> es tracta com a llista de valors permesos buida
        assertFalse(validator.getErrorsValidacio().isEmpty());
    }

    // ------------------------- documentTipusByName -------------------------

    @Test
    public void documentTipusByNameAmbNomEstandardElTroba() throws Exception {
        DadesConsultaMultipleValidator validator = validadorBuit();

        assertEquals(DocumentTipus.NIF, validator.documentTipusByName("NIF"));
        assertEquals(DocumentTipus.NIF, validator.documentTipusByName("nif"));
    }

    @Test
    public void documentTipusByNameAmbAliasPasaporteTrobaPassaport() throws Exception {
        DadesConsultaMultipleValidator validator = validadorBuit();

        assertEquals(DocumentTipus.Passaport, validator.documentTipusByName("Pasaporte"));
    }

    @Test
    public void documentTipusByNameAmbNomDesconegutRetornaNull() throws Exception {
        DadesConsultaMultipleValidator validator = validadorBuit();

        assertNull(validator.documentTipusByName("XYZ"));
    }

    private DadesConsultaMultipleValidator validadorBuit() throws Exception {
        String[] capcalera = {"DatosGenericos/Solicitante/IdExpediente"};
        List<String[]> linies = new ArrayList<>();
        linies.add(capcalera);
        linies.add(new String[]{"EXP1"});
        return new DadesConsultaMultipleValidator(
                serveiService, linies, List.of(), servei(), errorsCommand, Locale.forLanguageTag("ca"));
    }

    // ------------------------- extracció de dades genèriques -------------------------

    @Test
    public void lineaAmbTipusDocumentDesconegutGeneraErrorDeLinia() throws Exception {
        List<String[]> linies = linies(new String[]{"EXP1", "TIPUS_INVALID", "12345678Z", "Nom", "valor"});
        ServeiCampDto camp1 = campEspecific("DatosEspecificos/camp1", ServeiCampDtoTipus.TEXT, false);

        DadesConsultaMultipleValidator validator = new DadesConsultaMultipleValidator(
                serveiService, linies, List.of(camp1), servei(), errorsCommand, Locale.forLanguageTag("ca"));

        assertEquals(1, validator.getErrorsValidacioPerLinia().get(0).size());
        assertEquals("consulta.fitxer.camp.document.tipus.invalid", validator.getErrorsValidacioPerLinia().get(0).get(0));
    }

    // ------------------------- validate() -------------------------

    @Test
    public void validateAmbDocumentNumObligatoriIBuitGeneraError() throws Exception {
        // ConsultaCommand.titularDocumentTipus té NIF com a valor per defecte (no és null encara
        // que la columna no aparegui a la capçalera), així que amb aquest fitxer només es pot
        // incomplir l'obligatorietat del número de document.
        String[] capcalera = {"DatosGenericos/Titular/TipoDocumentacion", "DatosGenericos/Titular/Documentacion"};
        List<String[]> linies = new ArrayList<>();
        linies.add(capcalera);
        linies.add(new String[]{"NIF", null});
        ServeiDto servei = servei();
        servei.setPinbalDocumentObligatori(true);
        mockDelegatSenseRegles(List.of());

        DadesConsultaMultipleValidator validator = new DadesConsultaMultipleValidator(
                serveiService, linies, List.of(), servei, errorsCommand, Locale.forLanguageTag("ca"));
        validator.validate();

        assertTrue(validator.hasErrors());
        assertEquals(1, validator.getErrorValidacioConsulta(0).size());
        assertTrue(errorsCommand.hasFieldErrors("multipleFitxer"));
    }

    @Test
    public void validateAmbDocumentTipusObligatoriINulGeneraError() throws Exception {
        // Un valor de tipus de document no reconegut sobreescriu el NIF per defecte amb null
        // durant la construcció (i ja hi afegeix el seu propi error); com que la capçalera tampoc
        // té columna de número de document (sense valor per defecte, sempre null), validate() hi
        // suma els altres dos errors d'obligatorietat (tipus i número).
        String[] capcalera = {"DatosGenericos/Titular/TipoDocumentacion"};
        List<String[]> linies = new ArrayList<>();
        linies.add(capcalera);
        linies.add(new String[]{"TIPUS_INVALID"});
        ServeiDto servei = servei();
        servei.setPinbalDocumentObligatori(true);
        mockDelegatSenseRegles(List.of());

        DadesConsultaMultipleValidator validator = new DadesConsultaMultipleValidator(
                serveiService, linies, List.of(), servei, errorsCommand, Locale.forLanguageTag("ca"));
        validator.validate();

        assertTrue(validator.hasErrors());
        assertEquals(3, validator.getErrorValidacioConsulta(0).size());
        assertTrue(errorsCommand.hasFieldErrors("multipleFitxer"));
    }

    @Test
    public void validateAmbDocumentTipusNoPermesGeneraError() throws Exception {
        List<String[]> linies = linies(new String[]{"EXP1", "NIF", "12345678Z", "Nom", "valor"});
        ServeiCampDto camp1 = campEspecific("DatosEspecificos/camp1", ServeiCampDtoTipus.TEXT, false);
        ServeiDto servei = servei();
        servei.setPinbalActiuCampDocument(true);
        servei.setPinbalPermesDocumentTipusDni(true);
        // NIF NO està entre els permesos (només DNI)
        mockDelegatSenseRegles(List.of(camp1));

        DadesConsultaMultipleValidator validator = new DadesConsultaMultipleValidator(
                serveiService, linies, List.of(camp1), servei, errorsCommand, Locale.forLanguageTag("ca"));
        validator.validate();

        assertTrue(validator.hasErrors());
        assertTrue(validator.getErrorValidacioConsulta(0).stream()
                .anyMatch(e -> e.contains("consulta.fitxer.camp.document.tipus")));
    }

    @Test
    public void validateAmbDocumentTipusPermesNoGeneraErrorDeTipus() throws Exception {
        List<String[]> linies = linies(new String[]{"EXP1", "NIF", "12345678Z", "Nom", "valor"});
        ServeiCampDto camp1 = campEspecific("DatosEspecificos/camp1", ServeiCampDtoTipus.TEXT, false);
        ServeiDto servei = servei();
        servei.setPinbalActiuCampDocument(true);
        servei.setPinbalPermesDocumentTipusNif(true);
        mockDelegatSenseRegles(List.of(camp1));

        DadesConsultaMultipleValidator validator = new DadesConsultaMultipleValidator(
                serveiService, linies, List.of(camp1), servei, errorsCommand, Locale.forLanguageTag("ca"));
        validator.validate();

        assertFalse(validator.hasErrors());
        assertFalse(errorsCommand.hasFieldErrors("multipleFitxer"));
    }

    @Test
    public void validateDelegaLaValidacioDeCampsObligatorisACadaLinia() throws Exception {
        // camp1 és obligatori per a DadesConsultaSimpleValidator; la línia el deixa buit
        String[] capcalera = {"DatosEspecificos/camp1"};
        List<String[]> linies = new ArrayList<>();
        linies.add(capcalera);
        linies.add(new String[]{null});
        ServeiCampDto camp1 = campEspecific("DatosEspecificos/camp1", ServeiCampDtoTipus.TEXT, true);
        mockDelegatSenseRegles(List.of(camp1));

        DadesConsultaMultipleValidator validator = new DadesConsultaMultipleValidator(
                serveiService, linies, List.of(camp1), servei(), errorsCommand, Locale.forLanguageTag("ca"));
        validator.validate();

        assertTrue(validator.hasErrors());
        assertFalse(validator.getErrorValidacioConsulta(0).isEmpty());
        assertTrue(errorsCommand.hasFieldErrors("multipleFitxer"));
    }

    @Test
    public void validateSenseErrorsNoRebutjaElFitxer() throws Exception {
        List<String[]> linies = linies(new String[]{"EXP1", "NIF", "12345678Z", "Nom", "valor"});
        ServeiCampDto camp1 = campEspecific("DatosEspecificos/camp1", ServeiCampDtoTipus.TEXT, false);
        mockDelegatSenseRegles(List.of(camp1));

        DadesConsultaMultipleValidator validator = new DadesConsultaMultipleValidator(
                serveiService, linies, List.of(camp1), servei(), errorsCommand, Locale.forLanguageTag("ca"));
        validator.validate();

        assertFalse(validator.hasErrors());
        assertFalse(errorsCommand.hasErrors());
        assertTrue(validator.getErrorsValidacio().isEmpty());
        assertEquals(1, validator.getErrorsValidacioPerLinia().size());
    }
}
