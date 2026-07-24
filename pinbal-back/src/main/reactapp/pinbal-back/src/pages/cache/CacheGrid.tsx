import React from 'react';
import { useTranslation } from 'react-i18next';
import Button from '@mui/material/Button';
import Icon from '@mui/material/Icon';
import {
    GridPage,
    MuiDataGrid,
    MuiDataGridColDef,
    useMuiDataGridApiRef,
    useMuiMessageDialog,
    useResourceApiService,
} from 'reactlib';

const columns: MuiDataGridColDef[] = [
    { field: 'codi', flex: 2 },
    { field: 'localHeapSize', flex: 1 },
];

const BuidarTotesButton: React.FC<{ onDone: () => void }> = ({ onDone }) => {
    const { t } = useTranslation();
    const { artifactAction } = useResourceApiService('cacheResource');
    const [showMessageDialog, messageDialogComponent] = useMuiMessageDialog();
    const [loading, setLoading] = React.useState(false);

    const handleClick = async () => {
        setLoading(true);
        try {
            await artifactAction(null, { code: 'buidarTotesCaches' });
            onDone();
            await showMessageDialog(t('page.caches.grid.buidarTotes.okTitle'), t('page.caches.grid.buidarTotes.okMessage'));
        } catch {
            await showMessageDialog(t('page.caches.grid.buidarTotes.errorTitle'), t('page.caches.grid.buidarTotes.errorMessage'));
        } finally {
            setLoading(false);
        }
    };

    return (
        <>
            <Button
                variant="outlined"
                startIcon={<Icon>delete_sweep</Icon>}
                onClick={handleClick}
                disabled={loading}
            >
                {t('page.caches.grid.buidarTotes.button')}
            </Button>
            {messageDialogComponent}
        </>
    );
};

export const CacheGrid: React.FC = () => {
    const { t } = useTranslation();
    const { artifactAction } = useResourceApiService('cacheResource');
    const dataGridApiRef = useMuiDataGridApiRef();
    const columnsWithLabels = React.useMemo(
        () => columns.map((column) => ({ ...column, headerName: t(`page.caches.grid.column.${column.field}`) })),
        [t],
    );
    const handleRefresh = () => dataGridApiRef.current?.refresh();
    const rowAdditionalActions = [
        {
            icon: 'delete',
            label: t('page.caches.grid.buidar'),
            onClick: (id: any) => artifactAction(null, { code: 'buidarCache', data: { ids: [id] } }).then(handleRefresh),
        },
    ];
    return (
        <GridPage>
            <MuiDataGrid
                apiRef={dataGridApiRef}
                title={t('page.caches.grid.title')}
                resourceName="cacheResource"
                columns={columnsWithLabels}
                toolbarType="upper"
                toolbarHideQuickFilter
                toolbarHideCreate
                rowHideUpdateButton
                rowHideDeleteButton
                rowAdditionalActions={rowAdditionalActions}
                toolbarElementsWithPositions={[{ position: 1, element: <BuidarTotesButton onDone={handleRefresh} /> }]}
            />
        </GridPage>
    );
};

export default CacheGrid;
