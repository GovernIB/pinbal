import React from 'react';
import {
    GridApiPro as DataGridApi,
    DataGridProProps as DataGridProps,
    GridRowsProp,
    GridRenderCellParams,
    GridRowClassNameParams,
    GridColDef,
    GridColumnResizeParams,
    GridFilterModel,
    GridSortModel,
    GridSortDirection,
    GridPaginationModel,
    GridRowSelectionModel,
    GridRowModesModel,
    GridSlots,
    GridEventListener,
    GridCallbackDetails,
    GridActionsCell,
    GridActionsCellItem,
    useGridApiRef as useMuiDatagridApiRef,
    useGridApiContext,
    useGridSelector,
    gridRowNodeSelector,
    gridColumnFieldsSelector,
    gridEditRowsStateSelector,
} from '@mui/x-data-grid-pro';
import { caES, esES, enUS } from '@mui/x-data-grid/locales';
import Box from '@mui/material/Box';
import Icon from '@mui/material/Icon';
import { capitalize } from '../../../util/text';
import { useDebounce } from '../../../util/useDebounce';
import { formattedFieldValue, isFieldNumericType } from '../../../util/fields';
import * as springFilterBuilder from '../../../util/springFilterBuilder';
import {
    ReactElementWithPosition,
    joinReactElementsWithPositionWithReactElementsWithPositions,
} from '../../../util/reactNodePosition';
import { Form, FormI18nKeys, useFormApiRef } from '../../form/Form';
import { FormField } from '../../form/FormField';
import { useAuthContext } from '../../AuthContext';
import { useBaseAppContext, DialogButton } from '../../BaseAppContext';
import { useMuiBaseAppContext } from '../MuiBaseAppContext';
import { useResourceApiService } from '../../ResourceApiProvider';
import { useResourceApiContext, ResourceType, ExportFileType } from '../../ResourceApiContext';
import {
    useApiDataCommon,
    useDataCommonEditable,
    DataCommonFindArgs,
    DataCommonAdditionalAction,
} from '../datacommon/MuiDataCommon';
import DataQuickFilter from '../datacommon/DataQuickFilter';
import { useDataToolbar, DataToolbarType } from '../datacommon/DataToolbar';
import DataGridRow from './DataGridRow';
import DataGridFooter from './DataGridFooter';
import DataGridNoRowsOverlay from './DataGridNoRowsOverlay';
import DataGridCustomStyle from './DataGridCustomStyle';
import DataGridBulkDelete from './DataGridBulkDelete';
import DataGridContext, {
    MuiDataGridApi,
    MuiDataGridApiRef,
    useDataGridContext,
    DEFAULT_ROW_SELECTION,
} from './DataGridContext';

const CREATE_ROW_ID = '###_CREATE_ID_###';

/**
 * Propietats de les columnes del component MuiDataGrid (també conté totes les propietats de les columnes del DataGrid de MUI).
 */
export type MuiDataGridColDef = GridColDef & {
    /** Nom del camp */
    field: string;
    /** Tipus del camp (sobreescriu el tipus del camp retornat pel backend) */
    fieldType?: string;
    /** Indica si el camp és de tipus divisa */
    currencyType?: boolean;
    /** Codi ISO per a mostrar el símbol de la moneda */
    currencyCode?: string | ((row: any) => string);
    /** Indica el nombre de llocs decimals que s'han de mostrar (només per a tipus divisa) */
    currencyDecimalPlaces?: number | ((row: any) => number);
    /** Indica el codi ISO del locale per a mostrar la divisa */
    currencyLocale?: string | ((row: any) => string);
    /** Indica el nombre de llocs decimals que s'han de mostrar (només per a tipus numèrics) */
    decimalPlaces?: number | ((row: any) => number);
    /** Indica que no s'ha de mostrar l'hora (només per a tipus data) */
    noTime?: boolean;
    /** Indica que no s'ha de mostrar els segons (només per a tipus data) */
    noSeconds?: boolean;
    /** Indica que aquesta columna no s'ha d'incloure a l'exportació */
    exportExcluded?: boolean;
    /** Processa i canvia l'ordenació dels camps (si es retorna undefined vol dir que l'ordenació no canvia) */
    sortProcessor?: (field: string, sort: GridSortDirection) => GridSortModel | undefined;
} & Omit<GridColDef, 'field'>;

/**
 * Propietats del component MuiDataGrid (també conté totes les propietats del DataGrid de MUI).
 */
