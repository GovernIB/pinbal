/**
 * 
 */
package es.caib.pinbal.core.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Estructura de dades en forma d'arbre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class ArbreDto<T> implements Serializable {

	private NodeDto<T> arrel;

	public ArbreDto() {
		super();
	}

	/**
	 * Retorna el Node arrel de l'arbre.
	 * 
	 * @return el node arrel.
	 */
	public NodeDto<T> getArrel() {
		return this.arrel;
	}

	/**
	 * Estableix el Node arrel de l'arbre.
	 * 
	 * @param rootNode el node arrel a establir.
	 */
	public void setArrel(NodeDto<T> arrel) {
		this.arrel = arrel;
	}

	/**
	 * Retorna l'arbre com una llista de objectes NodeDto<T>. Els elements de la
	 * llista es generen recorreguent l'arbre en l'ordre pre-establert.
	 * 
	 * @return una llista List<Node<T>>.
	 */
	public List<NodeDto<T>> toList() {
		List<NodeDto<T>> list = new ArrayList<NodeDto<T>>();
		if (arrel != null)
			recorregut(arrel, list);
		return list;
	}

	/**
	 * Retorna una representació textual de l'arbre. Els elements es generen
	 * recorreguent l'arbre en l'odre pre-establert.
	 * 
	 * @return la representació textual de l'arbre.
	 */
	public String toString() {
		return toList().toString();
	}

	private void recorregut(NodeDto<T> element, List<NodeDto<T>> list) {
		list.add(element);
		for (NodeDto<T> data : element.getFills()) {
			recorregut(data, list);
		}
	}

	private static final long serialVersionUID = -139254994389509932L;

}
