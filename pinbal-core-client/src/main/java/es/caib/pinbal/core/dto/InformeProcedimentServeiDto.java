package es.caib.pinbal.core.dto;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Objecte DTO amb informació d'un procediment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class InformeProcedimentServeiDto implements Serializable {

	private String entitatCodi;
	private String entitatNom;
	private String entitatCif;
	private String organGestorCodi;
	private String organGestorNom;
	private String procedimentCodi;
	private String procedimentNom;
	private String serveiCodi;
	private String serveiNom;
	private EmisorDto serveiEmisor;
	private String usuariCodi;
	private String usuariNom;
	private String usuariNif;

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
	public String getServeiNom() {
		return serveiNom;
	}
	public void setServeiNom(String serveiNom) {
		this.serveiNom = serveiNom;
	}
	public EmisorDto getServeiEmisor() {
		return serveiEmisor;
	}
	public void setServeiEmisor(EmisorDto serveiEmisor) {
		this.serveiEmisor = serveiEmisor;
	}
	public String getOrganGestorCodi() {
		return organGestorCodi;
	}
	public void setOrganGestorCodi(String organGestorCodi) {
		this.organGestorCodi = organGestorCodi;
	}
	public String getOrganGestorNom() {
		return organGestorNom;
	}
	public void setOrganGestorNom(String organGestorNom) {
		this.organGestorNom = organGestorNom;
	}
	public String getUsuariCodi() {
		return usuariCodi;
	}
	public void setUsuariCodi(String usuariCodi) {
		this.usuariCodi = usuariCodi;
	}
	public String getUsuariNom() {
		return usuariNom;
	}
	public void setUsuariNom(String usuariNom) {
		this.usuariNom = usuariNom;
	}
	public String getUsuariNif() {
		return usuariNif;
	}
	public void setUsuariNif(String usuariNif) {
		this.usuariNif = usuariNif;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = 3986823331500016935L;

}
