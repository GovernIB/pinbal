package es.caib.pinbal.back.datatables;

import es.caib.pinbal.back.datatables.DatatablesHelper.DatatablesParams;
import es.caib.pinbal.logic.intf.dto.PaginacioAmbOrdreDto;
import es.caib.pinbal.logic.intf.dto.PaginacioAmbOrdreDto.OrdreDireccioDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Representació d'una petició ServerSide de Datatables.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServerSideRequest {

	private int draw;
	private int start;
	private int length;
	private ServerSideSearch search;
	private List<ServerSideOrder> order;
	private List<ServerSideColumn> columns;

	public static PaginacioAmbOrdreDto getPaginacioDtoFromRequest(
			HttpServletRequest request) {
		return getPaginacioDtoFromRequest(request, null, null);
	}
	
	private static PaginacioAmbOrdreDto getPaginacioDtoFromRequest(
			HttpServletRequest request,
			Map<String, String[]> mapeigFiltres,
			Map<String, String[]> mapeigOrdenacions) {
		DatatablesParams params = new DatatablesParams(request);
		LOGGER.debug("Informació de la pàgina obtingudes de datatables (" +
				"draw=" + params.getDraw() + ", " +
				"start=" + params.getStart() + ", " +
				"length=" + params.getLength() + ")");
		PaginacioAmbOrdreDto paginacio = new PaginacioAmbOrdreDto();
		int paginaNum = params.getStart() / params.getLength();
		paginacio.setPaginaNum(paginaNum);
		if (params.getLength() != null && params.getLength().intValue() == -1) {
			paginacio.setPaginaTamany(Integer.MAX_VALUE);
		} else {
			paginacio.setPaginaTamany(params.getLength());
		}
		paginacio.setFiltre(params.getSearchValue());
		for (int i = 0; i < params.getColumnsSearchValue().size(); i++) {
			String columna = params.getColumnsData().get(i);
			String[] columnes = new String[] {columna};
			if (mapeigFiltres != null && mapeigFiltres.get(columna) != null) {
				columnes = mapeigFiltres.get(columna);
			}
			for (String col: columnes) {
				if (!"<null>".equals(col)) {
					paginacio.afegirFiltre(
							col,
							params.getColumnsSearchValue().get(i));
					LOGGER.debug("Afegit filtre a la paginació (" +
							"columna=" + col + ", " +
							"valor=" + params.getColumnsSearchValue().get(i) + ")");
				}
			}
		}
		for (int i = 0; i < params.getOrderColumn().size(); i++) {
			int columnIndex = params.getOrderColumn().get(i);
			String columna = params.getColumnsData().get(columnIndex);
			OrdreDireccioDto direccio;
			if ("asc".equals(params.getOrderDir().get(i)))
				direccio = OrdreDireccioDto.ASCENDENT;
			else
				direccio = OrdreDireccioDto.DESCENDENT;
			String[] columnes = new String[] {columna};
			if (mapeigOrdenacions != null && mapeigOrdenacions.get(columna) != null) {
				columnes = mapeigOrdenacions.get(columna);
			}
			for (String col: columnes) {
				paginacio.afegirOrdre(col, direccio);
				LOGGER.debug("Afegida ordenació a la paginació (columna=" + columna + ", direccio=" + direccio + ")");
			}
		}
		LOGGER.debug("Informació de la pàgina sol·licitada (paginaNum=" + paginacio.getPaginaNum() + ", paginaTamany=" + paginacio.getPaginaTamany() + ")");
		return paginacio;
	}

	
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

	public PageRequest toPageable() {
		int paginaNum = start / length;
		List<Order> orders = new ArrayList<Order>();
		for (ServerSideOrder sso: order) {
			orders.add(sso.toOrder(columns));
		}
		return PageRequest.of(
				paginaNum,
				length,
				Sort.by(orders.toArray(new Order[orders.size()])));
	}

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerSideRequest.class);

}
