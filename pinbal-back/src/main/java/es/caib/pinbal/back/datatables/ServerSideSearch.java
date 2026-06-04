package es.caib.pinbal.back.datatables;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representació de una cerca ServerSide de Datatables.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServerSideSearch {

	private String value;
	private boolean regex;

}
