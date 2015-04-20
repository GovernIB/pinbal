/**
 * 
 */
package es.caib.pinbal.webapp.jmesa;

import org.jmesa.view.html.HtmlBuilder;
import org.jmesa.view.html.toolbar.AbstractItem;

/**
 * Item per al toolbar de JMesa emprant Bootstrap.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BootstrapToolbarItem extends AbstractItem {

	public static final int TIPUS_FAST_BACKWARD = 0;
	public static final int TIPUS_STEP_BACKWARD = 1;
	public static final int TIPUS_PAGE = 2;
	public static final int TIPUS_STEP_FORWARD = 3;
	public static final int TIPUS_FAST_FORWARD = 4;
	public static final int TIPUS_PAGE_NUMITEMS = 5;

	int tipus;
	int index;
	int[] increments;

	public BootstrapToolbarItem(int tipus) {
		this.tipus = tipus;
	}
	public BootstrapToolbarItem(int tipus, int index) {
		this.tipus = tipus;
		this.index = index;
	}

	@Override
	public String disabled() {
		return render(true);
	}
	@Override
	public String enabled() {
		return render(false);
	}

	public String render(boolean disabled) {
		String styleClass = getStyleClass();
		if (styleClass == null || styleClass.length() == 0) 
			styleClass = "btn";
		else
			styleClass = styleClass + " btn";
		if (tipus == TIPUS_PAGE_NUMITEMS)
			styleClass = styleClass + " dropdown-toggle";
		if (disabled) {
			if (tipus != TIPUS_PAGE)
				styleClass = styleClass + " disabled";
			else
				styleClass = styleClass + " btn-primary";
		}
		String content;
		switch (tipus) {
		case TIPUS_FAST_BACKWARD:
			content = "icon-fast-backward";
			break;
		case TIPUS_STEP_BACKWARD:
			content = "icon-step-backward";
			break;
		case TIPUS_STEP_FORWARD:
			content = "icon-step-forward";
			break;
		case TIPUS_FAST_FORWARD:
			content = "icon-fast-forward";
			break;
		default:
			content = new Integer(index).toString();
		}
		String action = (!disabled) ? getAction() : "";
		HtmlBuilder html = new HtmlBuilder();
		if (tipus == TIPUS_PAGE) {
			html.a().styleClass(styleClass).href("#").onclick(action).close().append(content).aEnd();
		} else if (tipus == TIPUS_PAGE_NUMITEMS) {
			int currentItems = ((BootstrapToolbarItemRenderer)getToolbarItemRenderer()).getCoreContext().getLimit().getRowSelect().getMaxRows();
			html.button().styleClass(styleClass).append("data-toggle=\"dropdown\"").close().append(currentItems + " ").span().styleClass("caret").close().spanEnd().buttonEnd();
			html.ul().styleClass("dropdown-menu").close();
			for (int i = 0; i < increments.length; i ++) {
				html.li().close().a().href("#").onclick(action).close().append(new Integer(increments[i])).aEnd().liEnd();
			}
			html.ulEnd();
		} else {
			html.a().styleClass(styleClass).href("#").onclick(action).close().append("<i class=\"" + content + "\"></i>").aEnd();
		}
		return html.toString();
	}

}
