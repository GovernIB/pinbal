import React from 'react';
import Drawer from '@mui/material/Drawer';
import List from '@mui/material/List';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import ListItemButton from '@mui/material/ListItemButton';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import Divider from '@mui/material/Divider';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import { styled, useTheme, Theme, CSSObject } from '@mui/material/styles';
import { useBaseAppContext } from '../BaseAppContext';
import { useSmallScreen, useSmallHeader } from '../../util/useSmallScreen';

export type MenuEntry = {
    id: string;
    title?: string;
    description?: string;
    to?: string;
    icon: string;
    children?: MenuEntry[];
    divider?: boolean;
};

export type MenuProps = {
    title?: string;
    entries?: MenuEntry[];
    level?: number;
    onTitleClose?: () => void;
    shrink?: boolean;
    iconClicked?: boolean;
    drawerWidth?: number;
    footerHeight?: number;
    compactPanelWidth?: number;
    submenuTitleHeight?: number;
};

type ListMenuContentProps = MenuProps & {
    onMenuItemClick?: (entry: MenuEntry) => void;
    onMenuItemMouseEnter?: (entry: MenuEntry) => void;
    onMenuItemMouseLeave?: (entry: MenuEntry) => void;
    boldPrimary?: boolean;
    hideChildren?: boolean;
};

type MenuItemProps = React.PropsWithChildren & {
    entry: MenuEntry;
    primary: string;
    to?: string;
    icon?: string;
    level?: number;
    selected?: boolean;
    shrink?: boolean;
    boldPrimary?: boolean;
    onMenuItemClick?: (entry: MenuEntry) => void;
    onMenuItemMouseEnter?: (entry: MenuEntry) => void;
    onMenuItemMouseLeave?: (entry: MenuEntry) => void;
};

type MenuTitleProps = {
    title: string;
    onClose?: () => void;
};

const openedMixin = (theme: Theme, width: number | string): CSSObject => ({
    width,
    transition: theme.transitions.create('width', {
        easing: theme.transitions.easing.sharp,
        duration: theme.transitions.duration.enteringScreen,
    }),
});

const drawerClosedWidthSmall = (theme: Theme) => `calc(${theme.spacing(6)} + 1px)`;
const drawerClosedWidthStandard = (theme: Theme) => `calc(${theme.spacing(7)} + 1px)`;

const closedMixin = (theme: Theme): CSSObject => ({
    transition: theme.transitions.create('width', {
        easing: theme.transitions.easing.sharp,
        duration: theme.transitions.duration.leavingScreen,
    }),
    width: drawerClosedWidthSmall(theme),
    [theme.breakpoints.up('sm')]: {
        width: drawerClosedWidthStandard(theme),
    },
});

const ShrinkableDrawer = styled(Drawer, {
    shouldForwardProp: (prop) => prop !== 'open' && prop !== 'width',
})(({ theme, open, ...otherProps }) => {
    const width = (otherProps as any)?.['width'];
    const shrinkableStyles: CSSObject = {
        ...(open && {
            ...openedMixin(theme, width),
            '& .MuiDrawer-paper': openedMixin(theme, width),
            overflowWrap: 'anywhere',
        }),
        ...(!open && {
            ...closedMixin(theme),
            '& .MuiDrawer-paper': closedMixin(theme),
        }),
    };
    return {
        flexShrink: 0,
        boxSizing: 'border-box',
        ...shrinkableStyles,
    };
});

const StyledList = styled(List)<{ component?: React.ElementType }>({
    '& .MuiListItemIcon-root': {
        minWidth: 0,
        marginRight: 16,
    },
    '& .MuiSvgIcon-root': {
        fontSize: 20,
    },
    paddingTop: 0,
    paddingBottom: '8px',
    overflow: 'auto',
    overflowX: 'hidden',
});

const COMPACT_PANEL_WIDTH = 250;
const SUBMENU_TITLE_HEIGHT = 48;

