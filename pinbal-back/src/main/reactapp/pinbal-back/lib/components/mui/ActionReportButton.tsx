import React from 'react';
import Button from '@mui/material/Button';
import IconButton from '@mui/material/IconButton';
import Icon from '@mui/material/Icon';
import Badge from '@mui/material/Badge';
import { FormI18nKeys } from '../form/Form';
import {
    useActionDialogButtons,
    useReportDialogButtons,
    useConfirmDialogButtons,
} from '../AppButtons';
import { useBaseAppContext, DialogButton } from '../BaseAppContext';
import { ExportFileType } from '../ResourceApiContext';
import { useResourceApiService } from '../ResourceApiProvider';
import { useFormDialog, FormDialogSubmitFn, FormDialogCloseFn } from './form/FormDialog';

export type ActionReportCustomButtonProps = {
    disabled?: boolean;
    onClick?: () => void;
    title?: string;
    [x: string | number | symbol]: unknown;
};

type IconCustomButtonProps = ActionReportCustomButtonProps & React.PropsWithChildren;
type TextCustomButtonProps = ActionReportCustomButtonProps &
    React.PropsWithChildren & { icon?: string };

/**
 * Propietats del component ActionReportButton.
 */
export type ActionReportButtonProps = {
    /** Nom del recurs de l'API REST d'on es consultarà la informació per a mostrar el botó */
    resourceName: string;
    /** Codi de l'acció (si s'indica s'enten que aquest botó pertany a un artefacte de tipus acció) */
    action?: string;
    /** Codi de l'informe (si s'indica s'enten que aquest botó pertany a un artefacte de tipus informe) */
    report?: string;
    /** Tipus de fitxer que es generarà amb l'informe (només te sentit per a artefactes de tipus informe) */
    reportFileType?: ExportFileType;
    /** In de del recurs per l'artefacte (si no s'especifica s'executarà l'artefacte sobre el tipus de recurs indicat amb resourceName) */
    id?: any;
    /** Icona pel botó (si no s'especifica es mostrarà un botó de text) */
    icon?: any;
    /** Icona per a mostrar a dins el botó */
    buttonIcon?: any;
    /** Títol pel botó */
    title?: string;
    /** Indica que l'execució de l'artefacte requereix confirmació de l'usuari (només per a artefactes de tipus acció) */
    confirm?: boolean;
    /** Missatge de confirmació personalitzat */
    confirmMessage?: string | React.ReactElement;
    /** Indica que el botó apareix deshabilitat */
    disabled?: true;
    /** Indica que el botó apareix amb una insignia amb aquest nombre al seu interior */
    selectedCount?: number;
    /** Component de botó alternatiu */
    buttonComponent?: React.FC<ActionReportCustomButtonProps>;
    /** Dades addicionals pel formulari de l'artefacte */
    formAdditionalData?: any;
    /** Claus de traducció personalitzades pel component Form */
    formI18nKeys?: FormI18nKeys;
    /** Indica que el formulari ha de fer una petició onChange inicial */
    formInitOnChangeRequest?: true;
    /** Títol del diàleg amb el formulari */
    formDialogTitle?: string;
    /** Component amb el contingut (camps) del formulari */
    formDialogContent?: React.ReactElement;
    /** Component que mostra que l'acció/informe s'està executant */
    formDialogLoading?: React.ReactElement;
    /** Indica si s'ha de mostrar la icona de tancar al component de diàleg */
    formDialogCloseIcon?: boolean;
    /** Botons pel component de diàleg */
    formDialogButtons?: DialogButton[];
    /** Propietats pel component de diàleg */
    formDialogComponentProps?: any;
    /** Funció que processa els resultats d'executar l'artefacte i retorna un element per a mostrar al diàleg com a resultat (només per a artefactes de tipus acció) */
    formDialogResultProcessor?: (result?: any) => React.ReactElement | undefined;
    /** Event que es llença quan l'execució de l'artefacte finalitza sense errors */
    onSuccess?: (result?: any) => void;
    /** Event que es llença quan l'execució de l'artefacte finalitza amb errors */
    onError?: (error?: any) => void;
    /** Event que es llença quan es tanca la modal del formulari de l'artefacte */
    onClose?: () => void;
    /** Propietats pel component del botó */
    buttonComponentProps?: any;
    /** Propietats pel component de l'icona (només per a botons de tipus icona) */
    iconComponentProps?: any;
    /** Indica si s'ha d'executar l'acció automàticament al obrir el diàleg */
    dialogAutoSubmit?: boolean;
};

