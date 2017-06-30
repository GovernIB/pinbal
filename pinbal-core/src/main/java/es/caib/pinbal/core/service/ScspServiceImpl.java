/**
 * 
 */
package es.caib.pinbal.core.service;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.caib.pinbal.core.dto.ClauPrivadaDto;
import es.caib.pinbal.core.dto.EmissorCertDto;
import es.caib.pinbal.core.dto.OrganismeCessionariDto;
import es.caib.pinbal.core.dto.ParamConfDto;
import es.caib.pinbal.core.helper.DtoMappingHelper;
import es.caib.pinbal.core.model.ClauPrivada;
import es.caib.pinbal.core.model.EmissorCert;
import es.caib.pinbal.core.model.OrganismeCessionari;
import es.caib.pinbal.core.model.ParamConf;
import es.caib.pinbal.core.repository.ClauPrivadaRepository;
import es.caib.pinbal.core.repository.EmissorCertRepository;
import es.caib.pinbal.core.repository.OrganismeCessionariRepository;
import es.caib.pinbal.core.repository.ParamConfRepository;
import es.caib.pinbal.core.service.exception.ClauPrivadaNotFoundException;
import es.caib.pinbal.core.service.exception.EmissorCertNotFoundException;
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
	private EmissorCertRepository emissorCertRepository;
	@Resource
	private ClauPrivadaRepository clauPrivadaRepository;
	@Resource
	private OrganismeCessionariRepository organismeCessionariRepository;
	@Resource
	private DtoMappingHelper dtoMappingHelper;
	
	
	// Funcions de la taula de emissor de paràmetres de configuració.
	
	@Override
	@Transactional(readOnly = true)
	public ParamConfDto findParamConfByNom(String nom) {
		
		LOGGER.debug("Consulta un paràmetre de configuració (nom = " + nom + ")");
		
		return dtoMappingHelper.getMapperFacade().map(
				paramConfRepository.findByNom(nom),
				ParamConfDto.class);
	}

	@Override
	@Transactional
	public ParamConfDto createParamConf(ParamConfDto dto) {
		
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
	public ParamConfDto updateParamConf(ParamConfDto dto) throws ParamConfNotFoundException {
		
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
	public ParamConfDto deleteParamConf(String nom) throws ParamConfNotFoundException {
		
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
	public List<ParamConfDto> findAllParamConf() {
		
		LOGGER.debug("Consulta de tots els paràmetre de configuració");
		
		List<ParamConf> llista = paramConfRepository.findAll();
		
		return dtoMappingHelper.getMapperFacade().mapAsList(
				llista,
				ParamConfDto.class);
	}
	
	
	// Funcions de la taula de emissor de certificat.
	
	@Override
	@Transactional(readOnly = true)
	public EmissorCertDto findEmissorCertById(Long id) {

		LOGGER.debug("Consulta un emissor certificat (id = " + id + ")");
		
		return dtoMappingHelper.getMapperFacade().map(
				emissorCertRepository.findById(id),
				EmissorCertDto.class);
	}

	@Override
	@Transactional
	public EmissorCertDto createEmissorCert(EmissorCertDto dto) {

		LOGGER.debug("Creant una nou emissor de certificat : " + dto);
		
		EmissorCert entity = EmissorCert.getBuilder(
				dto.getNom(),
				dto.getCif(),
				dto.getDataBaixa()).build();
		
		return dtoMappingHelper.getMapperFacade().map(
				emissorCertRepository.save(entity),
				EmissorCertDto.class);
	}

	@Override
	@Transactional
	public EmissorCertDto updateEmissorCert(EmissorCertDto dto) throws EmissorCertNotFoundException {

		LOGGER.debug("Actualitzant el emissor certificat (id = " + dto.getId() +
					 ") amb la informació: " + dto);
		
		EmissorCert entity = emissorCertRepository.findById(dto.getId());
		if (entity == null) {
			LOGGER.debug("No s'ha trobat el emissor certificat (id = " + dto.getId() + ")");
			throw new EmissorCertNotFoundException();
		}
		
		entity.update(
				dto.getNom(),
				dto.getCif(),
				dto.getDataBaixa());
		
		return dtoMappingHelper.getMapperFacade().map(
				entity,
				EmissorCertDto.class);
	}

	@Override
	@Transactional
	public EmissorCertDto deleteEmissorCert(Long id) throws EmissorCertNotFoundException {

		LOGGER.debug("Esborrant el emissor certificat (id =" + id + ")");
		
		EmissorCert entity = emissorCertRepository.findById(id);
		if (entity == null) {
			LOGGER.debug("No s'ha trobat el emissor certificat (id = " + id + ")");
			throw new EmissorCertNotFoundException();
		}
		
		emissorCertRepository.delete(entity);
		
		return dtoMappingHelper.getMapperFacade().map(
				entity,
				EmissorCertDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public List<EmissorCertDto> findAllEmissorCert() {

		LOGGER.debug("Consulta de tots els emissors certificats");
		
		List<EmissorCert> llista = emissorCertRepository.findAll();
		
		return dtoMappingHelper.getMapperFacade().mapAsList(
				llista,
				EmissorCertDto.class);
	}
	
	
	// Funcions de la taula de claus privades
	
	@Override
	@Transactional(readOnly = true)
	public ClauPrivadaDto findClauPrivadaById(Long id) {

		LOGGER.debug("Consulta una clau privada (id = " + id + ")");
		
		return dtoMappingHelper.getMapperFacade().map(
				clauPrivadaRepository.findById(id),
				ClauPrivadaDto.class);
	}

	@Override
	@Transactional
	public ClauPrivadaDto createClauPrivada(ClauPrivadaDto dto) {

		LOGGER.debug("Creant una nova clau privada : " + dto);
		
		OrganismeCessionari organisme = organismeCessionariRepository.findById(
				dto.getOrganisme());
		ClauPrivada entity = ClauPrivada.getBuilder(
				dto.getAlies(),
				dto.getNom(),
				dto.getPassword(),
				dto.getNumSerie(),
				dto.getDataBaixa(),
				dto.getDataAlta(),
				dto.getInteroperabilitat(),
				organisme).build();
		
		return dtoMappingHelper.getMapperFacade().map(
				clauPrivadaRepository.save(entity),
				ClauPrivadaDto.class);
	}

	@Override
	@Transactional
	public ClauPrivadaDto updateClauPrivada(ClauPrivadaDto dto) throws ClauPrivadaNotFoundException {

		LOGGER.debug("Actualitzant la clau privada (id = " + dto.getId() +
					 ") amb la informació: " + dto);
		
		ClauPrivada entity = clauPrivadaRepository.findById(dto.getId());
		if (entity == null) {
			LOGGER.debug("No s'ha trobat la clau privada (id = " + dto.getId() + ")");
			throw new ClauPrivadaNotFoundException();
		}
		
		OrganismeCessionari organisme = organismeCessionariRepository.findById(
				dto.getOrganisme());
		entity.update(
				dto.getAlies(),
				dto.getNom(),
				dto.getPassword(),
				dto.getNumSerie(),
				dto.getDataBaixa(),
				dto.getDataAlta(),
				dto.getInteroperabilitat(),
				organisme);
		
		return dtoMappingHelper.getMapperFacade().map(
				entity,
				ClauPrivadaDto.class);
	}

	@Override
	@Transactional
	public ClauPrivadaDto deleteClauPrivada(Long id) throws ClauPrivadaNotFoundException {

		LOGGER.debug("Esborrant la clau privada (id =" + id + ")");
		
		ClauPrivada entity = clauPrivadaRepository.findById(id);
		if (entity == null) {
			LOGGER.debug("No s'ha trobat la clau privada (id = " + id + ")");
			throw new ClauPrivadaNotFoundException();
		}
		
		clauPrivadaRepository.delete(entity);
		
		return dtoMappingHelper.getMapperFacade().map(
				entity,
				ClauPrivadaDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ClauPrivadaDto> findAllClauPrivada() {

		LOGGER.debug("Consulta de tots les claus privades");
		
		List<ClauPrivada> llista = clauPrivadaRepository.findAll();
		
		return dtoMappingHelper.getMapperFacade().mapAsList(
				llista,
				ClauPrivadaDto.class);
	}
	
	
	// Funcions dels organismes cessionaris
	
	@Override
	@Transactional(readOnly = true)
	public List<OrganismeCessionariDto> findAllOrganismeCessionari() {

		LOGGER.debug("Consulta de tots els organismes cessionaris");
		
		List<OrganismeCessionari> llista = organismeCessionariRepository.findAll();
		
		return dtoMappingHelper.getMapperFacade().mapAsList(
				llista,
				OrganismeCessionariDto.class);
	}
	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ScspServiceImpl.class);
	
}