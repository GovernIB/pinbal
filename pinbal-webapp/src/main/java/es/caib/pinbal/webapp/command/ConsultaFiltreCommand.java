/**
 * 
 */
package es.caib.pinbal.webapp.command;

import java.util.Date;

import javax.validation.constraints.Size;

import org.apache.commons.lang.builder.ToStringBuilder;

import es.caib.pinbal.core.dto.ConsultaDto.EstatTipus;
import es.caib.pinbal.core.dto.ConsultaFiltreDto;

/**
 * Command per a filtrar les consultes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ConsultaFiltreCommand {

	private Long procediment;
	@Size(max=64)
	private String servei;
	private EstatTipus estat;
	private Date dataInici;
	private Date dataFi;
	private String titularNom;
	private String titularDocument;
	private String funcionariNom;
	private String funcionariDocument;

	public Long getProcediment() {
		return procediment;
	}
	public void setProcediment(Long procediment) {
		this.procediment = procediment;
	}
	public String getServei() {
		return servei;
	}
	public void setServei(String servei) {
		this.servei = servei;
	}
	public EstatTipus getEstat() {
		return estat;
	}
	public void setEstat(EstatTipus estat) {
		this.estat = estat;
	}
	public Date getDataInici() {
		return dataInici;
	}
	public void setDataInici(Date dataInici) {
		this.dataInici = dataInici;
	}
	public Date getDataFi() {
		return dataFi;
	}
	public void setDataFi(Date dataFi) {
		this.dataFi = dataFi;
	}
	public String getTitularNom() {
		return titularNom;
	}
	public void setTitularNom(String titularNom) {
		this.titularNom = titularNom;
	}
	public String getTitularDocument() {
		return titularDocument;
	}
	public void setTitularDocument(String titularDocument) {
		this.titularDocument = titularDocument;
	}
	public String getFuncionariNom() {
		return funcionariNom;
	}
	public void setFuncionariNom(String funcionariNom) {
		this.funcionariNom = funcionariNom;
	}
	public String getFuncionariDocument() {
		return funcionariDocument;
	}
	public void setFuncionariDocument(String funcionariDocument) {
		this.funcionariDocument = funcionariDocument;
	}

	public static ConsultaFiltreDto asDto(ConsultaFiltreCommand command) {
		if (command == null)
			return null;
		ConsultaFiltreDto dto = CommandMappingHelper.getMapperFacade().map(
				command,
				ConsultaFiltreDto.class);
		dto.setProcedimentId(command.getProcediment());
		dto.setServeiCodi(command.getServei());
		return dto;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
