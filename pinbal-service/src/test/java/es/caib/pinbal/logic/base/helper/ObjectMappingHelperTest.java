package es.caib.pinbal.logic.base.helper;

import es.caib.pinbal.logic.intf.base.exception.ObjectMappingException;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class ObjectMappingHelperTest {

    private final ObjectMappingHelper helper = new ObjectMappingHelper();

    public enum Estat { ACTIU, INACTIU }

    public static class Origen {
        public String nom;
        public Integer edat;
        public Boolean actiu;
        public Date data;
        public Estat estat;
        public String nomesEnOrigen;

        public Origen() {}

        public Origen(String nom, Integer edat, Boolean actiu, Date data, Estat estat) {
            this.nom = nom;
            this.edat = edat;
            this.actiu = actiu;
            this.data = data;
            this.estat = estat;
        }
    }

    public static class Desti {
        private String nom;
        private int edat;
        private boolean actiu;
        private Date data;
        private Estat estat;

        public Desti() {}

        public String getNom() { return nom; }
        public void setNom(String nom) { this.nom = nom; }
        public int getEdat() { return edat; }
        public void setEdat(int edat) { this.edat = edat; }
        public boolean isActiu() { return actiu; }
        public void setActiu(boolean actiu) { this.actiu = actiu; }
        public Date getData() { return data; }
        public void setData(Date data) { this.data = data; }
        public Estat getEstat() { return estat; }
        public void setEstat(Estat estat) { this.estat = estat; }
    }

    @Test
    public void newInstanceMap_sourceNull_retornaNull() {
        assertNull(helper.newInstanceMap(null, Desti.class));
    }

    @Test
    public void newInstanceMap_copiaCampsAmbElMateixNom() {
        Date ara = new Date();
        Origen origen = new Origen("Joan", 30, true, ara, Estat.ACTIU);

        Desti desti = helper.newInstanceMap(origen, Desti.class);

        assertEquals("Joan", desti.getNom());
        assertEquals(30, desti.getEdat());
        assertTrue(desti.isActiu());
        assertEquals(ara, desti.getData());
        assertEquals(Estat.ACTIU, desti.getEstat());
    }

    /**
     * NOTA: ObjectMappingHelper.map() té un bug al camí de valor per defecte per a
     * primitius (targetField.setInt/setBoolean/... directes) que no crida
     * ReflectionUtils.makeAccessible(targetField) abans d'escriure — a diferència de
     * la resta de camins, que passen per setFieldValue() i sí la criden. Amb un camp
     * de destí primitiu privat (el cas habitual en entitats/DTOs reals, que solen
     * estar en un paquet diferent d'ObjectMappingHelper) això llança
     * IllegalAccessException. Aquest test documenta el comportament actual.
     */
    @Test
    public void map_campPrimitiuPrivatAmbOrigenNull_llancaIllegalStateExceptionPerBugDAccessibilitat() {
        Origen origen = new Origen(null, null, null, null, null);
        Desti desti = new Desti();
        desti.setEdat(99);

        assertThrows(IllegalStateException.class, () -> helper.map(origen, desti));
    }

    @Test
    public void map_ambCampsIgnorats_noEsModifiquen() {
        Origen origen = new Origen("Joan", 30, true, new Date(), Estat.ACTIU);
        Desti desti = new Desti();
        desti.setNom("Nom previ");

        helper.map(origen, desti, "nom");

        assertEquals("Nom previ", desti.getNom());
        assertEquals(30, desti.getEdat());
    }

    @Test
    public void map_campNomesEnOrigen_esIgnoraSenseError() {
        Origen origen = new Origen("Joan", 30, true, new Date(), Estat.ACTIU);
        origen.nomesEnOrigen = "valor";
        Desti desti = new Desti();

        assertDoesNotThrow(() -> helper.map(origen, desti));
        assertEquals("Joan", desti.getNom());
    }

    @Test
    public void clone_copiaTotsElsCampsAUnaNovaInstancia() {
        Origen original = new Origen("Joan", 30, true, new Date(), Estat.ACTIU);

        Origen clonat = helper.clone(original);

        assertNotSame(original, clonat);
        assertEquals(original.nom, clonat.nom);
        assertEquals(original.edat, clonat.edat);
        assertEquals(original.actiu, clonat.actiu);
        assertEquals(original.estat, clonat.estat);
    }

    @Test
    public void clone_objecteNull_retornaNull() {
        assertNull(helper.clone(null));
    }

    @Test
    public void clone_modificarClonatNoAfectaOriginal() {
        Origen original = new Origen("Joan", 30, true, new Date(), Estat.ACTIU);

        Origen clonat = helper.clone(original);
        clonat.nom = "Altre nom";

        assertEquals("Joan", original.nom);
        assertEquals("Altre nom", clonat.nom);
    }

    public static class SenseConstructorNiBuilder {
        private SenseConstructorNiBuilder(String x) {}
    }

    @Test
    public void newInstanceMap_classeSenseConstructorBuitNiBuilder_llancaObjectMappingException() {
        Origen origen = new Origen("Joan", 30, true, new Date(), Estat.ACTIU);

        assertThrows(ObjectMappingException.class,
                () -> helper.newInstanceMap(origen, SenseConstructorNiBuilder.class));
    }
}
