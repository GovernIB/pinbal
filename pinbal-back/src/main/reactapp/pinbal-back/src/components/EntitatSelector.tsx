import React from 'react';
import MenuItem from '@mui/material/MenuItem';
import ListItemText from '@mui/material/ListItemText';
import Icon from '@mui/material/Icon';
import TextField from '@mui/material/TextField';
import InputAdornment from '@mui/material/InputAdornment';
import { usePinbalContext } from './PinbalContext.ts';

const EntitatSelector: React.FC = () => {
    const { entitatsAvailable, currentEntitatId, setCurrentEntitatId } = usePinbalContext();
    const handleEntitatChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        setCurrentEntitatId(Number(event.target.value));
    };
    return entitatsAvailable ? (
        <TextField
            value={currentEntitatId != null ? '' + currentEntitatId : ''}
            onChange={handleEntitatChange}
            size="small"
            select
            slotProps={{
                input: {
                    startAdornment: (
                        <InputAdornment position="start" sx={{ mr: 2 }}>
                            <Icon>layers</Icon>
                        </InputAdornment>
                    ),
                },
            }}
            sx={{ mr: 1 }}
        >
            {entitatsAvailable.map((e) => (
                <MenuItem key={e.id} value={e.id}>
                    <ListItemText>{e.nom}</ListItemText>
                </MenuItem>
            ))}
        </TextField>
    ) : null;
};

export default EntitatSelector;
