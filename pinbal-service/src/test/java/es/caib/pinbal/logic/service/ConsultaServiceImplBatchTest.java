package es.caib.pinbal.logic.service;

import es.caib.pinbal.logic.helper.ConfigHelper;
import es.caib.pinbal.logic.helper.ConsultaHelper;
import es.caib.pinbal.logic.helper.DtoMappingHelper;
import es.caib.pinbal.logic.helper.EmailReportEstatHelper;
import es.caib.pinbal.logic.helper.ExcelHelper;
import es.caib.pinbal.logic.helper.IntegracioHelper;
import es.caib.pinbal.logic.helper.LoggerHelper;
import es.caib.pinbal.logic.helper.PeticioScspEstadistiquesHelper;
import es.caib.pinbal.logic.helper.PeticioScspHelper;
import es.caib.pinbal.logic.helper.PluginHelper;
import es.caib.pinbal.logic.helper.ServeiHelper;
import es.caib.pinbal.logic.helper.UsuariHelper;
import es.caib.pinbal.logic.helper.mock.JustificantHelperFactory;
import es.caib.pinbal.logic.intf.dto.CarregaDto;
import es.caib.pinbal.logic.intf.dto.ConsultaDto;
import es.caib.pinbal.logic.intf.dto.EntitatDto;
import es.caib.pinbal.logic.intf.dto.EstadisticaDto;
import es.caib.pinbal.logic.intf.dto.EstadistiquesFiltreDto;
import es.caib.pinbal.logic.intf.dto.EstadistiquesFiltreDto.EstadistiquesAgrupacioDto;
import es.caib.pinbal.logic.intf.dto.EstatTipus;
import es.caib.pinbal.logic.intf.dto.JustificantEstat;
import es.caib.pinbal.logic.intf.service.ConsultaService;
import es.caib.pinbal.logic.intf.service.exception.ConsultaNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.ConsultaScspEstatException;
import es.caib.pinbal.logic.intf.service.exception.EntitatNotFoundException;
import es.caib.pinbal.persist.entity.Consulta;
import es.caib.pinbal.persist.entity.Entitat;
import es.caib.pinbal.persist.entity.Procediment;
import es.caib.pinbal.persist.entity.ProcedimentServei;
import es.caib.pinbal.persist.entity.Servei;
import es.caib.pinbal.persist.entity.Usuari;
import es.caib.pinbal.persist.entity.explotacio.ExplotConsultaDimensio;
import es.caib.pinbal.persist.entity.explotacio.ExplotConsultaDimensioEntity;
import es.caib.pinbal.persist.entity.explotacio.ExplotConsultaFets;
import es.caib.pinbal.persist.entity.explotacio.ExplotConsultaFetsEntity;
import es.caib.pinbal.persist.entity.explotacio.ExplotTempsEntity;
import es.caib.pinbal.persist.repository.ConsultaRepository;
import es.caib.pinbal.persist.repository.EntitatRepository;
import es.caib.pinbal.persist.repository.EntitatUsuariRepository;
import es.caib.pinbal.persist.repository.ProcedimentRepository;
import es.caib.pinbal.persist.repository.ProcedimentServeiRepository;
import es.caib.pinbal.persist.repository.ServeiJustificantCampRepository;
import es.caib.pinbal.persist.repository.ServeiRepository;
import es.caib.pinbal.persist.repository.SuperConsultaRepository;
import es.caib.pinbal.persist.repository.TokenRepository;
import es.caib.pinbal.persist.repository.UsuariRepository;
import es.caib.pinbal.persist.repository.dadesobertes.DadesObertesConsultaRepository;
import es.caib.pinbal.persist.repository.explotacio.ExplotConsultaDimensioRepository;
import es.caib.pinbal.persist.repository.explotacio.ExplotConsultaFetsRepository;
import es.caib.pinbal.persist.repository.explotacio.ExplotTempsRepository;
import es.caib.pinbal.persist.repository.llistat.LlistatConsultaRepository;
import es.caib.pinbal.plugin.SistemaExternException;
import es.caib.pinbal.scsp.ResultatEnviamentPeticio;
import es.caib.pinbal.scsp.ScspHelper;
import es.caib.pluginsib.arxiu.api.Expedient;
import es.caib.pluginsib.arxiu.api.ExpedientEstat;
import es.caib.pluginsib.arxiu.api.ExpedientMetadades;
import ma.glasnost.orika.MapperFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class ConsultaServiceImplBatchTest {

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
    @Mock private TransactionStatus transactionStatus;
    @Mock private ApplicationContext applicationContext;

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
        ReflectionTestUtils.setField(consultaService, "applicationContext", applicationContext);
        // new TransactionTemplate(transactionManager).execute(...) crida transactionManager.getTransaction(...)
        // internament; sense aquest stub el TransactionStatus seria null i petaria als callbacks que l'usen.
        when(transactionManager.getTransaction(any())).thenReturn(transactionStatus);
        auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("usuari1");
        when(auth.getAuthorities()).thenReturn(Collections.emptyList());
        securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
    }

    private ConsultaDto dtoAmbEstat(String estat) {
        ConsultaDto dto = new ConsultaDto();
        dto.setEstat(estat);
        return dto;
    }

    // ===================== findAmbPare =====================

    @Test
    public void findAmbPare_pareNoTrobat_llancaException() {
        when(consultaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ConsultaNotFoundException.class, () -> consultaService.findAmbPare(99L));
    }

    @Test
    public void findAmbPare_ok_retornaFilles() throws Exception {
        Consulta pare = mock(Consulta.class);
        Consulta filla = mock(Consulta.class);
        when(consultaRepository.findById(1L)).thenReturn(Optional.of(pare));
        when(consultaRepository.findByPareOrderByScspSolicitudIdAsc(pare)).thenReturn(Collections.singletonList(filla));
        when(mapperFacade.map(any(Consulta.class), eq(ConsultaDto.class))).thenReturn(dtoAmbEstat("Tramitada"));

        List<ConsultaDto> resposta = consultaService.findAmbPare(1L);

        assertEquals(1, resposta.size());
    }

    // ===================== countConsultesMultiplesProcessant =====================

    @Test
    public void countConsultesMultiplesProcessant_retornaComptador() throws Exception {
        Usuari usuari = mock(Usuari.class);
        when(usuariRepository.findById("usuari1")).thenReturn(Optional.of(usuari));
        when(consultaRepository.countByEstatAndCreatedByAndMultipleTrue(EstatTipus.Processant, usuari)).thenReturn(3L);

        long resultat = consultaService.countConsultesMultiplesProcessant(10L);

        assertEquals(3L, resultat);
    }

    // ===================== findEstadistiquesByFiltre =====================

    @Test
    public void findEstadistiquesByFiltre_entitatNoTrobada_llancaException() {
        EstadistiquesFiltreDto filtre = new EstadistiquesFiltreDto();
        filtre.setEntitatId(5L);
        when(entitatRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(EntitatNotFoundException.class, () -> consultaService.findEstadistiquesByFiltre(filtre));
    }

    @Test
    public void findEstadistiquesByFiltre_tempsFinalNoExisteix_generaDadesIRetornaBuit() throws Exception {
        EstadistiquesFiltreDto filtre = new EstadistiquesFiltreDto();
        when(explotTempsRepository.findFirstByData(any())).thenReturn(null);

        List<EstadisticaDto> resposta = consultaService.findEstadistiquesByFiltre(filtre);

        assertTrue(resposta.isEmpty());
        verify(self, atLeastOnce()).generarDadesExplotacio(any());
    }

    @Test
    public void findEstadistiquesByFiltre_fetsBuits_retornaBuit() throws Exception {
        EstadistiquesFiltreDto filtre = new EstadistiquesFiltreDto();
        ExplotTempsEntity temps = new ExplotTempsEntity(new java.util.Date());
        when(explotTempsRepository.findFirstByData(any())).thenReturn(temps);
        when(explotConsultaFetsRepository.findByFiltre(any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any()))
                .thenReturn(Collections.emptyList());

        List<EstadisticaDto> resposta = consultaService.findEstadistiquesByFiltre(filtre);

        assertTrue(resposta.isEmpty());
    }

    @Test
    public void findEstadistiquesByFiltre_ambFets_ordenaISumaEstadistiques() throws Exception {
        EstadistiquesFiltreDto filtre = new EstadistiquesFiltreDto();
        filtre.setAgrupacio(EstadistiquesAgrupacioDto.PROCEDIMENT_SERVEI);
        ExplotTempsEntity temps = new ExplotTempsEntity(new java.util.Date());
        when(explotTempsRepository.findFirstByData(any())).thenReturn(temps);

        ExplotConsultaFets fet1 = ExplotConsultaFets.builder()
                .entitatId(1L).entitatCodi("E1").procedimentId(10L).procedimentCodi("P10")
                .serveiCodi("SV_A").usuariCodi("U1")
                .recOk(2).recError(1).webOk(3).webError(0)
                .recMassOk(0).recMassError(0).webMassOk(0).webMassError(0)
                .build();
        ExplotConsultaFets fet2 = ExplotConsultaFets.builder()
                .entitatId(1L).entitatCodi("E1").procedimentId(11L).procedimentCodi("P11")
                .serveiCodi("SV_B").usuariCodi("U1")
                .recOk(5).recError(0).webOk(1).webError(1)
                .recMassOk(0).recMassError(0).webMassOk(0).webMassError(0)
                .build();
        when(explotConsultaFetsRepository.findByFiltre(any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any()))
                .thenReturn(Arrays.asList(fet1, fet2));

        Procediment procediment10 = mock(Procediment.class);
        when(procediment10.getNom()).thenReturn("Procediment Deu");
        when(procediment10.getCodi()).thenReturn("P10");
        Procediment procediment11 = mock(Procediment.class);
        when(procediment11.getNom()).thenReturn("Procediment Onze");
        when(procediment11.getCodi()).thenReturn("P11");
        when(procedimentRepository.findById(10L)).thenReturn(Optional.of(procediment10));
        when(procedimentRepository.findById(11L)).thenReturn(Optional.of(procediment11));

        Servei serveiA = mock(Servei.class);
        when(serveiA.getDescripcio()).thenReturn("Servei A");
        Servei serveiB = mock(Servei.class);
        when(serveiB.getDescripcio()).thenReturn("Servei B");
        when(serveiRepository.findByCode("SV_A")).thenReturn(Collections.singletonList(serveiA));
        when(serveiRepository.findByCode("SV_B")).thenReturn(Collections.singletonList(serveiB));

        List<EstadisticaDto> resposta = consultaService.findEstadistiquesByFiltre(filtre);

        assertEquals(2, resposta.size());
        assertTrue(resposta.stream().allMatch(EstadisticaDto::isConteSumatori));
    }

    @Test
    public void findEstadistiquesByFiltre_procedimentNoTrobat_esFiltra() throws Exception {
        EstadistiquesFiltreDto filtre = new EstadistiquesFiltreDto();
        ExplotTempsEntity temps = new ExplotTempsEntity(new java.util.Date());
        when(explotTempsRepository.findFirstByData(any())).thenReturn(temps);

        ExplotConsultaFets fet = ExplotConsultaFets.builder()
                .entitatId(1L).entitatCodi("E1").procedimentId(99L).procedimentCodi("P99")
                .serveiCodi("SV_X").usuariCodi("U1")
                .build();
        when(explotConsultaFetsRepository.findByFiltre(any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any()))
                .thenReturn(Collections.singletonList(fet));
        when(procedimentRepository.findById(99L)).thenReturn(Optional.empty());

        List<EstadisticaDto> resposta = consultaService.findEstadistiquesByFiltre(filtre);

        assertTrue(resposta.isEmpty());
    }

    @Test
    public void findEstadistiquesByFiltre_ambDataInici_restaFetsAnteriors() throws Exception {
        EstadistiquesFiltreDto filtre = new EstadistiquesFiltreDto();
        java.util.Date dataFi = new java.util.Date();
        java.util.Date dataInici = new java.util.Date(dataFi.getTime() - 86400000L);
        filtre.setDataFi(dataFi);
        filtre.setDataInici(dataInici);
        ExplotTempsEntity tempsFinal = new ExplotTempsEntity(dataFi);
        ExplotTempsEntity tempsInicial = new ExplotTempsEntity(dataInici);
        when(explotTempsRepository.findFirstByData(any()))
                .thenReturn(tempsFinal)
                .thenReturn(tempsInicial);

        ExplotConsultaFets fetFinal = ExplotConsultaFets.builder()
                .entitatId(1L).entitatCodi("E1").procedimentId(10L).procedimentCodi("P10")
                .serveiCodi("SV_A").usuariCodi("U1")
                .recOk(10).webOk(10)
                .build();
        ExplotConsultaFets fetInicial = ExplotConsultaFets.builder()
                .entitatId(1L).entitatCodi("E1").procedimentId(10L).procedimentCodi("P10")
                .serveiCodi("SV_A").usuariCodi("U1")
                .recOk(4).webOk(1)
                .build();
        when(explotConsultaFetsRepository.findByFiltre(eq(tempsFinal), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any()))
                .thenReturn(Collections.singletonList(fetFinal));
        when(explotConsultaFetsRepository.findByFiltre(eq(tempsInicial), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any()))
                .thenReturn(Collections.singletonList(fetInicial));

        Procediment procediment = mock(Procediment.class);
        when(procediment.getNom()).thenReturn("Procediment Deu");
        when(procediment.getCodi()).thenReturn("P10");
        when(procedimentRepository.findById(10L)).thenReturn(Optional.of(procediment));
        Servei servei = mock(Servei.class);
        when(servei.getDescripcio()).thenReturn("Servei A");
        when(serveiRepository.findByCode("SV_A")).thenReturn(Collections.singletonList(servei));

        List<EstadisticaDto> resposta = consultaService.findEstadistiquesByFiltre(filtre);

        assertEquals(1, resposta.size());
        assertEquals(6, resposta.get(0).getNumRecobrimentOk());
        assertEquals(9, resposta.get(0).getNumWebUIOk());
    }

    // ===================== findEstadistiquesGlobalsByFiltre =====================

    @Test
    public void findEstadistiquesGlobalsByFiltre_senseEntitats_nomesGlobal() {
        EstadistiquesFiltreDto filtre = new EstadistiquesFiltreDto();
        when(consultaRepository.countByEntitat(anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any()))
                .thenReturn(Collections.emptyList());
        when(explotTempsRepository.findFirstByData(any())).thenReturn(null);

        java.util.Map<EntitatDto, List<EstadisticaDto>> resposta = consultaService.findEstadistiquesGlobalsByFiltre(filtre);

        assertEquals(1, resposta.size());
        assertTrue(resposta.containsKey(null));
    }

    @Test
    public void findEstadistiquesGlobalsByFiltre_ambEntitatTrobada_afegeixEntrada() {
        EstadistiquesFiltreDto filtre = new EstadistiquesFiltreDto();
        Object[] fila = new Object[] {1L, 5L};
        when(consultaRepository.countByEntitat(anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any()))
                .thenReturn(Collections.singletonList(fila));
        Entitat entitat = mock(Entitat.class);
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitat));
        EntitatDto entitatDto = EntitatDto.builder().id(1L).build();
        when(mapperFacade.map(entitat, EntitatDto.class)).thenReturn(entitatDto);
        when(explotTempsRepository.findFirstByData(any())).thenReturn(null);

        java.util.Map<EntitatDto, List<EstadisticaDto>> resposta = consultaService.findEstadistiquesGlobalsByFiltre(filtre);

        assertEquals(2, resposta.size());
        assertTrue(resposta.containsKey(entitatDto));
        assertTrue(resposta.containsKey(null));
    }

    @Test
    public void findEstadistiquesGlobalsByFiltre_entitatNoTrobada_saltaIContinua() {
        EstadistiquesFiltreDto filtre = new EstadistiquesFiltreDto();
        Object[] fila = new Object[] {99L, 3L};
        when(consultaRepository.countByEntitat(anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any()))
                .thenReturn(Collections.singletonList(fila));
        when(entitatRepository.findById(99L)).thenReturn(Optional.empty());
        when(explotTempsRepository.findFirstByData(any())).thenReturn(null);

        java.util.Map<EntitatDto, List<EstadisticaDto>> resposta = consultaService.findEstadistiquesGlobalsByFiltre(filtre);

        // Nomes hi ha l'entrada global, l'entitat inexistent es descarta amb un log d'error
        assertEquals(1, resposta.size());
        assertTrue(resposta.containsKey(null));
    }

    // ===================== findEstadistiquesCarrega =====================

    @Test
    public void findEstadistiquesCarrega_delegaEnHelper() {
        List<CarregaDto> carregues = Collections.singletonList(mock(CarregaDto.class));
        when(peticioScspEstadistiquesHelper.consultaEstadistiques()).thenReturn(carregues);

        List<CarregaDto> resposta = consultaService.findEstadistiquesCarrega();

        assertSame(carregues, resposta);
    }

    // ===================== auditoriaGenerarAuditor =====================

    @Test
    public void auditoriaGenerarAuditor_entitatNoTrobada_llancaException() {
        when(entitatRepository.findById(7L)).thenReturn(Optional.empty());

        assertThrows(EntitatNotFoundException.class,
                () -> consultaService.auditoriaGenerarAuditor(7L, new java.util.Date(), new java.util.Date(), 5));
    }

    @Test
    public void auditoriaGenerarAuditor_menysConsultesQueLimit_retornaTotes() throws Exception {
        Entitat entitat = mock(Entitat.class);
        when(entitatRepository.findById(7L)).thenReturn(Optional.of(entitat));
        Consulta c1 = mock(Consulta.class);
        when(c1.getId()).thenReturn(101L);
        Consulta c2 = mock(Consulta.class);
        when(c2.getId()).thenReturn(102L);
        when(consultaRepository.findByEntitatAndDataIniciAndDataFi(eq(entitat), any(), any())).thenReturn(Arrays.asList(c1, c2));

        List<Long> resposta = consultaService.auditoriaGenerarAuditor(7L, new java.util.Date(), new java.util.Date(), 5);

        assertEquals(Arrays.asList(101L, 102L), resposta);
    }

    @Test
    public void auditoriaGenerarAuditor_mesConsultesQueLimit_retornaSeleccioAleatoria() throws Exception {
        Entitat entitat = mock(Entitat.class);
        when(entitatRepository.findById(7L)).thenReturn(Optional.of(entitat));
        Consulta c1 = mock(Consulta.class);
        when(c1.getId()).thenReturn(101L);
        Consulta c2 = mock(Consulta.class);
        when(c2.getId()).thenReturn(102L);
        Consulta c3 = mock(Consulta.class);
        when(c3.getId()).thenReturn(103L);
        when(consultaRepository.findByEntitatAndDataIniciAndDataFi(eq(entitat), any(), any())).thenReturn(Arrays.asList(c1, c2, c3));

        List<Long> resposta = consultaService.auditoriaGenerarAuditor(7L, new java.util.Date(), new java.util.Date(), 1);

        assertEquals(1, resposta.size());
        assertTrue(Arrays.asList(101L, 102L, 103L).containsAll(resposta));
    }

    // ===================== auditoriaConsultarAuditor =====================

    @Test
    public void auditoriaConsultarAuditor_entitatNoTrobada_llancaException() {
        when(entitatRepository.findById(8L)).thenReturn(Optional.empty());

        assertThrows(EntitatNotFoundException.class,
                () -> consultaService.auditoriaConsultarAuditor(8L, Collections.singletonList(1L)));
    }

    @Test
    public void auditoriaConsultarAuditor_ok_retornaConsultes() throws Exception {
        Entitat entitat = mock(Entitat.class);
        when(entitatRepository.findById(8L)).thenReturn(Optional.of(entitat));
        Consulta c1 = mock(Consulta.class);
        when(consultaRepository.findByEntitatAndIds(eq(entitat), anyList())).thenReturn(Collections.singletonList(c1));
        when(mapperFacade.map(any(Consulta.class), eq(ConsultaDto.class))).thenReturn(dtoAmbEstat("Tramitada"));

        List<ConsultaDto> resposta = consultaService.auditoriaConsultarAuditor(8L, Collections.singletonList(1L));

        assertEquals(1, resposta.size());
    }

    // ===================== auditoriaGenerarSuperauditor =====================

    @Test
    public void auditoriaGenerarSuperauditor_filtraEntitatsSenseConsultesIGeneraAuditoria() {
        Entitat entitat1 = mock(Entitat.class);
        when(entitat1.getId()).thenReturn(1L);
        Entitat entitat2 = mock(Entitat.class);
        when(entitat2.getId()).thenReturn(2L);
        when(entitatRepository.findAll()).thenReturn(new ArrayList<>(Arrays.asList(entitat1, entitat2)));
        Object[] fila = new Object[] {1L, 4L};
        when(consultaRepository.countByEntitat(anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any()))
                .thenReturn(Collections.singletonList(fila));
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitat1));
        Consulta c1 = mock(Consulta.class);
        when(c1.getId()).thenReturn(201L);
        when(consultaRepository.findByEntitatAndDataIniciAndDataFi(eq(entitat1), any(), any())).thenReturn(Collections.singletonList(c1));

        List<Long> resposta = consultaService.auditoriaGenerarSuperauditor(new java.util.Date(), new java.util.Date(), 5, 5);

        assertEquals(Collections.singletonList(201L), resposta);
    }

    @Test
    public void auditoriaGenerarSuperauditor_seleccionaSubconjuntEntitats() {
        Entitat entitat1 = mock(Entitat.class);
        when(entitat1.getId()).thenReturn(1L);
        Entitat entitat2 = mock(Entitat.class);
        when(entitat2.getId()).thenReturn(2L);
        when(entitatRepository.findAll()).thenReturn(new ArrayList<>(Arrays.asList(entitat1, entitat2)));
        Object[] fila1 = new Object[] {1L, 4L};
        Object[] fila2 = new Object[] {2L, 4L};
        when(consultaRepository.countByEntitat(anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any()))
                .thenReturn(Arrays.asList(fila1, fila2));
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitat1));
        when(entitatRepository.findById(2L)).thenReturn(Optional.of(entitat2));
        Consulta cUnica = mock(Consulta.class);
        when(cUnica.getId()).thenReturn(301L);
        when(consultaRepository.findByEntitatAndDataIniciAndDataFi(any(Entitat.class), any(), any())).thenReturn(Collections.singletonList(cUnica));

        List<Long> resposta = consultaService.auditoriaGenerarSuperauditor(new java.util.Date(), new java.util.Date(), 1, 5);

        // Independentment de quina de les 2 entitats se selecciona a l'atzar, nomes en queda una amb 1 consulta
        assertEquals(1, resposta.size());
        assertEquals(301L, resposta.get(0));
    }

    // ===================== auditoriaConsultarSuperauditor =====================

    @Test
    public void auditoriaConsultarSuperauditor_agrupaPerEntitat() throws Exception {
        Consulta c1 = mock(Consulta.class);
        Consulta c2 = mock(Consulta.class);
        List<Object[]> files = Arrays.asList(
                new Object[] {c1, 1L},
                new Object[] {c2, 1L});
        when(consultaRepository.findByIds(Arrays.asList(10L, 20L))).thenReturn(files);
        Entitat entitat = mock(Entitat.class);
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitat));
        EntitatDto entitatDto = EntitatDto.builder().id(1L).build();
        when(mapperFacade.map(entitat, EntitatDto.class)).thenReturn(entitatDto);
        when(mapperFacade.map(any(Consulta.class), eq(ConsultaDto.class))).thenReturn(dtoAmbEstat("Tramitada"));

        java.util.Map<EntitatDto, List<ConsultaDto>> resposta = consultaService.auditoriaConsultarSuperauditor(Arrays.asList(10L, 20L));

        assertEquals(1, resposta.size());
        assertEquals(2, resposta.get(entitatDto).size());
    }

    // ===================== autoRevisarEstatPeticionsMultiplesPendents =====================

    @Test
    public void autoRevisarEstatPeticionsMultiplesPendents_errorObtenintLlistat_noLlancaException() {
        when(consultaRepository.findIdsAndScspPeticionIdsByEstatAndMultipleOrderByIdAsc(EstatTipus.Processant, true))
                .thenThrow(new RuntimeException("Error de bd"));

        assertDoesNotThrow(() -> consultaService.autoRevisarEstatPeticionsMultiplesPendents());
    }

    @Test
    public void autoRevisarEstatPeticionsMultiplesPendents_sensePendents_noFaRes() {
        when(consultaRepository.findIdsAndScspPeticionIdsByEstatAndMultipleOrderByIdAsc(EstatTipus.Processant, true))
                .thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> consultaService.autoRevisarEstatPeticionsMultiplesPendents());

        verify(consultaRepository, never()).saveAndFlush(any(Consulta.class));
    }

    @Test
    public void autoRevisarEstatPeticionsMultiplesPendents_unPendent_actualitzaEstat() throws Exception {
        Object[] fila = new Object[] {1L, "PET-001"};
        when(consultaRepository.findIdsAndScspPeticionIdsByEstatAndMultipleOrderByIdAsc(EstatTipus.Processant, true))
                .thenReturn(Collections.singletonList(fila));
        Consulta pendent = mock(Consulta.class);
        when(pendent.getEstat()).thenReturn(EstatTipus.Processant);
        when(pendent.isMultiple()).thenReturn(true);
        when(pendent.getFills()).thenReturn(Collections.emptyList());
        when(pendent.getScspPeticionId()).thenReturn("PET-001");
        when(consultaRepository.findById(1L)).thenReturn(Optional.of(pendent));
        ResultatEnviamentPeticio resultat = new ResultatEnviamentPeticio();
        when(scspHelper.recuperarResultatEnviamentPeticio("PET-001")).thenReturn(resultat);

        assertDoesNotThrow(() -> consultaService.autoRevisarEstatPeticionsMultiplesPendents());

        verify(consultaRepository).saveAndFlush(pendent);
        verify(peticioScspHelper).updateEstatConsulta(pendent, resultat, null);
    }

    @Test
    public void autoRevisarEstatPeticionsMultiplesPendents_errorNoRecuperable_esRegistraILoopContinua() throws Exception {
        Object[] fila = new Object[] {2L, "PET-002"};
        when(consultaRepository.findIdsAndScspPeticionIdsByEstatAndMultipleOrderByIdAsc(EstatTipus.Processant, true))
                .thenReturn(Collections.singletonList(fila));
        Consulta pendent = mock(Consulta.class);
        when(pendent.getEstat()).thenReturn(EstatTipus.Processant);
        when(pendent.isMultiple()).thenReturn(true);
        when(pendent.getScspPeticionId()).thenReturn("PET-002");
        when(consultaRepository.findById(2L)).thenReturn(Optional.of(pendent));
        when(scspHelper.recuperarResultatEnviamentPeticio("PET-002")).thenThrow(new RuntimeException("Error no recuperable"));

        assertDoesNotThrow(() -> consultaService.autoRevisarEstatPeticionsMultiplesPendents());

        verify(consultaRepository, never()).saveAndFlush(any(Consulta.class));
    }

    @Test
    public void autoRevisarEstatPeticionsMultiplesPendents_errorRecuperableExhaureixReintents_continuaSenseLlancar() throws Exception {
        Object[] fila = new Object[] {3L, "PET-003"};
        when(consultaRepository.findIdsAndScspPeticionIdsByEstatAndMultipleOrderByIdAsc(EstatTipus.Processant, true))
                .thenReturn(Collections.singletonList(fila));
        Consulta pendent = mock(Consulta.class);
        when(pendent.getEstat()).thenReturn(EstatTipus.Processant);
        when(pendent.isMultiple()).thenReturn(true);
        when(pendent.getScspPeticionId()).thenReturn("PET-003");
        when(consultaRepository.findById(3L)).thenReturn(Optional.of(pendent));
        // Error classificat com recuperable (conté "connection is closed"): es reintenta fins exhaurir intents
        when(scspHelper.recuperarResultatEnviamentPeticio("PET-003"))
                .thenThrow(new DataAccessResourceFailureException("connection is closed"));

        assertDoesNotThrow(() -> consultaService.autoRevisarEstatPeticionsMultiplesPendents());

        verify(consultaRepository, never()).saveAndFlush(any(Consulta.class));
    }

    // ===================== recuperarRespostaConsultaMultiple =====================

    @Test
    public void recuperarRespostaConsultaMultiple_consultaNoTrobada_llancaException() {
        when(consultaRepository.findById(50L)).thenReturn(Optional.empty());

        assertThrows(ConsultaNotFoundException.class, () -> consultaService.recuperarRespostaConsultaMultiple(50L));
    }

    @Test
    public void recuperarRespostaConsultaMultiple_noEsMultiple_llancaException() {
        Consulta consulta = mock(Consulta.class);
        when(consulta.isMultiple()).thenReturn(false);
        when(consultaRepository.findById(51L)).thenReturn(Optional.of(consulta));

        assertThrows(ConsultaNotFoundException.class, () -> consultaService.recuperarRespostaConsultaMultiple(51L));
    }

    @Test
    public void recuperarRespostaConsultaMultiple_noPertanyAUsuari_llancaException() {
        Consulta consulta = mock(Consulta.class);
        when(consulta.isMultiple()).thenReturn(true);
        Usuari creador = mock(Usuari.class);
        when(creador.getCodi()).thenReturn("altre_usuari");
        when(consulta.getCreatedBy()).thenReturn(Optional.of(creador));
        when(consultaRepository.findById(52L)).thenReturn(Optional.of(consulta));

        assertThrows(ConsultaNotFoundException.class, () -> consultaService.recuperarRespostaConsultaMultiple(52L));
    }

    @Test
    public void recuperarRespostaConsultaMultiple_noEstaProcessant_retornaSenseFerRes() throws Exception {
        Consulta consulta = mock(Consulta.class);
        when(consulta.isMultiple()).thenReturn(true);
        Usuari creador = mock(Usuari.class);
        when(creador.getCodi()).thenReturn("usuari1");
        when(consulta.getCreatedBy()).thenReturn(Optional.of(creador));
        when(consulta.getEstat()).thenReturn(EstatTipus.Tramitada);
        when(consultaRepository.findById(53L)).thenReturn(Optional.of(consulta));

        consultaService.recuperarRespostaConsultaMultiple(53L);

        verify(scspHelper, never()).recuperarResultatEnviamentPeticio(any());
    }

    @Test
    public void recuperarRespostaConsultaMultiple_administrador_recuperaEncaraNoSiguiSeuUsuari() throws Exception {
        Consulta consulta = mock(Consulta.class);
        when(consulta.isMultiple()).thenReturn(true);
        Usuari creador = mock(Usuari.class);
        when(creador.getCodi()).thenReturn("altre_usuari");
        when(consulta.getCreatedBy()).thenReturn(Optional.of(creador));
        when(consulta.getEstat()).thenReturn(EstatTipus.Processant);
        when(consulta.getScspPeticionId()).thenReturn("PET-054");
        when(consulta.getFills()).thenReturn(Collections.emptyList());
        when(consultaRepository.findById(54L)).thenReturn(Optional.of(consulta));
        GrantedAuthority admin = mock(GrantedAuthority.class);
        when(admin.getAuthority()).thenReturn("PBL_ADMIN");
        List<GrantedAuthority> authorities = Collections.singletonList(admin);
        doReturn(authorities).when(auth).getAuthorities();
        es.scsp.common.task.PollingTask pollingTask = mock(es.scsp.common.task.PollingTask.class);
        when(applicationContext.getBean("pollingTimerTask", es.scsp.common.task.PollingTask.class)).thenReturn(pollingTask);
        ResultatEnviamentPeticio resultat = new ResultatEnviamentPeticio();
        when(scspHelper.recuperarResultatEnviamentPeticio("PET-054")).thenReturn(resultat);

        consultaService.recuperarRespostaConsultaMultiple(54L);

        verify(pollingTask).processarPeticio("PET-054");
        verify(consultaRepository).saveAndFlush(consulta);
    }

    @Test
    public void recuperarRespostaConsultaMultiple_errorProcessant_llancaConsultaScspEstatException() throws Exception {
        Consulta consulta = mock(Consulta.class);
        when(consulta.isMultiple()).thenReturn(true);
        Usuari creador = mock(Usuari.class);
        when(creador.getCodi()).thenReturn("usuari1");
        when(consulta.getCreatedBy()).thenReturn(Optional.of(creador));
        when(consulta.getEstat()).thenReturn(EstatTipus.Processant);
        when(consulta.getScspPeticionId()).thenReturn("PET-055");
        when(consultaRepository.findById(55L)).thenReturn(Optional.of(consulta));
        es.scsp.common.task.PollingTask pollingTask = mock(es.scsp.common.task.PollingTask.class);
        when(applicationContext.getBean("pollingTimerTask", es.scsp.common.task.PollingTask.class)).thenReturn(pollingTask);
        doThrow(new RuntimeException("Error processant")).when(pollingTask).processarPeticio(any());

        assertThrows(ConsultaScspEstatException.class, () -> consultaService.recuperarRespostaConsultaMultiple(55L));
    }

    // ===================== autoGenerarJustificantsPendents =====================

    @Test
    public void autoGenerarJustificantsPendents_recobrimentActiu_sensePendents_noFaRes() {
        when(configHelper.getConfigAsBoolean("es.caib.pinbal.justificant.recobriment.generar", true)).thenReturn(true);
        when(consultaRepository.findByEstatAndJustificantEstatAndMultipleFalseAndArxiuExpedientTancatFalseOrderByIdAsc(EstatTipus.Tramitada, JustificantEstat.PENDENT))
                .thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> consultaService.autoGenerarJustificantsPendents());
    }

    @Test
    public void autoGenerarJustificantsPendents_recobrimentInactiu_usaAltreRepositori() {
        when(configHelper.getConfigAsBoolean("es.caib.pinbal.justificant.recobriment.generar", true)).thenReturn(false);
        when(consultaRepository.findByEstatAndJustificantEstatAndMultipleFalseAndArxiuExpedientTancatFalseAndRecobrimentFalseOrderByIdAsc(EstatTipus.Tramitada, JustificantEstat.PENDENT))
                .thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> consultaService.autoGenerarJustificantsPendents());

        verify(consultaRepository, never()).findByEstatAndJustificantEstatAndMultipleFalseAndArxiuExpedientTancatFalseOrderByIdAsc(any(), any());
    }

    @Test
    public void autoGenerarJustificantsPendents_errorGeneracio_esRegistraILoopContinua() {
        when(configHelper.getConfigAsBoolean("es.caib.pinbal.justificant.recobriment.generar", true)).thenReturn(true);
        Consulta pendent = mock(Consulta.class);
        // consulta.getEstat() no stubejat retorna null: obtenirJustificantComu llança JustificantGeneracioException immediatament
        when(pendent.getScspPeticionId()).thenReturn("PET-060");
        when(consultaRepository.findByEstatAndJustificantEstatAndMultipleFalseAndArxiuExpedientTancatFalseOrderByIdAsc(EstatTipus.Tramitada, JustificantEstat.PENDENT))
                .thenReturn(Collections.singletonList(pendent));

        assertDoesNotThrow(() -> consultaService.autoGenerarJustificantsPendents());
    }

    // ===================== autoTancarExpedientsPendents =====================

    @Test
    public void autoTancarExpedientsPendents_sensePendents_noFaRes() {
        when(consultaRepository.findByEstatAndJustificantEstatAndMultipleFalseAndArxiuExpedientTancatFalseOrderByIdAsc(EstatTipus.Tramitada, JustificantEstat.OK))
                .thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> consultaService.autoTancarExpedientsPendents());

        verify(pluginHelper, never()).isPluginArxiuActiu();
    }

    @Test
    public void autoTancarExpedientsPendents_noEsPotTancar_noCridaPlugin() {
        Consulta pendent = mock(Consulta.class);
        when(pendent.getPare()).thenReturn(null);
        when(pendent.isCustodiat()).thenReturn(false);
        when(consultaRepository.findByEstatAndJustificantEstatAndMultipleFalseAndArxiuExpedientTancatFalseOrderByIdAsc(EstatTipus.Tramitada, JustificantEstat.OK))
                .thenReturn(Collections.singletonList(pendent));

        assertDoesNotThrow(() -> consultaService.autoTancarExpedientsPendents());

        verify(pluginHelper, never()).isPluginArxiuActiu();
    }

    @Test
    public void autoTancarExpedientsPendents_pluginInactiu_noTancaExpedient() throws Exception {
        Consulta pendent = mock(Consulta.class);
        when(pendent.getPare()).thenReturn(null);
        when(pendent.isCustodiat()).thenReturn(true);
        when(consultaRepository.findByEstatAndJustificantEstatAndMultipleFalseAndArxiuExpedientTancatFalseOrderByIdAsc(EstatTipus.Tramitada, JustificantEstat.OK))
                .thenReturn(Collections.singletonList(pendent));
        when(pluginHelper.isPluginArxiuActiu()).thenReturn(false);

        assertDoesNotThrow(() -> consultaService.autoTancarExpedientsPendents());

        verify(pluginHelper, never()).arxiuExpedientConsultar(any(), any());
    }

    @Test
    public void autoTancarExpedientsPendents_consultaSimple_tancaExpedientObert() throws Exception {
        Consulta pendent = mock(Consulta.class);
        when(pendent.getPare()).thenReturn(null);
        when(pendent.isCustodiat()).thenReturn(true);
        when(pendent.getScspPeticionId()).thenReturn("PET-070");
        when(pendent.getArxiuExpedientUuid()).thenReturn("UUID-070");
        when(consultaRepository.findByEstatAndJustificantEstatAndMultipleFalseAndArxiuExpedientTancatFalseOrderByIdAsc(EstatTipus.Tramitada, JustificantEstat.OK))
                .thenReturn(Collections.singletonList(pendent));
        when(pluginHelper.isPluginArxiuActiu()).thenReturn(true);
        Expedient expedient = new Expedient();
        ExpedientMetadades metadades = new ExpedientMetadades();
        metadades.setEstat(ExpedientEstat.OBERT);
        expedient.setMetadades(metadades);
        when(pluginHelper.arxiuExpedientConsultar("PET-070", "UUID-070")).thenReturn(expedient);

        assertDoesNotThrow(() -> consultaService.autoTancarExpedientsPendents());

        verify(pluginHelper).arxiuExpedientTancar("PET-070", "UUID-070");
        verify(pendent).updateArxiuExpedientTancat(true);
        verify(consultaRepository).saveAndFlush(pendent);
    }

    @Test
    public void autoTancarExpedientsPendents_consultaMultiple_actualitzaTotsElsFills() throws Exception {
        Consulta pare = mock(Consulta.class);
        Consulta pendent = mock(Consulta.class);
        when(pendent.getPare()).thenReturn(pare);
        when(pendent.isCustodiat()).thenReturn(true);
        when(pendent.getScspPeticionId()).thenReturn("PET-071");
        when(pendent.getArxiuExpedientUuid()).thenReturn("UUID-071");
        when(consultaRepository.findByEstatAndJustificantEstatAndMultipleFalseAndArxiuExpedientTancatFalseOrderByIdAsc(EstatTipus.Tramitada, JustificantEstat.OK))
                .thenReturn(Collections.singletonList(pendent));
        when(consultaRepository.countByPare(pendent)).thenReturn(2);
        when(consultaRepository.countByPareAndCustodiat(pendent, true)).thenReturn(2);
        when(pluginHelper.isPluginArxiuActiu()).thenReturn(true);
        Expedient expedient = new Expedient();
        ExpedientMetadades metadades = new ExpedientMetadades();
        metadades.setEstat(ExpedientEstat.TANCAT);
        expedient.setMetadades(metadades);
        when(pluginHelper.arxiuExpedientConsultar("PET-071", "UUID-071")).thenReturn(expedient);
        Consulta fill1 = mock(Consulta.class);
        Consulta fill2 = mock(Consulta.class);
        when(pare.getFills()).thenReturn(Arrays.asList(fill1, fill2));

        assertDoesNotThrow(() -> consultaService.autoTancarExpedientsPendents());

        verify(pluginHelper, never()).arxiuExpedientTancar(any(), any());
        verify(fill1).updateArxiuExpedientTancat(true);
        verify(fill2).updateArxiuExpedientTancat(true);
        verify(consultaRepository).saveAndFlush(fill1);
        verify(consultaRepository).saveAndFlush(fill2);
    }

    @Test
    public void autoTancarExpedientsPendents_errorSistemaExtern_esRegistraILoopContinua() throws Exception {
        Consulta pendent = mock(Consulta.class);
        when(pendent.getPare()).thenReturn(null);
        when(pendent.isCustodiat()).thenReturn(true);
        when(pendent.getScspPeticionId()).thenReturn("PET-072");
        when(pendent.getArxiuExpedientUuid()).thenReturn("UUID-072");
        when(consultaRepository.findByEstatAndJustificantEstatAndMultipleFalseAndArxiuExpedientTancatFalseOrderByIdAsc(EstatTipus.Tramitada, JustificantEstat.OK))
                .thenReturn(Collections.singletonList(pendent));
        when(pluginHelper.isPluginArxiuActiu()).thenReturn(true);
        when(pluginHelper.arxiuExpedientConsultar("PET-072", "UUID-072")).thenThrow(new SistemaExternException("Error de connexió amb l'arxiu"));

        assertDoesNotThrow(() -> consultaService.autoTancarExpedientsPendents());

        verify(pendent, never()).updateArxiuExpedientTancat(anyBoolean());
    }

    // ===================== autoEnviarPeticionsPendents =====================

    @Test
    public void autoEnviarPeticionsPendents_sensePendents_noFaRes() {
        when(consultaRepository.findByEstatAndMultipleAndConsentimentNotNullOrderByIdAsc(EstatTipus.Pendent, false))
                .thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> consultaService.autoEnviarPeticionsPendents());

        verify(peticioScspHelper, never()).enviarPeticioScspPendent(any(), any());
    }

    @Test
    public void autoEnviarPeticionsPendents_noSHaDEnviar_noCridaScsp() {
        Consulta pendent = mock(Consulta.class);
        when(consultaRepository.findByEstatAndMultipleAndConsentimentNotNullOrderByIdAsc(EstatTipus.Pendent, false))
                .thenReturn(Collections.singletonList(pendent));
        when(peticioScspHelper.isEnviarConsultaServei(pendent, true)).thenReturn(false);

        assertDoesNotThrow(() -> consultaService.autoEnviarPeticionsPendents());

        verify(peticioScspHelper, never()).enviarPeticioScspPendent(any(), any());
    }

    @Test
    public void autoEnviarPeticionsPendents_enviamentOk() throws Exception {
        Consulta pendent = mock(Consulta.class);
        when(pendent.getId()).thenReturn(80L);
        when(consultaRepository.findByEstatAndMultipleAndConsentimentNotNullOrderByIdAsc(EstatTipus.Pendent, false))
                .thenReturn(Collections.singletonList(pendent));
        when(peticioScspHelper.isEnviarConsultaServei(pendent, true)).thenReturn(true);

        assertDoesNotThrow(() -> consultaService.autoEnviarPeticionsPendents());

        verify(peticioScspHelper).enviarPeticioScspPendent(80L, scspHelper);
        verify(consultaHelper, never()).processarErrorConsulta(any(), any(), any(), any());
    }

    @Test
    public void autoEnviarPeticionsPendents_errorEnviament_processaError() throws Exception {
        Consulta pendent = mock(Consulta.class);
        when(pendent.getId()).thenReturn(81L);
        when(pendent.getServeiCodi()).thenReturn("SV_ERR");
        when(consultaRepository.findByEstatAndMultipleAndConsentimentNotNullOrderByIdAsc(EstatTipus.Pendent, false))
                .thenReturn(Collections.singletonList(pendent));
        when(peticioScspHelper.isEnviarConsultaServei(pendent, true)).thenReturn(true);
        RuntimeException error = new RuntimeException("Error inesperat enviant");
        doThrow(error).when(peticioScspHelper).enviarPeticioScspPendent(81L, scspHelper);

        assertDoesNotThrow(() -> consultaService.autoEnviarPeticionsPendents());

        verify(consultaHelper).processarErrorConsulta(eq(81L), any(), anyLong(), eq(error));
    }

    // ===================== generarDadesExplotacio =====================

    @Test
    public void generarDadesExplotacio_senseArgument_delegaAmbAhir() {
        when(explotTempsRepository.findFirstByData(any())).thenReturn(new ExplotTempsEntity(new java.util.Date()));
        when(superConsultaRepository.getDimensionsPerEstadistiques()).thenReturn(Collections.emptyList());
        when(explotConsultaDimensioRepository.findAllOrdered()).thenReturn(Collections.emptyList());
        when(superConsultaRepository.getConsultesPerEstadistiques(anyBoolean(), any())).thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> consultaService.generarDadesExplotacio());

        verify(integracioHelper).addAccioOk(eq(IntegracioHelper.INTCODI_EXPLOTACIO), any(), any(), any(), anyLong());
    }

    @Test
    public void generarDadesExplotacio_tempsNoExisteix_elCrea() {
        when(explotTempsRepository.findFirstByData(any())).thenReturn(null);
        when(explotTempsRepository.save(any(ExplotTempsEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        when(superConsultaRepository.getDimensionsPerEstadistiques()).thenReturn(Collections.emptyList());
        when(explotConsultaDimensioRepository.findAllOrdered()).thenReturn(Collections.emptyList());
        when(superConsultaRepository.getConsultesPerEstadistiques(anyBoolean(), any())).thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> consultaService.generarDadesExplotacio(new java.util.Date()));

        verify(explotTempsRepository).save(any(ExplotTempsEntity.class));
        verify(integracioHelper).addAccioOk(eq(IntegracioHelper.INTCODI_EXPLOTACIO), any(), any(), any(), anyLong());
    }

    @Test
    public void generarDadesExplotacio_ambDimensioNovaIFetCoincident_desaFetsEntity() {
        ExplotTempsEntity temps = new ExplotTempsEntity(new java.util.Date());
        when(explotTempsRepository.findFirstByData(any())).thenReturn(temps);

        ExplotConsultaDimensio dimensio = new ExplotConsultaDimensio(1L, "E1", 10L, "P10", "SV_A", "U1");
        when(superConsultaRepository.getDimensionsPerEstadistiques()).thenReturn(Collections.singletonList(dimensio));
        when(explotConsultaDimensioRepository.findAllOrdered()).thenReturn(new ArrayList<>());
        ExplotConsultaDimensioEntity dimensioEntity = ExplotConsultaDimensioEntity.builder()
                .entitatId(1L).entitatCodi("E1").procedimentId(10L).procedimentCodi("P10")
                .serveiCodi("SV_A").usuariCodi("U1")
                .build();
        when(explotConsultaDimensioRepository.save(any(ExplotConsultaDimensioEntity.class))).thenReturn(dimensioEntity);

        ExplotConsultaFets fet = ExplotConsultaFets.builder()
                .entitatId(1L).procedimentId(10L).serveiCodi("SV_A").usuariCodi("U1")
                .recOk(1).webOk(1)
                .build();
        when(superConsultaRepository.getConsultesPerEstadistiques(anyBoolean(), any())).thenReturn(Collections.singletonList(fet));

        assertDoesNotThrow(() -> consultaService.generarDadesExplotacio(new java.util.Date()));

        verify(explotConsultaDimensioRepository).save(any(ExplotConsultaDimensioEntity.class));
        verify(explotConsultaFetsRepository).save(any(ExplotConsultaFetsEntity.class));
        verify(explotConsultaFetsRepository).deleteAllByTemps(temps);
    }

    @Test
    public void generarDadesExplotacio_errorInesperat_esRegistraErrorNoLlanca() {
        when(explotTempsRepository.findFirstByData(any())).thenThrow(new RuntimeException("Error de bd inesperat"));

        assertDoesNotThrow(() -> consultaService.generarDadesExplotacio(new java.util.Date()));

        verify(integracioHelper).addAccioError(eq(IntegracioHelper.INTCODI_EXPLOTACIO), any(), any(), any(), anyLong(), eq("ERROR"), any());
    }
}
