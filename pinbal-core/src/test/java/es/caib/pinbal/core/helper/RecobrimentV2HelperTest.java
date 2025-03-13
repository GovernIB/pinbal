package es.caib.pinbal.core.helper;

import es.caib.pinbal.client.recobriment.v2.DadesComunes;
import es.caib.pinbal.client.recobriment.v2.Funcionari;
import es.caib.pinbal.client.recobriment.v2.PeticioSincrona;
import es.caib.pinbal.client.recobriment.v2.SolicitudSimple;
import es.caib.pinbal.core.dto.ServeiCampDto;
import es.caib.pinbal.core.model.Entitat;
import es.caib.pinbal.core.model.Procediment;
import es.caib.pinbal.core.model.ServeiConfig;
import es.caib.pinbal.core.repository.EntitatRepository;
import es.caib.pinbal.core.repository.ProcedimentRepository;
import es.caib.pinbal.core.repository.ServeiCampRepository;
import es.caib.pinbal.core.repository.ServeiConfigRepository;
import es.caib.pinbal.core.service.ServeiService;
import es.caib.pinbal.core.service.exception.ServeiNotFoundException;
import es.caib.pinbal.plugins.DadesUsuari;
import es.caib.pinbal.scsp.ScspHelper;
import es.scsp.bean.common.Peticion;
import es.scsp.common.domain.core.Servicio;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.BindException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RecobrimentV2HelperTest {


    @InjectMocks
    private RecobrimentV2Helper recobrimentV2Helper = new RecobrimentV2Helper();

    @Mock
    private ServeiConfigRepository serveiConfigRepository;

    @Mock
    private ServeiCampRepository serveiCampRepository;

    @Mock
    private EntitatRepository entitatRepository;

    @Mock
    private ProcedimentRepository procedimentRepository;

    @Mock
    private PluginHelper pluginHelper;

    @Mock
    private ServeiService serveiService;

    ScspHelper scspHelper;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        scspHelper = mock(ScspHelper.class);
        recobrimentV2Helper.setScspHelper(scspHelper);
    }

    // TESTS validateDadesComunes
    // /////////////////////////////////////////////////////////

    @Test
    public void testValidateDadesComunes_NullDadesComunes() {
        PeticioSincrona peticio = PeticioSincrona.builder().build();
        BindException errors = new BindException(peticio, "peticio");

        recobrimentV2Helper.validateDadesComunes(null, "SERVEI_CODI", errors);

        assertFalse(errors.getAllErrors().isEmpty());
        verifyZeroInteractions(serveiConfigRepository, entitatRepository, procedimentRepository);
    }

    @Test
    public void testValidateDadesComunes_InvalidServeiCodi() {
        DadesComunes dadesComunes = DadesComunes.builder()
                .serveiCodi("INVALID_CODI")
                .build();
        PeticioSincrona peticio = PeticioSincrona.builder().dadesComunes(dadesComunes).build();
        BindException errors = new BindException(peticio, "peticio");

        when(serveiConfigRepository.findByServei("INVALID_CODI")).thenReturn(null);

        recobrimentV2Helper.validateDadesComunes(dadesComunes, "INVALID_CODI", errors);

        assertFalse(errors.getFieldErrors("dadesComunes.serveiCodi").isEmpty());
        verify(serveiConfigRepository).findByServei("INVALID_CODI");
    }

    @Test
    public void testValidateDadesComunes_InvalidEntitatCif() {
        DadesComunes dadesComunes = DadesComunes.builder()
                .entitatCif("INVALID_CIF")
                .build();
        PeticioSincrona peticio = PeticioSincrona.builder().dadesComunes(dadesComunes).build();
        BindException errors = new BindException(peticio, "peticio");

        when(entitatRepository.findByCif("INVALID_CIF")).thenReturn(null);

        recobrimentV2Helper.validateDadesComunes(dadesComunes, "SERVEI_CODI", errors);

        assertFalse(errors.getFieldErrors("dadesComunes.entitatCif").isEmpty());
        verify(entitatRepository).findByCif("INVALID_CIF");
    }

    @Test
    public void testValidateDadesComunes_InvalidProcedimentCodi() {
        DadesComunes dadesComunes = DadesComunes.builder()
                .procedimentCodi("INVALID_PROCEDIMENT")
                .build();
        PeticioSincrona peticio = PeticioSincrona.builder().dadesComunes(dadesComunes).build();
        BindException errors = new BindException(peticio, "peticio");

        when(procedimentRepository.findByEntitatAndCodi(any(Entitat.class), eq("INVALID_PROCEDIMENT"))).thenReturn(null);

        recobrimentV2Helper.validateDadesComunes(dadesComunes, "SERVEI_CODI", errors);

        assertFalse(errors.getFieldErrors("dadesComunes.procedimentCodi").isEmpty());
        verify(procedimentRepository).findByEntitatAndCodi(any(Entitat.class), eq("INVALID_PROCEDIMENT"));
    }

    @Test
    public void testValidateDadesComunes_ProcedimentEntitatMismatch() {
        Entitat entitat = new Entitat();
        entitat.setCodi("ENT01");
        entitat.setCif("VALID_CIF");

        es.caib.pinbal.core.model.Procediment procediment = new es.caib.pinbal.core.model.Procediment();
        procediment.setEntitat(new Entitat());
        procediment.getEntitat().setCodi("ENT02");
        procediment.getEntitat().setCif("DIFFERENT_CIF");

        DadesComunes dadesComunes = DadesComunes.builder()
                .entitatCif("VALID_CIF")
                .procedimentCodi("VALID_CODE")
                .build();

        PeticioSincrona peticio = PeticioSincrona.builder().dadesComunes(dadesComunes).build();
        BindException errors = new BindException(peticio, "peticio");

        when(entitatRepository.findByCif("VALID_CIF")).thenReturn(entitat);
        when(procedimentRepository.findByEntitatAndCodi(any(Entitat.class), eq("VALID_CODE"))).thenReturn(procediment);

        recobrimentV2Helper.validateDadesComunes(dadesComunes, "SERVEI_CODI", errors);

        assertFalse(errors.getFieldErrors("dadesComunes.procedimentCodi").isEmpty());
        verify(entitatRepository).findByCif("VALID_CIF");
        verify(procedimentRepository).findByEntitatAndCodi(any(Entitat.class), eq("VALID_CODE"));
    }

    @Test
    public void testValidateDadesComunes_MissingConsentiment() {
        DadesComunes dadesComunes = DadesComunes.builder()
                .consentiment(null)
                .build();
        PeticioSincrona peticio = PeticioSincrona.builder().dadesComunes(dadesComunes).build();
        BindException errors = new BindException(peticio, "peticio");

        recobrimentV2Helper.validateDadesComunes(dadesComunes, "SERVEI_CODI", errors);

        assertFalse(errors.getFieldErrors("dadesComunes.consentiment").isEmpty());
    }

    @Test
    public void testValidateDadesComunes_MissingFuncionari() {
        DadesComunes dadesComunes = DadesComunes.builder()
                .funcionari(null)
                .build();
        PeticioSincrona peticio = PeticioSincrona.builder().dadesComunes(dadesComunes).build();
        BindException errors = new BindException(peticio, "peticio");

        recobrimentV2Helper.validateDadesComunes(dadesComunes, "SERVEI_CODI", errors);

        assertFalse(errors.getFieldErrors("dadesComunes.funcionari").isEmpty());
    }

    @Test
    public void testValidateDadesComunes_InvalidFuncionari() {
        Funcionari funcionari = new Funcionari();
        funcionari.setNom(null);
        funcionari.setNif("123546789132456789Z");

        DadesComunes dadesComunes = DadesComunes.builder()
                .funcionari(funcionari)
                .build();
        PeticioSincrona peticio = PeticioSincrona.builder().dadesComunes(dadesComunes).build();
        BindException errors = new BindException(peticio, "peticio");

        recobrimentV2Helper.validateDadesComunes(dadesComunes, "SERVEI_CODI", errors);

        assertFalse(errors.getFieldErrors("dadesComunes.funcionari.nom").isEmpty());
        assertFalse(errors.getFieldErrors("dadesComunes.funcionari.nif").isEmpty());
    }


    // TESTS validateDadesSolicitud
    // /////////////////////////////////////////////////////////

    @Test
    public void testValidateDadesSolicitud_NullSolicitud() {
        PeticioSincrona peticio = PeticioSincrona.builder().build();
        BindException errors = new BindException(peticio, "peticio");
        recobrimentV2Helper.validateDadesSolicitud(null, "SERVEI_CODI", errors, serveiService);

        assertFalse(errors.getAllErrors().isEmpty());
        assertFalse(errors.getFieldErrors("solicitud").isEmpty());
    }

    @Test
    public void testValidateDadesSolicitud_InvalidSolicitudId() {
        SolicitudSimple solicitud = SolicitudSimple.builder()
                .expedient("EXPEDIENT_TOO_LONG_EXCEEDING_MAX_CHARACTERS_12345678901234567890_MORE_THAN_ALLOWED_25")
                .build();
        PeticioSincrona peticio = PeticioSincrona.builder().solicitud(solicitud).build();
        BindException errors = new BindException(peticio, "peticio");

        ServeiConfig serveiConfig = new ServeiConfig();
        serveiConfig.setDocumentObligatori(false);
        when(serveiConfigRepository.findByServei("SERVEI_CODI")).thenReturn(serveiConfig);

        recobrimentV2Helper.validateDadesSolicitud(solicitud, "SERVEI_CODI", errors, serveiService);

        assertFalse(errors.getFieldErrors("solicitud.expedient").isEmpty());
    }

    @Test
    public void testValidateDadesSolicitud_NullTitular() {
        ServeiConfig serveiConfig = new ServeiConfig();
        serveiConfig.setDocumentObligatori(true);
        when(serveiConfigRepository.findByServei("SERVEI_CODI")).thenReturn(serveiConfig);

        SolicitudSimple solicitud = SolicitudSimple.builder().titular(null).build();
        PeticioSincrona peticio = PeticioSincrona.builder().solicitud(solicitud).build();
        BindException errors = new BindException(peticio, "peticio");
        recobrimentV2Helper.validateDadesSolicitud(solicitud, "SERVEI_CODI", errors, serveiService);

        assertFalse(errors.getFieldErrors("solicitud.titular").isEmpty());
        verify(serveiConfigRepository).findByServei("SERVEI_CODI");
    }

    @Test
    public void testValidateDadesSolicitud_InvalidTitularDocumentNumero() {
        es.caib.pinbal.client.recobriment.v2.Titular titular = es.caib.pinbal.client.recobriment.v2.Titular.builder()
                .documentTipus(es.caib.pinbal.client.recobriment.v2.Titular.DocumentTipus.NIF)
                .documentNumero("INVALID_NIF")
                .build();

        SolicitudSimple solicitud = SolicitudSimple.builder().titular(titular).build();
        PeticioSincrona peticio = PeticioSincrona.builder().solicitud(solicitud).build();
        BindException errors = new BindException(peticio, "peticio");

        ServeiConfig serveiConfig = new ServeiConfig();
        serveiConfig.setDocumentObligatori(false);
        when(serveiConfigRepository.findByServei("SERVEI_CODI")).thenReturn(serveiConfig);

        recobrimentV2Helper.validateDadesSolicitud(solicitud, "SERVEI_CODI", errors, serveiService);

        assertFalse(errors.getFieldErrors("solicitud.titular.documentNumero").isEmpty());
    }

    @Test
    public void testValidateDadesSolicitud_InvalidCampDadesEspecifiques() {
        Map<String, String> dadesEspecifiques = new HashMap<>();
        dadesEspecifiques.put("INVALID_KEY", "INVALID_VALUE");

        SolicitudSimple solicitud = SolicitudSimple.builder().dadesEspecifiques(dadesEspecifiques).build();
        PeticioSincrona peticio = PeticioSincrona.builder().solicitud(solicitud).build();
        BindException errors = new BindException(peticio, "peticio");

        ServeiConfig serveiConfig = new ServeiConfig();
        serveiConfig.setDocumentObligatori(false);
        when(serveiConfigRepository.findByServei("SERVEI_CODI")).thenReturn(serveiConfig);

        recobrimentV2Helper.validateDadesSolicitud(solicitud, "SERVEI_CODI", errors, serveiService);

        assertFalse(errors.getAllErrors().isEmpty());
        assertEquals(errors.getFieldErrors("solicitud.dadesEspecifiques").get(0).getDefaultMessage(), "Els següents camps no estan definits al servei: INVALID_KEY");
    }

    @Test
    public void testValidateDadesSolicitud_InvalidValorDadesEspecifiques() throws ServeiNotFoundException {
        Map<String, String> dadesEspecifiques = new HashMap<>();
//        dadesEspecifiques.put("INVALID_KEY", "INVALID_VALUE");

        SolicitudSimple solicitud = SolicitudSimple.builder().dadesEspecifiques(dadesEspecifiques).build();
        PeticioSincrona peticio = PeticioSincrona.builder().solicitud(solicitud).build();
        BindException errors = new BindException(peticio, "peticio");

        ServeiConfig serveiConfig = new ServeiConfig();
        serveiConfig.setDocumentObligatori(false);
        when(serveiConfigRepository.findByServei("SERVEI_CODI")).thenReturn(serveiConfig);
        List<ServeiCampDto> mockedCamps = new ArrayList<>();
        ServeiCampDto camp = new ServeiCampDto();
        camp.setPath("DatosEspecificos/camp_obligatori");
        camp.setObligatori(true);
        mockedCamps.add(camp);
        when(serveiService.findServeiCamps("SERVEI_CODI")).thenReturn(mockedCamps);

        recobrimentV2Helper.validateDadesSolicitud(solicitud, "SERVEI_CODI", errors, serveiService);

        assertFalse(errors.getAllErrors().isEmpty());
        assertEquals(errors.getFieldErrors("solicitud.dadesEspecifiques[DatosEspecificos/camp_obligatori]").get(0).getDefaultMessage(), "Aquest camp és obligatori");
    }


    // TESTS validateDadesSolicituds
    // /////////////////////////////////////////////////////////

    @Test
    public void testValidateDadesSolicituds_NullSolicitudList() {
        String serveiCodi = "SERVEI_CODI";
        Servicio mockedServicio = mock(Servicio.class);
        when(scspHelper.getServicio(anyString())).thenReturn(mockedServicio);
        when(mockedServicio.getMaxSolicitudesPeticion()).thenReturn(1);
        Map<String, List<String>> errors = recobrimentV2Helper.validateDadesSolicituds(null, serveiCodi, serveiService);

        assertFalse(errors.isEmpty());
        assertTrue(errors.containsKey("GLOBAL"));
        assertEquals("La consulta excedeix el màxim de sol·licituds permeses pel servei", errors.get("GLOBAL").get(0));
    }

    @Test
    public void testValidateDadesSolicituds_ExceedsMaxAllowed() throws ServeiNotFoundException {
        String serveiCodi = "SERVEI_CODI";
        Servicio mockedServicio = mock(Servicio.class);
        List<ServeiCampDto> mockedCamps = new ArrayList<>();
        when(scspHelper.getServicio(anyString())).thenReturn(mockedServicio);
        when(serveiService.findServeiCamps(serveiCodi)).thenReturn(mockedCamps);
        when(mockedServicio.getMaxSolicitudesPeticion()).thenReturn(1);

        List<SolicitudSimple> solicituds = new ArrayList<>();
        solicituds.add(SolicitudSimple.builder().build());
        solicituds.add(SolicitudSimple.builder().build());

        Map<String, List<String>> errors = recobrimentV2Helper.validateDadesSolicituds(solicituds, serveiCodi, serveiService);

        assertFalse(errors.isEmpty());
        assertTrue(errors.containsKey("GLOBAL"));
        assertEquals("La consulta excedeix el màxim de sol·licituds permeses pel servei", errors.get("GLOBAL").get(0));
    }

    @Test
    public void testValidateDadesSolicituds_WithIndividualErrors() throws ServeiNotFoundException {
        String serveiCodi = "SERVEI_CODI";
        Servicio mockedServicio = mock(Servicio.class);
        when(scspHelper.getServicio(anyString())).thenReturn(mockedServicio);
        when(mockedServicio.getMaxSolicitudesPeticion()).thenReturn(5);

        Map<String, String> dadesEspecifiques = new HashMap<>();
        List<SolicitudSimple> solicituds = new ArrayList<>();
        SolicitudSimple solicitud = SolicitudSimple.builder().dadesEspecifiques(dadesEspecifiques).build();
        solicituds.add(solicitud);
//        PeticioAsincrona peticio = PeticioAsincrona.builder().solicituds(solicituds).build();
//        BindException errors = new BindException(peticio, "peticio");

        ServeiConfig serveiConfig = new ServeiConfig();
        serveiConfig.setDocumentObligatori(false);
        when(serveiConfigRepository.findByServei(serveiCodi)).thenReturn(serveiConfig);
        List<ServeiCampDto> mockedCamps = new ArrayList<>();
        ServeiCampDto camp = new ServeiCampDto();
        camp.setPath("DatosEspecificos/camp_obligatori");
        camp.setObligatori(true);
        mockedCamps.add(camp);
        when(serveiService.findServeiCamps(serveiCodi)).thenReturn(mockedCamps);

        Map<String, List<String>> errors = recobrimentV2Helper.validateDadesSolicituds(solicituds, serveiCodi, serveiService);

        assertFalse(errors.isEmpty());
        assertTrue(errors.containsKey("000001:dadesEspecifiques[DatosEspecificos/camp_obligatori]"));
        assertEquals("Aquest camp és obligatori", errors.get("000001:dadesEspecifiques[DatosEspecificos/camp_obligatori]").get(0));
    }

    // TESTS toPeticion
    // /////////////////////////////////////////////////////////

    @Test
    public void testToPeticion_ValidPeticioSincrona() throws Exception {
        DadesComunes dadesComunes = DadesComunes.builder()
                .serveiCodi("VALID_SERVICE_CODE")
                .entitatCif("VALID_CIF")
                .procedimentCodi("VALID_PROCEDIMENT_CODE")
                .funcionari(Funcionari.builder().nom("FUNC123").nif("12345678Z").build())
                .departament("Valid Department")
                .finalitat("Valid Finality")
                .consentiment(DadesComunes.Consentiment.Si)
                .build();

        SolicitudSimple solicitud = SolicitudSimple.builder()
//                .id("SOL123")
                .titular(es.caib.pinbal.client.recobriment.v2.Titular.builder()
                        .documentTipus(es.caib.pinbal.client.recobriment.v2.Titular.DocumentTipus.NIE)
                        .documentNumero("X1234567L")
                        .nom("Titular Name")
                        .llinatge1("Surname1")
                        .llinatge2("Surname2")
                        .build())
                .build();

        PeticioSincrona peticioSincrona = PeticioSincrona.builder()
                .dadesComunes(dadesComunes)
                .solicitud(solicitud)
                .build();

        Procediment mockProcediment = new Procediment();
        mockProcediment.setCodi("VALID_PROCEDIMENT_CODE");
        mockProcediment.setNom("Valid Procediment Name");

        Entitat mockEntitat = new Entitat();
        mockEntitat.setCif("VALID_CIF");
        mockEntitat.setNom("Valid Entitat Name");

        DadesUsuari dadesUsuari = new DadesUsuari();
        dadesUsuari.setNif("12345678Z");
        dadesUsuari.setNom("Funcionari Name");

        when(serveiConfigRepository.findByServei("VALID_SERVICE_CODE")).thenReturn(new ServeiConfig());
        when(procedimentRepository.findByEntitatAndCodi(any(Entitat.class), eq("VALID_PROCEDIMENT_CODE"))).thenReturn(mockProcediment);
        when(entitatRepository.findByCif("VALID_CIF")).thenReturn(mockEntitat);
        when(pluginHelper.dadesUsuariConsultarAmbUsuariCodi(anyString())).thenReturn(dadesUsuari);

        Peticion peticion = recobrimentV2Helper.toPeticion(peticioSincrona);

        assertNotNull(peticion);
        assertEquals("VALID_SERVICE_CODE", peticion.getAtributos().getCodigoCertificado());
//        assertEquals("SOL123", peticion.getSolicitudes().getSolicitudTransmision().get(0).getId());
    }

    @Test
    public void testToPeticion_NullPeticioSincrona() throws Exception {
        Peticion peticion = recobrimentV2Helper.toPeticion((PeticioSincrona) null);
        assertNull(peticion);
    }

    @Test
    public void testToPeticion_MissingDadesComunes() {
        PeticioSincrona peticioSincrona = PeticioSincrona.builder()
                .dadesComunes(null)
                .build();

        Exception exception = null;
        try {
            recobrimentV2Helper.toPeticion(peticioSincrona);
        } catch (Exception e) {
            exception = e;
        }
        assertNotNull(exception);
        assertTrue(exception instanceof NullPointerException);
    }

