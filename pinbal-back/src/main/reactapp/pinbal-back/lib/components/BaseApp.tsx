import React from 'react';
import componentsCa from '../i18n/componentsCa';
import componentsEn from '../i18n/componentsEn';
import componentsEs from '../i18n/componentsEs';
import { usePersistentState } from '../util/usePersistentState';
import { FormFieldComponent } from './form/FormField';
import { ResourceApiFormFieldDefault } from './form/FormFieldDefault';
import {
    BaseAppContext,
    MessageDialogShowFn,
    DialogButton,
    TemporalMessageShowFn,
    TemporalMessageSeverity,
    RouterNavigateFunction,
} from './BaseAppContext';
import { DetailFieldCustomProps } from './detail/DetailField';
import { useResourceApiContext } from './ResourceApiContext';

export const MARGIN_UNIT_PX = 8;
export const LIB_I18N_NS = 'reactlib';
export const PERSISTENT_LANGUAGE_KEY = 'lang';
export const PERSISTENT_SESSION_KEY = 'user-session';

type I18nHandleLanguageChangeFn = (lang?: string) => void;
type I18nAddResourceBundleCallback = (lang: string, ns: string, bundle: any) => void;

type ContentComponentSlots = {
    appbar?: React.ReactElement;
    footer?: React.ReactElement;
    menu?: React.ReactElement;
    offline: React.ReactElement;
};

export type BaseAppProps = React.PropsWithChildren & {
    code: string;
    persistentLanguage?: boolean;
    i18nUseTranslation: (ns?: string) => { t: any };
    i18nCurrentLanguage?: string;
    i18nHandleLanguageChange?: I18nHandleLanguageChangeFn;
    i18nAddResourceBundleCallback?: I18nAddResourceBundleCallback;
    routerGoBack: (fallback?: string) => void;
    routerNavigate: RouterNavigateFunction;
    routerAnyHistoryEntryExist: () => boolean;
    routerUseBlocker?: (shouldBlock: boolean | ((args: any) => boolean)) => void;
    routerUseLocationPath: () => string;
    linkComponent: React.ElementType;
    saveAs?: (data: Blob | string, filename?: string) => void;
    formFieldComponents?: FormFieldComponent[];
    detailFieldComponent?: React.FC<DetailFieldCustomProps>;
    contentComponentSlots: ContentComponentSlots;
    fixedContentExpandsToAvailableHeightEnabled?: boolean;
    marginsDisabled?: boolean;
};

export type BaseAppContentComponentProps = React.PropsWithChildren & {
    offline: boolean;
    appReady: boolean;
    marginsDisabled: boolean;
    contentExpandsToAvailableHeight: boolean;
    appbarComponent?: React.ReactElement;
    footerComponent?: React.ReactElement;
    menuComponent?: React.ReactElement;
    offlineComponent?: React.ReactElement;
    legacyMargins?: boolean;
};

const useDialog = () => {
    const dialogShowFn = React.useRef<MessageDialogShowFn>(undefined);
    const setMessageDialogShow = (fn: MessageDialogShowFn) => {
        dialogShowFn.current = fn;
    };
    const messageDialogShow: MessageDialogShowFn = (
        title: string | null,
        message: string | React.ReactElement,
        dialogButtons?: DialogButton[],
        componentProps?: any
    ) => {
        if (dialogShowFn.current) {
            return dialogShowFn.current(title, message, dialogButtons, componentProps);
        } else {
            console.warn('Dialog component not configured in BaseApp');
            return new Promise((_resolve, reject) => reject());
        }
    };
    return {
        setMessageDialogShow,
        messageDialogShow,
    };
};

const useTemporalMessage = () => {
    const temporalMessageShowFn = React.useRef<TemporalMessageShowFn>(undefined);
    const setTemporalMessageShow = (fn: TemporalMessageShowFn) => {
        temporalMessageShowFn.current = fn;
    };
    const temporalMessageShow: TemporalMessageShowFn = (
        title: string | null,
        message: string,
        severity?: TemporalMessageSeverity,
        additionalComponents?: React.ReactElement[]
    ) => {
        if (temporalMessageShowFn.current) {
            temporalMessageShowFn.current(title, message, severity, additionalComponents);
        } else {
            console.warn('Temporal message component not configured in BaseApp');
        }
    };
    return {
        setTemporalMessageShow,
        temporalMessageShow,
    };
};

