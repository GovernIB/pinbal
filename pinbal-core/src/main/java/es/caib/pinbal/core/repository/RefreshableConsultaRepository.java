/**
 * 
 */
package es.caib.pinbal.core.repository;

import es.caib.pinbal.core.model.Consulta;

/**
 * Interfície per a poder refrescar les consultes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface RefreshableConsultaRepository {

	void refresh(Consulta consulta);

}
