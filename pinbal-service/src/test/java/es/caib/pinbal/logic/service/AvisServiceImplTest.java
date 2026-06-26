package es.caib.pinbal.logic.service;

import es.caib.pinbal.logic.helper.DtoMappingHelper;
import es.caib.pinbal.logic.intf.dto.AvisDto;
import es.caib.pinbal.logic.intf.dto.AvisNivellEnumDto;
import es.caib.pinbal.logic.intf.service.exception.NotFoundException;
import es.caib.pinbal.persist.entity.Avis;
import es.caib.pinbal.persist.repository.AvisRepository;
import ma.glasnost.orika.MapperFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class AvisServiceImplTest {

    @Mock
    private AvisRepository avisRepository;

    @Mock
    private DtoMappingHelper dtoMappingHelper;

    @Mock
    private MapperFacade mapperFacade;

    @InjectMocks
    private AvisServiceImpl avisService;

    private Avis avis;
    private AvisDto avisDto;

    @BeforeEach
    public void setUp() {
        avis = Avis.getBuilder("Assumpte", "Missatge", new Date(), null, AvisNivellEnumDto.INFO).build();
        ReflectionTestUtils.setField(avis, "id", 1L);

        avisDto = new AvisDto();
        avisDto.setId(1L);
        avisDto.setAssumpte("Assumpte");
        avisDto.setMissatge("Missatge");
        avisDto.setDataInici(new Date());
        avisDto.setAvisNivell(AvisNivellEnumDto.INFO);

        when(dtoMappingHelper.getMapperFacade()).thenReturn(mapperFacade);
    }

    @Test
    public void create_guardaIRetornaDto() {
        when(avisRepository.save(any(Avis.class))).thenReturn(avis);
        when(mapperFacade.map(avis, AvisDto.class)).thenReturn(avisDto);

        AvisDto result = avisService.create(avisDto);

        assertNotNull(result);
        verify(avisRepository).save(any(Avis.class));
    }

    @Test
    public void update_avisExisteix_actualitzaIRetornaDto() {
        when(avisRepository.findById(1L)).thenReturn(Optional.of(avis));
        when(mapperFacade.map(avis, AvisDto.class)).thenReturn(avisDto);

        AvisDto result = avisService.update(avisDto);

        assertNotNull(result);
        verify(avisRepository).findById(1L);
    }

    @Test
    public void update_avisNoExisteix_llancaNotFoundException() {
        when(avisRepository.findById(99L)).thenReturn(Optional.empty());
        avisDto.setId(99L);

        assertThrows(NotFoundException.class, () -> avisService.update(avisDto));
    }

    @Test
    public void updateActiva_avisExisteix_actualitzaActiu() {
        when(avisRepository.findById(1L)).thenReturn(Optional.of(avis));
        when(mapperFacade.map(avis, AvisDto.class)).thenReturn(avisDto);

        AvisDto result = avisService.updateActiva(1L, false);

        assertNotNull(result);
        verify(avisRepository).findById(1L);
    }

    @Test
    public void updateActiva_avisNoExisteix_llancaNotFoundException() {
        when(avisRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> avisService.updateActiva(99L, true));
    }

    @Test
    public void delete_avisExisteix_esborraIRetornaDto() {
        when(avisRepository.findById(1L)).thenReturn(Optional.of(avis));
        when(mapperFacade.map(avis, AvisDto.class)).thenReturn(avisDto);

        AvisDto result = avisService.delete(1L);

        assertNotNull(result);
        verify(avisRepository).delete(avis);
    }

    @Test
    public void delete_avisNoExisteix_llancaNotFoundException() {
        when(avisRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> avisService.delete(99L));
    }

    @Test
    public void findById_avisExisteix_retornaDto() {
        when(avisRepository.findById(1L)).thenReturn(Optional.of(avis));
        when(mapperFacade.map(avis, AvisDto.class)).thenReturn(avisDto);

        AvisDto result = avisService.findById(1L);

        assertNotNull(result);
    }

    @Test
    public void findById_avisNoExisteix_retornaNull() {
        when(avisRepository.findById(99L)).thenReturn(Optional.empty());
        when(mapperFacade.map(null, AvisDto.class)).thenReturn(null);

        assertNull(avisService.findById(99L));
    }

    @Test
    public void findPaginat_retornaPaginaDto() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Avis> page = new PageImpl<>(Collections.singletonList(avis), pageable, 1);
        when(avisRepository.findAll(pageable)).thenReturn(page);
        when(dtoMappingHelper.pageEntities2pageDto(page, AvisDto.class, pageable))
                .thenReturn(new PageImpl<>(Collections.singletonList(avisDto), pageable, 1));

        Page<AvisDto> result = avisService.findPaginat(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    public void findActive_retornaAvisosActius() {
        when(avisRepository.findActive(any(Date.class))).thenReturn(Collections.singletonList(avis));
        when(dtoMappingHelper.convertirList(anyList(), eq(AvisDto.class))).thenReturn(Collections.singletonList(avisDto));

        List<AvisDto> result = avisService.findActive();

        assertEquals(1, result.size());
    }

    @Test
    public void findAllIds_retornaIdsDesDelRepositori() {
        when(avisRepository.findAllIds()).thenReturn(Arrays.asList(1L, 2L, 3L));

        List<Long> result = avisService.findAllIds();

        assertEquals(3, result.size());
    }
}
