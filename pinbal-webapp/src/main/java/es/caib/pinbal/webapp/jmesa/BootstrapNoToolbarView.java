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
import org.jmesa.view.html.component.HtmlColumn;
import org.jmesa.view.html.component.HtmlRow;

/**
 * Toolbar fet a mida per l'aplicació sense toolbar ni statusbar.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BootstrapNoToolbarView extends AbstractHtmlView {

	public Object render() {
        HtmlSnippets snippets = getHtmlSnippets();
        HtmlBuilder html = new HtmlBuilder();
        CoreContext coreContext = getCoreContext();
        if (coreContext.getAllItems().size() > 0) {
	        html.append(snippets.tableStart());
	        html.append(snippets.theadStart());
	        renderHeader(html);
	        html.append(snippets.theadEnd());
	        html.append(snippets.tbodyStart());
	        html.append(snippets.body());
	        html.append(snippets.tbodyEnd());
	        html.append(snippets.footer());
	        html.append(snippets.tableEnd());
	        html.append(snippets.initJavascriptLimit());
        }
        return html.toString();
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