export type ActionReportLogicExecFn = (
    id: any,
    dialogTitle?: any,
    formAdditionalData?: any,
    formDialogComponentProps?: any
) => void;

export type ActionReportLogicResult = {
    available: boolean;
    apiLink: any;
    formDialogComponent: React.ReactElement;
    exec: ActionReportLogicExecFn;
    close: FormDialogCloseFn;
};

const getActionReportLink = (action: string | undefined, report: string | undefined) => {
    if (action != null) {
        return 'exec_' + action;
    } else if (report != null) {
        return 'generate_' + report;
    }
};

const ButtonWithBadge: React.FC<any & React.PropsWithChildren> = (props) => {
    const { selectedCount, children } = props;
    return (
        <Badge badgeContent={selectedCount} color="secondary">
            {children}
        </Badge>
    );
};

const IconCustomButton: React.FC<IconCustomButtonProps> = (props) => {
    const { disabled, onClick, title, children, ...otherProps } = props;
    return (
        <IconButton disabled={disabled} onClick={onClick} title={title} {...otherProps}>
            {children}
        </IconButton>
    );
};

const TextCustomButton: React.FC<TextCustomButtonProps> = (props) => {
    const { disabled, onClick, title, icon, ...otherProps } = props;
    return (
        <Button
            disabled={disabled}
            onClick={onClick}
            startIcon={icon != null ? <Icon>{icon}</Icon> : undefined}
            {...otherProps}>
            {title}
        </Button>
    );
};

/**
 * Hook amb la lògica associada al component ActionReportButton.
 *
 * @param resourceName - Nom rel recurs de l'API REST.
 * @param action - Codi de l'acció.
 * @param report - Codi de l'informe.
 * @param reportFileType - Tipus d'arxiu generat per l'informe (només per als informes).
 * @param confirm - Indica si l'execució de l'acció requereix confirmació de l'usuari (només per a les accions).
 * @param confirmMessage - Missatge de confirmació personalitzat.
 * @param formAdditionalDataArg - Dades addicionals pel formulari.
 * @param formI18nKeys - Claus de traducció personalitzades pel component Form.
 * @param formInitOnChangeRequest - Indica si el formulari ha de fer una petició onChange inicial.
 * @param formDialogContent - Contingut (camps) pel formulari del diàleg.
 * @param formDialogLoading - Component que mostra que l'acció/informe s'està executant.
 * @param formDialogButtons - Botons pel component de diàleg.
 * @param formDialogComponentPropsArg - Propietats pel component del diàleg.
 * @param formDialogResultProcessor - Funció que processa els resultats d'executar l'artefacte i retorna un element per a mostrar al diàleg com a resultat (només per a artefactes de tipus acció).
 * @param onSuccess - Event que es llença quan l'execució de l'artefacte finalitza sense errors.
 * @param onError - Event que es llença quan l'execució de l'artefacte finalitza amb errors.
 * @param onClose - Event que es llença quan es tanca la modal del formulari de l'artefacte.
 * @param dialogCloseIcon - Indica si s'ha de mostrar la icona de tancar a la modal del formulari de l'artefacte.
 * @param dialogCloseCallback - Callback que es crida quan es tanca el diàleg.
 * @param dialogAutoSubmit - Indica si s'ha d'executar l'acció automàticament al obrir el diàleg.
 * @returns un objecte amb el resultat d'executar la lògica.
 */
