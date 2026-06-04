import React from 'react';
import { Box, Typography } from '@mui/material';
import CloudUploadIcon from '@mui/icons-material/CloudUpload';
import { useDropzone } from 'react-dropzone';
import { useTranslation } from 'react-i18next';
import { useFormContext } from 'reactlib';

interface FormDropzoneFieldProps {
    name: string; // El 'targetFieldName' (ex: 'attachment')
    accept?: string; // Les extensions acceptades (ex: '.pdf,.zip')
    maxSize?: number; // Opcional, per defecte 10MB
    textValidacio?: string; // Per al text de l'error
    children: React.ReactNode;
}

export const FormDropzoneField: React.FC<FormDropzoneFieldProps> = ({
    name,
    accept,
    maxSize = 10 * 1024 * 1024,
    textValidacio,
    children,
}) => {
    const { t } = useTranslation();
    const { apiRef, validationSetFieldErrors } = useFormContext();

    const onDrop = (acceptedFiles: File[]) => {
        if (acceptedFiles.length > 0) {
            const file = acceptedFiles[0];
            const reader = new FileReader();

            // Aquest esdeveniment s'executa quan el fitxer s'ha acabat de llegir
            reader.onload = () => {
                const base64Content = reader.result as string;

                // Netegem el prefix del Base64
                const cleanContent = base64Content.split(',')[1] || base64Content;

                // Estructura de l'objecte pel formulari
                const fileForForm = {
                    name: file.name,
                    content: cleanContent,
                    contentType: file.type || 'application/pdf',
                    contentLength: file.size,
                };

                // Actualitzem directament el formulari
                apiRef?.current?.setFieldValue?.(name, fileForForm);
            };

            // Iniciem la lectura del fitxer com a DataURL (Base64)
            reader.readAsDataURL(file);
        }
    };

    const validarFormField = () => {
        validationSetFieldErrors(name, [
            {
                field: name,
                message: textValidacio ?? t('component.FormDropzoneField.validacio'),
            },
        ]);
    };

    const { getRootProps, getInputProps, isDragActive, fileRejections } = useDropzone({
        onDrop,
        multiple: false,
        accept: accept ? { 'application/octet-stream': accept.split(',') } : undefined,
        maxSize,
        noClick: true, // Desactivem el clic perquè no s'obri el selector si l'usuari vol clicar un input normal del formulari
    });

    React.useEffect(() => {
        if (fileRejections.length > 0) {
            validarFormField();
        }
    }, [fileRejections]);

    return (
        <Box sx={{ width: '100%' }}>
            <Box
                {...getRootProps()} // Aquí injectem els esdeveniments de drag & drop
                sx={{
                    borderColor: isDragActive ? 'primary.main' : '#ccc',
                    borderRadius: 1,
                    backgroundColor: isDragActive ? 'customBackground' : undefined,
                    transition: 'all 0.2s ease',
                    pt: 1,
                }}
            >
                {children}

                <Box
                    sx={{
                        display: 'flex',
                        flexDirection: 'column',
                        alignItems: 'center',
                        gap: 1,
                        textAlign: 'center',
                        py: 1,
                        maskImage: 'linear-gradient(to bottom, transparent 0%, black 40%)',
                        WebkitMaskImage: 'linear-gradient(to bottom, transparent 0%, black 40%)',
                        borderLeft: '2px dashed',
                        borderRight: '2px dashed',
                        borderBottom: '2px dashed',
                        borderTop: 'none',
                        borderColor: isDragActive ? 'primary.main' : '#ccc',
                    }}
                >
                    <input {...getInputProps()} />

                    <Box
                        sx={{ fontSize: '40px', color: isDragActive ? 'primary.main' : undefined }}
                    >
                        <CloudUploadIcon fontSize="inherit" />
                    </Box>

                    <Typography variant="h6" color="textSecondary" sx={{ fontWeight: 400 }}>
                        {isDragActive
                            ? t('component.FormDropzoneField.amollar')
                            : t('component.FormDropzoneField.arrosegar')}
                    </Typography>
                </Box>
            </Box>
        </Box>
    );
};
