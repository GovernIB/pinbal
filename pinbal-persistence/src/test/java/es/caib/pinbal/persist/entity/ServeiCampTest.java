package es.caib.pinbal.persist.entity;

import es.caib.pinbal.logic.intf.dto.ServeiCampDto.ServeiCampDtoValidacioDataTipus;
import es.caib.pinbal.logic.intf.dto.ServeiCampDto.ServeiCampDtoValidacioOperacio;
import es.caib.pinbal.persist.entity.ServeiCamp.ServeiCampTipus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServeiCampTest {

    @Test
    public void testGetBuilderSimple() {
        ServeiCamp camp = ServeiCamp.getBuilder("SERVEI1", "path.simple", 1, 10).build();

        assertEquals("SERVEI1", camp.getServei());
        assertEquals("path.simple", camp.getPath());
        assertEquals(1, camp.getOrdre());
        assertEquals(10, camp.getMida());
        assertEquals(ServeiCampTipus.TEXT, camp.getTipus());
    }

    @Test
    public void testGetBuilderComplet() {
        ServeiCamp camp = ServeiCamp.getBuilder(
                "SERVEI1", "path.complet", ServeiCampTipus.NUMERIC, "Etiqueta", "0", 2, 20).build();

        assertEquals("SERVEI1", camp.getServei());
        assertEquals("path.complet", camp.getPath());
        assertEquals(ServeiCampTipus.NUMERIC, camp.getTipus());
        assertEquals("Etiqueta", camp.getEtiqueta());
        assertEquals("0", camp.getValorPerDefecte());
        assertEquals(2, camp.getOrdre());
        assertEquals(20, camp.getMida());
        assertEquals(0L, camp.getVersion());
        assertFalse(camp.isInicialitzar());
        assertFalse(camp.isObligatori());
        assertTrue(camp.isModificable());
        assertTrue(camp.isVisible());
        assertNull(camp.getEnumDescripcions());
        assertNull(camp.getCampPare());
        assertNull(camp.getGrup());
        assertNotNull(camp.getCampsFills());
        assertTrue(camp.getCampsFills().isEmpty());
    }

    @Test
    public void testSettersDirectes() {
        ServeiCamp camp = ServeiCamp.getBuilder("SERVEI1", "path.a", 1, 10).build();

        camp.setObligatori(true);
        camp.setModificable(false);
        camp.setVisible(false);

        assertTrue(camp.isObligatori());
        assertFalse(camp.isModificable());
        assertFalse(camp.isVisible());
    }

    @Test
    public void testUpdate() {
        ServeiCamp campPare = ServeiCamp.getBuilder("SERVEI1", "path.pare", 1, 10).build();
        ServeiCamp campCmp2 = ServeiCamp.getBuilder("SERVEI1", "path.cmp2", 2, 10).build();
        ServeiCampGrup grup = ServeiCampGrup.getBuilder("SERVEI1", null, "Grup 1", "Ajuda grup", 1).build();
        String[] descripcions = {"Un", "Dos", "Tres"};

        ServeiCamp camp = ServeiCamp.getBuilder("SERVEI1", "path.fill", 3, 15).build();
        camp.update(
                ServeiCampTipus.ENUM,
                "Etiqueta nova",
                25,
                "1",
                "Comentari",
                descripcions,
                "dd/MM/yyyy",
                campPare,
                "valorPare",
                grup,
                true,
                true,
                false,
                false,
                "^[0-9]+$",
                1,
                100,
                ServeiCampDtoValidacioOperacio.GT,
                campCmp2,
                5,
                ServeiCampDtoValidacioDataTipus.DIES);

        assertEquals(ServeiCampTipus.ENUM, camp.getTipus());
        assertEquals("Etiqueta nova", camp.getEtiqueta());
        assertEquals(25, camp.getMida());
        assertEquals("1", camp.getValorPerDefecte());
        assertEquals("Comentari", camp.getComentari());
        assertArrayEquals(descripcions, camp.getEnumDescripcions());
        assertEquals("dd/MM/yyyy", camp.getDataFormat());
        assertEquals(campPare, camp.getCampPare());
        assertEquals("valorPare", camp.getValorPare());
        assertEquals(grup, camp.getGrup());
        assertTrue(camp.isInicialitzar());
        assertTrue(camp.isObligatori());
        assertFalse(camp.isModificable());
        assertFalse(camp.isVisible());
        assertEquals("^[0-9]+$", camp.getValidacioRegexp());
        assertEquals(1, camp.getValidacioMin());
        assertEquals(100, camp.getValidacioMax());
        assertEquals(ServeiCampDtoValidacioOperacio.GT, camp.getValidacioDataCmpOperacio());
        assertEquals(campCmp2, camp.getValidacioDataCmpCamp2());
        assertEquals(5, camp.getValidacioDataCmpNombre());
        assertEquals(ServeiCampDtoValidacioDataTipus.DIES, camp.getValidacioDataCmpTipus());
    }

    @Test
    public void testUpdateEnumDescripcionsNull() {
        ServeiCamp camp = ServeiCamp.getBuilder("SERVEI1", "path.enum", 1, 10).build();

        camp.updateEnumDescripcions(new String[]{"A", "B"});
        assertArrayEquals(new String[]{"A", "B"}, camp.getEnumDescripcions());

        camp.updateEnumDescripcions(null);
        assertNull(camp.getEnumDescripcions());
    }

    @Test
    public void testUpdateEnumDescripcionsMassaLlarg() {
        ServeiCamp camp = ServeiCamp.getBuilder("SERVEI1", "path.enumllarg", 1, 10).build();

        StringBuilder llarga = new StringBuilder();
        for (int i = 0; i < 1100; i++)
            llarga.append("x");

        camp.updateEnumDescripcions(new String[]{llarga.toString()});

        assertEquals(1024, camp.getEnumDescripcions()[0].length());
    }

    @Test
    public void testUpdateTipusOrdreMidaGrup() {
        ServeiCamp camp = ServeiCamp.getBuilder("SERVEI1", "path.b", 1, 10).build();
        ServeiCampGrup grup = ServeiCampGrup.getBuilder("SERVEI1", null, "Grup 2", null, 1).build();

        camp.updateTipus(ServeiCampTipus.BOOLEA);
        camp.updateOrdre(9);
        camp.updateMida(99);
        camp.updateGrup(grup);

        assertEquals(ServeiCampTipus.BOOLEA, camp.getTipus());
        assertEquals(9, camp.getOrdre());
        assertEquals(99, camp.getMida());
        assertEquals(grup, camp.getGrup());
    }

    @Test
    public void testDeleteCampPare() {
        ServeiCamp campPare = ServeiCamp.getBuilder("SERVEI1", "path.pare2", 1, 10).build();
        ServeiCamp camp = ServeiCamp.getBuilder(
                "SERVEI1", "path.fill2", ServeiCampTipus.TEXT, "E", "V", 1, 10).build();
        camp.update(
                ServeiCampTipus.TEXT, "E", 10, "V", null, null, null,
                campPare, "valorPare", null,
                false, false, true, true,
                null, null, null, null, null, null, null);

        assertEquals(campPare, camp.getCampPare());

        camp.deleteCampPare();

        assertNull(camp.getCampPare());
    }

    @Test
    public void testIsEnumOLloc() {
        assertFalse(ServeiCampTipus.isEnumOLloc(null));
        assertTrue(ServeiCampTipus.isEnumOLloc(ServeiCampTipus.ENUM));
        assertTrue(ServeiCampTipus.isEnumOLloc(ServeiCampTipus.PAIS));
        assertTrue(ServeiCampTipus.isEnumOLloc(ServeiCampTipus.PROVINCIA));
        assertTrue(ServeiCampTipus.isEnumOLloc(ServeiCampTipus.MUNICIPI_5));
        assertTrue(ServeiCampTipus.isEnumOLloc(ServeiCampTipus.MUNICIPI_3));
        assertFalse(ServeiCampTipus.isEnumOLloc(ServeiCampTipus.TEXT));
        assertFalse(ServeiCampTipus.isEnumOLloc(ServeiCampTipus.NUMERIC));
        assertFalse(ServeiCampTipus.isEnumOLloc(ServeiCampTipus.DATA));
        assertFalse(ServeiCampTipus.isEnumOLloc(ServeiCampTipus.ETIQUETA));
        assertFalse(ServeiCampTipus.isEnumOLloc(ServeiCampTipus.BOOLEA));
        assertFalse(ServeiCampTipus.isEnumOLloc(ServeiCampTipus.DOC_IDENT));
        assertFalse(ServeiCampTipus.isEnumOLloc(ServeiCampTipus.ADJUNT_BINARI));
        assertFalse(ServeiCampTipus.isEnumOLloc(ServeiCampTipus.ADJUNT_XML));
    }

    @Test
    public void testEqualsAndHashCode() {
        // ServeiCamp hereta equals()/hashCode() (part) d'AbstractPersistable, basat en id;
        // com que l'id és null a instàncies noves, mai són iguals entre elles encara que
        // coincideixin servei/path, però el hashCode de la subclasse sí que es basa en els camps.
        ServeiCamp camp1 = ServeiCamp.getBuilder("SERVEI1", "path.eq", 1, 10).build();
        ServeiCamp camp2 = ServeiCamp.getBuilder("SERVEI1", "path.eq", 1, 10).build();
        ServeiCamp camp3 = ServeiCamp.getBuilder("SERVEI2", "path.altre", 2, 20).build();

        assertEquals(camp1, camp1);
        assertNotEquals(camp1, camp2);
        assertNotEquals(camp1, camp3);
        assertNotEquals(camp1, null);
        assertNotEquals(camp1, new Object());
        assertEquals(camp1.hashCode(), camp2.hashCode());
        assertNotEquals(camp1.hashCode(), camp3.hashCode());
    }

    @Test
    public void testToString() {
        ServeiCamp camp = ServeiCamp.getBuilder("SERVEI1", "path.tostring", 1, 10).build();

        assertNotNull(camp.toString());
    }
}
