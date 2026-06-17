package es.caib.pinbal.client.serveis;

import es.caib.pinbal.client.comu.LogLevel;
import es.caib.pinbal.client.comu.Page;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class ServeiClientTest {

    private ServeiClient serveiClient;
    private ServeiClient serveiClientNoAccess;

    private String existingServeiCodi = "";

    @Before
    public void setUp() {
        String urlBase = "http://localhost:8180/pinbalapi"; // Exemples; ajusta això segons el teu entorn
        String usuari = "pblwsrep";
        String contrasenya = "pblwsrep";
        LogLevel logLevel = LogLevel.DEBUG;

        existingServeiCodi = "SVDDGPCIWS02";

        serveiClient = new ServeiClient(urlBase, usuari, contrasenya, logLevel);
        serveiClientNoAccess = new ServeiClient(urlBase, "pblws", "pblws", logLevel);

    }

    // GET Serveis
    // ////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testGetServeis() {
        try {
            // Prova amb alguns paràmetres que saps que estan configurats correctament al servidor de proves
            Page<Servei> serveis = serveiClient.getServeis("SVDD", null, 0, 10, "asc");
            assertNotNull("La pàgina de serveis no hauria de ser nul·la", serveis);
            assertTrue("El contingut de la pàgina de serveis hauria de tenir elements", serveis.getContent().size() > 0);
        } catch (Exception e) {
            fail("La crida a getServeis ha fallat: " + e.getMessage());
        }
    }

    @Test
    public void testGetServeisNoAccess () {
        try {
            // Assuming getServeis is correctly setup to return 204 with no results for these parameters
            Page<Servei> serveis = serveiClientNoAccess.getServeis("SVDD", null, 0, 10, "asc");
            fail("Ha d'haver llançat una excepció d'AccessDenegat");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Accés denegat"));
        }
    }

    @Test
    public void testGetServeisNoResults () {
        try {
            // Assuming getServeis is correctly setup to return 204 with no results for these parameters
            Page<Servei> serveis = serveiClient.getServeis("codiInexistent", "descripcioInexistent", 0, 10, "asc");
            assertNotNull("La pàgina de procediments no hauria de ser nul·la", serveis);
            assertTrue("La llista de procediments hauria d'estar buida", serveis.getContent().isEmpty());
        } catch (Exception e) {
            fail("Ha fallat la verificació per a pàgina buida de serveis: " + e.getMessage());
        }
    }


    // GET Servei
    // ////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testGetServei() {
        try {
            Servei servei = serveiClient.getServei(existingServeiCodi);
            assertNotNull("El servei no hauria de ser nul", servei);
            assertEquals("Els codis del servei haurien de coincidir", existingServeiCodi, servei.getCodi());
        } catch (Exception e) {
            fail("La crida a getServei ha fallat: " + e.getMessage());
        }
    }

    @Test
    public void testGetServeiNoAccess() {
        try {
            Servei servei = serveiClientNoAccess.getServei(existingServeiCodi);
            fail("Ha d'haver llançat una excepció d'AccessDenegat");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Accés denegat"));
        }
    }

    @Test
    public void testGetServeiNoResults() {
        try {
            String serveiCodi = "serveiInexistentCodi"; // Servei codi that does not exist
            Servei servei = serveiClient.getServei(serveiCodi);
            fail("Ha d'haver llançat una excepció per recurs no trobat");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Recurs no trobat"));
        } catch (IOException e) {
            fail("No hauria d'haver donat una IOException");
        }
    }

}