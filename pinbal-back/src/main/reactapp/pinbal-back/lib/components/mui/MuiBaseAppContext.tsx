import React from 'react';
import { MuiDataGridProps } from './datagrid/MuiDataGrid';
import { MuiDataListProps } from './datalist/MuiDataList';
import { MuiFormProps } from './form/MuiForm';
import { MuiFilterProps } from './form/MuiFilter';
import { MuiDetailProps } from './detail/MuiDetail';

export type DefaultMuiComponentProps = {
    dataGrid?: Partial<MuiDataGridProps>;
    dataList?: Partial<MuiDataListProps>;
    form?: Partial<MuiFormProps>;
    filter?: Partial<MuiFilterProps>;
    detail?: Partial<MuiDetailProps>;
};

export type MuiBaseAppContextType = {
    defaultMuiComponentProps: DefaultMuiComponentProps;
};

export const MuiBaseAppContext = React.createContext<MuiBaseAppContextType | undefined>(undefined);
export const useMuiBaseAppContext = () => {
    const context = React.useContext(MuiBaseAppContext);
    if (context === undefined) {
        throw new Error('useMuiBaseAppContext must be used within a MuiBaseAppProvider');
    }
    return context;
};

export const useOptionalMuiBaseAppContext = (): MuiBaseAppContextType | undefined => {
    return React.useContext(MuiBaseAppContext);
};

export default MuiBaseAppContext;
