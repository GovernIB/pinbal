package es.caib.pinbal.persist.entity;

import es.caib.pinbal.logic.intf.dto.ConsultaDto.Consentiment;
import es.caib.pinbal.logic.intf.dto.EstatTipus;
import es.caib.pinbal.logic.intf.dto.JustificantEstat;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HistoricConsultaTest {

    private HistoricConsulta crearHistoricConsulta() {
        Entitat entitat = Entitat.getBuilder("ENT1", "Entitat 1", "Q1234567A", Entitat.EntitatTipus.GOVERN).build();
        Procediment procediment = Procediment.getBuilder(entitat, "PROC1", "Procediment 1", "Departament", null, null, null, null).build();
        ProcedimentServei procedimentServei = ProcedimentServei.getBuilder(procediment, "SERVEI1").build();
        Servei serveiScsp = new Servei();
        serveiScsp.setDescripcio("Descripcio servei");

        HistoricConsulta historic = new HistoricConsulta();
        historic.scspPeticionId = "PET1";
        historic.scspSolicitudId = "SOL1";
        historic.departamentNom = "Departament";
        historic.funcionariNom = "Joan Petit";
        historic.funcionariDocumentNum = "12345678A";
        historic.titularDocumentTipus = "NIF";
        historic.titularDocumentNum = "87654321B";
        historic.titularNom = "Pere";
        historic.titularLlinatge1 = "Gran";
        historic.titularLlinatge2 = "Petit";
        historic.titularNomComplet = "Pere Gran Petit";
        historic.finalitat = "Finalitat";
        historic.consentiment = Consentiment.Si;
        historic.expedientId = "EXP1";
        historic.estat = EstatTipus.Tramitada;
        historic.recobriment = true;
        historic.multiple = false;
        historic.procedimentServei = procedimentServei;
        historic.procediment = procediment;
        historic.serveiCodi = "SERVEI1";
        historic.serveiScsp = serveiScsp;
        historic.entitat = entitat;
        return historic;
    }

    @Test
    public void testCampsBasics() {
        HistoricConsulta historic = crearHistoricConsulta();

        assertEquals("PET1", historic.getScspPeticionId());
        assertEquals("SOL1", historic.getScspSolicitudId());
        assertEquals("Departament", historic.getDepartamentNom());
        assertEquals("Joan Petit", historic.getFuncionariNom());
        assertEquals("12345678A", historic.getFuncionariDocumentNum());
        assertEquals("NIF", historic.getTitularDocumentTipus());
        assertEquals("87654321B", historic.getTitularDocumentNum());
        assertEquals("Pere", historic.getTitularNom());
        assertEquals("Gran", historic.getTitularLlinatge1());
        assertEquals("Petit", historic.getTitularLlinatge2());
        assertEquals("Pere Gran Petit", historic.getTitularNomComplet());
        assertEquals("Finalitat", historic.getFinalitat());
        assertEquals(Consentiment.Si, historic.getConsentiment());
        assertEquals("EXP1", historic.getExpedientId());
        assertEquals(EstatTipus.Tramitada, historic.getEstat());
        assertTrue(historic.isRecobriment());
        assertFalse(historic.isMultiple());
        assertEquals("SERVEI1", historic.getServeiCodi());
        assertEquals("Descripcio servei", historic.getServeiDescriptio());
        assertEquals("PROC1", historic.getProcediment().getCodi());
        assertEquals("ENT1", historic.getEntitat().getCodi());
        assertEquals("Procediment 1", historic.getProcedimentNom());
        assertEquals("Entitat 1", historic.getEntitatNom());
        assertEquals("Q1234567A", historic.getEntitatCif());
        assertNull(historic.getPare());
        assertTrue(historic.getFills().isEmpty());
    }

    @Test
    public void testGetTitularNomSencer_ambNomComplet() {
        HistoricConsulta historic = crearHistoricConsulta();

        assertEquals("Pere Gran Petit", historic.getTitularNomSencer());
    }

    @Test
    public void testGetTitularNomSencer_senseNomComplet() {
        HistoricConsulta historic = crearHistoricConsulta();
        historic.titularNomComplet = null;
        historic.titularLlinatge2 = null;

        assertEquals("Pere Gran", historic.getTitularNomSencer());
    }

    @Test
    public void testUpdateJustificantEstat() {
        HistoricConsulta historic = crearHistoricConsulta();

        historic.updateJustificantEstat(JustificantEstat.OK, true, "CUST1", "http://url", "err", "EXPUUID", "DOCUUID");

        assertEquals(JustificantEstat.OK, historic.getJustificantEstat());
        assertTrue(historic.isCustodiat());
        assertEquals("CUST1", historic.getCustodiaId());
        assertEquals("http://url", historic.getCustodiaUrl());
        assertEquals("err", historic.getJustificantError());
        assertEquals("EXPUUID", historic.getArxiuExpedientUuid());
        assertEquals("DOCUUID", historic.getArxiuDocumentUuid());
    }

    @Test
    public void testUpdateJustificantEstat_errorLlarg() {
        HistoricConsulta historic = crearHistoricConsulta();

        String errorLlarg = "b".repeat(2010);
        historic.updateJustificantEstat(JustificantEstat.ERROR, false, null, null, errorLlarg, null, null);

        assertEquals(1998, historic.getJustificantError().length());
        assertTrue(historic.getJustificantError().endsWith(" [...]"));
    }

    @Test
    public void testUpdateAplicacioGuardaJustificantArxiu() {
        HistoricConsulta historic = crearHistoricConsulta();

        historic.updateAplicacioGuardaJustificantArxiu(true);

        assertTrue(historic.isAplicacioGuardaJustificantArxiu());
    }

    @Test
    public void testUpdateArxiuExpedientUuid() {
        HistoricConsulta historic = crearHistoricConsulta();

        historic.updateArxiuExpedientUuid("EXPUUID2");

        assertEquals("EXPUUID2", historic.getArxiuExpedientUuid());
    }

    @Test
    public void testUpdateArxiuDocumentUuid() {
        HistoricConsulta historic = crearHistoricConsulta();

        historic.updateArxiuDocumentUuid("DOCUUID2");

        assertEquals("DOCUUID2", historic.getArxiuDocumentUuid());
    }

    @Test
    public void testEquals_mateixaReferencia() {
        HistoricConsulta historic = crearHistoricConsulta();

        assertEquals(historic, historic);
    }

    @Test
    public void testEquals_null() {
        HistoricConsulta historic = crearHistoricConsulta();

        assertNotEquals(historic, null);
    }

    @Test
    public void testEquals_altraClasse() {
        HistoricConsulta historic = crearHistoricConsulta();

        assertNotEquals(historic, new Object());
    }

    @Test
    public void testEquals_instanciaDiferentSenseId() {
        // AbstractPersistable basa equals() en l'id; sense id assignat dues instàncies
        // diferents mai són iguals encara que comparteixin scspPeticionId.
        HistoricConsulta historic1 = crearHistoricConsulta();
        HistoricConsulta historic2 = crearHistoricConsulta();

        assertNotEquals(historic1, historic2);
    }

    @Test
    public void testHashCode() {
        HistoricConsulta historic = crearHistoricConsulta();

        assertEquals(historic.hashCode(), historic.hashCode());
    }

    @Test
    public void testToString() {
        HistoricConsulta historic = crearHistoricConsulta();

        assertNotNull(historic.toString());
    }
}
