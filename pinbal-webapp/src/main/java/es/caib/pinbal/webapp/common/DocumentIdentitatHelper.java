/**
 * 
 */
package es.caib.pinbal.webapp.common;

import java.util.regex.Pattern;

/**
 * Utilitat per a validar documents d'identitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DocumentIdentitatHelper {

	// Validació del DNI
	private static final Pattern dniPattern = Pattern.compile("[0-9]{8}[A-Z]");
	public static boolean validacioDni(String dni) {
		if (!dniPattern.matcher(dni).matches()) {
			return false;
		}
		String nums = dni.substring(0, 8);
		String lletra = dni.substring(8);
		return lletra.equals(lletraNif(new Integer(nums).intValue()));
	}

	// Validació del NIE
	private static final Pattern niePattern = Pattern.compile("[XYZ][0-9]{7}[A-Z]");
	public static boolean validacioNie(String nie) {
		if (!niePattern.matcher(nie).matches()) {
			return false;
		}
		String nums = nie.substring(1, 8);
		String lletra = nie.substring(8);
		String primeraLletra = nie.substring(0, 1);
		String primeresLletres = "XYZ";
		return lletra.equals(
				lletraNif(
						new Integer(primeresLletres.indexOf(primeraLletra) + nums).intValue()));
	}

	// Validació del CIF
	// Sólo admiten números comocarácter de control
	private static final String CONTROL_SOLO_NUMEROS = "ABEH";
	// Sólo admiten letras como carácter de control
	private static final String CONTROL_SOLO_LETRAS = "KPQS";
	// Conversión de dígito a letra de control
	private static final String CONTROL_NUMERO_A_LETRA = "JABCDEFGHI";
	private static final Pattern cifPattern = Pattern.compile("[[A-H][J-N][P-S]VW][0-9]{7}[0-9A-J]");
	public static boolean validacioCif(String cif) {
		if (!cifPattern.matcher(cif).matches()) {
			return false;
		}
		int digitoD = digitoCif(cif);
		if (digitoD == -1)
			return false;
		final char letraIni = cif.charAt(0);
		final char caracterFin = cif.charAt(8);
		final boolean esControlValido =
				// ¿el carácter de control es válido como letra?
				(CONTROL_SOLO_NUMEROS.indexOf(letraIni) < 0 && CONTROL_NUMERO_A_LETRA.charAt(digitoD) == caracterFin) ||
				// ¿el carácter de control es válido como dígito?
				(CONTROL_SOLO_LETRAS.indexOf(letraIni) < 0 && digitoD == Character.digit(caracterFin, 10));
		return esControlValido;
	}

	// Validació del NIF
	public static boolean validacioNif(String nif) {
		// Un NIF pot ser DNI o NIE. Si no ho és se valida com un CIF
		if (dniPattern.matcher(nif).matches()) {
			return validacioDni(nif);
		} else if (niePattern.matcher(nif).matches()) {
			return validacioNie(nif);
		} else {
			return validacioCif(nif);
		}
	}

	// Validació del passaport
	public static boolean validacioPass(String pass) {
		return true;
	}

	private static final String NIF_STRING_ASOCIATION = "TRWAGMYFPDXBNJZSQVHLCKE";
	private static String lletraNif(int dni) {
		//return String.valueOf(dni) + NIF_STRING_ASOCIATION.charAt(dni % 23);
		return "" + NIF_STRING_ASOCIATION.charAt(dni % 23);
	}
	private static int digitoCif(String cif) {
		int parA = 0;
		for (int i = 2; i < 8; i += 2) {
			final int digito = Character.digit(cif.charAt(i), 10);
			if (digito < 0) {
				return -1;
			}
			parA += digito;
		}
		int nonB = 0;
		for (int i = 1; i < 9; i += 2) {
			final int digito = Character.digit(cif.charAt(i), 10);
			if (digito < 0) {
				return -1;
			}
			int nn = 2 * digito;
			if (nn > 9) {
				nn = 1 + (nn - 10);
			}
			nonB += nn;
		}
		final int parcialC = parA + nonB;
		final int digitoE = parcialC % 10;
		return (digitoE > 0) ? (10 - digitoE) : 0;
	}

}
