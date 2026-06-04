import React from 'react';
import TextField from '@mui/material/TextField';
import MenuItem from '@mui/material/MenuItem';
import { useBaseAppContext } from '../../BaseAppContext';
import { FormFieldCustomProps } from '../../form/FormField';
import { useFormFieldCommon } from './FormFieldText';

export const FormFieldCheckboxSelect: React.FC<FormFieldCustomProps> = (props) => {
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
    const { t } = useBaseAppContext();
    const [open, setOpen] = React.useState(false);
    const { helperText, title, startAdornment } = useFormFieldCommon(
        field,
        fieldError,
        inline,
        componentProps
    );
    const inputProps = {
        readOnly,
        ...componentProps?.slotProps?.input,
        startAdornment,
    };
    return (
        <TextField
            select
            name={name}
            label={!inline ? label : undefined}
            placeholder={componentProps?.placeholder ?? (inline ? label : undefined)}
            value={value ?? ''}
            required={required ?? field.required}
            disabled={disabled}
            error={fieldError != null}
            title={title}
            onChange={(e) => {
                const value = e.target.value;
                onChange(value !== '' ? value : null);
            }}
            fullWidth
            {...componentProps}
            helperText={helperText ?? componentProps.helperText}
            slotProps={{
                input: inputProps,
                select: {
                    open,
                    readOnly,
                    onClose: () => setOpen(false),
                    onOpen: () => setOpen(true),
                },
            }}>
            <MenuItem value={''}>&nbsp;</MenuItem>
            <MenuItem value={'true'}>{t('form.field.checkboxSelect.true')}</MenuItem>
            <MenuItem value={'false'}>{t('form.field.checkboxSelect.false')}</MenuItem>
        </TextField>
    );
};
export default FormFieldCheckboxSelect;
