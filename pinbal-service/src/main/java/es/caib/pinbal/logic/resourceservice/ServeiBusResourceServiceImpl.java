package es.caib.pinbal.logic.resourceservice;

import es.caib.pinbal.logic.base.service.BaseMutableResourceService;
import es.caib.pinbal.logic.intf.base.exception.AnswerRequiredException;
import es.caib.pinbal.logic.intf.base.exception.ResourceNotCreatedException;
import es.caib.pinbal.logic.intf.model.ServeiBusResource;
import es.caib.pinbal.logic.intf.resourceservice.ServeiBusResourceService;
import es.caib.pinbal.logic.intf.service.EntitatService;
import es.caib.pinbal.persist.resourceentity.ServeiBusResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Implementació del servei de consulta i modificació de les redireccions d'un servei per al
 * bus de serveis.
 * <p>
 * Mapeig genèric per reflexió a {@link ServeiBusResourceEntity} (mateixa taula que
 * {@link es.caib.pinbal.persist.entity.ServeiBus}). L'única lògica pròpia és validar, en crear
 * una redirecció, que el servei SCSP referenciat existeixi (com fa
 * {@code ServeiServiceImpl.createServeiBus}); no hi ha cap altre efecte secundari (ni cache, ni
 * sincronització SCSP) a diferència d'altres recursos d'aquesta àrea.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ServeiBusResourceServiceImpl
		extends BaseMutableResourceService<ServeiBusResource, Long, ServeiBusResourceEntity>
		implements ServeiBusResourceService {

	private final EntitatService entitatService;

	@Override
	protected void beforeCreateEntity(
			ServeiBusResourceEntity entity,
			ServeiBusResource resource,
			Map<String, AnswerRequiredException.AnswerValue> answers) {
		if (!entitatService.scspServeiExisteix(entity.getServeiCodi())) {
			throw new ResourceNotCreatedException(ServeiBusResource.class, "No existeix cap servei SCSP amb codi " + entity.getServeiCodi());
		}
	}

}
