/**
 * 
 */
package es.caib.pinbal.logic.service;

import es.caib.pinbal.logic.helper.DtoMappingHelper;
import es.caib.pinbal.logic.intf.dto.AvisDto;
import es.caib.pinbal.logic.intf.service.AvisService;
import es.caib.pinbal.logic.intf.service.exception.NotFoundException;
import es.caib.pinbal.persist.entity.Avis;
import es.caib.pinbal.persist.repository.AvisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Implementació del servei de gestió d'avisos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class AvisServiceImpl implements AvisService {

	private final AvisRepository avisRepository;
	private final DtoMappingHelper dtoMappingHelper;

	@Transactional
	@Override
	public AvisDto create(AvisDto avis) {
        log.debug("Creant una nova avis (avis={})", avis);
		Avis entity = Avis.getBuilder(
				avis.getAssumpte(),
				avis.getMissatge(),
				avis.getDataInici(),
				avis.getDataFinal(),
				avis.getAvisNivell()).build();
		return dtoMappingHelper.getMapperFacade().map(
				avisRepository.save(entity),
				AvisDto.class);
	}

	@Transactional
	@Override
	public AvisDto update(
			AvisDto avis) {
        log.debug("Actualitzant avis existent (avis={})", avis);
		Avis avisEntity = avisRepository.findById(avis.getId()).orElseThrow(() -> new NotFoundException(avis.getId(), Avis.class));
		avisEntity.update(
				avis.getAssumpte(),
				avis.getMissatge(),
				avis.getDataInici(),
				avis.getDataFinal(),
				avis.getAvisNivell());
		return dtoMappingHelper.getMapperFacade().map(
				avisEntity,
				AvisDto.class);
	}

	@Transactional
	@Override
	public AvisDto updateActiva(
			Long id,
			boolean activa) {
        log.debug("Actualitzant propietat activa d'una avis existent (id={}, activa={})", id, activa);
		Avis avisEntity = avisRepository.findById(id).orElseThrow(() -> new NotFoundException(id, Avis.class));
		avisEntity.updateActiva(activa);
		return dtoMappingHelper.getMapperFacade().map(
				avisEntity,
				AvisDto.class);
	}

	@Transactional
	@Override
	public AvisDto delete(
			Long id) {
        log.debug("Esborrant avis (id={})", id);
		Avis avisEntity = avisRepository.findById(id).orElseThrow(() -> new NotFoundException(id, Avis.class));
		avisRepository.delete(avisEntity);
		return dtoMappingHelper.getMapperFacade().map(
				avisEntity,
				AvisDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public AvisDto findById(Long id) {
        log.debug("Consulta de l'avis (id={})", id);
		Avis avisEntity = avisRepository.findById(id).orElse(null);
		return dtoMappingHelper.getMapperFacade().map(
				avisEntity,
				AvisDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public Page<AvisDto> findPaginat(Pageable pageable) {
        log.debug("Consulta de totes les avisos paginades (pageable={})", pageable);
		Page<Avis> paginaEntitats = avisRepository.findAll(pageable);
		return dtoMappingHelper.pageEntities2pageDto(
				paginaEntitats,
				AvisDto.class,
				pageable);
	}

	@Transactional(readOnly = true)
	@Override
	public List<AvisDto> findActive() {
		return dtoMappingHelper.convertirList(
				avisRepository.findActive(DateUtils.truncate(new Date(), Calendar.DATE)), 
				AvisDto.class);
	}

    @Override
    public List<Long> findAllIds() {
        return avisRepository.findAllIds();
    }

}
