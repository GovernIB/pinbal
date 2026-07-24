import React from 'react';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import Button from '@mui/material/Button';
import Alert from '@mui/material/Alert';
import Icon from '@mui/material/Icon';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import {
    MuiFormDialog,
    MuiDataFormDialogApi,
    MuiFormTabs,
    MuiFormTabContent,
    useFormContext,
    useResourceApiService,
    useMuiMessageDialog,
} from 'reactlib';
import GridFormField from '../../components/GridFormField';
import { usePinbalContext, ROLE_ADMIN } from '../../components/PinbalContext';
import { downloadArtifactReport, JUSTIFICANT_DISPONIBLE_ESTATS } from './consultaJustificant';
import { ConsultaEstatChip } from './ConsultaEstatChip';
import ConsultaRespostaArbre, { ArbreRespostaNode } from './ConsultaRespostaArbre';
import XmlViewerDialog from './XmlViewerDialog';
import { Origen } from './ConsultaGrid';

// Botó genèric de descàrrega d'un informe (artifact REPORT) del recurs de consulta.
const DownloadReportButton: React.FC<{
    resourceName: string;
    code: string;
    label: string;
    fallbackFileName: string;
    disabled?: boolean;
}> = ({ resourceName, code, label, fallbackFileName, disabled }) => {
    const { id } = useFormContext();
    const { artifactReport } = useResourceApiService(resourceName);
    const [downloading, setDownloading] = React.useState(false);
    const [error, setError] = React.useState(false);
    const { t } = useTranslation();

    const handleDownload = () => {
        setError(false);
        setDownloading(true);
        downloadArtifactReport(artifactReport, id, code, fallbackFileName)
            .catch(() => setError(true))
            .finally(() => setDownloading(false));
    };

    return (
        <Grid sx={{ display: 'flex', flexDirection: 'column', gap: 0.5 }}>
            <Button
                variant="outlined"
                size="small"
                startIcon={<Icon fontSize="small">download</Icon>}
                onClick={handleDownload}
                disabled={disabled || downloading}
            >
                {label}
            </Button>
            {error && <Alert severity="warning">{t('page.consulta.detall.justificantNoDisponible')}</Alert>}
        </Grid>
    );
};

// Botó de "veure xml" que obre un diàleg de només lectura amb el contingut cru ja carregat
// junt amb el recurs (peticioXml/respostaXml), sense necessitat de cap petició addicional.
const ViewXmlButton: React.FC<{ label: string; dialogTitle: string; xml?: string }> = ({
    label,
    dialogTitle,
    xml,
}) => {
    const [open, setOpen] = React.useState(false);
    return (
        <>
            <Button variant="outlined" size="small" startIcon={<Icon fontSize="small">info</Icon>} onClick={() => setOpen(true)}>
                {label}
            </Button>
            <XmlViewerDialog open={open} title={dialogTitle} xml={xml} onClose={() => setOpen(false)} />
        </>
    );
};

// Capçalera persistent (fora de les pestanyes), equivalent al "tab-header" sempre visible del JSP:
// identificació de la petició + accions de veure/descarregar els missatges XML.
const ConsultaDetallPeticioHeader: React.FC<{ resourceName: string }> = ({ resourceName }) => {
    const { t } = useTranslation();
    const { data } = useFormContext();
    const { currentRole } = usePinbalContext();
    const isAdmin = currentRole === ROLE_ADMIN;
    const fl = (name: string) => t(`page.consulta.detall.field.${name}`);
    return (
        <Box sx={{ mb: 2 }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1 }}>
                <Typography variant="subtitle1" sx={{ fontWeight: 'bold' }}>
                    {t('page.consulta.detall.tabs.dadesPeticio')}
                </Typography>
                <Box sx={{ display: 'flex', gap: 1 }}>
                    {data?.hiHaPeticio && (
                        <ViewXmlButton
                            label={t('page.consulta.detall.peticio.veureXml')}
                            dialogTitle={t('page.consulta.detall.peticio.xmlPeticioTitol')}
                            xml={data?.peticioXml}
                        />
                    )}
                    {isAdmin && (
                        <DownloadReportButton
                            resourceName={resourceName}
                            code="xmlZip"
                            label={t('page.consulta.detall.peticio.descarregarMissatges')}
                            fallbackFileName={`xmls_${data?.scspPeticionId}.zip`}
                        />
                    )}
                </Box>
            </Box>
            <Grid container spacing={2}>
                <GridFormField size={{ xs: 12, sm: 3 }} name="scspPeticionId" label={fl('scspPeticionId')} disabled />
                <GridFormField size={{ xs: 12, sm: 3 }} name="scspSolicitudId" label={fl('scspSolicitudId')} disabled />
                <GridFormField size={{ xs: 12, sm: 2 }} name="creacioData" label={fl('creacioData')} disabled />
                <GridFormField size={{ xs: 12, sm: 2 }} name="procedimentCodiNom" label={fl('procedimentCodiNom')} disabled />
                <GridFormField size={{ xs: 12, sm: 2 }} name="serveiCodiNom" label={fl('serveiCodiNom')} disabled />
            </Grid>
        </Box>
    );
};

