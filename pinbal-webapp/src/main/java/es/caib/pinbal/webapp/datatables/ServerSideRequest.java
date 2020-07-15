package es.caib.pinbal.webapp.datatables;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

/**
 * Representació d'una petició ServerSide de Datatables.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ServerSideRequest {

	private int draw;
	private int start;
	private int length;
	private ServerSideSearch search;
	private List<ServerSideOrder> order;
	private List<ServerSideColumn> columns;

	public ServerSideRequest(HttpServletRequest request) {
		if (request.getParameter("draw") != null)
			draw = Integer.parseInt(request.getParameter("draw"));
		if (request.getParameter("start") != null)
			start = Integer.parseInt(request.getParameter("start"));
		if (request.getParameter("length") != null)
			length = Integer.parseInt(request.getParameter("length"));
		String searchValue = request.getParameter("search[value]");
		if (searchValue != null && !searchValue.isEmpty()) {
			search = new ServerSideSearch();
			search.setValue(searchValue);
			search.setRegex(
					Boolean.parseBoolean(request.getParameter("search[regex]")));
		}
		for (int i = 0;; i++) {
			String paramPrefix = "order[" + i + "]";
			String orderColumn = request.getParameter(paramPrefix + "[column]");
			if (orderColumn != null) {
				ServerSideOrder sso = new ServerSideOrder();
				sso.setColumn(
						Integer.parseInt(request.getParameter(paramPrefix + "[column]")));
				sso.setDir(request.getParameter(paramPrefix + "[dir]"));
				if (order == null) {
					order = new ArrayList<ServerSideOrder>();
				}
				order.add(sso);
			} else {
				break;
			}
		}
		for (int i = 0;; i++) {
			String paramPrefix = "columns[" + i + "]";
			if (request.getParameter(paramPrefix + "[data]") != null) {
				ServerSideColumn column = new ServerSideColumn();
				column.setData(
						request.getParameter(paramPrefix + "[data]"));
				column.setName(
						request.getParameter(paramPrefix + "[name]"));
				column.setSearchable(
						Boolean.parseBoolean(request.getParameter(paramPrefix + "[searchable]")));
				column.setOrderable(
						Boolean.parseBoolean(request.getParameter(paramPrefix + "[orderable]")));
				String columnSearchValue = request.getParameter(paramPrefix + "[search][value]");
				if (columnSearchValue != null && !columnSearchValue.isEmpty()) {
					ServerSideSearch columnSearch = new ServerSideSearch();
					columnSearch.setValue(columnSearchValue);
					columnSearch.setRegex(
							Boolean.parseBoolean(request.getParameter(paramPrefix + "[search][regex]")));
					column.setSearch(columnSearch);
				}
				if (columns == null) {
					columns = new ArrayList<ServerSideColumn>();
				}
				columns.add(column);
			} else {
				break;
			}
		}
	}

	public int getDraw() {
		return draw;
	}
	public void setDraw(int draw) {
		this.draw = draw;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public ServerSideSearch getSearch() {
		return search;
	}
	public void setSearch(ServerSideSearch search) {
		this.search = search;
	}
	public List<ServerSideOrder> getOrder() {
		return order;
	}
	public void setOrder(List<ServerSideOrder> order) {
		this.order = order;
	}
	public List<ServerSideColumn> getColumns() {
		return columns;
	}
	public void setColumns(List<ServerSideColumn> columns) {
		this.columns = columns;
	}

	public PageRequest toPageable() {
		int paginaNum = start / length;
		List<Order> orders = new ArrayList<Order>();
		for (ServerSideOrder sso: order) {
			orders.add(sso.toOrder(columns));
		}
		return new PageRequest(
				paginaNum,
				length,
				new Sort(orders.toArray(new Order[orders.size()])));
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
