package es.caib.pinbal.persist.entity;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConfigTypeTest {

    @Test
    public void testGetValidValues_SenseValue() {
        ConfigType type = new ConfigType();

        assertTrue(type.getValidValues().isEmpty());
    }

    @Test
    public void testGetValidValues_ValueBuit() {
        ConfigType type = new ConfigType();
        ReflectionTestUtils.setField(type, "value", "");

        assertTrue(type.getValidValues().isEmpty());
    }

    @Test
    public void testGetValidValues_UnValor() {
        ConfigType type = new ConfigType();
        ReflectionTestUtils.setField(type, "code", "TIPUS1");
        ReflectionTestUtils.setField(type, "value", "SI");

        assertEquals("TIPUS1", type.getCode());
        assertEquals(1, type.getValidValues().size());
        assertEquals("SI", type.getValidValues().get(0));
    }

    @Test
    public void testGetValidValues_DiversosValors() {
        ConfigType type = new ConfigType();
        ReflectionTestUtils.setField(type, "value", "SI,NO,POTSER");

        assertEquals("SI,NO,POTSER", type.getValue());
        assertEquals(3, type.getValidValues().size());
        assertEquals("SI", type.getValidValues().get(0));
        assertEquals("NO", type.getValidValues().get(1));
        assertEquals("POTSER", type.getValidValues().get(2));
    }
}
