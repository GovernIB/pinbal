import React from 'react';
import { useBaseAppContext } from '../BaseAppContext';
import useLogConsole from '../../util/useLogConsole';
import { processType } from '../../util/fields';
import { useFormContext, FormFieldDataActionType, FormFieldError } from './FormContext';
import { useOptionalFilterContext } from './FilterContext';

const LOG_PREFIX = 'FIELD';

export type FormFieldComponent = {
    type: string;
    component: React.FC<FormFieldCustomProps>;
};

/**
 * Propietats comunes dels components FormField.
 */
export type FormFieldCommonProps = {
    /** Nom del camp (identifica el camp del formulari que es vol mostrar) */
    name: string;
    /** Etiqueta del camp (si no s'especifica s'utilitzarà l'etiqueta del backend) */
    label?: string;
    /** Indica que aquest camp pertany a un formulari d'una sola línia */
    inline?: boolean;
    /** Indica que aquest camp és obligatori (si no s'especifica s'utilitzarà el valor proporcionat pel backend) */
    required?: boolean;
    /** Indica que aquest camp està deshabilitat */
    disabled?: boolean;
    /** Indica que aquest camp és de nomes lectura */
    readOnly?: boolean;
    /** Event que es llença quan es canvia el valor del camp */
    onChange?: (value: any) => void;
    /** Propietats addicionals del component */
    componentProps?: any;
};

/**
 * Propietats del component FormField (es poden especificar propietats addicionals que es passaran directament a la implementació específica pel tipus de camp).
 */
export type FormFieldProps = FormFieldCommonProps & {
    /** Tipus del component (si no s'especifica s'utilitzarà el tipus del backend) */
    type?: string;
    /** Validador pel camp. Ha de retornar un array d'errors si el camp no és vàlid */
    validator?: (value: any) => FormFieldError[] | void;
    /** Indica si s'han d'imprimir a la consola missatges de depuració */
    debug?: boolean;
    [x: string | number | symbol]: unknown;
};

type FormFieldRendererProps = FormFieldCommonProps & {
    value?: any;
    field?: any;
    fieldError?: FormFieldError;
    fieldTypeMap?: Map<string, string>;
    onFieldValueChange: (value: any) => void;
    type?: string;
    debug?: boolean;
};

export type FormFieldCustomProps = FormFieldCommonProps & {
    value: any;
    field: any;
    type?: string;
    fieldError?: FormFieldError;
    onChange: (value: any) => void;
};

const useFormFieldComponent = (type?: string, field?: any, fieldTypeMap?: Map<string, string>) => {
    const { getFormFieldComponent } = useBaseAppContext();
    const fieldType = field?.type ? (fieldTypeMap?.get(field?.type) ?? field?.type) : field?.type;
    const processedType = processType(field, type ?? fieldType);
    return {
        type: processedType,
        FormFieldComponent: getFormFieldComponent(processedType),
    };
};

const FormFieldRenderer: React.FC<FormFieldRendererProps> = (props) => {
    const {
        name,
        label: labelProp,
        value,
        field,
        fieldError,
        fieldTypeMap,
        inline,
        required,
        disabled,
        readOnly,
        onFieldValueChange,
        componentProps,
        type,
        debug,
        ...otherProps
    } = props;
    const logConsole = useLogConsole(LOG_PREFIX);
    const label = labelProp ?? field?.label ?? name;
    debug && logConsole.debug('Field', name, 'rendered', value ? 'with value: ' + value : 'empty');
    const { type: formFieldType, FormFieldComponent } = useFormFieldComponent(
        type,
        field,
        fieldTypeMap
    );
    return FormFieldComponent ? (
        <FormFieldComponent
            name={name}
            label={label}
            value={value}
            type={formFieldType}
            field={field}
            fieldError={fieldError}
            inline={inline}
            required={required}
            disabled={disabled}
            readOnly={readOnly}
            onChange={onFieldValueChange}
            componentProps={componentProps}
            {...otherProps}
        />
    ) : (
        <span>[&nbsp;Unknown field: {name}&nbsp;]</span>
    );
};

const Renderer = React.memo(FormFieldRenderer);

/**
 * Camp de formulari.
 *
 * @param props - Propietats del component.
 * @returns Element JSX del camp.
 */
export const FormField: React.FC<FormFieldProps> = (props) => {
    const {
        name,
        inline: inlineProp,
        required,
        disabled,
        readOnly,
        onChange,
        componentProps,
        type,
        validator,
        debug,
        ...otherProps
    } = props;
    const {
        isReady: isFormReady,
        isSaveActionPresent,
        fields,
        fieldErrors,
        fieldTypeMap,
        inline: inlineCtx,
        dataGetFieldValue,
        dataDispatchAction,
        validationSetFieldErrors,
        commonFieldComponentProps,
    } = useFormContext();
    const filterContext = useOptionalFilterContext();
    const field = React.useMemo(() => {
        if (fields) {
            const field = fields.find((f) => f.name === name);
            return field ?? null;
        }
    }, [fields, name]);
    const fieldError = React.useMemo(() => {
        if (fieldErrors) {
            const fieldError = fieldErrors.find((e) => e.field === name);
            return fieldError ?? undefined;
        } else {
            return undefined;
        }
    }, [fieldErrors, name]);
    const isReady = isFormReady && field !== undefined;
    const value = dataGetFieldValue(name);
    const handleFieldValueChange = React.useCallback(
        (changedValue: any) => {
            dataDispatchAction({
                type: FormFieldDataActionType.FIELD_CHANGE,
                payload: { fieldName: name, field, value: changedValue },
            });
            onChange?.(changedValue);
            validationSetFieldErrors(name, validator?.(changedValue) ?? undefined);
        },
        [dataDispatchAction, name, field, onChange]
    );
    const inline = inlineProp ?? inlineCtx;
    const forceDisabledAndReadonly = filterContext == null && !isSaveActionPresent;
    const joinedComponentProps = React.useMemo(
        () => ({
            ...commonFieldComponentProps,
            ...componentProps,
        }),
        [commonFieldComponentProps, componentProps]
    );
    React.useEffect(() => {
        validationSetFieldErrors(name, validator?.(value) ?? undefined);
    }, []);
    return isReady ? (
        <Renderer
            name={name}
            value={value}
            field={field}
            fieldError={fieldError}
            fieldTypeMap={fieldTypeMap}
            inline={inline}
            required={required}
            disabled={forceDisabledAndReadonly || disabled}
            readOnly={forceDisabledAndReadonly || readOnly}
            componentProps={joinedComponentProps}
            type={type}
            debug={debug}
            onFieldValueChange={handleFieldValueChange}
            {...otherProps}
        />
    ) : null;
};

export default FormField;
