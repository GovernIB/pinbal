/**
 * 
 */
package es.caib.pinbal.core.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Acció realitzada sobre una integració.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class IntegracioAccioDto extends AbstractIdentificable<Long> implements Serializable {

	private Long id;
	private String idPeticio;	
	private Date data;
	private String descripcio;

	//	private Map<String, String> parametres;
	private List<IntegracioAccioParamDto> parametres = new ArrayList<IntegracioAccioParamDto>();

	//	private IntegracioDto integracio;
	private String codi;
	
	private IntegracioAccioTipusEnumDto tipus;
	private long tempsResposta;
	private IntegracioAccioEstatEnumDto estat;
	private EntitatDto entitat;
	private String errorDescripcio;
	private String excepcioMessage;
	private String excepcioStacktrace;

	public String getIdPeticio() {
		return idPeticio;
	}

	public void setIdPeticio(String idPeticio) {
		this.idPeticio = idPeticio;
	}

	public String getCodi() {
		return codi;
	}

	public void setCodi(String codi) {
		this.codi = codi;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	
//	public Map<String, String> getParametres() {
//		return parametres;
//	}
//	public void setParametres(Map<String, String> parametres) {
//		this.parametres = parametres;
//	}
	
	public List<IntegracioAccioParamDto> getParametres() {
		return parametres;
	}

	public void setParametres(List<IntegracioAccioParamDto> parametres) {
		this.parametres = parametres;
	}
	
//	public IntegracioDto getIntegracio() {
//		return integracio;
//	}
//	public void setIntegracio(IntegracioDto integracio) {
//		this.integracio = integracio;
//	}
	public IntegracioAccioTipusEnumDto getTipus() {
		return tipus;
	}
	public void setTipus(IntegracioAccioTipusEnumDto tipus) {
		this.tipus = tipus;
	}
	public long getTempsResposta() {
		return tempsResposta;
	}
	public void setTempsResposta(long tempsResposta) {
		this.tempsResposta = tempsResposta;
	}
	public IntegracioAccioEstatEnumDto getEstat() {
		return estat;
	}
	public void setEstat(IntegracioAccioEstatEnumDto estat) {
		this.estat = estat;
	}
	public EntitatDto getEntitat() {
		return entitat;
	}
	public void setEntitat(EntitatDto entitat) {
		this.entitat = entitat;
	}
	public String getErrorDescripcio() {
		return errorDescripcio;
	}
	public void setErrorDescripcio(String errorDescripcio) {
		this.errorDescripcio = errorDescripcio;
	}
	public String getExcepcioMessage() {
		return excepcioMessage;
	}
	public void setExcepcioMessage(String excepcioMessage) {
		this.excepcioMessage = excepcioMessage;
	}
	public String getExcepcioStacktrace() {
		return excepcioStacktrace;
	}
	public void setExcepcioStacktrace(String excepcioStacktrace) {
		this.excepcioStacktrace = excepcioStacktrace;
	}
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
