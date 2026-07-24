import React from 'react';
import { useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import Alert from '@mui/material/Alert';
import { FormPage, MuiForm, MuiFormTabs, MuiFormTabContent } from 'reactlib';
import GridFormField from '../../components/GridFormField';
import ServeiFormTabRedireccions from './ServeiFormTabRedireccions';
import { useTabParam } from '../../hooks/useSearchParams';

const ServeiFormTabDades: React.FC = () => {
    const { t } = useTranslation();
    return (
        <Grid container spacing={2}>
            <Grid size={12}>
                <Alert severity="info">{t('page.serveis.form.notice')}</Alert>
            </Grid>
            <GridFormField size={3} name="codi" disabled />
            <GridFormField size={9} name="descripcio" />
            <GridFormField size={3} name="pinbalEntitatTipus" />
            <GridFormField size={3} name="pinbalRoleName" />
            <GridFormField size={3} name="maxPeticionsMinut" />
            <GridFormField size={3} name="actiu" />
            <GridFormField size={4} name="pinbalPermesDocumentTipusDni" />
            <GridFormField size={4} name="pinbalPermesDocumentTipusNif" />
            <GridFormField size={4} name="pinbalPermesDocumentTipusCif" />
            <GridFormField size={4} name="pinbalPermesDocumentTipusNie" />
            <GridFormField size={4} name="pinbalPermesDocumentTipusPas" />
            <GridFormField size={4} name="pinbalDocumentObligatori" />
        </Grid>
    );
};

const ServeiFormContent: React.FC = () => {
    const { t } = useTranslation();
    const initialTab = useTabParam();
    const tabs = [t('page.serveis.form.tabs.dades'), t('page.serveis.form.tabs.redireccions')];
    return (
        <MuiFormTabs tabs={tabs} tabIndexesWithGrids={[1]} initialIndex={initialTab}>
            <MuiFormTabContent index={0} showOnCreate>
                <ServeiFormTabDades />
            </MuiFormTabContent>
            <MuiFormTabContent index={1}>
                <ServeiFormTabRedireccions />
            </MuiFormTabContent>
        </MuiFormTabs>
    );
};

export const ServeiForm: React.FC = () => {
    const { t } = useTranslation();
    const { id: paramId } = useParams();
    const id = paramId != null ? parseInt(paramId) : paramId;
    return (
        <FormPage>
            <MuiForm
                resourceName="serveiResource"
                id={id}
                title={t('page.serveis.form.title')}
                commonFieldComponentProps={{ size: 'small' }}
            >
                <ServeiFormContent />
            </MuiForm>
        </FormPage>
    );
};

export default ServeiForm;
