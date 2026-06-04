import React from 'react';
import { useBaseAppContext } from '../BaseAppContext';
import { useAnswerRequiredDialogButtons } from '../AppButtons';
import { BaseApp, BaseAppProps } from '../BaseApp';
import { useOptionalResourceApiContext } from '../ResourceApiContext';
import { useMessageDialog } from './Dialog';
import { useTemporalMessage } from './TemporalMessage';
import AppBar from './AppBar';
import Menu, { MenuEntry } from './Menu';
import OfflineMessage from './OfflineMessage';
import { useToolbarMenuIcon } from './ToolbarMenuIcon';
import { FormFieldCheckbox } from './form/FormFieldCheckbox';
import { FormFieldCheckboxSelect } from './form/FormFieldCheckboxSelect';
import { FormFieldColor } from './form/FormFieldColor';
import { FormFieldDate } from './form/FormFieldDate';
import { FormFieldDateTimeLocal } from './form/FormFieldDateTimeLocal';
import { FormFieldDuration } from './form/FormFieldDuration';
import { FormFieldEnum } from './form/FormFieldEnum';
import { FormFieldNumber } from './form/FormFieldNumber';
import { FormFieldReference } from './form/FormFieldReference';
import { FormFieldText } from './form/FormFieldText';
import { FormFieldTime } from './form/FormFieldTime';
import { FormFieldRange } from './form/FormFieldRange';
import { FormFieldFile } from './form/FormFieldFile';
import { MuiDetailField } from './detail/MuiDetailField';
import {
    DefaultMuiComponentProps,
    MuiBaseAppContext,
    MuiBaseAppContextType,
} from './MuiBaseAppContext';

export type MuiBaseAppProps = Omit<BaseAppProps, 'contentComponentSlots'> & {
    headerTitle?: string | React.ReactElement;
    headerVersion?: string;
    headerLogo?: string;
    headerLogoStyle?: any;
    headerAppbarStyle?: any;
    headerAppbarBackgroundColor?: string;
    headerAppbarBackgroundImg?: string;
    headerAdditionalComponents?: React.ReactElement | React.ReactElement[];
    headerAdditionalAuthComponents?: React.ReactElement | React.ReactElement[];
    headerAuthBadgeIcon?: string;
    offline?: React.ReactElement;
    footer?: React.ReactElement;
    footerHeight?: number;
    menuTitle?: string;
    menuEntries?: MenuEntry[];
    menuOnTitleClose?: () => void;
    menuShrinkDisabled?: boolean;
    menuWidth?: number;
    menuPanelWidth?: number;
    defaultMuiComponentProps?: DefaultMuiComponentProps;
};

const baseFormFieldComponents = [
    {
        type: 'checkbox',
        component: FormFieldCheckbox,
    },
    {
        type: 'checkbox-select',
        component: FormFieldCheckboxSelect,
    },
    {
        type: 'color',
        component: FormFieldColor,
    },
    {
        type: 'date',
        component: FormFieldDate,
    },
    {
        type: 'datetime-local',
        component: FormFieldDateTimeLocal,
    },
    {
        type: 'decimal',
        component: FormFieldNumber,
    },
    {
        type: 'duration',
        component: FormFieldDuration,
    },
    {
        type: 'enum',
        component: FormFieldEnum,
    },
    {
        type: 'number',
        component: FormFieldNumber,
    },
    {
        type: 'reference',
        component: FormFieldReference,
    },
    {
        type: 'text',
        component: FormFieldText,
    },
    {
        type: 'textarea',
        component: FormFieldText,
    },
    {
        type: 'time',
        component: FormFieldTime,
    },
    {
        type: 'range',
        component: FormFieldRange,
    },
    {
        type: 'file',
        component: FormFieldFile,
    },
    {
        type: 'url',
        component: FormFieldText,
    },
];

