import React from 'react';
import { TimePicker } from '@mui/x-date-pickers/TimePicker';
import { FormFieldCustomProps } from '../../form/FormField';
import { useFormFieldCommon } from './FormFieldText';
import { useFormFieldDateCommon } from './FormFieldDate';

export const FormFieldTime: React.FC<FormFieldCustomProps> = (props) => {
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
    } = props;
    const { helperText, title, startAdornment } = useFormFieldCommon(
        field,
        fieldError,
        inline,
        componentProps
    );
    const { dateValue, dateError, handleOnChange, handleOnBlur } = useFormFieldDateCommon(
        value?.includes('T') ? value : value ? '00000000T' + value : value,
        onChange
    );
    const processedInputProps = {
        ...componentProps?.slotProps?.input,
        startAdornment,
    };
    return (
        <TimePicker
            name={name}
            label={!inline ? label : undefined}
            value={dateValue ?? null}
            disabled={disabled}
            readOnly={readOnly}
            onChange={handleOnChange}
            ampm={false}
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
export default FormFieldTime;
