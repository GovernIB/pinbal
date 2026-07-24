import React from 'react';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import { MuiDataGrid, MuiDataGridColDef, useFormContext } from 'reactlib';
import GridFormField from '../../components/GridFormField';

const columns: MuiDataGridColDef[] = [
    { field: 'urlDesti', flex: 3, sortable: false },
    { field: 'entitat', flex: 1.5, sortable: false },
];

const ServeiFormTabRedireccionsFormContent: React.FC = () => {
    return (
        <Grid container spacing={2}>
            <GridFormField size={12} name="urlDesti" />
            <GridFormField size={12} name="entitat" />
        </Grid>
    );
};

const ServeiFormTabRedireccions: React.FC = () => {
    const { t } = useTranslation();
    const { data, apiRef: formApiRef } = useFormContext();
    const handleDataGridRowChanges = () => formApiRef.current?.refresh();
    const columnsWithLabels = React.useMemo(
        () => columns.map((column) => ({ ...column, headerName: t(`page.serveis.form.redireccions.field.${column.field}`) })),
        [t],
    );
    return (
        <MuiDataGrid
            title=""
            resourceName="serveiBusResource"
            fixedFilter={"serveiCodi:'" + data?.codi + "'"}
            formAdditionalData={{ serveiCodi: data?.codi }}
            columns={columnsWithLabels}
            paginationActive
            popupEditActive
            popupEditFormDialogResourceTitle={t('page.serveis.form.redireccions.resourceTitle')}
            popupEditFormContent={<ServeiFormTabRedireccionsFormContent />}
            onRowCreate={handleDataGridRowChanges}
            onRowUpdate={handleDataGridRowChanges}
            onRowDelete={handleDataGridRowChanges}
        />
    );
};

export default ServeiFormTabRedireccions;
