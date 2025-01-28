package es.caib.pinbal.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Informació d'un fitxer XSD d'un servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Builder
@Data
@NoArgsConstructor @AllArgsConstructor
public class ServeiXsdDto {

	private String servei;
	private XsdTipusEnumDto tipus;
	private String nomArxiu;
	private String path;
	private Date dataModificacio;

	public String getDataModificacioString() {
		if (dataModificacio == null) return null;

		DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		return sdf.format(dataModificacio);
	}

}
