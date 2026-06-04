import React from 'react';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import { useTheme } from '@mui/material/styles';
import { BasePage } from 'reactlib';

const HomeToolbar = () => {
    const theme = useTheme();
    const { t } = useTranslation();
    return (
        <Toolbar
            sx={{
                width: '100%',
                display: 'flex',
                py: 2,
                borderBottom: '1px solid ' + theme.palette.divider,
            }}>
            <div style={{ minWidth: 0, width: '100%' }}>
                <Typography variant="h4">{t('page.home.toolbar.title')}</Typography>
                <Typography
                    variant="body2"
                    sx={{
                        color: theme.palette.text.secondary,
                        width: '100%',
                        overflow: 'hidden',
                        whiteSpace: 'nowrap',
                        textOverflow: 'ellipsis',
                    }}>
                    {t('page.home.toolbar.subtitle')} (v{import.meta.env.VITE_APP_VERSION})
                </Typography>
            </div>
        </Toolbar>
    );
};

const Home: React.FC = () => {
    return (
        <BasePage toolbar={<HomeToolbar />}>
            <Grid container spacing={2}>
                <Grid size={12} sx={{ mb: 1 }}></Grid>
            </Grid>
        </BasePage>
    );
};

export default Home;
