import { useTranslation } from 'react-i18next';
import { Outlet } from 'react-router-dom';
import Alert from '@mui/material/Alert';
import { CssBaseline } from '@mui/material';
import { ThemeProvider, useTheme, useColorScheme } from '@mui/material/styles';
import { envVar, OidcAuthProvider, ContainerAuthProvider, ResourceApiProvider } from 'reactlib';
import goibLogoLight from './assets/goib_logo_light.svg';
import goibLogoDark from './assets/goib_logo_dark.svg';
import pinbalLogoLight from './assets/pinbal_logo_light.png';
import pinbalLogoDark from './assets/pinbal_logo_dark.png';
import { BaseApp } from './components/BaseApp';
import DrassanaFooter from './components/DrassanaFooter';
import PinbalProvider from './components/PinbalProvider';
import { usePinbalContext, ROLE_ADMIN, ROLE_REPRES } from './components/PinbalContext';
import theme from './theme';

export const envVars = {
    VITE_API_URL: import.meta.env.VITE_API_URL,
    VITE_API_PUBLIC_URL: import.meta.env.VITE_API_PUBLIC_URL,
    VITE_API_BASE_URL: import.meta.env.VITE_API_BASE_URL,
    VITE_API_SUFFIX: import.meta.env.VITE_API_SUFFIX,
    VITE_AUTH_URL: import.meta.env.VITE_AUTH_URL,
    VITE_AUTH_REALM: import.meta.env.VITE_AUTH_REALM,
    VITE_AUTH_CLIENTID: import.meta.env.VITE_AUTH_CLIENTID,
    VITE_APP_VERSION: import.meta.env.VITE_APP_VERSION,
};

const getAuthConfig = () => ({
    url: envVar('VITE_AUTH_URL', envVars),
    realm: envVar('VITE_AUTH_REALM', envVars),
    clientId: envVar('VITE_AUTH_CLIENTID', envVars),
});

// Context path del backoffice (el fixa el build Maven a partir de pinbal.context.back)
const contextPath = import.meta.env.VITE_CONTEXT_PATH || '/pinbalback';
// API del backoffice que es fa servir si no s'ha configurat es.caib.pinbal.front.api.url
const DEFAULT_API_PATH = contextPath + '/apinew/';

export const getEnvApiUrl = () => {
    const envApiPublicUrl = envVar('VITE_API_PUBLIC_URL', envVars);
    const envApiUrl = envVar('VITE_API_URL', envVars);
    if (envApiPublicUrl || envApiUrl) {
        // "||" i no "??": una propietat definida però buida s'ha de considerar no configurada
        return envApiPublicUrl || envApiUrl;
    }
    const envApiBaseUrl = envVar('VITE_API_BASE_URL', envVars);
    if (envApiBaseUrl) {
        return envApiBaseUrl + (envVar('VITE_API_SUFFIX', envVars) || '/api');
    }
    // Sense configuració: mateix origen que l'aplicació (protocol, host i port del navegador,
    // vàlid també darrere d'un proxy invers). Ha de ser una URL absoluta (ResourceApiProvider
    // fa new URL(apiUrl)).
    return window.location.origin + (envVar('VITE_API_SUFFIX', envVars) || DEFAULT_API_PATH);
};

const isAuthUrlPresent = envVar('VITE_AUTH_URL', envVars) != null;
const AuthProvider = isAuthUrlPresent ? OidcAuthProvider : ContainerAuthProvider;
const version = '0.0.0';

