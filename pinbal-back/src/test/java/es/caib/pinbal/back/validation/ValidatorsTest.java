package es.caib.pinbal.back.validation;

import es.caib.pinbal.back.command.ClauPrivadaCommand;
import es.caib.pinbal.back.command.ClauPublicaCommand;
import es.caib.pinbal.back.command.EmissorCertCommand;
import es.caib.pinbal.back.command.EntitatCommand;
import es.caib.pinbal.back.command.ParamConfCommand;
import es.caib.pinbal.back.command.ProcedimentCommand;
import es.caib.pinbal.back.command.ServeiCampGrupCommand;
import es.caib.pinbal.back.command.ServeiCommand;
import es.caib.pinbal.back.command.ServeiReglaCommand;
import es.caib.pinbal.back.command.UsuariCodiCommand;
import es.caib.pinbal.back.helper.MessageHelper;
import es.caib.pinbal.logic.intf.dto.ClauPrivadaDto;
import es.caib.pinbal.logic.intf.dto.ClauPublicaDto;
import es.caib.pinbal.logic.intf.dto.EmissorCertDto;
import es.caib.pinbal.logic.intf.dto.EntitatDto;
import es.caib.pinbal.logic.intf.dto.ParamConfDto;
import es.caib.pinbal.logic.intf.dto.ProcedimentDto;
import es.caib.pinbal.logic.intf.dto.ServeiCampGrupDto;
import es.caib.pinbal.logic.intf.dto.UsuariDto;
import es.caib.pinbal.logic.intf.dto.regles.ServeiReglaDto;
import es.caib.pinbal.logic.intf.service.EntitatService;
import es.caib.pinbal.logic.intf.service.ProcedimentService;
import es.caib.pinbal.logic.intf.service.ScspService;
import es.caib.pinbal.logic.intf.service.ServeiService;
import es.caib.pinbal.logic.intf.service.UsuariService;
import es.caib.pinbal.logic.intf.service.exception.ServeiNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests dels {@code ConstraintValidator} del paquet {@code validation}. Els camps
 * {@code @Autowired} s'injecten per reflexió (no tenen setter), i {@link ConstraintValidatorContext}
 * es mockeja amb {@code RETURNS_DEEP_STUBS} per no haver d'encadenar manualment
 * {@code buildConstraintViolationWithTemplate(...).addNode(...).addConstraintViolation()}.
 * {@link MessageHelper#INSTANCE} és un singleton estàtic: se li configura un {@link MessageSource}
 * mock abans de cada test perquè els validadors que en depenen no llancin NPE.
 */
public class ValidatorsTest {

    private final ConstraintValidatorContext context = mock(ConstraintValidatorContext.class, RETURNS_DEEP_STUBS);

    @BeforeEach
    public void configurarMessageHelper() {
        MessageSource messageSource = mock(MessageSource.class);
        when(messageSource.getMessage(any(), any(), any())).thenReturn("missatge de prova");
        MessageHelper.INSTANCE.setMessageSource(messageSource);
    }

    private void setField(Object target, String name, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    // ------------------------- ArxiuNoBuitValidator -------------------------

    @Test
    public void arxiuNoBuitAmbArxiuBuitEsInvalid() {
        ArxiuNoBuitValidator validator = new ArxiuNoBuitValidator();
        MultipartFile buit = new MockMultipartFile("arxiu", new byte[0]);

        assertFalse(validator.isValid(buit, context));
    }

    @Test
    public void arxiuNoBuitAmbArxiuAmbContingutEsValid() {
        ArxiuNoBuitValidator validator = new ArxiuNoBuitValidator();
        MultipartFile ambContingut = new MockMultipartFile("arxiu", "contingut".getBytes());

        assertTrue(validator.isValid(ambContingut, context));
    }

    @Test
    public void arxiuNoBuitAmbNullEsInvalid() {
        assertFalse(new ArxiuNoBuitValidator().isValid(null, context));
    }

    // ------------------------- DataValidaValidator -------------------------

    @Test
    public void dataValidaAmbDataCorrecta() {
        DataValidaValidator validator = new DataValidaValidator();
        validator.initialize(annotacioDataValida("dd/MM/yyyy"));

        assertTrue(validator.isValid("31/01/2024", context));
    }

    @Test
    public void dataValidaAmbDataInexistentEsInvalida() {
        DataValidaValidator validator = new DataValidaValidator();
        validator.initialize(annotacioDataValida("dd/MM/yyyy"));

        assertFalse(validator.isValid("31/02/2024", context));
    }

    @Test
    public void dataValidaAmbValorBuitEsValid() {
        DataValidaValidator validator = new DataValidaValidator();
        validator.initialize(annotacioDataValida("dd/MM/yyyy"));

        assertTrue(validator.isValid("", context));
    }

    private DataValida annotacioDataValida(String format) {
        DataValida annotacio = mock(DataValida.class);
        when(annotacio.format()).thenReturn(format);
        return annotacio;
    }

    // ------------------------- DocumentIdentitatValidator -------------------------

    @Test
    public void documentIdentitatNifValid() {
        DocumentIdentitatValidator validator = new DocumentIdentitatValidator();
        validator.initialize(annotacioDocument(es.caib.pinbal.logic.intf.dto.ConsultaDto.DocumentTipus.NIF));

        assertTrue(validator.isValid("00000000T", context));
    }

    @Test
    public void documentIdentitatNifInvalid() {
        DocumentIdentitatValidator validator = new DocumentIdentitatValidator();
        validator.initialize(annotacioDocument(es.caib.pinbal.logic.intf.dto.ConsultaDto.DocumentTipus.NIF));

        assertFalse(validator.isValid("00000000A", context));
    }

    @Test
    public void documentIdentitatCifDelegaEnValidacioNif() {
        DocumentIdentitatValidator validator = new DocumentIdentitatValidator();
        validator.initialize(annotacioDocument(es.caib.pinbal.logic.intf.dto.ConsultaDto.DocumentTipus.CIF));

        assertTrue(validator.isValid("00000000T", context));
    }

    @Test
    public void documentIdentitatDniValid() {
        DocumentIdentitatValidator validator = new DocumentIdentitatValidator();
        validator.initialize(annotacioDocument(es.caib.pinbal.logic.intf.dto.ConsultaDto.DocumentTipus.DNI));

        assertTrue(validator.isValid("00000000T", context));
    }

    @Test
    public void documentIdentitatNieValid() {
        DocumentIdentitatValidator validator = new DocumentIdentitatValidator();
        validator.initialize(annotacioDocument(es.caib.pinbal.logic.intf.dto.ConsultaDto.DocumentTipus.NIE));

        assertTrue(validator.isValid("X0000000T", context));
    }

    @Test
    public void documentIdentitatPassaportSempreValid() {
        DocumentIdentitatValidator validator = new DocumentIdentitatValidator();
        validator.initialize(annotacioDocument(es.caib.pinbal.logic.intf.dto.ConsultaDto.DocumentTipus.Passaport));

        assertTrue(validator.isValid("qualsevol", context));
    }

    @Test
    public void documentIdentitatAmbValorBuitEsValid() {
        DocumentIdentitatValidator validator = new DocumentIdentitatValidator();
        validator.initialize(annotacioDocument(es.caib.pinbal.logic.intf.dto.ConsultaDto.DocumentTipus.NIF));

        assertTrue(validator.isValid("", context));
    }

    private DocumentIdentitat annotacioDocument(es.caib.pinbal.logic.intf.dto.ConsultaDto.DocumentTipus tipus) {
        DocumentIdentitat annotacio = mock(DocumentIdentitat.class);
        when(annotacio.documentTipus()).thenReturn(tipus);
        return annotacio;
    }

    // ------------------------- DocumentIdentitatNif/Dni/Nie/Cif/PassValidator (delegen a helper) -------------------------

    @Test
    public void documentIdentitatNifValidatorAmbBuitEsValid() {
        assertTrue(new DocumentIdentitatNifValidator().isValid("", context));
        assertTrue(new DocumentIdentitatNifValidator().isValid("00000000T", context));
    }

    @Test
    public void documentIdentitatDniValidatorAmbBuitEsValid() {
        assertTrue(new DocumentIdentitatDniValidator().isValid(null, context));
        assertTrue(new DocumentIdentitatDniValidator().isValid("00000000T", context));
    }

    @Test
    public void documentIdentitatNieValidatorAmbValorInvalid() {
        assertFalse(new DocumentIdentitatNieValidator().isValid("Z0000000A", context));
    }

    @Test
    public void documentIdentitatCifValidatorAmbBuitEsValid() {
        assertTrue(new DocumentIdentitatCifValidator().isValid("", context));
    }

    @Test
    public void documentIdentitatPassValidatorAmbBuitEsValid() {
        assertTrue(new DocumentIdentitatPassValidator().isValid("", context));
    }

    // ------------------------- CifEmisorNoRepetitValidator -------------------------

    @Test
    public void cifEmisorNoRepetitSenseCifEsValid() throws Exception {
        CifEmisorNoRepetitValidator validator = new CifEmisorNoRepetitValidator();
        EmissorCertCommand command = new EmissorCertCommand();

        assertTrue(validator.isValid(command, context));
    }

    @Test
    public void cifEmisorNoRepetitSenseCoincidenciaEsValid() throws Exception {
        CifEmisorNoRepetitValidator validator = new CifEmisorNoRepetitValidator();
        ScspService scspService = mock(ScspService.class);
        setField(validator, "scspService", scspService);
        EmissorCertCommand command = new EmissorCertCommand();
        command.setCif("B00000000");
        when(scspService.findEmissorCertByCif("B00000000")).thenReturn(null);

        assertTrue(validator.isValid(command, context));
    }

    @Test
    public void cifEmisorNoRepetitAmbCoincidenciaDAltreRegistreEsInvalid() throws Exception {
        CifEmisorNoRepetitValidator validator = new CifEmisorNoRepetitValidator();
        ScspService scspService = mock(ScspService.class);
        setField(validator, "scspService", scspService);
        EmissorCertCommand command = new EmissorCertCommand();
        command.setId(1L);
        command.setCif("B00000000");
        EmissorCertDto trobat = new EmissorCertDto();
        trobat.setId(2L);
        when(scspService.findEmissorCertByCif("B00000000")).thenReturn(trobat);

        assertFalse(validator.isValid(command, context));
    }

    @Test
    public void cifEmisorNoRepetitAmbCoincidenciaDelMateixRegistreEsValid() throws Exception {
        CifEmisorNoRepetitValidator validator = new CifEmisorNoRepetitValidator();
        ScspService scspService = mock(ScspService.class);
        setField(validator, "scspService", scspService);
        EmissorCertCommand command = new EmissorCertCommand();
        command.setId(2L);
        command.setCif("B00000000");
        EmissorCertDto trobat = new EmissorCertDto();
        trobat.setId(2L);
        when(scspService.findEmissorCertByCif("B00000000")).thenReturn(trobat);

        assertTrue(validator.isValid(command, context));
    }

    // ------------------------- CifEntitatNoRepetitValidator -------------------------

    @Test
    public void cifEntitatNoRepetitSenseCoincidenciaEsValid() throws Exception {
        CifEntitatNoRepetitValidator validator = new CifEntitatNoRepetitValidator();
        EntitatService entitatService = mock(EntitatService.class);
        setField(validator, "entitatService", entitatService);
        CifEntitatNoRepetit annotacio = mock(CifEntitatNoRepetit.class);
        when(annotacio.campId()).thenReturn("id");
        when(annotacio.campCif()).thenReturn("cif");
        validator.initialize(annotacio);
        EntitatCommand command = new EntitatCommand();
        command.setCif("B00000000");

        assertTrue(validator.isValid(command, context));
    }

    @Test
    public void cifEntitatNoRepetitAmbCoincidenciaDAltreRegistreEsInvalid() throws Exception {
        CifEntitatNoRepetitValidator validator = new CifEntitatNoRepetitValidator();
        EntitatService entitatService = mock(EntitatService.class);
        setField(validator, "entitatService", entitatService);
        CifEntitatNoRepetit annotacio = mock(CifEntitatNoRepetit.class);
        when(annotacio.campId()).thenReturn("id");
        when(annotacio.campCif()).thenReturn("cif");
        validator.initialize(annotacio);
        EntitatCommand command = new EntitatCommand();
        command.setId(1L);
        command.setCif("B00000000");
        EntitatDto trobada = new EntitatDto();
        trobada.setId(2L);
        when(entitatService.findByCif("B00000000")).thenReturn(trobada);

        assertFalse(validator.isValid(command, context));
    }

    // ------------------------- CodiEntitatNoRepetitValidator -------------------------

    @Test
    public void codiEntitatNoRepetitSenseCoincidenciaEsValid() throws Exception {
        CodiEntitatNoRepetitValidator validator = new CodiEntitatNoRepetitValidator();
        EntitatService entitatService = mock(EntitatService.class);
        setField(validator, "entitatService", entitatService);
        CodiEntitatNoRepetit annotacio = mock(CodiEntitatNoRepetit.class);
        when(annotacio.campId()).thenReturn("id");
        when(annotacio.campCodi()).thenReturn("codi");
        validator.initialize(annotacio);
        EntitatCommand command = new EntitatCommand();
        command.setCodi("ENT1");

        assertTrue(validator.isValid(command, context));
    }

    @Test
    public void codiEntitatNoRepetitAmbCoincidenciaSenseIdEsInvalid() throws Exception {
        CodiEntitatNoRepetitValidator validator = new CodiEntitatNoRepetitValidator();
        EntitatService entitatService = mock(EntitatService.class);
        setField(validator, "entitatService", entitatService);
        CodiEntitatNoRepetit annotacio = mock(CodiEntitatNoRepetit.class);
        when(annotacio.campId()).thenReturn("id");
        when(annotacio.campCodi()).thenReturn("codi");
        validator.initialize(annotacio);
        EntitatCommand command = new EntitatCommand();
        command.setCodi("ENT1");
        when(entitatService.findByCodi("ENT1")).thenReturn(new EntitatDto());

        assertFalse(validator.isValid(command, context));
    }

    @Test
    public void codiEntitatNoRepetitAmbCoincidenciaDelMateixRegistreEsValid() throws Exception {
        CodiEntitatNoRepetitValidator validator = new CodiEntitatNoRepetitValidator();
        EntitatService entitatService = mock(EntitatService.class);
        setField(validator, "entitatService", entitatService);
        CodiEntitatNoRepetit annotacio = mock(CodiEntitatNoRepetit.class);
        when(annotacio.campId()).thenReturn("id");
        when(annotacio.campCodi()).thenReturn("codi");
        validator.initialize(annotacio);
        EntitatCommand command = new EntitatCommand();
        command.setId(3L);
        command.setCodi("ENT1");
        EntitatDto trobada = new EntitatDto();
        trobada.setId(3L);
        when(entitatService.findByCodi("ENT1")).thenReturn(trobada);

        assertTrue(validator.isValid(command, context));
    }

    // ------------------------- CodiProcedimentNoRepetitValidator -------------------------

    @Test
    public void codiProcedimentNoRepetitSenseCoincidenciaEsValid() throws Exception {
        CodiProcedimentNoRepetitValidator validator = new CodiProcedimentNoRepetitValidator();
        ProcedimentService procedimentService = mock(ProcedimentService.class);
        setField(validator, "procedimentService", procedimentService);
        CodiProcedimentNoRepetit annotacio = mock(CodiProcedimentNoRepetit.class);
        when(annotacio.campId()).thenReturn("id");
        when(annotacio.campEntitatId()).thenReturn("entitatId");
        when(annotacio.campCodi()).thenReturn("codi");
        validator.initialize(annotacio);
        ProcedimentCommand command = new ProcedimentCommand();
        command.setEntitatId(1L);
        command.setCodi("PROC1");

        assertTrue(validator.isValid(command, context));
    }

    @Test
    public void codiProcedimentNoRepetitAmbCoincidenciaSenseIdEsInvalid() throws Exception {
        CodiProcedimentNoRepetitValidator validator = new CodiProcedimentNoRepetitValidator();
        ProcedimentService procedimentService = mock(ProcedimentService.class);
        setField(validator, "procedimentService", procedimentService);
        CodiProcedimentNoRepetit annotacio = mock(CodiProcedimentNoRepetit.class);
        when(annotacio.campId()).thenReturn("id");
        when(annotacio.campEntitatId()).thenReturn("entitatId");
        when(annotacio.campCodi()).thenReturn("codi");
        validator.initialize(annotacio);
        ProcedimentCommand command = new ProcedimentCommand();
        command.setEntitatId(1L);
        command.setCodi("PROC1");
        when(procedimentService.findAmbEntitatICodi(1L, "PROC1")).thenReturn(new ProcedimentDto());

        assertFalse(validator.isValid(command, context));
    }

    // ------------------------- CodiServeiNoRepetitValidator -------------------------

    @Test
    public void codiServeiNoRepetitSenseCreacioEsValid() throws Exception {
        CodiServeiNoRepetitValidator validator = new CodiServeiNoRepetitValidator();
        ServeiService serveiService = mock(ServeiService.class);
        setField(validator, "serveiService", serveiService);
        CodiServeiNoRepetit annotacio = mock(CodiServeiNoRepetit.class);
        when(annotacio.campCodi()).thenReturn("codi");
        when(annotacio.campCreacio()).thenReturn("creacio");
        validator.initialize(annotacio);
        ServeiCommand command = new ServeiCommand();
        command.setCodi("SERV1");
        command.setCreacio(false);

        assertTrue(validator.isValid(command, context));
    }

    @Test
    public void codiServeiNoRepetitAmbCreacioIServeiExistentEsInvalid() throws Exception {
        CodiServeiNoRepetitValidator validator = new CodiServeiNoRepetitValidator();
        ServeiService serveiService = mock(ServeiService.class);
        setField(validator, "serveiService", serveiService);
        CodiServeiNoRepetit annotacio = mock(CodiServeiNoRepetit.class);
        when(annotacio.campCodi()).thenReturn("codi");
        when(annotacio.campCreacio()).thenReturn("creacio");
        validator.initialize(annotacio);
        ServeiCommand command = new ServeiCommand();
        command.setCodi("SERV1");
        command.setCreacio(true);
        when(serveiService.findAmbCodiPerAdminORepresentant("SERV1")).thenReturn(new es.caib.pinbal.logic.intf.dto.ServeiDto());

        assertFalse(validator.isValid(command, context));
    }

    @Test
    public void codiServeiNoRepetitAmbCreacioIServeiInexistentEsValid() throws Exception {
        CodiServeiNoRepetitValidator validator = new CodiServeiNoRepetitValidator();
        ServeiService serveiService = mock(ServeiService.class);
        setField(validator, "serveiService", serveiService);
        CodiServeiNoRepetit annotacio = mock(CodiServeiNoRepetit.class);
        when(annotacio.campCodi()).thenReturn("codi");
        when(annotacio.campCreacio()).thenReturn("creacio");
        validator.initialize(annotacio);
        ServeiCommand command = new ServeiCommand();
        command.setCodi("SERV1");
        command.setCreacio(true);
        when(serveiService.findAmbCodiPerAdminORepresentant("SERV1")).thenThrow(new ServeiNotFoundException("SERV1"));

        assertTrue(validator.isValid(command, context));
    }

    // ------------------------- NomParamConfNoRepetitValidator -------------------------

    @Test
    public void nomParamConfNoRepetitSenseCoincidenciaEsValid() throws Exception {
        NomParamConfNoRepetitValidator validator = new NomParamConfNoRepetitValidator();
        ScspService scspService = mock(ScspService.class);
        setField(validator, "scspService", scspService);
        NomParamConfNoRepetit annotacio = mock(NomParamConfNoRepetit.class);
        when(annotacio.campNom()).thenReturn("nom");
        validator.initialize(annotacio);
        ParamConfCommand command = new ParamConfCommand();
        command.setNom("PARAM1");

        assertTrue(validator.isValid(command, context));
    }

    @Test
    public void nomParamConfNoRepetitAmbCoincidenciaIForcreateEsInvalid() throws Exception {
        NomParamConfNoRepetitValidator validator = new NomParamConfNoRepetitValidator();
        ScspService scspService = mock(ScspService.class);
        setField(validator, "scspService", scspService);
        NomParamConfNoRepetit annotacio = mock(NomParamConfNoRepetit.class);
        when(annotacio.campNom()).thenReturn("nom");
        validator.initialize(annotacio);
        ParamConfCommand command = new ParamConfCommand();
        command.setNom("PARAM1");
        command.setForcreate(true);
        when(scspService.findParamConfByNom("PARAM1")).thenReturn(new ParamConfDto());

        assertFalse(validator.isValid(command, context));
    }

    @Test
    public void nomParamConfNoRepetitAmbCoincidenciaSenseForcreateEsValid() throws Exception {
        NomParamConfNoRepetitValidator validator = new NomParamConfNoRepetitValidator();
        ScspService scspService = mock(ScspService.class);
        setField(validator, "scspService", scspService);
        NomParamConfNoRepetit annotacio = mock(NomParamConfNoRepetit.class);
        when(annotacio.campNom()).thenReturn("nom");
        validator.initialize(annotacio);
        ParamConfCommand command = new ParamConfCommand();
        command.setNom("PARAM1");
        command.setForcreate(false);
        when(scspService.findParamConfByNom("PARAM1")).thenReturn(new ParamConfDto());

        assertTrue(validator.isValid(command, context));
    }

    // ------------------------- ClauPrivadaNoRepetidaValidator -------------------------

    @Test
    public void clauPrivadaNoRepetidaSenseNomNiAliesEsValid() throws Exception {
        ClauPrivadaNoRepetidaValidator validator = new ClauPrivadaNoRepetidaValidator();
        setField(validator, "scspService", mock(ScspService.class));

        assertTrue(validator.isValid(new ClauPrivadaCommand(), context));
    }

    @Test
    public void clauPrivadaNoRepetidaAmbNomRepetitEsInvalid() throws Exception {
        ClauPrivadaNoRepetidaValidator validator = new ClauPrivadaNoRepetidaValidator();
        ScspService scspService = mock(ScspService.class);
        setField(validator, "scspService", scspService);
        ClauPrivadaCommand command = new ClauPrivadaCommand();
        command.setNom("Clau1");
        ClauPrivadaDto trobada = new ClauPrivadaDto();
        trobada.setId(2L);
        when(scspService.findClauPrivadaByNom("Clau1")).thenReturn(trobada);

        assertFalse(validator.isValid(command, context));
    }

    @Test
    public void clauPrivadaNoRepetidaAmbAliesRepetitEsInvalid() throws Exception {
        ClauPrivadaNoRepetidaValidator validator = new ClauPrivadaNoRepetidaValidator();
        ScspService scspService = mock(ScspService.class);
        setField(validator, "scspService", scspService);
        ClauPrivadaCommand command = new ClauPrivadaCommand();
        command.setAlies("alies1");
        ClauPrivadaDto trobada = new ClauPrivadaDto();
        trobada.setId(2L);
        when(scspService.findClauPrivadaByAlies("alies1")).thenReturn(trobada);

        assertFalse(validator.isValid(command, context));
    }

    // ------------------------- ClauPublicaNoRepetidaValidator -------------------------

    @Test
    public void clauPublicaNoRepetidaAmbNomRepetitEsInvalid() throws Exception {
        ClauPublicaNoRepetidaValidator validator = new ClauPublicaNoRepetidaValidator();
        ScspService scspService = mock(ScspService.class);
        setField(validator, "scspService", scspService);
        ClauPublicaCommand command = new ClauPublicaCommand();
        command.setNom("Clau1");
        ClauPublicaDto trobada = new ClauPublicaDto();
        trobada.setId(2L);
        when(scspService.findClauPublicaByNom("Clau1")).thenReturn(trobada);

        assertFalse(validator.isValid(command, context));
    }

    @Test
    public void clauPublicaNoRepetidaSenseCoincidenciesEsValid() throws Exception {
        ClauPublicaNoRepetidaValidator validator = new ClauPublicaNoRepetidaValidator();
        setField(validator, "scspService", mock(ScspService.class));

        assertTrue(validator.isValid(new ClauPublicaCommand(), context));
    }

    // ------------------------- UsuariExistsValidator -------------------------

    @Test
    public void usuariExistsSenseCodiAnticEsValid() {
        UsuariExistsValidator validator = new UsuariExistsValidator();
        UsuariExists annotacio = mock(UsuariExists.class);
        validator.initialize(annotacio);

        assertTrue(validator.isValid(new UsuariCodiCommand(), context));
    }

    @Test
    public void usuariExistsAmbCodiAnticInexistentEsInvalid() throws Exception {
        UsuariExistsValidator validator = new UsuariExistsValidator();
        UsuariService usuariService = mock(UsuariService.class);
        setField(validator, "usuariService", usuariService);
        UsuariExists annotacio = mock(UsuariExists.class);
        when(annotacio.message()).thenReturn("usuari.exists.error");
        validator.initialize(annotacio);
        UsuariCodiCommand command = new UsuariCodiCommand();
        command.setCodiAntic("U1");
        when(usuariService.getDades("U1")).thenReturn(null);

        assertFalse(validator.isValid(command, context));
    }

    @Test
    public void usuariExistsAmbCodiAnticExistentEsValid() throws Exception {
        UsuariExistsValidator validator = new UsuariExistsValidator();
        UsuariService usuariService = mock(UsuariService.class);
        setField(validator, "usuariService", usuariService);
        UsuariExists annotacio = mock(UsuariExists.class);
        validator.initialize(annotacio);
        UsuariCodiCommand command = new UsuariCodiCommand();
        command.setCodiAntic("U1");
        when(usuariService.getDades("U1")).thenReturn(new UsuariDto());

        assertTrue(validator.isValid(command, context));
    }

    // ------------------------- ServeiGrupValidator -------------------------

    @Test
    public void serveiGrupSenseNomEsValid() {
        ServeiGrupValidator validator = new ServeiGrupValidator();
        ServeiGrup annotacio = mock(ServeiGrup.class);
        validator.initialize(annotacio);

        assertTrue(validator.isValid(new ServeiCampGrupCommand(), context));
    }

    @Test
    public void serveiGrupAmbNomRepetitEsInvalid() throws Exception {
        ServeiGrupValidator validator = new ServeiGrupValidator();
        ServeiService serveiService = mock(ServeiService.class);
        setField(validator, "serveiService", serveiService);
        ServeiGrup annotacio = mock(ServeiGrup.class);
        when(annotacio.message()).thenReturn("servei.camp.grup.error");
        validator.initialize(annotacio);
        ServeiCampGrupCommand command = new ServeiCampGrupCommand();
        command.setNom("Grup1");
        command.setServei("SERV1");
        ServeiCampGrupDto trobat = new ServeiCampGrupDto();
        trobat.setId(2L);
        when(serveiService.serveiCampGrupFindByNom("SERV1", "Grup1")).thenReturn(trobat);

        assertFalse(validator.isValid(command, context));
    }

    // ------------------------- ServeiReglaValidator -------------------------

    @Test
    public void serveiReglaSenseNomEsValid() {
        ServeiReglaValidator validator = new ServeiReglaValidator();
        ServeiRegla annotacio = mock(ServeiRegla.class);
        validator.initialize(annotacio);

        assertTrue(validator.isValid(new ServeiReglaCommand(), context));
    }

    @Test
    public void serveiReglaAmbNomRepetitEsInvalid() throws Exception {
        ServeiReglaValidator validator = new ServeiReglaValidator();
        ServeiService serveiService = mock(ServeiService.class);
        setField(validator, "serveiService", serveiService);
        ServeiRegla annotacio = mock(ServeiRegla.class);
        when(annotacio.message()).thenReturn("servei.regla.error");
        validator.initialize(annotacio);
        ServeiReglaCommand command = ServeiReglaCommand.builder().nom("Regla1").serveiId(1L).build();
        ServeiReglaDto trobada = ServeiReglaDto.builder().id(2L).build();
        when(serveiService.serveiReglaFindByNom(1L, "Regla1")).thenReturn(trobada);

        assertFalse(validator.isValid(command, context));
    }

    // ------------------------- ServeiUrlValidator -------------------------

    @Test
    public void serveiUrlAmbAlgunaUrlDefinidaEsValid() {
        ServeiUrlValidator validator = new ServeiUrlValidator();
        ServeiUrl annotacio = mock(ServeiUrl.class);
        validator.initialize(annotacio);
        ServeiCommand command = new ServeiCommand();
        command.setScspUrlSincrona("http://exemple.test");

        assertTrue(validator.isValid(command, context));
    }

    @Test
    public void serveiUrlSenseCapUrlDefinidaEsInvalid() {
        ServeiUrlValidator validator = new ServeiUrlValidator();
        ServeiUrl annotacio = mock(ServeiUrl.class);
        when(annotacio.message()).thenReturn("servei.url.error");
        validator.initialize(annotacio);

        assertFalse(validator.isValid(new ServeiCommand(), context));
    }
}
