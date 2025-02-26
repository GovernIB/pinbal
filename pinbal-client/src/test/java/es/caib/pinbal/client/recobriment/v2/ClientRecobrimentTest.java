package es.caib.pinbal.client.recobriment.v2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import es.caib.pinbal.client.comu.LogLevel;
import es.caib.pinbal.client.procediments.Procediment;
import es.caib.pinbal.client.serveis.Servei;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ClientRecobrimentTest {

    private ClientRecobriment clientRecobriment;
    private ClientRecobriment clientRecobrimentNoContent;

    private static final String EXISTING_ENTITAT_CODI = "LIM";
    private static final String EXISTING_ENTITAT_CIF = "12345678Z";
    private static final String EMPTY_ENTITAT_CODI = "07063";
    private static final String EMPTY_ENTITAT_CIF = "P0706300A";
    private static final String EXISTING_PROCEDIMENT_CODI = "TEST";
    private static final String EMPTY_PROCEDIMENT_CODI = "BUIT";
    private static final String EXISTING_SERVEI_CODI = "SCDCPAJU";
    private static final String EMPTY_SERVEI_CODI = "SCDHPAJU";
    private static final String ENUM_CAMP_CODI = "DatosEspecificos/Solicitud/Titular/Documentacion/Tipo";
    private static final String PAIS_ENUM_CAMP_CODI = "DatosEspecificos/Solicitud/Titular/DatosPersonales/Nombre";
    private static final String PROVINCIA_ENUM_CAMP_CODI = "DatosEspecificos/Solicitud/ProvinciaSolicitud";
    private static final String MUNICIPI_ENUM_CAMP_CODI = "DatosEspecificos/Solicitud/MunicipioSolicitud";
    private static final String FUNCIONARI_NIF = "18225486x";
    private static final String FUNCIONARI_NOM = "Melcior Andreu Nadal";
    private static final String FUNCIONARI_CODI = "sandreu";

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
        String entitatCodi = EXISTING_ENTITAT_CODI; // Example entity code; adjust according to your environment

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
        String entitatCodi = EMPTY_ENTITAT_CODI;

        try {
            List<Procediment> procediments = clientRecobriment.getProcediments(EMPTY_ENTITAT_CODI);
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
        String entitatCodi = EXISTING_ENTITAT_CODI;

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
        String entitatCodi = EMPTY_ENTITAT_CODI;

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
        String procedimentCodi = EXISTING_PROCEDIMENT_CODI; // Example procedure code; adjust as needed

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
        String procedimentCodi = EMPTY_PROCEDIMENT_CODI; // Example of a procedure code returning no services

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
        String serveiCodi = EXISTING_SERVEI_CODI; // Example procedure code; adjust as needed

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
        String serveiCodi = EMPTY_SERVEI_CODI; // Example of a procedure code returning no services

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
        String serveiCodi = EXISTING_SERVEI_CODI; // Example procedure code; adjust as needed
        String campPath = ENUM_CAMP_CODI;

        // Act
        List<ValorEnum> result = clientRecobriment.getValorsEnum(serveiCodi, campPath, null, null);

        // Assert
        Assert.assertNotNull("The result list should not be null", result);
        Assert.assertFalse("The result list should not be empty", result.isEmpty());
        // Add specific assertions if expected data is known
    }

    @Test
    public void testGetValorsEnumPais_success() throws IOException {
        String serveiCodi = EXISTING_SERVEI_CODI; // Example procedure code; adjust as needed
        String campPath = PAIS_ENUM_CAMP_CODI;

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
        String serveiCodi = EXISTING_SERVEI_CODI; // Example procedure code; adjust as needed
        String campPath = PROVINCIA_ENUM_CAMP_CODI;

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
        String serveiCodi = EXISTING_SERVEI_CODI; // Example procedure code; adjust as needed
        String campPath = MUNICIPI_ENUM_CAMP_CODI;

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
        String campPath = ENUM_CAMP_CODI;

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
        String serveiCodi = EMPTY_SERVEI_CODI;
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

//    @Test
    public void peticionSincrona_success() throws UniformInterfaceException, ClientHandlerException, IOException {
        String entitatCif = EXISTING_ENTITAT_CIF;
        String procedimentCodi = EXISTING_PROCEDIMENT_CODI;
        String serveiCodi = EXISTING_SERVEI_CODI;

        Map<String, String> dadesEspecifiques = new HashMap<>();
        dadesEspecifiques.put("DatosEspecificos/Solicitud/ProvinciaSolicitud", "07");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/MunicipioSolicitud", "033");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/Titular/Documentacion/Tipo", "DNI");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/Titular/Documentacion/Valor", "18225486x");

        PeticioSincrona peticioSincrona = getPeticioSincrona(entitatCif, procedimentCodi, serveiCodi, dadesEspecifiques);

        try {
            PeticioRespostaSincrona respuesta = clientRecobriment.peticioSincrona(serveiCodi, peticioSincrona);
            assertNotNull(respuesta);
            assertFalse("La resposta indica que s'ha produit un error en l'enviament", respuesta.isError());
            System.out.println("-> peticionSincrona = " + objectToJsonString(respuesta));
        } catch (Exception e) {
            fail("Excepció no esperada: " + e.getMessage());
        }
    }

    @Test
    public void peticionSincrona_NullDadesComunes() throws UniformInterfaceException, ClientHandlerException, IOException {
        String entitatCif = EXISTING_ENTITAT_CIF;
        String procedimentCodi = EXISTING_PROCEDIMENT_CODI;
        String serveiCodi = EXISTING_SERVEI_CODI;

        Map<String, String> dadesEspecifiques = new HashMap<>();
        dadesEspecifiques.put("DatosEspecificos/Solicitud/ProvinciaSolicitud", "07");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/MunicipioSolicitud", "033");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/Titular/Documentacion/Tipo", "DNI");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/Titular/Documentacion/Valor", "18225486x");

        PeticioSincrona peticioSincrona = getPeticioSincrona(entitatCif, procedimentCodi, serveiCodi, dadesEspecifiques);
        peticioSincrona.setDadesComunes(null);

        try {
            PeticioRespostaSincrona respuesta = clientRecobriment.peticioSincrona(serveiCodi, peticioSincrona);
            assertNotNull(respuesta);
            assertTrue("La resposta indica que no s'ha produit cap error en l'enviament", respuesta.isError());
            assertFalse("No s'ha produït error en les dades comunes", respuesta.getErrorsValidacio().get("dadesComunes").isEmpty());
            assertTrue(
                    "No s'ha produït error en les dades comunes",
                    respuesta.getErrorsValidacio()
                            .get("dadesComunes")
                            .contains("No s'ha trobat l'element dadesComunes")
            );
            System.out.println("-> resposta = " + objectToJsonString(respuesta));
        } catch (Exception e) {
            fail("Excepció no esperada: " + e.getMessage());
        }
    }

    @Test
    public void peticionSincrona_ServeiCodiMismatch() throws UniformInterfaceException, ClientHandlerException, IOException {
        String entitatCif = EXISTING_ENTITAT_CIF;
        String procedimentCodi = EXISTING_PROCEDIMENT_CODI;
        String serveiCodi = EXISTING_SERVEI_CODI;

        Map<String, String> dadesEspecifiques = new HashMap<>();
        dadesEspecifiques.put("DatosEspecificos/Solicitud/ProvinciaSolicitud", "07");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/MunicipioSolicitud", "033");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/Titular/Documentacion/Tipo", "DNI");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/Titular/Documentacion/Valor", "18225486x");

        PeticioSincrona peticioSincrona = getPeticioSincrona(entitatCif, procedimentCodi, serveiCodi, dadesEspecifiques);

        try {
            PeticioRespostaSincrona respuesta = clientRecobriment.peticioSincrona("ALTRE_SERVEI_CODI", peticioSincrona);
            assertNotNull(respuesta);
            assertTrue("La resposta indica que no s'ha produit cap error en l'enviament", respuesta.isError());
            assertNotNull("No s'ha produït error en les dades comunes", respuesta.getErrorsValidacio().get("dadesComunes.serveiCodi"));
            assertTrue(
                    "No s'ha produït error en les dades comunes",
                    respuesta.getErrorsValidacio()
                            .get("dadesComunes.serveiCodi")
                            .contains("El servei informat a la petició ALTRE_SERVEI_CODI no coindideix amb el de la solicitud " + serveiCodi)
            );
            System.out.println("-> peticionSincrona = " + objectToJsonString(respuesta));
        } catch (Exception e) {
            fail("Excepció no esperada: " + e.getMessage());
        }
    }

    @Test
    public void peticionSincrona_InvalidServeiCodi() throws UniformInterfaceException, ClientHandlerException, IOException {
        String entitatCif = EXISTING_ENTITAT_CIF;
        String procedimentCodi = EXISTING_PROCEDIMENT_CODI;
        String serveiCodi = "NO_EXISTING_SERVEI_CODI";

        Map<String, String> dadesEspecifiques = new HashMap<>();
        dadesEspecifiques.put("DatosEspecificos/Solicitud/ProvinciaSolicitud", "07");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/MunicipioSolicitud", "033");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/Titular/Documentacion/Tipo", "DNI");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/Titular/Documentacion/Valor", "18225486x");

        PeticioSincrona peticioSincrona = getPeticioSincrona(entitatCif, procedimentCodi, serveiCodi, dadesEspecifiques);

        try {
            PeticioRespostaSincrona respuesta = clientRecobriment.peticioSincrona(serveiCodi, peticioSincrona);
            assertNotNull(respuesta);
            assertTrue("La resposta indica que no s'ha produit cap error en l'enviament", respuesta.isError());
            assertNotNull("No s'ha produït error en les dades comunes", respuesta.getErrorsValidacio().get("dadesComunes.serveiCodi"));
            assertTrue(
                    "No s'ha produït error en les dades comunes",
                    respuesta.getErrorsValidacio()
                            .get("dadesComunes.serveiCodi")
                            .contains("No s'ha trobat el servei amb codi " + serveiCodi)
            );
            System.out.println("-> peticionSincrona = " + objectToJsonString(respuesta));
        } catch (Exception e) {
            fail("Excepció no esperada: " + e.getMessage());
        }
    }

    @Test
    public void peticionSincrona_InvalidEntitatCif() throws UniformInterfaceException, ClientHandlerException, IOException {
        String entitatCif = "99999999Q";
        String procedimentCodi = EXISTING_PROCEDIMENT_CODI;
        String serveiCodi = EXISTING_SERVEI_CODI;

        Map<String, String> dadesEspecifiques = new HashMap<>();
        dadesEspecifiques.put("DatosEspecificos/Solicitud/ProvinciaSolicitud", "07");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/MunicipioSolicitud", "033");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/Titular/Documentacion/Tipo", "DNI");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/Titular/Documentacion/Valor", "18225486x");

        PeticioSincrona peticioSincrona = getPeticioSincrona(entitatCif, procedimentCodi, serveiCodi, dadesEspecifiques);

        try {
            PeticioRespostaSincrona respuesta = clientRecobriment.peticioSincrona(serveiCodi, peticioSincrona);
            assertNotNull(respuesta);
            assertTrue("La resposta indica que no s'ha produit cap error en l'enviament", respuesta.isError());
            assertNotNull("No s'ha produït error en les dades comunes", respuesta.getErrorsValidacio().get("dadesComunes.entitatCif"));
            assertTrue(
                    "No s'ha produït error en les dades comunes",
                    respuesta.getErrorsValidacio()
                            .get("dadesComunes.entitatCif")
                            .contains("No s'ha trobat l'entitat amb el CIF " + entitatCif)
            );
            System.out.println("-> peticionSincrona = " + objectToJsonString(respuesta));
        } catch (Exception e) {
            fail("Excepció no esperada: " + e.getMessage());
        }
    }

    @Test
    public void peticionSincrona_InvalidProcedimentCodi() throws UniformInterfaceException, ClientHandlerException, IOException {
        String entitatCif = EXISTING_ENTITAT_CIF;
        String procedimentCodi = "NO_EXISTING_PROCEDIMENT_CODI";
        String serveiCodi = EXISTING_SERVEI_CODI;

        Map<String, String> dadesEspecifiques = new HashMap<>();
        dadesEspecifiques.put("DatosEspecificos/Solicitud/ProvinciaSolicitud", "07");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/MunicipioSolicitud", "033");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/Titular/Documentacion/Tipo", "DNI");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/Titular/Documentacion/Valor", "18225486x");

        PeticioSincrona peticioSincrona = getPeticioSincrona(entitatCif, procedimentCodi, serveiCodi, dadesEspecifiques);

        try {
            PeticioRespostaSincrona respuesta = clientRecobriment.peticioSincrona(serveiCodi, peticioSincrona);
            assertNotNull(respuesta);
            assertTrue("La resposta indica que no s'ha produit cap error en l'enviament", respuesta.isError());
            assertNotNull("No s'ha produït error en les dades comunes", respuesta.getErrorsValidacio().get("dadesComunes.procedimentCodi"));
            assertTrue(
                    "No s'ha produït error en les dades comunes",
                    respuesta.getErrorsValidacio()
                            .get("dadesComunes.procedimentCodi")
                            .contains("No s'ha trobat el procediment amb el codi " + procedimentCodi)
            );
            System.out.println("-> peticionSincrona = " + objectToJsonString(respuesta));
        } catch (Exception e) {
            fail("Excepció no esperada: " + e.getMessage());
        }
    }

    @Test
    public void peticionSincrona_ProcedimentEntitatMismatch() throws UniformInterfaceException, ClientHandlerException, IOException {
        String entitatCif = EMPTY_ENTITAT_CIF;
        String procedimentCodi = EXISTING_PROCEDIMENT_CODI;
        String serveiCodi = EXISTING_SERVEI_CODI;

        Map<String, String> dadesEspecifiques = new HashMap<>();
        dadesEspecifiques.put("DatosEspecificos/Solicitud/ProvinciaSolicitud", "07");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/MunicipioSolicitud", "033");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/Titular/Documentacion/Tipo", "DNI");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/Titular/Documentacion/Valor", "18225486x");

        PeticioSincrona peticioSincrona = getPeticioSincrona(entitatCif, procedimentCodi, serveiCodi, dadesEspecifiques);

        try {
            PeticioRespostaSincrona respuesta = clientRecobriment.peticioSincrona(serveiCodi, peticioSincrona);
            assertNotNull(respuesta);
            assertTrue("La resposta indica que no s'ha produit cap error en l'enviament", respuesta.isError());
            assertNotNull("No s'ha produït error en les dades comunes", respuesta.getErrorsValidacio().get("dadesComunes.procedimentCodi"));
            assertTrue(
                    "No s'ha produït error en les dades comunes",
                    respuesta.getErrorsValidacio()
                            .get("dadesComunes.procedimentCodi")
                            .contains("L'entitat del procediment no té el CIF indicant al camp dadesComunes.entitatCif")
            );
            System.out.println("-> peticionSincrona = " + objectToJsonString(respuesta));
        } catch (Exception e) {
            fail("Excepció no esperada: " + e.getMessage());
        }
    }

    @Test
    public void peticionSincrona_MissingConsentiment() throws UniformInterfaceException, ClientHandlerException, IOException {
        String entitatCif = EXISTING_ENTITAT_CIF;
        String procedimentCodi = EXISTING_PROCEDIMENT_CODI;
        String serveiCodi = EXISTING_SERVEI_CODI;

        Map<String, String> dadesEspecifiques = new HashMap<>();
        dadesEspecifiques.put("DatosEspecificos/Solicitud/ProvinciaSolicitud", "07");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/MunicipioSolicitud", "033");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/Titular/Documentacion/Tipo", "DNI");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/Titular/Documentacion/Valor", "18225486x");

        PeticioSincrona peticioSincrona = getPeticioSincrona(entitatCif, procedimentCodi, serveiCodi, dadesEspecifiques);
        peticioSincrona.getDadesComunes().setConsentiment(null);

        try {
            PeticioRespostaSincrona respuesta = clientRecobriment.peticioSincrona(serveiCodi, peticioSincrona);
            assertNotNull(respuesta);
            assertTrue("La resposta indica que no s'ha produit cap error en l'enviament", respuesta.isError());
            assertNotNull("No s'ha produït error en les dades comunes", respuesta.getErrorsValidacio().get("dadesComunes.consentiment"));
            assertTrue(
                    "No s'ha produït error en les dades comunes",
                    respuesta.getErrorsValidacio()
                            .get("dadesComunes.consentiment")
                            .contains("No s'ha trobat l'element dadesComunes.consentiment")
            );
            System.out.println("-> peticionSincrona = " + objectToJsonString(respuesta));
        } catch (Exception e) {
            fail("Excepció no esperada: " + e.getMessage());
        }
    }

    @Test
    public void peticionSincrona_MissingFuncionari() throws UniformInterfaceException, ClientHandlerException, IOException {
        String entitatCif = EXISTING_ENTITAT_CIF;
        String procedimentCodi = EXISTING_PROCEDIMENT_CODI;
        String serveiCodi = EXISTING_SERVEI_CODI;

        Map<String, String> dadesEspecifiques = new HashMap<>();
        dadesEspecifiques.put("DatosEspecificos/Solicitud/ProvinciaSolicitud", "07");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/MunicipioSolicitud", "033");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/Titular/Documentacion/Tipo", "DNI");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/Titular/Documentacion/Valor", "18225486x");

        PeticioSincrona peticioSincrona = getPeticioSincrona(entitatCif, procedimentCodi, serveiCodi, dadesEspecifiques);
        peticioSincrona.getDadesComunes().setFuncionari(null);

        try {
            PeticioRespostaSincrona respuesta = clientRecobriment.peticioSincrona(serveiCodi, peticioSincrona);
            assertNotNull(respuesta);
            assertTrue("La resposta indica que no s'ha produit cap error en l'enviament", respuesta.isError());
            assertNotNull("No s'ha produït error en les dades comunes", respuesta.getErrorsValidacio().get("dadesComunes.funcionari"));
            assertTrue(
                    "No s'ha produït error en les dades comunes",
                    respuesta.getErrorsValidacio()
                            .get("dadesComunes.funcionari")
                            .contains("No s'ha trobat l'element dadesComunes.funcionari"));
            System.out.println("-> peticionSincrona = " + objectToJsonString(respuesta));
        } catch (Exception e) {
            fail("Excepció no esperada: " + e.getMessage());
        }
    }

    @Test
    public void peticionSincrona_InvalidFuncionari() throws UniformInterfaceException, ClientHandlerException, IOException {
        String entitatCif = EXISTING_ENTITAT_CIF;
        String procedimentCodi = EXISTING_PROCEDIMENT_CODI;
        String serveiCodi = EXISTING_SERVEI_CODI;

        Map<String, String> dadesEspecifiques = new HashMap<>();
        dadesEspecifiques.put("DatosEspecificos/Solicitud/ProvinciaSolicitud", "07");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/MunicipioSolicitud", "033");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/Titular/Documentacion/Tipo", "DNI");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/Titular/Documentacion/Valor", "18225486x");

        PeticioSincrona peticioSincrona = getPeticioSincrona(entitatCif, procedimentCodi, serveiCodi, dadesEspecifiques);
        peticioSincrona.getDadesComunes().getFuncionari().setCodi("iiiiiiiiiiiiiiiiiiii");

        try {
            PeticioRespostaSincrona respuesta = clientRecobriment.peticioSincrona(serveiCodi, peticioSincrona);
            assertNotNull(respuesta);
            assertTrue("La resposta indica que no s'ha produit cap error en l'enviament", respuesta.isError());
            assertNotNull("No s'ha produït error en les dades comunes", respuesta.getErrorsValidacio().get("dadesComunes.funcionari.codi"));
            assertTrue(
                    "No s'ha produït error en les dades comunes",
                    respuesta.getErrorsValidacio()
                            .get("dadesComunes.funcionari.codi")
                            .get(0)
                            .contains("Camp massa llarg."));
            System.out.println("-> peticionSincrona = " + objectToJsonString(respuesta));
        } catch (Exception e) {
            fail("Excepció no esperada: " + e.getMessage());
        }
    }

    @Test
    public void peticionSincrona_MissingDepartament() throws UniformInterfaceException, ClientHandlerException, IOException {
        String entitatCif = EXISTING_ENTITAT_CIF;
        String procedimentCodi = EXISTING_PROCEDIMENT_CODI;
        String serveiCodi = EXISTING_SERVEI_CODI;

        Map<String, String> dadesEspecifiques = new HashMap<>();
        dadesEspecifiques.put("DatosEspecificos/Solicitud/ProvinciaSolicitud", "07");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/MunicipioSolicitud", "033");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/Titular/Documentacion/Tipo", "DNI");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/Titular/Documentacion/Valor", "18225486x");

        PeticioSincrona peticioSincrona = getPeticioSincrona(entitatCif, procedimentCodi, serveiCodi, dadesEspecifiques);
        peticioSincrona.getDadesComunes().setDepartament(null);

        try {
            PeticioRespostaSincrona respuesta = clientRecobriment.peticioSincrona(serveiCodi, peticioSincrona);
            assertNotNull(respuesta);
            assertTrue("La resposta indica que no s'ha produit cap error en l'enviament", respuesta.isError());
            assertNotNull("No s'ha produït error en les dades comunes", respuesta.getErrorsValidacio().get("dadesComunes.departament"));
            assertTrue(
                    "No s'ha produït error en les dades comunes",
                    respuesta.getErrorsValidacio()
                            .get("dadesComunes.departament")
                            .contains("No s'ha trobat l'element dadesComunes.departament")
            );
            System.out.println("-> peticionSincrona = " + objectToJsonString(respuesta));
        } catch (Exception e) {
            fail("Excepció no esperada: " + e.getMessage());
        }
    }

    @Test
    public void peticionSincrona_DepartamentSize() throws UniformInterfaceException, ClientHandlerException, IOException {
        String entitatCif = EXISTING_ENTITAT_CIF;
        String procedimentCodi = EXISTING_PROCEDIMENT_CODI;
        String serveiCodi = EXISTING_SERVEI_CODI;

        Map<String, String> dadesEspecifiques = new HashMap<>();
        dadesEspecifiques.put("DatosEspecificos/Solicitud/ProvinciaSolicitud", "07");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/MunicipioSolicitud", "033");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/Titular/Documentacion/Tipo", "DNI");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/Titular/Documentacion/Valor", "18225486x");

        PeticioSincrona peticioSincrona = getPeticioSincrona(entitatCif, procedimentCodi, serveiCodi, dadesEspecifiques);
        peticioSincrona.getDadesComunes().setDepartament("iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii" +
                "iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii" +
                "iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii" +
                "iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii" +
                "iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii" +
                "iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii");

        try {
            PeticioRespostaSincrona respuesta = clientRecobriment.peticioSincrona(serveiCodi, peticioSincrona);
            assertNotNull(respuesta);
            assertTrue("La resposta indica que no s'ha produit cap error en l'enviament", respuesta.isError());
            assertNotNull("No s'ha produït error en les dades comunes", respuesta.getErrorsValidacio().get("dadesComunes.departament"));
            assertTrue(
                    "No s'ha produït error en les dades comunes",
                    respuesta.getErrorsValidacio()
                            .get("dadesComunes.departament")
                            .get(0)
                            .contains("Camp massa llarg.")
            );
            System.out.println("-> peticionSincrona = " + objectToJsonString(respuesta));
        } catch (Exception e) {
            fail("Excepció no esperada: " + e.getMessage());
        }
    }

    @Test
    public void peticionSincrona_MissingFinalitat() throws UniformInterfaceException, ClientHandlerException, IOException {
        String entitatCif = EXISTING_ENTITAT_CIF;
        String procedimentCodi = EXISTING_PROCEDIMENT_CODI;
        String serveiCodi = EXISTING_SERVEI_CODI;

        Map<String, String> dadesEspecifiques = new HashMap<>();
        dadesEspecifiques.put("DatosEspecificos/Solicitud/ProvinciaSolicitud", "07");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/MunicipioSolicitud", "033");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/Titular/Documentacion/Tipo", "DNI");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/Titular/Documentacion/Valor", "18225486x");

        PeticioSincrona peticioSincrona = getPeticioSincrona(entitatCif, procedimentCodi, serveiCodi, dadesEspecifiques);
        peticioSincrona.getDadesComunes().setFinalitat(null);

        try {
            PeticioRespostaSincrona respuesta = clientRecobriment.peticioSincrona(serveiCodi, peticioSincrona);
            assertNotNull(respuesta);
            assertTrue("La resposta indica que no s'ha produit cap error en l'enviament", respuesta.isError());
            assertNotNull("No s'ha produït error en les dades comunes", respuesta.getErrorsValidacio().get("dadesComunes.finalitat"));
            assertTrue(
                    "No s'ha produït error en les dades comunes",
                    respuesta.getErrorsValidacio()
                            .get("dadesComunes.finalitat")
                            .contains("No s'ha trobat l'element dadesComunes.finalitat")
            );
            System.out.println("-> peticionSincrona = " + objectToJsonString(respuesta));
        } catch (Exception e) {
            fail("Excepció no esperada: " + e.getMessage());
        }
    }

    @Test
    public void peticionSincrona_FinalitatSize() throws UniformInterfaceException, ClientHandlerException, IOException {
        String entitatCif = EXISTING_ENTITAT_CIF;
        String procedimentCodi = EXISTING_PROCEDIMENT_CODI;
        String serveiCodi = EXISTING_SERVEI_CODI;

        Map<String, String> dadesEspecifiques = new HashMap<>();
        dadesEspecifiques.put("DatosEspecificos/Solicitud/ProvinciaSolicitud", "07");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/MunicipioSolicitud", "033");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/Titular/Documentacion/Tipo", "DNI");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/Titular/Documentacion/Valor", "18225486x");

        PeticioSincrona peticioSincrona = getPeticioSincrona(entitatCif, procedimentCodi, serveiCodi, dadesEspecifiques);
        peticioSincrona.getDadesComunes().setFinalitat("iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii" +
                "iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii" +
                "iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii" +
                "iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii" +
                "iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii" +
                "iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii");

        try {
            PeticioRespostaSincrona respuesta = clientRecobriment.peticioSincrona(serveiCodi, peticioSincrona);
            assertNotNull(respuesta);
            assertTrue("La resposta indica que no s'ha produit cap error en l'enviament", respuesta.isError());
            assertNotNull("No s'ha produït error en les dades comunes", respuesta.getErrorsValidacio().get("dadesComunes.finalitat"));
            assertTrue(
                    "No s'ha produït error en les dades comunes",
                    respuesta.getErrorsValidacio()
                            .get("dadesComunes.finalitat")
                            .get(0)
                            .contains("Camp massa llarg.")
            );
            System.out.println("-> peticionSincrona = " + objectToJsonString(respuesta));
        } catch (Exception e) {
            fail("Excepció no esperada: " + e.getMessage());
        }
    }

    @Test
    public void peticionSincrona_NullSolicitud() throws UniformInterfaceException, ClientHandlerException, IOException {
        String entitatCif = EXISTING_ENTITAT_CIF;
        String procedimentCodi = EXISTING_PROCEDIMENT_CODI;
        String serveiCodi = EXISTING_SERVEI_CODI;

        Map<String, String> dadesEspecifiques = new HashMap<>();
        dadesEspecifiques.put("DatosEspecificos/Solicitud/ProvinciaSolicitud", "07");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/MunicipioSolicitud", "033");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/Titular/Documentacion/Tipo", "DNI");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/Titular/Documentacion/Valor", "18225486x");

        PeticioSincrona peticioSincrona = getPeticioSincrona(entitatCif, procedimentCodi, serveiCodi, dadesEspecifiques);
        peticioSincrona.setSolicitud(null);

        try {
            PeticioRespostaSincrona respuesta = clientRecobriment.peticioSincrona(serveiCodi, peticioSincrona);
            assertNotNull(respuesta);
            assertTrue("La resposta indica que no s'ha produit cap error en l'enviament", respuesta.isError());
            assertNotNull("No s'ha produït error en les dades comunes", respuesta.getErrorsValidacio().get("solicitud"));
            assertTrue(
                    "No s'ha produït error en la solicitud",
                    respuesta.getErrorsValidacio()
                            .get("solicitud")
                            .get(0)
                            .contains("No s'ha trobat l'element solicitud")
            );
            System.out.println("-> peticionSincrona = " + objectToJsonString(respuesta));
        } catch (Exception e) {
            fail("Excepció no esperada: " + e.getMessage());
        }
    }

    @Test
    public void peticionSincrona_InvalidSolicitud() throws UniformInterfaceException, ClientHandlerException, IOException {
        String entitatCif = EXISTING_ENTITAT_CIF;
        String procedimentCodi = EXISTING_PROCEDIMENT_CODI;
        String serveiCodi = EXISTING_SERVEI_CODI;

        Map<String, String> dadesEspecifiques = new HashMap<>();
        dadesEspecifiques.put("DatosEspecificos/Solicitud/ProvinciaSolicitud", "07");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/MunicipioSolicitud", "033");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/Titular/Documentacion/Tipo", "DNI");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/Titular/Documentacion/Valor", "18225486x");

        PeticioSincrona peticioSincrona = getPeticioSincrona(entitatCif, procedimentCodi, serveiCodi, dadesEspecifiques);
        peticioSincrona.getSolicitud().setId("iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii" +
                "iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii");

        try {
            PeticioRespostaSincrona respuesta = clientRecobriment.peticioSincrona(serveiCodi, peticioSincrona);
            assertNotNull(respuesta);
            assertTrue("La resposta indica que no s'ha produit cap error en l'enviament", respuesta.isError());
            assertNotNull("No s'ha produït error en les dades comunes", respuesta.getErrorsValidacio().get("solicitud.id"));
            assertTrue(
                    "No s'ha produït error en la solicitud",
                    respuesta.getErrorsValidacio()
                            .get("solicitud.id")
                            .get(0)
                            .contains("Camp massa llarg."));
            System.out.println("-> peticionSincrona = " + objectToJsonString(respuesta));
        } catch (Exception e) {
            fail("Excepció no esperada: " + e.getMessage());
        }
    }

    @Test
    public void peticionSincrona_NullTitular() throws UniformInterfaceException, ClientHandlerException, IOException {
        String entitatCif = EXISTING_ENTITAT_CIF;
        String procedimentCodi = EXISTING_PROCEDIMENT_CODI;
        String serveiCodi = EXISTING_SERVEI_CODI;

        Map<String, String> dadesEspecifiques = new HashMap<>();
        dadesEspecifiques.put("DatosEspecificos/Solicitud/ProvinciaSolicitud", "07");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/MunicipioSolicitud", "033");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/Titular/Documentacion/Tipo", "DNI");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/Titular/Documentacion/Valor", "18225486x");

        PeticioSincrona peticioSincrona = getPeticioSincrona(entitatCif, procedimentCodi, serveiCodi, dadesEspecifiques);
        peticioSincrona.getSolicitud().setTitular(null);

        try {
            PeticioRespostaSincrona respuesta = clientRecobriment.peticioSincrona(serveiCodi, peticioSincrona);
            assertNotNull(respuesta);
            assertTrue("La resposta indica que no s'ha produit cap error en l'enviament", respuesta.isError());
            assertNotNull("No s'ha produït error en la solicitud", respuesta.getErrorsValidacio().get("solicitud.titular"));
            assertTrue(
                    "No s'ha produït error en la solicitud",
                    respuesta.getErrorsValidacio()
                            .get("solicitud.titular")
                            .contains("No s'ha trobat l'element solicitud.titular"));
            System.out.println("-> peticionSincrona = " + objectToJsonString(respuesta));
        } catch (Exception e) {
            fail("Excepció no esperada: " + e.getMessage());
        }
    }

    @Test
    public void peticionSincrona_InvalidTitular() throws UniformInterfaceException, ClientHandlerException, IOException {
        String entitatCif = EXISTING_ENTITAT_CIF;
        String procedimentCodi = EXISTING_PROCEDIMENT_CODI;
        String serveiCodi = EXISTING_SERVEI_CODI;

        Map<String, String> dadesEspecifiques = new HashMap<>();
        dadesEspecifiques.put("DatosEspecificos/Solicitud/ProvinciaSolicitud", "07");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/MunicipioSolicitud", "033");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/Titular/Documentacion/Tipo", "DNI");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/Titular/Documentacion/Valor", "18225486x");

        PeticioSincrona peticioSincrona = getPeticioSincrona(entitatCif, procedimentCodi, serveiCodi, dadesEspecifiques);
        peticioSincrona.getSolicitud().getTitular().setDocumentNumero("INVALID_DNI");

        try {
            PeticioRespostaSincrona respuesta = clientRecobriment.peticioSincrona(serveiCodi, peticioSincrona);
            assertNotNull(respuesta);
            assertTrue("La resposta indica que no s'ha produit cap error en l'enviament", respuesta.isError());
            assertNotNull("No s'ha produït error en la solicitud", respuesta.getErrorsValidacio().get("solicitud.titular.documentNumero"));
            assertTrue(
                    "No s'ha produït error en la solicitud",
                    respuesta.getErrorsValidacio()
                            .get("solicitud.titular.documentNumero")
                            .contains("El valor de l'element dadesComunes.titular.documentTipus no és un DNI vàlid"));
            System.out.println("-> peticionSincrona = " + objectToJsonString(respuesta));
        } catch (Exception e) {
            fail("Excepció no esperada: " + e.getMessage());
        }
    }

    @Test
    public void peticionSincrona_InvalidCampDadesEspecifiques() throws UniformInterfaceException, ClientHandlerException, IOException {
        String entitatCif = EXISTING_ENTITAT_CIF;
        String procedimentCodi = EXISTING_PROCEDIMENT_CODI;
        String serveiCodi = EXISTING_SERVEI_CODI;

        Map<String, String> dadesEspecifiques = new HashMap<>();
        dadesEspecifiques.put("DatosEspecificos/Solicitud/ProvinciaSolicitud", "07");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/MunicipioSolicitud", "033");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/Titular/Documentacion/Tipo", "DNI");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/Titular/Documentacion/Valor", "18225486x");
        dadesEspecifiques.put("Invalid/Path", "xxxxx");

        PeticioSincrona peticioSincrona = getPeticioSincrona(entitatCif, procedimentCodi, serveiCodi, dadesEspecifiques);

        try {
            PeticioRespostaSincrona respuesta = clientRecobriment.peticioSincrona(serveiCodi, peticioSincrona);
            assertNotNull(respuesta);
            assertTrue("La resposta indica que no s'ha produit cap error en l'enviament", respuesta.isError());
            assertNotNull("No s'ha produït error en les dades especifiques", respuesta.getErrorsValidacio().get("solicitud.dadesEspecifiques"));
            assertTrue(
                    "No s'ha produït error en les dades especifiques",
                    respuesta.getErrorsValidacio()
                            .get("solicitud.dadesEspecifiques")
                            .contains("Els següents camps no estan definits al servei: Invalid/Path")
            );
            System.out.println("-> peticionSincrona = " + objectToJsonString(respuesta));
        } catch (Exception e) {
            fail("Excepció no esperada: " + e.getMessage());
        }
    }

    @Test
    public void peticionSincrona_InvalidCampDadesEspecifiquesObligatori() throws UniformInterfaceException, ClientHandlerException, IOException {
        String entitatCif = EXISTING_ENTITAT_CIF;
        String procedimentCodi = EXISTING_PROCEDIMENT_CODI;
        String serveiCodi = EXISTING_SERVEI_CODI;

        Map<String, String> dadesEspecifiques = new HashMap<>();
//        dadesEspecifiques.put("DatosEspecificos/Solicitud/ProvinciaSolicitud", "07");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/MunicipioSolicitud", "033");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/Titular/Documentacion/Tipo", "DNI");
        dadesEspecifiques.put("DatosEspecificos/Solicitud/Titular/Documentacion/Valor", "18225486x");

        PeticioSincrona peticioSincrona = getPeticioSincrona(entitatCif, procedimentCodi, serveiCodi, dadesEspecifiques);

        try {
            PeticioRespostaSincrona respuesta = clientRecobriment.peticioSincrona(serveiCodi, peticioSincrona);
            assertNotNull(respuesta);
            assertTrue("La resposta indica que no s'ha produit cap error en l'enviament", respuesta.isError());
            assertNotNull(
                    "No s'ha produït error en les dades especifiques",
                    respuesta.getErrorsValidacio()
                            .get("solicitud.dadesEspecifiques[DatosEspecificos/Solicitud/ProvinciaSolicitud]")
            );
            assertTrue(
                    "No s'ha produït error en les dades especifiques",
                    respuesta.getErrorsValidacio()
                            .get("solicitud.dadesEspecifiques[DatosEspecificos/Solicitud/ProvinciaSolicitud]")
                            .contains("Aquest camp és obligatori")
            );
            System.out.println("-> peticionSincrona = " + objectToJsonString(respuesta));
        } catch (Exception e) {
            fail("Excepció no esperada: " + e.getMessage());
        }
    }

