package es.caib.pinbal.persist.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EntitatServeiTest {

    private Entitat crearEntitat(String codi) {
        return Entitat.getBuilder(codi, "Entitat " + codi, "Q1234567A", Entitat.EntitatTipus.GOVERN).build();
    }

    @Test
    public void testGetBuilder() {
        Entitat entitat = crearEntitat("ENT1");

        EntitatServei entitatServei = EntitatServei.getBuilder(entitat, "SERVEI1").build();

        assertEquals(entitat, entitatServei.getEntitat());
        assertEquals("SERVEI1", entitatServei.getServei());
        assertEquals(0, entitatServei.getVersion());
    }

    @Test
    public void testConfigurarIdPerTest() {
        EntitatServei entitatServei = EntitatServei.getBuilder(crearEntitat("ENT1"), "SERVEI1").build();

        entitatServei.configurarIdPerTest(7L);

        assertEquals(7L, entitatServei.getId());
    }

    @Test
    public void testEqualsAndHashCode() {
        Entitat entitat1 = crearEntitat("ENT1");
        Entitat entitat1b = crearEntitat("ENT1");
        Entitat entitat2 = crearEntitat("ENT2");

        EntitatServei es1 = EntitatServei.getBuilder(entitat1, "SERVEI1").build();
        es1.configurarIdPerTest(1L);

        EntitatServei es2 = EntitatServei.getBuilder(entitat1b, "SERVEI1").build();
        es2.configurarIdPerTest(1L);

        EntitatServei esServeiDiferent = EntitatServei.getBuilder(entitat1, "SERVEI2").build();
        esServeiDiferent.configurarIdPerTest(1L);

        EntitatServei esEntitatDiferent = EntitatServei.getBuilder(entitat2, "SERVEI1").build();
        esEntitatDiferent.configurarIdPerTest(1L);

        EntitatServei esIdDiferent = EntitatServei.getBuilder(entitat1, "SERVEI1").build();
        esIdDiferent.configurarIdPerTest(2L);

        assertEquals(es1, es1);
        assertEquals(es1, es2);
        assertEquals(es1.hashCode(), es2.hashCode());
        assertNotEquals(es1, esServeiDiferent);
        assertNotEquals(es1, esEntitatDiferent);
        assertNotEquals(es1, esIdDiferent);
        assertNotEquals(es1, null);
        assertNotEquals(es1, new Object());
    }

    @Test
    public void testToString() {
        EntitatServei entitatServei = EntitatServei.getBuilder(crearEntitat("ENT1"), "SERVEI1").build();

        assertNotNull(entitatServei.toString());
    }
}
