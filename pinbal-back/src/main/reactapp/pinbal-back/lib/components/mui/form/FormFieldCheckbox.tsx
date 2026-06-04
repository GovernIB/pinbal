import React from 'react';
import FormControl from '@mui/material/FormControl';
import FormHelperText from '@mui/material/FormHelperText';
import FormControlLabel from '@mui/material/FormControlLabel';
import Checkbox from '@mui/material/Checkbox';
import Switch from '@mui/material/Switch';
import { FormFieldCustomProps } from '../../form/FormField';
import { useFormFieldCommon } from './FormFieldText';

type FormFieldCheckboxProps = FormFieldCustomProps & {
    /** Indica si s'ha de mostrar el camp amb el component MUI Switch */
    typeSwitch?: true;
};

export const FormFieldCheckbox: React.FC<FormFieldCheckboxProps> = (props) => {
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
        typeSwitch,
        componentProps,
    } = props;
    const { helperText, title, startAdornment } = useFormFieldCommon(
        field,
        fieldError,
        inline,
        componentProps
    );
    const { helperText: componentPropsHelperText, ...otherComponentProps } = componentProps;
    const control = typeSwitch ? (
        <Switch
            checked={value ? true : false}
            color={fieldError != null ? 'error' : undefined}
            title={title}
            onChange={!readOnly ? (e) => onChange(e.target.checked) : undefined}
            disabled={disabled}
            sx={{ ml: 1 }}
            {...otherComponentProps}
        />
    ) : (
        <Checkbox
            checked={value ? true : false}
            color={fieldError != null ? 'error' : undefined}
            title={title}
            onChange={!readOnly ? (e) => onChange(e.target.checked) : undefined}
            disableRipple={readOnly}
            disabled={disabled}
            sx={{ ml: 1 }}
            {...otherComponentProps}
        />
    );
    const formControlSx = !inline
        ? { top: typeSwitch ? '12px' : '4px', ml: typeSwitch ? 2 : 1.4 }
        : undefined;
    const formHelperText = helperText ?? componentPropsHelperText;
    return (
        <FormControl error={!!fieldError} sx={formControlSx}>
            <FormControlLabel
                name={name}
                required={required}
                label={
                    !inline ? (
                        label
                    ) : (
                        <div style={{ position: 'relative', top: '4px' }}>{startAdornment}</div>
                    )
                }
                slotProps={{
                    typography: {
                        color: fieldError != null ? 'error' : undefined,
                    },
                    ...componentProps?.slotProps,
                }}
                control={control}
            />
            {formHelperText && <FormHelperText>{formHelperText}</FormHelperText>}
        </FormControl>
    );
};
export default FormFieldCheckbox;