//    @Test
//    public void peticionSincrona_InvalidValorDadesEspecifiques() throws UniformInterfaceException, ClientHandlerException, IOException {
//        String entitatCif = EXISTING_ENTITAT_CIF;
//        String procedimentCodi = EXISTING_PROCEDIMENT_CODI;
//        String serveiCodi = EXISTING_SERVEI_CODI;
//
//        Map<String, String> dadesEspecifiques = new HashMap<>();
//        dadesEspecifiques.put("DatosEspecificos/Solicitud/ProvinciaSolicitud", "07");
//        dadesEspecifiques.put("DatosEspecificos/Solicitud/MunicipioSolicitud", "033");
//        dadesEspecifiques.put("DatosEspecificos/Solicitud/Titular/Documentacion/Tipo", "INVALID_VALUE");
//        dadesEspecifiques.put("DatosEspecificos/Solicitud/Titular/Documentacion/Valor", "18225486x");
//
//        PeticioSincrona peticioSincrona = getPeticioSincrona(entitatCif, procedimentCodi, serveiCodi, dadesEspecifiques);
//
//        try {
//            PeticioRespostaSincrona respuesta = clientRecobriment.peticioSincrona(serveiCodi, peticioSincrona);
//            assertNotNull(respuesta);
//            assertTrue("La resposta indica que no s'ha produit cap error en l'enviament", respuesta.isError());
//            assertNotNull(
//                    "No s'ha produït error en les dades especifiques",
//                    respuesta.getErrorsValidacio()
//                            .get("solicitud.dadesEspecifiques[DatosEspecificos/Solicitud/Titular/Documentacion/Tipo]")
//            );
//            assertTrue(
//                    "No s'ha produït error en les dades especifiques",
//                    respuesta.getErrorsValidacio()
//                            .get("solicitud.dadesEspecifiques[DatosEspecificos/Solicitud/Titular/Documentacion/Tipo]")
//                            .contains("Aquest camp és obligatori")
//            );
//            System.out.println("-> peticionSincrona = " + objectToJsonString(respuesta));
//        } catch (Exception e) {
//            fail("Excepció no esperada: " + e.getMessage());
//        }
//    }



    private static PeticioSincrona getPeticioSincrona(
            String entitatCif,
            String procedimentCodi,
            String serveiCodi,
            Map<String, String> dadesEspecifiques) {

        PeticioSincrona peticio = PeticioSincrona.builder()
                .dadesComunes(DadesComunes.builder()
                        .entitatCif(entitatCif)
                        .procedimentCodi(procedimentCodi)
                        .serveiCodi(serveiCodi)
                        .funcionari(Funcionari.builder()
                                .codi(FUNCIONARI_CODI)
                                .nif(FUNCIONARI_NIF)
                                .nom(FUNCIONARI_NOM)
                                .build())
                        .departament("Departament de test")
                        .consentiment(DadesComunes.Consentiment.Si)
                        .finalitat("Test peticionSincrona")
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
        return peticio;
    }

    private static String objectToJsonString(Object obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        return mapper.writeValueAsString(obj);
    }

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