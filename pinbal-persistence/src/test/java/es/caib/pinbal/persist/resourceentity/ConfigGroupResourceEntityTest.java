package es.caib.pinbal.persist.resourceentity;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConfigGroupResourceEntityTest {

    @Test
    public void testGettersSetters() {
        ConfigGroupResourceEntity entity = new ConfigGroupResourceEntity();
        ConfigResourceEntity config = new ConfigResourceEntity();
        config.setId("app.nom");
        Set<ConfigResourceEntity> configs = Collections.singleton(config);
        ConfigGroupResourceEntity inner = new ConfigGroupResourceEntity();
        inner.setKey("GRUP2");
        Set<ConfigGroupResourceEntity> innerConfigs = Collections.singleton(inner);

        entity.setKey("GRUP1");
        entity.setDescriptionKey("grup1.desc");
        entity.setPosition(1);
        entity.setParentCode("GRUP0");
        entity.setConfigs(configs);
        entity.setInnerConfigs(innerConfigs);

        assertEquals("GRUP1", entity.getKey());
        assertEquals("grup1.desc", entity.getDescriptionKey());
        assertEquals(1, entity.getPosition());
        assertEquals("GRUP0", entity.getParentCode());
        assertEquals(configs, entity.getConfigs());
        assertEquals(innerConfigs, entity.getInnerConfigs());
    }

    @Test
    public void testGetIdSetId() {
        ConfigGroupResourceEntity entity = new ConfigGroupResourceEntity();

        entity.setId("GRUP1");

        assertEquals("GRUP1", entity.getId());
        assertEquals("GRUP1", entity.getKey());
    }

    @Test
    public void testIsNew() {
        ConfigGroupResourceEntity entity = new ConfigGroupResourceEntity();

        assertTrue(entity.isNew());

        entity.setId("GRUP1");

        assertFalse(entity.isNew());
    }

    @Test
    public void testEqualsAndHashCode() {
        ConfigGroupResourceEntity entity1 = new ConfigGroupResourceEntity();
        entity1.setId("GRUP1");
        ConfigGroupResourceEntity entity2 = new ConfigGroupResourceEntity();
        entity2.setId("GRUP1");
        ConfigGroupResourceEntity entity3 = new ConfigGroupResourceEntity();
        entity3.setId("GRUP2");

        assertEquals(entity1, entity2);
        assertEquals(entity1.hashCode(), entity2.hashCode());
        assertNotEquals(entity1, entity3);
        assertNotEquals(entity1, null);
        assertNotEquals(entity1, new Object());
        assertEquals(entity1, entity1);
    }

    @Test
    public void testToString() {
        ConfigGroupResourceEntity entity = new ConfigGroupResourceEntity();
        entity.setId("GRUP1");

        assertNotNull(entity.toString());
        assertTrue(entity.toString().contains("GRUP1"));
    }
}
