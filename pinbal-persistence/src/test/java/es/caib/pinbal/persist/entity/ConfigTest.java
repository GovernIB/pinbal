package es.caib.pinbal.persist.entity;

import es.caib.pinbal.logic.intf.dto.ConfigSourceEnumDto;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConfigTest {

    @Test
    public void testConstructor() {
        Config config = new Config("app.nom", "PINBAL");

        assertEquals("app.nom", config.getKey());
        assertEquals("PINBAL", config.getValue());
    }

    @Test
    public void testNoArgsConstructor() {
        Config config = new Config();

        assertNull(config.getKey());
        assertNull(config.getValue());
    }

    @Test
    public void testUpdate() {
        Config config = new Config("app.nom", "PINBAL");

        config.update("PINBAL2");

        assertEquals("PINBAL2", config.getValue());
    }

    @Test
    public void testGetValidValues_SenseType() {
        Config config = new Config("app.nom", "PINBAL");

        assertTrue(config.getValidValues().isEmpty());
    }

    @Test
    public void testGetValidValues_AmbType() {
        Config config = new Config("app.nom", "PINBAL");
        ConfigType type = new ConfigType();
        ReflectionTestUtils.setField(type, "code", "TIPUS1");
        ReflectionTestUtils.setField(type, "value", "A,B,C");
        ReflectionTestUtils.setField(config, "type", type);

        assertEquals(3, config.getValidValues().size());
        assertEquals("TIPUS1", config.getTypeCode());
    }

    @Test
    public void testGetTypeCode_SenseType() {
        Config config = new Config("app.nom", "PINBAL");

        assertEquals("", config.getTypeCode());
    }

    @Test
    public void testIsEditable_Database() {
        Config config = new Config("app.nom", "PINBAL");
        ReflectionTestUtils.setField(config, "sourceProperty", ConfigSourceEnumDto.DATABASE);

        assertTrue(config.isEditable());
    }

    @Test
    public void testIsEditable_NoDatabase() {
        Config config = new Config("app.nom", "PINBAL");
        ReflectionTestUtils.setField(config, "sourceProperty", ConfigSourceEnumDto.FILE);

        assertFalse(config.isEditable());
    }

    @Test
    public void testIsEditable_SenseSourceProperty() {
        Config config = new Config("app.nom", "PINBAL");

        assertFalse(config.isEditable());
    }

    @Test
    public void testAltresCamps() {
        Config config = new Config("app.nom", "PINBAL");
        Usuari usuari = Usuari.getBuilderInicialitzat("CODI1", "Joan Petit", "12345678A").build();
        Date data = new Date();
        ReflectionTestUtils.setField(config, "descriptionKey", "app.nom.desc");
        ReflectionTestUtils.setField(config, "groupCode", "GRUP1");
        ReflectionTestUtils.setField(config, "position", 3);
        ReflectionTestUtils.setField(config, "lastModifiedBy", usuari);
        ReflectionTestUtils.setField(config, "lastModifiedDate", data);

        assertEquals("app.nom.desc", config.getDescriptionKey());
        assertEquals("GRUP1", config.getGroupCode());
        assertEquals(3, config.getPosition());
        assertEquals(usuari, config.getLastModifiedBy());
        assertEquals(data, config.getLastModifiedDate());
    }
}
