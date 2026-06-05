import React from 'react';
import {
    useNavigate,
    useLocation,
    useBlocker,
    Link as RouterLink,
    type LinkProps as RouterLinkProps,
} from 'react-router-dom';
import { saveAs } from 'file-saver';
import i18n from '../i18n/i18n';
import { useTranslation } from 'react-i18next';
import Box from '@mui/material/Box';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import 'dayjs/locale/ca';
import 'dayjs/locale/es';
import {
    MuiBaseApp,
    type MenuEntry,
    useBaseAppContext,
    useResourceApiContext,
    useMuiFormDialogApiRef,
} from 'reactlib';
import { usePinbalContext, ROLE_ADMIN } from './PinbalContext.ts';
import Offline from './Offline';
import RoleSelector from './RoleSelector';
import EntitatSelector from './EntitatSelector';
import { UserProfileMenu, UserProfileFormDialog } from './UserProfile';

export type MenuEntryWithResource = MenuEntry & {
    resourceName?: string;
    hidden?: boolean | (() => boolean);
    children?: MenuEntryWithResource[];
};

export type HeaderBackgroundModuleItem = {
    color?: string;
    image?: string;
};

export type BaseAppProps = React.PropsWithChildren & {
    code: string;
    logo?: string;
    logoStyle?: any;
    title?: string | React.ReactElement;
    title_logo?: string;
    version: string;
    availableLanguages?: string[];
    menuEntries?: MenuEntryWithResource[];
    appbarBackgroundColor?: string;
    appbarBackgroundImg?: string;
    appbarStyle?: any;
    footer?: React.ReactElement;
    footerHeight?: number;
};

export const Link = React.forwardRef<HTMLAnchorElement, RouterLinkProps>((itemProps, ref) => {
    return <RouterLink ref={ref} {...itemProps} role={undefined} />;
});

const filterMenuEntries = (
    menuEntries: MenuEntryWithResource[] | undefined,
    resourceNames?: string[]
): MenuEntry[] | undefined => {
    return menuEntries
        ?.map((e) => {
            const filteredChildren = filterMenuEntries(e.children, resourceNames);
            const passesResource =
                e.resourceName == null || resourceNames?.includes(e.resourceName);
            const passesHidden = e.hidden == null || !e.hidden;
            if (!passesResource || !passesHidden) {
                return filteredChildren && filteredChildren.length > 0
                    ? { ...e, children: filteredChildren }
                    : null;
            }
            const { resourceName, ...otherProps } = e;
            return {
                ...otherProps,
                ...(filteredChildren ? { children: filteredChildren } : {}),
            };
        })
        .filter((e): e is MenuEntry => e !== null);
};

const useBaseAppMenuEntries = (menuEntries?: MenuEntryWithResource[]) => {
    const { isReady: apiIsReady, indexState: apiIndex } = useResourceApiContext();
    return React.useMemo(() => {
        if (apiIsReady) {
            const apiLinks = apiIndex?.links.getAll();
            const resourceNames = apiLinks?.map((l: any) => l.rel);
            return filterMenuEntries(menuEntries, resourceNames);
        } else {
            return [];
        }
    }, [apiIsReady, apiIndex]);
};

const useLocationPath = () => {
    const location = useLocation();
    return location.pathname;
};

const CustomLocalizationProvider = ({ children }: React.PropsWithChildren) => {
    const { currentLanguage } = useBaseAppContext();
    const adapterLocale = React.useMemo(() => {
        const languageTwoChars = currentLanguage?.substring(0, 2).toLowerCase();
        switch (languageTwoChars) {
            case 'ca':
            case 'es':
            case 'en':
                return languageTwoChars;
            default:
                return 'ca';
        }
    }, [currentLanguage]);
    const adapter = AdapterDayjs;
    return (
        <LocalizationProvider dateAdapter={adapter} adapterLocale={adapterLocale}>
            {children}
        </LocalizationProvider>
    );
};

export const BaseApp: React.FC<BaseAppProps> = (props) => {
    const {
        code,
        logo,
        logoStyle,
        title,
        version,
        menuEntries,
        appbarBackgroundColor,
        appbarBackgroundImg,
        appbarStyle,
        footer,
        footerHeight,
        children,
    } = props;
    const navigate = useNavigate();
    const location = useLocation();
    const { currentRole } = usePinbalContext();
    const baseAppMenuEntries = useBaseAppMenuEntries(menuEntries);
    const formDialogApiRef = useMuiFormDialogApiRef();
    const i18nHandleLanguageChange = (language?: string) => {
        i18n.changeLanguage(language);
    };
    const i18nAddResourceBundleCallback = (language: string, namespace: string, bundle: any) => {
        i18n.addResourceBundle(language, namespace, bundle);
    };
    const anyHistoryEntryExist = () => location.key !== 'default';
    const goBack = (fallback?: string) => {
        if (anyHistoryEntryExist()) {
            navigate(-1);
        } else if (fallback != null) {
            navigate(fallback);
        } else {
            console.warn(
                "[BACK] Couldn't go back, neither fallback specified nor previous entry exists in navigation history"
            );
        }
    };
    return (
        <MuiBaseApp
            code={code}
            headerTitle={title}
            headerLogo={logo}
            headerLogoStyle={logoStyle}
            headerVersion={version}
            headerAppbarStyle={appbarStyle}
            headerAppbarBackgroundColor={appbarBackgroundColor}
            headerAppbarBackgroundImg={appbarBackgroundImg}
            headerAdditionalComponents={[
                <RoleSelector key="role_selector" />,
                ...(currentRole !== ROLE_ADMIN ? [<EntitatSelector key="entitat_selector" />] : []),
            ]}
            headerAdditionalAuthComponents={[
                <Box key="user_profile" sx={{ display: 'flex', justifyContent: 'center', mb: 1 }}>
                    <UserProfileMenu formDialogApiRef={formDialogApiRef} />
                </Box>,
            ]}
            offline={<Offline />}
            footer={footer}
            footerHeight={footerHeight}
            persistentLanguage
            i18nUseTranslation={useTranslation}
            i18nCurrentLanguage={i18n.language}
            i18nHandleLanguageChange={i18nHandleLanguageChange}
            i18nAddResourceBundleCallback={i18nAddResourceBundleCallback}
            routerGoBack={goBack}
            routerNavigate={navigate}
            routerUseBlocker={useBlocker}
            routerUseLocationPath={useLocationPath}
            routerAnyHistoryEntryExist={anyHistoryEntryExist}
            linkComponent={Link}
            saveAs={saveAs}
            menuEntries={baseAppMenuEntries}
        >
            <CustomLocalizationProvider>
                <UserProfileFormDialog formDialogApiRef={formDialogApiRef} />
                {children}
            </CustomLocalizationProvider>
        </MuiBaseApp>
    );
};

export default BaseApp;
