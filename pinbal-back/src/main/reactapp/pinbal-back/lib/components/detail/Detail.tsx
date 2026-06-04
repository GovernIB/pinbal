import React from 'react';
import { useResourceApiService } from '../ResourceApiProvider';
import useLogConsole from '../../util/useLogConsole';
import DetailContext from './DetailContext';

const LOG_PREFIX = 'DETAIL';

/**
 * Propietats del component Detail.
 */
export type DetailProps = React.PropsWithChildren & {
    /** Títol del detall */
    title?: string;
    /** Nom del recurs de l'API REST d'on es consultarà la informació per a mostrar el detall */
    resourceName: string;
    /** Id del recurs que s'ha de consultar*/
    id: any;
    /** Perspectives que s'enviaran al consultar la informació del recurs */
    perspectives?: string[];
    /** Adreça que s'ha de mostrar al fer click al botó de retrocedir (només s'utilitzarà si l'historial està buit) */
    goBackLink?: string;
    /** Mapeig dels tipus de camp */
    fieldTypeMap?: Map<string, string>;
    /** Indica si s'han d'imprimir a la consola missatges de depuració */
    debug?: boolean;
};

/**
 * Component base de detalls independent de la llibreria de interfície d'usuari.
 *
 * @param props - Propietats del component.
 * @returns Element JSX dels detalls.
 */
export const Detail: React.FC<DetailProps> = (props) => {
    const { resourceName, id, perspectives, fieldTypeMap, debug = false, children } = props;
    const logConsole = useLogConsole(LOG_PREFIX);
    const {
        isReady: apiIsReady,
        currentFields: apiCurrentFields,
        currentError: apiCurrentError,
        getOne: apiGetOne,
    } = useResourceApiService(resourceName);
    const [isLoading, setIsLoading] = React.useState<boolean>(true);
    const [fields, setFields] = React.useState<any[]>();
    const isReady = !isLoading;
    const [data, setData] = React.useState<any>();
    const dataGetValue = (callback: (state: any) => any) => callback(data);
    const getInitialData = React.useCallback(
        async (id: any): Promise<any> => {
            return await apiGetOne(id, { data: { perspectives } });
        },
        [apiGetOne, perspectives]
    );
    const reset = (data: any) => {
        setData(data);
        setIsLoading(false);
    };
    const refresh = () => {
        if (fields) {
            getInitialData(id).then((initialData: any) => {
                debug && logConsole.debug('Initial data loaded', initialData);
                reset(initialData);
            });
        }
    };
    React.useEffect(() => {
        if (apiCurrentError) {
            setIsLoading(false);
        }
    }, [apiCurrentError]);
    React.useEffect(() => {
        if (apiIsReady) {
            debug && logConsole.debug('Loading fields from resource', resourceName);
            setFields(apiCurrentFields);
        }
    }, [apiIsReady]);
    React.useEffect(() => {
        refresh();
    }, [id, fields]);
    const context = React.useMemo(
        () => ({
            id,
            resourceName,
            isLoading,
            isReady,
            fields,
            fieldTypeMap,
            data,
            dataGetFieldValue: (fieldName: string) => dataGetValue((state) => state?.[fieldName]),
        }),
        [isLoading, fields, data]
    );
    return (
        <DetailContext.Provider value={context}>{isReady ? children : null}</DetailContext.Provider>
    );
};

export default Detail;
