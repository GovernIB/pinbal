/**
 * 
 */
package es.caib.pinbal.core.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * Informació d'un fitxer.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class FitxerDto implements Serializable {

	private String nom;
	private String contentType;
	private byte[] contingut;

	public long getLongitud() {
		return (contingut != null) ? contingut.length : 0;
	}

	public String getExtensio() {
		int indexPunt = nom.lastIndexOf(".");
		if (indexPunt != -1 && indexPunt < nom.length() - 1) {
			return nom.substring(indexPunt + 1);
		} else {
			return null;
		}
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}