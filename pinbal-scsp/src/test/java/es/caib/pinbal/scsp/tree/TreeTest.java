package es.caib.pinbal.scsp.tree;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TreeTest {

    @Test
    public void arbreBuitToListRetornaListaBuida() {
        Tree<String> tree = new Tree<>();
        assertNull(tree.getRootElement());
        assertTrue(tree.toList().isEmpty());
    }

    @Test
    public void setRootElementIGetRootElementFuncionen() {
        Tree<String> tree = new Tree<>();
        Node<String> arrel = new Node<>("arrel");
        tree.setRootElement(arrel);
        assertEquals(arrel, tree.getRootElement());
    }

    @Test
    public void toListRecorreEnPreordre() {
        Tree<String> tree = new Tree<>();
        Node<String> arrel = new Node<>("arrel");
        Node<String> filll1 = new Node<>("fill1");
        Node<String> fill2 = new Node<>("fill2");
        Node<String> net = new Node<>("net");
        filll1.addChild(net);
        arrel.addChild(filll1);
        arrel.addChild(fill2);
        tree.setRootElement(arrel);

        List<Node<String>> resultat = tree.toList();

        assertEquals(4, resultat.size());
        assertEquals("arrel", resultat.get(0).getData());
        assertEquals("fill1", resultat.get(1).getData());
        assertEquals("net", resultat.get(2).getData());
        assertEquals("fill2", resultat.get(3).getData());
    }

    @Test
    public void toStringDelegaEnToList() {
        Tree<String> tree = new Tree<>();
        tree.setRootElement(new Node<>("unic"));

        assertEquals(tree.toList().toString(), tree.toString());
    }
}
