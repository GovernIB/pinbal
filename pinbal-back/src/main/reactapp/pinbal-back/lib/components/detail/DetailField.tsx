import React from 'react';
import { useBaseAppContext } from '../BaseAppContext';
import useLogConsole from '../../util/useLogConsole';
import { processType } from '../../util/fields';
import { useDetailContext } from './DetailContext';

const LOG_PREFIX = 'FIELD';

/**
 * Propietats comunes dels components FormField.
 */
export type DetailFieldCommonProps = {
    /** Nom del camp (identifica el camp del formulari que es vol mostrar) */
    name: string;
    /** Etiqueta del camp (si no s'especifica s'utilitzarà l'etiqueta del backend) */
    label?: string;
    /** Valor del camp (si no s'especifica s'utilitzarà el valor del backend) */
    value?: any;
};

/**
 * Propietats del component FormField (es poden especificar propietats addicionals que es passaran directament a la implementació específica pel tipus de camp).
 */
export type DetailFieldProps = DetailFieldCommonProps & {
    /** Tipus del component (si no s'especifica s'utilitzarà el tipus del backend) */
    type?: string;
    /** Indica si s'han d'imprimir a la consola missatges de depuració */
    debug?: boolean;
    [x: string | number | symbol]: unknown;
};

export type DetailFieldCustomProps = DetailFieldCommonProps & {
    field: any;
    inline?: true;
    fieldTypeMap?: Map<string, string>;
    type?: string;
    formattedFieldParams?: any;
};

type DetailFieldRendererProps = DetailFieldCustomProps & {
    debug?: boolean;
};

const useDetailFieldComponent = (
    type?: string,
    field?: any,
    fieldTypeMap?: Map<string, string>
) => {
    const { getDetailFieldComponent } = useBaseAppContext();
    const fieldType = field?.type ? (fieldTypeMap?.get(field?.type) ?? field?.type) : field?.type;
    const processedType = processType(field, type ?? fieldType);
    return {
        type: processedType,
        DetailFieldComponent: getDetailFieldComponent(processedType),
    };
};

const DetailFieldRenderer: React.FC<DetailFieldRendererProps> = (props) => {
    const {
        name,
        label: labelProp,
        value,
        field,
        fieldTypeMap,
        type,
        debug,
        ...otherProps
    } = props;
    const logConsole = useLogConsole(LOG_PREFIX);
    const label = labelProp ?? field?.label ?? name;
    debug && logConsole.debug('Field', name, 'rendered', value ? 'with value: ' + value : 'empty');
    const { type: formFieldType, DetailFieldComponent } = useDetailFieldComponent(
        type,
        field,
        fieldTypeMap
    );
    return DetailFieldComponent ? (
        <DetailFieldComponent
            name={name}
            label={label}
            value={value}
            type={formFieldType}
            field={field}
            {...otherProps}
        />
    ) : (
        <span>[&nbsp;Unknown detail field: {name}&nbsp;]</span>
    );
};

const Renderer = React.memo(DetailFieldRenderer);

/**
 * Camp pels detalls.
 *
 * @param props - Propietats del component.
 * @returns Element JSX del camp.
 */
export const DetailField: React.FC<DetailFieldProps> = (props) => {
    const { name, type, value, debug, ...otherProps } = props;
    const { isReady: isFormReady, fields, fieldTypeMap, dataGetFieldValue } = useDetailContext();
    const field = React.useMemo(() => {
        if (fields) {
            const field = fields.find((f) => f.name === name);
            return field ?? null;
        }
    }, [fields, name]);
    const isReady = isFormReady && field !== undefined;
    return isReady ? (
        <Renderer
            name={name}
            value={value ?? dataGetFieldValue(name)}
            field={field}
            fieldTypeMap={fieldTypeMap}
            type={type}
            debug={debug}
            {...otherProps}
        />
    ) : null;
};

export default DetailField;
