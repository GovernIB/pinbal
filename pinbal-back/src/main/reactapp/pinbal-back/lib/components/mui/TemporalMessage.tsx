import React from 'react';
import Snackbar from '@mui/material/Snackbar';
import Alert from '@mui/material/Alert';
import AlertTitle from '@mui/material/AlertTitle';
import { TemporalMessageSeverity, TemporalMessageShowFn } from '../BaseAppContext';

const TEMPORALMSG_DURATION_DEFAULT = 5000;
const TEMPORALMSG_DURATION_ERROR = 10000;

type TemporalMessageProps = {
    open: boolean;
    setOpen: (open: boolean) => void;
    title?: string;
    message?: string;
    severity?: TemporalMessageSeverity;
    additionalComponents?: React.ReactElement[];
};

export const useTemporalMessage: () => [TemporalMessageShowFn, React.ReactElement] = () => {
    const [open, setOpen] = React.useState<boolean>(false);
    const [title, setTitle] = React.useState<string>();
    const [message, setMessage] = React.useState<string>();
    const [severity, setSeverity] = React.useState<TemporalMessageSeverity>();
    const [additionalComponents, setAdditionalComponents] = React.useState<
        React.ReactElement[] | undefined
    >();
    const show: TemporalMessageShowFn = (
        title: string | null,
        message: string,
        severity?: TemporalMessageSeverity,
        additionalComponents?: React.ReactElement[]
    ) => {
        setTitle(title ?? undefined);
        setMessage(message);
        setSeverity(severity);
        setAdditionalComponents(additionalComponents);
        setOpen(true);
    };
    const component = (
        <TemporalMessage
            open={open}
            setOpen={setOpen}
            title={title}
            message={message}
            severity={severity}
            additionalComponents={additionalComponents}
        />
    );
    return [show, component];
};

export const TemporalMessage: React.FC<TemporalMessageProps> = (props) => {
    const { open, setOpen, title, message, severity, additionalComponents } = props;
    const autoHideDuration =
        severity === 'error' ? TEMPORALMSG_DURATION_ERROR : TEMPORALMSG_DURATION_DEFAULT;
    return (
        <Snackbar
            open={open}
            onClose={() => setOpen(false)}
            autoHideDuration={autoHideDuration}
            anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}>
            <Alert
                onClose={() => setOpen(false)}
                severity={severity ?? 'info'}
                sx={{ width: '100%' }}>
                {title && <AlertTitle>{title}</AlertTitle>}
                {message}
                {additionalComponents}
            </Alert>
        </Snackbar>
    );
};

export default TemporalMessage;
