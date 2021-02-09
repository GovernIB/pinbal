/**
 * 
 */
package es.caib.pinbal.scsp;

import es.caib.pinbal.scsp.XmlHelper.DadesEspecifiquesNode;
import es.caib.pinbal.scsp.tree.Node;
import es.caib.pinbal.scsp.tree.Tree;
import es.scsp.common.domain.core.Servicio;


/**
 * Test per a obtenir l'esquema de dades específiques.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class XsdParserTest {

	public static void main(String[] args) {
		try {
			new XsdParserTest().test();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void test() throws Exception {
		Servicio servicio = new Servicio();
		servicio.setCodCertificado("SVDSCDWS01");
		servicio.setVersionEsquema("V3");
		Tree<DadesEspecifiquesNode> arbre = new XmlHelper().getArbrePerDadesEspecifiques(servicio, false);
		if (arbre.getRootElement() != null) {
			printNode(arbre.getRootElement(), 0);
		} else {
			System.out.println("Sense dades específiques");
		}
	}

	private void printNode(Node<DadesEspecifiquesNode> node, int nivell) {
		printOutAmbNivell(node.getData().toString(), nivell);
		if (node.getNumberOfChildren() > 0) {
			for (Node<DadesEspecifiquesNode> child: node.getChildren()) {
				printNode(child, nivell + 1);
			}
		}
	}
	private void printOutAmbNivell(String str, int nivell) {
		for (int i = 0; i < nivell; i++)
			System.out.print("\t");
		System.out.println(str);
	}

}
