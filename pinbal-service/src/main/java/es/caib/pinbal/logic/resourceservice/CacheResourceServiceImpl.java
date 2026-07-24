package es.caib.pinbal.logic.resourceservice;

import es.caib.pinbal.logic.base.service.BaseMutableResourceService.ActionExecutor;
import es.caib.pinbal.logic.base.service.BaseNoDatabaseMutableResourceService;
import es.caib.pinbal.logic.helper.CacheHelper;
import es.caib.pinbal.logic.intf.base.exception.ActionExecutionException;
import es.caib.pinbal.logic.intf.base.exception.AnswerRequiredException;
import es.caib.pinbal.logic.intf.base.exception.ResourceFieldNotFoundException;
import es.caib.pinbal.logic.intf.model.CacheBuidarParams;
import es.caib.pinbal.logic.intf.model.CacheResource;
import es.caib.pinbal.logic.intf.resourceservice.CacheResourceService;
import es.caib.pinbal.persist.base.entity.NoDatabaseResourceEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementació del servei de consulta i buidatge de les caches de l'aplicació.
 * <p>
 * Recurs "NoDatabase": la llista es construeix a partir de {@link CacheHelper#getAllCaches()},
 * no hi ha cap taula de base de dades. Segueix el mateix ordre "conegut primer" que la pantalla
 * JSP (equivalent al mapa {@code ordreCaches} d'{@code AplicacioServiceImpl}).
 *
 * @author Límit Tecnologies
 */
@Service
@RequiredArgsConstructor
public class CacheResourceServiceImpl
        extends BaseNoDatabaseMutableResourceService<CacheResource, String>
        implements CacheResourceService {

    private final CacheHelper cacheHelper;

    private static final Map<String, Integer> ORDRE_CACHES = new HashMap<>();

    static {
        ORDRE_CACHES.put("serveiDescripcio", 0);
        ORDRE_CACHES.put("emisorNombre", 1);
        ORDRE_CACHES.put("clavePrivadaNombre", 2);
        ORDRE_CACHES.put("clavePrivadaNumeroSerie", 3);
        ORDRE_CACHES.put("clavePublicaNombre", 4);
        ORDRE_CACHES.put("clavePublicaNumeroSerie", 5);
        ORDRE_CACHES.put("procediments", 6);
        ORDRE_CACHES.put("serveis", 7);
        ORDRE_CACHES.put("serveisEntitat", 8);
        ORDRE_CACHES.put("serveisProcediment", 9);
        ORDRE_CACHES.put("dadesEspecifiques", 10);
        ORDRE_CACHES.put("enumerats", 11);
        ORDRE_CACHES.put("paisos", 12);
        ORDRE_CACHES.put("provincies", 13);
        ORDRE_CACHES.put("municipis", 14);
        ORDRE_CACHES.put("usuariAmbCodi", 15);
        ORDRE_CACHES.put("usuariAmbNif", 16);
    }

    @PostConstruct
    public void init() {
        register("buidarCache", new ActionExecutor<NoDatabaseResourceEntity<CacheResource, String>, CacheBuidarParams, Serializable>() {
            @Override
            public Serializable exec(
                    String code,
                    NoDatabaseResourceEntity<CacheResource, String> entity,
                    CacheBuidarParams params) throws ActionExecutionException {
                if (params == null || params.getIds() == null || params.getIds().isEmpty()) {
                    throw new ActionExecutionException(CacheResource.class, null, code, "Cal indicar quines caches s'han de buidar");
                }
                params.getIds().forEach(cacheHelper::clearCache);
                return null;
            }

            @Override
            public void onChange(
                    Serializable id,
                    CacheBuidarParams previous,
                    String fieldName,
                    Object fieldValue,
                    Map<String, AnswerRequiredException.AnswerValue> answers,
                    String[] previousFieldNames,
                    CacheBuidarParams target) {
            }
        });
        register("buidarTotesCaches", new ActionExecutor<NoDatabaseResourceEntity<CacheResource, String>, Serializable, Serializable>() {
            @Override
            public Serializable exec(
                    String code,
                    NoDatabaseResourceEntity<CacheResource, String> entity,
                    Serializable params) throws ActionExecutionException {
                cacheHelper.clearAllCaches();
                return null;
            }

            @Override
            public void onChange(
                    Serializable id,
                    Serializable previous,
                    String fieldName,
                    Object fieldValue,
                    Map<String, AnswerRequiredException.AnswerValue> answers,
                    String[] previousFieldNames,
                    Serializable target) {
            }
        });
    }

    private CacheResource toResource(String codi) {
        CacheResource resource = new CacheResource();
        resource.setId(codi);
        resource.setCodi(codi);
        resource.setLocalHeapSize(cacheHelper.getCacheSize(codi));
        return resource;
    }

    @Override
    protected Optional<NoDatabaseResourceEntity<CacheResource, String>> entityRepositoryFindOne(String id) {
        Collection<String> caches = cacheHelper.getAllCaches();
        if (!caches.contains(id)) {
            return Optional.empty();
        }
        return Optional.of(NoDatabaseResourceEntity.<CacheResource, String>builder().id(id).resource(toResource(id)).build());
    }

    @Override
    public Page<CacheResource> findPage(
            String quickFilter,
            String filter,
            String[] namedQueries,
            String[] perspectives,
            Pageable pageable) {
        List<CacheResource> resources = cacheHelper.getAllCaches().stream().
                map(this::toResource).
                filter(r -> quickFilter == null || quickFilter.isBlank() || r.getCodi().toLowerCase().contains(quickFilter.toLowerCase())).
                sorted(Comparator.comparing(r -> ORDRE_CACHES.getOrDefault(r.getCodi(), 1000))).
                collect(Collectors.toList());
        return new PageImpl<>(resources, pageable != null && pageable.isPaged() ? pageable : Pageable.unpaged(), resources.size());
    }

    @Override
    public CacheResource create(CacheResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
        throw new UnsupportedOperationException("Les caches no es poden crear");
    }

    @Override
    public CacheResource update(String id, CacheResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
        throw new UnsupportedOperationException("Les caches no es poden modificar, només buidar");
    }

    @Override
    public void delete(String id, Map<String, AnswerRequiredException.AnswerValue> answers) {
        throw new UnsupportedOperationException("Les caches no es poden esborrar, només buidar");
    }

}
