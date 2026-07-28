package es.caib.pinbal.persist.resourceentity;

import es.caib.pinbal.logic.intf.dto.ConfigSourceEnumDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConfigResourceEntityTest {

    @Test
    public void testGettersSetters() {
        ConfigResourceEntity entity = new ConfigResourceEntity();
        entity.setKey("app.nom");
        entity.setValue("PINBAL");
        entity.setDescriptionKey("app.nom.desc");
        entity.setSourceProperty(ConfigSourceEnumDto.DATABASE);
        entity.setGroupCode("GRUP1");
        entity.setTypeCode("TIPUS1");
        entity.setPosition(2);

        assertEquals("app.nom", entity.getKey());
        assertEquals("PINBAL", entity.getValue());
        assertEquals("app.nom.desc", entity.getDescriptionKey());
        assertEquals(ConfigSourceEnumDto.DATABASE, entity.getSourceProperty());
        assertEquals("GRUP1", entity.getGroupCode());
        assertEquals("TIPUS1", entity.getTypeCode());
        assertEquals(2, entity.getPosition());
    }

    @Test
    public void testGetIdSetId() {
        ConfigResourceEntity entity = new ConfigResourceEntity();

        entity.setId("app.nom");

        assertEquals("app.nom", entity.getId());
        assertEquals("app.nom", entity.getKey());
    }

    @Test
    public void testIsEditable() {
        ConfigResourceEntity entity = new ConfigResourceEntity();

        assertFalse(entity.isEditable());

        entity.setSourceProperty(ConfigSourceEnumDto.FILE);
        assertFalse(entity.isEditable());

        entity.setSourceProperty(ConfigSourceEnumDto.DATABASE);
        assertTrue(entity.isEditable());
    }

    @Test
    public void testIsNew() {
        ConfigResourceEntity entity = new ConfigResourceEntity();

        assertTrue(entity.isNew());

        entity.setId("app.nom");

        assertFalse(entity.isNew());
    }

    @Test
    public void testEqualsAndHashCode() {
        ConfigResourceEntity entity1 = new ConfigResourceEntity();
        entity1.setId("app.nom");
        ConfigResourceEntity entity2 = new ConfigResourceEntity();
        entity2.setId("app.nom");
        ConfigResourceEntity entity3 = new ConfigResourceEntity();
        entity3.setId("app.altre");

        assertEquals(entity1, entity2);
        assertEquals(entity1.hashCode(), entity2.hashCode());
        assertNotEquals(entity1, entity3);
        assertNotEquals(entity1, null);
        assertNotEquals(entity1, new Object());
        assertEquals(entity1, entity1);
    }

    @Test
    public void testToString() {
        ConfigResourceEntity entity = new ConfigResourceEntity();

        assertTrue(entity.toString().contains("<new>"));

        entity.setId("app.nom");

        assertNotNull(entity.toString());
        assertTrue(entity.toString().contains("app.nom"));
    }
}
