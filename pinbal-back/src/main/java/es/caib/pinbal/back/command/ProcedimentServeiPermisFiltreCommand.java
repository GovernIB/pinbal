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
