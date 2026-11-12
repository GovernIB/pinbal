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
    {
        field: 'justificantGestionatPerAplicacio',
        flex: 1.2,
        type: 'boolean',
        sortable: false,
        // Només té sentit per als usuaris d'aplicació
        valueGetter: (value: any, row: any) => (row?.aplicacio ? value : null),
    },
];

const EntitatFormTabUsuarisFormContent: React.FC = () => {
    const { t } = useTranslation();
    // Un cop creada l'assignació, l'usuari no es pot canviar (només editar rols/estat).
    const { id, data, apiRef } = useFormContext();
    const aplicacio = !!data?.aplicacio;
    // Per defecte, en marcar el rol aplicació el justificant el gestiona l'aplicació client
    // (mateix comportament que el formulari JSP d'usuaris de l'entitat).
    const aplicacioAnterior = React.useRef<boolean | undefined>(undefined);
    React.useEffect(() => {
        if (aplicacioAnterior.current === false && aplicacio) {
            apiRef?.current?.setFieldValue?.('justificantGestionatPerAplicacio', true);
        }
        aplicacioAnterior.current = aplicacio;
    }, [aplicacio, apiRef]);
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
            {/* Configuració de client: només aplica als usuaris d'aplicació */}
            {aplicacio && (
                <GridFormField
                    size={12}
                    name="justificantGestionatPerAplicacio"
                    label={t('page.entitats.form.usuaris.field.justificantGestionatPerAplicacio')}
                />
            )}
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
