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
public class InformeProcedimentDto implements Serializable {

	private Long id;

	private String codi;
	private String nom;
	private String departament;
	private boolean actiu;

	@ToString.Exclude
	private EntitatDto entitat;

	@ToString.Include(name = "entitat.id")
	private Long entitatId() {
		return entitat != null ? entitat.getId() : null;
	}

	private static final long serialVersionUID = 3986823331500016935L;

}
