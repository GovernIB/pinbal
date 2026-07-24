import React from 'react';
import { useTranslation } from 'react-i18next';
import Grid from '@mui/material/Grid';
import Box from '@mui/material/Box';
import Paper from '@mui/material/Paper';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemButton from '@mui/material/ListItemButton';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import Typography from '@mui/material/Typography';
import {
    MuiForm,
    FormField,
    TextHighlight,
    useBaseAppContext,
    useResourceApiService,
    FormApiRef,
    useFormContext,
} from 'reactlib';

const fieldPropType = (typeCode?: string, validValues?: string[]) => {
    if (validValues != null && validValues.length > 0) {
        return 'search';
    }
    switch (typeCode) {
        case 'INT':
        case 'FLOAT':
            return 'number';
        case 'BOOL':
            return 'checkbox';
        default:
            return 'text';
    }
};

const PropsListItem: React.FC<{ item: any; highlight?: string }> = ({ item, highlight }) => {
    const { t } = useTranslation();
    const { patch: apiPatch } = useResourceApiService('configResource');
    const { temporalMessageShow } = useBaseAppContext();
    const { modified } = useFormContext();
    const [changedValue, setChangedValue] = React.useState<any>(undefined);
    const disabled = !item.editable;
    const password = item.typeCode === 'PASS' || item.typeCode === 'CREDENTIALS' ? true : undefined;
    const decimalScale = item.typeCode === 'INT' ? 0 : undefined;

    const handleFieldOnChange = (value: any) => setChangedValue(value);
    const handleSaveClick = () => {
        apiPatch(item.id, { data: { value: changedValue } })
            .then(() => {
                setChangedValue(undefined);
                temporalMessageShow(null, t('page.propietats.save.success'), 'success');
            })
            .catch((error: any) => temporalMessageShow(t('page.propietats.save.error'), error?.message, 'error'));
    };

    return (
        <Grid container spacing={2} sx={{ width: '100%' }}>
            <Grid size={6} sx={{ '& p.MuiTypography-root': { fontSize: '14px' } }}>
                <TextHighlight text={item.description ?? item.key} match={highlight} ignoreCase />
            </Grid>
            <Grid size={6}>
                <Box sx={{ display: 'flex', flexDirection: 'row', justifyContent: 'space-between', gap: 1 }}>
                    <FormField
                        name={item.key}
                        inline
                        password={password}
                        decimalScale={decimalScale}
                        disabled={disabled}
                        onChange={handleFieldOnChange}
                        componentProps={{ helperText: item.key }}
                    />
                    <Box sx={{ display: 'flex', justifyContent: 'flex-end', alignItems: 'flex-start' }}>
                        {modified && changedValue !== undefined && (
                            <IconButton color="primary" onClick={handleSaveClick}>
                                <Icon fontSize="small">save</Icon>
                            </IconButton>
                        )}
                    </Box>
                </Box>
            </Grid>
        </Grid>
    );
};

export const PropietatsProps: React.FC<{
    quickFilter?: string;
    group?: any;
    formApiRef?: FormApiRef;
}> = ({ quickFilter, group, formApiRef }) => {
    const { t } = useTranslation();
    const { isReady: apiIsReady, find: apiFind } = useResourceApiService('configResource');
    const [configs, setConfigs] = React.useState<any[]>();
    const [customFields, setCustomFields] = React.useState<any[]>();

    React.useEffect(() => {
        if (apiIsReady && group != null) {
            const args = {
                quickFilter,
                filter: "groupCode:'" + group.id + "'",
                sorts: ['position,asc'],
                unpaged: true,
            };
            apiFind(args).then((response) => {
                const rows = response.rows;
                setConfigs(rows);
                setCustomFields(
                    rows.map((c: any) => {
                        const type = fieldPropType(c.typeCode, c.validValues);
                        const options = c.validValues?.length
                            ? Object.fromEntries(c.validValues.map((v: string) => [v, v]))
                            : undefined;
                        return { name: c.key, type, label: '', value: c.value, options };
                    }),
                );
            });
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [apiIsReady, quickFilter, group]);

    if (group == null || customFields == null) {
        return null;
    }

    return (
        <Box sx={{ px: 3 }}>
            <Typography variant="h6" sx={{ mb: 1 }}>
                {group.description}
            </Typography>
            <List component={Paper}>
                {customFields.length ? (
                    <MuiForm
                        apiRef={formApiRef}
                        resourceName="configResource"
                        customFields={customFields}
                        hiddenToolbar
                        commonFieldComponentProps={{ size: 'small' }}
                    >
                        {configs?.map((c) => (
                            <ListItem key={c.key} disablePadding>
                                <ListItemButton disableRipple>
                                    <PropsListItem item={c} highlight={quickFilter} />
                                </ListItemButton>
                            </ListItem>
                        ))}
                    </MuiForm>
                ) : (
                    <Box sx={{ width: '100%', textAlign: 'center', px: 2, py: 4 }}>
                        <Icon fontSize="large" color="disabled">
                            block
                        </Icon>
                        <Typography variant="h5" color="text.secondary">
                            {t('page.propietats.empty')}
                        </Typography>
                    </Box>
                )}
            </List>
        </Box>
    );
};

export default PropietatsProps;
