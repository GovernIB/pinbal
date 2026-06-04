import React from 'react';
import TextField from '@mui/material/TextField';
import InputAdornment from '@mui/material/InputAdornment';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import { useBaseAppContext } from '../../BaseAppContext';

type DataQuickFilterProps = {
    value: string;
    setFocus?: true;
    onChange: (value: string) => void;
} & any;

export const DataQuickFilter: React.FC<DataQuickFilterProps> = (props) => {
    const { value, setFocus, onChange, ...otherProps } = props;
    const { t } = useBaseAppContext();
    return (
        <TextField
            title={t('datacommon.quickfilter.label')}
            size="small"
            value={value}
            inputRef={setFocus ? (input) => input && input.focus() : undefined}
            onChange={(event) => onChange(event.target.value)}
            slotProps={{
                htmlInput: {
                    'aria-label': t('datacommon.quickfilter.label'),
                },
                input: {
                    endAdornment: (
                        <>
                            {value && (
                                <InputAdornment position="end">
                                    <IconButton size="small" onClick={() => onChange('')}>
                                        <Icon fontSize="inherit">clear</Icon>
                                    </IconButton>
                                </InputAdornment>
                            )}
                            <InputAdornment position="end">
                                <Icon>search</Icon>
                            </InputAdornment>
                        </>
                    ),
                },
            }}
            {...otherProps}
        />
    );
};

export default DataQuickFilter;
