import React from 'react';
import { useTranslation } from 'react-i18next';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Icon from '@mui/material/Icon';
import { useMuiMessageDialog, useResourceApiService } from 'reactlib';

// Accions de nivell de recurs (no lligades a cap propietat concreta), equivalents al menú
// desplegable de config.jsp: sincronitzar amb JBoss i reiniciar les tasques en segon pla.
export const PropietatsActions: React.FC = () => {
    const { t } = useTranslation();
    const { artifactAction } = useResourceApiService('configResource');
    const [showMessageDialog, messageDialogComponent] = useMuiMessageDialog();
    const [loadingSync, setLoadingSync] = React.useState(false);
    const [loadingReiniciar, setLoadingReiniciar] = React.useState(false);

    const handleSync = async () => {
        setLoadingSync(true);
        try {
            const editedKeys = (await artifactAction(null, { code: 'syncFromJBoss' })) as unknown as string[];
            await showMessageDialog(
                t('page.propietats.actions.sync.okTitle'),
                t('page.propietats.actions.sync.okMessage', { count: editedKeys?.length ?? 0 }),
            );
        } catch {
            await showMessageDialog(t('page.propietats.actions.sync.errorTitle'), t('page.propietats.actions.sync.errorMessage'));
        } finally {
            setLoadingSync(false);
        }
    };

    const handleReiniciarTasques = async () => {
        setLoadingReiniciar(true);
        try {
            await artifactAction(null, { code: 'reiniciarTasques' });
            await showMessageDialog(
                t('page.propietats.actions.reiniciarTasques.okTitle'),
                t('page.propietats.actions.reiniciarTasques.okMessage'),
            );
        } catch {
            await showMessageDialog(
                t('page.propietats.actions.reiniciarTasques.errorTitle'),
                t('page.propietats.actions.reiniciarTasques.errorMessage'),
            );
        } finally {
            setLoadingReiniciar(false);
        }
    };

    return (
        <Box sx={{ display: 'flex', gap: 1 }}>
            <Button
                variant="outlined"
                size="small"
                startIcon={<Icon fontSize="small">sync</Icon>}
                onClick={handleSync}
                disabled={loadingSync}
            >
                {t('page.propietats.actions.sync.button')}
            </Button>
            <Button
                variant="outlined"
                size="small"
                startIcon={<Icon fontSize="small">restart_alt</Icon>}
                onClick={handleReiniciarTasques}
                disabled={loadingReiniciar}
            >
                {t('page.propietats.actions.reiniciarTasques.button')}
            </Button>
            {messageDialogComponent}
        </Box>
    );
};

export default PropietatsActions;
