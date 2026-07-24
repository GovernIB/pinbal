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
    { field: 'codi', flex: 1 },
    { field: 'descripcio', flex: 3 },
    { field: 'pinbalEntitatTipus', flex: 1 },
    { field: 'pinbalRoleName', flex: 1 },
    { field: 'actiu', flex: 0.6, type: 'boolean' },
];

const springFilterBuilder = (data: any) => {
    return filterBuilder.and(
        filterBuilder.like('codi', data?.codi),
        filterBuilder.like('descripcio', data?.descripcio),
        filterBuilder.eq('actiu', `'${data?.actiu}'`),
    );
};

const ContentFilter: React.FC = () => {
    const { t } = useTranslation();
    const filterApiRef = useFilterApiContext();
    const handleButtonClick = () => filterApiRef.current?.clear();
    return (
        <Grid container spacing={2}>
            <GridFormField size={2} name="codi" label={t('page.serveis.grid.filter.codi')} />
            <GridFormField size={6} name="descripcio" label={t('page.serveis.grid.filter.descripcio')} />
            <GridFormField size={1.5} name="actiu" label={t('page.serveis.grid.filter.actiu')} />
            <Grid size={0.5} sx={{ textAlign: 'center' }}>
                <IconButton onClick={handleButtonClick} title={t('comu.netejarFiltre')}>
                    <Icon>filter_alt_off</Icon>
                </IconButton>
            </Grid>
        </Grid>
    );
};

export const ServeiGrid: React.FC = () => {
    const { t } = useTranslation();
    const filterDataGridProps = useDatagridFilterProps(
        'serveiResource',
        'FILTER_SERVEI',
        springFilterBuilder,
        <ContentFilter />,
    );
    const pageSizeOptionsDataGridProps = useDatagridPageSizeOptionsProps();
    const columnsWithLabels = React.useMemo(
        () => columns.map((column) => ({ ...column, headerName: t(`page.serveis.grid.column.${column.field}`) })),
        [t],
    );
    return (
        <GridPage autoHeight={pageSizeOptionsDataGridProps.autoHeight}>
            <MuiDataGrid
                title={t('page.serveis.grid.title')}
                resourceName="serveiResource"
                columns={columnsWithLabels}
                paginationActive
                persistentStateActive
                persistentStateClearPageSortPropsOnTopLevelRouteChange
                {...filterDataGridProps}
                {...pageSizeOptionsDataGridProps}
                toolbarType="upper"
                toolbarHideCreate
                rowLink="form/{{id}}"
                rowUpdateLink="form/{{id}}"
                rowHideDeleteButton
            />
        </GridPage>
    );
};

export default ServeiGrid;
