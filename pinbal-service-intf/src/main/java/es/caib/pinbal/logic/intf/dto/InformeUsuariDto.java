package es.caib.pinbal.logic.intf.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
@ToString
public class InformeUsuariDto implements Serializable {

	private String codi;
	private String nif;
	private String nom;
	private String departament;

	private EntitatDto entitat;

	private static final long serialVersionUID = 3986823331500016935L;

}
