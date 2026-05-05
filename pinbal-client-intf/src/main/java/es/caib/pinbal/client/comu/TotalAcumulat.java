/**
 * 
 */
package es.caib.pinbal.client.comu;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Informació del total acumulat de peticions en una data determinada.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Data
@AllArgsConstructor
public class TotalAcumulat {

	private long any;
	private long mes;
	private long dia;
	private long hora;
	private long minut;
	
}
