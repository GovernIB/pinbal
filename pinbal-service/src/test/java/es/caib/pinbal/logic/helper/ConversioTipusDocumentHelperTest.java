package es.caib.pinbal.logic.helper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class ConversioTipusDocumentHelperTest {

    @Mock
    private ConfigHelper configHelper;

    @InjectMocks
    private ConversioTipusDocumentHelper conversioHelper;

    @Test
    public void nomArxiuConvertit_arxiuAmbExtensio_canviaExtensio() {
        String result = conversioHelper.nomArxiuConvertit("document.odt", "pdf");
        assertEquals("document.pdf", result);
    }

    @Test
    public void nomArxiuConvertit_arxiuSenseExtensio_afegeixExtensio() {
        String result = conversioHelper.nomArxiuConvertit("document", "pdf");
        assertEquals("document.pdf", result);
    }

    @Test
    public void nomArxiuConvertit_arxiuAmbMultiplesPunts_canviaUltimExtensio() {
        String result = conversioHelper.nomArxiuConvertit("document.final.odt", "pdf");
        assertEquals("document.final.pdf", result);
    }

    @Test
    public void getArxiuMimeType_arxiuPdf_retornaMimeType() {
        String mimeType = conversioHelper.getArxiuMimeType("document.pdf");
        assertNotNull(mimeType);
    }

    @Test
    public void getArxiuMimeType_arxiuHtml_retornaMimeType() {
        String mimeType = conversioHelper.getArxiuMimeType("pagina.html");
        assertNotNull(mimeType);
    }

    @Test
    public void convertir_mateixaExtensio_copiaDirectament() throws Exception {
        when(configHelper.getConfig("es.caib.pinbal.conversio.tipus", "openoffice")).thenReturn("openoffice");
        byte[] contingut = "test content".getBytes();
        InputStream in = new ByteArrayInputStream(contingut);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        conversioHelper.convertir("document.pdf", in, "pdf", out);

        assertArrayEquals(contingut, out.toByteArray());
    }

    @Test
    public void convertir_mateixaExtensioMinuscules_copiaDirectament() throws Exception {
        when(configHelper.getConfig("es.caib.pinbal.conversio.tipus", "openoffice")).thenReturn("openoffice");
        byte[] contingut = "hola".getBytes();
        InputStream in = new ByteArrayInputStream(contingut);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        conversioHelper.convertir("DOCUMENT.PDF", in, "PDF", out);

        assertArrayEquals(contingut, out.toByteArray());
    }

    @Test
    public void convertir_arxiuSenseExtensio_copiaDirectament() throws Exception {
        when(configHelper.getConfig("es.caib.pinbal.conversio.tipus", "openoffice")).thenReturn("openoffice");
        byte[] contingut = "sense ext".getBytes();
        InputStream in = new ByteArrayInputStream(contingut);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        conversioHelper.convertir("document", in, "pdf", out);

        assertArrayEquals(contingut, out.toByteArray());
    }

    @Test
    public void convertir_conversioODTaPDF_tipoXdocreport_llancaExcepcio() throws Exception {
        when(configHelper.getConfig("es.caib.pinbal.conversio.tipus", "openoffice")).thenReturn("xdocreport");
        byte[] contingut = "odt content".getBytes();
        InputStream in = new ByteArrayInputStream(contingut);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // XDocReport intentarà processar el contingut com a ODT i fallarà
        assertThrows(Exception.class, () ->
                conversioHelper.convertir("document.odt", in, "pdf", out));
    }
}
