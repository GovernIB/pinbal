import React from 'react';
import { useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import { FormPage, MuiForm } from 'reactlib';
import GridFormField from '../../components/GridFormField';

const ParamConfFormContent: React.FC = () => {
    const { id } = useParams();
    return (
        <Grid container spacing={2}>
            <GridFormField size={4} name="nom" disabled={id != null} />
            <GridFormField size={8} name="valor" />
            <GridFormField size={12} name="descripcio" />
        </Grid>
    );
};

export const ParamConfForm: React.FC = () => {
    const { t } = useTranslation();
    const { id } = useParams();
    return (
        <FormPage>
            <MuiForm
                resourceName="paramConfResource"
                id={id}
                title={id != null ? t('page.paramConfs.form.titleUpdate') : t('page.paramConfs.form.titleCreate')}
                createLink="./{{id}}"
                commonFieldComponentProps={{ size: 'small' }}
            >
                <ParamConfFormContent />
            </MuiForm>
        </FormPage>
    );
};

export default ParamConfForm;
