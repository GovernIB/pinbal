package es.caib.pinbal.persist.resourceentity;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConfigTypeResourceEntityTest {

    @Test
    public void testGetValidValues_SenseValue() {
        ConfigTypeResourceEntity entity = new ConfigTypeResourceEntity();

        assertTrue(entity.getValidValues().isEmpty());
    }

    @Test
    public void testGetValidValues_ValueBuit() {
        ConfigTypeResourceEntity entity = new ConfigTypeResourceEntity();
        ReflectionTestUtils.setField(entity, "value", "");

        assertTrue(entity.getValidValues().isEmpty());
    }

    @Test
    public void testGetValidValues_DiversosValors() {
        ConfigTypeResourceEntity entity = new ConfigTypeResourceEntity();
        ReflectionTestUtils.setField(entity, "code", "TIPUS1");
        ReflectionTestUtils.setField(entity, "value", "SI,NO");

        assertEquals("TIPUS1", entity.getCode());
        assertEquals("SI,NO", entity.getValue());
        assertEquals(2, entity.getValidValues().size());
        assertEquals("SI", entity.getValidValues().get(0));
        assertEquals("NO", entity.getValidValues().get(1));
    }
}
