package es.caib.pinbal.persist.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ScspParametreConfiguracioTest {

    @Test
    public void testBuilder() {
        ScspParametreConfiguracio parametre = ScspParametreConfiguracio.builder()
                .nombre("PARAM1")
                .valor("valor1")
                .descripcion("Descripcio 1")
                .build();

        assertEquals("PARAM1", parametre.getNombre());
        assertEquals("valor1", parametre.getValor());
        assertEquals("Descripcio 1", parametre.getDescripcion());
    }

    @Test
    public void testConstructors() {
        ScspParametreConfiguracio senseArgs = new ScspParametreConfiguracio();

        assertNull(senseArgs.getNombre());
        assertNull(senseArgs.getValor());
        assertNull(senseArgs.getDescripcion());

        ScspParametreConfiguracio ambArgs = new ScspParametreConfiguracio("PARAM1", "valor1", "Descripcio 1");

        assertEquals("PARAM1", ambArgs.getNombre());
        assertEquals("valor1", ambArgs.getValor());
        assertEquals("Descripcio 1", ambArgs.getDescripcion());
    }

    @Test
    public void testUpdate() {
        ScspParametreConfiguracio parametre = ScspParametreConfiguracio.builder()
                .nombre("PARAM1")
                .valor("valor1")
                .descripcion("Descripcio 1")
                .build();

        parametre.update("valor2", "Descripcio 2");

        assertEquals("PARAM1", parametre.getNombre());
        assertEquals("valor2", parametre.getValor());
        assertEquals("Descripcio 2", parametre.getDescripcion());
    }

    @Test
    public void testEqualsAndHashCode() {
        ScspParametreConfiguracio parametre1 = ScspParametreConfiguracio.builder()
                .nombre("PARAM1").valor("valor1").descripcion("Descripcio 1").build();
        ScspParametreConfiguracio parametre2 = ScspParametreConfiguracio.builder()
                .nombre("PARAM1").valor("valor2").descripcion("Descripcio 2").build();
        ScspParametreConfiguracio parametre3 = ScspParametreConfiguracio.builder()
                .nombre("PARAM2").valor("valor1").descripcion("Descripcio 1").build();

        assertEquals(parametre1, parametre2);
        assertEquals(parametre1.hashCode(), parametre2.hashCode());
        assertNotEquals(parametre1, parametre3);
        assertNotEquals(parametre1, null);
        assertNotEquals(parametre1, new Object());
        assertEquals(parametre1, parametre1);
    }

    @Test
    public void testToString() {
        ScspParametreConfiguracio parametre = ScspParametreConfiguracio.builder()
                .nombre("PARAM1").valor("valor1").descripcion("Descripcio 1").build();

        assertNotNull(parametre.toString());
    }
}
