import { useSearchParams } from 'react-router-dom';

/**
 * Hook per obtenir el número de pestanya des de la URL.
 * Retorna el número o undefined si no existeix o no es vàlid.
 */
export const useTabParam = () => {
    const [searchParams] = useSearchParams();

    const tabValue = searchParams.get('tab');
    const initialTab = tabValue ? Number(tabValue) : undefined;

    return initialTab;
};
