package es.caib.pinbal.persist.entity;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrganismeCessionariTest {

    private List<ClauPrivada> crearClaus() {
        List<ClauPrivada> claus = new ArrayList<>();
        claus.add(ClauPrivada.getBuilder(
                "ALIES1", "Clau privada 1", "secret123", "NS12345", null, new Date(), true, null, false).build());
        return claus;
    }

    @Test
    public void testGetBuilder() {
        Date dataBaixa = new Date(1000L);
        Date dataAlta = new Date(2000L);
        byte[] logo = new byte[] { 1, 2, 3 };
        List<ClauPrivada> claus = crearClaus();

        OrganismeCessionari organisme = OrganismeCessionari.getBuilder(
                "Organisme 1", "Q1234567A", dataBaixa, dataAlta, Boolean.FALSE, logo, claus).build();

        assertEquals("Organisme 1", organisme.getNom());
        assertEquals("Q1234567A", organisme.getCif());
        assertEquals(dataBaixa, organisme.getDataBaixa());
        assertEquals(dataAlta, organisme.getDataAlta());
        assertEquals(Boolean.FALSE, organisme.getBloquejat());
        assertEquals(logo, organisme.getLogo());
        assertEquals(claus, organisme.getClaus());
        assertNull(organisme.getId());
    }

    @Test
    public void testSetters() {
        OrganismeCessionari organisme = OrganismeCessionari.getBuilder(
                "Organisme 1", "Q1234567A", null, new Date(), Boolean.FALSE, null, new ArrayList<>()).build();

        organisme.setId(5L);
        organisme.setNom("Nou nom");
        organisme.setCif("Q7654321B");
        organisme.setBloquejat(Boolean.TRUE);

        assertEquals(5L, organisme.getId());
        assertEquals("Nou nom", organisme.getNom());
        assertEquals("Q7654321B", organisme.getCif());
        assertTrue(organisme.getBloquejat());
    }

    @Test
    public void testUpdate() {
        OrganismeCessionari organisme = OrganismeCessionari.getBuilder(
                "Organisme 1", "Q1234567A", null, new Date(), Boolean.FALSE, null, new ArrayList<>()).build();
        Date novaDataBaixa = new Date(3000L);
        Date novaDataAlta = new Date(4000L);
        byte[] nouLogo = new byte[] { 4, 5, 6 };
        List<ClauPrivada> novesClaus = crearClaus();

        organisme.update("Organisme 2", "Q7654321B", novaDataBaixa, novaDataAlta, Boolean.TRUE, nouLogo, novesClaus);

        assertEquals("Organisme 2", organisme.getNom());
        assertEquals("Q7654321B", organisme.getCif());
        assertEquals(novaDataBaixa, organisme.getDataBaixa());
        assertEquals(novaDataAlta, organisme.getDataAlta());
        assertTrue(organisme.getBloquejat());
        assertEquals(nouLogo, organisme.getLogo());
        assertEquals(novesClaus, organisme.getClaus());
    }

    @Test
    public void testUpdateEntitat() {
        Date dataAlta = new Date();
        OrganismeCessionari organisme = OrganismeCessionari.getBuilder(
                "Organisme 1", "Q1234567A", null, dataAlta, Boolean.FALSE, null, new ArrayList<>()).build();

        organisme.updateEntitat("Organisme 2", "Q7654321B", Boolean.TRUE);

        assertEquals("Organisme 2", organisme.getNom());
        assertEquals("Q7654321B", organisme.getCif());
        assertTrue(organisme.getBloquejat());
        assertEquals(dataAlta, organisme.getDataAlta());
    }

    @Test
    public void testEqualsAndHashCode() {
        OrganismeCessionari organisme1 = OrganismeCessionari.getBuilder(
                "Organisme 1", "Q1234567A", null, new Date(), Boolean.FALSE, null, new ArrayList<>()).build();
        organisme1.setId(1L);
        OrganismeCessionari organisme2 = OrganismeCessionari.getBuilder(
                "Organisme diferent", "Q0000000Z", null, new Date(), Boolean.TRUE, null, new ArrayList<>()).build();
        organisme2.setId(1L);
        OrganismeCessionari organisme3 = OrganismeCessionari.getBuilder(
                "Organisme 1", "Q1234567A", null, new Date(), Boolean.FALSE, null, new ArrayList<>()).build();
        organisme3.setId(2L);

        assertEquals(organisme1, organisme2);
        assertEquals(organisme1.hashCode(), organisme2.hashCode());
        assertNotEquals(organisme1, organisme3);
        assertNotEquals(organisme1, null);
        assertNotEquals(organisme1, new Object());
        assertEquals(organisme1, organisme1);
    }

    @Test
    public void testToString() {
        OrganismeCessionari organisme = OrganismeCessionari.getBuilder(
                "Organisme 1", "Q1234567A", null, new Date(), Boolean.FALSE, null, new ArrayList<>()).build();

        assertNotNull(organisme.toString());
    }
}
