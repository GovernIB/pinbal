import React from 'react';
import { useTranslation } from 'react-i18next';
import Box from '@mui/material/Box';
import TextField from '@mui/material/TextField';
import InputAdornment from '@mui/material/InputAdornment';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import Tooltip from '@mui/material/Tooltip';
import { FormApiRef, GridPage, useDebounce, useFormApiRef } from 'reactlib';
import { PropietatsGroups } from './PropietatsGroups';
import { PropietatsProps } from './PropietatsProps';
import PropietatsActions from './PropietatsActions';

const PropietatsQuickFilter: React.FC<{
    onChange: (quickFilter: string | undefined) => void;
    formApiRef: FormApiRef;
}> = ({ onChange, formApiRef }) => {
    const { t } = useTranslation();
    const [quickFilter, setQuickFilter] = React.useState<string>('');
    const quickFilterDebounced = useDebounce(quickFilter);
    React.useEffect(() => {
        onChange?.(quickFilterDebounced);
    }, [quickFilterDebounced, onChange]);
    return (
        <Box sx={{ display: 'flex', gap: 1, flexGrow: 1, alignItems: 'center' }}>
            <TextField
                value={quickFilter}
                onChange={(event) => setQuickFilter(event.target.value)}
                label={t('page.propietats.find')}
                variant="outlined"
                size="small"
                sx={{ maxWidth: 400 }}
                slotProps={{
                    input: {
                        startAdornment: (
                            <InputAdornment position="start">
                                <Icon fontSize="small">search</Icon>
                            </InputAdornment>
                        ),
                        endAdornment: quickFilter && (
                            <InputAdornment position="end">
                                <IconButton size="small" onClick={() => setQuickFilter('')}>
                                    <Icon fontSize="inherit">clear</Icon>
                                </IconButton>
                            </InputAdornment>
                        ),
                    },
                }}
            />
            <Tooltip title={t('page.propietats.revert')}>
                <IconButton size="small" onClick={() => formApiRef.current?.revert()}>
                    <Icon>undo</Icon>
                </IconButton>
            </Tooltip>
            <Box sx={{ flexGrow: 1 }} />
            <PropietatsActions />
        </Box>
    );
};

export const Propietats: React.FC = () => {
    const { t } = useTranslation();
    const [quickFilter, setQuickFilter] = React.useState<string>();
    const [selectedGroup, setSelectedGroup] = React.useState<any>();
    const formApiRef = useFormApiRef();
    return (
        <GridPage disableMargins>
            <Box sx={{ display: 'flex', flexDirection: 'column', height: '100%', overflow: 'hidden' }}>
                <Box sx={{ display: 'flex', alignItems: 'center', p: 2, flexShrink: 0, bgcolor: 'background.paper', borderBottom: 1, borderColor: 'divider' }}>
                    <Box component="h2" sx={{ m: 0, mr: 3, fontSize: '1.25rem' }}>
                        {t('page.propietats.title')}
                    </Box>
                    <PropietatsQuickFilter onChange={setQuickFilter} formApiRef={formApiRef} />
                </Box>
                <Box sx={{ display: 'flex', flexGrow: 1, overflow: 'hidden' }}>
                    <Box
                        sx={{
                            width: { xs: '220px', md: '280px', lg: '360px' },
                            flexShrink: 0,
                            height: '100%',
                            minHeight: 0,
                            borderRight: 1,
                            borderColor: 'divider',
                            overflowY: 'auto',
                            p: 2,
                        }}
                    >
                        <PropietatsGroups quickFilter={quickFilter} onChange={setSelectedGroup} />
                    </Box>
                    <Box sx={{ flexGrow: 1, height: 'auto', overflowY: 'auto', py: 2 }}>
                        <PropietatsProps group={selectedGroup} quickFilter={quickFilter} formApiRef={formApiRef} />
                    </Box>
                </Box>
            </Box>
        </GridPage>
    );
};

export default Propietats;
