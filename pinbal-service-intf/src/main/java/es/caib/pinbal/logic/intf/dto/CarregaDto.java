/**
 * 
 */
package es.caib.pinbal.logic.intf.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Informació per a l'estadística de càrrega de consultes de l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class CarregaDto implements Serializable {

	private long countWeb;
	private long countRecobriment;
	private Long entitatId;
	private String entitatCodi;
	private String entitatNom;
	private String entitatCif;
	private String departamentNom;
	private Long procedimentServeiId;
	private String procedimentCodi;
	private String procedimentNom;
	private String serveiCodi;
	private String serveiDescripcio;
	private CarregaDetailedCountDto detailedWebCount;
	private CarregaDetailedCountDto detailedRecobrimentCount;

	public CarregaDto(
			long countWeb,
			long countRecobriment,
			Long entitatId,
			String entitatCodi,
			String entitatNom,
			String entitatCif,
			String departamentNom,
			Long procedimentServeiId,
			String procedimentCodi,
			String procedimentNom,
			String serveiCodi,
			String serveiDescripcio) {
		super();
		this.countWeb = countWeb;
		this.countRecobriment = countRecobriment;
		this.entitatId = entitatId;
		this.entitatCodi = entitatCodi;
		this.entitatNom = entitatNom;
		this.entitatCif = entitatCif;
		this.departamentNom = departamentNom;
		this.procedimentServeiId = procedimentServeiId;
		this.procedimentCodi = procedimentCodi;
		this.procedimentNom = procedimentNom;
		this.serveiCodi = serveiCodi;
		this.serveiDescripcio = serveiDescripcio;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((entitatId == null) ? 0 : entitatId.hashCode());
		result = prime * result + ((procedimentServeiId == null) ? 0 : procedimentServeiId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CarregaDto other = (CarregaDto)obj;
		if (entitatId == null) {
			if (other.entitatId != null)
				return false;
		} else if (!entitatId.equals(other.entitatId))
			return false;
		if (procedimentServeiId == null) {
			if (other.procedimentServeiId != null)
				return false;
		} else if (!procedimentServeiId.equals(other.procedimentServeiId))
			return false;
		return true;
	}

	@Getter
	@Setter
	@AllArgsConstructor
	public static class CarregaDetailedCountDto {

		private long any;
		private long mes;
		private long dia;
		private long hora;
		private long minut;

	}

	private static final long serialVersionUID = -139254994389509932L;

}
