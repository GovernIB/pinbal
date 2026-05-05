package es.caib.pinbal.logic.intf.base.util;

/**
 * Utilitats per strings.
 * 
 * @author Límit Tecnologies
 */
public class StringUtil {

	public static String capitalize(String str) {
		return Character.toUpperCase(str.charAt(0)) + str.substring(1);
	}

	public static String decapitalize(String str) {
		return Character.toLowerCase(str.charAt(0)) + str.substring(1);
	}

	public static String removeLeadingAndTrailingChars(String str, Integer numChars) {
		if (str.length() > 2 * numChars) {
			return str.substring(numChars, str.length() - numChars);
		} else {
			return "";
		}
	}

}
