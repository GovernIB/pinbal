package es.caib.pinbal.persist.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UsuariTest {

    @Test
    public void testGetBuilderInicialitzat() {
        Usuari usuari = Usuari.getBuilderInicialitzat("CODI1", "Joan Petit", "12345678A").build();

        assertEquals("CODI1", usuari.getCodi());
        assertEquals("Joan Petit", usuari.getNom());
        assertEquals("12345678A", usuari.getNif());
        assertTrue(usuari.isInicialitzat());
        assertFalse(usuari.isNoInicialitzatNif());
        assertFalse(usuari.isNoInicialitzatCodi());
    }

    @Test
    public void testGetBuilderNoInicialitzatCodi() {
        Usuari usuari = Usuari.getBuilderNoInicialitzatCodi("CODI1").build();

        assertEquals("CODI1", usuari.getCodi());
        assertFalse(usuari.isInicialitzat());
        assertTrue(usuari.isNoInicialitzatCodi());
        assertFalse(usuari.isNoInicialitzatNif());
    }

    @Test
    public void testGetBuilderNoInicialitzatNif() {
        Usuari usuari = Usuari.getBuilderNoInicialitzatNif("12345678A").build();

        assertEquals("12345678A", usuari.getCodi());
        assertFalse(usuari.isInicialitzat());
        assertTrue(usuari.isNoInicialitzatNif());
        assertFalse(usuari.isNoInicialitzatCodi());
    }

    @Test
    public void testUpdate() {
        Usuari usuari = Usuari.getBuilderNoInicialitzatCodi("CODI1").build();

        usuari.update("Joan Petit", "12345678A");

        assertEquals("Joan Petit", usuari.getNom());
        assertEquals("12345678A", usuari.getNif());
        assertTrue(usuari.isInicialitzat());
    }

    @Test
    public void testUpdateEmail() {
        Usuari usuari = Usuari.getBuilderInicialitzat("CODI1", "Joan Petit", "12345678A").build();

        usuari.updateEmail("joan.petit@caib.es");

        assertEquals("joan.petit@caib.es", usuari.getEmail());
    }

    @Test
    public void testUpdateIdioma() {
        Usuari usuari = Usuari.getBuilderInicialitzat("CODI1", "Joan Petit", "12345678A").build();

        usuari.updateIdioma("ca");

        assertEquals("ca", usuari.getIdioma());
    }

    @Test
    public void testUpdateValorsPerDefecte_SenseEntitat() {
        Usuari usuari = Usuari.getBuilderInicialitzat("CODI1", "Joan Petit", "12345678A").build();

        usuari.updateValorsPerDefecte("ca", 1L, "SERVEI1", "Departament", "Finalitat", 20);

        assertEquals("ca", usuari.getIdioma());
        assertEquals(1L, usuari.getProcedimentId());
        assertEquals("SERVEI1", usuari.getServeiCodi());
        assertEquals("Departament", usuari.getDepartament());
        assertEquals("Finalitat", usuari.getFinalitat());
        assertEquals(20, usuari.getNumElementsPagina());
    }

    @Test
    public void testUpdateValorsPerDefecte_AmbEntitat() {
        Usuari usuari = Usuari.getBuilderInicialitzat("CODI1", "Joan Petit", "12345678A").build();

        usuari.updateValorsPerDefecte("ca", 1L, "SERVEI1", 2L, "Departament", "Finalitat", 20);

        assertEquals("ca", usuari.getIdioma());
        assertEquals(1L, usuari.getProcedimentId());
        assertEquals("SERVEI1", usuari.getServeiCodi());
        assertEquals(2L, usuari.getEntitatId());
        assertEquals("Departament", usuari.getDepartament());
        assertEquals("Finalitat", usuari.getFinalitat());
        assertEquals(20, usuari.getNumElementsPagina());
    }

    @Test
    public void testMoureEntitats() {
        Usuari origen = Usuari.getBuilderInicialitzat("CODI1", "Joan Petit", "12345678A").build();
        Usuari desti = Usuari.getBuilderInicialitzat("CODI2", "Pere Gran", "87654321B").build();
        Entitat entitat = Entitat.getBuilder("ENT1", "Entitat 1", "Q1234567A", Entitat.EntitatTipus.GOVERN).build();
        EntitatUsuari entitatUsuari = EntitatUsuari.getBuilder(
                entitat, origen, "Departament", true, false, false, false, true).build();
        origen.getEntitats().add(entitatUsuari);

        origen.moureEntitats(desti);

        assertTrue(origen.getEntitats().isEmpty());
        assertEquals(1, desti.getEntitats().size());
        assertEquals(desti, entitatUsuari.getUsuari());
    }

    @Test
    public void testEqualsAndHashCode() {
        Usuari usuari1 = Usuari.getBuilderInicialitzat("CODI1", "Joan Petit", "12345678A").build();
        Usuari usuari2 = Usuari.getBuilderInicialitzat("CODI1", "Un altre nom", "87654321B").build();
        Usuari usuari3 = Usuari.getBuilderInicialitzat("CODI2", "Joan Petit", "12345678A").build();

        assertEquals(usuari1, usuari2);
        assertEquals(usuari1.hashCode(), usuari2.hashCode());
        assertNotEquals(usuari1, usuari3);
        assertNotEquals(usuari1, null);
        assertNotEquals(usuari1, new Object());
        assertEquals(usuari1, usuari1);
    }

    @Test
    public void testToString() {
        Usuari usuari = Usuari.getBuilderInicialitzat("CODI1", "Joan Petit", "12345678A").build();

        assertNotNull(usuari.toString());
    }
}
