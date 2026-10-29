package es.caib.pinbal.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import es.caib.pinbal.client.comu.LogLevel;
import es.caib.pinbal.client.procediments.ProcedimentBasic;
import es.caib.pinbal.client.recobriment.ClientRecobriment;
import es.caib.pinbal.client.recobriment.model.ScspJustificante;
import es.caib.pinbal.client.recobriment.v2.DadaEspecifica;
import es.caib.pinbal.client.recobriment.v2.DadaEspecificaBasic;
import es.caib.pinbal.client.recobriment.v2.DadaTipusEnum;
import es.caib.pinbal.client.recobriment.v2.DadesComunes;
import es.caib.pinbal.client.recobriment.v2.Entitat;
import es.caib.pinbal.client.recobriment.v2.Funcionari;
import es.caib.pinbal.client.recobriment.v2.GrupDadesEspecifiques;
import es.caib.pinbal.client.recobriment.v2.PeticioAsincrona;
import es.caib.pinbal.client.recobriment.v2.PeticioConfirmacioAsincrona;
import es.caib.pinbal.client.recobriment.v2.PeticioRespostaAsincrona;
import es.caib.pinbal.client.recobriment.v2.PeticioRespostaSincrona;
import es.caib.pinbal.client.recobriment.v2.PeticioSincrona;
import es.caib.pinbal.client.recobriment.v2.Regla;
import es.caib.pinbal.client.recobriment.v2.SolicitudSimple;
import es.caib.pinbal.client.recobriment.v2.Titular;
import es.caib.pinbal.client.recobriment.v2.ValorEnum;
import es.caib.pinbal.client.serveis.ServeiBasic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests d'integració de la darrera versió del client de recobriment v2 contra
 * una instància de PINBAL en marxa.
 * <p>
 * La configuració es pot sobreescriure amb propietats de sistema, per exemple:
 * <pre>
 * mvn test -Dpinbal.url=http://localhost:8180/pinbalapi -Dpinbal.usuari=admin -Dpinbal.contrasenya=admin
 * </pre>
 */
public class ClientRecobrimentTest {

    private static final String URL_BASE = System.getProperty("pinbal.url", "https://proves.caib.es/pinbalapi");
    private static final String USUARI = System.getProperty("pinbal.usuari", "$ripea_pinbal");
    private static final String CONTRASENYA = System.getProperty("pinbal.contrasenya", "ripea_pinbal");
    private static final String ENTITAT_CIF = System.getProperty("pinbal.entitatCif", "12345678Z");
    private static final String PROCEDIMENT_CODI = System.getProperty("pinbal.procediment", "TEST");
    private static final String SERVEI_CODI = System.getProperty("pinbal.servei", "SCDCPAJU");
    private static final String PROVINCIA_CODI = "07";

    private ClientRecobriment client;

    @BeforeEach
    public void setUp() {
        client = new ClientRecobriment(URL_BASE, USUARI, CONTRASENYA, LogLevel.DEBUG);
    }

    // Consulta d'entitats, procediments i serveis
    // /////////////////////////////////////////////////////////////

    @Test
    public void getEntitats() throws Exception {
        List<Entitat> entitats = client.getEntitats();

        assertNotNull(entitats);
        assertFalse(entitats.isEmpty(), "L'usuari no té permís a cap entitat");
        entitats.forEach(e -> assertNotNull(e.getCodi()));
        imprimir("getEntitats", entitats);
    }

    @Test
    public void getProcediments() throws Exception {
        List<ProcedimentBasic> procediments = client.getProcediments(getEntitatCodi());

        assertNotNull(procediments);
        assertTrue(procediments.stream().anyMatch(p -> PROCEDIMENT_CODI.equals(p.getCodi())),
                "No s'ha trobat el procediment " + PROCEDIMENT_CODI);
        imprimir("getProcediments", procediments);
    }

    @Test
    public void getServeis() throws Exception {
        List<ServeiBasic> serveis = client.getServeis();

        assertNotNull(serveis);
        assertTrue(conteServei(serveis), "No s'ha trobat el servei " + SERVEI_CODI);
    }

    @Test
    public void getServeisPerEntitat() throws Exception {
        List<ServeiBasic> serveis = client.getServeisPerEntitat(getEntitatCodi());

        assertNotNull(serveis);
        assertTrue(conteServei(serveis), "No s'ha trobat el servei " + SERVEI_CODI + " a l'entitat");
    }

