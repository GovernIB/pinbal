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
public class ProcedimentServeiPermisFiltreCommand {
	private String codi;
	private String nif;
	private String nom;

	// Elimina els espais en els camps de cerca
	public void eliminarEspaisCampsCerca() {
		this.codi = eliminarEspais(this.codi);
		this.nif = eliminarEspais(this.nif);
		this.nom = eliminarEspais(this.nom);
	}

	private String eliminarEspais(String str) {
		return (str != null) ? str.trim() : null;
	}

}
