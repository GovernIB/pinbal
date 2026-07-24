package es.caib.pinbal.logic.intf.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Paràmetres de l'acció "syncDir3" d'{@link OrganGestorResource}.
 *
 * @author Límit Tecnologies
 */
@Getter
@Setter
@NoArgsConstructor
public class OrganGestorSyncDir3Params implements Serializable {

	@NotNull
	private Long entitatId;

}
