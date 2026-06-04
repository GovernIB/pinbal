import React from 'react';
import IconButton from '@mui/material/IconButton';
import Icon from '@mui/material/Icon';
import useSmallScreen from '../../util/useSmallScreen';

type UseToolbarMenuIconReturnType = {
    shrink: boolean;
    iconClicked: boolean;
    buttonComponent: React.ReactNode;
};

type ToolbarMenuIconProps = {
    icon: string;
    iconFlipX?: boolean;
    handleClick: () => void;
};

export const useToolbarMenuIcon = (): UseToolbarMenuIconReturnType => {
    const smallScreen = useSmallScreen();
    const [shrink, setShrink] = React.useState<boolean>(false);
    const [iconClicked, setIconClicked] = React.useState<boolean>(false);
    const handleToolbarMenuIconClick = () => {
        !smallScreen && setShrink((shrink) => !shrink);
        setIconClicked((c) => !c);
    };
    const buttonComponent = (
        <ToolbarMenuIcon
            icon={smallScreen ? 'menu' : 'menu_open'}
            iconFlipX={shrink}
            handleClick={handleToolbarMenuIconClick}
        />
    );
    return {
        shrink,
        iconClicked,
        buttonComponent,
    };
};

export const ToolbarMenuIcon: React.FC<ToolbarMenuIconProps> = (props) => {
    const { icon, iconFlipX, handleClick } = props;
    return (
        <IconButton
            size="large"
            edge="start"
            color="inherit"
            aria-label="menu"
            onClick={handleClick}
            sx={{ mr: 2 }}>
            <Icon sx={iconFlipX ? { transform: 'scaleX(-1)' } : undefined}>{icon}</Icon>
        </IconButton>
    );
};

export default ToolbarMenuIcon;
