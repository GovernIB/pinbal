import * as React from 'react';
import IconButton from '@mui/material/IconButton';
import Icon from '@mui/material/Icon';
import TextField from '@mui/material/TextField';
import { useFormContext } from '../../form/FormContext';
import { useResourceApiService, ResourceApiBlobResponse } from '../../ResourceApiProvider';
import { FormFieldCustomProps } from '../../form/FormField';
import { toBase64 } from '../../../util/files';
import { useFormFieldCommon } from './FormFieldText';

type FormFieldFileProps = FormFieldCustomProps & {
    /** Atribut accept per a l'input HTML */
    accept?: string;
    /** Callback que es cridarà al descarregar l'arxiu */
    onFileDownload?: (blobResponse: ResourceApiBlobResponse) => void;
};

export const FormFieldFile: React.FC<FormFieldFileProps> = (props) => {
    const {
        name,
        label,
        value,
        field,
        fieldError,
        inline,
        required,
        disabled,
        readOnly,
        onChange,
        componentProps,
        accept,
        onFileDownload,
    } = props;
    const [fileName, setFileName] = React.useState<string | undefined>(value?.name);
    const fileInputRef = React.useRef<HTMLInputElement>(null);
    const { helperText, title, startAdornment } = useFormFieldCommon(
        field,
        fieldError,
        inline,
        componentProps
    );
    const { id, resourceName } = useFormContext();
    const { isReady: apiIsReady, fieldDownload: apiFieldDownload } =
        useResourceApiService(resourceName);
    const handleOnChange = () => {
        const currentFile = fileInputRef.current?.files?.[0];
        if (currentFile != null) {
            toBase64(currentFile).then((base64) => {
                const fileValue = {
                    name: currentFile.name,
                    content: base64,
                    contentType: currentFile.type,
                    contentLength: currentFile.size,
                };
                onChange(fileValue);
                setFileName(currentFile?.name);
            });
        } else {
            onChange(null);
            setFileName(undefined);
        }
    };
    const handleClearButtonClick = (event: any) => {
        event.stopPropagation();
        event.preventDefault();
        if (fileInputRef.current) {
            fileInputRef.current.value = '';
        }
        onChange(null);
        setFileName(undefined);
    };
    const handleDownloadButtonClick = (event: any) => {
        event.stopPropagation();
        event.preventDefault();
        if (apiIsReady) {
            apiFieldDownload(id, { fieldName: name }).then(onFileDownload);
        }
    };
    const clearEndAdornment =
        value != null ? (
            <IconButton size="small" onClick={handleClearButtonClick}>
                <Icon fontSize="inherit">clear</Icon>
            </IconButton>
        ) : undefined;
    const downloadButtonActive =
        id != null && apiIsReady && value != null && !value.content && onFileDownload != null;
    const downloadEndAdornment = downloadButtonActive ? (
        <IconButton size="small" onClick={handleDownloadButtonClick}>
            <Icon fontSize="inherit">file_download</Icon>
        </IconButton>
    ) : undefined;
    const endAdornment = (
        <>
            {clearEndAdornment}
            {downloadEndAdornment}
            {componentProps?.slotProps?.input?.endAdornment}
            {<Icon>attach_file</Icon>}
        </>
    );
    const inputProps = {
        readOnly,
        ...componentProps?.slotProps?.input,
        startAdornment,
        endAdornment,
    };
    const htmlInputProps = {
        maxLength: field?.maxLength,
        ...componentProps?.slotProps?.htmlInput,
    };
    return (
        <>
            <input
                type="file"
                hidden
                accept={accept}
                onChange={handleOnChange}
                ref={fileInputRef}
            />
            <TextField
                name={name}
                label={!inline ? label : undefined}
                placeholder={componentProps?.placeholder ?? (inline ? label : undefined)}
                value={fileName ?? ''}
                required={required ?? field?.required}
                disabled={disabled}
                error={fieldError != null}
                title={title}
                onClick={(event) => {
                    if (readOnly || disabled) {
                        return;
                    }
                    event.stopPropagation;
                    event.preventDefault();
                    (event.target as any).blur();
                    fileInputRef.current?.click();
                }}
                fullWidth
                {...componentProps}
                helperText={helperText ?? componentProps.helperText}
                slotProps={{
                    input: inputProps,
                    htmlInput: htmlInputProps,
                }}
                sx={{
                    '& input': !readOnly && !disabled ? { cursor: 'pointer' } : undefined,
                }}
            />
        </>
    );
};

export default FormFieldFile;
