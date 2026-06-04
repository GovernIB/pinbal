import React from 'react';
import Box from '@mui/material/Box';
import CircularProgress from '@mui/material/CircularProgress';
import { DialogButton } from '../../BaseAppContext';
import { ResourceType } from '../../ResourceApiContext';
import { useFormDialogButtons } from '../../AppButtons';
import { FormI18nKeys } from '../../form/Form';
import { FormApi } from '../../form/FormContext';
import Dialog, { DialogProps } from '../Dialog';
import MuiForm from './MuiForm';

type FormDialogProps = DialogProps & {
    resourceName: string;
    resourceType?: ResourceType;
    resourceTypeCode?: string;
    id?: any;
    additionalData?: any;
    initOnChangeRequest?: boolean;
    apiRef?: React.RefObject<FormApi>;
    dialogComponentProps?: any;
    formComponentProps?: any;
    formI18nKeys?: FormI18nKeys;
    noForm?: boolean;
};

export type FormDialogSubmitFn = (id: any, data?: any) => Promise<React.ReactElement | undefined>;
export type FormDialogShowArgs = {
    title?: string;
    additionalData?: any;
    initOnChangeRequest?: boolean;
    formContent?: React.ReactNode;
    dialogButtons?: DialogButton[];
    dialogComponentProps?: any;
    formComponentProps?: any;
};
export type FormDialogShowFn = (id: any, args?: FormDialogShowArgs) => Promise<any>;
export type FormDialogCloseFn = () => void;
export type UseFormDialogFn = (
    resourceName: string,
    resourceType?: ResourceType,
    resourceTypeCode?: string,
    dialogButtons?: DialogButton[],
    customSubmit?: FormDialogSubmitFn,
    customSubmitErrorMessage?: string,
    defaultFormContent?: React.ReactNode,
    loadingComponent?: React.ReactNode,
    defaultDialogComponentProps?: any,
    defaultFormComponentProps?: any,
    formI18nKeys?: FormI18nKeys,
    closeFn?: (reason?: string) => boolean,
    closeIcon?: boolean,
    autoSubmit?: boolean
) => [FormDialogShowFn, React.ReactElement, FormDialogCloseFn];

const FormDialogLoading: React.FC = () => {
    return (
        <Box sx={{ display: 'flex', justifyContent: 'center' }}>
            <CircularProgress size={50} sx={{ my: 4 }} />
        </Box>
    );
};

