import React from 'react';
import { useTranslation } from 'react-i18next';
import Chip from '@mui/material/Chip';
import Icon from '@mui/material/Icon';

// Icones equivalents a les de consulta.jsp (formatState / template-estat): Error, Pendent,
// Processant, Tramitada.
const ESTAT_CONFIG: Record<string, { icon: string; color: 'error' | 'warning' | 'info' | 'success' }> = {
    Error: { icon: 'error', color: 'error' },
    Pendent: { icon: 'schedule', color: 'warning' },
    Processant: { icon: 'autorenew', color: 'info' },
    Tramitada: { icon: 'check_circle', color: 'success' },
};

export const ConsultaEstatChip: React.FC<{ estat?: string; error?: string }> = ({ estat, error }) => {
    const { t } = useTranslation();
    if (!estat) return null;
    const config = ESTAT_CONFIG[estat];
    return (
        <Chip
            size="small"
            variant="outlined"
            color={config?.color}
            icon={config ? <Icon fontSize="small">{config.icon}</Icon> : undefined}
            label={t(`page.consulta.grid.column.estatEnum.${estat}`)}
            title={estat === 'Error' ? error : undefined}
        />
    );
};

export default ConsultaEstatChip;
