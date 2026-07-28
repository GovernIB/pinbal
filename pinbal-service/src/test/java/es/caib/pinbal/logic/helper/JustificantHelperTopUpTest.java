package es.caib.pinbal.logic.helper;

import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import es.caib.pinbal.logic.intf.dto.FitxerDto;
import es.caib.pinbal.logic.intf.dto.JustificantEstat;
import es.caib.pinbal.persist.entity.Consulta;
import es.caib.pinbal.persist.entity.HistoricConsulta;
import es.caib.pinbal.persist.entity.OrganGestor;
import es.caib.pinbal.persist.entity.Procediment;
import es.caib.pinbal.persist.entity.ProcedimentServei;
import es.caib.pinbal.persist.entity.ServeiConfig;
import es.caib.pinbal.persist.entity.ServeiJustificantCamp;
import es.caib.pinbal.persist.repository.ConsultaRepository;
import es.caib.pinbal.persist.repository.HistoricConsultaRepository;
import es.caib.pinbal.persist.repository.ServeiConfigRepository;
import es.caib.pinbal.persist.repository.ServeiJustificantCampRepository;
import es.caib.pinbal.plugin.firmaservidor.FirmaServidorPlugin;
import es.caib.pinbal.plugin.firmaservidor.SignaturaResposta;
import es.caib.pinbal.scsp.JustificantArbreHelper.ElementArbre;
import es.caib.pinbal.scsp.ResultatEnviamentPeticio;
import es.caib.pinbal.scsp.ScspHelper;
import es.caib.pluginsib.arxiu.api.ContingutArxiu;
import es.caib.pluginsib.arxiu.api.ContingutOrigen;
import es.caib.pluginsib.arxiu.api.Document;
import es.caib.pluginsib.arxiu.api.DocumentContingut;
import es.caib.pluginsib.arxiu.api.DocumentEstatElaboracio;
import es.caib.pluginsib.arxiu.api.DocumentTipus;
import es.scsp.common.exceptions.ScspException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests complementaris de {@link JustificantHelper} centrats en els mètodes i
 * branques que {@link JustificantHelperTest} no cobreix: generació real del
 * justificant (plantilla Freemarker/ODT, conversió i fusió de PDF), descàrrega
 * del fitxer generat i les diferents branques d'error de
 * generarCustodiarJustificantPendent.
 */
@ExtendWith(MockitoExtension.class)
public class JustificantHelperTopUpTest {

    @Mock
    private ServeiJustificantCampRepository serveiJustificantCampRepository;
    @Mock
    private ServeiConfigRepository serveiConfigRepository;
    @Mock
    private ConversioTipusDocumentHelper conversioTipusDocumentHelper;
    @Mock
    private PluginHelper pluginHelper;
    @Mock
    private IntegracioHelper integracioHelper;
    @Mock
    private ConfigHelper configHelper;
    @Mock
    private ConsultaRepository consultaRepository;
    @Mock
    private HistoricConsultaRepository historicConsultaRepository;
    @Mock
    private ScspHelper scspHelper;
    @Mock
    private ServeiConfig serveiConfig;
    @Mock
    private MessageSource messageSource;

    @Spy
    @InjectMocks
    private JustificantHelper justificantHelper;

    private ProcedimentServei procedimentServei;

    @BeforeEach
    public void setUp() {
        OrganGestor organGestor = new OrganGestor();
        organGestor.setCodi("ORG");

        Procediment procediment = new Procediment();
        procediment.setCodi("PROC");
        procediment.setCodiSia("SIA");
        procediment.setOrganGestor(organGestor);

        procedimentServei = ProcedimentServei.getBuilder(procediment, "SERVEI").build();

        justificantHelper.setMessageSource(messageSource);

        lenient().when(messageSource.getMessage(anyString(), any(), any())).thenReturn("text");
        lenient().when(serveiConfigRepository.findByServei("SERVEI")).thenReturn(serveiConfig);
        lenient().when(configHelper.getConfig("es.caib.pinbal.justificant.extensio.sortida", "pdf")).thenReturn("pdf");
        lenient().when(configHelper.getConfig("es.caib.pinbal.plugin.arxiu.serie.documental")).thenReturn("SERIE");
        lenient().when(conversioTipusDocumentHelper.nomArxiuConvertit(anyString(), anyString())).thenReturn("justificant.pdf");
        lenient().when(consultaRepository.findByScspPeticionId(anyString())).thenReturn(Collections.<Consulta>emptyList());
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private Consulta mockConsulta(String scspPeticionId, String scspSolicitudId) {
        Consulta consulta = Mockito.mock(Consulta.class);
        lenient().when(consulta.getId()).thenReturn(1L);
        lenient().when(consulta.getScspPeticionId()).thenReturn(scspPeticionId);
        lenient().when(consulta.getScspSolicitudId()).thenReturn(scspSolicitudId);
        lenient().when(consulta.getProcedimentServei()).thenReturn(procedimentServei);
        return consulta;
    }

    private byte[] minimalPdf() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        com.lowagie.text.Document document = new com.lowagie.text.Document();
        PdfWriter.getInstance(document, baos);
        document.open();
        document.add(new Paragraph("contingut de prova"));
        document.close();
        return baos.toByteArray();
    }

