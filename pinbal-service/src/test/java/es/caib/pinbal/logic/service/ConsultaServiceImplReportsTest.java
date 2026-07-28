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
import es.caib.pinbal.logic.intf.dto.ArbreRespostaDto;
import es.caib.pinbal.logic.intf.dto.EstatTipus;
import es.caib.pinbal.logic.intf.dto.FitxerDto;
import es.caib.pinbal.logic.intf.dto.InformeGeneralEstatDto;
import es.caib.pinbal.logic.intf.dto.InformeProcedimentServeiDto;
import es.caib.pinbal.logic.intf.dto.InformeRepresentantFiltreDto;
import es.caib.pinbal.logic.intf.service.ConsultaService;
import es.caib.pinbal.logic.intf.service.exception.ConsultaNotFoundException;
import es.caib.pinbal.persist.entity.Consulta;
import es.caib.pinbal.persist.entity.Entitat;
import es.caib.pinbal.persist.entity.EntitatUsuari;
import es.caib.pinbal.persist.entity.OrganGestor;
import es.caib.pinbal.persist.entity.Procediment;
import es.caib.pinbal.persist.entity.ProcedimentServei;
import es.caib.pinbal.persist.entity.ScspToken;
import es.caib.pinbal.persist.entity.ScspTokenId;
import es.caib.pinbal.persist.entity.Usuari;
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
import es.caib.pinbal.plugin.usuari.DadesUsuari;
import es.caib.pinbal.scsp.JustificantArbreHelper.ElementArbre;
import es.caib.pinbal.scsp.ScspHelper;
import es.scsp.common.domain.core.EmisorCertificado;
import es.scsp.common.domain.core.Servicio;
import es.scsp.common.exceptions.ScspException;
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
import org.springframework.context.MessageSource;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.domain.Sort;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class ConsultaServiceImplReportsTest {

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
    @Mock private ApplicationContext applicationContext;
    @Mock private MessageSource messageSource;

    @InjectMocks
    private ConsultaServiceImpl consultaService;

    private Authentication auth;
    private SecurityContext securityContext;

    @BeforeEach
    public void setUp() {
        when(dtoMappingHelper.getMapperFacade()).thenReturn(mapperFacade);
        ReflectionTestUtils.setField(consultaService, "scspHelper", scspHelper);
        ReflectionTestUtils.setField(consultaService, "applicationContext", applicationContext);
        ReflectionTestUtils.setField(LoggerHelper.class, "INSTANCE", loggerHelper);
        auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("usuari1");
        when(auth.getAuthorities()).thenReturn(Collections.emptyList());
        securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
    }

    private Entitat crearEntitat(Long id, String codi, String nom, String cif) {
        Entitat entitat = Entitat.getBuilder(codi, nom, cif, Entitat.EntitatTipus.AJUNTAMENT).build();
        entitat.configurarIdPerTest(id);
        return entitat;
    }

    private Procediment crearProcediment(Long id, Entitat entitat, String codi, String nom, String departament, OrganGestor organGestor) {
        Procediment procediment = Procediment.getBuilder(entitat, codi, nom, departament, organGestor, null, null, null).build();
        procediment.configurarIdPerTest(id);
        return procediment;
    }

    private ProcedimentServei crearProcedimentServei(Long id, Procediment procediment, String serveiCodi) {
        ProcedimentServei ps = ProcedimentServei.getBuilder(procediment, serveiCodi).build();
        ps.configurarIdPerTest(id);
        return ps;
    }

    private Servicio crearServicio(String codi, String descripcio, EmisorCertificado emisor) {
        Servicio servicio = new Servicio();
        servicio.setCodCertificado(codi);
        servicio.setDescripcion(descripcio);
        servicio.setEmisor(emisor);
        return servicio;
    }

    private AccessControlEntry crearAce(String principal) {
        AccessControlEntry ace = mock(AccessControlEntry.class);
        when(ace.getSid()).thenReturn(new PrincipalSid(principal));
        return ace;
    }

    private void stubAclEntries(List<AccessControlEntry> aces) throws NotFoundException {
        MutableAcl acl = mock(MutableAcl.class);
        when(acl.getEntries()).thenReturn(aces);
        when(aclService.readAclById(any())).thenReturn(acl);
    }

    private void stubAclNotFound() throws NotFoundException {
        when(aclService.readAclById(any())).thenThrow(mock(NotFoundException.class));
    }

    @Test
    public void isOptimitzarTransaccionsNovaConsulta_configuratTrue_retornaTrue() {
        when(configHelper.getConfigAsBoolean("es.caib.pinbal.optimitzar.transaccions.nova.consulta", false)).thenReturn(true);

        assertTrue(consultaService.isOptimitzarTransaccionsNovaConsulta());
    }

    @Test
    public void isOptimitzarTransaccionsNovaConsulta_noConfigurat_retornaFalse() {
        when(configHelper.getConfigAsBoolean("es.caib.pinbal.optimitzar.transaccions.nova.consulta", false)).thenReturn(false);

        assertFalse(consultaService.isOptimitzarTransaccionsNovaConsulta());
    }

    @Test
    public void informeGeneralEstat_ambCoincidenciesISenseCoincidencies_calculaPeticionsIUsuaris() throws Exception {
        Entitat entitatCalvia = crearEntitat(10L, "CALVIA", "Ajuntament de Calvià", "P0700100A");
        Procediment procedimentUrb = crearProcediment(20L, entitatCalvia, "URB01", "Urbanisme", "Urbanisme", null);
        ProcedimentServei psAmbCoincidencies = crearProcedimentServei(30L, procedimentUrb, "SV01");

        Entitat entitatPalma = crearEntitat(11L, "PALMA", "Ajuntament de Palma", "P0704000F");
        Procediment procedimentTribut = crearProcediment(21L, entitatPalma, "TRIB01", "Tributs", "Hisenda", null);
        ProcedimentServei psSenseCoincidencies = crearProcedimentServei(31L, procedimentTribut, "SV02");

        when(procedimentServeiRepository.findAll(any(Sort.class)))
                .thenReturn(Arrays.asList(psAmbCoincidencies, psSenseCoincidencies));

        List<Object[]> consultes = new ArrayList<>();
        consultes.add(new Object[] { 10L, 20L, "SV01", EstatTipus.Tramitada, 5L });
        consultes.add(new Object[] { 10L, 20L, "SV01", EstatTipus.Error, 2L });
        Date data = new Date();
        when(consultaRepository.countGroupByProcedimentServeiEstat(any(Date.class), any(Date.class))).thenReturn(consultes);

        EmisorCertificado emisor = new EmisorCertificado(1L, "Emissor SCSP", "Q0700100A");
        when(scspHelper.getServicio("SV01")).thenReturn(crearServicio("SV01", "Servei Urbanisme", emisor));
        when(scspHelper.getServicio("SV02")).thenReturn(crearServicio("SV02", "Servei Tributs", null));
        when(scspHelper.getEmisorNombre("Q0700100A")).thenReturn("Emissor SCSP Nom");

        stubAclEntries(Arrays.asList(crearAce("usuari1"), crearAce("usuari2")));

        List<InformeGeneralEstatDto> resposta = consultaService.informeGeneralEstat(data, data);

        assertEquals(2, resposta.size());
        InformeGeneralEstatDto dtoAmbCoincidencies = resposta.get(0);
        assertEquals("CALVIA", dtoAmbCoincidencies.getEntitatCodi());
        assertEquals("SV01", dtoAmbCoincidencies.getServeiCodi());
        assertEquals(5, dtoAmbCoincidencies.getPeticionsCorrectes());
        assertEquals(2, dtoAmbCoincidencies.getPeticionsErronees());
        assertEquals(2, dtoAmbCoincidencies.getServeiUsuaris());
        assertNotNull(dtoAmbCoincidencies.getServeiEmisor());
        assertEquals("Q0700100A", dtoAmbCoincidencies.getServeiEmisor().getCif());
        assertEquals("Emissor SCSP Nom", dtoAmbCoincidencies.getServeiEmisor().getNom());

        InformeGeneralEstatDto dtoSenseCoincidencies = resposta.get(1);
        assertEquals("PALMA", dtoSenseCoincidencies.getEntitatCodi());
        assertEquals(0, dtoSenseCoincidencies.getPeticionsCorrectes());
        assertEquals(0, dtoSenseCoincidencies.getPeticionsErronees());
        assertNull(dtoSenseCoincidencies.getServeiEmisor());
    }

    @Test
    public void informeGeneralEstat_aclSenseEntradesTrobades_serveiUsuarisZero() throws Exception {
        Entitat entitat = crearEntitat(12L, "MANACOR", "Ajuntament de Manacor", "P0703500J");
        Procediment procediment = crearProcediment(22L, entitat, "PROC01", "Procediment", "Departament", null);
        ProcedimentServei ps = crearProcedimentServei(32L, procediment, "SV03");

        when(procedimentServeiRepository.findAll(any(Sort.class))).thenReturn(Collections.singletonList(ps));
        when(consultaRepository.countGroupByProcedimentServeiEstat(any(Date.class), any(Date.class))).thenReturn(new ArrayList<>());
        when(scspHelper.getServicio("SV03")).thenReturn(crearServicio("SV03", "Servei", null));
        stubAclNotFound();

        Date data = new Date();
        List<InformeGeneralEstatDto> resposta = consultaService.informeGeneralEstat(data, data);

        assertEquals(1, resposta.size());
        assertEquals(0, resposta.get(0).getServeiUsuaris());
        assertEquals(0, resposta.get(0).getPeticionsCorrectes());
    }

    @Test
    public void informeUsuarisEntitatOrganProcedimentServei_rolAdmin_retornaUsuarisAmbPermis() throws Exception {
        Entitat entitat = crearEntitat(40L, "INCA", "Ajuntament d'Inca", "P0702700B");
        OrganGestor organGestor = new OrganGestor();
        organGestor.setCodi("OG01");
        organGestor.setNom("Servei Jurídic");
        organGestor.setActiu(true);
        Procediment procediment = crearProcediment(41L, entitat, "PROC02", "Llicències", "Urbanisme", organGestor);
        ProcedimentServei ps = crearProcedimentServei(42L, procediment, "SV04");

        when(procedimentServeiRepository.findAllActius()).thenReturn(Collections.singletonList(ps));
        stubAclEntries(Collections.singletonList(crearAce("usuari1")));

        Usuari usuari = Usuari.getBuilderInicialitzat("USR001", "Joan Fiol", "41111111A").build();
        EntitatUsuari entitatUsuari = EntitatUsuari.getBuilder(entitat, usuari, "Departament", true, false, false, false, true).build();
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodis(any(), any(), any(), any(), any(), any()))
                .thenReturn(Collections.singletonList(entitatUsuari));

        when(scspHelper.getServicio("SV04")).thenReturn(crearServicio("SV04", "Llicències", null));

        List<InformeProcedimentServeiDto> resposta = consultaService.informeUsuarisEntitatOrganProcedimentServei(
                40L, "PBL_ADMIN", new InformeRepresentantFiltreDto());

        assertEquals(1, resposta.size());
        InformeProcedimentServeiDto dto = resposta.get(0);
        assertEquals("INCA", dto.getEntitatCodi());
        assertEquals("OG01", dto.getOrganGestorCodi());
        assertTrue(dto.isOrganGestorActiu());
        assertEquals("USR001", dto.getUsuariCodi());
        assertEquals("41111111A", dto.getUsuariNif());
        assertEquals("Joan Fiol", dto.getUsuariNom());
        assertEquals("SV04", dto.getServeiCodi());
    }

    @Test
    public void informeUsuarisEntitatOrganProcedimentServei_rolRepresentantAmbFiltre_cridaCercaFiltrada() throws Exception {
        Entitat entitat = crearEntitat(50L, "SOLLER", "Ajuntament de Sóller", "P0705400H");
        Procediment procediment = crearProcediment(51L, entitat, "PROC03", "Procediment", "Departament", null);
        ProcedimentServei ps = crearProcedimentServei(52L, procediment, "SV05");

        InformeRepresentantFiltreDto filtre = new InformeRepresentantFiltreDto();
        filtre.setProcedimentId(51L);

        when(procedimentServeiRepository.findAllActiusAmbFiltre(
                eq(50L), eq(true), isNull(), eq(false), eq(51L), eq(true), isNull()))
                .thenReturn(Collections.singletonList(ps));
        stubAclNotFound();
        when(scspHelper.getServicio("SV05")).thenReturn(crearServicio("SV05", "Servei", null));

        List<InformeProcedimentServeiDto> resposta = consultaService.informeUsuarisEntitatOrganProcedimentServei(
                50L, "PBL_REPRES", filtre);

        assertTrue(resposta.isEmpty());
        verify(procedimentServeiRepository).findAllActiusAmbFiltre(
                eq(50L), eq(true), isNull(), eq(false), eq(51L), eq(true), isNull());
        verify(entitatUsuariRepository, never()).findByEntitatIdAndUsuariCodis(any(), any(), any(), any(), any(), any());
    }

    @Test
    public void informeUsuarisEntitatOrganProcedimentServei_rolDesconegut_retornaLlistaBuida() {
        List<InformeProcedimentServeiDto> resposta = consultaService.informeUsuarisEntitatOrganProcedimentServei(
                60L, "PBL_ALTRE", new InformeRepresentantFiltreDto());

        assertTrue(resposta.isEmpty());
        verify(procedimentServeiRepository, never()).findAllActius();
        verify(procedimentServeiRepository, never()).findAllActiusAmbFiltre(any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any());
    }

    private Consulta crearConsulta(Long id, ProcedimentServei ps, String scspPeticionId) {
        Consulta consulta = Consulta.getBuilder(
                scspPeticionId, "Joan Fiol", "41111111A", "NIF", "12345678Z",
                "Antònia", "Ramis", "Vives", "Antònia Ramis Vives", "Departament",
                ps, "Tràmit", null, null, false, false, null).build();
        consulta.configurarIdPerTest(id);
        return consulta;
    }

    @Test
    public void generarArbreResposta_ambDocumentIFillsAnidats_retornaArbreMapejat() throws Exception {
        Entitat entitat = crearEntitat(70L, "ALCUDIA", "Ajuntament d'Alcúdia", "P0700200I");
        Procediment procediment = crearProcediment(71L, entitat, "PROC04", "Procediment", "Departament", null);
        ProcedimentServei ps = crearProcedimentServei(72L, procediment, "SV06");
        Consulta consulta = crearConsulta(80L, ps, "PET-0001");
        when(consultaRepository.findById(80L)).thenReturn(java.util.Optional.of(consulta));

        ElementArbre arrel = new ElementArbre("Arrel");
        ElementArbre solicitant = new ElementArbre("Sol·licitant", "Antònia Ramis Vives", "Respuesta/DatosGenericos/Solicitante");
        arrel.addFill(solicitant);
        ElementArbre document = new ElementArbre(
                "Fitxer",
                "PetitContingutBase64XX==",
                "Respuesta/DatosEspecificos/Fichero/Contenido");
        arrel.addFill(document);

        when(scspHelper.generarArbreJustificant("PET-0001", "PET-0001", null)).thenReturn(arrel);
        when(serveiJustificantCampRepository.findXpathDocumentByServei("SV06"))
                .thenReturn(Collections.singletonList("Respuesta/DatosEspecificos/Fichero/Contenido"));

        ArbreRespostaDto resposta = consultaService.generarArbreResposta(80L);

        assertNotNull(resposta);
        assertEquals("Arrel", resposta.getTitol());
        assertEquals(2, resposta.getFills().size());
        assertFalse(resposta.getFills().get(0).isDocument());
        assertTrue(resposta.getFills().get(1).isDocument());
    }

    @Test
    public void generarArbreResposta_consultaNoTrobada_retornaNull() throws Exception {
        when(consultaRepository.findById(81L)).thenReturn(java.util.Optional.empty());

        ArbreRespostaDto resposta = consultaService.generarArbreResposta(81L);

        assertNull(resposta);
    }

    @Test
    public void generarArbreResposta_scspHelperLlancaExcepcio_retornaNull() throws Exception {
        Entitat entitat = crearEntitat(73L, "MAO", "Ajuntament de Maó", "P0703600E");
        Procediment procediment = crearProcediment(74L, entitat, "PROC05", "Procediment", "Departament", null);
        ProcedimentServei ps = crearProcedimentServei(75L, procediment, "SV07");
        Consulta consulta = crearConsulta(82L, ps, "PET-0002");
        when(consultaRepository.findById(82L)).thenReturn(java.util.Optional.of(consulta));
        when(scspHelper.generarArbreJustificant("PET-0002", "PET-0002", null))
                .thenThrow(new ScspException("SCSP-01", "Error recuperant la resposta"));

        ArbreRespostaDto resposta = consultaService.generarArbreResposta(82L);

        assertNull(resposta);
    }

    @Test
    public void autoGenerarEmailReportEstat_ambAdministradors_enviaCorreuAmbEmails() throws Exception {
        when(procedimentServeiRepository.findAll(any(Sort.class))).thenReturn(new ArrayList<>());
        when(consultaRepository.countGroupByProcedimentServeiEstat(any(Date.class), any(Date.class))).thenReturn(new ArrayList<>());
        byte[] excel = "excel-content".getBytes();
        when(excelHelper.generarReportEstatExcel(anyList())).thenReturn(excel);

        DadesUsuari admin1 = DadesUsuari.builder().codi("ADM1").nom("Admin Un").email("admin1@caib.es").build();
        DadesUsuari admin2 = DadesUsuari.builder().codi("ADM2").nom("Admin Dos").email("admin2@caib.es").build();
        when(pluginHelper.dadesUsuariFindAmbGrup("PBL_ADMIN")).thenReturn(Arrays.asList(admin1, admin2));

        consultaService.autoGenerarEmailReportEstat();

        verify(emailReportEstatHelper).sendMail(
                new String[] { "admin1@caib.es", "admin2@caib.es" }, excel);
    }

    @Test
    public void autoGenerarEmailReportEstat_errorSistemaExtern_enviaCorreuSenseDestinataris() throws Exception {
        when(procedimentServeiRepository.findAll(any(Sort.class))).thenReturn(new ArrayList<>());
        when(consultaRepository.countGroupByProcedimentServeiEstat(any(Date.class), any(Date.class))).thenReturn(new ArrayList<>());
        byte[] excel = "excel-content".getBytes();
        when(excelHelper.generarReportEstatExcel(anyList())).thenReturn(excel);
        when(pluginHelper.dadesUsuariFindAmbGrup("PBL_ADMIN")).thenThrow(new SistemaExternException("No hi ha connexió amb el sistema extern"));

        consultaService.autoGenerarEmailReportEstat();

        verify(emailReportEstatHelper).sendMail(new String[0], excel);
    }

    @Test
    public void setApplicationContext_assignaCampApplicationContext() {
        ApplicationContext altre = mock(ApplicationContext.class);

        consultaService.setApplicationContext(altre);

        assertSame(altre, ReflectionTestUtils.getField(consultaService, "applicationContext"));
    }

    @Test
    public void setMessageSource_assignaCampMessageSource() {
        MessageSource altre = mock(MessageSource.class);

        consultaService.setMessageSource(altre);

        assertSame(altre, ReflectionTestUtils.getField(consultaService, "messageSource"));
    }

    @Test
    public void onApplicationEvent_contextDisponible_assignaSelf() {
        ConsultaService selfBean = mock(ConsultaService.class);
        when(applicationContext.getBean(ConsultaService.class)).thenReturn(selfBean);
        ContextRefreshedEvent event = new ContextRefreshedEvent(applicationContext);

        assertDoesNotThrow(() -> consultaService.onApplicationEvent(event));

        assertSame(selfBean, ReflectionTestUtils.getField(consultaService, "self"));
    }

    @Test
    public void onApplicationEvent_applicationContextLlancaExcepcio_noPropagaException() {
        when(applicationContext.getBean(ConsultaService.class)).thenThrow(new IllegalStateException("Context no disponible"));
        ContextRefreshedEvent event = new ContextRefreshedEvent(applicationContext);

        assertDoesNotThrow(() -> consultaService.onApplicationEvent(event));

        assertNull(ReflectionTestUtils.getField(consultaService, "self"));
    }

    private ScspToken crearToken(Integer tipoMensaje, String datos) {
        ScspToken token = mock(ScspToken.class);
        when(token.getTipoMensaje()).thenReturn(tipoMensaje);
        when(token.getDatos()).thenReturn(datos);
        return token;
    }

    @Test
    public void descarregarXmlTokensZip_consultaNoTrobada_llancaException() {
        when(consultaRepository.findById(90L)).thenReturn(java.util.Optional.empty());

        assertThrows(ConsultaNotFoundException.class, () -> consultaService.descarregarXmlTokensZip(90L));
    }

    @Test
    public void descarregarXmlTokensZip_sensePeticionsToken_retornaNull() throws Exception {
        Entitat entitat = crearEntitat(91L, "FELANITX", "Ajuntament de Felanitx", "P0702100M");
        Procediment procediment = crearProcediment(92L, entitat, "PROC06", "Procediment", "Departament", null);
        ProcedimentServei ps = crearProcedimentServei(93L, procediment, "SV08");
        Consulta consulta = crearConsulta(94L, ps, "PET-0003");
        when(consultaRepository.findById(94L)).thenReturn(java.util.Optional.of(consulta));
        when(tokenRepository.findByIdPeticionOrderByTipoMensajeAsc("PET-0003")).thenReturn(new ArrayList<>());

        FitxerDto resposta = consultaService.descarregarXmlTokensZip(94L);

        assertNull(resposta);
    }

    @Test
    public void descarregarXmlTokensZip_ambVariosTokens_generaZipAmbCarpetesIComptador() throws Exception {
        Entitat entitat = crearEntitat(95L, "MANACOR", "Ajuntament de Manacor", "P0703500J");
        Procediment procediment = crearProcediment(96L, entitat, "PROC07", "Procediment", "Departament", null);
        ProcedimentServei ps = crearProcedimentServei(97L, procediment, "SV09");
        Consulta consulta = crearConsulta(98L, ps, "PET-0004");
        when(consultaRepository.findById(98L)).thenReturn(java.util.Optional.of(consulta));

        List<ScspToken> tokens = Arrays.asList(
                crearToken(ScspTokenId.PETICION, "<peticion>1</peticion>"),
                crearToken(ScspTokenId.PETICION, "<peticion>2</peticion>"),
                crearToken(ScspTokenId.RESPUESTA, "<respuesta>ok</respuesta>"),
                crearToken(9999, null));
        when(tokenRepository.findByIdPeticionOrderByTipoMensajeAsc("PET-0004")).thenReturn(tokens);

        FitxerDto resposta = consultaService.descarregarXmlTokensZip(98L);

        assertNotNull(resposta);
        assertEquals("XML_PET-0004.zip", resposta.getNom());
        Set<String> entries = new HashSet<>();
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(resposta.getContingut()))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                entries.add(entry.getName());
            }
        }
        assertEquals(4, entries.size());
        assertTrue(entries.contains("peticion/peticion.xml"));
        assertTrue(entries.contains("peticion/peticion_1.xml"));
        assertTrue(entries.contains("respuesta/respuesta.xml"));
        assertTrue(entries.contains("altres/altres.xml"));
    }

    @Test
    public void descarregarXmlTokensZip_errorAlGenerarZip_retornaZipBuit() throws Exception {
        Entitat entitat = crearEntitat(99L, "SINEU", "Ajuntament de Sineu", "P0705300J");
        Procediment procediment = crearProcediment(100L, entitat, "PROC08", "Procediment", "Departament", null);
        ProcedimentServei ps = crearProcedimentServei(101L, procediment, "SV10");
        Consulta consulta = crearConsulta(102L, ps, "PET-0005");
        when(consultaRepository.findById(102L)).thenReturn(java.util.Optional.of(consulta));
        when(tokenRepository.findByIdPeticionOrderByTipoMensajeAsc("PET-0005")).thenThrow(new RuntimeException("Error de connexió"));

        FitxerDto resposta = consultaService.descarregarXmlTokensZip(102L);

        assertNotNull(resposta);
        assertEquals("XML_PET-0005.zip", resposta.getNom());
        int entriesCount = 0;
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(resposta.getContingut()))) {
            while (zis.getNextEntry() != null) {
                entriesCount++;
            }
        }
        assertEquals(0, entriesCount);
    }
}
