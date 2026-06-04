import React from 'react';
import { useTranslation } from 'react-i18next';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import CircularProgress from '@mui/material/CircularProgress';
import { useAuthContext, useResourceApiContext, useResourceApiService } from 'reactlib';
import {
    PinbalContext,
    ROLE_PREFIX,
    ROLE_ADMIN,
    ROLE_REPRES,
    ROLE_AUDIT,
    ROLE_SUPERAUDIT,
    ROLE_USER,
} from './PinbalContext.ts';

const ALLOWED_ROLES = [
    ROLE_ADMIN,
    ROLE_REPRES,
    ROLE_AUDIT,
    ROLE_SUPERAUDIT,
    ROLE_USER,
].reverse();

const decodeJwt = (token: string) => {
    const payload = token.split('.')[1];
    const base64 = payload.replace(/-/g, '+').replace(/_/g, '/');
    return JSON.parse(atob(base64));
};

const useSessionStorage = (...keyParts: any[]) => {
    const key = keyParts
        .map((p) => (typeof p === 'object' && p !== null ? JSON.stringify(p) : String(p)))
        .join('|');
    const getValue = () => {
        return sessionStorage.getItem(key);
    };
    const setValue = (value: string | null) => {
        if (value != null) {
            sessionStorage.setItem(key, value);
        } else {
            sessionStorage.removeItem(key);
        }
    };
    return {
        getValue,
        setValue,
    };
};

const createSession = (entitatId?: number, organId?: number) => {
    const sessionObject = {
        ...(entitatId != null && { e: entitatId }),
        ...(organId != null && { o: organId }),
    };
    return JSON.stringify(sessionObject);
};
const getSessionValue = (json: string | undefined, field: string) => {
    if (json != null) {
        const parsed = JSON.parse(json);
        return parsed[field];
    }
};

const useCurrentUser = () => {
    const {
        isReady: apiIsReady,
        find: apiFind,
        currentFields: apiFields,
    } = useResourceApiService('usuariResource');
    const [currentUser, setCurrentUser] = React.useState<string>();
    const [currentUserGridPageSizeOptions, setCurrentUserGridPageSizeOptions] =
        React.useState<number[]>();
    React.useEffect(() => {
        if (apiIsReady) {
            apiFind({ unpaged: true }).then((response) => {
                if (response.rows.length) {
                    setCurrentUser(response.rows[0]);
                }
            });
            const gridPageSizeOptionsField = apiFields?.find(
                (f) => f.name === 'numElementsPaginaDefecte'
            );
            const gridPageSizeOptions =
                gridPageSizeOptionsField != null
                    ? Object.values(gridPageSizeOptionsField?.options).map((v: any) => parseInt(v))
                    : [10, 20, 50, 100];
            if (!gridPageSizeOptions.includes(-1)) {
                gridPageSizeOptions.unshift(-1);
            }
            setCurrentUserGridPageSizeOptions(gridPageSizeOptions);
        }
    }, [apiIsReady]);
    return { currentUser, setCurrentUser, currentUserGridPageSizeOptions };
};

const useCurrentRole = () => {
    const {
        isReady: authIsReady,
        getUserId: authGetUserId,
        getToken: authGetToken,
    } = useAuthContext();
    const { httpHeaders: apiHttpHeaders, setHttpHeaders: apiSetHttpHeaders } =
        useResourceApiContext();
    const [currentUserId, setCurrentUserId] = React.useState<string>();
    const [rolesAvailable, setRolesAvailable] = React.useState<string[]>();
    const [currentRole, setCurrentRole] = React.useState<string>();
    const { getValue: roleSessionGetValue, setValue: roleSessionSetValue } = useSessionStorage(
        currentUserId,
        'currentRole'
    );
    React.useEffect(() => {
        // Obté els rols disponibles del token JWT o de __AUTH_ROLES__
        if (authIsReady) {
            const userId = authGetUserId();
            setCurrentUserId(userId);
            const token = authGetToken();
            if (token != null) {
                const tokenDecoded = decodeJwt(token);
                if (tokenDecoded.realm_access != null) {
                    const realmRoles =
                        tokenDecoded.realm_access?.roles?.filter(
                            (r: string) => r === ROLE_USER || r.startsWith(ROLE_PREFIX)
                        ) ?? [];
                    const rolesAvailable = ALLOWED_ROLES.filter((a) => realmRoles.includes(a));
                    setRolesAvailable(rolesAvailable);
                } else {
                    const windowAuthRoles = (window as any).__AUTH_ROLES__ ?? [];
                    const rolesAvailable = ALLOWED_ROLES.filter((a) => windowAuthRoles.includes(a));
                    setRolesAvailable(rolesAvailable);
                }
            }
        }
    }, [authIsReady]);
    React.useEffect(() => {
        // Configura l'estat amb el rol actual si aquest encara no s'ha inicialitzat i els rols disponibles ja s'han obtingut
        if (rolesAvailable != null && currentRole == null) {
            const sessionValue = roleSessionGetValue();
            const isSessionValueInRolesAvailable =
                sessionValue != null && rolesAvailable?.includes(sessionValue);
            if (sessionValue != null && isSessionValueInRolesAvailable) {
                setCurrentRole(sessionValue);
            } else if (rolesAvailable?.length && currentRole == null) {
                setCurrentRole(rolesAvailable[0]);
            }
        }
    }, [rolesAvailable, currentRole]);
    React.useEffect(() => {
        // Configura el session storage i la capçalera HTTP amb el rol actual quan aquest canvia
        if (currentRole !== undefined) {
            roleSessionSetValue(currentRole);
            if (currentRole) {
                apiSetHttpHeaders([{ 'X-App-Role': currentRole }]);
            }
        }
    }, [currentRole]);
    const currentRoleFromHttpHeader = apiHttpHeaders?.find((h) => 'X-App-Role' in h)?.[
        'X-App-Role'
    ];
    const roleHttpHeaderInitialized =
        currentRole != null && currentRole === currentRoleFromHttpHeader;
    return {
        currentUserId,
        currentRole,
        currentRoleReady: roleHttpHeaderInitialized,
        rolesAvailable,
        setCurrentRole,
    };
};

