package es.caib.pinbal.logic.service;

import es.caib.pinbal.logic.helper.CacheHelper;
import es.caib.pinbal.logic.helper.DtoMappingHelper;
import es.caib.pinbal.logic.intf.dto.EntitatDto;
import es.caib.pinbal.logic.intf.dto.EntitatDto.EntitatTipusDto;
import es.caib.pinbal.logic.intf.service.exception.EntitatNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.EntitatServeiNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.ServeiNotFoundException;
import es.caib.pinbal.persist.entity.Entitat;
import es.caib.pinbal.persist.entity.Entitat.EntitatTipus;
import es.caib.pinbal.persist.entity.EntitatServei;
import es.caib.pinbal.persist.repository.ClauPrivadaRepository;
import es.caib.pinbal.persist.repository.EntitatRepository;
import es.caib.pinbal.persist.repository.EntitatServeiRepository;
import es.caib.pinbal.persist.repository.EntitatUsuariRepository;
import es.caib.pinbal.persist.repository.ServeiConfigRepository;
import es.caib.pinbal.persist.repository.UsuariRepository;
import es.caib.pinbal.scsp.ScspHelper;
import es.scsp.common.domain.core.Servicio;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class EntitatServiceImplTest {

    @Mock private EntitatRepository entitatRepository;
    @Mock private EntitatServeiRepository entitatServeiRepository;
    @Mock private EntitatUsuariRepository entitatUsuariRepository;
    @Mock private ServeiConfigRepository serveiConfigRepository;
    @Mock private UsuariRepository usuariRepository;
    @Mock private DtoMappingHelper dtoMappingHelper;
    @Mock private CacheHelper cacheHelper;
    @Mock private ClauPrivadaRepository clauPrivadaRepository;
    @Mock private MapperFacade mapperFacade;
    @Mock private ScspHelper scspHelper;

    @InjectMocks
    private EntitatServiceImpl entitatService;

    private Entitat entitatMock;

    @BeforeEach
    public void setUp() {
        when(dtoMappingHelper.getMapperFacade()).thenReturn(mapperFacade);
        // Inject the mock ScspHelper to bypass lazy initialization
        ReflectionTestUtils.setField(entitatService, "scspHelper", scspHelper);
        entitatMock = mock(Entitat.class);
        when(entitatMock.getCodi()).thenReturn("ENT01");
        when(entitatMock.getCif()).thenReturn("A07002132");
        when(entitatMock.getNom()).thenReturn("Entitat Test");
    }

    @Test
    public void findAll_retornaLlista() {
        when(entitatRepository.findAll(any(Sort.class))).thenReturn(Collections.singletonList(entitatMock));
        EntitatDto dto = new EntitatDto();
        when(mapperFacade.mapAsList(anyList(), eq(EntitatDto.class))).thenReturn(Collections.singletonList(dto));

        List<EntitatDto> result = entitatService.findAll();

        assertEquals(1, result.size());
    }

    @Test
    public void findById_existeix_retornaDto() {
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitatMock));
        EntitatDto dto = new EntitatDto();
        when(mapperFacade.map(entitatMock, EntitatDto.class)).thenReturn(dto);

        EntitatDto result = entitatService.findById(1L);

        assertNotNull(result);
    }

    @Test
    public void findByCodi_retornaDto() {
        when(entitatRepository.findByCodi("ENT01")).thenReturn(entitatMock);
        EntitatDto dto = new EntitatDto();
        when(mapperFacade.map(entitatMock, EntitatDto.class)).thenReturn(dto);

        EntitatDto result = entitatService.findByCodi("ENT01");

        assertNotNull(result);
    }

    @Test
    public void findByCif_retornaDto() {
        when(entitatRepository.findByCif("A07002132")).thenReturn(entitatMock);
        EntitatDto dto = new EntitatDto();
        when(mapperFacade.map(entitatMock, EntitatDto.class)).thenReturn(dto);

        EntitatDto result = entitatService.findByCif("A07002132");

        assertNotNull(result);
    }

    @Test
    public void findAmbFiltrePaginat_retornaPagina() {
        Page<Entitat> page = new PageImpl<>(Collections.singletonList(entitatMock));
        when(entitatRepository.findByFiltre(anyBoolean(), any(), anyBoolean(), any(),
                anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), any(),
                anyBoolean(), any(), any(Pageable.class))).thenReturn(page);
        EntitatDto dto = new EntitatDto();
        when(mapperFacade.mapAsList(anyList(), eq(EntitatDto.class))).thenReturn(Collections.singletonList(dto));

        Page<EntitatDto> result = entitatService.findAmbFiltrePaginat(null, null, null, null, null, Pageable.unpaged(), null);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    public void create_cridaScspIRepository() throws Exception {
        EntitatDto dto = new EntitatDto();
        dto.setCodi("ENT01");
        dto.setNom("Entitat Test");
        dto.setCif("A07002132");
        dto.setTipus(EntitatTipusDto.GOVERN);
        when(entitatRepository.save(any(Entitat.class))).thenReturn(entitatMock);
        when(mapperFacade.map(entitatMock, EntitatDto.class)).thenReturn(dto);

        EntitatDto result = entitatService.create(dto);

        assertNotNull(result);
        verify(scspHelper).organismoCesionarioSave(eq("A07002132"), eq("Entitat Test"), any(), isNull(), anyBoolean());
    }

    @Test
    public void delete_noExisteix_llancaException() {
        when(entitatRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntitatNotFoundException.class, () -> entitatService.delete(99L));
    }

    @Test
    public void delete_existeix_esborraIRetornaDto() throws EntitatNotFoundException {
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitatMock));
        EntitatDto dto = new EntitatDto();
        when(mapperFacade.map(entitatMock, EntitatDto.class)).thenReturn(dto);

        EntitatDto result = entitatService.delete(1L);

        assertNotNull(result);
        verify(scspHelper).organismoCesionarioDelete("A07002132");
        verify(entitatRepository).delete(entitatMock);
    }

    @Test
    public void updateActiva_noExisteix_llancaException() {
        when(entitatRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntitatNotFoundException.class, () -> entitatService.updateActiva(99L, true));
    }

    @Test
    public void updateActiva_existeix_actualitzaActiva() throws EntitatNotFoundException {
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitatMock));
        EntitatDto dto = new EntitatDto();
        when(mapperFacade.map(entitatMock, EntitatDto.class)).thenReturn(dto);

        EntitatDto result = entitatService.updateActiva(1L, false);

        assertNotNull(result);
        verify(entitatMock).updateActiva(false);
    }

    @Test
    public void addServei_entitatNoExisteix_llancaException() {
        when(entitatRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntitatNotFoundException.class, () -> entitatService.addServei(99L, "SV001"));
    }

    @Test
    public void addServei_serveiNoExisteix_llancaException() {
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitatMock));
        when(scspHelper.getServicio("SV001")).thenReturn(null);
        when(entitatMock.getEntitatServeis()).thenReturn(Collections.emptyList());
        assertThrows(ServeiNotFoundException.class, () -> entitatService.addServei(1L, "SV001"));
    }

    @Test
    public void removeServei_entitatNoExisteix_llancaException() {
        when(entitatRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntitatNotFoundException.class, () -> entitatService.removeServei(99L, "SV001"));
    }

    @Test
    public void removeServei_serveiNoExisteixEnEntitat_llancaException() {
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitatMock));
        when(entitatServeiRepository.findByEntitatIdAndServei(1L, "SV001")).thenReturn(null);
        assertThrows(EntitatServeiNotFoundException.class, () -> entitatService.removeServei(1L, "SV001"));
    }

    @Test
    public void removeServei_existeix_esborraServei() throws Exception {
        EntitatServei entitatServei = mock(EntitatServei.class);
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitatMock));
        when(entitatServeiRepository.findByEntitatIdAndServei(1L, "SV001")).thenReturn(entitatServei);
        when(entitatMock.getEntitatServeis()).thenReturn(Collections.emptyList());

        entitatService.removeServei(1L, "SV001");

        verify(entitatServeiRepository).delete(entitatServei);
    }
}
