/**
 * 
 */
package es.caib.pinbal.scsp.tree;

import java.util.ArrayList;
import java.util.List;

/**
 * Estructura de dades genèrica amb estructura d'arbre.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class Tree<T> {

	private Node<T> rootElement;

	public Tree() {
		super();
	}

	/**
	 * retorna el Node arrel de l'arbre.
	 * @return el node arrel.
	 */
	public Node<T> getRootElement() {
		return this.rootElement;
	}

	/**
	 * Estableix el Node arrel de l'arbre.
	 * @param rootNode the root element to set.
	 */
	public void setRootElement(Node<T> rootElement) {
		this.rootElement = rootElement;
	}

	/**
	 * Retorna el Tree<T> com una llista de objectes Node<T>. Els elements de la
	 * llista es generen recorreguent l'arbre en l'ordre pre-establert..
	 * @return una llista List<Node<T>>.
	 */
	public List<Node<T>> toList() {
		List<Node<T>> list = new ArrayList<Node<T>>();
		walk(rootElement, list);
		return list;
	}

	/**
	 * Returns a String representation of the Tree. The elements are generated
	 * from a pre-order traversal of the Tree.
	 * @return the String representation of the Tree.
	 */
	public String toString() {
		return toList().toString();
	}

	/**
	 * Walks the Tree in pre-order style. This is a recursive method, and is
	 * called from the toList() method with the root element as the first
	 * argument. It appends to the second argument, which is passed by reference
	 * as it recurses down the tree.
	 * @param element the starting element.
	 * @param list the output of the walk.
	 */
	private void walk(Node<T> element, List<Node<T>> list) {
		list.add(element);
		for (Node<T> data : element.getChildren()) {
			walk(data, list);
		}
	}

}
