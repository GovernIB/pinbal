package es.caib.pinbal.client.recobriment;

import es.caib.pinbal.client.procediments.ProcedimentBasic;
import es.caib.pinbal.client.recobriment.model.ScspJustificante;
import es.caib.pinbal.client.recobriment.v2.DadaEspecifica;
import es.caib.pinbal.client.recobriment.v2.DadaEspecificaBasic;
import es.caib.pinbal.client.recobriment.v2.DadaTipusEnum;
import es.caib.pinbal.client.recobriment.v2.DadesComunes;
import es.caib.pinbal.client.recobriment.v2.Entitat;
import es.caib.pinbal.client.recobriment.v2.EstatEnum;
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
import es.caib.pinbal.client.recobriment.v2.Validacio;
import es.caib.pinbal.client.recobriment.v2.ValorEnum;
import es.caib.pinbal.client.serveis.ServeiBasic;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests unitaris del client del recobriment v2 contra un servidor simulat.
 * Comproven que cada mètode crida l'endpoint correcte i que interpreta bé la
 * resposta del servidor.
 */
public class ClientRecobrimentUnitTest {

    /** Format de les dates que retorna PINBAL (Spring Boot, WRITE_DATES_AS_TIMESTAMPS=false). */
    private static final String DATA_JSON = "2026-10-28T09:15:30.000+00:00";
    private static final String AUTORITZACIO = "Basic dXN1YXJpOmNvbnRyYXNlbnlh"; // usuari:contrasenya

    private ServidorPinbalSimulat servidor;
    private ClientRecobriment client;

    @BeforeEach
    public void setUp() throws Exception {
        servidor = new ServidorPinbalSimulat();
        client = new ClientRecobriment(servidor.getUrlBase(), "usuari", "contrasenya");
    }

    @AfterEach
    public void tearDown() {
        servidor.atura();
    }

    // Entitats, procediments i serveis
    // /////////////////////////////////////////////////////////

    @Test
    public void getEntitats() throws Exception {
        servidor.responJson("GET", "entitats", "[{\"codi\":\"E1\",\"nom\":\"Entitat 1\",\"cif\":\"A00000000\"}]");

        List<Entitat> entitats = client.getEntitats();

        assertEquals(1, entitats.size());
        assertEquals("E1", entitats.get(0).getCodi());
        assertEquals("Entitat 1", entitats.get(0).getNom());
        assertEquals("A00000000", entitats.get(0).getCif());
        assertEquals("GET", servidor.getDarrerMetode());
        assertEquals(AUTORITZACIO, servidor.getDarreraAutoritzacio());
    }

    @Test
    public void getProcediments() throws Exception {
        servidor.responJson("GET", "entitats/E1/procediments",
                "[{\"codi\":\"P1\",\"nom\":\"Procediment 1\",\"actiu\":true,\"organGestorDir3\":\"A04000000\",\"codiSia\":\"123456\"}]");

        List<ProcedimentBasic> procediments = client.getProcediments("E1");

        assertEquals(1, procediments.size());
        ProcedimentBasic procediment = procediments.get(0);
        assertEquals("P1", procediment.getCodi());
        assertEquals("Procediment 1", procediment.getNom());
        assertTrue(procediment.isActiu());
        assertEquals("A04000000", procediment.getOrganGestorDir3());
        assertEquals("123456", procediment.getCodiSia());
    }

    @Test
    public void getServeis_senseAmbPermisos_noEnviaElParametre() throws Exception {
        servidor.responJson("GET", "serveis", "[{\"codi\":\"S1\",\"descripcio\":\"Servei 1\",\"actiu\":true}]");

        List<ServeiBasic> serveis = client.getServeis();

        assertEquals(1, serveis.size());
        assertEquals("S1", serveis.get(0).getCodi());
        assertEquals("Servei 1", serveis.get(0).getDescripcio());
        assertTrue(serveis.get(0).getActiu());
        assertNull(serveis.get(0).getPermis());
        assertNull(servidor.getDarreraQuery());
    }

