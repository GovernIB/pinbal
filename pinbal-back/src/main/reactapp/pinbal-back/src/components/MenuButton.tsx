import React, {ReactElement} from "react";
import {Button, Icon, Menu, MenuItem} from "@mui/material";

type MenuButtonProps = {
    id: string;
    hidden?: boolean;
    children?: any;
    buttonLabel?: string | ReactElement;
    buttonProps?: any;
    menuProps?: any;
    arrowDown?: string;
    arrowUp?: string;
    hiddenIcon?: boolean,
}

const MenuButton = (props:MenuButtonProps) => {
    const {
        id,
        hidden,
        children,
        buttonLabel,
        buttonProps,
        menuProps,
        arrowDown = 'arrow_drop_down',
        arrowUp = 'arrow_drop_up',
        hiddenIcon = false
    } = props;
    const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);
    const open = Boolean(anchorEl);
    const handleClick = (event: React.MouseEvent<HTMLElement>) => {
        setAnchorEl(event.currentTarget);
    };
    const handleClose = () => {
        setAnchorEl(null);
    };

    if (hidden){
        return <></>
    }

    return(<>
        <Button
            id={`menu-button-${id}`}
            aria-controls={'demo-customized-menu'}
            aria-haspopup="true"
            aria-expanded={open ? 'true' : undefined}
            onClick={handleClick}

            endIcon={!hiddenIcon && <Icon sx={{m: 0}}>{open ? arrowUp : arrowDown}</Icon>}
            {...buttonProps}
        >
            {buttonLabel}
        </Button>
        <Menu
            id={`menu-button-${id}`}
            MenuListProps={{
                'aria-labelledby': 'demo-customized-button',
            }}
            anchorEl={anchorEl}
            open={open}
            onClose={handleClose}

            elevation={3}
            anchorOrigin={{
                vertical: 'bottom',
                horizontal: 'right',
            }}
            transformOrigin={{
                vertical: 'top',
                horizontal: 'right',
            }}

            {...menuProps}
        >
            {children}
        </Menu>
    </>)
}

type MenuActionButtonProps = MenuButtonProps & {
    actions: any[];
    entity?: any;
}
export const MenuActionButton = (props:MenuActionButtonProps) => {
    const {
        entity,
        actions,
        children,
        ...other
    } = props;

    return <MenuButton {...other}>
        {actions.map((action:any, index:number) =>
            !(typeof action.hidden === 'function' ? action.hidden(entity) : action.hidden)
            && (!action?.linkTo && !action?.clickShowUpdateDialog)
            && <div key={`action-${index}`} title={ typeof action.title == 'function' ?action.title?.(entity) :action.title}>
                <MenuItem onClick={()=>
                    entity?.id
                        ? action?.onClick?.(entity?.id, entity)
                        : action?.onClick?.(entity)
                } disabled={typeof action?.disabled === 'function' ? action?.disabled(entity) : action?.disabled}>
                    {action.icon && <Icon>{action.icon}</Icon>}{action.label}
                </MenuItem>
            </div>
        )}
        {children}
    </MenuButton>
}
export default MenuButton;