    // ------------------------------------------------------------------
    // generarAmbPlantillaFreemarker
    // ------------------------------------------------------------------

    @Test
    public void generarAmbPlantillaFreemarker_senseTraduccions_noHiHaDocuments() throws Exception {
        ElementArbre arrel = new ElementArbre("Arrel");
        arrel.addFill(new ElementArbre("Node 1", "Valor 1", "node1.xpath"));
        arrel.addFill(new ElementArbre("Node 2", "Valor 2", "node2.xpath"));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        List<JustificantHelper.NodeInfo> result = justificantHelper.generarAmbPlantillaFreemarker(
                arrel, "Descripcio servei", null, null, out);

        assertTrue(result.isEmpty());
        assertTrue(out.size() > 0);
    }

    @Test
    public void generarAmbPlantillaFreemarker_ambTraduccioDocument_afegeixANodesTipusDocument() throws Exception {
        ElementArbre arrel = new ElementArbre("Arrel");
        arrel.addFill(new ElementArbre("Node normal", "Valor 1", "node1.xpath"));
        arrel.addFill(new ElementArbre("Node document", "Valor doc", "node2.xpath"));

        List<ServeiJustificantCamp> traduccions = Collections.singletonList(
                ServeiJustificantCamp.getBuilder("SERVEI", "node2.xpath", "ca", "ES", "Titol document", true).build());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        List<JustificantHelper.NodeInfo> result = justificantHelper.generarAmbPlantillaFreemarker(
                arrel, "Descripcio servei", traduccions, new java.util.Locale("ca", "ES"), out);

        assertEquals(1, result.size());
        assertEquals("Titol document", result.get(0).getTitol());
    }

    // ------------------------------------------------------------------
    // generar()
    // ------------------------------------------------------------------

    @Test
    public void generar_extensioSortidaIgualQueOrigen_noConverteix() throws Exception {
        Consulta consulta = mockConsulta("PET-1", "SOL-1");
        when(configHelper.getConfig("es.caib.pinbal.justificant.extensio.sortida", "pdf")).thenReturn("odt");
        when(scspHelper.generarArbreJustificant("PET-1", "SOL-1", null)).thenReturn(new ElementArbre("Arrel"));
        when(scspHelper.getServicioDescripcion("SERVEI")).thenReturn("Descripcio servei");
        when(serveiJustificantCampRepository.findByServeiAndLocaleIdiomaAndLocaleRegio(eq("SERVEI"), anyString(), anyString()))
                .thenReturn(null);

        FitxerDto result = justificantHelper.generar(consulta, scspHelper);

        assertTrue(result.getContingut().length > 0);
        verify(integracioHelper).addAccioOk(eq("PET-1"), anyString(), anyString(), Mockito.<Map<String, String>>any(),
                Mockito.eq(es.caib.pinbal.logic.intf.dto.IntegracioAccioTipusEnumDto.ENVIAMENT), anyLong());
        verify(conversioTipusDocumentHelper, never()).convertir(any(), any(), any(), any());
    }

    @Test
    public void generar_extensioSortidaDiferent_converteixSenseDocumentsAdjunts() throws Exception {
        Consulta consulta = mockConsulta("PET-2", "SOL-2");
        when(scspHelper.generarArbreJustificant("PET-2", "SOL-2", null)).thenReturn(new ElementArbre("Arrel"));
        when(scspHelper.getServicioDescripcion("SERVEI")).thenReturn("Descripcio servei");
        when(serveiJustificantCampRepository.findByServeiAndLocaleIdiomaAndLocaleRegio(eq("SERVEI"), anyString(), anyString()))
                .thenReturn(null);

        FitxerDto result = justificantHelper.generar(consulta, scspHelper);

        assertEquals("justificant.pdf", result.getNom());
        verify(conversioTipusDocumentHelper).convertir(anyString(), any(), eq("pdf"), any());
    }

    @Test
    public void generar_convertirPdfaActivat_convertitAPdfa() throws Exception {
        Consulta consulta = mockConsulta("PET-3", "SOL-3");
        when(scspHelper.generarArbreJustificant("PET-3", "SOL-3", null)).thenReturn(new ElementArbre("Arrel"));
        when(scspHelper.getServicioDescripcion("SERVEI")).thenReturn("Descripcio servei");
        when(serveiJustificantCampRepository.findByServeiAndLocaleIdiomaAndLocaleRegio(eq("SERVEI"), anyString(), anyString()))
                .thenReturn(null);
        when(configHelper.getConfigAsBoolean("es.caib.pinbal.justificant.convertir.pdfa", false)).thenReturn(true);

        byte[] pdfaBytes = "PDFA".getBytes();
        doAnswer(inv -> {
            OutputStream os = inv.getArgument(1);
            os.write(pdfaBytes);
            return null;
        }).when(conversioTipusDocumentHelper).convertirPdfToPdfa(any(), any());

        FitxerDto result = justificantHelper.generar(consulta, scspHelper);

        assertArrayEquals(pdfaBytes, result.getContingut());
        verify(conversioTipusDocumentHelper).convertirPdfToPdfa(any(), any());
    }

