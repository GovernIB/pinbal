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
    { field: 'nom', flex: 2 },
    { field: 'cif', flex: 1 },
    { field: 'dataBaixa', flex: 1 },
];

const springFilterBuilder = (data: any) => {
    return filterBuilder.and(
        filterBuilder.like('nom', data?.nom),
        filterBuilder.like('cif', data?.cif),
    );
};

const ContentFilter: React.FC = () => {
    const { t } = useTranslation();
    const filterApiRef = useFilterApiContext();
    const handleButtonClick = () => filterApiRef.current?.clear();
    return (
        <Grid container spacing={2}>
            <GridFormField size={3} name="nom" label={t('page.emissorCerts.grid.filter.nom')} />
            <GridFormField size={3} name="cif" label={t('page.emissorCerts.grid.filter.cif')} />
            <Grid size={0.5} sx={{ textAlign: 'center' }}>
                <IconButton onClick={handleButtonClick} title={t('comu.netejarFiltre')}>
                    <Icon>filter_alt_off</Icon>
                </IconButton>
            </Grid>
        </Grid>
    );
};

export const EmissorCertGrid: React.FC = () => {
    const { t } = useTranslation();
    const filterDataGridProps = useDatagridFilterProps(
        'emissorCertResource',
        'FILTER_EMISSORCERT',
        springFilterBuilder,
        <ContentFilter />,
    );
    const pageSizeOptionsDataGridProps = useDatagridPageSizeOptionsProps();
    const columnsWithLabels = React.useMemo(
        () => columns.map((column) => ({ ...column, headerName: t(`page.emissorCerts.grid.column.${column.field}`) })),
        [t],
    );
    return (
        <GridPage autoHeight={pageSizeOptionsDataGridProps.autoHeight}>
            <MuiDataGrid
                title={t('page.emissorCerts.grid.title')}
                resourceName="emissorCertResource"
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

export default EmissorCertGrid;
