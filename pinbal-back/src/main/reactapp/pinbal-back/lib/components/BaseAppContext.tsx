import React from 'react';
import { FormFieldCustomProps } from './form/FormField';
import { DetailFieldCustomProps } from './detail/DetailField';

export interface RouterNavigateFunction {
    (to: any, options?: any): void;
    (delta: number): void;
}

export type TemporalMessageSeverity = 'success' | 'info' | 'warning' | 'error';
export type TemporalMessageShowFn = (
    title: string | null,
    message: string,
    severity?: TemporalMessageSeverity,
    additionalComponents?: React.ReactElement[]
) => void;

export type DialogVariant = 'text' | 'outlined' | 'contained';
export type ContentDialogShowFn = (
    title: string | null,
    content: React.ReactElement,
    dialogButtons?: DialogButton[],
    componentProps?: any,
    modal?: boolean
) => Promise<any>;
export type MessageDialogShowFn = (
    title: string | null,
    message: string | React.ReactElement,
    dialogButtons?: DialogButton[],
    componentProps?: any,
    modal?: boolean
) => Promise<string>;
export type DialogButton = {
    value: any;
    text: string;
    icon?: string;
    componentProps?: any;
};

export type BaseAppContextType = {
    code: string;
    getFormFieldComponent: (type?: string) => React.FC<FormFieldCustomProps> | undefined;
    getDetailFieldComponent: (type?: string) => React.FC<DetailFieldCustomProps> | undefined;
    setMarginsDisabled: (marginsDisabled: boolean) => void;
    contentExpandsToAvailableHeight: boolean;
    setContentExpandsToAvailableHeight: (expand: boolean) => void;
    getLinkComponent: () => any;
    goBack: (fallback?: string) => void;
    navigate: RouterNavigateFunction;
    useBlocker: ((shouldBlock: boolean | ((args: any) => boolean)) => void) | undefined;
    useLocationPath: () => string;
    anyHistoryEntryExist: () => boolean;
    topLevelRouteChanged: boolean;
    setMessageDialogShow: (fn: MessageDialogShowFn) => void;
    messageDialogShow: MessageDialogShowFn;
    setTemporalMessageShow: (fn: TemporalMessageShowFn) => void;
    temporalMessageShow: TemporalMessageShowFn;
    currentLanguage: string | undefined;
    setCurrentLanguage: (lang?: string | undefined) => void;
    t: (key: string, params?: any) => any;
    useDrag?: (fn: () => any, deps?: unknown[]) => any;
    useDrop?: (fn: () => any, deps?: unknown[]) => any;
    saveAs?: (data: Blob | string, filename?: string) => void;
};

export const BaseAppContext = React.createContext<BaseAppContextType | undefined>(undefined);
export const useBaseAppContext = () => {
    const context = React.useContext(BaseAppContext);
    if (context === undefined) {
        throw new Error('useAppContext must be used within an AppProvider');
    }
    return context;
};

export const useOptionalBaseAppContext = (): BaseAppContextType | undefined => {
    return React.useContext(BaseAppContext);
};

export default BaseAppContext;
