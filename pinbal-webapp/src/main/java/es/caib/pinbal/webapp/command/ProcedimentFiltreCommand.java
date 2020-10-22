/**
 * 
 */
package es.caib.pinbal.webapp.command;

import es.caib.pinbal.core.dto.FiltreActiuEnumDto;
import lombok.Data;

/**
 * Command per a filtrar els procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
public class ProcedimentFiltreCommand {
	

	private String codi;
	private String nom;
	private String departament;
	private Long organGestorId;
	private String codiSia;
	private FiltreActiuEnumDto actiu;
	
	
	
	
}
