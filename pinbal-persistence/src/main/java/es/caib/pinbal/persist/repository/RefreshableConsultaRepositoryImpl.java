/**
 * 
 */
package es.caib.pinbal.persist.repository;

import es.caib.pinbal.persist.entity.Consulta;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
