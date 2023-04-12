/**
 * 
 */
package es.caib.pinbal.core.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Dades d'un node de dades específiques.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
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
				sb.append(path[i]);
				sb.append(separador);
			}
		}
		sb.append(nom);
		return sb.toString();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
