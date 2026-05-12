/**
 * 
 */
package es.caib.pinbal.logic.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import es.caib.pinbal.logic.model.Consulta;

/**
 * Interfície per a poder refrescar les consultes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class RefreshableConsultaRepositoryImpl implements RefreshableConsultaRepository {

	@PersistenceContext
	private EntityManager em;

	@Override
	public void refresh(Consulta consulta) {
		em.refresh(consulta);
	}

}
