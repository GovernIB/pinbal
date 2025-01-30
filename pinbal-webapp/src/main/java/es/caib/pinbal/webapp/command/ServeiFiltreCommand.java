package es.caib.pinbal.webapp.command;

import lombok.Getter;
import lombok.Setter;

/**
 * Command per a filtrar els serveis
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class ServeiFiltreCommand {
	private String codi;
	private String descripcio;
	private String emissor;
	private Boolean actiu;
	private String scspVersionEsquema;

	// Elimina els espais en els camps de cerca
	public void eliminarEspaisCampsCerca() {
		this.codi = eliminarEspais(this.codi);
		this.descripcio = eliminarEspais(this.descripcio);
		this.scspVersionEsquema = eliminarEspais(this.scspVersionEsquema);
	}

	private String eliminarEspais(String str) {
		return (str != null) ? str.trim() : null;
	}

}