    @Test
    public void getServeis_ambPermisos() throws Exception {
        servidor.responJson("GET", "serveis",
                "[{\"codi\":\"S1\",\"descripcio\":\"Servei 1\",\"actiu\":true,\"permis\":true,\"documentsTipusPermesos\":[\"DNI\",\"NIE\"]}]");

        List<ServeiBasic> serveis = client.getServeis(true);

        assertEquals("ambPermisos=true", servidor.getDarreraQuery());
        assertTrue(serveis.get(0).getPermis());
        assertEquals(Arrays.asList(Titular.DocumentTipus.DNI, Titular.DocumentTipus.NIE), serveis.get(0).getDocumentsTipusPermesos());
    }

    @Test
    public void getServeisPerEntitat() throws Exception {
        servidor.responJson("GET", "entitats/E1/serveis", "[{\"codi\":\"S1\"}]");

        List<ServeiBasic> serveis = client.getServeisPerEntitat("E1", true);

        assertEquals("S1", serveis.get(0).getCodi());
        assertEquals("ambPermisos=true", servidor.getDarreraQuery());
    }

    @Test
    public void getServeisPerEntitat_ambProcedimentsCodiSia() throws Exception {
        servidor.responJson("GET", "entitats/E1/serveis", "[" +
                "{\"codi\":\"S1\",\"permis\":true,\"documentsTipusPermesos\":[\"DNI\"],\"procedimentsCodiSia\":[\"100001\",\"200002\"]}," +
                "{\"codi\":\"S2\",\"permis\":false,\"documentsTipusPermesos\":[],\"procedimentsCodiSia\":[]}]");

        List<ServeiBasic> serveis = client.getServeisPerEntitat("E1", true);

        assertEquals(Arrays.asList("100001", "200002"), serveis.get(0).getProcedimentsCodiSia());
        assertTrue(serveis.get(1).getProcedimentsCodiSia().isEmpty());
    }

    @Test
    public void getServeisPerEntitat_senseAmbPermisos_procedimentsCodiSiaNull() throws Exception {
        servidor.responJson("GET", "entitats/E1/serveis", "[{\"codi\":\"S1\",\"actiu\":true}]");

        List<ServeiBasic> serveis = client.getServeisPerEntitat("E1");

        assertNull(serveis.get(0).getPermis());
        assertNull(serveis.get(0).getProcedimentsCodiSia());
    }

    @Test
    public void getServeisPerProcediment() throws Exception {
        servidor.responJson("GET", "entitats/E1/procediments/P1/serveis", "[{\"codi\":\"S1\"},{\"codi\":\"S2\"}]");

        List<ServeiBasic> serveis = client.getServeisPerProcediment("E1", "P1");

        assertEquals(2, serveis.size());
        assertEquals("S2", serveis.get(1).getCodi());
        assertNull(servidor.getDarreraQuery());
    }

    // Dades específiques, grups i regles
    // /////////////////////////////////////////////////////////

