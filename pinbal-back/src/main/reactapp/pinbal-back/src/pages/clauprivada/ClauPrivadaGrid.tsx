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
    { field: 'alies', flex: 1.3 },
    { field: 'nom', flex: 1.6 },
    { field: 'numSerie', flex: 1.3 },
    { field: 'dataAlta', flex: 0.9 },
    { field: 'dataBaixa', flex: 0.9 },
    { field: 'organisme', flex: 1.3, fieldType: 'reference' },
    { field: 'perEntitat', flex: 0.7, type: 'boolean' },
];

const springFilterBuilder = (data: any) => {
    return filterBuilder.and(
        filterBuilder.like('alies', data?.alies),
        filterBuilder.like('nom', data?.nom),
        filterBuilder.like('numSerie', data?.numSerie),
    );
};

const ContentFilter: React.FC = () => {
    const { t } = useTranslation();
    const filterApiRef = useFilterApiContext();
    const handleButtonClick = () => filterApiRef.current?.clear();
    return (
        <Grid container spacing={2}>
            <GridFormField size={2.5} name="alies" label={t('page.clauPrivades.grid.filter.alies')} />
            <GridFormField size={2.5} name="nom" label={t('page.clauPrivades.grid.filter.nom')} />
            <GridFormField size={2.5} name="numSerie" label={t('page.clauPrivades.grid.filter.numSerie')} />
            <Grid size={0.5} sx={{ textAlign: 'center' }}>
                <IconButton onClick={handleButtonClick} title={t('comu.netejarFiltre')}>
                    <Icon>filter_alt_off</Icon>
                </IconButton>
            </Grid>
        </Grid>
    );
};

export const ClauPrivadaGrid: React.FC = () => {
    const { t } = useTranslation();
    const filterDataGridProps = useDatagridFilterProps(
        'clauPrivadaResource',
        'FILTER_CLAUPRIVADA',
        springFilterBuilder,
        <ContentFilter />,
    );
    const pageSizeOptionsDataGridProps = useDatagridPageSizeOptionsProps();
    const columnsWithLabels = React.useMemo(
        () => columns.map((column) => ({ ...column, headerName: t(`page.clauPrivades.grid.column.${column.field}`) })),
        [t],
    );
    return (
        <GridPage autoHeight={pageSizeOptionsDataGridProps.autoHeight}>
            <MuiDataGrid
                title={t('page.clauPrivades.grid.title')}
                resourceName="clauPrivadaResource"
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

export default ClauPrivadaGrid;
