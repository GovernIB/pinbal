package es.caib.pinbal.logic.intf.dto;

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
public class IntegracioFiltreDto implements Serializable {
	
	private String codi;
	private Date data;
	private String descripcio;	
	private String idPeticio;	
	private IntegracioAccioTipusEnumDto tipus;
	private IntegracioAccioEstatEnumDto estat;
	@ToString.Exclude
	private EntitatDto entitat;	
	
	@ToString.Include(name = "entitat.id")
	private Long entitatId() {
		return entitat != null ? entitat.getId() : null;
	}

	private static final long serialVersionUID = -248365773192710830L;

}
