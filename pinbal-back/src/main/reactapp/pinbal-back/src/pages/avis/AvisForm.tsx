import React from 'react';
import { useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import { FormPage, MuiForm } from 'reactlib';
import GridFormField from '../../components/GridFormField';

const AvisFormContent: React.FC<{ id?: number }> = ({ id }) => (
    <Grid container spacing={2}>
        <GridFormField size={8} name="assumpte" />
        <GridFormField size={4} name="avisNivell" />
        <GridFormField size={12} name="missatge" />
        <GridFormField size={4} name="dataInici" />
        <GridFormField size={4} name="dataFinal" />
        {id != null && <GridFormField size={4} name="actiu" />}
    </Grid>
);

export const AvisForm: React.FC = () => {
    const { t } = useTranslation();
    const { id: paramId } = useParams();
    const id = paramId != null ? parseInt(paramId) : paramId;
    return (
        <FormPage>
            <MuiForm
                resourceName="avisResource"
                id={id}
                title={id != null ? t('page.avisos.form.titleUpdate') : t('page.avisos.form.titleCreate')}
                createLink="./{{id}}"
                commonFieldComponentProps={{ size: 'small' }}
            >
                <AvisFormContent id={id} />
            </MuiForm>
        </FormPage>
    );
};

export default AvisForm;
