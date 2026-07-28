package es.caib.pinbal.scsp.tree;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class NodeTest {

    @Test
    public void constructorBuitDeixaDadesNulesISenseFills() {
        Node<String> node = new Node<>();
        assertEquals(null, node.getData());
        assertEquals(0, node.getNumberOfChildren());
        assertNotNull(node.getChildren());
        assertTrue(node.getChildren().isEmpty());
    }

    @Test
    public void constructorAmbDadesLesEstableix() {
        Node<String> node = new Node<>("valor");
        assertEquals("valor", node.getData());
    }

    @Test
    public void setDataIGetDataFuncionen() {
        Node<String> node = new Node<>();
        node.setData("nou-valor");
        assertEquals("nou-valor", node.getData());
    }

    @Test
    public void addChildAfegeixFillsIActualitzaElRecompte() {
        Node<String> pare = new Node<>("pare");
        Node<String> fill1 = new Node<>("fill1");
        Node<String> fill2 = new Node<>("fill2");

        pare.addChild(fill1);
        pare.addChild(fill2);

        assertEquals(2, pare.getNumberOfChildren());
        assertEquals(fill1, pare.getChildren().get(0));
        assertEquals(fill2, pare.getChildren().get(1));
    }

    @Test
    public void setChildrenSubstitueixLaLlista() {
        Node<String> pare = new Node<>("pare");
        java.util.List<Node<String>> fills = new java.util.ArrayList<>();
        fills.add(new Node<>("a"));
        pare.setChildren(fills);

        assertEquals(1, pare.getNumberOfChildren());
    }

    @Test
    public void insertChildAtEnLaPosicioFinalEsComportaComAddChild() {
        Node<String> pare = new Node<>("pare");
        Node<String> fill1 = new Node<>("fill1");
        pare.addChild(fill1);

        Node<String> fill2 = new Node<>("fill2");
        pare.insertChildAt(1, fill2);

        assertEquals(2, pare.getNumberOfChildren());
        assertEquals(fill2, pare.getChildren().get(1));
    }

    @Test
    public void insertChildAtEnUnaPosicioIntermediaDesplacaElsSeguents() {
        Node<String> pare = new Node<>("pare");
        Node<String> fillA = new Node<>("A");
        Node<String> fillB = new Node<>("B");
        pare.addChild(fillA);
        pare.addChild(fillB);

        Node<String> fillC = new Node<>("C");
        pare.insertChildAt(1, fillC);

        assertEquals(3, pare.getNumberOfChildren());
        assertEquals(fillA, pare.getChildren().get(0));
        assertEquals(fillC, pare.getChildren().get(1));
        assertEquals(fillB, pare.getChildren().get(2));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void insertChildAtAmbIndexInvalidLlancaExcepcio() {
        Node<String> pare = new Node<>("pare");
        pare.addChild(new Node<>("existent"));
        pare.insertChildAt(5, new Node<>("x"));
    }

    @Test
    public void removeChildAtEliminaElFillIndicat() {
        Node<String> pare = new Node<>("pare");
        Node<String> fillA = new Node<>("A");
        Node<String> fillB = new Node<>("B");
        pare.addChild(fillA);
        pare.addChild(fillB);

        pare.removeChildAt(0);

        assertEquals(1, pare.getNumberOfChildren());
        assertEquals(fillB, pare.getChildren().get(0));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void removeChildAtAmbIndexInvalidLlancaExcepcio() {
        Node<String> pare = new Node<>("pare");
        pare.addChild(new Node<>("existent"));
        pare.removeChildAt(5);
    }

    @Test
    public void toStringInclouLesDadesIElsFills() {
        Node<String> pare = new Node<>("pare");
        pare.addChild(new Node<>("fill1"));
        pare.addChild(new Node<>("fill2"));

        String resultat = pare.toString();

        assertEquals("{pare,[fill1,fill2]}", resultat);
    }

    @Test
    public void toStringSenseFillsNomesMostraLesDades() {
        Node<String> node = new Node<>("sol");
        assertEquals("{sol,[]}", node.toString());
    }
}
