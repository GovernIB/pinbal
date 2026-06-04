import React, { KeyboardEvent } from 'react';
import { useAuthContext } from '../AuthContext';
import { useBaseAppContext } from '../BaseAppContext';
import { ResourceApiError } from '../ResourceApiProvider';
import Form, { useFormApiRef } from './Form';
import { FilterApi, FilterApiRef, FilterContext, useFilterContext } from './FilterContext';
import { FormFieldError } from './FormContext';

/**
 * Propietats del component Filter.
 */
export type FilterProps = React.PropsWithChildren & {
    /** Nom del recurs de l'API REST d'on es consultarà la informació per a mostrar el filtre */
    resourceName: string;
    /** Codi de l'artefacte del filtre */
    code: string;
    /** Indica si l'aplicació del filtre està controlada per un botó o si s'aplica de forma automàtica */
    buttonControlled?: true;
    /** Indica si s'ha de validar el filtre en cada canvi */
    validationActive?: true;
    /** Indica si la persistència de l'estat està activa */
    persistentStateActive?: true;
    /** La clau amb la que es desarà l'estat (s'utilitzarà el valor de resourceName si no s'especifica) */
    persistentStateKey?: string;
    /** El magatzem del navegador que s'utilitzarà per a persistir l'estat (LocalStorage per defecte) */
    persistentStateStorage?: 'local' | 'session';
    /** Referència a l'api del component */
    apiRef?: FilterApiRef;
    /** Funció encarregada de transforma les dades del filtre en una cadena en format Spring Filter */
    springFilterBuilder: (data: any) => string | undefined;
    /** Dades inicials pel filtre */
    initialData?: any;
    /** Dades addicionals pel filtre */
    additionalData?: any;
    /** Indica si s'ha de fer una petició onChange sense cap camp associat quan es crea el component */
    initOnChangeRequest?: true;
    /** Indica si s'ha de filtrar quan es pitgi la tecla Intro en algun camp */
    filterOnFieldEnterKeyPressed?: true;
    /** Propietats comunes per a tots els components FormField de dins aquest component */
    commonFieldComponentProps?: any;
    /** Event que es llença quan es modifica alguna dada del filtre */
    onDataChange?: (data: any) => void;
    /** Event que es llença quan hi ha canvis en el filtre Spring Filter que genera aquest component */
    onSpringFilterChange?: (springFilter: string | undefined) => void;
    /** Errors de validació */
    validationErrors?: FormFieldError[];
    /** Indica si s'han d'imprimir a la consola missatges de depuració */
    debug?: true;
};

const usePersistentState = (
    active: boolean,
    initialDataProp: any,
    key: string,
    storeInLocalStorage?: boolean
) => {
    const { code } = useBaseAppContext();
    const { isAuthenticated, getUserId } = useAuthContext();
    const userSuffix = isAuthenticated ? '_' + getUserId().toUpperCase() : '';
    const storageKey = code + '_FLT_' + key.toUpperCase() + userSuffix;
    const loadInitialState = () => {
        try {
            const storage = storeInLocalStorage ? localStorage : sessionStorage;
            const raw = storage.getItem(storageKey);
            return raw ? JSON.parse(raw) : null;
        } catch {
            return null;
        }
    };
    const saveState = (state: any) => {
        try {
            const storage = storeInLocalStorage ? localStorage : sessionStorage;
            storage.setItem(storageKey, JSON.stringify(state));
        } catch {}
    };
    const initialState = active ? loadInitialState() : undefined;
    const [initialData, setInitialData] = React.useState<Record<string, number>>(
        initialState || initialDataProp
    );
    React.useEffect(() => {
        active && saveState(initialData);
    }, [initialData]);
    return {
        initialData,
        setInitialData,
    };
};

/**
 * Hook per a accedir a l'API de Filter des de fora del context del component.
 *
 * @returns referència a l'API del component Filter.
 */
export const useFilterApiRef: () => FilterApiRef = () => {
    const filterApiRef = React.useRef<FilterApi>(null);
    return filterApiRef;
};

/**
 * Hook per a accedir a l'API de Filter des de dins el context del component.
 *
 * @returns referència a l'API del component Filter.
 */
