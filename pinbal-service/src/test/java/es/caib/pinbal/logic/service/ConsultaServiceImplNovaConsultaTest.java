package es.caib.pinbal.logic.service;

import es.caib.pinbal.logic.helper.*;
import es.caib.pinbal.logic.helper.mock.JustificantHelperFactory;
import es.caib.pinbal.logic.intf.dto.ConsultaDto;
import es.caib.pinbal.logic.intf.service.ConsultaService;
import es.caib.pinbal.logic.intf.service.exception.*;
import es.caib.pinbal.persist.entity.*;
import es.caib.pinbal.persist.repository.*;
import es.caib.pinbal.persist.repository.dadesobertes.DadesObertesConsultaRepository;
import es.caib.pinbal.persist.repository.explotacio.ExplotConsultaDimensioRepository;
import es.caib.pinbal.persist.repository.explotacio.ExplotConsultaFetsRepository;
import es.caib.pinbal.persist.repository.explotacio.ExplotTempsRepository;
import es.caib.pinbal.persist.repository.llistat.LlistatConsultaRepository;
import es.caib.pinbal.scsp.ResultatEnviamentPeticio;
import es.caib.pinbal.scsp.ScspHelper;
import es.caib.pinbal.scsp.Solicitud;
import ma.glasnost.orika.MapperFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class ConsultaServiceImplNovaConsultaTest {

    @Mock private ConsultaRepository consultaRepository;
    @Mock private DadesObertesConsultaRepository dadesObertesConsultaRepository;
    @Mock private EntitatRepository entitatRepository;
    @Mock private EntitatUsuariRepository entitatUsuariRepository;
    @Mock private ExplotConsultaDimensioRepository explotConsultaDimensioRepository;
    @Mock private ExplotConsultaFetsRepository explotConsultaFetsRepository;
    @Mock private ExplotTempsRepository explotTempsRepository;
    @Mock private LlistatConsultaRepository llistatConsultaRepository;
    @Mock private ProcedimentRepository procedimentRepository;
    @Mock private ProcedimentServeiRepository procedimentServeiRepository;
    @Mock private ServeiJustificantCampRepository serveiJustificantCampRepository;
    @Mock private ServeiRepository serveiRepository;
    @Mock private SuperConsultaRepository superConsultaRepository;
    @Mock private TokenRepository tokenRepository;
    @Mock private UsuariRepository usuariRepository;
    @Mock private ConfigHelper configHelper;
    @Mock private ConsultaHelper consultaHelper;
    @Mock private DtoMappingHelper dtoMappingHelper;
    @Mock private EmailReportEstatHelper emailReportEstatHelper;
    @Mock private ExcelHelper excelHelper;
    @Mock private IntegracioHelper integracioHelper;
    @Mock private JustificantHelperFactory justificantHelperFactory;
    @Mock private PeticioScspEstadistiquesHelper peticioScspEstadistiquesHelper;
    @Mock private PeticioScspHelper peticioScspHelper;
    @Mock private PluginHelper pluginHelper;
    @Mock private ServeiHelper serveiHelper;
    @Mock private UsuariHelper usuariHelper;
    @Mock private MutableAclService aclService;
    @Mock private PlatformTransactionManager transactionManager;
    @Mock private MapperFacade mapperFacade;
    @Mock private ScspHelper scspHelper;
    @Mock private LoggerHelper loggerHelper;
    @Mock private ConsultaService self;

    @InjectMocks
    private ConsultaServiceImpl consultaService;

    private Authentication auth;
    private SecurityContext securityContext;

    @BeforeEach
    public void setUp() {
        when(dtoMappingHelper.getMapperFacade()).thenReturn(mapperFacade);
        ReflectionTestUtils.setField(consultaService, "scspHelper", scspHelper);
        ReflectionTestUtils.setField(LoggerHelper.class, "INSTANCE", loggerHelper);
        ReflectionTestUtils.setField(consultaService, "propertiesCopiades", true);
        ReflectionTestUtils.setField(consultaService, "self", self);
        auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("usuari1");
        when(auth.getAuthorities()).thenReturn(Collections.emptyList());
        securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
    }

    private ProcedimentServei crearProcedimentServeiActiu(String serveiCodi) {
        Entitat entitat = new Entitat();
        entitat.setCodi("ENT01");
        entitat.setNom("Ajuntament de Prova");
        entitat.setCif("Q0700001A");

        Procediment procediment = new Procediment();
        procediment.setCodi("PROC01");
        procediment.setNom("Procediment de prova");
        procediment.setEntitat(entitat);

        Servei servei = new Servei();
        servei.setCodi(serveiCodi);
        servei.setDescripcio("Servei de prova");

        ProcedimentServei ps = ProcedimentServei.getBuilder(procediment, serveiCodi).build();
//        ps.setServeiScsp(servei);
        return ps;
    }

    private ConsultaDto crearConsultaDtoPeticio(Long procedimentId, String serveiCodi) {
        ConsultaDto consulta = new ConsultaDto();
        consulta.setProcedimentId(procedimentId);
        consulta.setServeiCodi(serveiCodi);
        consulta.setFuncionariNom("Joan Fuster");
        consulta.setFuncionariNif("12345678A");
        consulta.setTitularNom("Maria");
        consulta.setTitularLlinatge1("Ramis");
        consulta.setTitularDocumentNum("87654321B");
        consulta.setDepartamentNom("Departament TIC");
        consulta.setFinalitat("Tramitació expedient");
        consulta.setConsentiment(ConsultaDto.Consentiment.Si);
        return consulta;
    }

    private ResultatEnviamentPeticio crearResultatOk() {
        ResultatEnviamentPeticio resultat = new ResultatEnviamentPeticio();
        resultat.setErrorGeneracio(false);
        resultat.setErrorEnviament(false);
        resultat.setErrorRecepcio(false);
        resultat.setEstatCodi("0003");
        resultat.setEstatDescripcio("Tramitada");
        resultat.setIdsSolicituds(new String[] {"SOL001"});
        return resultat;
    }

    // ===================== novaConsulta =====================

    @Test
    public void novaConsulta_procedimentServeiNoTrobat_llancaException() {
        ConsultaDto consulta = crearConsultaDtoPeticio(1L, "SV001");
        when(procedimentServeiRepository.findByProcedimentIdAndServei(1L, "SV001")).thenReturn(null);

        assertThrows(ProcedimentServeiNotFoundException.class, () -> consultaService.novaConsulta(consulta));
    }

    @Test
    public void novaConsulta_serveiNoPermes_llancaException() throws Exception {
        ConsultaDto consulta = crearConsultaDtoPeticio(1L, "SV001");
        ProcedimentServei ps = crearProcedimentServeiActiu("SV001");
        when(procedimentServeiRepository.findByProcedimentIdAndServei(1L, "SV001")).thenReturn(ps);
        when(serveiHelper.isServeiPermesPerUsuari(any(Entitat.class), any(Procediment.class), eq("SV001"))).thenReturn(false);

        assertThrows(ServeiNotAllowedException.class, () -> consultaService.novaConsulta(consulta));
    }

    @Test
    public void novaConsulta_ambEnviament_ok() throws Exception {
        ConsultaDto consulta = crearConsultaDtoPeticio(1L, "SV001");
        ProcedimentServei ps = crearProcedimentServeiActiu("SV001");
        when(procedimentServeiRepository.findByProcedimentIdAndServei(1L, "SV001")).thenReturn(ps);
        when(serveiHelper.isServeiPermesPerUsuari(any(Entitat.class), any(Procediment.class), eq("SV001"))).thenReturn(true);
        when(scspHelper.generarIdPeticion("SV001")).thenReturn("PET001");
        when(peticioScspHelper.isEnviarConsultaServei(any(Consulta.class), eq(false))).thenReturn(true);
        when(peticioScspHelper.convertirEnSolicitud(any(Consulta.class))).thenReturn(new Solicitud());
        ResultatEnviamentPeticio resultat = crearResultatOk();
        when(peticioScspHelper.enviarPeticioScsp(any(Consulta.class), any(), eq(true), anyBoolean(), eq(scspHelper))).thenReturn(resultat);
        when(consultaRepository.save(any(Consulta.class))).thenAnswer(inv -> inv.getArgument(0));

        ConsultaDto dtoResultat = new ConsultaDto();
        dtoResultat.setId(10L);
        dtoResultat.setEstat("Tramitada");
        when(mapperFacade.map(any(Consulta.class), eq(ConsultaDto.class))).thenReturn(dtoResultat);

        ConsultaDto resposta = consultaService.novaConsulta(consulta);

        assertSame(dtoResultat, resposta);
        verify(consultaHelper).propagaCreacioConsulta(any(Consulta.class));
        verify(integracioHelper).addAccioOk(eq("PET001"), any(), any(), any(), any(), anyLong());
    }

    @Test
    public void novaConsulta_senseEnviament_ok() throws Exception {
        ConsultaDto consulta = crearConsultaDtoPeticio(1L, "SV001");
        ProcedimentServei ps = crearProcedimentServeiActiu("SV001");
        when(procedimentServeiRepository.findByProcedimentIdAndServei(1L, "SV001")).thenReturn(ps);
        when(serveiHelper.isServeiPermesPerUsuari(any(Entitat.class), any(Procediment.class), eq("SV001"))).thenReturn(true);
        when(scspHelper.generarIdPeticion("SV001")).thenReturn("PET002");
        when(peticioScspHelper.isEnviarConsultaServei(any(Consulta.class), eq(false))).thenReturn(false);
        when(consultaRepository.save(any(Consulta.class))).thenAnswer(inv -> inv.getArgument(0));

        ConsultaDto dtoResultat = new ConsultaDto();
        dtoResultat.setEstat("Pendent");
        when(mapperFacade.map(any(Consulta.class), eq(ConsultaDto.class))).thenReturn(dtoResultat);

        ConsultaDto resposta = consultaService.novaConsulta(consulta);

        assertSame(dtoResultat, resposta);
        verify(peticioScspHelper, times(0)).enviarPeticioScsp(any(), any(), anyBoolean(), anyBoolean(), any());
    }

    @Test
    public void novaConsulta_errorComunicacio_retornaDtoAmbError() throws Exception {
        ConsultaDto consulta = crearConsultaDtoPeticio(1L, "SV001");
        ProcedimentServei ps = crearProcedimentServeiActiu("SV001");
        when(procedimentServeiRepository.findByProcedimentIdAndServei(1L, "SV001")).thenReturn(ps);
        when(serveiHelper.isServeiPermesPerUsuari(any(Entitat.class), any(Procediment.class), eq("SV001"))).thenReturn(true);
        when(scspHelper.generarIdPeticion("SV001")).thenReturn("PET003");
        when(peticioScspHelper.isEnviarConsultaServei(any(Consulta.class), eq(false))).thenReturn(true);
        when(peticioScspHelper.convertirEnSolicitud(any(Consulta.class))).thenReturn(new Solicitud());
        when(peticioScspHelper.enviarPeticioScsp(any(Consulta.class), any(), eq(true), anyBoolean(), eq(scspHelper)))
                .thenThrow(new ConsultaScspComunicacioException("PET003", "Error de comunicació amb SCSP"));
        when(consultaRepository.save(any(Consulta.class))).thenAnswer(inv -> inv.getArgument(0));
        when(usuariHelper.getUsuariAutenticat()).thenReturn(null);

        ConsultaDto dtoResultat = new ConsultaDto();
        when(mapperFacade.map(any(Consulta.class), eq(ConsultaDto.class))).thenReturn(dtoResultat);

        ConsultaDto resposta = consultaService.novaConsulta(consulta);

        assertEquals("ERROR", resposta.getRespostaEstadoCodigo());
        assertEquals("Error de comunicació amb SCSP", resposta.getRespostaEstadoError());
        verify(integracioHelper).addAccioError(eq("PET003"), any(), any(), any(), any(), anyLong(), any(), any());
    }

    @Test
    public void novaConsulta_errorGeneracio_propagaException() throws Exception {
        ConsultaDto consulta = crearConsultaDtoPeticio(1L, "SV001");
        ProcedimentServei ps = crearProcedimentServeiActiu("SV001");
        when(procedimentServeiRepository.findByProcedimentIdAndServei(1L, "SV001")).thenReturn(ps);
        when(serveiHelper.isServeiPermesPerUsuari(any(Entitat.class), any(Procediment.class), eq("SV001"))).thenReturn(true);
        when(scspHelper.generarIdPeticion("SV001")).thenReturn("PET004");
        doThrow(new ConsultaScspGeneracioException("Dades específiques invàlides"))
                .when(peticioScspHelper).processarIEmmagatzemarDadesEspecifiques(any(Consulta.class), any());

        assertThrows(ConsultaScspGeneracioException.class, () -> consultaService.novaConsulta(consulta));
    }

    // ===================== novaConsultaInit =====================

    @Test
    public void novaConsultaInit_procedimentServeiNoTrobat_llancaException() {
        ConsultaDto consulta = crearConsultaDtoPeticio(2L, "SV002");
        when(procedimentServeiRepository.findByProcedimentIdAndServei(2L, "SV002")).thenReturn(null);

        assertThrows(ProcedimentServeiNotFoundException.class, () -> consultaService.novaConsultaInit(consulta));
    }

    @Test
    public void novaConsultaInit_procedimentServeiNoActiu_llancaException() {
        ConsultaDto consulta = crearConsultaDtoPeticio(2L, "SV002");
        ProcedimentServei ps = crearProcedimentServeiActiu("SV002");
        ps.updateActiu(false);
        when(procedimentServeiRepository.findByProcedimentIdAndServei(2L, "SV002")).thenReturn(ps);

        assertThrows(ProcedimentServeiNotFoundException.class, () -> consultaService.novaConsultaInit(consulta));
    }

    @Test
    public void novaConsultaInit_serveiNoPermes_llancaException() {
        ConsultaDto consulta = crearConsultaDtoPeticio(2L, "SV002");
        ProcedimentServei ps = crearProcedimentServeiActiu("SV002");
        when(procedimentServeiRepository.findByProcedimentIdAndServei(2L, "SV002")).thenReturn(ps);
        when(serveiHelper.isServeiPermesPerUsuari(any(Entitat.class), any(Procediment.class), eq("SV002"))).thenReturn(false);

        assertThrows(ServeiNotAllowedException.class, () -> consultaService.novaConsultaInit(consulta));
    }

    @Test
    public void novaConsultaInit_ok_desaIPropaga() throws Exception {
        ConsultaDto consulta = crearConsultaDtoPeticio(2L, "SV002");
        ProcedimentServei ps = crearProcedimentServeiActiu("SV002");
        when(procedimentServeiRepository.findByProcedimentIdAndServei(2L, "SV002")).thenReturn(ps);
        when(serveiHelper.isServeiPermesPerUsuari(any(Entitat.class), any(Procediment.class), eq("SV002"))).thenReturn(true);
        when(scspHelper.generarIdPeticion("SV002")).thenReturn("PET005");
        when(consultaRepository.save(any(Consulta.class))).thenAnswer(inv -> inv.getArgument(0));

        ConsultaDto dtoResultat = new ConsultaDto();
        dtoResultat.setId(20L);
        when(mapperFacade.map(any(Consulta.class), eq(ConsultaDto.class))).thenReturn(dtoResultat);

        ConsultaDto resposta = consultaService.novaConsultaInit(consulta);

        assertSame(dtoResultat, resposta);
        verify(consultaHelper).propagaCreacioConsulta(any(Consulta.class));
        verify(integracioHelper).addAccioOk(eq("PET005"), any(), any(), any(), any(), anyLong());
    }

    // ===================== novaConsultaEnviament =====================

    @Test
    public void novaConsultaEnviament_procedimentServeiNoTrobat_llancaException() {
        ConsultaDto consulta = crearConsultaDtoPeticio(3L, "SV003");
        consulta.setScspPeticionId("PET006");
        when(procedimentServeiRepository.findByProcedimentIdAndServei(3L, "SV003")).thenReturn(null);

        assertThrows(ProcedimentServeiNotFoundException.class,
                () -> consultaService.novaConsultaEnviament(100L, consulta));
    }

    @Test
    public void novaConsultaEnviament_consultaNoTrobada_llancaException() {
        ConsultaDto consulta = crearConsultaDtoPeticio(3L, "SV003");
        consulta.setScspPeticionId("PET007");
        ProcedimentServei ps = crearProcedimentServeiActiu("SV003");
        when(procedimentServeiRepository.findByProcedimentIdAndServei(3L, "SV003")).thenReturn(ps);
        when(consultaRepository.findById(101L)).thenReturn(Optional.empty());

        assertThrows(ConsultaNotFoundException.class,
                () -> consultaService.novaConsultaEnviament(101L, consulta));
    }

    @Test
    public void novaConsultaEnviament_ambEnviament_ok() throws Exception {
        ConsultaDto consulta = crearConsultaDtoPeticio(3L, "SV003");
        consulta.setScspPeticionId("PET008");
        consulta.setScspSolicitudId("SOL008");
        ProcedimentServei ps = crearProcedimentServeiActiu("SV003");
        when(procedimentServeiRepository.findByProcedimentIdAndServei(3L, "SV003")).thenReturn(ps);
        Consulta existent = Consulta.getBuilder(
                        "PET008", "Joan Fuster", "12345678A", null, "87654321B",
                        "Maria", "Ramis", null, null, "Departament TIC",
                        ps, "Tramitació expedient", ConsultaDto.Consentiment.Si, null, false, false, null)
                .build();
        when(consultaRepository.findById(102L)).thenReturn(Optional.of(existent));
        when(peticioScspHelper.isEnviarConsultaServei(existent, false)).thenReturn(true);
        when(peticioScspHelper.convertirEnSolicitud(existent)).thenReturn(new Solicitud());
        ResultatEnviamentPeticio resultat = crearResultatOk();
        when(peticioScspHelper.enviarPeticioScsp(eq(existent), any(), eq(true), anyBoolean(), eq(scspHelper))).thenReturn(resultat);

        consultaService.novaConsultaEnviament(102L, consulta);

        verify(consultaHelper).propagaCanviConsulta(existent);
        verify(integracioHelper).addAccioOk(eq("PET008"), any(), any(), any(), any(), anyLong());
    }

    @Test
    public void novaConsultaEnviament_senseEnviament_noCridaScsp() throws Exception {
        ConsultaDto consulta = crearConsultaDtoPeticio(3L, "SV003");
        consulta.setScspPeticionId("PET009");
        ProcedimentServei ps = crearProcedimentServeiActiu("SV003");
        when(procedimentServeiRepository.findByProcedimentIdAndServei(3L, "SV003")).thenReturn(ps);
        Consulta existent = Consulta.getBuilder(
                        "PET009", "Joan Fuster", "12345678A", null, "87654321B",
                        "Maria", "Ramis", null, null, "Departament TIC",
                        ps, "Tramitació expedient", ConsultaDto.Consentiment.Si, null, false, false, null)
                .build();
        when(consultaRepository.findById(103L)).thenReturn(Optional.of(existent));
        when(peticioScspHelper.isEnviarConsultaServei(existent, false)).thenReturn(false);

        consultaService.novaConsultaEnviament(103L, consulta);

        verify(peticioScspHelper, times(0)).enviarPeticioScsp(any(), any(), anyBoolean(), anyBoolean(), any());
    }

    @Test
    public void novaConsultaEnviament_errorComunicacio_processaError() throws Exception {
        ConsultaDto consulta = crearConsultaDtoPeticio(3L, "SV003");
        consulta.setScspPeticionId("PET010");
        ProcedimentServei ps = crearProcedimentServeiActiu("SV003");
        when(procedimentServeiRepository.findByProcedimentIdAndServei(3L, "SV003")).thenReturn(ps);
        Consulta existent = Consulta.getBuilder(
                        "PET010", "Joan Fuster", "12345678A", null, "87654321B",
                        "Maria", "Ramis", null, null, "Departament TIC",
                        ps, "Tramitació expedient", ConsultaDto.Consentiment.Si, null, false, false, null)
                .build();
        when(consultaRepository.findById(104L)).thenReturn(Optional.of(existent));
        when(peticioScspHelper.isEnviarConsultaServei(existent, false)).thenReturn(true);
        when(peticioScspHelper.convertirEnSolicitud(existent)).thenReturn(new Solicitud());
        when(peticioScspHelper.enviarPeticioScsp(eq(existent), any(), eq(true), anyBoolean(), eq(scspHelper)))
                .thenThrow(new ConsultaScspComunicacioException("PET010", "Error comunicant amb SCSP"));
        when(consultaRepository.save(any(Consulta.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapperFacade.map(any(Consulta.class), eq(ConsultaDto.class))).thenReturn(new ConsultaDto());

        consultaService.novaConsultaEnviament(104L, consulta);

        verify(integracioHelper).addAccioError(eq("PET010"), any(), any(), any(), any(), anyLong(), any(), any());
    }

    // ===================== novaConsultaEstat =====================

    @Test
    public void novaConsultaEstat_consultaNoTrobada_llancaException() {
        when(consultaRepository.findById(200L)).thenReturn(Optional.empty());

        assertThrows(ConsultaNotFoundException.class, () -> consultaService.novaConsultaEstat(200L));
    }

    @Test
    public void novaConsultaEstat_pendent_noRefrescaEstat() throws Exception {
        ProcedimentServei ps = crearProcedimentServeiActiu("SV004");
        Consulta existent = Consulta.getBuilder(
                        "PET011", "Joan Fuster", "12345678A", null, "87654321B",
                        "Maria", "Ramis", null, null, "Departament TIC",
                        ps, "Tramitació expedient", ConsultaDto.Consentiment.Si, null, false, false, null)
                .build();
        when(consultaRepository.findById(201L)).thenReturn(Optional.of(existent));
        ConsultaDto dtoResultat = new ConsultaDto();
        when(mapperFacade.map(existent, ConsultaDto.class)).thenReturn(dtoResultat);

        ConsultaDto resposta = consultaService.novaConsultaEstat(201L);

        assertSame(dtoResultat, resposta);
        verify(scspHelper, times(0)).recuperarResultatEnviamentPeticio(any());
        verify(integracioHelper).addAccioOk(eq("PET011"), any(), any(), any(), any(), anyLong());
    }

    @Test
    public void novaConsultaEstat_processant_refrescaEstat() throws Exception {
        ProcedimentServei ps = crearProcedimentServeiActiu("SV004");
        Consulta existent = Consulta.getBuilder(
                        "PET012", "Joan Fuster", "12345678A", null, "87654321B",
                        "Maria", "Ramis", null, null, "Departament TIC",
                        ps, "Tramitació expedient", ConsultaDto.Consentiment.Si, null, false, false, null)
                .build();
        existent.updateEstat(es.caib.pinbal.logic.intf.dto.EstatTipus.Processant);
        when(consultaRepository.findById(202L)).thenReturn(Optional.of(existent));
        ResultatEnviamentPeticio resultat = crearResultatOk();
        when(scspHelper.recuperarResultatEnviamentPeticio("PET012")).thenReturn(resultat);
        ConsultaDto dtoResultat = new ConsultaDto();
        when(mapperFacade.map(existent, ConsultaDto.class)).thenReturn(dtoResultat);

        ConsultaDto resposta = consultaService.novaConsultaEstat(202L);

        assertSame(dtoResultat, resposta);
        verify(consultaHelper).propagaCanviConsulta(existent);
        verify(peticioScspHelper).updateEstatConsulta(eq(existent), eq(resultat), any());
    }

    @Test
    public void novaConsultaEstat_errorAlRecuperarResultat_llancaConsultaScspEstatException() throws Exception {
        ProcedimentServei ps = crearProcedimentServeiActiu("SV004");
        Consulta existent = Consulta.getBuilder(
                        "PET013", "Joan Fuster", "12345678A", null, "87654321B",
                        "Maria", "Ramis", null, null, "Departament TIC",
                        ps, "Tramitació expedient", ConsultaDto.Consentiment.Si, null, false, false, null)
                .build();
        existent.updateEstat(es.caib.pinbal.logic.intf.dto.EstatTipus.Processant);
        when(consultaRepository.findById(203L)).thenReturn(Optional.of(existent));
        when(scspHelper.recuperarResultatEnviamentPeticio("PET013")).thenThrow(new RuntimeException("Comunicació fallida"));

        assertThrows(ConsultaScspEstatException.class, () -> consultaService.novaConsultaEstat(203L));

        verify(integracioHelper).addAccioError(eq("PET013"), any(), any(), any(), any(), anyLong(), any(), any());
    }

    // ===================== novaConsultaMultiple =====================

    @Test
    public void novaConsultaMultiple_procedimentServeiNoTrobat_llancaException() {
        ConsultaDto consulta = crearConsultaDtoPeticio(4L, "SV005");
        when(procedimentServeiRepository.findByProcedimentIdAndServei(4L, "SV005")).thenReturn(null);

        assertThrows(ProcedimentServeiNotFoundException.class, () -> consultaService.novaConsultaMultiple(consulta));
    }

    @Test
    public void novaConsultaMultiple_serveiNoPermes_llancaException() {
        ConsultaDto consulta = crearConsultaDtoPeticio(4L, "SV005");
        ProcedimentServei ps = crearProcedimentServeiActiu("SV005");
        when(procedimentServeiRepository.findByProcedimentIdAndServei(4L, "SV005")).thenReturn(ps);
        when(serveiHelper.isServeiPermesPerUsuari(any(Entitat.class), any(Procediment.class), eq("SV005"))).thenReturn(false);

        assertThrows(ServeiNotAllowedException.class, () -> consultaService.novaConsultaMultiple(consulta));
    }

    @Test
    public void novaConsultaMultiple_ok_desaConsultesFilles() throws Exception {
        ConsultaDto consulta = crearConsultaDtoPeticio(4L, "SV005");
        consulta.setFuncionariNom("Joan Fuster");
        consulta.setFuncionariNif("12345678A");
        ProcedimentServei ps = crearProcedimentServeiActiu("SV005");
        when(procedimentServeiRepository.findByProcedimentIdAndServei(4L, "SV005")).thenReturn(ps);
        when(serveiHelper.isServeiPermesPerUsuari(any(Entitat.class), any(Procediment.class), eq("SV005"))).thenReturn(true);
        when(scspHelper.generarIdPeticion("SV005")).thenReturn("PET014");

        Solicitud solicitud1 = new Solicitud();
        solicitud1.setTitularDocument("11111111A");
        solicitud1.setTitularNom("Pere");
        solicitud1.setConsentiment(es.caib.pinbal.scsp.Consentiment.Si);
        when(peticioScspHelper.convertirEnMultiplesSolicituds(eq(consulta), eq(ps))).thenReturn(java.util.Collections.singletonList(solicitud1));

        ResultatEnviamentPeticio resultat = crearResultatOk();
        resultat.setIdsSolicituds(new String[] {"SOL-FILL-1"});
        when(peticioScspHelper.enviarPeticioScsp(any(Consulta.class), any(), eq(false), anyBoolean(), eq(scspHelper))).thenReturn(resultat);
        when(consultaRepository.save(any(Consulta.class))).thenAnswer(inv -> inv.getArgument(0));

        ConsultaDto dtoResultat = new ConsultaDto();
        dtoResultat.setId(40L);
        when(mapperFacade.map(any(Consulta.class), eq(ConsultaDto.class))).thenReturn(dtoResultat);

        ConsultaDto resposta = consultaService.novaConsultaMultiple(consulta);

        assertSame(dtoResultat, resposta);
        verify(consultaRepository, times(2)).save(any(Consulta.class));
        verify(consultaHelper, times(2)).propagaCreacioConsulta(any(Consulta.class));
    }

    @Test
    public void novaConsultaMultiple_errorGeneracioSolicituds_propagaException() throws Exception {
        ConsultaDto consulta = crearConsultaDtoPeticio(4L, "SV005");
        ProcedimentServei ps = crearProcedimentServeiActiu("SV005");
        when(procedimentServeiRepository.findByProcedimentIdAndServei(4L, "SV005")).thenReturn(ps);
        when(serveiHelper.isServeiPermesPerUsuari(any(Entitat.class), any(Procediment.class), eq("SV005"))).thenReturn(true);
        when(scspHelper.generarIdPeticion("SV005")).thenReturn("PET015");
        when(peticioScspHelper.convertirEnMultiplesSolicituds(eq(consulta), eq(ps)))
                .thenThrow(new ConsultaScspGeneracioException("No s'han pogut generar les sol·licituds"));

        assertThrows(ConsultaScspGeneracioException.class, () -> consultaService.novaConsultaMultiple(consulta));
    }

    @Test
    public void novaConsultaMultiple_errorComunicacio_retornaDtoAmbError() throws Exception {
        ConsultaDto consulta = crearConsultaDtoPeticio(4L, "SV005");
        ProcedimentServei ps = crearProcedimentServeiActiu("SV005");
        when(procedimentServeiRepository.findByProcedimentIdAndServei(4L, "SV005")).thenReturn(ps);
        when(serveiHelper.isServeiPermesPerUsuari(any(Entitat.class), any(Procediment.class), eq("SV005"))).thenReturn(true);
        when(scspHelper.generarIdPeticion("SV005")).thenReturn("PET016");
        when(peticioScspHelper.convertirEnMultiplesSolicituds(eq(consulta), eq(ps))).thenReturn(java.util.Collections.emptyList());
        when(peticioScspHelper.enviarPeticioScsp(any(Consulta.class), any(), eq(false), anyBoolean(), eq(scspHelper)))
                .thenThrow(new ConsultaScspComunicacioException("PET016", "Error de comunicació múltiple"));
        when(consultaRepository.save(any(Consulta.class))).thenAnswer(inv -> inv.getArgument(0));
        when(usuariHelper.getUsuariAutenticat()).thenReturn(null);

        ConsultaDto dtoResultat = new ConsultaDto();
        when(mapperFacade.map(any(Consulta.class), eq(ConsultaDto.class))).thenReturn(dtoResultat);

        ConsultaDto resposta = consultaService.novaConsultaMultiple(consulta);

        assertEquals("ERROR", resposta.getRespostaEstadoCodigo());
        assertEquals("Error de comunicació múltiple", resposta.getRespostaEstadoError());
    }

    // ===================== peticioSincrona =====================

    @Test
    public void peticioSincrona_optimitzacioActiva_delegaEnSelfIEsExit() throws Exception {
        ConsultaDto consulta = crearConsultaDtoPeticio(5L, "SV006");
        when(configHelper.getConfigAsBoolean(eq("es.caib.pinbal.optimitzar.transaccions.nova.consulta"), eq(false))).thenReturn(true);

        ConsultaDto consultaInit = new ConsultaDto();
        consultaInit.setId(50L);
        when(self.novaConsultaInit(consulta)).thenReturn(consultaInit);

        ConsultaDto consultaEstat = new ConsultaDto();
        consultaEstat.setEstat("Tramitada");
        when(self.novaConsultaEstat(50L)).thenReturn(consultaEstat);

        ConsultaDto resposta = consultaService.peticioSincrona(consulta);

        assertSame(consultaEstat, resposta);
        assertFalse(resposta.isEstatError());
        verify(self).novaConsultaEnviament(50L, consulta);
        verify(self, times(0)).novaConsulta(any());
    }

    @Test
    public void peticioSincrona_optimitzacioInactiva_delegaEnSelfNovaConsulta() throws Exception {
        ConsultaDto consulta = crearConsultaDtoPeticio(5L, "SV006");
        when(configHelper.getConfigAsBoolean(eq("es.caib.pinbal.optimitzar.transaccions.nova.consulta"), eq(false))).thenReturn(false);

        ConsultaDto dtoResultat = new ConsultaDto();
        dtoResultat.setEstat("Tramitada");
        when(self.novaConsulta(consulta)).thenReturn(dtoResultat);

        ConsultaDto resposta = consultaService.peticioSincrona(consulta);

        assertSame(dtoResultat, resposta);
        verify(self, times(0)).novaConsultaInit(any());
    }

    @Test
    public void peticioSincrona_respostaAmbEstatError_noLlancaException() throws Exception {
        ConsultaDto consulta = crearConsultaDtoPeticio(5L, "SV006");
        when(configHelper.getConfigAsBoolean(eq("es.caib.pinbal.optimitzar.transaccions.nova.consulta"), eq(false))).thenReturn(false);

        ConsultaDto dtoResultat = new ConsultaDto();
        dtoResultat.setEstat("Error");
        when(self.novaConsulta(consulta)).thenReturn(dtoResultat);

        ConsultaDto resposta = consultaService.peticioSincrona(consulta);

        assertSame(dtoResultat, resposta);
        assertTrue(resposta.isEstatError());
    }

    @Test
    public void peticioSincrona_propagaExceptionDeSelf() throws Exception {
        ConsultaDto consulta = crearConsultaDtoPeticio(5L, "SV006");
        when(configHelper.getConfigAsBoolean(eq("es.caib.pinbal.optimitzar.transaccions.nova.consulta"), eq(false))).thenReturn(false);
        when(self.novaConsulta(consulta)).thenThrow(new ServeiNotAllowedException());

        assertThrows(ServeiNotAllowedException.class, () -> consultaService.peticioSincrona(consulta));
    }

    // ===================== peticioAsincrona =====================

    @Test
    public void peticioAsincrona_delegaEnSelfNovaConsultaMultiple_exit() throws Exception {
        ConsultaDto consulta = crearConsultaDtoPeticio(6L, "SV007");
        ConsultaDto dtoResultat = new ConsultaDto();
        dtoResultat.setEstat("Pendent");
        when(self.novaConsultaMultiple(consulta)).thenReturn(dtoResultat);

        ConsultaDto resposta = consultaService.peticioAsincrona(consulta);

        assertSame(dtoResultat, resposta);
        assertNotNull(resposta);
    }

    @Test
    public void peticioAsincrona_respostaAmbEstatError() throws Exception {
        ConsultaDto consulta = crearConsultaDtoPeticio(6L, "SV007");
        ConsultaDto dtoResultat = new ConsultaDto();
        dtoResultat.setEstat("Error");
        when(self.novaConsultaMultiple(consulta)).thenReturn(dtoResultat);

        ConsultaDto resposta = consultaService.peticioAsincrona(consulta);

        assertTrue(resposta.isEstatError());
    }

    @Test
    public void peticioAsincrona_propagaValidacioDadesPeticioException() throws Exception {
        ConsultaDto consulta = crearConsultaDtoPeticio(6L, "SV007");
        when(self.novaConsultaMultiple(consulta)).thenThrow(new ValidacioDadesPeticioException("Dades no vàlides"));

        assertThrows(ValidacioDadesPeticioException.class, () -> consultaService.peticioAsincrona(consulta));
    }
}
