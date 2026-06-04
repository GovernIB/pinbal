import { Box, Card, CardContent, CardHeader, Grid, Typography } from '@mui/material';
import IconButton from '@mui/material/IconButton';
import Icon from '@mui/material/Icon';

const iconButton = {
    p: 0.5,
    borderRadius: '5px',
    maxWidth: 'max-content',
    border: '1px solid grey',
};

const CardHead = (props: any) => {
    const { icon, children, componentProps, ...other } = props;
    return (
        <CardHeader
            title={
                <Box display={'flex'} alignItems={'center'} {...componentProps}>
                    {icon && <Icon>{icon}</Icon>}
                    {children}
                </Box>
            }
            {...other}
        />
    );
};

export const CardButton = (props: any) => {
    const { text, icon, onClick, flex, buttonProps, hidden } = props;

    if (hidden) {
        return <></>;
    }

    return (
        <Grid size={{ xs: flex ?? 12 }} display={'flex'} justifyContent={'end'}>
            <IconButton sx={{ ...iconButton, ...buttonProps }} title={text} onClick={onClick}>
                <Typography
                    sx={{ display: 'flex', alignItems: 'center' }}
                    variant={'caption'}
                    color={'textPrimary'}
                >
                    {icon && <Icon fontSize={'inherit'}>{icon}</Icon>}
                    {text}
                </Typography>
            </IconButton>
        </Grid>
    );
};

const isEmpty = (value: any) => {
    return (
        !value ||
        value?.length === 0 ||
        value?.trim?.() === '' ||
        value?.every?.((item: any) => isEmpty(item))
    );
};

export const DetailCard = (props: any) => {
    const {
        icon,
        title,
        header,
        children,
        size = 12,
        hidden,
        cardProps = {},
        headerProps = {},
        variant = 'overline',
        ...other
    } = props;

    if (hidden) {
        return <></>;
    }

    return (
        <Grid size={size}>
            <Card sx={cardProps}>
                {(title || header) && (
                    <CardHead
                        icon={icon}
                        className={'detail'}
                        sx={{ py: 0, px: 2, ...headerProps }}
                    >
                        {title && (
                            <Typography mt={0.5} variant={variant}>
                                {title}
                            </Typography>
                        )}
                        {header}
                    </CardHead>
                )}

                <CardContent sx={{ p: '0 !important' }}>
                    <Grid container {...other}>
                        {children}
                    </Grid>
                </CardContent>
            </Card>
        </Grid>
    );
};

export const DetailCardContent = (props: any) => {
    const {
        title,
        children,
        isObject,
        size = 12,
        titleSize = 12,
        textSize = 12,
        componentTitleProps,
        componentTextProps,
        hidden,
        ...other
    } = props;

    if (hidden) {
        return <></>;
    }

    return (
        <Grid
            size={size}
            container
            direction={'row'}
            {...other}
            sx={{
                p: 1,
                borderLeft: '1px solid',
                borderTop: '1px solid',
                ...(other?.sx ?? {}),
                borderColor: other?.sx?.borderColor || 'divider',
            }}
        >
            <Grid size={titleSize}>
                <Typography color={'primary'} sx={{ fontWeight: 600, ...componentTitleProps }}>
                    {title}
                </Typography>
            </Grid>
            <Grid size={textSize}>
                {isEmpty(children) ? (
                    ' - '
                ) : isObject ? (
                    children
                ) : (
                    <Typography color="textSecondary" sx={componentTextProps} component="div">
                        {children}
                    </Typography>
                )}
            </Grid>
        </Grid>
    );
};

export const CardData = (props: any) => {
    const {
        icon,
        title,
        header,
        children,
        xs,
        hidden,
        hiddenIfEmpty,
        buttons,
        cardProps,
        headerProps = {},
        variant = 'h4',
        ...other
    } = props;

    if (hidden || (hiddenIfEmpty && isEmpty(children))) {
        return <></>;
    }

    return (
        <Grid size={{ xs: xs ?? 12 }}>
            <Card sx={cardProps}>
                {(title || header) && (
                    <CardHead icon={icon} sx={headerProps}>
                        {title && (
                            <Typography mt={0.5} variant={variant}>
                                {title}
                            </Typography>
                        )}
                        {header}
                    </CardHead>
                )}

                <CardContent hidden={!children}>
                    <Grid container columnSpacing={1} rowSpacing={1} size={{ xs: 12 }} {...other}>
                        {children}
                        {buttons?.map((button: any) => (
                            <CardButton key={button?.text} {...button} />
                        ))}
                    </Grid>
                </CardContent>
            </Card>
        </Grid>
    );
};

export const CardPage = (props: any) => {
    const { icon, title, header, headerProps, children, ...other } = props;
    return (
        <Card
            sx={{
                height: '100%',
                display: 'flex',
                flexDirection: 'column',
            }}
        >
            {(title || header) && (
                <CardHead icon={icon} sx={headerProps} {...other}>
                    {title && (
                        <Typography mt={0.5} variant={'h4'}>
                            {title}
                        </Typography>
                    )}
                    {header}
                </CardHead>
            )}

            <CardContent sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
                {children}
            </CardContent>
        </Card>
    );
};

export const ContenidoData = (props: any) => {
    const {
        title,
        titleXs,
        children,
        textXs,
        xs,
        componentTitleProps,
        componentTextProps,
        hidden,
        hiddenIfEmpty,
        ...other
    } = props;

    if (hidden || (hiddenIfEmpty && isEmpty(children))) {
        return <></>;
    }

    return (
        <Grid container direction={'row'} columnSpacing={1} size={{ xs: xs ?? 12 }} {...other}>
            <Grid size={{ xs: titleXs ?? 4 }}>
                <Typography sx={{ fontWeight: 600, ...componentTitleProps }}>{title}:</Typography>
            </Grid>
            <Grid size={{ xs: textXs ?? 8 }}>
                <Typography
                    color="textSecondary"
                    sx={{ fontWeight: '400', ...componentTextProps }}
                    component="span"
                >
                    {children}
                </Typography>
            </Grid>
        </Grid>
    );
};
