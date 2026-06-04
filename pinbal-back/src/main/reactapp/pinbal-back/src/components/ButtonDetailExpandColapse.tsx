import { Box, Icon, IconButton, Tooltip } from '@mui/material';
import { GridApiPro } from '@mui/x-data-grid-pro';
import { useTranslation } from 'react-i18next';

interface ButtonDetailExpandColapseProps {
    datagridApiRef: React.RefObject<GridApiPro | null>;
}

const ButtonDetailExpandColapse: React.FC<ButtonDetailExpandColapseProps> = (props) => {
    const { t } = useTranslation();
    const { datagridApiRef } = props;

    const handleExpandAll = () => {
        if (datagridApiRef.current) {
            const allIdsArray = datagridApiRef.current.getAllRowIds();
            // Convertir a Set per mantenir la compatibilitat amb el selector
            const allIdsSet = new Set(allIdsArray);
            datagridApiRef.current.setExpandedDetailPanels(allIdsSet);
        }
    };

    const handleCollapseAll = () => {
        if (datagridApiRef.current) {
            datagridApiRef.current.setExpandedDetailPanels(new Set());
        }
    };

    return (
        <Box
            sx={{
                display: 'flex',
                flexDirection: 'row',
                justifyContent: 'center',
                gap: 0.5,
            }}
        >
            <Tooltip title={t('component.ButtonDetailExpandColapse.expandAll')} arrow>
                <IconButton size="small" color="primary" onClick={handleExpandAll}>
                    <Icon fontSize="small">unfold_more</Icon>
                </IconButton>
            </Tooltip>
            <Tooltip title={t('component.ButtonDetailExpandColapse.collapseAll')} arrow>
                <IconButton size="small" color="primary" onClick={handleCollapseAll}>
                    <Icon fontSize="small">unfold_less</Icon>
                </IconButton>
            </Tooltip>
        </Box>
    );
};

export default ButtonDetailExpandColapse;
