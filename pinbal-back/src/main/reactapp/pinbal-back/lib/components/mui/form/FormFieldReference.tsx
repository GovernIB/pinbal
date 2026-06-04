import React from 'react';
import { GridColDef, GridSortModel } from '@mui/x-data-grid-pro';
import Autocomplete, { AutocompleteChangeReason } from '@mui/material/Autocomplete';
import TextField from '@mui/material/TextField';
import CircularProgress from '@mui/material/CircularProgress';
import IconButton from '@mui/material/IconButton';
import Icon from '@mui/material/Icon';
import Checkbox from '@mui/material/Checkbox';
import { useFormContext } from '../../form/FormContext';
import { FormFieldCustomProps } from '../../form/FormField';
import { useBaseAppContext } from '../../BaseAppContext';
import { useResourceApiContext } from '../../ResourceApiContext';
import { useDebounce } from '../../../util/useDebounce';
import { useDataGridDialog } from '../datagrid/DataGridDialog';
import { useFormFieldCommon } from './FormFieldText';

const DEFAULT_PAGE_SIZE = 5;

type FormFieldReferenceRendererArgs = {
    id: any;
    description: string;
};

type FormFieldRefProps = FormFieldCustomProps & {
    /** Indica si el camp permet múltiples valors */
    multiple?: boolean;
    /** Filtre en format Spring Filter per a consultar els valors al backend */
    filter?: string;
    /** Model per a l'ordenació dels resultats */
    sortModel?: GridSortModel;
    /** Paràmetre per a consultar els valors al backend */
    namedQueries?: string[];
    /** Paràmetre per a consultar els valors al backend */
    perspectives?: string[];
    /** Mida de pàgina per a consultar els valors al backend */
    optionsPageSize?: number;
    /** Indica que la consulta de valors al backend s'ha de fer sense paginació */
    optionsUnpaged?: boolean;
    /** Configuració de columnes pel grid de la finestra emergent de consulta avançada */
    advancedSearchColumns?: GridColDef[];
    /** Alçada per a la finestra emergent de consulta avançada */
    advancedSearchDialogHeight?: number;
    /** Propietats del component Dialog per a la finestra emergent de consulta avançada */
    advancedSearchDialogComponentProps?: any;
    /** Callback per a substituir la funció que fa la consulta de resultats */
    optionsRequest?: (q: string) => AdvancedSearchOptionsRequestType;
    /** Callback per personalitzar cada element de la llista de resultats */
    optionRenderer?: (args: FormFieldReferenceRendererArgs) => React.ReactElement;
};

type AdvancedSearchDialogApi = {
    show: () => Promise<any>;
};

type AdvancedSearchDialogProps = React.PropsWithChildren & {
    title: string;
    fieldName: string;
    columns: GridColDef[];
    filter?: string;
    sortModel?: GridSortModel;
    namedQueries?: string[];
    perspectives?: string[];
    apiRef: React.RefObject<AdvancedSearchDialogApi | null>;
    dialogHeight?: number;
    dialogComponentProps?: any;
};

export type FormFieldRefOptionsResponse = {
    options: any[];
    page?: any;
};

type AdvancedSearchOptionsRequestType =
    | Promise<FormFieldRefOptionsResponse>
    | [Promise<FormFieldRefOptionsResponse>, () => void];

const AdvancedSearchDialog: React.FC<AdvancedSearchDialogProps> = (props) => {
    const {
        title,
        fieldName,
        columns,
        filter,
        sortModel,
        namedQueries,
        perspectives,
        apiRef,
        dialogHeight,
        dialogComponentProps,
    } = props;
    const { resourceName, resourceType, resourceTypeCode } = useFormContext();
    const [gridDialogShow, gridDialogComponent] = useDataGridDialog(
        resourceName,
        columns,
        resourceType,
        resourceTypeCode,
        fieldName,
        true,
        {
            fullWidth: true,
            maxWidth: 'md',
            ...dialogComponentProps,
        }
    );
    const show = () => {
        return gridDialogShow({
            title,
            height: dialogHeight,
            dataGridComponentProps: {
                readOnly: true,
                paginationActive: true,
                titleDisabled: true,
                quickFilterSetFocus: true,
                quickFilterFullWidth: true,
                toolbarHideRefresh: true,
                filter,
                sortModel,
                namedQueries,
                perspectives,
            },
        });
    };
    if (apiRef) {
        apiRef.current = { show };
    }
    return <>{gridDialogComponent}</>;
};

