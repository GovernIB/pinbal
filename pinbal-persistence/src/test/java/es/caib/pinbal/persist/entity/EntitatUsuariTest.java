package es.caib.pinbal.persist.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EntitatUsuariTest {

    private Entitat crearEntitat(String codi) {
        return Entitat.getBuilder(codi, "Entitat " + codi, "Q1234567A", Entitat.EntitatTipus.AJUNTAMENT).build();
    }

    private Usuari crearUsuari(String codi) {
        return Usuari.getBuilderInicialitzat(codi, "Nom " + codi, "12345678A").build();
    }

    @Test
    public void testGetBuilder() {
        Entitat entitat = crearEntitat("ENT1");
        Usuari usuari = crearUsuari("USR1");

        EntitatUsuari entitatUsuari = EntitatUsuari.getBuilder(
                entitat, usuari, "Departament 1", true, false, true, false, true).build();

        assertEquals(entitat, entitatUsuari.getEntitat());
        assertEquals(usuari, entitatUsuari.getUsuari());
        assertEquals("Departament 1", entitatUsuari.getDepartament());
        assertTrue(entitatUsuari.isRepresentant());
        assertFalse(entitatUsuari.isDelegat());
        assertTrue(entitatUsuari.isAuditor());
        assertFalse(entitatUsuari.isAplicacio());
        assertTrue(entitatUsuari.isActiu());
        assertFalse(entitatUsuari.isPrincipal());
        assertEquals(0, entitatUsuari.getVersion());
    }

    @Test
    public void testUpdate() {
        EntitatUsuari entitatUsuari = EntitatUsuari.getBuilder(
                crearEntitat("ENT1"), crearUsuari("USR1"), "Departament 1",
                false, false, false, false, true).build();

        entitatUsuari.update("Departament 2", true, true, true, true, false);

        assertEquals("Departament 2", entitatUsuari.getDepartament());
        assertTrue(entitatUsuari.isRepresentant());
        assertTrue(entitatUsuari.isDelegat());
        assertTrue(entitatUsuari.isAuditor());
        assertTrue(entitatUsuari.isAplicacio());
        assertFalse(entitatUsuari.isActiu());
    }

    @Test
    public void testUpdatePrincipal() {
        EntitatUsuari entitatUsuari = EntitatUsuari.getBuilder(
                crearEntitat("ENT1"), crearUsuari("USR1"), "Departament 1",
                false, false, false, false, true).build();

        entitatUsuari.updatePrincipal(true);

        assertTrue(entitatUsuari.isPrincipal());
    }

    @Test
    public void testCanviPrincipal() {
        EntitatUsuari entitatUsuari = EntitatUsuari.getBuilder(
                crearEntitat("ENT1"), crearUsuari("USR1"), "Departament 1",
                false, false, false, false, true).build();

        assertTrue(entitatUsuari.canviPrincipal());
        assertTrue(entitatUsuari.isPrincipal());
        assertFalse(entitatUsuari.canviPrincipal());
        assertFalse(entitatUsuari.isPrincipal());
    }

    @Test
    public void testCanviActiu() {
        EntitatUsuari entitatUsuari = EntitatUsuari.getBuilder(
                crearEntitat("ENT1"), crearUsuari("USR1"), "Departament 1",
                false, false, false, false, true).build();

        assertFalse(entitatUsuari.canviActiu());
        assertFalse(entitatUsuari.isActiu());
        assertTrue(entitatUsuari.canviActiu());
        assertTrue(entitatUsuari.isActiu());
    }

    @Test
    public void testUpdateUsuari() {
        EntitatUsuari entitatUsuari = EntitatUsuari.getBuilder(
                crearEntitat("ENT1"), crearUsuari("USR1"), "Departament 1",
                false, false, false, false, true).build();
        Usuari nouUsuari = crearUsuari("USR2");

        entitatUsuari.updateUsuari(nouUsuari);

        assertEquals(nouUsuari, entitatUsuari.getUsuari());
    }

    @Test
    public void testConfigurarIdPerTest() {
        EntitatUsuari entitatUsuari = EntitatUsuari.getBuilder(
                crearEntitat("ENT1"), crearUsuari("USR1"), "Departament 1",
                false, false, false, false, true).build();

        entitatUsuari.configurarIdPerTest(3L);

        assertEquals(3L, entitatUsuari.getId());
    }

    @Test
    public void testEqualsAndHashCode() {
        Entitat entitat1 = crearEntitat("ENT1");
        Entitat entitat1b = crearEntitat("ENT1");
        Entitat entitat2 = crearEntitat("ENT2");
        Usuari usuari1 = crearUsuari("USR1");
        Usuari usuari1b = crearUsuari("USR1");
        Usuari usuari2 = crearUsuari("USR2");

        EntitatUsuari eu1 = EntitatUsuari.getBuilder(
                entitat1, usuari1, "Departament 1", false, false, false, false, true).build();
        eu1.configurarIdPerTest(1L);

        EntitatUsuari eu2 = EntitatUsuari.getBuilder(
                entitat1b, usuari1b, "Departament 2", true, true, true, true, false).build();
        eu2.configurarIdPerTest(1L);

        EntitatUsuari euUsuariDiferent = EntitatUsuari.getBuilder(
                entitat1, usuari2, "Departament 1", false, false, false, false, true).build();
        euUsuariDiferent.configurarIdPerTest(1L);

        EntitatUsuari euEntitatDiferent = EntitatUsuari.getBuilder(
                entitat2, usuari1, "Departament 1", false, false, false, false, true).build();
        euEntitatDiferent.configurarIdPerTest(1L);

        EntitatUsuari euIdDiferent = EntitatUsuari.getBuilder(
                entitat1, usuari1, "Departament 1", false, false, false, false, true).build();
        euIdDiferent.configurarIdPerTest(2L);

        assertEquals(eu1, eu1);
        assertEquals(eu1, eu2);
        assertEquals(eu1.hashCode(), eu2.hashCode());
        assertNotEquals(eu1, euUsuariDiferent);
        assertNotEquals(eu1, euEntitatDiferent);
        assertNotEquals(eu1, euIdDiferent);
        assertNotEquals(eu1, null);
        assertNotEquals(eu1, new Object());
    }

    @Test
    public void testToString() {
        EntitatUsuari entitatUsuari = EntitatUsuari.getBuilder(
                crearEntitat("ENT1"), crearUsuari("USR1"), "Departament 1",
                false, false, false, false, true).build();

        assertNotNull(entitatUsuari.toString());
    }
}