    @Test
    public void generar_ambDocumentAdjuntValid_fusionaPdf() throws Exception {
        Consulta consulta = mockConsulta("PET-4", "SOL-4");
        byte[] pdfAdjunt = minimalPdf();
        ElementArbre arrel = new ElementArbre("Arrel");
        arrel.addFill(new ElementArbre("Document adjunt", Base64.getEncoder().encodeToString(pdfAdjunt), "doc.xpath"));

        when(scspHelper.generarArbreJustificant("PET-4", "SOL-4", null)).thenReturn(arrel);
        when(scspHelper.getServicioDescripcion("SERVEI")).thenReturn("Descripcio servei");
        List<ServeiJustificantCamp> traduccions = Collections.singletonList(
                ServeiJustificantCamp.getBuilder("SERVEI", "doc.xpath", "ca", "ES", "Document Adjunt", true).build());
        when(serveiJustificantCampRepository.findByServeiAndLocaleIdiomaAndLocaleRegio(eq("SERVEI"), anyString(), anyString()))
                .thenReturn(traduccions);

        byte[] justificantConvertit = minimalPdf();
        doAnswer(inv -> {
            OutputStream os = inv.getArgument(3);
            os.write(justificantConvertit);
            return null;
        }).when(conversioTipusDocumentHelper).convertir(any(), any(), any(), any());

        FitxerDto result = justificantHelper.generar(consulta, scspHelper);

        assertTrue(result.getContingut().length > 0);
    }

    @Test
    public void generar_erroEnGenerarArbre_registraAccioErrorIRellanca() throws Exception {
        Consulta consulta = mockConsulta("PET-5", "SOL-5");
        ScspException scspException = new ScspException("Error generant arbre", "0234");
        when(scspHelper.generarArbreJustificant("PET-5", "SOL-5", null)).thenThrow(scspException);

        assertThrows(ScspException.class, () -> justificantHelper.generar(consulta, scspHelper));

        verify(integracioHelper).addAccioError(eq("PET-5"), anyString(), anyString(), Mockito.<Map<String, String>>any(),
                Mockito.eq(es.caib.pinbal.logic.intf.dto.IntegracioAccioTipusEnumDto.ENVIAMENT), anyLong(), anyString(), eq(scspException));
    }

    // ------------------------------------------------------------------
    // getPdfAmbTitol (branca d'error, via reflexió)
    // ------------------------------------------------------------------

    @Test
    public void getPdfAmbTitol_contingutInvalid_tornaElContingutOriginal() {
        byte[] contingutInvalid = "no es un pdf".getBytes();
        JustificantHelper.NodeInfo node = new JustificantHelper.NodeInfo(
                0, "Titol", Base64.getEncoder().encodeToString(contingutInvalid), "x.path");

        byte[] resultat = ReflectionTestUtils.invokeMethod(justificantHelper, "getPdfAmbTitol", node);

        assertArrayEquals(contingutInvalid, resultat);
    }

    // ------------------------------------------------------------------
    // getExtensioArxiu (via reflexió)
    // ------------------------------------------------------------------

    @Test
    public void getExtensioArxiu_ambPunt_retornaExtensio() {
        String extensio = ReflectionTestUtils.invokeMethod(justificantHelper, "getExtensioArxiu", "fitxer.odt");
        assertEquals("odt", extensio);
    }

    @Test
    public void getExtensioArxiu_sensePunt_retornaNull() {
        String extensio = ReflectionTestUtils.invokeMethod(justificantHelper, "getExtensioArxiu", "fitxersenseextensio");
        assertNull(extensio);
    }

    // ------------------------------------------------------------------
    // descarregarFitxerGenerat
    // ------------------------------------------------------------------

    @Test
    public void descarregarFitxerGenerat_adjuntPdfBase64Present_retornaContingutDecodificat() throws Exception {
        Consulta consulta = mockConsulta("PET-10", "SOL-10");
        when(serveiConfig.getJustificantTipus()).thenReturn(ServeiConfig.JustificantTipus.ADJUNT_PDF_BASE64);
        when(serveiConfig.getJustificantXpath()).thenReturn("xpath.doc");
        byte[] contingut = "contingut pdf".getBytes();
        Map<String, Object> dades = new HashMap<>();
        dades.put("xpath.doc", Base64.getEncoder().encodeToString(contingut));
        when(scspHelper.getDadesEspecifiquesResposta("PET-10", "SOL-10")).thenReturn(dades);

        FitxerDto result = justificantHelper.descarregarFitxerGenerat(consulta, scspHelper, false);

        assertArrayEquals(contingut, result.getContingut());
    }

    @Test
    public void descarregarFitxerGenerat_adjuntPdfBase64Absent_iEstatDesconegut_retornaBuit() throws Exception {
        Consulta consulta = mockConsulta("PET-11", "SOL-11");
        when(serveiConfig.getJustificantTipus()).thenReturn(ServeiConfig.JustificantTipus.ADJUNT_PDF_BASE64);
        when(serveiConfig.getJustificantXpath()).thenReturn("xpath.doc");
        when(scspHelper.getDadesEspecifiquesResposta("PET-11", "SOL-11")).thenReturn(new HashMap<>());
        when(consulta.getJustificantEstat()).thenReturn(JustificantEstat.PENDENT);

        FitxerDto result = justificantHelper.descarregarFitxerGenerat(consulta, scspHelper, false);

        assertNull(result.getContingut());
        assertNull(result.getNom());
    }

