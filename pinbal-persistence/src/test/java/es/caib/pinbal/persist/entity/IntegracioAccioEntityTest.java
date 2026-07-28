package es.caib.pinbal.persist.entity;

import es.caib.pinbal.logic.intf.dto.IntegracioAccioEstatEnumDto;
import es.caib.pinbal.logic.intf.dto.IntegracioAccioTipusEnumDto;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IntegracioAccioEntityTest {

    @Test
    public void testGetBuilder() {
        Date data = new Date();

        IntegracioAccioEntity accio = IntegracioAccioEntity.getBuilder(
                "CODI1",
                "PET1",
                data,
                "Descripció de prova",
                IntegracioAccioTipusEnumDto.ENVIAMENT,
                150L,
                IntegracioAccioEstatEnumDto.OK,
                "Cap error",
                "Cap excepció",
                "Cap stacktrace").build();

        assertEquals("CODI1", accio.getCodi());
        assertEquals("PET1", accio.getIdPeticio());
        assertEquals(data, accio.getData());
        assertEquals("Descripció de prova", accio.getDescripcio());
        assertEquals(IntegracioAccioTipusEnumDto.ENVIAMENT, accio.getTipus());
        assertEquals(150L, accio.getTempsResposta());
        assertEquals(IntegracioAccioEstatEnumDto.OK, accio.getEstat());
        assertEquals("Cap error", accio.getErrorDescripcio());
        assertEquals("Cap excepció", accio.getExcepcioMessage());
        assertEquals("Cap stacktrace", accio.getExcepcioStacktrace());
        assertTrue(accio.getParametres().isEmpty());
    }

    @Test
    public void testSettersICodiEntitat() {
        IntegracioAccioEntity accio = IntegracioAccioEntity.getBuilder(
                "CODI1",
                "PET1",
                new Date(),
                "Descripció",
                IntegracioAccioTipusEnumDto.RECEPCIO,
                50L,
                IntegracioAccioEstatEnumDto.ERROR,
                null,
                null,
                null).build();

        accio.setCodiUsuari("USUARI1");
        accio.setCodiEntitat("ENT1");

        assertEquals("USUARI1", accio.getCodiUsuari());
        assertEquals("ENT1", accio.getCodiEntitat());
        assertEquals(IntegracioAccioTipusEnumDto.RECEPCIO, accio.getTipus());
        assertEquals(IntegracioAccioEstatEnumDto.ERROR, accio.getEstat());
    }

    @Test
    public void testGetParametresIAfegirParam() {
        IntegracioAccioEntity accio = IntegracioAccioEntity.getBuilder(
                "CODI1",
                "PET1",
                new Date(),
                "Descripció",
                IntegracioAccioTipusEnumDto.ENVIAMENT,
                10L,
                IntegracioAccioEstatEnumDto.OK,
                null,
                null,
                null).build();
        IntegracioAccioParamEntity param = IntegracioAccioParamEntity.getBuilder(
                accio, "PARAM1", "Valor 1").build();

        accio.getParametres().add(param);

        assertEquals(1, accio.getParametres().size());
        assertEquals(param, accio.getParametres().get(0));
    }

    @Test
    public void testAbbreviateValorsLlargs() {
        String codiLlarg = repeat("A", 100);
        String descripcioLlarga = repeat("B", 2000);

        IntegracioAccioEntity accio = IntegracioAccioEntity.getBuilder(
                codiLlarg,
                "PET1",
                new Date(),
                descripcioLlarga,
                IntegracioAccioTipusEnumDto.ENVIAMENT,
                10L,
                IntegracioAccioEstatEnumDto.OK,
                null,
                null,
                null).build();

        assertTrue(accio.getCodi().length() <= 64);
        assertTrue(accio.getDescripcio().length() <= 960);
    }

    private static String repeat(String s, int times) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++) {
            sb.append(s);
        }
        return sb.toString();
    }
}
