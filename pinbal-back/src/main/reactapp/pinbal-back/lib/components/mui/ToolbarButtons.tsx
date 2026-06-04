import React from 'react';
import Toolbar from '@mui/material/Toolbar';
import Button from '@mui/material/Button';
import Icon from '@mui/material/Icon';
import { DialogButton } from '../BaseAppContext';

type ToolbarButtonsProps = {
    buttons: DialogButton[];
    handleClose: (value?: any) => void;
    bottomAligned?: true;
};

export const ToolbarButtons: React.FC<ToolbarButtonsProps> = (props) => {
    const { buttons, handleClose, bottomAligned } = props;
    const bottomAlignedSxProps = bottomAligned
        ? {
              position: 'absolute',
              bottom: 0,
              width: '100%',
              pr: 2,
              zIndex: 10,
          }
        : {};
    return (
        <>
            <Toolbar
                disableGutters
                sx={{
                    display: 'flex',
                    justifyContent: 'flex-end',
                    gap: '8px',
                    ...bottomAlignedSxProps,
                }}>
                {buttons.map((b: DialogButton, i: number) => (
                    <Button key={i} onClick={() => handleClose(b.value)} {...b.componentProps}>
                        {b.icon && <Icon sx={{ mr: 1 }}>{b.icon}</Icon>}
                        {b.text}
                    </Button>
                ))}
            </Toolbar>
            {bottomAligned && <Toolbar />}
        </>
    );
};

export default ToolbarButtons;
