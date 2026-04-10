package es.caib.pinbal.scsp.mock;

import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ClienteUnicoMockPersistentTest {

    @Test
    public void injectaDocumentBase64AlsXpathConfigurats() throws Exception {
        ClienteUnicoMockPersistent mock = new ClienteUnicoMockPersistent();
        String xml = "<DatosEspecificos><Resposta><Estat>OK</Estat></Resposta></DatosEspecificos>";

        String xmlAmbDocument = mock.injectarDocumentsEnXmlResposta(
                xml,
                Arrays.asList("DatosEspecificos/Resposta/DocumentPdf"),
                "PDF-MOCK".getBytes("UTF-8"));

        Element datosEspecificos = mock.parseDatosEspecificos(xmlAmbDocument);
        assertNotNull(datosEspecificos);

        NodeList documents = datosEspecificos.getElementsByTagName("DocumentPdf");
        assertEquals(1, documents.getLength());
        assertEquals("UERGLU1PQ0s=", documents.item(0).getTextContent());
    }

    @Test
    public void extreuDatosEspecificosDesDunWrapperTransmisionDatos() throws Exception {
        ClienteUnicoMockPersistent mock = new ClienteUnicoMockPersistent();
        String xml = "<TransmisionDatos><DatosGenericos/><DatosEspecificos><Resposta><Camp>1</Camp></Resposta></DatosEspecificos></TransmisionDatos>";

        Element datosEspecificos = mock.parseDatosEspecificos(xml);

        assertNotNull(datosEspecificos);
        assertEquals("DatosEspecificos", datosEspecificos.getNodeName());
        assertEquals("1", datosEspecificos.getElementsByTagName("Camp").item(0).getTextContent());
    }

    @Test
    public void generaRespostaAmbPrestacionsRepetidesPerServeiTsd() throws Exception {
        ClienteUnicoMockPersistent mock = new ClienteUnicoMockPersistent();

        String xmlResposta = mock.generarXmlRespostaPerServei(
                ClienteUnicoMockPersistent.SERVEI_TSD_PRESTACIONS,
                "PINBAL00000000000000392071");

        Element datosEspecificos = mock.parseDatosEspecificos(xmlResposta);
        assertNotNull(datosEspecificos);

        NodeList prestacions = datosEspecificos.getElementsByTagName("prestacion");
        assertEquals(3, prestacions.getLength());
        assertEquals("3.871,16", ((Element) prestacions.item(0)).getElementsByTagName("importePrestacion").item(0).getTextContent());
        assertEquals("800,50", ((Element) prestacions.item(1)).getElementsByTagName("importePrestacion").item(0).getTextContent());
        assertEquals("3.871,16", ((Element) prestacions.item(2)).getElementsByTagName("importePrestacion").item(0).getTextContent());
        assertEquals("282,35", ((Element) prestacions.item(0)).getElementsByTagName("importeEfectivo").item(0).getTextContent());
    }

    @Test
    public void noGeneraRespostaEspecialPerAltresServeis() {
        ClienteUnicoMockPersistent mock = new ClienteUnicoMockPersistent();

        assertNull(mock.generarXmlRespostaPerServei("ALTRE_SERVEI", "SOL-1"));
    }
}
