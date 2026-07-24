import React from 'react';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import { MuiDataGrid, MuiDataGridColDef, useFormContext } from 'reactlib';
import GridFormField from '../../components/GridFormField';

const columns: MuiDataGridColDef[] = [
    { field: 'usuariCodi', flex: 1.5, sortable: false },
    { field: 'departament', flex: 1.5, sortable: false },
    { field: 'principal', flex: 0.8, type: 'boolean', sortable: false },
    { field: 'representant', flex: 0.8, type: 'boolean', sortable: false },
    { field: 'delegat', flex: 0.8, type: 'boolean', sortable: false },
    { field: 'auditor', flex: 0.8, type: 'boolean', sortable: false },
    { field: 'aplicacio', flex: 0.8, type: 'boolean', sortable: false },
    { field: 'actiu', flex: 0.6, type: 'boolean', sortable: false },
];

const EntitatFormTabUsuarisFormContent: React.FC = () => {
    // Un cop creada l'assignació, l'usuari no es pot canviar (només editar rols/estat).
    const { id } = useFormContext();
    return (
        <Grid container spacing={2}>
            <GridFormField size={6} name="usuariCodi" disabled={id != null} />
            <GridFormField size={6} name="departament" />
            <GridFormField size={4} name="principal" />
            <GridFormField size={4} name="representant" />
            <GridFormField size={4} name="delegat" />
            <GridFormField size={4} name="auditor" />
            <GridFormField size={4} name="aplicacio" />
            <GridFormField size={4} name="actiu" />
        </Grid>
    );
};

const EntitatFormTabUsuaris: React.FC = () => {
    const { t } = useTranslation();
    const { id, apiRef: formApiRef } = useFormContext();
    const handleDataGridRowChanges = () => formApiRef.current?.refresh();
    const columnsWithLabels = React.useMemo(
        () => columns.map((column) => ({ ...column, headerName: t(`page.entitats.form.usuaris.field.${column.field}`) })),
        [t],
    );
    return (
        <MuiDataGrid
            title=""
            resourceName="entitatUsuariResource"
            fixedFilter={'entitat.id:' + id}
            formAdditionalData={{ entitat: { id } }}
            columns={columnsWithLabels}
            paginationActive
            popupEditActive
            popupEditFormDialogResourceTitle={t('page.entitats.form.usuaris.resourceTitle')}
            popupEditFormContent={<EntitatFormTabUsuarisFormContent />}
            onRowCreate={handleDataGridRowChanges}
            onRowUpdate={handleDataGridRowChanges}
            rowHideDeleteButton
        />
    );
};

export default EntitatFormTabUsuaris;
