package es.caib.pinbal.logic.service;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import es.caib.pinbal.client.dadesobertes.DadesObertesResposta;
import es.caib.pinbal.client.dadesobertes.DadesObertesRespostaConsulta;
import es.caib.pinbal.logic.helper.*;
import es.caib.pinbal.logic.helper.mock.JustificantHelperFactory;
import es.caib.pinbal.logic.intf.dto.*;
import es.caib.pinbal.logic.intf.service.exception.ConsultaNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.EntitatNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.JustificantGeneracioException;
import es.caib.pinbal.persist.entity.*;
import es.caib.pinbal.persist.entity.llistat.LlistatConsulta;
import es.caib.pinbal.persist.repository.*;
import es.caib.pinbal.persist.repository.dadesobertes.DadesObertesConsultaRepository;
import es.caib.pinbal.persist.repository.explotacio.ExplotConsultaDimensioRepository;
import es.caib.pinbal.persist.repository.explotacio.ExplotConsultaFetsRepository;
import es.caib.pinbal.persist.repository.explotacio.ExplotTempsRepository;
import es.caib.pinbal.persist.repository.llistat.LlistatConsultaRepository;
import es.caib.pinbal.scsp.ScspHelper;
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
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.ByteArrayOutputStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class ConsultaServiceImplQueryTest {

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
    @Mock private JustificantHelper justificantHelper;

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
        when(justificantHelperFactory.getJustificantHelper()).thenReturn(justificantHelper);
        auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("usuari1");
        when(auth.getAuthorities()).thenReturn(Collections.emptyList());
        securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);
    }

	private void ferUsuariAdmin() {
		doReturn(Collections.singletonList(new SimpleGrantedAuthority("PBL_ADMIN")))
			.when(auth)
			.getAuthorities();
	}

    private Consulta consultaAmbPropietari(String usuariCodi) {
        Consulta consulta = mock(Consulta.class);
        Usuari usuari = mock(Usuari.class);
        when(usuari.getCodi()).thenReturn(usuariCodi);
        when(consulta.getCreatedBy()).thenReturn(Optional.of(usuari));
        return consulta;
    }

    private ConsultaDto consultaDtoNoError() {
        ConsultaDto dto = new ConsultaDto();
        dto.setEstat(EstatTipus.Pendent.name());
        return dto;
    }

    private byte[] pdfValid() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document();
        PdfWriter.getInstance(doc, baos);
        doc.open();
        doc.add(new Paragraph("justificant"));
        doc.close();
        return baos.toByteArray();
    }

    private Page<LlistatConsulta> stubFindByCreatedByAndFiltrePaginat(Page<LlistatConsulta> page) {
        when(llistatConsultaRepository.findByCreatedByAndFiltrePaginat(
                any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(),
                anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(),
                anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), anyBoolean(), any()))
                .thenReturn(page);
        return page;
    }

    private Page<LlistatConsulta> stubFindByProcedimentServeiProcedimentEntitatIdAndCreatedBy(Page<LlistatConsulta> page) {
        when(llistatConsultaRepository.findByProcedimentServeiProcedimentEntitatIdAndCreatedBy(
                anyLong(), anyBoolean(), any(), any(), anyBoolean(), any()))
                .thenReturn(page);
        return page;
    }

    private Page<LlistatConsulta> stubFindByFiltrePaginatAdmin(Page<LlistatConsulta> page) {
        when(llistatConsultaRepository.findByFiltrePaginatAdmin(
                anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(),
                anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(),
                anyBoolean(), any(), any(), any(), any(), any(), any()))
                .thenReturn(page);
        return page;
    }

    // ---------- obtenirJustificant(Long, boolean) ----------

    @Test
    public void obtenirJustificant_consultaNoTrobada_llancaException() {
        when(consultaRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ConsultaNotFoundException.class, () -> consultaService.obtenirJustificant(1L, false));
    }

    @Test
    public void obtenirJustificant_noAdmin_noEsPropietari_llancaException() {
        Consulta consulta = consultaAmbPropietari("altre-usuari");
        when(consultaRepository.findById(1L)).thenReturn(Optional.of(consulta));
        assertThrows(ConsultaNotFoundException.class, () -> consultaService.obtenirJustificant(1L, false));
    }

    @Test
    public void obtenirJustificant_estatNoTramitada_llancaJustificantGeneracioException() {
        Consulta consulta = consultaAmbPropietari("usuari1");
        when(consulta.getEstat()).thenReturn(EstatTipus.Processant);
        when(consultaRepository.findById(1L)).thenReturn(Optional.of(consulta));
        assertThrows(JustificantGeneracioException.class, () -> consultaService.obtenirJustificant(1L, false));
    }

    @Test
    public void obtenirJustificant_admin_tramitada_descarregaCorrecta() throws Exception {
        Consulta consulta = consultaAmbPropietari("usuari1");
        when(consulta.getId()).thenReturn(1L);
        when(consulta.getEstat()).thenReturn(EstatTipus.Tramitada);
        when(consulta.getJustificantEstat()).thenReturn(JustificantEstat.OK);
        when(consultaRepository.findById(1L)).thenReturn(Optional.of(consulta));
        when(consultaRepository.getOne(1L)).thenReturn(consulta);
        FitxerDto fitxer = FitxerDto.builder().nom("just.pdf").contentType("application/pdf").contingut(new byte[]{1, 2, 3}).build();
        when(justificantHelper.descarregarFitxerGenerat(eq(consulta), eq(scspHelper), eq(true))).thenReturn(fitxer);

        JustificantDto resultat = consultaService.obtenirJustificant(1L, true);

        assertFalse(resultat.isError());
        assertEquals("just.pdf", resultat.getNom());
        assertEquals(JustificantDto.class, resultat.getClass());
    }

    @Test
    public void obtenirJustificant_admin_tramitada_justificantErrorGeneracio() throws Exception {
        Consulta consulta = consultaAmbPropietari("usuari1");
        when(consulta.getId()).thenReturn(1L);
        when(consulta.getEstat()).thenReturn(EstatTipus.Tramitada);
        when(consulta.getJustificantEstat()).thenReturn(JustificantEstat.PENDENT).thenReturn(JustificantEstat.ERROR);
        when(consultaRepository.findById(1L)).thenReturn(Optional.of(consulta));
        when(consultaRepository.getOne(1L)).thenReturn(consulta);

        JustificantDto resultat = consultaService.obtenirJustificant(1L, true);

        assertTrue(resultat.isError());
    }

    // ---------- obtenirJustificant(idpeticion, idsolicitud, ...) ----------

    @Test
    public void obtenirJustificantPerPeticio_noTrobada_llancaException() {
        when(consultaRepository.findByScspPeticionIdAndScspSolicitudId("P1", "S1")).thenReturn(null);
        assertThrows(ConsultaNotFoundException.class,
                () -> consultaService.obtenirJustificant("P1", "S1", true, true));
    }

    @Test
    public void obtenirJustificantPerPeticio_noTramitada_llancaJustificantGeneracioException() {
        Consulta consulta = mock(Consulta.class);
        when(consulta.getEstat()).thenReturn(EstatTipus.Pendent);
        when(consultaRepository.findByScspPeticionIdAndScspSolicitudId("P1", "S1")).thenReturn(consulta);
        assertThrows(JustificantGeneracioException.class,
                () -> consultaService.obtenirJustificant("P1", "S1", true, true));
    }

    // ---------- reintentarGeneracioJustificant ----------

    @Test
    public void reintentarGeneracioJustificant_consultaNoTrobada_llancaException() {
        when(consultaRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ConsultaNotFoundException.class,
                () -> consultaService.reintentarGeneracioJustificant(1L, false, false));
    }

    @Test
    public void reintentarGeneracioJustificant_noAdmin_noPropietari_llancaException() {
        Consulta consulta = consultaAmbPropietari("altre");
        when(consultaRepository.findById(1L)).thenReturn(Optional.of(consulta));
        assertThrows(ConsultaNotFoundException.class,
                () -> consultaService.reintentarGeneracioJustificant(1L, false, false));
    }

    @Test
    public void reintentarGeneracioJustificant_admin_noDescarregar_justificantOkArxiu() throws Exception {
        Consulta consulta = consultaAmbPropietari("usuari1");
        when(consulta.getId()).thenReturn(1L);
        when(consulta.getEstat()).thenReturn(EstatTipus.Tramitada);
        when(consulta.getJustificantEstat()).thenReturn(JustificantEstat.PENDENT).thenReturn(JustificantEstat.OK_NO_CUSTODIA);
        when(consulta.getArxiuDocumentUuid()).thenReturn("uuid-123");
        when(consulta.getScspPeticionId()).thenReturn("PET001");
        when(consultaRepository.findById(1L)).thenReturn(Optional.of(consulta));
        when(consultaRepository.getOne(1L)).thenReturn(consulta);
        // reintentarGeneracioJustificant crida obtenirJustificantComu amb versioImprimible=true
        // sempre, per tant el camí "sense descarregar" recupera el CSV via pluginHelper, no l'uuid.
        es.caib.pluginsib.arxiu.api.Document documentArxiu = new es.caib.pluginsib.arxiu.api.Document();
        es.caib.pluginsib.arxiu.api.DocumentMetadades metadades = new es.caib.pluginsib.arxiu.api.DocumentMetadades();
        metadades.setCsv("CSV-123");
        documentArxiu.setMetadades(metadades);
        when(pluginHelper.arxiuDocumentConsultar(eq("PET001"), eq("uuid-123"), any(), eq(false), eq(false)))
                .thenReturn(documentArxiu);

        JustificantDto resultat = consultaService.reintentarGeneracioJustificant(1L, false, true);

        assertEquals("CSV-123", resultat.getArxiuCsv());
        assertFalse(resultat.isError());
    }

    // ---------- obtenirJustificantMultipleConcatenat ----------

    @Test
    public void obtenirJustificantMultipleConcatenat_consultaNoTrobada_llancaException() {
        when(consultaRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ConsultaNotFoundException.class, () -> consultaService.obtenirJustificantMultipleConcatenat(1L));
    }

    @Test
    public void obtenirJustificantMultipleConcatenat_noPropietariNiAdmin_llancaException() {
        Consulta consulta = consultaAmbPropietari("altre");
        when(consultaRepository.findById(1L)).thenReturn(Optional.of(consulta));
        assertThrows(ConsultaNotFoundException.class, () -> consultaService.obtenirJustificantMultipleConcatenat(1L));
    }

    @Test
    public void obtenirJustificantMultipleConcatenat_noEsMultiple_llancaException() {
        Consulta consulta = consultaAmbPropietari("usuari1");
        when(consulta.isMultiple()).thenReturn(false);
        when(consultaRepository.findById(1L)).thenReturn(Optional.of(consulta));
        assertThrows(ConsultaNotFoundException.class, () -> consultaService.obtenirJustificantMultipleConcatenat(1L));
    }

    @Test
    public void obtenirJustificantMultipleConcatenat_correcte_generaPdfConcatenat() throws Exception {
        Consulta consulta = consultaAmbPropietari("usuari1");
        when(consulta.isMultiple()).thenReturn(true);
        when(consulta.getScspPeticionId()).thenReturn("PET001");
        Consulta filla = mock(Consulta.class);
        when(consulta.getFills()).thenReturn(Collections.singletonList(filla));
        when(consultaRepository.findById(1L)).thenReturn(Optional.of(consulta));
        FitxerDto fitxerFilla = FitxerDto.builder().nom("filla.pdf").contingut(pdfValid()).build();
        when(justificantHelper.generar(eq(filla), eq(scspHelper))).thenReturn(fitxerFilla);

        FitxerDto resultat = consultaService.obtenirJustificantMultipleConcatenat(1L);

        assertEquals("PBL_PET001.pdf", resultat.getNom());
        assertNotNull(resultat.getContingut());
        assertTrue(resultat.getContingut().length > 0);
    }

    @Test
    public void obtenirJustificantMultipleConcatenat_errorGenerant_llancaJustificantGeneracioException() throws Exception {
        Consulta consulta = consultaAmbPropietari("usuari1");
        when(consulta.isMultiple()).thenReturn(true);
        Consulta filla = mock(Consulta.class);
        when(consulta.getFills()).thenReturn(Collections.singletonList(filla));
        when(consultaRepository.findById(1L)).thenReturn(Optional.of(consulta));
        when(justificantHelper.generar(eq(filla), eq(scspHelper))).thenThrow(new RuntimeException("boom"));

        assertThrows(JustificantGeneracioException.class, () -> consultaService.obtenirJustificantMultipleConcatenat(1L));
    }

    // ---------- obtenirJustificantMultipleZip ----------

    @Test
    public void obtenirJustificantMultipleZip_consultaNoTrobada_llancaException() {
        when(consultaRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ConsultaNotFoundException.class, () -> consultaService.obtenirJustificantMultipleZip(1L));
    }

    @Test
    public void obtenirJustificantMultipleZip_noEsMultiple_llancaException() {
        Consulta consulta = consultaAmbPropietari("usuari1");
        when(consulta.isMultiple()).thenReturn(false);
        when(consultaRepository.findById(1L)).thenReturn(Optional.of(consulta));
        assertThrows(ConsultaNotFoundException.class, () -> consultaService.obtenirJustificantMultipleZip(1L));
    }

    @Test
    public void obtenirJustificantMultipleZip_correcte_generaZip() throws Exception {
        Consulta consulta = consultaAmbPropietari("usuari1");
        when(consulta.isMultiple()).thenReturn(true);
        when(consulta.getScspPeticionId()).thenReturn("PET002");
        Consulta filla = mock(Consulta.class);
        when(consulta.getFills()).thenReturn(Collections.singletonList(filla));
        when(consultaRepository.findById(1L)).thenReturn(Optional.of(consulta));
        FitxerDto fitxerFilla = FitxerDto.builder().nom("filla.pdf").contingut(new byte[]{9, 8, 7}).build();
        when(justificantHelper.generar(eq(filla), eq(scspHelper))).thenReturn(fitxerFilla);

        FitxerDto resultat = consultaService.obtenirJustificantMultipleZip(1L);

        assertEquals("PBL_PET002.zip", resultat.getNom());
        assertNotNull(resultat.getContingut());
        assertTrue(resultat.getContingut().length > 0);
    }

    // ---------- findSimplesByFiltrePaginatPerDelegat ----------

    @Test
    public void findSimplesByFiltrePaginatPerDelegat_entitatNoTrobada_llancaException() {
        when(entitatRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntitatNotFoundException.class,
                () -> consultaService.findSimplesByFiltrePaginatPerDelegat(1L, null, PageRequest.of(0, 10)));
    }

    @Test
    public void findSimplesByFiltrePaginatPerDelegat_correcte_retornaPagina() throws Exception {
        Entitat entitat = mock(Entitat.class);
        when(entitat.getId()).thenReturn(1L);
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitat));
        Page<LlistatConsulta> page = stubFindByProcedimentServeiProcedimentEntitatIdAndCreatedBy(new PageImpl<>(Collections.emptyList()));
        Page<ConsultaDto> pageDto = new PageImpl<>(Collections.emptyList());
        when(dtoMappingHelper.pageEntities2pageDto(eq(page), eq(ConsultaDto.class), any())).thenReturn(pageDto);

        Page<ConsultaDto> resultat = consultaService.findSimplesByFiltrePaginatPerDelegat(1L, null, PageRequest.of(0, 10));

        assertNotNull(resultat);
        assertEquals(0, resultat.getTotalElements());
    }

    // ---------- findMultiplesByFiltrePaginatPerDelegat ----------

    @Test
    public void findMultiplesByFiltrePaginatPerDelegat_entitatNoTrobada_llancaException() {
        when(entitatRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(EntitatNotFoundException.class,
                () -> consultaService.findMultiplesByFiltrePaginatPerDelegat(2L, null, PageRequest.of(0, 10)));
    }

    @Test
    public void findMultiplesByFiltrePaginatPerDelegat_ambFiltre_retornaPagina() throws Exception {
        Entitat entitat = mock(Entitat.class);
        when(entitat.getId()).thenReturn(2L);
        when(entitatRepository.findById(2L)).thenReturn(Optional.of(entitat));
        ConsultaFiltreDto filtre = new ConsultaFiltreDto();
        filtre.setTitularNom("Joan Fuster");
        Page<LlistatConsulta> page = stubFindByCreatedByAndFiltrePaginat(new PageImpl<>(Collections.emptyList()));
        Page<ConsultaDto> pageDto = new PageImpl<>(Collections.emptyList());
        when(dtoMappingHelper.pageEntities2pageDto(eq(page), eq(ConsultaDto.class), any())).thenReturn(pageDto);

        Page<ConsultaDto> resultat = consultaService.findMultiplesByFiltrePaginatPerDelegat(2L, filtre, PageRequest.of(0, 10));

        assertNotNull(resultat);
    }

    // ---------- findByFiltrePaginatPerAuditor ----------

    @Test
    public void findByFiltrePaginatPerAuditor_entitatNoTrobada_llancaException() {
        when(entitatRepository.findById(3L)).thenReturn(Optional.empty());
        assertThrows(EntitatNotFoundException.class,
                () -> consultaService.findByFiltrePaginatPerAuditor(3L, null, PageRequest.of(0, 10)));
    }

    @Test
    public void findByFiltrePaginatPerAuditor_noEsAuditor_llancaException() {
        Entitat entitat = mock(Entitat.class);
        when(entitat.getId()).thenReturn(3L);
        when(entitatRepository.findById(3L)).thenReturn(Optional.of(entitat));
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodi(3L, "usuari1")).thenReturn(null);
        assertThrows(EntitatNotFoundException.class,
                () -> consultaService.findByFiltrePaginatPerAuditor(3L, null, PageRequest.of(0, 10)));
    }

    @Test
    public void findByFiltrePaginatPerAuditor_correcte_retornaPagina() throws Exception {
        Entitat entitat = mock(Entitat.class);
        when(entitat.getId()).thenReturn(3L);
        when(entitatRepository.findById(3L)).thenReturn(Optional.of(entitat));
        EntitatUsuari entitatUsuari = mock(EntitatUsuari.class);
        when(entitatUsuari.isAuditor()).thenReturn(true);
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodi(3L, "usuari1")).thenReturn(entitatUsuari);
        Page<LlistatConsulta> page = stubFindByProcedimentServeiProcedimentEntitatIdAndCreatedBy(new PageImpl<>(Collections.emptyList()));
        Page<ConsultaDto> pageDto = new PageImpl<>(Collections.emptyList());
        when(dtoMappingHelper.pageEntities2pageDto(eq(page), eq(ConsultaDto.class), any())).thenReturn(pageDto);

        Page<ConsultaDto> resultat = consultaService.findByFiltrePaginatPerAuditor(3L, null, PageRequest.of(0, 10));

        assertNotNull(resultat);
    }

    // ---------- findByFiltrePerAuditor ----------

    @Test
    public void findByFiltrePerAuditor_entitatNoTrobada_llancaException() {
        when(entitatRepository.findById(4L)).thenReturn(Optional.empty());
        assertThrows(EntitatNotFoundException.class,
                () -> consultaService.findByFiltrePerAuditor(4L, null));
    }

    @Test
    public void findByFiltrePerAuditor_noEsAuditor_llancaException() {
        Entitat entitat = mock(Entitat.class);
        when(entitat.getId()).thenReturn(4L);
        when(entitatRepository.findById(4L)).thenReturn(Optional.of(entitat));
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodi(4L, "usuari1")).thenReturn(null);
        assertThrows(EntitatNotFoundException.class,
                () -> consultaService.findByFiltrePerAuditor(4L, null));
    }

    @Test
    public void findByFiltrePerAuditor_correcte_retornaLlista() throws Exception {
        Entitat entitat = mock(Entitat.class);
        when(entitat.getId()).thenReturn(4L);
        when(entitatRepository.findById(4L)).thenReturn(Optional.of(entitat));
        EntitatUsuari entitatUsuari = mock(EntitatUsuari.class);
        when(entitatUsuari.isAuditor()).thenReturn(true);
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodi(4L, "usuari1")).thenReturn(entitatUsuari);
        Page<LlistatConsulta> page = stubFindByProcedimentServeiProcedimentEntitatIdAndCreatedBy(new PageImpl<>(Collections.emptyList()));
        List<ConsultaDto> continguts = new ArrayList<>();
        continguts.add(new ConsultaDto());
        Page<ConsultaDto> pageDto = new PageImpl<>(continguts);
        when(dtoMappingHelper.pageEntities2pageDto(eq(page), eq(ConsultaDto.class), any())).thenReturn(pageDto);

        List<ConsultaDto> resultat = consultaService.findByFiltrePerAuditor(4L, null);

        assertEquals(1, resultat.size());
    }

    // ---------- findByFiltrePaginatPerSuperauditor ----------

    @Test
    public void findByFiltrePaginatPerSuperauditor_entitatNoTrobada_llancaException() {
        when(entitatRepository.findById(5L)).thenReturn(Optional.empty());
        assertThrows(EntitatNotFoundException.class,
                () -> consultaService.findByFiltrePaginatPerSuperauditor(5L, null, PageRequest.of(0, 10)));
    }

    @Test
    public void findByFiltrePaginatPerSuperauditor_correcte_retornaPagina() throws Exception {
        Entitat entitat = mock(Entitat.class);
        when(entitat.getId()).thenReturn(5L);
        when(entitatRepository.findById(5L)).thenReturn(Optional.of(entitat));
        Page<LlistatConsulta> page = stubFindByProcedimentServeiProcedimentEntitatIdAndCreatedBy(new PageImpl<>(Collections.emptyList()));
        Page<ConsultaDto> pageDto = new PageImpl<>(Collections.emptyList());
        when(dtoMappingHelper.pageEntities2pageDto(eq(page), eq(ConsultaDto.class), any())).thenReturn(pageDto);

        Page<ConsultaDto> resultat = consultaService.findByFiltrePaginatPerSuperauditor(5L, null, PageRequest.of(0, 10));

        assertNotNull(resultat);
    }

    // ---------- findByFiltrePaginatPerAdmin ----------

    @Test
    public void findByFiltrePaginatPerAdmin_correcte_retornaPagina() throws Exception {
        ConsultaFiltreDto filtre = new ConsultaFiltreDto();
        filtre.setEntitatId(6L);
        Page<LlistatConsulta> page = stubFindByFiltrePaginatAdmin(new PageImpl<>(Collections.emptyList()));
        Page<ConsultaDto> pageDto = new PageImpl<>(Collections.emptyList());
        when(dtoMappingHelper.pageEntities2pageDto(eq(page), eq(ConsultaDto.class), any())).thenReturn(pageDto);

        Page<ConsultaDto> resultat = consultaService.findByFiltrePaginatPerAdmin(filtre, PageRequest.of(0, 10));

        assertNotNull(resultat);
    }

    @Test
    public void findByFiltrePaginatPerAdmin_ambEstatIMultiple_retornaPagina() throws Exception {
        ConsultaFiltreDto filtre = new ConsultaFiltreDto();
        filtre.setEstat(ConsultaDto.EstatTipus.Tramitada);
        filtre.setMultiple(Boolean.TRUE);
        filtre.setRecobriment(Boolean.FALSE);
        Page<LlistatConsulta> page = stubFindByFiltrePaginatAdmin(new PageImpl<>(Collections.emptyList()));
        Page<ConsultaDto> pageDto = new PageImpl<>(Collections.emptyList());
        when(dtoMappingHelper.pageEntities2pageDto(eq(page), eq(ConsultaDto.class), any())).thenReturn(pageDto);

        Page<ConsultaDto> resultat = consultaService.findByFiltrePaginatPerAdmin(filtre, PageRequest.of(0, 10));

        assertNotNull(resultat);
    }

    // ---------- findByFiltrePerOpenData ----------

    @Test
    public void findByFiltrePerOpenData_correcte_retornaLlista() throws Exception {
        when(dadesObertesConsultaRepository.findByOpendata(
                anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any()))
                .thenReturn(Collections.singletonList(mock(DadesObertesRespostaConsulta.class)));

        List<DadesObertesRespostaConsulta> resultat = consultaService.findByFiltrePerOpenData(
                "A00000001", new Date(), new Date(), "PROC1", "SV001");

        assertEquals(1, resultat.size());
    }

    @Test
    public void findByFiltrePerOpenData_sensePararametres_retornaLlistaBuida() throws Exception {
        when(dadesObertesConsultaRepository.findByOpendata(
                anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any()))
                .thenReturn(Collections.emptyList());

        List<DadesObertesRespostaConsulta> resultat = consultaService.findByFiltrePerOpenData(
                null, null, null, null, null);

        assertTrue(resultat.isEmpty());
    }

    // ---------- findByFiltrePerOpenDataV2 ----------

    @Test
    public void findByFiltrePerOpenDataV2_correcte_ambProperaPagina() throws Exception {
        ConsultaOpenDataDto filtre = ConsultaOpenDataDto.builder()
                .entitatCodi("A00000001")
                .pagina(0)
                .mida(10)
                .appPath("/opendata/consultes")
                .build();
        when(dadesObertesConsultaRepository.countByOpendata(
                anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any()))
                .thenReturn(25);
        Page<DadesObertesRespostaConsulta> page = new PageImpl<>(Collections.singletonList(mock(DadesObertesRespostaConsulta.class)));
        when(dadesObertesConsultaRepository.findByOpendata(
                anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), any(Pageable.class)))
                .thenReturn(page);

        DadesObertesResposta resultat = consultaService.findByFiltrePerOpenDataV2(filtre);

        assertEquals(25, resultat.getTotalElements());
        assertNotNull(resultat.getProperaPagina());
        assertEquals(1, resultat.getDades().size());
    }

    @Test
    public void findByFiltrePerOpenDataV2_darreraPagina_senseProperaPagina() throws Exception {
        ConsultaOpenDataDto filtre = ConsultaOpenDataDto.builder()
                .pagina(2)
                .mida(10)
                .appPath("/opendata/consultes")
                .build();
        when(dadesObertesConsultaRepository.countByOpendata(
                anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any()))
                .thenReturn(25);
        Page<DadesObertesRespostaConsulta> page = new PageImpl<>(Collections.emptyList());
        when(dadesObertesConsultaRepository.findByOpendata(
                anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(), any(Pageable.class)))
                .thenReturn(page);

        DadesObertesResposta resultat = consultaService.findByFiltrePerOpenDataV2(filtre);

        assertNull(resultat.getProperaPagina());
    }

    // ---------- findOneDelegat ----------

    @Test
    public void findOneDelegat_consultaNoTrobada_llancaException() {
        when(consultaRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(ConsultaNotFoundException.class, () -> consultaService.findOneDelegat(10L));
    }

    @Test
    public void findOneDelegat_noPropietari_llancaException() {
        Consulta consulta = consultaAmbPropietari("altre");
        when(consultaRepository.findById(10L)).thenReturn(Optional.of(consulta));
        assertThrows(ConsultaNotFoundException.class, () -> consultaService.findOneDelegat(10L));
    }

    @Test
    public void findOneDelegat_correcte_retornaDto() throws Exception {
        Consulta consulta = consultaAmbPropietari("usuari1");
        when(consultaRepository.findById(10L)).thenReturn(Optional.of(consulta));
        when(mapperFacade.map(eq(consulta), eq(ConsultaDto.class))).thenReturn(consultaDtoNoError());
        when(scspHelper.recuperarResposta(any(), any(), anyBoolean())).thenReturn(null);

        ConsultaDto resultat = consultaService.findOneDelegat(10L);

        assertNotNull(resultat);
        assertEquals(EstatTipus.Pendent.name(), resultat.getEstat());
    }

    // ---------- findOneAuditor ----------

    @Test
    public void findOneAuditor_consultaNoTrobada_llancaException() {
        when(consultaRepository.findById(11L)).thenReturn(Optional.empty());
        assertThrows(ConsultaNotFoundException.class, () -> consultaService.findOneAuditor(11L));
    }

    @Test
    public void findOneAuditor_noEsAuditor_llancaException() {
        Consulta consulta = mock(Consulta.class);
        ProcedimentServei ps = mock(ProcedimentServei.class);
        Procediment procediment = mock(Procediment.class);
        Entitat entitat = mock(Entitat.class);
        when(entitat.getId()).thenReturn(20L);
        when(procediment.getEntitat()).thenReturn(entitat);
        when(ps.getProcediment()).thenReturn(procediment);
        when(consulta.getProcedimentServei()).thenReturn(ps);
        when(consultaRepository.findById(11L)).thenReturn(Optional.of(consulta));
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodi(20L, "usuari1")).thenReturn(null);

        assertThrows(ConsultaNotFoundException.class, () -> consultaService.findOneAuditor(11L));
    }

    @Test
    public void findOneAuditor_correcte_retornaDto() throws Exception {
        Consulta consulta = mock(Consulta.class);
        ProcedimentServei ps = mock(ProcedimentServei.class);
        Procediment procediment = mock(Procediment.class);
        Entitat entitat = mock(Entitat.class);
        when(entitat.getId()).thenReturn(21L);
        when(procediment.getEntitat()).thenReturn(entitat);
        when(ps.getProcediment()).thenReturn(procediment);
        when(consulta.getProcedimentServei()).thenReturn(ps);
        when(consultaRepository.findById(11L)).thenReturn(Optional.of(consulta));
        EntitatUsuari entitatUsuari = mock(EntitatUsuari.class);
        when(entitatUsuari.isAuditor()).thenReturn(true);
        when(entitatUsuariRepository.findByEntitatIdAndUsuariCodi(21L, "usuari1")).thenReturn(entitatUsuari);
        when(mapperFacade.map(eq(consulta), eq(ConsultaDto.class))).thenReturn(consultaDtoNoError());
        when(scspHelper.recuperarResposta(any(), any(), anyBoolean())).thenReturn(null);

        ConsultaDto resultat = consultaService.findOneAuditor(11L);

        assertNotNull(resultat);
    }

    // ---------- findOneSuperauditor ----------

    @Test
    public void findOneSuperauditor_consultaNoTrobada_llancaException() {
        when(consultaRepository.findById(12L)).thenReturn(Optional.empty());
        assertThrows(ConsultaNotFoundException.class, () -> consultaService.findOneSuperauditor(12L));
    }

    @Test
    public void findOneSuperauditor_correcte_retornaDto() throws Exception {
        Consulta consulta = mock(Consulta.class);
        when(consultaRepository.findById(12L)).thenReturn(Optional.of(consulta));
        when(mapperFacade.map(eq(consulta), eq(ConsultaDto.class))).thenReturn(consultaDtoNoError());
        when(scspHelper.recuperarResposta(any(), any(), anyBoolean())).thenReturn(null);

        ConsultaDto resultat = consultaService.findOneSuperauditor(12L);

        assertNotNull(resultat);
    }

    // ---------- findOneAdmin ----------

    @Test
    public void findOneAdmin_consultaNoTrobada_llancaException() {
        when(consultaRepository.findById(13L)).thenReturn(Optional.empty());
        assertThrows(ConsultaNotFoundException.class, () -> consultaService.findOneAdmin(13L));
    }

    @Test
    public void findOneAdmin_correcte_retornaDto() throws Exception {
        Consulta consulta = mock(Consulta.class);
        when(consultaRepository.findById(13L)).thenReturn(Optional.of(consulta));
        when(mapperFacade.map(eq(consulta), eq(ConsultaDto.class))).thenReturn(consultaDtoNoError());
        when(scspHelper.recuperarResposta(any(), any(), anyBoolean())).thenReturn(null);

        ConsultaDto resultat = consultaService.findOneAdmin(13L);

        assertNotNull(resultat);
    }
}
