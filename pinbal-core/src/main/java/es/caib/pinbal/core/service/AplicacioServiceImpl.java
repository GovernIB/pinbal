/**
 * 
 */
package es.caib.pinbal.core.service;

import es.caib.pinbal.core.dto.CacheDto;
import es.caib.pinbal.core.dto.PaginaDto;
import es.caib.pinbal.core.helper.CacheHelper;
import es.caib.pinbal.core.helper.IntegracioHelper;
import es.caib.pinbal.core.helper.PaginacioHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementació dels mètodes per a gestionar l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Service
public class AplicacioServiceImpl implements AplicacioService {

	@Resource
	private IntegracioHelper integracioHelper;
	@Resource
	private CacheHelper cacheHelper;
	@Resource
	private PaginacioHelper paginacioHelper;

	private static final Map<String, Integer> ordreCaches;

	static {
		ordreCaches = new HashMap<>();
		ordreCaches.put("serveiDescripcio", 0);
		ordreCaches.put("emisorNombre", 1);
		ordreCaches.put("clavePrivadaNombre", 2);
		ordreCaches.put("clavePrivadaNumeroSerie", 3);
		ordreCaches.put("clavePublicaNombre", 4);
		ordreCaches.put("clavePublicaNumeroSerie", 5);
		ordreCaches.put("procediments", 6);
		ordreCaches.put("serveis", 7);
		ordreCaches.put("serveisEntitat", 8);
		ordreCaches.put("serveisProcediment", 9);
		ordreCaches.put("dadesEspecifiques", 10);
		ordreCaches.put("enumerats", 11);
		ordreCaches.put("paisos", 12);
		ordreCaches.put("provincies", 13);
		ordreCaches.put("municipis", 14);
		ordreCaches.put("usuariAmbCodi", 15);
		ordreCaches.put("usuariAmbNif", 16);
	}

	@Override
	public PaginaDto<CacheDto> getAllCaches() {
		log.debug("Recuperant el llistat de les caches disponibles");
		List<CacheDto> caches = new ArrayList<>();
		Collection<String> cachesValues = cacheHelper.getAllCaches();
		CacheDto cache;
		for (String cacheValue : cachesValues) {
			cache = new CacheDto();
			cache.setCodi(cacheValue);
			cache.setLocalHeapSize(cacheHelper.getCacheSize(cacheValue));
			caches.add(cache);
		}
		Collections.sort(caches, new Comparator<CacheDto>() {
			@Override
			public int compare(CacheDto c1, CacheDto c2) {
				Integer c1Pos = ordreCaches.get(c1.getCodi());
				if (c1Pos == null) {
					c1Pos = 1000;
				}
				Integer c2Pos = ordreCaches.get(c2.getCodi());
				if (c2Pos == null) {
					c2Pos = 1001;
				}
				return c1Pos.compareTo(c2Pos);
			}
		});
		return paginacioHelper.toPaginaDto(caches, CacheDto.class);
	}

	@Override
	public void removeCache(String value) {
		log.debug("Esborrant la cache (value=" + value + ")");
		cacheHelper.clearCache(value);
	}

	@Override
	public void removeAllCaches() {
		log.debug("Esborrant totes les caches");
		cacheHelper.clearAllCaches();
	}

}
