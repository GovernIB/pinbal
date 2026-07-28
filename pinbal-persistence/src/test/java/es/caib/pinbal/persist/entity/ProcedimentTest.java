package es.caib.pinbal.persist.entity;

import es.caib.pinbal.logic.intf.dto.ProcedimentClaseTramiteEnumDto;
import es.caib.pinbal.logic.intf.dto.ProcedimentServeiSimpleDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProcedimentTest {

    private Entitat crearEntitat(String codi) {
        return Entitat.getBuilder(codi, "Entitat " + codi, "Q1234567A", Entitat.EntitatTipus.AJUNTAMENT).build();
    }

    @Test
    public void testGetBuilder() {
        Entitat entitat = crearEntitat("ENT1");
        OrganGestor organGestor = new OrganGestor();
        organGestor.setCodi("OG1");

        Procediment procediment = Procediment.getBuilder(
                entitat, "PROC1", "Procediment 1", "Departament 1", organGestor, "SIA1",
                Boolean.TRUE, ProcedimentClaseTramiteEnumDto.TRIBUTARIO).build();

        assertEquals(entitat, procediment.getEntitat());
        assertEquals("PROC1", procediment.getCodi());
        assertEquals("Procediment 1", procediment.getNom());
        assertEquals("Departament 1", procediment.getDepartament());
        assertEquals(organGestor, procediment.getOrganGestor());
        assertEquals("SIA1", procediment.getCodiSia());
        assertEquals(Boolean.TRUE, procediment.getValorCampAutomatizado());
        assertEquals(ProcedimentClaseTramiteEnumDto.TRIBUTARIO, procediment.getValorCampClaseTramite());
        assertTrue(procediment.isActiu());
        assertNull(procediment.getCodiSiaOrigen());
        assertTrue(procediment.getServeis().isEmpty());
        assertEquals(0, procediment.getVersion());
    }

    @Test
    public void testUpdate() {
        Procediment procediment = Procediment.getBuilder(
                crearEntitat("ENT1"), "PROC1", "Procediment 1", "Departament 1", null, "SIA1",
                Boolean.FALSE, ProcedimentClaseTramiteEnumDto.TRIBUTARIO).build();

        OrganGestor nouOrganGestor = new OrganGestor();
        nouOrganGestor.setCodi("OG2");
        procediment.update("PROC2", "Procediment 2", "Departament 2", nouOrganGestor, "SIA2",
                Boolean.TRUE, ProcedimentClaseTramiteEnumDto.SANCIONADOR);

        assertEquals("PROC2", procediment.getCodi());
        assertEquals("Procediment 2", procediment.getNom());
        assertEquals("Departament 2", procediment.getDepartament());
        assertEquals(nouOrganGestor, procediment.getOrganGestor());
        assertEquals("SIA2", procediment.getCodiSia());
        assertEquals(Boolean.TRUE, procediment.getValorCampAutomatizado());
        assertEquals(ProcedimentClaseTramiteEnumDto.SANCIONADOR, procediment.getValorCampClaseTramite());
    }

    @Test
    public void testUpdateActiu() {
        Procediment procediment = Procediment.getBuilder(
                crearEntitat("ENT1"), "PROC1", "Procediment 1", "Departament 1", null, "SIA1",
                Boolean.FALSE, null).build();

        procediment.updateActiu(false);

        assertFalse(procediment.isActiu());
    }

    @Test
    public void testUpdateCodiSiaOrigen() {
        Procediment procediment = Procediment.getBuilder(
                crearEntitat("ENT1"), "PROC1", "Procediment 1", "Departament 1", null, "SIA1",
                Boolean.FALSE, null).build();

        procediment.updateCodiSiaOrigen("SIAORIGEN1");

        assertEquals("SIAORIGEN1", procediment.getCodiSiaOrigen());
    }

    @Test
    public void testAddServei() {
        Procediment procediment = Procediment.getBuilder(
                crearEntitat("ENT1"), "PROC1", "Procediment 1", "Departament 1", null, "SIA1",
                Boolean.FALSE, null).build();

        procediment.addServei("SERVEI1");

        assertEquals(1, procediment.getServeis().size());
        ProcedimentServei procedimentServei = procediment.getServeis().get(0);
        assertEquals("SERVEI1", procedimentServei.getServei());
        assertEquals(procediment, procedimentServei.getProcediment());
        assertTrue(procedimentServei.isActiu());
    }

    @Test
    public void testGetServeisActius() {
        Procediment procediment = Procediment.getBuilder(
                crearEntitat("ENT1"), "PROC1", "Procediment 1", "Departament 1", null, "SIA1",
                Boolean.FALSE, null).build();

        procediment.addServei("SERVEI1");
        procediment.addServei("SERVEI2");
        procediment.getServeis().get(0).updateProcedimentCodi("PROC1");
        procediment.getServeis().get(1).updateActiu(false);

        List<ProcedimentServeiSimpleDto> actius = procediment.getServeisActius();

        assertEquals(1, actius.size());
        assertEquals("PROC1", actius.get(0).getProcedimentCodi());
        assertEquals("SERVEI1", actius.get(0).getServeiCodi());
        assertTrue(actius.get(0).isActiu());
    }

    @Test
    public void testConfigurarIdPerTest() {
        Procediment procediment = Procediment.getBuilder(
                crearEntitat("ENT1"), "PROC1", "Procediment 1", "Departament 1", null, "SIA1",
                Boolean.FALSE, null).build();

        procediment.configurarIdPerTest(10L);

        assertEquals(10L, procediment.getId());
    }

    @Test
    public void testEqualsAndHashCode() {
        Entitat entitat1 = crearEntitat("ENT1");
        Entitat entitat1b = crearEntitat("ENT1");
        Entitat entitat2 = crearEntitat("ENT2");

        Procediment procediment1 = Procediment.getBuilder(
                entitat1, "PROC1", "Procediment 1", "Departament 1", null, "SIA1",
                Boolean.FALSE, null).build();
        procediment1.configurarIdPerTest(1L);

        Procediment procediment2 = Procediment.getBuilder(
                entitat1b, "PROC1", "Un altre nom", "Departament 2", null, "SIA2",
                Boolean.TRUE, ProcedimentClaseTramiteEnumDto.SANCIONADOR).build();
        procediment2.configurarIdPerTest(1L);

        Procediment procedimentCodiDiferent = Procediment.getBuilder(
                entitat1, "PROC2", "Procediment 1", "Departament 1", null, "SIA1",
                Boolean.FALSE, null).build();
        procedimentCodiDiferent.configurarIdPerTest(1L);

        Procediment procedimentEntitatDiferent = Procediment.getBuilder(
                entitat2, "PROC1", "Procediment 1", "Departament 1", null, "SIA1",
                Boolean.FALSE, null).build();
        procedimentEntitatDiferent.configurarIdPerTest(1L);

        Procediment procedimentIdDiferent = Procediment.getBuilder(
                entitat1, "PROC1", "Procediment 1", "Departament 1", null, "SIA1",
                Boolean.FALSE, null).build();
        procedimentIdDiferent.configurarIdPerTest(2L);

        assertEquals(procediment1, procediment1);
        assertEquals(procediment1, procediment2);
        assertEquals(procediment1.hashCode(), procediment2.hashCode());
        assertNotEquals(procediment1, procedimentCodiDiferent);
        assertNotEquals(procediment1, procedimentEntitatDiferent);
        assertNotEquals(procediment1, procedimentIdDiferent);
        assertNotEquals(procediment1, null);
        assertNotEquals(procediment1, new Object());
    }

    @Test
    public void testToString() {
        Procediment procediment = Procediment.getBuilder(
                crearEntitat("ENT1"), "PROC1", "Procediment 1", "Departament 1", null, "SIA1",
                Boolean.FALSE, null).build();

        assertNotNull(procediment.toString());
    }
}
