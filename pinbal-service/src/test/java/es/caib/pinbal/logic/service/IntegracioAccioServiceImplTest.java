package es.caib.pinbal.logic.service;

import es.caib.pinbal.logic.helper.ConfigHelper;
import es.caib.pinbal.logic.helper.DtoMappingHelper;
import es.caib.pinbal.logic.helper.IntegracioHelper;
import es.caib.pinbal.logic.helper.PaginacioHelper;
import es.caib.pinbal.logic.intf.dto.IntegracioAccioDto;
import es.caib.pinbal.logic.intf.dto.IntegracioAccioEstatEnumDto;
import es.caib.pinbal.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.pinbal.logic.intf.dto.IntegracioDto;
import es.caib.pinbal.logic.intf.dto.IntegracioFiltreDto;
import es.caib.pinbal.logic.intf.dto.PaginaDto;
import es.caib.pinbal.logic.intf.dto.PaginacioAmbOrdreDto;
import es.caib.pinbal.logic.intf.dto.IntegracioAccioParamDto;
import es.caib.pinbal.persist.entity.IntegracioAccioEntity;
import es.caib.pinbal.persist.entity.IntegracioAccioParamEntity;
import es.caib.pinbal.persist.repository.IntegracioAccioParamRepository;
import es.caib.pinbal.persist.repository.IntegracioAccioRepository;
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
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class IntegracioAccioServiceImplTest {

    @Mock private IntegracioAccioRepository integracioAccioRepository;
    @Mock private IntegracioAccioParamRepository integracioAccioParamRepository;
    @Mock private DtoMappingHelper dtoMappingHelper;
    @Mock private PaginacioHelper paginacioHelper;
    @Mock private ConfigHelper configHelper;
    @Mock private PlatformTransactionManager transactionManager;
    @Mock private IntegracioHelper integracioHelper;

    @InjectMocks
    private IntegracioAccioServiceImpl integracioAccioService;

    @BeforeEach
    public void setUp() {
        integracioAccioService.setIntegracioHelper(integracioHelper);
    }

    @Test
    public void create_guardaAccioIParametres() {
        IntegracioAccioDto dto = buildAccioDto();
        IntegracioAccioEntity entity = mock(IntegracioAccioEntity.class);
        when(entity.getParametres()).thenReturn(Collections.emptyList());
        when(integracioAccioRepository.save(any())).thenReturn(entity);
        IntegracioAccioDto expected = new IntegracioAccioDto();
        when(dtoMappingHelper.convertir(entity, IntegracioAccioDto.class)).thenReturn(expected);

        IntegracioAccioDto result = integracioAccioService.create(dto);

        assertNotNull(result);
        verify(integracioAccioRepository).save(any(IntegracioAccioEntity.class));
    }

    @Test
    public void create_ambParametres_guardaParametres() {
        IntegracioAccioDto dto = buildAccioDto();
        IntegracioAccioParamDto param = new IntegracioAccioParamDto();
        param.setNom("codi");
        param.setDescripcio("EXP001");
        dto.setParametres(Collections.singletonList(param));

        IntegracioAccioEntity entity = mock(IntegracioAccioEntity.class);
        when(entity.getParametres()).thenReturn(new java.util.ArrayList<>());
        when(integracioAccioRepository.save(any())).thenReturn(entity);
        when(dtoMappingHelper.convertir(entity, IntegracioAccioDto.class)).thenReturn(new IntegracioAccioDto());
        IntegracioAccioParamEntity paramEntity = mock(IntegracioAccioParamEntity.class);
        when(integracioAccioParamRepository.save(any())).thenReturn(paramEntity);

        integracioAccioService.create(dto);

        verify(integracioAccioParamRepository).save(any(IntegracioAccioParamEntity.class));
    }

    @Test
    public void findPaginat_senseFiltresData_executaConsulta() {
        IntegracioFiltreDto filtre = new IntegracioFiltreDto();
        filtre.setCodi("ARXIU");
        PaginacioAmbOrdreDto paginacio = new PaginacioAmbOrdreDto();
        paginacio.setPaginaNum(0);
        paginacio.setPaginaTamany(10);

        Page<IntegracioAccioEntity> page = new PageImpl<>(Collections.emptyList());
        when(paginacioHelper.toSpringDataPageable(any(PaginacioAmbOrdreDto.class))).thenReturn(mock(Pageable.class));
        when(integracioAccioRepository.findByFiltrePaginat(
                anyString(), anyBoolean(), any(), any(), anyBoolean(), anyString(),
                anyBoolean(), any(), anyBoolean(), any(), anyBoolean(), anyString(), any()))
                .thenReturn(page);
        PaginaDto<IntegracioAccioDto> pageDto = new PaginaDto<>();
        when(paginacioHelper.toPaginaDto(page, IntegracioAccioDto.class)).thenReturn(pageDto);

        PaginaDto<IntegracioAccioDto> result = integracioAccioService.findPaginat(paginacio, filtre);

        assertNotNull(result);
    }

    @Test
    public void delete_ambCodi_esborraParametresIAccions() {
        when(integracioAccioRepository.countByCodi("ARXIU")).thenReturn(5);

        int result = integracioAccioService.delete("ARXIU");

        assertEquals(5, result);
        verify(integracioAccioParamRepository).deleteByIntegracioAccioCodi("ARXIU");
        verify(integracioAccioRepository).deleteByCodiMonitor("ARXIU");
    }

    @Test
    public void delete_codiNull_retornaZero() {
        int result = integracioAccioService.delete(null);

        assertEquals(0, result);
        verify(integracioAccioRepository, never()).deleteByCodiMonitor(any());
    }

    @Test
    public void deleteAll_esborraTotsElsRegistres() {
        when(integracioAccioRepository.countAll()).thenReturn(100);

        int result = integracioAccioService.deleteAll();

        assertEquals(100, result);
        verify(integracioAccioParamRepository).deleteAll();
        verify(integracioAccioRepository).deleteAll();
    }

    @Test
    public void esborrarDadesAntigues_dataNull_retornaZero() {
        int result = integracioAccioService.esborrarDadesAntigues(null);
        assertEquals(0, result);
        verify(integracioAccioRepository, never()).countMonitorByDataBefore(any());
    }

    @Test
    public void esborrarDadesAntigues_ambData_comptaIEsborra() {
        Date data = new Date();
        when(integracioAccioRepository.countMonitorByDataBefore(data)).thenReturn(10);

        int result = integracioAccioService.esborrarDadesAntigues(data);

        assertEquals(10, result);
        verify(integracioAccioParamRepository).deleteDataBefore(data);
        verify(integracioAccioRepository).deleteDataBefore(data);
    }

    @Test
    public void countErrors_retornaMapa() {
        List<Object[]> resultats = Arrays.asList(
                new Object[]{"ARXIU", 5L},
                new Object[]{"USUARIS", 2L}
        );
        when(integracioAccioRepository.countErrorsGroupByCodi(any(Date.class))).thenReturn(resultats);

        Map<String, Integer> result = integracioAccioService.countErrors(24);

        assertEquals(2, result.size());
        assertEquals(5, result.get("ARXIU"));
        assertEquals(2, result.get("USUARIS"));
    }

    @Test
    public void integracioFindAll_delegaAlIntegracioHelper() {
        List<IntegracioDto> expected = Collections.singletonList(new IntegracioDto());
        when(integracioHelper.findAll()).thenReturn(expected);

        List<IntegracioDto> result = integracioAccioService.integracioFindAll();

        assertSame(expected, result);
    }

    @Test
    public void getAll_omplNumErrors() {
        IntegracioDto integracio = new IntegracioDto();
        integracio.setCodi("ARXIU");
        when(integracioHelper.findAll()).thenReturn(Collections.singletonList(integracio));
        when(integracioAccioRepository.countErrorsGroupByCodi()).thenReturn(
                Collections.singletonList(new Object[]{"ARXIU", 3L}));

        List<IntegracioDto> result = integracioAccioService.getAll();

        assertEquals(1, result.size());
        assertEquals(3, result.get(0).getNumErrors());
    }

    private IntegracioAccioDto buildAccioDto() {
        IntegracioAccioDto dto = new IntegracioAccioDto();
        dto.setCodi("ARXIU");
        dto.setIdPeticio("PET001");
        dto.setData(new Date());
        dto.setDescripcio("Desc");
        dto.setTipus(IntegracioAccioTipusEnumDto.ENVIAMENT);
        dto.setTempsResposta(100L);
        dto.setEstat(IntegracioAccioEstatEnumDto.OK);
        dto.setParametres(Collections.emptyList());
        return dto;
    }
}
