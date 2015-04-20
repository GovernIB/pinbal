/**
 * 
 */
package es.caib.pinbal.webapp.jmesa;

import static org.jmesa.view.html.HtmlConstants.TOOLBAR_MAX_ROWS_DROPLIST_INCREMENTS;

import org.apache.commons.lang.StringUtils;
import org.jmesa.core.CoreContext;
import org.jmesa.limit.Limit;
import org.jmesa.view.html.toolbar.AbstractItemRenderer;
import org.jmesa.view.html.toolbar.FirstPageItemRenderer;
import org.jmesa.view.html.toolbar.LastPageItemRenderer;
import org.jmesa.view.html.toolbar.MaxRowsItemRenderer;
import org.jmesa.view.html.toolbar.NextPageItemRenderer;
import org.jmesa.view.html.toolbar.PrevPageItemRenderer;
import org.jmesa.view.html.toolbar.ToolbarItem;
import org.jmesa.view.html.toolbar.ToolbarItemRenderer;

/**
 * ItemRenderer per a emprar JMesa amb Bootstrap.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BootstrapToolbarItemRenderer extends AbstractItemRenderer {

	public BootstrapToolbarItemRenderer(
			ToolbarItem item,
			CoreContext coreContext) {
        setToolbarItem(item);
        setCoreContext(coreContext);
    }
	public String render() {
		BootstrapToolbarItem item = (BootstrapToolbarItem)getToolbarItem();
		if (item.tipus == BootstrapToolbarItem.TIPUS_PAGE) {
			return renderPageItem(item);
		} else if (item.tipus == BootstrapToolbarItem.TIPUS_PAGE_NUMITEMS) {
			return renderPageNumItems(item);
		} else {
			ToolbarItemRenderer renderer = null;
			switch (item.tipus) {
			case BootstrapToolbarItem.TIPUS_FAST_BACKWARD:
				renderer = new FirstPageItemRenderer(item, getCoreContext());
				break;
			case BootstrapToolbarItem.TIPUS_STEP_BACKWARD:
				renderer = new PrevPageItemRenderer(item, getCoreContext());
				break;
			case BootstrapToolbarItem.TIPUS_STEP_FORWARD:
				renderer = new NextPageItemRenderer(item, getCoreContext());
				break;
			case BootstrapToolbarItem.TIPUS_FAST_FORWARD:
				renderer = new LastPageItemRenderer(item, getCoreContext());
				break;
			case BootstrapToolbarItem.TIPUS_PAGE_NUMITEMS:
				renderer = new MaxRowsItemRenderer(item, getCoreContext());
				break;
			}
			renderer.setOnInvokeAction(getOnInvokeAction());
			item.setToolbarItemRenderer(renderer);
			return renderer.render();
		}
    }

	public int getCurentItems() {
		return getCoreContext().getLimit().getRowSelect().getMaxRows();
	}



	private String renderPageItem(BootstrapToolbarItem item) {
		Limit limit = getCoreContext().getLimit();
        int currentPage = limit.getRowSelect().getPage();
        int page = item.index;
        if (currentPage == page) {
            return item.disabled();
        }
        StringBuilder action = new StringBuilder();
        action.append("jQuery.jmesa.setPageToLimit('" + limit.getId() + "','" + page + "');" + getOnInvokeActionJavaScript(limit, item));
        item.setAction(action.toString());
        return item.enabled();
	}

	private String renderPageNumItems(BootstrapToolbarItem item) {
		if (item.increments == null || item.increments.length == 0) {
			String increments[] = StringUtils.split(getCoreContext().getPreference(TOOLBAR_MAX_ROWS_DROPLIST_INCREMENTS), ",");
			int[] values = new int[increments.length];
			for (int i = 0; i < increments.length; i++) {
				values[i] = Integer.valueOf(increments[i]);
			}
			item.increments = values;
		}

		Limit limit = getCoreContext().getLimit();
		int maxRows = limit.getRowSelect().getMaxRows();
		item.index = maxRows;

		if (!incrementsContainsMaxRows(item, maxRows)) {
			throw new IllegalStateException("The maxRowIncrements does not contain the maxRows.");
		}

		StringBuilder action = new StringBuilder();
		action.append("jQuery.jmesa.setMaxRowsToLimit('"
				+ limit.getId() + "', this.textContent);"
				+ getOnInvokeActionJavaScript(limit, item));
		item.setAction(action.toString());

		return item.enabled();
	}

	private boolean incrementsContainsMaxRows(BootstrapToolbarItem item, int maxRows) {
        boolean found = false;
        int[] increments = item.increments;
        for (int increment : increments) {
            if (increment == maxRows) {
                found = true;
            }
        }
        return found;
    }

}
