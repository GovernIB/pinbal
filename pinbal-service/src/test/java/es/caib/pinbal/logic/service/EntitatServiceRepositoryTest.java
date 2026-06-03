/**
 *
 */
package es.caib.pinbal.logic.service;

import es.caib.pinbal.logic.helper.CacheHelper;
import es.caib.pinbal.logic.helper.DtoMappingHelper;
import es.caib.pinbal.logic.intf.dto.EntitatDto;
import es.caib.pinbal.logic.intf.service.exception.EntitatNotFoundException;
import es.caib.pinbal.logic.model.EntitatTestUtil;
import es.caib.pinbal.persist.entity.Entitat;
import es.caib.pinbal.persist.repository.ClauPrivadaRepository;
import es.caib.pinbal.persist.repository.EntitatRepository;
import es.caib.pinbal.persist.repository.EntitatServeiRepository;
import es.caib.pinbal.persist.repository.EntitatUsuariRepository;
import es.caib.pinbal.persist.repository.ServeiConfigRepository;
import es.caib.pinbal.persist.repository.UsuariRepository;
import es.caib.pinbal.scsp.ScspHelper;
import ma.glasnost.orika.MapperFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Test unitari per al servei de manteniment d'entitats.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@ExtendWith(MockitoExtension.class)
public class EntitatServiceRepositoryTest {

	private static final Long ENTITAT_ID = Long.valueOf(5);
	private static final String CODI = "PPT";
	private static final String NOM = "Pepet";
	private static final String CIF = "00000000T";
	private static final String RESP = "resp";

	@InjectMocks
	private EntitatServiceImpl entitatService;

	@Mock
	private EntitatRepository entitatRepository;
	@Mock
	private EntitatServeiRepository entitatServeiRepository;
	@Mock
	private EntitatUsuariRepository entitatUsuariRepository;
	@Mock
	private ServeiConfigRepository serveiConfigRepository;
	@Mock
	private UsuariRepository usuariRepository;
	@Mock
	private DtoMappingHelper dtoMappingHelper;
	@Mock
	private CacheHelper cacheHelper;
	@Mock
	private ClauPrivadaRepository clauPrivadaRepository;
	@Mock
	private ScspHelper scspHelper;
	@Mock
	private MapperFacade mapperFacade;

	@BeforeEach
	public void setUp() {
		// ScspHelper és creat lazy usant applicationContext; l'injectem directament
		// per evitar NullPointerException en els mètodes que el criden.
		ReflectionTestUtils.setField(entitatService, "scspHelper", scspHelper);
		// lenient: no tots els tests arriben a cridar el mapper (p.ex. els que llancen excepció)
		lenient().when(dtoMappingHelper.getMapperFacade()).thenReturn(mapperFacade);
	}

	@Test
	public void create() {
		EntitatDto created = EntitatTestUtil.createDto(null, NOM);
		Entitat persisted = EntitatTestUtil.createModelObject(ENTITAT_ID, CODI, NOM, CIF, RESP);
		EntitatDto persistedDto = EntitatTestUtil.createDto(ENTITAT_ID, NOM);
		when(entitatRepository.save(any(Entitat.class))).thenReturn(persisted);
		when(mapperFacade.map(persisted, EntitatDto.class)).thenReturn(persistedDto);

		EntitatDto returned = entitatService.create(created);

		ArgumentCaptor<Entitat> entitatArgument = ArgumentCaptor.forClass(Entitat.class);
		verify(entitatRepository, times(1)).save(entitatArgument.capture());
		verifyNoMoreInteractions(entitatRepository);
		assertEntitat(created, entitatArgument.getValue());
		assertEquals(persistedDto, returned);
	}

	@Test
	public void delete() throws EntitatNotFoundException {
		Entitat deleted = EntitatTestUtil.createModelObject(ENTITAT_ID, CODI, NOM, CIF, RESP);
		EntitatDto deletedDto = EntitatTestUtil.createDto(ENTITAT_ID, NOM);
		when(entitatRepository.findById(ENTITAT_ID)).thenReturn(Optional.of(deleted));
		when(mapperFacade.map(deleted, EntitatDto.class)).thenReturn(deletedDto);

		EntitatDto returned = entitatService.delete(ENTITAT_ID);

		verify(entitatRepository, times(1)).findById(ENTITAT_ID);
		verify(entitatRepository, times(1)).delete(deleted);
		verifyNoMoreInteractions(entitatRepository);
		assertEquals(deletedDto, returned);
	}

	@Test
	public void deleteWhenPersonIsNotFound() throws EntitatNotFoundException {
		when(entitatRepository.findById(ENTITAT_ID)).thenReturn(Optional.empty());
		assertThrows(EntitatNotFoundException.class, () ->
				entitatService.delete(ENTITAT_ID)
		);
		verify(entitatRepository, times(1)).findById(ENTITAT_ID);
		verifyNoMoreInteractions(entitatRepository);
	}

	@Test
	public void findAll() {
		List<Entitat> entities = new ArrayList<>();
		List<EntitatDto> entityDtos = new ArrayList<>();
		when(entitatRepository.findAll(any(Sort.class))).thenReturn(entities);
		when(mapperFacade.mapAsList(entities, EntitatDto.class)).thenReturn(entityDtos);

		List<EntitatDto> returned = entitatService.findAll();

		verify(entitatRepository, times(1)).findAll(any(Sort.class));
		verifyNoMoreInteractions(entitatRepository);
		assertEquals(entityDtos, returned);
	}

	@Test
	public void findById() {
		Entitat entitat = EntitatTestUtil.createModelObject(ENTITAT_ID, CODI, NOM, CIF, RESP);
		EntitatDto entitatDto = EntitatTestUtil.createDto(ENTITAT_ID, NOM);
		when(entitatRepository.findById(ENTITAT_ID)).thenReturn(Optional.of(entitat));
		// findById al servei passa l'Optional directament al mapper
		when(mapperFacade.map(eq(Optional.of(entitat)), eq(EntitatDto.class))).thenReturn(entitatDto);

		EntitatDto returned = entitatService.findById(ENTITAT_ID);

		verify(entitatRepository, times(1)).findById(ENTITAT_ID);
		verifyNoMoreInteractions(entitatRepository);
		assertEquals(entitatDto, returned);
	}

	@Test
	public void update() throws EntitatNotFoundException {
		EntitatDto updated = EntitatTestUtil.createDto(ENTITAT_ID, NOM);
		Entitat entitat = EntitatTestUtil.createModelObject(ENTITAT_ID, CODI, NOM, CIF, RESP);
		EntitatDto updatedDto = EntitatTestUtil.createDto(ENTITAT_ID, NOM);
		when(entitatRepository.findById(updated.getId())).thenReturn(Optional.of(entitat));
		when(entitatServeiRepository.findByEntitat(entitat)).thenReturn(Collections.emptyList());
		when(mapperFacade.map(entitat, EntitatDto.class)).thenReturn(updatedDto);

		EntitatDto returned = entitatService.update(updated);

		verify(entitatRepository, times(1)).findById(updated.getId());
		verifyNoMoreInteractions(entitatRepository);
		assertEntitat(updated, returned);
	}

	@Test
	public void updateWhenPersonIsNotFound() throws EntitatNotFoundException {
		EntitatDto updated = EntitatTestUtil.createDto(ENTITAT_ID, NOM);
		when(entitatRepository.findById(updated.getId())).thenReturn(Optional.empty());
		assertThrows(EntitatNotFoundException.class, () ->
				entitatService.update(updated)
		);
		verify(entitatRepository, times(1)).findById(updated.getId());
		verifyNoMoreInteractions(entitatRepository);
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
