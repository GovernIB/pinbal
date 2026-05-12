/**
 * 
 */
package es.caib.pinbal.persist.repository;

import es.caib.pinbal.persist.entity.Consulta;

/**
 * Interfície per a poder refrescar les consultes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface RefreshableConsultaRepository {

	void refresh(Consulta consulta);

}
