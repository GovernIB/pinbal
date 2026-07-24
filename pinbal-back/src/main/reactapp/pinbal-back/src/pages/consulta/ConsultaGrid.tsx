import React from 'react';
import { useTranslation } from 'react-i18next';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import ToggleButton from '@mui/material/ToggleButton';
import ToggleButtonGroup from '@mui/material/ToggleButtonGroup';
import Box from '@mui/material/Box';
import { Grid } from '@mui/material';
import { GridRenderCellParams } from '@mui/x-data-grid-pro';
import {
    GridPage,
    MuiDataGrid,
    MuiDataGridColDef,
    useFilterApiContext,
    useMuiFormDialogApiRef,
    useResourceApiService,
    springFilterBuilder as filterBuilder,
} from 'reactlib';
import GridFormField from '../../components/GridFormField';
import { formatStartOfDay, formatEndOfDay } from '../../utils/dateUtils';
import { useDatagridFilterProps, useDatagridPageSizeOptionsProps } from '../../hooks/useDataGrid';
import { usePinbalContext, ROLE_ADMIN } from '../../components/PinbalContext';
import ConsultaDetallDialog from './ConsultaDetallDialog';
import { downloadArtifactReport, JUSTIFICANT_DISPONIBLE_ESTATS } from './consultaJustificant';
import { ConsultaEstatChip } from './ConsultaEstatChip';

export type Origen = 'recents' | 'historic';

type ConsultaGridRow = {
    scspPeticionId?: string;
    estat?: string;
    justificantEstat?: string;
    multiple?: boolean;
};

type ConsultaRowAction = {
    icon: string;
    label: string;
    showInMenu?: boolean;
    hidden?: (row: ConsultaGridRow) => boolean;
    onClick: (id: number, row: ConsultaGridRow) => void;
};

// Selector "Recents / Històric" (canvia el recurs), mostrat a la barra d'eines de la graella,
// separat visualment del títol per no confondre'l amb l'acció principal de la pantalla.
const OrigenToggle: React.FC<{ origen: Origen; setOrigen: (o: Origen) => void }> = ({ origen, setOrigen }) => {
    const { t } = useTranslation();
    const handleOrigenChange = (_e: React.MouseEvent<HTMLElement>, value: Origen | null) => {
        if (value !== null) setOrigen(value);
    };
    return (
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1.5, ml: 2, pl: 2, borderLeft: 1, borderColor: 'divider' }}>
            <ToggleButtonGroup value={origen} exclusive size="small" onChange={handleOrigenChange}>
                <ToggleButton value="recents">{t('page.consulta.grid.origen.recents')}</ToggleButton>
                <ToggleButton value="historic">{t('page.consulta.grid.origen.historic')}</ToggleButton>
            </ToggleButtonGroup>
        </Box>
    );
};

