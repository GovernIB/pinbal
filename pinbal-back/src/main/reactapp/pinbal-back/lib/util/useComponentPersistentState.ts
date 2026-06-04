import React from 'react';
import { useOptionalAuthContext } from '../components/AuthContext';

export type PersistentStateGetStateFn = () => any;

const generateKey = (
    keyPrefix: string,
    locationPathname: string,
    resourceName: string,
    currentUserId?: string
) => {
    return (
        keyPrefix +
        (currentUserId ? '_' + currentUserId : '') +
        ('_' + resourceName) +
        locationPathname.replace('/', '_')
    );
};

const sessionStorageLoad = (key: string) => {
    const state = sessionStorage.getItem(key);
    const parsedState = state != null ? JSON.parse(state) : state;
    return parsedState;
};

const sessionStorageSave = (key: string, value: any) => {
    sessionStorage.setItem(key, JSON.stringify(value));
};

export const useSessionComponentPersistentState = (
    keyPrefix: string,
    resourceName: string,
    getState: PersistentStateGetStateFn
) => {
    const { isReady: authIsReady, getUserId: authGetUserId } = useOptionalAuthContext() ?? {};
    const [state, setState] = React.useState<any>();
    const locationPathname = location.pathname;
    React.useLayoutEffect(() => {
        if (authIsReady) {
            const stateKey = generateKey(
                keyPrefix,
                locationPathname,
                resourceName,
                authGetUserId?.()
            );
            if (state === undefined) {
                const loadedState = sessionStorageLoad(stateKey);
                setState(loadedState ?? null);
            }
            if (state != null || state === null) {
                const beforeunloadListener = () => sessionStorageSave(stateKey, getState());
                window.addEventListener('beforeunload', beforeunloadListener);
                return () => {
                    window.removeEventListener('beforeunload', beforeunloadListener);
                    beforeunloadListener();
                };
            }
        }
    }, [authIsReady, state]);
    const isReady = state !== undefined;
    return { state, isReady };
};
