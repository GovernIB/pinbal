import React from 'react';
import MuiToolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import { red } from '@mui/material/colors';
import { useTheme } from '@mui/material/styles';
import {
    ReactElementWithPosition,
    joinReactElementsWithReactElementsWithPositions,
} from '../../util/reactNodePosition';

export type ToolbarProps = React.PropsWithChildren & {
    title?: React.ReactNode;
    subtitle?: string;
    lovMode?: boolean;
    upperToolbar?: boolean;
    error?: boolean;
    noFlexGrow?: true;
    sx?: any;
    elementsWithPositions?: ReactElementWithPosition[];
};

export const Toolbar: React.FC<ToolbarProps> = (props) => {
    const {
        title,
        subtitle,
        elementsWithPositions,
        upperToolbar,
        error,
        noFlexGrow,
        sx: sxProp,
        children,
    } = props;
    const theme = useTheme();
    const titleElement = subtitle ? (
        <div style={{ minWidth: 0, width: '100%' }}>
            <Typography variant="h6">{title}</Typography>
            <Typography
                variant="body2"
                sx={{
                    color: theme.palette.text.secondary,
                    width: '100%',
                    overflow: 'hidden',
                    whiteSpace: 'nowrap',
                    textOverflow: 'ellipsis',
                }}>
                {subtitle}
            </Typography>
        </div>
    ) : (
        <Typography variant="h6">{title}</Typography>
    );
    const flexGrow = !noFlexGrow ? <div style={{ flexGrow: 1 }} /> : <></>;
    const upperToolbarBgColor =
        theme.palette.mode === 'light' ? theme.palette.grey[200] : theme.palette.grey[900];
    const toolbarElements: React.ReactElement[] =
        title != null ? [titleElement, flexGrow] : [flexGrow];
    return (
        <>
            <MuiToolbar
                disableGutters
                sx={{
                    width: '100%',
                    display: 'flex',
                    px: upperToolbar ? 2 : 0,
                    ml: 0,
                    mr: 0,
                    mt: 0,
                    backgroundColor: error
                        ? red[100]
                        : upperToolbar
                          ? upperToolbarBgColor
                          : undefined,
                    ...sxProp,
                }}>
                {joinReactElementsWithReactElementsWithPositions(
                    toolbarElements,
                    elementsWithPositions,
                    true
                )}
            </MuiToolbar>
            {children}
        </>
    );
};

export default Toolbar;
