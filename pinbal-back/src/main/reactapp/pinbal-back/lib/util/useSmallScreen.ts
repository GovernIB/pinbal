import { useTheme } from '@mui/material/styles';
import useMediaQuery from '@mui/material/useMediaQuery';

const SMALL_SCREEN_LIMIT = 'md';
const SMALL_HEADER_LIMIT = 'sm';

export const useSmallScreen = () => {
    const theme = useTheme();
    return useMediaQuery(theme.breakpoints.down(SMALL_SCREEN_LIMIT));
};

export const useSmallHeader = () => {
    const theme = useTheme();
    return useMediaQuery(theme.breakpoints.down(SMALL_HEADER_LIMIT));
};

export default useSmallScreen;
