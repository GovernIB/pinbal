package es.caib.pinbal.client.procediments;

import es.caib.pinbal.client.comu.LogLevel;
import es.caib.pinbal.client.comu.Page;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ProcedimentClientTest {

    private ProcedimentClient procedimentClient;

    private String existingEntitatCodi = "";
    private String existingUsuariCodi = "";
    private String existingProcedimentCodi = "";
    private Long existingProcedimentId = null;
    private String existingOrganCodi = "";
    private String existingServeiCodi = "";

    @Before
    public void setUp() {
        // Inicialitza el client amb els paràmetres adequats per al servidor real
        String urlBase = "http://localhost:8180/pinbalapi/interna"; // Exemples; ajusta això segons el teu entorn
        String usuari = "pblws";
        String contrasenya = "pblws";
        LogLevel logLevel = LogLevel.DEBUG;

        existingEntitatCodi = "LIM";
        existingUsuariCodi = "admin";
        existingProcedimentCodi = "TEST";
        existingProcedimentId = 45L;
        existingOrganCodi = "A04026919";
        existingServeiCodi = "SVDDGPCIWS02";

        procedimentClient = new ProcedimentClient(urlBase, usuari, contrasenya, logLevel);
    }


    // CREATE Procediment
    // ////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testCreateProcediment() {
        Procediment nouProcediment = Procediment.builder()
                .codi("PRC_TEST")
                .nom("Procediment per a les proves unitàries")
                .entitatCodi(existingEntitatCodi)
                .organGestorDir3(existingOrganCodi)
                .build();

        try {
            procedimentClient.createProcediment(nouProcediment);
            // Aquí podries afegir comprovacions addicionals si fos necessari
        } catch (Exception e) {
            fail("Ha fallat la creació del procediment: " + e.getMessage());
        }
    }

    @Test
    public void testCreateProcedimentInvalidInput() {
        Procediment invalidProcediment = new Procediment();  // No oblidis de configurar aquest objecte amb valors invàlids

        try {
            procedimentClient.createProcediment(invalidProcediment);
            fail("S'esperava una excepció per entrada invàlida");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Entrada invàlida"));
        } catch (Exception e) {
            fail("S'esperava una excepció de runtime: " + e.getMessage());
        }
    }

    @Test
    public void testCreateProcedimentEntitatNotFound() {
        Procediment nouProcediment = Procediment.builder()
                .codi("validCodi")
                .nom("Valid Procediment")
                .entitatCodi("validEntitat")
                .organGestorDir3("validOrganGestor")
                .build();

        try {
            procedimentClient.createProcediment(nouProcediment);
            fail("S'esperava una excepció per error del servidor");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Recurs no trobat"));
        } catch (Exception e) {
            fail("S'esperava una excepció de Not Found: " + e.getMessage());
        }
    }


    // UPDATE Procediment
    // ////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testUpdateProcedimentSuccess() {
        Procediment procedimentToUpdate = Procediment.builder()
                .id(existingProcedimentId)
                .codi(existingProcedimentCodi)
                .nom("Procediment de test - Mod. " + System.currentTimeMillis())
                .entitatCodi(existingEntitatCodi)
                .organGestorDir3("A04003003")
                .build();

        try {
            procedimentClient.updateProcediment(existingProcedimentId, procedimentToUpdate);
            // Assuming the update is successful, no exception should be thrown
        } catch (Exception e) {
            fail("Ha fallat l'actualització del procediment: " + e.getMessage());
        }
    }

    @Test
    public void testUpdateProcedimentInvalidInput() {
        // Update a procediment with missing mandatory fields for invalid input case
        Procediment invalidProcediment = new Procediment();

        try {
            procedimentClient.updateProcediment(existingProcedimentId, invalidProcediment);
            fail("S'esperava una excepció per entrada invàlida");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Entrada invàlida"));
        } catch (Exception e) {
            fail("S'esperava una excepció de runtime: " + e.getMessage());
        }
    }

    @Test
    public void testUpdateProcedimentInvalidIdentificador() {
        // Update a procediment with missing mandatory fields for invalid input case
        Procediment invalidProcediment = Procediment.builder().id(9999L).build();

        try {
            procedimentClient.updateProcediment(existingProcedimentId, invalidProcediment);
            fail("S'esperava una excepció per identificador del procediment no coincideix amb el procedimentId informat");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("procediment no coincideix"));
        } catch (Exception e) {
            fail("S'esperava una excepció de runtime: " + e.getMessage());
        }
    }

    @Test
    public void testUpdateProcedimentNotFound() {
        Long procedimentId = 9999L; // ID that does not exist
        Procediment procedimentToUpdate = Procediment.builder()
                .id(procedimentId)
                .codi("nonExistentCodi")
                .nom("NonExistent Procediment")
                .entitatCodi(existingEntitatCodi)
                .organGestorDir3("A04003003")
                .build();

        try {
            procedimentClient.updateProcediment(procedimentId, procedimentToUpdate);
            fail("S'esperava una excepció per procediment no trobat");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Procediment no trobat"));
        } catch (Exception e) {
            fail("S'esperava una excepció de runtime: " + e.getMessage());
        }
    }

