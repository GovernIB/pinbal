package es.caib.pinbal.persist.entity;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class EmissorCertTest {

    @Test
    public void testGetBuilder() {
        Date dataBaixa = new Date(1000L);

        EmissorCert emissorCert = EmissorCert.getBuilder("Emissor 1", "Q1234567A", dataBaixa).build();

        assertEquals("Emissor 1", emissorCert.getNom());
        assertEquals("Q1234567A", emissorCert.getCif());
        assertEquals(dataBaixa, emissorCert.getDataBaixa());
        assertNull(emissorCert.getId());
    }

    @Test
    public void testUpdate() {
        EmissorCert emissorCert = EmissorCert.getBuilder("Emissor 1", "Q1234567A", new Date()).build();

        Date novaDataBaixa = new Date(2000L);
        emissorCert.update("Emissor 2", "Q7654321B", novaDataBaixa);

        assertEquals("Emissor 2", emissorCert.getNom());
        assertEquals("Q7654321B", emissorCert.getCif());
        assertEquals(novaDataBaixa, emissorCert.getDataBaixa());
    }

    @Test
    public void testEqualsAndHashCode() {
        // no hi ha setId(), de manera que l'id sempre és null i equals() (basat en id)
        // considera iguals dos objectes encara que el nom (usat pel hashCode) sigui diferent
        EmissorCert emissorCert1 = EmissorCert.getBuilder("Emissor 1", "Q1234567A", new Date()).build();
        EmissorCert emissorCert2 = EmissorCert.getBuilder("Emissor 2", "Q7654321B", new Date()).build();

        assertEquals(emissorCert1, emissorCert2);
        assertNotEquals(emissorCert1.hashCode(), emissorCert2.hashCode());
        assertNotEquals(emissorCert1, null);
        assertNotEquals(emissorCert1, new Object());
        assertEquals(emissorCert1, emissorCert1);
    }

    @Test
    public void testToString() {
        EmissorCert emissorCert = EmissorCert.getBuilder("Emissor 1", "Q1234567A", new Date()).build();

        assertNotNull(emissorCert.toString());
    }
}
