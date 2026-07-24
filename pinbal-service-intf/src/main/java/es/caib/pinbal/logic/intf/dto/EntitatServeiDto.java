package es.caib.pinbal.logic.intf.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * Objecte DTO amb informació de l'assignació d'un servei SCSP a una entitat.
 *
 * @author Límit Tecnologies
 */
@Getter @Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EntitatServeiDto extends AbstractIdentificable<Long> implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long entitatId;
	private String servei;

}
