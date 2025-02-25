package es.caib.pinbal.client.usuaris;

import es.caib.pinbal.client.comu.LogLevel;
import es.caib.pinbal.client.comu.Page;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class UsuariClientTest {

    private UsuariClient usuariClient;
    private UsuariClient usuariClientNoAccess;

    private String existingEntitatCodi = "";
    private String existingUsuariCodi = "";
    private String existingProcedimentCodi = "";
    private String existingServeiCodi = "";

    @Before
    public void setUp() {
        // La URL base ha de corresponder-se amb el teu servidor real
        String urlBase = "http://localhost:8180/pinbalapi"; // Exemples; ajusta això segons el teu entorn
        String usuari = "pblwsrep";
        String contrasenya = "pblwsrep";
        LogLevel logLevel = LogLevel.DEBUG;

        existingEntitatCodi = "LIM";
        existingUsuariCodi = "admin";
        existingProcedimentCodi = "TEST";
        existingServeiCodi = "SVDDGPCIWS02";
        
        usuariClient = new UsuariClient(urlBase, usuari, contrasenya, logLevel);
        usuariClientNoAccess = new UsuariClient(urlBase, "pblws", "pblws", logLevel);
    }

    // CREATE or UPDATE Usuari
    // ////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testCreateOrUpdateUsuari_Success() throws Exception {
        UsuariEntitat usuariEntitat = UsuariEntitat.builder().codi("admin").representant(true).delegat(false).actiu(true).build();
        usuariEntitat.setEntitatCodi(existingEntitatCodi);

        try {
            usuariClient.createOrUpdateUsuari(usuariEntitat);
            // If no exceptions, the method executes successfully
        } catch (Exception e) {
            fail("No hauria d'haver llançat excepció: " + e.getMessage());
        }
    }

    @Test
    public void testCreateOrUpdateUsuari_NoAccess() throws Exception {
        UsuariEntitat usuariEntitat = UsuariEntitat.builder().codi("admin").representant(true).delegat(false).actiu(true).build();
        usuariEntitat.setEntitatCodi(existingEntitatCodi);

        try {
            usuariClientNoAccess.createOrUpdateUsuari(usuariEntitat);
            fail("Ha d'haver llançat una excepció d'AccessDenegat");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Accés denegat"));
        }
    }

    @Test
    public void testCreateOrUpdateUsuari_InvalidInput() throws Exception {
        UsuariEntitat usuariEntitat = UsuariEntitat.builder().codi("admin").representant(true).delegat(false).actiu(true).build();
        usuariEntitat.setEntitatCodi(null); // Invalid data

        try {
            usuariClient.createOrUpdateUsuari(usuariEntitat);
            fail("Ha d'haver llançat una excepció per entrada invàlida");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Entrada invàlida"));
        }
    }

    @Test
    public void testCreateOrUpdateUsuari_ServerError() throws Exception {
        UsuariEntitat usuariEntitat = UsuariEntitat.builder().nom("John%O'Brien").actiu(true).build();
        usuariEntitat.setEntitatCodi(existingEntitatCodi);

        try {
            usuariClient.createOrUpdateUsuari(usuariEntitat);
            fail("Ha d'haver llançat una excepció per error del servidor");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Resposta inesperada del servidor"));
        }
    }


    // GET Usuaris
    // ////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testGetUsuaris() throws Exception {
        String entitatCodi = existingEntitatCodi;
        FiltreUsuaris filtreUsuaris = FiltreUsuaris.builder().nom("adm").build();
        int page = 0;
        int size = 10;
        String sort = "usuari.codi,asc";

        // Realitza la petició i obté el resultat
        Page<UsuariEntitat> usuarisPage = usuariClient.getUsuaris(entitatCodi, filtreUsuaris, page, size, sort);

        // Verifica que el resultat no sigui nul i conté dades
        assertNotNull("La pàgina d'usuaris no hauria de ser nul", usuarisPage);
        assertNotNull("La llista de contingut no hauria de ser nul", usuarisPage.getContent());

        // Opcional: Comprova si s'ha recuperat almenys un usuari
        assertTrue("Hi hauria d'haver almenys un usuari a la pàgina", usuarisPage.getContent().size() > 0);
    }

    @Test
    public void testGetUsuarisNoAccess() throws Exception {
        String entitatCodi = existingEntitatCodi;
        FiltreUsuaris filtreUsuaris = FiltreUsuaris.builder().nom("adm").build();
        int page = 0;
        int size = 10;
        String sort = "usuari.codi,asc";

        try {
            // Realitza la petició i obté el resultat
            Page<UsuariEntitat> usuarisPage = usuariClientNoAccess.getUsuaris(entitatCodi, filtreUsuaris, page, size, sort);
            fail("Ha d'haver llançat una excepció d'AccessDenegat");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Accés denegat"));
        }

    }

    @Test
    public void testGetUsuaris_NoUsersFound () throws Exception {
        FiltreUsuaris filtreUsuaris = FiltreUsuaris.builder().codi("emptyFiltre").build();
        int page = 0;
        int size = 10;
        String sort = "usuari.codi,asc";

        Page<UsuariEntitat> usuarisPage = usuariClient.getUsuaris(existingEntitatCodi, filtreUsuaris, page, size, sort);

        assertNotNull("La pàgina d'usuaris no hauria de ser nul", usuarisPage);
        assertEquals("El nombre total d'usuaris hauria de ser zero", 0, usuarisPage.getContent().size());
    }

    @Test
    public void testGetUsuaris_InvalidInput () throws Exception {
        String entitatCodi = null; // Invalid input
        FiltreUsuaris filtreUsuaris = FiltreUsuaris.builder().nom("adm").build();
        int page = 0;
        int size = 10;
        String sort = "usuari.codi,asc";

        try {
            usuariClient.getUsuaris(entitatCodi, filtreUsuaris, page, size, sort);
            fail("Ha d'haver llançat una excepció per entrada invàlida");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Recurs no trobat"));
        }
    }

    // GET Usuari
    // ////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testGetUsuari_Success() throws Exception {
        String usuariCodi = existingUsuariCodi;
        String entitatCodi = existingEntitatCodi;

        try {
            UsuariEntitat usuariEntitat = usuariClient.getUsuari(usuariCodi, entitatCodi);
            assertNotNull("L'usuari no hauria de ser nul", usuariEntitat);
        } catch (Exception e) {
            fail("No hauria d'haver llançat excepció: " + e.getMessage());
        }
    }

    @Test
    public void testGetUsuari_NoAccess() throws Exception {
        String usuariCodi = existingUsuariCodi;
        String entitatCodi = existingEntitatCodi;

        try {
            UsuariEntitat usuariEntitat = usuariClientNoAccess.getUsuari(usuariCodi, entitatCodi);
            fail("Ha d'haver llançat una excepció d'AccessDenegat");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Accés denegat"));
        }
    }

    @Test
    public void testGetUsuari_NotFound() throws Exception {
        String usuariCodi = "nonExistentUser";
        String entitatCodi = existingEntitatCodi;

        UsuariEntitat usuariEntitat = usuariClient.getUsuari(usuariCodi, entitatCodi);
        assertNull("L'usuari hauria de ser nul quan no es troba", usuariEntitat);
    }

    @Test
    public void testGetUsuari_InvalidInput() throws Exception {
        String usuariCodi = null; // Invalid input
        String entitatCodi = null; // Invalid input

        try {
            usuariClient.getUsuari(usuariCodi, entitatCodi);
            fail("Ha d'haver llançat una excepció per entrada invàlida");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Recurs no trobat"));
        }
    }

    // GRANT Permisos usuari
    // ////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testGrantPermissions_Success() throws Exception {
        String usuariCodi = existingUsuariCodi;
        PermisosServei permisosServei = new PermisosServei(
                usuariCodi,
                existingEntitatCodi,
                Arrays.asList(ProcedimentServei.builder().procedimentCodi(existingProcedimentCodi).serveiCodi(existingServeiCodi).build()));

        try {
            usuariClient.grantPermissions(usuariCodi, permisosServei);
            // If no exceptions, the method executes successfully
        } catch (Exception e) {
            fail("No hauria d'haver llançat excepció: " + e.getMessage());
        }
    }

    @Test
    public void testGrantPermissions_NoAccess() throws Exception {
        String usuariCodi = existingUsuariCodi;
        PermisosServei permisosServei = new PermisosServei(
                usuariCodi,
                existingEntitatCodi,
                Arrays.asList(ProcedimentServei.builder().procedimentCodi(existingProcedimentCodi).serveiCodi(existingServeiCodi).build()));

        try {
            usuariClientNoAccess.grantPermissions(usuariCodi, permisosServei);
            fail("Ha d'haver llançat una excepció d'AccessDenegat");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Accés denegat"));
        }
    }

    @Test
    public void testGrantPermissions_InvalidInput() throws Exception {
        String usuariCodi = existingUsuariCodi;
        PermisosServei permisosServei = new PermisosServei(
                null,
                existingEntitatCodi,
                new ArrayList<ProcedimentServei>());

        try {
            usuariClient.grantPermissions(usuariCodi, permisosServei);
            fail("Ha d'haver llançat una excepció per entrada invàlida");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Entrada invàlida"));
        }
    }

    @Test
    public void testGrantPermissions_UserNotFound() throws Exception {
        String usuariCodi = "nonExistentUser";
        PermisosServei permisosServei = new PermisosServei(
                "nonExistingUser",
                existingEntitatCodi,
                new ArrayList<ProcedimentServei>());

        try {
            usuariClient.grantPermissions(usuariCodi, permisosServei);
            fail("Ha d'haver llançat una excepció per usuari no trobat");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Entrada invàlida"));
        }
    }


    // GET Permisos usuari
    // ////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testGetUserPermissions_Success() throws Exception {
        String usuariCodi = existingUsuariCodi;
        String entitatCodi = existingEntitatCodi;

        try {
            PermisosServei permisosServei = usuariClient.getUserPermissions(usuariCodi, entitatCodi);
            assertNotNull("Els permisos no haurien de ser nuls", permisosServei);
        } catch (Exception e) {
            fail("No hauria d'haver llançat excepció: " + e.getMessage());
        }
    }

    @Test
    public void testGetUserPermissions_NoAccess() throws Exception {
        String usuariCodi = existingUsuariCodi;
        String entitatCodi = existingEntitatCodi;

        try {
            PermisosServei permisosServei = usuariClientNoAccess.getUserPermissions(usuariCodi, entitatCodi);
            fail("Ha d'haver llançat una excepció d'AccessDenegat");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Accés denegat"));
        }
    }

    @Test
    public void testGetUserPermissions_NotFound() throws Exception {
        String usuariCodi = "nobody";
        String entitatCodi = existingEntitatCodi;

        PermisosServei permisosServei = usuariClient.getUserPermissions(usuariCodi, entitatCodi);
        assertNull("Els permisos haurien de ser nuls quan no es troben", permisosServei);
    }

    @Test
    public void testGetUserPermissions_InvalidInput() throws Exception {
        String usuariCodi = null; // Invalid input
        String entitatCodi = existingEntitatCodi;

        try {
            usuariClient.getUserPermissions(usuariCodi, entitatCodi);
            fail("Ha d'haver llançat una excepció per entrada invàlida");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Entrada invàlida"));
        }
    }

}