export type MuiDataGridProps = {
    /** Títol que es mostrarà a la barra d'eines */
    title?: string;
    /** Indica si s'ha de mostrar o no el títol a la barra d'eines */
    titleDisabled?: true;
    /** Subtítol que es mostrarà a la barra d'eines */
    subtitle?: string;
    /** Nom del recurs de l'API REST que es consultarà per a obtenir la informació que es mostrarà a la graella */
    resourceName: string;
    /** Tipus de l'artefacte associat al recurs (aquest atribut només s'ha d'emplenar si volem consultar informació del camp associat al recurs d'un artefacte) */
    resourceType?: ResourceType;
    /** Codi de l'artefacte associat al recurs (aquest atribut només s'ha d'emplenar si volem consultar informació del camp associat al recurs d'un artefacte) */
    resourceTypeCode?: string;
    /** Nom del camp de l'artefacte associat al recurs (aquest atribut només s'ha d'emplenar si volem consultar informació del camp associat al recurs d'un artefacte) */
    resourceFieldName?: string;
    /** Configuració de les columnes de la graella  */
    columns: MuiDataGridColDef[];
    /** Indica si la graella és de només lectura (no es permeten modificacions) */
    readOnly?: true;
    /** Força l'estat de carregant de la graella */
    loading?: true;
    /** Desactiva la primera petició automàtica al backend per a obtenir la informació a mostrar a la graella */
    autoFindDisabled?: boolean;
    /** Text pel missatge de que no hi ha resultats */
    noRowsText?: string;
    /** Activa la selecció de files */
    selectionActive?: true;
    /** Activa la paginació */
    paginationActive?: true;
    /** Model d'ordenació que s'afegirà sempre abans que els altres criteris d'ordenació */
    fixedSortModel?: GridSortModel;
    /** Model d'ordenació inicial */
    defaultSortModel?: GridSortModel;
    /** Model de paginació inicial (un valor a pageSize de -1 indica que la mida de la pàgina s'ajusta a l'alçada del component) */
    defaultPaginationModel?: GridPaginationModel;
    /** Filtre en format Spring Filter que s'enviarà en les consultes d'informació al backend */
    filter?: string;
    /** Filtre en format Spring Filter que s'afegirà sempre a filter amb and */
    fixedFilter?: string;
    /** Valor inicial pel filtre ràpid */
    quickFilterInitialValue?: string;
    /** Indica si el camp de filtre ràpid ha de tenir el focus quan es crei el component */
    quickFilterSetFocus?: true;
    /** Indica si el camp de filtre ràpid ha d'ocupar el 100% de l'espai horitzontal disponible */
    quickFilterFullWidth?: true;
    /** Consultes per nom que s'enviaran en les consultes d'informació al backend */
    namedQueries?: string[];
    /** Perspectives que s'enviaran en les consultes d'informació al backend */
    perspectives?: string[];
    /** Format de fitxer que s'enviarà com a paràmetre al fer la petició d'exportació d'informació al backend */
    exportFileType?: ExportFileType;
    /** Dades addicionals pel formulari de creació o modificació d'una fila de la graella */
    formAdditionalData?: ((row: any, action: string) => any) | any;
    /** Llista d'ids de les files expandides per defecte */
    treeDataDefaultExpandedRowIds?: any[];
    /** Tipus de barra d'eines que es mostrarà a la part superior */
    toolbarType?: DataToolbarType;
    /** Oculta la barra d'eines de la part superior */
    toolbarHide?: true;
    /** Indica si el toolbar ha de mostrar un botó per a tornar enrere */
    toolbarBackButton?: true;
    /** Indica si el toolbar ha de mostrar un botó per a l'esborrat massiu de files */
    toolbarBulkDelete?: true;
    /** Oculta el botó d'exportació de la barra d'eines */
    toolbarHideExport?: false;
    /** Oculta el botó de creació de la barra d'eines */
    toolbarHideCreate?: true;
    /** Oculta el botó de refresc de la barra d'eines */
    toolbarHideRefresh?: true;
    /** Oculta el camp de filtre ràpid de la barra d'eines */
    toolbarHideQuickFilter?: true;
    /** Adreça que s'ha de mostrar al fer clic sobre el botó de crear una nova fila */
    toolbarCreateLink?: string;
    /** Elements addicionals (amb la seva posició) per a la barra d'eines */
    toolbarElementsWithPositions?: ReactElementWithPosition[];
    /** Fila addicional que es col·locarà just a davall la barra d'eines */
    toolbarAdditionalRow?: React.ReactElement;
    /** Estil minHeight per a la fila addicional */
    toolbarAdditionalRowMinHeight?: string;
    /** Adreça que s'ha de mostrar al fer clic sobre una fila de la graella (només es permet fer clic sobre les files si s'especifica algun valor) */
    rowLink?: string;
    /** Adreça que s'ha de mostrar al fer clic sobre el botó per a mostrar els detalls d'una fila (només en mode només lectura) */
    rowDetailLink?: string;
    /** Adreça que s'ha de mostrar al fer clic sobre el botó de modificar una fila */
    rowUpdateLink?: string;
    /** Funció que indica si l'enllaç d'una determinada fila està activa */
    isRowLinkActive?: (row: any) => boolean;
    /** Deshabilita el botó d'actualització de cada fila (por ser també una funció que reb la fila i retorna true/false) */
    rowDisableUpdateButton?: boolean | ((row: any) => boolean);
    /** Deshabilita el botó d'esborrar de cada fila (por ser també una funció que reb la fila i retorna true/false) */
    rowDisableDeleteButton?: boolean | ((row: any) => boolean);
    /** Deshabilita el botó de detalls de cada fila (por ser també una funció que reb la fila i retorna true/false) */
    rowDisableDetailsButton?: boolean | ((row: any) => boolean);
    /** Oculta el botó de modificació de cada fila (por ser també una funció que reb la fila i retorna true/false) */
    rowHideUpdateButton?: boolean | ((row: any) => boolean);
    /** Oculta el botó d'esborrar de cada fila (por ser també una funció que reb la fila i retorna true/false) */
    rowHideDeleteButton?: boolean | ((row: any) => boolean);
    /** Oculta el botó de detalls de cada fila (por ser també una funció que reb la fila i retorna true/false) */
    rowHideDetailsButton?: boolean | ((row: any) => boolean);
    /** Index a dins la llista de columnes a on insertar la columna d'accions (si no s'especifica s'inserta al final) */
    rowActionsColumnIndex?: number;
    /** Propietats addicionals per a la columna d'accions de la graella */
    rowActionsColumnProps?: any;
    /** Accions addicionals per a cada fila */
    rowAdditionalActions?: DataCommonAdditionalAction[];
    /** Model amb les files seleccionades */
    rowSelectionModel?: GridRowSelectionModel;
    /** Funció que processa les files abans de passar-les al DataGrid de MUI */
    rowsTransformer?: (rows: any[]) => any[];
    /** Indica que la creació i modificació en la mateixa fila està activa */
    inlineEditActive?: boolean;
    /** Indica que només la creació en la mateixa fila està activa */
    inlineEditCreateActive?: boolean;
    /** Indica que només la modificació en la mateixa fila està activa */
    inlineEditUpdateActive?: boolean;
    /** Indica que la creació i modificació amb finestra emergent està activa */
    popupEditActive?: boolean;
    /** Indica que només la creació amb finestra emergent està activa */
    popupEditCreateActive?: boolean;
    /** Indica que només la modificació amb finestra emergent està activa */
    popupEditUpdateActive?: boolean;
    /** Contingut (camps) del formulari de creació / modificació */
    popupEditFormContent?: React.ReactElement;
    /** Títol per la finestra emergent */
    popupEditFormDialogTitle?: string;
    /** Nom del recurs per la finestra emergent (s'afegeix al títol per defecte) */
    popupEditFormDialogResourceTitle?: string;
    /** Botons pel component Dialog de la finestra emergent */
    popupEditFormDialogButtons?: DialogButton[];
    /** Propietats pel component Dialog de la finestra emergent */
    popupEditFormDialogComponentProps?: any;
    /** Event onClose pel component Dialog de la finestra emergent */
    popupEditFormDialogOnClose?: (reason?: string) => boolean;
    /** Propietats pel component Form de la finestra emergent */
    popupEditFormComponentProps?: any;
    /** Claus de traducció personalitzades pel component Form de la finestra emergent */
    popupEditFormI18nKeys?: FormI18nKeys;
    /** Indica si la persistència de l'estat està activa */
    persistentStateActive?: true;
    /** Indica que s'han d'esborrar l'ordenació i la pàgina actual de l'estat persistent si hi ha un canvi en la ruta de primer nivell */
    persistentStateClearPageSortPropsOnTopLevelRouteChange?: true;
    /** La clau amb la que es desarà l'estat (s'utilitzarà el valor de resourceName si no s'especifica) */
    persistentStateKey?: string;
    /** El magatzem del navegador que s'utilitzarà per a persistir l'estat (LocalStorage per defecte) */
    persistentStateStorage?: 'local' | 'session';
    /** Event que es llença quan es fa clic sobre una fila */
    onRowClick?: GridEventListener<'rowClick'>;
    /** Event que es llença quan hi ha canvis en les files que mostra la graella */
    onRowsChange?: (rows: GridRowsProp, pageInfo: any) => void;
    /** Event que es llença quan hi ha canvis en l'ordenació de la graella */
    onRowOrderChange?: GridEventListener<'rowOrderChange'>;
    /** Event que es llença quan hi ha canvis en les files seleccionades de la graella */
    onRowSelectionModelChange?: (
        rowSelectionModel: GridRowSelectionModel,
        details: GridCallbackDetails
    ) => void;
    /** Event que es llença quan es crea una nova fila */
    onRowCreate?: (row: any) => void;
    /** Event que es llença quan es modifica una fila */
    onRowUpdate?: (row: any) => void;
    /** Event que es llença quan s'elimina una fila */
    onRowDelete?: (id: any | any[]) => void;
    /** Event que es llença quan canvia l'estat persistit (només es crida si persistentStateActive és true) */
    onPersistentStateChange?: (state: any) => void;
    /** Referència a l'api del component */
    apiRef?: MuiDataGridApiRef;
    /** Referència a l'api interna del component DataGrid de MUI */
    datagridApiRef?: React.RefObject<DataGridApi | null>;
    /** Alçada del component en píxels */
    height?: number;
    /**
     * Indica si l'alçada del component s'ha d'ajustar al nombre de files que s'han de mostrar
     * @warning Canviar aquest valor dinàmicament fa que el DataGrid de MUI es torni a montar de nou (l'estat intern i subscripcions a events es perden).
     */
    autoHeight?: boolean;
    /** Indica que les files parells s'han de mostrar d'un color més oscur per a facilitar la seva lectura */
    striped?: true;
    /** Indica que només s'han de mostrar les vores horitzontals de la graella */
    semiBordered?: true;
    /** Estils addicionals pel contenidor de la graella */
    sx?: any;
    /** Indica si s'han d'imprimir a la consola missatges de depuració */
    debug?: true;
} & Omit<DataGridProps, 'apiRef'>;

