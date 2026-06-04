import React from 'react';
import Typography from '@mui/material/Typography';
import { useResourceApiContext } from '../../ResourceApiContext';
import { DetailFieldCustomProps } from '../../detail/DetailField';
import { formattedFieldValue } from '../../../util/fields';

export const MuiDetailField: React.FC<DetailFieldCustomProps> = (props) => {
    const { label, value, field, inline, type, formattedFieldParams } = props;
    const { currentLanguage } = useResourceApiContext();
    const formattedValue = formattedFieldValue(value, field, {
        type,
        currentLanguage,
        ...formattedFieldParams,
    });
    if (inline) {
        return (
            <Typography variant="body1" gutterBottom>
                <strong>{label}:</strong> {formattedValue}
            </Typography>
        );
    } else {
        return (
            <>
                <Typography variant="subtitle2" color="text.secondary">
                    {label}
                </Typography>
                <Typography variant="body1" gutterBottom>
                    {formattedValue}
                </Typography>
            </>
        );
    }
};
export default MuiDetailField;
