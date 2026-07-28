package es.caib.pinbal.logic.helper;

import es.caib.pinbal.logic.intf.dto.ConsultaDto;
import es.caib.pinbal.logic.intf.dto.EstatTipus;
import es.caib.pinbal.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.pinbal.logic.intf.service.exception.ConsultaScspComunicacioException;
import es.caib.pinbal.logic.intf.service.exception.ConsultaScspGeneracioException;
import es.caib.pinbal.persist.entity.Consulta;
import es.caib.pinbal.persist.entity.Entitat;
import es.caib.pinbal.persist.entity.OrganGestor;
import es.caib.pinbal.persist.entity.Procediment;
import es.caib.pinbal.persist.entity.ProcedimentServei;
import es.caib.pinbal.persist.entity.ServeiCamp;
import es.caib.pinbal.persist.entity.ServeiCamp.ServeiCampTipus;
import es.caib.pinbal.persist.entity.ServeiConfig;
import es.caib.pinbal.persist.repository.ConsultaRepository;
import es.caib.pinbal.persist.repository.ProcedimentRepository;
import es.caib.pinbal.persist.repository.ServeiCampRepository;
import es.caib.pinbal.persist.repository.ServeiConfigRepository;
import es.caib.pinbal.scsp.DocumentTipus;
import es.caib.pinbal.scsp.ResultatEnviamentPeticio;
import es.caib.pinbal.scsp.ScspHelper;
import es.caib.pinbal.scsp.Solicitud;
import es.scsp.bean.common.confirmacion.Atributos;
import es.scsp.bean.common.confirmacion.ConfirmacionPeticion;
import es.scsp.bean.common.confirmacion.Estado;
import es.scsp.bean.common.peticion.Peticion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.w3c.dom.Element;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PeticioScspHelperTest {

    @Mock private ConsultaRepository consultaRepository;
    @Mock private ServeiConfigRepository serveiConfigRepository;
    @Mock private ServeiCampRepository serveiCampRepository;
    @Mock private ProcedimentRepository procedimentRepository;
    @Mock private PeticioScspEstadistiquesHelper peticionsScspEstadistiquesHelper;
    @Mock private IntegracioHelper integracioHelper;
    @Mock private ConsultaHelper consultaHelper;
    @Mock private LoggerHelper loggerHelper;

    @InjectMocks
    private PeticioScspHelper peticioScspHelper;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(LoggerHelper.class, "INSTANCE", loggerHelper);
    }

    @Test
    public void isEnviarConsultaServei_senseMaxPeticions_retornaTrue() {
        Consulta consulta = mock(Consulta.class);
        ProcedimentServei ps = mock(ProcedimentServei.class);
        when(ps.getServei()).thenReturn("SV001");
        when(consulta.getProcedimentServei()).thenReturn(ps);

        ServeiConfig serveiConfig = mock(ServeiConfig.class);
        when(serveiConfig.getMaxPeticionsMinut()).thenReturn(null);
        when(serveiConfigRepository.findByServei("SV001")).thenReturn(serveiConfig);

        boolean result = peticioScspHelper.isEnviarConsultaServei(consulta, false);

        assertTrue(result);
    }

    @Test
    public void isEnviarConsultaServei_ambMaxPeticionsNoCoberta_retornaTrue() {
        Consulta consulta = mock(Consulta.class);
        ProcedimentServei ps = mock(ProcedimentServei.class);
        when(ps.getServei()).thenReturn("SV002");
        when(consulta.getProcedimentServei()).thenReturn(ps);

        ServeiConfig serveiConfig = mock(ServeiConfig.class);
        when(serveiConfig.getMaxPeticionsMinut()).thenReturn(100);
        when(serveiConfigRepository.findByServei("SV002")).thenReturn(serveiConfig);

        // Auto=true to skip the existOlderFromAutoPendingToSend check
        boolean result = peticioScspHelper.isEnviarConsultaServei(consulta, true);

        assertTrue(result);
    }

    @Test
    public void isEnviarConsultaServei_maxPeticionsAssolides_retornaFalse() {
        Consulta consulta = mock(Consulta.class);
        ProcedimentServei ps = mock(ProcedimentServei.class);
        when(ps.getServei()).thenReturn("SV003");
        when(consulta.getProcedimentServei()).thenReturn(ps);

        ServeiConfig serveiConfig = mock(ServeiConfig.class);
        when(serveiConfig.getMaxPeticionsMinut()).thenReturn(1);
        when(serveiConfigRepository.findByServei("SV003")).thenReturn(serveiConfig);

        // First call → sets count to 1
        peticioScspHelper.isEnviarConsultaServei(consulta, true);
        // Second call in same minute → count (1) >= maxPeticionsMinut (1) → false
        boolean result = peticioScspHelper.isEnviarConsultaServei(consulta, true);

        assertFalse(result);
    }

    // ------------------------------------------------------------------
    // Fixture helper reused by many tests below
    // ------------------------------------------------------------------

    private static class ConsultaFixture {
        Consulta consulta;
        ProcedimentServei procedimentServei;
        Procediment procediment;
        Entitat entitat;
    }

    private ConsultaFixture crearConsultaFixture(String serveiCodi) {
        ConsultaFixture f = new ConsultaFixture();
        f.entitat = new Entitat();
        f.entitat.setCif("Q1111111A");
        f.entitat.setNom("Entitat Test");

        f.procediment = new Procediment();
        f.procediment.setCodi("PROC1");
        f.procediment.setNom("Procediment 1");
        f.procediment.setEntitat(f.entitat);

        f.procedimentServei = mock(ProcedimentServei.class);
        lenient().when(f.procedimentServei.getServei()).thenReturn(serveiCodi);
        lenient().when(f.procedimentServei.getProcediment()).thenReturn(f.procediment);
        lenient().when(f.procedimentServei.getProcedimentCodi()).thenReturn(null);
        lenient().when(f.procedimentServei.getServeiScsp()).thenReturn(null);

        f.consulta = mock(Consulta.class);
        lenient().when(f.consulta.getProcedimentServei()).thenReturn(f.procedimentServei);
        lenient().when(f.consulta.getScspPeticionId()).thenReturn("PET1");
        lenient().when(f.consulta.getScspSolicitudId()).thenReturn("SOL1");
        lenient().when(f.consulta.getFuncionariNom()).thenReturn("Func Nom");
        lenient().when(f.consulta.getFuncionariDocumentNum()).thenReturn("12345678A");
        lenient().when(f.consulta.getTitularDocumentTipus()).thenReturn(null);
        lenient().when(f.consulta.getTitularDocumentNum()).thenReturn("87654321B");
        lenient().when(f.consulta.getTitularNom()).thenReturn("Titular");
        lenient().when(f.consulta.getTitularLlinatge1()).thenReturn("Llinatge1");
        lenient().when(f.consulta.getTitularLlinatge2()).thenReturn("Llinatge2");
        lenient().when(f.consulta.getTitularNomComplet()).thenReturn("Titular Complet");
        lenient().when(f.consulta.getFinalitat()).thenReturn("Finalitat");
        lenient().when(f.consulta.getConsentiment()).thenReturn(ConsultaDto.Consentiment.Si);
        lenient().when(f.consulta.getDepartamentNom()).thenReturn("Departament");
        lenient().when(f.consulta.getExpedientId()).thenReturn("EXP1");
        lenient().when(f.consulta.getDadesEspecifiques()).thenReturn(null);
        lenient().when(f.consulta.getServeiCodi()).thenReturn(serveiCodi);
        lenient().when(f.consulta.isRecobriment()).thenReturn(false);
        lenient().when(f.consulta.isMultiple()).thenReturn(false);

        return f;
    }

    // ------------------------------------------------------------------
    // isEnviarConsultaServei / isMateixMinut / existOlderFromAutoPendingToSend
    // ------------------------------------------------------------------

    @Test
    public void isEnviarConsultaServei_mateixMinutSotaLimit_augmentaComptador() {
        Consulta consulta = mock(Consulta.class);
        ProcedimentServei ps = mock(ProcedimentServei.class);
        when(ps.getServei()).thenReturn("SV10");
        when(consulta.getProcedimentServei()).thenReturn(ps);

        ServeiConfig serveiConfig = new ServeiConfig();
        serveiConfig.setMaxPeticionsMinut(5);
        when(serveiConfigRepository.findByServei("SV10")).thenReturn(serveiConfig);

        // Primera crida: no hi ha interval previ -> l'obri i posa comptador a 1
        assertTrue(peticioScspHelper.isEnviarConsultaServei(consulta, true));
        // Segona crida (mateix minut, comptador 1 < 5) -> true i incrementa
        assertTrue(peticioScspHelper.isEnviarConsultaServei(consulta, true));
    }

    @Test
    public void isEnviarConsultaServei_mateixMinutAutoFalseAmbPendentAntiga_retornaFalse() {
        Consulta consulta = mock(Consulta.class);
        ProcedimentServei ps = mock(ProcedimentServei.class);
        when(ps.getServei()).thenReturn("SV11");
        when(consulta.getProcedimentServei()).thenReturn(ps);

        ServeiConfig serveiConfig = new ServeiConfig();
        serveiConfig.setMaxPeticionsMinut(5);
        when(serveiConfigRepository.findByServei("SV11")).thenReturn(serveiConfig);
        when(consultaRepository.findByEstatAndMultipleAndProcedimentServeiAndConsentimentNotNullOrderByIdAsc(
                EstatTipus.Pendent, false, ps))
                .thenReturn(Arrays.asList(mock(Consulta.class), mock(Consulta.class)));

        // Primera crida amb auto=true obre l'interval sense passar per la comprovació
        assertTrue(peticioScspHelper.isEnviarConsultaServei(consulta, true));
        // Segona crida (mateix minut) amb auto=false i pendents antigues -> false
        assertFalse(peticioScspHelper.isEnviarConsultaServei(consulta, false));
    }

    @Test
    public void isEnviarConsultaServei_noMateixMinutAutoFalseAmbPendentAntiga_retornaFalse() {
        Consulta consulta = mock(Consulta.class);
        ProcedimentServei ps = mock(ProcedimentServei.class);
        when(ps.getServei()).thenReturn("SV12");
        when(consulta.getProcedimentServei()).thenReturn(ps);

        ServeiConfig serveiConfig = new ServeiConfig();
        serveiConfig.setMaxPeticionsMinut(5);
        when(serveiConfigRepository.findByServei("SV12")).thenReturn(serveiConfig);
        when(consultaRepository.findByEstatAndMultipleAndProcedimentServeiAndConsentimentNotNullOrderByIdAsc(
                EstatTipus.Pendent, false, ps))
                .thenReturn(Arrays.asList(mock(Consulta.class), mock(Consulta.class)));

        boolean result = peticioScspHelper.isEnviarConsultaServei(consulta, false);

        assertFalse(result);
    }

    // ------------------------------------------------------------------
    // enviarPeticioScsp
    // ------------------------------------------------------------------

    @Test
    public void enviarPeticioScsp_sincrona_delegaAScspHelperEnviarSincrona() throws Exception {
        ConsultaFixture f = crearConsultaFixture("SVSINC");
        when(serveiConfigRepository.findByServei("SVSINC")).thenReturn(new ServeiConfig());
        when(serveiCampRepository.findPathInicialitzablesByServei("SVSINC")).thenReturn(Collections.emptyList());

        ScspHelper scspHelper = mock(ScspHelper.class);
        ResultatEnviamentPeticio resultat = new ResultatEnviamentPeticio();
        resultat.setEstatCodi("0001");
        resultat.setEstatDescripcio("ok");
        List<Solicitud> solicituds = Collections.singletonList(new Solicitud());
        when(scspHelper.enviarPeticionSincrona(eq("PET1"), eq(solicituds), anyBoolean(), anyBoolean(), anyList(), anyBoolean()))
                .thenReturn(resultat);

        ResultatEnviamentPeticio result = peticioScspHelper.enviarPeticioScsp(f.consulta, solicituds, true, false, scspHelper);

        assertSame(resultat, result);
        verify(scspHelper).enviarPeticionSincrona(eq("PET1"), eq(solicituds), anyBoolean(), anyBoolean(), anyList(), anyBoolean());
        verify(scspHelper, never()).enviarPeticionAsincrona(any(), any(), anyBoolean(), anyBoolean(), any(), anyBoolean());
        verify(peticionsScspEstadistiquesHelper).actualitzarEstadistiquesPeticio(isNull(), eq(solicituds), eq(false));
    }

    @Test
    public void enviarPeticioScsp_asincrona_delegaAScspHelperEnviarAsincrona() throws Exception {
        ConsultaFixture f = crearConsultaFixture("SVASINC");
        when(serveiConfigRepository.findByServei("SVASINC")).thenReturn(new ServeiConfig());
        when(serveiCampRepository.findPathInicialitzablesByServei("SVASINC")).thenReturn(Collections.emptyList());

        ScspHelper scspHelper = mock(ScspHelper.class);
        ResultatEnviamentPeticio resultat = new ResultatEnviamentPeticio();
        resultat.setEstatCodi("0002");
        resultat.setEstatDescripcio("ok async");
        List<Solicitud> solicituds = Collections.singletonList(new Solicitud());
        when(scspHelper.enviarPeticionAsincrona(eq("PET1"), eq(solicituds), anyBoolean(), anyBoolean(), anyList(), anyBoolean()))
                .thenReturn(resultat);

        ResultatEnviamentPeticio result = peticioScspHelper.enviarPeticioScsp(f.consulta, solicituds, false, true, scspHelper);

        assertSame(resultat, result);
        verify(scspHelper).enviarPeticionAsincrona(eq("PET1"), eq(solicituds), anyBoolean(), anyBoolean(), anyList(), anyBoolean());
        verify(scspHelper, never()).enviarPeticionSincrona(any(), any(), anyBoolean(), anyBoolean(), any(), anyBoolean());
    }

    // ------------------------------------------------------------------
    // enviarPeticioScspPendent
    // ------------------------------------------------------------------

    @Test
    public void enviarPeticioScspPendent_ambExit_actualitzaEstatIRegistraAccioOk() throws Exception {
        ConsultaFixture f = crearConsultaFixture("SVPEND");
        when(f.consulta.getId()).thenReturn(5L);
        when(consultaRepository.getOne(5L)).thenReturn(f.consulta);
        when(serveiConfigRepository.findByServei("SVPEND")).thenReturn(new ServeiConfig());
        when(serveiCampRepository.findPathInicialitzablesByServei("SVPEND")).thenReturn(Collections.emptyList());

        ScspHelper scspHelper = mock(ScspHelper.class);
        ResultatEnviamentPeticio resultat = new ResultatEnviamentPeticio();
        resultat.setEstatCodi("0001");
        resultat.setEstatDescripcio("ok");
        resultat.setIdsSolicituds(new String[] { "SOLX" });
        when(scspHelper.enviarPeticionSincrona(any(), anyList(), anyBoolean(), anyBoolean(), anyList(), anyBoolean()))
                .thenReturn(resultat);

        peticioScspHelper.enviarPeticioScspPendent(5L, scspHelper);

        verify(f.consulta).updateEstat(EstatTipus.Processant);
        verify(f.consulta).updateEstat(EstatTipus.Pendent);
        verify(f.consulta).updateScspSolicitudId("SOLX");
        verify(integracioHelper).addAccioOk(anyString(), anyString(), anyString(), anyMap(), any(IntegracioAccioTipusEnumDto.class), anyLong());
        verify(consultaHelper, atLeastOnce()).propagaCanviConsulta(f.consulta);
    }

    @Test
    public void enviarPeticioScspPendent_ambErrorDeGeneracio_actualitzaEstatError() throws Exception {
        ConsultaFixture f = crearConsultaFixture("SVGEN");
        when(f.consulta.getId()).thenReturn(6L);
        when(consultaRepository.getOne(6L)).thenReturn(f.consulta);
        when(serveiConfigRepository.findByServei("SVGEN")).thenReturn(new ServeiConfig());
        // Dades específiques amb JSON invàlid -> convertirEnSolicitud llança ConsultaScspGeneracioException
        when(f.consulta.getDadesEspecifiques()).thenReturn("{no-es-json");

        ScspHelper scspHelper = mock(ScspHelper.class);

        peticioScspHelper.enviarPeticioScspPendent(6L, scspHelper);

        verify(f.consulta).updateEstatError(anyString());
        verify(integracioHelper).addAccioError(
                anyString(), anyString(), anyString(), anyMap(), any(IntegracioAccioTipusEnumDto.class), anyLong(), anyString(), any(ConsultaScspGeneracioException.class));
        verifyNoInteractions(scspHelper);
    }

    @Test
    public void enviarPeticioScspPendent_ambErrorDeComunicacio_actualitzaEstatError() throws Exception {
        ConsultaFixture f = crearConsultaFixture("SVCOM");
        when(f.consulta.getId()).thenReturn(7L);
        when(consultaRepository.getOne(7L)).thenReturn(f.consulta);
        when(serveiConfigRepository.findByServei("SVCOM")).thenReturn(new ServeiConfig());
        when(serveiCampRepository.findPathInicialitzablesByServei("SVCOM")).thenReturn(Collections.emptyList());

        ScspHelper scspHelper = mock(ScspHelper.class);
        when(scspHelper.enviarPeticionSincrona(any(), anyList(), anyBoolean(), anyBoolean(), anyList(), anyBoolean()))
                .thenThrow(new ConsultaScspComunicacioException("PET1", "boom"));

        peticioScspHelper.enviarPeticioScspPendent(7L, scspHelper);

        verify(f.consulta).updateEstatError(anyString());
        verify(integracioHelper).addAccioError(
                anyString(), anyString(), anyString(), anyMap(), any(IntegracioAccioTipusEnumDto.class), anyLong(), anyString(), any(ConsultaScspComunicacioException.class));
    }

    // ------------------------------------------------------------------
    // processarIEmmagatzemarDadesEspecifiques (i processarDadesEspecifiques)
    // ------------------------------------------------------------------

    @Test
    public void processarIEmmagatzemarDadesEspecifiques_transformaCampsIElimina() throws Exception {
        Consulta consulta = mock(Consulta.class);
        ProcedimentServei ps = mock(ProcedimentServei.class);
        when(ps.getServei()).thenReturn("SVDE");
        when(consulta.getProcedimentServei()).thenReturn(ps);

        ServeiCamp campDataFormatCustom = mock(ServeiCamp.class);
        when(campDataFormatCustom.getPath()).thenReturn("data1");
        when(campDataFormatCustom.getTipus()).thenReturn(ServeiCampTipus.DATA);
        when(campDataFormatCustom.getDataFormat()).thenReturn("yyyyMMdd");

        ServeiCamp campDataFormatDefault = mock(ServeiCamp.class);
        when(campDataFormatDefault.getPath()).thenReturn("data2");
        when(campDataFormatDefault.getTipus()).thenReturn(ServeiCampTipus.DATA);
        when(campDataFormatDefault.getDataFormat()).thenReturn(null);

        ServeiCamp campDataBuit = mock(ServeiCamp.class);
        when(campDataBuit.getPath()).thenReturn("data3");
        when(campDataBuit.getTipus()).thenReturn(ServeiCampTipus.DATA);

        ServeiCamp campBooleaSi = mock(ServeiCamp.class);
        when(campBooleaSi.getPath()).thenReturn("bool1");
        when(campBooleaSi.getTipus()).thenReturn(ServeiCampTipus.BOOLEA);

        ServeiCamp campBooleaNo = mock(ServeiCamp.class);
        when(campBooleaNo.getPath()).thenReturn("bool2");
        when(campBooleaNo.getTipus()).thenReturn(ServeiCampTipus.BOOLEA);

        ServeiCamp campBooleaBuit = mock(ServeiCamp.class);
        when(campBooleaBuit.getPath()).thenReturn("bool3");
        when(campBooleaBuit.getTipus()).thenReturn(ServeiCampTipus.BOOLEA);

        ServeiCamp campDocTipus = mock(ServeiCamp.class);
        when(campDocTipus.getPath()).thenReturn("docTipus1");

        ServeiCamp campDocIdent = mock(ServeiCamp.class);
        when(campDocIdent.getPath()).thenReturn("docIdent1");
        when(campDocIdent.getTipus()).thenReturn(ServeiCampTipus.DOC_IDENT);
        when(campDocIdent.getCampPare()).thenReturn(campDocTipus);

        when(serveiCampRepository.findByServeiOrderByGrupOrdreAsc("SVDE")).thenReturn(Arrays.asList(
                campDataFormatCustom, campDataFormatDefault, campDataBuit,
                campBooleaSi, campBooleaNo, campBooleaBuit, campDocIdent));

        Map<String, Object> dades = new HashMap<>();
        dades.put("data1", "25/12/2025");
        dades.put("data2", "01/01/2020");
        dades.put("data3", "");
        dades.put("bool1", "SI");
        dades.put("bool2", "no");
        dades.put("bool3", "");
        dades.put("docIdent1", "");
        dades.put("docTipus1", "1");

        peticioScspHelper.processarIEmmagatzemarDadesEspecifiques(consulta, dades);

        assertEquals("20251225", dades.get("data1"));
        assertEquals("01012020", dades.get("data2"));
        assertNull(dades.get("data3"));
        assertEquals("true", dades.get("bool1"));
        assertEquals("false", dades.get("bool2"));
        assertNull(dades.get("bool3"));
        assertFalse(dades.containsKey("docTipus1"));
        verify(consulta).updateDadesEspecifiques(anyString());
    }

    @Test
    public void processarIEmmagatzemarDadesEspecifiques_dataInvalida_llancaExcepcio() {
        Consulta consulta = mock(Consulta.class);
        ProcedimentServei ps = mock(ProcedimentServei.class);
        when(ps.getServei()).thenReturn("SVDE2");
        when(consulta.getProcedimentServei()).thenReturn(ps);

        ServeiCamp campData = mock(ServeiCamp.class);
        when(campData.getPath()).thenReturn("data1");
        when(campData.getTipus()).thenReturn(ServeiCampTipus.DATA);

        when(serveiCampRepository.findByServeiOrderByGrupOrdreAsc("SVDE2")).thenReturn(Collections.singletonList(campData));

        Map<String, Object> dades = new HashMap<>();
        dades.put("data1", "no-es-una-data");

        assertThrows(ConsultaScspGeneracioException.class,
                () -> peticioScspHelper.processarIEmmagatzemarDadesEspecifiques(consulta, dades));
    }

    // ------------------------------------------------------------------
    // convertirEnSolicitud(Consulta)
    // ------------------------------------------------------------------

    @Test
    public void convertirEnSolicitud_procedimentAmbCodiSiaOrigen_usaProcedimentRepository() throws Exception {
        ConsultaFixture f = crearConsultaFixture("SV20");
        f.procediment.setCodiSiaOrigen("SIA1");
        Procediment procediment2 = new Procediment();
        procediment2.setCodi("PROC2");
        procediment2.setNom("Procediment 2");
        when(procedimentRepository.findByEntitatAndCodiSia(f.entitat, "SIA1")).thenReturn(procediment2);
        when(serveiConfigRepository.findByServei("SV20")).thenReturn(new ServeiConfig());

        Solicitud solicitud = peticioScspHelper.convertirEnSolicitud(f.consulta);

        assertEquals("PROC2", solicitud.getProcedimentCodi());
        assertEquals("Procediment 2", solicitud.getProcedimentNom());
    }

    @Test
    public void convertirEnSolicitud_titularDocumentTipusIDadesEspecifiquesValides() throws Exception {
        ConsultaFixture f = crearConsultaFixture("SV21");
        when(f.consulta.getTitularDocumentTipus()).thenReturn("NIF");
        when(f.consulta.getDadesEspecifiques()).thenReturn("{\"clau\":\"valor\"}");
        ServeiConfig serveiConfig = new ServeiConfig();
        serveiConfig.setPinbalUnitatDir3FromEntitat(true);
        f.entitat.setUnitatArrel("UA1");
        when(serveiConfigRepository.findByServei("SV21")).thenReturn(serveiConfig);

        Solicitud solicitud = peticioScspHelper.convertirEnSolicitud(f.consulta);

        assertEquals(DocumentTipus.NIF, solicitud.getTitularDocumentTipus());
        assertEquals("valor", solicitud.getDadesEspecifiquesMap().get("clau"));
        assertEquals("UA1", solicitud.getUnitatTramitadoraCodi());
    }

    @Test
    public void convertirEnSolicitud_dadesEspecifiquesInvalides_llancaExcepcio() {
        ConsultaFixture f = crearConsultaFixture("SV22");
        when(f.consulta.getDadesEspecifiques()).thenReturn("{no-es-json");
        ServeiConfig serveiConfig = new ServeiConfig();
        serveiConfig.setPinbalUnitatDir3("DIR3CODE");
        when(serveiConfigRepository.findByServei("SV22")).thenReturn(serveiConfig);

        assertThrows(ConsultaScspGeneracioException.class,
                () -> peticioScspHelper.convertirEnSolicitud(f.consulta));
    }

    @Test
    public void convertirEnSolicitud_isUseAutoClasseFalseIOrganGestorIProcedimentCodiOverride() throws Exception {
        ConsultaFixture f = crearConsultaFixture("SV23");
        when(f.procedimentServei.getProcedimentCodi()).thenReturn("PROC_OVERRIDE");
        f.procediment.setValorCampAutomatizado(true);
        OrganGestor organGestor = new OrganGestor();
        organGestor.setCodi("OG1");
        f.procediment.setOrganGestor(organGestor);
        ServeiConfig serveiConfig = new ServeiConfig();
        serveiConfig.setUseAutoClasse(false);
        when(serveiConfigRepository.findByServei("SV23")).thenReturn(serveiConfig);

        Solicitud solicitud = peticioScspHelper.convertirEnSolicitud(f.consulta);

        assertEquals("PROC_OVERRIDE", solicitud.getProcedimentCodi());
        assertNull(solicitud.getProcedimentValorCampAutomatizado());
        assertEquals("OG1", solicitud.getUnitatTramitadoraCodi());
    }

    // ------------------------------------------------------------------
    // convertirEnSolicitud (overload amb Entitat/Procediment/ScspHelper)
    // ------------------------------------------------------------------

    @Test
    public void convertirEnSolicitudOverload_ambRecobrimentIConfigPerDefecte() throws Exception {
        Entitat entitat = new Entitat();
        entitat.setCif("CIF1");
        entitat.setNom("Nom Entitat");
        Procediment procediment = new Procediment();
        procediment.setCodi("PROC1");
        procediment.setNom("Procediment1");
        procediment.setEntitat(entitat);
        procediment.setValorCampAutomatizado(true);
        ProcedimentServei ps = mock(ProcedimentServei.class);
        when(ps.getProcedimentCodi()).thenReturn(null);

        when(serveiConfigRepository.findByServei("SVOV")).thenReturn(new ServeiConfig());
        when(serveiCampRepository.findPathInicialitzablesByServei("SVOV")).thenReturn(Collections.emptyList());

        ScspHelper scspHelper = mock(ScspHelper.class);
        Element dadesElement = mock(Element.class);
        Element resultElement = mock(Element.class);
        when(scspHelper.copiarDadesEspecifiquesRecobriment(eq("SVOV"), eq(dadesElement), anyBoolean(), anyBoolean(), anyList()))
                .thenReturn(resultElement);

        Solicitud solicitud = peticioScspHelper.convertirEnSolicitud(
                entitat, procediment, "SVOV", "FuncNom", "FuncNif",
                ConsultaDto.DocumentTipus.NIF, "DocNum", "TitNom", "Llin1", "Llin2", "NomComplet",
                "Finalitat", ConsultaDto.Consentiment.Si, "DeptNom", "UNITAT_FALLBACK", "EXP1",
                dadesElement, ps, scspHelper);

        assertEquals("PROC1", solicitud.getProcedimentCodi());
        assertEquals(DocumentTipus.NIF, solicitud.getTitularDocumentTipus());
        assertSame(resultElement, solicitud.getDadesEspecifiquesElement());
        assertEquals("UNITAT_FALLBACK", solicitud.getUnitatTramitadoraCodi());
    }

    @Test
    public void convertirEnSolicitudOverload_ambCodiSiaOrigenIUseAutoClasseFalse() throws Exception {
        Entitat entitat = new Entitat();
        entitat.setCif("CIF2");
        entitat.setNom("Nom Entitat 2");
        Procediment procediment = new Procediment();
        procediment.setCodi("PROC_ORIG");
        procediment.setNom("Procediment Orig");
        procediment.setCodiSiaOrigen("SIA9");
        procediment.setEntitat(entitat);
        Procediment procedimentReal = new Procediment();
        procedimentReal.setCodi("PROC_REAL");
        procedimentReal.setNom("Procediment Real");
        when(procedimentRepository.findByEntitatAndCodiSia(entitat, "SIA9")).thenReturn(procedimentReal);

        ProcedimentServei ps = mock(ProcedimentServei.class);
        when(ps.getProcedimentCodi()).thenReturn(null);

        ServeiConfig serveiConfig = new ServeiConfig();
        serveiConfig.setUseAutoClasse(false);
        when(serveiConfigRepository.findByServei("SVOV2")).thenReturn(serveiConfig);
        when(serveiCampRepository.findPathInicialitzablesByServei("SVOV2")).thenReturn(Collections.emptyList());

        ScspHelper scspHelper = mock(ScspHelper.class);
        Element dadesElement = mock(Element.class);
        when(scspHelper.copiarDadesEspecifiquesRecobriment(eq("SVOV2"), eq(dadesElement), anyBoolean(), anyBoolean(), anyList()))
                .thenReturn(null);

        Solicitud solicitud = peticioScspHelper.convertirEnSolicitud(
                entitat, procediment, "SVOV2", "FuncNom", "FuncNif",
                null, "DocNum", "TitNom", "Llin1", "Llin2", "NomComplet",
                "Finalitat", ConsultaDto.Consentiment.Llei, "DeptNom", null, "EXP1",
                dadesElement, ps, scspHelper);

        assertEquals("PROC_REAL", solicitud.getProcedimentCodi());
        assertNull(solicitud.getTitularDocumentTipus());
        assertNull(solicitud.getUnitatTramitadoraCodi());
    }

    // ------------------------------------------------------------------
    // convertirEnMultiplesSolicituds
    // ------------------------------------------------------------------

    @Test
    public void convertirEnMultiplesSolicituds_generaUnaSolicitudPerFila() throws Exception {
        ConsultaDto dto = new ConsultaDto();
        dto.setServeiCodi("SVM");
        dto.setEntitatCif("CIF1");
        dto.setEntitatNom("Entitat Nom");
        dto.setFuncionariNom("Func");
        dto.setFuncionariNif("11111111A");
        dto.setFinalitat("Fin");
        dto.setConsentiment(ConsultaDto.Consentiment.Si);
        dto.setDepartamentNom("Depart");
        dto.setCampsPeticioMultiple(new String[] {
                "DatosGenericos/Titular/TipoDocumentacion",
                "DatosGenericos/Titular/Documentacion",
                "DatosGenericos/Titular/Nombre",
                "DatosGenericos/Titular/Apellido1",
                "DatosGenericos/Titular/Apellido2",
                "DatosGenericos/Titular/NombreCompleto",
                "DatosGenericos/Solicitante/IdExpediente",
                "campEspecific1"
        });
        dto.setDadesPeticioMultiple(new String[][] {
                { "NIF", "12345678A", "Nom1", "Llin1", "Llin2", "Nom Complet 1", "EXP1", "valorEspecific1" },
                { "Pasaporte", "P123", "Nom2", "Llin1b", "Llin2b", "Nom Complet 2", "EXP2", "valorEspecific2" }
        });

        Entitat entitat = new Entitat();
        Procediment procediment = new Procediment();
        procediment.setCodi("PROC1");
        procediment.setNom("Procediment 1");
        procediment.setEntitat(entitat);
        ProcedimentServei ps = mock(ProcedimentServei.class);
        when(ps.getProcediment()).thenReturn(procediment);

        ServeiCamp campEspecific = mock(ServeiCamp.class);
        when(campEspecific.getPath()).thenReturn("campEspecific1");
        when(campEspecific.getTipus()).thenReturn(ServeiCampTipus.TEXT);
        when(serveiCampRepository.findByServeiOrderByGrupOrdreAsc("SVM")).thenReturn(Collections.singletonList(campEspecific));
        when(serveiConfigRepository.findByServei("SVM")).thenReturn(new ServeiConfig());

        List<Solicitud> result = peticioScspHelper.convertirEnMultiplesSolicituds(dto, ps);

        assertEquals(2, result.size());
        Solicitud s1 = result.get(0);
        assertEquals(DocumentTipus.NIF, s1.getTitularDocumentTipus());
        assertEquals("12345678A", s1.getTitularDocument());
        assertEquals("valorEspecific1", s1.getDadesEspecifiquesMap().get("campEspecific1"));
        Solicitud s2 = result.get(1);
        assertEquals(DocumentTipus.Passaport, s2.getTitularDocumentTipus());
        assertEquals("EXP2", s2.getExpedientId());
    }

    @Test
    public void convertirEnMultiplesSolicituds_excepcioGenericaEsEmboliadaEnConsultaScspGeneracioException() {
        ConsultaDto dto = new ConsultaDto();
        dto.setServeiCodi("SVM2");
        dto.setCampsPeticioMultiple(new String[] {});
        dto.setDadesPeticioMultiple(new String[][] { new String[] {} });
        dto.setConsentiment(null); // provoca NullPointerException dins el bucle

        Entitat entitat = new Entitat();
        Procediment procediment = new Procediment();
        procediment.setCodi("P");
        procediment.setNom("N");
        procediment.setEntitat(entitat);
        ProcedimentServei ps = mock(ProcedimentServei.class);
        when(ps.getProcediment()).thenReturn(procediment);
        when(serveiCampRepository.findByServeiOrderByGrupOrdreAsc("SVM2")).thenReturn(Collections.emptyList());

        assertThrows(ConsultaScspGeneracioException.class,
                () -> peticioScspHelper.convertirEnMultiplesSolicituds(dto, ps));
    }

    @Test
    public void convertirEnMultiplesSolicituds_reenviaConsultaScspGeneracioExceptionSenseEmboliar() {
        ConsultaDto dto = new ConsultaDto();
        dto.setServeiCodi("SVM3");
        dto.setCampsPeticioMultiple(new String[] { "campData1" });
        dto.setDadesPeticioMultiple(new String[][] { new String[] { "no-es-una-data" } });
        dto.setConsentiment(ConsultaDto.Consentiment.Si);

        Entitat entitat = new Entitat();
        Procediment procediment = new Procediment();
        procediment.setCodi("P");
        procediment.setNom("N");
        procediment.setEntitat(entitat);
        ProcedimentServei ps = mock(ProcedimentServei.class);
        when(ps.getProcediment()).thenReturn(procediment);

        ServeiCamp campData = mock(ServeiCamp.class);
        when(campData.getPath()).thenReturn("campData1");
        when(campData.getTipus()).thenReturn(ServeiCampTipus.DATA);
        when(serveiCampRepository.findByServeiOrderByGrupOrdreAsc("SVM3")).thenReturn(Collections.singletonList(campData));
        when(serveiConfigRepository.findByServei("SVM3")).thenReturn(new ServeiConfig());

        assertThrows(ConsultaScspGeneracioException.class,
                () -> peticioScspHelper.convertirEnMultiplesSolicituds(dto, ps));
    }

    // ------------------------------------------------------------------
    // updateEstatConsulta / calcularDataResposta / updateEstatConsultaError
    // ------------------------------------------------------------------

    @Test
    public void updateEstatConsulta_totsElsCodisEstatMapegenACorrectEstatTipus() {
        Consulta c1 = mock(Consulta.class);
        when(c1.getId()).thenReturn(1L);
        ResultatEnviamentPeticio r1 = new ResultatEnviamentPeticio();
        r1.setEstatCodi("0001");
        peticioScspHelper.updateEstatConsulta(c1, r1, null);
        verify(c1).updateEstat(EstatTipus.Pendent);

        Consulta c2 = mock(Consulta.class);
        when(c2.getId()).thenReturn(2L);
        ResultatEnviamentPeticio r2 = new ResultatEnviamentPeticio();
        r2.setEstatCodi("0002");
        peticioScspHelper.updateEstatConsulta(c2, r2, null);
        verify(c2).updateEstat(EstatTipus.Processant);

        Consulta c3 = mock(Consulta.class);
        when(c3.getId()).thenReturn(3L);
        ResultatEnviamentPeticio r3 = new ResultatEnviamentPeticio();
        r3.setEstatCodi("0003");
        peticioScspHelper.updateEstatConsulta(c3, r3, null);
        verify(c3).updateEstat(EstatTipus.Tramitada);

        Consulta c4 = mock(Consulta.class);
        when(c4.getId()).thenReturn(4L);
        ResultatEnviamentPeticio r4 = new ResultatEnviamentPeticio();
        r4.setEstatCodi("0004");
        peticioScspHelper.updateEstatConsulta(c4, r4, null);
        verify(c4).updateEstat(EstatTipus.Processant);
    }

    @Test
    public void updateEstatConsulta_ambError_actualitzaEstatError() {
        Consulta consulta = mock(Consulta.class);
        when(consulta.getId()).thenReturn(1L);
        ResultatEnviamentPeticio resultat = new ResultatEnviamentPeticio();
        resultat.setErrorGeneracio(true);
        resultat.setEstatCodi("9999");
        resultat.setEstatDescripcio("Error greu");

        peticioScspHelper.updateEstatConsulta(consulta, resultat, null);

        verify(consulta).updateEstat(EstatTipus.Error);
        verify(consulta).updateEstatError("[9999] Error greu");
        // updateEstatConsulta delegates to updateEstatConsultaError (which calls propagaCanviConsulta itself)
        // and then calls propagaCanviConsulta again unconditionally at its own end — called twice in this path.
        verify(consultaHelper, times(2)).propagaCanviConsulta(consulta);
    }

    @Test
    public void updateEstatConsulta_ambConsultaMultipleITempsEstimat_calculaDataEsperada() {
        Consulta consulta = mock(Consulta.class);
        when(consulta.getId()).thenReturn(1L);
        when(consulta.isMultiple()).thenReturn(true);

        Estado estado = new Estado();
        estado.setTiempoEstimadoRespuesta(5);
        Atributos atributos = new Atributos();
        atributos.setEstado(estado);
        ConfirmacionPeticion confirmacio = new ConfirmacionPeticion();
        confirmacio.setAtributos(atributos);

        ResultatEnviamentPeticio resultat = new ResultatEnviamentPeticio();
        resultat.setEstatCodi("0001");
        resultat.setConfirmacionPeticion(confirmacio);

        peticioScspHelper.updateEstatConsulta(consulta, resultat, null);

        verify(consulta).updateDateEsperadaResposta(any());
    }

    @Test
    public void updateEstatConsulta_ambAccioParams_ompleElMapa() {
        Consulta consulta = mock(Consulta.class);
        when(consulta.getId()).thenReturn(1L);
        when(consulta.getScspPeticionId()).thenReturn("PET1");
        when(consulta.getScspSolicitudId()).thenReturn("SOL1");
        ResultatEnviamentPeticio resultat = new ResultatEnviamentPeticio();
        resultat.setErrorEnviament(true);
        resultat.setEstatCodi("9998");
        resultat.setEstatDescripcio("Error enviament");

        Map<String, String> accioParams = new HashMap<>();
        peticioScspHelper.updateEstatConsulta(consulta, resultat, accioParams);

        assertEquals("PET1", accioParams.get("idPeticion"));
        assertEquals("SOL1", accioParams.get("idSolicitud"));
        assertEquals("[9998] Error enviament", accioParams.get("error"));
    }

    @Test
    public void updateEstatConsulta_ambIdNull_noPropagaCanvi() {
        Consulta consulta = mock(Consulta.class);
        ResultatEnviamentPeticio resultat = new ResultatEnviamentPeticio();
        resultat.setEstatCodi("0001");

        peticioScspHelper.updateEstatConsulta(consulta, resultat, null);

        verify(consultaHelper, never()).propagaCanviConsulta(any());
    }

    @Test
    public void calcularDataResposta_ambNull_retornaNull() {
        assertNull(peticioScspHelper.calcularDataResposta(null));
    }

    @Test
    public void calcularDataResposta_ambHores_calculaDataFutura() {
        long abans = System.currentTimeMillis();
        java.util.Date resultat = peticioScspHelper.calcularDataResposta(2);
        assertNotNull(resultat);
        assertTrue(resultat.getTime() > abans);
    }

    @Test
    public void updateEstatConsultaError_ambIdNoNull_propagaCanvi() {
        Consulta consulta = mock(Consulta.class);
        when(consulta.getId()).thenReturn(9L);

        peticioScspHelper.updateEstatConsultaError(consulta, "error greu");

        verify(consulta).updateEstat(EstatTipus.Error);
        verify(consulta).updateEstatError("error greu");
        verify(consultaHelper).propagaCanviConsulta(consulta);
    }

    @Test
    public void updateEstatConsultaError_ambIdNull_noPropagaCanvi() {
        Consulta consulta = mock(Consulta.class);

        peticioScspHelper.updateEstatConsultaError(consulta, "error greu");

        verify(consulta).updateEstat(EstatTipus.Error);
        verify(consulta).updateEstatError("error greu");
        verify(consultaHelper, never()).propagaCanviConsulta(any());
    }

    // ------------------------------------------------------------------
    // isGestioXsdActiva / isIniDadesEspecifiques / isAddDadesEspecifiques / isUseAutoClasse
    // ------------------------------------------------------------------

    @Test
    public void configMethods_ambServeiConfigPresent() {
        ServeiConfig actiu = new ServeiConfig();
        actiu.setActivaGestioXsd(true);
        actiu.setIniDadesEspecifiques(true);
        actiu.setAddDadesEspecifiques(false);
        actiu.setUseAutoClasse(false);
        when(serveiConfigRepository.findByServei("SVCFG1")).thenReturn(actiu);

        assertTrue(peticioScspHelper.isGestioXsdActiva("SVCFG1"));
        assertTrue(peticioScspHelper.isIniDadesEspecifiques("SVCFG1"));
        assertFalse(peticioScspHelper.isUseAutoClasse("SVCFG1"));

        ServeiConfig inactiu = new ServeiConfig();
        when(serveiConfigRepository.findByServei("SVCFG2")).thenReturn(inactiu);
        assertFalse(peticioScspHelper.isGestioXsdActiva("SVCFG2"));
        assertFalse(peticioScspHelper.isIniDadesEspecifiques("SVCFG2"));
        assertTrue(peticioScspHelper.isUseAutoClasse("SVCFG2"));
    }

    @Test
    public void configMethods_ambServeiConfigNull_usaValorsPerDefecte() {
        when(serveiConfigRepository.findByServei("SVCFG3")).thenReturn(null);

        assertFalse(peticioScspHelper.isGestioXsdActiva("SVCFG3"));
        assertFalse(peticioScspHelper.isIniDadesEspecifiques("SVCFG3"));
        assertTrue(peticioScspHelper.isUseAutoClasse("SVCFG3"));
    }

    // ------------------------------------------------------------------
    // generarPeticioXml
    // ------------------------------------------------------------------

    @Test
    public void generarPeticioXml_delegaAScspHelper() throws Exception {
        ConsultaFixture f = crearConsultaFixture("SVXML");
        ServeiConfig serveiConfig = new ServeiConfig();
        when(serveiConfigRepository.findByServei("SVXML")).thenReturn(serveiConfig);
        when(serveiCampRepository.findPathInicialitzablesByServei("SVXML")).thenReturn(Collections.emptyList());

        ScspHelper scspHelper = mock(ScspHelper.class);
        Peticion peticio = new Peticion();
        when(scspHelper.crearPeticion(eq("PET1"), anyList(), anyBoolean(), anyBoolean(), anyList(), anyBoolean()))
                .thenReturn(peticio);
        when(scspHelper.generaPeticioXml(peticio)).thenReturn("<xml/>");

        String xml = peticioScspHelper.generarPeticioXml(f.consulta, scspHelper);

        assertEquals("<xml/>", xml);
        verify(scspHelper).generaPeticioXml(peticio);
    }
}
