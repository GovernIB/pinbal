import React from 'react';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import { MuiPickersAdapterContext } from '@mui/x-date-pickers/LocalizationProvider';
import { FormFieldCustomProps } from '../../form/FormField';
import { timestampToIsoDateTime } from '../../../util/dateFormat';
import { useFormFieldCommon } from './FormFieldText';

export const useFormFieldDateCommon = (value: any, onChange: (value: any) => void) => {
    const pickersAdapterContext = React.useContext(MuiPickersAdapterContext);
    const adapterUtils = pickersAdapterContext?.utils;
    const adapterLib = adapterUtils?.lib;
    const textToDateValue = (textValue: string) => {
        if (adapterUtils == null) {
            console.warn('[FormFieldDateTime] Adapter utils not found in context');
            return textValue;
        }
        return textValue != null ? adapterUtils.date(textValue) : textValue;
    };
    const dateToTextValue = (dateValue: any, dateError?: string) => {
        if (dateError == null && dateValue != null) {
            if (adapterLib === 'date-fns') {
                return timestampToIsoDateTime((dateValue as Date).getTime());
            } else if (adapterLib === 'dayjs') {
                return dateValue.format('YYYY-MM-DDTHH:mm:ss');
            } else {
                console.warn('[FormFieldDateTime] Unknown adapter lib:', adapterLib);
                return dateValue;
            }
        } else {
            return null;
        }
    };
    const [dateValue, setDateValue] = React.useState<any>(textToDateValue(value));
    const [dateError, setDateError] = React.useState<string>();
    React.useEffect(() => {
        setDateValue(textToDateValue(value));
    }, [value]);
    const handleOnChange = (changedValue: any, context: any) => {
        setDateValue(changedValue);
        const error = context?.validationError;
        setDateError(error);
        const textValue = dateToTextValue(changedValue, error);
        if (textValue != null) {
            onChange(textValue);
        }
        if (changedValue == null || (value != null && error != null)) {
            onChange(null);
        }
    };
    const handleOnBlur = () => {
        if (dateError != null) {
            setDateValue(null);
            setTimeout(() => {
                setDateError(undefined);
            }, 0);
        }
    };
    return {
        dateValue,
        dateError,
        handleOnChange,
        handleOnBlur,
    };
};

export const FormFieldDate: React.FC<FormFieldCustomProps> = (props) => {
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
        value,
        onChange
    );
    const processedInputProps = {
        ...componentProps?.slotProps?.input,
        startAdornment,
    };
    return (
        <DatePicker
            name={name}
            label={!inline ? label : undefined}
            value={dateValue ?? null}
            disabled={disabled}
            readOnly={readOnly}
            onChange={handleOnChange}
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
export default FormFieldDate;
