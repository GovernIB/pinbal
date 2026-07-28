package es.caib.pinbal.persist.resourceentity;

import es.caib.pinbal.logic.intf.model.UsuariResource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UsuariResourceEntityTest {

    private UsuariResource buildResource() {
        UsuariResource resource = new UsuariResource();
        resource.setCodi("CODI1");
        resource.setNom("Joan Petit");
        resource.setNif("12345678A");
        resource.setInicialitzat(true);
        resource.setEmail("joan.petit@caib.es");
        resource.setIdioma("ca");
        resource.setDepartament("Departament");
        resource.setFinalitat("Finalitat");
        resource.setNumElementsPagina(20);
        return resource;
    }

    @Test
    public void testBuilder() {
        UsuariResourceEntity entity = UsuariResourceEntity.builder().resource(buildResource()).build();

        assertEquals("CODI1", entity.getId());
        assertEquals("CODI1", entity.getCodi());
        assertEquals("Joan Petit", entity.getNom());
        assertEquals("12345678A", entity.getNif());
        assertTrue(entity.isInicialitzat());
        assertEquals("joan.petit@caib.es", entity.getEmail());
        assertEquals("ca", entity.getIdioma());
        assertEquals("Departament", entity.getDepartament());
        assertEquals("Finalitat", entity.getFinalitat());
        assertEquals(20, entity.getNumElementsPagina());
        assertEquals(0, entity.getVersion());
    }

    @Test
    public void testNoArgsConstructorAndSetters() {
        UsuariResourceEntity entity = new UsuariResourceEntity();

        entity.setId("CODI2");
        entity.setNom("Pere Gran");
        entity.setNif("87654321B");
        entity.setInicialitzat(false);
        entity.setEmail("pere.gran@caib.es");
        entity.setIdioma("es");
        entity.setDepartament("Altre Departament");
        entity.setFinalitat("Altra Finalitat");
        entity.setNumElementsPagina(10);
        entity.setVersion(2L);

        assertEquals("CODI2", entity.getId());
        assertEquals("CODI2", entity.getCodi());
        assertEquals("Pere Gran", entity.getNom());
        assertEquals("87654321B", entity.getNif());
        assertFalse(entity.isInicialitzat());
        assertEquals("pere.gran@caib.es", entity.getEmail());
        assertEquals("es", entity.getIdioma());
        assertEquals("Altre Departament", entity.getDepartament());
        assertEquals("Altra Finalitat", entity.getFinalitat());
        assertEquals(10, entity.getNumElementsPagina());
        assertEquals(2L, entity.getVersion());
    }

    @Test
    public void testIsNew() {
        UsuariResourceEntity entity = new UsuariResourceEntity();

        assertTrue(entity.isNew());

        entity.setId("CODI1");

        assertFalse(entity.isNew());
    }

    @Test
    public void testEqualsAndHashCode() {
        UsuariResourceEntity entity1 = new UsuariResourceEntity();
        entity1.setId("CODI1");
        UsuariResourceEntity entity2 = new UsuariResourceEntity();
        entity2.setId("CODI1");
        UsuariResourceEntity entity3 = new UsuariResourceEntity();
        entity3.setId("CODI2");

        assertEquals(entity1, entity2);
        assertEquals(entity1.hashCode(), entity2.hashCode());
        assertNotEquals(entity1, entity3);
        assertNotEquals(entity1, null);
        assertNotEquals(entity1, new Object());
        assertEquals(entity1, entity1);
    }

    @Test
    public void testToString() {
        UsuariResourceEntity entity = new UsuariResourceEntity();
        entity.setId("CODI1");

        assertNotNull(entity.toString());
        assertTrue(entity.toString().contains("CODI1"));
    }
}
