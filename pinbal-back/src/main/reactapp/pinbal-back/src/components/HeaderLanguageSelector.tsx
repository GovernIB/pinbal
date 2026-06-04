import React from 'react';
import { useTranslation } from 'react-i18next';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import ToggleButtonGroup from '@mui/material/ToggleButtonGroup';
import ToggleButton from '@mui/material/ToggleButton';
import { useBaseAppContext } from 'reactlib';

const TYPOGRAPHY_VARIANT = 'h6';

export type LanguageItem = {
    locale: string;
    name: string;
} & any;

type HeaderLanguageSelectorProps = {
    type?: 'typography' | 'buttons';
    languages?: string[];
    onLanguageChange?: (language?: string) => void;
} & any;

const HeaderLanguageSelectorTypography: React.FC<HeaderLanguageSelectorProps> = (props) => {
    const { languages, onLanguageChange, sx: otherSx, ...otherProps } = props;
    const { currentLanguage, setCurrentLanguage } = useBaseAppContext();
    React.useEffect(() => {
        onLanguageChange?.(currentLanguage);
    }, [currentLanguage]);
    const handleLanguageChange = (locale: string) => {
        if (locale !== null) {
            setCurrentLanguage(locale);
        }
    };
    return languages?.length ? (
        <Box sx={{ display: 'flex', ...otherSx }} {...otherProps}>
            {languages.map((l: LanguageItem, i: number) => (
                <React.Fragment key={l.locale}>
                    {currentLanguage === l.locale ? (
                        <Typography variant={TYPOGRAPHY_VARIANT} sx={{ fontWeight: 'bold' }}>
                            {l.locale.toUpperCase()}
                        </Typography>
                    ) : (
                        <Typography
                            variant={TYPOGRAPHY_VARIANT}
                            onClick={() => handleLanguageChange(l.locale)}
                            sx={{ cursor: 'pointer', fontWeight: '400' }}>
                            {l.locale.toUpperCase()}
                        </Typography>
                    )}
                    {i < languages.length - 1 && (
                        <Typography
                            key={'locale_sep_' + i}
                            variant={TYPOGRAPHY_VARIANT}
                            sx={{ mx: 1 }}>
                            |
                        </Typography>
                    )}
                </React.Fragment>
            ))}
        </Box>
    ) : null;
};

const HeaderLanguageSelectorButtons: React.FC<HeaderLanguageSelectorProps> = (props) => {
    const { languages, onLanguageChange, sx: otherSx, ...otherProps } = props;
    const { currentLanguage, setCurrentLanguage } = useBaseAppContext();
    React.useEffect(() => {
        onLanguageChange?.(currentLanguage);
    }, [currentLanguage]);
    const handleLanguageChange = (locale: string) => {
        if (locale !== null) {
            setCurrentLanguage(locale);
        }
    };
    return (
        languages?.length && (
            <ToggleButtonGroup
                value={currentLanguage}
                exclusive
                size="small"
                onChange={(_event, locale) => handleLanguageChange(locale)}
                sx={{ mx: 1, ...otherSx }}
                {...otherProps}>
                {languages.map((l: LanguageItem) => (
                    <ToggleButton value={l.locale} key={l.locale}>
                        <span
                            className={'fi fi-' + (l.locale === 'ca' ? 'es-ct' : l.locale)}
                            style={{ marginRight: '8px' }}
                        />
                        {l.name}
                    </ToggleButton>
                ))}
            </ToggleButtonGroup>
        )
    );
};

const HeaderLanguageSelector: React.FC<HeaderLanguageSelectorProps> = (props) => {
    const { type, languages, onLanguageChange, sx: otherSx, ...otherProps } = props;
    const { t } = useTranslation();
    const allLanguages = [
        {
            locale: 'ca',
            name: t('component.HeaderLanguageSelector.languages.ca'),
        },
        {
            locale: 'es',
            name: t('component.HeaderLanguageSelector.languages.es'),
        },
        {
            locale: 'en',
            name: t('component.HeaderLanguageSelector.languages.en'),
        },
    ];
    const filteredLanguages = languages?.length
        ? allLanguages.filter((l) => languages.includes(l.locale))
        : allLanguages;
    return type === 'typography' ? (
        <HeaderLanguageSelectorTypography
            languages={filteredLanguages}
            onLanguageChange={onLanguageChange}
            {...otherProps}
        />
    ) : (
        <HeaderLanguageSelectorButtons
            languages={filteredLanguages}
            onLanguageChange={onLanguageChange}
            {...otherProps}
        />
    );
};

export default HeaderLanguageSelector;
