/**
 * 
 */
package es.caib.pinbal.webapp.jmesa;

/**
 * Informació d'una columna de la taula jMesa
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class InfoColumna {

	private String camp;
	private String descripcio;

	private boolean esAccio = false;
	private String accioUrl;
	private String accioIcona;
	private String accioDescripcio;
	private String accioCampAfegir;

	public InfoColumna(
			String camp) {
		this.camp = camp;
	}
	public InfoColumna(
			String camp,
			String descripcio) {
		this.camp = camp;
		this.descripcio = descripcio;
	}
	public InfoColumna(
			String accioUrl,
			String accioIcona,
			String accioDescripcio) {
		this.esAccio = true;
		this.accioUrl = accioUrl;
		this.accioIcona = accioIcona;
		this.accioDescripcio = accioDescripcio;
	}
	public InfoColumna(
			String accioUrl,
			String accioIcona,
			String accioDescripcio,
			String accioCampAfegir) {
		this.esAccio = true;
		this.accioUrl = accioUrl;
		this.accioIcona = accioIcona;
		this.accioDescripcio = accioDescripcio;
		this.accioCampAfegir = accioCampAfegir;
	}

	public String getCamp() {
		return camp;
	}
	public void setCamp(String camp) {
		this.camp = camp;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public boolean isEsAccio() {
		return esAccio;
	}
	public void setEsAccio(boolean esAccio) {
		this.esAccio = esAccio;
	}
	public String getAccioUrl() {
		return accioUrl;
	}
	public void setAccioUrl(String accioUrl) {
		this.accioUrl = accioUrl;
	}
	public String getAccioIcona() {
		return accioIcona;
	}
	public void setAccioIcona(String accioIcona) {
		this.accioIcona = accioIcona;
	}
	public String getAccioDescripcio() {
		return accioDescripcio;
	}
	public void setAccioDescripcio(String accioDescripcio) {
		this.accioDescripcio = accioDescripcio;
	}
	public String getAccioCampAfegir() {
		return accioCampAfegir;
	}
	public void setAccioCampAfegir(String accioCampAfegir) {
		this.accioCampAfegir = accioCampAfegir;
	}

}
