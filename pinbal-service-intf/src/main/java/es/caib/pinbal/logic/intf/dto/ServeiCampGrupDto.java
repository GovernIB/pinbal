/**
 * 
 */
package es.caib.pinbal.logic.intf.dto;

import es.caib.pinbal.logic.intf.helper.ToStringHelper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * Dades d'un grup de camps per al formulari d'un servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Builder
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString
public class ServeiCampGrupDto implements Serializable {

	private Long id;
	private String servei;
	private String nom;
	private Long pareId;
	@ToString.Exclude
	private String ajuda;
	private int ordre;
	@ToString.Exclude
	private List<ServeiCampGrupDto> fills;

	@Builder.Default
	private boolean grupRegla = false;

	public String getAjudaHtml() {
		String ajudaHtml = ajuda;
		if (ajudaHtml != null) {
			ajudaHtml = ajudaHtml
					.replace("\n", "<br/>")
					.replace(" ", "&nbsp;")
					.replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
		}
		return ajudaHtml;
	}

	public String getAjudaScaped() {
		String ajudaScaped = ajuda;
		if (ajudaScaped != null) {
			ajudaScaped = ajudaScaped
					.replace("'", "&apos;")
					.replace("\"", "&quot;");
		}
		return ajudaScaped;
	}

	@ToString.Include(name = "ajuda.length")
	private Integer ajudaLongitud() {
		return ToStringHelper.longitud(ajuda);
	}

	@ToString.Include(name = "fills.size")
	private Integer fillsMida() {
		return ToStringHelper.mida(fills);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
