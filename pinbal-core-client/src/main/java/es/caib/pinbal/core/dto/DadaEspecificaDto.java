/**
 * 
 */
package es.caib.pinbal.core.dto;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Dades d'un node de dades específiques.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DadaEspecificaDto implements Serializable {

	public static final String SEPARADOR_PATH_DEFAULT = "/";
	public static final String SEPARADOR_PATH_ALTERNATIU = "-";

	private String[] path;
	private String nom;
	private String[] enumeracioValors;
	private boolean complexa;

	public String[] getPath() {
		return path;
	}
	public void setPath(String[] path) {
		this.path = path;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public boolean isComplexa() {
		return complexa;
	}
	public void setComplexa(boolean complexa) {
		this.complexa = complexa;
	}
	public String[] getEnumeracioValors() {
		return enumeracioValors;
	}
	public void setEnumeracioValors(String[] enumeracioValors) {
		this.enumeracioValors = enumeracioValors;
	}

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