const isCurrentMenuEntryOrAnyChildrenSelected = (
    menuEntry: MenuEntry,
    locationPath: string
): boolean => {
    const anyChildSelected =
        menuEntry.children?.find((e) => isCurrentMenuEntryOrAnyChildrenSelected(e, locationPath)) !=
        null;
    if (menuEntry.to != null) {
        const menuEntryTo = locationPath.startsWith('/')
            ? menuEntry.to.startsWith('/')
                ? menuEntry.to
                : '/' + menuEntry.to
            : menuEntry.to;
        const selected = menuEntryTo === locationPath || locationPath.startsWith(menuEntryTo + '/');
        return selected || anyChildSelected;
    } else {
        return anyChildSelected;
    }
};

const menuItemIconClassName = 'menu-item-icon';

const MenuItem: React.FC<MenuItemProps> = (props) => {
    const {
        entry,
        primary,
        to,
        icon,
        level = 0,
        selected,
        shrink,
        boldPrimary = level === 0,
        onMenuItemClick,
        onMenuItemMouseEnter,
        onMenuItemMouseLeave,
        children,
    } = props;
    const { getLinkComponent } = useBaseAppContext();
    const [expanded, setExpanded] = React.useState<boolean>(selected ?? false);
    const itemButtonSx = {
        minHeight: 48,
        justifyContent: !shrink ? 'initial' : 'center',
        '& :before':
            level > 0 && !shrink
                ? {
                      content: '""',
                      display: 'block',
                      position: 'absolute',
                      zIndex: '100',
                      top: '0',
                      left: '40px',
                      height: '100%',
                      width: '2px',
                      opacity: '1',
                      background: selected ? 'hsl(210, 100%, 60%)' : 'hsl(215, 15%, 92%)',
                  }
                : undefined,
    };
    const itemIconSx = {
        minWidth: 0,
        ml: !shrink ? 1 : -1,
        mr: !shrink ? 1 : 'auto',
        justifyContent: 'center',
    };
    const itemTextSx = {
        opacity: !shrink ? 1 : 0,
        '& span': {
            fontSize: '14px',
            fontWeight: boldPrimary ? 'bold' : undefined,
        },
    };
    const handleMenuItemClick = () => {
        if (children != null) {
            setExpanded((expanded) => !expanded);
        } else {
            onMenuItemClick?.(entry);
        }
    };
    const expandedIconComponent = children != null ? (
            <Icon fontSize={'small'}>{expanded ? 'expand_less' : 'expand_more'}</Icon>
    ) : null;
    const iconComponent = icon ? (
        <ListItemIcon className={menuItemIconClassName} sx={itemIconSx}>
            <Icon fontSize={'small'}>{icon}</Icon>
        </ListItemIcon>
    ) : null;
    return (
        <>
            {(!shrink || !children) && (
                <ListItemButton
                    title={shrink ? primary : undefined}
                    selected={selected}
                    to={children == null ? to : undefined}
                    component={
                        children == null ? (to != null ? getLinkComponent() : undefined) : undefined
                    }
                    onClick={handleMenuItemClick}
                    onMouseEnter={() => onMenuItemMouseEnter?.(entry)}
                    onMouseLeave={() => onMenuItemMouseLeave?.(entry)}
                    sx={itemButtonSx}
                    style={{
                        paddingLeft: shrink ? '40px' : 24 + 16 * level + (level > 0 ? 8 : 0) + 'px',
                    }}>
                    {iconComponent}
                    <ListItemText primary={primary} sx={itemTextSx} />
                    {expandedIconComponent}
                </ListItemButton>
            )}
            {(shrink || expanded) && children}
        </>
    );
};

