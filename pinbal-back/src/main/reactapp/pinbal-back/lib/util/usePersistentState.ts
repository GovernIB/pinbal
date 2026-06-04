import React from 'react';
import { useOptionalAuthContext } from '../components/AuthContext';

export type PersistentStateReturned = {
    persistentStateReady: boolean;
    persistentStateGet: (field?: string) => any;
    persistentStateSet: (field: string, value: any) => void;
    persistentStateRemove: (field: string) => void;
};

const generateKey = (appName: string, currentUser: string) => {
    return appName + (currentUser != null ? '_' + currentUser : '') + '_state';
};

const loadPersistentState = (key: string) => {
    const stringState = localStorage.getItem(key);
    return stringState != null ? JSON.parse(stringState) : {};
};

const savePersistentState = (key: string, value: any) => {
    localStorage.setItem(key, JSON.stringify(value));
};

export const usePersistentState: (appName: string) => PersistentStateReturned = (appName) => {
    const persistentStateRef = React.useRef<any>(undefined);
    const [persistentStateReady, setPersistentStateReady] = React.useState<boolean>(false);
    const { isReady: authIsReady, getUserId: authGetUserId } = useOptionalAuthContext() ?? {};
    const key = generateKey(appName, authGetUserId?.());
    React.useEffect(() => {
        if (authIsReady == null || authIsReady) {
            const loaded = loadPersistentState(key);
            persistentStateRef.current = loaded;
            !persistentStateReady && setPersistentStateReady(true);
        }
    }, [authIsReady]);
    const persistentStateGet = (field?: string): any => {
        const state = persistentStateRef.current;
        return field != null ? state?.[field] : state;
    };
    const persistentStateSet = (field: string, value: any) => {
        const state = persistentStateRef.current;
        const newState = { ...state, [field]: value };
        persistentStateRef.current = newState;
        persistentStateReady && savePersistentState(key, newState);
    };
    const persistentStateRemove = (field: string) => {
        const state = persistentStateRef.current;
        const { [field]: _, ...newState } = state;
        persistentStateRef.current = newState;
        persistentStateReady && savePersistentState(key, newState);
        persistentStateReady && savePersistentState(key, newState);
    };
    return {
        persistentStateReady,
        persistentStateGet,
        persistentStateSet,
        persistentStateRemove,
    };
};
