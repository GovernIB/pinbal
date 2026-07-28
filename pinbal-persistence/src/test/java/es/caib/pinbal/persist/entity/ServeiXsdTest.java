package es.caib.pinbal.persist.entity;

import es.caib.pinbal.logic.intf.dto.XsdTipusEnumDto;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ServeiXsdTest {

    @Test
    public void testBuilder() {
        Date data = new Date(1000L);

        ServeiXsd xsd = ServeiXsd.builder()
                .servei("SERVEI1")
                .tipus(XsdTipusEnumDto.PETICIO)
                .nomArxiu("peticio.xsd")
                .path("/xsd/peticio.xsd")
                .dataModificacio(data)
                .build();

        assertEquals("SERVEI1", xsd.getServei());
        assertEquals(XsdTipusEnumDto.PETICIO, xsd.getTipus());
        assertEquals("peticio.xsd", xsd.getNomArxiu());
        assertEquals("/xsd/peticio.xsd", xsd.getPath());
        assertEquals(data, xsd.getDataModificacio());
        assertEquals(0L, xsd.getVersion());
        assertNull(xsd.getId());
    }

    @Test
    public void testNoArgsIAllArgsConstructor() {
        ServeiXsd buit = new ServeiXsd();
        assertNull(buit.getServei());

        Date data = new Date(2000L);
        ServeiXsd complet = new ServeiXsd("SERVEI2", XsdTipusEnumDto.RESPOSTA, "resposta.xsd", "/xsd/resposta.xsd", data, 3L);

        assertEquals("SERVEI2", complet.getServei());
        assertEquals(XsdTipusEnumDto.RESPOSTA, complet.getTipus());
        assertEquals("resposta.xsd", complet.getNomArxiu());
        assertEquals("/xsd/resposta.xsd", complet.getPath());
        assertEquals(data, complet.getDataModificacio());
        assertEquals(3L, complet.getVersion());
    }

    @Test
    public void testSetNomArxiu() {
        ServeiXsd xsd = ServeiXsd.builder()
                .servei("SERVEI1")
                .tipus(XsdTipusEnumDto.DATOS_ESPECIFICOS)
                .nomArxiu("original.xsd")
                .path("/xsd/original.xsd")
                .dataModificacio(new Date())
                .build();

        xsd.setNomArxiu("modificat.xsd");

        assertEquals("modificat.xsd", xsd.getNomArxiu());
    }

    @Test
    public void testUpdateServeiXsd() {
        ServeiXsd xsd = ServeiXsd.builder()
                .servei("SERVEI1")
                .tipus(XsdTipusEnumDto.CONFIRMACIO_PETICIO)
                .nomArxiu("confirmacio.xsd")
                .path("/xsd/confirmacio.xsd")
                .dataModificacio(new Date(1000L))
                .build();

        long versionAbans = xsd.getVersion();
        xsd.updateServeiXsd();

        assertEquals(versionAbans + 1, xsd.getVersion());
        assertNotNull(xsd.getDataModificacio());
        assertNotEquals(new Date(1000L), xsd.getDataModificacio());
    }

    @Test
    public void testEqualsAndHashCode() {
        Date data = new Date(1000L);
        ServeiXsd xsd1 = ServeiXsd.builder()
                .servei("SERVEI1")
                .tipus(XsdTipusEnumDto.SOLICITUD_RESPOSTA)
                .nomArxiu("sol.xsd")
                .path("/xsd/sol.xsd")
                .dataModificacio(data)
                .build();
        ServeiXsd xsd2 = ServeiXsd.builder()
                .servei("SERVEI1")
                .tipus(XsdTipusEnumDto.SOLICITUD_RESPOSTA)
                .nomArxiu("sol.xsd")
                .path("/xsd/sol.xsd")
                .dataModificacio(data)
                .build();
        ServeiXsd xsd3 = ServeiXsd.builder()
                .servei("SERVEI2")
                .tipus(XsdTipusEnumDto.PETICIO)
                .nomArxiu("altre.xsd")
                .path("/xsd/altre.xsd")
                .dataModificacio(new Date(2000L))
                .build();

        assertEquals(xsd1, xsd1);
        assertEquals(xsd1, xsd2);
        assertEquals(xsd1.hashCode(), xsd2.hashCode());
        assertNotEquals(xsd1, xsd3);
        assertNotEquals(xsd1, null);
        assertNotEquals(xsd1, new Object());
    }

    @Test
    public void testToString() {
        ServeiXsd xsd = ServeiXsd.builder()
                .servei("SERVEI1")
                .tipus(XsdTipusEnumDto.PETICIO)
                .nomArxiu("peticio.xsd")
                .path("/xsd/peticio.xsd")
                .dataModificacio(new Date())
                .build();

        assertNotNull(xsd.toString());
    }
}
