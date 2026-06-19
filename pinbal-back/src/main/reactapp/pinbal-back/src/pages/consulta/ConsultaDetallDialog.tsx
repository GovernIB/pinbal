import React from 'react';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import Button from '@mui/material/Button';
import Alert from '@mui/material/Alert';
import Icon from '@mui/material/Icon';
import { saveAs } from 'file-saver';
import {
    MuiFormDialog,
    MuiDataFormDialogApi,
    useFormContext,
    useResourceApiContext,
    useAuthContext,
} from 'reactlib';
import GridFormField from '../../components/GridFormField';

type Origen = 'recents' | 'historic';

// Construeix la URL de descàrrega del justificant a partir de l'apiUrl del recurs.
const justificantPath = (origen: Origen) => (origen === 'recents' ? 'consultes' : 'historicConsultes');

const parseFilename = (contentDisposition: string | null): string | undefined => {
    if (!contentDisposition) return undefined;
    const match = /filename="?([^"]+)"?/.exec(contentDisposition);
    return match?.[1];
};

// Botó de descàrrega del justificant. Llegeix l'estat de la consulta del context del formulari.
const JustificantButton: React.FC<{ origen: Origen }> = ({ origen }) => {
    const { t } = useTranslation();
    const { data } = useFormContext();
    const { apiUrl, httpHeaders } = useResourceApiContext();
    const { getToken } = useAuthContext();
    const [downloading, setDownloading] = React.useState(false);
    const [error, setError] = React.useState(false);

    const justificantEstat = data?.justificantEstat;
    const justificantEstatError = data?.justificantEstatError;
    const noDisponible = justificantEstat == null || justificantEstat === 'NO_DISPONIBLE';

    const handleDownload = async () => {
        setError(false);
        setDownloading(true);
        try {
            const url = `${apiUrl}/${justificantPath(origen)}/${data.id}/justificant`;
            const headers: Record<string, string> = {};
            const token = getToken?.();
            if (token) headers['Authorization'] = `Bearer ${token}`;
            httpHeaders?.forEach((h) => Object.entries(h).forEach(([k, v]) => (headers[k] = v as string)));
            const resp = await fetch(url, { headers, credentials: 'include' });
            if (resp.status === 204 || !resp.ok) {
                setError(true);
                return;
            }
            const blob = await resp.blob();
            const filename = parseFilename(resp.headers.get('Content-Disposition')) ?? `justificant_${data.id}.pdf`;
            saveAs(blob, filename);
        } catch (e) {
            setError(true);
        } finally {
            setDownloading(false);
        }
    };

    return (
        <>
            <Grid size={12} sx={{ display: 'flex', gap: 1, alignItems: 'center' }}>
                <Button
                    variant="contained"
                    startIcon={<Icon>download</Icon>}
                    onClick={handleDownload}
                    disabled={downloading || noDisponible}
                >
                    {t('page.consulta.detall.descarregarJustificant')}
                </Button>
            </Grid>
            {justificantEstatError && data?.justificantError && (
                <Grid size={12}>
                    <Alert severity="error" sx={{ '& .MuiAlert-message': { width: '100%' } }}>
                        <pre style={{ whiteSpace: 'pre-wrap', margin: 0, maxHeight: 160, overflow: 'auto' }}>
                            {data.justificantError}
                        </pre>
                    </Alert>
                </Grid>
            )}
            {error && (
                <Grid size={12}>
                    <Alert severity="warning">{t('page.consulta.detall.justificantNoDisponible')}</Alert>
                </Grid>
            )}
        </>
    );
};

const ConsultaDetallContent: React.FC<{ origen: Origen }> = ({ origen }) => {
    const { t } = useTranslation();
    const fl = (name: string) => t(`page.consulta.detall.field.${name}`);
    return (
        <Grid container spacing={2}>
            <GridFormField size={{ xs: 12, sm: 6 }} name="scspPeticionId" label={fl('scspPeticionId')} disabled />
            <GridFormField size={{ xs: 12, sm: 6 }} name="scspSolicitudId" label={fl('scspSolicitudId')} disabled />
            <GridFormField size={{ xs: 12, sm: 4 }} name="estat" label={fl('estat')} disabled />
            <GridFormField size={{ xs: 12, sm: 4 }} name="creacioData" label={fl('creacioData')} disabled />
            <GridFormField size={{ xs: 12, sm: 4 }} name="respostaData" label={fl('respostaData')} disabled />
            <GridFormField size={{ xs: 12, sm: 6 }} name="entitatNom" label={fl('entitatNom')} disabled />
            <GridFormField size={{ xs: 12, sm: 6 }} name="entitatCif" label={fl('entitatCif')} disabled />
            <GridFormField size={{ xs: 12, sm: 6 }} name="procedimentCodiNom" label={fl('procedimentCodiNom')} disabled />
            <GridFormField size={{ xs: 12, sm: 6 }} name="serveiCodiNom" label={fl('serveiCodiNom')} disabled />
            <GridFormField size={{ xs: 12, sm: 6 }} name="funcionariNomAmbDocument" label={fl('funcionariNomAmbDocument')} disabled />
            <GridFormField size={{ xs: 12, sm: 6 }} name="departamentNom" label={fl('departamentNom')} disabled />
            <GridFormField size={{ xs: 12, sm: 4 }} name="titularDocumentTipus" label={fl('titularDocumentTipus')} disabled />
            <GridFormField size={{ xs: 12, sm: 4 }} name="titularDocumentNum" label={fl('titularDocumentNum')} disabled />
            <GridFormField size={{ xs: 12, sm: 4 }} name="titularNomComplet" label={fl('titularNomComplet')} disabled />
            <GridFormField size={{ xs: 12, sm: 6 }} name="finalitat" label={fl('finalitat')} disabled />
            <GridFormField size={{ xs: 12, sm: 3 }} name="consentiment" label={fl('consentiment')} disabled />
            <GridFormField size={{ xs: 12, sm: 3 }} name="expedientId" label={fl('expedientId')} disabled />
            <GridFormField size={{ xs: 12, sm: 6 }} name="justificantEstat" label={fl('justificantEstat')} disabled />
            <JustificantButton origen={origen} />
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
            <ConsultaDetallContent origen={origen} />
        </MuiFormDialog>
    );
};

export default ConsultaDetallDialog;
