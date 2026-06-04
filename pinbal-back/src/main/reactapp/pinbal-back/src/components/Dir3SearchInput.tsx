import React from 'react';
import Grid from '@mui/material/Grid';
import Button from '@mui/material/Button';
import Icon from '@mui/material/Icon';
import TextField from '@mui/material/TextField';
import {
    MuiDataGridDialog,
    MuiFilter,
    useFormContext,
    useFilterContext,
    springFilterBuilder as filterBuilder,
    useMuiDataGridDialogApiRef,
} from 'reactlib';
import GridFormField from './GridFormField';
import { useTranslation } from 'react-i18next';

const Dir3SearchFilterContent: React.FC = () => {
    const { data } = useFormContext();
    const { apiRef: filterApiRef } = useFilterContext();
    const { t } = useTranslation();

    return (
        <Grid container spacing={2} sx={{ mt: 1 }}>
            <GridFormField size={3} name="codi" />
            <GridFormField size={6} name="denominacio" />
            <GridFormField size={3} name="nivellAdministracio" />
            <GridFormField size={3} name="comunitatAutonoma" autocomplete />
            <GridFormField
                size={3}
                name="provincia"
                autocomplete
                requestParams={
                    data?.comunitatAutonoma != null
                        ? { comunitatAutonoma: data?.comunitatAutonoma }
                        : undefined
                }
            />
            <GridFormField
                size={3}
                name="municipi"
                autocomplete
                requestParams={data?.provincia != null ? { provincia: data?.provincia } : undefined}
            />
            <Grid size={1.5}>
                <Button
                    variant="outlined"
                    onClick={() => filterApiRef.current?.clear()}
                    startIcon={<Icon>filter_alt_off</Icon>}
                    fullWidth
                >
                    {t('component.Dir3SearchInput.dialog.netejar')}
                </Button>
            </Grid>
            <Grid size={1.5}>
                <Button
                    variant="contained"
                    onClick={() => filterApiRef.current?.filter()}
                    startIcon={<Icon>filter_alt</Icon>}
                    fullWidth
                >
                    {t('comu.filtrar')}
                </Button>
            </Grid>
        </Grid>
    );
};

export const Dir3SearchInput: React.FC<{ name: string; required?: true }> = (props) => {
    const { name, required } = props;
    const [dir3Name, setDir3Name] = React.useState<string>();
    const { apiRef: formApiRef } = useFormContext();
    const gridDialogApiRef = useMuiDataGridDialogApiRef();
    const { t } = useTranslation();

    const dir3DialogColumns = [
        {
            field: 'codi',
            flex: 2,
        },
        {
            field: 'denominacio',
            flex: 6,
        },
        {
            field: 'cif',
            flex: 2,
        },
        {
            field: 'sir',
            flex: 1,
            renderCell: (params: any) => {
                return params.value ? (
                    <Icon color="success">check_circle</Icon>
                ) : (
                    <Icon color="disabled">cancel</Icon>
                );
            },
        },
        {
            field: 'selectable',
            flex: 2,
            renderCell: (params: any) => {
                return params.value ? (
                    <Icon color="success">check_circle</Icon>
                ) : (
                    <>
                        <Icon color="warning" sx={{ mr: 1 }}>
                            warning
                        </Icon>
                        {params.row.noCif && t('component.Dir3SearchInput.dialog.noCif')}
                        {params.row.noSir && t('component.Dir3SearchInput.dialog.noSir')}
                        {params.row.viaValib && t('component.Dir3SearchInput.dialog.viaValib')}
                    </>
                );
            },
        },
    ];

    const springFilterBuilder = (data: any) => {
        return filterBuilder.and(
            filterBuilder.like('codi', data?.codi),
            filterBuilder.like('denominacio', data?.denominacio),
            filterBuilder.eq('nivellAdministracio', `'${data?.nivellAdministracio}'`),
            filterBuilder.eq('comunitatAutonoma', data?.comunitatAutonoma),
            filterBuilder.eq('provincia', data?.provincia),
            filterBuilder.eq('municipi', data?.municipi)
        );
    };

    const handleSearchClick = () => {
        gridDialogApiRef.current
            ?.show()
            .then((value) => {
                formApiRef.current?.setFieldValue(name, value.codi);
                setDir3Name(value.denominacio);
            })
            .catch(() => {});
    };

    return (
        <>
            <Grid container spacing={2}>
                <GridFormField size={2} name={name} required={required} readOnly />
                <Grid size={9}>
                    <TextField value={dir3Name ?? ''} disabled size="small" fullWidth />
                </Grid>
                <Grid size={1}>
                    <Button
                        variant="outlined"
                        startIcon={<Icon>search</Icon>}
                        fullWidth
                        onClick={handleSearchClick}
                    >
                        {t('component.Dir3SearchInput.search')}
                    </Button>
                </Grid>
            </Grid>
            <MuiDataGridDialog
                resourceName="dir3Resource"
                title={t('component.Dir3SearchInput.dialog.title')}
                columns={dir3DialogColumns}
                dataGridComponentProps={{
                    readOnly: true,
                    isRowSelectable: (params: any) => params.row.selectable,
                    getRowClassName: (params: any) =>
                        params.row.selectable ? 'selectable-row' : 'no-hover',
                    sx: {
                        '& .selectable-row': {
                            cursor: 'pointer',
                        },
                        '& .no-hover:hover': {
                            backgroundColor: 'transparent',
                        },
                    },
                    autoFindDisabled: true,
                    toolbarHide: true,
                    toolbarAdditionalRow: (
                        <MuiFilter
                            resourceName="dir3Resource"
                            code="FILTER_DIR3"
                            springFilterBuilder={springFilterBuilder}
                            buttonControlled
                            validationActive
                            commonFieldComponentProps={{ size: 'small' }}
                            componentProps={{ sx: { mb: 2 } }}
                        >
                            <Dir3SearchFilterContent />
                        </MuiFilter>
                    ),
                    height: 500,
                }}
                dialogComponentProps={{ fullWidth: true, maxWidth: 'lg' }}
                apiRef={gridDialogApiRef}
                onRowClickEnabled={(row) => row.selectable}
            />
        </>
    );
};

export default Dir3SearchInput;
