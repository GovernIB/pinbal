/**
 * 
 */
package es.caib.pinbal.logic.intf.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.ToString;

import java.io.Serializable;

/**
 * Informació d'un fitxer.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString
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

	public String getNomSenseExtensio() {
		int indexPunt = nom.lastIndexOf(".");
		if (indexPunt != -1) {
			return nom.substring(0, indexPunt);
		} else {
			return nom;
		}
	}

	private static final long serialVersionUID = -139254994389509932L;

}