const useFormFieldComponents = (formFieldComponents?: FormFieldComponent[]) => {
    const formFieldComponentsMap: any = {};
    formFieldComponents?.forEach((ffc) => {
        formFieldComponentsMap[ffc.type] = ffc.component;
    });
    const formFieldComponentsRef = React.useRef<any>(formFieldComponentsMap);
    const getFormFieldComponent = (type?: string) => {
        if (type && formFieldComponentsRef.current && formFieldComponentsRef.current[type]) {
            return formFieldComponentsRef.current[type];
        } else {
            console.warn('Form field type ' + type + ' not found, using default');
            return ResourceApiFormFieldDefault;
        }
    };
    return getFormFieldComponent;
};

const useI18n = (
    code: string,
    persistentLanguage: boolean,
    i18nUseTranslation: (ns?: string) => { t: any },
    i18nCurrentLanguage?: string,
    i18nHandleLanguageChange?: I18nHandleLanguageChangeFn,
    i18nAddResourceBundleCallback?: I18nAddResourceBundleCallback
) => {
    const { persistentStateReady, persistentStateGet, persistentStateSet } =
        usePersistentState(code);
    const { t: tI18Next } = i18nUseTranslation(LIB_I18N_NS);
    const { t: tI18NextGlobal } = i18nUseTranslation();
    const { currentLanguage, setCurrentLanguage } = useResourceApiContext();
    React.useEffect(() => {
        i18nAddResourceBundleCallback?.('ca', LIB_I18N_NS, componentsCa);
        i18nAddResourceBundleCallback?.('es', LIB_I18N_NS, componentsEs);
        i18nAddResourceBundleCallback?.('en', LIB_I18N_NS, componentsEn);
    }, []);
    React.useEffect(() => {
        if (persistentLanguage && persistentStateReady && currentLanguage == null) {
            const lang = persistentStateGet(PERSISTENT_LANGUAGE_KEY);
            setCurrentLanguage(lang ?? '');
        }
    }, [persistentStateReady]);
    React.useEffect(() => {
        if (persistentLanguage && persistentStateReady) {
            persistentStateSet(PERSISTENT_LANGUAGE_KEY, currentLanguage);
        }
    }, [currentLanguage]);
    React.useEffect(() => {
        i18nCurrentLanguage && setCurrentLanguage(i18nCurrentLanguage);
    }, [i18nCurrentLanguage]);
    React.useEffect(() => {
        currentLanguage && i18nHandleLanguageChange?.(currentLanguage);
    }, [currentLanguage]);
    const t = (key: string, params?: any) => {
        const result = tI18Next(key, params);
        if (result === key) {
            return tI18NextGlobal(key, params);
        }
        return result;
    };
    return {
        currentLanguage,
        setCurrentLanguage,
        t,
    };
};

const ContentComponentDefault: React.FC<BaseAppContentComponentProps> = (props) => {
    const {
        offline,
        appReady,
        marginsDisabled,
        contentExpandsToAvailableHeight,
        appbarComponent,
        footerComponent,
        menuComponent,
        offlineComponent,
        children,
    } = props;
    const mainBoxHeight = contentExpandsToAvailableHeight ? '100vh' : undefined;
    const childrenOrOfflineComponent = !offline ? children : offlineComponent;
    return (
        <div
            style={{
                display: 'flex',
                flexDirection: 'column',
                height: mainBoxHeight,
            }}>
            {appbarComponent}
            <div
                style={{
                    display: 'flex',
                    flexGrow: 1,
                    minHeight: 0,
                }}>
                <nav>{menuComponent}</nav>
                <main
                    style={{
                        flexGrow: 1,
                        minWidth: 0,
                        ...(!marginsDisabled ? { margin: '16px 24px' } : null),
                    }}>
                    {appReady ? childrenOrOfflineComponent : null}
                </main>
            </div>
            {footerComponent && <footer>{footerComponent}</footer>}
        </div>
    );
};

