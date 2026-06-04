import { Divider, Icon, IconButton } from '@mui/material';
import { useTranslation } from 'react-i18next';
import { MuiDataGridApiRef } from 'reactlib';

const GridToolbarButton: React.FC<{ gridApiRef: MuiDataGridApiRef }> = (props) => {
    const { gridApiRef } = props;
    const { t } = useTranslation();

    const addButtonClick = () => {
        gridApiRef.current?.triggerCreate();
    };
    const refreshButtonClick = () => {
        gridApiRef.current?.refresh();
    };

    return (
        <>
            <Divider orientation="vertical" flexItem sx={{ mx: 1 }} />
            <IconButton
                onClick={addButtonClick}
                title={t('component.GridToolbarButton.add')}
            >
                <Icon>add</Icon>
            </IconButton>
            <IconButton
                onClick={refreshButtonClick}
                title={t('component.GridToolbarButton.refresh')}
            >
                <Icon>refresh</Icon>
            </IconButton>
        </>
    );
};

export default GridToolbarButton;
