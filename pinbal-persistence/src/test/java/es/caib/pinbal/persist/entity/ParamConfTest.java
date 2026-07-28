package es.caib.pinbal.persist.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ParamConfTest {

    @Test
    public void testGetBuilder() {
        ParamConf paramConf = ParamConf.getBuilder("PARAM1", "valor1", "Descripcio 1").build();

        assertEquals("PARAM1", paramConf.getNom());
        assertEquals("valor1", paramConf.getValor());
        assertEquals("Descripcio 1", paramConf.getDescripcio());
    }

    @Test
    public void testUpdate() {
        ParamConf paramConf = ParamConf.getBuilder("PARAM1", "valor1", "Descripcio 1").build();

        paramConf.update("valor2", "Descripcio 2");

        assertEquals("PARAM1", paramConf.getNom());
        assertEquals("valor2", paramConf.getValor());
        assertEquals("Descripcio 2", paramConf.getDescripcio());
    }

    @Test
    public void testEqualsAndHashCode() {
        ParamConf paramConf1 = ParamConf.getBuilder("PARAM1", "valor1", "Descripcio 1").build();
        ParamConf paramConf2 = ParamConf.getBuilder("PARAM1", "valor2", "Descripcio 2").build();
        ParamConf paramConf3 = ParamConf.getBuilder("PARAM2", "valor1", "Descripcio 1").build();

        assertEquals(paramConf1, paramConf2);
        assertEquals(paramConf1.hashCode(), paramConf2.hashCode());
        assertNotEquals(paramConf1, paramConf3);
        assertNotEquals(paramConf1, null);
        assertNotEquals(paramConf1, new Object());
        assertEquals(paramConf1, paramConf1);
    }

    @Test
    public void testToString() {
        ParamConf paramConf = ParamConf.getBuilder("PARAM1", "valor1", "Descripcio 1").build();

        assertNotNull(paramConf.toString());
    }
}
