import React from 'react';
import { useTranslation } from 'react-i18next';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Icon from '@mui/material/Icon';
import Typography from '@mui/material/Typography';

export type ArbreRespostaNode = {
    titol?: string;
    descripcio?: string;
    document?: boolean;
    documentContingutBase64?: string;
    documentNom?: string;
    documentMimeType?: string;
    fills?: ArbreRespostaNode[];
};

// Descarrega el document embegut (base64) de com a un fitxer, equivalent a l'enllaç
// "data:...;base64,..." de renderFills.jsp.
const downloadEmbeddedDocument = (node: ArbreRespostaNode) => {
    const link = document.createElement('a');
    link.href = `data:${node.documentMimeType};base64,${node.documentContingutBase64}`;
    link.download = node.documentNom ?? 'document';
    link.click();
};

// Renderitza recursivament l'arbre de dades de la resposta SCSP (equivalent a
// import/renderFills.jsp), sense la previsualització inline de PDF del JSP.
const ConsultaRespostaArbreNode: React.FC<{ node: ArbreRespostaNode; level: number }> = ({ node, level }) => {
    const { t } = useTranslation();
    return (
        <Box sx={{ ml: level * 2, py: 0.5 }}>
            <Typography component="span" sx={{ fontWeight: 'bold', mr: 1 }}>
                {node.titol}
                {node.descripcio || node.document ? ':' : ''}
            </Typography>
            {node.document && node.documentContingutBase64 ? (
                <Button
                    size="small"
                    startIcon={<Icon fontSize="small">download</Icon>}
                    onClick={() => downloadEmbeddedDocument(node)}
                >
                    {t('comu.boto.descarregar')}
                </Button>
            ) : (
                <Typography component="span">{node.descripcio}</Typography>
            )}
            {node.fills?.map((fill, index) => (
                <ConsultaRespostaArbreNode key={index} node={fill} level={level + 1} />
            ))}
        </Box>
    );
};

const ConsultaRespostaArbre: React.FC<{ arbre?: ArbreRespostaNode }> = ({ arbre }) => {
    if (!arbre?.fills?.length) return null;
    return (
        <Box>
            {arbre.fills.map((fill, index) => (
                <ConsultaRespostaArbreNode key={index} node={fill} level={0} />
            ))}
        </Box>
    );
};

export default ConsultaRespostaArbre;
