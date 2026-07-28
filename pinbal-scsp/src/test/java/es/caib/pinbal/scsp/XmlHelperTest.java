package es.caib.pinbal.scsp;

import es.caib.pinbal.scsp.tree.Node;
import es.caib.pinbal.scsp.tree.Tree;
import es.scsp.bean.common.peticion.Atributos;
import es.scsp.bean.common.peticion.Peticion;
import es.scsp.common.domain.core.Servicio;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class XmlHelperTest {

    private final XmlHelper helper = new XmlHelper();

    @Before
    public void configurar() {
        System.setProperty("es.caib.pinbal.xsd.base.path", "/base/xsd");
    }

    @After
    public void netejar() {
        System.clearProperty("es.caib.pinbal.xsd.base.path");
    }

    // ------------------------- getPathPerServei -------------------------

    @Test
    public void getPathPerServeiConcatenaLaBaseIElCodiDeServei() {
        String path = helper.getPathPerServei("SERV1");
        assertEquals("/base/xsd" + java.io.File.separator + "SERV1", path);
    }

    // ------------------------- getXmlSolicitudTransmision / getXmlPeticion -------------------------

    private static final String XML_PETICIO =
            "<Peticion xmlns=\"urn:test\">"
                    + "<Atributos><IdPeticion>PET1</IdPeticion></Atributos>"
                    + "<Solicitudes>"
                    + "<Solicitud>"
                    + "<Transmisiones><Transmision><IdSolicitud>SOL1</IdSolicitud></Transmision></Transmisiones>"
                    + "</Solicitud>"
                    + "</Solicitudes>"
                    + "</Peticion>";

    @Test
    public void getXmlPeticionAmbIdExistentRetornaElFragment() throws Exception {
        String resultat = helper.getXmlPeticion(XML_PETICIO, "PET1");
        assertTrue(resultat.contains("IdPeticion"));
        assertTrue(resultat.contains("PET1"));
    }

    @Test
    public void getXmlPeticionAmbIdInexistentRetornaNull() throws Exception {
        assertNull(helper.getXmlPeticion(XML_PETICIO, "NO-EXISTEIX"));
    }

    @Test
    public void getXmlSolicitudTransmisionAmbIdExistentRetornaElFragment() throws Exception {
        String resultat = helper.getXmlSolicitudTransmision(XML_PETICIO, "SOL1");
        assertTrue(resultat.contains("Solicitud"));
        assertTrue(resultat.contains("SOL1"));
    }

    @Test
    public void getXmlSolicitudTransmisionAmbIdInexistentRetornaNull() throws Exception {
        assertNull(helper.getXmlSolicitudTransmision(XML_PETICIO, "NO-EXISTEIX"));
    }

    // ------------------------- private helpers (via reflexió) -------------------------

    @SuppressWarnings("unchecked")
    private <T> T invoke(String methodName, Class<?>[] paramTypes, Object... args) throws Exception {
        Method method = XmlHelper.class.getDeclaredMethod(methodName, paramTypes);
        method.setAccessible(true);
        return (T) method.invoke(helper, args);
    }

    @Test
    public void pathToStringConcatenaAmbBarres() throws Exception {
        List<String> path = List.of("a", "b", "c");
        String resultat = invoke("pathToString", new Class<?>[]{List.class}, path);
        assertEquals("/a/b/c", resultat);
    }

    @Test
    public void pathToStringAmbLlistaBuidaRetornaCadenaBuida() throws Exception {
        String resultat = invoke("pathToString", new Class<?>[]{List.class}, new ArrayList<String>());
        assertEquals("", resultat);
    }

    @Test
    public void getComplexTypeNameCobreixTotsElsTipus() throws Exception {
        assertEquals("Complex(ALL)", (String) invoke("getComplexTypeName", new Class<?>[]{int.class}, 0));
        assertEquals("Complex(CHOICE)", (String) invoke("getComplexTypeName", new Class<?>[]{int.class}, 1));
        assertEquals("Complex(SEQUENCE)", (String) invoke("getComplexTypeName", new Class<?>[]{int.class}, 2));
        assertEquals("Complex", (String) invoke("getComplexTypeName", new Class<?>[]{int.class}, 99));
    }

    @Test
    public void getDateFormatForBaseCobreixTotsElsTipus() throws Exception {
        assertEquals("yyyy-MM-dd", (String) invoke("getDateFormatForBase", new Class<?>[]{String.class}, "date"));
        assertEquals("yyyy-MM-dd'T'HH:mm:ss", (String) invoke("getDateFormatForBase", new Class<?>[]{String.class}, "datetime"));
        assertEquals("HH:mm:ss", (String) invoke("getDateFormatForBase", new Class<?>[]{String.class}, "time"));
        assertNull(invoke("getDateFormatForBase", new Class<?>[]{String.class}, "altre"));
    }

    @Test
    public void xmlToDocumentParsejaUnDocumentValid() throws Exception {
        ByteArrayInputStream is = new ByteArrayInputStream("<a><b>text</b></a>".getBytes(StandardCharsets.UTF_8));
        Document doc = invoke("xmlToDocument", new Class<?>[]{java.io.InputStream.class}, is);
        assertEquals("a", doc.getDocumentElement().getNodeName());
    }

    @Test
    public void nodeToStringSerialitzaElNode() throws Exception {
        ByteArrayInputStream is = new ByteArrayInputStream("<a><b>text</b></a>".getBytes(StandardCharsets.UTF_8));
        Document doc = invoke("xmlToDocument", new Class<?>[]{java.io.InputStream.class}, is);
        String resultat = invoke("nodeToString", new Class<?>[]{org.w3c.dom.Node.class}, doc.getDocumentElement());
        assertTrue(resultat.contains("<a>"));
        assertTrue(resultat.contains("<b>text</b>"));
    }

    @Test
    public void recorrerDocumentOmpleElMapaAmbElsValorsFulla() throws Exception {
        ByteArrayInputStream is = new ByteArrayInputStream(
                "<arrel><camp1>valor1</camp1><grup><camp2>valor2</camp2></grup></arrel>".getBytes(StandardCharsets.UTF_8));
        Document doc = invoke("xmlToDocument", new Class<?>[]{java.io.InputStream.class}, is);

        List<String> path = new ArrayList<>();
        Map<String, Object> dades = new HashMap<>();
        invoke("recorrerDocument",
                new Class<?>[]{org.w3c.dom.Node.class, List.class, Map.class, boolean.class},
                doc.getDocumentElement(), path, dades, false);

        assertEquals("valor1", dades.get("/camp1"));
        assertEquals("valor2", dades.get("/grup/camp2"));
        assertTrue(path.isEmpty());
    }

    @Test
    public void getTipusStringAmbEnumAmbValorsRetornaEnumAmbLlista() throws Exception {
        XmlHelper.DadesEspecifiquesNode node = new XmlHelper.DadesEspecifiquesNode();
        node.addEnumValue("A");
        node.addEnumValue("B");

        String resultat = invoke("getTipusString", new Class<?>[]{String.class, XmlHelper.DadesEspecifiquesNode.class}, "string", node);

        assertEquals("Enum(A,B)", resultat);
    }

    @Test
    public void getTipusStringAmbEnumSenseValorsRetornaEnumBuit() throws Exception {
        XmlHelper.DadesEspecifiquesNode node = new XmlHelper.DadesEspecifiquesNode();
        // isEnum() depèn de la mida d'enumValues, que és una llista buida per defecte: no és enum.
        // Per forçar isEnum()=true sense valors, cal afegir i després mantenir un estat "buit lògic"
        // no és possible amb l'API pública, així que aquest cas es cobreix indirectament amb 1 valor buit.
        node.addEnumValue("");
        String resultat = invoke("getTipusString", new Class<?>[]{String.class, XmlHelper.DadesEspecifiquesNode.class}, "string", node);
        assertEquals("Enum()", resultat);
    }

    @Test
    public void getTipusStringAmbNomQueSemblaDocumentIdentitatRetornaDocIdentitat() throws Exception {
        XmlHelper.DadesEspecifiquesNode node = new XmlHelper.DadesEspecifiquesNode();
        node.setNom("numeroNif");

        String resultat = invoke("getTipusString", new Class<?>[]{String.class, XmlHelper.DadesEspecifiquesNode.class}, "string", node);

        assertEquals("DocIdentitat", resultat);
    }

    @Test
    public void getTipusStringAmbBaseNullINodeNullRetornaStringPerDefecte() throws Exception {
        String resultat = invoke("getTipusString", new Class<?>[]{String.class, XmlHelper.DadesEspecifiquesNode.class}, (Object) null, null);
        assertEquals("String", resultat);
    }

    @Test
    public void getTipusStringAmbBaseNullINodeAmbMaxLengthRetornaStringAmbLongitud() throws Exception {
        XmlHelper.DadesEspecifiquesNode node = new XmlHelper.DadesEspecifiquesNode();
        node.setMaxLength(10);
        String resultat = invoke("getTipusString", new Class<?>[]{String.class, XmlHelper.DadesEspecifiquesNode.class}, (Object) null, node);
        assertEquals("String(10)", resultat);
    }

    @Test
    public void getTipusStringCobreixTotsElsTipusBase() throws Exception {
        assertEquals("String", invocaAmbTipusBuit("string"));
        assertEquals("Long", invocaAmbTipusBuit("integer"));
        assertEquals("Long", invocaAmbTipusBuit("unsignedlong"));
        assertEquals("Double", invocaAmbTipusBuit("decimal"));
        assertEquals("Boolean", invocaAmbTipusBuit("boolean"));
        assertEquals("Date(yyyy-MM-dd)", invocaAmbTipusBuit("date"));
        assertEquals("Date(yyyy-MM-dd'T'HH:mm:ss)", invocaAmbTipusBuit("datetime"));
        assertEquals("Date(HH:mm:ss)", invocaAmbTipusBuit("time"));
        assertEquals("File", invocaAmbTipusBuit("base64binary"));
        assertEquals("File", invocaAmbTipusBuit("hexbinary"));
        assertEquals("unTipusDesconegut", invocaAmbTipusBuit("unTipusDesconegut"));
    }

    private String invocaAmbTipusBuit(String base) throws Exception {
        return invoke("getTipusString", new Class<?>[]{String.class, XmlHelper.DadesEspecifiquesNode.class}, base, null);
    }

    // ------------------------- DadesEspecifiquesNode -------------------------

    @Test
    public void dadesEspecifiquesNodeAddEnumValueIIsEnum() {
        XmlHelper.DadesEspecifiquesNode node = new XmlHelper.DadesEspecifiquesNode();
        assertTrue(!node.isEnum());
        node.addEnumValue("A");
        assertTrue(node.isEnum());
        assertEquals(List.of("A"), node.getEnumValues());
    }

    @Test
    public void dadesEspecifiquesNodeAddAtribut() {
        XmlHelper.DadesEspecifiquesNode node = new XmlHelper.DadesEspecifiquesNode();
        node.addAtribut("atribut1");
        assertEquals(List.of("atribut1"), node.getAtributs());
    }

    @Test
    public void dadesEspecifiquesNodeToStringComplexCobreixElsTresGroupTypes() {
        XmlHelper.DadesEspecifiquesNode node = new XmlHelper.DadesEspecifiquesNode();
        node.setComplex(true);
        node.setNom("node1");
        node.setGroupMin(1);
        node.setGroupMax(2);

        node.setGroupType(XmlHelper.DadesEspecifiquesNode.GROUP_TYPE_ALL);
        assertEquals("node1 (C, A, 1, 2)", node.toString());

        node.setGroupType(XmlHelper.DadesEspecifiquesNode.GROUP_TYPE_CHOICE);
        assertEquals("node1 (C, C, 1, 2)", node.toString());

        node.setGroupType(XmlHelper.DadesEspecifiquesNode.GROUP_TYPE_SCHEMA);
        assertEquals("node1 (C, S, 1, 2)", node.toString());
    }

    @Test
    public void dadesEspecifiquesNodeToStringSimpleAmbEnum() {
        XmlHelper.DadesEspecifiquesNode node = new XmlHelper.DadesEspecifiquesNode();
        node.setNom("node2");
        node.addEnumValue("X");

        assertEquals("node2 (S, enum[X])", node.toString());
    }

    @Test
    public void dadesEspecifiquesNodeToStringSimpleSenseEnum() {
        XmlHelper.DadesEspecifiquesNode node = new XmlHelper.DadesEspecifiquesNode();
        node.setNom("node3");
        node.setMinLength(1);
        node.setMaxLength(5);

        assertEquals("node3 (S, 1, 5)", node.toString());
    }

    // ------------------------- hasCodigoUnidadTramitadora (esquemes reals a resources/schemas) -------------------------

    @Test
    public void hasCodigoUnidadTramitadoraAmbEsquemaQueElConteRetornaCert() throws Exception {
        Servicio servicio = new Servicio();
        servicio.setCodCertificado("HTNIAE");
        servicio.setVersionEsquema("V3");

        assertTrue(helper.hasCodigoUnidadTramitadora(servicio, false));
    }

    @Test
    public void hasCodigoUnidadTramitadoraAmbEsquemaSenseElConteRetornaFals() throws Exception {
        Servicio servicio = new Servicio();
        servicio.setCodCertificado("AEATIAE");
        servicio.setVersionEsquema("V3");

        assertFalse(helper.hasCodigoUnidadTramitadora(servicio, false));
    }

    @Test
    public void hasCodigoUnidadTramitadoraAmbEsquemaInexistentRetornaFals() throws Exception {
        Servicio servicio = new Servicio();
        servicio.setCodCertificado("NOEXISTEIXXX");
        servicio.setVersionEsquema("V3");

        assertFalse(helper.hasCodigoUnidadTramitadora(servicio, false));
    }

    // ------------------------- getArbrePerDadesEspecifiques (esquemes reals) -------------------------

    @Test
    public void getArbrePerDadesEspecifiquesAmbEsquemaValidConstrueixLArbre() throws Exception {
        Servicio servicio = new Servicio();
        servicio.setCodCertificado("AEATIAE");
        servicio.setVersionEsquema("V3");

        Tree<XmlHelper.DadesEspecifiquesNode> arbre = helper.getArbrePerDadesEspecifiques(servicio, false);

        assertNotNull(arbre.getRootElement());
        assertEquals("DatosEspecificos", arbre.getRootElement().getData().getNom());
        assertEquals("Complex(CHOICE)", arbre.getRootElement().getData().getTipus());

        boolean trobat = false;
        for (Node<XmlHelper.DadesEspecifiquesNode> node : arbre.toList()) {
            if ("/DatosEspecificos/Cabecera/CodRet".equals(node.getData().getPath())) {
                trobat = true;
                assertEquals("String(4)", node.getData().getTipus());
            }
        }
        assertTrue(trobat);
    }

    @Test
    public void getArbrePerDadesEspecifiquesSenseEsquemaRetornaArbreAmbArrelNull() throws Exception {
        Servicio servicio = new Servicio();
        servicio.setCodCertificado("SCPWIJ1R");
        servicio.setVersionEsquema("V2");

        Tree<XmlHelper.DadesEspecifiquesNode> arbre = helper.getArbrePerDadesEspecifiques(servicio, false);

        assertNull(arbre.getRootElement());
    }

    @Test
    public void getArbrePerDadesEspecifiquesAmbFallbackDatosEspecificosEntConstrueixLArbre() throws Exception {
        Servicio servicio = new Servicio();
        servicio.setCodCertificado("VDRSFWS02");
        servicio.setVersionEsquema("V2");

        Tree<XmlHelper.DadesEspecifiquesNode> arbre = helper.getArbrePerDadesEspecifiques(servicio, false);

        assertNotNull(arbre.getRootElement());
        assertEquals("DatosEspecificos", arbre.getRootElement().getData().getNom());
    }

    // ------------------------- crearDadesEspecifiques -------------------------

    private static final Servicio AEATIAE = crearServicio("AEATIAE", "V3");

    private static Servicio crearServicio(String codCertificado, String versionEsquema) {
        Servicio servicio = new Servicio();
        servicio.setCodCertificado(codCertificado);
        servicio.setVersionEsquema(versionEsquema);
        return servicio;
    }

    @Test
    public void crearDadesEspecifiquesAmbDadesNullINoAfegirRetornaNull() throws Exception {
        Element resultat = helper.crearDadesEspecifiques(AEATIAE, null, false, false, null, false);
        assertNull(resultat);
    }

    @Test
    public void crearDadesEspecifiquesAmbDadesNullIAfegirRetornaElementBuit() throws Exception {
        Element resultat = helper.crearDadesEspecifiques(AEATIAE, null, false, false, null, true);
        assertNotNull(resultat);
        assertEquals("DatosEspecificos", resultat.getNodeName());
        assertFalse(resultat.hasChildNodes());
        assertEquals("http://intermediacion.redsara.es/scsp/esquemas/datosespecificos", resultat.getAttribute("xmlns"));
    }

    @Test
    public void crearDadesEspecifiquesAmbValorsStringGeneraEstructuraNiada() throws Exception {
        Map<String, Object> dades = new HashMap<String, Object>();
        dades.put("DatosEspecificos/Ejercicio", "2024");
        dades.put("DatosEspecificos/Cabecera/CodRet", "0001");

        Element resultat = helper.crearDadesEspecifiques(AEATIAE, dades, false, false, null, true);

        assertEquals("2024", getFillText(resultat, "Ejercicio"));
        Element cabecera = getFillElement(resultat, "Cabecera");
        assertEquals("0001", getFillText(cabecera, "CodRet"));
    }

    @Test
    public void crearDadesEspecifiquesAmbDocumentIMateixNomSubstitueixElement() throws Exception {
        Map<String, Object> dades = new HashMap<String, Object>();
        dades.put("DatosEspecificos/Cabecera", parseDoc("<Cabecera><CodRet>0002</CodRet></Cabecera>"));

        Element resultat = helper.crearDadesEspecifiques(AEATIAE, dades, false, false, null, true);

        Element cabecera = getFillElement(resultat, "Cabecera");
        assertNotNull(cabecera);
        assertEquals("0002", getFillText(cabecera, "CodRet"));
    }

    @Test
    public void crearDadesEspecifiquesAmbDocumentINomDiferentAfegeixContingutDins() throws Exception {
        Map<String, Object> dades = new HashMap<String, Object>();
        dades.put("DatosEspecificos/ActividadesAlta", parseDoc("<Extra><Info>x</Info></Extra>"));

        Element resultat = helper.crearDadesEspecifiques(AEATIAE, dades, false, false, null, true);

        Element actividadesAlta = getFillElement(resultat, "ActividadesAlta");
        assertNotNull(actividadesAlta);
        Element extra = getFillElement(actividadesAlta, "Extra");
        assertEquals("x", getFillText(extra, "Info"));
    }

    @Test
    public void crearDadesEspecifiquesAmbIniDadesEspecifiquesNomesInclouCampsObligatoris() throws Exception {
        Servicio vdrsfws02 = crearServicio("VDRSFWS02", "V2");

        Element resultat = helper.crearDadesEspecifiques(vdrsfws02, new HashMap<String, Object>(), false, true, null, true);

        assertNotNull(getFillElement(resultat, "Solicitud"));
        assertNull(getFillElement(resultat, "Domicilio"));
    }

    @Test
    public void crearDadesEspecifiquesAmbPathCampsInicialitzarForcaCampOpcional() throws Exception {
        Servicio vdrsfws02 = crearServicio("VDRSFWS02", "V2");
        List<String> pathCampsInicialitzar = new ArrayList<String>();
        pathCampsInicialitzar.add("DatosEspecificos/Domicilio");

        Element resultat = helper.crearDadesEspecifiques(vdrsfws02, new HashMap<String, Object>(), false, true, pathCampsInicialitzar, true);

        assertNotNull(getFillElement(resultat, "Solicitud"));
        assertNotNull(getFillElement(resultat, "Domicilio"));
    }

    // ------------------------- copiarDadesEspecifiquesRecobriment -------------------------

    @Test
    public void copiarDadesEspecifiquesRecobrimentAfegeixFillsDelNodeRebut() throws Exception {
        Document rebut = parseDoc("<DatosEspecificos xmlns=\"x\"><Ejercicio>2025</Ejercicio></DatosEspecificos>");

        Element resultat = helper.copiarDadesEspecifiquesRecobriment(AEATIAE, rebut.getDocumentElement(), false, false, null);

        assertEquals("2025", getFillText(resultat, "Ejercicio"));
    }

    // ------------------------- generatePeticioXml -------------------------

    @Test
    public void generatePeticioXmlGeneraXmlAmbAtributsDePeticio() throws Exception {
        Peticion peticio = new Peticion();
        Atributos atributos = new Atributos();
        atributos.setIdPeticion("PET1");
        atributos.setCodigoCertificado("AEATIAE");
        peticio.setAtributos(atributos);

        String xml = helper.generatePeticioXml(peticio);

        assertTrue(xml.startsWith("<?xml"));
        assertTrue(xml.contains("<Peticion"));
        assertTrue(xml.contains("IdPeticion>PET1<"));
        assertTrue(xml.contains("CodigoCertificado>AEATIAE<"));
    }

    // ------------------------- getDadesEspecifiquesXml -------------------------

    @Test
    public void getDadesEspecifiquesXmlAmbDatosEspecificosExtreuValors() throws Exception {
        String xmlPeticio = "<Respuesta xmlns=\"http://www.map.es/scsp/esquemas/V2/respuesta\">"
                + "<DatosEspecificos xmlns=\"http://www.map.es/scsp/esquemas/datosespecificos\">"
                + "<Solicitud><Tutor>SI</Tutor></Solicitud>"
                + "</DatosEspecificos></Respuesta>";

        Map<String, Object> dades = helper.getDadesEspecifiquesXml(xmlPeticio);

        assertEquals("SI", dades.get("/DatosEspecificos/Solicitud/Tutor"));
    }

    @Test
    public void getDadesEspecifiquesXmlAmbXmlNullRetornaMapaBuit() throws Exception {
        Map<String, Object> dades = helper.getDadesEspecifiquesXml(null);
        assertTrue(dades.isEmpty());
    }

    @Test
    public void getDadesEspecifiquesXmlSenseDatosEspecificosRetornaMapaBuit() throws Exception {
        Map<String, Object> dades = helper.getDadesEspecifiquesXml("<Root><Other>x</Other></Root>");
        assertTrue(dades.isEmpty());
    }

    // ------------------------- utilitats del test -------------------------

    private Document parseDoc(String xml) throws Exception {
        DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
        fac.setNamespaceAware(true);
        return fac.newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
    }

    private Element getFillElement(Element pare, String nomFill) {
        org.w3c.dom.NodeList fills = pare.getChildNodes();
        for (int i = 0; i < fills.getLength(); i++) {
            org.w3c.dom.Node fill = fills.item(i);
            if (fill instanceof Element && nomFill.equals(fill.getNodeName())) {
                return (Element) fill;
            }
        }
        return null;
    }

    private String getFillText(Element pare, String nomFill) {
        Element fill = getFillElement(pare, nomFill);
        return fill != null ? fill.getTextContent() : null;
    }
}
