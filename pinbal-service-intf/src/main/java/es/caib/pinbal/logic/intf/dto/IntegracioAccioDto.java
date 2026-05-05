/**
 * 
 */
package es.caib.pinbal.logic.intf.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Acció realitzada sobre una integració.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
public class IntegracioAccioDto extends AbstractIdentificable<Long> implements Serializable {

	private Long id;
	private String idPeticio;	
	private Date data;
	private String descripcio;
	private List<IntegracioAccioParamDto> parametres = new ArrayList<IntegracioAccioParamDto>();
	private String codi;
	private IntegracioAccioTipusEnumDto tipus;
	private long tempsResposta;
	private IntegracioAccioEstatEnumDto estat;
	private EntitatDto entitat;
	private String errorDescripcio;
	private String excepcioMessage;
	private String excepcioStacktrace;

	public boolean isEstatOk(){
		return estat == IntegracioAccioEstatEnumDto.OK;
	}

	public int getParametresCount() {
		if (parametres == null) {
			return 0;
		} else {
			return parametres.size();
		}
	}

	private static final long serialVersionUID = -139254994389509932L;

}
