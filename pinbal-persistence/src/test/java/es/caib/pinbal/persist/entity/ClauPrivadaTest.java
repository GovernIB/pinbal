package es.caib.pinbal.persist.entity;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClauPrivadaTest {

    private OrganismeCessionari crearOrganisme() {
        return OrganismeCessionari.getBuilder(
                "Organisme Test", "Q1234567A", null, new Date(), Boolean.FALSE, null, new ArrayList<>()).build();
    }

    @Test
    public void testGetBuilder() {
        OrganismeCessionari organisme = crearOrganisme();
        Date dataAlta = new Date(1000L);

        ClauPrivada clauPrivada = ClauPrivada.getBuilder(
                "ALIES1", "Clau privada 1", "secret123", "NS12345", null, dataAlta, true, organisme, false).build();

        assertEquals("ALIES1", clauPrivada.getAlies());
        assertEquals("Clau privada 1", clauPrivada.getNom());
        assertEquals("secret123", clauPrivada.getPassword());
        assertEquals("NS12345", clauPrivada.getNumSerie());
        assertNull(clauPrivada.getDataBaixa());
        assertEquals(dataAlta, clauPrivada.getDataAlta());
        assertTrue(clauPrivada.isInteroperabilitat());
        assertEquals(organisme, clauPrivada.getOrganisme());
        assertFalse(clauPrivada.isPerEntitat());
        assertNull(clauPrivada.getId());
    }

    @Test
    public void testSetPerEntitat() {
        ClauPrivada clauPrivada = ClauPrivada.getBuilder(
                "ALIES1", "Clau privada 1", "secret123", "NS12345", null, new Date(), true, crearOrganisme(), false).build();

        clauPrivada.setPerEntitat(true);

        assertTrue(clauPrivada.isPerEntitat());
    }

    @Test
    public void testIsCaducada_ambDataPassada() {
        ClauPrivada clauPrivada = ClauPrivada.getBuilder(
                "ALIES1", "Clau privada 1", "secret123", "NS12345", new Date(1000L), new Date(), true, crearOrganisme(), false).build();

        assertTrue(clauPrivada.isCaducada());
    }

    @Test
    public void testIsCaducada_ambDataFutura() {
        ClauPrivada clauPrivada = ClauPrivada.getBuilder(
                "ALIES1", "Clau privada 1", "secret123", "NS12345", new Date(System.currentTimeMillis() + 1000000L),
                new Date(), true, crearOrganisme(), false).build();

        assertFalse(clauPrivada.isCaducada());
    }

    @Test
    public void testIsCaducada_senseData() {
        ClauPrivada clauPrivada = ClauPrivada.getBuilder(
                "ALIES1", "Clau privada 1", "secret123", "NS12345", null, new Date(), true, crearOrganisme(), false).build();

        assertFalse(clauPrivada.isCaducada());
    }

    @Test
    public void testUpdate() {
        ClauPrivada clauPrivada = ClauPrivada.getBuilder(
                "ALIES1", "Clau privada 1", "secret123", "NS12345", null, new Date(), true, crearOrganisme(), false).build();
        OrganismeCessionari nouOrganisme = crearOrganisme();
        Date novaDataBaixa = new Date(2000L);
        Date novaDataAlta = new Date(3000L);

        clauPrivada.update("ALIES2", "Clau privada 2", "altrasecret", "NS54321", novaDataBaixa, novaDataAlta, false, nouOrganisme, true);

        assertEquals("ALIES2", clauPrivada.getAlies());
        assertEquals("Clau privada 2", clauPrivada.getNom());
        assertEquals("altrasecret", clauPrivada.getPassword());
        assertEquals("NS54321", clauPrivada.getNumSerie());
        assertEquals(novaDataBaixa, clauPrivada.getDataBaixa());
        assertEquals(novaDataAlta, clauPrivada.getDataAlta());
        assertFalse(clauPrivada.isInteroperabilitat());
        assertEquals(nouOrganisme, clauPrivada.getOrganisme());
        assertTrue(clauPrivada.isPerEntitat());
    }

    @Test
    public void testEqualsAndHashCode() {
        // id no té setter (es genera per JPA), de manera que dos objectes acabats de
        // construir sempre tenen id null i, per tant, es consideren iguals entre ells
        ClauPrivada clauPrivada1 = ClauPrivada.getBuilder(
                "ALIES1", "Clau privada 1", "secret123", "NS1", null, new Date(), true, crearOrganisme(), false).build();
        ClauPrivada clauPrivada2 = ClauPrivada.getBuilder(
                "ALIES2", "Clau privada 2", "altrasecret", "NS2", null, new Date(), false, crearOrganisme(), true).build();

        assertEquals(clauPrivada1, clauPrivada2);
        assertEquals(clauPrivada1.hashCode(), clauPrivada2.hashCode());
        assertNotEquals(clauPrivada1, null);
        assertNotEquals(clauPrivada1, new Object());
        assertEquals(clauPrivada1, clauPrivada1);
    }

    @Test
    public void testToString() {
        ClauPrivada clauPrivada = ClauPrivada.getBuilder(
                "ALIES1", "Clau privada 1", "secret123", "NS12345", null, new Date(), true, crearOrganisme(), false).build();

        assertNotNull(clauPrivada.toString());
    }
}
