/**
 * 
 */
package es.caib.pinbal.webapp.jmesa;

/**
 * Informació relativa a un grid.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class GridInfo {

	private int startIndex;
	private int pageSize;
	private int totalRows;

	public int getStartIndex() {
		return startIndex;
	}
	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getTotalRows() {
		return totalRows;
	}
	public void setTotalRows(int totalRows) {
		this.totalRows = totalRows;
	}

}
