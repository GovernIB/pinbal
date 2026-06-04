import React, { KeyboardEvent } from 'react';
import { useLocation } from 'react-router-dom';
import { useBaseAppContext } from '../BaseAppContext';
import {
    ResourceApiError,
    ResourceApiOnChangeArgs,
    useResourceApiService,
} from '../ResourceApiProvider';
import { ResourceType } from '../ResourceApiContext';
import { useConfirmDialogButtons } from '../AppButtons';
import { processApiFields } from '../../util/fields';
import { shallowEqual } from '../../util/equals';
import useLogConsole from '../../util/useLogConsole';
import { useReducerWithActionMiddleware } from '../../util/useReducerWithActionMiddleware';
import ResourceApiFormContext, {
    FormApi,
    FormApiRef,
    FormFieldError,
    FormFieldDataAction,
    FormFieldDataActionType,
    useFormContext,
    useOptionalFormContext,
} from './FormContext';
import FormBlocker from './FormBlocker';

const LOG_PREFIX = 'FORM';

/**
 * Propietats del component Form.
 */
export type FormProps = React.PropsWithChildren & {
    /** Títol del formulari */
    title?: string;
    /** Nom del recurs de l'API REST d'on es consultarà la informació per a mostrar el formulari */
    resourceName: string;
    /** Tipus de l'artefacte associat al recurs (emplenar només quan es vulgui mostrar el formulari associat a un artefacte del recurs) */
    resourceType?: ResourceType;
    /** Codi de l'artefacte associat al recurs (emplenar només quan es vulgui mostrar el formulari associat a un artefacte del recurs) */
    resourceTypeCode?: string;
    /** Id del recurs a modificar (si no s'especifica s'assumirà que és un formulari de creació) */
    id?: any;
    /** Referència a l'api del component */
    apiRef?: FormApiRef;
    /** Dades inicials pel formulari */
    initialData?: any;
    /** Dades addicionals que s'enviaran amb les dades del formulari */
    additionalData?: any;
    /** Perspectives que s'enviaran al consultar la informació del recurs */
    perspectives?: string[];
    /** Camps personalitzats per a inicialitzar el formulari (si s'especifica aquesta propietat no es consultaran els camps a l'API REST) */
    customFields?: any[];
    /** Indica si s'ha de fer una petició onChange sense cap camp associat quan es crea el component */
    initOnChangeRequest?: true;
    /** Indica si s'ha d'aturar l'enviament del formulari si hi ha errors del validador (que no siguin errors de validació de l'API REST) */
    avoidSubmitIfAnyValidatorErrors?: true;
    /** Indica si s'ha de desar el formulari quan es pitgi la tecla Intro en algun camp */
    saveOnFieldEnterKeyPressed?: true;
    /** Propietats comunes per a tots els components FormField de dins aquest component */
    commonFieldComponentProps?: any;
    /** Adreça que s'ha de mostrar una vegada creat un registre (Es substituirà el text '{{id}}' per l'id del recurs creat) */
    createLink?: string;
    /** Adreça que s'ha de mostrar una vegada modificat un registre (Es substituirà el text '{{id}}' per l'id del recurs modificat) */
    updateLink?: string;
    /** Adreça que s'ha de mostrar una vegada creat o modificat un registre (Es substituirà el text '{{id}}' per l'id del recurs creat) */
    saveLink?: string;
    /** Adreça que s'ha de mostrar al fer click al botó de retrocedir (només s'utilitzarà si l'historial està buit) */
    goBackLink?: string;
    /** Event que es llença quan s'han carregat les dades del formulari */
    onReady?: (data: any) => void;
    /** Event que es llença quan es modifica alguna dada del formulari */
    onDataChange?: (data: any, initial: boolean) => void;
    /** Event que es llença quan es fa un reset del formulari */
    onReset?: (data: any) => void;
    /** Event que es llença quan es crea un nou registre */
    onCreateSuccess?: (data: any) => void;
    /** Event que es llença quan es modifica un registre */
    onUpdateSuccess?: (data: any) => void;
    /** Event que es llença quan es desa un registre (creat o modificat) */
    onSaveSuccess?: (data: any) => void;
    /** Event que es llença just abans de crear un registre (creat o modificat). El resultat retornat és el que s'enviarà en la petició. */
    onBeforeCreateSuccess?: (data: any) => any;
    /** Event que es llença just abans de modificar un registre (creat o modificat). El resultat retornat és el que s'enviarà en la petició. */
    onBeforeUpdateSuccess?: (data: any) => any;
    /** Event que es llença just abans de desar un registre (creat o modificat). El resultat retornat és el que s'enviarà en la petició. */
    onBeforeSaveSuccess?: (data: any) => any;
    /** Event que es llença quan es produeixen errors de validació al enviar el formulari */
    onValidationErrorsChange?: (id: any, validationErrors?: FormFieldError[]) => void;
    /** Validador per a les dades del formulari. Es crida en cada canvi i retorna una llista d'errors (o null/undefined si tot es correcte) */
    dataValidator?: (data: any) => FormFieldError[] | undefined;
    /** Errors de validació */
    validationErrors?: FormFieldError[];
    /** Mapeig dels tipus de camp */
    fieldTypeMap?: Map<string, string>;
    /** Indica que és un formulari d'una sola línia (per exemple: formularis que es mostran a una fila de la graella) */
    inline?: true;
    /** Indica que el bloquejador de sortida de formulari quan s'han fet modificacions està deshabilitat */
    formBlockerDisabled?: true;
    /** Claus alternatives per a les traduccions */
    i18nKeys?: FormI18nKeys;
    /** Indica si s'han d'imprimir a la consola missatges de depuració */
    debug?: true;
};

