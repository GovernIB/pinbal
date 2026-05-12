package es.caib.pinbal.webapp.datatables;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Representació d'una columna ServerSide de Datatables.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ServerSideColumn {

	private String data;
	private String name;
	private boolean searchable;
	private boolean orderable;
	private ServerSideSearch search;

	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isSearchable() {
		return searchable;
	}
	public void setSearchable(boolean searchable) {
		this.searchable = searchable;
	}
	public boolean isOrderable() {
		return orderable;
	}
	public void setOrderable(boolean orderable) {
		this.orderable = orderable;
	}
	public ServerSideSearch getSearch() {
		return search;
	}
	public void setSearch(ServerSideSearch search) {
		this.search = search;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
