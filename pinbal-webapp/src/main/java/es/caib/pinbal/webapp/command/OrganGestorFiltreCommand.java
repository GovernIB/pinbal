/**
 * 
 */
package es.caib.pinbal.webapp.command;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.pinbal.core.dto.OrganGestorEstatEnumDto;

/**
 * Command per a filtrar els organs gestors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class OrganGestorFiltreCommand {

	private Long entitatId;
	private String codi;
	private String nom;
	private OrganGestorEstatEnumDto estat;

	public OrganGestorFiltreCommand() {
		super();
	}
	public OrganGestorFiltreCommand(Long entitatId) {
		this.entitatId = entitatId;
	}

	public Long getEntitatId() {
		return entitatId;
	}
	public void setEntitatId(Long entitatId) {
		this.entitatId = entitatId;
	}
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public OrganGestorEstatEnumDto getEstat() {
		return estat;
	}
	public void setEstat(OrganGestorEstatEnumDto estat) {
		this.estat = estat;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
