package es.caib.pinbal.persist.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServeiJustificantCampTest {

    @Test
    public void testGetBuilder() {
        ServeiJustificantCamp camp = ServeiJustificantCamp.getBuilder(
                "SERVEI1", "/arrel/camp", "ca", "ES", "Traducció", true).build();

        assertEquals("SERVEI1", camp.getServei());
        assertEquals("/arrel/camp", camp.getXpath());
        assertEquals("ca", camp.getLocaleIdioma());
        assertEquals("ES", camp.getLocaleRegio());
        assertEquals("Traducció", camp.getTraduccio());
        assertTrue(camp.isDocument());
        assertEquals(0L, camp.getVersion());
    }

    @Test
    public void testUpdate() {
        ServeiJustificantCamp camp = ServeiJustificantCamp.getBuilder(
                "SERVEI1", "/arrel/camp", "ca", "ES", "Traducció", false).build();

        camp.update("Nova traducció", true);

        assertEquals("Nova traducció", camp.getTraduccio());
        assertTrue(camp.isDocument());
    }

    @Test
    public void testEqualsAndHashCode() {
        // ServeiJustificantCamp hereta equals() d'AbstractPersistable, basat en id: com que
        // l'id és null a instàncies noves, mai són iguals encara que coincideixin els camps.
        ServeiJustificantCamp camp1 = ServeiJustificantCamp.getBuilder(
                "SERVEI1", "/arrel/camp", "ca", "ES", "Traducció", true).build();
        ServeiJustificantCamp camp2 = ServeiJustificantCamp.getBuilder(
                "SERVEI1", "/arrel/camp", "ca", "ES", "Traducció", true).build();
        ServeiJustificantCamp camp3 = ServeiJustificantCamp.getBuilder(
                "SERVEI2", "/altre/camp", "es", "ES", "Otra traducción", false).build();

        assertEquals(camp1, camp1);
        assertNotEquals(camp1, camp2);
        assertNotEquals(camp1, camp3);
        assertNotEquals(camp1, null);
        assertNotEquals(camp1, new Object());
        assertEquals(camp1.hashCode(), camp2.hashCode());
        assertNotEquals(camp1.hashCode(), camp3.hashCode());
    }

    @Test
    public void testToString() {
        ServeiJustificantCamp camp = ServeiJustificantCamp.getBuilder(
                "SERVEI1", "/arrel/camp", "ca", "ES", "Traducció", false).build();

        assertNotNull(camp.toString());
        assertFalse(camp.isDocument());
    }
}