    @Test
    public void getServeisPerEntitat_ambPermisos_informaProcedimentsCodiSia() throws Exception {
        List<ServeiBasic> serveis = client.getServeisPerEntitat(getEntitatCodi(), true);

        assertNotNull(serveis);
        for (ServeiBasic servei : serveis) {
            assertNotNull(servei.getPermis(), "Permís no informat al servei " + servei.getCodi());
            assertNotNull(servei.getProcedimentsCodiSia(), "Codis SIA no informats al servei " + servei.getCodi());
            if (!servei.getPermis()) {
                assertTrue(servei.getProcedimentsCodiSia().isEmpty(),
                        "El servei " + servei.getCodi() + " no és permès però té codis SIA");
            }
            for (String codiSia : servei.getProcedimentsCodiSia()) {
                assertFalse(codiSia == null || codiSia.trim().isEmpty(), "Codi SIA buit al servei " + servei.getCodi());
            }
        }
    }

    @Test
    public void getServeisPerEntitat_senseAmbPermisos_noInformaProcedimentsCodiSia() throws Exception {
        List<ServeiBasic> serveis = client.getServeisPerEntitat(getEntitatCodi());

        assertNotNull(serveis);
        for (ServeiBasic servei : serveis) {
            assertNull(servei.getProcedimentsCodiSia());
        }
    }

    @Test
    public void getServeisPerProcediment() throws Exception {
        List<ServeiBasic> serveis = client.getServeisPerProcediment(getEntitatCodi(), PROCEDIMENT_CODI);

        assertNotNull(serveis);
        assertTrue(conteServei(serveis), "No s'ha trobat el servei " + SERVEI_CODI + " al procediment");
    }

    // Formulari de dades específiques
    // /////////////////////////////////////////////////////////////

    @Test
    public void getDadesEspecifiques() throws Exception {
        List<DadaEspecifica> dades = client.getDadesEspecifiques(SERVEI_CODI);

        assertNotNull(dades);
        assertFalse(dades.isEmpty());
        for (DadaEspecifica dada : dades) {
            assertNotNull(dada.getCodi());
            assertNotNull(dada.getTipus(), "Camp sense tipus: " + dada.getCodi());
            if (dada.getEnumTipus() != null) {
                assertEquals(DadaTipusEnum.ENUM, dada.getTipus(), "Camp amb enumTipus que no és de tipus ENUM: " + dada.getCodi());
            }
        }
        imprimir("getDadesEspecifiques", dades);
    }

    @Test
    public void getDadesEspecifiques_serveiInexistent() {
        RuntimeException ex = assertThrows(RuntimeException.class, () -> client.getDadesEspecifiques("SERVEI_INEXISTENT"));
        assertTrue(ex.getMessage().startsWith("Recurs no trobat"), "S'esperava un error 404: " + ex.getMessage());
    }

    @Test
    public void getGrups() throws Exception {
        List<GrupDadesEspecifiques> grups = client.getGrups(SERVEI_CODI);
        imprimir("getGrups", grups);
        if (grups == null) {
            return; // El servei no té grups (HTTP 204)
        }

        comprovarGrups(grups, null);

        // Tots els camps agrupats han de fer referència a un grup existent
        Set<String> codisGrups = codisGrups(grups);
        for (DadaEspecifica dada : client.getDadesEspecifiques(SERVEI_CODI)) {
            if (dada.getGrup() != null) {
                assertTrue(codisGrups.contains(dada.getGrup()), "El camp " + dada.getCodi() + " referencia un grup inexistent: " + dada.getGrup());
            }
        }
    }

    @Test
    public void getRegles() throws Exception {
        List<Regla> regles = client.getRegles(SERVEI_CODI);
        imprimir("getRegles", regles);
        if (regles == null) {
            return; // El servei no té regles (HTTP 204)
        }

        Set<String> codisCamps = client.getDadesEspecifiques(SERVEI_CODI).stream()
                .map(DadaEspecifica::getCodi)
                .collect(Collectors.toSet());
        List<GrupDadesEspecifiques> grups = client.getGrups(SERVEI_CODI);
        Set<String> codisGrups = grups != null ? codisGrups(grups) : Collections.emptySet();

        int ordreAnterior = Integer.MIN_VALUE;
        for (Regla regla : regles) {
            assertTrue(regla.getOrdre() >= ordreAnterior, "Les regles no estan ordenades: " + regla.getNom());
            ordreAnterior = regla.getOrdre();
            assertNotNull(regla.getModificat(), "Regla sense condició: " + regla.getNom());
            assertNotNull(regla.getAccio(), "Regla sense acció: " + regla.getNom());

            boolean reglaCamps = regla.getModificat() == Regla.ReglaModificat.CAMPS
                    || regla.getModificat() == Regla.ReglaModificat.ALGUN_CAMP;
            Set<String> codis = reglaCamps ? codisCamps : codisGrups;
            List<String> valors = new ArrayList<>(regla.getModificatValors());
            valors.addAll(regla.getAfectatValors());
            for (String valor : valors) {
                assertTrue(codis.contains(valor), "La regla " + regla.getNom() + " referencia un element inexistent: " + valor);
            }
        }
    }