    @Test
    public void getDadesEspecifiques() throws Exception {
        servidor.responJson("GET", "serveis/S1/dadesEspecifiques", "[" +
                "{\"codi\":\"DatosEspecificos/Solicitud/Nacionalidad\",\"nom\":\"Nacionalidad\",\"tipus\":\"ENUM\"," +
                "\"etiqueta\":\"Nacionalitat\",\"obligatori\":true,\"modificable\":true,\"grup\":\"Grup 1/Subgrup\"," +
                "\"enumTipus\":\"PAIS\",\"visible\":false,\"ordre\":3,\"campNou\":\"ignorat\"}," +
                "{\"codi\":\"DatosEspecificos/Solicitud/Edat\",\"tipus\":\"NUMERIC\"," +
                "\"validacio\":{\"tipus\":\"NUM_RANG\",\"min\":1,\"max\":99},\"visible\":true,\"ordre\":1}]");

        List<DadaEspecifica> dades = client.getDadesEspecifiques("S1");

        assertEquals(2, dades.size());
        DadaEspecifica nacionalitat = dades.get(0);
        assertEquals("DatosEspecificos/Solicitud/Nacionalidad", nacionalitat.getCodi());
        assertEquals("Nacionalidad", nacionalitat.getNom());
        assertEquals(DadaTipusEnum.ENUM, nacionalitat.getTipus());
        assertEquals("Nacionalitat", nacionalitat.getEtiqueta());
        assertTrue(nacionalitat.isObligatori());
        assertEquals("Grup 1/Subgrup", nacionalitat.getGrup());
        assertEquals(DadaEspecifica.EnumTipus.PAIS, nacionalitat.getEnumTipus());
        assertFalse(nacionalitat.isVisible());
        assertEquals(3, nacionalitat.getOrdre());
        DadaEspecifica edat = dades.get(1);
        assertNull(edat.getEnumTipus());
        assertTrue(edat.isVisible());
        assertEquals(1, edat.getOrdre());
        assertEquals(Validacio.ValidacioTipus.NUM_RANG, edat.getValidacio().getTipus());
        assertEquals(Integer.valueOf(99), edat.getValidacio().getMax());
    }

    @Test
    public void getDadesEspecifiquesResposta() throws Exception {
        servidor.responJson("GET", "serveis/S1/dadesEspecifiquesResposta",
                "[{\"codi\":\"DatosEspecificos/Retorno/Estado\",\"nom\":\"Estado\",\"tipus\":\"TEXT\",\"format\":\"MaxSize(10)\"}]");

        List<DadaEspecificaBasic> dades = client.getDadesEspecifiquesResposta("S1");

        assertEquals(1, dades.size());
        assertEquals("DatosEspecificos/Retorno/Estado", dades.get(0).getCodi());
        assertEquals(DadaTipusEnum.TEXT, dades.get(0).getTipus());
        assertEquals("MaxSize(10)", dades.get(0).getFormat());
    }

    @Test
    public void getGrups() throws Exception {
        servidor.responJson("GET", "serveis/S1/grups", "[" +
                "{\"codi\":\"Grup 1\",\"nom\":\"Grup 1\",\"ordre\":1,\"ajuda\":\"Ajuda\",\"subgrups\":[" +
                "{\"codi\":\"Grup 1/Subgrup\",\"nom\":\"Subgrup\",\"ordre\":1,\"subgrups\":[]}]}," +
                "{\"codi\":\"Grup 2\",\"nom\":\"Grup 2\",\"ordre\":2,\"subgrups\":[]}]");

        List<GrupDadesEspecifiques> grups = client.getGrups("S1");

        assertEquals(2, grups.size());
        GrupDadesEspecifiques grup1 = grups.get(0);
        assertEquals("Grup 1", grup1.getCodi());
        assertEquals(1, grup1.getOrdre());
        assertEquals("Ajuda", grup1.getAjuda());
        assertEquals(1, grup1.getSubgrups().size());
        assertEquals("Grup 1/Subgrup", grup1.getSubgrups().get(0).getCodi());
        assertEquals("Subgrup", grup1.getSubgrups().get(0).getNom());
        assertEquals(2, grups.get(1).getOrdre());
        assertTrue(grups.get(1).getSubgrups().isEmpty());
    }

    @Test
    public void getRegles() throws Exception {
        servidor.responJson("GET", "serveis/S1/regles", "[" +
                "{\"nom\":\"R1\",\"ordre\":1,\"modificat\":\"ALGUN_CAMP\",\"modificatValors\":[\"path/a\"]," +
                "\"accio\":\"OBLIGATORI\",\"afectatValors\":[\"path/b\",\"path/c\"]}," +
                "{\"nom\":\"R2\",\"ordre\":2,\"modificat\":\"GRUPS\",\"modificatValors\":[\"Grup 1\"]," +
                "\"accio\":\"OCULTAR\",\"afectatValors\":[\"Grup 2\"]}]");

        List<Regla> regles = client.getRegles("S1");

        assertEquals(2, regles.size());
        Regla regla = regles.get(0);
        assertEquals("R1", regla.getNom());
        assertEquals(1, regla.getOrdre());
        assertEquals(Regla.ReglaModificat.ALGUN_CAMP, regla.getModificat());
        assertEquals(Collections.singletonList("path/a"), regla.getModificatValors());
        assertEquals(Regla.ReglaAccio.OBLIGATORI, regla.getAccio());
        assertEquals(Arrays.asList("path/b", "path/c"), regla.getAfectatValors());
        assertEquals(Regla.ReglaModificat.GRUPS, regles.get(1).getModificat());
        assertEquals(Regla.ReglaAccio.OCULTAR, regles.get(1).getAccio());
    }

