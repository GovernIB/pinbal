package es.caib.pinbal.webapp.helper;

import java.util.ArrayList;
import java.util.List;



/**
 * Utilitat per a facilitar la generació de options dels camps
 * select a les pàgines JSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class HtmlSelectOptionHelper {

	public static List<HtmlOption> getOptionsForEnum(
			Class<?> enumeracio) {
		return getOptionsForEnum(enumeracio, null);
	}
	public static List<HtmlOption> getOptionsForEnum(
			Class<?> enumeracio,
			String textKeyPrefix) {
		List<HtmlOption> resposta = new ArrayList<HtmlOption>();
		if (enumeracio.isEnum()) {
			for (Object e: enumeracio.getEnumConstants()) {
				resposta.add(new HtmlOption(
						((Enum<?>)e).name(),
						(textKeyPrefix != null) ? textKeyPrefix + ((Enum<?>)e).name() : ((Enum<?>)e).name()));
			}
		}
		
		return resposta;
	}
	public static List<HtmlOption> getOptionsForArray(
			String[] values,
			String[] texts) {
		List<HtmlOption> resposta = new ArrayList<HtmlOption>();
		for (int i = 0; i < values.length; i++) {
			resposta.add(new HtmlOption(values[i], texts[i]));
		}
		return resposta;
	}

	public static class HtmlOption {
		private String value;
		private String text;
		public HtmlOption(String value, String text) {
			this.value = value;
			this.text = text;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
	}

}
