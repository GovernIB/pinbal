/**
 * 
 */
package es.caib.pinbal.core.service;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.pinbal.core.dto.PaginaLlistatDto;
import es.caib.pinbal.core.dto.PaginacioAmbOrdreDto;
import es.caib.pinbal.core.dto.ParamConfDto;
import es.caib.pinbal.core.helper.DtoMappingHelper;
import es.caib.pinbal.core.helper.PaginacioHelper;
import es.caib.pinbal.core.model.ParamConf;
import es.caib.pinbal.core.repository.ParamConfRepository;
import es.caib.pinbal.core.service.exception.ParamConfNotFoundException;

/**
 * Implementació d'un paràmetre de configuració que es comunica amb la base de
 * dades emprant un repositori proporcionat per spring-data-jpa.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class ScspServiceImpl implements ScspService {
	
	@Resource
	private ParamConfRepository paramConfRepository;
	@Resource
	private DtoMappingHelper dtoMappingHelper;
	
	
	@Override
	@Transactional(readOnly = true)
	public ParamConfDto findByNom(String nom) {
		
		LOGGER.debug("Consulta un paràmetre de configuració (nom = " + nom + ")");
		
		return dtoMappingHelper.getMapperFacade().map(
				paramConfRepository.findByNom(nom),
				ParamConfDto.class);
	}

	@Override
	@Transactional
	public ParamConfDto create(ParamConfDto dto) {
		
		LOGGER.debug("Creant una nou paràmetre de configuració : " + dto);
		
		ParamConf entity = ParamConf.getBuilder(
				dto.getNom(),
				dto.getValor(),
				dto.getDescripcio()).build();
		
		return dtoMappingHelper.getMapperFacade().map(
				paramConfRepository.save(entity),
				ParamConfDto.class);
	}

	@Override
	@Transactional
	public ParamConfDto update(ParamConfDto dto) throws ParamConfNotFoundException {
		
		LOGGER.debug("Actualitzant el paràmetre de configuració (nom = " + dto.getNom() +
					 ") amb la informació: " + dto);
		
		ParamConf entity = paramConfRepository.findByNom(dto.getNom());
		if (entity == null) {
			LOGGER.debug("No s'ha trobat el paràmetre de configuració (nom = " + dto.getNom() + ")");
			throw new ParamConfNotFoundException();
		}
		
		entity.update(
				dto.getValor(),
				dto.getDescripcio());
		
		return dtoMappingHelper.getMapperFacade().map(
				entity,
				ParamConfDto.class);
	}

	@Override
	@Transactional
	public ParamConfDto delete(String nom) throws ParamConfNotFoundException {
		
		LOGGER.debug("Esborrant el paràmetre de configuració (nom =" + nom + ")");
		
		ParamConf entity = paramConfRepository.findByNom(nom);
		if (entity == null) {
			LOGGER.debug("No s'ha trobat el paràmetre de configuració (nom = " + nom + ")");
			throw new ParamConfNotFoundException();
		}
		
		paramConfRepository.delete(entity);
		
		return dtoMappingHelper.getMapperFacade().map(
				entity,
				ParamConfDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ParamConfDto> findAll() {
		
		LOGGER.debug("Consulta de tots els paràmetre de configuració");
		
		List<ParamConf> llista = paramConfRepository.findAll();
		
		return dtoMappingHelper.getMapperFacade().mapAsList(
				llista,
				ParamConfDto.class);
	}
	
	@Override
	@Transactional(readOnly = true)
	public PaginaLlistatDto<ParamConfDto> findAllPaginat(
			PaginacioAmbOrdreDto paginacioAmbOrdre) {
		
		LOGGER.debug("Consulta de tots els paràmetre de configuració paginats.");
		
		Pageable pageable = PaginacioHelper.toSpringDataPageableWithoutId(
				paginacioAmbOrdre,
				null);
		Page<ParamConf> llista = paramConfRepository.findAll(pageable);
		
		return PaginacioHelper.toPaginaLlistatDto(
				llista,
				dtoMappingHelper,
				ParamConfDto.class);
	}
	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ScspServiceImpl.class);
	
}
