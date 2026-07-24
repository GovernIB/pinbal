package es.caib.pinbal.logic.resourceservice;

import es.caib.pinbal.logic.base.service.BaseMutableResourceService;
import es.caib.pinbal.logic.helper.CacheHelper;
import es.caib.pinbal.logic.intf.base.exception.AnswerRequiredException;
import es.caib.pinbal.logic.intf.base.exception.ResourceNotFoundException;
import es.caib.pinbal.logic.intf.model.EntitatUsuariResource;
import es.caib.pinbal.logic.intf.resourceservice.EntitatUsuariResourceService;
import es.caib.pinbal.logic.intf.service.UsuariService;
import es.caib.pinbal.logic.intf.service.exception.EntitatNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.UsuariExternNotFoundException;
import es.caib.pinbal.persist.resourceentity.EntitatUsuariResourceEntity;
import es.caib.pinbal.persist.resourcerepository.EntitatUsuariResourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Implementació del servei de consulta i modificació dels usuaris d'una entitat.
 * <p>
 * {@code getOne}/{@code findPage}/{@code update} usen el mapeig genèric per reflexió a
 * {@link EntitatUsuariResourceEntity} (mateixa taula que {@link es.caib.pinbal.persist.entity.EntitatUsuari}):
 * tots els camps (departament, representant, delegat, auditor, aplicació, actiu, principal)
 * són columnes normals, i a diferència de {@code create}, en una actualització l'usuari
 * referenciat ja existeix (no cal aprovisionar-lo des del sistema extern), així que no calen
 * mètodes especials per a cada camp. Nota: no es crida {@code UsuariService.establirPrincipal}
 * (que fa el seu propi fetch/save de la mateixa fila via l'entitat de negoci {@code EntitatUsuari}
 * — provocaria un conflicte de bloqueig optimista amb el save genèric d'aquest servei); com que
 * {@code principal} és només un booleà sense cap més efecte al domini (vegeu
 * {@code EntitatUsuari.canviPrincipal()}), el mapeig genèric ja el persisteix correctament.
 * <p>
 * {@code create} sobreescriu el mètode complet perquè depèn de
 * {@link UsuariService#actualitzarDadesAdmin} — que pot aprovisionar l'usuari des del sistema
 * extern si encara no existeix localment, cosa que el mapeig genèric no pot fer (la resolució
 * de referències espera que l'entitat referenciada ja existeixi). No hi ha esborrat (a la JSP
 * els usuaris es desactiven amb {@link UsuariService#canviActiu}, no s'esborren).
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EntitatUsuariResourceServiceImpl
		extends BaseMutableResourceService<EntitatUsuariResource, Long, EntitatUsuariResourceEntity>
		implements EntitatUsuariResourceService {

	private final UsuariService usuariService;
	private final CacheHelper cacheHelper;
	private final EntitatUsuariResourceRepository entitatUsuariResourceRepository;

	@Override
	protected EntitatUsuariResource entityToResource(EntitatUsuariResourceEntity entity) {
		EntitatUsuariResource resource = super.entityToResource(entity);
		if (entity.getUsuari() != null) {
			resource.setUsuariCodi(entity.getUsuari().getId());
		}
		return resource;
	}

	@Override
	protected void beforeUpdateEntity(
			EntitatUsuariResourceEntity entity,
			EntitatUsuariResource resource,
			Map<String, AnswerRequiredException.AnswerValue> answers) {
		String usuariCodiNou = resolUsuariCodi(resource);
		if (usuariCodiNou != null && !usuariCodiNou.equals(entity.getUsuari().getId())) {
			throw new UnsupportedOperationException("L'usuari d'una assignació entitat-usuari no es pot modificar, només crear o desactivar");
		}
	}

	@Override
	protected void afterUpdateSave(
			EntitatUsuariResourceEntity entity,
			EntitatUsuariResource resource,
			Map<String, AnswerRequiredException.AnswerValue> answers,
			boolean anyOrderChanged) {
		cacheHelper.evictPermisosPerDelegat(entity.getUsuari().getId());
	}

	@Override
	public EntitatUsuariResource create(EntitatUsuariResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
		Long entitatId = resource.getEntitat() != null ? resource.getEntitat().getId() : null;
		String usuariCodi = resolUsuariCodi(resource);
		try {
			usuariService.actualitzarDadesAdmin(
					entitatId,
					usuariCodi,
					null,
					resource.getDepartament(),
					resource.isRepresentant(),
					resource.isDelegat(),
					resource.isAuditor(),
					resource.isAplicacio(),
					false,
					resource.isActiu());
		} catch (EntitatNotFoundException ex) {
			throw new ResourceNotFoundException(EntitatUsuariResource.class, String.valueOf(entitatId));
		} catch (UsuariExternNotFoundException ex) {
			throw new ResourceNotFoundException(EntitatUsuariResource.class, usuariCodi);
		}
		EntitatUsuariResourceEntity entity = entitatUsuariResourceRepository.findByEntitatIdAndUsuariCodi(entitatId, usuariCodi);
		if (entity == null) {
			throw new ResourceNotFoundException(EntitatUsuariResource.class, entitatId + "-" + usuariCodi);
		}
		return entityToResource(entity);
	}

	@Override
	public void delete(Long id, Map<String, AnswerRequiredException.AnswerValue> answers) {
		throw new UnsupportedOperationException(
				"Un usuari d'una entitat no es pot esborrar, només desactivar (camp actiu)");
	}

	private String resolUsuariCodi(EntitatUsuariResource resource) {
		if (resource.getUsuari() != null && resource.getUsuari().getId() != null) {
			return resource.getUsuari().getId();
		}
		return resource.getUsuariCodi();
	}

}
