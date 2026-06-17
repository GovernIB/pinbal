package es.caib.pinbal.logic.resourceservice;

import es.caib.pinbal.logic.base.helper.AuthenticationHelper;
import es.caib.pinbal.logic.base.service.BaseNoDatabaseReadonlyResourceService;
import es.caib.pinbal.logic.intf.base.config.BaseConfig;
import es.caib.pinbal.logic.intf.base.exception.ResourceNotFoundException;
import es.caib.pinbal.logic.intf.dto.EntitatDto;
import es.caib.pinbal.logic.intf.model.EntitatResource;
import es.caib.pinbal.logic.intf.resourceservice.EntitatResourceService;
import es.caib.pinbal.logic.intf.service.EntitatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementació del servei de consulta d'entitats.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EntitatResourceServiceImpl
		extends BaseNoDatabaseReadonlyResourceService<EntitatResource, Long>
		implements EntitatResourceService {

	private final EntitatService entitatService;
	private final AuthenticationHelper authenticationHelper;

	@Override
	@Transactional(readOnly = true)
	public EntitatResource getOne(Long id, String[] perspectives) throws ResourceNotFoundException {
		EntitatDto dto = entitatService.findById(id);
		if (dto == null) {
			throw new ResourceNotFoundException(EntitatResource.class, String.valueOf(id));
		}
		return toResource(dto);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<EntitatResource> findPage(
			String quickFilter,
			String filter,
			String[] namedQueries,
			String[] perspectives,
			Pageable pageable) {
		List<EntitatDto> entitats = getEntitatsForCurrentUser();
		if (quickFilter != null && !quickFilter.isBlank()) {
			String q = quickFilter.toLowerCase();
			entitats = entitats.stream()
					.filter(e -> (e.getCodi() != null && e.getCodi().toLowerCase().contains(q))
							|| (e.getNom() != null && e.getNom().toLowerCase().contains(q)))
					.collect(Collectors.toList());
		}
		int total = entitats.size();
		List<EntitatResource> resources;
		if (pageable.isUnpaged()) {
			resources = entitats.stream().map(this::toResource).collect(Collectors.toList());
		} else {
			int from = (int) Math.min(pageable.getOffset(), total);
			int to = (int) Math.min(pageable.getOffset() + pageable.getPageSize(), total);
			resources = entitats.subList(from, to).stream().map(this::toResource).collect(Collectors.toList());
		}
		return new PageImpl<>(resources, pageable, total);
	}

	private List<EntitatDto> getEntitatsForCurrentUser() {
		if (authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ADMIN)) {
			return entitatService.findAll();
		}
		return entitatService.findActivesAmbUsuariCodi(authenticationHelper.getCurrentUserName());
	}

	private EntitatResource toResource(EntitatDto dto) {
		EntitatResource resource = new EntitatResource();
		resource.setId(dto.getId());
		resource.setCodi(dto.getCodi());
		resource.setNom(dto.getNom());
		resource.setCif(dto.getCif());
		resource.setUnitatArrel(dto.getUnitatArrel());
		resource.setTipus(dto.getTipus());
		resource.setActiva(dto.isActiva());
		return resource;
	}

}
