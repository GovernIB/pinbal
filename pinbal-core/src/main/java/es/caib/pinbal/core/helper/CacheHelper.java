package es.caib.pinbal.core.helper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Objects;

@Slf4j
@Component
public class CacheHelper {

    @Resource
    private CacheManager cacheManager;

    @CacheEvict(value = "procediments", key = "#entitatCodi")
    public void evictProcedimentsPerEntitat(String entitatCodi){}

    @CacheEvict(value = "serveis")
    public void evictServeis(){}

    @CacheEvict(value = "serveisEntitat", key = "#entitatCodi")
    public void evictServeisEntitat(String entitatCodi){}

    @CacheEvict(value = "serveisProcediment", key = "#procedimentCodi")
    public void evictServeisProcediment(String procedimentCodi){}

    @CacheEvict(value = "dadesEspecifiques", key = "#serveiCodi")
    public void evictDadesEspecifiques(String serveiCodi){}

    public void evictEnumeratsPerServei(String serveiCodi) {

        Cache cache = cacheManager.getCache("enumerats");

        if (cache != null) {
            net.sf.ehcache.Ehcache nativeCache = (net.sf.ehcache.Ehcache) Objects.requireNonNull(cache.getNativeCache());
            // Itera per la llista de claus a EhCache
            for (Object key : nativeCache.getKeys()) {
                String keyString = String.valueOf(key);
                // Si la key comença amb "serveiCodi:"
                if (keyString.startsWith(serveiCodi + ":")) {
                    cache.evict(key); // Evict de la key
                }
            }
        }
    }

//    public void clearCache(String key) {
//        var cache = cacheManager.getCache("conCache");
//        if (cache != null) {
//            cache.evict(key);
//        }
//    }

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

//    public long getCacheSize(String cacheName) {
//
//        try {
//            Cache cache = cacheManager.getCache(cacheName);
//            if (cache == null) {
//                return 0L;
//            }
//            javax.cache.Cache c = (javax.cache.Cache)cache.getNativeCache();
//            return StreamSupport.stream(Spliterators.spliteratorUnknownSize(c.iterator(), Spliterator.ORDERED), false).count();
//        } catch (Exception ex) {
//            log.error("Error obtenint mida de la cache " + cacheName, ex);
//            return 0L;
//        }
//    }

    public long getCacheSize(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        Object nativeCache = cache.getNativeCache();
        if (nativeCache instanceof net.sf.ehcache.Ehcache) {
            net.sf.ehcache.Ehcache ehCache = (net.sf.ehcache.Ehcache) nativeCache;
            return ehCache.getStatistics().getLocalHeapSizeInBytes();
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
