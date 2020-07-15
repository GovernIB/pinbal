package es.caib.pinbal.webapp.command;


/**
 * Command per a filtrar els serveis
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ServeiFiltreCommand {

	private String codi;
	private String descripcio;
	private String emissor;
	private Boolean activa;
	
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getDescripcio() {
		return descripcio;
	}
	public void setDescripcio(String descripcio) {
		this.descripcio = descripcio;
	}
	public String getEmissor() {
		return emissor;
	}
	public void setEmissor(String emissor) {
		this.emissor = emissor;
	}
	public Boolean getActiva() {
		return activa;
	}
	public void setActiva(Boolean activa) {
		this.activa = activa;
	}
	
}
