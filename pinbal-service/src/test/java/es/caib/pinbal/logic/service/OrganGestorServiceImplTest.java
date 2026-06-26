package es.caib.pinbal.logic.service;

import es.caib.pinbal.logic.helper.DtoMappingHelper;
import es.caib.pinbal.logic.helper.PaginacioHelper;
import es.caib.pinbal.logic.helper.PluginHelper;
import es.caib.pinbal.logic.intf.dto.OrganGestorDto;
import es.caib.pinbal.logic.intf.dto.OrganGestorEstatEnum;
import es.caib.pinbal.logic.intf.dto.PaginacioAmbOrdreDto;
import es.caib.pinbal.persist.entity.Entitat;
import es.caib.pinbal.persist.entity.OrganGestor;
import es.caib.pinbal.persist.repository.EntitatRepository;
import es.caib.pinbal.persist.repository.OrganGestorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrganGestorServiceImplTest {

    @Mock private OrganGestorRepository organGestorRepository;
    @Mock private EntitatRepository entitatRepository;
    @Mock private DtoMappingHelper dtoMappingHelper;
    @Mock private PluginHelper pluginHelper;
    @Mock private PaginacioHelper paginacioHelper;

    @InjectMocks
    private OrganGestorServiceImpl organGestorService;

    private Entitat entitat;
    private OrganGestor organ;
    private OrganGestorDto organDto;

    @BeforeEach
    public void setUp() {
        entitat = new Entitat();
        ReflectionTestUtils.setField(entitat, "id", 1L);
        entitat.setNom("Entitat Test");

        organ = new OrganGestor();
        ReflectionTestUtils.setField(organ, "id", 10L);
        organ.setCodi("A04000001");
        organ.setNom("Direcció General");
        organ.setEntitat(entitat);

        organDto = new OrganGestorDto();
        organDto.setId(10L);
        organDto.setCodi("A04000001");
        organDto.setNom("Direcció General");
    }

    @Test
    public void findAll_retornaTotsElsOrgans() {
        when(organGestorRepository.findAll()).thenReturn(Collections.singletonList(organ));
        when(dtoMappingHelper.convertirList(anyList(), eq(OrganGestorDto.class)))
                .thenReturn(Collections.singletonList(organDto));

        List<OrganGestorDto> result = organGestorService.findAll();

        assertEquals(1, result.size());
    }

    @Test
    public void findItem_organExisteix_retornaDto() {
        when(organGestorRepository.findById(10L)).thenReturn(Optional.of(organ));
        when(dtoMappingHelper.convertir(organ, OrganGestorDto.class)).thenReturn(organDto);

        OrganGestorDto result = organGestorService.findItem(10L);

        assertNotNull(result);
        assertEquals("A04000001", result.getCodi());
    }

    @Test
    public void findItem_organNoExisteix_retornaNull() {
        when(organGestorRepository.findById(99L)).thenReturn(Optional.empty());
        when(dtoMappingHelper.convertir(null, OrganGestorDto.class)).thenReturn(null);

        assertNull(organGestorService.findItem(99L));
    }

    @Test
    public void findByEntitat_entitatNoExisteix_retornaLluidaBuida() {
        when(entitatRepository.findById(99L)).thenReturn(Optional.empty());

        List<OrganGestorDto> result = organGestorService.findByEntitat(99L);

        assertTrue(result.isEmpty());
    }

    @Test
    public void findByEntitat_entitatExisteix_retornaOrgans() {
        entitat.setOrganGestors(Collections.singletonList(organ));
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitat));
        when(dtoMappingHelper.convertirList(anyList(), eq(OrganGestorDto.class)))
                .thenReturn(Collections.singletonList(organDto));

        List<OrganGestorDto> result = organGestorService.findByEntitat(1L);

        assertEquals(1, result.size());
    }

    @Test
    public void findActivesByEntitat_retornaOrgansActius() {
        when(organGestorRepository.findByEntitatIdAndActiuIsTrue(1L))
                .thenReturn(Collections.singletonList(organ));
        when(dtoMappingHelper.convertirList(anyList(), eq(OrganGestorDto.class)))
                .thenReturn(Collections.singletonList(organDto));

        List<OrganGestorDto> result = organGestorService.findActivesByEntitat(1L);

        assertEquals(1, result.size());
    }

    @Test
    public void findByEntitatAmbFiltre_ambFiltre_retornaOrgans() {
        when(organGestorRepository.findByEntitatAndCodiNom(1L, "Dir"))
                .thenReturn(Collections.singletonList(organ));
        when(dtoMappingHelper.convertirList(anyList(), eq(OrganGestorDto.class)))
                .thenReturn(Collections.singletonList(organDto));

        List<OrganGestorDto> result = organGestorService.findByEntitatAmbFiltre(1L, "Dir");

        assertEquals(1, result.size());
    }

    @Test
    public void syncDir3OrgansGestors_unitatArrelBuida_llancaException() {
        entitat.setUnitatArrel("");
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitat));

        assertThrows(Exception.class, () -> organGestorService.syncDir3OrgansGestors(1L));
    }

    @Test
    public void syncDir3OrgansGestors_unitatArrelNull_llancaException() {
        entitat.setUnitatArrel(null);
        when(entitatRepository.findById(1L)).thenReturn(Optional.of(entitat));

        assertThrows(Exception.class, () -> organGestorService.syncDir3OrgansGestors(1L));
    }
}