const ListMenuContent: React.FC<ListMenuContentProps> = (props) => {
    const {
        entries,
        level,
        shrink,
        onMenuItemClick,
        onMenuItemMouseEnter,
        onMenuItemMouseLeave,
        boldPrimary,
        hideChildren,
    } = props;
    const { useLocationPath } = useBaseAppContext();
    const locationPath = useLocationPath();
    return (
        <StyledList>
            {entries?.map((item, index) => {
                const selected = isCurrentMenuEntryOrAnyChildrenSelected(item, locationPath);
                const entryComponent = item.divider ? (
                    <Divider key={index} />
                ) : (
                    <MenuItem
                        key={index}
                        entry={item}
                        primary={item.title ?? ''}
                        to={item.to}
                        icon={item.icon}
                        level={level}
                        selected={selected}
                        shrink={shrink}
                        boldPrimary={boldPrimary}
                        onMenuItemClick={onMenuItemClick}
                        onMenuItemMouseEnter={onMenuItemMouseEnter}
                        onMenuItemMouseLeave={onMenuItemMouseLeave}>
                        {item.children?.length && !hideChildren ? (
                            <Box>
                                <ListMenuContent
                                    entries={item.children}
                                    level={(level ?? 0) + 1}
                                    shrink={shrink}
                                    boldPrimary={boldPrimary}
                                    onMenuItemClick={onMenuItemClick}
                                />
                            </Box>
                        ) : null}
                    </MenuItem>
                );
                return entryComponent;
            })}
        </StyledList>
    );
};

const MenuTitle: React.FC<MenuTitleProps> = (props) => {
    const { title, onClose } = props;
    const theme = useTheme();
    const handleButtonClick = () => onClose?.();
    return (
        <Box>
            <ListItemButton sx={{ backgroundColor: theme.palette.grey[200] }}>
                <ListItemIcon sx={{ minWidth: '40px' }}>
                    <IconButton size="small" onClick={handleButtonClick}>
                        <Icon fontSize={'small'}>clear</Icon>
                    </IconButton>
                </ListItemIcon>
                <ListItemText primary={title} sx={{ '& span': { fontWeight: 'bold' } }} />
            </ListItemButton>
            <Divider />
        </Box>
    );
};

