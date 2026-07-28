package es.caib.pinbal.persist.base.entity;

import es.caib.pinbal.logic.intf.base.model.Resource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NoDatabaseResourceEntityTest {

    static class FakeResource implements Resource<Long> {
        private Long id;

        FakeResource(Long id) {
            this.id = id;
        }

        @Override
        public Long getId() {
            return id;
        }

        @Override
        public void setId(Long id) {
            this.id = id;
        }
    }

    @Test
    public void testBuilder() {
        FakeResource resource = new FakeResource(1L);

        NoDatabaseResourceEntity<Resource<Long>, Long> entitat =
                NoDatabaseResourceEntity.<Resource<Long>, Long>builder()
                        .id(1L)
                        .resource(resource)
                        .build();

        assertEquals(1L, entitat.getId());
        assertEquals(resource, entitat.getResource());
        assertFalse(entitat.isNew());
    }

    @Test
    public void testConstructorSenseArguments() {
        NoDatabaseResourceEntity<Resource<Long>, Long> entitat = new NoDatabaseResourceEntity<>();

        assertTrue(entitat.isNew());

        FakeResource resource = new FakeResource(2L);
        entitat.setId(2L);
        entitat.setResource(resource);

        assertEquals(2L, entitat.getId());
        assertEquals(resource, entitat.getResource());
        assertFalse(entitat.isNew());
    }
}