    @Test
    public void descarregarFitxerGenerat_okAmbArxiuDocumentUuid_recuperaDeArxiu() throws Exception {
        Consulta consulta = mockConsulta("PET-12", "SOL-12");
        when(consulta.getJustificantEstat()).thenReturn(JustificantEstat.OK);
        when(consulta.getArxiuDocumentUuid()).thenReturn("DOC-EXISTENT");

        byte[] contingutArxiu = "contingut arxiu".getBytes();
        Document documentArxiu = Mockito.mock(Document.class);
        DocumentContingut documentContingut = Mockito.mock(DocumentContingut.class);
        when(documentContingut.getContingut()).thenReturn(contingutArxiu);
        when(documentArxiu.getContingut()).thenReturn(documentContingut);
        when(pluginHelper.arxiuDocumentConsultar("PET-12", "DOC-EXISTENT", null, true, false))
                .thenReturn(documentArxiu);

        FitxerDto result = justificantHelper.descarregarFitxerGenerat(consulta, scspHelper, false);

        assertArrayEquals(contingutArxiu, result.getContingut());
    }

    @Test
    public void descarregarFitxerGenerat_okAplicacioGuardaArxiuNoRecobriment_desaAArxiu() throws Exception {
        Consulta consulta = mockConsulta("PET-13", "SOL-13");
        when(consulta.getJustificantEstat()).thenReturn(JustificantEstat.OK);
        when(consulta.getArxiuDocumentUuid()).thenReturn(null);
        when(consulta.isAplicacioGuardaJustificantArxiu()).thenReturn(true);
        when(consulta.isRecobriment()).thenReturn(false);
        when(consulta.getArxiuExpedientUuid()).thenReturn(null);
        when(consulta.getTitularDocumentNum()).thenReturn("12345678A");

        FitxerDto fitxerGenerat = FitxerDto.builder().nom("justificant.pdf").contingut(new byte[]{1, 2, 3}).build();
        doReturn(fitxerGenerat).when(justificantHelper).generar(eq(consulta), eq(scspHelper));

        SignaturaResposta firmat = SignaturaResposta.builder()
                .contingut(new byte[]{9, 9, 9})
                .nom("justificant.pdf")
                .mime("application/pdf")
                .build();
        when(pluginHelper.firmaServidorFirmar(any(FitxerDto.class), eq(FirmaServidorPlugin.TipusFirma.PADES),
                anyString(), anyString(), anyString())).thenReturn(firmat);
        when(pluginHelper.arxiuExpedientCercarAmbNom("PET-13")).thenReturn(null);
        when(pluginHelper.arxiuExpedientCrear(eq("PET-13"), eq("12345678A"), eq("ORG"), eq("SIA"), eq("PROC"), eq("SERIE")))
                .thenReturn("EXP-NOU");
        when(pluginHelper.arxiuDocumentGuardarFirmaPades(eq("PET-13"), eq("EXP-NOU"), eq("SOL-13"), eq("ORG"), eq("SERIE"),
                any(FitxerDto.class), Mockito.<ContingutOrigen>any(), Mockito.<DocumentEstatElaboracio>any(),
                Mockito.<DocumentTipus>any())).thenReturn("DOC-NOU");

        FitxerDto result = justificantHelper.descarregarFitxerGenerat(consulta, scspHelper, false);

        assertArrayEquals(firmat.getContingut(), result.getContingut());
        verify(consulta).updateArxiuExpedientUuid("EXP-NOU");
        verify(consulta).updateArxiuDocumentUuid("DOC-NOU");
    }

    @Test
    public void descarregarFitxerGenerat_okAplicacioGuardaArxiuRecobriment_noDesaAArxiu() throws Exception {
        Consulta consulta = mockConsulta("PET-14", "SOL-14");
        when(consulta.getJustificantEstat()).thenReturn(JustificantEstat.OK);
        when(consulta.getArxiuDocumentUuid()).thenReturn(null);
        when(consulta.isAplicacioGuardaJustificantArxiu()).thenReturn(true);
        when(consulta.isRecobriment()).thenReturn(true);

        FitxerDto fitxerGenerat = FitxerDto.builder().nom("justificant.pdf").contingut(new byte[]{1, 2, 3}).build();
        doReturn(fitxerGenerat).when(justificantHelper).generar(eq(consulta), eq(scspHelper));

        SignaturaResposta firmat = SignaturaResposta.builder()
                .contingut(new byte[]{9, 9, 9})
                .nom("justificant.pdf")
                .mime("application/pdf")
                .build();
        when(pluginHelper.firmaServidorFirmar(any(FitxerDto.class), eq(FirmaServidorPlugin.TipusFirma.PADES),
                anyString(), anyString(), anyString())).thenReturn(firmat);

        FitxerDto result = justificantHelper.descarregarFitxerGenerat(consulta, scspHelper, false);

        assertArrayEquals(firmat.getContingut(), result.getContingut());
        verify(pluginHelper, never()).arxiuDocumentGuardarFirmaPades(any(), any(), any(), any(), any(), any(),
                Mockito.<ContingutOrigen>any(), Mockito.<DocumentEstatElaboracio>any(), Mockito.<DocumentTipus>any());
        verify(consulta, never()).updateArxiuExpedientUuid(any());
    }