    @Test
    public void getValorsEnum_ambFiltre() throws Exception {
        servidor.responJson("GET", "serveis/S1/camps/DatosEspecificos/Solicitud/Provincia/enumerat/PROVINCIA",
                "[{\"codi\":\"07\",\"valor\":\"Illes Balears\"}]");

        List<ValorEnum> valors = client.getValorsEnum("S1", "DatosEspecificos/Solicitud/Provincia", "PROVINCIA", "CA");

        assertEquals(1, valors.size());
        assertEquals("07", valors.get(0).getCodi());
        assertEquals("Illes Balears", valors.get(0).getValor());
        assertEquals("filtre=CA", servidor.getDarreraQuery());
    }

    @Test
    public void getValorsEnum_senseFiltre() throws Exception {
        servidor.responJson("GET", "serveis/S1/camps/Tipo/enumerat/ENUM", "[{\"codi\":\"A\",\"valor\":\"Opció A\"}]");

        List<ValorEnum> valors = client.getValorsEnum("S1", "Tipo", "ENUM", null);

        assertEquals("Opció A", valors.get(0).getValor());
        assertNull(servidor.getDarreraQuery());
    }

    // Consultes
    // /////////////////////////////////////////////////////////

    @Test
    public void peticioSincrona() throws Exception {
        servidor.responJson("POST", "serveis/S1/peticioSincrona", "{" +
                "\"error\":false,\"estat\":\"TRAMITADA\",\"missatge\":\"OK\"," +
                "\"resposta\":{\"idSolicitud\":\"SOL1\",\"dataResposta\":\"" + DATA_JSON + "\"," +
                "\"titular\":{\"documentTipus\":\"DNI\",\"documentNumero\":\"12345678Z\"}," +
                "\"dadesEspecifiques\":{\"DatosEspecificos/Retorno/Estado\":\"0003\"}}}");

        PeticioRespostaSincrona resposta = client.peticioSincrona("S1", novaPeticioSincrona());

        assertEquals("POST", servidor.getDarrerMetode());
        assertTrue(servidor.getDarrerContentType().startsWith("application/json"));
        String cos = servidor.getDarrerCos();
        assertTrue(cos.contains("\"serveiCodi\":\"S1\""), cos);
        assertTrue(cos.contains("\"documentNumero\":\"12345678Z\""), cos);
        assertTrue(cos.contains("\"consentiment\":\"Si\""), cos);
        assertTrue(cos.contains("\"DatosEspecificos/Solicitud/Provincia\":\"07\""), cos);
        assertFalse(resposta.isError());
        assertEquals(EstatEnum.TRAMITADA, resposta.getEstat());
        assertEquals("SOL1", resposta.getResposta().getIdSolicitud());
        assertEquals(dataEsperada(), resposta.getResposta().getDataResposta());
        assertEquals(Titular.DocumentTipus.DNI, resposta.getResposta().getTitular().getDocumentTipus());
        assertEquals("0003", resposta.getResposta().getDadesEspecifiques().get("DatosEspecificos/Retorno/Estado"));
    }

