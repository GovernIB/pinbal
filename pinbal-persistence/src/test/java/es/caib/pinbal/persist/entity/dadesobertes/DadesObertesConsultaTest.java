package es.caib.pinbal.persist.entity.dadesobertes;

import es.caib.pinbal.client.dadesobertes.DadesObertesRespostaConsulta.DadesObertesConsultaResultat;
import es.caib.pinbal.client.dadesobertes.DadesObertesRespostaConsulta.DadesObertesConsultaTipus;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DadesObertesConsultaTest {

    @Test
    public void testBuilderIGetters() {
        Date data = new Date();

        DadesObertesConsulta consulta = DadesObertesConsulta.builder()
                .id(1L)
                .entitatCodi("ENT1")
                .entitatNom("Entitat 1")
                .entitatNif("Q1234567A")
                .entitatTipus("GOVERN")
                .departamentCodi("DEP1")
                .departamentNom("Departament")
                .procedimentCodi("PROC1")
                .procedimentNom("Procediment 1")
                .serveiCodi("SERVEI1")
                .serveiNom("Servei 1")
                .emissorNom("Emissor")
                .emissorNif("87654321B")
                .consentiment("Si")
                .finalitat("Finalitat")
                .titularDocumentTipus("NIF")
                .solicitudId("SOL1")
                .data(data)
                .tipus(DadesObertesConsultaTipus.WEB)
                .resultat(DadesObertesConsultaResultat.PROCES)
                .multiple(true)
                .build();

        assertEquals(1L, consulta.getId());
        assertEquals("ENT1", consulta.getEntitatCodi());
        assertEquals("Entitat 1", consulta.getEntitatNom());
        assertEquals("Q1234567A", consulta.getEntitatNif());
        assertEquals("GOVERN", consulta.getEntitatTipus());
        assertEquals("DEP1", consulta.getDepartamentCodi());
        assertEquals("Departament", consulta.getDepartamentNom());
        assertEquals("PROC1", consulta.getProcedimentCodi());
        assertEquals("Procediment 1", consulta.getProcedimentNom());
        assertEquals("SERVEI1", consulta.getServeiCodi());
        assertEquals("Servei 1", consulta.getServeiNom());
        assertEquals("Emissor", consulta.getEmissorNom());
        assertEquals("87654321B", consulta.getEmissorNif());
        assertEquals("Si", consulta.getConsentiment());
        assertEquals("Finalitat", consulta.getFinalitat());
        assertEquals("NIF", consulta.getTitularDocumentTipus());
        assertEquals("SOL1", consulta.getSolicitudId());
        assertEquals(data, consulta.getData());
        assertEquals(DadesObertesConsultaTipus.WEB, consulta.getTipus());
        assertEquals(DadesObertesConsultaResultat.PROCES, consulta.getResultat());
        assertTrue(consulta.isMultiple());
    }

    @Test
    public void testUpdate() {
        DadesObertesConsulta consulta = new DadesObertesConsulta();

        consulta.update(DadesObertesConsultaResultat.OK, "SOL2");

        assertEquals(DadesObertesConsultaResultat.OK, consulta.getResultat());
        assertEquals("SOL2", consulta.getSolicitudId());
    }

    @Test
    public void testValorsPerDefecte() {
        DadesObertesConsulta consulta = new DadesObertesConsulta();

        assertFalse(consulta.isMultiple());
    }
}
