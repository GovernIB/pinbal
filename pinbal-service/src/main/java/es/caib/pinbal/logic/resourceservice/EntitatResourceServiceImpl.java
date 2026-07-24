package es.caib.pinbal.logic.resourceservice;

import es.caib.pinbal.logic.base.helper.AuthenticationHelper;
import es.caib.pinbal.logic.base.service.BaseMutableResourceService;
import es.caib.pinbal.logic.intf.base.config.BaseConfig;
import es.caib.pinbal.logic.intf.base.exception.AnswerRequiredException;
import es.caib.pinbal.logic.intf.model.EntitatResource;
import es.caib.pinbal.logic.intf.resourceservice.EntitatResourceService;
import es.caib.pinbal.logic.intf.service.EntitatService;
import es.caib.pinbal.persist.entity.EntitatUsuari;
import es.caib.pinbal.persist.repository.EntitatUsuariRepository;
import es.caib.pinbal.persist.resourceentity.EntitatResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementació del servei de consulta i modificació d'entitats.
 * <p>
 * Mapeig genèric per reflexió a {@link EntitatResourceEntity} (mateixa taula que l'entitat de
 * negoci). Els efectes secundaris de negoci (alta/baixa/actualització a SCSP de l'organisme
 * cessionari) es repliquen als hooks perquè no formen part de la persistència del recurs.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EntitatResourceServiceImpl
		extends BaseMutableResourceService<EntitatResource, Long, EntitatResourceEntity>
		implements EntitatResourceService {

	private final EntitatService entitatService;
	private final AuthenticationHelper authenticationHelper;
	private final EntitatUsuariRepository entitatUsuariRepository;

	@Override
	protected Specification<EntitatResourceEntity> additionalSpecification(String[] namedQueries) {
		if (authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ADMIN)) {
			return null;
		}
		List<Long> entitatIds = entitatUsuariRepository.findByUsuariCodi(authenticationHelper.getCurrentUserName()).
				stream().
				map(EntitatUsuari::getEntitat).
				map(es.caib.pinbal.persist.entity.Entitat::getId).
				collect(Collectors.toList());
		return (root, query, cb) -> cb.and(
				root.get("id").in(entitatIds),
				cb.isTrue(root.get("activa")));
	}

	@Override
	protected void beforeCreateSave(
			EntitatResourceEntity entity,
			EntitatResource resource,
			Map<String, AnswerRequiredException.AnswerValue> answers) {
		entitatService.scspOrganismeCessionariAlta(entity.getCif(), entity.getNom(), entity.isActiva());
	}

	@Override
	protected void afterUpdateSave(
			EntitatResourceEntity entity,
			EntitatResource resource,
			Map<String, AnswerRequiredException.AnswerValue> answers,
			boolean anyOrderChanged) {
		entitatService.scspOrganismeCessionariActualitzacio(entity.getCif(), entity.getNom(), entity.isActiva());
		entitatService.scspSincronitzarServeisActius(entity.getId());
	}

	@Override
	protected void beforeDelete(
			EntitatResourceEntity entity,
			Map<String, AnswerRequiredException.AnswerValue> answers) {
		entitatService.scspOrganismeCessionariBaixa(entity.getCif());
	}

}
