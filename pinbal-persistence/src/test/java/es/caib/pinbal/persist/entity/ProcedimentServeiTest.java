package es.caib.pinbal.persist.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProcedimentServeiTest {

    private Procediment crearProcediment(String codi) {
        Entitat entitat = Entitat.getBuilder("ENT1", "Entitat 1", "Q1234567A", Entitat.EntitatTipus.CONSELL).build();
        return Procediment.getBuilder(entitat, codi, "Procediment " + codi, "Departament", null, "SIA1",
                Boolean.FALSE, null).build();
    }

    @Test
    public void testGetBuilder() {
        Procediment procediment = crearProcediment("PROC1");

        ProcedimentServei procedimentServei = ProcedimentServei.getBuilder(procediment, "SERVEI1").build();

        assertEquals(procediment, procedimentServei.getProcediment());
        assertEquals("SERVEI1", procedimentServei.getServei());
        assertTrue(procedimentServei.isActiu());
        assertNull(procedimentServei.getProcedimentCodi());
        assertNull(procedimentServei.getServeiScsp());
        assertEquals(0, procedimentServei.getVersion());
    }

    @Test
    public void testUpdateProcedimentCodi() {
        ProcedimentServei procedimentServei = ProcedimentServei.getBuilder(crearProcediment("PROC1"), "SERVEI1").build();

        procedimentServei.updateProcedimentCodi("PROC1");

        assertEquals("PROC1", procedimentServei.getProcedimentCodi());
    }

    @Test
    public void testUpdateActiu() {
        ProcedimentServei procedimentServei = ProcedimentServei.getBuilder(crearProcediment("PROC1"), "SERVEI1").build();

        procedimentServei.updateActiu(false);

        assertFalse(procedimentServei.isActiu());
    }

    @Test
    public void testConfigurarIdPerTest() {
        ProcedimentServei procedimentServei = ProcedimentServei.getBuilder(crearProcediment("PROC1"), "SERVEI1").build();

        procedimentServei.configurarIdPerTest(5L);

        assertEquals(5L, procedimentServei.getId());
    }

    @Test
    public void testEqualsAndHashCode() {
        Procediment procediment1 = crearProcediment("PROC1");
        Procediment procediment2 = crearProcediment("PROC2");

        ProcedimentServei ps1 = ProcedimentServei.getBuilder(procediment1, "SERVEI1").build();
        ps1.configurarIdPerTest(1L);

        ProcedimentServei ps2 = ProcedimentServei.getBuilder(procediment1, "SERVEI1").build();
        ps2.configurarIdPerTest(1L);

        ProcedimentServei psServeiDiferent = ProcedimentServei.getBuilder(procediment1, "SERVEI2").build();
        psServeiDiferent.configurarIdPerTest(1L);

        ProcedimentServei psProcedimentDiferent = ProcedimentServei.getBuilder(procediment2, "SERVEI1").build();
        psProcedimentDiferent.configurarIdPerTest(1L);

        ProcedimentServei psIdDiferent = ProcedimentServei.getBuilder(procediment1, "SERVEI1").build();
        psIdDiferent.configurarIdPerTest(2L);

        assertEquals(ps1, ps1);
        assertEquals(ps1, ps2);
        assertEquals(ps1.hashCode(), ps2.hashCode());
        assertNotEquals(ps1, psServeiDiferent);
        assertNotEquals(ps1, psProcedimentDiferent);
        assertNotEquals(ps1, psIdDiferent);
        assertNotEquals(ps1, null);
        assertNotEquals(ps1, new Object());
    }

    @Test
    public void testToString() {
        ProcedimentServei procedimentServei = ProcedimentServei.getBuilder(crearProcediment("PROC1"), "SERVEI1").build();

        assertNotNull(procedimentServei.toString());
    }
}
