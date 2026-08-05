import React from 'react';
import useLogConsole from '../util/useLogConsole';
import AuthContext from './AuthContext';

const LOG_PREFIX = '[CAUTH]';
const CHECK_TOKEN_TIMEOUT_MARGIN_SECS = 1;

type AuthProviderProps = React.PropsWithChildren & {
    /** La url a carregar després de fer logout */
    logoutUrl: string;
    /** Indica que l'autenticació és obligatòria (no es pot veure res si no s'està autenticat) */
    mandatory?: true;
    /** Indica si s'han d'imprimir a la consola missatges de depuració */
    debug?: true;
};

const parseJwt = (token?: string) => {
    if (token != null) {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(
            atob(base64)
                .split('')
                .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
                .join('')
        );
        return JSON.parse(jsonPayload);
    } else {
        return token;
    }
};

const useTokenWatchTimeout = (callback: () => void, delay: number = 1000) => {
    let timeoutId: any;
    const start = (newDelay?: number) => {
        clearTimeout(timeoutId);
        timeoutId = setTimeout(callback, newDelay ?? delay);
    };
    const refresh = (newDelay?: number) => start(newDelay);
    const stop = () => clearTimeout(timeoutId);
    return {
        refresh,
        stop,
    };
};

export const AuthProvider = (props: AuthProviderProps) => {
    const { logoutUrl, mandatory, debug, children } = props;
    const [loading, setLoading] = React.useState<boolean>(true);
    const tokenRef = React.useRef<string>(undefined);
    const tokenParsedRef = React.useRef<any>(undefined);
    const logConsole = useLogConsole(LOG_PREFIX);
    const isAuthenticated = !loading && tokenRef.current != null;
    const authSrc = document.head.getElementsByTagName('script')[2].src;
    // NO apuntis a un "/logout" derivat genèricament (sibling de "/authToken"): aquesta app no en
    // mapeja cap i acaba caient en el comportament per defecte de l'adaptador Keycloak de JBoss, que
    // no fa el flux complet (id_token_hint, end_session_endpoint per descobriment) i redirigeix a un
    // "/login?logout" que tampoc existeix (404). Cal apuntar al mateix endpoint que ja fa servir amb
    // èxit l'enllaç "Desconnectar" de la interfície JSP clàssica (AuthController.logout()).
    // "origen=react" fa que, un cop Keycloak acabi el logout, el navegador torni a la SPA React en
    // lloc de l'arrel de la interfície JSP (comportament per defecte quan no s'indica cap origen).
    const signOutUrl = authSrc.replace('/authToken', '/usuari/logout') + '?origen=react';
    const checkToken = () => {
        debug && logConsole.debug('Verificació del token iniciada');
        getToken()
            .then((token) => {
                if (token) {
                    setToken(token, true);
                } else {
                    debug && logConsole.debug('La verificació del token ha retornat <null>');
                    window.location.href = logoutUrl;
                }
            })
            .catch((error) => {
                logConsole.error('Error al verificar el token:', JSON.stringify(error));
                window.location.href = logoutUrl;
            });
    };
    const { refresh: checkTokenRefresh, stop: checkTokenStop } = useTokenWatchTimeout(checkToken);
    const getToken = async () => {
        const response = await fetch(authSrc);
        const text = await response.text();
        const match = text.match(/window\.__AUTH_TOKEN__\s*=\s*'([^']+)'/);
        return match ? match[1] : null;
    };
    const setToken = (token: string, verified?: boolean) => {
        tokenRef.current = token;
        const tokenParsed = parseJwt(token);
        tokenParsedRef.current = tokenParsed;
        const checkTokenTimeout =
            (tokenParsed.exp - Date.now() / 1000 + CHECK_TOKEN_TIMEOUT_MARGIN_SECS) * 1000;
        tokenParsed && checkTokenRefresh(checkTokenTimeout);
        setLoading(false);
        debug && logConsole.debug('Token', verified ? 'verificat:' : 'obtingut:', token);
    };
    React.useEffect(() => {
        debug &&
            logConsole.debug(
                "Inicialitzant proveidor d'autenticació de JBoss",
                authSrc,
                signOutUrl,
                logoutUrl
            );
        if ((window as any).__AUTH_TOKEN__) {
            setToken((window as any).__AUTH_TOKEN__);
        } else {
            debug && logConsole.debug("No s'ha trobat el token, programant consulta periòdica");
            const checkTokenInterval = setInterval(() => {
                if ((window as any).__AUTH_TOKEN__) {
                    debug && logConsole.debug('Consultant si el token ja està disponible');
                    setToken((window as any).__AUTH_TOKEN__);
                    clearInterval(checkTokenInterval);
                }
            }, 100);
            setTimeout(() => {
                clearInterval(checkTokenInterval);
                if (!(window as any).__AUTH_TOKEN__) {
                    debug &&
                        logConsole.debug(
                            'La consulta periòdica no ha pogut obtenir el token. El consultarem directament.'
                        );
                    getToken()
                        .then((token) => {
                            token && setToken(token);
                        })
                        .catch((error) => {
                            logConsole.error('Error al obtenir el token directament', error);
                        });
                }
            }, 1000);
        }
        return () => {
            checkTokenStop();
        };
    }, []);
    const signIn = loading ? undefined : () => {};
    const signOut = loading
        ? undefined
        : () => {
              // NO facis un fetch(signOutUrl): el tancament de sessió acaba redirigint a l'endpoint
              // d'autorització de Keycloak (origen diferent), que no exposa capçaleres CORS perquè
              // està pensat per a una navegació real del navegador, no per a ser llegit via JS. Amb
              // fetch(), a més, les cookies de sessió de Keycloak no s'envien mai cap a aquest origen
              // diferent (credentials per defecte "same-origin"), de manera que la sessió SSO de
              // Keycloak no arriba mai a tancar-se encara que la petició no fallés. Cal una navegació
              // real perquè el navegador segueixi tota la cadena de redireccions amb les credencials.
              debug && logConsole.debug('Tancament de sessió');
              window.location.href = signOutUrl;
          };
    const context = {
        isLoading: loading,
        isReady: !loading,
        isAuthenticated,
        bearerTokenActive: false,
        getToken: () => tokenRef.current,
        getTokenParsed: () => tokenParsedRef.current,
        getUserId: () => tokenParsedRef.current?.['preferred_username'],
        getUserName: () => tokenParsedRef.current?.['name'],
        getUserEmail: () => tokenParsedRef.current?.['email'],
        signIn,
        signOut,
    };
    const showChildren = !loading && (!mandatory || (mandatory && isAuthenticated));
    return (
        <AuthContext.Provider value={context}>
            {showChildren ? children : null}
        </AuthContext.Provider>
    );
};