const processFindSortModel = (
    fixedSortModel: GridSortModel | undefined,
    sortModel: GridSortModel | undefined,
    columns: MuiDataGridColDef[]
) => {
    const result: any[] = [];
    [...(fixedSortModel ?? []), ...(sortModel ?? [])].forEach(({ field, sort }) => {
        const columnForCurrentField = columns.find((c) => c.field === field);
        const mappedFields = columnForCurrentField?.sortProcessor
            ? columnForCurrentField.sortProcessor(field, sort)
            : undefined;
        if (mappedFields) {
            mappedFields.forEach((mappedField) => result.push(mappedField));
        } else {
            result.push({ field, sort });
        }
    });
    return result as GridSortModel;
};

const rowLinkFind = (rowLink: string | undefined, rowLinks: any[] | undefined) => {
    if (rowLink != null) {
        const isNegative = rowLink != null && rowLink.startsWith('!');
        return isNegative ? rowLinks?.[rowLink.substring(1) as any] : rowLinks?.[rowLink as any];
    }
};
const rowLinkShowCheck = (rowLink: string | undefined, rowLinks: any[] | undefined) => {
    if (rowLink != null) {
        const found = rowLinkFind(rowLink, rowLinks); //
        const isNegative = rowLink != null && rowLink.startsWith('!');
        if (found) {
            return isNegative ? found == null : found != null;
        } else {
            return isNegative;
        }
    } else {
        return true;
    }
};
const rowArtifactShowCheck = (
    action: string | undefined,
    report: string | undefined,
    artifacts: any[] | undefined
) => {
    if (action != null) {
        return artifacts?.find((a) => a.type === 'ACTION' && a.code === action) != null;
    } else if (report != null) {
        return artifacts?.find((a) => a.type === 'REPORT' && a.code === report) != null;
    } else {
        return true;
    }
};

const rowActionsToGridActionsCellItem = (
    label: string,
    titleProp?: string,
    icon?: string,
    linkTo?: string,
    linkState?: any,
    linkTarget?: string,
    onClick?: (event: any) => void,
    showInMenu?: boolean,
    disabled?: boolean
) => {
    const { getLinkComponent } = useBaseAppContext();
    const additionalProps: any = showInMenu ? { showInMenu: true } : {};
    linkTo && (additionalProps['component'] = getLinkComponent());
    linkTo && (additionalProps['to'] = linkTo);
    linkState && (additionalProps['state'] = linkState);
    linkTarget && (additionalProps['target'] = linkTarget);
    const title = !showInMenu ? label : titleProp;
    const actionCellItem = (
        <GridActionsCellItem
            label={label}
            title={title}
            icon={icon ? <Icon>{icon}</Icon> : undefined}
            onClick={onClick}
            disabled={disabled}
            {...additionalProps}
        />
    );
    return actionCellItem;
};

const rowActionsToGridActionsCellItems = (
    rowActions: DataCommonAdditionalAction[],
    id: any,
    row: any,
    artifacts: any[] | undefined,
    forceDisabled?: boolean
): React.ReactElement[] => {
    const { apiRef: dataGridApiRef } = useDataGridContext();
    const actions: React.ReactElement[] = [];
    rowActions.forEach((rowAction: DataCommonAdditionalAction) => {
        const rowLink = rowLinkFind(rowAction.rowLink, row['_actions']);
        const rowLinkShow = rowLinkShowCheck(rowAction.rowLink, row['_actions']);
        const rowArtifactShow = rowArtifactShowCheck(rowAction.action, rowAction.report, artifacts);
        const rowActionLinkTo =
            typeof rowAction.linkTo === 'function'
                ? rowAction.linkTo?.(row)
                : rowAction.linkTo?.replace('{{id}}', '' + id);
        const rowActionLinkState =
            typeof rowAction.linkState === 'function'
                ? rowAction.linkState?.(row)
                : rowAction.linkState;
        const rowActionLinkTarget =
            typeof rowAction.linkTarget === 'function'
                ? rowAction.linkTarget?.(row)
                : rowAction.linkTarget;
        const rowActionOnClick = (event: any) => {
            if (rowAction.clickShowCreateDialog) {
                dataGridApiRef.current?.triggerCreate?.(row);
            } else if (rowAction.clickShowUpdateDialog) {
                dataGridApiRef.current?.triggerUpdate?.(id, row);
            } else if (rowAction.clickTriggerDelete) {
                dataGridApiRef.current?.triggerDelete?.(id);
            } else {
                rowAction.onClick?.(id, row, event);
            }
        };
        const label =
            typeof rowAction.label === 'function' ? rowAction.label(row) : rowAction.label;
        const title =
            typeof rowAction.title === 'function' ? rowAction.title(row) : rowAction.title;
        const icon = typeof rowAction.icon === 'function' ? rowAction.icon(row) : rowAction.icon;
        const showInMenu =
            typeof rowAction.showInMenu === 'function'
                ? rowAction.showInMenu(row)
                : rowAction.showInMenu;
        const disabled =
            forceDisabled ||
            (typeof rowAction.disabled === 'function'
                ? rowAction.disabled(row)
                : rowAction.disabled);
        const hidden =
            typeof rowAction.hidden === 'function' ? rowAction.hidden(row) : rowAction.hidden;
        rowLinkShow &&
            rowArtifactShow &&
            !hidden &&
            actions.push(
                rowActionsToGridActionsCellItem(
                    label ?? (rowLink != null ? rowLink?.title : rowAction),
                    title,
                    icon,
                    rowActionLinkTo,
                    rowActionLinkState,
                    rowActionLinkTarget,
                    rowActionOnClick,
                    showInMenu,
                    disabled
                )
            );
    });
    return actions;
};

