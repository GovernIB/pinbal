package es.caib.pinbal.persist.entity;

import es.caib.pinbal.logic.intf.dto.IntegracioAccioEstatEnumDto;
import es.caib.pinbal.logic.intf.dto.IntegracioAccioTipusEnumDto;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IntegracioAccioParamEntityTest {

    private IntegracioAccioEntity crearAccio() {
        return IntegracioAccioEntity.getBuilder(
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
    }

    @Test
    public void testGetBuilder() {
        IntegracioAccioEntity accio = crearAccio();

        IntegracioAccioParamEntity param = IntegracioAccioParamEntity.getBuilder(
                accio, "PARAM1", "Valor del paràmetre").build();

        assertEquals(accio, param.getIntegracioAccio());
        assertEquals("PARAM1", param.getNom());
        assertEquals("Valor del paràmetre", param.getDescripcio());
    }

    @Test
    public void testSetters() {
        IntegracioAccioEntity accio = crearAccio();
        IntegracioAccioParamEntity param = IntegracioAccioParamEntity.getBuilder(
                accio, "PARAM1", "Valor").build();
        IntegracioAccioEntity altraAccio = crearAccio();

        param.setIntegracioAccio(altraAccio);
        param.setNom("PARAM2");
        param.setDescripcio("Un altre valor");

        assertEquals(altraAccio, param.getIntegracioAccio());
        assertEquals("PARAM2", param.getNom());
        assertEquals("Un altre valor", param.getDescripcio());
    }

    @Test
    public void testAbbreviateValorsLlargs() {
        String nomLlarg = repeat("N", 300);
        String descripcioLlarga = repeat("D", 1200);

        IntegracioAccioParamEntity param = IntegracioAccioParamEntity.getBuilder(
                crearAccio(), nomLlarg, descripcioLlarga).build();

        assertTrue(param.getNom().length() <= 256);
        assertTrue(param.getDescripcio().length() <= 1024);
    }

    private static String repeat(String s, int times) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++) {
            sb.append(s);
        }
        return sb.toString();
    }
}
