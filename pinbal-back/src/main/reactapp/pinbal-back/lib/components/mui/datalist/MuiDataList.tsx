import React from 'react';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemText from '@mui/material/ListItemText';
import IconButton from '@mui/material/IconButton';
import Icon from '@mui/material/Icon';
import Box from '@mui/material/Box';
import Menu from '@mui/material/Menu';
import MenuItem from '@mui/material/MenuItem';
import { useTheme } from '@mui/material/styles';
import { FormI18nKeys } from '../../form/Form';
import { capitalize } from '../../../util/text';
import { formattedFieldValue } from '../../../util/fields';
import {
    useApiDataCommon,
    useDataCommonEditable,
    DataCommonAdditionalAction,
} from '../datacommon/MuiDataCommon';
import { useDataToolbar } from '../datacommon/DataToolbar';
import DataNoRows from '../datacommon/DataNoRows';
import DataQuickFilter from '../datacommon/DataQuickFilter';
import {
    ReactElementWithPosition,
    joinReactElementsWithPositionWithReactElementsWithPositions,
} from '../../../util/reactNodePosition';
import { DialogButton } from '../../BaseAppContext';
import { useMuiBaseAppContext } from '../MuiBaseAppContext';
import { ResourceType } from '../../ResourceApiContext';
import { useResourceApiService } from '../../ResourceApiProvider';

type MuiDataListFieldRendererArgs = {
    value: any;
    row: any;
    formattedValue?: string;
};

export type MuiDataListProps = {
    title: string;
    titleDisabled?: true;
    subtitle?: string;
    resourceName: string;
    resourceType?: ResourceType;
    resourceTypeCode?: string;
    resourceFieldName?: string;
    primaryField: string;
    secondaryField?: string;
    primaryFieldRenderer?: (args: MuiDataListFieldRendererArgs) => React.ReactElement;
    secondaryFieldRenderer?: (args: MuiDataListFieldRendererArgs) => React.ReactElement;
    readOnly?: boolean;
    loading?: true;
    findDisabled?: boolean;
    quickFilterInitialValue?: string;
    filter?: string;
    namedQueries?: string[];
    perspectives?: string[];
    formAdditionalData?: any;
    toolbarHide?: true;
    toolbarBackButton?: true;
    toolbarHideExport?: false;
    toolbarHideCreate?: boolean;
    toolbarHideRefresh?: boolean;
    toolbarHideQuickFilter?: boolean;
    toolbarCreateLink?: string;
    toolbarElementsWithPositions?: ReactElementWithPosition[];
    toolbarAdditionalRow?: React.ReactElement;
    rowUpdateLink?: string;
    rowDetailLink?: string;
    rowDisableUpdateButton?: boolean | ((row: any) => boolean);
    rowDisableDeleteButton?: boolean | ((row: any) => boolean);
    rowDisableDetailsButton?: boolean | ((row: any) => boolean);
    rowHideUpdateButton?: boolean | ((row: any) => boolean);
    rowHideDeleteButton?: boolean | ((row: any) => boolean);
    rowHideDetailsButton?: boolean | ((row: any) => boolean);
    rowAdditionalActions?: DataCommonAdditionalAction[];
    popupEditActive?: boolean;
    popupEditCreateActive?: boolean;
    popupEditUpdateActive?: boolean;
    popupEditFormContent?: React.ReactElement;
    popupEditFormDialogTitle?: string;
    popupEditFormDialogResourceTitle?: string;
    popupEditFormDialogButtons: DialogButton[] | undefined;
    popupEditFormDialogComponentProps?: any;
    popupEditFormDialogOnClose?: (reason?: string) => boolean;
    popupEditFormComponentProps?: any;
    popupEditFormI18nKeys: FormI18nKeys | undefined;
};

const fieldDescription = (name: string, value: any, fields: any[] | undefined) => {
    const field = fields?.find((f) => f.name === name);
    return formattedFieldValue(value, field);
};

const rowActionToIconButton = (
    rowAction: DataCommonAdditionalAction,
    row: any,
    handleRowActionClick: any,
    key: any
) => {
    const title = typeof rowAction.title === 'function' ? rowAction.title(row) : rowAction.title;
    const icon = typeof rowAction.icon === 'function' ? rowAction.icon(row) : rowAction.icon;
    return (
        <IconButton
            key={key}
            onClick={(event) => handleRowActionClick(rowAction, row, event)}
            title={title}
            size="small">
            <Icon fontSize="small">{icon ?? 'question_mark'}</Icon>
        </IconButton>
    );
};

