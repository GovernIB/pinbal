import React from 'react';
import { GridColDef, GridRowParams } from '@mui/x-data-grid-pro';
import { ResourceType } from '../../ResourceApiContext';
import Dialog, { DialogProps } from '../Dialog';
import MuiDataGrid from './MuiDataGrid';

type DataGridDialogProps = DialogProps & {
    resourceName: string;
    columns: GridColDef[];
    resourceType?: ResourceType;
    resourceTypeCode?: string;
    resourceFieldName?: string;
    dataGridHeight?: number;
    dataGridOnRowClick?: (params: GridRowParams) => void;
    height?: number | null;
    dialogComponentProps?: any;
    dataGridComponentProps?: any;
};

export type DataGridDialogShowArgs = {
    title?: string;
    height?: number;
    dialogComponentProps?: any;
    dataGridComponentProps?: any;
};

export type DataGridDialogShowFn = (args?: DataGridDialogShowArgs) => Promise<any>;
export type DataGridDialogCloseFn = () => void;

export type UseDataGridDialogFn = (
    resourceName: string,
    columns: GridColDef[],
    resourceType?: ResourceType,
    resourceTypeCode?: string,
    resourceFieldName?: string,
    onRowClickEnabled?: boolean | ((row: any) => boolean),
    defaultDialogComponentProps?: any,
    defaultDataGridComponentProps?: any
) => [DataGridDialogShowFn, React.ReactElement, DataGridDialogCloseFn];

export const useDataGridDialog: UseDataGridDialogFn = (
    resourceName: string,
    columns: GridColDef[],
    resourceType?: ResourceType,
    resourceTypeCode?: string,
    resourceFieldName?: string,
    onRowClickEnabled?: boolean | ((row: any) => boolean),
    defaultDialogComponentProps?: any,
    defaultDataGridComponentProps?: any
) => {
    const [open, setOpen] = React.useState<boolean>(false);
    const [title, setTitle] = React.useState<string>();
    const [height, setHeight] = React.useState<number>();
    const [dialogComponentProps, setDialogComponentProps] = React.useState<any>(
        defaultDialogComponentProps
    );
    const [dataGridComponentProps, setDataGridComponentProps] = React.useState<any>(
        defaultDataGridComponentProps
    );
    const [resolveFn, setResolveFn] = React.useState<(value?: any) => void>();
    const [rejectFn, setRejectFn] = React.useState<(value: any) => void>();
    const showDialog = (args?: DataGridDialogShowArgs) => {
        setTitle(args?.title);
        setHeight(args?.height);
        setDialogComponentProps(
            args?.dialogComponentProps != null
                ? { ...defaultDialogComponentProps, ...args?.dialogComponentProps }
                : defaultDialogComponentProps
        );
        setDataGridComponentProps(
            args?.dataGridComponentProps != null
                ? { ...defaultDataGridComponentProps, ...args?.dataGridComponentProps }
                : defaultDataGridComponentProps
        );
        setOpen(true);
        return new Promise<string>((resolve, reject) => {
            setResolveFn(() => resolve);
            setRejectFn(() => reject);
        });
    };
    const closeCallback = () => {
        // S'ha tancat la modal o s'ha fet click a fora de la finestra
        onRowClickEnabled && rejectFn?.(undefined);
        setOpen(false);
    };
    const handleRowClick = (params: GridRowParams) => {
        if (onRowClickEnabled != null) {
            if (typeof onRowClickEnabled === 'function') {
                const enabled = onRowClickEnabled(params.row);
                enabled && resolveFn?.(params.row);
                enabled && setOpen(false);
            } else {
                onRowClickEnabled && resolveFn?.(params.row);
                setOpen(false);
            }
        } else {
            setOpen(false);
        }
    };
    const closeDialog = () => setOpen(false);
    const dialogComponent = (
        <DataGridDialog
            resourceName={resourceName}
            columns={columns}
            resourceType={resourceType}
            resourceTypeCode={resourceTypeCode}
            resourceFieldName={resourceFieldName}
            dataGridOnRowClick={onRowClickEnabled ? handleRowClick : undefined}
            height={height}
            open={open}
            closeCallback={closeCallback}
            title={title}
            dialogComponentProps={dialogComponentProps}
            dataGridComponentProps={dataGridComponentProps}
        />
    );
    return [showDialog, dialogComponent, closeDialog];
};

export const DataGridDialog: React.FC<DataGridDialogProps> = (props) => {
    const {
        columns,
        resourceName,
        resourceType,
        resourceTypeCode,
        resourceFieldName,
        dataGridHeight,
        dataGridOnRowClick,
        dialogComponentProps,
        dataGridComponentProps,
        children,
        ...otherProps
    } = props;
    return (
        <Dialog componentProps={dialogComponentProps} {...otherProps}>
            <MuiDataGrid
                columns={columns}
                resourceName={resourceName}
                resourceType={resourceType}
                resourceTypeCode={resourceTypeCode}
                resourceFieldName={resourceFieldName}
                onRowClick={dataGridOnRowClick}
                height={dataGridHeight ?? 370}
                {...dataGridComponentProps}
            />
        </Dialog>
    );
};

export default DataGridDialog;
