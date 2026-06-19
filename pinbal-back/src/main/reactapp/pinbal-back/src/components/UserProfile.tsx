import React from 'react';
import { useTranslation } from 'react-i18next';
import MenuItem from '@mui/material/MenuItem';
import ListItemText from '@mui/material/ListItemText';
import ListItemIcon from '@mui/material/ListItemIcon';
import Icon from '@mui/material/Icon';
import Grid from '@mui/material/Grid';
import Divider from '@mui/material/Divider';
import TextField from '@mui/material/TextField';
import InputAdornment from '@mui/material/InputAdornment';
import Typography from '@mui/material/Typography';
import ToggleButton from '@mui/material/ToggleButton';
import ToggleButtonGroup from '@mui/material/ToggleButtonGroup';
import { useColorScheme } from '@mui/material/styles';
import 'dayjs/locale/ca';
import 'dayjs/locale/es';
import { MuiFormDialog, MuiDataFormDialogApi, useBaseAppContext, useAuthContext } from 'reactlib';
import { usePinbalContext } from './PinbalContext.ts';
import GridFormField from './GridFormField';

const selectorLabelSx = {
    display: 'block',
    ml: 1.75,
    mb: 0.75,
    fontSize: '0.75rem',
    lineHeight: 1,
    color: 'text.secondary',
};

// Adornament d'icona a la dreta del camp (es passa via slotProps tal com espera FormFieldText).
// spacing afegeix marge dret per deixar lloc a la fletxa del desplegable als camps de tipus select.
const endIcon = (name: string, spacing?: boolean) => ({
    slotProps: {
        input: {
            endAdornment: (
                <InputAdornment position="end" sx={spacing ? { mr: 2 } : undefined}>
                    <Icon fontSize="small">{name}</Icon>
                </InputAdornment>
            ),
        },
    },
});

export const UserProfileMenu: React.FC<{
    formDialogApiRef: React.RefObject<MuiDataFormDialogApi | null>;
}> = (props) => {
    const { formDialogApiRef } = props;
    const { t } = useTranslation();
    const { getUserId: authGetUserId } = useAuthContext();
    const showUserProfileDialog = () => {
        formDialogApiRef.current?.show(authGetUserId()).catch(() => null);
    };

    return (
        <MenuItem onClick={() => showUserProfileDialog()} sx={{ width: '100%' }}>
            <ListItemIcon>
                <Icon fontSize="small">account_circle</Icon>
            </ListItemIcon>
            <ListItemText>{t('component.UserProfile.perfil')}</ListItemText>
        </MenuItem>
    );
};

// Selector del tema de l'aplicació (clar / obscur / sistema) lligat al colorScheme de MUI
const ThemeSelector: React.FC = () => {
    const { t } = useTranslation();
    const { mode, setMode } = useColorScheme();
    const handleChange = (
        _event: React.MouseEvent<HTMLElement>,
        newMode: 'light' | 'dark' | 'system' | null
    ) => {
        if (newMode !== null) {
            setMode(newMode);
        }
    };
    return (
        <>
            <Typography component="label" sx={selectorLabelSx}>
                {t('component.UserProfile.tema.label')}
            </Typography>
            <ToggleButtonGroup
                value={mode ?? 'system'}
                exclusive
                onChange={handleChange}
                size="small"
                sx={{ display: 'flex', width: '100%' }}
            >
                <ToggleButton value="light" sx={{ flex: 1, gap: 1 }}>
                    <Icon>light_mode</Icon> {t('component.UserProfile.tema.clar')}
                </ToggleButton>
                <ToggleButton value="dark" sx={{ flex: 1, gap: 1 }}>
                    <Icon>dark_mode</Icon> {t('component.UserProfile.tema.obscur')}
                </ToggleButton>
                <ToggleButton value="system" sx={{ flex: 1, gap: 1 }}>
                    <Icon>settings_brightness</Icon> {t('component.UserProfile.tema.sistema')}
                </ToggleButton>
            </ToggleButtonGroup>
        </>
    );
};

// Camp de només lectura amb els rols de l'usuari. Els rols NO es tradueixen (es mostren tal qual).
const RolesField: React.FC = () => {
    const { t } = useTranslation();
    const { rolesAvailable } = usePinbalContext();
    return (
        <TextField
            fullWidth
            size="small"
            label={t('component.UserProfile.rols')}
            value={rolesAvailable?.join(', ') ?? ''}
            disabled
            slotProps={{
                input: {
                    readOnly: true,
                    endAdornment: (
                        <InputAdornment position="end">
                            <Icon fontSize="small">recent_actors</Icon>
                        </InputAdornment>
                    ),
                },
            }}
        />
    );
};

export const UserProfileFormDialog: React.FC<{
    formDialogApiRef: React.RefObject<MuiDataFormDialogApi | null>;
}> = (props) => {
    const { formDialogApiRef } = props;
    const { t } = useTranslation();
    const { currentLanguage, setCurrentLanguage } = useBaseAppContext();
    const { currentUser, setCurrentUser } = usePinbalContext();

    const syncLanguage = (data: any) => {
        const profileLanguage = data?.idioma?.toLowerCase();
        if (profileLanguage != null && currentLanguage !== profileLanguage) {
            setCurrentLanguage(profileLanguage);
        }
    };

    React.useEffect(() => {
        syncLanguage(currentUser);
    }, [currentUser]);

    return (
        <MuiFormDialog
            resourceName="usuariResource"
            title={t('component.UserProfile.perfil')}
            apiRef={formDialogApiRef}
            dialogComponentProps={{ fullWidth: true, maxWidth: 'md' }}
            formComponentProps={{
                commonFieldComponentProps: { size: 'small' },
                onSaveSuccess: (data: any) => {
                    syncLanguage(data);
                    setCurrentUser(data);
                },
            }}
        >
            <Grid container spacing={2} sx={{ px: 1 }}>
                <Grid size={12}>
                    <Divider>{t('component.UserProfile.seccioDades')}</Divider>
                </Grid>
                <GridFormField
                    size={{ xs: 12, sm: 6, md: 3 }}
                    name="codi"
                    disabled
                    componentProps={endIcon('tag')}
                />
                <GridFormField
                    size={{ xs: 12, sm: 6, md: 5 }}
                    name="nom"
                    disabled
                    componentProps={endIcon('person')}
                />
                <GridFormField
                    size={{ xs: 12, sm: 6, md: 4 }}
                    name="nif"
                    disabled
                    componentProps={endIcon('badge')}
                />
                <GridFormField
                    size={{ xs: 12, sm: 6, md: 6 }}
                    name="email"
                    disabled
                    componentProps={endIcon('alternate_email')}
                />
                <Grid size={{ xs: 12, md: 6 }}>
                    <RolesField />
                </Grid>
                <Grid size={12}>
                    <Divider>{t('component.UserProfile.seccioConfig')}</Divider>
                </Grid>
                <GridFormField
                    size={{ xs: 12, sm: 6, md: 4 }}
                    name="idioma"
                    componentProps={endIcon('language', true)}
                />
                <GridFormField
                    size={{ xs: 12, sm: 6, md: 4 }}
                    name="numElementsPagina"
                    emptyValueDescription={t('component.UserProfile.auto')}
                    componentProps={endIcon('format_list_numbered')}
                />
                <Grid size={12}>
                    <ThemeSelector />
                </Grid>
            </Grid>
        </MuiFormDialog>
    );
};