    @Test
    public void descarregarFitxerGenerat_okCustodia_ambCustodiaIdExplicit() throws Exception {
        Consulta consulta = mockConsulta("PET-15", "SOL-15");
        when(consulta.getJustificantEstat()).thenReturn(JustificantEstat.OK);
        when(consulta.getArxiuDocumentUuid()).thenReturn(null);
        when(consulta.isAplicacioGuardaJustificantArxiu()).thenReturn(false);
        when(consulta.getCustodiaId()).thenReturn("CUST-1");

        byte[] contingutCustodia = "custodia".getBytes();
        when(pluginHelper.custodiaObtenirDocument("CUST-1")).thenReturn(contingutCustodia);

        FitxerDto result = justificantHelper.descarregarFitxerGenerat(consulta, scspHelper, false);

        assertArrayEquals(contingutCustodia, result.getContingut());
    }

    @Test
    public void descarregarFitxerGenerat_okCustodia_senseCustodiaIdPeroCustodiat() throws Exception {
        Consulta consulta = mockConsulta("PET-16", "SOL-16");
        when(consulta.getJustificantEstat()).thenReturn(JustificantEstat.OK);
        when(consulta.getArxiuDocumentUuid()).thenReturn(null);
        when(consulta.isAplicacioGuardaJustificantArxiu()).thenReturn(false);
        when(consulta.getCustodiaId()).thenReturn(null);
        when(consulta.isCustodiat()).thenReturn(true);

        byte[] contingutCustodia = "custodia2".getBytes();
        when(pluginHelper.custodiaObtenirDocument("PET-16")).thenReturn(contingutCustodia);

        FitxerDto result = justificantHelper.descarregarFitxerGenerat(consulta, scspHelper, false);

        assertArrayEquals(contingutCustodia, result.getContingut());
    }

    @Test
    public void descarregarFitxerGenerat_okCustodia_senseCustodiaIdINoCustodiat() throws Exception {
        Consulta consulta = mockConsulta("PET-17", "SOL-17");
        when(consulta.getJustificantEstat()).thenReturn(JustificantEstat.OK);
        when(consulta.getArxiuDocumentUuid()).thenReturn(null);
        when(consulta.isAplicacioGuardaJustificantArxiu()).thenReturn(false);
        when(consulta.getCustodiaId()).thenReturn(null);
        when(consulta.isCustodiat()).thenReturn(false);

        byte[] contingutCustodia = "custodia3".getBytes();
        when(pluginHelper.custodiaObtenirDocument("PET-17#SOL-17")).thenReturn(contingutCustodia);

        FitxerDto result = justificantHelper.descarregarFitxerGenerat(consulta, scspHelper, false);

        assertArrayEquals(contingutCustodia, result.getContingut());
    }

    @Test
    public void descarregarFitxerGenerat_okNoCustodia_generaJustificantSotaDemanda() throws Exception {
        Consulta consulta = mockConsulta("PET-18", "SOL-18");
        when(consulta.getJustificantEstat()).thenReturn(JustificantEstat.OK_NO_CUSTODIA);

        FitxerDto fitxerGenerat = FitxerDto.builder().nom("justificant.pdf").contingut(new byte[]{4, 5, 6}).build();
        doReturn(fitxerGenerat).when(justificantHelper).generar(eq(consulta), eq(scspHelper));

        FitxerDto result = justificantHelper.descarregarFitxerGenerat(consulta, scspHelper, false);

        assertArrayEquals(fitxerGenerat.getContingut(), result.getContingut());
    }

    // ------------------------------------------------------------------
    // getResultatEnviamentPeticio (via reflexió)
    // ------------------------------------------------------------------

    @Test
    public void getResultatEnviamentPeticio_excepcioAlRecuperar_marcaErrorIRetornaNull() throws Exception {
        Consulta consulta = mockConsulta("PET-20", "SOL-20");
        ScspException ex = new ScspException("No es pot recuperar", "0001");
        when(scspHelper.recuperarResultatEnviamentPeticio("PET-20")).thenThrow(ex);

        ResultatEnviamentPeticio resultat = ReflectionTestUtils.invokeMethod(
                justificantHelper, "getResultatEnviamentPeticio", consulta, scspHelper);

        assertNull(resultat);
        verify(consulta).updateJustificantEstat(eq(JustificantEstat.ERROR), eq(false), isNull(), isNull(),
                anyString(), isNull(), isNull());
    }