export type FormI18nKeys = {
    createSuccess?: string;
    createError?: string;
    updateSuccess?: string;
    updateError?: string;
    deleteSuccess?: string;
    deleteError?: string;
};

const getApiSaveProcessedData = (
    id: any,
    data: any,
    onBeforeCreateSuccess: ((data: any) => any) | undefined,
    onBeforeUpdateSuccess: ((data: any) => any) | undefined,
    onBeforeSaveSuccess: ((data: any) => any) | undefined
) => {
    if (id == null && onBeforeCreateSuccess != null) {
        return onBeforeCreateSuccess(data);
    } else if (id != null && onBeforeUpdateSuccess != null) {
        return onBeforeUpdateSuccess(data);
    } else if (onBeforeSaveSuccess != null) {
        return onBeforeSaveSuccess(data);
    } else {
        return data;
    }
};

const formDataReducer = (state: any, action: FormFieldDataAction): any => {
    const { type, payload } = action;
    switch (type) {
        case FormFieldDataActionType.RESET: {
            return payload;
        }
        case FormFieldDataActionType.FIELD_CHANGE: {
            return {
                ...state,
                [payload.fieldName]: payload.value,
                ...payload.changes,
            };
        }
    }
};

const getInitialDataFromFields = (fields: any[] | undefined) => {
    const initialDataFromFields: any = {};
    fields?.forEach((f) => f.value && (initialDataFromFields[f.name] = f.value));
    return initialDataFromFields;
};

const useControlledId = (idProp: any) => {
    const wasControlled = React.useRef<boolean>(idProp !== undefined);
    const isIdControlled = idProp !== undefined;
    const [internalId, setInternalId] = React.useState<any>(idProp ?? null);
    React.useEffect(() => {
        if (wasControlled.current !== isIdControlled) {
            console.warn(
                'Form is changing from ' +
                    (wasControlled.current ? 'controlled' : 'uncontrolled') +
                    ' to ' +
                    (isIdControlled ? 'controlled' : 'uncontrolled') +
                    ' state.'
            );
        }
    }, [idProp]);
    return {
        id: isIdControlled ? idProp : internalId,
        setInternalId: (id: any) => {
            setInternalId(id);
            if (isIdControlled) {
                console.warn(
                    "You shouldn't set internalId in a controlled id Form (idProp=" + idProp + ')'
                );
            }
        },
    };
};

/**
 * Hook per a accedir a l'API de Form des de fora del context del component.
 *
 * @returns referència a l'API del component Form.
 */