const useGridColumns = (
    columns: MuiDataGridColDef[],
    rowActionsColumnIndex: number | undefined,
    rowActionsColumnProps: any,
    rowActions: DataCommonAdditionalAction[],
    rowInlineEditActive: boolean,
    fields: any[] | undefined,
    inlineStopRowEditMode: (id: any, ignoreModifications?: boolean) => void,
    artifacts: any[] | undefined
) => {
    const { t } = useBaseAppContext();
    const { currentLanguage } = useResourceApiContext();
    const processedColumns = React.useMemo(() => {
        const processedColumns: MuiDataGridColDef[] = columns.map((c) => {
            const field = fields?.find((f) => f.name === c.field);
            const isNumericType = isFieldNumericType(field, c.fieldType);
            const isCurrencyType = c.currencyType;
            return {
                valueGetter: (value: any, row: any, column: GridColDef) => {
                    if (column.field?.includes('.')) {
                        const value = column.field
                            .split('.')
                            .reduce(
                                (o: any, x: string) =>
                                    typeof o == 'undefined' || o === null ? o : o[x],
                                row
                            );
                        return value;
                    } else {
                        return value;
                    }
                },
                valueFormatter: (value: never, row: any) => {
                    const cany: any = c;
                    const formattedValue = formattedFieldValue(value, field, {
                        type: isCurrencyType ? 'currency' : c.fieldType,
                        currentLanguage,
                        currencyCode: cany['currencyCode'],
                        currencyDecimalPlaces: cany['currencyDecimalPlaces'],
                        currencyLocale: cany['currencyLocale'],
                        decimalPlaces: cany['decimalPlaces'],
                        noTime: cany['noTime'],
                        noSeconds: cany['noSeconds'],
                        formatterParams: row,
                    });
                    return formattedValue;
                },
                headerName: field ? field?.label : '',
                headerAlign: isNumericType ? 'right' : undefined,
                align: isNumericType ? 'right' : undefined,
                display: 'flex',
                exportable: field != null,
                editable: rowInlineEditActive,
                renderEditCell: (params) => {
                    return <FormField name={params.field} label="" inline />;
                },
                ...c,
            };
        });
        if (rowActions && rowActions.length) {
            const actionsColumn = {
                field: ' ',
                type: 'actions',
                sortable: false,
                hideable: false,
                exportable: false,
                renderCell: (params: GridRenderCellParams) => {
                    const gridApiRef = useGridApiContext();
                    const rowModesModel = useGridSelector(gridApiRef, gridEditRowsStateSelector);
                    const anyRowInEditMode = Object.keys(rowModesModel).length > 0;
                    const currentRowInEditMode = typeof rowModesModel[params.id] !== 'undefined';
                    const rowEditActions = [
                        {
                            label: t('grid.edit.save'),
                            icon: 'save',
                            onClick: () => inlineStopRowEditMode(params.id),
                        },
                        {
                            label: t('grid.edit.cancel'),
                            icon: 'clear',
                            onClick: () => inlineStopRowEditMode(params.id, true),
                        },
                    ];
                    const actionsCellItems = rowActionsToGridActionsCellItems(
                        currentRowInEditMode ? rowEditActions : rowActions,
                        params.id,
                        params.row,
                        artifacts,
                        anyRowInEditMode && !currentRowInEditMode
                    );
                    return <GridActionsCell {...params}>{actionsCellItems}</GridActionsCell>;
                },
                ...rowActionsColumnProps,
            };
            processedColumns.splice(
                rowActionsColumnIndex ?? processedColumns.length,
                0,
                actionsColumn
            );
        }
        return processedColumns;
    }, [columns, fields, artifacts]);
    return processedColumns;
};

