package es.caib.pinbal.core.dto;

import java.io.Serializable;

public class ProcedimentServeiSimpleDto implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6085171827817934826L;
	
	private String serveiCodi;
	private String procedimentCodi;
	private boolean actiu;
	
	
	public String getServeiCodi() {
		return serveiCodi;
	}
	public void setServeiCodi(String serveiCodi) {
		this.serveiCodi = serveiCodi;
	}
	public String getProcedimentCodi() {
		return procedimentCodi;
	}
	public void setProcedimentCodi(String procedimentCodi) {
		this.procedimentCodi = procedimentCodi;
	}
	public boolean isActiu() {
		return actiu;
	}
	public void setActiu(boolean actiu) {
		this.actiu = actiu;
	}

}
