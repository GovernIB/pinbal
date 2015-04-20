/**
 * 
 */
package es.caib.pinbal.core.dto;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Dades per mostrar l'estadística de consultes realitzades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EstadisticaDto implements Serializable {

	private ProcedimentDto procediment;
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

	public ProcedimentDto getProcediment() {
		return procediment;
	}
	public void setProcediment(ProcedimentDto procediment) {
		this.procediment = procediment;
	}
	public String getServeiCodi() {
		return serveiCodi;
	}
	public void setServeiCodi(String serveiCodi) {
		this.serveiCodi = serveiCodi;
	}
	public String getServeiNom() {
		return serveiNom;
	}
	public void setServeiNom(String serveiNom) {
		this.serveiNom = serveiNom;
	}
	public long getNumRecobrimentOk() {
		return numRecobrimentOk;
	}
	public void setNumRecobrimentOk(long numRecobrimentOk) {
		this.numRecobrimentOk = numRecobrimentOk;
	}
	public long getNumRecobrimentError() {
		return numRecobrimentError;
	}
	public void setNumRecobrimentError(long numRecobrimentError) {
		this.numRecobrimentError = numRecobrimentError;
	}
	public long getNumWebUIOk() {
		return numWebUIOk;
	}
	public void setNumWebUIOk(long numWebUIOk) {
		this.numWebUIOk = numWebUIOk;
	}
	public long getNumWebUIError() {
		return numWebUIError;
	}
	public void setNumWebUIError(long numWebUIError) {
		this.numWebUIError = numWebUIError;
	}
	public boolean isConteSumatori() {
		return conteSumatori;
	}
	public void setConteSumatori(boolean conteSumatori) {
		this.conteSumatori = conteSumatori;
	}
	public long getSumatoriNumRegistres() {
		return sumatoriNumRegistres;
	}
	public void setSumatoriNumRegistres(long sumatoriNumRegistres) {
		this.sumatoriNumRegistres = sumatoriNumRegistres;
	}
	public long getSumatoriNumRecobrimentOk() {
		return sumatoriNumRecobrimentOk;
	}
	public void setSumatoriNumRecobrimentOk(long sumatoriNumRecobrimentOk) {
		this.sumatoriNumRecobrimentOk = sumatoriNumRecobrimentOk;
	}
	public long getSumatoriNumRecobrimentError() {
		return sumatoriNumRecobrimentError;
	}
	public void setSumatoriNumRecobrimentError(long sumatoriNumRecobrimentError) {
		this.sumatoriNumRecobrimentError = sumatoriNumRecobrimentError;
	}
	public long getSumatoriNumWebUIOk() {
		return sumatoriNumWebUIOk;
	}
	public void setSumatoriNumWebUIOk(long sumatoriNumWebUIOk) {
		this.sumatoriNumWebUIOk = sumatoriNumWebUIOk;
	}
	public long getSumatoriNumWebUIError() {
		return sumatoriNumWebUIError;
	}
	public void setSumatoriNumWebUIError(long sumatoriNumWebUIError) {
		this.sumatoriNumWebUIError = sumatoriNumWebUIError;
	}
	public long getNumTotal() {
		return getNumRecobrimentOk() + getNumRecobrimentError() + getNumWebUIOk() + getNumWebUIError();
	}
	public long getSumatoriNumTotal() {
		return getSumatoriNumRecobrimentOk() + getSumatoriNumRecobrimentError() + getSumatoriNumWebUIOk() + getSumatoriNumWebUIError();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	private static final long serialVersionUID = -139254994389509932L;

}
