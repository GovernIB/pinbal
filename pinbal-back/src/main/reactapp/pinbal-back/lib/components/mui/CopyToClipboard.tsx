import React from 'react';
import Icon from '@mui/material/Icon';
import Button from '@mui/material/Button';
import IconButton from '@mui/material/IconButton';
import Snackbar from '@mui/material/Snackbar';
import Alert from '@mui/material/Alert';
import { useBaseAppContext } from '../BaseAppContext';

export type CopyToClipboardProps = {
    /** Referència a l'element HTML del qual es vol copiar el contingut */
    targetRef: React.RefObject<HTMLElement>;
    /** Si true, copia el HTML (innerHTML), si false, només el text (innerText) */
    copyHtml?: boolean;
    /** Si true, mostra un botó petit amb icona (en comptes del botó complet) */
    iconOnly?: boolean;
    /** Text personalitzat per al missatge del Snackbar */
    successMessage?: string;
    /** Propietats comunes per a tots els components FormField de dins aquest component */
    buttonComponentProps?: any;
};

export const CopyToClipboard = (props: CopyToClipboardProps) => {
    const {
        targetRef,
        copyHtml = false,
        iconOnly = false,
        successMessage,
        buttonComponentProps,
    } = props;
    const { t } = useBaseAppContext();
    const [open, setOpen] = React.useState<boolean>(false);
    const icon = <Icon>content_copy</Icon>;
    const handleClick = async () => {
        try {
            const content = copyHtml
                ? targetRef.current?.innerHTML || ''
                : targetRef.current?.innerText || '';
            if (content) {
                await navigator.clipboard.writeText(content);
                setOpen(true);
            }
        } catch (error) {
            console.error(t('copyToClipboard.error') + ':', error);
        }
    };
    return (
        <>
            {iconOnly ? (
                <IconButton
                    title={t('copyToClipboard.copy')}
                    onClick={() => handleClick()}
                    {...buttonComponentProps}>
                    {icon}
                </IconButton>
            ) : (
                <Button startIcon={icon} onClick={() => handleClick()} {...buttonComponentProps}>
                    {t('copyToClipboard.copy')}
                </Button>
            )}
            <Snackbar
                open={open}
                anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
                autoHideDuration={2000}
                onClose={() => setOpen(false)}>
                <Alert
                    severity="success"
                    variant="filled"
                    onClose={() => setOpen(false)}
                    sx={{ width: '100%' }}>
                    {successMessage ?? t('copyToClipboard.default')}
                </Alert>
            </Snackbar>
        </>
    );
};

export default CopyToClipboard;
