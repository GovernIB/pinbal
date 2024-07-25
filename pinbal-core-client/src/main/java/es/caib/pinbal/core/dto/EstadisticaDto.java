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

/**
 * Dades per mostrar l'estadística de consultes realitzades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
@Builder
@NoArgsConstructor @AllArgsConstructor
public class EstadisticaDto implements Serializable {

	private Long procedimentId;
	private String procedimentNom;
	private String procedimentCodi;
	private String procedimentDepartament;
	private String serveiCodi;
	private String serveiNom;
	private long numRecobrimentOk;
	private long numRecobrimentError;
	private long numWebUIOk;
	private long numWebUIError;
	private boolean conteSumatori;
	private long sumatoriNumRegistres;
	private long sumatoriNumRecobrimentOk;
	private long sumatoriNumRecobrimentError;
	private long sumatoriNumWebUIOk;
	private long sumatoriNumWebUIError;

	public long getNumTotal() {
		return getNumRecobrimentOk() + getNumRecobrimentError() + getNumWebUIOk() + getNumWebUIError();
	}
	public long getSumatoriNumTotal() {
		return getSumatoriNumRecobrimentOk() + getSumatoriNumRecobrimentError() + getSumatoriNumWebUIOk() + getSumatoriNumWebUIError();
	}

	public String getProcedimentNomAmbDepartament() {
		if (procedimentDepartament != null && !procedimentDepartament.isEmpty()) {
			return procedimentNom + " (" + procedimentDepartament + ")";
		} else {
			return procedimentNom;
		}
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
