import React from 'react';
import { useTranslation } from 'react-i18next';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import { Grid } from '@mui/material';
import {
    GridPage,
    MuiDataGrid,
    useFilterApiContext,
    springFilterBuilder as filterBuilder,
} from 'reactlib';
import GridFormField from '../../components/GridFormField';
import { formatStartOfDay, formatEndOfDay } from '../../utils/dateUtils';
import { useDatagridFilterProps, useDatagridPageSizeOptionsProps } from '../../hooks/useDataGrid';

const columns = [
    { field: 'scspPeticionId' },
    { field: 'creacioData' },
    { field: 'creacioUsuariNomCodi' },
    { field: 'funcionariNomAmbDocument' },
    { field: 'procedimentCodiNom' },
    { field: 'serveiCodiNom' },
    { field: 'estat' },
    { field: 'dataEsperadaResposta' },
    { field: 'entitat' },
];

const springFilterBuilder = (data: any) => {
    return filterBuilder.and(
        data?.entitatId && filterBuilder.eq('entitat.id', `${data.entitatId}`),
        filterBuilder.like('scspPeticionId', data?.scspPeticionId),
        filterBuilder.like('procedimentCodiNom', data?.procedimentCodiNom),
        filterBuilder.like('serveiCodiNom', data?.serveiCodiNom),
        data?.estat && filterBuilder.eq('estat', `'${data.estat}'`),
        data?.dataInici && filterBuilder.gte('creacioData', `'${formatStartOfDay(data?.dataInici)}'`),
        data?.dataFi && filterBuilder.lte('creacioData', `'${formatEndOfDay(data?.dataFi)}'`),
        filterBuilder.like('funcionariNomAmbDocument', data?.funcionariNomAmbDocument),
        data?.recobriment !== undefined && data?.recobriment !== null && data?.recobriment !== ''
            ? filterBuilder.eq('recobriment', `'${data.recobriment}'`)
            : undefined,
        data?.multiple !== undefined && data?.multiple !== null && data?.multiple !== ''
            ? filterBuilder.eq('multiple', `'${data.multiple}'`)
            : undefined,
    );
};

const ContentFilter: React.FC = () => {
    const filterApiRef = useFilterApiContext();
    const { t } = useTranslation();
    const [advancedFilter, setAdvancedFilter] = React.useState(false);

    const handleClearClick = () => {
        filterApiRef.current?.clear();
    };
    const handleAdvancedFilterClick = () => {
        setAdvancedFilter(!advancedFilter);
    };

    return (
        <Grid container spacing={1}>
            <GridFormField size={3} name="entitatId" />
            <GridFormField size={3} name="scspPeticionId" />
            <GridFormField size={3} name="estat" />
            <GridFormField size={1.5} name="dataInici" />
            <GridFormField size={1.5} name="dataFi" />
            {advancedFilter && (
                <>
                    <GridFormField size={3} name="procedimentCodiNom" />
                    <GridFormField size={3} name="serveiCodiNom" />
                    <GridFormField size={3} name="funcionariNomAmbDocument" />
                    <GridFormField size={1.5} name="recobriment" />
                    <GridFormField size={1.5} name="multiple" />
                </>
            )}
            <Grid size={0.5} sx={{ textAlign: 'center' }}>
                <IconButton onClick={handleClearClick} title={t('comu.netejarFiltre')}>
                    <Icon>filter_alt_off</Icon>
                </IconButton>
            </Grid>
            <Grid size={0.5} sx={{ textAlign: 'center' }}>
                <IconButton
                    onClick={handleAdvancedFilterClick}
                    title={t(advancedFilter ? 'comu.tancarFiltreAvançat' : 'comu.obrirFiltreAvançat')}
                >
                    <Icon sx={{ transform: advancedFilter ? 'rotate(180deg)' : 'none' }}>filter_list</Icon>
                </IconButton>
            </Grid>
        </Grid>
    );
};

const AdminConsultaGrid: React.FC = () => {
    const { t } = useTranslation();
    const filterDataGridProps = useDatagridFilterProps(
        'consultaAdminResource',
        'FILTER_CONSULTA_ADMIN',
        springFilterBuilder,
        <ContentFilter />,
    );
    const pageSizeOptionsDataGridProps = useDatagridPageSizeOptionsProps();

    return (
        <GridPage autoHeight={pageSizeOptionsDataGridProps.autoHeight}>
            <MuiDataGrid
                title={t('page.admin.consulta.grid.title')}
                resourceName="consultaAdminResource"
                columns={columns}
                defaultSortModel={[{ field: 'creacioData', sort: 'desc' }]}
                paginationActive
                persistentStateActive
                persistentStateClearPageSortPropsOnTopLevelRouteChange
                {...filterDataGridProps}
                {...pageSizeOptionsDataGridProps}
            />
        </GridPage>
    );
};

export default AdminConsultaGrid;
