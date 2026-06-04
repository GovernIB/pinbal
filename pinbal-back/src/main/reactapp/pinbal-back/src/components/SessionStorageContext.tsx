import React, {createContext, useContext, useState, useEffect, useRef, useCallback} from 'react';

type SessionData = Record<string, any>;

interface SessionStorageContextProps {
    data: SessionData;
    setValue: (key: string, value: any) => void;
    removeValue: (key: string) => void;
}

const SessionStorageContext = createContext<SessionStorageContextProps | undefined>(undefined);

export const SessionStorageProvider = ({ children }: { children: React.ReactNode }) => {
    const [data, setData] = useState<SessionData>(() => {
        const allKeys = Object.keys(sessionStorage);
        const initialData: SessionData = {};
        allKeys.forEach((key) => {
            try {
                initialData[key] = JSON.parse(sessionStorage.getItem(key)!);
            } catch {
                initialData[key] = sessionStorage.getItem(key);
            }
        });
        return initialData;
    });

    const setValue = (key: string, value: any) => {
        setData((prev) => {
            if (prev[key] === value) return prev; // no cambia → evita re-render
            sessionStorage.setItem(key, JSON.stringify(value));
            return { ...prev, [key]: value };
        });
    };

    const removeValue = (key: string) => {
        sessionStorage.removeItem(key);
        setData((prev) => {
            const newData = { ...prev };
            delete newData[key];
            return newData;
        });
    };

    // Detectar cambios externos al sessionStorage (ej: otras pestañas)
    useEffect(() => {
        const handleStorage = (e: StorageEvent) => {
            if (e.storageArea === sessionStorage && e.key) {
                const newValue = e.newValue ? JSON.parse(e.newValue) : null;
                setData((prev) => {
                    if (prev[e.key!] === newValue) return prev; // No cambiar → evita re-render
                    return { ...prev, [e.key!]: newValue };
                });
            }
        };
        window.addEventListener('storage', handleStorage);
        return () => window.removeEventListener('storage', handleStorage);
    }, []);

    return (
        <SessionStorageContext.Provider value={{ data, setValue, removeValue }}>
            <SessionProvider>
                {children}
            </SessionProvider>
        </SessionStorageContext.Provider>
    );
};

const useSessionStorage = () => {
    const ctx = useContext(SessionStorageContext);
    if (!ctx) throw new Error('useSessionStorage debe usarse dentro de <SessionStorageProvider>');
    return ctx;
};

const initialized :Map<string, boolean> = new Map();
export const useSession = (key:string) => {
    const {data, setValue, removeValue} = useSessionStorage();
    return {
        value: data[key],
        isInitialized: () => !!initialized.get(key) || !!data[key],
        save: (val:any) => {
            initialized.set(key, !!val);
            setValue(key, val)
        },
        remove: () => {
            initialized.set(key, false);
            removeValue(key)
        },
    };
};
export const useSessionList = (key:string) => {
    const { value: container, save, remove } = useSession(key);
    const containerRef = useRef<any[]>(container ?? []);

    const listSave = (key:string, newValue:any) => {
        containerRef.current = {
            ...containerRef.current,
            [key]: newValue
        };
        save(containerRef.current)
    }

    return {
        container,
        get: (key:string)=> container?.[key],
        save: listSave,
        remove: (key:string) => listSave(key, undefined),
        removeAll: () => {
            if (containerRef?.current) {
                containerRef.current = [];
            }
            remove();
        }
    }
}

type SessionMap = Map<string, any>;

type SessionContextType = {
    sessionMap: SessionMap;
    setSessionByKey: (key: string, session: any) => void;
    clearSessionByKey: (key: string) => void;
};

const SessionContext = createContext<SessionContextType | undefined>(undefined);

export const SessionProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const [sessionMap, setSessionMap] = useState<SessionMap>(new Map());

    const setSessionByKey = (key: string, session: any) => {
        setSessionMap((prev) => {
            const newMap = new Map(prev);
            newMap.set(key, session);
            return newMap;
        });
    };

    const clearSessionByKey = (key: string) => {
        setSessionMap((prev) => {
            const newMap = new Map(prev);
            newMap.delete(key);
            return newMap;
        });
    };

    return (
        <SessionContext.Provider value={{ sessionMap, setSessionByKey, clearSessionByKey }}>
            {children}
        </SessionContext.Provider>
    );
};

export const useSessionContext = (key: string) => {
    const context = useContext(SessionContext);
    if (!context) throw new Error('useSessionContext debe usarse dentro de <SessionProvider>');

    const { sessionMap, setSessionByKey, clearSessionByKey } = context;

    const value = sessionMap.get(key);

    const save = useCallback(
        (newValue: any) => {
            setSessionByKey(key, newValue);
        },
        [key, setSessionByKey]
    );

    const clear = useCallback(() => {
        clearSessionByKey(key);
    }, [key, clearSessionByKey]);

    return { value, save, clear };
};