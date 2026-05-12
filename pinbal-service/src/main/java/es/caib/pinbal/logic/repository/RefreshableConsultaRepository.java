/**
 * 
 */
package es.caib.pinbal.logic.repository;

import es.caib.pinbal.logic.model.Consulta;

/**
 * Interfície per a poder refrescar les consultes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface RefreshableConsultaRepository {

	void refresh(Consulta consulta);

}
