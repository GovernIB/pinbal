package es.caib.pinbal.client.comu;

public enum LogLevel {
    TRACE(0),
    DEBUG(1),
    INFO(2),
    WARN(3),
    ERROR(4),
    OFF(5);

    int level;

    LogLevel(int level) {
        this.level =  level;
    }
    public int getLevel() {
        return level;
    }
    public static LogLevel getLogLevel(int level) {
        for (LogLevel logLevel : LogLevel.values()) {
            if (logLevel.getLevel() == level) {
                return logLevel;
            }
        }
        return null;
    }

    public boolean isTraceEnabled() {
        return this.level <= TRACE.level;
    }
    public boolean isDebugEnabled() {
        return this.level <= DEBUG.level;
    }
    public boolean isInfoEnabled() {
        return this.level <= INFO.level;
    }
    public boolean isWarnEnabled() {
        return this.level <= WARN.level;
    }
    public boolean isErrorEnabled() {
        return this.level <= ERROR.level;
    }
    public boolean isOffEnabled() {
        return this.level >= OFF.level;
    }
}