const MuiComponentsConfigurer: React.FC = () => {
    const [messageDialogShow, messageDialogComponent] = useMessageDialog();
    const [temporalMessageShow, temporalMessageComponent] = useTemporalMessage();
    const resourceApiContext = useOptionalResourceApiContext();
    const { setMessageDialogShow, setTemporalMessageShow } = useBaseAppContext();
    const getAnswerRequiredButtons = useAnswerRequiredDialogButtons();
    const openAnswerRequiredDialog = (
        title: string | undefined,
        question: string,
        trueFalseAnswerRequired: boolean,
        availableAnswers?: string[]
    ) => {
        return messageDialogShow(
            title ?? 'Atenció',
            question,
            getAnswerRequiredButtons(trueFalseAnswerRequired, availableAnswers),
            {
                slotProps: {
                    paper: {
                        sx: { minWidth: 400 },
                    },
                },
            },
            true
        );
    };
    React.useEffect(() => {
        setMessageDialogShow(messageDialogShow);
        setTemporalMessageShow(temporalMessageShow);
        if (resourceApiContext != null) {
            resourceApiContext.setOpenAnswerRequiredDialog(openAnswerRequiredDialog);
        }
    }, []);
    return (
        <>
            {messageDialogComponent}
            {temporalMessageComponent}
        </>
    );
};

const useMenu = (
    menuTitle: string | undefined,
    menuEntries: MenuEntry[] | undefined,
    menuOnTitleClose: (() => void) | undefined,
    menuWidth: number | undefined,
    menuFooterHeight: number | undefined,
    menuPanelWidth: number | undefined
) => {
    const { shrink, iconClicked, buttonComponent: menuButton } = useToolbarMenuIcon();
    const menuComponent =
        menuEntries != null ? (
            <Menu
                title={menuTitle}
                entries={menuEntries}
                onTitleClose={menuOnTitleClose}
                drawerWidth={menuWidth}
                shrink={shrink}
                iconClicked={iconClicked}
                footerHeight={menuFooterHeight}
                compactPanelWidth={menuPanelWidth}
            />
        ) : undefined;
    if (menuComponent && menuFooterHeight == null) {
        console.warn(
            '[BaseApp] Footer height not defined. This may cause some menu options to not display correctly.'
        );
    }
    return {
        menuButton,
        menuComponent,
    };
};

export const MuiBaseApp: React.FC<MuiBaseAppProps> = (props) => {
    const {
        headerTitle,
        headerVersion,
        headerLogo,
        headerLogoStyle,
        headerAppbarStyle,
        headerAppbarBackgroundColor,
        headerAppbarBackgroundImg,
        headerAdditionalComponents,
        headerAdditionalAuthComponents,
        headerAuthBadgeIcon,
        offline,
        footer,
        footerHeight,
        menuTitle,
        menuEntries,
        menuOnTitleClose,
        menuShrinkDisabled,
        menuWidth,
        menuPanelWidth,
        formFieldComponents,
        children,
        defaultMuiComponentProps,
        ...otherProps
    } = props;
    const mergedFormFieldComponents = [...baseFormFieldComponents, ...(formFieldComponents ?? [])];
    const { menuButton, menuComponent } = useMenu(
        menuTitle,
        menuEntries,
        menuOnTitleClose,
        menuWidth,
        footerHeight,
        menuPanelWidth
    );
    const appbarComponent =
        headerTitle != null ? (
            <AppBar
                title={headerTitle}
                version={headerVersion}
                logo={headerLogo}
                logoStyle={headerLogoStyle}
                menuButton={!menuShrinkDisabled && menuEntries != null ? menuButton : undefined}
                additionalToolbarComponents={headerAdditionalComponents}
                additionalAuthComponents={headerAdditionalAuthComponents}
                style={headerAppbarStyle}
                backgroundColor={headerAppbarBackgroundColor}
                backgroundImg={headerAppbarBackgroundImg}
                authBadgeIcon={headerAuthBadgeIcon}
            />
        ) : undefined;
    const offlineComponent = offline ?? <OfflineMessage />;
    const muiContext: MuiBaseAppContextType = {
        defaultMuiComponentProps: {
            ...defaultMuiComponentProps,
        },
    };
    return (
        <BaseApp
            formFieldComponents={mergedFormFieldComponents}
            detailFieldComponent={MuiDetailField}
            {...otherProps}
            contentComponentSlots={{
                appbar: appbarComponent,
                footer,
                menu: menuComponent,
                offline: offlineComponent,
            }}>
            <MuiBaseAppContext.Provider value={muiContext}>
                <MuiComponentsConfigurer />
                {children}
            </MuiBaseAppContext.Provider>
        </BaseApp>
    );
};

export default MuiBaseApp;
