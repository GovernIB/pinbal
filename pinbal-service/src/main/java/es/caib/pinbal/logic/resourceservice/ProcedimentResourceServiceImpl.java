package es.caib.pinbal.logic.resourceservice;

import es.caib.pinbal.logic.base.service.BaseMutableResourceService;
import es.caib.pinbal.logic.helper.CacheHelper;
import es.caib.pinbal.logic.intf.base.exception.AnswerRequiredException;
import es.caib.pinbal.logic.intf.model.ProcedimentResource;
import es.caib.pinbal.logic.intf.resourceservice.ProcedimentResourceService;
import es.caib.pinbal.persist.resourceentity.ProcedimentResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Implementació del servei de consulta i modificació de procediments administratius.
 * <p>
 * Mapeig genèric per reflexió a {@link ProcedimentResourceEntity} (mateixa taula que
 * {@link es.caib.pinbal.persist.entity.Procediment}). Només cobreix les dades bàsiques:
 * l'assignació de serveis, la graella de permisos, el clonatge i l'assistent de migració es
 * continuen gestionant des del manteniment JSP.
 * <p>
 * L'actiu es persisteix pel mapeig genèric (és un camp normal de l'entitat), però la
 * invalidació de cache que fa {@code ProcedimentService.updateActiu} es replica a mà en lloc de
 * cridar aquell mètode: reutilitzar-lo faria un segon fetch/save de la mateixa fila dins la
 * mateixa transacció (una altra instància JPA de {@code Procediment}), amb risc de conflicte
 * de bloqueig optimista (@Version) amb el save genèric d'aquest servei.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProcedimentResourceServiceImpl
		extends BaseMutableResourceService<ProcedimentResource, Long, ProcedimentResourceEntity>
		implements ProcedimentResourceService {

	private final CacheHelper cacheHelper;

	@Override
	protected void afterCreateSave(
			ProcedimentResourceEntity entity,
			ProcedimentResource resource,
			Map<String, AnswerRequiredException.AnswerValue> answers,
			boolean anyOrderChanged) {
		cacheHelper.evictProcedimentsPerEntitat(entity.getEntitat().getCodi());
	}

	@Override
	protected void beforeUpdateEntity(
			ProcedimentResourceEntity entity,
			ProcedimentResource resource,
			Map<String, AnswerRequiredException.AnswerValue> answers) {
		if (resource.isActiu() != entity.isActiu()) {
			cacheHelper.evictProcedimentsPerEntitat(entity.getEntitat().getCodi());
		}
	}

	@Override
	protected void afterDelete(
			ProcedimentResourceEntity entity,
			Map<String, AnswerRequiredException.AnswerValue> answers) {
		cacheHelper.evictProcedimentsPerEntitat(entity.getEntitat().getCodi());
	}

}
