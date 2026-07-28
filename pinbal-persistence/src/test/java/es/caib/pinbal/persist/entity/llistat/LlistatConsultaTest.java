package es.caib.pinbal.persist.entity.llistat;

import es.caib.pinbal.logic.intf.dto.EstatTipus;
import es.caib.pinbal.logic.intf.dto.JustificantEstat;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LlistatConsultaTest {

    @Test
    public void testBuilderIGetters() {
        Date data = new Date();
        Date der = new Date();

        LlistatConsulta llistat = LlistatConsulta.builder()
                .id(1L)
                .peticioId("PET1")
                .solicitudId("SOL1")
                .data(data)
                .departamentNom("Departament")
                .recobriment(true)
                .multiple(false)
                .usuariCodi("USUARI1")
                .usuariNom("Joan Petit")
                .funcionariNif("12345678A")
                .funcionariNom("Joan Petit")
                .titularNom("Pere Gran")
                .titularDocumentTipus("NIF")
                .titularDocumentNum("87654321B")
                .procedimentId(2L)
                .procedimentCodi("PROC1")
                .procedimentNom("Procediment 1")
                .serveiCodi("SERVEI1")
                .serveiNom("Servei 1")
                .estat(EstatTipus.Tramitada)
                .error("Cap error")
                .justificantEstat(JustificantEstat.OK)
                .entitatId(3L)
                .entitatCodi("ENT1")
                .pareId(4L)
                .dataEsperadaResposta(der)
                .build();

        assertEquals(1L, llistat.getId());
        assertEquals("PET1", llistat.getPeticioId());
        assertEquals("SOL1", llistat.getSolicitudId());
        assertEquals(data, llistat.getData());
        assertEquals("Departament", llistat.getDepartamentNom());
        assertTrue(llistat.isRecobriment());
        assertFalse(llistat.isMultiple());
        assertEquals("USUARI1", llistat.getUsuariCodi());
        assertEquals("Joan Petit", llistat.getUsuariNom());
        assertEquals("12345678A", llistat.getFuncionariNif());
        assertEquals("Joan Petit", llistat.getFuncionariNom());
        assertEquals("Pere Gran", llistat.getTitularNom());
        assertEquals("NIF", llistat.getTitularDocumentTipus());
        assertEquals("87654321B", llistat.getTitularDocumentNum());
        assertEquals(2L, llistat.getProcedimentId());
        assertEquals("PROC1", llistat.getProcedimentCodi());
        assertEquals("Procediment 1", llistat.getProcedimentNom());
        assertEquals("SERVEI1", llistat.getServeiCodi());
        assertEquals("Servei 1", llistat.getServeiNom());
        assertEquals(EstatTipus.Tramitada, llistat.getEstat());
        assertEquals("Cap error", llistat.getError());
        assertEquals(JustificantEstat.OK, llistat.getJustificantEstat());
        assertEquals(3L, llistat.getEntitatId());
        assertEquals("ENT1", llistat.getEntitatCodi());
        assertEquals(4L, llistat.getPareId());
        assertEquals(der, llistat.getDataEsperadaResposta());
    }

    @Test
    public void testUpdate() {
        LlistatConsulta llistat = new LlistatConsulta();
        Date der = new Date();

        llistat.update(EstatTipus.Error, "SOL2", "Error greu", der);

        assertEquals(EstatTipus.Error, llistat.getEstat());
        assertEquals("SOL2", llistat.getSolicitudId());
        assertEquals("Error greu", llistat.getError());
        assertEquals(der, llistat.getDataEsperadaResposta());
    }

    @Test
    public void testValorsPerDefecte() {
        LlistatConsulta llistat = new LlistatConsulta();

        assertFalse(llistat.isRecobriment());
        assertFalse(llistat.isMultiple());
    }
}
