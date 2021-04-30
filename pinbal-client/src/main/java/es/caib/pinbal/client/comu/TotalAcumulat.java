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

	private long any;
	private long mes;
	private long dia;
	private long hora;
	private long minut;
	
	public TotalAcumulat(long any, long mes, long dia, long hora, long minut) {
		super();
		this.any = any;
		this.mes = mes;
		this.dia = dia;
		this.hora = hora;
		this.minut = minut;
	}

	public long getAny() {
		return any;
	}
	public void setAny(long any) {
		this.any = any;
	}
	public long getMes() {
		return mes;
	}
	public void setMes(long mes) {
		this.mes = mes;
	}
	public long getDia() {
		return dia;
	}
	public void setDia(long dia) {
		this.dia = dia;
	}
	public long getHora() {
		return hora;
	}
	public void setHora(long hora) {
		this.hora = hora;
	}
	public long getMinut() {
		return minut;
	}
	public void setMinut(long minut) {
		this.minut = minut;
	}

}
