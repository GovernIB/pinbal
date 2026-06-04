import React from 'react';
import { FormFieldCustomProps } from './FormField';

export const ResourceApiFormFieldDefault: React.FC<FormFieldCustomProps> = (props) => {
    const { name, label, value, onChange } = props;
    return (
        <input
            name={name}
            title={label}
            value={value ?? ''}
            onChange={(e) => onChange(e.target.value)}
        />
    );
};
export default ResourceApiFormFieldDefault;
