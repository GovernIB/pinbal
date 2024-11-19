package es.caib.pinbal.core.dto;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import lombok.Getter;
import lombok.Setter;

/**
 * Objecte DTO amb informació per filtrar el llistat de consultes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class IntegracioFiltreDto implements Serializable {	
	
	private String codi;
	private Date data;
	private String descripcio;	
	private String idPeticio;	
	private IntegracioAccioTipusEnumDto tipus;
	private IntegracioAccioEstatEnumDto estat;
	private EntitatDto entitat;	
	
	public String getCodi() {
		return codi;
	}

	public void setCodi(String codi) {
		this.codi = codi;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public String getDescripcio() {
		return descripcio;
	}

	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}

	public String getIdPeticio() {
		return idPeticio;
	}

	public void setIdPeticio(String idPeticio) {
		this.idPeticio = idPeticio;
	}

	public IntegracioAccioTipusEnumDto getTipus() {
		return tipus;
	}

	public void setTipus(IntegracioAccioTipusEnumDto tipus) {
		this.tipus = tipus;
	}

	public IntegracioAccioEstatEnumDto getEstat() {
		return estat;
	}

	public void setEstat(IntegracioAccioEstatEnumDto estat) {
		this.estat = estat;
	}

	public EntitatDto getEntitat() {
		return entitat;
	}

	public void setEntitat(EntitatDto entitat) {
		this.entitat = entitat;
	}
	

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	
	private static final long serialVersionUID = -248365773192710830L;

}