const emptyFunction = () => {};

export const BaseApp: React.FC<BaseAppProps> = (props) => {
    const {
        code,
        persistentLanguage,
        i18nUseTranslation,
        i18nCurrentLanguage,
        i18nHandleLanguageChange,
        i18nAddResourceBundleCallback,
        routerGoBack,
        routerNavigate,
        routerUseBlocker,
        routerUseLocationPath,
        routerAnyHistoryEntryExist,
        linkComponent,
        saveAs,
        formFieldComponents,
        detailFieldComponent,
        contentComponentSlots,
        fixedContentExpandsToAvailableHeightEnabled,
        marginsDisabled: marginsDisabledProp,
        children,
    } = props;
    const { offline } = useResourceApiContext();
    const [marginsDisabled, setMarginsDisabled] = React.useState<boolean>(
        marginsDisabledProp ?? false
    );
    const [contentExpandsToAvailableHeight, setContentExpandsToAvailableHeight] =
        React.useState<boolean>(fixedContentExpandsToAvailableHeightEnabled ?? false);
    const getLinkComponent = () => linkComponent;
    const { setMessageDialogShow, messageDialogShow } = useDialog();
    const { setTemporalMessageShow, temporalMessageShow } = useTemporalMessage();
    const getFormFieldComponent = useFormFieldComponents(formFieldComponents);
    const getDetailFieldComponent = (_type?: string) => detailFieldComponent;
    const { currentLanguage, setCurrentLanguage, t } = useI18n(
        code,
        persistentLanguage ?? false,
        i18nUseTranslation,
        i18nCurrentLanguage,
        i18nHandleLanguageChange,
        i18nAddResourceBundleCallback
    );
    const locationPath = routerUseLocationPath();
    const previousLocationPath = React.useRef<string>(locationPath);
    const [topLevelRouteChanged, setTopLevelRouteChanged] = React.useState<boolean>(false);
    React.useEffect(() => {
        const prev = previousLocationPath.current;
        const getTopLevel = (path: string) => path.split('/')[1] || '';
        const topLevelRouteChanged = getTopLevel(prev) !== getTopLevel(locationPath);
        setTopLevelRouteChanged(topLevelRouteChanged);
        previousLocationPath.current = locationPath;
    }, [locationPath]);
    const context = {
        code,
        getFormFieldComponent,
        getDetailFieldComponent,
        setMarginsDisabled: marginsDisabledProp == null ? setMarginsDisabled : emptyFunction,
        contentExpandsToAvailableHeight,
        setContentExpandsToAvailableHeight:
            fixedContentExpandsToAvailableHeightEnabled == null
                ? setContentExpandsToAvailableHeight
                : emptyFunction,
        getLinkComponent,
        goBack: routerGoBack,
        navigate: routerNavigate,
        useBlocker: routerUseBlocker,
        useLocationPath: routerUseLocationPath,
        anyHistoryEntryExist: routerAnyHistoryEntryExist,
        topLevelRouteChanged,
        setMessageDialogShow,
        messageDialogShow,
        setTemporalMessageShow,
        temporalMessageShow,
        currentLanguage,
        setCurrentLanguage,
        t,
        saveAs,
    };
    const languageReady = !persistentLanguage || currentLanguage != null;
    const appReady = languageReady;
    return (
        <BaseAppContext.Provider value={context}>
            <ContentComponentDefault
                offline={offline}
                appReady={appReady}
                marginsDisabled={marginsDisabled}
                contentExpandsToAvailableHeight={contentExpandsToAvailableHeight}
                appbarComponent={contentComponentSlots.appbar}
                footerComponent={contentComponentSlots.footer}
                menuComponent={contentComponentSlots.menu}
                offlineComponent={contentComponentSlots.offline}>
                {children}
            </ContentComponentDefault>
        </BaseAppContext.Provider>
    );
};

export default BaseApp;