export const Menu: React.FC<MenuProps> = (props) => {
    const {
        title,
        entries,
        onTitleClose,
        shrink,
        iconClicked,
        drawerWidth = 240,
        footerHeight,
        compactPanelWidth = COMPACT_PANEL_WIDTH,
        submenuTitleHeight = SUBMENU_TITLE_HEIGHT,
    } = props;
    const smallScreen = useSmallScreen();
    const smallHeader = useSmallHeader();
    const theme = useTheme();
    const [open, setOpen] = React.useState<boolean>(false);
    const [compactPanelEntryId, setCompactPanelEntryId] = React.useState<string | null>(null);
    const closeTimeoutRef = React.useRef<ReturnType<typeof setTimeout> | null>(null);
    const compactMenuContainerRef = React.useRef<HTMLDivElement | null>(null);
    const compactMode = !smallScreen && !!shrink;
    // El menú compacte no es mostra quan la mida de pantalla és petita
    const compactMenuWidth = drawerClosedWidthStandard(theme);
    const topMargin = smallHeader ? theme.spacing(7) : theme.spacing(8);
    const compactPanelEntry = compactMode
        ? entries?.find((entry) => entry.id === compactPanelEntryId && entry.children?.length)
        : undefined;

    React.useEffect(() => {
        setOpen((o) => !o);
    }, [iconClicked]);
    React.useEffect(() => {
        setOpen(false);
    }, [smallScreen]);
    React.useEffect(() => {
        if (!compactMode || !compactPanelEntry) {
            return;
        }
        // Si hi ha un panell flotant obert, qualsevol clic fora del panell i fora
        // de la columna d'icones el tanca.
        const handlePointerDownOutside = (event: MouseEvent) => {
            const target = event.target as Node | null;
            if (target == null || !compactMenuContainerRef.current?.contains(target)) {
                setCompactPanelEntryId(null);
            }
        };
        document.addEventListener('mousedown', handlePointerDownOutside);
        return () => {
            document.removeEventListener('mousedown', handlePointerDownOutside);
        };
    }, [compactMode, compactPanelEntry]);
    const handleMenuItemClick = () => {
        setOpen(false);
        setCompactPanelEntryId(null);
    };
    const handleCompactEntryClick = (entry: MenuEntry) => {
        if (entry.children?.length) {
            setCompactPanelEntryId((current) => (current === entry.id ? null : entry.id));
        } else {
            setCompactPanelEntryId(null);
        }
    };
    const resetCloseTimeout = () => {
        if (closeTimeoutRef.current) {
            clearTimeout(closeTimeoutRef.current);
            closeTimeoutRef.current = null;
        }
    }
    const handleCompactEntryMouseEnter = (entry: MenuEntry) => {
        if (!compactMode || !window.matchMedia('(hover: hover)').matches) {
            return;
        }
        resetCloseTimeout();
        if (entry.children?.length) {
            setCompactPanelEntryId(entry.id);
        } else {
            setCompactPanelEntryId(null);
        }
    };
    const handleMouseLeave = () => {
        if (!compactMode || !window.matchMedia('(hover: hover)').matches) {
            return;
        }
        closeTimeoutRef.current = setTimeout(() => {
            setCompactPanelEntryId(null);
        }, 150);
    };
    const drawerContent = (
        <>
            <Box sx={{ mt: topMargin }} />
            {title && <MenuTitle title={title} onClose={onTitleClose} />}
            {compactMode ? (
                <div ref={compactMenuContainerRef}>
                    <ListMenuContent
                        entries={entries}
                        hideChildren={true}
                        shrink={true}
                        onMenuItemClick={handleCompactEntryClick}
                        onMenuItemMouseEnter={(entry) => handleCompactEntryMouseEnter(entry)}
                        onMenuItemMouseLeave={handleMouseLeave}
                    />
                    {compactPanelEntry ? (
                        <Box
                            onMouseEnter={resetCloseTimeout}
                            onMouseLeave={handleMouseLeave}
                            sx={(theme) => ({
                                position: 'fixed',
                                top: topMargin,
                                bottom: footerHeight ? `${footerHeight}px` : null,
                                left: compactMenuWidth,
                                width: compactPanelWidth,
                                borderRight: `1px solid ${theme.palette.divider}`,
                                backgroundColor: theme.palette.background.paper,
                                color:  theme.palette.text.primary,
                                overflowY: 'auto',
                                zIndex: theme.zIndex.drawer + 1,
                            })}>
                            <Box
                                sx={(theme) => ({
                                    px: 2,
                                    py: 1.7,
                                    backgroundColor: theme.palette.background.paper,
                                    borderBottom: `1px solid ${theme.palette.divider}`,
                                    minHeight: submenuTitleHeight,
                                })}>
                                <Typography
                                    variant="subtitle1"
                                    sx={() => ({
                                        fontWeight: 700,
                                        color: theme.palette.primary.light,
                                    })}>
                                    {compactPanelEntry.title}
                                </Typography>
                                {compactPanelEntry.description ? (
                                    <Typography
                                        variant="body2"
                                        sx={{
                                            mt: 0.25,
                                            color: 'text.secondary',
                                            fontSize: '0.6rem',
                                        }}>
                                        {compactPanelEntry.description}
                                    </Typography>
                                ) : null}
                            </Box>
                            <Box
                                sx={{
                                    [`& .${menuItemIconClassName}`]: { ml: -1 },
                                    overflowWrap: 'anywhere',
                                }}
                            >
                                <ListMenuContent
                                    entries={compactPanelEntry.children}
                                    level={0}
                                    shrink={false}
                                    boldPrimary={false}
                                    onMenuItemClick={handleMenuItemClick}
                                />
                            </Box>
                        </Box>
                    ) : null}
                </div>
            ) : (
                <ListMenuContent
                    entries={entries}
                    shrink={false}
                    onMenuItemClick={handleMenuItemClick}
                />
            )}
            {footerHeight && <Box sx={{ mb: footerHeight + 'px' }} />}
        </>
    );
    return !smallScreen ? (
        <ShrinkableDrawer variant={'permanent'} open={!shrink} {...{ width: drawerWidth }}>
            {drawerContent}
        </ShrinkableDrawer>
    ) : (
        <Drawer
            open={open}
            onClose={() => setOpen(false)}
            sx={{
                display: { sm: 'block', md: 'none' },
                flexShrink: 0,
                '& .MuiDrawer-paper': {
                    width: drawerWidth,
                    boxSizing: 'border-box',
                },
            }}>
            {drawerContent}
        </Drawer>
    );
};

export default Menu;
