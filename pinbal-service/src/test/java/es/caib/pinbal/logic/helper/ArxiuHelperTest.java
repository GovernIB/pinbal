package es.caib.pinbal.logic.helper;

import es.caib.pinbal.logic.intf.dto.arxiu.ArxiuDetallDto;
import es.caib.pinbal.logic.intf.dto.arxiu.ArxiuFirmaDto;
import es.caib.pinbal.logic.intf.dto.arxiu.ArxiuFirmaPerfilEnumDto;
import es.caib.pluginsib.arxiu.api.ContingutOrigen;
import es.caib.pluginsib.arxiu.api.Document;
import es.caib.pluginsib.arxiu.api.DocumentEstatElaboracio;
import es.caib.pluginsib.arxiu.api.DocumentMetadades;
import es.caib.pluginsib.arxiu.api.Firma;
import es.caib.pluginsib.arxiu.api.FirmaPerfil;
import es.caib.pluginsib.arxiu.api.FirmaTipus;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ArxiuHelperTest {

    @Test
    public void getArxiuDetall_documentAmbMetadadesNull_noPetaMeta() {
        Document doc = mock(Document.class);
        when(doc.getIdentificador()).thenReturn("ID001");
        when(doc.getNom()).thenReturn("document.pdf");
        when(doc.getMetadades()).thenReturn(null);
        when(doc.getEstat()).thenReturn(null);
        when(doc.getFirmes()).thenReturn(null);

        ArxiuDetallDto result = ArxiuHelper.getArxiuDetall(doc);

        assertNotNull(result);
        assertEquals("ID001", result.getIdentificador());
        assertEquals("document.pdf", result.getNom());
    }

    @Test
    public void getArxiuDetall_documentSenseFirmes_firmesNull() {
        Document doc = mock(Document.class);
        when(doc.getIdentificador()).thenReturn("ID002");
        when(doc.getNom()).thenReturn("doc2.pdf");
        when(doc.getMetadades()).thenReturn(null);
        when(doc.getEstat()).thenReturn(null);
        when(doc.getFirmes()).thenReturn(null);

        ArxiuDetallDto result = ArxiuHelper.getArxiuDetall(doc);

        assertNull(result.getFirmes());
    }

    @Test
    public void getArxiuDetall_documentAmbFirmesVuides_firmesVuides() {
        Document doc = mock(Document.class);
        when(doc.getIdentificador()).thenReturn("ID003");
        when(doc.getNom()).thenReturn("doc3.pdf");
        when(doc.getMetadades()).thenReturn(null);
        when(doc.getEstat()).thenReturn(null);
        when(doc.getFirmes()).thenReturn(Collections.emptyList());

        ArxiuDetallDto result = ArxiuHelper.getArxiuDetall(doc);

        assertNotNull(result.getFirmes());
        assertTrue(result.getFirmes().isEmpty());
    }

    @Test
    public void getArxiuDetall_documentAmbMetadades_emplenaMetadades() {
        Document doc = mock(Document.class);
        DocumentMetadades meta = mock(DocumentMetadades.class);
        when(meta.getVersioNti()).thenReturn("1.0");
        when(meta.getIdentificador()).thenReturn("ENI001");
        when(meta.getSerieDocumental()).thenReturn("SD001");
        when(meta.getFormat()).thenReturn(null);
        when(meta.getOrigen()).thenReturn(ContingutOrigen.CIUTADA);
        when(meta.getEstatElaboracio()).thenReturn(DocumentEstatElaboracio.ORIGINAL);
        when(doc.getIdentificador()).thenReturn("ID004");
        when(doc.getNom()).thenReturn("doc4.pdf");
        when(doc.getMetadades()).thenReturn(meta);
        when(doc.getEstat()).thenReturn(null);
        when(doc.getFirmes()).thenReturn(null);

        ArxiuDetallDto result = ArxiuHelper.getArxiuDetall(doc);

        assertNotNull(result);
        assertEquals("1.0", result.getEniVersio());
        assertEquals("ENI001", result.getEniIdentificador());
    }

    // ------------------------- firmes -------------------------

    private static Document documentAmbFirma(Firma firma, Map<String, Object> metadadesAddicionals) {
        Document doc = mock(Document.class);
        DocumentMetadades meta = mock(DocumentMetadades.class);
        when(meta.getMetadadesAddicionals()).thenReturn(metadadesAddicionals);
        when(meta.getOrigen()).thenReturn(ContingutOrigen.ADMINISTRACIO);
        when(meta.getEstatElaboracio()).thenReturn(DocumentEstatElaboracio.ORIGINAL);
        when(doc.getIdentificador()).thenReturn("ID010");
        when(doc.getNom()).thenReturn("justificant.pdf");
        when(doc.getMetadades()).thenReturn(meta);
        when(doc.getEstat()).thenReturn(null);
        when(doc.getFirmes()).thenReturn(Collections.singletonList(firma));
        return doc;
    }

    private static Firma firmaPades(FirmaPerfil perfil) {
        Firma firma = new Firma();
        firma.setTipus(FirmaTipus.PADES);
        firma.setPerfil(perfil);
        return firma;
    }

    @Test
    public void getArxiuDetall_firmaPadesSenseDetalls_esCompletaAmbDadesDelDocument() {
        Map<String, Object> metadades = new HashMap<>();
        metadades.put("eni:perfil_firma", "EPES");

        ArxiuDetallDto result = ArxiuHelper.getArxiuDetall(documentAmbFirma(firmaPades(null), metadades));

        ArxiuFirmaDto firma = result.getFirmes().get(0);
        assertTrue(firma.isIntegrada());
        assertEquals(ArxiuFirmaPerfilEnumDto.EPES, firma.getPerfil());
        assertEquals("EPES", firma.getPerfilDescripcio());
        assertEquals("justificant.pdf", firma.getFitxerNom());
        assertEquals("application/pdf", firma.getTipusMime());
    }

    @Test
    public void getArxiuDetall_perfilAmbFormatNoEstandard_esMostraElValorDeLArxiu() {
        Map<String, Object> metadades = new HashMap<>();
        metadades.put("eni:perfil_firma", "PERFIL DESCONEGUT");

        ArxiuFirmaDto firma = ArxiuHelper.getArxiuDetall(documentAmbFirma(firmaPades(null), metadades)).getFirmes().get(0);

        assertNull(firma.getPerfil());
        assertEquals("PERFIL DESCONEGUT", firma.getPerfilDescripcio());
    }

    @Test
    public void getArxiuDetall_perfilInformatPelPlugin_esRespecta() {
        Map<String, Object> metadades = new HashMap<>();
        metadades.put("eni:perfil_firma", "EPES");

        ArxiuFirmaDto firma = ArxiuHelper.getArxiuDetall(documentAmbFirma(firmaPades(FirmaPerfil.BASELINE_B_LEVEL), metadades)).getFirmes().get(0);

        assertEquals(ArxiuFirmaPerfilEnumDto.BASELINE_B_LEVEL, firma.getPerfil());
        assertEquals("BASELINE_B_LEVEL", firma.getPerfilDescripcio());
    }

    @Test
    public void getArxiuDetall_firmaDetached_noEsCompletaAmbDadesDelDocument() {
        Firma firmaCades = new Firma();
        firmaCades.setTipus(FirmaTipus.CADES_DET);

        ArxiuFirmaDto firma = ArxiuHelper.getArxiuDetall(documentAmbFirma(firmaCades, null)).getFirmes().get(0);

        assertFalse(firma.isIntegrada());
        assertNull(firma.getFitxerNom());
        assertNull(firma.getTipusMime());
        assertNull(firma.getPerfilDescripcio());
    }

    @Test
    public void toPerfil_acceptaVariantsHabituals() {
        assertEquals(ArxiuFirmaPerfilEnumDto.EPES, ArxiuHelper.toPerfil("EPES"));
        assertEquals(ArxiuFirmaPerfilEnumDto.EPES, ArxiuHelper.toPerfil("AdES-EPES"));
        assertEquals(ArxiuFirmaPerfilEnumDto.BASELINE_B_LEVEL, ArxiuHelper.toPerfil("BASELINE B-Level"));
        assertNull(ArxiuHelper.toPerfil("xyz"));
        assertNull(ArxiuHelper.toPerfil(null));
    }
}
