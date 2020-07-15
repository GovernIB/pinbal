package es.caib.pinbal.webapp.datatables;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Representació de una cerca ServerSide de Datatables.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ServerSideSearch {

	private String value;
	private boolean regex;

	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public boolean isRegex() {
		return regex;
	}
	public void setRegex(boolean regex) {
		this.regex = regex;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
