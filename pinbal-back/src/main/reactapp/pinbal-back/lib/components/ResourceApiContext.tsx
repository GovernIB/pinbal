import React from 'react';
import { Client, State } from 'ketting';

export type OpenAnswerRequiredDialogFn = (
    title: string | undefined,
    question: string,
    trueFalseAnswerRequired: boolean,
    availableAnswers: string[]
) => Promise<string>;

export type ResourceType = 'ACTION' | 'REPORT' | 'FILTER';
export type ExportFileType = 'CSV' | 'ODS' | 'ODT' | 'XLSX' | 'DOCX' | 'PDF';
export type ReportOutputFormat =
    | 'PDF'
    | 'XLS'
    | 'CSV'
    | 'ODS'
    | 'XLSX'
    | 'ODT'
    | 'RTF'
    | 'DOCX'
    | 'PPTX';

export type ResourceApiContextType = {
    isLoading: boolean;
    isReady: boolean;
    apiUrl: string;
    offline: boolean;
    indexState: State | undefined;
    indexError: Error | undefined;
    currentLanguage: string | undefined;
    refreshApiIndex: () => void;
    getKettingClient: () => Client | undefined;
    requestHref: (href: string, templateData?: any) => Promise<State>;
    isDebugRequests: () => boolean | undefined;
    setCurrentLanguage: (currentLanguage?: string) => void;
    httpHeaders?: Record<string, string>[];
    setHttpHeaders: (httpHeaders?: Record<string, string>[]) => void;
    getOpenAnswerRequiredDialog: () => OpenAnswerRequiredDialogFn | undefined;
    setOpenAnswerRequiredDialog: (openAnswerRequiredDialog: OpenAnswerRequiredDialogFn) => void;
};

export const ResourceApiContext = React.createContext<ResourceApiContextType | undefined>(
    undefined
);

export const useResourceApiContext = (hookName?: string): ResourceApiContextType => {
    const context = React.useContext(ResourceApiContext);
    if (context === undefined) {
        throw new Error(
            (hookName ?? 'useResourceApiContext') + ' must be used within a ResourceApiProvider'
        );
    }
    return context;
};

export const useOptionalResourceApiContext = (): ResourceApiContextType | undefined => {
    return React.useContext(ResourceApiContext);
};

export default ResourceApiContext;
