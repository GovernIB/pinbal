package es.caib.pinbal.logic.resourceservice;

import es.caib.pinbal.logic.base.service.BaseReadonlyResourceService;
import es.caib.pinbal.logic.intf.model.ConfigGroupResource;
import es.caib.pinbal.logic.intf.resourceservice.ConfigGroupResourceService;
import es.caib.pinbal.persist.resourceentity.ConfigGroupResourceEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

/**
 * Implementació del servei de només lectura dels grups de propietats de configuració.
 * <p>
 * {@code descriptionKey} es resol a text via {@link MessageSource}, el mateix mecanisme que fa
 * servir el JSP amb {@code <spring:message>}.
 *
 * @author Límit Tecnologies
 */
@Service
@RequiredArgsConstructor
public class ConfigGroupResourceServiceImpl
        extends BaseReadonlyResourceService<ConfigGroupResource, String, ConfigGroupResourceEntity>
        implements ConfigGroupResourceService {

    private final MessageSource messageSource;

    @Override
    protected ConfigGroupResource entityToResource(ConfigGroupResourceEntity entity) {
        ConfigGroupResource resource = super.entityToResource(entity);
        // El camp @Id de l'entitat es diu "key", no "id": el mapeig genèric per reflexió (que
        // copia camps pel mateix nom) no pot omplir resource.id tot sol.
        resource.setId(entity.getId());
        if (entity.getDescriptionKey() != null) {
            resource.setDescription(messageSource.getMessage(
                    entity.getDescriptionKey(),
                    null,
                    entity.getDescriptionKey(),
                    LocaleContextHolder.getLocale()));
        }
        return resource;
    }

}