export const useActionReportLogic = (
    resourceName: string,
    action?: string,
    report?: string,
    reportFileType?: ExportFileType,
    confirm?: boolean,
    confirmMessage?: string | React.ReactElement,
    formAdditionalDataArg?: any,
    formI18nKeys?: FormI18nKeys,
    formInitOnChangeRequest?: boolean,
    formDialogContent?: React.ReactElement,
    formDialogLoading?: React.ReactElement,
    formDialogButtons?: DialogButton[],
    formDialogComponentPropsArg?: any,
    formDialogResultProcessor?: (result?: any) => React.ReactElement | undefined,
    onSuccess?: (result?: any) => void,
    onError?: (error?: any) => void,
    onClose?: () => void,
    dialogCloseIcon?: boolean,
    dialogCloseCallback?: (reason?: string) => boolean,
    dialogAutoSubmit?: boolean
): ActionReportLogicResult => {
    const { t, messageDialogShow, temporalMessageShow, saveAs } = useBaseAppContext();
    const actionDialogButtons = useActionDialogButtons();
    const reportDialogButtons = useReportDialogButtons();
    const confirmDialogButtons = useConfirmDialogButtons();
    const {
        isReady: apiIsReady,
        artifacts: apiArtifacts,
        artifactAction: apiArtifactAction,
        artifactReport: apiArtifactReport,
    } = useResourceApiService(resourceName);
    const execAction: FormDialogSubmitFn = (id: any, data: any) =>
        new Promise((resolve, reject) => {
            if (action != null) {
                const requestArgs = {
                    id,
                    code: action,
                    data: { ...formAdditionalDataArg, ...data },
                };
                apiArtifactAction(id, requestArgs)
                    .then((result: any) => {
                        if (onSuccess) {
                            onSuccess(result);
                        } else {
                            temporalMessageShow(null, t('actionreport.action.success'), 'success');
                        }
                        resolve(formDialogResultProcessor?.(result));
                    })
                    .catch((error) => {
                        onError?.(error);
                        reject(error);
                    });
            } else {
                console.error("Couldn't exec action without code");
            }
        });
    const generateReport: FormDialogSubmitFn = (id: any, data: any) =>
        new Promise((resolve, reject) => {
            if (report != null) {
                const requestArgs = {
                    id,
                    code: report,
                    fileType: reportFileType,
                    data,
                };
                apiArtifactReport(id, requestArgs)
                    .then((result: any) => {
                        saveAs?.(result.blob, result.fileName);
                        if (onSuccess) {
                            onSuccess(result);
                        } else {
                            temporalMessageShow(null, t('actionreport.report.success'), 'success');
                        }
                        resolve(formDialogResultProcessor?.(result));
                    })
                    .catch((error) => {
                        onError?.(error);
                        reject(error);
                    });
            } else {
                console.error("Couldn't generate report without code");
            }
        });
    const [formDialogShow, formDialogComponent, formDialogClose] = useFormDialog(
        resourceName,
        action ? 'ACTION' : report ? 'REPORT' : undefined,
        action ? action : report ? report : undefined,
        formDialogButtons ??
            (action ? actionDialogButtons : report ? reportDialogButtons : undefined),
        action ? execAction : generateReport,
        action ? t('actionreport.action.error') : t('actionreport.report.error'),
        null,
        formDialogLoading,
        null,
        {
            resourceType: action ? 'action' : 'report',
            resourceTypeCode: action ?? report,
        },
        formI18nKeys,
        dialogCloseCallback,
        dialogCloseIcon ?? false,
        dialogAutoSubmit
    );
    const exec = (
        id: any,
        dialogTitle?: any,
        formAdditionalData?: any,
        formDialogComponentProps?: any
    ) => {
        if (hasForm) {
            const formDialogTitle =
                apiLink?.title ?? (action != null ? 'Exec ' + action : 'Generate ' + report);
            formDialogShow(id, {
                title: dialogTitle ?? formDialogTitle,
                additionalData: formAdditionalData,
                initOnChangeRequest: formInitOnChangeRequest,
                formContent: formDialogContent,
                dialogComponentProps: formDialogComponentProps ??
                    formDialogComponentPropsArg ?? {
                        fullWidth: true,
                        maxWidth: 'md',
                    },
            })
                .catch((_error) => {})
                .finally(() => onClose?.());
        } else if (action != null) {
            if (confirm) {
                const confirmDialogComponentProps = {
                    maxWidth: 'sm',
                    fullWidth: true,
                };
                messageDialogShow(
                    t('actionreport.action.confirm.title'),
                    confirmMessage ??
                        t('actionreport.action.confirm.message', {
                            action: apiLink?.title ?? action,
                        }),
                    confirmDialogButtons,
                    confirmDialogComponentProps
                ).then((value: any) => {
                    if (value) {
                        execAction(id);
                    }
                });
            } else {
                execAction(id);
            }
        } else if (report != null) {
            generateReport(id);
        }
    };
    const [artifact, setArtifact] = React.useState<any>();
    const [apiLink, setApiLink] = React.useState<any>();
    const hasForm = artifact != null && artifact.formClassActive;
    React.useEffect(() => {
        if (action == null && report == null) {
            console.error('[ActionReportButton] No action or report prop specified');
        }
    }, []);
    React.useEffect(() => {
        if (resourceName != null && (action != null || report != null) && apiIsReady) {
            apiArtifacts({ includeLinks: true }).then((artifacts) => {
                const artifactType = action != null ? 'ACTION' : 'REPORT';
                const artifactCode = action ?? report;
                const artifact = artifacts.find(
                    (a: any) => a.type === artifactType && a.code === artifactCode
                );
                if (artifact != null) {
                    setArtifact(artifact);
                    const actionReportLink = getActionReportLink(action, report);
                    actionReportLink != null &&
                        setApiLink((artifact as any)._links[actionReportLink]);
                } else {
                    console.warn(
                        "Couldn't find artifact (type=" +
                            artifactType +
                            ', code=' +
                            artifactCode +
                            ')'
                    );
                }
            });
        } else {
            setApiLink(undefined);
        }
    }, [apiIsReady]);
    return {
        available: artifact != null,
        apiLink,
        formDialogComponent,
        exec,
        close: formDialogClose,
    };
};

