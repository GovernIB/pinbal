/**
 * 
 */
package es.caib.pinbal.webapp.command;

import es.caib.pinbal.core.dto.FiltreActiuEnumDto;
import lombok.Data;

/**
 * Command per a filtrar els procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
public class ProcedimentFiltreCommand {

	private String codi;
	private String nom;
	private String departament;
	private Long organGestorId;
	private String codiSia;
	private FiltreActiuEnumDto actiu;

	// Elimina els espais en els camps de cerca
	public void eliminarEspaisCampsCerca() {
		this.codi = eliminarEspais(this.codi);
		this.nom = eliminarEspais(this.nom);
		this.departament = eliminarEspais(this.departament);
		this.codiSia = eliminarEspais(this.codiSia);
	}

	private String eliminarEspais(String str) {
		return (str != null) ? str.trim() : null;
	}

}
