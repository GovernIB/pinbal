import { useFormContext } from 'reactlib';
import { FormFieldDataActionType } from '../../lib/components/form/FormContext';
import { Box, Grid, Switch, Tooltip, Typography } from '@mui/material';

type PermissionGridSwitchProps = {
    name: string;
    label: string;
    tooltip?: string;
    icon?: React.ReactNode;
    size?: number;
    onChange?: (newValue: boolean) => void;
};

const PermissionGridSwitch: React.FC<PermissionGridSwitchProps> = (props) => {
    const { name, label, tooltip, icon, size = 12, onChange } = props;
    const { data, dataDispatchAction, fields } = useFormContext();
    const field = fields?.find((f) => f.name === name);
    const value = data?.[name] ?? false;

    const changeValue = (event: React.ChangeEvent<HTMLInputElement>) => {
        const newValue = event.target.checked;

        dataDispatchAction({
            type: FormFieldDataActionType.FIELD_CHANGE,
            payload: { fieldName: name, value: newValue, field },
        });
        if (onChange) {
            onChange(newValue);
        }
    };

    return (
        <Tooltip title={tooltip} arrow placement="left">
            <Grid size={size}>
                <Box
                    sx={{
                        display: 'flex',
                        justifyContent: 'space-between',
                        width: '100%',
                        alignItems: 'center',
                        px: 1,
                    }}
                >
                    <Box sx={{ display: 'flex', gap: 2 }}>
                        {icon}
                        <Typography>{label}</Typography>
                    </Box>
                    <Switch onChange={changeValue} checked={value} />
                </Box>
            </Grid>
        </Tooltip>
    );
};

export default PermissionGridSwitch;
