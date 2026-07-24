package es.caib.pinbal.logic.resourceservice;

import es.caib.pinbal.logic.base.service.BaseMutableResourceService;
import es.caib.pinbal.logic.intf.base.exception.AnswerRequiredException;
import es.caib.pinbal.logic.intf.model.ServeiResource;
import es.caib.pinbal.logic.intf.resourceservice.ServeiResourceService;
import es.caib.pinbal.logic.intf.service.ServeiService;
import es.caib.pinbal.persist.resourceentity.ServeiResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Implementació del servei de consulta i modificació de la configuració PINBAL dels serveis SCSP.
 * <p>
 * Mapeig genèric per reflexió a {@link ServeiResourceEntity} (mateixa taula que
 * {@link es.caib.pinbal.persist.entity.ServeiConfig}). El camp {@code descripcio} viu a SCSP
 * (taula {@code core_servicio}, no accessible com a entitat JPA local d'aquest recurs) i es
 * gestiona als hooks via {@link ServeiService}. {@code create}/{@code delete} no estan
 * suportats: donar d'alta un servei requereix la configuració SCSP completa (URLs,
 * seguretat...) que no forma part d'aquest recurs, i esborrar-lo n'elimina tota la
 * configuració SCSP associada.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ServeiResourceServiceImpl
		extends BaseMutableResourceService<ServeiResource, Long, ServeiResourceEntity>
		implements ServeiResourceService {

	private final ServeiService serveiService;

	@Override
	protected ServeiResource entityToResource(ServeiResourceEntity entity) {
		ServeiResource resource = super.entityToResource(entity);
		resource.setDescripcio(serveiService.scspDescripcio(entity.getCodi()));
		return resource;
	}

	@Override
	protected void beforeUpdateEntity(
			ServeiResourceEntity entity,
			ServeiResource resource,
			Map<String, AnswerRequiredException.AnswerValue> answers) {
		if (resource.getCodi() != null && !resource.getCodi().equals(entity.getCodi())) {
			throw new UnsupportedOperationException("El codi d'un servei no es pot modificar");
		}
		if (resource.isActiu() != entity.isActiu()) {
			serveiService.evictCachesPerServei(entity.getCodi());
		}
	}

	@Override
	protected void afterUpdateSave(
			ServeiResourceEntity entity,
			ServeiResource resource,
			Map<String, AnswerRequiredException.AnswerValue> answers,
			boolean anyOrderChanged) {
		serveiService.scspActualitzarDescripcio(entity.getCodi(), resource.getDescripcio());
	}

	@Override
	public ServeiResource create(ServeiResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
		throw new UnsupportedOperationException(
				"Un servei nou no es pot crear des d'aquí: cal la configuració SCSP completa (URLs, seguretat...), "
						+ "disponible al manteniment de serveis de l'aplicació JSP");
	}

	@Override
	public void delete(Long id, Map<String, AnswerRequiredException.AnswerValue> answers) {
		throw new UnsupportedOperationException(
				"Un servei no es pot esborrar des d'aquí perquè n'elimina tota la configuració SCSP; "
						+ "feis-ho des del manteniment de serveis de l'aplicació JSP");
	}

}
