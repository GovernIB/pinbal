import React from 'react';
import { useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import { FormPage, MuiForm } from 'reactlib';
import GridFormField from '../../components/GridFormField';

const ClauPrivadaFormContent: React.FC = () => (
    <Grid container spacing={2}>
        <GridFormField size={4} name="alies" />
        <GridFormField size={4} name="nom" />
        <GridFormField size={4} name="numSerie" />
        <GridFormField size={4} name="password" />
        <GridFormField size={4} name="dataAlta" />
        <GridFormField size={4} name="dataBaixa" />
        <GridFormField size={6} name="organisme" />
        <GridFormField size={3} name="interoperabilitat" />
        <GridFormField size={3} name="perEntitat" />
    </Grid>
);

export const ClauPrivadaForm: React.FC = () => {
    const { t } = useTranslation();
    const { id: paramId } = useParams();
    const id = paramId != null ? parseInt(paramId) : paramId;
    return (
        <FormPage>
            <MuiForm
                resourceName="clauPrivadaResource"
                id={id}
                title={id != null ? t('page.clauPrivades.form.titleUpdate') : t('page.clauPrivades.form.titleCreate')}
                createLink="./{{id}}"
                commonFieldComponentProps={{ size: 'small' }}
            >
                <ClauPrivadaFormContent />
            </MuiForm>
        </FormPage>
    );
};

export default ClauPrivadaForm;
