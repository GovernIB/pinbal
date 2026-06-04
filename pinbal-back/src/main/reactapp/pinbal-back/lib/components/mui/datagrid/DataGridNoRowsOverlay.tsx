import React from 'react';
import { useBaseAppContext } from '../../BaseAppContext';
import DataNoRows from '../datacommon/DataNoRows';

type DataGridNoRowsOverlayProps = {
    requestPending?: boolean;
    noRowsText?: string;
};

const DataGridNoRowsOverlay: React.FC<DataGridNoRowsOverlayProps> = (props) => {
    const { requestPending, noRowsText } = props;
    const { t } = useBaseAppContext();
    const icon = requestPending ? 'pending' : undefined;
    const message = requestPending ? t('grid.requestPending') : noRowsText;
    return <DataNoRows icon={icon} message={message} />;
};

export default DataGridNoRowsOverlay;
