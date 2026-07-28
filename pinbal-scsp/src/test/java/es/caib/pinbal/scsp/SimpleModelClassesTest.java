package es.caib.pinbal.scsp;

import es.scsp.bean.common.confirmacion.ConfirmacionPeticion;
import org.junit.Test;
import org.w3c.dom.Element;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests agrupats per a les classes senzilles de model/excepció del mòdul (enums, DTOs amb
 * getters/setters de Lombok o manuals, i l'excepció CrearPeticioScspException).
 */
public class SimpleModelClassesTest {

    // ------------------------- enums -------------------------

    @Test
    public void consentimentTeElsDosValors() {
        assertEquals(2, Consentiment.values().length);
        assertEquals(Consentiment.Si, Consentiment.valueOf("Si"));
        assertEquals(Consentiment.Llei, Consentiment.valueOf("Llei"));
    }

    @Test
    public void documentTipusTeTotsElsValors() {
        DocumentTipus[] valors = DocumentTipus.values();
        assertEquals(5, valors.length);
        assertEquals(DocumentTipus.NIF, DocumentTipus.valueOf("NIF"));
    }

    // ------------------------- CrearPeticioScspException -------------------------

    @Test
    public void crearPeticioScspExceptionAmbMissatge() {
        CrearPeticioScspException ex = new CrearPeticioScspException("error de prova");
        assertEquals("error de prova", ex.getMessage());
        assertNull(ex.getCause());
    }

    @Test
    public void crearPeticioScspExceptionAmbMissatgeICausa() {
        RuntimeException causa = new RuntimeException("causa");
        CrearPeticioScspException ex = new CrearPeticioScspException("error de prova", causa);
        assertEquals("error de prova", ex.getMessage());
        assertEquals(causa, ex.getCause());
    }

    // ------------------------- ResultatEnviamentPeticio -------------------------

    @Test
    public void resultatEnviamentPeticioSenseErrorsNoEsError() {
        ResultatEnviamentPeticio resultat = new ResultatEnviamentPeticio();
        resultat.setEstatCodi("CODI");
        resultat.setEstatDescripcio("DESCRIPCIO");

        assertFalse(resultat.isError());
        assertNull(resultat.getErrorCodi());
        assertNull(resultat.getErrorDescripcio());
    }

    @Test
    public void resultatEnviamentPeticioAmbErrorGeneracioEsError() {
        ResultatEnviamentPeticio resultat = new ResultatEnviamentPeticio();
        resultat.setErrorGeneracio(true);
        resultat.setEstatCodi("CODI");
        resultat.setEstatDescripcio("DESCRIPCIO");

        assertTrue(resultat.isError());
        assertEquals("CODI", resultat.getErrorCodi());
        assertEquals("DESCRIPCIO", resultat.getErrorDescripcio());
    }

    @Test
    public void resultatEnviamentPeticioAmbErrorEnviamentEsError() {
        ResultatEnviamentPeticio resultat = new ResultatEnviamentPeticio();
        resultat.setErrorEnviament(true);
        assertTrue(resultat.isError());
    }

    @Test
    public void resultatEnviamentPeticioAmbErrorRecepcioEsError() {
        ResultatEnviamentPeticio resultat = new ResultatEnviamentPeticio();
        resultat.setErrorRecepcio(true);
        assertTrue(resultat.isError());
    }

    @Test
    public void resultatEnviamentPeticioGettersISetters() {
        ResultatEnviamentPeticio resultat = new ResultatEnviamentPeticio();
        String[] ids = {"id1", "id2"};
        ConfirmacionPeticion confirmacio = new ConfirmacionPeticion();

        resultat.setIdsSolicituds(ids);
        resultat.setConfirmacionPeticion(confirmacio);

        assertEquals(ids, resultat.getIdsSolicituds());
        assertEquals(confirmacio, resultat.getConfirmacionPeticion());
    }

    // ------------------------- Solicitud -------------------------

    @Test
    public void solicitudGettersISetters() {
        Solicitud solicitud = new Solicitud();
        Element element = null;
        Map<String, Object> dades = new HashMap<>();
        dades.put("clau", "valor");

        solicitud.setServeiCodi("SERV1");
        solicitud.setProcedimentCodi("PROC1");
        solicitud.setProcedimentNom("Procediment 1");
        solicitud.setProcedimentValorCampAutomatizado(Boolean.TRUE);
        solicitud.setProcedimentValorCampClaseTramite((short) 3);
        solicitud.setSolicitantIdentificacio("12345678Z");
        solicitud.setSolicitantNom("Solicitant");
        solicitud.setFuncionariNom("Funcionari");
        solicitud.setFuncionariNif("87654321X");
        solicitud.setTitularDocumentTipus(DocumentTipus.DNI);
        solicitud.setTitularDocument("12345678Z");
        solicitud.setTitularNom("Titular");
        solicitud.setTitularLlinatge1("Llinatge1");
        solicitud.setTitularLlinatge2("Llinatge2");
        solicitud.setTitularNomComplet("Titular Llinatge1 Llinatge2");
        solicitud.setFinalitat("Finalitat de prova");
        solicitud.setConsentiment(Consentiment.Si);
        solicitud.setUnitatTramitadora("Unitat");
        solicitud.setUnitatTramitadoraCodi("U1");
        solicitud.setExpedientId("EXP1");
        solicitud.setDadesEspecifiquesElement(element);
        solicitud.setDadesEspecifiquesMap(dades);

        assertEquals("SERV1", solicitud.getServeiCodi());
        assertEquals("PROC1", solicitud.getProcedimentCodi());
        assertEquals(Boolean.TRUE, solicitud.getProcedimentValorCampAutomatizado());
        assertEquals(Short.valueOf((short) 3), solicitud.getProcedimentValorCampClaseTramite());
        assertEquals(DocumentTipus.DNI, solicitud.getTitularDocumentTipus());
        assertEquals(Consentiment.Si, solicitud.getConsentiment());
        assertEquals(dades, solicitud.getDadesEspecifiquesMap());
    }

    // ------------------------- Resposta -------------------------

    @Test
    public void respostaGettersISetters() {
        Resposta resposta = new Resposta();
        Date data = new Date();
        ResultatEnviamentPeticio resultat = new ResultatEnviamentPeticio();

        resposta.setFuncionariNom("Funcionari");
        resposta.setFuncionariNif("12345678Z");
        resposta.setConsentiment(Consentiment.Llei);
        resposta.setExpedientId("EXP1");
        resposta.setFinalitat("Finalitat");
        resposta.setUnitatTramitadora("Unitat");
        resposta.setUnitatTramitadoraCodi("U1");
        resposta.setRespostaData(data);
        resposta.setPeticioXml("<peticio/>");
        resposta.setRespostaXml("<resposta/>");
        resposta.setResultatEnviament(resultat);

        assertEquals("Funcionari", resposta.getFuncionariNom());
        assertEquals("12345678Z", resposta.getFuncionariNif());
        assertEquals(Consentiment.Llei, resposta.getConsentiment());
        assertEquals("EXP1", resposta.getExpedientId());
        assertEquals("Finalitat", resposta.getFinalitat());
        assertEquals("Unitat", resposta.getUnitatTramitadora());
        assertEquals("U1", resposta.getUnitatTramitadoraCodi());
        assertEquals(data, resposta.getRespostaData());
        assertEquals("<peticio/>", resposta.getPeticioXml());
        assertEquals("<resposta/>", resposta.getRespostaXml());
        assertEquals(resultat, resposta.getResultatEnviament());
    }
}
