package es.caib.pinbal.logic.helper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CacheHelperTest {

    @Mock
    private CacheManager cacheManager;

    @Mock
    private PluginHelper pluginHelper;

    @InjectMocks
    private CacheHelper cacheHelper;

    @BeforeEach
    public void setUp() {
        // pluginHelper s'injecta amb @Lazy (setter) per trencar un cicle de dependències, i
        // @InjectMocks només fa injecció per constructor quan n'hi ha un de disponible.
        cacheHelper.setPluginHelper(pluginHelper);
    }

    @Test
    public void clearCache_cacheExisteix_neteja() {
        Cache cache = mock(Cache.class);
        when(cacheManager.getCache("serveis")).thenReturn(cache);

        cacheHelper.clearCache("serveis");

        verify(cache).clear();
        verify(pluginHelper, never()).resetPlugins(anyString());
    }

    @Test
    public void clearCache_usuariAmbCodi_tambeReiniciaPluginDadesUsuari() {
        Cache cache = mock(Cache.class);
        when(cacheManager.getCache("usuariAmbCodi")).thenReturn(cache);

        cacheHelper.clearCache("usuariAmbCodi");

        verify(cache).clear();
        verify(pluginHelper).resetPlugins("USUARIS");
    }

    @Test
    public void clearCache_usuariAmbNif_tambeReiniciaPluginDadesUsuari() {
        Cache cache = mock(Cache.class);
        when(cacheManager.getCache("usuariAmbNif")).thenReturn(cache);

        cacheHelper.clearCache("usuariAmbNif");

        verify(cache).clear();
        verify(pluginHelper).resetPlugins("USUARIS");
    }

    @Test
    public void clearCache_cacheNoExisteix_noCridaExcepcio() {
        when(cacheManager.getCache("inexistent")).thenReturn(null);

        assertDoesNotThrow(() -> cacheHelper.clearCache("inexistent"));
        verify(cacheManager).getCache("inexistent");
    }

    @Test
    public void clearAllCaches_netejaTotes() {
        Cache cache1 = mock(Cache.class);
        Cache cache2 = mock(Cache.class);
        Collection<String> names = Arrays.asList("serveis", "procediments");
        when(cacheManager.getCacheNames()).thenReturn(names);
        when(cacheManager.getCache("serveis")).thenReturn(cache1);
        when(cacheManager.getCache("procediments")).thenReturn(cache2);

        cacheHelper.clearAllCaches();

        verify(cache1).clear();
        verify(cache2).clear();
    }

    @Test
    public void getAllCaches_retornaNomsCaches() {
        Collection<String> names = Arrays.asList("serveis", "procediments", "enumerats");
        when(cacheManager.getCacheNames()).thenReturn(names);

        Collection<String> result = cacheHelper.getAllCaches();

        assertEquals(3, result.size());
        assertTrue(result.contains("serveis"));
    }

    @Test
    public void appVersion_setGet_funciona() {
        CacheHelper.setAppVersion("1.2.3");
        assertEquals("1.2.3", CacheHelper.getAppVersion());
    }

    @Test
    public void getCacheSize_cacheNull_retornaZero() {
        when(cacheManager.getCache("inexistent")).thenReturn(null);

        long size = cacheHelper.getCacheSize("inexistent");

        assertEquals(0L, size);
    }
}
