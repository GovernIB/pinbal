/**
 * 
 */
package es.caib.pinbal.logic.intf.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Informació d'una pàgina de resultat de consultes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class PaginaLlistatDto<T> implements Iterable<T>, Serializable {

	private int numero; // De la pàgina actual
	private int tamany; // De la pàgina actual
	private int total; // De pàgines
	private long elementsTotal;
	private boolean anteriors;
	private boolean primera;
	private boolean posteriors;
	private boolean darrera;
	private List<T> contingut = new ArrayList<T>();

	public int getElementsNombre() {
		if (isBuida())
			return 0;
		else
			return contingut.size();
	}
	public boolean isBuida() {
		return contingut == null || contingut.size() == 0;
	}

	@Override
	public Iterator<T> iterator() {
		if (contingut != null)
			return getContingut().iterator();
		else
			return new ArrayList<T>().iterator();
	}

	private static final long serialVersionUID = -139254994389509932L;

}
