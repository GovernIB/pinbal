package es.caib.pinbal.persist.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ServeiBusTest {

    @Test
    public void testGetBuilder() {
        Entitat entitat = Entitat.getBuilder("ENT1", "Entitat 1", "Q1234567A", Entitat.EntitatTipus.GOVERN).build();

        ServeiBus servei = ServeiBus.getBuilder("SERVEI1", "https://desti.example.org", entitat).build();

        assertEquals("SERVEI1", servei.getServei());
        assertEquals("https://desti.example.org", servei.getUrlDesti());
        assertEquals(entitat, servei.getEntitat());
        assertEquals(0L, servei.getVersion());
    }

    @Test
    public void testUpdate() {
        Entitat entitat1 = Entitat.getBuilder("ENT1", "Entitat 1", "Q1234567A", Entitat.EntitatTipus.GOVERN).build();
        Entitat entitat2 = Entitat.getBuilder("ENT2", "Entitat 2", "Q7654321B", Entitat.EntitatTipus.CONSELL).build();

        ServeiBus servei = ServeiBus.getBuilder("SERVEI1", "https://desti.example.org", entitat1).build();

        servei.update("https://nou-desti.example.org", entitat2);

        assertEquals("https://nou-desti.example.org", servei.getUrlDesti());
        assertEquals(entitat2, servei.getEntitat());
    }

    @Test
    public void testEqualsAndHashCode() {
        // ServeiBus hereta equals() d'AbstractPersistable, basat en id: com que l'id és
        // null a instàncies noves, mai són iguals encara que coincideixin servei i entitat.
        Entitat entitat = Entitat.getBuilder("ENT1", "Entitat 1", "Q1234567A", Entitat.EntitatTipus.GOVERN).build();
        ServeiBus servei1 = ServeiBus.getBuilder("SERVEI1", "https://desti.example.org", entitat).build();
        ServeiBus servei2 = ServeiBus.getBuilder("SERVEI1", "https://desti.example.org", entitat).build();
        ServeiBus servei3 = ServeiBus.getBuilder("SERVEI2", "https://altre.example.org", null).build();

        assertEquals(servei1, servei1);
        assertNotEquals(servei1, servei2);
        assertNotEquals(servei1, servei3);
        assertNotEquals(servei1, null);
        assertNotEquals(servei1, new Object());
        assertEquals(servei1.hashCode(), servei2.hashCode());
        assertNotEquals(servei1.hashCode(), servei3.hashCode());
    }

    @Test
    public void testToString() {
        Entitat entitat = Entitat.getBuilder("ENT1", "Entitat 1", "Q1234567A", Entitat.EntitatTipus.GOVERN).build();
        ServeiBus servei = ServeiBus.getBuilder("SERVEI1", "https://desti.example.org", entitat).build();

        assertNotNull(servei.toString());
    }
}
