/**
 * 
 */
package es.caib.pinbal.scsp;


import es.scsp.bean.common.ConfirmacionPeticion;
import lombok.Getter;
import lombok.Setter;

/**
 * Resultat de l'enviament d'una petició SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter
public class ResultatEnviamentPeticio {

	protected boolean errorGeneracio;
	protected boolean errorEnviament;
	protected boolean errorRecepcio;
	protected String estatCodi;
	protected String estatDescripcio;

	protected String[] idsSolicituds;
	protected ConfirmacionPeticion confirmacionPeticion;


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
