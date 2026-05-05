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
	private EntitatDto entitat;	
	
	private static final long serialVersionUID = -248365773192710830L;

}
