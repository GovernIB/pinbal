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
import { useDatagridFilterProps, useDatagridPageSizeOptionsProps } from '../../hooks/useDataGrid';

const columns: MuiDataGridColDef[] = [
    { field: 'assumpte', flex: 2 },
    { field: 'avisNivell', flex: 1 },
    { field: 'dataInici', flex: 1 },
    { field: 'dataFinal', flex: 1 },
    { field: 'actiu', flex: 0.7, type: 'boolean' },
];

const springFilterBuilder = (data: any) => {
    return filterBuilder.and(
        filterBuilder.like('assumpte', data?.assumpte),
        data?.avisNivell && filterBuilder.eq('avisNivell', `'${data.avisNivell}'`),
        data?.actiu !== undefined && data?.actiu !== null && data?.actiu !== ''
            ? filterBuilder.eq('actiu', `'${data.actiu}'`)
            : undefined,
    );
};

const ContentFilter: React.FC = () => {
    const { t } = useTranslation();
    const filterApiRef = useFilterApiContext();
    const handleButtonClick = () => filterApiRef.current?.clear();
    return (
        <Grid container spacing={2}>
            <GridFormField size={4} name="assumpte" label={t('page.avisos.grid.filter.assumpte')} />
            <GridFormField size={2} name="avisNivell" label={t('page.avisos.grid.filter.avisNivell')} />
            <GridFormField size={1.5} name="actiu" label={t('page.avisos.grid.filter.actiu')} />
            <Grid size={0.5} sx={{ textAlign: 'center' }}>
                <IconButton onClick={handleButtonClick} title={t('comu.netejarFiltre')}>
                    <Icon>filter_alt_off</Icon>
                </IconButton>
            </Grid>
        </Grid>
    );
};

export const AvisGrid: React.FC = () => {
    const { t } = useTranslation();
    const filterDataGridProps = useDatagridFilterProps(
        'avisResource',
        'FILTER_AVIS',
        springFilterBuilder,
        <ContentFilter />,
    );
    const pageSizeOptionsDataGridProps = useDatagridPageSizeOptionsProps();
    const columnsWithLabels = React.useMemo(
        () => columns.map((column) => ({ ...column, headerName: t(`page.avisos.grid.column.${column.field}`) })),
        [t],
    );
    return (
        <GridPage autoHeight={pageSizeOptionsDataGridProps.autoHeight}>
            <MuiDataGrid
                title={t('page.avisos.grid.title')}
                resourceName="avisResource"
                defaultSortModel={[{ field: 'dataInici', sort: 'desc' }]}
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

export default AvisGrid;