const ConsultaDetallAltresDadesTab: React.FC = () => {
    const { t } = useTranslation();
    const fl = (name: string) => t(`page.consulta.detall.field.${name}`);
    return (
        <Grid container spacing={2}>
            <GridFormField size={{ xs: 12, sm: 6 }} name="funcionariNomAmbDocument" label={fl('funcionariNomAmbDocument')} disabled />
            <GridFormField size={{ xs: 12, sm: 6 }} name="departamentNom" label={fl('departamentNom')} disabled />
            <GridFormField size={{ xs: 12, sm: 6 }} name="entitatNom" label={fl('entitatNom')} disabled />
            <GridFormField size={{ xs: 12, sm: 6 }} name="entitatCif" label={fl('entitatCif')} disabled />
            <GridFormField size={{ xs: 12, sm: 4 }} name="titularDocumentTipus" label={fl('titularDocumentTipus')} disabled />
            <GridFormField size={{ xs: 12, sm: 4 }} name="titularDocumentNum" label={fl('titularDocumentNum')} disabled />
            <GridFormField size={{ xs: 12, sm: 4 }} name="titularNomComplet" label={fl('titularNomComplet')} disabled />
            <GridFormField size={{ xs: 12, sm: 9 }} name="finalitat" label={fl('finalitat')} disabled />
            <GridFormField size={{ xs: 12, sm: 3 }} name="consentiment" label={fl('consentiment')} disabled />
            <GridFormField size={{ xs: 12, sm: 4 }} name="expedientId" label={fl('expedientId')} disabled />
        </Grid>
    );
};

// Arbre de dades de la resposta: només disponible per a consultes recents (l'històric no permet
// regenerar-lo, per limitacions del servei de domini).
const ConsultaDetallRespostaTab: React.FC<{ resourceName: string; supportsRespostaArbre: boolean }> = ({
    resourceName,
    supportsRespostaArbre,
}) => {
    const { data, id } = useFormContext();
    const { artifactReport } = useResourceApiService(resourceName);
    const { t } = useTranslation();
    const fl = (name: string) => t(`page.consulta.detall.field.${name}`);
    const [arbre, setArbre] = React.useState<ArbreRespostaNode>();
    const [loading, setLoading] = React.useState(false);

    React.useEffect(() => {
        if (supportsRespostaArbre && id != null && data?.respostaData) {
            setLoading(true);
            artifactReport(id, { code: 'respostaArbre' })
                .then((result) => setArbre(Array.isArray(result) ? result[0] : result))
                .finally(() => setLoading(false));
        }
    }, [supportsRespostaArbre, id, data?.respostaData, artifactReport]);

    if (!data?.respostaData) {
        return <Alert severity="info">{t('page.consulta.detall.justificantNoDisponible')}</Alert>;
    }
    return (
        <Grid container spacing={2}>
            <GridFormField size={{ xs: 12, sm: 4 }} name="respostaData" label={fl('respostaData')} disabled />
            {data?.hiHaResposta && (
                <Grid sx={{ display: 'flex', alignItems: 'center' }}>
                    <ViewXmlButton
                        label={t('page.consulta.detall.resposta.veureXml')}
                        dialogTitle={t('page.consulta.detall.resposta.xmlRespostaTitol')}
                        xml={data?.respostaXml}
                    />
                </Grid>
            )}
            {loading && <Grid size={12}>{t('comu.opcio.carregant')}</Grid>}
            {arbre && (
                <Grid size={12}>
                    <ConsultaRespostaArbre arbre={arbre} />
                </Grid>
            )}
        </Grid>
    );
};

const ReintentarJustificantButton: React.FC<{ resourceName: string }> = ({ resourceName }) => {
    const { t } = useTranslation();
    const { id, apiRef } = useFormContext();
    const { artifactReport } = useResourceApiService(resourceName);
    const [showMessageDialog, messageDialogComponent] = useMuiMessageDialog();
    const [loading, setLoading] = React.useState(false);

    const handleClick = async () => {
        setLoading(true);
        try {
            await artifactReport(id, { code: 'justificantReintentar' });
            apiRef?.current?.refresh();
            await showMessageDialog(
                t('page.consulta.detall.justificants.reintentar.okTitol'),
                t('page.consulta.detall.justificants.reintentar.okMissatge'),
            );
        } catch {
            await showMessageDialog(
                t('page.consulta.detall.justificants.reintentar.errorTitol'),
                t('page.consulta.detall.justificants.reintentar.errorMissatge'),
            );
        } finally {
            setLoading(false);
        }
    };

    return (
        <>
            <Button
                variant="outlined"
                size="small"
                color="warning"
                startIcon={<Icon fontSize="small">refresh</Icon>}
                onClick={handleClick}
                disabled={loading}
            >
                {t('page.consulta.detall.justificants.reintentar.boto')}
            </Button>
            {messageDialogComponent}
        </>
    );
};

