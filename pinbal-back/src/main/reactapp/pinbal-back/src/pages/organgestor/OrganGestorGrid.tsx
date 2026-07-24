import React from 'react';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import Button from '@mui/material/Button';
import {
    GridPage,
    MuiDataGrid,
    MuiDataGridColDef,
    springFilterBuilder as filterBuilder,
    useFilterApiContext,
    useMuiDataGridApiRef,
    useMuiMessageDialog,
    useResourceApiService,
} from 'reactlib';
import GridFormField from '../../components/GridFormField';
import { usePinbalContext } from '../../components/PinbalContext';
import { useDatagridFilterProps, useDatagridPageSizeOptionsProps } from '../../hooks/useDataGrid';

const columns: MuiDataGridColDef[] = [
    { field: 'codi', flex: 1 },
    { field: 'nom', flex: 3 },
    { field: 'pare', flex: 1.5 },
    { field: 'estat', flex: 1 },
    { field: 'actiu', flex: 0.6, type: 'boolean' },
];

const springFilterBuilder = (data: any) => {
    return filterBuilder.and(
        filterBuilder.like('codi', data?.codi),
        filterBuilder.like('nom', data?.nom),
    );
};

const ContentFilter: React.FC = () => {
    const { t } = useTranslation();
    const filterApiRef = useFilterApiContext();
    const handleButtonClick = () => filterApiRef.current?.clear();
    return (
        <Grid container spacing={2}>
            <GridFormField size={2} name="codi" label={t('page.organGestors.grid.filter.codi')} />
            <GridFormField size={6} name="nom" label={t('page.organGestors.grid.filter.nom')} />
            <Grid size={0.5} sx={{ textAlign: 'center' }}>
                <IconButton onClick={handleButtonClick} title={t('comu.netejarFiltre')}>
                    <Icon>filter_alt_off</Icon>
                </IconButton>
            </Grid>
        </Grid>
    );
};

const SyncDir3Button: React.FC<{ onSynced: () => void }> = ({ onSynced }) => {
    const { t } = useTranslation();
    const { currentEntitatId } = usePinbalContext();
    const { artifactAction } = useResourceApiService('organGestorResource');
    const [showMessageDialog, messageDialogComponent] = useMuiMessageDialog();
    const [loading, setLoading] = React.useState(false);

    const handleClick = async () => {
        setLoading(true);
        try {
            await artifactAction(null, { code: 'syncDir3', data: { entitatId: currentEntitatId } });
            await showMessageDialog(t('page.organGestors.grid.syncDir3.okTitle'), t('page.organGestors.grid.syncDir3.okMessage'));
            onSynced();
        } catch {
            await showMessageDialog(t('page.organGestors.grid.syncDir3.errorTitle'), t('page.organGestors.grid.syncDir3.errorMessage'));
        } finally {
            setLoading(false);
        }
    };

    return (
        <>
            <Button variant="outlined" startIcon={<Icon>sync</Icon>} onClick={handleClick} loading={loading}>
                {t('page.organGestors.grid.syncDir3.button')}
            </Button>
            {messageDialogComponent}
        </>
    );
};

export const OrganGestorGrid: React.FC = () => {
    const { t } = useTranslation();
    const { currentEntitatId } = usePinbalContext();
    const gridApiRef = useMuiDataGridApiRef();
    const filterDataGridProps = useDatagridFilterProps(
        'organGestorResource',
        'FILTER_ORGAN_GESTOR',
        springFilterBuilder,
        <ContentFilter />,
    );
    const pageSizeOptionsDataGridProps = useDatagridPageSizeOptionsProps();
    const columnsWithLabels = React.useMemo(
        () => columns.map((column) => ({ ...column, headerName: t(`page.organGestors.grid.column.${column.field}`) })),
        [t],
    );
    const handleSynced = () => gridApiRef.current?.refresh();
    return (
        <GridPage autoHeight={pageSizeOptionsDataGridProps.autoHeight}>
            <MuiDataGrid
                apiRef={gridApiRef}
                title={t('page.organGestors.grid.title')}
                resourceName="organGestorResource"
                fixedFilter={'entitat.id:' + currentEntitatId}
                columns={columnsWithLabels}
                paginationActive
                persistentStateActive
                persistentStateClearPageSortPropsOnTopLevelRouteChange
                {...filterDataGridProps}
                {...pageSizeOptionsDataGridProps}
                toolbarType="upper"
                toolbarHideCreate
                toolbarAdditionalRow={<SyncDir3Button onSynced={handleSynced} />}
                rowHideUpdateButton
                rowHideDeleteButton
            />
        </GridPage>
    );
};

export default OrganGestorGrid;
