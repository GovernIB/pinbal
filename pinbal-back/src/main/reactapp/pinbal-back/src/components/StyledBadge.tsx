import { styled } from '@mui/material/styles';
import Badge from '@mui/material/Badge';

type ThemeColorKey = 'primary' | 'secondary' | 'error' | 'warning' | 'info' | 'success';

interface StyledBadgeProps {
    badgecolor?: ThemeColorKey | string;
    textcolor?: ThemeColorKey | string;
}

export const StyledBadge = styled(Badge)<StyledBadgeProps>(({ theme, badgecolor = 'primary', textcolor = 'white' }) => {
    const resolveColor = (value: string | undefined) =>
        theme.palette[value as ThemeColorKey]?.main || value;

    return {
        paddingLeft: theme.spacing(1),
        paddingRight: theme.spacing(1),
        '& .MuiBadge-badge': {
            backgroundColor: resolveColor(badgecolor),
            color: resolveColor(textcolor),
        },
    };
});