export const useFilterApiContext: () => FilterApiRef = () => {
    const filterContext = useFilterContext();
    return filterContext.apiRef;
};

/**
 * Component base de filtre independent de la llibreria de interfície d'usuari.
 *
 * @param props - Propietats del component.
 * @returns Element JSX del filtre.
 */
export const Filter: React.FC<FilterProps> = (props) => {
    const {
        resourceName,
        code,
        buttonControlled,
        validationActive,
        persistentStateActive,
        persistentStateKey,
        persistentStateStorage,
        springFilterBuilder,
        initialData: initialDataProp,
        additionalData,
        filterOnFieldEnterKeyPressed,
        onDataChange,
        onSpringFilterChange,
        validationErrors,
        apiRef: apiRefProp,
        children,
        ...otherFormProps
    } = props;
    const { initialData, setInitialData } = usePersistentState(
        persistentStateActive ?? false,
        initialDataProp,
        persistentStateKey ?? resourceName,
        persistentStateStorage === 'local'
    );
    const [nextDataChangeAsUncontrolled, setNextDataChangeAsUncontrolled] =
        React.useState<boolean>(false);
    const formApiRef = useFormApiRef();
    const filter = (data?: any) => {
        const applyFilter = () => {
            const formData = data ?? formApiRef.current?.getData();
            const springFilter = springFilterBuilder(formData);
            onSpringFilterChange?.(springFilter);
        };
        if (validationActive) {
            formApiRef.current?.validate().then(applyFilter);
        } else {
            applyFilter();
        }
    };
    const clear = () => {
        setNextDataChangeAsUncontrolled(!buttonControlled);
        formApiRef.current?.revert(true);
    };
    const handleDataChange = (data: any) => {
        onDataChange?.(data);
        if (nextDataChangeAsUncontrolled) {
            setNextDataChangeAsUncontrolled(false);
            filter(data);
            setInitialData(data);
        } else if (!buttonControlled) {
            filter(data);
            setInitialData(data);
        }
    };
    const fieldTypeMap = new Map<string, string>([
        ['datetime-local', 'date'],
        ['checkbox', 'checkbox-select'],
    ]);
    const handleFilterEnterKeyPressed = filterOnFieldEnterKeyPressed
        ? (e: KeyboardEvent<HTMLInputElement>) => {
              if (e.key === 'Enter') {
                  e.stopPropagation();
                  e.preventDefault();
                  filter();
              }
          }
        : undefined;
    const getFilterApi = () => ({
        clear,
        filter,
        getId: () => formApiRef.current?.getId(),
        getData: () => formApiRef.current?.getData(),
        refresh: () => formApiRef.current?.refresh(),
        reset: (data?: any, id?: any) => formApiRef.current?.reset(data, id),
        revert: (unconfirmed?: boolean) => formApiRef.current?.revert(unconfirmed),
        validate: async () => await formApiRef.current?.validate(),
        save: async () => await formApiRef.current?.save(),
        delete: () => formApiRef.current?.delete(),
        focus: (name?: string) => formApiRef.current?.focus(name),
        setFieldValue: (name: string, value: any) => formApiRef.current?.setFieldValue(name, value),
        setModified: (modified: boolean) => formApiRef.current?.setModified(modified),
        handleSubmissionErrors: (error: ResourceApiError, temporalMessageTitle?: string) =>
            formApiRef.current?.handleSubmissionErrors(error, temporalMessageTitle),
    });
    const apiRef = React.useRef<FilterApi>(getFilterApi());
    if (apiRefProp) {
        apiRefProp.current = getFilterApi();
    }
    const context = {
        resourceName,
        code,
        apiRef,
    };
    return (
        <FilterContext.Provider value={context}>
            <div onKeyDown={handleFilterEnterKeyPressed}>
                <Form
                    resourceName={resourceName}
                    resourceType="FILTER"
                    resourceTypeCode={code}
                    initialData={initialData}
                    additionalData={additionalData}
                    onDataChange={handleDataChange}
                    fieldTypeMap={fieldTypeMap}
                    validationErrors={validationErrors}
                    apiRef={formApiRef}
                    formBlockerDisabled
                    {...otherFormProps}>
                    {children}
                </Form>
            </div>
        </FilterContext.Provider>
    );
};

export default Filter;
