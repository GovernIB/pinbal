package es.caib.pinbal.logic.intf.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * Informació de la sessió d'usuari.
 *
 * @author Límit Tecnologies
 */
@Getter
@AllArgsConstructor
public class UserSession implements Serializable {

	private Long entitatId;

}
