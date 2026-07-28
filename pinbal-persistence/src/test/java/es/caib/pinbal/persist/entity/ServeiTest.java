package es.caib.pinbal.persist.entity;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ServeiTest {

    @Test
    public void testGettersSetters() {
        Servei servei = new Servei();
        EmissorCert emissor = EmissorCert.getBuilder("Emissor 1", "Q1234567A", new Date()).build();
        Date dataAlta = new Date(1000L);
        Date dataBaixa = new Date(2000L);

        servei.setId(1L);
        servei.setCodi("SERVEI1");
        servei.setDescripcio("Descripció del servei");
        servei.setScspEmisor(emissor);
        servei.setScspFechaAlta(dataAlta);
        servei.setScspFechaBaja(dataBaixa);
        servei.setCaducitat(30);
        servei.setScspVersionEsquema("2.0");

        assertEquals(1L, servei.getId());
        assertEquals("SERVEI1", servei.getCodi());
        assertEquals("Descripció del servei", servei.getDescripcio());
        assertEquals(emissor, servei.getScspEmisor());
        assertEquals(dataAlta, servei.getScspFechaAlta());
        assertEquals(dataBaixa, servei.getScspFechaBaja());
        assertEquals(30, servei.getCaducitat());
        assertEquals("2.0", servei.getScspVersionEsquema());
    }

    @Test
    public void testEqualsAndHashCode() {
        Servei servei1 = new Servei();
        servei1.setCodi("SERVEI1");
        Servei servei2 = new Servei();
        servei2.setCodi("SERVEI1");
        servei2.setDescripcio("Una altra descripció");
        Servei servei3 = new Servei();
        servei3.setCodi("SERVEI2");
        Servei serveiSenseCodi = new Servei();

        assertEquals(servei1, servei1);
        assertEquals(servei1, servei2);
        assertEquals(servei1.hashCode(), servei2.hashCode());
        assertNotEquals(servei1, servei3);
        assertNotEquals(servei1, serveiSenseCodi);
        assertNotEquals(serveiSenseCodi, servei1);
        assertEquals(new Servei(), new Servei());
        assertNotEquals(servei1, null);
        assertNotEquals(servei1, new Object());
    }

    @Test
    public void testToString() {
        Servei servei = new Servei();
        servei.setCodi("SERVEI1");

        assertNotNull(servei.toString());
    }
}
