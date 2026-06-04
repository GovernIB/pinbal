import React from 'react';
import { ResourceType } from '../ResourceApiContext';
import { ResourceApiError } from '../ResourceApiProvider';

export type FormApi = {
    getId: () => any;
    getData: () => any;
    refresh: () => void;
    reset: (data?: any, id?: any) => void;
    revert: (unconfirmed?: boolean) => void;
    validate: () => Promise<void>;
    save: () => Promise<any>;
    delete: () => void;
    focus: (name?: string) => void;
    setFieldValue: (name: string, value: any) => void;
    setModified: (modified: boolean) => void;
    handleSubmissionErrors: (error: ResourceApiError, temporalMessageTitle?: string) => void;
};

export type FormApiRef = React.RefObject<FormApi | null>;

export enum FormFieldDataActionType {
    RESET = 'RESET',
    FIELD_CHANGE = 'FIELD_CHANGE',
}

export type FormFieldDataActionPayload = {
    field: any;
    fieldName: string;
    value: any;
    changes?: any;
};

export type FormFieldDataAction = {
    type: FormFieldDataActionType;
    payload: FormFieldDataActionPayload;
};

export type FormContextType = {
    id?: any;
    resourceName: string;
    resourceType?: ResourceType;
    resourceTypeCode?: string;
    isLoading: boolean;
    isReady: boolean;
    apiLinks?: any;
    isSaveActionPresent: boolean;
    isDeleteActionPresent: boolean;
    fields?: any[];
    fieldErrors?: FormFieldError[];
    fieldTypeMap?: Map<string, string>;
    inline?: true;
    data?: any;
    modified: boolean;
    apiRef: FormApiRef;
    dataGetFieldValue: (fieldName: string) => any;
    dataDispatchAction: (action: FormFieldDataAction) => void;
    validationSetFieldErrors: (fieldName: string, errors?: FormFieldError[]) => void;
    commonFieldComponentProps?: any;
};

export type FormFieldError = {
    field: string;
    code?: string;
    message: string;
};

export const FormContext = React.createContext<FormContextType | undefined>(undefined);

export const useFormContext = () => {
    const context = React.useContext(FormContext);
    if (context === undefined) {
        throw new Error('useFormContext must be used within a FormProvider');
    }
    return context;
};

export const useOptionalFormContext = (): FormContextType | undefined => {
    return React.useContext(FormContext);
};

export default FormContext;
