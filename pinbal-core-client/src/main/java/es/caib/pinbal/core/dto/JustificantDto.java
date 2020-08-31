/**
 * 
 */
package es.caib.pinbal.core.dto;

import es.caib.pinbal.core.dto.ConsultaDto.JustificantEstat;

/**
 * Informació d'un fitxer.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@SuppressWarnings("serial")
public class JustificantDto extends FitxerDto {

	private JustificantEstat estat;
	private boolean error;
	private String errorDescripcio;

	public JustificantEstat getEstat() {
		return estat;
	}
	public void setEstat(JustificantEstat estat) {
		this.estat = estat;
	}
	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	public String getErrorDescripcio() {
		return errorDescripcio;
	}
	public void setErrorDescripcio(String errorDescripcio) {
		this.errorDescripcio = errorDescripcio;
	}

}
