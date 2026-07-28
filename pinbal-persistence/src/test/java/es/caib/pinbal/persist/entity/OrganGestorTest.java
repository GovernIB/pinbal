package es.caib.pinbal.persist.entity;

import es.caib.pinbal.logic.intf.dto.OrganGestorEstatEnum;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrganGestorTest {

    private Entitat crearEntitat(String codi) {
        return Entitat.getBuilder(codi, "Entitat " + codi, "Q1234567A", Entitat.EntitatTipus.CONSELL).build();
    }

    @Test
    public void testGettersSetters() {
        Entitat entitat = crearEntitat("ENT1");
        OrganGestor pare = new OrganGestor();
        pare.setCodi("OGPARE");
        List<Procediment> procediments = new ArrayList<>();
        List<OrganGestor> fills = new ArrayList<>();

        OrganGestor organGestor = new OrganGestor();
        organGestor.setCodi("OG1");
        organGestor.setNom("Organ Gestor 1");
        organGestor.setEntitat(entitat);
        organGestor.setPare(pare);
        organGestor.setActiu(true);
        organGestor.setEstat(OrganGestorEstatEnum.V);
        organGestor.setProcediments(procediments);
        organGestor.setFills(fills);

        assertEquals("OG1", organGestor.getCodi());
        assertEquals("Organ Gestor 1", organGestor.getNom());
        assertEquals(entitat, organGestor.getEntitat());
        assertEquals(pare, organGestor.getPare());
        assertTrue(organGestor.isActiu());
        assertEquals(OrganGestorEstatEnum.V, organGestor.getEstat());
        assertEquals(procediments, organGestor.getProcediments());
        assertEquals(fills, organGestor.getFills());
    }

    @Test
    public void testFillIdForTesting() {
        OrganGestor organGestor = new OrganGestor();

        organGestor.fillIdForTesting(5L);

        assertEquals(5L, organGestor.getId());
    }

    @Test
    public void testPreRemove() throws Exception {
        OrganGestor pare = new OrganGestor();
        pare.setCodi("OGPARE");
        OrganGestor fill1 = new OrganGestor();
        fill1.setCodi("OGFILL1");
        fill1.setPare(pare);
        OrganGestor fill2 = new OrganGestor();
        fill2.setCodi("OGFILL2");
        fill2.setPare(pare);
        List<OrganGestor> fills = new ArrayList<>();
        fills.add(fill1);
        fills.add(fill2);
        pare.setFills(fills);

        Method preRemove = OrganGestor.class.getDeclaredMethod("preRemove");
        preRemove.setAccessible(true);
        preRemove.invoke(pare);

        assertNull(fill1.getPare());
        assertNull(fill2.getPare());
    }
}
