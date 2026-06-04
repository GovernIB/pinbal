import React from 'react';

export type DetailContextType = {
    id?: any;
    resourceName: string;
    isLoading: boolean;
    isReady: boolean;
    fields?: any[];
    fieldTypeMap?: Map<string, string>;
    data?: any;
    dataGetFieldValue: (fieldName: string) => any;
};

export const DetailContext = React.createContext<DetailContextType | undefined>(undefined);

export const useDetailContext = () => {
    const context = React.useContext(DetailContext);
    if (context === undefined) {
        throw new Error('useDetailContext must be used within a DetailProvider');
    }
    return context;
};

export const useOptionalDetailContext = (): DetailContextType | undefined => {
    return React.useContext(DetailContext);
};

export default DetailContext;
