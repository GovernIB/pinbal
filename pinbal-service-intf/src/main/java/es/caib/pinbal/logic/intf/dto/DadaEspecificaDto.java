/**
 * 
 */
package es.caib.pinbal.logic.intf.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * Dades d'un node de dades específiques.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
@ToString
public class DadaEspecificaDto implements Serializable {

	public static final String SEPARADOR_PATH_DEFAULT = "/";
	public static final String SEPARADOR_PATH_ALTERNATIU = "-";

	public enum TipusDadaComplexaEnum {
		ALL, CHOICE, SEQUENCE;

		public static TipusDadaComplexaEnum getTipus(Integer ordinal) {
			switch (ordinal) {
				case 0:	return ALL;
				case 1: return CHOICE;
				case 2:	return SEQUENCE;
				default: return null;
			}
		}
	}

	private String[] path;
	private String nom;
	private String[] enumeracioValors;
	private boolean complexa;
	private TipusDadaComplexaEnum tipusDadaComplexa;
    private String tipus;



	public String getPathAmbSeparadorDefault() {
		return getPathAmbSeparador(SEPARADOR_PATH_DEFAULT);
	}
	public String getPathAmbSeparadorAlternatiu() {
		return getPathAmbSeparador(SEPARADOR_PATH_ALTERNATIU);
	}

	public boolean isEnumeracio() {
		return enumeracioValors != null && enumeracioValors.length > 0;
	}

	private String getPathAmbSeparador(String separador) {
		StringBuilder sb = new StringBuilder();
		if (path != null) {
			for (int i = 0; i < path.length; i++) {
				if (path[i].startsWith("__"))
					continue;
				sb.append(path[i]);
				sb.append(separador);
			}
		}
		sb.append(nom);
		return sb.toString();
	}


	private static final long serialVersionUID = -139254994389509932L;

}
