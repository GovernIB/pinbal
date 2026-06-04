import { useTranslation } from 'react-i18next';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import CancelIcon from '@mui/icons-material/Cancel';
import ErrorIcon from '@mui/icons-material/Error';
import { Chip } from '@mui/material';

type Estat = 'ERROR' | 'WARN' | 'OK';

type ChipEstatProps = {
    estat: Estat;
    label?: string;
};

const ChipEstat = ({ estat, label }: ChipEstatProps) => {
    const { t } = useTranslation();

    const getLabel = () => {
        if (estat === 'ERROR') return t('page.integracio.detall.estatEnum.error');
        if (estat === 'WARN') return t('page.integracio.detall.estatEnum.warn');
        if (estat === 'OK') return t('page.integracio.detall.estatEnum.ok');
    };

    const getIcon = () => {
        if (estat === 'ERROR') return <CancelIcon />;
        if (estat === 'WARN') return <ErrorIcon />;
        if (estat === 'OK') return <CheckCircleIcon />;
    };

    const getColor = () => {
        if (estat === 'ERROR') return 'error';
        if (estat === 'WARN') return 'warning';
        if (estat === 'OK') {
            return 'success';
        }
    };

    return (
        <Chip
            label={label ?? getLabel()}
            color={getColor()}
            size="small"
            icon={getIcon()}
            sx={{ px: 0.5 }}
        />
    );
};
export default ChipEstat;
