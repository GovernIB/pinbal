/**
 * 
 */
package es.caib.pinbal.logic.intf.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * Informació d'una ClavePublica SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@ToString(callSuper = true)
public class ClauPublicaDto extends AbstractIdentificable<Long> implements Serializable {

	private Long id;

	private String alies;
	private String nom;
	private String numSerie;
	private Date dataAlta;
	private Date dataBaixa;

	private static final long serialVersionUID = -8620175604318725073L;

}
