package es.caib.pinbal.logic.service;

import es.caib.pinbal.logic.helper.CacheHelper;
import es.caib.pinbal.logic.helper.PaginacioHelper;
import es.caib.pinbal.logic.intf.dto.CacheDto;
import es.caib.pinbal.logic.intf.dto.PaginaDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AplicacioServiceImplTest {

    @Mock
    private CacheHelper cacheHelper;

    @Mock
    private PaginacioHelper paginacioHelper;

    @InjectMocks
    private AplicacioServiceImpl aplicacioService;

    @Test
    public void getAllCaches_retornaLlistaCaches() {
        when(cacheHelper.getAllCaches()).thenReturn(Arrays.asList("serveis", "procediments"));
        when(cacheHelper.getCacheSize("serveis")).thenReturn(5L);
        when(cacheHelper.getCacheSize("procediments")).thenReturn(3L);
        PaginaDto<CacheDto> pageDto = new PaginaDto<>();
        when(paginacioHelper.toPaginaDto(anyList(), eq(CacheDto.class))).thenReturn(pageDto);

        PaginaDto<CacheDto> result = aplicacioService.getAllCaches();

        assertNotNull(result);
        verify(paginacioHelper).toPaginaDto(anyList(), eq(CacheDto.class));
    }

    @Test
    public void getAllCaches_cachesBuides_retornaLlistaOrdenada() {
        when(cacheHelper.getAllCaches()).thenReturn(Collections.emptyList());
        PaginaDto<CacheDto> pageDto = new PaginaDto<>();
        when(paginacioHelper.toPaginaDto(anyList(), eq(CacheDto.class))).thenReturn(pageDto);

        PaginaDto<CacheDto> result = aplicacioService.getAllCaches();

        assertNotNull(result);
    }

    @Test
    public void getAllCaches_cacheAmbOrdreConescut_apareixPrimerACLlistat() {
        when(cacheHelper.getAllCaches()).thenReturn(Arrays.asList("serveis", "serveiDescripcio"));
        when(cacheHelper.getCacheSize(anyString())).thenReturn(0L);

        aplicacioService.getAllCaches();

        verify(paginacioHelper).toPaginaDto(argThat((List<CacheDto> list) ->
                list.size() == 2 && "serveiDescripcio".equals(list.get(0).getCodi())
        ), eq(CacheDto.class));
    }

    @Test
    public void removeCache_cridaClearCache() {
        aplicacioService.removeCache("serveis");
        verify(cacheHelper).clearCache("serveis");
    }

    @Test
    public void removeAllCaches_cridaClearAllCaches() {
        aplicacioService.removeAllCaches();
        verify(cacheHelper).clearAllCaches();
    }

    @Test
    public void setAppVersion_getAppVersion_persisteix() {
        try (MockedStatic<CacheHelper> mocked = mockStatic(CacheHelper.class)) {
            aplicacioService.setAppVersion("2.0.0");
            mocked.verify(() -> CacheHelper.setAppVersion("2.0.0"));
        }
    }

    @Test
    public void getAppVersion_delegaAlCacheHelper() {
        try (MockedStatic<CacheHelper> mocked = mockStatic(CacheHelper.class)) {
            mocked.when(CacheHelper::getAppVersion).thenReturn("3.0.0");
            assertEquals("3.0.0", aplicacioService.getAppVersion());
        }
    }
}