    @Test
    public void getValorsEnum() throws Exception {
        List<DadaEspecifica> dadesEnum = client.getDadesEspecifiques(SERVEI_CODI).stream()
                .filter(d -> d.getEnumTipus() != null)
                .collect(Collectors.toList());
        assertFalse(dadesEnum.isEmpty(), "El servei " + SERVEI_CODI + " no té camps de tipus llista");

        for (DadaEspecifica dada : dadesEnum) {
            DadaEspecifica.EnumTipus enumTipus = dada.getEnumTipus();
            // Els municipis requereixen el codi de província com a filtre
            String filtre = enumTipus == DadaEspecifica.EnumTipus.MUNICIPI_3 || enumTipus == DadaEspecifica.EnumTipus.MUNICIPI_5
                    ? PROVINCIA_CODI
                    : null;

            List<ValorEnum> valors = client.getValorsEnum(SERVEI_CODI, dada.getCodi(), enumTipus.name(), filtre);

            assertNotNull(valors, "Sense valors per al camp " + dada.getCodi());
            assertFalse(valors.isEmpty(), "Sense valors per al camp " + dada.getCodi());
            valors.forEach(v -> assertNotNull(v.getCodi()));
            imprimir("getValorsEnum(" + dada.getCodi() + ", " + enumTipus + ")", valors);
        }
    }

    @Test
    public void getDadesEspecifiquesResposta() throws Exception {
        List<DadaEspecificaBasic> dades = client.getDadesEspecifiquesResposta(SERVEI_CODI);
        imprimir("getDadesEspecifiquesResposta", dades);
        if (dades != null) {
            dades.forEach(d -> assertNotNull(d.getCodi()));
        }
    }

    // Realització de consultes
    // /////////////////////////////////////////////////////////////

    @Test
    public void peticioSincrona() throws Exception {
        PeticioRespostaSincrona resposta = client.peticioSincrona(SERVEI_CODI, novaPeticioSincrona());
        imprimir("peticioSincrona", resposta);

        assertNotNull(resposta);
        assertFalse(resposta.isError(), "Error en la petició: " + resposta.getMissatge() + " " + resposta.getErrorsValidacio());
        assertNotNull(resposta.getDadesComunes());
        assertNotNull(resposta.getDadesComunes().getIdPeticio());
        assertNotNull(resposta.getResposta());
    }

    @Test
    public void peticioSincrona_obtenirResposta() throws Exception {
        PeticioRespostaSincrona sincrona = client.peticioSincrona(SERVEI_CODI, novaPeticioSincrona());
        assertFalse(sincrona.isError(), "Error en la petició: " + sincrona.getMissatge());
        String idPeticio = sincrona.getDadesComunes().getIdPeticio();

        PeticioRespostaAsincrona resposta = client.getResposta(idPeticio);
        imprimir("getResposta(" + idPeticio + ")", resposta);

        assertNotNull(resposta);
        assertFalse(resposta.isError(), "Error obtenint la resposta: " + resposta.getMissatge());
        assertEquals(idPeticio, resposta.getDadesComunes().getIdPeticio());
        assertNotNull(resposta.getRespostes());
        assertEquals(1, resposta.getRespostes().size());
    }

