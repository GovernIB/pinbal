package es.caib.pinbal.logic.intf.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * Objecte DTO amb informació d'un procediment.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@ToString
public class InformeGeneralEstatDto implements Serializable {

	private String entitatCodi;
	private String entitatNom;
	private String entitatCif;
	private String departament;
	private String procedimentCodi;
	private String procedimentNom;
	private String serveiCodi;
	private String serveiNom;
	private EmisorDto serveiEmisor;
	private Integer serveiUsuaris;
	private Integer peticionsCorrectes;
	private Integer peticionsErronees;
	private Integer peticionsPendents;

	private static final long serialVersionUID = 3986823331500016935L;

}
