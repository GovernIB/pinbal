/**
 * 
 */
package es.caib.pinbal.core.model;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

import org.junit.Test;

import es.caib.pinbal.core.model.Entitat.EntitatTipus;

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
		assertNull(built.getCreatedBy());
		assertNull(built.getCreatedDate());
		assertNull(built.getLastModifiedBy());
		assertNull(built.getLastModifiedDate());
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
