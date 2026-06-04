import React from 'react';
import { DateTimePicker } from '@mui/x-date-pickers/DateTimePicker';
import { FormFieldCustomProps } from '../../form/FormField';
import { useFormFieldCommon } from './FormFieldText';
import { useFormFieldDateCommon } from './FormFieldDate';

type FormFieldDateTimeLocalProps = FormFieldCustomProps & {
    /** Indica que el camp no ha de permetre introduir segons */
    noSeconds?: true;
};

export const FormFieldDateTimeLocal: React.FC<FormFieldDateTimeLocalProps> = (props) => {
    const {
        name,
        label,
        value,
        field,
        fieldError,
        inline,
        required,
        disabled,
        readOnly,
        onChange,
        componentProps,
        noSeconds,
    } = props;
    const { helperText, title, startAdornment } = useFormFieldCommon(
        field,
        fieldError,
        inline,
        componentProps
    );
    const { dateValue, dateError, handleOnChange, handleOnBlur } = useFormFieldDateCommon(
        value,
        onChange
    );
    const processedInputProps = {
        ...componentProps?.slotProps?.input,
        startAdornment,
    };
    const views: any[] = ['year', 'month', 'day', 'hours', 'minutes'];
    if (!noSeconds) {
        views.push('seconds');
    }
    return (
        <DateTimePicker
            name={name}
            label={!inline ? label : undefined}
            value={dateValue ?? null}
            disabled={disabled}
            readOnly={readOnly}
            onChange={handleOnChange}
            ampm={false}
            views={views}
            {...componentProps}
            slotProps={{
                field: { clearable: dateError == null },
                textField: {
                    ...componentProps,
                    required: required ?? field.required,
                    error: fieldError != null || dateError != null,
                    placeholder: componentProps?.placeholder ?? (inline ? label : undefined),
                    title: componentProps?.title ?? title,
                    helperText,
                    fullWidth: true,
                    onBlur: handleOnBlur,
                    InputProps: processedInputProps,
                },
            }}
        />
    );
};
export default FormFieldDateTimeLocal;
