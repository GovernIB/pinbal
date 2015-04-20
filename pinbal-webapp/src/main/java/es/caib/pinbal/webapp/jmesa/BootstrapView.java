/**
 * 
 */
package es.caib.pinbal.webapp.jmesa;

import java.util.Iterator;
import java.util.List;

import org.jmesa.core.CoreContext;
import org.jmesa.view.component.Column;
import org.jmesa.view.html.AbstractHtmlView;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.HtmlConstants;
import org.jmesa.view.html.HtmlSnippets;
import org.jmesa.view.html.HtmlUtils;
import org.jmesa.view.html.component.HtmlColumn;
import org.jmesa.view.html.component.HtmlRow;

/**
 * Toolbar fet a mida per l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BootstrapView extends AbstractHtmlView {

	private boolean headerStatusbar = true;
	private boolean headerToolbar = false;
	private boolean footerStatusbar = false;
	private boolean footerToolbar = true;

	public Object render() {
        HtmlSnippets snippets = getHtmlSnippets();
        HtmlBuilder html = new HtmlBuilder();
        if (headerStatusbar || headerToolbar) {
        	if (headerStatusbar && !headerToolbar) {
        		renderStatusbar(html, snippets.statusBarText());
        	} else if (!headerStatusbar && headerToolbar) {
        		renderToolbar(html, snippets.statusBarText());
        	} else {
        		 renderStatusAndToolbar(html, snippets.statusBarText());
        	}
        }
        CoreContext coreContext = getCoreContext();
        if (coreContext.getAllItems().size() > 0) {
	        html.append(snippets.tableStart());
	        html.append(snippets.theadStart());
	        renderHeader(html);
	        //html.append(snippets.header());
	        html.append(snippets.theadEnd());
	        html.append(snippets.tbodyStart());
	        html.append(snippets.body());
	        html.append(snippets.tbodyEnd());
	        html.append(snippets.footer());
	        html.append(snippets.tableEnd());
	        if (footerStatusbar || footerToolbar) {
	        	if (footerStatusbar && !footerToolbar) {
	        		renderStatusbar(html, snippets.statusBarText());
	        	} else if (!footerStatusbar && footerToolbar) {
	        		renderToolbar(html, snippets.statusBarText());
	        	} else {
	        		 renderStatusAndToolbar(html, snippets.statusBarText());
	        	}
	        }
	        html.append(snippets.initJavascriptLimit());
        }
        return html.toString();
    }



	private void renderToolbar(
			HtmlBuilder html,
			String statusText) {
		CoreContext coreContext = getCoreContext();
        if (HtmlUtils.totalPages(coreContext) > 1) {
        	html.div().styleClass("row-fluid").close().div().styleClass("span12").close();
        	html.div().styleClass("pull-right").close().append(getToolbar().render()).divEnd();
        	html.divEnd().divEnd();
        }
	}
	private void renderStatusbar(
			HtmlBuilder html,
			String statusText) {
		html.div().styleClass("well").style("margin-top:8px;padding:4px 4px").close().append(statusText).divEnd();
	}
	private void renderStatusAndToolbar(
			HtmlBuilder html,
			String statusText) {
		CoreContext coreContext = getCoreContext();
        if (HtmlUtils.totalPages(coreContext) > 1) {
			html.div().styleClass("row-fluid").close();
			html.div().styleClass("span7").close().div().styleClass("well").style("margin-top:8px;padding:4px 4px").close().append(statusText).divEnd().divEnd();
			html.div().styleClass("span5").close().div().styleClass("pull-right").close().append(getToolbar().render()).divEnd();
			html.divEnd().divEnd();
        } else {
        	renderStatusbar(html, statusText);
        }
	}

	private void renderHeader(HtmlBuilder html) {
        String headerClass = getCoreContext().getPreference(HtmlConstants.HEADER_CLASS);
        html.tr(1).styleClass(headerClass).close();
        HtmlRow row = getTable().getRow();
        List<Column> columns = row.getColumns();
        for (Iterator<Column> iter = columns.iterator(); iter.hasNext();) {
            HtmlColumn column = (HtmlColumn) iter.next();
            BootstrapHeaderEditor editor = new BootstrapHeaderEditor();
            editor.setCoreContext(getCoreContext());
            editor.setColumn(column);
            column.setHeaderEditor(editor);
            html.append(column.getHeaderRenderer().render());
        }
        html.trEnd(1);
	}

}
