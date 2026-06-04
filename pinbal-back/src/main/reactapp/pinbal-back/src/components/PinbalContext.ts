import React from 'react';

export const ROLE_PREFIX = 'NOT_';
export const ROLE_ADMIN = ROLE_PREFIX + 'ADMIN';
export const ROLE_REPRES = ROLE_PREFIX + 'REPRES';
export const ROLE_AUDIT = ROLE_PREFIX + 'AUDIT';
export const ROLE_SUPERAUDIT = ROLE_PREFIX + 'SUPERAUDIT';
export const ROLE_USER = 'tothom';

export type NotibContextType = {
    isReady: boolean;
    currentUser: any;
    setCurrentUser: (currentUser: any | undefined) => void;
    currentUserGridPageSizeOptions?: number[];
    rolesAvailable?: string[];
    entitatsAvailable?: any[];
    currentRole?: string;
    setCurrentRole: (currentRole: string | undefined) => void;
    currentEntitatId?: number;
    setCurrentEntitatId: (currentRole: any | undefined) => void;
    currentEntitatLoading?: boolean;
    currentEntitat?: any;
};

export const PinbalContext = React.createContext<NotibContextType | undefined>(undefined);

export const usePinbalContext = () => {
    const context = React.useContext(PinbalContext);
    if (context === undefined) {
        throw new Error('usePinbalContext must be used within a ResourceApi provider');
    }
    return context;
};
