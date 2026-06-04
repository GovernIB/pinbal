import React from 'react';
import { useTranslation } from 'react-i18next';
import { FormField, MuiDataGrid, MuiDataGridApiRef, useFormContext } from 'reactlib';
import { GridColumnHeaderParams } from '@mui/x-data-grid';

export type PermissionGridEntry = {
    headerName: string;
    field: string;
    renderHeader?: (params: GridColumnHeaderParams) => React.ReactNode;
    description?: string;
    flex?: number;
    type?: string;
};

const PermissionGrid: React.FC<{
    resourceName: string;
    id: any;
    apiRef?: MuiDataGridApiRef;
    permissionEntries: PermissionGridEntry[];
    permissionForm: React.ReactElement | undefined;
    toolbarAdditionalRow?:
        | React.ReactElement<unknown, string | React.JSXElementConstructor<any>>
        | undefined;
    toolbarHide?: true;
    withOrganGestor?: boolean;
}> = (props) => {
    const {
        resourceName,
        id,
        permissionEntries,
        permissionForm,
        toolbarAdditionalRow,
        apiRef,
        toolbarHide,
        withOrganGestor,
    } = props;
    const { t } = useTranslation();
    const { apiRef: formApiRef } = useFormContext();
    const sidGrantedAuthorityEnumOptions = [
        {
            value: false,
            description: t('component.PermissionGrid.grantedAuthority.user'),
        },
        {
            value: true,
            description: t('component.PermissionGrid.grantedAuthority.role'),
        },
    ];
    const columns: any[] = React.useMemo(() => {
        const columns = [];
        columns.push(
            {
                headerName: t('component.PermissionGrid.tipus'),
                field: 'sidGrantedAuthority',
                sortable: false,
                flex: 1,
                valueFormatter: (value: any) =>
                    value
                        ? t('component.PermissionGrid.grantedAuthority.role')
                        : t('component.PermissionGrid.grantedAuthority.user'),
                renderEditCell: (params: any) => {
                    return (
                        <FormField
                            name={params.field}
                            label=""
                            type="enum"
                            options={sidGrantedAuthorityEnumOptions}
                            required
                            inline
                        />
                    );
                },
            },
            {
                field: 'sidName',
                sortable: false,
                flex: 4,
                renderEditCell: (params: any) => {
                    return (
                        <FormField
                            name={params.field}
                            label=""
                            required
                            inline
                            readOnly={!params.id.startsWith('###')}
                        />
                    );
                },
            }
        );
        withOrganGestor && columns.push({
            field: 'organGestor',
            sortable: false,
            flex: 1,
        });
        columns.push(
            ...permissionEntries.map((e) => ({
                headerName: e.headerName,
                field: e.field,
                type: e.type,
                sortable: false,
                flex: e.flex ?? 1,
                description: e.description,
                renderHeader: e.renderHeader,
            }))
        );
        return columns;
    }, [t, permissionEntries]);
    const handleDataGridRowChanges = () => {
        formApiRef.current?.refresh();
    };
    return (
        <MuiDataGrid
            apiRef={apiRef}
            title=""
            resourceName="aclEntryResource"
            columns={columns}
            fixedFilter={"resourceName:'" + resourceName + "' and resourceId:" + id}
            formAdditionalData={{
                sidGrantedAuthority: false,
                resourceName,
                resourceId: id,
            }}
            paginationActive
            //density="standard"
            toolbarHide={toolbarHide}
            toolbarHideQuickFilter
            toolbarAdditionalRow={toolbarAdditionalRow}
            popupEditActive
            popupEditFormDialogTitle={t('component.PermissionGrid.popupTitle')}
            popupEditFormContent={permissionForm}
            popupEditFormDialogComponentProps={{ maxWidth: 'sm' }}
            onRowCreate={handleDataGridRowChanges}
            onRowDelete={handleDataGridRowChanges}
        />
    );
};

export default PermissionGrid;
