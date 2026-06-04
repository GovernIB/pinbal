import React from 'react';
import { SxProps } from '@mui/material';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import Badge from '@mui/material/Badge';
import { useBaseAppContext } from '../BaseAppContext';

type ToolbarIconParams = {
    title?: string;
    linkTo?: string;
    linkState?: any;
    badge?: React.ReactNode;
    onClick?: React.MouseEventHandler<HTMLButtonElement>;
    disabled?: boolean;
    small?: boolean;
    color?: string;
    sx?: SxProps;
};

type ToolbarIconProps = {
    icon: string;
    title?: string;
    linkTo?: string;
    badge?: React.ReactNode;
    onClick?: React.MouseEventHandler<HTMLButtonElement>;
    disabled?: boolean;
    small?: boolean;
    color?: string;
    otherProps?: any;
};

export const toToolbarIcon = (icon: string, params?: ToolbarIconParams): React.ReactElement => (
    <ToolbarIcon
        icon={icon}
        title={params?.title}
        linkTo={params?.linkTo}
        badge={params?.badge}
        small={params?.small}
        onClick={params?.onClick ?? undefined}
        disabled={params?.disabled}
        color={params?.color}
        otherProps={{ sx: params?.sx }}
    />
);

export const ToolbarIcon: React.FC<ToolbarIconProps> = (props) => {
    const { icon, title, linkTo, badge, onClick, disabled, small, color, otherProps } = props;
    const { getLinkComponent } = useBaseAppContext();
    const ic = <Icon fontSize={small ? 'small' : undefined}>{icon}</Icon>;
    const linkProps: any = linkTo
        ? {
              component: getLinkComponent(),
              to: linkTo,
          }
        : null;
    return (
        <IconButton
            title={title}
            onClick={onClick}
            disabled={disabled}
            size={small ? 'small' : undefined}
            color={color}
            {...linkProps}
            {...otherProps}>
            {badge ? (
                <Badge badgeContent={badge} color="primary">
                    {ic}
                </Badge>
            ) : (
                ic
            )}
        </IconButton>
    );
};

export default ToolbarIcon;
