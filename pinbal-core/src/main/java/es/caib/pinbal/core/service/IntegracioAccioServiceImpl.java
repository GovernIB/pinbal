package es.caib.pinbal.core.service;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import es.caib.pinbal.core.dto.IntegracioAccioDto;
import es.caib.pinbal.core.dto.IntegracioAccioEstatEnumDto;
import es.caib.pinbal.core.dto.IntegracioAccioParamDto;
import es.caib.pinbal.core.dto.IntegracioAccioTipusEnumDto;
import es.caib.pinbal.core.dto.IntegracioDto;
import es.caib.pinbal.core.dto.IntegracioFiltreDto;
import es.caib.pinbal.core.dto.PaginaDto;
import es.caib.pinbal.core.dto.PaginacioAmbOrdreDto;
import es.caib.pinbal.core.helper.ConfigHelper;
import es.caib.pinbal.core.helper.DtoMappingHelper;
import es.caib.pinbal.core.helper.IntegracioHelper;
import es.caib.pinbal.core.helper.PaginacioHelper;
import es.caib.pinbal.core.model.IntegracioAccioEntity;
import es.caib.pinbal.core.model.IntegracioAccioParamEntity;
import es.caib.pinbal.core.repository.IntegracioAccioParamRepository;
import es.caib.pinbal.core.repository.IntegracioAccioRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class IntegracioAccioServiceImpl implements IntegracioAccioService {
	
	@Resource
	private DtoMappingHelper dtoMappingHelper;
	
	@Resource
	private IntegracioAccioRepository integracioAccioRepository;
	
	@Resource
	private IntegracioAccioParamRepository integracioAccioParamRepository;
	
	@Resource
	private PaginacioHelper paginacioHelper;
	
	@Resource
	private IntegracioHelper integracioHelper;
	
	@Resource
	private ConfigHelper configHelper;
	
	@Transactional(readOnly = true)
	public List<IntegracioAccioDto> findAll() {
		log.debug("Consulta de tots el monitor d'integració");
		List<IntegracioAccioEntity> integracioAccions = integracioAccioRepository.findAll();
		return dtoMappingHelper.convertirList(integracioAccions, IntegracioAccioDto.class);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	@Override
	public IntegracioAccioDto create(IntegracioAccioDto integracioAccio) {
		logger.trace("Creant una nova integracioAccio (" +
				"integracioAccio=" + integracioAccio + ")");
		IntegracioAccioEntity entity = integracioAccioRepository.save(
				IntegracioAccioEntity.getBuilder(
						integracioAccio.getCodi(),
						integracioAccio.getIdPeticio(),
						integracioAccio.getData(),
						integracioAccio.getDescripcio(),
						integracioAccio.getTipus(),
						integracioAccio.getTempsResposta(),
						integracioAccio.getEstat(),
						integracioAccio.getErrorDescripcio(),
						integracioAccio.getExcepcioMessage(),
						integracioAccio.getExcepcioStacktrace()).build());
		for (IntegracioAccioParamDto paramDto : integracioAccio.getParametres()) {
			IntegracioAccioParamEntity paramEntity = IntegracioAccioParamEntity.getBuilder(
							entity, 
							paramDto.getNom(), 
							paramDto.getDescripcio()).build();
			paramEntity = integracioAccioParamRepository.save(paramEntity); 
			entity.getParametres().add(paramEntity);
		}
		return dtoMappingHelper.convertir(
				entity,
				IntegracioAccioDto.class);
	}
	
	@Transactional(readOnly = true)
	@Override
	public PaginaDto<IntegracioAccioDto> findPaginat(PaginacioAmbOrdreDto paginacioParams, IntegracioFiltreDto integracioFiltreDto/*String codiMonitor*/) {
		logger.debug("Consulta de totes les monitorIntegracios paginades (" +
				"paginacioParams=" + paginacioParams + ")");
		boolean isDataNula = integracioFiltreDto.getData() == null;
		Calendar c = new GregorianCalendar();
		Date data = integracioFiltreDto.getData();
		Date dataFi = new Date();
		if (data != null) {
			c.setTime(data);
			data = c.getTime();
			c.add(Calendar.DAY_OF_YEAR, 1);
			dataFi = c.getTime();
		}
		String codiMonitor = integracioFiltreDto.getCodi();
		String descripcio = integracioFiltreDto.getDescripcio();
		String idPeticio = integracioFiltreDto.getIdPeticio();
		IntegracioAccioEstatEnumDto estat = integracioFiltreDto.getEstat();
		IntegracioAccioTipusEnumDto tipus = integracioFiltreDto.getTipus();
		PaginaDto<IntegracioAccioDto> resposta;
		resposta = paginacioHelper.toPaginaDto(
				integracioAccioRepository.findByFiltrePaginat(
						codiMonitor,
						isDataNula,
						data, 
						dataFi, 
						descripcio == null || descripcio.isEmpty(),
						descripcio != null && !descripcio.isEmpty() ? descripcio : "",						
						estat == null, 
						estat != null ? estat : IntegracioAccioEstatEnumDto.OK,
						tipus == null, 
						tipus != null ? tipus : null, 
						idPeticio == null || idPeticio.isEmpty(),
						idPeticio != null && !idPeticio.isEmpty() ? idPeticio : "",	
						paginacioHelper.toSpringDataPageable(paginacioParams)),
				IntegracioAccioDto.class);	
		return resposta;
	}
	
	@Transactional
	@Override
	public int delete(String codi) {
		logger.debug("Esborrant dades del monitor d'integració per la integració amb codi : " + codi);
		int n = 0;
		if (codi != null) {
			// Total
			n = integracioAccioRepository.countByCodi(codi);
			// Paràmetres
			integracioAccioParamRepository.deleteByIntegracioAccioCodi(codi);
			// Entrades
			integracioAccioRepository.deleteByCodiMonitor(codi);
		}
		return n;
	}
	
	@Transactional
	@Override
	public int deleteAll() {
		logger.debug("Esborrant totes les dades del monitor d'integració");
		// Total
		int n = integracioAccioRepository.countAll();
		// Paràmetres
		integracioAccioParamRepository.deleteAll();
		// Entrades
		integracioAccioRepository.deleteAll();
		return n;		
	}
	
	
	@Override
	public int esborrarDadesAntigues(Date data) {
		logger.trace("Esborrant dades del monitor d'integració anteriors a : " + data);
		int total = 0;
		if (data != null) {
			total = integracioAccioRepository.countMonitorByDataBefore(data);
			integracioAccioParamRepository.deleteDataBefore(data);
			integracioAccioParamRepository.flush();
			integracioAccioRepository.deleteDataBefore(data);
		}
		return total;
	}
	
	@Transactional
	@Override
	public void esborrarDadesAntigesMonitorIntegracio() {
		String diesAntiguitat = configHelper.getConfig("es.caib.pinbal.tasca.auto.exp.esborrar.monitor.dies.antiguitat", "30");
		logger.debug("Execució de tasca programada d'esborrar dades del monitor d'integracions mab " + diesAntiguitat + " dies d'antiguitat.");
		try {
			int dies = Integer.parseInt(diesAntiguitat);
			Calendar c = new GregorianCalendar();
			c.setTime(new Date());
			c.add(Calendar.DATE, -dies);
			Date data = c.getTime();
			int n = esborrarDadesAntigues(data);
			if (n > 0) {
				logger.debug(n + " dades de monitor d'integració antigues esborrades.");
			}
		} catch (Exception e) {
			logger.error("Error en la tasca d'esborrar dades antigues del monitor d'integracions", e);
		}
	}
	
	@Transactional(readOnly = true)
	@Override
	public Map<String, Integer> countErrors(int numeroHores) {
		DateTime date = new DateTime();
		long dataInici = date.getMillis() - (numeroHores * 60*60*1000);
		DateTime dataIniciDate = new DateTime(dataInici);
		
		Map<String, Integer> errors = new HashMap<String, Integer>();
		List<Object[]> resultats = integracioAccioRepository.countErrorsGroupByCodi(dataIniciDate.toDate());
		for (Object[] resultat : resultats) {
			errors.put(
					(String) resultat[0], 
					((Long) resultat[1]).intValue());
		}
		return errors;
	}
	
	@Override
	public List<IntegracioDto> integracioFindAll() {
		logger.trace("Consultant les integracions");
		return integracioHelper.findAll();
	}
	
	private static final Logger logger = LoggerFactory.getLogger(IntegracioAccioServiceImpl.class);

}
