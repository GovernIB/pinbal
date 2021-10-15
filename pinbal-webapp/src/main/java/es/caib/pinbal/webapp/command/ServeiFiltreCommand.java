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
	private Boolean activa;
	private String scspVersionEsquema;
}
