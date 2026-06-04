import React from 'react';
import { DateTimeField } from '@mui/x-date-pickers/DateTimeField';
import { FormFieldCustomProps } from '../../form/FormField';
import { parseIsoDuration } from '../../../util/durationFormat';
import { FormFieldNumber } from './FormFieldNumber';
import { useFormFieldCommon } from './FormFieldText';
import { useFormFieldDateCommon } from './FormFieldDate';

type FormFieldDurationProps = FormFieldCustomProps & {
    /** Indica si el camp ha de permetre introduir dies */
    days?: boolean;
    /** Indica si el camp ha de permetre introduir segons */
    hours?: boolean;
    /** Indica si el camp ha de permetre introduir segons */
    minutes?: boolean;
};

const intPadding = (num?: number, digits?: number) => {
    const numToPad = num ?? 0;
    return numToPad.toLocaleString('es-ES', {
        minimumIntegerDigits: digits ?? 2,
        useGrouping: false,
    });
};

const useDurationState = (value: any, onChange: (value: any) => void) => {
    const textIsoDurationToTextIsoDate = (textIsoDuration: string) => {
        const duration = parseIsoDuration(textIsoDuration);
        if (textIsoDuration != null && duration != null) {
            const isoDate =
                intPadding(duration.years, 4) +
                '-' +
                intPadding(duration.months) +
                '-' +
                intPadding(duration.days);
            const isoTime =
                intPadding(duration.hours) +
                ':' +
                intPadding(duration.minutes) +
                ':' +
                intPadding(duration.seconds);
            return isoDate + 'T' + isoTime;
        } else {
            return textIsoDuration;
        }
    };
    const textIsoDateToTextIsoDuration = (textIsoDate: any) => {
        if (textIsoDate != null) {
            const parts = textIsoDate.split('T');
            const datePart = parts[0];
            const dateParts = datePart.split('-'); // YYYY-MM-DD
            const dateDuration =
                datePart == '00000000'
                    ? ''
                    : //parseInt(dateParts[0]) + 'Y' +
                      //parseInt(dateParts[1]) + 'M' +
                      parseInt(dateParts[2]) + 'D';
            const timePart = parts[1].substring(0, 8); // hh:mm:ss
            const timeParts = timePart.split(':');
            const timeDuration =
                parseInt(timeParts[0]) +
                'H' +
                parseInt(timeParts[1]) +
                'M' +
                parseInt(timeParts[2]) +
                'S';
            return 'P' + dateDuration + 'T' + timeDuration;
        } else {
            return textIsoDate;
        }
    };
    const handleOnChange = (value: any) => {
        onChange(textIsoDateToTextIsoDuration(value));
    };
    const {
        dateValue,
        dateError,
        handleOnChange: dateStateHandleOnChange,
        handleOnBlur,
    } = useFormFieldDateCommon(textIsoDurationToTextIsoDate(value), handleOnChange);
    return {
        dateValue,
        dateError,
        handleOnChange: dateStateHandleOnChange,
        handleOnBlur,
    };
};

export const FormFieldDurationDays: React.FC<FormFieldCustomProps> = (props) => {
    const { value: valueProp, onChange: onChangeProp, ...otherProps } = props;
    const duration = valueProp && valueProp.length ? parseIsoDuration(valueProp) : undefined;
    const value = duration ? duration.days : undefined;
    const handlerNumberFieldChange = (value: any) => {
        onChangeProp('P' + value + 'D');
    };
    return <FormFieldNumber value={value} onChange={handlerNumberFieldChange} {...otherProps} />;
};

export const FormFieldDuration: React.FC<FormFieldDurationProps> = (props) => {
    const {
        days = true,
        hours = true,
        minutes,
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
    const { dateValue, dateError, handleOnChange, handleOnBlur } = useDurationState(
        value,
        onChange
    );
    const processedInputProps = {
        ...componentProps?.slotProps?.input,
        startAdornment,
    };
    const format = (days ? ' DD[d]' : '') + (hours ? ' HH[h]' : '') + (minutes ? ' mm[m]' : '');
    return (
        <DateTimeField
            name={name}
            label={!inline ? label : undefined}
            value={dateValue ?? null}
            disabled={disabled}
            readOnly={readOnly}
            onChange={handleOnChange}
            format={format}
            ampm={false}
            clearable={dateError == null}
            {...componentProps}
            slotProps={{
                textField: {
                    ...componentProps,
                    required: required ?? field.required,
                    error: fieldError != null,
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

export default FormFieldDuration;
