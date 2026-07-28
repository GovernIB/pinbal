package es.caib.pinbal.persist.entity.explotacio;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExplotConsultaFetsTest {

    private ExplotConsultaFets crear(long recOk, long recError) {
        return ExplotConsultaFets.builder()
                .entitatId(1L)
                .entitatCodi("ENT1")
                .procedimentId(2L)
                .procedimentCodi("PROC1")
                .serveiCodi("SERVEI1")
                .usuariCodi("USUARI1")
                .recOk(recOk)
                .recError(recError)
                .recPend(1)
                .recProc(2)
                .recMassOk(3)
                .recMassError(4)
                .recMassPend(5)
                .recMassProc(6)
                .webOk(7)
                .webError(8)
                .webPend(9)
                .webProc(10)
                .webMassOk(11)
                .webMassError(12)
                .webMassPend(13)
                .webMassProc(14)
                .build();
    }

    @Test
    public void testBuilderIGetters() {
        ExplotConsultaFets fets = crear(100, 5);

        assertEquals(1L, fets.getEntitatId());
        assertEquals("ENT1", fets.getEntitatCodi());
        assertEquals(2L, fets.getProcedimentId());
        assertEquals("PROC1", fets.getProcedimentCodi());
        assertEquals("SERVEI1", fets.getServeiCodi());
        assertEquals("USUARI1", fets.getUsuariCodi());
        assertEquals(100, fets.getRecOk());
        assertEquals(5, fets.getRecError());
        assertEquals(1, fets.getRecPend());
        assertEquals(2, fets.getRecProc());
        assertEquals(3, fets.getRecMassOk());
        assertEquals(4, fets.getRecMassError());
        assertEquals(5, fets.getRecMassPend());
        assertEquals(6, fets.getRecMassProc());
        assertEquals(7, fets.getWebOk());
        assertEquals(8, fets.getWebError());
        assertEquals(9, fets.getWebPend());
        assertEquals(10, fets.getWebProc());
        assertEquals(11, fets.getWebMassOk());
        assertEquals(12, fets.getWebMassError());
        assertEquals(13, fets.getWebMassPend());
        assertEquals(14, fets.getWebMassProc());
    }

    @Test
    public void testMinus() {
        ExplotConsultaFets actual = crear(100, 10);
        ExplotConsultaFets anterior = ExplotConsultaFets.builder()
                .entitatId(1L)
                .entitatCodi("ENT1")
                .procedimentId(2L)
                .procedimentCodi("PROC1")
                .serveiCodi("SERVEI1")
                .usuariCodi("USUARI1")
                .recOk(30)
                .recError(2)
                .recPend(0)
                .recProc(0)
                .recMassOk(0)
                .recMassError(0)
                .recMassPend(0)
                .recMassProc(0)
                .webOk(0)
                .webError(0)
                .webPend(0)
                .webProc(0)
                .webMassOk(0)
                .webMassError(0)
                .webMassPend(0)
                .webMassProc(0)
                .build();

        ExplotConsultaFets resultat = actual.minus(anterior);

        assertEquals(1L, resultat.getEntitatId());
        assertEquals("ENT1", resultat.getEntitatCodi());
        assertEquals(2L, resultat.getProcedimentId());
        assertEquals("PROC1", resultat.getProcedimentCodi());
        assertEquals("SERVEI1", resultat.getServeiCodi());
        assertNull(resultat.getUsuariCodi());
        assertEquals(70, resultat.getRecOk());
        assertEquals(8, resultat.getRecError());
        assertEquals(1, resultat.getRecPend());
        assertEquals(2, resultat.getRecProc());
    }

    @Test
    public void testMinus_entitatIncorrecta() {
        ExplotConsultaFets actual = crear(1, 1);
        ExplotConsultaFets anterior = ExplotConsultaFets.builder()
                .entitatId(2L)
                .procedimentId(2L)
                .serveiCodi("SERVEI1")
                .usuariCodi("USUARI1")
                .build();

        assertThrows(RuntimeException.class, () -> actual.minus(anterior));
    }

    @Test
    public void testMinus_procedimentIncorrecte() {
        ExplotConsultaFets actual = crear(1, 1);
        ExplotConsultaFets anterior = ExplotConsultaFets.builder()
                .entitatId(1L)
                .procedimentId(3L)
                .serveiCodi("SERVEI1")
                .usuariCodi("USUARI1")
                .build();

        assertThrows(RuntimeException.class, () -> actual.minus(anterior));
    }

    @Test
    public void testMinus_serveiIncorrecte() {
        ExplotConsultaFets actual = crear(1, 1);
        ExplotConsultaFets anterior = ExplotConsultaFets.builder()
                .entitatId(1L)
                .procedimentId(2L)
                .serveiCodi("SERVEI2")
                .usuariCodi("USUARI1")
                .build();

        assertThrows(RuntimeException.class, () -> actual.minus(anterior));
    }

    @Test
    public void testMinus_usuariIncorrecte() {
        ExplotConsultaFets actual = crear(1, 1);
        ExplotConsultaFets anterior = ExplotConsultaFets.builder()
                .entitatId(1L)
                .procedimentId(2L)
                .serveiCodi("SERVEI1")
                .usuariCodi("USUARI2")
                .build();

        assertThrows(RuntimeException.class, () -> actual.minus(anterior));
    }

    @Test
    public void testEqualsAndHashCode() {
        ExplotConsultaFets fets1 = crear(1, 1);
        ExplotConsultaFets fets2 = crear(1, 1);
        ExplotConsultaFets fets3 = crear(2, 1);

        assertEquals(fets1, fets2);
        assertEquals(fets1.hashCode(), fets2.hashCode());
        assertNotEquals(fets1, fets3);
        assertNotEquals(fets1, null);
        assertNotEquals(fets1, new Object());
        assertEquals(fets1, fets1);
    }

    @Test
    public void testToString() {
        ExplotConsultaFets fets = crear(1, 1);

        assertNotNull(fets.toString());
    }
}
