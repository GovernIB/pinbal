/**
 * 
 */
package es.caib.pinbal.webapp.jmesa;

import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jmesa.facade.TableFacade;
import org.jmesa.limit.Limit;
import org.jmesa.limit.LimitActionFactory;
import org.jmesa.limit.Order;
import org.jmesa.limit.RowSelect;
import org.jmesa.limit.Sort;
import org.jmesa.model.TableModel;
import org.jmesa.model.TableModelUtils;
import org.jmesa.view.component.Column;
import org.jmesa.view.html.component.HtmlColumn;
import org.jmesa.view.html.component.HtmlRow;
import org.jmesa.view.html.component.HtmlTable;

import es.caib.pinbal.core.dto.OrdreDto;
import es.caib.pinbal.core.dto.OrdreDto.OrdreDireccio;
import es.caib.pinbal.core.dto.PaginaLlistatDto;
import es.caib.pinbal.core.dto.PaginacioAmbOrdreDto;

/**
 * Helper per a la creació de grids emprant JMesa
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class JMesaGridHelper {

	public static TableModel crearGrid(
			HttpServletRequest request,
			String gridId,
			String campIdentificador,
			List<InfoColumna> columnes,
			boolean esDescripcioKeys,
			Collection<?> items) {
		TableModel tableModel = new TableModel(gridId, request);
		tableModel.setEditable(false);
		tableModel.setToolbar(new BootstrapToolbar());
		tableModel.setItems(items);
		HtmlTable htmlTable = new HtmlTable();
		HtmlRow htmlRow = new HtmlRow();
		htmlRow.setUniqueProperty(campIdentificador);
		htmlRow.setHighlighter(false);
		for (InfoColumna columna: columnes) {
			if (columna.isEsAccio()) {
				String propietat = columna.getAccioIcona() + "#" + columna.getAccioDescripcio() + "#" + columna.getAccioUrl();
				Column col = new HtmlColumn(propietat);
				col.setTitle("&nbsp;");
				col.setCellEditor(new HtmlButtonCellEditor(columna.getAccioCampAfegir()));
				htmlRow.addColumn(col);
			} else {
				Column col = new HtmlColumn(columna.getCamp());
				if (columna.getDescripcio() != null) {
					if (esDescripcioKeys)
						col.setTitleKey(columna.getDescripcio());
					else
						col.setTitle(columna.getDescripcio());
				}
				htmlRow.addColumn(col);
			}
		}
		htmlTable.setRow(htmlRow);
		tableModel.setTable(htmlTable);
		tableModel.setView(new BootstrapView());
		return tableModel;
	}

	public static List<?> consultarPaginaIActualitzarLimit(
			String id,
			HttpServletRequest request,
			ConsultaPagina<?> consultaPagina,
			OrdreDto ordrePerDefecte) throws Exception {
		TableFacade tableFacade = new TableFacade(id, request);
		LimitActionFactory limitActionFactory = new LimitActionFactory(
				id,
				tableFacade.getWebContext().getParameterMap());
		int page = limitActionFactory.getPage();
		Integer limitMaxRows = limitActionFactory.getMaxRows();
		int maxRows = (limitMaxRows != null) ? limitMaxRows.intValue() : tableFacade.getMaxRows();
		PaginacioAmbOrdreDto paginacio = new PaginacioAmbOrdreDto();
		paginacio.setPaginaNum(page - 1);
		paginacio.setPaginaTamany(maxRows);
		Limit limit = tableFacade.getLimit();
		configurarOrdresAmbLimit(
				paginacio,
				limit,
				ordrePerDefecte);
		PaginaLlistatDto<?> pagina = consultaPagina.consultar(paginacio);
        int totalRows = new Long(pagina.getElementsTotal()).intValue();
        if (limit.hasRowSelect()) {
            limit.setRowSelect(
            		new RowSelect(
            				limit.getRowSelect().getPage(),
            				limit.getRowSelect().getMaxRows(),
            				totalRows));
        } else {
            tableFacade.setTotalRows(totalRows);
        }
        request.setAttribute(tableFacade.getId() + TableModelUtils.LIMIT_ATTR, tableFacade.getLimit());
        return pagina.getContingut();
	}

	private static void configurarOrdresAmbLimit(
			PaginacioAmbOrdreDto paginacio,
			Limit limit,
			OrdreDto ordrePerDefecte) {
		if (limit.getSortSet() != null && limit.getSortSet().isSorted()) {
			for (Sort sort: limit.getSortSet().getSorts()) {
				if (!Order.NONE.equals(sort.getOrder())) {
					OrdreDto ordre = new OrdreDto(
							sort.getProperty(),
							(Order.ASC.equals(sort.getOrder())) ? OrdreDireccio.ASCENDENT : OrdreDireccio.DESCENDENT);
					paginacio.afegirOrdre(ordre);
				}
			}
		} else {
			paginacio.afegirOrdre(ordrePerDefecte);
		}
	}

	public interface ConsultaPagina<T> {
		public PaginaLlistatDto<T> consultar(PaginacioAmbOrdreDto paginacioAmbOrdre) throws Exception;
	}

}
