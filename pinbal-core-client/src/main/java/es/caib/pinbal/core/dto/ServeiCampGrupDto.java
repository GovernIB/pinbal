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
import java.util.List;

/**
 * Dades d'un grup de camps per al formulari d'un servei.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Builder
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ServeiCampGrupDto implements Serializable {

	private Long id;
	private String servei;
	private String nom;
	private Long pareId;
	private String ajuda;
	private int ordre;
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

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