const usePersistentState = (
    active: boolean,
    persistentStateClearPageSortPropsOnTopLevelRouteChange: boolean,
    columns: GridColDef[],
    onPersistentStateChange: ((state: any) => void) | undefined,
    sortModelProp: GridSortModel | undefined,
    defaultSortModel: GridSortModel | undefined,
    paginationModelProp: GridPaginationModel | undefined,
    defaultPaginationModel: GridPaginationModel | undefined,
    quickFilterProp: string | undefined,
    defaultQuickFilter: string | undefined,
    defaultExpandedRowIds: any[] | undefined,
    apiRef: React.RefObject<DataGridApi | null>,
    key: string,
    storeInLocalStorage?: boolean
) => {
    const { code, topLevelRouteChanged } = useBaseAppContext();
    const { isAuthenticated, getUserId } = useAuthContext();
    const userSuffix = isAuthenticated ? '_' + getUserId().toUpperCase() : '';
    const storageKey = code + '_DTG_' + key.toUpperCase() + userSuffix;
    const loadInitialState = () => {
        try {
            const storage = storeInLocalStorage ? localStorage : sessionStorage;
            const raw = storage.getItem(storageKey);
            const parsedState = raw ? JSON.parse(raw) : null;
            if (persistentStateClearPageSortPropsOnTopLevelRouteChange && topLevelRouteChanged) {
                const { sortModel, paginationModel, expandedRowIds, ...otherState } = parsedState;
                const state: any = {
                    paginationModel: {
                        page: 0,
                        pageSize: paginationModel?.pageSize,
                    },
                    expandedRowIds: [],
                    ...otherState,
                };
                onPersistentStateChange?.(state);
                return state;
            } else {
                onPersistentStateChange?.(parsedState);
                return parsedState;
            }
        } catch {
            return null;
        }
    };
    const saveState = (state: any) => {
        try {
            const storage = storeInLocalStorage ? localStorage : sessionStorage;
            storage.setItem(storageKey, JSON.stringify(state));
            onPersistentStateChange?.(state);
        } catch {}
    };
    const initialState = active ? loadInitialState() : undefined;
    const [widths, setWidths] = React.useState<Record<string, number>>(initialState?.widths || {});
    const [orderedFields, setOrderedFields] = React.useState<string[]>(
        initialState?.orderedFields || columns.map((c) => c.field)
    );
    const [columnVisibilityModel, setColumnVisibilityModel] = React.useState(
        initialState?.columnVisibilityModel || {}
    );
    const [pinnedColumns, setPinnedColumns] = React.useState(initialState?.pinnedColumns || {});
    const [autoPageSize, setAutoPageSize] = React.useState<boolean>(
        initialState?.autoPageSize != null
            ? initialState?.autoPageSize
            : defaultPaginationModel == null || defaultPaginationModel.pageSize === -1
    );
    const [quickFilter, setQuickFilter] = React.useState<string>(
        initialState?.quickFilter || defaultQuickFilter
    );
    const [sortModel, setSortModel] = React.useState<GridSortModel>(
        initialState?.sortModel || (defaultSortModel ?? [])
    );
    const [paginationModel, setPaginationModel] = React.useState<GridPaginationModel>(
        initialState?.paginationModel || defaultPaginationModel
    );
    const [expandedRowIds, setExpandedRowIds] = React.useState<any[]>(
        initialState?.expandedRowIds || (defaultExpandedRowIds ?? [])
    );
    React.useEffect(() => {
        quickFilterProp !== undefined && setQuickFilter(quickFilterProp);
    }, [quickFilterProp]);
    React.useEffect(() => {
        sortModelProp !== undefined && setSortModel(sortModelProp);
    }, [sortModelProp]);
    React.useEffect(() => {
        paginationModelProp !== undefined && setPaginationModel(paginationModelProp);
    }, [paginationModelProp]);
    React.useEffect(() => {
        if (active) {
            const unsubscribe = apiRef.current?.subscribeEvent(
                'rowExpansionChange',
                (params: any) => {
                    setExpandedRowIds((prev) => {
                        if (params.childrenExpanded) {
                            return [...prev, params.id];
                        } else {
                            return prev.filter((id) => id !== params.id);
                        }
                    });
                }
            );
            return unsubscribe;
        }
    }, []);
    React.useEffect(() => {
        active &&
            saveState({
                widths,
                orderedFields,
                columnVisibilityModel,
                pinnedColumns,
                autoPageSize,
                quickFilter,
                sortModel,
                paginationModel,
                expandedRowIds,
            });
    }, [
        widths,
        orderedFields,
        columnVisibilityModel,
        pinnedColumns,
        autoPageSize,
        quickFilter,
        sortModel,
        paginationModel,
        expandedRowIds,
    ]);
    const onColumnWidthChange = React.useCallback(
        (params: GridColumnResizeParams) => {
            const { colDef, width } = params;
            setWidths((prev) => ({ ...prev, [colDef.field]: width }));
        },
        [setWidths]
    );
    const onColumnOrderChange = React.useCallback(() => {
        setOrderedFields(gridColumnFieldsSelector(apiRef));
    }, [apiRef, setOrderedFields]);
    const onColumnVisibilityModelChange = React.useCallback((model: any) => {
        setColumnVisibilityModel(model);
    }, []);
    const onPinnedColumnsChange = React.useCallback((model: any) => {
        setPinnedColumns(model);
    }, []);
    const onSortModelChange = React.useCallback((model: any) => {
        setSortModel(model);
    }, []);
    const onPaginationModelChange = React.useCallback((model: any) => {
        setPaginationModel(model);
    }, []);
    const persistentStateColumns = React.useMemo(
        () =>
            active
                ? orderedFields.reduce<GridColDef[]>((acc, field) => {
                      const column = columns.find((col) => col.field === field);
                      if (!column) {
                          return acc;
                      }
                      if (widths[field]) {
                          acc.push({
                              ...column,
                              flex: 0,
                              width: widths[field],
                          });
                          return acc;
                      }
                      acc.push(column);
                      return acc;
                  }, [])
                : columns,
        [columns, widths, orderedFields]
    );
    return {
        persistentStateColumns,
        persistentStateProps: active
            ? {
                  onColumnWidthChange,
                  onColumnOrderChange,
                  onColumnVisibilityModelChange,
                  onPinnedColumnsChange,
                  onSortModelChange,
                  onPaginationModelChange,
                  columnVisibilityModel,
                  pinnedColumns,
                  sortModel,
                  paginationModel,
                  autoPageSize,
              }
            : {
                  onSortModelChange,
                  onPaginationModelChange,
                  sortModel,
                  paginationModel,
                  autoPageSize,
              },
        quickFilter: quickFilter ?? '',
        expandedRowIds,
        setQuickFilter,
        setAutoPageSize,
        setExpandedRowIds,
    };
};

const useLocaleText = () => {
    const { currentLanguage } = useBaseAppContext();
    if (currentLanguage === 'ca') {
        return caES.components.MuiDataGrid.defaultProps.localeText;
    } else if (currentLanguage === 'es') {
        return esES.components.MuiDataGrid.defaultProps.localeText;
    } else {
        return enUS.components.MuiDataGrid.defaultProps.localeText;
    }
};

/**
 * Hook per a accedir a l'API de MuiDataGrid des de fora del context del component.
 *
 * @returns referència a l'API del component MuiDataGrid.
 */
export const useMuiDataGridApiRef: () => MuiDataGridApiRef = () => {
    const gridApiRef = React.useRef<MuiDataGridApi>(null);
    return gridApiRef;
};

/**
 * Hook per a accedir a l'API de MuiDataGrid des de dins el context del component.
 *
 * @returns referència a l'API del component MuiDataGrid.
 */
export const useMuiDataGridApiContext: () => MuiDataGridApiRef = () => {
    const gridContext = useDataGridContext();
    return gridContext.apiRef;
};

/**
 * Graella per a visualitzar dades provinents d'una API REST basada en Base-Boot.
 *
 * @param props - Propietats del component.
 * @returns Element JSX de la graella.
 */