const ConsultaDetallJustificantsTab: React.FC<{ resourceName: string }> = ({ resourceName }) => {
    const { t } = useTranslation();
    const { data } = useFormContext();
    const fl = (name: string) => t(`page.consulta.detall.field.${name}`);

    const justificantDisponible =
        data?.estat === 'Tramitada' && JUSTIFICANT_DISPONIBLE_ESTATS.includes(data?.justificantEstat);

    return (
        <Grid container spacing={2}>
            <GridFormField size={{ xs: 12, sm: 6 }} name="justificantEstat" label={fl('justificantEstat')} disabled />
            {data?.justificantEstatError && data?.justificantError && (
                <Grid size={12}>
                    <Alert severity="error" sx={{ '& .MuiAlert-message': { width: '100%' } }}>
                        <Box component="strong" sx={{ display: 'block', mb: 0.5 }}>
                            {t('page.consulta.detall.justificants.error')}
                        </Box>
                        <pre style={{ whiteSpace: 'pre-wrap', margin: 0, maxHeight: 160, overflow: 'auto' }}>
                            {data.justificantError}
                        </pre>
                    </Alert>
                </Grid>
            )}
            {data?.justificantEstatError && (
                <Grid>
                    <ReintentarJustificantButton resourceName={resourceName} />
                </Grid>
            )}
            <Grid>
                <DownloadReportButton
                    resourceName={resourceName}
                    code="justificant"
                    label={t('page.consulta.detall.justificants.justificant.descarregar')}
                    fallbackFileName={`justificant_${data?.scspPeticionId}.pdf`}
                    disabled={!justificantDisponible}
                />
            </Grid>
            {data?.multiple && (
                <>
                    <Grid size={12}>
                        <Box component="strong">{t('page.consulta.detall.justificants.zip.titol')}</Box>
                    </Grid>
                    <Grid>
                        <DownloadReportButton
                            resourceName={resourceName}
                            code="justificantZip"
                            label={t('page.consulta.detall.justificants.zip.descarregarJustificants')}
                            fallbackFileName={`justificants_${data?.scspPeticionId}.zip`}
                        />
                    </Grid>
                </>
            )}
        </Grid>
    );
};

const ConsultaDetallContent: React.FC<{ resourceName: string; supportsRespostaArbre: boolean }> = ({
    resourceName,
    supportsRespostaArbre,
}) => {
    const { t } = useTranslation();
    const { data } = useFormContext();
    const tabs = [
        t('page.consulta.detall.tabs.altresDades'),
        t('page.consulta.detall.tabs.resposta'),
        t('page.consulta.detall.tabs.justificants'),
    ];
    return (
        <Grid container spacing={2}>
            <Grid size={12}>
                <ConsultaDetallPeticioHeader resourceName={resourceName} />
            </Grid>
            <Grid size={12} sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                <ConsultaEstatChip estat={data?.estat} error={data?.error} />
            </Grid>
            {data?.estat === 'Error' && data?.error && (
                <Grid size={12}>
                    <Alert severity="error">{data.error.split('|||')[0]}</Alert>
                </Grid>
            )}
            <Grid size={12}>
                <MuiFormTabs tabs={tabs}>
                    <MuiFormTabContent index={0}>
                        <ConsultaDetallAltresDadesTab />
                    </MuiFormTabContent>
                    <MuiFormTabContent index={1}>
                        <ConsultaDetallRespostaTab resourceName={resourceName} supportsRespostaArbre={supportsRespostaArbre} />
                    </MuiFormTabContent>
                    <MuiFormTabContent index={2}>
                        <ConsultaDetallJustificantsTab resourceName={resourceName} />
                    </MuiFormTabContent>
                </MuiFormTabs>
            </Grid>
        </Grid>
    );
};

const ConsultaDetallDialog: React.FC<{
    apiRef: React.RefObject<MuiDataFormDialogApi | null>;
    resourceName: string;
    origen: Origen;
}> = ({ apiRef, resourceName, origen }) => {
    const { t } = useTranslation();
    return (
        <MuiFormDialog
            resourceName={resourceName}
            title={t('page.consulta.detall.titol')}
            apiRef={apiRef}
            dialogComponentProps={{ fullWidth: true, maxWidth: 'lg' }}
            formComponentProps={{ commonFieldComponentProps: { size: 'small' } }}
            dialogButtons={[
                {
                    value: false,
                    text: t('page.consulta.detall.tancar'),
                    componentProps: { variant: 'outlined' },
                },
            ]}
        >
            <ConsultaDetallContent resourceName={resourceName} supportsRespostaArbre={origen === 'recents'} />
        </MuiFormDialog>
    );
};

export default ConsultaDetallDialog;
