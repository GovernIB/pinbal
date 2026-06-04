import React from 'react';
import { useTranslation } from 'react-i18next';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import { BasePage, useResourceApiContext } from 'reactlib';
import offline from '../assets/offline.svg';

export const Offline: React.FC = () => {
    const { t } = useTranslation();
    const { refreshApiIndex } = useResourceApiContext();
    return <BasePage expandHeight>
        <Box
            sx={{
                display: 'flex',
                flexDirection: 'column',
                justifyContent: 'center',
                alignItems: 'center',
                height: '100%',
            }}>
            <img width="5%" alt="Not found" src={offline} />
            <Typography variant="h4">{t('component.Offline.message')}</Typography>
            <Button variant="contained" onClick={() => refreshApiIndex()} sx={{ mt: 2 }}>
                {t('component.Offline.retry')}
            </Button>
        </Box>
    </BasePage>;
}

export default Offline;