export const MuiDataGrid: React.FC<MuiDataGridProps> = (props) => {
    const { defaultMuiComponentProps } = useMuiBaseAppContext();
    const {
        title,
        titleDisabled,
        subtitle,
        resourceName,
        resourceType,
        resourceTypeCode,
        resourceFieldName,
        columns,
        readOnly,
        loading: loadingProp,
        autoFindDisabled,
        noRowsText,
        selectionActive,
        paginationActive,
        sortModel: sortModelProp,
        fixedSortModel,
        defaultSortModel,
        paginationModel: paginationModelProp,
        defaultPaginationModel,
        filter: filterProp,
        fixedFilter,
        quickFilterInitialValue,
        quickFilterSetFocus,
        quickFilterFullWidth,
        namedQueries,
        perspectives,
        exportFileType = 'PDF',
        formAdditionalData,
        treeDataDefaultExpandedRowIds,
        toolbarType = 'default',
        toolbarHide,
        toolbarBackButton,
        toolbarBulkDelete,
        toolbarHideExport = true,
        toolbarHideCreate,
        toolbarHideRefresh,
        toolbarHideQuickFilter,
        toolbarCreateLink,
        toolbarElementsWithPositions,
        toolbarAdditionalRow,
        toolbarAdditionalRowMinHeight,
        rowLink,
        rowDetailLink,
        rowUpdateLink,
        isRowLinkActive,
        rowDisableUpdateButton,
        rowDisableDeleteButton,
        rowDisableDetailsButton,
        rowHideUpdateButton,
        rowHideDeleteButton,
        rowHideDetailsButton,
        rowActionsColumnIndex,
        rowActionsColumnProps,
        rowAdditionalActions = [],
        rowSelectionModel: rowSelectionModelProp = DEFAULT_ROW_SELECTION,
        rowsTransformer,
        inlineEditActive,
        inlineEditCreateActive,
        inlineEditUpdateActive,
        popupEditActive,
        popupEditCreateActive,
        popupEditUpdateActive,
        popupEditFormContent,
        popupEditFormDialogTitle,
        popupEditFormDialogResourceTitle,
        popupEditFormDialogButtons,
        popupEditFormDialogComponentProps,
        popupEditFormDialogOnClose,
        popupEditFormComponentProps,
        popupEditFormI18nKeys,
        persistentStateActive,
        persistentStateClearPageSortPropsOnTopLevelRouteChange,
        persistentStateKey,
        persistentStateStorage,
        onRowClick,
        onRowsChange,
        onRowOrderChange,
        onRowSelectionModelChange,
        onRowCreate,
        onRowUpdate,
        onRowDelete: onRowDeleteProp,
        onPersistentStateChange,
        apiRef: apiRefProp,
        datagridApiRef: datagridApiRefProp,
        height,
        autoHeight,
        striped,
        semiBordered,
        sx,
        debug = false,
        ...otherProps
    } = { ...defaultMuiComponentProps.dataGrid, ...props };
    const datagridApiRefInternal = useMuiDatagridApiRef();
    const datagridApiRef = datagridApiRefProp ?? datagridApiRefInternal;
    const formApiRef = useFormApiRef();
    const anyArtifactRowAction =
        rowAdditionalActions?.find((a) => a.action != null || a.report != null) != null;
    const [filter, setFilter] = React.useState<string | undefined>(filterProp);
    const [_filterModel, setFilterModel] = React.useState<GridFilterModel>();
    const [rowSelectionModel, setRowSelectionModel] =
        React.useState<GridRowSelectionModel>(rowSelectionModelProp);
    const [rowModesModel, setRowModesModel] = React.useState<GridRowModesModel>({});
    const [findArgs, setFindArgs] = React.useState<DataCommonFindArgs>();
    const anyRowInEditMode = Object.keys(rowModesModel).length > 0;
    const inlineCreate = () => {
        const sortedRowIds = datagridApiRef.current?.getSortedRowIds();
        const page = datagridApiRef.current?.state.pagination.paginationModel.page ?? 0;
        const pageSize = datagridApiRef.current?.state.pagination.paginationModel.pageSize ?? 0;
        const start = page * pageSize;
        const end = start + pageSize;
        const numRowsInPage = sortedRowIds?.slice(start, end).length;
        if (sortedRowIds != null && numRowsInPage === pageSize) {
            const lastRowId = sortedRowIds[end - 1];
            const lastRowData = datagridApiRef.current?.getRow(lastRowId);
            datagridApiRef.current?.updateRows([{ id: lastRowId, _action: 'delete' }]);
            datagridApiRef.current?.updateRows([
                { id: CREATE_ROW_ID, _previousRowData: lastRowData, isNew: true },
            ]);
        } else {
            datagridApiRef.current?.updateRows([{ id: CREATE_ROW_ID, isNew: true }]);
        }
        formApiRef.current?.reset();
        datagridApiRef.current?.startRowEditMode({ id: CREATE_ROW_ID });
        setTimeout(() => formApiRef.current?.focus());
    };
    const inlineUpdate = (id: any, row?: any) => {
        formApiRef.current?.reset(row, id);
        datagridApiRef.current?.startRowEditMode({ id });
        setTimeout(() => formApiRef.current?.focus());
    };
    const inlineStopRowEditMode = (id: any, ignoreModifications?: boolean) => {
        if (ignoreModifications) {
            datagridApiRef.current?.stopRowEditMode({ id, ignoreModifications });
            if (id === CREATE_ROW_ID) {
                const rowData = datagridApiRef.current?.getRow(id);
                const previousRowData = rowData?._previousRowData;
                datagridApiRef.current?.updateRows([{ id, _action: 'delete' }]);
                if (previousRowData != null) {
                    datagridApiRef.current?.updateRows([previousRowData]);
                }
            }
        } else {
            datagridApiRef.current?.stopRowEditMode({ id });
        }
    };
    const onRowDelete = (id: any | any[]) => {
        const ids = Array.isArray(id) ? id : id != null ? [id] : null;
        if (ids != null) {
            setRowSelectionModel((prev) => {
                const newIds = new Set([...prev.ids].filter((id) => !ids.includes(id)));
                return { ...prev, ids: newIds };
            });
        }
        onRowDeleteProp?.(id);
    };
    const {
        currentActions: apiCurrentActions,
        currentError: apiCurrentError,
        delete: apiDelete,
        bulkDelete: apiBulkDelete,
    } = useResourceApiService(resourceName);
    const {
        loading,
        fields,
        rows,
        pageInfo,
        artifacts,
        error: apiDataCommonError,
        refresh,
        export: exportt,
    } = useApiDataCommon(
        resourceName,
        resourceType,
        resourceTypeCode,
        resourceFieldName,
        loadingProp,
        autoFindDisabled,
        findArgs,
        anyArtifactRowAction
    );
    const {
        toolbarAddElement,
        rowEditActions,
        formDialogComponent,
        triggerCreate,
        triggerUpdate,
        triggerDelete,
    } = useDataCommonEditable(
        resourceName,
        readOnly ?? false,
        formAdditionalData,
        toolbarCreateLink,
        anyRowInEditMode,
        inlineEditActive || inlineEditCreateActive ? inlineCreate : undefined,
        inlineEditActive || inlineEditUpdateActive ? inlineUpdate : undefined,
        rowDetailLink,
        rowUpdateLink,
        rowDisableUpdateButton,
        rowDisableDeleteButton,
        rowDisableDetailsButton,
        rowHideUpdateButton,
        rowHideDeleteButton,
        rowHideDetailsButton,
        inlineEditActive,
        inlineEditCreateActive,
        inlineEditUpdateActive,
        popupEditActive,
        popupEditCreateActive,
        popupEditUpdateActive,
        popupEditFormContent,
        popupEditFormDialogTitle,
        popupEditFormDialogResourceTitle,
        popupEditFormDialogButtons,
        popupEditFormDialogComponentProps,
        popupEditFormDialogOnClose,
        popupEditFormComponentProps,
        popupEditFormI18nKeys,
        apiCurrentActions,
        apiDelete,
        apiBulkDelete,
        refresh,
        onRowCreate,
        onRowUpdate,
        onRowDelete
    );
    const processedColumns = useGridColumns(
        columns,
        rowActionsColumnIndex,
        rowActionsColumnProps,
        [...rowAdditionalActions, ...rowEditActions],
        (inlineEditActive ?? false) || (inlineEditUpdateActive ?? false),
        fields,
        inlineStopRowEditMode,
        artifacts
    );
    const {
        persistentStateColumns,
        persistentStateProps,
        quickFilter,
        expandedRowIds,
        setQuickFilter,
        setAutoPageSize,
        setExpandedRowIds,
    } = usePersistentState(
        persistentStateActive ?? false,
        persistentStateClearPageSortPropsOnTopLevelRouteChange ?? false,
        processedColumns,
        onPersistentStateChange,
        sortModelProp,
        defaultSortModel,
        paginationModelProp,
        defaultPaginationModel,
        undefined, // quickFilterProp
        quickFilterInitialValue, // defaultQuickFilter
        treeDataDefaultExpandedRowIds,
        datagridApiRef,
        persistentStateKey ?? resourceName,
        persistentStateStorage === 'local'
    );
    const {
        sortModel,
        paginationModel,
        onPaginationModelChange,
        autoPageSize,
        ...otherPersistentStateProps
    } = persistentStateProps;
    const isUpperToolbarType = toolbarType === 'upper';
    const gridMargins = isUpperToolbarType ? { m: 2 } : null;
    const canDeleteAnyRow = rows.some((r) => r['_actions']?.['delete'] != null);
    const quickFilterDebounced = useDebounce(quickFilter);
    React.useEffect(() => {
        if (!paginationActive || (paginationModel != null && paginationModel.pageSize !== -1)) {
            const processedFindSortModel = processFindSortModel(fixedSortModel, sortModel, columns);
            const sorts = processedFindSortModel?.length
                ? processedFindSortModel.map(({ field, sort }) => `${field},${sort}`)
                : undefined;
            const paginationArgs = paginationActive
                ? {
                      page: paginationModel?.page,
                      size: paginationModel?.pageSize,
                  }
                : { unpaged: true };
            const processedFilter = springFilterBuilder.and(fixedFilter, filter);
            const newFindArgs = {
                ...paginationArgs,
                sorts,
                quickFilter: quickFilterDebounced?.length ? quickFilterDebounced : undefined,
                filter: processedFilter !== '' ? processedFilter : undefined,
                namedQueries,
                perspectives,
            };
            if (JSON.stringify(findArgs) !== JSON.stringify(newFindArgs)) {
                setFindArgs(newFindArgs);
                setExpandedRowIds(treeDataDefaultExpandedRowIds ?? []);
            }
        }
    }, [
        paginationActive,
        fixedSortModel,
        sortModel,
        paginationModel,
        quickFilterDebounced,
        filter,
        fixedFilter,
        namedQueries,
        perspectives,
        columns,
    ]);
    React.useEffect(() => {
        onRowsChange?.(rows, pageInfo);
        if ((otherProps as any).treeData && rows.length) {
            const firstNode = gridRowNodeSelector(datagridApiRef, rows[0].id);
            if (firstNode?.depth !== undefined) {
                expandedRowIds?.forEach((id) => {
                    const node = gridRowNodeSelector(datagridApiRef, id);
                    if (node?.type === 'group') {
                        const api = datagridApiRef.current as {
                            setRowChildrenExpansion?: (id: any, expanded: boolean) => void;
                        };
                        api.setRowChildrenExpansion?.(id, true);
                    }
                });
            }
        }
    }, [rows]);
    React.useEffect(() => {
        setFilter(filterProp);
    }, [filterProp]);
    const toolbarNodesPosition = 2;
    const toolbarGridElementsWithPositions: ReactElementWithPosition[] = [];
    toolbarAddElement != null &&
        toolbarGridElementsWithPositions.push({
            position: toolbarNodesPosition,
            element: !toolbarHideCreate ? toolbarAddElement : <span />,
        });
    toolbarBulkDelete &&
        toolbarGridElementsWithPositions.push({
            position: toolbarNodesPosition,
            element: (
                <DataGridBulkDelete
                    rowSelectionModel={rowSelectionModel}
                    disabled={!canDeleteAnyRow}
                    onClick={triggerDelete}
                />
            ),
        });
    const toolbarNumElements =
        toolbarNodesPosition +
        (toolbarHideExport ? 0 : 1) +
        (toolbarHideRefresh ? 0 : 1) +
        (toolbarHideQuickFilter ? 0 : 1);
    const joinedToolbarElementsWithPositions =
        joinReactElementsWithPositionWithReactElementsWithPositions(
            toolbarNumElements,
            toolbarGridElementsWithPositions,
            toolbarElementsWithPositions
        );
    const gridExport = () => {
        const exportFields: string[] = columns
            .filter((c) => {
                const field = fields?.find((f) => f.name === c.field);
                return field != null;
            })
            .map((c) => c.field);
        exportt(exportFields, exportFileType, true);
    };
    const toolbar = useDataToolbar(
        title ?? capitalize(resourceName) ?? '<unknown>',
        titleDisabled ?? false,
        subtitle,
        toolbarType,
        apiCurrentError || apiDataCommonError,
        <DataQuickFilter
            value={quickFilter}
            onChange={setQuickFilter}
            setFocus={quickFilterSetFocus}
            fullWidth={quickFilterFullWidth}
            sx={{ ml: quickFilterFullWidth ? 0 : 1 }}
        />,
        refresh,
        gridExport,
        toolbarBackButton,
        toolbarHideExport,
        toolbarHideRefresh,
        toolbarHideQuickFilter,
        joinedToolbarElementsWithPositions
    );
    const getDataGridApi = () => ({
        refresh,
        export: gridExport,
        triggerCreate,
        triggerUpdate,
        triggerDelete,
        setFilter,
    });
    const apiRef = React.useRef<MuiDataGridApi>(getDataGridApi());
    React.useEffect(() => {
        apiRef.current = getDataGridApi();
    }, [refresh, gridExport, triggerCreate, triggerUpdate, triggerDelete, setFilter]);
    if (apiRefProp) {
        apiRefProp.current = getDataGridApi();
    }
    const filteringProps: any = {
        filterMode: !(otherProps as any).treeData ? 'server' : undefined,
        disableColumnFilter: true,
        onFilterModelChange: setFilterModel,
    };
    const sortingProps: any = {
        sortingMode: 'server',
        sortModel,
    };
    const paginationProps: any = paginationActive
        ? {
              paginationMode: 'server',
              pagination: true,
              paginationModel,
              onPaginationModelChange,
              autoPageSize: !autoHeight && autoPageSize,
              rowCount: pageInfo?.totalElements ?? 0,
          }
        : null;
    const selectionProps: any =
        selectionActive || toolbarBulkDelete
            ? {
                  checkboxSelection: otherProps.checkboxSelection ?? true,
                  disableRowSelectionOnClick: otherProps.disableRowSelectionOnClick ?? true,
                  onRowSelectionModelChange: (
                      rowSelectionModel: GridRowSelectionModel,
                      details: GridCallbackDetails
                  ) => {
                      setRowSelectionModel(rowSelectionModel);
                      onRowSelectionModelChange?.(rowSelectionModel, details);
                  },
                  rowSelectionModel,
                  keepNonExistentRowsSelected: true,
                  checkboxSelectionVisibleOnly:
                      (otherProps.checkboxSelection ?? true) ? true : undefined,
              }
            : {
                  disableRowSelectionOnClick: true,
              };
    const inlineEditingProps: any =
        inlineEditActive || inlineEditCreateActive
            ? {
                  editMode: 'row',
                  onRowModesModelChange: setRowModesModel,
                  onRowEditStart: (params: any) => {
                      formApiRef.current?.reset(params.row, params.id);
                      setTimeout(() => formApiRef.current?.focus(params.field));
                  },
                  onRowEditStop: (params: any) => {
                      if (params.id === CREATE_ROW_ID) {
                          const previousRowData = params.row._previousRowData;
                          datagridApiRef.current?.updateRows([
                              { id: CREATE_ROW_ID, _action: 'delete' },
                          ]);
                          if (previousRowData != null) {
                              datagridApiRef.current?.updateRows([previousRowData]);
                          }
                      }
                  },
                  processRowUpdate: (newRow: any) =>
                      new Promise((resolve, reject) => {
                          formApiRef.current
                              ?.save()
                              .then((saved) => {
                                  resolve(
                                      newRow.id === CREATE_ROW_ID
                                          ? { ...saved, id: CREATE_ROW_ID }
                                          : saved
                                  );
                                  if (newRow.id === CREATE_ROW_ID) {
                                      onRowCreate?.(newRow);
                                  } else {
                                      onRowUpdate?.(newRow);
                                  }
                                  refresh();
                              })
                              .catch(reject);
                      }),
                  onProcessRowUpdateError: (error: any) => {
                      if (!error.modificationCanceledError && error.status === 422) {
                          const errors = error.errors ?? error.validationErrors;
                          const fieldErrors = errors
                              ?.filter((e: any) => e.field != null)
                              .map((e: any) => e.field);
                          if (fieldErrors?.length) {
                              setTimeout(() => formApiRef.current?.focus(fieldErrors[0]));
                          }
                      }
                  },
              }
            : null;
    const stripedProps: any = striped
        ? {
              getRowClassName: (params: GridRowClassNameParams) =>
                  params.indexRelativeToCurrentPage % 2 === 0 ? 'even' : 'odd',
          }
        : null;
    const processedRows = React.useMemo(
        () => (rowsTransformer ? rowsTransformer(rows) : rows),
        [rows]
    );
    const localeText = useLocaleText();
    const isRowsPresentInOtherProps = 'rows' in otherProps;
    const memoizedSlots = React.useMemo(() => {
        return {
            row: DataGridRow as GridSlots['row'],
            footer: DataGridFooter as GridSlots['footer'],
            noRowsOverlay: DataGridNoRowsOverlay,
        };
    }, []);
    const memoizedSlotProps = React.useMemo(() => {
        const requestPending =
            loading === undefined && autoFindDisabled && !isRowsPresentInOtherProps;
        return {
            row: { linkTo: rowLink, isRowLinkActive, isRowClickActive: onRowClick != null },
            footer: {
                paginationActive,
                selectionActive,
                paginationModel,
                pageInfo,
                setRowSelectionModel,
                pageSizeOptions: otherProps?.pageSizeOptions,
                autoPageSize,
                setAutoPageSize,
            },
            noRowsOverlay: {
                requestPending,
                noRowsText,
            },
        };
    }, [
        rowLink,
        isRowLinkActive,
        paginationActive,
        selectionActive,
        paginationModel,
        pageInfo,
        setRowSelectionModel,
        otherProps?.pageSizeOptions,
        autoHeight,
        autoPageSize,
        setAutoPageSize,
        loading,
        autoFindDisabled,
        isRowsPresentInOtherProps,
        noRowsText,
    ]);
    const memoizedSx = React.useMemo(() => {
        return {
            height: autoHeight ? 'auto' : undefined,
            ...gridMargins,
            ...sx,
        };
    }, [autoHeight, gridMargins, sx]);
    const content = (
        <>
            {!toolbarHide && toolbar}
            {toolbarAdditionalRow ? (
                <Box
                    className="toolbarAdditionalRow"
                    sx={{
                        ...gridMargins,
                        mb: 0,
                        ...(toolbarAdditionalRowMinHeight != null
                            ? { minHeight: toolbarAdditionalRowMinHeight }
                            : {}),
                    }}>
                    {toolbarAdditionalRow}
                </Box>
            ) : null}
            {formDialogComponent}
            <DataGridCustomStyle
                {...otherProps}
                loading={loading}
                rows={otherProps?.rows ?? processedRows}
                columns={persistentStateColumns}
                onRowOrderChange={onRowOrderChange}
                apiRef={datagridApiRef}
                {...filteringProps}
                {...sortingProps}
                {...paginationProps}
                {...selectionProps}
                {...inlineEditingProps}
                {...otherPersistentStateProps}
                {...stripedProps}
                slots={memoizedSlots}
                slotProps={memoizedSlotProps}
                onRowClick={onRowClick}
                semiBordered={semiBordered}
                autoHeight={autoHeight}
                localeText={localeText}
                sx={memoizedSx}
            />
        </>
    );
    // Workaround for bug in MUI-X v6 related to the DataGrid height https://github.com/mui/mui-x/issues/10520
    const virtualScrollerStyles = {
        [`& .MuiDataGrid-main`]: {
            flex: '1 1 0px',
        },
    };
    const context = {
        resourceName,
        loading: loading ?? false,
        findArgs,
        rows: processedRows,
        selection: rowSelectionModel,
        apiRef,
    };
    const inlineEditable = inlineEditActive || inlineEditCreateActive || inlineEditUpdateActive;
    return (
        <DataGridContext.Provider value={context}>
            {autoHeight ? (
                content
            ) : (
                <Box
                    sx={{
                        display: 'flex',
                        flexDirection: 'column',
                        height: height ? height : '100%',
                        ...virtualScrollerStyles,
                    }}>
                    {inlineEditable ? (
                        <Form
                            resourceName={resourceName}
                            apiRef={formApiRef}
                            additionalData={formAdditionalData}
                            commonFieldComponentProps={{ size: 'small' }}>
                            {content}
                        </Form>
                    ) : (
                        content
                    )}
                </Box>
            )}
        </DataGridContext.Provider>
    );
};

export default MuiDataGrid;
