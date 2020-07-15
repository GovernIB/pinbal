package es.caib.pinbal.webapp.datatables;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

/**
 * Representació de una ordenació ServerSide de Datatables.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ServerSideOrder {

	private int column;
	private String dir;

	public int getColumn() {
		return column;
	}
	public void setColumn(int column) {
		this.column = column;
	}
	public String getDir() {
		return dir;
	}
	public void setDir(String dir) {
		this.dir = dir;
	}

	public Order toOrder(List<ServerSideColumn> columns) {
		return new Order(
				Direction.fromString(dir),
				columns.get(column).getData());
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
