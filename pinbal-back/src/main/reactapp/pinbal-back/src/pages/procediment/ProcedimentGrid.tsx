import React from 'react';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import {
    GridPage,
    MuiDataGrid,
    MuiDataGridColDef,
    springFilterBuilder as filterBuilder,
    useFilterApiContext,
} from 'reactlib';
import GridFormField from '../../components/GridFormField';
import { usePinbalContext } from '../../components/PinbalContext';
import { useDatagridFilterProps, useDatagridPageSizeOptionsProps } from '../../hooks/useDataGrid';

const columns: MuiDataGridColDef[] = [
    { field: 'codi', flex: 1 },
    { field: 'nom', flex: 2 },
    { field: 'departament', flex: 1.5 },
    { field: 'organGestor', flex: 1.5 },
    { field: 'codiSia', flex: 1 },
    { field: 'actiu', flex: 0.6, type: 'boolean' },
];

const springFilterBuilder = (data: any) => {
    return filterBuilder.and(
        filterBuilder.like('codi', data?.codi),
        filterBuilder.like('nom', data?.nom),
        filterBuilder.eq('actiu', `'${data?.actiu}'`),
    );
};

const ContentFilter: React.FC = () => {
    const { t } = useTranslation();
    const filterApiRef = useFilterApiContext();
    const handleButtonClick = () => filterApiRef.current?.clear();
    return (
        <Grid container spacing={2}>
            <GridFormField size={2} name="codi" label={t('page.procediments.grid.filter.codi')} />
            <GridFormField size={6} name="nom" label={t('page.procediments.grid.filter.nom')} />
            <GridFormField size={1.5} name="actiu" label={t('page.procediments.grid.filter.actiu')} />
            <Grid size={0.5} sx={{ textAlign: 'center' }}>
                <IconButton onClick={handleButtonClick} title={t('comu.netejarFiltre')}>
                    <Icon>filter_alt_off</Icon>
                </IconButton>
            </Grid>
        </Grid>
    );
};

export const ProcedimentGrid: React.FC = () => {
    const { t } = useTranslation();
    const { currentEntitatId } = usePinbalContext();
    const filterDataGridProps = useDatagridFilterProps(
        'procedimentResource',
        'FILTER_PROCEDIMENT',
        springFilterBuilder,
        <ContentFilter />,
    );
    const pageSizeOptionsDataGridProps = useDatagridPageSizeOptionsProps();
    const columnsWithLabels = React.useMemo(
        () => columns.map((column) => ({ ...column, headerName: t(`page.procediments.grid.column.${column.field}`) })),
        [t],
    );
    return (
        <GridPage autoHeight={pageSizeOptionsDataGridProps.autoHeight}>
            <MuiDataGrid
                title={t('page.procediments.grid.title')}
                resourceName="procedimentResource"
                fixedFilter={'entitat.id:' + currentEntitatId}
                columns={columnsWithLabels}
                paginationActive
                persistentStateActive
                persistentStateClearPageSortPropsOnTopLevelRouteChange
                {...filterDataGridProps}
                {...pageSizeOptionsDataGridProps}
                toolbarType="upper"
                toolbarCreateLink="form"
                rowLink="form/{{id}}"
                rowUpdateLink="form/{{id}}"
            />
        </GridPage>
    );
};

export default ProcedimentGrid;
