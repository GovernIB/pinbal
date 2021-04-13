/**
 * 
 */
package es.caib.pinbal.client.comu;

/**
 * Informació del total acumulat de peticions en una data determinada.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class TotalAcumulat {

	private int any;
	private int mes;
	private int dia;
	private int hora;
	private int minut;

	public int getAny() {
		return any;
	}
	public void setAny(int any) {
		this.any = any;
	}
	public int getMes() {
		return mes;
	}
	public void setMes(int mes) {
		this.mes = mes;
	}
	public int getDia() {
		return dia;
	}
	public void setDia(int dia) {
		this.dia = dia;
	}
	public int getHora() {
		return hora;
	}
	public void setHora(int hora) {
		this.hora = hora;
	}
	public int getMinut() {
		return minut;
	}
	public void setMinut(int minut) {
		this.minut = minut;
	}

}
