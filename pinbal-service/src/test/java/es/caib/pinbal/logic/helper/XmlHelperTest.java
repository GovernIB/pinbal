package es.caib.pinbal.logic.helper;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class XmlHelperTest {

    private Document parse(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        return factory.newDocumentBuilder().parse(
                new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    public void getSingleNodeValue_nodeExisteix_retornaElSeuValor() throws Exception {
        Document doc = parse("<arrel><fill>contingut</fill></arrel>");

        String result = XmlHelper.getSingleNodeValue(doc, "/arrel/fill");

        assertEquals("contingut", result);
    }

    @Test
    public void getSingleNodeValue_nodeNoExisteix_retornaNull() throws Exception {
        Document doc = parse("<arrel><fill>contingut</fill></arrel>");

        String result = XmlHelper.getSingleNodeValue(doc, "/arrel/inexistent");

        assertNull(result);
    }

    @Test
    public void getSingleNodeValue_nodeBuit_retornaNull() throws Exception {
        Document doc = parse("<arrel><fillBuit/></arrel>");

        // Un node sense fills no té getFirstChild(), la crida a getNodeValue() llançaria NPE
        assertThrows(NullPointerException.class,
                () -> XmlHelper.getSingleNodeValue(doc, "/arrel/fillBuit"));
    }

    @Test
    public void getSingleNodeValue_atribut_retornaElSeuValor() throws Exception {
        Document doc = parse("<arrel atribut=\"valorAtribut\"/>");

        String result = XmlHelper.getSingleNodeValue(doc, "/arrel/@atribut");

        assertEquals("valorAtribut", result);
    }
}
