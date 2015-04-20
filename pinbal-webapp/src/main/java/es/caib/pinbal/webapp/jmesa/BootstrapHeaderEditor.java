/**
 * 
 */
package es.caib.pinbal.webapp.jmesa;

import org.jmesa.limit.Limit;
import org.jmesa.limit.Order;
import org.jmesa.limit.Sort;
import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.component.HtmlColumn;
import org.jmesa.view.html.editor.HtmlHeaderEditor;

/**
 * Toolbar fet a mida per l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BootstrapHeaderEditor extends HtmlHeaderEditor {

	@Override
	public Object getValue() {
        HtmlBuilder html = new HtmlBuilder();
        
        html.div();

        Limit limit = getCoreContext().getLimit();
        HtmlColumn column = getColumn();

        if (column.isSortable()) {
            Sort sort = limit.getSortSet().getSort(column.getProperty());

            html.onmouseover("this.style.cursor='pointer'");
            html.onmouseout("this.style.cursor='default'");

            if (sort != null) {
                Order nextOrder = nextSortOrder(sort.getOrder(), column);
                html.append(onclick(nextOrder, column, limit));
            } else {
                Order[] sortOrder = column.getSortOrder();
                if (sortOrder[0] == Order.NONE) {
                    Order nextOrder = nextSortOrder(sortOrder[0], column);
                    html.append(onclick(nextOrder, column, limit));
                } else {
                    Order nextOrder = sortOrder[0];
                    html.append(onclick(nextOrder, column, limit));
                }
            }
        }
        
        html.close();
        html.append(getTitle(column));

        if (column.isSortable()) {
            Sort sort = limit.getSortSet().getSort(column.getProperty());
            if (sort != null) {
                if (sort.getOrder() == Order.ASC) {
                    html.nbsp();
                    html.append("<b class=\"caret reverse\"></b>");
                } else if (sort.getOrder() == Order.DESC) {
                    html.nbsp();
                    html.append("<b class=\"caret\"></b>");
                }
            } else {
                /*String sortDefaultImage = getCoreContext().getPreference(HtmlConstants.SORT_DEFAULT_IMAGE);
                if (sortDefaultImage != null && sortDefaultImage.length() > 0) {
                    html.nbsp();
                    html.img();
                    html.src(imagesPath + sortDefaultImage);
                    html.style("border:0");
                    html.alt("Arrow");
                    html.end();
                }*/
            }
        }
        
        html.divEnd();

        return html.toString();
    }

}