const useFieldOptions = (
    open: boolean,
    inputValue: string | undefined,
    optionsRequest: (q: string) => AdvancedSearchOptionsRequestType
) => {
    const [loading, setLoading] = React.useState<boolean>(false);
    const [options, setOptions] = React.useState<any[]>([]);
    const [page, setPage] = React.useState<any>();
    const [error, setError] = React.useState<any>();
    const debouncedInputValue = useDebounce(inputValue, undefined, true);
    React.useEffect(() => {
        if (open) {
            const q = debouncedInputValue?.length ? debouncedInputValue : null;
            setLoading(true);
            const or = optionsRequest(q);
            const optionsRequestPromise = Array.isArray(or) ? or[0] : or;
            const optionsRequestCancel = Array.isArray(or) ? or[1] : undefined;
            setError(undefined);
            optionsRequestPromise
                .then((response) => {
                    setOptions(response.options);
                    setPage(response?.page);
                })
                .catch(setError)
                .finally(() => {
                    setLoading(false);
                });
            return () => optionsRequestCancel?.();
        }
    }, [open, optionsRequest, debouncedInputValue]);
    return { loading, options, page, error };
};

export const FormFieldReference: React.FC<FormFieldRefProps> = (props) => {
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
        filter,
        sortModel,
        namedQueries,
        perspectives,
        advancedSearchColumns,
        optionsPageSize = DEFAULT_PAGE_SIZE,
        optionsUnpaged,
        optionsRequest: optionsRequestProp,
        optionRenderer,
        multiple: multipleProp,
        advancedSearchDialogHeight,
        advancedSearchDialogComponentProps,
    } = props;
    const { t } = useBaseAppContext();
    const { requestHref } = useResourceApiContext();
    const advancedSearchApiRef = React.useRef<AdvancedSearchDialogApi>(null);
    const multiple = (field?.multiple || multipleProp) ?? false;
    const isEmptyValue = multiple ? !value?.length : value == null;
    const [open, setOpen] = React.useState<boolean>(false);
    const [inputValue, setInputValue] = React.useState<string>('');
    const [optionsQuickFilter, setOptionsQuickFilter] = React.useState<string>('');
    const [ignoreOnInputChangeEvent, setIgnoreOnInputChangeEvent] = React.useState<boolean>(false);
    const processValueChange = (value: any) => {
        onChange(value);
        setInputValue(value?.description ?? '');
        setOptionsQuickFilter('');
    };
    const optionsRequest = React.useCallback(
        (q: string) => {
            if (optionsRequestProp != null) {
                return optionsRequestProp(q);
            } else {
                return new Promise<FormFieldRefOptionsResponse>((resolve, reject) => {
                    const dataSource = field.dataSource;
                    const valueField = dataSource.valueField;
                    const labelField = dataSource.labelField;
                    const pageArgs = optionsUnpaged
                        ? { page: 'UNPAGED' }
                        : { page: 0, size: optionsPageSize };
                    const sorts =
                        sortModel && sortModel.length
                            ? sortModel.map((sm) => sm.field + ',' + sm.sort)
                            : undefined;
                    const templateData = {
                        quickFilter: q,
                        filter,
                        sorts,
                        namedQuery: namedQueries,
                        perspective: perspectives,
                        ...pageArgs,
                    };
                    requestHref(dataSource.href, templateData)
                        .then((state) => {
                            const options = state.getEmbedded().map((e) => ({
                                id: e.data[valueField],
                                description: e.data[labelField],
                            }));
                            const response = {
                                options,
                                page: state.data.page,
                            };
                            resolve(response);
                        })
                        .catch(reject);
                });
            }
        },
        [optionsRequestProp, filter, sortModel, namedQueries, perspectives]
    );
    const {
        loading: optionsLoading,
        options,
        page: optionsPage,
        error: optionsError,
    } = useFieldOptions(open, optionsQuickFilter, optionsRequest);
    const handleOnChange = (_event: Event, value: any, reason: AutocompleteChangeReason): void => {
        if (reason === 'clear') {
            setOpen(true);
        }
        processValueChange(value);
    };
    const handleOnInputChange = (_event: any, newValue: string) => {
        if (ignoreOnInputChangeEvent) {
            setIgnoreOnInputChangeEvent(false);
            return;
        }
        if (isEmptyValue || multiple || newValue?.length == 0) {
            setInputValue(newValue);
            setOptionsQuickFilter(newValue);
        } else {
            setIgnoreOnInputChangeEvent(true);
            onChange(null);
            if (
                newValue.startsWith(value.description) &&
                newValue.length > value.description.length
            ) {
                const newChar = newValue.slice(-1);
                setInputValue(newChar);
                setOptionsQuickFilter(newChar);
            } else {
                setInputValue(newValue);
                setOptionsQuickFilter(newValue);
            }
        }
    };
    const handleAdvancedSearchClick = () => {
        advancedSearchApiRef.current
            ?.show()
            .then((row: any) => {
                const valueField = field.dataSource.valueField;
                const labelField = field.dataSource.labelField;
                const valueReference = {
                    id: row[valueField],
                    description: row[labelField],
                };
                const valueReferenceWithData = valueReference
                    ? { ...valueReference, data: row }
                    : null;
                if (multiple) {
                    if (value != null) {
                        const currentValues = Array.isArray(value) ? value : [value];
                        const currentValueFound = currentValues.find(
                            (v) => v.id === valueReferenceWithData?.id
                        );
                        processValueChange(
                            currentValueFound
                                ? currentValues
                                : [...currentValues, valueReferenceWithData]
                        );
                    } else {
                        processValueChange([valueReferenceWithData]);
                    }
                } else {
                    processValueChange(valueReferenceWithData);
                }
            })
            .catch(() => {});
    };
    const autoFocus = componentProps?.autoFocus;
    const startAdornmentIcons: React.ReactElement[] = [];
    const optionsErrorIconElement =
        optionsError != null ? (
            <Icon fontSize="small" color="error" title={optionsError.message} sx={{ ml: 1 }}>
                warning
            </Icon>
        ) : null;
    const advancedSearchButtonActive = advancedSearchColumns != null && !disabled && !readOnly;
    const advancedSearchButtonElement = advancedSearchButtonActive ? (
        <IconButton onClick={handleAdvancedSearchClick} size="small" tabIndex={-1}>
            <Icon fontSize="small">manage_search</Icon>
        </IconButton>
    ) : null;
    optionsErrorIconElement != null && startAdornmentIcons.push(optionsErrorIconElement);
    advancedSearchButtonElement != null && startAdornmentIcons.push(advancedSearchButtonElement);
    const { helperText, title, startAdornment } = useFormFieldCommon(
        field,
        fieldError,
        inline,
        componentProps,
        startAdornmentIcons
    );
    const loadingElement = <CircularProgress color="inherit" size={20} />;
    const endAdornment = optionsLoading
        ? loadingElement
        : componentProps?.slotProps?.input?.endAdornment;
    const valueMultipleAdapted = React.useMemo(() => {
        return multiple
            ? value != null
                ? Array.isArray(value)
                    ? value
                    : [value]
                : []
            : (value ?? null);
    }, [multiple, value]);
    const inputValueMultipleAdapted = React.useMemo(() => {
        return multiple ? inputValue : value != null ? value.description : inputValue;
    }, [multiple, value, inputValue]);
    return (
        <>
            {advancedSearchButtonActive && (
                <AdvancedSearchDialog
                    title={t('form.field.reference.advanced.title')}
                    fieldName={field.name}
                    columns={advancedSearchColumns}
                    filter={filter}
                    sortModel={sortModel}
                    namedQueries={namedQueries}
                    perspectives={perspectives}
                    apiRef={advancedSearchApiRef}
                    dialogHeight={advancedSearchDialogHeight}
                    dialogComponentProps={advancedSearchDialogComponentProps}
                />
            )}
            <Autocomplete
                name={name}
                value={valueMultipleAdapted}
                onChange={handleOnChange}
                inputValue={inputValueMultipleAdapted}
                onInputChange={handleOnInputChange}
                options={options}
                multiple={multiple}
                readOnly={readOnly}
                disableCloseOnSelect={multiple}
                open={open}
                onOpen={() => !disabled && !readOnly && setOpen(true)}
                onClose={(event: Event, reason) => {
                    if (reason === 'escape') {
                        // Esborra el valor de inputValue si no hi ha cap opció seleccionada
                        // quan es pitja la tecla escape.
                        handleOnInputChange(event, value?.description ?? '');
                    }
                    setOpen(false);
                }}
                getOptionLabel={(option: any) => option?.description}
                isOptionEqualToValue={(option: any, value: any) => option.id === value?.id}
                renderOption={(props, option: any, { selected }) => {
                    const optionRendererArgs = {
                        id: option.id,
                        description: option.description,
                    };
                    const optionDescription = optionRenderer
                        ? optionRenderer(optionRendererArgs)
                        : option.description;
                    if (multiple) {
                        const { key, ...optionProps } = props;
                        return (
                            <li key={key} {...optionProps}>
                                <Checkbox
                                    checked={selected}
                                    icon={<Icon>check_box_outline_blank</Icon>}
                                    checkedIcon={<Icon>check_box</Icon>}
                                    sx={{ mr: 1 }}
                                />
                                {optionDescription}
                            </li>
                        );
                    } else {
                        return (
                            <li {...props} key={option.id}>
                                {optionDescription}
                            </li>
                        );
                    }
                }}
                filterOptions={(options) => {
                    if (!optionsUnpaged) {
                        const currentPageSize = optionsPage?.size ?? DEFAULT_PAGE_SIZE;
                        if (optionsPage?.totalElements > currentPageSize) {
                            options.push({
                                id: '___pageLabel',
                                description: t('form.field.reference.page', {
                                    size: currentPageSize,
                                    totalElements: optionsPage?.totalElements,
                                }),
                                disabled: true,
                            });
                        }
                    }
                    return options;
                }}
                getOptionDisabled={(option) => option.disabled}
                fullWidth
                {...componentProps}
                renderInput={(params) => (
                    <TextField
                        {...params}
                        label={!inline ? label : undefined}
                        placeholder={componentProps?.placeholder ?? (inline ? label : undefined)}
                        disabled={disabled}
                        required={required ?? field.required}
                        error={fieldError != null}
                        title={componentProps?.title ?? title}
                        helperText={helperText}
                        autoFocus={autoFocus}
                        sx={{
                            // Sin esto, si la columna no tiene suficiente espacio para el texto y el icono,
                            // coloca uno encima del otro y se ve cortado por la mitad.
                            '& .MuiAutocomplete-inputRoot': {
                                flexWrap: inline ? 'nowrap' : undefined,
                            },
                        }}
                        slotProps={{
                            input: {
                                ...params.InputProps,
                                startAdornment: params.InputProps.startAdornment ? (
                                    <>
                                        {startAdornment}
                                        {params.InputProps.startAdornment}
                                    </>
                                ) : (
                                    startAdornment
                                ),
                                endAdornment: params.InputProps.endAdornment ? (
                                    <>
                                        {params.InputProps.endAdornment}
                                        {endAdornment}
                                    </>
                                ) : (
                                    endAdornment
                                ),
                            },
                            htmlInput: params.inputProps,
                        }}
                    />
                )}
                slotProps={{
                    popper: {
                        sx: {
                            minWidth: '300px',
                        },
                    },
                    // The next prop fixes a bug in Firefox where the focus was put into the Listbox
                    // container, and then lost focus of the form completely when navigating to the next input
                    listbox: { tabIndex: '-1' },
                }}
                openText={t('form.field.reference.open')}
                closeText={t('form.field.reference.close')}
                clearText={t('form.field.reference.clear')}
                loadingText={t('form.field.reference.loading')}
                noOptionsText={
                    optionsLoading
                        ? t('form.field.reference.loading')
                        : t('form.field.reference.noOptions')
                }
            />
        </>
    );
};
export default FormFieldReference;
