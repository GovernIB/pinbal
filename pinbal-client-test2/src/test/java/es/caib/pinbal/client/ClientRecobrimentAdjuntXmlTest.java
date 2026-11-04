package es.caib.pinbal.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import es.caib.pinbal.client.comu.LogLevel;
import es.caib.pinbal.client.recobriment.ClientRecobriment;
import es.caib.pinbal.client.recobriment.v2.DadaEspecifica;
import es.caib.pinbal.client.recobriment.v2.DadaEspecificaBasic;
import es.caib.pinbal.client.recobriment.v2.DadaTipusEnum;
import es.caib.pinbal.client.recobriment.v2.DadesComunes;
import es.caib.pinbal.client.recobriment.v2.Funcionari;
import es.caib.pinbal.client.recobriment.v2.PeticioAsincrona;
import es.caib.pinbal.client.recobriment.v2.PeticioConfirmacioAsincrona;
import es.caib.pinbal.client.recobriment.v2.PeticioRespostaSincrona;
import es.caib.pinbal.client.recobriment.v2.PeticioSincrona;
import es.caib.pinbal.client.recobriment.v2.SolicitudSimple;
import es.caib.pinbal.client.recobriment.v2.Titular;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Tests d'integració de l'enviament de camps de tipus fitxer XML (ADJUNT_XML) amb el client de
 * recobriment v2. Els fitxers XML s'envien a les dades específiques codificats en base64, i PINBAL
 * els insereix com a nodes XML a la petició SCSP.
 * <p>
 * Requereixen un servei amb un camp de tipus fitxer XML, que s'ha d'indicar amb la propietat
 * {@code pinbal.adjuntXml.servei}. Si no s'indica, els tests s'ometen. Propietats:
 * <ul>
 *     <li>{@code pinbal.adjuntXml.servei}: codi del servei (obligatori).</li>
 *     <li>{@code pinbal.adjuntXml.camp}: codi (path) del camp XML. Per defecte, el primer camp del
 *     servei amb format {@value DadaEspecificaBasic#FORMAT_FILE_XML}.</li>
 *     <li>{@code pinbal.adjuntXml.xml}: contingut XML que s'envia al camp. Ha de complir l'esquema del
 *     servei. Per defecte, un element buit amb el nom del darrer element del path.</li>
 * </ul>
 * La resta de propietats (URL, usuari, entitat, procediment) són les de {@link ClientRecobrimentTest}.
 * Exemple contra el pinbal-scsp-fake, amb un camp ADJUNT_XML amb path DatosEspecificos/Consulta
 * al servei SVDDGPCIWS02:
 * <pre>
 * mvn test -Dtest=ClientRecobrimentAdjuntXmlTest -Dpinbal.url=http://localhost:8080/pinbalapi
 *     -Dpinbal.usuari=... -Dpinbal.contrasenya=... -Dpinbal.entitatCif=S2816015H
 *     -Dpinbal.adjuntXml.servei=SVDDGPCIWS02
 *     -Dpinbal.adjuntXml.xml="&lt;Consulta&gt;&lt;AnioNacimiento&gt;1980&lt;/AnioNacimiento&gt;&lt;/Consulta&gt;"
 * </pre>
 */
public class ClientRecobrimentAdjuntXmlTest {

    private static final String URL_BASE = System.getProperty("pinbal.url", "https://proves.caib.es/pinbalapi");
    private static final String USUARI = System.getProperty("pinbal.usuari", "$ripea_pinbal");
    private static final String CONTRASENYA = System.getProperty("pinbal.contrasenya", "ripea_pinbal");
    private static final String ENTITAT_CIF = System.getProperty("pinbal.entitatCif", "12345678Z");
    private static final String PROCEDIMENT_CODI = System.getProperty("pinbal.procediment", "TEST");
    private static final String SERVEI_CODI = System.getProperty("pinbal.adjuntXml.servei");
    private static final String CAMP_CODI = System.getProperty("pinbal.adjuntXml.camp");
    private static final String XML = System.getProperty("pinbal.adjuntXml.xml");

    private ClientRecobriment client;

    @BeforeEach
    public void setUp() {
        assumeTrue(SERVEI_CODI != null && !SERVEI_CODI.trim().isEmpty(),
                "No s'ha indicat cap servei amb camps ADJUNT_XML (propietat pinbal.adjuntXml.servei)");
        client = new ClientRecobriment(URL_BASE, USUARI, CONTRASENYA, LogLevel.DEBUG);
    }

    @Test
    public void getDadesEspecifiques_campsFitxerInformenFormat() throws Exception {
        List<DadaEspecifica> dades = client.getDadesEspecifiques(SERVEI_CODI);
        imprimir("getDadesEspecifiques", dades);

        assertNotNull(dades);
        boolean ambXml = false;
        for (DadaEspecifica dada : dades) {
            if (dada.getTipus() == DadaTipusEnum.FILE) {
                assertTrue(DadaEspecificaBasic.FORMAT_FILE_BINARI.equals(dada.getFormat())
                                || DadaEspecificaBasic.FORMAT_FILE_XML.equals(dada.getFormat()),
                        "Camp FILE sense format BINARI o XML: " + dada.getCodi() + " (" + dada.getFormat() + ")");
                ambXml |= DadaEspecificaBasic.FORMAT_FILE_XML.equals(dada.getFormat());
            }
        }
        assertTrue(ambXml, "El servei " + SERVEI_CODI + " no té cap camp FILE amb format XML");
    }

    @Test
    public void peticioSincrona_xmlEnBase64() throws Exception {
        String campCodi = getCampXml();
        PeticioSincrona peticio = novaPeticioSincrona(campCodi, base64(getXml(campCodi)));

        PeticioRespostaSincrona resposta = client.peticioSincrona(SERVEI_CODI, peticio);
        imprimir("peticioSincrona", resposta);

        assertNotNull(resposta);
        assertFalse(resposta.isError(), "Error en la petició: " + resposta.getMissatge() + " " + resposta.getErrorsValidacio());
        assertNotNull(resposta.getDadesComunes().getIdPeticio());
    }

    @Test
    public void peticioSincrona_xmlNoValid_retornaErrorValidacio() throws Exception {
        String campCodi = getCampXml();
        PeticioSincrona peticio = novaPeticioSincrona(campCodi, base64("<Arrel><NoTancat></Arrel>"));

        PeticioRespostaSincrona resposta = client.peticioSincrona(SERVEI_CODI, peticio);
        imprimir("peticioSincrona", resposta);

        assertTrue(resposta.isError(), "S'esperava un error de validació");
        assertNotNull(resposta.getErrorsValidacio());
        assertTrue(resposta.getErrorsValidacio().containsKey("solicitud.dadesEspecifiques[" + campCodi + "]"),
                "No hi ha error de validació per al camp " + campCodi + ": " + resposta.getErrorsValidacio());
    }

    @Test
    public void peticioAsincrona_xmlNoValid_retornaErrorValidacio() throws Exception {
        String campCodi = getCampXml();
        PeticioSincrona correcta = novaPeticioSincrona(campCodi, base64(getXml(campCodi)));
        PeticioSincrona incorrecta = novaPeticioSincrona(campCodi, base64("no és XML"));
        PeticioAsincrona peticio = PeticioAsincrona.builder()
                .dadesComunes(correcta.getDadesComunes())
                .solicituds(List.of(correcta.getSolicitud(), incorrecta.getSolicitud()))
                .build();

        PeticioConfirmacioAsincrona confirmacio = client.peticioAsincrona(SERVEI_CODI, peticio);
        imprimir("peticioAsincrona", confirmacio);

        assertTrue(confirmacio.isError(), "S'esperava un error de validació");
        assertNotNull(confirmacio.getErrorsValidacio());
        // Només falla la segona sol·licitud, i l'error s'indexa pel seu número
        assertEquals(1, confirmacio.getErrorsValidacio().size(), "Errors inesperats: " + confirmacio.getErrorsValidacio());
        assertTrue(confirmacio.getErrorsValidacio().containsKey("000001:dadesEspecifiques[" + campCodi + "]"),
                "No hi ha error de validació per al camp " + campCodi + ": " + confirmacio.getErrorsValidacio());
    }

    // Utilitats
    // /////////////////////////////////////////////////////////////

    private String getCampXml() throws Exception {
        if (CAMP_CODI != null && !CAMP_CODI.trim().isEmpty()) {
            return CAMP_CODI;
        }
        Optional<String> camp = client.getDadesEspecifiques(SERVEI_CODI).stream()
                .filter(d -> d.getTipus() == DadaTipusEnum.FILE && DadaEspecificaBasic.FORMAT_FILE_XML.equals(d.getFormat()))
                .map(DadaEspecifica::getCodi)
                .findFirst();
        return camp.orElseThrow(() -> new AssertionError("El servei " + SERVEI_CODI + " no té cap camp FILE amb format XML"));
    }

    private static String getXml(String campCodi) {
        if (XML != null && !XML.trim().isEmpty()) {
            return XML;
        }
        String element = campCodi.substring(campCodi.lastIndexOf('/') + 1);
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><" + element + "/>";
    }

    private static String base64(String xml) {
        return Base64.getEncoder().encodeToString(xml.getBytes(StandardCharsets.UTF_8));
    }

    private static PeticioSincrona novaPeticioSincrona(String campCodi, String valor) {
        Map<String, String> dadesEspecifiques = new HashMap<>();
        dadesEspecifiques.put(campCodi, valor);

        return PeticioSincrona.builder()
                .dadesComunes(DadesComunes.builder()
                        .entitatCif(ENTITAT_CIF)
                        .procedimentCodi(PROCEDIMENT_CODI)
                        .serveiCodi(SERVEI_CODI)
                        .funcionari(Funcionari.builder()
                                .nif("44444444A")
                                .nom("FUNCIONARI_NOM")
                                .build())
                        .departament("Departament de test")
                        .consentiment(DadesComunes.Consentiment.Si)
                        .finalitat("Test pinbal-client ADJUNT_XML " + SERVEI_CODI)
                        .build())
                .solicitud(SolicitudSimple.builder()
                        .titular(Titular.builder()
                                .documentTipus(Titular.DocumentTipus.DNI)
                                .documentNumero("43105084W")
                                .nom("Usuari")
                                .llinatge1("Test")
                                .build())
                        .expedient("testPinbal/adjuntXml")
                        .dadesEspecifiques(dadesEspecifiques)
                        .build())
                .build();
    }

    private static void imprimir(String operacio, Object resultat) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        System.out.println("-> " + operacio + " = " + mapper.writeValueAsString(resultat));
    }

}