// Filtres bàsics (mateixos camps que el llistat JSP) sempre visibles, i filtres avançats
// (entitat per a l'administrador, funcionari, recobriment, simples/múltiples) desplegables.
const ContentFilter: React.FC<{ isAdmin: boolean }> = ({ isAdmin }) => {
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
        <Grid container spacing={1} alignItems="center">
            {isAdmin && (
                <GridFormField size={2} name="entitat" label={t('page.consulta.grid.filter.entitat')} />
            )}
            <GridFormField size={2} name="scspPeticionId" label={t('page.consulta.grid.filter.scspPeticionId')} />
            <GridFormField size={2} name="procediment" label={t('page.consulta.grid.filter.procediment')} />
            <GridFormField size={2} name="serveiCodiNom" label={t('page.consulta.grid.filter.serveiCodiNom')} />
            <GridFormField size={1.5} name="estat" label={t('page.consulta.grid.filter.estat')} />
            <GridFormField size={1.2} name="dataInici" label={t('page.consulta.grid.filter.dataInici')} />
            <GridFormField size={1.2} name="dataFi" label={t('page.consulta.grid.filter.dataFi')} />
            <GridFormField size={1.5} name="titularNomComplet" label={t('page.consulta.grid.filter.titularNomComplet')} />
            <GridFormField size={1.5} name="titularDocumentNum" label={t('page.consulta.grid.filter.titularDocumentNum')} />
            {advancedFilter && (
                <>
                    <GridFormField
                        size={3}
                        name="funcionariNomAmbDocument"
                        label={t('page.consulta.grid.filter.funcionariNomAmbDocument')}
                    />
                    <GridFormField size={2} name="recobriment" label={t('page.consulta.grid.filter.recobriment')} />
                    <GridFormField size={2} name="multiple" label={t('page.consulta.grid.filter.multiple')} />
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
    const { artifactReport } = useResourceApiService(resourceName);

    const columns: MuiDataGridColDef[] = React.useMemo(() => {
        const cols: MuiDataGridColDef[] = [
            {
                field: 'scspPeticionId',
                flex: 1.2,
                sortable: true,
                renderCell: (params: GridRenderCellParams) => (
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                        {params.value}
                        {params.row?.recobriment && (
                            <Icon fontSize="small" title={t('page.consulta.grid.column.recobriment')}>
                                content_copy
                            </Icon>
                        )}
                        {params.row?.multiple && (
                            <Icon fontSize="small" title={t('page.consulta.grid.column.multiple')}>
                                dynamic_feed
                            </Icon>
                        )}
                    </Box>
                ),
            },
            { field: 'creacioData', flex: 1 },
            { field: 'procedimentCodiNom', flex: 1.4 },
            { field: 'serveiCodiNom', flex: 1.4 },
            { field: 'titularNomComplet', flex: 1.3, sortable: false },
            {
                field: 'titularDocumentNum',
                flex: 1,
                sortable: false,
                renderCell: (params: GridRenderCellParams) =>
                    [params.row?.titularDocumentTipus, params.value].filter(Boolean).join(' '),
            },
            {
                field: 'estat',
                flex: 0.9,
                sortable: false,
                renderCell: (params: GridRenderCellParams) => (
                    <ConsultaEstatChip estat={params.value} error={params.row?.error} />
                ),
            },
        ];
        if (isAdmin) {
            cols.push(
                { field: 'creacioUsuariNomCodi', flex: 1.1, sortable: false },
                { field: 'funcionariNomAmbDocument', flex: 1.3, sortable: false },
                { field: 'dataEsperadaResposta', flex: 1, sortable: false },
            );
        }
        return cols.map((c) => ({ ...c, headerName: t(`page.consulta.grid.column.${c.field}`) }));
    }, [t, isAdmin]);

    // L'entitat la decideix el filtre per a l'admin; per a la resta s'usa l'entitat seleccionada.
    const springFilterBuilder = (data: any) => {
        const entitatId = data?.entitat?.id ?? currentEntitatId;
        return filterBuilder.and(
            entitatId ? filterBuilder.eq('entitat.id', `${entitatId}`) : undefined,
            filterBuilder.like('scspPeticionId', data?.scspPeticionId),
            data?.procediment?.id ? filterBuilder.eq('procediment.id', `${data.procediment.id}`) : undefined,
            filterBuilder.like('serveiCodiNom', data?.serveiCodiNom),
            data?.estat && filterBuilder.eq('estat', `'${data.estat}'`),
            data?.dataInici && filterBuilder.gte('creacioData', `'${formatStartOfDay(data?.dataInici)}'`),
            data?.dataFi && filterBuilder.lte('creacioData', `'${formatEndOfDay(data?.dataFi)}'`),
            filterBuilder.like('titularNomComplet', data?.titularNomComplet),
            filterBuilder.like('titularDocumentNum', data?.titularDocumentNum),
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
        <ContentFilter isAdmin={isAdmin} />,
    );
    const pageSizeOptionsDataGridProps = useDatagridPageSizeOptionsProps();

    // Només hi ha 2 accions de descàrrega possibles per fila: el PDF del justificant (només si
    // la consulta ha estat tramitada i té justificant disponible, com al JSP) i el ZIP amb els
    // missatges XML (només administrador).
    const rowAdditionalActions: ConsultaRowAction[] = [
        {
            icon: 'visibility',
            label: t('page.consulta.detall.veure'),
            onClick: (id) => {
                detailDialogApiRef.current?.show(id).catch(() => null);
            },
        },
        {
            icon: 'picture_as_pdf',
            label: t('page.consulta.grid.accions.descarregarJustificant'),
            showInMenu: true,
            hidden: (row) =>
                row.estat !== 'Tramitada' || !JUSTIFICANT_DISPONIBLE_ESTATS.includes(row.justificantEstat ?? ''),
            onClick: (id, row) =>
                downloadArtifactReport(artifactReport, id, 'justificant', `justificant_${row.scspPeticionId}.pdf`),
        },
        {
            icon: 'data_object',
            label: t('page.consulta.grid.accions.descarregarZipXmls'),
            showInMenu: true,
            hidden: () => !isAdmin,
            onClick: (id, row) =>
                downloadArtifactReport(artifactReport, id, 'xmlZip', `xmls_${row.scspPeticionId}.zip`),
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
                toolbarType="upper"
                toolbarHideExport={false}
                exportFileType="XLSX"
                rowAdditionalActions={rowAdditionalActions}
                toolbarElementsWithPositions={[
                    { position: 1, element: <OrigenToggle origen={origen} setOrigen={setOrigen} /> },
                ]}
                {...filterDataGridProps}
                {...pageSizeOptionsDataGridProps}
            />
            <ConsultaDetallDialog apiRef={detailDialogApiRef} resourceName={resourceName} origen={origen} />
        </GridPage>
    );
};

export default ConsultaGrid;
