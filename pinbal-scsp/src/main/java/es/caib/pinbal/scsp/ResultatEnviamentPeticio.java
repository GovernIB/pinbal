/**
 * 
 */
package es.caib.pinbal.scsp;


/**
 * Resultat de l'enviament d'una petició SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ResultatEnviamentPeticio {

	protected boolean errorGeneracio;
	protected boolean errorEnviament;
	protected boolean errorRecepcio;
	protected String estatCodi;
	protected String estatDescripcio;

	protected String[] idsSolicituds; 



	public boolean isErrorGeneracio() {
		return errorGeneracio;
	}
	public void setErrorGeneracio(boolean errorGeneracio) {
		this.errorGeneracio = errorGeneracio;
	}
	public boolean isErrorEnviament() {
		return errorEnviament;
	}
	public void setErrorEnviament(boolean errorEnviament) {
		this.errorEnviament = errorEnviament;
	}
	public boolean isErrorRecepcio() {
		return errorRecepcio;
	}
	public void setErrorRecepcio(boolean errorRecepcio) {
		this.errorRecepcio = errorRecepcio;
	}
	public String getEstatCodi() {
		return estatCodi;
	}
	public void setEstatCodi(String estatCodi) {
		this.estatCodi = estatCodi;
	}
	public String getEstatDescripcio() {
		return estatDescripcio;
	}
	public void setEstatDescripcio(String estatDescripcio) {
		this.estatDescripcio = estatDescripcio;
	}
	public String[] getIdsSolicituds() {
		return idsSolicituds;
	}
	public void setIdsSolicituds(String[] idsSolicituds) {
		this.idsSolicituds = idsSolicituds;
	}

	public boolean isError() {
		return errorGeneracio || errorEnviament || errorRecepcio;
	}

	public String getErrorCodi() {
		if (isError())
			return estatCodi;
		else
			return null;
	}
	public String getErrorDescripcio() {
		if (isError())
			return estatDescripcio;
		else
			return null;
	}

}
