/**
 * 
 */
package es.caib.pinbal.core.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Informació per a paginar i ordenar els resultats de les consultes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PaginacioAmbOrdreDto implements Serializable {

	private int paginaNum;
	private int paginaTamany;
	private List<OrdreDto> ordres = new ArrayList<OrdreDto>();

	public int getPaginaNum() {
		return paginaNum;
	}
	public void setPaginaNum(int paginaNum) {
		this.paginaNum = paginaNum;
	}
	public int getPaginaTamany() {
		return paginaTamany;
	}
	public void setPaginaTamany(int paginaTamany) {
		this.paginaTamany = paginaTamany;
	}
	public List<OrdreDto> getOrdres() {
		return ordres;
	}
	public void setOrdres(List<OrdreDto> ordres) {
		this.ordres = ordres;
	}

	public void afegirOrdre(OrdreDto ordre) {
		getOrdres().add(ordre);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
