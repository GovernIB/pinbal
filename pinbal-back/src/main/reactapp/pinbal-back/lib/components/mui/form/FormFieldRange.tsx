import React from 'react';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import Slider from '@mui/material/Slider';
import { FormFieldCustomProps } from '../../form/FormField';

export const FormFieldRange: React.FC<FormFieldCustomProps> = (props) => {
    const {
        name,
        label,
        value,
        field: _field,
        fieldError: _fieldError,
        inline: _inline,
        required: _required,
        disabled,
        readOnly: _readOnly,
        onChange,
        componentProps,
    } = props;
    /*const marks = [{
        value: 0,
        label: '0%'
    }, {
        value: 100,
        label: '100%',
    }];*/
    return (
        <Box sx={{ ml: 1, mr: 1 }}>
            <Typography variant="subtitle1" gutterBottom>
                {label}
            </Typography>
            <Slider
                name={name}
                defaultValue={value}
                min={0}
                max={100}
                step={5}
                marks /*={marks}*/
                valueLabelDisplay="auto"
                disabled={disabled}
                onChange={(_event: Event, value: number | number[]) => onChange(value)}
                {...componentProps}
            />
        </Box>
    );
};
export default FormFieldRange;
