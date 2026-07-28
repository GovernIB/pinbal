package es.caib.pinbal.persist.entity;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ClauPublicaTest {

    @Test
    public void testGetBuilder() {
        Date dataAlta = new Date(1000L);
        Date dataBaixa = new Date(2000L);

        ClauPublica clauPublica = ClauPublica.getBuilder("ALIES1", "Clau publica 1", "NS12345", dataAlta, dataBaixa).build();

        assertEquals("ALIES1", clauPublica.getAlies());
        assertEquals("Clau publica 1", clauPublica.getNom());
        assertEquals("NS12345", clauPublica.getNumSerie());
        assertEquals(dataAlta, clauPublica.getDataAlta());
        assertEquals(dataBaixa, clauPublica.getDataBaixa());
        assertNull(clauPublica.getId());
    }

    @Test
    public void testSetters() {
        ClauPublica clauPublica = ClauPublica.getBuilder("ALIES1", "Clau publica 1", "NS12345", new Date(), new Date()).build();

        clauPublica.setId(5L);
        clauPublica.setNom("Nou nom");
        clauPublica.setAlies("NOU_ALIES");
        clauPublica.setNumSerie("NS99999");
        Date novaDataAlta = new Date(3000L);
        Date novaDataBaixa = new Date(4000L);
        clauPublica.setDataAlta(novaDataAlta);
        clauPublica.setDataBaixa(novaDataBaixa);

        assertEquals(5L, clauPublica.getId());
        assertEquals("Nou nom", clauPublica.getNom());
        assertEquals("NOU_ALIES", clauPublica.getAlies());
        assertEquals("NS99999", clauPublica.getNumSerie());
        assertEquals(novaDataAlta, clauPublica.getDataAlta());
        assertEquals(novaDataBaixa, clauPublica.getDataBaixa());
    }

    @Test
    public void testUpdate() {
        ClauPublica clauPublica = ClauPublica.getBuilder("ALIES1", "Clau publica 1", "NS12345", new Date(), new Date()).build();

        Date novaDataAlta = new Date(5000L);
        Date novaDataBaixa = new Date(6000L);
        clauPublica.update("ALIES2", "Clau publica 2", "NS54321", novaDataAlta, novaDataBaixa);

        assertEquals("ALIES2", clauPublica.getAlies());
        assertEquals("Clau publica 2", clauPublica.getNom());
        assertEquals("NS54321", clauPublica.getNumSerie());
        assertEquals(novaDataAlta, clauPublica.getDataAlta());
        assertEquals(novaDataBaixa, clauPublica.getDataBaixa());
    }

    @Test
    public void testEqualsAndHashCode() {
        // equals es basa exclusivament en l'id; el hashCode es basa en el nom
        ClauPublica clauPublica1 = ClauPublica.getBuilder("ALIES1", "Nom comu", "NS1", new Date(), new Date()).build();
        clauPublica1.setId(1L);
        ClauPublica clauPublica2 = ClauPublica.getBuilder("ALIES2", "Nom comu", "NS2", new Date(), new Date()).build();
        clauPublica2.setId(1L);
        ClauPublica clauPublica3 = ClauPublica.getBuilder("ALIES3", "Nom comu", "NS3", new Date(), new Date()).build();
        clauPublica3.setId(2L);

        assertEquals(clauPublica1, clauPublica2);
        assertEquals(clauPublica1.hashCode(), clauPublica2.hashCode());
        assertNotEquals(clauPublica1, clauPublica3);
        assertNotEquals(clauPublica1, null);
        assertNotEquals(clauPublica1, new Object());
        assertEquals(clauPublica1, clauPublica1);
    }

    @Test
    public void testToString() {
        ClauPublica clauPublica = ClauPublica.getBuilder("ALIES1", "Clau publica 1", "NS12345", new Date(), new Date()).build();

        assertNotNull(clauPublica.toString());
    }
}
