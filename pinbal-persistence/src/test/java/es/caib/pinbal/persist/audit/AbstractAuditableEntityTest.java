package es.caib.pinbal.persist.audit;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AbstractAuditableEntityTest {

    static class FakeAuditableEntity extends AbstractAuditableEntity<Long> {
    }

    @Test
    public void testCreatedByBuit() {
        FakeAuditableEntity entitat = new FakeAuditableEntity();

        assertFalse(entitat.getCreatedBy().isPresent());
    }

    @Test
    public void testCreatedBy() {
        FakeAuditableEntity entitat = new FakeAuditableEntity();

        entitat.setCreatedBy("joan");

        assertTrue(entitat.getCreatedBy().isPresent());
        assertEquals("joan", entitat.getCreatedBy().get());
    }

    @Test
    public void testCreatedDate() {
        FakeAuditableEntity entitat = new FakeAuditableEntity();
        LocalDateTime data = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        assertFalse(entitat.getCreatedDate().isPresent());

        entitat.setCreatedDate(data);

        assertTrue(entitat.getCreatedDate().isPresent());
        assertEquals(data, entitat.getCreatedDate().get());
    }

    @Test
    public void testLastModifiedBy() {
        FakeAuditableEntity entitat = new FakeAuditableEntity();

        assertFalse(entitat.getLastModifiedBy().isPresent());

        entitat.setLastModifiedBy("pere");

        assertTrue(entitat.getLastModifiedBy().isPresent());
        assertEquals("pere", entitat.getLastModifiedBy().get());
    }

    @Test
    public void testLastModifiedDate() {
        FakeAuditableEntity entitat = new FakeAuditableEntity();
        LocalDateTime data = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        assertFalse(entitat.getLastModifiedDate().isPresent());

        entitat.setLastModifiedDate(data);

        assertTrue(entitat.getLastModifiedDate().isPresent());
        assertEquals(data, entitat.getLastModifiedDate().get());
    }

    @Test
    public void testTablePrefix() {
        assertEquals("not", AbstractAuditableEntity.TABLE_PREFIX);
    }
}
