package es.caib.pinbal.logic.intf.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * Objecte DTO amb informació d'una parella procediment-servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class ProcedimentServeiDto implements Serializable {

	@ToString.Exclude
	private ProcedimentDto procediment;
	@ToString.Exclude
	private ServeiDto servei;

	@ToString.Include(name = "procediment.id")
	private Long procedimentId() {
		return procediment != null ? procediment.getId() : null;
	}

	@ToString.Include(name = "servei.id")
	private Long serveiId() {
		return servei != null ? servei.getId() : null;
	}

	private static final long serialVersionUID = 3986823331500016935L;

}