const ListItemSecondaryActions: React.FC<any> = (props) => {
    const { row, rowAdditionalActions, rowEditActions, showUpdateDialog } = props;
    const rowActions: DataCommonAdditionalAction[] = [...rowAdditionalActions, ...rowEditActions];
    const noMenuRowActions = rowActions.filter((a) => !a.showInMenu);
    const showInMenuRowActions = rowActions.filter((a) => a.showInMenu);
    const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);
    const open = Boolean(anchorEl);
    const handleMoreMenuClick = (event: React.MouseEvent<HTMLButtonElement>) => {
        setAnchorEl(event.currentTarget);
    };
    const handleRowActionClick = (
        rowAction: DataCommonAdditionalAction,
        row: any,
        event: React.MouseEvent
    ) => {
        if (rowAction.clickShowUpdateDialog) {
            showUpdateDialog(row.id, row);
        } else {
            rowAction.onClick?.(row.id, row, event);
        }
    };
    const moreMenuIcon =
        showInMenuRowActions.length > 0 ? (
            <>
                <IconButton size="small" onClick={handleMoreMenuClick} sx={{ ml: 1 }}>
                    <Icon fontSize="small">more_vert</Icon>
                </IconButton>
                <Menu anchorEl={anchorEl} open={open} onClose={() => setAnchorEl(null)}>
                    {showInMenuRowActions.map((ra, i) => {
                        const label = typeof ra.label === 'function' ? ra.label(row) : ra.label;
                        const icon = typeof ra.icon === 'function' ? ra.icon(row) : ra.icon;
                        return (
                            <MenuItem
                                key={i}
                                onClick={(event) => handleRowActionClick(ra, row, event)}>
                                <Icon color="action" sx={{ mr: 1.5 }}>
                                    {icon ?? 'question_mark'}
                                </Icon>
                                {label}
                            </MenuItem>
                        );
                    })}
                </Menu>
            </>
        ) : null;
    return (
        <>
            {noMenuRowActions.map((ra, i) =>
                rowActionToIconButton(ra, row, handleRowActionClick, i)
            )}
            {moreMenuIcon}
        </>
    );
};

