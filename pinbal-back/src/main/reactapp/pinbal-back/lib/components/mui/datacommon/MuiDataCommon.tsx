import React from 'react';
import { useResourceApiService, ResourceApiFindCommonArgs } from '../../ResourceApiProvider';
import { ResourceType, ExportFileType } from '../../ResourceApiContext';
import { FormI18nKeys } from '../../form/Form';
import { useBaseAppContext, DialogButton } from '../../BaseAppContext';
import { useConfirmDialogButtons, useCloseDialogButtons } from '../../AppButtons';
import { toToolbarIcon } from '../ToolbarIcon';
import DataFormDialog, { useDataFormDialogApiRef } from './DataFormDialog';

export type DataCommonFindArgs = ResourceApiFindCommonArgs;

export type DataCommonAdditionalAction = {
    /** Text de l'acció */
    label?: string | ((row: any) => string);
    /** Tooltip de l'acció */
    title?: string | ((row: any) => string);
    /** Icona de l'acció */
    icon?: string | ((row: any) => string);
    /** Indica si l'acció s'ha de mostrar a dins el menú dels tres puntets */
    showInMenu?: boolean | ((row: any) => boolean);
    /** Indica si l'acció s'ha de mostrar deshabilitada */
    disabled?: boolean | ((row: any) => boolean);
    /** Indica si l'acció s'ha d'ocultar */
    hidden?: boolean | ((row: any) => boolean);
    /** Enllaç a on s'ha d'anar al fer clic sobre l'acció */
    linkTo?: ((row: any) => string) | string;
    /** Estat que s'ha de passar al router quan es carregui l'enllaç indicat a linkTo */
    linkState?: ((row: any) => any) | any;
    /** Especifica la finestra o pipella a on es carrega l'enllaç indicat a linkTo */
    linkTarget?: ((row: any) => string) | string;
    /** Indica el link HAL de la fila que s'ha d'utilitzar per a mostrar o executar aquesta acció */
    rowLink?: string;
    /** Indica que aquesta acció ha d'executar una acció del recurs */
    action?: string;
    /** Indica que aquesta acció ha de generar un informe del recurs */
    report?: string;
    /** Indica que al fer clic sobre aquesta acció s'ha d'obrir un diàleg de creació de recurs */
    clickShowCreateDialog?: boolean;
    /** Indica que al fer clic sobre aquesta acció s'ha d'obrir un diàleg de modificació de recurs */
    clickShowUpdateDialog?: boolean;
    /** Indica que al fer clic sobre aquesta acció s'ha d'eliminar el recurs */
    clickTriggerDelete?: boolean;
    /** Handler per a l'event onClick de l'acció */
    onClick?: (id: any, row: any, event: React.MouseEvent) => void;
};

export type DataCommonExportFn = (
    fields?: string[],
    fileType?: ExportFileType,
    forceUnpaged?: boolean
) => void;
export type DataCommonTriggerCreateFn = (row?: any) => void;
export type DataCommonTriggerUpdateFn = (id: any, row?: any) => void;
export type DataCommonTriggerDeleteFn = (id: any) => void;

