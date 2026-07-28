package es.caib.pinbal.persist.entity.explotacio;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExplotConsultaFetsEntityTest {

    @Test
    public void testNoArgsConstructorISetters() {
        ExplotConsultaFetsEntity entity = new ExplotConsultaFetsEntity();
        ExplotConsultaDimensioEntity dimensio = new ExplotConsultaDimensioEntity();
        ExplotTempsEntity temps = new ExplotTempsEntity();

        entity.setNumRecobrimentOk(1);
        entity.setNumRecobrimentError(2);
        entity.setNumRecobrimentPendent(3);
        entity.setNumRecobrimentProcessant(4);
        entity.setNumRecobrimentMassiuOk(5);
        entity.setNumRecobrimentMassiuError(6);
        entity.setNumRecobrimentMassiuPendent(7);
        entity.setNumRecobrimentMassiuProcessant(8);
        entity.setNumWebOk(9);
        entity.setNumWebError(10);
        entity.setNumWebPendent(11);
        entity.setNumWebProcessant(12);
        entity.setNumWebMassiuOk(13);
        entity.setNumWebMassiuError(14);
        entity.setNumWebMassiuPendent(15);
        entity.setNumWebMassiuProcessant(16);
        entity.setConsultaDimensio(dimensio);
        entity.setTemps(temps);

        assertEquals(1, entity.getNumRecobrimentOk());
        assertEquals(2, entity.getNumRecobrimentError());
        assertEquals(3, entity.getNumRecobrimentPendent());
        assertEquals(4, entity.getNumRecobrimentProcessant());
        assertEquals(5, entity.getNumRecobrimentMassiuOk());
        assertEquals(6, entity.getNumRecobrimentMassiuError());
        assertEquals(7, entity.getNumRecobrimentMassiuPendent());
        assertEquals(8, entity.getNumRecobrimentMassiuProcessant());
        assertEquals(9, entity.getNumWebOk());
        assertEquals(10, entity.getNumWebError());
        assertEquals(11, entity.getNumWebPendent());
        assertEquals(12, entity.getNumWebProcessant());
        assertEquals(13, entity.getNumWebMassiuOk());
        assertEquals(14, entity.getNumWebMassiuError());
        assertEquals(15, entity.getNumWebMassiuPendent());
        assertEquals(16, entity.getNumWebMassiuProcessant());
        assertEquals(dimensio, entity.getConsultaDimensio());
        assertEquals(temps, entity.getTemps());
    }

    @Test
    public void testBuilder() {
        ExplotConsultaDimensioEntity dimensio = new ExplotConsultaDimensioEntity();
        ExplotTempsEntity temps = new ExplotTempsEntity();

        ExplotConsultaFetsEntity entity = ExplotConsultaFetsEntity.builder()
                .numRecobrimentOk(1)
                .numRecobrimentError(2)
                .numRecobrimentPendent(3)
                .numRecobrimentProcessant(4)
                .numRecobrimentMassiuOk(5)
                .numRecobrimentMassiuError(6)
                .numRecobrimentMassiuPendent(7)
                .numRecobrimentMassiuProcessant(8)
                .numWebOk(9)
                .numWebError(10)
                .numWebPendent(11)
                .numWebProcessant(12)
                .numWebMassiuOk(13)
                .numWebMassiuError(14)
                .numWebMassiuPendent(15)
                .numWebMassiuProcessant(16)
                .consultaDimensio(dimensio)
                .temps(temps)
                .build();

        assertEquals(1, entity.getNumRecobrimentOk());
        assertEquals(16, entity.getNumWebMassiuProcessant());
        assertEquals(dimensio, entity.getConsultaDimensio());
        assertEquals(temps, entity.getTemps());
    }
}