export const useFormApiRef: () => FormApiRef = () => {
    const formApiRef = React.useRef<FormApi>(null);
    return formApiRef;
};

/**
 * Hook per a accedir a l'API de Form des de dins el context del component.
 *
 * @returns referència a l'API del component Form.
 */
export const useFormApiContext: () => FormApiRef = () => {
    const formContext = useFormContext();
    return formContext.apiRef;
};

/**
 * Component base de formulari independent de la llibreria de interfície d'usuari.
 *
 * @param props - Propietats del component.
 * @returns Element JSX del formulari.
 */
export const Form: React.FC<FormProps> = (props) => {
    const {
        resourceName,
        resourceType,
        resourceTypeCode,
        id: idProp,
        apiRef: apiRefProp,
        initialData: initialDataProp,
        additionalData: additionalDataProp,
        perspectives,
        customFields,
        initOnChangeRequest,
        avoidSubmitIfAnyValidatorErrors,
        saveOnFieldEnterKeyPressed,
        commonFieldComponentProps,
        createLink,
        updateLink,
        saveLink,
        goBackLink,
        onReady,
        onDataChange,
        onReset,
        onCreateSuccess,
        onUpdateSuccess,
        onSaveSuccess,
        onBeforeCreateSuccess,
        onBeforeUpdateSuccess,
        onBeforeSaveSuccess,
        onValidationErrorsChange,
        dataValidator,
        validationErrors,
        fieldTypeMap,
        inline,
        formBlockerDisabled,
        i18nKeys,
        debug = false,
        children,
    } = props;
    const logConsole = useLogConsole(LOG_PREFIX);
    const {
        goBack,
        navigate,
        useLocationPath,
        temporalMessageShow,
        messageDialogShow,
        contentExpandsToAvailableHeight,
        t,
    } = useBaseAppContext();
    const locationPath = useLocationPath();
    const divRef = React.useRef<HTMLDivElement>(null);
    const {
        isReady: apiIsReady,
        currentFields: apiCurrentFields,
        currentActions: apiCurrentActions,
        currentError: apiCurrentError,
        getOne: apiGetOne,
        onChange: apiOnChange,
        create: apiCreate,
        update: apiUpdate,
        delete: apiDelete,
        artifacts: apiArtifacts,
        artifactFormOnChange: apiArtifactFormOnChange,
        artifactFormValidate: apiArtifactFormValidate,
    } = useResourceApiService(resourceName);
    const confirmDialogButtons = useConfirmDialogButtons();
    const confirmDialogComponentProps = { maxWidth: 'sm', fullWidth: true };
    const [isLoading, setIsLoading] = React.useState<boolean>(true);
    const [modified, setModified] = React.useState<boolean>(false);
    const [externalModified, setExternalModified] = React.useState<boolean>(false);
    const [fields, setFields] = React.useState<any[]>();
    const [validatorFieldErrors, setValidatorFieldErrors] = React.useState<FormFieldError[]>();
    const [apiFieldErrors, setApiFieldErrors] = React.useState<FormFieldError[]>();
    const [revertData, setRevertData] = React.useState<any>(undefined);
    const [isDataInitialized, setIsDataInitialized] = React.useState<boolean>(false);
    const [apiActions, setApiActions] = React.useState<any>(undefined);
    const [navigateToLink, setNavigateToLink] = React.useState<string>();
    const { id, setInternalId } = useControlledId(idProp);
    const location = useLocation();
    const additionalData = additionalDataProp ?? location.state?.additionalData;
    const isSaveActionPresent =
        resourceType == null ? apiActions?.[id != null ? 'update' : 'create'] != null : true;
    const isDeleteActionPresent = id && apiActions?.['delete'] != null;
    const isReady = !isLoading;
    const anyModified = modified || externalModified;
    const sendOnChangeRequest = React.useCallback(
        (id: any, args: ResourceApiOnChangeArgs): Promise<any> => {
            if (resourceType == null) {
                return apiOnChange(id, args);
            } else if (resourceTypeCode != null) {
                const artifactArgs = {
                    id,
                    type: resourceType,
                    code: resourceTypeCode,
                    ...args,
                };
                return apiArtifactFormOnChange(artifactArgs);
            } else {
                return new Promise((_resolve, reject) =>
                    reject("Couldn't send artifact onChange request: empty resource type code")
                );
            }
        },
        [apiOnChange, resourceType, resourceTypeCode, apiArtifactFormOnChange]
    );
    const onChangeActionMiddleware = React.useCallback(
        (state: any, action: FormFieldDataAction) => {
            if (action.type === FormFieldDataActionType.FIELD_CHANGE) {
                const { field, fieldName, value: fieldValue } = action.payload;
                if (field?.onChangeActive) {
                    return new Promise<FormFieldDataAction>((resolve, reject) => {
                        const onChangeArgs = {
                            fieldName,
                            fieldValue,
                            previous: state,
                        };
                        sendOnChangeRequest(id, onChangeArgs)
                            .then((changes: any) => {
                                resolve({
                                    type: action.type,
                                    payload: { ...action.payload, changes },
                                });
                            })
                            .catch(reject);
                    });
                }
            }
        },
        [id, sendOnChangeRequest]
    );
    const [data, dataDispatchAction] = useReducerWithActionMiddleware<FormFieldDataAction>(
        formDataReducer,
        {},
        onChangeActionMiddleware,
        (error: any) => temporalMessageShow(t('form.onChange.error'), error.message, 'error')
    );
    const getId = () => id;
    const getData = () => data;
    const dataGetValue = (callback: (state: any) => any) => callback(data);
    const getInitialData = React.useCallback(
        async (
            id: any,
            fields: any[],
            additionalData: any,
            initOnChangeRequest?: boolean
        ): Promise<any> => {
            // Obté les dades inicials.
            // Si és un formulari d'artefacte obté les dades dels camps
            // Si no és un formulari d'artefacte:
            //     - Si és un formulari de modificació obté les dades fent una petició al servidor
            //     - Si és un formulari de creació obté les dades dels camps
            const getInitialDataFromApiGetOne = resourceType == null && id != null;
            const initialData = getInitialDataFromApiGetOne
                ? await apiGetOne(id, {
                      data: { perspective: perspectives },
                      includeLinks: true,
                  })
                : getInitialDataFromFields(fields);
            const mergedData = { ...initialData, ...additionalData };
            if (initOnChangeRequest) {
                return new Promise<any>((resolve, reject) => {
                    sendOnChangeRequest(id, { previous: mergedData })
                        .then((onChangeData) => {
                            resolve({ ...mergedData, ...onChangeData });
                        })
                        .catch(reject);
                });
            } else {
                return mergedData;
            }
        },
        [apiGetOne, sendOnChangeRequest]
    );
    const handleSubmissionErrors = (
        error: ResourceApiError,
        temporalMessageTitle?: string,
        reject?: (reason?: any) => void
    ) => {
        if (!error.modificationCanceledError) {
            // Quan es produeixen errors es fa un reject de la promesa.
            // Si els errors els tracta el mateix component Form aleshores la
            // cridada a reject es fa amb un valor buit.
            // Si l'error s'ha de mostrar a l'usuari es fa un reject amb l'error.
            if (error.status === 422) {
                const errors = error.errors ?? error.validationErrors;
                // TODO mostrar globalErrors
                //const globalErrors = errors?.find((e: any) => e.field == null);
                const fieldErrors = errors
                    ?.filter((e: any) => e.field != null)
                    .map((e: any) => ({
                        code: e.code,
                        field: e.field,
                        message: e.message,
                    }));
                setApiFieldErrors(fieldErrors);
                onValidationErrorsChange?.(id, fieldErrors);
            } else {
                temporalMessageShow(
                    temporalMessageTitle ?? '',
                    error.description ?? error.message,
                    'error'
                );
            }
        } else {
            temporalMessageShow(
                temporalMessageTitle ?? '',
                error.description ?? error.message,
                'error'
            );
        }
        reject?.(error);
    };
    const reset = (
        data: any,
        newId?: any,
        revertData?: any,
        navigateToLink?: boolean,
        isDataInitialized?: boolean
    ) => {
        const joinedData = { ...data, ...revertData };
        dataDispatchAction({
            type: FormFieldDataActionType.RESET,
            payload: joinedData,
        });
        setIsLoading(false);
        setModified(false);
        setExternalModified(false);
        setRevertData(data);
        setApiFieldErrors(undefined);
        validateWithValidator(joinedData);
        setIsDataInitialized(isDataInitialized != null ? isDataInitialized : true);
        if (navigateToLink) {
            if (id == null) {
                const link =
                    createLink != null || saveLink != null ? (createLink ?? saveLink) : undefined;
                setNavigateToLink(link);
            } else {
                const link =
                    updateLink != null || saveLink != null ? (updateLink ?? saveLink) : undefined;
                setNavigateToLink(link);
            }
        }
        newId !== undefined && setInternalId(newId);
        onReset?.(joinedData);
    };
    const externalReset = (data?: any, id?: any) => {
        // Versió de reset per a cridar externament mitjançant l'API
        const {
            _actions: initialDataActions,
            _links: initialDataLinks,
            _templates: initialDataTemplates,
            ...realInitialData
        } = data ?? {};
        id != null && setApiActions(initialDataActions);
        const mergedData = {
            ...additionalData,
            ...realInitialData,
        };
        if (initOnChangeRequest) {
            sendOnChangeRequest(id, { previous: mergedData }).then((changedData: any) => {
                reset({ ...additionalData, ...changedData }, id);
            });
        } else {
            reset(mergedData, id);
        }
    };
    const refresh = (force?: boolean) =>
        new Promise((resolve, reject) => {
            if (fields && (force || !isDataInitialized)) {
                getInitialData(id, fields, additionalData, initOnChangeRequest)
                    .then((initialData: any) => {
                        debug && logConsole.debug('Initial data loaded', initialData);
                        const {
                            _actions: initialDataActions,
                            _links: initialDataLinks,
                            _templates: initialDataTemplates,
                            ...realInitialData
                        } = initialData;
                        id != null && setApiActions(initialDataActions);
                        reset(realInitialData, undefined, initialDataProp);
                        resolve(realInitialData);
                    })
                    .catch(reject);
            }
        });
    const revert = (unconfirmed?: boolean) => {
        const revertFn = () => {
            reset(revertData);
        };
        if (unconfirmed) {
            revertFn();
        } else {
            messageDialogShow(
                t('form.revert.title'),
                t('form.revert.confirm'),
                confirmDialogButtons,
                confirmDialogComponentProps
            ).then((value: any) => {
                value && revertFn();
            });
        }
    };
    const validateWithValidator = (data: any) => {
        const validatorFieldErrors = dataValidator?.(data);
        setValidatorFieldErrors(validatorFieldErrors);
        onValidationErrorsChange?.(id, fieldErrors);
    };
    const validate = () =>
        new Promise<any>((resolve, reject) => {
            if (resourceType != null) {
                if (resourceTypeCode != null) {
                    setApiFieldErrors(undefined);
                    apiArtifactFormValidate({
                        type: resourceType,
                        code: resourceTypeCode,
                        data,
                    })
                        .then(resolve)
                        .catch((error: ResourceApiError) => {
                            handleSubmissionErrors(error, t('form.validate.error'), reject);
                        });
                } else {
                    reject("Couldn't send artifact validate request: empty resource type code");
                    console.error();
                }
            } else {
                reject('Form validation only available in form artifacts');
            }
        });
    const navigateToSaveLink = (link: string | undefined, id: any, replace?: boolean) => {
        const linkIdReplaced = link?.replace('{{id}}', '' + id);
        if (linkIdReplaced?.startsWith('.')) {
            linkIdReplaced && navigate(locationPath + '/' + linkIdReplaced, { replace });
        } else if (linkIdReplaced?.startsWith('/')) {
            linkIdReplaced &&
                navigate(linkIdReplaced.substring(1), {
                    replace: true,
                });
        } else {
            const sli = locationPath?.lastIndexOf('/');
            if (sli != -1) {
                linkIdReplaced &&
                    navigate(locationPath.substring(0, sli + 1) + linkIdReplaced, { replace });
            } else {
                linkIdReplaced && navigate(linkIdReplaced, { replace });
            }
        }
    };
    const save = () =>
        new Promise<any>((resolve, reject) => {
            if (resourceType == null) {
                if (avoidSubmitIfAnyValidatorErrors && validatorFieldErrors?.length) {
                    reject(t('form.validate.saveErrors'));
                } else {
                    setApiFieldErrors(undefined);
                    const apiSaveData = getApiSaveProcessedData(
                        id,
                        data,
                        onBeforeCreateSuccess,
                        onBeforeUpdateSuccess,
                        onBeforeSaveSuccess
                    );
                    const apiSaveAction =
                        id != null
                            ? apiUpdate(id, { data: apiSaveData })
                            : apiCreate({ data: apiSaveData });
                    apiSaveAction
                        .then((savedData: any) => {
                            const message =
                                id != null
                                    ? t(i18nKeys?.updateSuccess ?? 'form.update.success', {
                                          data: savedData,
                                      })
                                    : t(i18nKeys?.createSuccess ?? 'form.create.success', {
                                          data: savedData,
                                      });
                            temporalMessageShow(null, message, 'success');
                            if (id != null) {
                                onUpdateSuccess != null
                                    ? onUpdateSuccess(savedData)
                                    : onSaveSuccess?.(data);
                            } else {
                                onCreateSuccess != null
                                    ? onCreateSuccess(savedData)
                                    : onSaveSuccess?.(data);
                            }
                            reset(
                                savedData,
                                id == null ? savedData.id : undefined,
                                undefined,
                                true,
                                false
                            );
                            resolve(savedData);
                        })
                        .catch((error: ResourceApiError) => {
                            const title =
                                id != null
                                    ? t(i18nKeys?.updateError ?? 'form.update.error', { error })
                                    : t(i18nKeys?.createError ?? 'form.create.error', { error });
                            handleSubmissionErrors(error, title, reject);
                        });
                }
            } else {
                reject(t('form.update.wrong_resource_type', { resourceType }));
            }
        });
    const delette = () => {
        messageDialogShow(
            t('form.delete.title'),
            t('form.delete.confirm'),
            confirmDialogButtons,
            confirmDialogComponentProps
        ).then((value: any) => {
            if (value) {
                apiDelete(id)
                    .then(() => {
                        goBack(goBackLink);
                        temporalMessageShow(
                            null,
                            t(i18nKeys?.deleteSuccess ?? 'form.delete.success'),
                            'success'
                        );
                    })
                    .catch((error: ResourceApiError) => {
                        temporalMessageShow(
                            t(i18nKeys?.deleteError ?? 'form.delete.error'),
                            error.message,
                            'error'
                        );
                    });
            }
        });
    };
    const focus = (name?: string) => {
        const input = divRef.current?.querySelector<HTMLInputElement>(
            'input' + (name != null ? '[name="' + name + '"]' : '')
        );
        if (input) {
            input.focus();
        }
    };
    const setFieldValue = (name: string, value: any) => {
        const field = fields?.find((f) => f.name === name);
        dataDispatchAction({
            type: FormFieldDataActionType.FIELD_CHANGE,
            payload: { fieldName: name, field, value },
        });
    };
    const handleFormEnterKeyPressed = saveOnFieldEnterKeyPressed
        ? (e: KeyboardEvent<HTMLInputElement>) => {
              if (e.key === 'Enter') {
                  e.stopPropagation();
                  e.preventDefault();
                  save();
              }
          }
        : undefined;
    React.useEffect(() => {
        if (apiCurrentError) {
            setIsLoading(false);
        }
    }, [apiCurrentError]);
    React.useEffect(() => {
        // Obté els camps pel formulari fent una petició al servidor
        if (apiIsReady) {
            debug &&
                logConsole.debug(
                    'Loading fields' + (resourceType ? ' of type' : ''),
                    resourceType,
                    resourceTypeCode
                );
            setApiActions(apiCurrentActions);
            if (customFields == null) {
                if (resourceType == null) {
                    setFields(apiCurrentFields);
                } else if (resourceTypeCode != null) {
                    apiArtifacts({}).then((artifacts: any[]) => {
                        const artifact = artifacts.find(
                            (a: any) =>
                                a.type === resourceType.toUpperCase() && a.code === resourceTypeCode
                        );
                        if (artifact != null) {
                            if (artifact.formClassActive) {
                                setFields(processApiFields(artifact.fields));
                            }
                        } else {
                            console.warn(
                                "Couldn't find artifact (type=" +
                                    resourceType +
                                    ', code=' +
                                    resourceTypeCode +
                                    ')'
                            );
                        }
                    });
                }
            } else {
                setFields(customFields);
            }
        }
    }, [apiIsReady, customFields]);
    React.useEffect(() => {
        // Obté les dades inicials pel formulari
        if (apiIsReady && fields != null) {
            refresh(customFields != null).then((data) => {
                onReady?.(data);
            });
        }
    }, [id, fields]);
    React.useEffect(() => {
        // Controla l'estat de formulari amb modificacions
        if (isReady) {
            setModified(!shallowEqual(data, revertData));
            onDataChange?.(data, !modified);
            if (modified) {
                validateWithValidator(data);
            }
        }
    }, [isReady, data]);
    React.useEffect(() => {
        // Navega cap al link que s'ha guardat a l'estat
        if (navigateToLink) {
            navigateToSaveLink(navigateToLink, id, true);
        }
    }, [navigateToLink]);
    const getFormApi = () => ({
        getId,
        getData,
        refresh: () => refresh(true),
        reset: externalReset,
        revert,
        validate,
        save,
        delete: delette,
        focus,
        setFieldValue,
        setModified: setExternalModified,
        handleSubmissionErrors,
    });
    const apiRef = React.useRef<FormApi>(getFormApi());
    React.useEffect(() => {
        apiRef.current = getFormApi();
    }, [
        getId,
        getData,
        refresh,
        externalReset,
        revert,
        validate,
        save,
        delette,
        focus,
        setFieldValue,
        setExternalModified,
        handleSubmissionErrors,
    ]);
    if (apiRefProp) {
        apiRefProp.current = getFormApi();
    }
    const fieldErrors = [
        ...(validationErrors ?? []),
        ...(validatorFieldErrors ?? []),
        ...(apiFieldErrors ?? []),
    ];
    const parentFormContext = useOptionalFormContext();
    const context = React.useMemo(
        () => ({
            id,
            resourceName,
            resourceType,
            resourceTypeCode,
            isLoading,
            isReady,
            apiActions,
            isSaveActionPresent,
            isDeleteActionPresent,
            fields,
            fieldErrors,
            fieldTypeMap,
            inline,
            data,
            modified: anyModified,
            apiRef,
            dataGetFieldValue: (fieldName: string) => dataGetValue((state) => state?.[fieldName]),
            dataDispatchAction,
            validationSetFieldErrors: (fieldName: string, errors?: FormFieldError[]) =>
                setValidatorFieldErrors((errs) => [
                    ...(errs?.filter((e) => e.field !== fieldName) ?? []),
                    ...(errors ?? []),
                ]),
            commonFieldComponentProps,
        }),
        [
            isLoading,
            apiActions,
            fields,
            fieldErrors,
            data,
            dataDispatchAction,
            setValidatorFieldErrors,
            commonFieldComponentProps,
        ]
    );
    const divStyle: React.CSSProperties = contentExpandsToAvailableHeight
        ? { display: 'flex', flexDirection: 'column', height: '100%' }
        : {};
    return (
        <ResourceApiFormContext.Provider value={context}>
            <div style={divStyle} onKeyDown={handleFormEnterKeyPressed} ref={divRef}>
                {isReady ? children : null}
            </div>
            {!formBlockerDisabled && parentFormContext == null && (
                <FormBlocker modified={anyModified} />
            )}
        </ResourceApiFormContext.Provider>
    );
};

export default Form;
