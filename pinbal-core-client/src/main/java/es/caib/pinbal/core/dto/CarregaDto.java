/**
 * 
 */
package es.caib.pinbal.core.dto;

import java.io.Serializable;

/**
 * Informació per a l'estadística de càrrega de consultes de l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
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

	public long getCountWeb() {
		return countWeb;
	}
	public void setCountWeb(long countWeb) {
		this.countWeb = countWeb;
	}
	public long getCountRecobriment() {
		return countRecobriment;
	}
	public void setCountRecobriment(long countRecobriment) {
		this.countRecobriment = countRecobriment;
	}
	public Long getEntitatId() {
		return entitatId;
	}
	public void setEntitatId(Long entitatId) {
		this.entitatId = entitatId;
	}
	public String getEntitatCodi() {
		return entitatCodi;
	}
	public void setEntitatCodi(String entitatCodi) {
		this.entitatCodi = entitatCodi;
	}
	public String getEntitatNom() {
		return entitatNom;
	}
	public void setEntitatNom(String entitatNom) {
		this.entitatNom = entitatNom;
	}
	public String getEntitatCif() {
		return entitatCif;
	}
	public void setEntitatCif(String entitatCif) {
		this.entitatCif = entitatCif;
	}
	public String getDepartamentNom() {
		return departamentNom;
	}
	public void setDepartamentNom(String departamentNom) {
		this.departamentNom = departamentNom;
	}
	public Long getProcedimentServeiId() {
		return procedimentServeiId;
	}
	public void setProcedimentServeiId(Long procedimentServeiId) {
		this.procedimentServeiId = procedimentServeiId;
	}
	public String getProcedimentCodi() {
		return procedimentCodi;
	}
	public void setProcedimentCodi(String procedimentCodi) {
		this.procedimentCodi = procedimentCodi;
	}
	public String getProcedimentNom() {
		return procedimentNom;
	}
	public void setProcedimentNom(String procedimentNom) {
		this.procedimentNom = procedimentNom;
	}
	public String getServeiCodi() {
		return serveiCodi;
	}
	public void setServeiCodi(String serveiCodi) {
		this.serveiCodi = serveiCodi;
	}
	public String getServeiDescripcio() {
		return serveiDescripcio;
	}
	public void setServeiDescripcio(String serveiDescripcio) {
		this.serveiDescripcio = serveiDescripcio;
	}
	public CarregaDetailedCountDto getDetailedWebCount() {
		return detailedWebCount;
	}
	public void setDetailedWebCount(CarregaDetailedCountDto detailedWebCount) {
		this.detailedWebCount = detailedWebCount;
	}
	public CarregaDetailedCountDto getDetailedRecobrimentCount() {
		return detailedRecobrimentCount;
	}
	public void setDetailedRecobrimentCount(CarregaDetailedCountDto detailedRecobrimentCount) {
		this.detailedRecobrimentCount = detailedRecobrimentCount;
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

	public static class CarregaDetailedCountDto {
		private long any;
		private long mes;
		private long dia;
		private long hora;
		private long minut;
		public CarregaDetailedCountDto(long any, long mes, long dia, long hora, long minut) {
			super();
			this.any = any;
			this.mes = mes;
			this.dia = dia;
			this.hora = hora;
			this.minut = minut;
		}
		public long getAny() {
			return any;
		}
		public void setAny(long any) {
			this.any = any;
		}
		public long getMes() {
			return mes;
		}
		public void setMes(long mes) {
			this.mes = mes;
		}
		public long getDia() {
			return dia;
		}
		public void setDia(long dia) {
			this.dia = dia;
		}
		public long getHora() {
			return hora;
		}
		public void setHora(long hora) {
			this.hora = hora;
		}
		public long getMinut() {
			return minut;
		}
		public void setMinut(long minut) {
			this.minut = minut;
		}
	}

	private static final long serialVersionUID = -139254994389509932L;

}
