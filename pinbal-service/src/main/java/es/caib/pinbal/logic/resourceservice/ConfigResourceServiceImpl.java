package es.caib.pinbal.logic.resourceservice;

import es.caib.pinbal.logic.base.service.BaseMutableResourceService;
import es.caib.pinbal.logic.base.service.BaseMutableResourceService.ActionExecutor;
import es.caib.pinbal.logic.helper.ConfigHelper;
import es.caib.pinbal.logic.helper.LoggerHelper;
import es.caib.pinbal.logic.helper.PluginHelper;
import es.caib.pinbal.logic.intf.base.exception.ActionExecutionException;
import es.caib.pinbal.logic.intf.base.exception.AnswerRequiredException;
import es.caib.pinbal.logic.intf.base.exception.ResourceNotUpdatedException;
import es.caib.pinbal.logic.intf.dto.ConfigSourceEnumDto;
import es.caib.pinbal.logic.intf.model.ConfigResource;
import es.caib.pinbal.logic.intf.resourceservice.ConfigResourceService;
import es.caib.pinbal.logic.intf.service.ConfigService;
import es.caib.pinbal.persist.resourceentity.ConfigResourceEntity;
import es.caib.pinbal.persist.resourceentity.ConfigTypeResourceEntity;
import es.caib.pinbal.persist.resourcerepository.ConfigTypeResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Implementació del servei de manteniment de les propietats de configuració de l'aplicació.
 * <p>
 * Replica la lògica d'{@code ConfigServiceImpl}: només es pot modificar el valor si la propietat
 * és editable, s'emmascaren els valors de tipus contrasenya, es mostra el valor efectiu de les
 * propietats llegides de fitxer, i cada modificació recarrega la configuració i reinicia els
 * plugins afectats.
 *
 * @author Límit Tecnologies
 */
@Service
@RequiredArgsConstructor
public class ConfigResourceServiceImpl
        extends BaseMutableResourceService<ConfigResource, String, ConfigResourceEntity>
        implements ConfigResourceService {

    private static final String TYPE_PASSWORD = "PASS";

    private final ConfigTypeResourceRepository configTypeResourceRepository;
    private final ConfigHelper configHelper;
    private final PluginHelper pluginHelper;
    private final ConfigService configService;
    private final MessageSource messageSource;

    @PostConstruct
    public void init() {
        register("syncFromJBoss", new ActionExecutor<ConfigResourceEntity, Serializable, Serializable>() {
            @Override
            public Serializable exec(String code, ConfigResourceEntity entity, Serializable params) throws ActionExecutionException {
                try {
                    return (Serializable) configService.syncFromJBossProperties();
                } catch (Exception ex) {
                    throw new ActionExecutionException(ConfigResource.class, null, code, ex.getMessage(), ex);
                }
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
        register("reiniciarTasques", new ActionExecutor<ConfigResourceEntity, Serializable, Serializable>() {
            @Override
            public Serializable exec(String code, ConfigResourceEntity entity, Serializable params) throws ActionExecutionException {
                try {
                    configService.reiniciarTasques();
                    return null;
                } catch (Exception ex) {
                    throw new ActionExecutionException(ConfigResource.class, null, code, ex.getMessage(), ex);
                }
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

    @Override
    protected ConfigResource entityToResource(ConfigResourceEntity entity) {
        ConfigResource resource = super.entityToResource(entity);
        // El camp @Id de l'entitat es diu "key", no "id": el mapeig genèric per reflexió (que
        // copia camps pel mateix nom) no pot omplir resource.id tot sol.
        resource.setId(entity.getId());
        resource.setTypeCode(entity.getTypeCode());
        resource.setEditable(entity.isEditable());
        if (entity.getDescriptionKey() != null) {
            resource.setDescription(messageSource.getMessage(
                    entity.getDescriptionKey(),
                    null,
                    entity.getDescriptionKey(),
                    LocaleContextHolder.getLocale()));
        }
        if (entity.getTypeCode() != null) {
            ConfigTypeResourceEntity type = configTypeResourceRepository.findById(entity.getTypeCode()).orElse(null);
            resource.setValidValues(type != null ? type.getValidValues() : List.of());
        } else {
            resource.setValidValues(List.of());
        }
        if (TYPE_PASSWORD.equals(entity.getTypeCode())) {
            resource.setValue("*****");
        } else if (ConfigSourceEnumDto.FILE.equals(entity.getSourceProperty())) {
            resource.setValue(configHelper.getConfig(entity.getKey(), entity.getValue()));
        }
        return resource;
    }

    @Override
    protected void beforeUpdateEntity(
            ConfigResourceEntity entity,
            ConfigResource resource,
            Map<String, AnswerRequiredException.AnswerValue> answers) {
        if (!entity.isEditable()) {
            throw new ResourceNotUpdatedException(ConfigResource.class, entity.getKey(), "Aquesta propietat no és configurable");
        }
        if ("null".equals(resource.getValue())) {
            resource.setValue(null);
        }
    }

    @Override
    protected void afterUpdateSave(
            ConfigResourceEntity entity,
            ConfigResource resource,
            Map<String, AnswerRequiredException.AnswerValue> answers,
            boolean anyOrderChanged) {
        configHelper.reloadDbProperties();
        pluginHelper.resetPlugins(entity.getGroupCode());
        if (entity.getKey().startsWith(LoggerHelper.PREFIX)) {
            LoggerHelper.resetLogs();
        }
    }

    @Override
    public ConfigResource create(ConfigResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
        throw new UnsupportedOperationException("Les propietats de configuració no es poden crear des d'aquí");
    }

    @Override
    public void delete(String id, Map<String, AnswerRequiredException.AnswerValue> answers) {
        throw new UnsupportedOperationException("Les propietats de configuració no es poden esborrar");
    }

}