//    // PATCH Procediment
//    // ////////////////////////////////////////////////////////////////////////////////
//
//    @Test
//    public void testPatchProcedimentSuccess() {
//        Long procedimentId = 1L;
//        ProcedimentPatch patch = ProcedimentPatch.builder()
//                .nom(OptionalField.of("Procediment de test - Mod. " + System.currentTimeMillis()))
//                .actiu(OptionalField.of(true))
//                .build();
//
//        try {
//            procedimentClient.patchProcediment(procedimentId, patch);
//            // Assuming the patch is successful, no exception should be thrown
//        } catch (Exception e) {
//            fail("Ha fallat l'actualització parcial del procediment: " + e.getMessage());
//        }
//    }
//
//    @Test
//    public void testPatchProcedimentNotFound() {
//        Long procedimentId = 9999L; // Non-existent ID
//        ProcedimentPatch patch = ProcedimentPatch.builder()
//                .nom(OptionalField.of("Non-existent Name"))
//                .build();
//
//        try {
//            procedimentClient.patchProcediment(procedimentId, patch);
//            fail("S'esperava una excepció per procediment no trobat");
//        } catch (RuntimeException e) {
//            assertTrue(e.getMessage().contains("Procediment no trobat"));
//        } catch (Exception e) {
//            fail("S'esperava una excepció de runtime: " + e.getMessage());
//        }
//    }


    // ENABLE Servei a Procediment
    // ////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testEnableServeiToProcedimentSuccess() {
        Long procedimentId = existingProcedimentId;
        String serveiCodi = existingServeiCodi;

        try {
            procedimentClient.enableServeiToProcediment(procedimentId, serveiCodi);
            // Assuming the enabling is successful, no exception should be thrown
        } catch (Exception e) {
            fail("Ha fallat l'habilitació del servei al procediment: " + e.getMessage());
        }
    }

    @Test
    public void testEnableServeiToProcediment_ProcedimentNotFound() {
        Long procedimentId = 9999L; // Non-existent ID
        String serveiCodi = "nonExistentServei";

        try {
            procedimentClient.enableServeiToProcediment(procedimentId, serveiCodi);
            fail("S'esperava una excepció per procediment o servei no trobat");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Procediment no trobat"));
        } catch (Exception e) {
            fail("S'esperava una excepció de runtime: " + e.getMessage());
        }
    }

    @Test
    public void testEnableServeiToProcediment_ServeiNotFound() {
        Long procedimentId = 9999L; // Non-existent ID
        String serveiCodi = "nonExistentServei";

        try {
            procedimentClient.enableServeiToProcediment(procedimentId, serveiCodi);
            fail("S'esperava una excepció per procediment o servei no trobat");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Servei no trobat"));
        } catch (Exception e) {
            fail("S'esperava una excepció de runtime: " + e.getMessage());
        }
    }

    @Test
    public void testEnableServeiToProcedimentServerError() {
        Long procedimentId = 1L;
        String serveiCodi = "validServei";

        try {
            procedimentClient.enableServeiToProcediment(procedimentId, serveiCodi);
            fail("S'esperava una excepció per error del servidor");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Resposta inesperada del servidor"));
        } catch (Exception e) {
            fail("S'esperava una excepció de runtime: " + e.getMessage());
        }
    }


    // GET Procediments
    // ////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testGetProcedimentsSuccess() {
        String entitatCodi = "validEntitat";
        String codi = "validCodi";
        String nom = "Valid Name";
        String organGestor = "validOrganGestor";
        int page = 0;
        int size = 10;
        String sort = "codi,asc";

        try {
            Page<Procediment> procedimentPage = procedimentClient.getProcediments(entitatCodi, codi, nom, organGestor, page, size, sort);
            assertNotNull("La pàgina de procediments no hauria de ser nul·la", procedimentPage);
            assertFalse("La llista de procediments no hauria d'estar buida", procedimentPage.getContent().isEmpty());
        } catch (Exception e) {
            fail("Ha fallat la recuperació dels procediments: " + e.getMessage());
        }
    }

    @Test
    public void testGetProcedimentsEmpty() {
        String entitatCodi = "validEntitat";
        String codi = "";
        String nom = "";
        String organGestor = "";
        int page = 0;
        int size = 10;
        String sort = "codi,asc";

        try {
            Page<Procediment> procedimentPage = procedimentClient.getProcediments(entitatCodi, codi, nom, organGestor, page, size, sort);
            assertNotNull("La pàgina de procediments no hauria de ser nul·la", procedimentPage);
            assertTrue("La llista de procediments hauria d'estar buida", procedimentPage.getContent().isEmpty());
        } catch (Exception e) {
            fail("Ha fallat la verificació per a pàgina buida de procediments: " + e.getMessage());
        }
    }

    @Test
    public void testGetProcedimentsEntityNotFound() {
        String entitatCodi = "nonExistentEntitat";
        String codi = "validCodi";
        String nom = "Valid Name";
        String organGestor = "validOrganGestor";
        int page = 0;
        int size = 10;
        String sort = "codi,asc";

        try {
            procedimentClient.getProcediments(entitatCodi, codi, nom, organGestor, page, size, sort);
            fail("S'esperava una excepció per entitat no trobada");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Entitat no trobada"));
        } catch (Exception e) {
            fail("S'esperava una excepció de runtime: " + e.getMessage());
        }
    }

    @Test
    public void testGetProcedimentsServerError() {
        String entitatCodi = "validEntitat";
        String codi = "serverErrorCodi";
        String nom = "Valid Name";
        String organGestor = "validOrganGestor";
        int page = 0;
        int size = 10;
        String sort = "codi,asc";

        try {
            procedimentClient.getProcediments(entitatCodi, codi, nom, organGestor, page, size, sort);
            fail("S'esperava una excepció per error del servidor");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Resposta inesperada del servidor"));
        } catch (Exception e) {
            fail("S'esperava una excepció de runtime: " + e.getMessage());
        }
    }


    // GET Procediment
    // ////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testGetProcedimentById() {
        Long procedimentId = 1L;  // Posa un ID de procediment vàlid que existeixi al servidor de proves

        try {
            Procediment procediment = procedimentClient.getProcediment(procedimentId);
            assertNotNull("El procediment hauria de no ser nul", procediment);
            assertEquals("Els IDs haurien de coincidir", procedimentId, procediment.getId());
        } catch (Exception e) {
            fail("Ha fallat la recuperació del procediment: " + e.getMessage());
        }
    }

    @Test
    public void testGetProcedimentByIdNotFound() {
        Long procedimentId = 9999L; // Non-existent ID

        try {
            procedimentClient.getProcediment(procedimentId);
            fail("S'esperava una excepció per procediment no trobat");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Procediment no trobat"));
        } catch (Exception e) {
            fail("S'esperava una excepció de runtime: " + e.getMessage());
        }
    }

    @Test
    public void testGetProcedimentByIdServerError() {
        Long procedimentId = -1L; // Simulate server error with invalid ID

        try {
            procedimentClient.getProcediment(procedimentId);
            fail("S'esperava una excepció per error del servidor");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Resposta inesperada del servidor"));
        } catch (Exception e) {
            fail("S'esperava una excepció de runtime: " + e.getMessage());
        }
    }


    // GET Procediment by codi i entitat
    // ////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testGetProcedimentByCodi() {
        String procedimentCodi = "codiExemple"; // Ha de ser un codi vàlid al servidor de proves
        String entitatCodi = "entitatExemple"; 

        try {
            Procediment procediment = procedimentClient.getProcediment(procedimentCodi, entitatCodi);
            assertNotNull("El procediment hauria de no ser nul", procediment);
            assertEquals("El codi del procediment ha de coincidir", procedimentCodi, procediment.getCodi());
        } catch (Exception e) {
            fail("Ha fallat la recuperació del procediment pel codi: " + e.getMessage());
        }
    }

    @Test
    public void testGetProcedimentByCodiNotFound() {
        String procedimentCodi = "nonExistentCodi";
        String entitatCodi = "nonExistentEntitat";

        try {
            procedimentClient.getProcediment(procedimentCodi, entitatCodi);
            fail("S'esperava una excepció per procediment no trobat");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Procediment no trobat"));
        } catch (Exception e) {
            fail("S'esperava una excepció de runtime: " + e.getMessage());
        }
    }

    @Test
    public void testGetProcedimentByCodiServerError() {
        String procedimentCodi = "serverErrorCodi";
        String entitatCodi = "validEntitat";

        try {
            procedimentClient.getProcediment(procedimentCodi, entitatCodi);
            fail("S'esperava una excepció per error del servidor");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Resposta inesperada del servidor"));
        } catch (Exception e) {
            fail("S'esperava una excepció de runtime: " + e.getMessage());
        }
    }
}