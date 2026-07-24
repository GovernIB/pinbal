package es.caib.pinbal.logic.resourceservice;

import es.caib.pinbal.logic.base.service.BaseMutableResourceService;
import es.caib.pinbal.logic.helper.CacheHelper;
import es.caib.pinbal.logic.intf.base.exception.AnswerRequiredException;
import es.caib.pinbal.logic.intf.base.exception.ResourceNotCreatedException;
import es.caib.pinbal.logic.intf.model.EntitatServeiResource;
import es.caib.pinbal.logic.intf.resourceservice.EntitatServeiResourceService;
import es.caib.pinbal.logic.intf.service.EntitatService;
import es.caib.pinbal.persist.resourceentity.EntitatServeiResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Implementació del servei de consulta i modificació dels serveis SCSP assignats a una entitat.
 * <p>
 * Mapeig genèric per reflexió a {@link EntitatServeiResourceEntity} (mateixa taula que
 * {@link es.caib.pinbal.persist.entity.EntitatServei}). No es permet modificar una assignació
 * (només crear-la o esborrar-la, com a la JSP), i es repliquen als hooks els efectes
 * secundaris de negoci (validació SCSP, sincronització de serveis actius, invalidació de cache).
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EntitatServeiResourceServiceImpl
		extends BaseMutableResourceService<EntitatServeiResource, Long, EntitatServeiResourceEntity>
		implements EntitatServeiResourceService {

	private final EntitatService entitatService;
	private final CacheHelper cacheHelper;

	@Override
	protected void beforeCreateEntity(
			EntitatServeiResourceEntity entity,
			EntitatServeiResource resource,
			Map<String, AnswerRequiredException.AnswerValue> answers) {
		if (!entitatService.scspServeiExisteix(entity.getServeiCodi())) {
			throw new ResourceNotCreatedException(EntitatServeiResource.class, "No existeix cap servei SCSP amb codi " + entity.getServeiCodi());
		}
	}

	@Override
	protected void afterCreateSave(
			EntitatServeiResourceEntity entity,
			EntitatServeiResource resource,
			Map<String, AnswerRequiredException.AnswerValue> answers,
			boolean anyOrderChanged) {
		sincronitzarServeisEntitat(entity);
	}

	@Override
	public EntitatServeiResource update(
			Long id,
			EntitatServeiResource resource,
			Map<String, AnswerRequiredException.AnswerValue> answers) {
		throw new UnsupportedOperationException("Una assignació entitat-servei no es pot modificar, només crear o esborrar");
	}

	@Override
	protected void afterDelete(
			EntitatServeiResourceEntity entity,
			Map<String, AnswerRequiredException.AnswerValue> answers) {
		sincronitzarServeisEntitat(entity);
	}

	private void sincronitzarServeisEntitat(EntitatServeiResourceEntity entity) {
		entitatService.scspSincronitzarServeisActius(entity.getEntitat().getId());
		cacheHelper.evictServeisEntitat(entity.getEntitat().getCodi());
	}

}