const useCurrentEntitat = (
    currentUserId: string | undefined,
    currentRole: string | undefined,
    currentRoleReady: boolean
) => {
    const { httpHeaders: apiHttpHeaders, setHttpHeaders: apiSetHttpHeaders } =
        useResourceApiContext();
    const {
        isReady: apiIsReady,
        find: apiFind,
        getOne: apiGetOne,
    } = useResourceApiService('entitatResource');
    const [entitatsAvailable, setEntitatsAvailable] = React.useState<any[]>();
    const [currentEntitatId, setCurrentEntitatId] = React.useState<number>();
    const [currentEntitatLoading, setCurrentEntitatLoading] = React.useState<boolean>();
    const [currentEntitat, setCurrentEntitat] = React.useState<any>();
    const { getValue: sessionSessionGetValue, setValue: sessionSessionSetValue } =
        useSessionStorage(currentUserId, 'currentSession');
    React.useEffect(() => {
        if (apiIsReady && currentRoleReady && currentRole != null) {
            setCurrentEntitatId(undefined);
            if (currentRole !== ROLE_ADMIN) {
                apiFind({ unpaged: true }).then((response) => {
                    const entitatsAvailable = response.rows;
                    setEntitatsAvailable(entitatsAvailable);
                    const sessionValue = getSessionValue(
                        sessionSessionGetValue() ?? undefined,
                        'e'
                    );
                    const isSessionValueInEntitatsAvailable = entitatsAvailable
                        .map((e) => e.id)
                        .includes(sessionValue);
                    if (isSessionValueInEntitatsAvailable) {
                        setCurrentEntitatId(sessionValue);
                    } else if (entitatsAvailable?.length && currentEntitatId == null) {
                        setCurrentEntitatId(entitatsAvailable[0].id);
                    }
                });
            } else {
                setEntitatsAvailable([]);
            }
        }
    }, [apiIsReady, currentRoleReady, currentRole]);
    React.useEffect(() => {
        if (currentRole != null && currentRole !== ROLE_ADMIN && currentEntitatId != null) {
            const session = createSession(currentEntitatId);
            sessionSessionSetValue(session);
            if (currentRole) {
                apiSetHttpHeaders([
                    {
                        'X-App-Role': currentRole,
                    },
                    {
                        'X-App-Session': session,
                    },
                ]);
            }
        }
    }, [currentRole, currentEntitatId]);
    React.useEffect(() => {
        if (currentEntitatId != null && apiIsReady) {
            setCurrentEntitatLoading(true);
            apiGetOne(currentEntitatId, { perspectives: ['PERMISSIONS'] })
                .then(setCurrentEntitat)
                .finally(() => setCurrentEntitatLoading(false));
        }
    }, [currentEntitatId]);
    const currentSessionFromHttpHeader = apiHttpHeaders?.find((h) => 'X-App-Session' in h)?.[
        'X-App-Session'
    ];
    const currentEntitatIdFromHttpHeader =
        currentSessionFromHttpHeader != null
            ? JSON.parse(currentSessionFromHttpHeader).e
            : undefined;
    const entitatIdHttpHeaderInitialized =
        currentRole === ROLE_ADMIN ||
        (currentEntitatId == null && currentEntitatIdFromHttpHeader == null) ||
        currentEntitatId === currentEntitatIdFromHttpHeader;
    const currentEntitatReady =
        apiIsReady && entitatsAvailable != null && entitatIdHttpHeaderInitialized;
    return {
        currentEntitatId,
        currentEntitatReady,
        currentEntitat,
        currentEntitatLoading,
        entitatsAvailable,
        setCurrentEntitatId,
    };
};

const NotibProviderLoading: React.FC = () => {
    const { t } = useTranslation();
    return (
        <Box
            sx={{
                display: 'flex',
                flexDirection: 'column',
                justifyContent: 'center',
                alignItems: 'center',
                height: '100vh',
            }}
        >
            <CircularProgress size={70} />
            <Typography sx={{ mt: 1 }}>{t('app.loading')}</Typography>
        </Box>
    );
};

export const PinbalProvider: React.FC<React.PropsWithChildren> = ({ children }) => {
    const { offline: apiOffline } = useResourceApiContext();
    const { currentUserId, currentRole, currentRoleReady, rolesAvailable, setCurrentRole } =
        useCurrentRole();
    const { currentUser, setCurrentUser, currentUserGridPageSizeOptions } = useCurrentUser();
    const {
        currentEntitatId,
        currentEntitatReady,
        currentEntitat,
        currentEntitatLoading,
        entitatsAvailable,
        setCurrentEntitatId,
    } = useCurrentEntitat(currentUserId, currentRole, currentRoleReady);
    const isReady = apiOffline || (currentRoleReady && currentEntitatReady && currentUser != null);
    const contextValue = {
        isReady,
        currentUser,
        setCurrentUser,
        currentUserGridPageSizeOptions,
        rolesAvailable,
        entitatsAvailable,
        currentRole,
        setCurrentRole,
        currentEntitatId,
        setCurrentEntitatId,
        currentEntitat,
        currentEntitatLoading,
    };
    return (
        <PinbalContext.Provider value={contextValue}>
            {isReady ? children : <NotibProviderLoading />}
        </PinbalContext.Provider>
    );
};

export default PinbalProvider;
