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
    { field: 'nom', flex: 1.5 },
    { field: 'valor', flex: 2 },
];

const springFilterBuilder = (data: any) => {
    return filterBuilder.and(
        filterBuilder.like('nom', data?.nom),
        filterBuilder.like('valor', data?.valor),
    );
};

const ContentFilter: React.FC = () => {
    const { t } = useTranslation();
    const filterApiRef = useFilterApiContext();
    const handleButtonClick = () => filterApiRef.current?.clear();
    return (
        <Grid container spacing={2}>
            <GridFormField size={3} name="nom" label={t('page.paramConfs.grid.filter.nom')} />
            <GridFormField size={3} name="valor" label={t('page.paramConfs.grid.filter.valor')} />
            <Grid size={0.5} sx={{ textAlign: 'center' }}>
                <IconButton onClick={handleButtonClick} title={t('comu.netejarFiltre')}>
                    <Icon>filter_alt_off</Icon>
                </IconButton>
            </Grid>
        </Grid>
    );
};

export const ParamConfGrid: React.FC = () => {
    const { t } = useTranslation();
    const filterDataGridProps = useDatagridFilterProps(
        'paramConfResource',
        'FILTER_PARAMCONF',
        springFilterBuilder,
        <ContentFilter />,
    );
    const pageSizeOptionsDataGridProps = useDatagridPageSizeOptionsProps();
    const columnsWithLabels = React.useMemo(
        () => columns.map((column) => ({ ...column, headerName: t(`page.paramConfs.grid.column.${column.field}`) })),
        [t],
    );
    return (
        <GridPage autoHeight={pageSizeOptionsDataGridProps.autoHeight}>
            <MuiDataGrid
                title={t('page.paramConfs.grid.title')}
                resourceName="paramConfResource"
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

export default ParamConfGrid;
