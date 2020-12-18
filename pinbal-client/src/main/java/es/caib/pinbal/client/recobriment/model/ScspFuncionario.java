/**
 * 
 */
package es.caib.pinbal.client.recobriment.model;

/**
 * Informació del funcionari d'una sol·licitud SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ScspFuncionario {

	private String nombreCompletoFuncionario;
	private String nifFuncionario;
	private String seudonimo;
	
	public String getNombreCompletoFuncionario() {
		return nombreCompletoFuncionario;
	}
	
	public void setNombreCompletoFuncionario(String nombreCompletoFuncionario) {
		this.nombreCompletoFuncionario = nombreCompletoFuncionario;
	}
	
	public String getNifFuncionario() {
		return nifFuncionario;
	}
	
	public void setNifFuncionario(String nifFuncionario) {
		this.nifFuncionario = nifFuncionario;
	}
	
	public String getSeudonimo() {
		return seudonimo;
	}
	
	public void setSeudonimo(String seudonimo) {
		this.seudonimo = seudonimo;
	}

}
