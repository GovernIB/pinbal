package es.caib.pinbal.logic.service;

import es.caib.pinbal.client.dadesobertes.DadesObertesResposta;
import es.caib.pinbal.client.dadesobertes.DadesObertesRespostaConsulta;
import es.caib.pinbal.logic.helper.ConfigHelper;
import es.caib.pinbal.logic.helper.DtoMappingHelper;
import es.caib.pinbal.logic.helper.JustificantHelper;
import es.caib.pinbal.logic.helper.PeticioScspEstadistiquesHelper;
import es.caib.pinbal.logic.helper.PluginHelper;
import es.caib.pinbal.logic.helper.mock.JustificantHelperFactory;
import es.caib.pinbal.logic.intf.dto.CarregaDto;
import es.caib.pinbal.logic.intf.dto.ConsultaDto;
import es.caib.pinbal.logic.intf.dto.ConsultaFiltreDto;
import es.caib.pinbal.logic.intf.dto.ConsultaOpenDataDto;
import es.caib.pinbal.logic.intf.dto.EstatTipus;
import es.caib.pinbal.logic.intf.dto.FitxerDto;
import es.caib.pinbal.logic.intf.dto.InformeGeneralEstatDto;
import es.caib.pinbal.logic.intf.dto.JustificantDto;
import es.caib.pinbal.logic.intf.dto.JustificantEstat;
import es.caib.pinbal.logic.intf.service.exception.ConsultaNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.EntitatNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.JustificantGeneracioException;
import es.caib.pinbal.logic.intf.service.exception.ScspException;
import es.caib.pinbal.persist.entity.Entitat;
import es.caib.pinbal.persist.entity.EntitatUsuari;
import es.caib.pinbal.persist.entity.HistoricConsulta;
import es.caib.pinbal.persist.entity.Procediment;
import es.caib.pinbal.persist.entity.ProcedimentServei;
import es.caib.pinbal.persist.entity.ScspToken;
import es.caib.pinbal.persist.entity.ScspTokenId;
import es.caib.pinbal.persist.entity.Usuari;
import es.caib.pinbal.persist.entity.llistat.LlistatHistoricConsulta;
import es.caib.pinbal.persist.repository.EntitatRepository;
import es.caib.pinbal.persist.repository.EntitatUsuariRepository;
import es.caib.pinbal.persist.repository.HistoricConsultaRepository;
import es.caib.pinbal.persist.repository.ProcedimentRepository;
import es.caib.pinbal.persist.repository.ProcedimentServeiRepository;
import es.caib.pinbal.persist.repository.TokenRepository;
import es.caib.pinbal.persist.repository.UsuariRepository;
import es.caib.pinbal.persist.repository.dadesobertes.DadesObertesHistoricConsultaRepository;
import es.caib.pinbal.persist.repository.llistat.LlistatHistoricConsultaRepository;
import es.caib.pinbal.scsp.Resposta;
import es.caib.pinbal.scsp.ScspHelper;
import es.caib.pluginsib.arxiu.api.Document;
import es.caib.pluginsib.arxiu.api.DocumentMetadades;
import es.scsp.common.domain.core.EmisorCertificado;
import es.scsp.common.domain.core.Servicio;
import ma.glasnost.orika.MapperFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class HistoricConsultaServiceImplTest {

    @Mock private HistoricConsultaRepository historicConsultaRepository;
    @Mock private ProcedimentRepository procedimentRepository;
    @Mock private ProcedimentServeiRepository procedimentServeiRepository;
    @Mock private EntitatRepository entitatRepository;
    @Mock private UsuariRepository usuariRepository;
    @Mock private EntitatUsuariRepository entitatUsuariRepository;
    @Mock private TokenRepository tokenRepository;
    @Mock private JustificantHelperFactory justificantHelperFactory;
    @Mock private DtoMappingHelper dtoMappingHelper;
    @Mock private PluginHelper pluginHelper;
    @Mock private PeticioScspEstadistiquesHelper peticioScspEstadistiquesHelper;
    @Mock private ConfigHelper configHelper;
    @Mock private MutableAclService aclService;
    @Mock private PlatformTransactionManager transactionManager;
    @Mock private DadesObertesHistoricConsultaRepository dadesObertesHistoricConsultaRepository;
    @Mock private LlistatHistoricConsultaRepository llistatHistoricConsultaRepository;
    @Mock private MapperFacade mapperFacade;
    @Mock private ScspHelper scspHelper;
    @Mock private JustificantHelper justificantHelper;

    @InjectMocks
    private HistoricConsultaServiceImpl historicConsultaService;

    @BeforeEach
    public void setUp() {
        when(dtoMappingHelper.getMapperFacade()).thenReturn(mapperFacade);
        ReflectionTestUtils.setField(historicConsultaService, "scspHelper", scspHelper);
        ReflectionTestUtils.setField(historicConsultaService, "propertiesCopiades", true);
        when(justificantHelperFactory.getJustificantHelper()).thenReturn(justificantHelper);
        when(configHelper.getConfig(anyString(), any())).thenReturn("Oracle");
        TransactionStatus status = mock(TransactionStatus.class);
        when(transactionManager.getTransaction(any())).thenReturn(status);
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private Authentication mockAuth(String name, String... roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String r : roles) {
            authorities.add(new SimpleGrantedAuthority(r));
        }
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(name);
        doReturn(authorities).when(auth).getAuthorities();
        SecurityContext sc = mock(SecurityContext.class);
        when(sc.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(sc);
        return auth;
    }

    private Usuari usuariAmbCodi(String codi) {
        Usuari u = mock(Usuari.class);
        when(u.getCodi()).thenReturn(codi);
        return u;
    }

    private Entitat crearEntitat(Long id, String codi) {
        Entitat e = new Entitat();
        e.configurarIdPerTest(id);
        e.setCodi(codi);
        e.setNom("Ajuntament de " + codi);
        e.setCif("P" + id + "0000A");
        return e;
    }

    private Procediment crearProcediment(Long id, Entitat entitat, String codi) {
        Procediment p = Procediment.getBuilder(entitat, codi, "Procediment " + codi, "Informatica", null, null, null, null).build();
        p.configurarIdPerTest(id);
        return p;
    }

    private ProcedimentServei crearProcedimentServei(Long id, Procediment procediment, String servei) {
        ProcedimentServei ps = ProcedimentServei.getBuilder(procediment, servei).build();
        ps.configurarIdPerTest(id);
        return ps;
    }

    private HistoricConsulta mockConsulta(Long id, EstatTipus estat, boolean multiple, Usuari createdBy) {
        HistoricConsulta c = mock(HistoricConsulta.class);
        when(c.getId()).thenReturn(id);
        when(c.getEstat()).thenReturn(estat);
        when(c.isMultiple()).thenReturn(multiple);
        when(c.getCreatedBy()).thenReturn(createdBy != null ? Optional.of(createdBy) : Optional.empty());
        when(c.getScspPeticionId()).thenReturn("PET" + id);
        when(c.getScspSolicitudId()).thenReturn("SOL" + id);
        return c;
    }

    private ConsultaDto consultaDtoAmbEstat(String estat) {
        ConsultaDto dto = new ConsultaDto();
        dto.setEstat(estat);
        return dto;
    }

    // ------------------------------------------------------------------
    // obtenirArxiuInfo
    // ------------------------------------------------------------------

    @Test
    public void obtenirArxiuInfo_consultaNoTrobada_retornaDetallBuid() {
        when(historicConsultaRepository.findById(99L)).thenReturn(Optional.empty());
        var result = historicConsultaService.obtenirArxiuInfo(99L);
        assertNotNull(result);
    }

    @Test
    public void obtenirArxiuInfo_trobada_retornaDetall() throws Exception {
        HistoricConsulta consulta = mockConsulta(1L, EstatTipus.Tramitada, false, null);
        when(consulta.getArxiuDocumentUuid()).thenReturn("uuid-1");
        when(historicConsultaRepository.findById(1L)).thenReturn(Optional.of(consulta));
        Document document = mock(Document.class);
        when(pluginHelper.arxiuDocumentConsultar("PET1", "uuid-1", null, false, false)).thenReturn(document);

        var result = historicConsultaService.obtenirArxiuInfo(1L);

        assertNotNull(result);
        verify(pluginHelper).arxiuDocumentConsultar("PET1", "uuid-1", null, false, false);
    }

    @Test
    public void obtenirArxiuInfo_pluginLlancaException_retornaDetallBuid() throws Exception {
        HistoricConsulta consulta = mockConsulta(1L, EstatTipus.Tramitada, false, null);
        when(historicConsultaRepository.findById(1L)).thenReturn(Optional.of(consulta));
        when(pluginHelper.arxiuDocumentConsultar(any(), any(), any(), anyBoolean(), anyBoolean()))
                .thenThrow(new RuntimeException("error arxiu"));

        var result = historicConsultaService.obtenirArxiuInfo(1L);

        assertNotNull(result);
    }

    // ------------------------------------------------------------------
    // obtenirJustificant(Long, boolean) -- exercises obtenirJustificantComu
    // amb descarregar=true, versioImprimible=true
    // ------------------------------------------------------------------

    @Test
    public void obtenirJustificant_consultaNoTrobada_llancaException() {
        when(historicConsultaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ConsultaNotFoundException.class,
                () -> historicConsultaService.obtenirJustificant(99L, true));
    }

    @Test
    public void obtenirJustificant_noAdmin_propietariNoCoincideix_llancaException() {
        mockAuth("usuari2");
        Usuari propietari = usuariAmbCodi("usuari1");
        HistoricConsulta consulta = mockConsulta(1L, EstatTipus.Tramitada, false, propietari);
        when(historicConsultaRepository.findById(1L)).thenReturn(Optional.of(consulta));

        assertThrows(ConsultaNotFoundException.class,
                () -> historicConsultaService.obtenirJustificant(1L, false));
    }

    @Test
    public void obtenirJustificant_estatNoTramitada_llancaException() {
        HistoricConsulta consulta = mockConsulta(1L, EstatTipus.Processant, false, null);
        when(historicConsultaRepository.findById(1L)).thenReturn(Optional.of(consulta));

        assertThrows(JustificantGeneracioException.class,
                () -> historicConsultaService.obtenirJustificant(1L, true));
    }

    @Test
    public void obtenirJustificant_descarregaAmbEstatError_retornaJustificantAmbError() throws Exception {
        HistoricConsulta consulta = mockConsulta(1L, EstatTipus.Tramitada, false, null);
        when(consulta.getJustificantEstat()).thenReturn(JustificantEstat.ERROR);
        when(historicConsultaRepository.findById(1L)).thenReturn(Optional.of(consulta));
        when(historicConsultaRepository.getOne(1L)).thenReturn(consulta);

        JustificantDto result = historicConsultaService.obtenirJustificant(1L, true);

        assertTrue(result.isError());
        assertEquals("La generació del justificant ha produït errors", result.getErrorDescripcio());
        verify(justificantHelper).generarCustodiarJustificantPendent(consulta, scspHelper);
        verify(historicConsultaRepository).saveAndFlush(consulta);
    }

    @Test
    public void obtenirJustificant_descarregaAmbEstatOk_retornaFitxerGenerat() throws Exception {
        HistoricConsulta consulta = mockConsulta(1L, EstatTipus.Tramitada, false, null);
        when(consulta.getJustificantEstat()).thenReturn(JustificantEstat.OK);
        when(historicConsultaRepository.findById(1L)).thenReturn(Optional.of(consulta));
        when(historicConsultaRepository.getOne(1L)).thenReturn(consulta);
        FitxerDto fitxer = FitxerDto.builder().nom("PBL_1.pdf").contentType("application/pdf").contingut(new byte[]{1, 2, 3}).build();
        when(justificantHelper.descarregarFitxerGenerat(consulta, scspHelper, true)).thenReturn(fitxer);

        JustificantDto result = historicConsultaService.obtenirJustificant(1L, true);

        assertFalse(result.isError());
        assertEquals("PBL_1.pdf", result.getNom());
        assertArrayEquals(new byte[]{1, 2, 3}, result.getContingut());
        verify(justificantHelper, never()).generarCustodiarJustificantPendent(any(), any());
    }

    @Test
    public void obtenirJustificant_descarregaAmbErrorEnLaDescarrega_retornaError() throws Exception {
        HistoricConsulta consulta = mockConsulta(1L, EstatTipus.Tramitada, false, null);
        when(consulta.getJustificantEstat()).thenReturn(JustificantEstat.OK_NO_CUSTODIA);
        when(historicConsultaRepository.findById(1L)).thenReturn(Optional.of(consulta));
        when(historicConsultaRepository.getOne(1L)).thenReturn(consulta);
        when(justificantHelper.descarregarFitxerGenerat(consulta, scspHelper, true))
                .thenThrow(new RuntimeException("no s'ha pogut descarregar"));

        JustificantDto result = historicConsultaService.obtenirJustificant(1L, true);

        assertTrue(result.isError());
        assertEquals("La descàrrega del justificant ha produit errors", result.getErrorDescripcio());
    }

    // ------------------------------------------------------------------
    // obtenirJustificant(String, String, boolean, boolean) -- descarregar=false paths
    // ------------------------------------------------------------------

    @Test
    public void obtenirJustificantPerPeticio_consultaNoTrobada_llancaException() {
        when(historicConsultaRepository.findByScspPeticionIdAndScspSolicitudId("PET001", "SOL001"))
                .thenReturn(null);
        assertThrows(ConsultaNotFoundException.class,
                () -> historicConsultaService.obtenirJustificant("PET001", "SOL001", false, false));
    }

    @Test
    public void obtenirJustificantPerPeticio_justificantEstatOk_retornaErrorNoGenerat() throws Exception {
        HistoricConsulta consulta = mockConsulta(1L, EstatTipus.Tramitada, false, null);
        when(consulta.getJustificantEstat()).thenReturn(JustificantEstat.OK);
        when(historicConsultaRepository.findByScspPeticionIdAndScspSolicitudId("PET001", "SOL001")).thenReturn(consulta);
        when(historicConsultaRepository.getOne(1L)).thenReturn(consulta);

        JustificantDto result = historicConsultaService.obtenirJustificant("PET001", "SOL001", true, false);

        assertTrue(result.isError());
        assertEquals("El justificant no s'ha generat, o no s'ha desat a l'arxiu", result.getErrorDescripcio());
    }

    @Test
    public void obtenirJustificantPerPeticio_arxiuUuidNull_retornaErrorNoTrobat() throws Exception {
        HistoricConsulta consulta = mockConsulta(1L, EstatTipus.Tramitada, false, null);
        when(consulta.getJustificantEstat()).thenReturn(JustificantEstat.NO_DISPONIBLE);
        when(consulta.getArxiuDocumentUuid()).thenReturn(null);
        when(historicConsultaRepository.findByScspPeticionIdAndScspSolicitudId("PET001", "SOL001")).thenReturn(consulta);
        when(historicConsultaRepository.getOne(1L)).thenReturn(consulta);

        JustificantDto result = historicConsultaService.obtenirJustificant("PET001", "SOL001", true, false);

        assertTrue(result.isError());
        assertEquals("El justificant no es troba a l'arxiu", result.getErrorDescripcio());
    }

    @Test
    public void obtenirJustificantPerPeticio_versioImprimibleAmbCsv_retornaCsv() throws Exception {
        HistoricConsulta consulta = mockConsulta(1L, EstatTipus.Tramitada, false, null);
        when(consulta.getJustificantEstat()).thenReturn(JustificantEstat.OK_NO_CUSTODIA);
        when(consulta.getArxiuDocumentUuid()).thenReturn("uuid-2");
        when(historicConsultaRepository.findByScspPeticionIdAndScspSolicitudId("PET001", "SOL001")).thenReturn(consulta);
        when(historicConsultaRepository.getOne(1L)).thenReturn(consulta);
        Document document = mock(Document.class);
        DocumentMetadades metadades = mock(DocumentMetadades.class);
        when(metadades.getCsv()).thenReturn("CSV-123");
        when(document.getMetadades()).thenReturn(metadades);
        when(pluginHelper.arxiuDocumentConsultar("PET1", "uuid-2", null, false, false)).thenReturn(document);

        JustificantDto result = historicConsultaService.obtenirJustificant("PET001", "SOL001", true, false);

        assertFalse(result.isError());
        assertEquals("CSV-123", result.getArxiuCsv());
    }

    @Test
    public void obtenirJustificantPerPeticio_versioImprimibleSenseCsv_retornaError() throws Exception {
        HistoricConsulta consulta = mockConsulta(1L, EstatTipus.Tramitada, false, null);
        when(consulta.getJustificantEstat()).thenReturn(JustificantEstat.OK_NO_CUSTODIA);
        when(consulta.getArxiuDocumentUuid()).thenReturn("uuid-3");
        when(historicConsultaRepository.findByScspPeticionIdAndScspSolicitudId("PET001", "SOL001")).thenReturn(consulta);
        when(historicConsultaRepository.getOne(1L)).thenReturn(consulta);
        when(pluginHelper.arxiuDocumentConsultar(any(), any(), any(), anyBoolean(), anyBoolean())).thenReturn(mock(Document.class));

        JustificantDto result = historicConsultaService.obtenirJustificant("PET001", "SOL001", true, false);

        assertTrue(result.isError());
        assertEquals("No s'ha pogut recuperar el CSV del justificant", result.getErrorDescripcio());
    }

    @Test
    public void obtenirJustificantPerPeticio_versioImprimibleAmbExcepcio_llancaException() throws Exception {
        HistoricConsulta consulta = mockConsulta(1L, EstatTipus.Tramitada, false, null);
        when(consulta.getJustificantEstat()).thenReturn(JustificantEstat.OK_NO_CUSTODIA);
        when(consulta.getArxiuDocumentUuid()).thenReturn("uuid-4");
        when(historicConsultaRepository.findByScspPeticionIdAndScspSolicitudId("PET001", "SOL001")).thenReturn(consulta);
        when(historicConsultaRepository.getOne(1L)).thenReturn(consulta);
        when(pluginHelper.arxiuDocumentConsultar(any(), any(), any(), anyBoolean(), anyBoolean()))
                .thenThrow(new RuntimeException("boom"));

        assertThrows(JustificantGeneracioException.class,
                () -> historicConsultaService.obtenirJustificant("PET001", "SOL001", true, false));
    }

    @Test
    public void obtenirJustificantPerPeticio_senseVersioImprimible_retornaArxiuUuid() throws Exception {
        HistoricConsulta consulta = mockConsulta(1L, EstatTipus.Tramitada, false, null);
        when(consulta.getJustificantEstat()).thenReturn(JustificantEstat.OK_NO_CUSTODIA);
        when(consulta.getArxiuDocumentUuid()).thenReturn("uuid-5");
        when(historicConsultaRepository.findByScspPeticionIdAndScspSolicitudId("PET001", "SOL001")).thenReturn(consulta);
        when(historicConsultaRepository.getOne(1L)).thenReturn(consulta);

        JustificantDto result = historicConsultaService.obtenirJustificant("PET001", "SOL001", false, false);

        assertFalse(result.isError());
        assertEquals("uuid-5", result.getArxiuUuid());
        verify(pluginHelper, never()).arxiuDocumentConsultar(any(), any(), any(), anyBoolean(), anyBoolean());
    }

    // ------------------------------------------------------------------
    // reintentarGeneracioJustificant
    // ------------------------------------------------------------------

    @Test
    public void reintentarGeneracioJustificant_consultaNoTrobada_llancaException() {
        when(historicConsultaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ConsultaNotFoundException.class,
                () -> historicConsultaService.reintentarGeneracioJustificant(99L, false));
    }

    @Test
    public void reintentarGeneracioJustificant_noAdminIPropietariNoCoincideix_llancaException() {
        mockAuth("usuari2");
        Usuari propietari = usuariAmbCodi("usuari1");
        HistoricConsulta consulta = mockConsulta(1L, EstatTipus.Tramitada, false, propietari);
        when(historicConsultaRepository.findById(1L)).thenReturn(Optional.of(consulta));

        assertThrows(ConsultaNotFoundException.class,
                () -> historicConsultaService.reintentarGeneracioJustificant(1L, false));
    }

    @Test
    public void reintentarGeneracioJustificant_propietariCoincideix_ok() throws Exception {
        mockAuth("usuari1");
        Usuari propietari = usuariAmbCodi("usuari1");
        HistoricConsulta consulta = mockConsulta(1L, EstatTipus.Tramitada, false, propietari);
        when(consulta.getJustificantEstat()).thenReturn(JustificantEstat.OK);
        when(historicConsultaRepository.findById(1L)).thenReturn(Optional.of(consulta));
        when(historicConsultaRepository.getOne(1L)).thenReturn(consulta);

        JustificantDto result = historicConsultaService.reintentarGeneracioJustificant(1L, false);

        assertTrue(result.isError());
    }

    @Test
    public void reintentarGeneracioJustificant_adminAmbPropietariDiferent_ok() throws Exception {
        mockAuth("admin1", "ROLE_ADMIN");
        Usuari propietari = usuariAmbCodi("usuari1");
        HistoricConsulta consulta = mockConsulta(1L, EstatTipus.Tramitada, false, propietari);
        when(consulta.getJustificantEstat()).thenReturn(JustificantEstat.OK);
        when(historicConsultaRepository.findById(1L)).thenReturn(Optional.of(consulta));
        when(historicConsultaRepository.getOne(1L)).thenReturn(consulta);
        FitxerDto fitxer = FitxerDto.builder().nom("PBL_1.pdf").contingut(new byte[]{9}).build();
        when(justificantHelper.descarregarFitxerGenerat(consulta, scspHelper, true)).thenReturn(fitxer);

        JustificantDto result = historicConsultaService.reintentarGeneracioJustificant(1L, true);

        assertFalse(result.isError());
    }

    // ------------------------------------------------------------------
    // obtenirJustificantMultipleConcatenat / Zip
    // ------------------------------------------------------------------

    @Test
    public void obtenirJustificantMultipleConcatenat_consultaNoTrobada_llancaException() {
        when(historicConsultaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ConsultaNotFoundException.class,
                () -> historicConsultaService.obtenirJustificantMultipleConcatenat(99L));
    }

    @Test
    public void obtenirJustificantMultipleConcatenat_propietariNoCoincideix_llancaException() {
        mockAuth("usuari2");
        Usuari propietari = usuariAmbCodi("usuari1");
        HistoricConsulta consulta = mockConsulta(1L, EstatTipus.Tramitada, true, propietari);
        when(historicConsultaRepository.findById(1L)).thenReturn(Optional.of(consulta));

        assertThrows(ConsultaNotFoundException.class,
                () -> historicConsultaService.obtenirJustificantMultipleConcatenat(1L));
    }

    @Test
    public void obtenirJustificantMultipleConcatenat_noEsMultiple_llancaException() {
        mockAuth("admin1", "ROLE_ADMIN");
        HistoricConsulta consulta = mockConsulta(1L, EstatTipus.Tramitada, false, null);
        when(historicConsultaRepository.findById(1L)).thenReturn(Optional.of(consulta));

        assertThrows(ConsultaNotFoundException.class,
                () -> historicConsultaService.obtenirJustificantMultipleConcatenat(1L));
    }

    @Test
    public void obtenirJustificantMultipleConcatenat_senseFills_llancaJustificantGeneracioException() {
        mockAuth("admin1", "ROLE_ADMIN");
        HistoricConsulta consulta = mockConsulta(1L, EstatTipus.Tramitada, true, null);
        when(consulta.getFills()).thenReturn(Collections.emptyList());
        when(historicConsultaRepository.findById(1L)).thenReturn(Optional.of(consulta));

        assertThrows(JustificantGeneracioException.class,
                () -> historicConsultaService.obtenirJustificantMultipleConcatenat(1L));
    }

    @Test
    public void obtenirJustificantMultipleZip_consultaNoTrobada_llancaException() {
        when(historicConsultaRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ConsultaNotFoundException.class,
                () -> historicConsultaService.obtenirJustificantMultipleZip(99L));
    }

    @Test
    public void obtenirJustificantMultipleZip_propietariNoCoincideix_llancaException() {
        mockAuth("usuari2");
        Usuari propietari = usuariAmbCodi("usuari1");
        HistoricConsulta consulta = mockConsulta(1L, EstatTipus.Tramitada, true, propietari);
        when(historicConsultaRepository.findById(1L)).thenReturn(Optional.of(consulta));

        assertThrows(ConsultaNotFoundException.class,
                () -> historicConsultaService.obtenirJustificantMultipleZip(1L));
    }

    @Test
    public void obtenirJustificantMultipleZip_noEsMultiple_llancaException() {
        mockAuth("admin1", "ROLE_ADMIN");
        HistoricConsulta consulta = mockConsulta(1L, EstatTipus.Tramitada, false, null);
        when(historicConsultaRepository.findById(1L)).thenReturn(Optional.of(consulta));

        assertThrows(ConsultaNotFoundException.class,
                () -> historicConsultaService.obtenirJustificantMultipleZip(1L));
    }

    @Test
    public void obtenirJustificantMultipleZip_senseFills_retornaZipBuid() throws Exception {
        mockAuth("admin1", "ROLE_ADMIN");
        HistoricConsulta consulta = mockConsulta(1L, EstatTipus.Tramitada, true, null);
        when(consulta.getFills()).thenReturn(Collections.emptyList());
        when(historicConsultaRepository.findById(1L)).thenReturn(Optional.of(consulta));

        FitxerDto result = historicConsultaService.obtenirJustificantMultipleZip(1L);

        assertNotNull(result);
        assertEquals("PBL_PET1.zip", result.getNom());
    }

    @Test
    public void obtenirJustificantMultipleZip_ambFills_retornaZipAmbEntrades() throws Exception {
        mockAuth("admin1", "ROLE_ADMIN");
        HistoricConsulta pare = mockConsulta(1L, EstatTipus.Tramitada, true, null);
        HistoricConsulta fill1 = mockConsulta(2L, EstatTipus.Tramitada, false, null);
        HistoricConsulta fill2 = mockConsulta(3L, EstatTipus.Tramitada, false, null);
        when(pare.getFills()).thenReturn(Arrays.asList(fill1, fill2));
        when(historicConsultaRepository.findById(1L)).thenReturn(Optional.of(pare));
        when(justificantHelper.generar(fill1, scspHelper))
                .thenReturn(FitxerDto.builder().nom("f1.pdf").contingut(new byte[]{1}).build());
        when(justificantHelper.generar(fill2, scspHelper))
                .thenReturn(FitxerDto.builder().nom("f2.pdf").contingut(new byte[]{2}).build());

        FitxerDto result = historicConsultaService.obtenirJustificantMultipleZip(1L);

        assertNotNull(result);
        assertTrue(result.getContingut().length > 0);
        verify(justificantHelper, times(2)).generar(any(), eq(scspHelper));
    }

    // ------------------------------------------------------------------
    // findSimplesByFiltrePaginatPerDelegat / findMultiplesByFiltrePaginatPerDelegat
    // ------------------------------------------------------------------

    @Test
    public void findSimplesByFiltrePaginatPerDelegat_entitatNoTrobada_llancaException() {
        mockAuth("usuari1");
        when(entitatRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntitatNotFoundException.class,
                () -> historicConsultaService.findSimplesByFiltrePaginatPerDelegat(1L, null, PageRequest.of(0, 10)));
    }

    @Test
    public void findSimplesByFiltrePaginatPerDelegat_filtreNull_ok() throws Exception {
        mockAuth("usuari1");
        Entitat entitat = crearEntitat(1L, "AJT001");
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitat));
        Page<LlistatHistoricConsulta> pageEntitats = new PageImpl<>(Collections.emptyList());
        when(llistatHistoricConsultaRepository.findByProcedimentServeiProcedimentEntitatIdAndCreatedBy(
                eq(1L), anyBoolean(), any(), anyBoolean(), anyBoolean(), any())).thenReturn(pageEntitats);
        Page<ConsultaDto> pageDto = new PageImpl<>(Collections.singletonList(new ConsultaDto()));
        when(dtoMappingHelper.pageEntities2pageDto(any(), eq(ConsultaDto.class), any())).thenReturn(pageDto);

        Page<ConsultaDto> result = historicConsultaService.findSimplesByFiltrePaginatPerDelegat(1L, null, PageRequest.of(0, 10));

        assertEquals(1, result.getContent().size());
    }

    @Test
    public void findMultiplesByFiltrePaginatPerDelegat_ambFiltre_ok() throws Exception {
        mockAuth("usuari1");
        Entitat entitat = crearEntitat(1L, "AJT001");
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitat));
        ConsultaFiltreDto filtre = new ConsultaFiltreDto();
        filtre.setScspPeticionId("PET");
        filtre.setEstat(ConsultaDto.EstatTipus.Tramitada);
        filtre.setDataInici(new Date());
        filtre.setDataFi(new Date());
        Page<ConsultaDto> pageDto = new PageImpl<>(Collections.emptyList());
        when(dtoMappingHelper.pageEntities2pageDto(any(), eq(ConsultaDto.class), any())).thenReturn(pageDto);

        Page<ConsultaDto> result = historicConsultaService.findMultiplesByFiltrePaginatPerDelegat(1L, filtre, PageRequest.of(0, 10));

        assertNotNull(result);
        verify(historicConsultaRepository).setSessionOptimizerModeToRule();
    }

    @Test
    public void findMultiplesByFiltrePaginatPerDelegat_dialectPostgres_noCridaSetSessionOptimizer() throws Exception {
        mockAuth("usuari1");
        when(configHelper.getConfig(anyString(), any())).thenReturn("PostgreSQL");
        Entitat entitat = crearEntitat(1L, "AJT001");
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitat));
        ConsultaFiltreDto filtre = new ConsultaFiltreDto();
        Page<ConsultaDto> pageDto = new PageImpl<>(Collections.emptyList());
        when(dtoMappingHelper.pageEntities2pageDto(any(), eq(ConsultaDto.class), any())).thenReturn(pageDto);

        historicConsultaService.findMultiplesByFiltrePaginatPerDelegat(1L, filtre, PageRequest.of(0, 10));

        verify(historicConsultaRepository, never()).setSessionOptimizerModeToRule();
    }

    // ------------------------------------------------------------------
    // findByFiltrePaginatPerAuditor / findByFiltrePerAuditor
    // ------------------------------------------------------------------

    @Test
    public void findByFiltrePaginatPerAuditor_entitatNoTrobada_llancaException() {
        mockAuth("usuari1");
        when(entitatRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntitatNotFoundException.class,
                () -> historicConsultaService.findByFiltrePaginatPerAuditor(1L, null, PageRequest.of(0, 10)));
    }

    @Test
    public void findByFiltrePaginatPerAuditor_entitatUsuariNull_llancaException() {
        mockAuth("usuari1");
        Entitat entitat = crearEntitat(1L, "AJT001");
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitat));
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodi(1L, "usuari1")).thenReturn(null);
        assertThrows(EntitatNotFoundException.class,
                () -> historicConsultaService.findByFiltrePaginatPerAuditor(1L, null, PageRequest.of(0, 10)));
    }

    @Test
    public void findByFiltrePaginatPerAuditor_noEsAuditor_llancaException() {
        mockAuth("usuari1");
        Entitat entitat = crearEntitat(1L, "AJT001");
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitat));
        EntitatUsuari eu = EntitatUsuari.getBuilder(entitat, usuariAmbCodi("usuari1"), "dept", false, false, false, false, true).build();
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodi(1L, "usuari1")).thenReturn(eu);
        assertThrows(EntitatNotFoundException.class,
                () -> historicConsultaService.findByFiltrePaginatPerAuditor(1L, null, PageRequest.of(0, 10)));
    }

    @Test
    public void findByFiltrePaginatPerAuditor_esAuditor_ok() throws Exception {
        mockAuth("usuari1");
        Entitat entitat = crearEntitat(1L, "AJT001");
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitat));
        EntitatUsuari eu = EntitatUsuari.getBuilder(entitat, usuariAmbCodi("usuari1"), "dept", false, false, true, false, true).build();
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodi(1L, "usuari1")).thenReturn(eu);
        Page<LlistatHistoricConsulta> pageEntitats = new PageImpl<>(Collections.emptyList());
        when(llistatHistoricConsultaRepository.findByProcedimentServeiProcedimentEntitatIdAndCreatedBy(
                eq(1L), anyBoolean(), any(), anyBoolean(), anyBoolean(), any())).thenReturn(pageEntitats);
        Page<ConsultaDto> pageDto = new PageImpl<>(Collections.emptyList());
        when(dtoMappingHelper.pageEntities2pageDto(any(), eq(ConsultaDto.class), any())).thenReturn(pageDto);

        Page<ConsultaDto> result = historicConsultaService.findByFiltrePaginatPerAuditor(1L, null, PageRequest.of(0, 10));

        assertNotNull(result);
    }

    @Test
    public void findByFiltrePerAuditor_entitatNoTrobada_llancaException() {
        mockAuth("usuari1");
        when(entitatRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntitatNotFoundException.class,
                () -> historicConsultaService.findByFiltrePerAuditor(1L, null));
    }

    @Test
    public void findByFiltrePerAuditor_noAuditor_llancaException() {
        mockAuth("usuari1");
        Entitat entitat = crearEntitat(1L, "AJT001");
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitat));
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodi(1L, "usuari1")).thenReturn(null);
        assertThrows(EntitatNotFoundException.class,
                () -> historicConsultaService.findByFiltrePerAuditor(1L, null));
    }

    @Test
    public void findByFiltrePerAuditor_esAuditor_retornaContingutPagina() throws Exception {
        mockAuth("usuari1");
        Entitat entitat = crearEntitat(1L, "AJT001");
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitat));
        EntitatUsuari eu = EntitatUsuari.getBuilder(entitat, usuariAmbCodi("usuari1"), "dept", false, false, true, false, true).build();
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodi(1L, "usuari1")).thenReturn(eu);
        Page<LlistatHistoricConsulta> pageEntitats = new PageImpl<>(Collections.emptyList());
        when(llistatHistoricConsultaRepository.findByProcedimentServeiProcedimentEntitatIdAndCreatedBy(
                eq(1L), anyBoolean(), any(), anyBoolean(), anyBoolean(), any())).thenReturn(pageEntitats);
        Page<ConsultaDto> pageDto = new PageImpl<>(Collections.singletonList(new ConsultaDto()));
        when(dtoMappingHelper.pageEntities2pageDto(any(), eq(ConsultaDto.class), any())).thenReturn(pageDto);

        List<ConsultaDto> result = historicConsultaService.findByFiltrePerAuditor(1L, null);

        assertEquals(1, result.size());
    }

    // ------------------------------------------------------------------
    // findByFiltrePaginatPerSuperauditor / findByFiltrePaginatPerAdmin
    // ------------------------------------------------------------------

    @Test
    public void findByFiltrePaginatPerSuperauditor_entitatNoTrobada_llancaException() {
        when(entitatRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntitatNotFoundException.class,
                () -> historicConsultaService.findByFiltrePaginatPerSuperauditor(1L, null, PageRequest.of(0, 10)));
    }

    @Test
    public void findByFiltrePaginatPerSuperauditor_ok() throws Exception {
        Entitat entitat = crearEntitat(1L, "AJT001");
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitat));
        Page<LlistatHistoricConsulta> pageEntitats = new PageImpl<>(Collections.emptyList());
        when(llistatHistoricConsultaRepository.findByProcedimentServeiProcedimentEntitatIdAndCreatedBy(
                eq(1L), anyBoolean(), any(), anyBoolean(), anyBoolean(), any())).thenReturn(pageEntitats);
        Page<ConsultaDto> pageDto = new PageImpl<>(Collections.emptyList());
        when(dtoMappingHelper.pageEntities2pageDto(any(), eq(ConsultaDto.class), any())).thenReturn(pageDto);

        Page<ConsultaDto> result = historicConsultaService.findByFiltrePaginatPerSuperauditor(1L, null, PageRequest.of(0, 10));

        assertNotNull(result);
    }

    @Test
    public void findByFiltrePaginatPerAdmin_filtrePlé_ok() throws Exception {
        ConsultaFiltreDto filtre = new ConsultaFiltreDto();
        filtre.setEntitatId(1L);
        filtre.setScspPeticionId("PET");
        filtre.setProcedimentId(2L);
        filtre.setServeiCodi("SV001");
        filtre.setEstat(ConsultaDto.EstatTipus.Error);
        filtre.setDataInici(new Date());
        filtre.setDataFi(new Date());
        filtre.setTitularNom("Joan");
        filtre.setTitularDocument("12345678A");
        filtre.setFuncionari("func1");
        filtre.setUsuari("usuari1");
        filtre.setRecobriment(true);
        filtre.setMultiple(false);
        Page<ConsultaDto> pageDto = new PageImpl<>(Collections.emptyList());
        when(dtoMappingHelper.pageEntities2pageDto(any(), eq(ConsultaDto.class), any())).thenReturn(pageDto);

        Page<ConsultaDto> result = historicConsultaService.findByFiltrePaginatPerAdmin(filtre, PageRequest.of(0, 10));

        assertNotNull(result);
    }

    @Test
    public void findByFiltrePaginatPerAdmin_filtreBuid_ok() throws Exception {
        ConsultaFiltreDto filtre = new ConsultaFiltreDto();
        Page<ConsultaDto> pageDto = new PageImpl<>(Collections.emptyList());
        when(dtoMappingHelper.pageEntities2pageDto(any(), eq(ConsultaDto.class), any())).thenReturn(pageDto);

        Page<ConsultaDto> result = historicConsultaService.findByFiltrePaginatPerAdmin(filtre, PageRequest.of(0, 10));

        assertNotNull(result);
    }

    // ------------------------------------------------------------------
    // findByFiltrePerOpenData / findByFiltrePerOpenDataV2
    // ------------------------------------------------------------------

    @Test
    public void findByFiltrePerOpenData_totBlanc_ok() throws Exception {
        when(dadesObertesHistoricConsultaRepository.findByOpendata(
                anyBoolean(), isNull(), anyBoolean(), isNull(), anyBoolean(), isNull(),
                anyBoolean(), isNull(), anyBoolean(), isNull())).thenReturn(Collections.emptyList());

        List<DadesObertesRespostaConsulta> result = historicConsultaService.findByFiltrePerOpenData(null, null, null, null, null);

        assertNotNull(result);
    }

    @Test
    public void findByFiltrePerOpenData_ambDades_ok() throws Exception {
        DadesObertesRespostaConsulta dto = mock(DadesObertesRespostaConsulta.class);
        when(dadesObertesHistoricConsultaRepository.findByOpendata(
                eq(false), eq("AJT001"), eq(false), eq("PROC1"), eq(false), eq("SV001"),
                eq(false), any(), eq(false), any())).thenReturn(Collections.singletonList(dto));

        List<DadesObertesRespostaConsulta> result = historicConsultaService.findByFiltrePerOpenData(
                "AJT001", new Date(), new Date(), "PROC1", "SV001");

        assertEquals(1, result.size());
    }

    @Test
    public void findByFiltrePerOpenDataV2_ambProperaPagina_generaUrl() throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.set(2026, Calendar.JUNE, 1);
        ConsultaOpenDataDto dto = ConsultaOpenDataDto.builder()
                .entitatCodi("AJT001")
                .procedimentCodi("PROC1")
                .serveiCodi("SV001")
                .dataInici(cal.getTime())
                .dataFi(cal.getTime())
                .pagina(0)
                .mida(1)
                .appPath("/opendata")
                .build();
        when(dadesObertesHistoricConsultaRepository.countByOpendata(
                anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any()))
                .thenReturn(5);
        when(dadesObertesHistoricConsultaRepository.findByOpendata(
                anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(mock(DadesObertesRespostaConsulta.class))));

        DadesObertesResposta result = historicConsultaService.findByFiltrePerOpenDataV2(dto);

        assertEquals(5, result.getTotalElements());
        assertNotNull(result.getProperaPagina());
        assertTrue(result.getProperaPagina().contains("entitatCodi=AJT001"));
        assertTrue(result.getProperaPagina().contains("procedimentCodi=PROC1"));
        assertTrue(result.getProperaPagina().contains("serveiCodi=SV001"));
    }

    @Test
    public void findByFiltrePerOpenDataV2_senseMesPagines_noGeneraUrl() throws Exception {
        ConsultaOpenDataDto dto = ConsultaOpenDataDto.builder()
                .pagina(0)
                .mida(50)
                .build();
        when(dadesObertesHistoricConsultaRepository.countByOpendata(
                anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any()))
                .thenReturn(1);
        when(dadesObertesHistoricConsultaRepository.findByOpendata(
                anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));

        DadesObertesResposta result = historicConsultaService.findByFiltrePerOpenDataV2(dto);

        assertNull(result.getProperaPagina());
    }

    // ------------------------------------------------------------------
    // findOneDelegat / findOneAuditor / findOneSuperauditor / findOneAdmin
    // ------------------------------------------------------------------

    @Test
    public void findOneDelegat_consultaNoTrobada_llancaException() {
        mockAuth("usuari1");
        when(historicConsultaRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ConsultaNotFoundException.class, () -> historicConsultaService.findOneDelegat(1L));
    }

    @Test
    public void findOneDelegat_propietariNoCoincideix_llancaException() {
        mockAuth("usuari2");
        Usuari propietari = usuariAmbCodi("usuari1");
        HistoricConsulta consulta = mockConsulta(1L, EstatTipus.Tramitada, false, propietari);
        when(historicConsultaRepository.findById(1L)).thenReturn(Optional.of(consulta));
        assertThrows(ConsultaNotFoundException.class, () -> historicConsultaService.findOneDelegat(1L));
    }

    @Test
    public void findOneDelegat_ok_retornaDtoAmbInfoScsp() throws Exception {
        mockAuth("usuari1");
        Usuari propietari = usuariAmbCodi("usuari1");
        HistoricConsulta consulta = mockConsulta(1L, EstatTipus.Tramitada, false, propietari);
        when(historicConsultaRepository.findById(1L)).thenReturn(Optional.of(consulta));
        ConsultaDto dto = consultaDtoAmbEstat("Tramitada");
        when(mapperFacade.map(consulta, ConsultaDto.class)).thenReturn(dto);
        Resposta resposta = new Resposta();
        resposta.setFinalitat("Tramitació expedient");
        resposta.setExpedientId("EXP001");
        resposta.setUnitatTramitadora("Departament TIC");
        resposta.setRespostaData(new Date());
        resposta.setRespostaXml("<resposta/>");
        resposta.setPeticioXml("<peticio/>");
        when(scspHelper.recuperarResposta("PET1", "SOL1", false)).thenReturn(resposta);
        when(scspHelper.getDadesEspecifiquesPeticio("PET1", "SOL1")).thenReturn(Collections.emptyMap());

        ConsultaDto result = historicConsultaService.findOneDelegat(1L);

        assertEquals("Tramitació expedient", result.getFinalitat());
        assertTrue(result.isHiHaPeticio());
        assertTrue(result.isHiHaResposta());
    }

    @Test
    public void findOneDelegat_errorRecuperantResposta_llancaScspException() throws Exception {
        mockAuth("usuari1");
        Usuari propietari = usuariAmbCodi("usuari1");
        HistoricConsulta consulta = mockConsulta(1L, EstatTipus.Error, false, propietari);
        when(historicConsultaRepository.findById(1L)).thenReturn(Optional.of(consulta));
        ConsultaDto dto = consultaDtoAmbEstat("Error");
        when(mapperFacade.map(consulta, ConsultaDto.class)).thenReturn(dto);
        when(scspHelper.recuperarResposta(any(), any(), anyBoolean())).thenThrow(new RuntimeException("no disponible"));

        assertThrows(ScspException.class, () -> historicConsultaService.findOneDelegat(1L));
    }

    @Test
    public void findOneAuditor_consultaNoTrobada_llancaException() {
        when(historicConsultaRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ConsultaNotFoundException.class, () -> historicConsultaService.findOneAuditor(1L));
    }

    @Test
    public void findOneAuditor_noEsAuditor_llancaException() {
        mockAuth("usuari1");
        Entitat entitat = crearEntitat(1L, "AJT001");
        Procediment procediment = crearProcediment(1L, entitat, "PROC1");
        ProcedimentServei ps = crearProcedimentServei(1L, procediment, "SV001");
        HistoricConsulta consulta = mock(HistoricConsulta.class);
        when(consulta.getProcedimentServei()).thenReturn(ps);
        when(historicConsultaRepository.findById(1L)).thenReturn(Optional.of(consulta));
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodi(1L, "usuari1")).thenReturn(null);

        assertThrows(ConsultaNotFoundException.class, () -> historicConsultaService.findOneAuditor(1L));
    }

    @Test
    public void findOneAuditor_esAuditor_ok() throws Exception {
        mockAuth("usuari1");
        Entitat entitat = crearEntitat(1L, "AJT001");
        Procediment procediment = crearProcediment(1L, entitat, "PROC1");
        ProcedimentServei ps = crearProcedimentServei(1L, procediment, "SV001");
        HistoricConsulta consulta = mockConsulta(1L, EstatTipus.Tramitada, false, null);
        when(consulta.getProcedimentServei()).thenReturn(ps);
        when(historicConsultaRepository.findById(1L)).thenReturn(Optional.of(consulta));
        EntitatUsuari eu = EntitatUsuari.getBuilder(entitat, usuariAmbCodi("usuari1"), "dept", false, false, true, false, true).build();
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodi(1L, "usuari1")).thenReturn(eu);
        ConsultaDto dto = consultaDtoAmbEstat("Tramitada");
        when(mapperFacade.map(consulta, ConsultaDto.class)).thenReturn(dto);
        when(scspHelper.recuperarResposta("PET1", "SOL1", false)).thenReturn(null);

        ConsultaDto result = historicConsultaService.findOneAuditor(1L);

        assertNotNull(result);
    }

    @Test
    public void findOneSuperauditor_consultaNoTrobada_llancaException() {
        when(historicConsultaRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ConsultaNotFoundException.class, () -> historicConsultaService.findOneSuperauditor(1L));
    }

    @Test
    public void findOneSuperauditor_ok() throws Exception {
        HistoricConsulta consulta = mockConsulta(1L, EstatTipus.Tramitada, false, null);
        when(historicConsultaRepository.findById(1L)).thenReturn(Optional.of(consulta));
        ConsultaDto dto = consultaDtoAmbEstat("Tramitada");
        when(mapperFacade.map(consulta, ConsultaDto.class)).thenReturn(dto);
        when(scspHelper.recuperarResposta("PET1", "SOL1", false)).thenReturn(null);

        ConsultaDto result = historicConsultaService.findOneSuperauditor(1L);

        assertNotNull(result);
    }

    @Test
    public void findOneAdmin_consultaNoTrobada_llancaException() {
        when(historicConsultaRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ConsultaNotFoundException.class, () -> historicConsultaService.findOneAdmin(1L));
    }

    @Test
    public void findOneAdmin_ok() throws Exception {
        HistoricConsulta consulta = mockConsulta(1L, EstatTipus.Tramitada, true, null);
        when(historicConsultaRepository.findById(1L)).thenReturn(Optional.of(consulta));
        ConsultaDto dto = consultaDtoAmbEstat("Tramitada");
        when(mapperFacade.map(consulta, ConsultaDto.class)).thenReturn(dto);
        Resposta resposta = new Resposta();
        resposta.setPeticioXml("<peticio/>");
        when(scspHelper.recuperarResposta("PET1", "SOL1", true)).thenReturn(resposta);

        ConsultaDto result = historicConsultaService.findOneAdmin(1L);

        assertNotNull(result);
        assertTrue(result.isHiHaPeticio());
        verify(scspHelper, never()).getDadesEspecifiquesPeticio(any(), any());
    }

    // ------------------------------------------------------------------
    // findAmbPare / findEstadistiquesCarrega
    // ------------------------------------------------------------------

    @Test
    public void findAmbPare_pareNoTrobat_llancaException() {
        when(historicConsultaRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ConsultaNotFoundException.class, () -> historicConsultaService.findAmbPare(1L));
    }

    @Test
    public void findAmbPare_ok_retornaFilles() throws Exception {
        HistoricConsulta pare = mockConsulta(1L, EstatTipus.Tramitada, true, null);
        when(historicConsultaRepository.findById(1L)).thenReturn(Optional.of(pare));
        HistoricConsulta fill1 = mockConsulta(2L, EstatTipus.Tramitada, false, null);
        HistoricConsulta fill2 = mockConsulta(3L, EstatTipus.Tramitada, false, null);
        when(historicConsultaRepository.findByPareOrderByScspSolicitudIdAsc(pare)).thenReturn(Arrays.asList(fill1, fill2));
        when(mapperFacade.map(any(HistoricConsulta.class), eq(ConsultaDto.class)))
                .thenReturn(consultaDtoAmbEstat("Tramitada"));
        when(scspHelper.recuperarResposta(any(), any(), anyBoolean())).thenReturn(null);

        List<ConsultaDto> result = historicConsultaService.findAmbPare(1L);

        assertEquals(2, result.size());
    }

    @Test
    public void findEstadistiquesCarrega_delegaAHelper() {
        CarregaDto carrega = new CarregaDto(0L, 0L, null, null, null, null, null, null, null, null, null, null);
        when(peticioScspEstadistiquesHelper.consultaEstadistiques()).thenReturn(Collections.singletonList(carrega));

        List<CarregaDto> result = historicConsultaService.findEstadistiquesCarrega();

        assertEquals(1, result.size());
    }

    // ------------------------------------------------------------------
    // auditoriaGenerarAuditor / auditoriaConsultarAuditor
    // ------------------------------------------------------------------

    @Test
    public void auditoriaGenerarAuditor_entitatNoTrobada_llancaException() {
        when(entitatRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntitatNotFoundException.class,
                () -> historicConsultaService.auditoriaGenerarAuditor(1L, new Date(), new Date(), 5));
    }

    @Test
    public void auditoriaGenerarAuditor_menysConsultesQueElLimit_retornaTotes() throws Exception {
        Entitat entitat = crearEntitat(1L, "AJT001");
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitat));
        HistoricConsulta c1 = mockConsulta(1L, EstatTipus.Tramitada, false, null);
        HistoricConsulta c2 = mockConsulta(2L, EstatTipus.Tramitada, false, null);
        when(historicConsultaRepository.findByEntitatAndDataIniciAndDataFi(eq(entitat), any(), any()))
                .thenReturn(Arrays.asList(c1, c2));

        List<Long> result = historicConsultaService.auditoriaGenerarAuditor(1L, new Date(), new Date(), 5);

        assertEquals(2, result.size());
        assertTrue(result.containsAll(Arrays.asList(1L, 2L)));
    }

    @Test
    public void auditoriaGenerarAuditor_mesConsultesQueElLimit_retornaSubconjunt() throws Exception {
        Entitat entitat = crearEntitat(1L, "AJT001");
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitat));
        List<HistoricConsulta> consultes = new ArrayList<>();
        for (long i = 1; i <= 5; i++) {
            consultes.add(mockConsulta(i, EstatTipus.Tramitada, false, null));
        }
        when(historicConsultaRepository.findByEntitatAndDataIniciAndDataFi(eq(entitat), any(), any()))
                .thenReturn(consultes);

        List<Long> result = historicConsultaService.auditoriaGenerarAuditor(1L, new Date(), new Date(), 2);

        assertEquals(2, result.size());
    }

    @Test
    public void auditoriaConsultarAuditor_entitatNoTrobada_llancaException() {
        when(entitatRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntitatNotFoundException.class,
                () -> historicConsultaService.auditoriaConsultarAuditor(1L, Arrays.asList(1L, 2L)));
    }

    @Test
    public void auditoriaConsultarAuditor_ok() throws Exception {
        Entitat entitat = crearEntitat(1L, "AJT001");
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitat));
        HistoricConsulta c1 = mockConsulta(1L, EstatTipus.Tramitada, false, null);
        when(historicConsultaRepository.findByEntitatAndIds(eq(entitat), anyList())).thenReturn(Collections.singletonList(c1));
        when(mapperFacade.map(c1, ConsultaDto.class)).thenReturn(consultaDtoAmbEstat("Tramitada"));
        when(scspHelper.recuperarResposta(any(), any(), anyBoolean())).thenReturn(null);

        List<ConsultaDto> result = historicConsultaService.auditoriaConsultarAuditor(1L, Collections.singletonList(1L));

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getEntitatId());
    }

    // ------------------------------------------------------------------
    // auditoriaGenerarSuperauditor / auditoriaConsultarSuperauditor
    // ------------------------------------------------------------------

    @Test
    public void auditoriaGenerarSuperauditor_filtraEntitatsSenseConsultes() {
        Entitat entitatAmbConsultes = crearEntitat(1L, "AJT001");
        Entitat entitatSenseConsultes = crearEntitat(2L, "AJT002");
        when(entitatRepository.findAll()).thenReturn(new ArrayList<>(Arrays.asList(entitatAmbConsultes, entitatSenseConsultes)));
        when(historicConsultaRepository.countByEntitat(
                anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any()))
                .thenReturn(Collections.singletonList(new Object[]{1L, 3L}));
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitatAmbConsultes));
        HistoricConsulta consulta10 = mockConsulta(10L, EstatTipus.Tramitada, false, null);
        when(historicConsultaRepository.findByEntitatAndDataIniciAndDataFi(eq(entitatAmbConsultes), any(), any()))
                .thenReturn(Collections.singletonList(consulta10));

        List<Long> result = historicConsultaService.auditoriaGenerarSuperauditor(new Date(), new Date(), 5, 5);

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0));
    }

    @Test
    public void auditoriaGenerarSuperauditor_masEntitatsQueElLimit_seleccionaSubconjunt() {
        List<Entitat> entitats = new ArrayList<>();
        List<Object[]> counts = new ArrayList<>();
        for (long i = 1; i <= 4; i++) {
            entitats.add(crearEntitat(i, "AJT00" + i));
            counts.add(new Object[]{i, 1L});
        }
        when(entitatRepository.findAll()).thenReturn(entitats);
        when(historicConsultaRepository.countByEntitat(
                anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any()))
                .thenReturn(counts);
        for (Entitat e : entitats) {
            when(entitatRepository.findById(e.getId())).thenReturn(Optional.of(e));
        }
        HistoricConsulta consulta100 = mockConsulta(100L, EstatTipus.Tramitada, false, null);
        when(historicConsultaRepository.findByEntitatAndDataIniciAndDataFi(any(Entitat.class), any(), any()))
                .thenReturn(Collections.singletonList(consulta100));

        List<Long> result = historicConsultaService.auditoriaGenerarSuperauditor(new Date(), new Date(), 2, 5);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void auditoriaConsultarSuperauditor_agrupaPerEntitat() throws Exception {
        HistoricConsulta c1 = mockConsulta(1L, EstatTipus.Tramitada, false, null);
        HistoricConsulta c2 = mockConsulta(2L, EstatTipus.Tramitada, false, null);
        when(historicConsultaRepository.findByIds(anyList()))
                .thenReturn(Arrays.asList(new Object[]{c1, 1L}, new Object[]{c2, 2L}));
        Entitat entitat1 = crearEntitat(1L, "AJT001");
        Entitat entitat2 = crearEntitat(2L, "AJT002");
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitat1));
        when(entitatRepository.findById(2L)).thenReturn(Optional.of(entitat2));
        when(mapperFacade.map(entitat1, es.caib.pinbal.logic.intf.dto.EntitatDto.class))
                .thenReturn(es.caib.pinbal.logic.intf.dto.EntitatDto.builder().id(1L).codi("AJT001").build());
        when(mapperFacade.map(entitat2, es.caib.pinbal.logic.intf.dto.EntitatDto.class))
                .thenReturn(es.caib.pinbal.logic.intf.dto.EntitatDto.builder().id(2L).codi("AJT002").build());
        when(mapperFacade.map(any(HistoricConsulta.class), eq(ConsultaDto.class))).thenReturn(consultaDtoAmbEstat("Tramitada"));
        when(scspHelper.recuperarResposta(any(), any(), anyBoolean())).thenReturn(null);

        var result = historicConsultaService.auditoriaConsultarSuperauditor(Arrays.asList(1L, 2L));

        assertEquals(2, result.size());
    }

    // ------------------------------------------------------------------
    // informeGeneralEstat
    // ------------------------------------------------------------------

    @Test
    public void informeGeneralEstat_calculaPeticionsCorrectesIErronies() {
        Entitat entitat = crearEntitat(1L, "AJT001");
        Procediment procediment = crearProcediment(1L, entitat, "PROC1");
        ProcedimentServei ps = crearProcedimentServei(1L, procediment, "SV001");
        when(procedimentServeiRepository.findAll(any(org.springframework.data.domain.Sort.class)))
                .thenReturn(Collections.singletonList(ps));
        Servicio servicio = mock(Servicio.class);
        when(servicio.getCodCertificado()).thenReturn("SV001");
        when(servicio.getDescripcion()).thenReturn("Servei prova");
        EmisorCertificado emisor = mock(EmisorCertificado.class);
        when(emisor.getCif()).thenReturn("Q1234567A");
        when(servicio.getEmisor()).thenReturn(emisor);
        when(scspHelper.getServicio("SV001")).thenReturn(servicio);
        when(scspHelper.getEmisorNombre("Q1234567A")).thenReturn("Ministeri Emissor");
        List<Object[]> consultes = Arrays.asList(
                new Object[]{1L, 1L, "SV001", EstatTipus.Error, 2L},
                new Object[]{1L, 1L, "SV001", EstatTipus.Tramitada, 3L});
        when(historicConsultaRepository.countGroupByProcedimentServeiEstat(any(), any())).thenReturn(consultes);
        MutableAcl acl = mock(MutableAcl.class);
        AccessControlEntry ace = mock(AccessControlEntry.class);
        when(ace.getSid()).thenReturn(new PrincipalSid("usuari1"));
        when(acl.getEntries()).thenReturn(Collections.singletonList(ace));
        when(aclService.readAclById(any())).thenReturn(acl);

        List<InformeGeneralEstatDto> result = historicConsultaService.informeGeneralEstat(new Date(), new Date());

        assertEquals(1, result.size());
        InformeGeneralEstatDto dto = result.get(0);
        assertEquals(2, dto.getPeticionsErronees());
        assertEquals(3, dto.getPeticionsCorrectes());
        assertEquals(1, dto.getServeiUsuaris());
        assertEquals("Ministeri Emissor", dto.getServeiEmisor().getNom());
    }

    @Test
    public void informeGeneralEstat_aclNoTrobada_usuarisZero() {
        Entitat entitat = crearEntitat(1L, "AJT001");
        Procediment procediment = crearProcediment(1L, entitat, "PROC1");
        ProcedimentServei ps = crearProcedimentServei(1L, procediment, "SV001");
        when(procedimentServeiRepository.findAll(any(org.springframework.data.domain.Sort.class)))
                .thenReturn(Collections.singletonList(ps));
        Servicio servicio = mock(Servicio.class);
        when(scspHelper.getServicio("SV001")).thenReturn(servicio);
        when(historicConsultaRepository.countGroupByProcedimentServeiEstat(any(), any())).thenReturn(Collections.emptyList());
        when(aclService.readAclById(any())).thenThrow(new NotFoundException("no trobada"));

        List<InformeGeneralEstatDto> result = historicConsultaService.informeGeneralEstat(new Date(), new Date());

        assertEquals(1, result.size());
        assertEquals(0, result.get(0).getServeiUsuaris());
        assertEquals(0, result.get(0).getPeticionsCorrectes());
    }

    // ------------------------------------------------------------------
    // arxivarConsultesAntigues
    // ------------------------------------------------------------------

    @Test
    public void arxivarConsultesAntigues_dialectPostgres_ok() {
        when(configHelper.getConfig(eq("es.caib.pinbal.hibernate.dialect"), any())).thenReturn("PostgreSQL");
        when(configHelper.getConfigAsInt(anyString(), anyInt())).thenReturn(180);
        when(historicConsultaRepository.arxivaConsultesPostgres(180)).thenReturn(3);
        when(dadesObertesHistoricConsultaRepository.arxivaConsultesPostgres(180)).thenReturn(3);
        when(llistatHistoricConsultaRepository.arxivaConsultesPostgres(180)).thenReturn(3);
        when(historicConsultaRepository.purgaConsultes(180)).thenReturn(3);
        when(dadesObertesHistoricConsultaRepository.purgaConsultes(180)).thenReturn(3);
        when(llistatHistoricConsultaRepository.purgaConsultes(180)).thenReturn(3);

        assertDoesNotThrow(historicConsultaService::arxivarConsultesAntigues);

        verify(historicConsultaRepository).arxivaConsultesPostgres(180);
        verify(historicConsultaRepository, never()).arxivaConsultesOracle(anyInt());
    }

    @Test
    public void arxivarConsultesAntigues_dialectOracle_ok() {
        when(configHelper.getConfig(eq("es.caib.pinbal.hibernate.dialect"), any())).thenReturn("Oracle");
        when(configHelper.getConfigAsInt(anyString(), anyInt())).thenReturn(90);
        when(historicConsultaRepository.arxivaConsultesOracle(90)).thenReturn(0);
        when(dadesObertesHistoricConsultaRepository.arxivaConsultesOracle(90)).thenReturn(0);
        when(llistatHistoricConsultaRepository.arxivaConsultesOracle(90)).thenReturn(0);

        assertDoesNotThrow(historicConsultaService::arxivarConsultesAntigues);

        verify(historicConsultaRepository, never()).purgaConsultes(anyInt());
    }

    @Test
    public void arxivarConsultesAntigues_desajustEnArxivadesVsEliminades_llancaException() {
        when(configHelper.getConfig(eq("es.caib.pinbal.hibernate.dialect"), any())).thenReturn("Oracle");
        when(configHelper.getConfigAsInt(anyString(), anyInt())).thenReturn(90);
        when(historicConsultaRepository.arxivaConsultesOracle(90)).thenReturn(5);
        when(dadesObertesHistoricConsultaRepository.arxivaConsultesOracle(90)).thenReturn(5);
        when(llistatHistoricConsultaRepository.arxivaConsultesOracle(90)).thenReturn(5);
        when(historicConsultaRepository.purgaConsultes(90)).thenReturn(2);

        assertThrows(RuntimeException.class, historicConsultaService::arxivarConsultesAntigues);
    }

    // ------------------------------------------------------------------
    // descarregarXmlTokensZip
    // ------------------------------------------------------------------

    @Test
    public void descarregarXmlTokensZip_consultaNoTrobada_llancaException() {
        when(historicConsultaRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ConsultaNotFoundException.class, () -> historicConsultaService.descarregarXmlTokensZip(1L));
    }

    @Test
    public void descarregarXmlTokensZip_senseTokens_retornaNull() throws Exception {
        HistoricConsulta consulta = mockConsulta(1L, EstatTipus.Tramitada, false, null);
        when(historicConsultaRepository.findById(1L)).thenReturn(Optional.of(consulta));
        when(tokenRepository.findByIdPeticionOrderByTipoMensajeAsc("PET1")).thenReturn(Collections.emptyList());

        FitxerDto result = historicConsultaService.descarregarXmlTokensZip(1L);

        assertNull(result);
    }

    @Test
    public void descarregarXmlTokensZip_ambTokens_generaZipAmbTotesLesCarpetes() throws Exception {
        HistoricConsulta consulta = mockConsulta(1L, EstatTipus.Tramitada, false, null);
        when(historicConsultaRepository.findById(1L)).thenReturn(Optional.of(consulta));
        List<ScspToken> tokens = new ArrayList<>();
        tokens.add(crearToken("PET1", ScspTokenId.PETICION, "<peticion/>"));
        tokens.add(crearToken("PET1", ScspTokenId.CONFIRMACION_PETICION, "<confirmacion/>"));
        tokens.add(crearToken("PET1", ScspTokenId.SOLICITUD_RESPUESTA, "<solicitud/>"));
        tokens.add(crearToken("PET1", ScspTokenId.RESPUESTA, "<respuesta/>"));
        tokens.add(crearToken("PET1", ScspTokenId.FAULT, "<fault/>"));
        tokens.add(crearToken("PET1", 99, "<altres/>"));
        tokens.add(crearToken("PET1", ScspTokenId.PETICION, "<peticion2/>"));
        when(tokenRepository.findByIdPeticionOrderByTipoMensajeAsc("PET1")).thenReturn(tokens);

        FitxerDto result = historicConsultaService.descarregarXmlTokensZip(1L);

        assertNotNull(result);
        assertEquals("XML_PET1.zip", result.getNom());
        assertTrue(result.getContingut().length > 0);
    }

    private ScspToken crearToken(String idPeticion, Integer tipoMensaje, String datos) {
        ScspToken t = mock(ScspToken.class);
        when(t.getIdPeticion()).thenReturn(idPeticion);
        when(t.getTipoMensaje()).thenReturn(tipoMensaje);
        when(t.getDatos()).thenReturn(datos);
        return t;
    }
}
