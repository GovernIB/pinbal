package es.caib.pinbal.persist.resourceentity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParamConfResourceEntityTest {

    @Test
    public void testGettersSetters() {
        ParamConfResourceEntity entity = new ParamConfResourceEntity();

        entity.setNom("scsp.timeout");
        entity.setValor("30000");
        entity.setDescripcio("Temps d'espera SCSP");

        assertEquals("scsp.timeout", entity.getNom());
        assertEquals("30000", entity.getValor());
        assertEquals("Temps d'espera SCSP", entity.getDescripcio());
    }

    @Test
    public void testGetIdSetId() {
        ParamConfResourceEntity entity = new ParamConfResourceEntity();

        entity.setId("scsp.timeout");

        assertEquals("scsp.timeout", entity.getId());
        assertEquals("scsp.timeout", entity.getNom());
    }

    @Test
    public void testIsNew() {
        ParamConfResourceEntity entity = new ParamConfResourceEntity();

        assertTrue(entity.isNew());

        entity.setId("scsp.timeout");

        assertFalse(entity.isNew());
    }

    @Test
    public void testEqualsAndHashCode() {
        ParamConfResourceEntity entity1 = new ParamConfResourceEntity();
        entity1.setId("scsp.timeout");
        ParamConfResourceEntity entity2 = new ParamConfResourceEntity();
        entity2.setId("scsp.timeout");
        ParamConfResourceEntity entity3 = new ParamConfResourceEntity();
        entity3.setId("scsp.retries");

        assertEquals(entity1, entity2);
        assertEquals(entity1.hashCode(), entity2.hashCode());
        assertNotEquals(entity1, entity3);
        assertNotEquals(entity1, null);
        assertNotEquals(entity1, new Object());
        assertEquals(entity1, entity1);
    }

    @Test
    public void testToString() {
        ParamConfResourceEntity entity = new ParamConfResourceEntity();
        entity.setId("scsp.timeout");

        assertNotNull(entity.toString());
        assertTrue(entity.toString().contains("scsp.timeout"));
    }
}
