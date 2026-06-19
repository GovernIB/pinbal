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
import { usePinbalContext, ROLE_ADMIN } from './components/PinbalContext';
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

export const getEnvApiUrl = () => {
    const envApiPublicUrl = envVar('VITE_API_PUBLIC_URL', envVars);
    const envApiUrl = envVar('VITE_API_URL', envVars);
    if (envApiPublicUrl || envApiUrl) {
        return envApiPublicUrl ?? envApiUrl;
    } else {
        const envApiBaseUrl = envVar('VITE_API_BASE_URL', envVars);
        const envApiSuffix = envVar('VITE_API_SUFFIX', envVars) ?? '/api';
        if (envApiBaseUrl) {
            return envApiBaseUrl + envApiSuffix;
        } else {
            const port = window.location.port ? ':' + window.location.port : '';
            return (
                window.location.protocol +
                '//' +
                window.location.hostname +
                port +
                envApiSuffix
            );
        }
    }
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