    @Test
    public void getResultatEnviamentPeticio_respostaError_marcaErrorIRetornaNull() throws Exception {
        Consulta consulta = mockConsulta("PET-21", "SOL-21");
        ResultatEnviamentPeticio resultatError = new ResultatEnviamentPeticio();
        resultatError.setErrorEnviament(true);
        resultatError.setEstatDescripcio("Error d'enviament SCSP");
        when(scspHelper.recuperarResultatEnviamentPeticio("PET-21")).thenReturn(resultatError);

        ResultatEnviamentPeticio resultat = ReflectionTestUtils.invokeMethod(
                justificantHelper, "getResultatEnviamentPeticio", consulta, scspHelper);

        assertNull(resultat);
        verify(consulta).updateJustificantEstat(eq(JustificantEstat.ERROR), eq(false), isNull(), isNull(),
                anyString(), isNull(), isNull());
    }

    // ------------------------------------------------------------------
    // generarCustodiarJustificantPendent - branques addicionals
    // ------------------------------------------------------------------

    @Test
    public void generarCustodiarJustificantPendent_noSignarICustodiar_marcaOkNoCustodia() throws Exception {
        Consulta consulta = mockConsulta("PET-30", "SOL-30");
        when(scspHelper.recuperarResultatEnviamentPeticio("PET-30")).thenReturn(new ResultatEnviamentPeticio());
        when(serveiConfigRepository.findByServei("SERVEI")).thenReturn(serveiConfig);
        when(configHelper.getConfigAsBoolean("es.caib.pinbal.justificant.signar.i.custodiar", false)).thenReturn(false);

        justificantHelper.generarCustodiarJustificantPendent(consulta, scspHelper);

        verify(consulta).updateJustificantEstat(JustificantEstat.OK_NO_CUSTODIA, false, null, null, null, null, null);
        verify(justificantHelper, never()).generar(any(Consulta.class), any(ScspHelper.class));
    }

    @Test
    public void generarCustodiarJustificantPendent_jaCustodiat_marcaOkNoCustodia() throws Exception {
        Consulta consulta = mockConsulta("PET-31", "SOL-31");
        when(scspHelper.recuperarResultatEnviamentPeticio("PET-31")).thenReturn(new ResultatEnviamentPeticio());
        when(serveiConfigRepository.findByServei("SERVEI")).thenReturn(serveiConfig);
        when(configHelper.getConfigAsBoolean("es.caib.pinbal.justificant.signar.i.custodiar", false)).thenReturn(true);
        when(consulta.isCustodiat()).thenReturn(true);

        justificantHelper.generarCustodiarJustificantPendent(consulta, scspHelper);

        verify(consulta).updateJustificantEstat(JustificantEstat.OK_NO_CUSTODIA, false, null, null, null, null, null);
    }

    @Test
    public void generarCustodiarJustificantPendent_aplicacioGuardaJustificantArxiu_noGeneraNiFirma() throws Exception {
        Consulta consulta = mockConsulta("PET-32", "SOL-32");
        when(scspHelper.recuperarResultatEnviamentPeticio("PET-32")).thenReturn(new ResultatEnviamentPeticio());
        when(serveiConfigRepository.findByServei("SERVEI")).thenReturn(serveiConfig);
        when(configHelper.getConfigAsBoolean("es.caib.pinbal.justificant.signar.i.custodiar", false)).thenReturn(true);
        when(consulta.isCustodiat()).thenReturn(false);
        when(consulta.isAplicacioGuardaJustificantArxiu()).thenReturn(true);
        when(consulta.getCustodiaUrl()).thenReturn(null);
        when(consulta.getArxiuExpedientUuid()).thenReturn(null);
        when(consulta.getArxiuDocumentUuid()).thenReturn(null);

        justificantHelper.generarCustodiarJustificantPendent(consulta, scspHelper);

        verify(justificantHelper, never()).generar(any(Consulta.class), any(ScspHelper.class));
        verify(pluginHelper, never()).firmaServidorFirmar(any(), any(), any(), any(), any());
        verify(consulta).updateJustificantEstat(eq(JustificantEstat.OK), eq(true), isNull(), isNull(), isNull(), isNull(), isNull());
    }

    @Test
    public void generarCustodiarJustificantPendent_pluginArxiuNoActiu_marcaError() throws Exception {
        Consulta consulta = mockConsulta("PET-33", "SOL-33");
        when(scspHelper.recuperarResultatEnviamentPeticio("PET-33")).thenReturn(new ResultatEnviamentPeticio());
        when(serveiConfigRepository.findByServei("SERVEI")).thenReturn(serveiConfig);
        when(configHelper.getConfigAsBoolean("es.caib.pinbal.justificant.signar.i.custodiar", false)).thenReturn(true);
        when(consulta.isCustodiat()).thenReturn(false);
        when(consulta.isAplicacioGuardaJustificantArxiu()).thenReturn(false);
        when(consulta.getCustodiaUrl()).thenReturn(null);
        when(consulta.getArxiuExpedientUuid()).thenReturn(null);
        when(consulta.getArxiuDocumentUuid()).thenReturn(null);

        FitxerDto fitxerGenerat = FitxerDto.builder().nom("justificant.pdf").contingut(new byte[]{1}).build();
        doReturn(fitxerGenerat).when(justificantHelper).generar(eq(consulta), eq(scspHelper));
        when(pluginHelper.isPluginArxiuActiu()).thenReturn(false);

        justificantHelper.generarCustodiarJustificantPendent(consulta, scspHelper);

        verify(consulta).updateJustificantEstat(eq(JustificantEstat.ERROR), eq(false), isNull(), isNull(),
                Mockito.contains("Plugin arxiu no activat"), isNull(), isNull());
    }