const InnerApp: React.FC = () => {
    const { t } = useTranslation();
    const { mode } = useColorScheme();
    const { currentRole, currentEntitatId } = usePinbalContext();

    const menuEntries = [
        {
            id: 'home',
            title: t('app.menu.home'),
            to: 'home',
            icon: 'home',
        },
        // La mateixa pantalla de consultes serveix per a administrador i delegat.
        // El menú només es mostra quan l'usuari és admin o té una entitat seleccionada.
        {
            id: 'consulta',
            title: t('app.menu.consulta'),
            to: 'consulta',
            icon: 'search',
        },
        // Manteniment d'entitats i serveis: només administradors (equivalent al menú JSP).
        ...(currentRole === ROLE_ADMIN
            ? [
                  {
                      id: 'entitats',
                      title: t('app.menu.entitats'),
                      to: 'entitats',
                      icon: 'domain',
                  },
                  {
                      id: 'serveis',
                      title: t('app.menu.serveis'),
                      to: 'serveis',
                      icon: 'miscellaneous_services',
                  },
              ]
            : []),
        // Procediments i òrgans gestors: administradors i representants (equivalent al menú JSP).
        ...(currentRole === ROLE_ADMIN || currentRole === ROLE_REPRES
            ? [
                  {
                      id: 'procediments',
                      title: t('app.menu.procediments'),
                      to: 'procediments',
                      icon: 'assignment',
                  },
                  {
                      id: 'organgestors',
                      title: t('app.menu.organGestors'),
                      to: 'organgestors',
                      icon: 'account_tree',
                  },
              ]
            : []),
        // Menú "Configurar": només administrador (equivalent al menú desplegable JSP menu_adm_config).
        ...(currentRole === ROLE_ADMIN
            ? [
                  {
                      id: 'configurar',
                      title: t('app.menu.configurar'),
                      icon: 'settings',
                      children: [
                          {
                              id: 'propietats',
                              title: t('app.menu.propietats'),
                              to: 'propietats',
                              icon: 'tune',
                          },
                          {
                              id: 'caches',
                              title: t('app.menu.caches'),
                              to: 'caches',
                              icon: 'memory',
                          },
                          {
                              id: 'avisos',
                              title: t('app.menu.avisos'),
                              to: 'avisos',
                              icon: 'campaign',
                          },
                          {
                              id: 'paramconfs',
                              title: t('app.menu.paramConfs'),
                              to: 'paramconfs',
                              icon: 'settings_applications',
                          },
                          {
                              id: 'emissorcerts',
                              title: t('app.menu.emissorCerts'),
                              to: 'emissorcerts',
                              icon: 'verified_user',
                          },
                          {
                              id: 'claupubliques',
                              title: t('app.menu.clauPubliques'),
                              to: 'claupubliques',
                              icon: 'vpn_key',
                          },
                          {
                              id: 'clauprivades',
                              title: t('app.menu.clauPrivades'),
                              to: 'clauprivades',
                              icon: 'key',
                          },
                      ],
                  },
              ]
            : []),
    ];
    const theme = useTheme();
    const bgColor = mode === 'light' ? theme.palette.background.paper : undefined;
    const textColor = bgColor ? theme.palette.getContrastText(bgColor) : undefined;
    const currentRoleAdminOrEntitatSelected = currentRole === ROLE_ADMIN || currentEntitatId != null;
    return (
        mode && (
            <BaseApp
                code="PINBAL"
                logo={mode === 'light' ? goibLogoLight : goibLogoDark}
                logoStyle={{
                    '& img': { height: '49px' },
                    pl: 1,
                    pr: '29px',
                    borderRight: '1px solid ' + theme.palette.divider,
                }}
                title={
                    <img
                        style={{
                            marginLeft: '8px',
                            height: '49px',
                            verticalAlign: 'middle',
                        }}
                        src={mode === 'light' ? pinbalLogoLight : pinbalLogoDark}
                        alt="Pinbal"
                    />
                }
                version={version}
                availableLanguages={['ca', 'es']}
                menuEntries={currentRoleAdminOrEntitatSelected ? menuEntries : undefined}
                appbarBackgroundColor={bgColor}
                appbarStyle={{ color: textColor }}
                footer={
                    <div style={{ height: '36px' }}>
                        <DrassanaFooter
                            title="PINBAL"
                            backgroundColor="#5F5D5D"
                            style={{ position: 'fixed', width: '100%', bottom: 0 }}
                        />
                    </div>
                }
                footerHeight={36}
            >
                {currentRoleAdminOrEntitatSelected ? (
                    <Outlet />
                ) : (
                    <Alert severity="error">{t('app.noEntitat')}</Alert>
                )}
            </BaseApp>
        )
    );
};

export const App = () => {
    const authConfig = getAuthConfig();
    return (
        <AuthProvider
            appBaseUrl={import.meta.env.BASE_URL}
            logoutUrl={import.meta.env.BASE_URL}
            config={authConfig}
            mandatory
        >
            <ResourceApiProvider apiUrl={getEnvApiUrl()}>
                <ThemeProvider theme={theme}>
                    <CssBaseline />
                    <PinbalProvider>
                        <InnerApp />
                    </PinbalProvider>
                </ThemeProvider>
            </ResourceApiProvider>
        </AuthProvider>
    );
};

export default App;
