package es.caib.pinbal.logic.resourceservice;

import es.caib.pinbal.logic.base.service.BaseMutableResourceService;
import es.caib.pinbal.logic.intf.model.ParamConfResource;
import es.caib.pinbal.logic.intf.resourceservice.ParamConfResourceService;
import es.caib.pinbal.persist.resourceentity.ParamConfResourceEntity;
import org.springframework.stereotype.Service;

/**
 * Implementació del servei de manteniment dels paràmetres de configuració SCSP.
 * <p>
 * El {@code nom} és la clau primària: la immutabilitat un cop creat ja la garanteix el mapeig
 * genèric (el camp es marca disabled al formulari d'edició). La unicitat en canvi requereix
 * l'override de {@link #getPkFromResource}: el camp {@code id} heretat de {@code BaseResource} és
 * {@code @JsonProperty(READ_ONLY)} (el client mai l'envia), així que sense aquest override
 * {@code buildPkChechingIfEntityAlreadyExists} sempre rebria {@code pk=null} i mai detectaria un
 * nom duplicat abans d'arribar a la base de dades.
 * <p>
 * També cal l'override d'{@link #entityToResource}: com que el camp {@code @Id} de
 * {@link ParamConfResourceEntity} es diu {@code nom} (no {@code id}), el mapeig genèric per
 * reflexió (que copia camps per nom) no pot omplir mai {@code resource.id} tot sol.
 *
 * @author Límit Tecnologies
 */
@Service
public class ParamConfResourceServiceImpl
        extends BaseMutableResourceService<ParamConfResource, String, ParamConfResourceEntity>
        implements ParamConfResourceService {

    @Override
    protected String getPkFromResource(ParamConfResource resource) {
        return resource.getNom();
    }

    @Override
    protected ParamConfResource entityToResource(ParamConfResourceEntity entity) {
        ParamConfResource resource = super.entityToResource(entity);
        resource.setId(entity.getId());
        return resource;
    }

}
