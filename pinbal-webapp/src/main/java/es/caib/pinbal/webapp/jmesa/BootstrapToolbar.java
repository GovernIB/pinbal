/**
 * 
 */
package es.caib.pinbal.webapp.jmesa;

import static org.jmesa.view.html.HtmlConstants.ON_INVOKE_ACTION;
import static org.jmesa.view.html.HtmlConstants.TOOLBAR_MAX_PAGE_NUMBERS;
import static org.jmesa.view.html.HtmlUtils.totalPages;

import org.jmesa.limit.Limit;
import org.jmesa.limit.RowSelect;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.toolbar.AbstractToolbar;
import org.jmesa.view.html.toolbar.ToolbarItem;

/**
 * Toolbar fet a mida per l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BootstrapToolbar extends AbstractToolbar {

	private boolean itemsAdded = false;

	@Override
	public String render() {
		addItems();
		HtmlBuilder html = new HtmlBuilder();
		html.div().styleClass("btn-toolbar").close();
		// Num. items per pàgina
		html.div().styleClass("btn-group").close();
		ToolbarItem itemNumItems = newItem(BootstrapToolbarItem.TIPUS_PAGE_NUMITEMS, 0);
		html.append(itemNumItems.getToolbarItemRenderer().render());
		html.divEnd();
		// Fi num. items per pàgina
		// Paginació
		html.div().styleClass("btn-group").close();
		for (ToolbarItem item : getToolbarItems()) {
			html.append(item.getToolbarItemRenderer().render());
		}
		html.divEnd();
		// Fi paginació
		html.divEnd();
		return html.toString();
	}



	private void addItems() {
		if (!itemsAdded) {
			addToolbarItem(newItem(BootstrapToolbarItem.TIPUS_FAST_BACKWARD, 0));
			addToolbarItem(newItem(BootstrapToolbarItem.TIPUS_STEP_BACKWARD, 0));
			addPageNumberItems();
			addToolbarItem(newItem(BootstrapToolbarItem.TIPUS_STEP_FORWARD, 0));
			addToolbarItem(newItem(BootstrapToolbarItem.TIPUS_FAST_FORWARD, 0));
			itemsAdded = true;
		}
	}

	private void addPageNumberItems() {
		Limit limit = getCoreContext().getLimit();
		RowSelect rowSelect = limit.getRowSelect();
		int page = rowSelect.getPage();
		int totalPages = totalPages(getCoreContext());
		int maxPages = Integer.valueOf(getCoreContext().getPreference(
				TOOLBAR_MAX_PAGE_NUMBERS));
		int centerPage = maxPages / 2 + 1;
		int startEndPages = maxPages / 2;
		if (totalPages > maxPages) {
			int start;
			int end;
			if (page <= centerPage) { // the start of the pages
				start = 1;
				end = maxPages;
			} else if (page >= totalPages - startEndPages) { // the last few pages
				start = totalPages - (maxPages - 1);
				end = totalPages;
			} else { // center everything else
				start = page - startEndPages;
				end = page + startEndPages;
			}
			for (int i = start; i <= end; i++) {
				addToolbarItem(newItem(BootstrapToolbarItem.TIPUS_PAGE, i));
			}
		} else {
			for (int i = 1; i <= totalPages; i++) {
				addToolbarItem(newItem(BootstrapToolbarItem.TIPUS_PAGE, i));
			}
		}
	}

	private BootstrapToolbarItem newItem(int tipus, int page) {
		BootstrapToolbarItem item = new BootstrapToolbarItem(tipus, page);
		BootstrapToolbarItemRenderer renderer = new BootstrapToolbarItemRenderer(
				item,
				getCoreContext());
		renderer.setOnInvokeAction(getOnInvokeAction());
		item.setToolbarItemRenderer(renderer);
		return item;
	}

	protected String getOnInvokeAction() {
		return getCoreContext().getPreference(ON_INVOKE_ACTION);
	}

}
