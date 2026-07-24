import React from 'react';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import { MuiDataGrid, MuiDataGridColDef, useFormContext } from 'reactlib';
import GridFormField from '../../components/GridFormField';

const columns: MuiDataGridColDef[] = [{ field: 'serveiCodi', flex: 1, sortable: false }];

const EntitatFormTabServeisFormContent: React.FC = () => {
    return (
        <Grid container spacing={2}>
            <GridFormField size={12} name="serveiCodi" />
        </Grid>
    );
};

const EntitatFormTabServeis: React.FC = () => {
    const { t } = useTranslation();
    const { id, apiRef: formApiRef } = useFormContext();
    const handleDataGridRowChanges = () => formApiRef.current?.refresh();
    const columnsWithLabels = React.useMemo(
        () => columns.map((column) => ({ ...column, headerName: t(`page.entitats.form.serveis.field.${column.field}`) })),
        [t],
    );
    return (
        <MuiDataGrid
            title=""
            resourceName="entitatServeiResource"
            fixedFilter={'entitat.id:' + id}
            formAdditionalData={{ entitat: { id } }}
            columns={columnsWithLabels}
            paginationActive
            popupEditActive
            popupEditUpdateActive={false}
            popupEditFormDialogResourceTitle={t('page.entitats.form.serveis.resourceTitle')}
            popupEditFormContent={<EntitatFormTabServeisFormContent />}
            onRowCreate={handleDataGridRowChanges}
            onRowDelete={handleDataGridRowChanges}
            rowHideUpdateButton
        />
    );
};

export default EntitatFormTabServeis;
