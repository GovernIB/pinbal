package es.caib.pinbal.core.helper;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class LoggerHelper {

    @Autowired
    ConfigHelper configHelper;

    @Setter
    private static Map<LoggingTipus, Boolean> logs = new HashMap<>();

    private static LoggerHelper INSTANCE = null;

    public static LoggerHelper getInstance() {
        return INSTANCE;
    }

    @PostConstruct
    public void postConstruct() {
        INSTANCE = this;
    }

    public static final String PREFIX = "es.caib.pinbal.log.tipus.";


    public void info(String msg, Logger log, LoggingTipus tipus) {

        if (log == null || !mostrarLog(tipus)) {
            return;
        }
        log.info(tipus.name() + ">>> " + msg);
    }

    public void info(String msg, Exception ex, Logger log, LoggingTipus tipus) {

        if (log == null ||!mostrarLog(tipus)) {
            return;
        }
        log.info(tipus.name() + ">>> " + msg, ex);
    }

    public void error(String msg, Logger log, LoggingTipus tipus) {

        if (log == null ||!mostrarLog(tipus)) {
            return;
        }
        log.error(tipus.name() + ">>> " + msg);
    }

    public void error(String msg, Exception ex, Logger log, LoggingTipus tipus) {

        if (log == null ||!mostrarLog(tipus)) {
            return;
        }
        log.error(tipus.name() + ">>> " + msg, ex);
    }

    private boolean mostrarLog(LoggingTipus tipus) {

        if (tipus == null) {
            return false;
        }
        if (!logs.containsKey(tipus)) {
            getLogTipus(tipus);
        }
        return logs.get(tipus);
    }

    private void getLogTipus(LoggingTipus tipus) {

        try {
            logs.put(tipus, configHelper.getAsBoolean(PREFIX + tipus, false));
        } catch (Exception ex) {
            logs.put(tipus, false);
            log.error("Error obtenint la config key ", ex);
        }
    }

    public static void resetLogs() {
        logs = new HashMap<>();
    }

    public enum LoggingTipus {

        GENERIC,
        CONSULTA,
        CONS_MULT,
        CONS_REC,
        CONS_REC_MULT;

    }

}
