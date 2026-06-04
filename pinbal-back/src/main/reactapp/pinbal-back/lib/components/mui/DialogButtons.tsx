import React from 'react';
import DialogActions from '@mui/material/DialogActions';
import Button from '@mui/material/Button';
import Icon from '@mui/material/Icon';
import { DialogButton } from '../BaseAppContext';

type DialogButtonsProps = {
    buttons: DialogButton[];
    handleClose: (value?: any) => void;
};

export const DialogButtons: React.FC<DialogButtonsProps> = (props) => {
    const { buttons, handleClose } = props;
    return (
        <DialogActions>
            {buttons.map((b: DialogButton, i: number) => (
                <Button key={i} onClick={() => handleClose(b.value)} {...b.componentProps}>
                    {b.icon && <Icon sx={{ mr: 1 }}>{b.icon}</Icon>}
                    {b.text}
                </Button>
            ))}
        </DialogActions>
    );
};

export default DialogButtons;
