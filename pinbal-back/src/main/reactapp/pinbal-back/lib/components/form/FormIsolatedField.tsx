import React from 'react';
import { FormFieldCommonProps } from './FormField';
import ResourceApiFormContext, { FormFieldDataAction, FormFieldError } from './FormContext';
import { useBaseAppContext } from '../BaseAppContext';
import {
    useResourceApiService,
    ResourceApiBlobResponse,
    ResourceApiError,
} from '../ResourceApiProvider';
import { processType } from '../../util/fields';

type FormIsolatedFieldProps = FormFieldCommonProps & {
    resourceName: string;
    value: any;
    label?: string;
    field?: any;
    fieldError?: FormFieldError;
    onChange: (value: any, fieldName: string) => void;
    style?: React.CSSProperties;
};

export const FormIsolatedField: React.FC<FormIsolatedFieldProps> = (props) => {
    const { resourceName, name, field: fieldProp, ...otherProps } = props;
    const { getFormFieldComponent } = useBaseAppContext();
    const { isReady: apiIsReady, currentFields: apiCurrentFields } =
        useResourceApiService(resourceName);
    const [fields, setFields] = React.useState<any>();
    const [field, setField] = React.useState<any>();
    const fieldType = processType(field, fieldProp?.type);
    const FormFieldComponent = getFormFieldComponent(fieldType ?? 'reference');
    React.useEffect(() => {
        if (apiIsReady && fieldProp == null) {
            setFields(apiCurrentFields);
            const field = apiCurrentFields?.find((f: any) => f.name === name);
            setField(field);
        }
    }, [apiIsReady, fieldProp]);
    const mockFormApi = {
        getId: () => null,
        getData: () => {},
        refresh: () => {},
        reset: (_data?: any, _id?: any) => {},
        revert: (_unconfirmed?: boolean) => null,
        save: () => new Promise((resolve) => resolve(null)),
        execAction: (_code: string) => new Promise((resolve) => resolve(null)),
        generateReport: (_code: string) =>
            new Promise<ResourceApiBlobResponse>((resolve) =>
                resolve({
                    blob: new Blob(),
                    fileName: '',
                })
            ),
        validate: () => new Promise<void>((resolve) => resolve()),
        delete: () => null,
        focus: (_name?: string) => null,
        setFieldValue: (_name: string, _value: any) => null,
        setModified: (_modified: boolean) => null,
        handleSubmissionErrors: (
            _error: ResourceApiError,
            _temporalMessageTitle?: string,
            _reject?: (reason: any) => void
        ) => null,
    };
    const formContext = {
        resourceName,
        fields,
        isLoading: fields == null,
        isReady: fields == null,
        isSaveActionPresent: false,
        isDeleteActionPresent: false,
        modified: false,
        apiRef: { current: mockFormApi },
        dataGetFieldValue: (_fieldName: string) => null,
        dataDispatchAction: (_action: FormFieldDataAction) => {},
        validationSetFieldErrors: (_fieldName: string, _errors?: FormFieldError[]) => {},
    };
    return FormFieldComponent != null ? (
        <ResourceApiFormContext.Provider value={formContext}>
            {(fieldProp != null || field != null) && (
                <FormFieldComponent {...otherProps} name={name} field={fieldProp ?? field} />
            )}
        </ResourceApiFormContext.Provider>
    ) : null;
};
export default FormIsolatedField;