    @Test
    public void generarCustodiarJustificantPendent_expedientIDocumentJaResolts_noTornaAGuardar() throws Exception {
        Consulta consulta = mockConsulta("PET-34", "SOL-34");
        when(scspHelper.recuperarResultatEnviamentPeticio("PET-34")).thenReturn(new ResultatEnviamentPeticio());
        when(serveiConfigRepository.findByServei("SERVEI")).thenReturn(serveiConfig);
        when(configHelper.getConfigAsBoolean("es.caib.pinbal.justificant.signar.i.custodiar", false)).thenReturn(true);
        when(consulta.isCustodiat()).thenReturn(false);
        when(consulta.isAplicacioGuardaJustificantArxiu()).thenReturn(false);
        when(consulta.getCustodiaUrl()).thenReturn(null);
        when(consulta.getArxiuExpedientUuid()).thenReturn("EXP-JA-EXISTENT");
        when(consulta.getArxiuDocumentUuid()).thenReturn("DOC-JA-EXISTENT");

        FitxerDto fitxerGenerat = FitxerDto.builder().nom("justificant.pdf").contingut(new byte[]{1}).build();
        doReturn(fitxerGenerat).when(justificantHelper).generar(eq(consulta), eq(scspHelper));
        when(pluginHelper.isPluginArxiuActiu()).thenReturn(true);

        justificantHelper.generarCustodiarJustificantPendent(consulta, scspHelper);

        verify(pluginHelper, never()).firmaServidorFirmar(any(), any(), any(), any(), any());
        verify(pluginHelper, never()).arxiuExpedientCrear(any(), any(), any(), any(), any(), any());
        verify(pluginHelper, never()).arxiuDocumentGuardarFirmaPades(any(), any(), any(), any(), any(), any(),
                Mockito.<ContingutOrigen>any(), Mockito.<DocumentEstatElaboracio>any(), Mockito.<DocumentTipus>any());
        verify(consulta).updateJustificantEstat(eq(JustificantEstat.OK), eq(true), isNull(), isNull(), isNull(),
                eq("EXP-JA-EXISTENT"), eq("DOC-JA-EXISTENT"));
    }

    @Test
    public void generarCustodiarJustificantPendent_erroAbansDelTryIntern_esPropaga() throws Exception {
        Consulta consulta = mockConsulta("PET-35", "SOL-35");
        when(scspHelper.recuperarResultatEnviamentPeticio("PET-35")).thenReturn(new ResultatEnviamentPeticio());
        when(serveiConfigRepository.findByServei("SERVEI")).thenThrow(new RuntimeException("Error inesperat BD"));

        assertThrows(RuntimeException.class,
                () -> justificantHelper.generarCustodiarJustificantPendent(consulta, scspHelper));
    }

    // ------------------------------------------------------------------
    // isDuplicateExpedientException / obtenirOCrearExpedientArxiu - branques via generarCustodiarJustificantPendent
    // ------------------------------------------------------------------

    @Test
    public void generarCustodiarJustificantPendent_expedientDuplicatNomesAmbCodiOracle_reutilitzaExistent() throws Exception {
        Consulta consulta = mockConsulta("PET-36", "SOL-36");
        when(scspHelper.recuperarResultatEnviamentPeticio("PET-36")).thenReturn(new ResultatEnviamentPeticio());
        when(serveiConfigRepository.findByServei("SERVEI")).thenReturn(serveiConfig);
        when(configHelper.getConfigAsBoolean("es.caib.pinbal.justificant.signar.i.custodiar", false)).thenReturn(true);
        when(consulta.isCustodiat()).thenReturn(false);
        when(consulta.isAplicacioGuardaJustificantArxiu()).thenReturn(false);
        when(consulta.getCustodiaUrl()).thenReturn(null);
        when(consulta.getArxiuExpedientUuid()).thenReturn(null);
        when(consulta.getArxiuDocumentUuid()).thenReturn(null);
        when(consulta.getTitularDocumentNum()).thenReturn("12345678A");

        FitxerDto fitxerGenerat = FitxerDto.builder().nom("justificant.pdf").contingut(new byte[]{1}).build();
        doReturn(fitxerGenerat).when(justificantHelper).generar(eq(consulta), eq(scspHelper));
        when(pluginHelper.isPluginArxiuActiu()).thenReturn(true);

        SignaturaResposta firmat = SignaturaResposta.builder().contingut(new byte[]{2}).nom("justificant.pdf").mime("application/pdf").build();
        when(pluginHelper.firmaServidorFirmar(any(FitxerDto.class), eq(FirmaServidorPlugin.TipusFirma.PADES),
                anyString(), anyString(), anyString())).thenReturn(firmat);

        ContingutArxiu expedientConcurrent = Mockito.mock(ContingutArxiu.class);
        when(expedientConcurrent.getIdentificador()).thenReturn("EXP-CONCURRENT");
        when(pluginHelper.arxiuExpedientCercarAmbNom("PET-36"))
                .thenReturn(null)
                .thenReturn(expedientConcurrent);
        when(pluginHelper.arxiuExpedientCrear(eq("PET-36"), eq("12345678A"), eq("ORG"), eq("SIA"), eq("PROC"), eq("SERIE")))
                .thenThrow(new RuntimeException("ORA-00001: violació de restricció única"));
        when(pluginHelper.arxiuDocumentGuardarFirmaPades(eq("PET-36"), eq("EXP-CONCURRENT"), eq("SOL-36"), eq("ORG"), eq("SERIE"),
                any(FitxerDto.class), Mockito.<ContingutOrigen>any(), Mockito.<DocumentEstatElaboracio>any(),
                Mockito.<DocumentTipus>any())).thenReturn("DOC-CONCURRENT");

        justificantHelper.generarCustodiarJustificantPendent(consulta, scspHelper);

        verify(consulta).updateJustificantEstat(eq(JustificantEstat.OK), eq(true), isNull(), isNull(), isNull(),
                eq("EXP-CONCURRENT"), eq("DOC-CONCURRENT"));
    }

