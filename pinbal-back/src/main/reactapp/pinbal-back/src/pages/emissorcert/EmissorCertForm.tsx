import React from 'react';
import { useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import { FormPage, MuiForm } from 'reactlib';
import GridFormField from '../../components/GridFormField';

const EmissorCertFormContent: React.FC = () => (
    <Grid container spacing={2}>
        <GridFormField size={6} name="nom" />
        <GridFormField size={3} name="cif" />
        <GridFormField size={3} name="dataBaixa" />
    </Grid>
);

export const EmissorCertForm: React.FC = () => {
    const { t } = useTranslation();
    const { id: paramId } = useParams();
    const id = paramId != null ? parseInt(paramId) : paramId;
    return (
        <FormPage>
            <MuiForm
                resourceName="emissorCertResource"
                id={id}
                title={id != null ? t('page.emissorCerts.form.titleUpdate') : t('page.emissorCerts.form.titleCreate')}
                createLink="./{{id}}"
                commonFieldComponentProps={{ size: 'small' }}
            >
                <EmissorCertFormContent />
            </MuiForm>
        </FormPage>
    );
};

export default EmissorCertForm;
