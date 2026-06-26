package es.caib.pinbal.logic.service;

import es.caib.pinbal.logic.helper.CacheHelper;
import es.caib.pinbal.logic.helper.DtoMappingHelper;
import es.caib.pinbal.logic.helper.PaginacioHelper;
import es.caib.pinbal.logic.helper.UsuariHelper;
import es.caib.pinbal.logic.intf.dto.CodiValor;
import es.caib.pinbal.logic.intf.dto.ProcedimentDto;
import es.caib.pinbal.logic.intf.service.exception.EntitatNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.ProcedimentNotFoundException;
import es.caib.pinbal.persist.entity.Entitat;
import es.caib.pinbal.persist.entity.OrganGestor;
import es.caib.pinbal.persist.entity.Procediment;
import es.caib.pinbal.persist.repository.EntitatRepository;
import es.caib.pinbal.persist.repository.EntitatServeiRepository;
import es.caib.pinbal.persist.repository.EntitatUsuariRepository;
import es.caib.pinbal.persist.repository.OrganGestorRepository;
import es.caib.pinbal.persist.repository.ProcedimentRepository;
import es.caib.pinbal.persist.repository.ProcedimentServeiRepository;
import es.caib.pinbal.persist.repository.ServeiConfigRepository;
import es.caib.pinbal.persist.repository.ServeiRepository;
import ma.glasnost.orika.MapperFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.acls.model.MutableAclService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class ProcedimentServiceImplTest {

    @Mock private ProcedimentRepository procedimentRepository;
    @Mock private EntitatRepository entitatRepository;
    @Mock private EntitatServeiRepository entitatServeiRepository;
    @Mock private EntitatUsuariRepository entitatUsuariRepository;
    @Mock private ProcedimentServeiRepository procedimentServeiRepository;
    @Mock private OrganGestorRepository organGestorRepository;
    @Mock private ServeiRepository serveiRepository;
    @Mock private ServeiConfigRepository serveiConfigRepository;
    @Mock private DtoMappingHelper dtoMappingHelper;
    @Mock private UsuariHelper usuariHelper;
    @Mock private PaginacioHelper paginacioHelper;
    @Mock private CacheHelper cacheHelper;
    @Mock private MutableAclService aclService;
    @Mock private MapperFacade mapperFacade;

    @InjectMocks
    private ProcedimentServiceImpl procedimentService;

    private Entitat entitatMock;
    private Procediment procedimentMock;

    @BeforeEach
    public void setUp() {
        when(dtoMappingHelper.getMapperFacade()).thenReturn(mapperFacade);
        entitatMock = mock(Entitat.class);
        when(entitatMock.getCodi()).thenReturn("ENT01");
        procedimentMock = mock(Procediment.class);
        when(procedimentMock.getEntitat()).thenReturn(entitatMock);
    }

    @Test
    public void delete_noExisteix_llancaException() {
        when(procedimentRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ProcedimentNotFoundException.class, () -> procedimentService.delete(99L));
    }

    @Test
    public void delete_existeix_esborraIRetornaDto() throws ProcedimentNotFoundException {
        when(procedimentRepository.findById(1L)).thenReturn(Optional.of(procedimentMock));
        ProcedimentDto dto = new ProcedimentDto();
        when(mapperFacade.map(procedimentMock, ProcedimentDto.class)).thenReturn(dto);

        ProcedimentDto result = procedimentService.delete(1L);

        assertNotNull(result);
        verify(procedimentRepository).delete(procedimentMock);
        verify(cacheHelper).evictProcedimentsPerEntitat("ENT01");
    }

    @Test
    public void findAmbEntitat_noExisteix_llancaException() {
        when(entitatRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntitatNotFoundException.class, () -> procedimentService.findAmbEntitat(99L));
    }

    @Test
    public void findAmbEntitat_existeix_retornaLlista() throws EntitatNotFoundException {
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitatMock));
        when(procedimentRepository.findByEntitatOrderByNomAsc(entitatMock)).thenReturn(Collections.singletonList(procedimentMock));
        ProcedimentDto dto = new ProcedimentDto();
        when(mapperFacade.mapAsList(anyList(), eq(ProcedimentDto.class))).thenReturn(Collections.singletonList(dto));

        List<ProcedimentDto> result = procedimentService.findAmbEntitat(1L);

        assertEquals(1, result.size());
    }

    @Test
    public void findAmbEntitatAmbFiltre_noExisteix_llancaException() {
        when(entitatRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntitatNotFoundException.class, () -> procedimentService.findAmbEntitat(99L, "filtre"));
    }

    @Test
    public void findAmbEntitatAmbFiltre_existeix_retornaLlista() throws EntitatNotFoundException {
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitatMock));
        when(procedimentRepository.findByEntitatAndFiltreOrderByNomAsc(eq(entitatMock), anyBoolean(), any())).thenReturn(Collections.singletonList(procedimentMock));
        ProcedimentDto dto = new ProcedimentDto();
        when(mapperFacade.mapAsList(anyList(), eq(ProcedimentDto.class))).thenReturn(Collections.singletonList(dto));

        List<ProcedimentDto> result = procedimentService.findAmbEntitat(1L, "filtre");

        assertEquals(1, result.size());
    }

    @Test
    public void findAmbEntitatPerOrigen_noExisteix_llancaException() {
        when(entitatRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntitatNotFoundException.class, () -> procedimentService.findAmbEntitatPerOrigen(99L));
    }

    @Test
    public void findAmbEntitatPerOrigen_existeix_retornaCodiValors() throws EntitatNotFoundException {
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitatMock));
        when(procedimentMock.getCodiSia()).thenReturn("SIA001");
        when(procedimentMock.getCodi()).thenReturn("PROC01");
        when(procedimentMock.getNom()).thenReturn("Procediment Test");
        when(procedimentRepository.findByEntitatIdPerOrigen(1L)).thenReturn(Collections.singletonList(procedimentMock));

        List<CodiValor> result = procedimentService.findAmbEntitatPerOrigen(1L);

        assertEquals(1, result.size());
        assertEquals("SIA001", result.get(0).getCodi());
    }

    @Test
    public void findAmbEntitatPerOrigen_llistaNula_retornaLlistaVuida() throws EntitatNotFoundException {
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitatMock));
        when(procedimentRepository.findByEntitatIdPerOrigen(1L)).thenReturn(null);

        List<CodiValor> result = procedimentService.findAmbEntitatPerOrigen(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    public void findById_existeix_retornaDto() throws ProcedimentNotFoundException {
        when(procedimentRepository.findById(1L)).thenReturn(Optional.of(procedimentMock));
        ProcedimentDto dto = new ProcedimentDto();
        when(mapperFacade.map(procedimentMock, ProcedimentDto.class)).thenReturn(dto);

        ProcedimentDto result = procedimentService.findById(1L);

        assertNotNull(result);
    }

    @Test
    public void findById_noExisteix_retornaNull() {
        when(procedimentRepository.findById(99L)).thenReturn(Optional.empty());
        ProcedimentDto result = procedimentService.findById(99L);
        assertNull(result);
    }

    @Test
    public void create_entitatNoExisteix_llancaException() {
        ProcedimentDto dto = new ProcedimentDto();
        dto.setEntitatId(99L);
        when(entitatRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntitatNotFoundException.class, () -> procedimentService.create(dto));
    }
}
