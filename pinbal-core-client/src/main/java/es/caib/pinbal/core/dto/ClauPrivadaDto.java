/**
 * 
 */
package es.caib.pinbal.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Date;

/**
 * Informació d'una ClauPrivada SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClauPrivadaDto extends AbstractIdentificable<Long> implements Serializable {
	
	private Long id;
	private String alies;
	private String nom;
	private String password;
	private String numSerie;
	private Date dataBaixa;
	private Date dataAlta;
	private boolean interoperabilitat;
	private Long organisme;
	

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -8620175604318725073L;

}
