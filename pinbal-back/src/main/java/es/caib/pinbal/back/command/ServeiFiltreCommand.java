package es.caib.pinbal.back.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Command per a filtrar els serveis
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
