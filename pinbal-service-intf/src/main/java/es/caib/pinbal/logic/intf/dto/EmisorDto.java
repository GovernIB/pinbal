/**
 * 
 */
package es.caib.pinbal.logic.intf.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * Informació d'un EmisorCertificado SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EmisorDto implements Serializable {

	private Long id;
	private String nom;
	private String cif;

	private static final long serialVersionUID = -8620175604318725073L;

}
