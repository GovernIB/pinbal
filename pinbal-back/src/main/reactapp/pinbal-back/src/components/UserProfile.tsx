import React from 'react';
import { useTranslation } from 'react-i18next';
import MenuItem from '@mui/material/MenuItem';
import ListItemText from '@mui/material/ListItemText';
import ListItemIcon from '@mui/material/ListItemIcon';
import Icon from '@mui/material/Icon';
import Grid from '@mui/material/Grid';
import { useColorScheme } from '@mui/material/styles';
import 'dayjs/locale/ca';
import 'dayjs/locale/es';
import { MuiFormDialog, MuiDataFormDialogApi, useBaseAppContext, useAuthContext } from 'reactlib';
import { usePinbalContext } from './PinbalContext.ts';
import GridFormField from './GridFormField';

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

export const UserProfileFormDialog: React.FC<{
    formDialogApiRef: React.RefObject<MuiDataFormDialogApi | null>;
}> = (props) => {
    const { formDialogApiRef } = props;
    const { t } = useTranslation();
    const { mode, setMode } = useColorScheme();
    const { currentLanguage, setCurrentLanguage } = useBaseAppContext();
    const { currentUser, setCurrentUser } = usePinbalContext();

    const handleSaveSuccess = (data: any, saveUser?: boolean) => {
        const profileLanguage = data?.idioma?.toLowerCase();
        if (profileLanguage != null && currentLanguage !== profileLanguage) {
            setCurrentLanguage(profileLanguage);
        }
        const profileMode = data?.tema?.toLowerCase() ?? 'system';
        if (mode !== profileMode) {
            setMode(profileMode);
        }
        if (saveUser) {
            setCurrentUser(data);
        }
    };

    React.useEffect(() => {
        handleSaveSuccess(currentUser);
    }, [currentUser]);

    return (
        <MuiFormDialog
            resourceName="usuariResource"
            title={t('component.UserProfile.perfil')}
            apiRef={formDialogApiRef}
            dialogComponentProps={{ fullWidth: true, maxWidth: 'lg' }}
            formComponentProps={{
                commonFieldComponentProps: { size: 'small' },
                onSaveSuccess: (data: any) => handleSaveSuccess(data, true),
            }}
        >
            <Grid container spacing={2}>
                <GridFormField size={4} name="codi" disabled />
                <GridFormField size={8} name="nomSencer" disabled />
                <GridFormField size={6} name="email" disabled />
                <GridFormField size={6} name="emailAlt" />
                <GridFormField
                    size={4}
                    name="numElementsPaginaDefecte"
                    emptyValueDescription={t('component.UserProfile.auto')}
                />
                <GridFormField size={4} name="idioma" />
                <GridFormField size={4} name="tema" />
                <GridFormField size={6} name="rebreEmailsNotificacio" />
                <GridFormField size={6} name="rebreEmailsNotificacioCreats" />
                <GridFormField size={12} name="entitatDefecte" />
                <GridFormField size={12} name="organDefecte" />
                <GridFormField size={12} name="procedimentDefecte" />
            </Grid>
        </MuiFormDialog>
    );
};
