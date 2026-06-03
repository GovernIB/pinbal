/**
 * 
 */
package es.caib.pinbal.persist.repository;

import es.caib.pinbal.persist.config.PersistenceTestConfig;
import es.caib.pinbal.persist.entity.Entitat;
import es.caib.pinbal.persist.model.EntitatTestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Test d'integració per al servei de manteniment d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = PersistenceTestConfig.class)
@Transactional
public class EntitatRepositoryIntegrationTest {

	private static final String CODI = "PPT";
	private static final String NOM = "Pepet";
	private static final String CIF = "00000000T";
	private static final String RESP = "resp";
	

	private static Long idGuardat;

	@Autowired
    EntitatRepository entitatRepository;

	@Test
    public void crud() {
		Entitat entitat = EntitatTestUtil.createModelObject(
				null,
				CODI,
				NOM,
				CIF,
				RESP);
		Entitat guardada = entitatRepository.save(entitat);
		assertNotNull(guardada);
		idGuardat = guardada.getId();
		Entitat obtinguda = entitatRepository.findById(idGuardat).orElse(null);
		assertEntitat(guardada, obtinguda);
		List<Entitat> entitatsAll = entitatRepository.findAll();
		assertTrue(entitatsAll.size() > 0);
		entitatRepository.deleteById(idGuardat);
		Entitat esborrada = entitatRepository.findById(idGuardat).orElse(null);
		assertNull(esborrada);
	}



	private void assertEntitat(Entitat expected, Entitat actual) {
		assertEquals(expected.getId(), actual.getId());
		assertEquals(expected.getNom(), actual.getNom());
		assertEquals(expected.getVersion(), actual.getVersion());
		assertEquals(expected.getCreatedBy(), actual.getCreatedBy());
		assertEquals(expected.getCreatedDate(), actual.getCreatedDate());
		assertEquals(expected.getLastModifiedBy(), actual.getLastModifiedBy());
		assertEquals(expected.getLastModifiedDate(), actual.getLastModifiedDate());
	}

}
