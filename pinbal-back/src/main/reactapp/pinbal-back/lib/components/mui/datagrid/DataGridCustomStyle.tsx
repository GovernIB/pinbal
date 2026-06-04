import { alpha, styled } from '@mui/material/styles';
import { DataGridPro as MuiDataGrid, gridClasses } from '@mui/x-data-grid-pro';

const ODD_OPACITY = 0.2;

const DataGridCustomStyle = styled(MuiDataGrid)((props) => {
    const { theme, semiBordered } = props as any;
    const customStyle = {
        [`& .${gridClasses.row}.even`]: {
            backgroundColor: theme.palette.grey[200],
            '&:hover': {
                backgroundColor: alpha(theme.palette.primary.main, ODD_OPACITY),
                '@media (hover: none)': {
                    backgroundColor: 'transparent',
                },
            },
            '&.Mui-selected': {
                backgroundColor: alpha(
                    theme.palette.primary.main,
                    ODD_OPACITY + theme.palette.action.selectedOpacity
                ),
                '&:hover': {
                    backgroundColor: alpha(
                        theme.palette.primary.main,
                        ODD_OPACITY +
                            theme.palette.action.selectedOpacity +
                            theme.palette.action.hoverOpacity
                    ),
                    // Reset on touch devices, it doesn't add specificity
                    '@media (hover: none)': {
                        backgroundColor: alpha(
                            theme.palette.primary.main,
                            ODD_OPACITY + theme.palette.action.selectedOpacity
                        ),
                    },
                },
            },
        },
        [`& .${gridClasses.cell}:focus`]: {
            outline: 'none !important',
        },
        [`& .${gridClasses.cell}:focus-within`]: {
            outline: 'none !important',
        },
        [`& .${gridClasses.columnHeader}:focus`]: {
            outline: 'none !important',
        },
        [`& .${gridClasses.columnHeader}:focus-within`]: {
            outline: 'none !important',
        },
        // [`& .${gridClasses.columnHeaderTitleContainerContent}`]: {
        //     flexGrow: 1
        // },
    };
    if (semiBordered) {
        customStyle['&.MuiDataGrid-root'] = { border: 'none' } as any;
        customStyle['& .MuiDataGrid-columnSeparator'] = { display: 'none' } as any;
        customStyle[`& .${gridClasses.columnHeaders}`] = {
            borderBottom: '1px solid ' + theme.palette.divider,
        } as any;
    }
    return customStyle;
});

export default DataGridCustomStyle;
