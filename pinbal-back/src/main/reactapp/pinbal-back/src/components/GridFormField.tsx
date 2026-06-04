import { Breakpoint, Button, Grid, GridSize, Icon, Typography } from '@mui/material';
import { FormField, FormFieldProps, useFormContext } from 'reactlib';
type ResponsiveStyleValue<T> = T | Array<T | null> | { [key in Breakpoint]?: T | null };

type GridFormFieldProps = FormFieldProps & {
    size: ResponsiveStyleValue<GridSize>;
};

export const GridButton = (props: any) => {
    const { title, size, children, hidden, ...other } = props;

    return (
        <Grid size={size} title={title} hidden={hidden}>
            <Button
                variant="outlined"
                sx={{ borderRadius: '4px', width: '100%', height: '100%' }}
                style={{ margin: 0 }}
                {...other}
            >
                {children}
            </Button>
        </Grid>
    );
};

export const GridButtonField = (props: any) => {
    const { name, icon, label, hiddenLabel = false, ...other } = props;
    const { data, apiRef, fields } = useFormContext();

    return (
        <GridButton
            onClick={() => {
                apiRef?.current?.setFieldValue?.(name, !data?.[name]);
            }}
            variant={data?.[name] ? 'contained' : 'outlined'}
            title={fields?.find?.((item) => item?.name === name)?.label || ''}
            {...other}
        >
            <Icon sx={{ m: 0, mr: hiddenLabel ? 0 : 1 }}>{icon}</Icon>
            {!hiddenLabel && (
                <Typography variant="subtitle2" sx={{ textTransform: 'none' }}>
                    {fields?.find?.((item) => item?.name === name)?.label || label || ''}
                </Typography>
            )}
        </GridButton>
    );
};

const GridFormField: React.FC<GridFormFieldProps> = (props) => {
    const { size } = props;

    return (
        <Grid size={size}>
            <FormField {...props} />
        </Grid>
    );
};
export default GridFormField;
