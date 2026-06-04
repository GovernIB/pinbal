package es.caib.pinbal.back.datatables;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representació d'una columna ServerSide de Datatables.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServerSideColumn {

	private String data;
	private String name;
	private boolean searchable;
	private boolean orderable;
	private ServerSideSearch search;

}
