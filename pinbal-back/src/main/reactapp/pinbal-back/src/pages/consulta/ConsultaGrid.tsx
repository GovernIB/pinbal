import React from 'react';
import { useTranslation } from 'react-i18next';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import ToggleButton from '@mui/material/ToggleButton';
import ToggleButtonGroup from '@mui/material/ToggleButtonGroup';
import { Grid } from '@mui/material';
import {
    GridPage,
    MuiDataGrid,
    useFilterApiContext,
    useMuiFormDialogApiRef,
    springFilterBuilder as filterBuilder,
} from 'reactlib';
import GridFormField from '../../components/GridFormField';
import { formatStartOfDay, formatEndOfDay } from '../../utils/dateUtils';
import { useDatagridFilterProps, useDatagridPageSizeOptionsProps } from '../../hooks/useDataGrid';
import { usePinbalContext, ROLE_ADMIN } from '../../components/PinbalContext';
import ConsultaDetallDialog from './ConsultaDetallDialog';

type Origen = 'recents' | 'historic';

const columnFields = [
    'scspPeticionId',
    'creacioData',
    'creacioUsuariNomCodi',
    'funcionariNomAmbDocument',
    'procedimentCodiNom',
    'serveiCodiNom',
    'estat',
    'dataEsperadaResposta',
    'entitat',
];

// Selector "Recents / Històric" (canvia el recurs) i "Simples / Múltiples" (camp multiple del filtre).
const ContentFilter: React.FC<{
    origen: Origen;
    setOrigen: (o: Origen) => void;
    isAdmin: boolean;
}> = ({ origen, setOrigen, isAdmin }) => {
    const filterApiRef = useFilterApiContext();
    const { t } = useTranslation();
    const [advancedFilter, setAdvancedFilter] = React.useState(false);

    const handleClearClick = () => {
        filterApiRef.current?.clear();
    };
    const handleAdvancedFilterClick = () => {
        setAdvancedFilter(!advancedFilter);
    };
    const handleOrigenChange = (_e: React.MouseEvent<HTMLElement>, value: Origen | null) => {
        if (value !== null) setOrigen(value);
    };

    return (
        <Grid container spacing={1} alignItems="center">
            <Grid>
                <ToggleButtonGroup value={origen} exclusive size="small" onChange={handleOrigenChange}>
                    <ToggleButton value="recents">{t('page.consulta.grid.origen.recents')}</ToggleButton>
                    <ToggleButton value="historic">{t('page.consulta.grid.origen.historic')}</ToggleButton>
                </ToggleButtonGroup>
            </Grid>
            <GridFormField size={2} name="multiple" label={t('page.consulta.grid.filter.multiple')} />
            {isAdmin && (
                <GridFormField size={2.5} name="entitatId" label={t('page.consulta.grid.filter.entitatId')} />
            )}
            <GridFormField size={2} name="scspPeticionId" label={t('page.consulta.grid.filter.scspPeticionId')} />
            <GridFormField size={1.5} name="estat" label={t('page.consulta.grid.filter.estat')} />
            <GridFormField size={1.5} name="dataInici" label={t('page.consulta.grid.filter.dataInici')} />
            <GridFormField size={1.5} name="dataFi" label={t('page.consulta.grid.filter.dataFi')} />
            {advancedFilter && (
                <>
                    <GridFormField size={3} name="serveiCodiNom" label={t('page.consulta.grid.filter.serveiCodiNom')} />
                    <GridFormField
                        size={3}
                        name="funcionariNomAmbDocument"
                        label={t('page.consulta.grid.filter.funcionariNomAmbDocument')}
                    />
                    <GridFormField size={2} name="recobriment" label={t('page.consulta.grid.filter.recobriment')} />
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

const ConsultaGrid: React.FC = () => {
    const { t } = useTranslation();
    const { currentRole, currentEntitatId } = usePinbalContext();
    const isAdmin = currentRole === ROLE_ADMIN;
    const [origen, setOrigen] = React.useState<Origen>('recents');
    const resourceName = origen === 'recents' ? 'consultaResource' : 'historicConsultaResource';
    const detailDialogApiRef = useMuiFormDialogApiRef();

    const columns = React.useMemo(
        () => columnFields.map((field) => ({ field, headerName: t(`page.consulta.grid.column.${field}`) })),
        [t],
    );

    // L'entitat la decideix el filtre per a l'admin; per a la resta s'usa l'entitat seleccionada.
    const springFilterBuilder = (data: any) => {
        const entitatId = data?.entitatId ?? currentEntitatId;
        return filterBuilder.and(
            entitatId ? filterBuilder.eq('entitat.id', `${entitatId}`) : undefined,
            filterBuilder.like('scspPeticionId', data?.scspPeticionId),
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

    const filterDataGridProps = useDatagridFilterProps(
        resourceName,
        'FILTER_CONSULTA',
        springFilterBuilder,
        <ContentFilter origen={origen} setOrigen={setOrigen} isAdmin={isAdmin} />,
    );
    const pageSizeOptionsDataGridProps = useDatagridPageSizeOptionsProps();

    const rowAdditionalActions = [
        {
            icon: 'visibility',
            title: t('page.consulta.detall.veure'),
            onClick: (id: any) => {
                detailDialogApiRef.current?.show(id).catch(() => null);
            },
        },
    ];

    return (
        <GridPage autoHeight={pageSizeOptionsDataGridProps.autoHeight}>
            <MuiDataGrid
                key={resourceName}
                title={t('page.consulta.grid.title')}
                resourceName={resourceName}
                columns={columns}
                defaultSortModel={[{ field: 'creacioData', sort: 'desc' }]}
                paginationActive
                persistentStateActive
                persistentStateClearPageSortPropsOnTopLevelRouteChange
                rowAdditionalActions={rowAdditionalActions}
                {...filterDataGridProps}
                {...pageSizeOptionsDataGridProps}
            />
            <ConsultaDetallDialog apiRef={detailDialogApiRef} resourceName={resourceName} origen={origen} />
        </GridPage>
    );
};

export default ConsultaGrid;
