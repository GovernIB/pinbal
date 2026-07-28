package es.caib.pinbal.persist.entity.explotacio;

import es.caib.pinbal.logic.intf.dto.DiaSetmanaEnum;
import org.junit.jupiter.api.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExplotTempsEntityTest {

    @Test
    public void testConstructorPerDefecte() {
        ExplotTempsEntity entity = new ExplotTempsEntity();

        int anyActual = Calendar.getInstance().get(Calendar.YEAR);

        assertEquals(anyActual, entity.getAnualitat());
        assertNotNull(entity.getData());
        assertNotNull(entity.getDiaSetmana());
        assertTrue(entity.getMes() >= 1 && entity.getMes() <= 12);
        assertTrue(entity.getTrimestre() >= 1 && entity.getTrimestre() <= 4);
        assertTrue(entity.getDia() >= 1 && entity.getDia() <= 31);
        assertNotNull(entity.getSetmana());
    }

    @Test
    public void testConstructorAmbData() {
        Calendar cal = Calendar.getInstance();
        cal.set(2024, Calendar.JUNE, 10, 0, 0, 0);
        Date data = cal.getTime();

        ExplotTempsEntity entity = new ExplotTempsEntity(data);

        assertEquals(2024, entity.getAnualitat());
        assertEquals(6, entity.getMes());
        assertEquals(2, entity.getTrimestre());
        assertEquals(10, entity.getDia());
        assertEquals(DiaSetmanaEnum.LUN, entity.getDiaSetmana());
        assertNotNull(entity.getData());
        assertNotNull(entity.getSetmana());
    }

    @Test
    public void testEmplenarCampsTotsElsDiesSetmana() {
        DiaSetmanaEnum[] esperats = {
                DiaSetmanaEnum.LUN, DiaSetmanaEnum.MAR, DiaSetmanaEnum.MIE, DiaSetmanaEnum.JUE,
                DiaSetmanaEnum.VIE, DiaSetmanaEnum.SAB, DiaSetmanaEnum.DOM};

        Calendar base = Calendar.getInstance();
        base.set(2024, Calendar.JUNE, 10, 0, 0, 0);

        for (int i = 0; i < esperats.length; i++) {
            Calendar cal = (Calendar) base.clone();
            cal.add(Calendar.DAY_OF_MONTH, i);
            ExplotTempsEntity entity = new ExplotTempsEntity(cal.getTime());
            assertEquals(esperats[i], entity.getDiaSetmana());
        }
    }

    @Test
    public void testTotsElsMesosITrimestres() {
        int[] mesosEsperats = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        int[] trimestresEsperats = {1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4};

        for (int mes = 0; mes < 12; mes++) {
            Calendar cal = Calendar.getInstance();
            cal.set(2024, mes, 15, 0, 0, 0);
            ExplotTempsEntity entity = new ExplotTempsEntity(cal.getTime());
            assertEquals(mesosEsperats[mes], entity.getMes());
            assertEquals(trimestresEsperats[mes], entity.getTrimestre());
        }
    }

    @Test
    public void testGetDataPerConsulta() {
        Calendar cal = Calendar.getInstance();
        cal.set(2024, Calendar.JUNE, 10, 0, 0, 0);
        ExplotTempsEntity entity = new ExplotTempsEntity(cal.getTime());

        Calendar esperat = Calendar.getInstance();
        esperat.setTime(entity.getData());
        esperat.add(Calendar.DAY_OF_YEAR, 1);

        assertEquals(esperat.getTime(), entity.getDataPerConsulta());
    }

    @Test
    public void testBuilderISetters() {
        Date data = new Date();

        ExplotTempsEntity entity = ExplotTempsEntity.builder()
                .data(data)
                .anualitat(2023)
                .mes(1)
                .trimestre(1)
                .setmana(2)
                .dia(3)
                .diaSetmana(DiaSetmanaEnum.DOM)
                .build();

        assertEquals(data, entity.getData());
        assertEquals(2023, entity.getAnualitat());
        assertEquals(1, entity.getMes());
        assertEquals(1, entity.getTrimestre());
        assertEquals(2, entity.getSetmana());
        assertEquals(3, entity.getDia());
        assertEquals(DiaSetmanaEnum.DOM, entity.getDiaSetmana());

        entity.setAnualitat(2025);
        entity.setMes(12);
        entity.setTrimestre(4);
        entity.setSetmana(52);
        entity.setDia(31);
        entity.setDiaSetmana(DiaSetmanaEnum.SAB);
        Date novaData = new Date();
        entity.setData(novaData);

        assertEquals(2025, entity.getAnualitat());
        assertEquals(12, entity.getMes());
        assertEquals(4, entity.getTrimestre());
        assertEquals(52, entity.getSetmana());
        assertEquals(31, entity.getDia());
        assertEquals(DiaSetmanaEnum.SAB, entity.getDiaSetmana());
        assertEquals(novaData, entity.getData());
    }
}
