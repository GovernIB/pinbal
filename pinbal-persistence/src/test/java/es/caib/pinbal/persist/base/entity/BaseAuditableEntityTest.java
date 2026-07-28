package es.caib.pinbal.persist.base.entity;

import es.caib.pinbal.logic.intf.base.model.Resource;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BaseAuditableEntityTest {

    static class FakeAuditableEntity extends BaseAuditableEntity<Resource<Long>, Long> {
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
    public void testSettersIGetters() {
        FakeAuditableEntity entitat = new FakeAuditableEntity();
        LocalDateTime ara = LocalDateTime.of(2024, 1, 15, 10, 30);

        entitat.setCreatedBy("joan");
        entitat.setCreatedDate(ara);
        entitat.setLastModifiedBy("pere");
        entitat.setLastModifiedDate(ara.plusDays(1));

        assertEquals("joan", entitat.getCreatedBy());
        assertEquals(ara, entitat.getCreatedDate());
        assertEquals("pere", entitat.getLastModifiedBy());
        assertEquals(ara.plusDays(1), entitat.getLastModifiedDate());
    }

    @Test
    public void testUpdateCreatedAmbData() {
        FakeAuditableEntity entitat = new FakeAuditableEntity();
        LocalDateTime data = LocalDateTime.of(2023, 5, 10, 12, 0);

        entitat.updateCreated("joan", data);

        assertEquals("joan", entitat.getCreatedBy());
        assertEquals(data, entitat.getCreatedDate());
    }

    @Test
    public void testUpdateCreatedSenseData() {
        FakeAuditableEntity entitat = new FakeAuditableEntity();

        entitat.updateCreated("joan", null);

        assertEquals("joan", entitat.getCreatedBy());
        assertNotNull(entitat.getCreatedDate());
    }

    @Test
    public void testUpdateLastModifiedAmbData() {
        FakeAuditableEntity entitat = new FakeAuditableEntity();
        LocalDateTime data = LocalDateTime.of(2023, 6, 20, 8, 15);

        entitat.updateLastModified("pere", data);

        assertEquals("pere", entitat.getLastModifiedBy());
        assertEquals(data, entitat.getLastModifiedDate());
    }

    @Test
    public void testUpdateLastModifiedSenseData() {
        FakeAuditableEntity entitat = new FakeAuditableEntity();

        entitat.updateLastModified("pere", null);

        assertEquals("pere", entitat.getLastModifiedBy());
        assertNotNull(entitat.getLastModifiedDate());
    }
}
