import React from 'react';
import { useTranslation } from 'react-i18next';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import Icon from '@mui/material/Icon';
import ToggleButtonGroup from '@mui/material/ToggleButtonGroup';
import ToggleButton from '@mui/material/ToggleButton';
import { useColorScheme } from '@mui/material/styles';

const TYPOGRAPHY_VARIANT = 'h6';

export type HeaderThemeSelectorProps = {
    type?: 'typography' | 'buttons';
} & any;

type SelectorProps = {
    mode?: 'light' | 'dark' | 'system';
    setMode: (mode?: any) => void;
} & any;

const HeaderThemeModeSelectorTypography: React.FC<SelectorProps> = (props) => {
    const { mode, setMode, sx: otherSx, ...otherProps } = props;
    const { t } = useTranslation();
    return (
        <Box sx={{ display: 'flex', mx: 1, ...otherSx }} {...otherProps}>
            <Typography
                variant={TYPOGRAPHY_VARIANT}
                onClick={mode !== 'light' ? () => setMode('light') : undefined}
                sx={
                    mode === 'light'
                        ? { fontWeight: 'bold' }
                        : { cursor: 'pointer', fontWeight: '400' }
                }>
                {t('component.HeaderThemeSelector.light')}
            </Typography>
            <Typography variant={TYPOGRAPHY_VARIANT} sx={{ mx: 1 }}>
                |
            </Typography>
            <Typography
                variant={TYPOGRAPHY_VARIANT}
                onClick={mode !== 'system' ? () => setMode('system') : undefined}
                sx={
                    mode === 'system'
                        ? { fontWeight: 'bold' }
                        : { cursor: 'pointer', fontWeight: '400' }
                }>
                {t('component.HeaderThemeSelector.system')}
            </Typography>
            <Typography variant={TYPOGRAPHY_VARIANT} sx={{ mx: 1 }}>
                |
            </Typography>
            <Typography
                variant={TYPOGRAPHY_VARIANT}
                onClick={mode !== 'dark' ? () => setMode('dark') : undefined}
                sx={
                    mode === 'dark'
                        ? { fontWeight: 'bold' }
                        : { cursor: 'pointer', fontWeight: '400' }
                }>
                {t('component.HeaderThemeSelector.dark')}
            </Typography>
        </Box>
    );
};

const HeaderThemeModeSelectorButtons: React.FC<SelectorProps> = (props) => {
    const { mode, setMode, sx: otherSx, ...otherProps } = props;
    const { t } = useTranslation();
    return (
        <ToggleButtonGroup
            value={mode}
            exclusive
            size="small"
            onChange={(_event, mode) => setMode(mode)}
            sx={{ mx: 1, ...otherSx }}
            {...otherProps}>
            <ToggleButton value="light">
                <Icon fontSize="small" sx={{ mr: 1 }}>
                    light_mode
                </Icon>
                {t('component.HeaderThemeSelector.light')}
            </ToggleButton>
            <ToggleButton value="system">
                <Icon fontSize="small" sx={{ mr: 1 }}>
                    settings_brightness
                </Icon>
                {t('component.HeaderThemeSelector.system')}
            </ToggleButton>
            <ToggleButton value="dark">
                <Icon fontSize="small" sx={{ mr: 1 }}>
                    dark_mode
                </Icon>
                {t('component.HeaderThemeSelector.dark')}
            </ToggleButton>
        </ToggleButtonGroup>
    );
};

const HeaderThemeModeSelector: React.FC<HeaderThemeSelectorProps> = (props) => {
    const { type, ...otherProps } = props;
    const { mode, setMode } = useColorScheme();
    return type === 'typography' ? (
        <HeaderThemeModeSelectorTypography mode={mode} setMode={setMode} {...otherProps} />
    ) : (
        <HeaderThemeModeSelectorButtons mode={mode} setMode={setMode} {...otherProps} />
    );
};

export default HeaderThemeModeSelector;