export const useFormDialog: UseFormDialogFn = (
    resourceName: string,
    resourceType?: ResourceType,
    resourceTypeCode?: string,
    dialogButtons?: DialogButton[],
    customSubmit?: FormDialogSubmitFn,
    customSubmitErrorMessage?: string,
    defaultFormContent?: React.ReactNode,
    loadingComponent?: React.ReactNode,
    defaultDialogComponentProps?: any,
    defaultFormComponentProps?: any,
    formI18nKeys?: FormI18nKeys,
    closeFn?: (reason?: string) => boolean,
    closeIcon?: boolean,
    autoSubmit?: boolean
) => {
    const formApiRef = React.useRef<FormApi | any>({});
    const formDialogButtons = useFormDialogButtons();
    const [open, setOpen] = React.useState<boolean>(false);
    const [title, setTitle] = React.useState<string | null>();
    const [id, setId] = React.useState<any>();
    const [additionalData, setAdditionalData] = React.useState<any>();
    const [dialogComponentProps, setDialogComponentProps] = React.useState<any>(
        defaultDialogComponentProps
    );
    const { initOnChangeRequest: initOnChangeRequestProp, ...otherFormComponentProps } =
        defaultFormComponentProps ?? {};
    const [initOnChangeRequest, setInitOnChangeRequest] =
        React.useState<boolean>(initOnChangeRequestProp);
    const [formComponentProps, setFormComponentProps] =
        React.useState<any>(otherFormComponentProps);
    const [resolveFn, setResolveFn] = React.useState<(value?: any) => void>();
    const [rejectFn, setRejectFn] = React.useState<(value: any) => void>();
    const [formContent, setFormContent] = React.useState<React.ReactNode | undefined>(
        defaultFormContent
    );
    const [submitReturnedContent, setSubmitReturnedContent] = React.useState<
        React.ReactNode | undefined
    >();
    const [buttons, setButtons] = React.useState<DialogButton[]>(
        dialogButtons ?? formDialogButtons
    );
    const [loading, setLoading] = React.useState<boolean>();
    const buttonCallback = (value: any) => {
        if (value) {
            const isCustomSubmit = customSubmit != null;
            const result = isCustomSubmit
                ? customSubmit(formApiRef.current.getId(), formApiRef.current.getData())
                : formApiRef.current.save();
            setLoading(true);
            result
                .then((value: any) => {
                    if (isCustomSubmit) {
                        // S'ha fet click al botó executar/generar i s'ha executat/generat correctament
                        if (value != null) {
                            // Si el mètode submit ha retornat alguna cosa es mostra com a contingut del diàleg
                            setSubmitReturnedContent(value);
                        } else {
                            // Si el mètode submit no retorna res es tanca el diàleg
                            setOpen(false);
                            resolveFn?.(value);
                        }
                    } else {
                        // S'ha fet click al botó desar i s'ha desat correctament
                        setOpen(false);
                        resolveFn?.(value);
                    }
                })
                .catch((error: any) => {
                    // S'ha fet click al botó desar i s'han produit errors
                    if (isCustomSubmit) {
                        formApiRef.current.handleSubmissionErrors(error, customSubmitErrorMessage);
                    }
                })
                .finally(() => setLoading(false));
        } else {
            // S'ha fet clic al botó de cancel·lar
            rejectFn?.(undefined);
            setOpen(false);
        }
    };
    const closeCallback = (reason: string) => {
        // S'ha tancat la modal amb la 'x' o s'ha fet click a fora de la finestra.
        if (!loading) {
            // Només es pot tancar la modal si no s'està en estat loading.
            const doClose = closeFn != null ? closeFn(reason) : true;
            if (doClose) {
                rejectFn?.(undefined);
                setOpen(false);
            }
        }
    };
    const show = (id: any, args?: FormDialogShowArgs) => {
        setId(id);
        setTitle(args?.title);
        setFormContent(args?.formContent ?? defaultFormContent);
        setAdditionalData(args?.additionalData ?? null);
        setInitOnChangeRequest(args?.initOnChangeRequest ?? initOnChangeRequestProp);
        setButtons(args?.dialogButtons ?? dialogButtons ?? formDialogButtons);
        setDialogComponentProps(
            args?.dialogComponentProps != null
                ? { ...defaultDialogComponentProps, ...args?.dialogComponentProps }
                : defaultDialogComponentProps
        );
        setFormComponentProps(
            args?.formComponentProps != null
                ? { ...otherFormComponentProps, ...args?.formComponentProps }
                : otherFormComponentProps
        );
        setOpen(true);
        setSubmitReturnedContent(undefined);
        setLoading(undefined);
        return new Promise<any>((resolve, reject) => {
            setResolveFn(() => resolve);
            setRejectFn(() => reject);
        });
    };
    // Deshabilita els botons si s'està en estat loading
    const processedButtons = loading
        ? buttons.map((b) => ({
              ...b,
              componentProps: {
                  ...b.componentProps,
                  disabled: true,
              },
          }))
        : buttons;
    const close = () => setOpen(false);
    const dialogComponent = (
        <FormDialog
            resourceName={resourceName}
            resourceType={resourceType}
            resourceTypeCode={resourceTypeCode}
            id={id}
            additionalData={additionalData}
            initOnChangeRequest={initOnChangeRequest}
            apiRef={formApiRef}
            open={open}
            buttonCallback={buttonCallback}
            closeCallback={closeCallback}
            title={title}
            buttons={processedButtons}
            dialogComponentProps={dialogComponentProps}
            formComponentProps={{
                ...formComponentProps,
                ...(autoSubmit && { onReady: () => buttonCallback(true) }),
            }}
            formI18nKeys={formI18nKeys}
            noForm={submitReturnedContent != null}
            closeIcon={closeIcon}>
            {loading
                ? (loadingComponent ?? <FormDialogLoading />)
                : (submitReturnedContent ?? formContent)}
        </FormDialog>
    );
    return [show, dialogComponent, close];
};

export const FormDialog: React.FC<FormDialogProps> = (props) => {
    const {
        resourceName,
        resourceType,
        resourceTypeCode,
        id,
        additionalData,
        initOnChangeRequest,
        apiRef,
        dialogComponentProps,
        formComponentProps,
        formI18nKeys,
        noForm,
        children,
        ...otherProps
    } = props;
    return (
        <Dialog componentProps={dialogComponentProps} {...otherProps}>
            {noForm ? (
                children
            ) : (
                <MuiForm
                    {...formComponentProps}
                    key={formComponentProps?.key}
                    resourceName={resourceName}
                    resourceType={resourceType}
                    resourceTypeCode={resourceTypeCode}
                    id={id}
                    additionalData={additionalData}
                    initOnChangeRequest={initOnChangeRequest}
                    i18nKeys={formI18nKeys}
                    apiRef={apiRef}
                    hiddenToolbar>
                    {children}
                </MuiForm>
            )}
        </Dialog>
    );
};

export default FormDialog;
