/**
 * 
 */
package es.caib.pinbal.client.dadesobertes;

import java.time.LocalDate;

/**
 * Estructura d'un element de la resposta per a les dades obertes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class DadesObertesRespostaConsulta {

	private String entitatCodi;
	private String entitatNom;
	private String entitatNif;
	private String departamentCodi;
	private String departamentNom;
	private String departamentNif;
	private String procedimentCodi;
	private String procedimentNom;
	private String emisor;
	private String serveiCodi;
	private String serveiNom;
	private LocalDate data;
	private String tipus;
	private String resultat;

	public String getEntitatCodi() {
		return entitatCodi;
	}
	public void setEntitatCodi(String entitatCodi) {
		this.entitatCodi = entitatCodi;
	}
	public String getEntitatNom() {
		return entitatNom;
	}
	public void setEntitatNom(String entitatNom) {
		this.entitatNom = entitatNom;
	}
	public String getEntitatNif() {
		return entitatNif;
	}
	public void setEntitatNif(String entitatNif) {
		this.entitatNif = entitatNif;
	}
	public String getDepartamentCodi() {
		return departamentCodi;
	}
	public void setDepartamentCodi(String departamentCodi) {
		this.departamentCodi = departamentCodi;
	}
	public String getDepartamentNom() {
		return departamentNom;
	}
	public void setDepartamentNom(String departamentNom) {
		this.departamentNom = departamentNom;
	}
	public String getDepartamentNif() {
		return departamentNif;
	}
	public void setDepartamentNif(String departamentNif) {
		this.departamentNif = departamentNif;
	}
	public String getProcedimentCodi() {
		return procedimentCodi;
	}
	public void setProcedimentCodi(String procedimentCodi) {
		this.procedimentCodi = procedimentCodi;
	}
	public String getProcedimentNom() {
		return procedimentNom;
	}
	public void setProcedimentNom(String procedimentNom) {
		this.procedimentNom = procedimentNom;
	}
	public String getEmisor() {
		return emisor;
	}
	public void setEmisor(String emisor) {
		this.emisor = emisor;
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
	public LocalDate getData() {
		return data;
	}
	public void setData(LocalDate data) {
		this.data = data;
	}
	public String getTipus() {
		return tipus;
	}
	public void setTipus(String tipus) {
		this.tipus = tipus;
	}
	public String getResultat() {
		return resultat;
	}
	public void setResultat(String resultat) {
		this.resultat = resultat;
	}

}
