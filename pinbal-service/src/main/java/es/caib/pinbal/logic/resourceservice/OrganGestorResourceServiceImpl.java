package es.caib.pinbal.logic.resourceservice;

import es.caib.pinbal.logic.base.service.BaseMutableResourceService;
import es.caib.pinbal.logic.intf.base.exception.ActionExecutionException;
import es.caib.pinbal.logic.intf.base.exception.AnswerRequiredException;
import es.caib.pinbal.logic.intf.model.OrganGestorResource;
import es.caib.pinbal.logic.intf.model.OrganGestorSyncDir3Params;
import es.caib.pinbal.logic.intf.resourceservice.OrganGestorResourceService;
import es.caib.pinbal.logic.intf.service.OrganGestorService;
import es.caib.pinbal.persist.resourceentity.OrganGestorResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Map;

/**
 * Implementació del servei de consulta d'òrgans gestors.
 * <p>
 * Mapeig genèric per reflexió a {@link OrganGestorResourceEntity} (mateixa taula que
 * {@link es.caib.pinbal.persist.entity.OrganGestor}). Els òrgans gestors no es creen,
 * modifiquen ni esborren manualment (ho fa exclusivament la sincronització amb DIR3, exposada
 * com a acció "syncDir3").
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrganGestorResourceServiceImpl
		extends BaseMutableResourceService<OrganGestorResource, Long, OrganGestorResourceEntity>
		implements OrganGestorResourceService {

	private final OrganGestorService organGestorService;

	@PostConstruct
	void registerActions() {
		register("syncDir3", new SyncDir3ActionExecutor());
	}

	@Override
	public OrganGestorResource create(OrganGestorResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
		throw new UnsupportedOperationException(
				"Un òrgan gestor no es pot crear des d'aquí: només es sincronitza des de DIR3 (acció «Sincronitzar amb DIR3»)");
	}

	@Override
	public OrganGestorResource update(
			Long id,
			OrganGestorResource resource,
			Map<String, AnswerRequiredException.AnswerValue> answers) {
		throw new UnsupportedOperationException(
				"Un òrgan gestor no es pot modificar des d'aquí: només es sincronitza des de DIR3 (acció «Sincronitzar amb DIR3»)");
	}

	@Override
	public void delete(Long id, Map<String, AnswerRequiredException.AnswerValue> answers) {
		throw new UnsupportedOperationException(
				"Un òrgan gestor no es pot esborrar des d'aquí: només es sincronitza des de DIR3 (acció «Sincronitzar amb DIR3»)");
	}

	private class SyncDir3ActionExecutor implements ActionExecutor<OrganGestorResourceEntity, OrganGestorSyncDir3Params, Boolean> {

		@Override
		public Boolean exec(String code, OrganGestorResourceEntity entity, OrganGestorSyncDir3Params params) throws ActionExecutionException {
			try {
				return organGestorService.syncDir3OrgansGestors(params.getEntitatId());
			} catch (Exception ex) {
				throw new ActionExecutionException(OrganGestorResource.class, null, code, "Error sincronitzant amb DIR3", ex);
			}
		}

		@Override
		public void onChange(
				Serializable id,
				OrganGestorSyncDir3Params previous,
				String fieldName,
				Object fieldValue,
				Map<String, AnswerRequiredException.AnswerValue> answers,
				String[] previousFieldNames,
				OrganGestorSyncDir3Params target) {
			// No hi ha lògica reactiva de camps per a aquesta acció.
		}

	}

}
