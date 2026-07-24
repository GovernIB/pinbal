import React from 'react';
import { useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import Alert from '@mui/material/Alert';
import { FormPage, MuiForm, useFormContext } from 'reactlib';
import GridFormField from '../../components/GridFormField';
import { usePinbalContext } from '../../components/PinbalContext';

const ProcedimentFormContent: React.FC<{ setSubtitle: (subtitle: string) => void }> = (props) => {
    const { setSubtitle } = props;
    const { t } = useTranslation();
    const { data } = useFormContext();
    React.useEffect(() => {
        setSubtitle(data?.codi ? data.codi + ', ' + data.nom : '');
    }, [data]);
    return (
        <Grid container spacing={2}>
            <Grid size={12}>
                <Alert severity="info">{t('page.procediments.form.notice')}</Alert>
            </Grid>
            <GridFormField size={3} name="codi" />
            <GridFormField size={9} name="nom" />
            <GridFormField size={4} name="departament" />
            <GridFormField size={5} name="organGestor" />
            <GridFormField size={3} name="actiu" />
            <GridFormField size={4} name="codiSia" />
            <GridFormField size={4} name="valorCampAutomatizado" />
            <GridFormField size={4} name="valorCampClaseTramite" />
        </Grid>
    );
};

export const ProcedimentForm: React.FC = () => {
    const { t } = useTranslation();
    const { id: paramId } = useParams();
    const id = paramId != null ? parseInt(paramId) : paramId;
    const { currentEntitatId } = usePinbalContext();
    const [subtitle, setSubtitle] = React.useState<string>();
    return (
        <FormPage>
            <MuiForm
                resourceName="procedimentResource"
                id={id}
                additionalData={{ entitat: { id: currentEntitatId } }}
                title={id != null ? t('page.procediments.form.titleUpdate') : t('page.procediments.form.titleCreate')}
                toolbarSubtitle={id != null ? subtitle : undefined}
                createLink="./{{id}}"
                commonFieldComponentProps={{ size: 'small' }}
            >
                <ProcedimentFormContent setSubtitle={setSubtitle} />
            </MuiForm>
        </FormPage>
    );
};

export default ProcedimentForm;