export const MuiDataList: React.FC<MuiDataListProps> = (props) => {
    const { defaultMuiComponentProps } = useMuiBaseAppContext();
    const {
        title,
        titleDisabled,
        subtitle,
        resourceName,
        resourceType,
        resourceTypeCode,
        resourceFieldName,
        primaryField,
        secondaryField,
        primaryFieldRenderer,
        secondaryFieldRenderer,
        readOnly,
        loading: loadingProp,
        findDisabled,
        quickFilterInitialValue,
        filter,
        namedQueries,
        perspectives,
        formAdditionalData,
        toolbarHide,
        toolbarBackButton,
        toolbarHideExport = true,
        toolbarHideCreate,
        toolbarHideRefresh,
        toolbarHideQuickFilter,
        toolbarCreateLink,
        toolbarElementsWithPositions,
        toolbarAdditionalRow,
        rowUpdateLink,
        rowDetailLink,
        rowDisableUpdateButton,
        rowDisableDeleteButton,
        rowDisableDetailsButton,
        rowHideUpdateButton,
        rowHideDeleteButton,
        rowHideDetailsButton,
        rowAdditionalActions = [],
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
    } = { ...defaultMuiComponentProps.dataList, ...props };
    const theme = useTheme();
    const [quickFilter, setQuickFilter] = React.useState<string>(quickFilterInitialValue ?? '');
    const {
        currentActions: apiCurrentActions,
        currentError: apiCurrentError,
        delete: apiDelete,
        bulkDelete: apiBulkDelete,
    } = useResourceApiService(resourceName);
    const findArgs = React.useMemo(
        () => ({
            filter,
            quickFilter: quickFilter?.length ? quickFilter : undefined,
            namedQueries,
            perspectives,
            unpaged: true,
        }),
        [filter, quickFilter, namedQueries, perspectives]
    );
    const {
        loading: _loading,
        fields,
        rows,
        refresh,
        export: exportt,
    } = useApiDataCommon(
        resourceName,
        resourceType,
        resourceTypeCode,
        resourceFieldName,
        loadingProp,
        findDisabled,
        findArgs,
    );
    const {
        toolbarAddElement,
        rowEditActions,
        formDialogComponent,
        triggerCreate: _showCreateDialog,
        triggerUpdate: showUpdateDialog,
    } = useDataCommonEditable(
        resourceName,
        readOnly ?? false,
        formAdditionalData,
        toolbarCreateLink,
        false,
        undefined,
        undefined,
        rowDetailLink,
        rowUpdateLink,
        rowDisableUpdateButton,
        rowDisableDeleteButton,
        rowDisableDetailsButton,
        rowHideUpdateButton,
        rowHideDeleteButton,
        rowHideDetailsButton,
        false,
        false,
        false,
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
        undefined,
        undefined,
        undefined
    );
    const toolbarNodesPosition = 2;
    const toolbarListElementsWithPositions: ReactElementWithPosition[] = [];
    toolbarAddElement != null &&
        toolbarListElementsWithPositions.push({
            position: toolbarNodesPosition,
            element: !toolbarHideCreate ? toolbarAddElement : <span />,
        });
    const toolbarNumElements =
        toolbarNodesPosition +
        (toolbarHideExport ? 0 : 1) +
        (toolbarHideRefresh ? 0 : 1) +
        (toolbarHideQuickFilter ? 0 : 1);
    const joinedElementsWithPositions = joinReactElementsWithPositionWithReactElementsWithPositions(
        toolbarNumElements,
        toolbarListElementsWithPositions,
        toolbarElementsWithPositions
    );
    const toolbar = useDataToolbar(
        title ?? capitalize(resourceName) ?? '<unknown>',
        titleDisabled ?? false,
        subtitle,
        'default',
        apiCurrentError,
        <DataQuickFilter
            value={quickFilter}
            onChange={setQuickFilter}
            sx={{ ml: 1 }} />,
        refresh,
        exportt,
        toolbarBackButton,
        toolbarHideExport,
        toolbarHideRefresh,
        toolbarHideQuickFilter,
        joinedElementsWithPositions
    );
    return (
        <>
            {!toolbarHide && toolbar}
            {toolbar}
            {toolbarAdditionalRow ? <Box sx={{ mb: 0 }}>{toolbarAdditionalRow}</Box> : null}
            {formDialogComponent}
            {rows?.length ? (
                <List
                    disablePadding
                    sx={{ border: '1px solid ' + theme.palette.divider, borderRadius: '4px' }}>
                    {rows.map((r, i) => {
                        const primary = fieldDescription(primaryField, r[primaryField], fields);
                        const secondary = secondaryField
                            ? fieldDescription(secondaryField, r[secondaryField], fields)
                            : undefined;
                        const primaryFieldRendererArgs = {
                            value: r[primaryField],
                            row: r,
                            formattedValue: primary,
                        };
                        const secondaryFieldRendererArgs = {
                            value: secondaryField ? r[secondaryField] : undefined,
                            row: r,
                            formattedValue: secondary,
                        };
                        return (
                            <ListItem
                                key={r.id}
                                secondaryAction={
                                    <ListItemSecondaryActions
                                        row={r}
                                        rowAdditionalActions={rowAdditionalActions}
                                        rowEditActions={rowEditActions}
                                        showUpdateDialog={showUpdateDialog}
                                    />
                                }
                                disablePadding
                                sx={i > 0 ? { borderTop: '1px solid #E0E0E0' } : undefined}>
                                <ListItemButton>
                                    <ListItemText
                                        primary={
                                            primaryFieldRenderer
                                                ? primaryFieldRenderer(primaryFieldRendererArgs)
                                                : primary
                                        }
                                        secondary={
                                            secondaryFieldRenderer
                                                ? secondaryFieldRenderer(secondaryFieldRendererArgs)
                                                : secondary
                                        }
                                    />
                                </ListItemButton>
                            </ListItem>
                        );
                    })}
                </List>
            ) : (
                <Box
                    sx={{
                        border: '1px solid ' + theme.palette.divider,
                        borderRadius: '4px',
                    }}>
                    <DataNoRows />
                </Box>
            )}
        </>
    );
};

export default MuiDataList;
