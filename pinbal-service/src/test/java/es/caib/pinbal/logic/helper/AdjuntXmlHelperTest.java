package es.caib.pinbal.logic.helper;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

public class AdjuntXmlHelperTest {

    private static final String XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><arrel><fill>Valor àèò</fill></arrel>";

    @Test
    public void parse_nullOBuit_retornaNull() {
        assertNull(AdjuntXmlHelper.parse(null));
        assertNull(AdjuntXmlHelper.parse(""));
        assertNull(AdjuntXmlHelper.parse("   "));
    }

    @Test
    public void parse_base64_retornaDocument() {
        String base64 = Base64.getEncoder().encodeToString(XML.getBytes(StandardCharsets.UTF_8));

        Document doc = AdjuntXmlHelper.parse(base64);

        assertEquals("arrel", doc.getDocumentElement().getNodeName());
        assertEquals("Valor àèò", doc.getDocumentElement().getElementsByTagName("fill").item(0).getTextContent());
    }

    @Test
    public void parse_base64AmbSaltsDeLinia_retornaDocument() {
        String base64 = Base64.getMimeEncoder(10, "\r\n".getBytes(StandardCharsets.US_ASCII))
                .encodeToString(XML.getBytes(StandardCharsets.UTF_8));
        assertTrue(base64.contains("\r\n"));

        Document doc = AdjuntXmlHelper.parse(base64);

        assertEquals("arrel", doc.getDocumentElement().getNodeName());
    }

    @Test
    public void parse_base64Iso88591_respectaCodificacioDeclarada() {
        String xml = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><arrel>àèò</arrel>";
        String base64 = Base64.getEncoder().encodeToString(xml.getBytes(StandardCharsets.ISO_8859_1));

        Document doc = AdjuntXmlHelper.parse(base64);

        assertEquals("àèò", doc.getDocumentElement().getTextContent());
    }

    @Test
    public void parse_xmlEnTextPla_retornaDocument() {
        Document doc = AdjuntXmlHelper.parse("  " + XML);

        assertEquals("arrel", doc.getDocumentElement().getNodeName());
    }

    @Test
    public void parse_base64NoValid_llancaIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> AdjuntXmlHelper.parse("això no és base64!"));
    }

    @Test
    public void parse_base64QueNoEsXml_llancaIllegalArgument() {
        String base64 = Base64.getEncoder().encodeToString("text pla".getBytes(StandardCharsets.UTF_8));

        assertThrows(IllegalArgumentException.class, () -> AdjuntXmlHelper.parse(base64));
    }

    @Test
    public void parse_xmlMalFormat_llancaIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> AdjuntXmlHelper.parse("<arrel><fill></arrel>"));
    }

    @Test
    public void parse_xmlAmbDoctype_esRebutja() {
        // Protecció XXE: no s'admeten DTD
        String xxe = "<?xml version=\"1.0\"?><!DOCTYPE arrel [<!ENTITY xxe SYSTEM \"file:///etc/passwd\">]><arrel>&xxe;</arrel>";
        String base64 = Base64.getEncoder().encodeToString(xxe.getBytes(StandardCharsets.UTF_8));

        assertThrows(IllegalArgumentException.class, () -> AdjuntXmlHelper.parse(base64));
    }

}
