/**
 * 
 */
package es.caib.pinbal.logic.intf.dto;

import es.caib.pinbal.logic.intf.helper.ToStringHelper;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Informació d'una ClauPrivada SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@ToString
public class OrganismeCessionariDto implements Serializable {

	private Long id;
	private String nom;
	private String cif;
	private Date dataBaixa;
	private Date dataAlta;
	private Boolean bloquejat;
	@ToString.Exclude
	private byte[] logo;
	@ToString.Exclude
	private List<ClauPrivadaDto> claus;
	
	public String getCadenaIdentificadora() {
		return nom + "(" + cif + ")";
	}

	@ToString.Include(name = "logo.length")
	private Integer logoLongitud() {
		return ToStringHelper.longitud(logo);
	}

	@ToString.Include(name = "claus.size")
	private Integer clausMida() {
		return ToStringHelper.mida(claus);
	}

	private static final long serialVersionUID = -8620175604318725073L;

}
