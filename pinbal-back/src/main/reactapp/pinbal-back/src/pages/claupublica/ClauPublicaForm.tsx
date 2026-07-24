import React from 'react';
import { useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import { FormPage, MuiForm } from 'reactlib';
import GridFormField from '../../components/GridFormField';

const ClauPublicaFormContent: React.FC = () => (
    <Grid container spacing={2}>
        <GridFormField size={4} name="alies" />
        <GridFormField size={4} name="nom" />
        <GridFormField size={4} name="numSerie" />
        <GridFormField size={6} name="dataAlta" />
        <GridFormField size={6} name="dataBaixa" />
    </Grid>
);

export const ClauPublicaForm: React.FC = () => {
    const { t } = useTranslation();
    const { id: paramId } = useParams();
    const id = paramId != null ? parseInt(paramId) : paramId;
    return (
        <FormPage>
            <MuiForm
                resourceName="clauPublicaResource"
                id={id}
                title={id != null ? t('page.clauPubliques.form.titleUpdate') : t('page.clauPubliques.form.titleCreate')}
                createLink="./{{id}}"
                commonFieldComponentProps={{ size: 'small' }}
            >
                <ClauPublicaFormContent />
            </MuiForm>
        </FormPage>
    );
};

export default ClauPublicaForm;
