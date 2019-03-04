/**
 * 
 */
package es.caib.pinbal.core.service;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.model.Entitat;
import es.caib.pinbal.core.model.EntitatTestUtil;
import es.caib.pinbal.core.repository.EntitatRepository;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;

/**
 * Test unitari per al servei de manteniment d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EntitatServiceRepositoryTest {

	private static final Long ENTITAT_ID = Long.valueOf(5);
	private static final String CODI = "PPT";
	private static final String NOM = "Pepet";
	private static final String CIF = "00000000T";
	private static final String RESP = "resp";

	private EntitatServiceImpl entitatService;

	private EntitatRepository entitatRepositoryMock;

	@Before
	public void setUp() {
		entitatService = new EntitatServiceImpl();
		entitatRepositoryMock = mock(EntitatRepository.class);
		entitatService.setEntitatRepository(entitatRepositoryMock);
	}

	@Test
	public void create(){
		EntitatDto created = EntitatTestUtil.createDto(null, NOM);
		Entitat persisted = EntitatTestUtil.createModelObject(
				ENTITAT_ID,
				CODI,
				NOM,
				CIF,
				RESP);
		try {
			when(entitatRepositoryMock.save(any(Entitat.class))).thenReturn(
					persisted);
		} catch (Exception e) {
			e.printStackTrace();
		}
		EntitatDto returned = entitatService.create(created);
		ArgumentCaptor<Entitat> entitatArgument = ArgumentCaptor
				.forClass(Entitat.class);
		verify(entitatRepositoryMock, times(1)).save(entitatArgument.capture());
		verifyNoMoreInteractions(entitatRepositoryMock);
		assertEntitat(created, entitatArgument.getValue());
		assertEquals(persisted, returned);
	}

	@Test
	public void delete() throws EntitatNotFoundException {
		Entitat deleted = EntitatTestUtil.createModelObject(
				ENTITAT_ID,
				CODI,
				NOM,
				CIF,
				RESP);
		when(entitatRepositoryMock.findOne(ENTITAT_ID)).thenReturn(deleted);
		EntitatDto returned = entitatService.delete(ENTITAT_ID);
		verify(entitatRepositoryMock, times(1)).findOne(ENTITAT_ID);
		verify(entitatRepositoryMock, times(1)).delete(deleted);
		verifyNoMoreInteractions(entitatRepositoryMock);
		assertEquals(deleted, returned);
	}

	@Test(expected = EntitatNotFoundException.class)
	public void deleteWhenPersonIsNotFound() throws EntitatNotFoundException {
		when(entitatRepositoryMock.findOne(ENTITAT_ID)).thenReturn(null);
		entitatService.delete(ENTITAT_ID);
		verify(entitatRepositoryMock, times(1)).findOne(ENTITAT_ID);
		verifyNoMoreInteractions(entitatRepositoryMock);
	}

	@Test
	public void findAll() {
		List<Entitat> persons = new ArrayList<Entitat>();
		when(entitatRepositoryMock.findAll()).thenReturn(persons);
		List<EntitatDto> returned = entitatService.findAll();
		verify(entitatRepositoryMock, times(1)).findAll();
		verifyNoMoreInteractions(entitatRepositoryMock);
		assertEquals(persons, returned);
	}

	@Test
	public void findById() {
		Entitat entitat = EntitatTestUtil.createModelObject(
				ENTITAT_ID,
				CODI,
				NOM,
				CIF,
				RESP);
		when(entitatRepositoryMock.findOne(ENTITAT_ID)).thenReturn(entitat);
		EntitatDto returned = entitatService.findById(ENTITAT_ID);
		verify(entitatRepositoryMock, times(1)).findOne(ENTITAT_ID);
		verifyNoMoreInteractions(entitatRepositoryMock);
		assertEquals(entitat, returned);
	}

	@Test
	public void update() throws EntitatNotFoundException {
		EntitatDto updated = EntitatTestUtil.createDto(ENTITAT_ID, NOM);
		Entitat entitat = EntitatTestUtil.createModelObject(
				ENTITAT_ID,
				CODI,
				NOM,
				CIF,
				RESP);
		when(entitatRepositoryMock.findOne(updated.getId())).thenReturn(entitat);
		EntitatDto returned = entitatService.update(updated);
		verify(entitatRepositoryMock, times(1)).findOne(updated.getId());
		verifyNoMoreInteractions(entitatRepositoryMock);
		assertEntitat(updated, returned);
	}

	@Test(expected = EntitatNotFoundException.class)
	public void updateWhenPersonIsNotFound() throws EntitatNotFoundException {
		EntitatDto updated = EntitatTestUtil.createDto(ENTITAT_ID, NOM);
		when(entitatRepositoryMock.findOne(updated.getId())).thenReturn(null);
		entitatService.update(updated);
		verify(entitatRepositoryMock, times(1)).findOne(updated.getId());
		verifyNoMoreInteractions(entitatRepositoryMock);
	}



	private void assertEntitat(EntitatDto expected, Entitat actual) {
		assertEquals(expected.getId(), actual.getId());
		assertEquals(expected.getNom(), actual.getNom());
	}
	private void assertEntitat(EntitatDto expected, EntitatDto actual) {
		assertEquals(expected.getId(), actual.getId());
		assertEquals(expected.getNom(), actual.getNom());
	}

}
