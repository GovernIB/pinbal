import React from 'react';
import { useTranslation } from 'react-i18next';
import Dialog from '@mui/material/Dialog';
import DialogTitle from '@mui/material/DialogTitle';
import DialogContent from '@mui/material/DialogContent';
import DialogActions from '@mui/material/DialogActions';
import Button from '@mui/material/Button';
import Icon from '@mui/material/Icon';
import TextField from '@mui/material/TextField';

// Diàleg simple de només lectura per mostrar el contingut XML cru d'una petició o resposta,
// equivalent al modal-missatge-xml del JSP (consultaXml.jsp).
const XmlViewerDialog: React.FC<{
    open: boolean;
    title: string;
    xml?: string;
    onClose: () => void;
}> = ({ open, title, xml, onClose }) => {
    const { t } = useTranslation();
    const handleCopy = () => {
        if (xml) navigator.clipboard.writeText(xml);
    };
    return (
        <Dialog open={open} onClose={onClose} fullWidth maxWidth="lg">
            <DialogTitle>{title}</DialogTitle>
            <DialogContent>
                <TextField
                    value={xml ?? ''}
                    slotProps={{ input: { readOnly: true } }}
                    multiline
                    minRows={16}
                    maxRows={24}
                    fullWidth
                    sx={{ '& textarea': { fontFamily: 'monospace', fontSize: '0.85rem' } }}
                />
            </DialogContent>
            <DialogActions>
                <Button startIcon={<Icon>content_copy</Icon>} onClick={handleCopy}>
                    {t('comu.clipboard.copy')}
                </Button>
                <Button variant="outlined" onClick={onClose}>
                    {t('page.consulta.detall.tancar')}
                </Button>
            </DialogActions>
        </Dialog>
    );
};

export default XmlViewerDialog;
