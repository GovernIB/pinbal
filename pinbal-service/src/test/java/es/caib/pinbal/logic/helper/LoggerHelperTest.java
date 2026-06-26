package es.caib.pinbal.logic.helper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoggerHelperTest {

    @Mock
    private ConfigHelper configHelper;

    @InjectMocks
    private LoggerHelper loggerHelper;

    @Mock
    private Logger mockLogger;

    @BeforeEach
    public void setUp() {
        // Reset static state between tests
        LoggerHelper.resetLogs();
    }

    @Test
    public void resetLogs_buidaElsMaps() {
        LoggerHelper.resetLogs();
        // After reset, logs map is empty; next call to info/error will re-read config
        assertDoesNotThrow(() -> LoggerHelper.resetLogs());
    }

    @Test
    public void info_loggerNull_noFaRes() {
        assertDoesNotThrow(() ->
                loggerHelper.info("msg", (Logger) null, LoggerHelper.LoggingTipus.GENERIC));
    }

    @Test
    public void info_logDesactivat_noEscriu() {
        when(configHelper.getConfigAsBoolean(anyString(), eq(false))).thenReturn(false);
        loggerHelper.info("msg", mockLogger, LoggerHelper.LoggingTipus.GENERIC);
        verify(mockLogger, never()).info(anyString());
    }

    @Test
    public void info_logActivat_escriu() {
        when(configHelper.getConfigAsBoolean(anyString(), eq(false))).thenReturn(true);
        loggerHelper.info("missatge test", mockLogger, LoggerHelper.LoggingTipus.CONSULTA);
        verify(mockLogger).info(contains("missatge test"));
    }

    @Test
    public void infoAmbException_logActivat_escriuAmbException() {
        when(configHelper.getConfigAsBoolean(anyString(), eq(false))).thenReturn(true);
        Exception ex = new RuntimeException("error");
        loggerHelper.info("missatge", ex, mockLogger, LoggerHelper.LoggingTipus.CONSULTA);
        verify(mockLogger).info(anyString(), eq(ex));
    }

    @Test
    public void error_logDesactivat_noEscriu() {
        when(configHelper.getConfigAsBoolean(anyString(), eq(false))).thenReturn(false);
        loggerHelper.error("error msg", mockLogger, LoggerHelper.LoggingTipus.CONS_MULT);
        verify(mockLogger, never()).error(anyString());
    }

    @Test
    public void error_logActivat_escriu() {
        when(configHelper.getConfigAsBoolean(anyString(), eq(false))).thenReturn(true);
        loggerHelper.error("error msg", mockLogger, LoggerHelper.LoggingTipus.CONS_REC);
        verify(mockLogger).error(contains("error msg"));
    }

    @Test
    public void errorAmbException_logActivat_escriuAmbException() {
        when(configHelper.getConfigAsBoolean(anyString(), eq(false))).thenReturn(true);
        Exception ex = new RuntimeException("exc");
        loggerHelper.error("error msg", ex, mockLogger, LoggerHelper.LoggingTipus.CONS_REC_MULT);
        verify(mockLogger).error(anyString(), eq(ex));
    }

    @Test
    public void info_tipusNull_noFaRes() {
        assertDoesNotThrow(() -> loggerHelper.info("msg", mockLogger, null));
        verify(mockLogger, never()).info(anyString());
    }

    @Test
    public void prefix_valorCorrecte() {
        assertTrue(LoggerHelper.PREFIX.startsWith("es.caib.pinbal"));
    }

    @Test
    public void loggingTipus_totsEsValors() {
        assertEquals(5, LoggerHelper.LoggingTipus.values().length);
        assertNotNull(LoggerHelper.LoggingTipus.valueOf("GENERIC"));
        assertNotNull(LoggerHelper.LoggingTipus.valueOf("CONSULTA"));
        assertNotNull(LoggerHelper.LoggingTipus.valueOf("CONS_MULT"));
        assertNotNull(LoggerHelper.LoggingTipus.valueOf("CONS_REC"));
        assertNotNull(LoggerHelper.LoggingTipus.valueOf("CONS_REC_MULT"));
    }
}
