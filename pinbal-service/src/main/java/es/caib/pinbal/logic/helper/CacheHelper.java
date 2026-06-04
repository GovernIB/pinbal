package es.caib.pinbal.logic.helper;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@Component
public class CacheHelper {

    private final CacheManager cacheManager;

    @Getter
    @Setter
    private static String appVersion;

    @CacheEvict(value = "procediments", key = "#entitatCodi")
    public void evictProcedimentsPerEntitat(String entitatCodi){}

    @CacheEvict(value = "serveis", allEntries = true)
    public void evictServeis(){}

    @CacheEvict(value = "serveisEntitat", key = "#entitatCodi")
    public void evictServeisEntitat(String entitatCodi){}

    @CacheEvict(value = "serveisProcediment", key = "#procedimentCodi")
    public void evictServeisProcediment(String procedimentCodi){}

    @CacheEvict(value = "dadesEspecifiques", key = "#serveiCodi")
    public void evictDadesEspecifiques(String serveiCodi){}

    public void evictEnumeratsPerServei(String serveiCodi) {
        evictByKeyPrefix("enumerats", serveiCodi);
    }

    @CacheEvict(value = "serveiPermesosPerDelegat", allEntries = true)
    public void evictPermisosPerDelegat() {}

    public void evictPermisosPerDelegat(String usuariCodi) {
        evictByKeyPrefix("serveiPermesosPerDelegat", usuariCodi);
    }

    public void evictByKeyPrefix(String cacheName, String keyPrefix) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            org.ehcache.Cache<Object, Object> ehCache = (org.ehcache.Cache<Object, Object>) cache.getNativeCache();
            ehCache.forEach(entry -> {
                String keyString = String.valueOf(entry.getKey());
                if (keyString.startsWith(keyPrefix + ":")) {
                    cache.evict(entry.getKey());
                }
            });
        }
    }


    public void clearCache(String cacheName) {

        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        }
    }

    public void clearAllCaches() {

        for(String cacheName : cacheManager.getCacheNames()) {
            clearCache(cacheName);
        }
    }

    public long getCacheSize(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            Object nativeCache = cache.getNativeCache();
            if (nativeCache instanceof org.ehcache.Cache) {
                org.ehcache.Cache<Object, Object> ehCache = (org.ehcache.Cache<Object, Object>) nativeCache;
                long count = 0;
                ehCache.forEach(entry -> {
                });
                for (org.ehcache.Cache.Entry<Object, Object> entry : ehCache) {
                    count++;
                }
                return count;
            }
        }
        return 0L;
    }

    public long getTotalEhCacheSize() {

        Long totalSize = 0L;
        for (String cacheName : cacheManager.getCacheNames()) {
            totalSize = getCacheSize(cacheName);
        }
        return totalSize;
    }

    public Collection<String> getAllCaches() {
        return cacheManager.getCacheNames();
    }
}
