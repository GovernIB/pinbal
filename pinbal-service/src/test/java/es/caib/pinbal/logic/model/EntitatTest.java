/**
 * 
 */
package es.caib.pinbal.logic.model;

import org.junit.jupiter.api.Test;

import es.caib.pinbal.persist.entity.Entitat;
import es.caib.pinbal.persist.entity.Entitat.EntitatTipus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test unitari per a la classe de model Entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EntitatTest {

	private static final String CODI = "PPT";
	private static final String NOM = "Pepet";
	private static final String CIF = "00000000T";
	private static final String CODI_UPDATED = "PPT1";
	private static final String NOM_UPDATED = "Pepet1";
	private static final String CIF_UPDATED = "12345678Z";

	@Test
	public void build() {
		Entitat built = Entitat.getBuilder(
				CODI,
				NOM,
				CIF,
				EntitatTipus.ALTRES).build();
		assertEquals(CODI, built.getCodi());
		assertEquals(NOM, built.getNom());
		assertEquals(CIF, built.getCif());
		assertEquals(0, built.getVersion());
		assertTrue(built.getCreatedBy().isEmpty());
		assertTrue(built.getCreatedDate().isEmpty());
		assertTrue(built.getLastModifiedBy().isEmpty());
		assertTrue(built.getLastModifiedDate().isEmpty());
		assertNull(built.getId());
	}

	@Test
	public void update() {
		Entitat built = Entitat.getBuilder(
				CODI,
				NOM,
				CIF,
				EntitatTipus.ALTRES).build();
		built.update(
				CODI_UPDATED,
				NOM_UPDATED,
				CIF_UPDATED,
				EntitatTipus.ALTRES);
		assertEquals(CODI_UPDATED, built.getCodi());
		assertEquals(NOM_UPDATED, built.getNom());
		assertEquals(CIF_UPDATED, built.getCif());
	}

}
