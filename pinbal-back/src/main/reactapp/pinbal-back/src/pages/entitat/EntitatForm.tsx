import React from 'react';
import { useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import { FormPage, MuiForm, MuiFormTabs, MuiFormTabContent, useFormContext } from 'reactlib';
import GridFormField from '../../components/GridFormField';
import EntitatFormTabServeis from './EntitatFormTabServeis';
import EntitatFormTabUsuaris from './EntitatFormTabUsuaris';
import { useTabParam } from '../../hooks/useSearchParams';

const EntitatFormTabDades: React.FC = () => {
    return (
        <Grid container spacing={2}>
            <GridFormField size={4} name="codi" />
            <GridFormField size={8} name="nom" />
            <GridFormField size={4} name="cif" />
            <GridFormField size={4} name="unitatArrel" />
            <GridFormField size={4} name="tipus" />
            <GridFormField size={3} name="activa" />
        </Grid>
    );
};

const EntitatFormContent: React.FC<{ setSubtitle: (subtitle: string) => void }> = (props) => {
    const { setSubtitle } = props;
    const { t } = useTranslation();
    const { data } = useFormContext();
    const initialTab = useTabParam();
    React.useEffect(() => {
        setSubtitle(data?.codi ? data.codi + ', ' + data.nom : '');
    }, [data]);

    const tabs = [
        t('page.entitats.form.tabs.dades'),
        t('page.entitats.form.tabs.serveis'),
        t('page.entitats.form.tabs.usuaris'),
    ];
    return (
        <MuiFormTabs tabs={tabs} tabIndexesWithGrids={[1, 2]} initialIndex={initialTab}>
            <MuiFormTabContent index={0} showOnCreate>
                <EntitatFormTabDades />
            </MuiFormTabContent>
            <MuiFormTabContent index={1}>
                <EntitatFormTabServeis />
            </MuiFormTabContent>
            <MuiFormTabContent index={2}>
                <EntitatFormTabUsuaris />
            </MuiFormTabContent>
        </MuiFormTabs>
    );
};

export const EntitatForm: React.FC = () => {
    const { t } = useTranslation();
    const { id: paramId } = useParams();
    const id = paramId != null ? parseInt(paramId) : paramId;
    const [subtitle, setSubtitle] = React.useState<string>();
    return (
        <FormPage>
            <MuiForm
                resourceName="entitatResource"
                id={id}
                title={id != null ? t('page.entitats.form.titleUpdate') : t('page.entitats.form.titleCreate')}
                toolbarSubtitle={id != null ? subtitle : undefined}
                createLink="./{{id}}"
                commonFieldComponentProps={{ size: 'small' }}
            >
                <EntitatFormContent setSubtitle={setSubtitle} />
            </MuiForm>
        </FormPage>
    );
};

export default EntitatForm;
