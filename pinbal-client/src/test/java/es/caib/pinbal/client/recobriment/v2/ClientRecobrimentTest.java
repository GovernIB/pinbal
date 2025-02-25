package es.caib.pinbal.client.recobriment.v2;

import es.caib.pinbal.client.comu.LogLevel;
import es.caib.pinbal.client.procediments.Procediment;
import es.caib.pinbal.client.serveis.Servei;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class ClientRecobrimentTest {

    private ClientRecobriment clientRecobriment;
    private ClientRecobriment clientRecobrimentNoContent;

    private String existingEntitatCodi = "LIM";
    private String emptyEntitatCodi = "07063";
    private String existingProcedimentCodi = "TEST";
    private String emptyProcedimentCodi = "BUIT";
    private String existingServeiCodi = "SCDCPAJU";
    private String emptyServeiCodi = "SCDHPAJU";
    private String enumCampCodi = "DatosEspecificos/Solicitud/Titular/Documentacion/Tipo";
    private String paisEnumCampCodi = "DatosEspecificos/Solicitud/Titular/DatosPersonales/Nombre";
    private String provinciaEnumCampCodi = "DatosEspecificos/Solicitud/ProvinciaSolicitud";
    private String municipiEnumCampCodi = "DatosEspecificos/Solicitud/MunicipioSolicitud";

    @Before
    public void setUp() {
        String urlBase = "http://localhost:8180/pinbalapi"; // Exemples; ajusta això segons el teu entorn
        String usuari = "pblwsrep";
        String contrasenya = "pblwsrep";
        LogLevel logLevel = LogLevel.DEBUG;

//        existingServeiCodi = "SVDDGPCIWS02";

        clientRecobriment = new ClientRecobriment(urlBase, usuari, contrasenya, logLevel);
        clientRecobrimentNoContent = new ClientRecobriment(urlBase, "pblwsno", "pblwsno", logLevel);

    }

    // TESTS getEntitats
    // /////////////////////////////////////////////////////////

    @Test
    public void testGetEntitats_success() throws IOException {

        // Act
        List<Entitat> result = clientRecobriment.getEntitats();

        // Assert
        Assert.assertNotNull(result);
        Assert.assertFalse("The result list should not be empty", result.isEmpty());
        // Add specific assertions if expected data is known
    }

    @Test
    public void testGetServeisNoContent() {
        try {
            // Assuming getServeis is correctly setup to return 204 with no results for these parameters
            List<Entitat> result = clientRecobrimentNoContent.getEntitats();
            assertNull("La llista de entitats hauria de ser nul·la", result);
        } catch (Exception e) {
            fail("Ha fallat la verificació per a pàgina buida de entitats: " + e.getMessage());
        }
    }


    // TESTS getProcediments
    // /////////////////////////////////////////////////////////

    @Test
    public void testGetProcediments_success() throws IOException {
        String entitatCodi = existingEntitatCodi; // Example entity code; adjust according to your environment

        // Act
        List<Procediment> result = clientRecobriment.getProcediments(entitatCodi);

        // Assert
        Assert.assertNotNull("The result list should not be null", result);
        Assert.assertFalse("The result list should not be empty", result.isEmpty());
        // Add additional assertions if necessary to match expected data
    }

    @Test
    public void testGetProcediments_EntitatNotFound() {
        String entitatCodi = "ENTITAT_NO_EXIST"; // Example entity code; adjust according to your environment

        try {
            clientRecobrimentNoContent.getProcediments(entitatCodi);
            fail("S'esperava una excepció per error del servidor");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Recurs no trobat"));
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Accés denegat"));
        }
    }

    @Test
    public void testGetProcediments_EntitatNoContent() {
        String entitatCodi = emptyEntitatCodi;

        try {
            List<Procediment> procediments = clientRecobriment.getProcediments(emptyEntitatCodi);
            assertNull("La llista de procediments hauria de ser nul·la", procediments);
        } catch (Exception e) {
            fail("Ha fallat la verificació per a pàgina buida de procediments: " + e.getMessage());
        }
    }


    // TESTS getServeis
    // /////////////////////////////////////////////////////////

    @Test
    public void testGetServeis_success() throws IOException {

        // Act
        List<Servei> result = clientRecobriment.getServeis();

        // Assert
        Assert.assertNotNull("The result list should not be null", result);
        Assert.assertFalse("The result list should not be empty", result.isEmpty());
        // Add additional assertions if necessary to match expected data
    }


    // TESTS getServeisPerEntitat
    // /////////////////////////////////////////////////////////

    @Test
    public void testGetServeisPerEntitat_success() throws IOException {
        String entitatCodi = existingEntitatCodi;

        // Act
        List<Servei> result = clientRecobriment.getServeisPerEntitat(entitatCodi);

        // Assert
        Assert.assertNotNull("The result list should not be null", result);
        Assert.assertFalse("The result list should not be empty", result.isEmpty());
        // Additional assertions can be added here for expected data
    }

    @Test
    public void testGetServeisPerEntitat_EntitatNotFound() {
        String entitatCodi = "ENTITAT_NO_EXIST";

        try {
            clientRecobrimentNoContent.getServeisPerEntitat(entitatCodi);
            fail("Expected exception not thrown for non-existent entity");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Recurs no trobat"));
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Accés denegat"));
        }
    }

    @Test
    public void testGetServeisPerEntitat_EmptyResponse() {
        String entitatCodi = emptyEntitatCodi;

        try {
            List<Servei> result = clientRecobriment.getServeisPerEntitat(entitatCodi);
            assertNull("The result list should be null for an empty response", result);
        } catch (Exception e) {
            fail("Verification failed for entity with no services: " + e.getMessage());
        }
    }


    // TESTS getServeisPerProcediment
    // /////////////////////////////////////////////////////////

    @Test
    public void testGetServeisPerProcediment_success() throws IOException {
        String procedimentCodi = existingProcedimentCodi; // Example procedure code; adjust as needed

        // Act
        List<Servei> result = clientRecobriment.getServeisPerProcediment(procedimentCodi);

        // Assert
        Assert.assertNotNull("The result list should not be null", result);
        Assert.assertFalse("The result list should not be empty", result.isEmpty());
        // Add specific assertions if expected data is known
    }

    @Test
    public void testGetServeisPerProcediment_ProcedimentNotFound() {
        String procedimentCodi = "PROC_NO_EXIST"; // Example of a non-existent procedure code

        try {
            clientRecobrimentNoContent.getServeisPerProcediment(procedimentCodi);
            fail("Expected exception not thrown for non-existent procedure");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Recurs no trobat"));
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Accés denegat"));
        }
    }

    @Test
    public void testGetServeisPerProcediment_EmptyResponse() {
        String procedimentCodi = emptyProcedimentCodi; // Example of a procedure code returning no services

        try {
            List<Servei> result = clientRecobrimentNoContent.getServeisPerProcediment(procedimentCodi);
            assertNull("The result list should be null for an empty response", result);
        } catch (Exception e) {
            fail("Verification failed for procedure with no services: " + e.getMessage());
        }
    }


    // TESTS getDadesEspecifiques
    // /////////////////////////////////////////////////////////

    @Test
    public void testGetDadesEspecifiques_success() throws IOException {
        String serveiCodi = existingServeiCodi; // Example procedure code; adjust as needed

        // Act
        List<DadaEspecifica> result = clientRecobriment.getDadesEspecifiques(serveiCodi);

        // Assert
        Assert.assertNotNull("The result list should not be null", result);
        Assert.assertFalse("The result list should not be empty", result.isEmpty());
        // Add specific assertions if expected data is known
    }

    @Test
    public void testGetDadesEspecifiques_ServeiNotFound() {
        String serveiCodi = "SERV_NO_EXIST"; // Example of a non-existent procedure code

        try {
            clientRecobrimentNoContent.getDadesEspecifiques(serveiCodi);
            fail("Expected exception not thrown for non-existent procedure");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Recurs no trobat"));
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Accés denegat"));
        }
    }

    @Test
    public void testGetDadesEspecifiques_EmptyResponse() {
        String serveiCodi = emptyServeiCodi; // Example of a procedure code returning no services

        try {
            List<DadaEspecifica> result = clientRecobrimentNoContent.getDadesEspecifiques(serveiCodi);
            assertNull("The result list should be null for an empty response", result);
        } catch (Exception e) {
            fail("Verification failed for procedure with no services: " + e.getMessage());
        }
    }

    // TESTS getValorsEnum
    // /////////////////////////////////////////////////////////

    @Test
    public void testGetValorsEnum_success() throws IOException {
        String serveiCodi = existingServeiCodi; // Example procedure code; adjust as needed
        String campPath = enumCampCodi;

        // Act
        List<ValorEnum> result = clientRecobriment.getValorsEnum(serveiCodi, campPath, null, null);

        // Assert
        Assert.assertNotNull("The result list should not be null", result);
        Assert.assertFalse("The result list should not be empty", result.isEmpty());
        // Add specific assertions if expected data is known
    }

    @Test
    public void testGetValorsEnumPais_success() throws IOException {
        String serveiCodi = existingServeiCodi; // Example procedure code; adjust as needed
        String campPath = paisEnumCampCodi;

        // Act
        List<ValorEnum> result = clientRecobriment.getValorsEnum(serveiCodi, campPath, "PAIS", null);

        // Assert
        Assert.assertNotNull("The result list should not be null", result);
        Assert.assertFalse("The result list should not be empty", result.isEmpty());
        // Add specific assertions if expected data is known
        Assert.assertEquals(244, result.size());
        Assert.assertEquals("724", result.get(66).getCodi());
        Assert.assertEquals("Espanya", result.get(66).getValor());
    }

    @Test
    public void testGetValorsEnumProvincia_success() throws IOException {
        String serveiCodi = existingServeiCodi; // Example procedure code; adjust as needed
        String campPath = provinciaEnumCampCodi;

        // Act
        List<ValorEnum> result = clientRecobriment.getValorsEnum(serveiCodi, campPath, "PROVINCIA", null);

        // Assert
        Assert.assertNotNull("The result list should not be null", result);
        Assert.assertFalse("The result list should not be empty", result.isEmpty());
        // Add specific assertions if expected data is known
        Assert.assertEquals(52, result.size());
        Assert.assertEquals("07", result.get(7).getCodi());
        Assert.assertEquals("Balears, Illes", result.get(7).getValor());
    }

    @Test
    public void testGetValorsEnumMunicipi_success() throws IOException {
        String serveiCodi = existingServeiCodi; // Example procedure code; adjust as needed
        String campPath = municipiEnumCampCodi;

        // Act
        List<ValorEnum> result = clientRecobriment.getValorsEnum(serveiCodi, campPath, "MUNICIPI_3", "07");

        // Assert
        Assert.assertNotNull("The result list should not be null", result);
        Assert.assertFalse("The result list should not be empty", result.isEmpty());
        // Add specific assertions if expected data is known
        Assert.assertEquals(67, result.size());
        Assert.assertEquals("033", result.get(33).getCodi());
        Assert.assertEquals("Manacor", result.get(33).getValor());
    }

    @Test
    public void testGetValorsEnum_ServeiNotFound() {
        String serveiCodi = "SERV_NO_EXIST"; // Example of a non-existent procedure code
        String campPath = enumCampCodi;

        try {
            clientRecobrimentNoContent.getValorsEnum(serveiCodi, campPath, null, null);
            fail("Expected exception not thrown for non-existent servei");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Recurs no trobat"));
        } catch (Exception e) {
            fail("Excepció no esperada: " + e.getMessage());
        }
    }

    @Test
    public void testGetValorsEnum_CampNotFound() {
        String serveiCodi = emptyServeiCodi; // Example of a procedure code returning no services
        String campPath = "CAMP_NO_EXIST";
        try {
            List<ValorEnum> result = clientRecobrimentNoContent.getValorsEnum(serveiCodi, campPath, null, null);
            fail("Expected exception not thrown for non-existent camp");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Recurs no trobat"));
        } catch (Exception e) {
            fail("Excepció no esperada: " + e.getMessage());
        }
    }

    // TESTS peticioSincrona
    // /////////////////////////////////////////////////////////


    // TESTS peticioAsincrona
    // /////////////////////////////////////////////////////////


    // TESTS getResposta
    // /////////////////////////////////////////////////////////


    // TESTS getJustificant
    // /////////////////////////////////////////////////////////


    // TESTS getJustificantImprimible
    // /////////////////////////////////////////////////////////


    // TESTS getJustificantCsv
    // /////////////////////////////////////////////////////////


    // TESTS getJustificantUuid
    // /////////////////////////////////////////////////////////


}