//    @Test
//    public void testToPeticion_ValidDadesEspecifiques() throws Exception {
//        Map<String, String> dadesEspecifiques = new HashMap<>();
//        dadesEspecifiques.put("SpecificKey", "SpecificValue");
//
//        DadesComunes dadesComunes = DadesComunes.builder()
//                .serveiCodi("VALID_SERVICE_CODE")
//                .entitatCif("VALID_CIF")
//                .procedimentCodi("VALID_PROCEDIMENT_CODE")
//                .funcionari(Funcionari.builder().codi("FUNC123").nif("12345678Z").build())
//                .departament("Valid Department")
//                .finalitat("Valid Finality")
//                .consentiment(DadesComunes.Consentiment.Si)
//                .build();
//
//        SolicitudSimple solicitud = SolicitudSimple.builder()
//                .id("SOL123")
//                .dadesEspecifiques(dadesEspecifiques)
//                .titular(es.caib.pinbal.client.recobriment.v2.Titular.builder()
//                        .documentTipus(es.caib.pinbal.client.recobriment.v2.Titular.DocumentTipus.NIE)
//                        .documentNumero("X1234567L")
//                        .nom("Titular Name")
//                        .llinatge1("Surname1")
//                        .llinatge2("Surname2")
//                        .build())
//                .build();
//
//        PeticioSincrona peticioSincrona = PeticioSincrona.builder()
//                .dadesComunes(dadesComunes)
//                .solicitud(solicitud)
//                .build();
//
//        Procediment mockProcediment = new Procediment();
//        mockProcediment.setCodi("VALID_PROCEDIMENT_CODE");
//        mockProcediment.setNom("Valid Procediment Name");
//
//        Entitat mockEntitat = new Entitat();
//        mockEntitat.setCif("VALID_CIF");
//        mockEntitat.setNom("Valid Entitat Name");
//
//        DadesUsuari dadesUsuari = new DadesUsuari();
//        dadesUsuari.setNif("12345678Z");
//        dadesUsuari.setNom("Funcionari Name");
//
//        List<String> mockedPaths = new ArrayList<>();
//
//        when(serveiConfigRepository.findByServei("VALID_SERVICE_CODE")).thenReturn(new ServeiConfig());
//        when(serveiCampRepository.findPathInicialitzablesByServei("VALID_SERVICE_CODE")).thenReturn(mockedPaths);
//        when(procedimentRepository.findByCodi("VALID_PROCEDIMENT_CODE")).thenReturn(mockProcediment);
//        when(entitatRepository.findByCif("VALID_CIF")).thenReturn(mockEntitat);
//        when(pluginHelper.dadesUsuariConsultarAmbUsuariCodi(anyString())).thenReturn(dadesUsuari);
//
//        Peticion peticion = recobrimentV2Helper.toPeticion(peticioSincrona);
//
//        assertNotNull(peticion);
//        assertNotNull(peticion.getSolicitudes().getSolicitudTransmision().get(0).getDatosEspecificos());
//    }

}