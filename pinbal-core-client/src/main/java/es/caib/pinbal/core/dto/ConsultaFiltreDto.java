package es.caib.pinbal.core.dto;

import es.caib.pinbal.core.dto.ConsultaDto.EstatTipus;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Date;

/**
 * Objecte DTO amb informació per filtrar el llistat de consultes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class ConsultaFiltreDto implements Serializable {

	private String scspPeticionId;
	private Long procedimentId;
	private String serveiCodi;
	private EstatTipus estat;
	private Date dataInici;
	private Date dataFi;
	private String titularNom;
	private String titularDocument;
	private String funcionari;
	private String usuari;
	private Long entitatId;
	private Boolean recobriment;
	private Boolean multiple;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -2822106398117415005L;

}
