/**
 * 
 */
package es.caib.pinbal.client.recobriment.model;

/**
 * Informació d'una transmissió d'una resposta SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ScspTransmisionDatos {

	private String id;
	private ScspDatosGenericos datosGenericos;
	private String datosEspecificos;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public ScspDatosGenericos getDatosGenericos() {
		return datosGenericos;
	}
	
	public void setDatosGenericos(ScspDatosGenericos datosGenericos) {
		this.datosGenericos = datosGenericos;
	}
	
	public String getDatosEspecificos() {
		return datosEspecificos;
	}
	
	public void setDatosEspecificos(String datosEspecificos) {
		this.datosEspecificos = datosEspecificos;
	}

}
