package es.caib.pinbal.logic.helper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MonitorHelperTest {

    @Test
    public void getArch_noLlancaException() {
        assertDoesNotThrow(() -> {
            String arch = MonitorHelper.getArch();
            assertNotNull(arch);
        });
    }

    @Test
    public void getName_noLlancaException() {
        assertDoesNotThrow(() -> {
            String name = MonitorHelper.getName();
            assertNotNull(name);
        });
    }

    @Test
    public void getVersion_noLlancaException() {
        assertDoesNotThrow(() -> {
            String version = MonitorHelper.getVersion();
            assertNotNull(version);
        });
    }

    @Test
    public void getSunOSMBean_noNull() {
        // sunOSMBean is initialized in static block — should not be null
        assertNotNull(MonitorHelper.getSunOSMBean());
    }

    @Test
    public void getActiu_retornaValor() {
        // actiu may be null initially or a boolean
        assertDoesNotThrow(() -> MonitorHelper.getActiu());
    }

    @Test
    public void getCPULoad_retornaString() {
        String load = MonitorHelper.getCPULoad();
        assertNotNull(load);
    }

    @Test
    public void getCpuTime_retornaValorPositiuOZero() {
        long cpuTime = MonitorHelper.getCpuTime();
        assertTrue(cpuTime >= 0);
    }

    @Test
    public void getUserTime_retornaValorPositiuOZero() {
        long userTime = MonitorHelper.getUserTime();
        assertTrue(userTime >= 0);
    }

    @Test
    public void humanReadableByteCount_menysDeUnitat_retornaBytes() {
        assertEquals("500 B", MonitorHelper.humanReadableByteCount(500));
    }

    @Test
    public void humanReadableByteCount_kilobytes_retornaKiloBytes() {
        String result = MonitorHelper.humanReadableByteCount(1500);
        assertTrue(result.contains("kB"));
    }

    @Test
    public void getThreadsIds_retornaArray() {
        long[] ids = MonitorHelper.getThreadsIds();
        assertNotNull(ids);
        assertTrue(ids.length > 0);
    }
}
