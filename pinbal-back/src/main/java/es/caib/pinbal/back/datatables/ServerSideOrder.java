package es.caib.pinbal.back.datatables;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

import java.util.List;

/**
 * Representació de una ordenació ServerSide de Datatables.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServerSideOrder {

	private int column;
	private String dir;

	public Order toOrder(List<ServerSideColumn> columns) {
		return new Order(
				Direction.fromString(dir),
				columns.get(column).getData());
	}

}
