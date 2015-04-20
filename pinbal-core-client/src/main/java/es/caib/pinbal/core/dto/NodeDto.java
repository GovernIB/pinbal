/**
 * 
 */
package es.caib.pinbal.core.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Node per a l'estructura ArbreDto.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class NodeDto<T> implements Serializable {

	public T dades;
	public List<NodeDto<T>> fills;

	public NodeDto() {
		super();
	}
	public NodeDto(T dades) {
		this();
		setDades(dades);
	}

	/**
     * Retorn els fills del node actual.
     * 
     * @return Els fills del node actual.
     */
	public List<NodeDto<T>> getFills() {
		if (this.fills == null) {
			return new ArrayList<NodeDto<T>>();
		}
		return fills;
	}
	/**
	 * Estableix els fills del node actual.
	 * 
	 * @param fills La llista de fills a establir.
	 */
	public void setFills(List<NodeDto<T>> fills) {
		this.fills = fills;
	}
	/**
	 * Retorna el nombre de fills per al node actual.
	 * 
	 * @return El nombre de fills.
	 */
	public int countFills() {
		if (fills == null)
			return 0;
		return fills.size();
	}
	/**
	 * Afegeix un fill al node actual.
	 * 
	 * @param fill El fill per afegir.
	 */
	public void addFill(NodeDto<T> fill) {
		if (fills == null) {
			fills = new ArrayList<NodeDto<T>>();
		}
		fills.add(fill);
	}
	/**
	 * Insereix un fill a un punt determinat.
	 * 
	 * @param index El punt a on s'afegeix el fill.
	 * @param fill El fill a afegir.
	 * @throws IndexOutOfBoundsException Si el punt excedeix
	 *        el tamany de la llista.
	 */
	public void insertFillAt(int index, NodeDto<T> fill)
			throws IndexOutOfBoundsException {
		if (index == countFills()) {
			// this is really an append
			addFill(fill);
			return;
		} else {
			fills.get(index); // just to throw the exception, and stop here
			fills.add(index, fill);
		}
	}
	/**
	 * Esborra un fill d'un punt determinat.
	 * 
	 * @param index El punt d'on eliminar el fill.
	 * @throws IndexOutOfBoundsException Si el punt excedeix
	 *        el tamany de la llista.
	 */
	public void removeFillAt(int index) throws IndexOutOfBoundsException {
		fills.remove(index);
	}
	/**
	 * Obté les dades associades amb el node.
	 * 
	 * @return Les dades.
	 */
	public T getDades() {
		return this.dades;
	}
	/**
	 * Estableix les dades per al node.
	 * 
	 * @param dades Les dades.
	 */
	public void setDades(T dades) {
		this.dades = dades;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{").append(getDades().toString()).append(",[");
		int i = 0;
		for (NodeDto<T> e : getFills()) {
			if (i > 0) {
				sb.append(",");
			}
			sb.append(e.getDades().toString());
			i++;
		}
		sb.append("]").append("}");
		return sb.toString();
	}

	private static final long serialVersionUID = -139254994389509932L;

}
