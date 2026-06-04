/**
 * 
 */
package es.caib.pinbal.back.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Command per a filtrar les entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntitatFiltreCommand {

	private String codi;
	private String nom;
	
	private String cif;
	private Boolean activa;
	private String tipus;
	private String unitatArrel;
	
	// Elimina els espais en els camps de cerca
	public void eliminarEspaisCampsCerca() {
		this.codi = eliminarEspais(this.codi);
		this.nom = eliminarEspais(this.nom);
		this.cif = eliminarEspais(this.cif);
		this.unitatArrel = eliminarEspais(this.unitatArrel);
	}

	private String eliminarEspais(String str) {
		return (str != null) ? str.trim() : null;
	}

}
