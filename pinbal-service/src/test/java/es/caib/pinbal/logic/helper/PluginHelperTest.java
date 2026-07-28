package es.caib.pinbal.logic.helper;

import es.caib.pinbal.logic.intf.dto.FitxerDto;
import es.caib.pinbal.plugin.SistemaExternException;
import es.caib.pinbal.plugin.firmaservidor.FirmaServidorPlugin;
import es.caib.pinbal.plugin.firmaservidor.SignaturaResposta;
import es.caib.pinbal.plugin.unitat.NodeDir3;
import es.caib.pinbal.plugin.unitat.UnitatOrganitzativa;
import es.caib.pinbal.plugin.unitat.UnitatsOrganitzativesPlugin;
import es.caib.pinbal.plugin.usuari.DadesUsuari;
import es.caib.pinbal.plugin.usuari.DadesUsuariPlugin;
import es.caib.pluginsib.arxiu.api.ArxiuException;
import es.caib.pluginsib.arxiu.api.ConsultaResultat;
import es.caib.pluginsib.arxiu.api.ContingutArxiu;
import es.caib.pluginsib.arxiu.api.ContingutOrigen;
import es.caib.pluginsib.arxiu.api.ContingutTipus;
import es.caib.pluginsib.arxiu.api.Document;
import es.caib.pluginsib.arxiu.api.DocumentContingut;
import es.caib.pluginsib.arxiu.api.DocumentEstatElaboracio;
import es.caib.pluginsib.arxiu.api.DocumentTipus;
import es.caib.pluginsib.arxiu.api.Expedient;
import es.caib.pluginsib.arxiu.api.Firma;
import es.caib.pluginsib.arxiu.api.FirmaPerfil;
import es.caib.pluginsib.arxiu.api.FirmaTipus;
import es.caib.pluginsib.arxiu.api.IArxiuPlugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PluginHelperTest {

    @Mock
    private IntegracioHelper integracioHelper;
    @Mock
    private ConfigHelper configHelper;
    @Mock
    private DadesUsuariPlugin dadesUsuariPlugin;
    @Mock
    private FirmaServidorPlugin firmaServidorPlugin;
    @Mock
    private IArxiuPlugin arxiuPlugin;
    @Mock
    private UnitatsOrganitzativesPlugin unitatsOrganitzativesPlugin;

    @InjectMocks
    private PluginHelper pluginHelper;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(pluginHelper, "dadesUsuariPlugin", dadesUsuariPlugin);
        ReflectionTestUtils.setField(pluginHelper, "firmaServidorPlugin", firmaServidorPlugin);
        ReflectionTestUtils.setField(pluginHelper, "arxiuPlugin", arxiuPlugin);
        ReflectionTestUtils.setField(pluginHelper, "unitatsOrganitzativesPlugin", unitatsOrganitzativesPlugin);
    }

    // ------------------------------------------------------------------
    // dadesUsuariConsultarAmbUsuariCodi
    // ------------------------------------------------------------------

    @Test
    public void dadesUsuariConsultarAmbUsuariCodi_ok() throws Exception {
        DadesUsuari dades = DadesUsuari.builder().codi("U1").nom("Usuari 1").build();
        when(dadesUsuariPlugin.consultarAmbUsuariCodi("U1")).thenReturn(dades);

        DadesUsuari result = pluginHelper.dadesUsuariConsultarAmbUsuariCodi("U1");

        assertEquals(dades, result);
        verify(integracioHelper).addAccioOk(eq(IntegracioHelper.INTCODI_USUARIS), anyString(), any(), any(), anyLongCompat());
        verify(integracioHelper, never()).addAccioError(anyString(), anyString(), any(), any(), anyLongCompat(), anyString(), any());
    }

    @Test
    public void dadesUsuariConsultarAmbUsuariCodi_sistemaExternException() throws Exception {
        SistemaExternException ex = new SistemaExternException("boom");
        when(dadesUsuariPlugin.consultarAmbUsuariCodi("U1")).thenThrow(ex);

        SistemaExternException thrown = assertThrows(SistemaExternException.class,
                () -> pluginHelper.dadesUsuariConsultarAmbUsuariCodi("U1"));

        assertEquals(ex, thrown);
        verify(integracioHelper).addAccioError(eq(IntegracioHelper.INTCODI_USUARIS), anyString(), any(), any(), anyLongCompat(), anyString(), eq(ex));
    }

    @Test
    public void dadesUsuariConsultarAmbUsuariCodi_altraExcepcio() throws Exception {
        when(dadesUsuariPlugin.consultarAmbUsuariCodi("U1")).thenThrow(new RuntimeException("no plugin"));

        assertThrows(SistemaExternException.class, () -> pluginHelper.dadesUsuariConsultarAmbUsuariCodi("U1"));
    }

    // ------------------------------------------------------------------
    // dadesUsuariConsultarAmbUsuariNif
    // ------------------------------------------------------------------

    @Test
    public void dadesUsuariConsultarAmbUsuariNif_ok() throws Exception {
        DadesUsuari dades = DadesUsuari.builder().nif("12345678A").build();
        when(dadesUsuariPlugin.consultarAmbUsuariNif("12345678A")).thenReturn(dades);

        DadesUsuari result = pluginHelper.dadesUsuariConsultarAmbUsuariNif("12345678A");

        assertEquals(dades, result);
        verify(integracioHelper).addAccioOk(eq(IntegracioHelper.INTCODI_USUARIS), anyString(), any(), any(), anyLongCompat());
    }

    @Test
    public void dadesUsuariConsultarAmbUsuariNif_sistemaExternException() throws Exception {
        SistemaExternException ex = new SistemaExternException("boom");
        when(dadesUsuariPlugin.consultarAmbUsuariNif("12345678A")).thenThrow(ex);

        assertThrows(SistemaExternException.class, () -> pluginHelper.dadesUsuariConsultarAmbUsuariNif("12345678A"));

        verify(integracioHelper).addAccioError(eq(IntegracioHelper.INTCODI_USUARIS), anyString(), any(), any(), anyLongCompat(), anyString(), eq(ex));
    }

    @Test
    public void dadesUsuariConsultarAmbUsuariNif_altraExcepcio() throws Exception {
        when(dadesUsuariPlugin.consultarAmbUsuariNif("X")).thenThrow(new RuntimeException("boom"));

        assertThrows(SistemaExternException.class, () -> pluginHelper.dadesUsuariConsultarAmbUsuariNif("X"));
    }

    // ------------------------------------------------------------------
    // dadesUsuariConsultarAmbUsuariCodiOrNif
    // ------------------------------------------------------------------

    @Test
    public void dadesUsuariConsultarAmbUsuariCodiOrNif_trobatAmbCodi_noConsultaNif() throws Exception {
        DadesUsuari dades = DadesUsuari.builder().codi("ABC").build();
        when(dadesUsuariPlugin.consultarAmbUsuariCodi("ABC")).thenReturn(dades);

        DadesUsuari result = pluginHelper.dadesUsuariConsultarAmbUsuariCodiOrNif("ABC");

        assertEquals(dades, result);
        verify(dadesUsuariPlugin, never()).consultarAmbUsuariNif(anyString());
    }

    @Test
    public void dadesUsuariConsultarAmbUsuariCodiOrNif_noTrobatAmbCodi_consultaNif() throws Exception {
        DadesUsuari dades = DadesUsuari.builder().nif("ABC").build();
        when(dadesUsuariPlugin.consultarAmbUsuariCodi("ABC")).thenReturn(null);
        when(dadesUsuariPlugin.consultarAmbUsuariNif("ABC")).thenReturn(dades);

        DadesUsuari result = pluginHelper.dadesUsuariConsultarAmbUsuariCodiOrNif("ABC");

        assertEquals(dades, result);
        verify(dadesUsuariPlugin).consultarAmbUsuariNif("ABC");
    }

    // ------------------------------------------------------------------
    // dadesUsuariLikeCodiNomOrNif
    // ------------------------------------------------------------------

    @Test
    public void dadesUsuariLikeCodiNomOrNif_ok() throws Exception {
        List<DadesUsuari> dades = Collections.singletonList(DadesUsuari.builder().codi("X").build());
        when(dadesUsuariPlugin.consultarAmbUsuariAny("tex")).thenReturn(dades);

        List<DadesUsuari> result = pluginHelper.dadesUsuariLikeCodiNomOrNif("tex");

        assertEquals(dades, result);
        verify(integracioHelper).addAccioOk(eq(IntegracioHelper.INTCODI_USUARIS), anyString(), any(), any(), anyLongCompat());
    }

    @Test
    public void dadesUsuariLikeCodiNomOrNif_sistemaExternException() throws Exception {
        SistemaExternException ex = new SistemaExternException("boom");
        when(dadesUsuariPlugin.consultarAmbUsuariAny("tex")).thenThrow(ex);

        assertThrows(SistemaExternException.class, () -> pluginHelper.dadesUsuariLikeCodiNomOrNif("tex"));

        verify(integracioHelper).addAccioError(eq(IntegracioHelper.INTCODI_USUARIS), anyString(), any(), any(), anyLongCompat(), anyString(), eq(ex));
    }

    @Test
    public void dadesUsuariLikeCodiNomOrNif_altraExcepcio() throws Exception {
        when(dadesUsuariPlugin.consultarAmbUsuariAny("tex")).thenThrow(new RuntimeException("boom"));

        assertThrows(SistemaExternException.class, () -> pluginHelper.dadesUsuariLikeCodiNomOrNif("tex"));
    }

    // ------------------------------------------------------------------
    // dadesUsuariFindAmbGrup
    // ------------------------------------------------------------------

    @Test
    public void dadesUsuariFindAmbGrup_ok() throws Exception {
        List<DadesUsuari> dades = Collections.singletonList(DadesUsuari.builder().codi("G").build());
        when(dadesUsuariPlugin.findAmbGrup("GRUP")).thenReturn(dades);

        List<DadesUsuari> result = pluginHelper.dadesUsuariFindAmbGrup("GRUP");

        assertEquals(dades, result);
        verify(integracioHelper).addAccioOk(eq(IntegracioHelper.INTCODI_USUARIS), anyString(), any(), any(), anyLongCompat());
    }

    @Test
    public void dadesUsuariFindAmbGrup_excepcio() throws Exception {
        when(dadesUsuariPlugin.findAmbGrup("GRUP")).thenThrow(new RuntimeException("boom"));

        assertThrows(SistemaExternException.class, () -> pluginHelper.dadesUsuariFindAmbGrup("GRUP"));

        verify(integracioHelper).addAccioError(eq(IntegracioHelper.INTCODI_USUARIS), anyString(), any(), any(), anyLongCompat(), anyString(), any());
    }

    // ------------------------------------------------------------------
    // custodiaObtenirDocument
    // ------------------------------------------------------------------

    @Test
    public void custodiaObtenirDocument_semprNull() throws Exception {
        assertNull(pluginHelper.custodiaObtenirDocument("doc1"));
    }

    // ------------------------------------------------------------------
    // isPluginFirmaServidorActiu / isPluginArxiuActiu
    // ------------------------------------------------------------------

    @Test
    public void isPluginFirmaServidorActiu_true() {
        when(configHelper.getConfig("es.caib.pinbal.plugin.firmaservidor.class")).thenReturn("com.foo.Bar");

        assertTrue(pluginHelper.isPluginFirmaServidorActiu());
    }

    @Test
    public void isPluginFirmaServidorActiu_falseQuanNull() {
        when(configHelper.getConfig("es.caib.pinbal.plugin.firmaservidor.class")).thenReturn(null);

        assertFalse(pluginHelper.isPluginFirmaServidorActiu());
    }

    @Test
    public void isPluginFirmaServidorActiu_falseQuanBuit() {
        when(configHelper.getConfig("es.caib.pinbal.plugin.firmaservidor.class")).thenReturn("");

        assertFalse(pluginHelper.isPluginFirmaServidorActiu());
    }

    @Test
    public void isPluginArxiuActiu_true() {
        when(configHelper.getConfig("es.caib.pinbal.plugin.arxiu.class")).thenReturn("com.foo.Arxiu");

        assertTrue(pluginHelper.isPluginArxiuActiu());
    }

    @Test
    public void isPluginArxiuActiu_false() {
        when(configHelper.getConfig("es.caib.pinbal.plugin.arxiu.class")).thenReturn(null);

        assertFalse(pluginHelper.isPluginArxiuActiu());
    }

    // ------------------------------------------------------------------
    // firmaServidorFirmar
    // ------------------------------------------------------------------

    @Test
    public void firmaServidorFirmar_ok() throws Exception {
        FitxerDto fitxer = FitxerDto.builder().nom("fitxer.pdf").contentType("application/pdf").contingut(new byte[]{1, 2}).build();
        SignaturaResposta resposta = SignaturaResposta.builder().nom("fitxer.pdf").mime("application/pdf").contingut(new byte[]{9}).build();
        when(firmaServidorPlugin.signar(any())).thenReturn(resposta);

        SignaturaResposta result = pluginHelper.firmaServidorFirmar(fitxer, FirmaServidorPlugin.TipusFirma.PADES, "motiu", "ca", "PET-1");

        assertEquals(resposta, result);
        verify(integracioHelper).addAccioOk(eq("PET-1"), eq(IntegracioHelper.INTCODI_FIRMASERV), anyString(), any(), any(), anyLongCompat());
    }

    @Test
    public void firmaServidorFirmar_excepcio() throws Exception {
        FitxerDto fitxer = FitxerDto.builder().nom("fitxer.pdf").contentType("application/pdf").contingut(new byte[]{1, 2}).build();
        when(firmaServidorPlugin.signar(any())).thenThrow(new SistemaExternException("no signa"));

        SistemaExternException thrown = assertThrows(SistemaExternException.class,
                () -> pluginHelper.firmaServidorFirmar(fitxer, FirmaServidorPlugin.TipusFirma.PADES, "motiu", "ca", "PET-1"));

        assertTrue(thrown.getMessage().contains("no signa"));
        verify(integracioHelper).addAccioError(eq("PET-1"), eq(IntegracioHelper.INTCODI_FIRMASERV), anyString(), any(), any(), anyLongCompat(), anyString(), any());
    }

    // ------------------------------------------------------------------
    // arxiuExpedientCrear
    // ------------------------------------------------------------------

    @Test
    public void arxiuExpedientCrear_ambClassificacio_ok() throws Exception {
        ContingutArxiu creat = new ContingutArxiu(ContingutTipus.EXPEDIENT);
        creat.setIdentificador("EXP-1");
        when(arxiuPlugin.expedientCrear(any(Expedient.class))).thenReturn(creat);

        String result = pluginHelper.arxiuExpedientCrear("Titol", "12345678A", "ORG", "CLASS-1", "PROC", "SERIE");

        assertEquals("EXP-1", result);
        verify(integracioHelper).addAccioOk(eq("Titol"), eq(IntegracioHelper.INTCODI_FIRMASERV), anyString(), any(), any(), anyLongCompat());
    }

    @Test
    public void arxiuExpedientCrear_senseClassificacio_construeixClassificacio() throws Exception {
        ContingutArxiu creat = new ContingutArxiu(ContingutTipus.EXPEDIENT);
        creat.setIdentificador("EXP-2");
        when(arxiuPlugin.expedientCrear(any(Expedient.class))).thenReturn(creat);

        String result = pluginHelper.arxiuExpedientCrear("Titol2", "12345678A", "ORG", null, "7", "SERIE");

        assertEquals("EXP-2", result);
    }

    @Test
    public void arxiuExpedientCrear_excepcio() throws Exception {
        when(arxiuPlugin.expedientCrear(any(Expedient.class))).thenThrow(new ArxiuException("no crea"));

        SistemaExternException thrown = assertThrows(SistemaExternException.class,
                () -> pluginHelper.arxiuExpedientCrear("Titol", "12345678A", "ORG", "CLASS-1", "PROC", "SERIE"));

        assertTrue(thrown.getMessage().contains("no crea"));
        verify(integracioHelper).addAccioError(eq("Titol"), eq(IntegracioHelper.INTCODI_FIRMASERV), anyString(), any(), any(), anyLongCompat(), anyString(), any());
    }

    // ------------------------------------------------------------------
    // arxiuExpedientConsultar
    // ------------------------------------------------------------------

    @Test
    public void arxiuExpedientConsultar_ok() throws Exception {
        Expedient expedient = new Expedient();
        expedient.setIdentificador("EXP-1");
        when(arxiuPlugin.expedientDetalls(eq("EXP-1"), isNull())).thenReturn(expedient);

        Expedient result = pluginHelper.arxiuExpedientConsultar("PET-1", "EXP-1");

        assertEquals(expedient, result);
        verify(integracioHelper).addAccioOk(eq("PET-1"), eq(IntegracioHelper.INTCODI_ARXIU), anyString(), any(), any(), anyLongCompat());
    }

    @Test
    public void arxiuExpedientConsultar_excepcio() throws Exception {
        when(arxiuPlugin.expedientDetalls(eq("EXP-1"), isNull())).thenThrow(new ArxiuException("no trobat"));

        assertThrows(SistemaExternException.class, () -> pluginHelper.arxiuExpedientConsultar("PET-1", "EXP-1"));

        verify(integracioHelper).addAccioError(eq("PET-1"), eq(IntegracioHelper.INTCODI_ARXIU), anyString(), any(), any(), anyLongCompat(), anyString(), any());
    }

    // ------------------------------------------------------------------
    // arxiuExpedientCercarAmbNom
    // ------------------------------------------------------------------

    @Test
    public void arxiuExpedientCercarAmbNom_trobat() throws Exception {
        ContingutArxiu ca = new ContingutArxiu(ContingutTipus.EXPEDIENT);
        ca.setIdentificador("EXP-3");
        ConsultaResultat resultat = new ConsultaResultat(1, Collections.singletonList(ca));
        when(arxiuPlugin.expedientConsulta(anyList(), eq(0), eq(1))).thenReturn(resultat);

        ContingutArxiu result = pluginHelper.arxiuExpedientCercarAmbNom("nom-expedient");

        assertEquals(ca, result);
    }

    @Test
    public void arxiuExpedientCercarAmbNom_noTrobat() throws Exception {
        ConsultaResultat resultat = new ConsultaResultat(0, Collections.emptyList());
        when(arxiuPlugin.expedientConsulta(anyList(), eq(0), eq(1))).thenReturn(resultat);

        ContingutArxiu result = pluginHelper.arxiuExpedientCercarAmbNom("nom-expedient");

        assertNull(result);
    }

    @Test
    public void arxiuExpedientCercarAmbNom_excepcio() throws Exception {
        when(arxiuPlugin.expedientConsulta(anyList(), eq(0), eq(1))).thenThrow(new ArxiuException("error cerca"));

        assertThrows(SistemaExternException.class, () -> pluginHelper.arxiuExpedientCercarAmbNom("nom-expedient"));

        verify(integracioHelper).addAccioError(eq("nom-expedient"), eq(IntegracioHelper.INTCODI_ARXIU), anyString(), any(), any(), anyLongCompat(), anyString(), any());
    }

    // ------------------------------------------------------------------
    // arxiuExpedientTancar / arxiuExpedientEsborrar
    // ------------------------------------------------------------------

    @Test
    public void arxiuExpedientTancar_ok() throws Exception {
        pluginHelper.arxiuExpedientTancar("PET-1", "EXP-1");

        verify(arxiuPlugin).expedientTancar("EXP-1");
        verify(integracioHelper).addAccioOk(eq("PET-1"), eq(IntegracioHelper.INTCODI_ARXIU), anyString(), any(), any(), anyLongCompat());
    }

    @Test
    public void arxiuExpedientTancar_excepcio() throws Exception {
        when(arxiuPlugin.expedientTancar("EXP-1")).thenThrow(new ArxiuException("no tanca"));

        assertThrows(SistemaExternException.class, () -> pluginHelper.arxiuExpedientTancar("PET-1", "EXP-1"));

        verify(integracioHelper).addAccioError(eq("PET-1"), eq(IntegracioHelper.INTCODI_ARXIU), anyString(), any(), any(), anyLongCompat(), anyString(), any());
    }

    @Test
    public void arxiuExpedientEsborrar_ok() throws Exception {
        pluginHelper.arxiuExpedientEsborrar("PET-1", "EXP-1");

        verify(arxiuPlugin).expedientEsborrar("EXP-1");
        verify(integracioHelper).addAccioOk(eq("PET-1"), eq(IntegracioHelper.INTCODI_ARXIU), anyString(), any(), any(), anyLongCompat());
    }

    @Test
    public void arxiuExpedientEsborrar_excepcio() throws Exception {
        org.mockito.Mockito.doThrow(new ArxiuException("no esborra")).when(arxiuPlugin).expedientEsborrar("EXP-1");

        assertThrows(SistemaExternException.class, () -> pluginHelper.arxiuExpedientEsborrar("PET-1", "EXP-1"));

        verify(integracioHelper).addAccioError(eq("PET-1"), eq(IntegracioHelper.INTCODI_ARXIU), anyString(), any(), any(), anyLongCompat(), anyString(), any());
    }

    // ------------------------------------------------------------------
    // arxiuDocumentGuardarFirmaPades
    // ------------------------------------------------------------------

    @Test
    public void arxiuDocumentGuardarFirmaPades_ok() throws Exception {
        FitxerDto fitxer = FitxerDto.builder().nom("document.pdf").contentType("application/pdf").contingut(new byte[]{1, 2}).build();
        ContingutArxiu creat = new ContingutArxiu(ContingutTipus.DOCUMENT);
        creat.setIdentificador("DOC-1");
        when(arxiuPlugin.documentCrear(any(Document.class), eq("EXP-1"))).thenReturn(creat);

        String result = pluginHelper.arxiuDocumentGuardarFirmaPades(
                "PET-1", "EXP-1", "Titol", "ORG", "SERIE", fitxer,
                ContingutOrigen.CIUTADA, DocumentEstatElaboracio.ORIGINAL, DocumentTipus.COMUNICACIO);

        assertEquals("DOC-1", result);
        verify(integracioHelper).addAccioOk(eq("PET-1"), eq(IntegracioHelper.INTCODI_ARXIU), anyString(), any(), any(), anyLongCompat());
    }

    @Test
    public void arxiuDocumentGuardarFirmaPades_excepcio() throws Exception {
        FitxerDto fitxer = FitxerDto.builder().nom("document.pdf").contentType("application/pdf").contingut(new byte[]{1, 2}).build();
        when(arxiuPlugin.documentCrear(any(Document.class), eq("EXP-1"))).thenThrow(new ArxiuException("no guarda"));

        assertThrows(SistemaExternException.class, () -> pluginHelper.arxiuDocumentGuardarFirmaPades(
                "PET-1", "EXP-1", "Titol", "ORG", "SERIE", fitxer,
                ContingutOrigen.CIUTADA, DocumentEstatElaboracio.ORIGINAL, DocumentTipus.COMUNICACIO));

        verify(integracioHelper).addAccioError(eq("PET-1"), eq(IntegracioHelper.INTCODI_ARXIU), anyString(), any(), any(), anyLongCompat(), anyString(), any());
    }

    // ------------------------------------------------------------------
    // arxiuDocumentConsultarMock
    // ------------------------------------------------------------------

    @Test
    public void arxiuDocumentConsultarMock_retornaDocumentMock() {
        Document document = pluginHelper.arxiuDocumentConsultarMock();

        assertEquals("documentIdentificador-test", document.getIdentificador());
    }

    // ------------------------------------------------------------------
    // arxiuDocumentConsultar
    // ------------------------------------------------------------------

    @Test
    public void arxiuDocumentConsultar_okSenseContingutNiVersioImprimible() throws Exception {
        Document document = new Document();
        document.setIdentificador("DOC-1");
        when(arxiuPlugin.documentDetalls("DOC-1", "1", false)).thenReturn(document);

        Document result = pluginHelper.arxiuDocumentConsultar("PET-1", "DOC-1", "1", false, false);

        assertEquals(document, result);
        verify(arxiuPlugin, never()).documentImprimible(anyString());
        verify(integracioHelper).addAccioOk(eq("PET-1"), eq(IntegracioHelper.INTCODI_ARXIU), anyString(), any(), any(), anyLongCompat());
    }

    @Test
    public void arxiuDocumentConsultar_ambContingutNull_esRegistraErrorPeroNoPeta() throws Exception {
        Document document = new Document();
        document.setIdentificador("DOC-2");
        document.setContingut(null);
        when(arxiuPlugin.documentDetalls("DOC-2", "1", true)).thenReturn(document);

        Document result = pluginHelper.arxiuDocumentConsultar("PET-1", "DOC-2", "1", true, false);

        assertEquals(document, result);
    }

    @Test
    public void arxiuDocumentConsultar_generaVersioImprimible() throws Exception {
        Document document = new Document();
        document.setIdentificador("DOC-3");
        DocumentContingut contingut = new DocumentContingut();
        contingut.setTipusMime("application/pdf");
        document.setContingut(contingut);
        Firma firma = new Firma();
        firma.setTipus(FirmaTipus.PADES);
        firma.setPerfil(FirmaPerfil.EPES);
        document.setFirmes(Collections.singletonList(firma));
        when(arxiuPlugin.documentDetalls("DOC-3", "1", true)).thenReturn(document);
        DocumentContingut impr = new DocumentContingut();
        impr.setTipusMime("application/pdf");
        when(arxiuPlugin.documentImprimible("DOC-3")).thenReturn(impr);

        Document result = pluginHelper.arxiuDocumentConsultar("PET-1", "DOC-3", "1", true, true);

        assertEquals(impr, result.getContingut());
        verify(arxiuPlugin).documentImprimible("DOC-3");
    }

    @Test
    public void arxiuDocumentConsultar_excepcioAmbCausaDiferent() throws Exception {
        RuntimeException causa = new RuntimeException("causa arrel");
        ArxiuException ex = new ArxiuException("error arxiu", causa);
        when(arxiuPlugin.documentDetalls("DOC-4", "1", false)).thenThrow(ex);

        SistemaExternException thrown = assertThrows(SistemaExternException.class,
                () -> pluginHelper.arxiuDocumentConsultar("PET-1", "DOC-4", "1", false, false));

        assertTrue(thrown.getMessage().contains("causa arrel"));
        verify(integracioHelper).addAccioError(eq("PET-1"), eq(IntegracioHelper.INTCODI_ARXIU), anyString(), any(), any(), anyLongCompat(), anyString(), eq(ex));
    }

    @Test
    public void arxiuDocumentConsultar_excepcioSenseCausa() throws Exception {
        ArxiuException ex = new ArxiuException("error simple");
        when(arxiuPlugin.documentDetalls("DOC-5", "1", false)).thenThrow(ex);

        SistemaExternException thrown = assertThrows(SistemaExternException.class,
                () -> pluginHelper.arxiuDocumentConsultar("PET-1", "DOC-5", "1", false, false));

        assertTrue(thrown.getMessage().contains("error simple"));
    }

    // ------------------------------------------------------------------
    // getOrganigramaOrganGestor / getOrganigramaAmbPare
    // ------------------------------------------------------------------

    @Test
    public void getOrganigramaOrganGestor_ok() throws Exception {
        NodeDir3 node = mock(NodeDir3.class);
        Map<String, NodeDir3> organigrama = Collections.singletonMap("A01", node);
        when(unitatsOrganitzativesPlugin.organigrama("A01")).thenReturn(organigrama);

        Map<String, NodeDir3> result = pluginHelper.getOrganigramaOrganGestor("A01");

        assertEquals(organigrama, result);
        verify(integracioHelper).addAccioOk(eq(IntegracioHelper.INTCODI_ORGANS), anyString(), any(), any(), anyLongCompat());
    }

    @Test
    public void getOrganigramaOrganGestor_excepcio() throws Exception {
        when(unitatsOrganitzativesPlugin.organigrama("A01")).thenThrow(new SistemaExternException("no organigrama"));

        assertThrows(SistemaExternException.class, () -> pluginHelper.getOrganigramaOrganGestor("A01"));

        verify(integracioHelper).addAccioError(eq(IntegracioHelper.INTCODI_ORGANS), anyString(), any(), any(), anyLongCompat(), anyString(), any());
    }

    @Test
    public void getOrganigramaAmbPare_ok() throws Exception {
        UnitatOrganitzativa unitat = mock(UnitatOrganitzativa.class);
        List<UnitatOrganitzativa> organs = Collections.singletonList(unitat);
        when(unitatsOrganitzativesPlugin.findAmbPare("A01")).thenReturn(organs);

        List<UnitatOrganitzativa> result = pluginHelper.getOrganigramaAmbPare("A01");

        assertEquals(organs, result);
        verify(integracioHelper).addAccioOk(eq(IntegracioHelper.INTCODI_ORGANS), anyString(), any(), any(), anyLongCompat());
    }

    @Test
    public void getOrganigramaAmbPare_excepcio() throws Exception {
        when(unitatsOrganitzativesPlugin.findAmbPare("A01")).thenThrow(new SistemaExternException("no fills"));

        assertThrows(SistemaExternException.class, () -> pluginHelper.getOrganigramaAmbPare("A01"));

        verify(integracioHelper).addAccioError(eq(IntegracioHelper.INTCODI_ORGANS), anyString(), any(), any(), anyLongCompat(), anyString(), any());
    }

    // ------------------------------------------------------------------
    // resetPlugins
    // ------------------------------------------------------------------

    @Test
    public void resetPlugins_buidaTotsElsPlugins() {
        pluginHelper.resetPlugins();

        assertNull(ReflectionTestUtils.getField(pluginHelper, "dadesUsuariPlugin"));
        assertNull(ReflectionTestUtils.getField(pluginHelper, "firmaServidorPlugin"));
        assertNull(ReflectionTestUtils.getField(pluginHelper, "arxiuPlugin"));
        assertNull(ReflectionTestUtils.getField(pluginHelper, "unitatsOrganitzativesPlugin"));
    }

    @Test
    public void resetPlugins_grupUsuaris_nomesBuidaUsuaris() {
        pluginHelper.resetPlugins("USUARIS");

        assertNull(ReflectionTestUtils.getField(pluginHelper, "dadesUsuariPlugin"));
        assertEquals(firmaServidorPlugin, ReflectionTestUtils.getField(pluginHelper, "firmaServidorPlugin"));
        assertEquals(arxiuPlugin, ReflectionTestUtils.getField(pluginHelper, "arxiuPlugin"));
        assertEquals(unitatsOrganitzativesPlugin, ReflectionTestUtils.getField(pluginHelper, "unitatsOrganitzativesPlugin"));
    }

    @Test
    public void resetPlugins_grupFirmaServidor_nomesBuidaFirma() {
        pluginHelper.resetPlugins("FIRMA_SERVIDOR");

        assertNull(ReflectionTestUtils.getField(pluginHelper, "firmaServidorPlugin"));
        assertEquals(dadesUsuariPlugin, ReflectionTestUtils.getField(pluginHelper, "dadesUsuariPlugin"));
    }

    @Test
    public void resetPlugins_grupArxiu_nomesBuidaArxiu() {
        pluginHelper.resetPlugins("ARXIU");

        assertNull(ReflectionTestUtils.getField(pluginHelper, "arxiuPlugin"));
        assertEquals(dadesUsuariPlugin, ReflectionTestUtils.getField(pluginHelper, "dadesUsuariPlugin"));
    }

    @Test
    public void resetPlugins_grupUnitats_nomesBuidaUnitats() {
        pluginHelper.resetPlugins("UNITATS");

        assertNull(ReflectionTestUtils.getField(pluginHelper, "unitatsOrganitzativesPlugin"));
        assertEquals(dadesUsuariPlugin, ReflectionTestUtils.getField(pluginHelper, "dadesUsuariPlugin"));
    }

    @Test
    public void resetPlugins_grupDesconegut_noBuidaRes() {
        pluginHelper.resetPlugins("ALTRE");

        assertEquals(dadesUsuariPlugin, ReflectionTestUtils.getField(pluginHelper, "dadesUsuariPlugin"));
        assertEquals(firmaServidorPlugin, ReflectionTestUtils.getField(pluginHelper, "firmaServidorPlugin"));
        assertEquals(arxiuPlugin, ReflectionTestUtils.getField(pluginHelper, "arxiuPlugin"));
        assertEquals(unitatsOrganitzativesPlugin, ReflectionTestUtils.getField(pluginHelper, "unitatsOrganitzativesPlugin"));
    }

    private static long anyLongCompat() {
        return org.mockito.ArgumentMatchers.anyLong();
    }
}
