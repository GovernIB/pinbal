package es.caib.pinbal.logic.helper;

import es.caib.pinbal.logic.intf.dto.arxiu.ArxiuDetallDto;
import es.caib.pluginsib.arxiu.api.ContingutOrigen;
import es.caib.pluginsib.arxiu.api.Document;
import es.caib.pluginsib.arxiu.api.DocumentEstatElaboracio;
import es.caib.pluginsib.arxiu.api.DocumentMetadades;
import org.junit.jupiter.api.Test;

import java.util.Collections;

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
}
