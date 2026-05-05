package es.caib.pinbal.logic.intf.dto;

import es.caib.pinbal.logic.intf.dto.ConsultaDto.EstatTipus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * Objecte DTO amb informació per filtrar les estadístiques.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@ToString
public class EstadistiquesFiltreDto implements Serializable {

	public enum EstadistiquesAgrupacioDto {
		PROCEDIMENT_SERVEI,
		SERVEI_PROCEDIMENT
	}

	private Long entitatId;
	private Long procedimentId;
	private String serveiCodi;
	private EstatTipus estat;
	private Date dataInici;
	private Date dataFi;
	private String usuariCodi;
	private EstadistiquesAgrupacioDto agrupacio;

	private static final long serialVersionUID = -2822106398117415005L;

}
