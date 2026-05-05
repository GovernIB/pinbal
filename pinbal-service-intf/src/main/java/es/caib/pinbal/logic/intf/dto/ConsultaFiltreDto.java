package es.caib.pinbal.logic.intf.dto;

import es.caib.pinbal.logic.intf.dto.ConsultaDto.EstatTipus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * Objecte DTO amb informació per filtrar el llistat de consultes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
@ToString
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

	private static final long serialVersionUID = -2822106398117415005L;

}
