export type LogConsoleType = {
    log: (...args: any[]) => void;
    error: (...args: any[]) => void;
    warn: (...args: any[]) => void;
    info: (...args: any[]) => void;
    debug: (...args: any[]) => void;
    trace: (...args: any[]) => void;
};

export const useLogConsole = (prefix: String): LogConsoleType => {
    const logPrefix = '[' + prefix + ']';
    return {
        log: (...args: any[]) => console.log(logPrefix, ...args),
        error: (...args: any[]) => console.error(logPrefix, ...args),
        warn: (...args: any[]) => console.warn(logPrefix, ...args),
        info: (...args: any[]) => console.info(logPrefix, ...args),
        debug: (...args: any[]) => console.debug(logPrefix, ...args),
        trace: (...args: any[]) => console.trace(logPrefix, ...args),
    };
};

export default useLogConsole;
