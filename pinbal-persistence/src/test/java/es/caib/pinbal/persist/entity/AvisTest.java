package es.caib.pinbal.persist.entity;

import es.caib.pinbal.logic.intf.dto.AvisNivellEnumDto;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AvisTest {

    @Test
    public void testGetBuilder() {
        Date dataInici = new Date(1000L);
        Date dataFinal = new Date(2000L);

        Avis avis = Avis.getBuilder(
                "Assumpte de prova",
                "Missatge de prova",
                dataInici,
                dataFinal,
                AvisNivellEnumDto.INFO).build();

        assertEquals("Assumpte de prova", avis.getAssumpte());
        assertEquals("Missatge de prova", avis.getMissatge());
        assertEquals(dataInici, avis.getDataInici());
        assertEquals(dataFinal, avis.getDataFinal());
        assertEquals(AvisNivellEnumDto.INFO, avis.getAvisNivell());
        assertTrue(avis.getActiu());
    }

    @Test
    public void testUpdate() {
        Avis avis = Avis.getBuilder(
                "Assumpte inicial",
                "Missatge inicial",
                new Date(1000L),
                new Date(2000L),
                AvisNivellEnumDto.INFO).build();

        Date novaDataInici = new Date(3000L);
        Date novaDataFinal = new Date(4000L);
        avis.update(
                "Assumpte nou",
                "Missatge nou",
                novaDataInici,
                novaDataFinal,
                AvisNivellEnumDto.ERROR);

        assertEquals("Assumpte nou", avis.getAssumpte());
        assertEquals("Missatge nou", avis.getMissatge());
        assertEquals(novaDataInici, avis.getDataInici());
        assertEquals(novaDataFinal, avis.getDataFinal());
        assertEquals(AvisNivellEnumDto.ERROR, avis.getAvisNivell());
    }

    @Test
    public void testUpdateActiva() {
        Avis avis = Avis.getBuilder(
                "Assumpte",
                "Missatge",
                new Date(),
                null,
                AvisNivellEnumDto.WARNING).build();

        avis.updateActiva(false);

        assertFalse(avis.getActiu());
    }
}