    @Test
    public void peticioSincrona_obtenirJustificant() throws Exception {
        PeticioRespostaSincrona sincrona = client.peticioSincrona(SERVEI_CODI, novaPeticioSincrona());
        assertFalse(sincrona.isError(), "Error en la petició: " + sincrona.getMissatge());
        String idPeticio = sincrona.getDadesComunes().getIdPeticio();
        String idSolicitud = sincrona.getResposta().getIdSolicitud();

        ScspJustificante justificant = client.getJustificant(idPeticio, idSolicitud);

        assertNotNull(justificant);
        assertNotNull(justificant.getContingut());
        assertTrue(new String(justificant.getContingut(), 0, 5).startsWith("%PDF"), "El justificant no és un PDF");
        System.out.println("-> getJustificant: " + justificant.getNom() + " (" + justificant.getContentType() + ", "
                + justificant.getContingut().length + " bytes)");
    }

    @Test
    public void peticioAsincrona() throws Exception {
        PeticioSincrona sincrona = novaPeticioSincrona();
        PeticioAsincrona peticio = PeticioAsincrona.builder()
                .dadesComunes(sincrona.getDadesComunes())
                .solicituds(List.of(sincrona.getSolicitud(), sincrona.getSolicitud()))
                .build();

        PeticioConfirmacioAsincrona confirmacio = client.peticioAsincrona(SERVEI_CODI, peticio);
        imprimir("peticioAsincrona", confirmacio);

        assertNotNull(confirmacio);
        assertFalse(confirmacio.isError(), "Error en la petició: " + confirmacio.getMissatge() + " " + confirmacio.getErrorsValidacio());
        assertNotNull(confirmacio.getConfirmacioPeticio());
        assertEquals(2, confirmacio.getConfirmacioPeticio().getNumSolicituds());

        String idPeticio = confirmacio.getConfirmacioPeticio().getIdPeticio();
        PeticioRespostaAsincrona resposta = client.getResposta(idPeticio);
        imprimir("getResposta(" + idPeticio + ")", resposta);
        assertNotNull(resposta);
        assertFalse(resposta.isError(), "Error obtenint la resposta: " + resposta.getMissatge());
    }

    // Utilitats
    // /////////////////////////////////////////////////////////////

    private String getEntitatCodi() throws Exception {
        List<Entitat> entitats = client.getEntitats();
        assertNotNull(entitats);
        return entitats.stream()
                .filter(e -> ENTITAT_CIF.equalsIgnoreCase(e.getCif()))
                .map(Entitat::getCodi)
                .findFirst()
                .orElseThrow(() -> new AssertionError("L'usuari no té permís a l'entitat amb CIF " + ENTITAT_CIF));
    }

    private static boolean conteServei(List<ServeiBasic> serveis) {
        return serveis.stream().anyMatch(s -> SERVEI_CODI.equals(s.getCodi()));
    }

    /** Comprova l'ordre de cada nivell i que el codi dels subgrups parteix del codi del grup pare. */
    private static void comprovarGrups(List<GrupDadesEspecifiques> grups, String codiPare) {
        int ordreAnterior = Integer.MIN_VALUE;
        for (GrupDadesEspecifiques grup : grups) {
            assertNotNull(grup.getCodi());
            assertTrue(grup.getOrdre() >= ordreAnterior, "Els grups no estan ordenats: " + grup.getCodi());
            ordreAnterior = grup.getOrdre();
            String codiEsperat = codiPare != null ? codiPare + "/" + grup.getNom() : grup.getNom();
            assertEquals(codiEsperat, grup.getCodi());
            comprovarGrups(grup.getSubgrups(), grup.getCodi());
        }
    }

    private static Set<String> codisGrups(List<GrupDadesEspecifiques> grups) {
        Set<String> codis = new HashSet<>();
        for (GrupDadesEspecifiques grup : grups) {
            codis.add(grup.getCodi());
            codis.addAll(codisGrups(grup.getSubgrups()));
        }
        return codis;
    }

    private static PeticioSincrona novaPeticioSincrona() {
        Map<String, String> dadesEspecifiques = new HashMap<>();
        dadesEspecifiques.put("DatosEspecificos/Solicitud/ProvinciaSolicitud", PROVINCIA_CODI);
        dadesEspecifiques.put("DatosEspecificos/Solicitud/MunicipioSolicitud", "033");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/Titular/Documentacion/Tipo", "DNI");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/Titular/Documentacion/Valor", "18225486x");

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
                        .finalitat("Test pinbal-client " + SERVEI_CODI)
                        .build())
                .solicitud(SolicitudSimple.builder()
                        .titular(Titular.builder()
                                .documentTipus(Titular.DocumentTipus.DNI)
                                .documentNumero("43105084W")
                                .nom("Usuari")
                                .llinatge1("Test")
                                .build())
                        .expedient("testPinbal/999")
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
