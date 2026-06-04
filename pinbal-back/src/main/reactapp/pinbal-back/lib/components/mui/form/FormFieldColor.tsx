import React from 'react';
import TextField from '@mui/material/TextField';
import IconButton from '@mui/material/IconButton';
import Icon from '@mui/material/Icon';
import InputAdornment from '@mui/material/InputAdornment';
import { FormFieldCustomProps } from '../../form/FormField';
import { useFormFieldCommon } from './FormFieldText';

export const FormFieldColor: React.FC<FormFieldCustomProps> = (props) => {
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
    const fileInputRef = React.useRef<HTMLInputElement>(undefined);
    const handleClearClick = () => {
        onChange(null);
    };
    const { helperText, title, startAdornment } = useFormFieldCommon(
        field,
        fieldError,
        inline,
        componentProps
    );
    const endAdornment = (
        <>
            {componentProps?.slotProps?.input?.endAdornment}
            <InputAdornment position="end">
                {value != null && !readOnly && (
                    <IconButton disabled={disabled} onClick={handleClearClick} size="small">
                        <Icon fontSize="small">clear</Icon>
                    </IconButton>
                )}
                <IconButton
                    disabled={disabled || readOnly}
                    onClick={() => (fileInputRef.current as any)?.querySelector('input').click()}
                    size="small">
                    <Icon fontSize="small">palette</Icon>
                </IconButton>
            </InputAdornment>
        </>
    );
    const inputProps = {
        readOnly,
        disabled: readOnly,
        ...componentProps?.slotProps?.input,
        startAdornment,
        endAdornment,
        ref: fileInputRef,
    };
    return (
        <TextField
            name={name}
            label={!inline ? label : undefined}
            placeholder={componentProps?.placeholder ?? (inline ? label : undefined)}
            type="color"
            value={value ?? ''}
            required={required ?? field.required}
            disabled={disabled}
            error={fieldError != null}
            title={title}
            onChange={(e) =>
                !readOnly && !disabled && onChange(e.target.value === '' ? null : e.target.value)
            }
            fullWidth
            {...componentProps}
            helperText={helperText ?? componentProps.helperText}
            slotProps={{
                input: inputProps,
            }}
            sx={{
                '& input': { opacity: value ? undefined : '0' },
                ...componentProps?.sx,
            }}
        />
    );
};
export default FormFieldColor;
