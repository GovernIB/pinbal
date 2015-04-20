/**
 * 
 */
package es.caib.pinbal.core.repository;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import es.caib.pinbal.core.model.Entitat;
import es.caib.pinbal.core.model.EntitatTestUtil;


/**
 * Test d'integració per al servei de manteniment d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/es/caib/pinbal/core/context/application-context-test.xml"})
@Transactional
public class EntitatRepositoryIntegrationTest {

	private static final String CODI = "PPT";
	private static final String NOM = "Pepet";
	private static final String CIF = "00000000T";
	private static final String RESP = "resp";
	

	private static Long idGuardat;

	@Resource
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
		Entitat obtinguda = entitatRepository.findOne(idGuardat);
		assertEntitat(guardada, obtinguda);
		List<Entitat> entitatsAll = entitatRepository.findAll();
		assertTrue(entitatsAll.size() > 0);
		entitatRepository.delete(idGuardat);
		Entitat esborrada = entitatRepository.findOne(idGuardat);
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
