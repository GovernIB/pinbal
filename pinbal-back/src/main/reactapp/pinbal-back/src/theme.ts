import { createTheme, alpha } from '@mui/material/styles';
import type {} from '@mui/x-tree-view/themeAugmentation';

// Estenem les interfícies de MUI per admetre el nou color
declare module '@mui/material/styles' {
  interface Palette {
    customBackground: string;
    greyBackground: string;
  }
  interface PaletteOptions {
    customBackground?: string;
    greyBackground?: string;
  }
}

const theme = createTheme({
    colorSchemes: {
        light: {
            palette: {
                mode: 'light',
                primary: { main: '#497e3a' },
                customBackground: '#f5f5f5',
                greyBackground: '#f5f5f5',
            },
        },
        dark: {
            palette: {
                mode: 'dark',
                primary: { main: '#86e56c' },
                customBackground: '#121212',
                greyBackground: '#222222'
            },
        },
    },
    components: {
        MuiTreeItem: {
            styleOverrides: {
                content: ({ theme }) => ({
                    '&:hover': {
                        backgroundColor: alpha(theme.palette.primary.main, 0.10),
                    },
                    '&.Mui-selected': {
                        backgroundColor: alpha(theme.palette.primary.main, 0.15),
                        '&:hover': {
                            backgroundColor: alpha(theme.palette.primary.main, 0.20),
                        },
                        '&.Mui-focused': {
                            backgroundColor: alpha(theme.palette.primary.main, 0.25),
                        },
                    },
                }),
            },
        },
    },
});

export default theme;