export const useApiDataCommon = (
    resourceName: string,
    resourceType?: ResourceType,
    resourceTypeCode?: string,
    resourceFieldName?: string,
    loadingProp?: boolean,
    autoFindDisabled?: boolean,
    findArgs?: DataCommonFindArgs,
    getArtifacts?: boolean
) => {
    const { saveAs } = useBaseAppContext();
    const {
        isReady: apiIsReady,
        currentFields: apiCurrentFields,
        find: apiFind,
        export: apiExport,
        artifacts: apiArtifacts,
        artifactFieldOptionsFields: apiArtifactFieldOptionsFields,
        artifactFieldOptionsFind: apiArtifactFieldOptionsFind,
        fieldOptionsFields: apiFieldOptionsFields,
        fieldOptionsFind: apiFieldOptionsFind,
    } = useResourceApiService(resourceName);
    const [firstRefresh, setFirstRefresh] = React.useState<boolean>(true);
    const [loading, setLoading] = React.useState<boolean>(loadingProp || !autoFindDisabled);
    const [fields, setFields] = React.useState<any[]>([]);
    const [rows, setRows] = React.useState<any[]>([]);
    const [pageInfo, setPageInfo] = React.useState<any>();
    const [artifacts, setArtifacts] = React.useState<any[]>();
    const [error, setError] = React.useState<any>();
    // Ref usada per a evitar problemes amb peticions de refresh concurrents.
    // La idea és descartar totes les respostes que no pertanyin a la darrera petició enviada.
    const lastRefreshTimestamp = React.useRef<number | null>(null);
    const refresh = () => {
        const refreshTimestamp = Date.now();
        lastRefreshTimestamp.current = refreshTimestamp;
        if (apiIsReady && findArgs != null) {
            const processedFindArgs = {
                ...findArgs,
                includeLinksInRows: true,
            };
            setLoading(true);
            setError(null);
            if (resourceFieldName == null) {
                apiFind(processedFindArgs)
                    .then((response) => {
                        // Si el valor guardar a lastRefreshTimestamp no és el mateix que el timestamp d'aquesta execució,
                        // no s'ha de processar la resposta, ja que hi ha una nova petició refresh en curs.
                        if (lastRefreshTimestamp.current !== refreshTimestamp) return;
                        setRows(response.rows);
                        setPageInfo(response.page);
                    })
                    .catch(setError)
                    .finally(() => setLoading(false));
            } else if (resourceType == null) {
                apiFieldOptionsFind({
                    fieldName: resourceFieldName,
                    ...processedFindArgs,
                })
                    .then((response) => {
                        // Si el valor guardar a lastRefreshTimestamp no és el mateix que el timestamp d'aquesta execució,
                        // no s'ha de processar la resposta, ja que hi ha una nova petició refresh en curs.
                        if (lastRefreshTimestamp.current !== refreshTimestamp) return;
                        setRows(response.rows);
                        setPageInfo(response.page);
                    })
                    .catch(setError)
                    .finally(() => setLoading(false));
            } else {
                const args = {
                    type: resourceType,
                    code: resourceTypeCode ?? '',
                    fieldName: resourceFieldName,
                    ...processedFindArgs,
                };
                apiArtifactFieldOptionsFind(args)
                    .then((response) => {
                        // Si el valor guardar a lastRefreshTimestamp no és el mateix que el timestamp d'aquesta execució,
                        // no s'ha de processar la resposta, ja que hi ha una nova petició refresh en curs.
                        if (lastRefreshTimestamp.current !== refreshTimestamp) return;
                        setRows(response.rows);
                        setPageInfo(response.page);
                    })
                    .catch(setError)
                    .finally(() => setLoading(false));
            }
        }
    };
    const exportt: DataCommonExportFn = (
        fields?: string[],
        fileType?: ExportFileType,
        forceUnpaged?: boolean
    ) => {
        const args = {
            ...(findArgs ?? {}),
            fields,
            fileType,
        };
        apiExport(forceUnpaged ? { ...args, unpaged: true } : args).then((response) => {
            if (saveAs) {
                saveAs(response.blob, response.fileName);
            } else {
                console.error(
                    "Couldn't save export file " +
                        response.fileName +
                        ': saveAs not available in BaseAppContext'
                );
            }
        });
    };
    React.useEffect(() => {
        if (apiIsReady) {
            const findDisabled = autoFindDisabled && firstRefresh;
            setFirstRefresh(false);
            if (resourceFieldName == null) {
                setFields(apiCurrentFields ?? []);
            } else if (resourceType == null) {
                apiFieldOptionsFields({ fieldName: resourceFieldName }).then((fields) => {
                    setFields(fields);
                });
            } else {
                const args = {
                    type: resourceType,
                    code: resourceTypeCode ?? '',
                    fieldName: resourceFieldName,
                };
                setError(null);
                apiArtifactFieldOptionsFields(args)
                    .then((fields) => {
                        setFields(fields);
                    })
                    .catch(setError);
            }
            !findDisabled && refresh();
        }
    }, [apiIsReady, autoFindDisabled, findArgs]);
    React.useEffect(() => {
        if (getArtifacts) {
            if (apiIsReady) {
                setError(null);
                apiArtifacts({})
                    .then((artifacts) => {
                        setArtifacts(artifacts);
                    })
                    .catch(setError);
            }
        } else {
            artifacts != null && setArtifacts(undefined);
        }
    }, [apiIsReady, getArtifacts]);
    return {
        loading,
        fields,
        rows,
        pageInfo,
        artifacts,
        error,
        refresh,
        export: exportt,
    };
};

