import React from 'react';
import IconButton from '@mui/material/IconButton';
import Icon from '@mui/material/Icon';
import Badge from '@mui/material/Badge';
import { GridRowSelectionModel } from '@mui/x-data-grid-pro';

type DataGridBulkDeleteProps = {
    rowSelectionModel: GridRowSelectionModel;
    disabled: boolean;
    onClick: (ids: any) => void
};

const DataGridBulkDelete: React.FC<DataGridBulkDeleteProps> = (props) => {
    const { rowSelectionModel, disabled, onClick } = props;
    const anyRowSelected = rowSelectionModel?.ids?.size;
    return <IconButton
        disabled={!anyRowSelected || disabled}
        title="Esborrat múltiple"
        onClick={() => onClick([...rowSelectionModel?.ids])}>
        <Badge badgeContent={rowSelectionModel?.ids?.size} color="primary">
            <Icon>delete_sweep</Icon>
        </Badge>
    </IconButton>;
};

export default DataGridBulkDelete;
