import React from 'react';
import Box from '@mui/material/Box';
import Icon from '@mui/material/Icon';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import { useBaseAppContext } from '../BaseAppContext';
import { useResourceApiContext } from '../ResourceApiContext';
import BasePage from '../BasePage';

export const OfflineMessage: React.FC = () => {
    const { t } = useBaseAppContext();
    const { refreshApiIndex } = useResourceApiContext();
    return (
        <BasePage expandHeight>
            <Box
                sx={{
                    display: 'flex',
                    flexDirection: 'column',
                    justifyContent: 'center',
                    alignItems: 'center',
                    height: '100%',
                }}>
                <Typography variant="h4">
                    <Icon color="error" fontSize="large" sx={{ mr: 1, paddingTop: '4px' }}>
                        warning
                    </Icon>
                    {t('app.offline.message')}
                </Typography>
                <Button variant="contained" onClick={() => refreshApiIndex()} sx={{ mt: 2 }}>
                    {t('buttons.misc.retry')}
                </Button>
            </Box>
        </BasePage>
    );
};

export default OfflineMessage;