    @Test
    public void peticioSincrona_errorsValidacio() throws Exception {
        servidor.responJson("POST", "serveis/S1/peticioSincrona", "{" +
                "\"error\":true,\"estat\":\"ERROR\",\"missatge\":\"Errors de validació\"," +
                "\"errorsValidacio\":{\"dadesComunes.finalitat\":[\"Camp obligatori\"]}}");

        PeticioRespostaSincrona resposta = client.peticioSincrona("S1", novaPeticioSincrona());

        assertTrue(resposta.isError());
        assertEquals(EstatEnum.ERROR, resposta.getEstat());
        assertEquals(Collections.singletonList("Camp obligatori"), resposta.getErrorsValidacio().get("dadesComunes.finalitat"));
    }

    @Test
    public void peticioAsincrona() throws Exception {
        servidor.responJson("POST", "serveis/S1/peticioAsincrona", "{" +
                "\"error\":false,\"estat\":\"PENDENT\"," +
                "\"confirmacioPeticio\":{\"serveiCodi\":\"S1\",\"idPeticio\":\"PET1\",\"numSolicituds\":2," +
                "\"dataEnviamentPeticio\":\"" + DATA_JSON + "\",\"dataEstimadaResposta\":\"" + DATA_JSON + "\"}}");
        PeticioAsincrona peticio = new PeticioAsincrona(
                novaPeticioSincrona().getDadesComunes(),
                Arrays.asList(solicitud("12345678Z"), solicitud("87654321X")));

        PeticioConfirmacioAsincrona confirmacio = client.peticioAsincrona("S1", peticio);

        assertEquals("POST", servidor.getDarrerMetode());
        assertTrue(servidor.getDarrerCos().contains("\"87654321X\""));
        assertEquals(EstatEnum.PENDENT, confirmacio.getEstat());
        assertEquals("PET1", confirmacio.getConfirmacioPeticio().getIdPeticio());
        assertEquals(2, confirmacio.getConfirmacioPeticio().getNumSolicituds());
        assertEquals(dataEsperada(), confirmacio.getConfirmacioPeticio().getDataEstimadaResposta());
    }

    // Respostes i justificants
    // /////////////////////////////////////////////////////////

    @Test
    public void getResposta() throws Exception {
        servidor.responJson("GET", "consultes/PET1/resposta", "{" +
                "\"error\":false,\"estat\":\"TRAMITADA\",\"dataEstimadaResposta\":\"" + DATA_JSON + "\"," +
                "\"respostes\":[{\"idSolicitud\":\"SOL1\"},{\"idSolicitud\":\"SOL2\"}]}");

        PeticioRespostaAsincrona resposta = client.getResposta("PET1");

        assertEquals(EstatEnum.TRAMITADA, resposta.getEstat());
        assertEquals(dataEsperada(), resposta.getDataEstimadaResposta());
        assertEquals(2, resposta.getRespostes().size());
        assertEquals("SOL2", resposta.getRespostes().get(1).getIdSolicitud());
    }

    @Test
    public void getJustificant() throws Exception {
        servidor.responJson("GET", "consultes/PET1/solicitud/SOL1/justificant",
                "{\"nom\":\"justificant.pdf\",\"contentType\":\"application/pdf\",\"contingut\":\"JVBERi0=\"}");

        ScspJustificante justificant = client.getJustificant("PET1", "SOL1");

        assertEquals("justificant.pdf", justificant.getNom());
        assertEquals("application/pdf", justificant.getContentType());
        assertArrayEquals("%PDF-".getBytes("UTF-8"), justificant.getContingut());
    }

    @Test
    public void getJustificantImprimible() throws Exception {
        servidor.responJson("GET", "consultes/PET1/solicitud/SOL1/justificantImprimible",
                "{\"nom\":\"imprimible.pdf\",\"contentType\":\"application/pdf\",\"contingut\":\"JVBERi0=\"}");

        ScspJustificante justificant = client.getJustificantImprimible("PET1", "SOL1");

        assertEquals("imprimible.pdf", justificant.getNom());
        assertEquals(5, justificant.getLongitud());
    }

