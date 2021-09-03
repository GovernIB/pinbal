/**
 * 
 */
package es.caib.pinbal.core.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.pinbal.core.dto.AvisDto;
import es.caib.pinbal.core.helper.DtoMappingHelper;
import es.caib.pinbal.core.model.Avis;
import es.caib.pinbal.core.repository.AvisRepository;

/**
 * Implementació del servei de gestió d'avisos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class AvisServiceImpl implements AvisService {

	@Autowired
	private AvisRepository avisRepository;

	@Resource
	private DtoMappingHelper dtoMappingHelper;
	

	@Transactional
	@Override
	public AvisDto create(AvisDto avis) {
		logger.debug("Creant una nova avis (" +
				"avis=" + avis + ")");
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
		logger.debug("Actualitzant avis existent (" +
				"avis=" + avis + ")");

		Avis avisEntity = avisRepository.findOne(avis.getId());
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
		logger.debug("Actualitzant propietat activa d'una avis existent (" +
				"id=" + id + ", " +
				"activa=" + activa + ")");
		Avis avisEntity = avisRepository.findOne(id);
		
		avisEntity.updateActiva(activa);
		return dtoMappingHelper.getMapperFacade().map(
				avisEntity,
				AvisDto.class);
	}
	
	

	@Transactional
	@Override
	public AvisDto delete(
			Long id) {
		logger.debug("Esborrant avis (" +
				"id=" + id +  ")");
		
		Avis avisEntity = avisRepository.findOne(id);
		avisRepository.delete(avisEntity);

		return dtoMappingHelper.getMapperFacade().map(
				avisEntity,
				AvisDto.class);
	}

	@Transactional(readOnly = true)
	@Override
	public AvisDto findById(Long id) {
		logger.debug("Consulta de l'avis (" +
				"id=" + id + ")");
		
		Avis avisEntity = avisRepository.findOne(id);
		AvisDto dto = dtoMappingHelper.getMapperFacade().map(
				avisEntity,
				AvisDto.class);
		return dto;
	}
	
	
	@Transactional(readOnly = true)
	@Override
	public Page<AvisDto> findPaginat(Pageable pageable) {
		logger.debug("Consulta de totes les avisos paginades (" +
				"pageable=" + pageable + ")");
		
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

	private static final Logger logger = LoggerFactory.getLogger(AvisServiceImpl.class);

}
