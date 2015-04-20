/**
 * 
 */
package es.caib.pinbal.webapp.jmesa;

import org.jmesa.util.ItemUtils;
import org.jmesa.view.editor.AbstractCellEditor;
import org.jmesa.view.html.HtmlBuilder;

/**
 * Informació relativa a un grid.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class HtmlButtonCellEditor extends AbstractCellEditor {

	private String campAfegirUrl;

	public HtmlButtonCellEditor() {
	}
	public HtmlButtonCellEditor(String campAfegirUrl) {
		this.campAfegirUrl = campAfegirUrl;
	}

	@Override
	public Object getValue(Object item, String property, int rowcount) {
		HtmlBuilder html = new HtmlBuilder();
		String[] parts = property.split("#");
		String url = parts[2];
		if (campAfegirUrl != null)
			url = url + ItemUtils.getItemValue(item, campAfegirUrl);
		html.a().href(url).append(" class=\"btn\"").close().append("<i class=\"" + parts[0] + "\"></i>").nbsp().append(parts[1]).aEnd();
		return html.toString();
	}

}
