package es.caib.pinbal.webapp.datatables;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

public class DatatablesHelper {

	public static class DatatablesParams {
		private Integer draw;
		private Integer start;
		private Integer length;
		private String searchValue;
		private Boolean searchRegex;
		private List<Integer> orderColumn = new ArrayList<Integer>();
		private List<String> orderDir = new ArrayList<String>();
		private List<String> columnsData = new ArrayList<String>();
		private List<String> columnsName = new ArrayList<String>();
		private List<Boolean> columnsSearchable = new ArrayList<Boolean>();
		private List<Boolean> columnsOrderable = new ArrayList<Boolean>();
		private List<String> columnsSearchValue = new ArrayList<String>();
		private List<Boolean> columnsSearchRegex = new ArrayList<Boolean>();
		public DatatablesParams(HttpServletRequest request) {
			if (request.getParameter("draw") != null)
				draw = Integer.parseInt(request.getParameter("draw"));
			if (request.getParameter("start") != null)
				start = Integer.parseInt(request.getParameter("start"));
			if (request.getParameter("length") != null)
				length = Integer.parseInt(request.getParameter("length"));
			if (request.getParameter("search[value]") != null)
				searchValue = request.getParameter("search[value]");
			if (request.getParameter("search[regex]") != null)
				searchRegex = Boolean.parseBoolean(request.getParameter("search[regex]"));
			for (int i = 0;; i++) {
				String paramPrefix = "order[" + i + "]";
				if (request.getParameter(paramPrefix + "[column]") != null) {
					orderColumn.add(Integer.parseInt(request.getParameter(paramPrefix + "[column]")));
					orderDir.add(request.getParameter(paramPrefix + "[dir]"));
				} else {
					break;
				}
			}
			for (int i = 0;; i++) {
				String paramPrefix = "columns[" + i + "]";
				if (request.getParameter(paramPrefix + "[data]") != null) {
					columnsData.add(request.getParameter(paramPrefix + "[data]"));
					columnsName.add(request.getParameter(paramPrefix + "[name]"));
					columnsSearchable.add(Boolean.parseBoolean(request.getParameter(paramPrefix + "[searchable]")));
					columnsOrderable.add(Boolean.parseBoolean(request.getParameter(paramPrefix + "[orderable]")));
					columnsSearchValue.add(request.getParameter(paramPrefix + "[search][value]"));
					columnsSearchRegex.add(Boolean.parseBoolean(request.getParameter(paramPrefix + "[search][regex]")));
				} else {
					break;
				}
			}
		}
		public Integer getDraw() {
			return draw;
		}
		public void setDraw(Integer draw) {
			this.draw = draw;
		}
		public Integer getStart() {
			return start;
		}
		public void setStart(Integer start) {
			this.start = start;
		}
		public Integer getLength() {
			return length;
		}
		public void setLength(Integer length) {
			this.length = length;
		}
		public String getSearchValue() {
			return searchValue;
		}
		public void setSearchValue(String searchValue) {
			this.searchValue = searchValue;
		}
		public Boolean getSearchRegex() {
			return searchRegex;
		}
		public void setSearchRegex(Boolean searchRegex) {
			this.searchRegex = searchRegex;
		}
		public List<Integer> getOrderColumn() {
			return orderColumn;
		}
		public void setOrderColumn(List<Integer> orderColumn) {
			this.orderColumn = orderColumn;
		}
		public List<String> getOrderDir() {
			return orderDir;
		}
		public void setOrderDir(List<String> orderDir) {
			this.orderDir = orderDir;
		}
		public List<String> getColumnsData() {
			return columnsData;
		}
		public void setColumnsData(List<String> columnsData) {
			this.columnsData = columnsData;
		}
		public List<String> getColumnsName() {
			return columnsName;
		}
		public void setColumnsName(List<String> columnsName) {
			this.columnsName = columnsName;
		}
		public List<Boolean> getColumnsSearchable() {
			return columnsSearchable;
		}
		public void setColumnsSearchable(List<Boolean> columnsSearchable) {
			this.columnsSearchable = columnsSearchable;
		}
		public List<Boolean> getColumnsOrderable() {
			return columnsOrderable;
		}
		public void setColumnsOrderable(List<Boolean> columnsOrderable) {
			this.columnsOrderable = columnsOrderable;
		}
		public List<String> getColumnsSearchValue() {
			return columnsSearchValue;
		}
		public void setColumnsSearchValue(List<String> columnsSearchValue) {
			this.columnsSearchValue = columnsSearchValue;
		}
		public List<Boolean> getColumnsSearchRegex() {
			return columnsSearchRegex;
		}
		public void setColumnsSearchRegex(List<Boolean> columnsSearchRegex) {
			this.columnsSearchRegex = columnsSearchRegex;
		}
	}
	
}
