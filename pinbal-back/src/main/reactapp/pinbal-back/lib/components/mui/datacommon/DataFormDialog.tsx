import React from 'react';
import { FormI18nKeys } from '../../form/Form';
import { useBaseAppContext, DialogButton } from '../../BaseAppContext';
import { useFormDialog, FormDialogSubmitFn } from '../form/FormDialog';

export type DataFormDialogApi = {
    show: (
        id?: any,
        additionalData?: any,
        title?: string,
        dialogButtons?: DialogButton[]
    ) => Promise<string>;
    close: () => void;
};

export type DataFormDialogProps = React.PropsWithChildren & {
    resourceName: string;
    title?: string;
    resourceTitle?: string;
    dialogButtons?: DialogButton[];
    dialogComponentProps?: any;
    formComponentProps?: any;
    formI18nKeys?: FormI18nKeys;
    apiRef?: React.RefObject<DataFormDialogApi | null>;
    formSubmit?: FormDialogSubmitFn;
    onClose?: (reason?: string) => boolean;
};

/**
 * Hook per a accedir a l'API de MuiDataFormDialog des de fora del context del component.
 *
 * @returns referència a l'API del component MuiDataFormDialog.
 */
export const useDataFormDialogApiRef: () => React.RefObject<DataFormDialogApi | null> = () => {
    const gridApiRef = React.useRef<DataFormDialogApi>(null);
    return gridApiRef;
};

export const DataFormDialog: React.FC<DataFormDialogProps> = (props) => {
    const {
        resourceName,
        title: titleProp,
        resourceTitle,
        dialogButtons,
        dialogComponentProps,
        formComponentProps,
        formI18nKeys,
        apiRef,
        formSubmit,
        onClose,
        children,
    } = props;
    const { t } = useBaseAppContext();
    const [formDialogShow, formDialogComponent, formDialogClose] = useFormDialog(
        resourceName,
        undefined,
        undefined,
        dialogButtons,
        formSubmit,
        t('datacommon.error'),
        children,
        null,
        dialogComponentProps ?? { fullWidth: true, maxWidth: 'md' },
        formComponentProps,
        formI18nKeys,
        onClose
    );
    const show = (id?: any, additionalData?: any, title?: string, dialogButtons?: DialogButton[]) =>
        formDialogShow(id, {
            title:
                title ??
                titleProp ??
                (id != null ? t('datacommon.update.label') : t('datacommon.create.label')) +
                    ' ' +
                    (resourceTitle ?? resourceName),
            additionalData,
            dialogButtons,
        });
    const close = () => formDialogClose();
    if (apiRef) {
        apiRef.current = { show, close };
    }
    return <>{formDialogComponent}</>;
};

export default DataFormDialog;