/**
 * Botó que activa un artefacte de tipus acció o informe.
 *
 * @param props - Propietats del component.
 * @returns Element JSX del botó.
 */
export const ActionReportButton: React.FC<ActionReportButtonProps> = (props) => {
    const {
        resourceName,
        action,
        report,
        reportFileType = 'PDF',
        id,
        icon,
        buttonIcon,
        title,
        confirm,
        confirmMessage,
        disabled,
        selectedCount,
        buttonComponent: buttonComponentProp,
        formAdditionalData,
        formI18nKeys,
        formInitOnChangeRequest,
        formDialogTitle,
        formDialogContent,
        formDialogLoading,
        formDialogCloseIcon,
        formDialogButtons,
        formDialogComponentProps,
        formDialogResultProcessor,
        onSuccess,
        onError,
        onClose,
        buttonComponentProps,
        iconComponentProps,
        dialogAutoSubmit,
    } = props;
    const {
        available,
        apiLink,
        formDialogComponent,
        exec: handleButtonClick,
    } = useActionReportLogic(
        resourceName,
        action,
        report,
        reportFileType,
        confirm,
        confirmMessage,
        formAdditionalData,
        formI18nKeys,
        formInitOnChangeRequest,
        formDialogContent,
        formDialogLoading,
        formDialogButtons,
        formDialogComponentProps,
        formDialogResultProcessor,
        onSuccess,
        onError,
        onClose,
        formDialogCloseIcon,
        undefined,
        dialogAutoSubmit
    );
    const buttonTitle = title ?? apiLink?.title ?? action ?? report;
    const ButtonComponent =
        buttonComponentProp ?? (icon != null ? IconCustomButton : TextCustomButton);
    const { onClick: onClickFromComponentProps, ...otherButtonComponentProps } =
        buttonComponentProps ?? {};
    const button = (
        <ButtonComponent
            disabled={disabled}
            onClick={() => {
                handleButtonClick(id, formDialogTitle);
                onClickFromComponentProps?.();
            }}
            title={buttonTitle}
            icon={icon == null ? buttonIcon : undefined}
            {...otherButtonComponentProps}>
            {icon != null && <Icon {...iconComponentProps}>{icon}</Icon>}
        </ButtonComponent>
    );
    return available ? (
        <>
            {selectedCount ? (
                <ButtonWithBadge selectedCount={selectedCount}>{button}</ButtonWithBadge>
            ) : (
                button
            )}
            {formDialogComponent}
        </>
    ) : null;
};

export default ActionReportButton;
