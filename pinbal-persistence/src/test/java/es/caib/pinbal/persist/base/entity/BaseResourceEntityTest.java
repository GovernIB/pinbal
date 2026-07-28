package es.caib.pinbal.persist.base.entity;

import es.caib.pinbal.logic.intf.base.model.Resource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BaseResourceEntityTest {

    static class FakeResourceEntity extends BaseResourceEntity<Resource<Long>, Long> {
        private Long id;

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
    public void testIsNew() {
        FakeResourceEntity entitat = new FakeResourceEntity();

        assertTrue(entitat.isNew());

        entitat.setId(1L);

        assertFalse(entitat.isNew());
    }

    @Test
    public void testEqualsAndHashCode() {
        FakeResourceEntity entitat1 = new FakeResourceEntity();
        entitat1.setId(1L);
        FakeResourceEntity entitat2 = new FakeResourceEntity();
        entitat2.setId(1L);
        FakeResourceEntity entitat3 = new FakeResourceEntity();
        entitat3.setId(2L);

        assertEquals(entitat1, entitat2);
        assertEquals(entitat1.hashCode(), entitat2.hashCode());
        assertNotEquals(entitat1, entitat3);
        assertNotEquals(entitat1, null);
        assertNotEquals(entitat1, new Object());
        assertEquals(entitat1, entitat1);
    }

    @Test
    public void testToStringNew() {
        FakeResourceEntity entitat = new FakeResourceEntity();

        assertEquals("FakeResourceEntity<new>", entitat.toString());
    }

    @Test
    public void testToStringAmbId() {
        FakeResourceEntity entitat = new FakeResourceEntity();
        entitat.setId(5L);

        assertEquals("FakeResourceEntity(id=5)", entitat.toString());
    }
}