export const useDataCommonEditable = (
    resourceName: string,
    readOnly: boolean,
    formAdditionalData: ((row?: any) => any) | any,
    toolbarCreateLink: string | undefined,
    toolbarDisableCreateLink: boolean | (() => boolean) | undefined,
    inlineCreate: ((row?: any) => void) | undefined,
    inlineUpdate: ((id: any, row?: any, additionalData?: any) => void) | undefined,
    rowDetailLink: string | undefined,
    rowUpdateLink: string | undefined,
    rowDisableUpdateButton: boolean | ((row: any) => boolean) | undefined,
    rowDisableDeleteButton: boolean | ((row: any) => boolean) | undefined,
    rowDisableDetailsButton: boolean | ((row: any) => boolean) | undefined,
    rowHideUpdateButton: boolean | ((row: any) => boolean) | undefined,
    rowHideDeleteButton: boolean | ((row: any) => boolean) | undefined,
    rowHideDetailsButton: boolean | ((row: any) => boolean) | undefined,
    inlineEditActive: boolean | undefined,
    inlineCreateEditActive: boolean | undefined,
    inlineUpdateEditActive: boolean | undefined,
    popupEditActive: boolean | undefined,
    popupEditCreateActive: boolean | undefined,
    popupEditUpdateActive: boolean | undefined,
    popupEditFormContent: React.ReactElement | undefined,
    popupEditFormDialogTitle: string | undefined,
    popupEditFormDialogResourceTitle: string | undefined,
    popupEditFormDialogButtons: DialogButton[] | undefined,
    popupEditFormDialogComponentProps: any,
    popupEditFormDialogOnClose: ((reason?: string) => boolean) | undefined,
    popupEditFormComponentProps: any,
    popupEditFormI18nKeys: FormI18nKeys | undefined,
    apiCurrentActions: any,
    apiDelete: (id: any) => Promise<any>,
    apiBulkDelete: (id: any[]) => Promise<any>,
    refresh: () => void,
    onCreate: ((row: any) => void) | undefined,
    onUpdate: ((row: any) => void) | undefined,
    onDelete: ((id: any | any[]) => void) | undefined
) => {
    const { t, temporalMessageShow, messageDialogShow } = useBaseAppContext();
    const dataFormDialogApiRef = useDataFormDialogApiRef();
    const confirmDialogButtons = useConfirmDialogButtons();
    const closeDialogButtons = useCloseDialogButtons();
    const confirmDialogComponentProps = { maxWidth: 'sm', fullWidth: true };
    const isInlineEditCreate = inlineEditActive || inlineCreateEditActive;
    const isInlineEditUpdate = inlineEditActive || inlineUpdateEditActive;
    const isPopupEditCreate = popupEditActive || popupEditCreateActive;
    const isPopupEditUpdate = popupEditActive || popupEditUpdateActive;
    const triggerCreate: DataCommonTriggerCreateFn = (row?: any, additionalData?: any) => {
        if (!isInlineEditCreate) {
            const processedAdditionalData = {
                ...(typeof formAdditionalData === 'function'
                    ? formAdditionalData(row, 'create')
                    : formAdditionalData),
                ...additionalData,
            };
            dataFormDialogApiRef.current
                ?.show(undefined, processedAdditionalData)
                .then((data) => {
                    onCreate?.(data);
                    refresh?.();
                })
                .catch(() => {
                    // Feim un catch buit perquè no aparegui a la consola el missatge: Uncaught (in promise)
                });
        } else {
            inlineCreate?.();
        }
    };
    const triggerUpdate: DataCommonTriggerUpdateFn = (id: any, row?: any, additionalData?: any) => {
        if (!inlineUpdate) {
            const processedAdditionalData = {
                ...(typeof formAdditionalData === 'function'
                    ? formAdditionalData(row, 'update')
                    : formAdditionalData),
                ...additionalData,
            };
            const hasUpdateAction = row?._actions['update'] != null;
            const noUpdateLinkTitle = !hasUpdateAction ? t('datacommon.details.label') : undefined;
            const noUpdateDialogButtons = !hasUpdateAction ? closeDialogButtons : undefined;
            dataFormDialogApiRef.current
                ?.show(id, processedAdditionalData, noUpdateLinkTitle, noUpdateDialogButtons)
                .then((data) => {
                    onUpdate?.(data);
                    refresh?.();
                })
                .catch(() => {
                    // Feim un catch buit perquè no aparegui a la consola el missatge: Uncaught (in promise)
                });
        } else {
            inlineUpdate(id, row);
        }
    };
    const triggerDelete = (id: any) => {
        const multiple = Array.isArray(id) && id.length > 1;
        if (!multiple) {
            const singleId = Array.isArray(id) ? id[0] : id;
            messageDialogShow(
                t('datacommon.delete.single.label'),
                t('datacommon.delete.single.confirm'),
                confirmDialogButtons,
                confirmDialogComponentProps
            )
                .then((value: any) => {
                    if (value) {
                        apiDelete(singleId)
                            .then(() => {
                                onDelete?.(singleId);
                                refresh?.();
                                temporalMessageShow(
                                    null,
                                    t('datacommon.delete.single.success'),
                                    'success'
                                );
                            })
                            .catch((error) => {
                                temporalMessageShow(
                                    t('datacommon.delete.single.error'),
                                    error.description ?? error.message,
                                    'error'
                                );
                            });
                    }
                })
                .catch(() => {
                    // Feim un catch buit perquè no aparegui a la consola el missatge: Uncaught (in promise)
                });
        } else {
            messageDialogShow(
                t('datacommon.delete.multiple.label'),
                t('datacommon.delete.multiple.confirm', { count: id.length }),
                confirmDialogButtons,
                confirmDialogComponentProps
            )
                .then((value: any) => {
                    if (value) {
                        apiBulkDelete(id)
                            .then((response) => {
                                if (response.errorCount === 0) {
                                    const deletedIds = response.items
                                        ?.filter((i: any) => !i.error)
                                        .map((i: any) => i.id);
                                    onDelete?.(deletedIds);
                                    refresh?.();
                                    temporalMessageShow(
                                        null,
                                        t('datacommon.delete.multiple.success', {
                                            count: response.successCount,
                                        }),
                                        'success'
                                    );
                                } else {
                                    temporalMessageShow(
                                        null,
                                        t('datacommon.delete.multiple.error', {
                                            count: response.errorCount,
                                        }),
                                        'warning'
                                    );
                                }
                            })
                            .catch((error) => {
                                temporalMessageShow(
                                    t('datacommon.delete.multiple.error', { count: id.length }),
                                    error.description ?? error.message,
                                    'error'
                                );
                            });
                    }
                })
                .catch(() => {
                    // Feim un catch buit perquè no aparegui a la consola el missatge: Uncaught (in promise)
                });
        }
    };
    const isCreateLinkPresent = apiCurrentActions?.['create'] != null;
    const createLinkConfigError =
        !readOnly && !isPopupEditCreate && !isInlineEditCreate && toolbarCreateLink == null;
    const toolbarDisableCreateLinkValue =
        typeof toolbarDisableCreateLink === 'function'
            ? toolbarDisableCreateLink()
            : toolbarDisableCreateLink;
    const toolbarAddElement =
        isCreateLinkPresent && !readOnly
            ? toToolbarIcon('add', {
                  title: t('datacommon.create.label'),
                  linkTo: toolbarCreateLink,
                  linkState: formAdditionalData
                      ? { additionalData: formAdditionalData }
                      : undefined,
                  onClick: !toolbarCreateLink ? triggerCreate : undefined,
                  disabled: toolbarDisableCreateLinkValue || createLinkConfigError,
              })
            : undefined;
    const rowEditActions: DataCommonAdditionalAction[] = [];
    const updateLinkConfigError =
        !readOnly && !isPopupEditUpdate && !isInlineEditUpdate && rowUpdateLink == null;
    !readOnly &&
        rowEditActions.push({
            label: t('datacommon.update.label'),
            rowLink: 'update',
            icon: 'edit',
            linkTo: rowUpdateLink,
            linkState:
                rowUpdateLink != null && formAdditionalData != null
                    ? { additionalData: formAdditionalData }
                    : undefined,
            disabled: rowDisableUpdateButton || updateLinkConfigError,
            hidden: rowHideUpdateButton,
            clickShowUpdateDialog: rowUpdateLink == null,
        });
    !readOnly &&
        rowEditActions.push({
            label: t('datacommon.delete.label'),
            icon: 'delete',
            disabled: rowDisableDeleteButton,
            hidden: rowHideDeleteButton,
            showInMenu: true,
            rowLink: 'delete',
            clickTriggerDelete: true,
        });
    isPopupEditUpdate &&
        !rowDetailLink &&
        rowEditActions.push({
            label: t('datacommon.details.label'),
            rowLink: '!update',
            icon: 'info',
            linkTo: rowUpdateLink,
            linkState:
                rowUpdateLink != null && formAdditionalData != null
                    ? { additionalData: formAdditionalData }
                    : undefined,
            disabled: rowDisableUpdateButton || updateLinkConfigError,
            hidden: rowHideUpdateButton,
            clickShowUpdateDialog: rowUpdateLink == null,
        });
    rowDetailLink &&
        rowEditActions.push({
            label: t('datacommon.details.label'),
            icon: 'info',
            linkTo: rowDetailLink,
            disabled: rowDisableDetailsButton,
            hidden: rowHideDetailsButton,
            rowLink: readOnly ? undefined : '!update',
        });
    React.useEffect(() => {
        if (createLinkConfigError || updateLinkConfigError) {
            createLinkConfigError &&
                console.warn(
                    'Create link not configured in data component for resource:',
                    resourceName
                );
            updateLinkConfigError &&
                console.warn(
                    'Update link not configured in data component for resource:',
                    resourceName
                );
            console.warn('\t(to avoid these messages configure data component as read only)');
        }
    }, [createLinkConfigError, updateLinkConfigError]);
    const formDialogComponent =
        !readOnly && (isPopupEditCreate || isPopupEditUpdate) ? (
            <DataFormDialog
                resourceName={resourceName}
                title={popupEditFormDialogTitle}
                resourceTitle={popupEditFormDialogResourceTitle}
                dialogButtons={popupEditFormDialogButtons}
                dialogComponentProps={popupEditFormDialogComponentProps}
                formComponentProps={popupEditFormComponentProps}
                formI18nKeys={popupEditFormI18nKeys}
                onClose={popupEditFormDialogOnClose}
                apiRef={dataFormDialogApiRef}>
                {popupEditFormContent}
            </DataFormDialog>
        ) : null;
    return {
        toolbarAddElement,
        rowEditActions,
        formDialogComponent,
        triggerCreate,
        triggerUpdate,
        triggerDelete,
    };
};
