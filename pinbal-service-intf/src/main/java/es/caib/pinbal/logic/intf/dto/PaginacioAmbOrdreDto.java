/**
 * 
 */
package es.caib.pinbal.logic.intf.dto;

import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Informació per a paginar i ordenar els resultats de les consultes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@ToString
public class PaginacioAmbOrdreDto implements Serializable {

	private int paginaNum;
	private int paginaTamany;
	private String filtre;
	private List<FiltreDto> filtres = new ArrayList<FiltreDto>();
	private List<OrdreDto> ordres = new ArrayList<OrdreDto>();

	public void afegirFiltre(
			String camp,
			String valor) {
		getFiltres().add(
				new FiltreDto(
						camp,
						valor));
	}
	
	public void afegirOrdre(
			String camp,
			OrdreDireccioDto direccio) {
		getOrdres().add(
				new OrdreDto(
						camp,
						direccio));
	}


	@Getter
	@Setter
	@AllArgsConstructor
	@NoArgsConstructor
	public class FiltreDto implements Serializable {
		private String camp;
		private String valor;

		private static final long serialVersionUID = -139254994389509932L;
	}
	
	public enum OrdreDireccioDto {
		ASCENDENT,
		DESCENDENT
	}
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public class OrdreDto implements Serializable {
		private String camp;
		private OrdreDireccioDto direccio;

		private static final long serialVersionUID = -139254994389509932L;
	}

	private static final long serialVersionUID = -139254994389509932L;

}
