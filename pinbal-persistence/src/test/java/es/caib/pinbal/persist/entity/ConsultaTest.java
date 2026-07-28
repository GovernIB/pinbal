package es.caib.pinbal.persist.entity;

import es.caib.pinbal.logic.intf.dto.ConsultaDto.Consentiment;
import es.caib.pinbal.logic.intf.dto.EstatTipus;
import es.caib.pinbal.logic.intf.dto.JustificantEstat;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConsultaTest {

    private ProcedimentServei crearProcedimentServei() {
        Entitat entitat = Entitat.getBuilder("ENT1", "Entitat 1", "Q1234567A", Entitat.EntitatTipus.GOVERN).build();
        entitat.configurarIdPerTest(3L);
        Procediment procediment = Procediment.getBuilder(entitat, "PROC1", "Procediment 1", "Departament", null, null, null, null).build();
        procediment.configurarIdPerTest(5L);
        return ProcedimentServei.getBuilder(procediment, "SERVEI1").build();
    }

    private Consulta crearConsulta() {
        return Consulta.getBuilder(
                "PET1",
                "Joan Petit",
                "12345678A",
                "NIF",
                "87654321B",
                "Pere",
                "Gran",
                "Petit",
                "Pere Gran Petit",
                "Departament",
                crearProcedimentServei(),
                "Finalitat",
                Consentiment.Si,
                "EXP1",
                true,
                false,
                null).build();
    }

    @Test
    public void testGetBuilder() {
        Consulta consulta = crearConsulta();

        assertEquals("PET1", consulta.getScspPeticionId());
        assertEquals("PET1", consulta.getScspSolicitudId());
        assertEquals("Joan Petit", consulta.getFuncionariNom());
        assertEquals("12345678A", consulta.getFuncionariDocumentNum());
        assertEquals("NIF", consulta.getTitularDocumentTipus());
        assertEquals("87654321B", consulta.getTitularDocumentNum());
        assertEquals("Pere", consulta.getTitularNom());
        assertEquals("Gran", consulta.getTitularLlinatge1());
        assertEquals("Petit", consulta.getTitularLlinatge2());
        assertEquals("Pere Gran Petit", consulta.getTitularNomComplet());
        assertEquals("Departament", consulta.getDepartamentNom());
        assertNotNull(consulta.getProcedimentServei());
        assertEquals("SERVEI1", consulta.getServeiCodi());
        assertEquals("Finalitat", consulta.getFinalitat());
        assertEquals(Consentiment.Si, consulta.getConsentiment());
        assertEquals("EXP1", consulta.getExpedientId());
        assertTrue(consulta.isRecobriment());
        assertFalse(consulta.isMultiple());
        assertNull(consulta.getPare());
        assertTrue(consulta.getFills().isEmpty());
        assertEquals(EstatTipus.Pendent, consulta.getEstat());
        assertEquals("PROC1", consulta.getProcediment().getCodi());
        assertEquals("ENT1", consulta.getEntitat().getCodi());
        assertEquals("Procediment 1", consulta.getProcedimentNom());
        assertEquals("Entitat 1", consulta.getEntitatNom());
        assertEquals("Q1234567A", consulta.getEntitatCif());
        assertEquals(5L, consulta.getProcedimentId());
        assertEquals(3L, consulta.getEntitatId());
        assertNull(consulta.getTransmision());
        assertNull(consulta.getServeiScsp());
        assertEquals(0L, consulta.getVersion());
    }

    @Test
    public void testGetBuilderAmbPare() {
        Consulta pare = crearConsulta();
        Consulta consulta = Consulta.getBuilder(
                "PET2", "Joan Petit", "12345678A", "NIF", "87654321B",
                "Pere", "Gran", "Petit", "Pere Gran Petit", "Departament",
                crearProcedimentServei(), "Finalitat", Consentiment.Llei, "EXP2",
                false, true, pare).build();

        assertEquals(pare, consulta.getPare());
        assertTrue(consulta.isMultiple());
        assertFalse(consulta.isRecobriment());
        assertEquals(Consentiment.Llei, consulta.getConsentiment());
    }

    @Test
    public void testUpdateEstat() {
        Consulta consulta = crearConsulta();

        consulta.updateEstat(EstatTipus.Tramitada);

        assertEquals(EstatTipus.Tramitada, consulta.getEstat());
    }

    @Test
    public void testUpdateEstatError_curt() {
        Consulta consulta = crearConsulta();

        consulta.updateEstatError("Error petit");

        assertEquals(EstatTipus.Error, consulta.getEstat());
        assertEquals("Error petit", consulta.getError());
    }

    @Test
    public void testUpdateEstatError_llarg() {
        Consulta consulta = crearConsulta();

        String errorLlarg = "a".repeat(4010);
        consulta.updateEstatError(errorLlarg);

        assertEquals(EstatTipus.Error, consulta.getEstat());
        assertEquals(4000, consulta.getError().length());
        assertTrue(consulta.getError().endsWith(" [...]"));
    }

    @Test
    public void testUpdateEstatError_null() {
        Consulta consulta = crearConsulta();

        consulta.updateEstatError(null);

        assertEquals(EstatTipus.Error, consulta.getEstat());
        assertNull(consulta.getError());
    }

    @Test
    public void testUpdateScspSolicitudId() {
        Consulta consulta = crearConsulta();

        consulta.updateScspSolicitudId("SOL1");

        assertEquals("SOL1", consulta.getScspSolicitudId());
    }

    @Test
    public void testUpdateArxiuExpedientTancat() {
        Consulta consulta = crearConsulta();

        consulta.updateArxiuExpedientTancat(true);

        assertTrue(consulta.isArxiuExpedientTancat());
    }

    @Test
    public void testUpdateDadesEspecifiques() {
        Consulta consulta = crearConsulta();

        consulta.updateDadesEspecifiques("<xml/>");

        assertEquals("<xml/>", consulta.getDadesEspecifiques());
    }

    @Test
    public void testUpdateDateEsperadaResposta() {
        Consulta consulta = crearConsulta();
        Date data = new Date();

        consulta.updateDateEsperadaResposta(data);

        assertEquals(data, consulta.getDataEsperadaResposta());
    }

    @Test
    public void testUpdateJustificantEstat() {
        Consulta consulta = crearConsulta();

        consulta.updateJustificantEstat(JustificantEstat.OK, true, "CUST1", "http://url", "err", "EXPUUID", "DOCUUID");

        assertEquals(JustificantEstat.OK, consulta.getJustificantEstat());
        assertTrue(consulta.isCustodiat());
        assertEquals("CUST1", consulta.getCustodiaId());
        assertEquals("http://url", consulta.getCustodiaUrl());
        assertEquals("err", consulta.getJustificantError());
        assertEquals("EXPUUID", consulta.getArxiuExpedientUuid());
        assertEquals("DOCUUID", consulta.getArxiuDocumentUuid());
    }

    @Test
    public void testUpdateJustificantEstat_errorLlarg() {
        Consulta consulta = crearConsulta();

        String errorLlarg = "b".repeat(2010);
        consulta.updateJustificantEstat(JustificantEstat.ERROR, false, null, null, errorLlarg, null, null);

        assertEquals(1998, consulta.getJustificantError().length());
        assertTrue(consulta.getJustificantError().endsWith(" [...]"));
    }

    @Test
    public void testUpdateAplicacioGuardaJustificantArxiu() {
        Consulta consulta = crearConsulta();

        consulta.updateAplicacioGuardaJustificantArxiu(true);

        assertTrue(consulta.isAplicacioGuardaJustificantArxiu());
    }

    @Test
    public void testUpdateArxiuExpedientUuid() {
        Consulta consulta = crearConsulta();

        consulta.updateArxiuExpedientUuid("EXPUUID2");

        assertEquals("EXPUUID2", consulta.getArxiuExpedientUuid());
    }

    @Test
    public void testUpdateArxiuDocumentUuid() {
        Consulta consulta = crearConsulta();

        consulta.updateArxiuDocumentUuid("DOCUUID2");

        assertEquals("DOCUUID2", consulta.getArxiuDocumentUuid());
    }

    @Test
    public void testGetTitularNomSencer_ambNomComplet() {
        Consulta consulta = crearConsulta();

        assertEquals("Pere Gran Petit", consulta.getTitularNomSencer());
    }

    @Test
    public void testGetTitularNomSencer_senseNomComplet() {
        Consulta consulta = Consulta.getBuilder(
                "PET3", "Joan Petit", "12345678A", "NIF", "87654321B",
                "Pere", "Gran", null, null, "Departament",
                crearProcedimentServei(), "Finalitat", Consentiment.Si, "EXP3",
                false, false, null).build();

        assertEquals("Pere Gran", consulta.getTitularNomSencer());
    }

    @Test
    public void testConfigurarIdPerTest() {
        Consulta consulta = crearConsulta();

        consulta.configurarIdPerTest(99L);

        assertEquals(99L, consulta.getId());
    }

    @Test
    public void testEqualsAndHashCode() {
        Consulta consulta1 = crearConsulta();
        consulta1.configurarIdPerTest(1L);
        Consulta consulta2 = crearConsulta();
        consulta2.configurarIdPerTest(1L);
        Consulta consulta3 = crearConsulta();
        consulta3.configurarIdPerTest(2L);

        assertEquals(consulta1, consulta2);
        assertEquals(consulta1.hashCode(), consulta2.hashCode());
        assertNotEquals(consulta1, consulta3);
        assertNotEquals(consulta1, null);
        assertNotEquals(consulta1, new Object());
        assertEquals(consulta1, consulta1);
    }

    @Test
    public void testToString() {
        Consulta consulta = crearConsulta();

        assertNotNull(consulta.toString());
    }
}
