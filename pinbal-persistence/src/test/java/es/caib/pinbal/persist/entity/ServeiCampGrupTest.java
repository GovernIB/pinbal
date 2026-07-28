package es.caib.pinbal.persist.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServeiCampGrupTest {

    @Test
    public void testGetBuilderSensePare() {
        ServeiCampGrup grup = ServeiCampGrup.getBuilder("SERVEI1", null, "Grup arrel", "Ajuda arrel", 1).build();

        assertEquals("SERVEI1", grup.getServei());
        assertEquals("Grup arrel", grup.getNom());
        assertEquals("Ajuda arrel", grup.getAjuda());
        assertEquals(1, grup.getOrdre());
        assertNull(grup.getPare());
        assertEquals(0L, grup.getVersion());
        assertNotNull(grup.getCamps());
        assertTrue(grup.getCamps().isEmpty());
        assertNotNull(grup.getFills());
        assertTrue(grup.getFills().isEmpty());
    }

    @Test
    public void testGetBuilderAmbPare() {
        ServeiCampGrup pare = ServeiCampGrup.getBuilder("SERVEI1", null, "Grup pare", null, 1).build();
        ServeiCampGrup fill = ServeiCampGrup.getBuilder("SERVEI1", pare, "Grup fill", "Ajuda fill", 2).build();

        assertEquals(pare, fill.getPare());
        assertEquals("Grup fill", fill.getNom());
    }

    @Test
    public void testUpdate() {
        ServeiCampGrup grup = ServeiCampGrup.getBuilder("SERVEI1", null, "Grup 1", "Ajuda 1", 1).build();

        grup.update("Grup renombrat", "Ajuda renombrada");

        assertEquals("Grup renombrat", grup.getNom());
        assertEquals("Ajuda renombrada", grup.getAjuda());
    }

    @Test
    public void testUpdateOrdre() {
        ServeiCampGrup grup = ServeiCampGrup.getBuilder("SERVEI1", null, "Grup 1", null, 1).build();

        grup.updateOrdre(5);

        assertEquals(5, grup.getOrdre());
    }

    @Test
    public void testUpdatePare() {
        ServeiCampGrup grup = ServeiCampGrup.getBuilder("SERVEI1", null, "Grup 1", null, 1).build();
        ServeiCampGrup nouPare = ServeiCampGrup.getBuilder("SERVEI1", null, "Grup 2", null, 2).build();

        grup.updatePare(nouPare);

        assertEquals(nouPare, grup.getPare());
    }

    @Test
    public void testCampsIFills() {
        ServeiCampGrup grup = ServeiCampGrup.getBuilder("SERVEI1", null, "Grup 1", null, 1).build();
        ServeiCamp camp = ServeiCamp.getBuilder("SERVEI1", "path.1", 1, 10).build();
        ServeiCampGrup fill = ServeiCampGrup.getBuilder("SERVEI1", grup, "Grup fill", null, 1).build();

        grup.getCamps().add(camp);
        grup.getFills().add(fill);

        assertEquals(1, grup.getCamps().size());
        assertEquals(camp, grup.getCamps().get(0));
        assertEquals(1, grup.getFills().size());
        assertEquals(fill, grup.getFills().get(0));
    }

    @Test
    public void testEqualsAndHashCode() {
        // ServeiCampGrup hereta equals() d'AbstractPersistable, basat en id: com que l'id és
        // null a instàncies noves, mai són iguals encara que coincideixin servei/nom.
        ServeiCampGrup grup1 = ServeiCampGrup.getBuilder("SERVEI1", null, "Grup 1", null, 1).build();
        ServeiCampGrup grup2 = ServeiCampGrup.getBuilder("SERVEI1", null, "Grup 1", null, 1).build();
        ServeiCampGrup grup3 = ServeiCampGrup.getBuilder("SERVEI2", null, "Grup 2", null, 2).build();

        assertEquals(grup1, grup1);
        assertNotEquals(grup1, grup2);
        assertNotEquals(grup1, grup3);
        assertNotEquals(grup1, null);
        assertNotEquals(grup1, new Object());
        assertEquals(grup1.hashCode(), grup2.hashCode());
        assertNotEquals(grup1.hashCode(), grup3.hashCode());
    }

    @Test
    public void testToString() {
        ServeiCampGrup grup = ServeiCampGrup.getBuilder("SERVEI1", null, "Grup 1", null, 1).build();

        assertNotNull(grup.toString());
    }
}