    @Test
    public void generarCustodiarJustificantPendent_erroNoDuplicatEnCrearExpedient_marcaError() throws Exception {
        Consulta consulta = mockConsulta("PET-37", "SOL-37");
        when(scspHelper.recuperarResultatEnviamentPeticio("PET-37")).thenReturn(new ResultatEnviamentPeticio());
        when(serveiConfigRepository.findByServei("SERVEI")).thenReturn(serveiConfig);
        when(configHelper.getConfigAsBoolean("es.caib.pinbal.justificant.signar.i.custodiar", false)).thenReturn(true);
        when(consulta.isCustodiat()).thenReturn(false);
        when(consulta.isAplicacioGuardaJustificantArxiu()).thenReturn(false);
        when(consulta.getCustodiaUrl()).thenReturn(null);
        when(consulta.getArxiuExpedientUuid()).thenReturn(null);
        when(consulta.getArxiuDocumentUuid()).thenReturn(null);
        when(consulta.getTitularDocumentNum()).thenReturn("12345678A");

        FitxerDto fitxerGenerat = FitxerDto.builder().nom("justificant.pdf").contingut(new byte[]{1}).build();
        doReturn(fitxerGenerat).when(justificantHelper).generar(eq(consulta), eq(scspHelper));
        when(pluginHelper.isPluginArxiuActiu()).thenReturn(true);

        SignaturaResposta firmat = SignaturaResposta.builder().contingut(new byte[]{2}).nom("justificant.pdf").mime("application/pdf").build();
        when(pluginHelper.firmaServidorFirmar(any(FitxerDto.class), eq(FirmaServidorPlugin.TipusFirma.PADES),
                anyString(), anyString(), anyString())).thenReturn(firmat);

        when(pluginHelper.arxiuExpedientCercarAmbNom("PET-37")).thenReturn(null);
        when(pluginHelper.arxiuExpedientCrear(eq("PET-37"), eq("12345678A"), eq("ORG"), eq("SIA"), eq("PROC"), eq("SERIE")))
                .thenThrow(new RuntimeException("Error de connexió amb l'arxiu"));

        justificantHelper.generarCustodiarJustificantPendent(consulta, scspHelper);

        verify(consulta).updateJustificantEstat(eq(JustificantEstat.ERROR), eq(false), isNull(), isNull(),
                anyString(), isNull(), isNull());
    }

    // ------------------------------------------------------------------
    // propagarArxiuExpedientUuidCompartit (via reflexió)
    // ------------------------------------------------------------------

    @Test
    public void propagarArxiuExpedientUuidCompartit_uuidNull_noFaResGuna() {
        Consulta consulta = mockConsulta("PET-40", "SOL-40");

        ReflectionTestUtils.invokeMethod(justificantHelper, "propagarArxiuExpedientUuidCompartit", consulta, null);

        verify(consultaRepository, never()).findByScspPeticionId(any());
    }

    @Test
    public void propagarArxiuExpedientUuidCompartit_historicConsulta_actualitzaGermanesSenseUuid() {
        HistoricConsulta historicConsulta = Mockito.mock(HistoricConsulta.class);
        when(historicConsulta.getScspPeticionId()).thenReturn("PET-41");

        HistoricConsulta germanaSenseUuid = Mockito.mock(HistoricConsulta.class);
        when(germanaSenseUuid.getArxiuExpedientUuid()).thenReturn(null);
        HistoricConsulta germanaAmbUuid = Mockito.mock(HistoricConsulta.class);
        when(germanaAmbUuid.getArxiuExpedientUuid()).thenReturn("JA-TE-UUID");

        when(historicConsultaRepository.findByScspPeticionId("PET-41"))
                .thenReturn(java.util.Arrays.asList(germanaSenseUuid, germanaAmbUuid));

        ReflectionTestUtils.invokeMethod(justificantHelper, "propagarArxiuExpedientUuidCompartit", historicConsulta, "EXP-COMPARTIT");

        verify(germanaSenseUuid).updateArxiuExpedientUuid("EXP-COMPARTIT");
        verify(germanaAmbUuid, never()).updateArxiuExpedientUuid(any());
    }
}
