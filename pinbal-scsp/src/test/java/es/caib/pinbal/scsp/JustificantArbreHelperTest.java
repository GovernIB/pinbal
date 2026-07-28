package es.caib.pinbal.scsp;

import es.scsp.bean.common.respuesta.Consentimiento;
import es.scsp.bean.common.respuesta.DatosGenericos;
import es.scsp.bean.common.respuesta.Emisor;
import es.scsp.bean.common.respuesta.Funcionario;
import es.scsp.bean.common.respuesta.Procedimiento;
import es.scsp.bean.common.respuesta.Solicitante;
import es.scsp.bean.common.respuesta.Titular;
import es.scsp.bean.common.respuesta.TipoDocumentacion;
import es.scsp.bean.common.respuesta.Transmision;
import es.scsp.bean.common.respuesta.TransmisionDatos;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JustificantArbreHelperTest {

    private final JustificantArbreHelper helper = new JustificantArbreHelper();

    @SuppressWarnings("unchecked")
    private <T> T invoke(String methodName, Class<?>[] paramTypes, Object... args) throws Exception {
        Method method = JustificantArbreHelper.class.getDeclaredMethod(methodName, paramTypes);
        method.setAccessible(true);
        return (T) method.invoke(helper, args);
    }

    private Node parseElement(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document doc = factory.newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
        return doc.getDocumentElement();
    }

    // ------------------------- getUnidadTramitadora -------------------------

    @Test
    public void getUnidadTramitadoraAmbUnidadInformadaLaRetorna() throws Exception {
        String resultat = invoke("getUnidadTramitadora", new Class<?>[]{String.class, String.class}, "Nom-Solicitant", "UnitatDirecta");
        assertEquals("UnitatDirecta", resultat);
    }

    @Test
    public void getUnidadTramitadoraSenseUnidadLExtreuDelNom() throws Exception {
        String resultat = invoke("getUnidadTramitadora", new Class<?>[]{String.class, String.class}, "Nom-Solicitant-UNITAT1", null);
        assertEquals("UNITAT1", resultat);
    }

    @Test
    public void getUnidadTramitadoraAmbNomNullRetornaEspai() throws Exception {
        String resultat = invoke("getUnidadTramitadora", new Class<?>[]{String.class, String.class}, (Object) null, null);
        assertEquals(" ", resultat);
    }

    // ------------------------- getExpediente -------------------------

    @Test
    public void getExpedienteAmbIdInformatElRetorna() throws Exception {
        String resultat = invoke("getExpediente", new Class<?>[]{String.class, String.class}, "finalitat", "EXP1");
        assertEquals("EXP1", resultat);
    }

    @Test
    public void getExpedienteExtreuDeLaFinalitatAmbSeparador() throws Exception {
        String resultat = invoke("getExpediente", new Class<?>[]{String.class, String.class}, "PROC#::#EXP2#::#Finalitat", null);
        assertEquals("EXP2", resultat);
    }

    @Test
    public void getExpedienteSenseSeparadorRetornaLaFinalitatOriginal() throws Exception {
        String resultat = invoke("getExpediente", new Class<?>[]{String.class, String.class}, "finalitat-sense-separador", null);
        assertEquals("finalitat-sense-separador", resultat);
    }

    // ------------------------- getFinalidad -------------------------

    @Test
    public void getFinalidadAmbSeparadorExtreuElDarrerTram() throws Exception {
        String resultat = invoke("getFinalidad", new Class<?>[]{String.class}, "PROC#::#EXP#::#Finalitat real");
        assertEquals("Finalitat real", resultat);
    }

    @Test
    public void getFinalidadSenseSeparadorRetornaElValorOriginal() throws Exception {
        String resultat = invoke("getFinalidad", new Class<?>[]{String.class}, "finalitat-simple");
        assertEquals("finalitat-simple", resultat);
    }

    // ------------------------- getProcedimiento -------------------------

    @Test
    public void getProcedimientoAmbSeparadorExtreuElPrimerTram() throws Exception {
        String resultat = invoke("getProcedimiento", new Class<?>[]{String.class, Procedimiento.class}, "PROC1#::#EXP#::#Finalitat", null);
        assertEquals("PROC1", resultat);
    }

    @Test
    public void getProcedimientoSenseSeparadorIProcedimientoNullRetornaBuit() throws Exception {
        String resultat = invoke("getProcedimiento", new Class<?>[]{String.class, Procedimiento.class}, "finalitat-simple", null);
        assertEquals("", resultat);
    }

    @Test
    public void getProcedimientoSenseSeparadorAmbProcedimientoRetornaElNom() throws Exception {
        Procedimiento procedimiento = new Procedimiento();
        procedimiento.setNombreProcedimiento("Nom del procediment");

        String resultat = invoke("getProcedimiento", new Class<?>[]{String.class, Procedimiento.class}, "finalitat-simple", procedimiento);

        assertEquals("Nom del procediment", resultat);
    }

    // ------------------------- teVarisNivells / esDarrerNivellVisible / esNodeFillText -------------------------

    @Test
    public void teVarisNivellsAmbTresNivellsRetornaCert() throws Exception {
        Node node = parseElement("<arrel><nivell1><nivell2><nivell3>text</nivell3></nivell2></nivell1></arrel>");
        assertTrue((Boolean) invoke("teVarisNivells", new Class<?>[]{Node.class}, node));
    }

    @Test
    public void teVarisNivellsSenseFillsRetornaFals() throws Exception {
        Node node = parseElement("<arrel/>");
        assertFalse((Boolean) invoke("teVarisNivells", new Class<?>[]{Node.class}, node));
    }

    @Test
    public void teVarisNivellsAmbNomesDosNivellsRetornaFals() throws Exception {
        Node node = parseElement("<arrel><nivell1>text</nivell1></arrel>");
        assertFalse((Boolean) invoke("teVarisNivells", new Class<?>[]{Node.class}, node));
    }

    @Test
    public void esDarrerNivellVisibleAmbTextRetornaCert() throws Exception {
        Node arrel = parseElement("<arrel>text</arrel>");
        Node textNode = arrel.getFirstChild();
        assertTrue((Boolean) invoke("esDarrerNivellVisible", new Class<?>[]{Node.class}, textNode));
    }

    @Test
    public void esDarrerNivellVisibleAmbUnFillAmbTextNoBuitRetornaCert() throws Exception {
        Node node = parseElement("<arrel>contingut</arrel>");
        assertTrue((Boolean) invoke("esDarrerNivellVisible", new Class<?>[]{Node.class}, node));
    }

    @Test
    public void esDarrerNivellVisibleAmbUnFillAmbTextBuitRetornaFals() throws Exception {
        Node node = parseElement("<arrel>   </arrel>");
        assertFalse((Boolean) invoke("esDarrerNivellVisible", new Class<?>[]{Node.class}, node));
    }

    @Test
    public void esDarrerNivellVisibleAmbDiversosFillsRetornaCert() throws Exception {
        Node node = parseElement("<arrel><a/><b/></arrel>");
        assertTrue((Boolean) invoke("esDarrerNivellVisible", new Class<?>[]{Node.class}, node));
    }

    @Test
    public void esDarrerNivellVisibleSenseFillsRetornaFals() throws Exception {
        Node node = parseElement("<arrel/>");
        assertFalse((Boolean) invoke("esDarrerNivellVisible", new Class<?>[]{Node.class}, node));
    }

    @Test
    public void esNodeFillTextAmbUnFillDeTextRetornaCert() throws Exception {
        Node node = parseElement("<arrel>text</arrel>");
        assertTrue((Boolean) invoke("esNodeFillText", new Class<?>[]{Node.class}, node));
    }

    @Test
    public void esNodeFillTextSenseFillsRetornaFals() throws Exception {
        Node node = parseElement("<arrel/>");
        assertFalse((Boolean) invoke("esNodeFillText", new Class<?>[]{Node.class}, node));
    }

    @Test
    public void esNodeFillTextAmbDiversosFillsRetornaFals() throws Exception {
        Node node = parseElement("<arrel><a/><b/></arrel>");
        assertFalse((Boolean) invoke("esNodeFillText", new Class<?>[]{Node.class}, node));
    }

    // ------------------------- getNodeNameDadesEspecifiquesTraduit -------------------------

    @Test
    public void getNodeNameDadesEspecifiquesTraduitCobreixLesClausEspecials() throws Exception {
        MessageSource messageSource = mock(MessageSource.class);
        when(messageSource.getMessage(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(Locale.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        helper.setMessageSource(messageSource);

        assertEquals("dades.especifiques.estat.codi",
                invoke("getNodeNameDadesEspecifiquesTraduit", new Class<?>[]{String.class, String.class, Locale.class}, "Estado.CodigoEstado", "node", Locale.forLanguageTag("ca")));
        assertEquals("dades.especifiques.estat.literal",
                invoke("getNodeNameDadesEspecifiquesTraduit", new Class<?>[]{String.class, String.class, Locale.class}, "Estado.LiteralError", "node", Locale.forLanguageTag("ca")));
        assertEquals("dades.especifiques.estat",
                invoke("getNodeNameDadesEspecifiquesTraduit", new Class<?>[]{String.class, String.class, Locale.class}, "Estado", "node", Locale.forLanguageTag("ca")));
        assertEquals("dades.especifiques.altre.camp",
                invoke("getNodeNameDadesEspecifiquesTraduit", new Class<?>[]{String.class, String.class, Locale.class}, "altre.camp", "node", Locale.forLanguageTag("ca")));
    }

    // ------------------------- ElementArbre -------------------------

    @Test
    public void elementArbreConstructorAmbNomesTitol() {
        JustificantArbreHelper.ElementArbre element = new JustificantArbreHelper.ElementArbre("Titol");
        assertEquals("Titol", element.getTitol());
        assertFalse(element.teFills());
    }

    @Test
    public void elementArbreConstructorAmbTitolIDescripcio() {
        JustificantArbreHelper.ElementArbre element = new JustificantArbreHelper.ElementArbre("Titol", "Descripcio");
        assertEquals("Titol", element.getTitol());
        assertEquals("Descripcio", element.getDescripcio());
    }

    @Test
    public void elementArbreConstructorComplet() {
        JustificantArbreHelper.ElementArbre element = new JustificantArbreHelper.ElementArbre("Titol", "Descripcio", "/xpath");
        assertEquals("/xpath", element.getXpathDatoEspecifico());
    }

    @Test
    public void elementArbreAddFillIGetFills() {
        JustificantArbreHelper.ElementArbre pare = new JustificantArbreHelper.ElementArbre("Pare");
        JustificantArbreHelper.ElementArbre fill = new JustificantArbreHelper.ElementArbre("Fill");

        pare.addFill(fill);

        assertTrue(pare.teFills());
        assertEquals(List.of(fill), pare.getFills());
    }

    @Test
    public void elementArbreSetFills() {
        JustificantArbreHelper.ElementArbre pare = new JustificantArbreHelper.ElementArbre("Pare");
        pare.setFills(List.of(new JustificantArbreHelper.ElementArbre("Fill")));
        assertTrue(pare.teFills());
    }

    // ------------------------- printStdout / imprimirJustificantStdout -------------------------

    @Test
    public void imprimirJustificantStdoutNoLlancaExcepcio() {
        JustificantArbreHelper.ElementArbre arrel = new JustificantArbreHelper.ElementArbre("Arrel", "Descripcio arrel");
        JustificantArbreHelper.ElementArbre fill = new JustificantArbreHelper.ElementArbre("Fill");
        arrel.addFill(fill);

        helper.imprimirJustificantStdout(arrel);
        // No hi ha assercions addicionals: l'objectiu és exercitar printStdout recursivament
        // (incloent-hi la branca amb i sense descripció) sense que llanci cap excepció.
    }

    // ------------------------- generarArbre -------------------------

    private JustificantArbreHelper helperAmbMessageSourceDelValorPerDefecte() {
        JustificantArbreHelper h = new JustificantArbreHelper();
        MessageSource ms = mock(MessageSource.class);
        when(ms.getMessage(anyString(), any(), anyString(), any(Locale.class)))
                .thenAnswer(invocation -> invocation.getArgument(2));
        h.setMessageSource(ms);
        return h;
    }

    @Test
    public void generarArbreAmbDatosGenericosCompletsIDatosEspecificsUnNivell() throws Exception {
        JustificantArbreHelper h = helperAmbMessageSourceDelValorPerDefecte();

        TransmisionDatos transmisio = new TransmisionDatos();
        DatosGenericos dg = new DatosGenericos();

        Transmision t = new Transmision();
        t.setCodigoCertificado("AEATIAE");
        t.setIdSolicitud("SOL1");
        t.setIdTransmision("TR1");
        t.setFechaGeneracion("2024-01-01");
        dg.setTransmision(t);

        Emisor emisor = new Emisor();
        emisor.setNifEmisor("S000");
        emisor.setNombreEmisor("Emissor Test");
        dg.setEmisor(emisor);

        Solicitante sol = new Solicitante();
        sol.setIdentificadorSolicitante("B123");
        sol.setNombreSolicitante("Nom Solicitant-UNITAT1");
        sol.setFinalidad("PROC1#::#EXP1#::#Final1");
        sol.setConsentimiento(Consentimiento.SI);
        Funcionario func = new Funcionario();
        func.setNombreCompletoFuncionario("Func Test");
        func.setNifFuncionario("12345678A");
        sol.setFuncionario(func);
        dg.setSolicitante(sol);

        Titular tit = new Titular();
        tit.setTipoDocumentacion(TipoDocumentacion.NIF);
        tit.setDocumentacion("12345678Z");
        tit.setNombre("Joan");
        tit.setApellido1("Garcia");
        tit.setApellido2("Lopez");
        dg.setTitular(tit);

        transmisio.setDatosGenericos(dg);
        transmisio.setDatosEspecificos(parseElement("<DatosEspecificos><Ejercicio>2024</Ejercicio></DatosEspecificos>"));

        JustificantArbreHelper.ElementArbre arrel = h.generarArbre(transmisio, "PET1", Locale.forLanguageTag("ca"));

        assertTrue(arrel.teFills());
        JustificantArbreHelper.ElementArbre solicitud = arrel.getFills().get(0);
        assertEquals("Solicitud", solicitud.getTitol());
        assertEquals("PET1", buscarDescripcio(solicitud, "Identificador de la petición"));
        assertEquals("SOL1", buscarDescripcio(solicitud, "Identificador de la solicitud"));
        assertEquals("TR1", buscarDescripcio(solicitud, "Identificador de la transmisión"));
        assertEquals("S000 - Emissor Test", buscarDescripcio(solicitud, "Emisor"));
        assertEquals("B123 - Nom Solicitant-UNITAT1", buscarDescripcio(solicitud, "Solicitante"));
        assertEquals("UNITAT1", buscarDescripcio(solicitud, "Unidad tramitadora"));
        assertEquals("PROC1", buscarDescripcio(solicitud, "Procedimiento"));
        assertEquals("Final1", buscarDescripcio(solicitud, "Finalidad"));
        assertEquals("EXP1", buscarDescripcio(solicitud, "Expediente"));

        JustificantArbreHelper.ElementArbre datosPersonales = arrel.getFills().get(1);
        assertEquals("Datos personales", datosPersonales.getTitol());
        assertEquals("12345678Z", buscarDescripcio(datosPersonales, "NIF"));
        assertEquals("Joan", buscarDescripcio(datosPersonales, "Nombre"));
        assertEquals("Garcia", buscarDescripcio(datosPersonales, "Primer apellido"));
        assertEquals("Lopez", buscarDescripcio(datosPersonales, "Segundo apellido"));

        // Amb un únic nivell de dades específiques, es fa un embolcall "Respuesta" i es recorre a partir d'ell.
        JustificantArbreHelper.ElementArbre datosEspecificos = arrel.getFills().get(2);
        assertEquals("2024", buscarDescripcio(datosEspecificos, "Ejercicio"));
    }

    @Test
    public void generarArbreAmbDatosEspecificsMultiNivellNoEmbolcallaAmbRespuesta() throws Exception {
        JustificantArbreHelper h = helperAmbMessageSourceDelValorPerDefecte();

        TransmisionDatos transmisio = new TransmisionDatos();
        DatosGenericos dg = new DatosGenericos();
        Transmision t = new Transmision();
        t.setCodigoCertificado("AEATIAE");
        dg.setTransmision(t);
        transmisio.setDatosGenericos(dg);
        transmisio.setDatosEspecificos(
                parseElement("<DatosEspecificos><Solicitud><Provincia>07</Provincia></Solicitud></DatosEspecificos>"));

        JustificantArbreHelper.ElementArbre arrel = h.generarArbre(transmisio, "PET2", Locale.forLanguageTag("ca"));

        JustificantArbreHelper.ElementArbre solicitudGenerica = arrel.getFills().get(0);
        assertEquals("Solicitud", solicitudGenerica.getTitol());

        JustificantArbreHelper.ElementArbre solicitudDadesEspecifiques = arrel.getFills().get(1);
        assertEquals("Solicitud", solicitudDadesEspecifiques.getTitol());
        assertEquals("07", buscarDescripcio(solicitudDadesEspecifiques, "Provincia"));
    }

    @Test
    public void generarArbreSenseDatosGenericosNiDatosEspecificosNoAfegeixCapFill() throws Exception {
        // Nota: el fill "Solicitud" (amb l'identificador de petició) només s'afegeix a l'arrel
        // dins la branca "if (transmision.getDatosGenericos() != null)", per tant sense
        // DatosGenericos l'arrel queda sense fills tot i haver-se construït l'element intern.
        JustificantArbreHelper h = helperAmbMessageSourceDelValorPerDefecte();

        TransmisionDatos transmisio = new TransmisionDatos();

        JustificantArbreHelper.ElementArbre arrel = h.generarArbre(transmisio, "PET3", Locale.forLanguageTag("ca"));

        assertFalse(arrel.teFills());
    }

    private String buscarDescripcio(JustificantArbreHelper.ElementArbre pare, String titol) {
        if (!pare.teFills()) {
            return null;
        }
        for (JustificantArbreHelper.ElementArbre fill : pare.getFills()) {
            if (titol.equals(fill.getTitol())) {
                return fill.getDescripcio();
            }
        }
        return null;
    }
}
