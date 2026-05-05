package es.caib.pinbal.logic.intf.dto;

import lombok.*;

import java.io.Serializable;

/**
 * Objecte DTO amb informació d'un servei SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
public class ParamConfDto extends AbstractIdentificable<Long> implements Serializable {

	private String nom;
	private String valor;
	private String descripcio;

	private static final long serialVersionUID = -5757581909233321212L;

}
