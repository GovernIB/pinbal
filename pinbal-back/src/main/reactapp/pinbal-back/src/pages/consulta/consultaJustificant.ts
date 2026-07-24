import { saveAs } from 'file-saver';
import { ResourceApiReportArgs, ResourceApiBlobResponse } from 'reactlib';

// Estats del justificant per als quals hi ha contingut descarregable (JSP: estat-pendent / estat-ok).
export const JUSTIFICANT_DISPONIBLE_ESTATS = ['PENDENT', 'OK', 'OK_NO_CUSTODIA'];

type ArtifactReportFn = (
    id: number,
    args: ResourceApiReportArgs,
) => Promise<ResourceApiBlobResponse[] | ResourceApiBlobResponse>;

// El backend ignora el fileType sol·licitat i retorna sempre el contingut real de l'informe
// (PDF pel justificant, ZIP pels arxius comprimits); el valor 'PDF' només serveix perquè el
// mecanisme genèric de reactlib faci una petició binària (blob) en lloc d'una de JSON.
export const downloadArtifactReport = (
    artifactReport: ArtifactReportFn,
    id: number,
    code: string,
    fallbackFileName: string,
): Promise<void> =>
    artifactReport(id, { code, fileType: 'PDF' }).then((response) => {
        const blobResponse = response as ResourceApiBlobResponse;
        saveAs(blobResponse.blob, blobResponse.fileName ?? fallbackFileName);
    });