    @Test
    public void getJustificantCsv() throws Exception {
        servidor.respon("GET", "consultes/PET1/solicitud/SOL1/justificantCsv", 200, "application/json", "CSV-1234");

        assertEquals("CSV-1234", client.getJustificantCsv("PET1", "SOL1"));
    }

    @Test
    public void getJustificantUuid() throws Exception {
        servidor.respon("GET", "consultes/PET1/solicitud/SOL1/justificantUuid", 200, "application/json", "a1b2c3d4-uuid");

        assertEquals("a1b2c3d4-uuid", client.getJustificantUuid("PET1", "SOL1"));
    }

    // Gestió d'errors
    // /////////////////////////////////////////////////////////

    @Test
    public void senseContingut_retornaNull() throws Exception {
        servidor.respon("GET", "serveis/S1/grups", 204, null, null);
        servidor.respon("GET", "serveis/S1/regles", 204, null, null);
        servidor.respon("GET", "consultes/PET1/resposta", 204, null, null);

        assertNull(client.getGrups("S1"));
        assertNull(client.getRegles("S1"));
        assertNull(client.getResposta("PET1"));
    }

    @Test
    public void justificantNoDesatArxiu_csvIUuidRetornenNull() throws Exception {
        // El justificant el gestiona l'aplicació i PINBAL no el desa a l'arxiu
        servidor.respon("GET", "consultes/PET1/solicitud/SOL1/justificantCsv", 204, null, null);
        servidor.respon("GET", "consultes/PET1/solicitud/SOL1/justificantUuid", 204, null, null);

        assertNull(client.getJustificantCsv("PET1", "SOL1"));
        assertNull(client.getJustificantUuid("PET1", "SOL1"));
    }

    @Test
    public void recursNoTrobat_llancaExcepcioAmbElMissatgeDelServidor() throws Exception {
        servidor.respon("GET", "serveis/NOEXISTEIX/regles", 404, "application/json", "{\"errorMessage\":\"Servei no trobat\"}");

        try {
            client.getRegles("NOEXISTEIX");
            fail("S'esperava una excepció");
        } catch (RuntimeException ex) {
            assertTrue(ex.getMessage().contains("Servei no trobat"), ex.getMessage());
        }
    }

    @Test
    public void errorServidor_llancaExcepcio() throws Exception {
        servidor.respon("GET", "serveis/S1/dadesEspecifiques", 500, "application/json", "{\"errorMessage\":\"Error intern\"}");

        try {
            client.getDadesEspecifiques("S1");
            fail("S'esperava una excepció");
        } catch (RuntimeException ex) {
            assertTrue(ex.getMessage().contains("Error intern"), ex.getMessage());
        }
    }

    // Utilitats
    // /////////////////////////////////////////////////////////

    private static PeticioSincrona novaPeticioSincrona() {
        DadesComunes dadesComunes = new DadesComunes();
        dadesComunes.setEntitatCif("A00000000");
        dadesComunes.setProcedimentCodi("P1");
        dadesComunes.setServeiCodi("S1");
        dadesComunes.setFuncionari(new Funcionari("Funcionari", "12345678Z"));
        dadesComunes.setConsentiment(DadesComunes.Consentiment.Si);
        dadesComunes.setFinalitat("Test");
        return new PeticioSincrona(dadesComunes, solicitud("12345678Z"));
    }

    private static SolicitudSimple solicitud(String documentNumero) {
        Titular titular = new Titular();
        titular.setDocumentTipus(Titular.DocumentTipus.DNI);
        titular.setDocumentNumero(documentNumero);
        Map<String, String> dadesEspecifiques = new HashMap<String, String>();
        dadesEspecifiques.put("DatosEspecificos/Solicitud/Provincia", "07");
        SolicitudSimple solicitud = new SolicitudSimple();
        solicitud.setTitular(titular);
        solicitud.setDadesEspecifiques(dadesEspecifiques);
        return solicitud;
    }

    private static Date dataEsperada() {
        Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        calendar.clear();
        calendar.set(2026, Calendar.OCTOBER, 28, 9, 15, 30);
        return calendar.getTime();
    }

}
