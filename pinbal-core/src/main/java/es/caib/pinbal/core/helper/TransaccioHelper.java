/**
 * 
 */
package es.caib.pinbal.core.helper;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import es.caib.pinbal.core.model.Consulta;
import es.caib.pinbal.core.model.Consulta.EstatTipus;
import es.caib.pinbal.core.repository.ConsultaRepository;


/**
 * Helper per a executar els mètodes dins transaccions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class TransaccioHelper {

	@Resource
	private ConsultaRepository consultaRepository;

	
	@Transactional
	public void updateErrorConsulta(
			Long consultaId,
			String error) {
		Consulta consulta = consultaRepository.findOne(consultaId);
		consulta.updateEstat(EstatTipus.Error);
		consulta.updateEstatError(error);
	}

}
