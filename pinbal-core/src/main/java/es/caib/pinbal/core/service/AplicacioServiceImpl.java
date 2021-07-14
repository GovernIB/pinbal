/**
 * 
 */
package es.caib.pinbal.core.service;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import es.caib.pinbal.core.dto.IntegracioAccioDto;
import es.caib.pinbal.core.dto.IntegracioDto;
import es.caib.pinbal.core.helper.IntegracioHelper;

/**
 * Implementació dels mètodes per a gestionar l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class AplicacioServiceImpl implements AplicacioService {


	@Resource
	private IntegracioHelper integracioHelper;



	@Override
	public List<IntegracioDto> integracioFindAll() {
		
		logger.trace("Consultant les integracions");
		return integracioHelper.findAll();
	}

	@Override
	public List<IntegracioAccioDto> integracioFindDarreresAccionsByCodi(String codi) {
		logger.trace("Consultant les darreres accions per a la integració (" +
				"codi=" + codi + ")");
		return integracioHelper.findAccionsByIntegracioCodi(codi);
	}



	private static final Logger logger = LoggerFactory.getLogger(AplicacioServiceImpl.class);

}
