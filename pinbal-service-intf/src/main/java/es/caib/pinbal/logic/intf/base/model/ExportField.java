package es.caib.pinbal.logic.intf.base.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Informació d'un camp per a l'exportació.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@AllArgsConstructor
public class ExportField {

	private String name;
	private String label;

}
