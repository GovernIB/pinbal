/**
 * 
 */
package es.caib.pinbal.logic.intf.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * Informació d'una entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@ToString
public class IntegracioAccioParamDto implements Serializable{

	private Long id;
	private String nom;	
	private String descripcio;

	private static final long serialVersionUID = 9085316830957612966L;
}
