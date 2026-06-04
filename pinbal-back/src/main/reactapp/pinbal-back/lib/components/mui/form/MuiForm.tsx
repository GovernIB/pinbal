import React from 'react';
import Box from '@mui/material/Box';
import { Form, FormProps, useFormApiContext } from '../../form/Form';
import { useBaseAppContext } from '../../BaseAppContext';
import { useMuiBaseAppContext } from '../MuiBaseAppContext';
import { useFormContext } from '../../form/FormContext';
import { ReactElementWithPosition } from '../../../util/reactNodePosition';
import { toToolbarIcon } from '../ToolbarIcon';
import { Toolbar } from '../Toolbar';

/**
 * Propietats del component MuiForm (també conté les propietats del component Form).
 */
export type MuiFormProps = FormProps & {
    /** Subtítol de la barra d'eines */
    toolbarSubtitle?: string;
    /** Elements addicionals (amb la seva posició) per a la barra d'eines */
    toolbarElementsWithPositions?: ReactElementWithPosition[];
    /** Indica si la barra d'eines està oculta */
    hiddenToolbar?: true;
    /** Indica si el botó de retrocedir ha d'estar ocult */
    hiddenBackButton?: true;
    /** Indica si el botó de restablir els valors ha d'estar ocult */
    hiddenRevertButton?: true;
    /** Indica si el botó de desar ha d'estar ocult */
    hiddenSaveButton?: true;
    /** Indica si el botó d'esborrar ha d'estar ocult */
    hiddenDeleteButton?: true;
    /** Propietats del component del formulari */
    componentProps?: any;
};

const MuiFormContent: React.FC<React.PropsWithChildren | any> = (props) => {
    const {
        id,
        title,
        resourceName,
        toolbarSubtitle,
        toolbarElementsWithPositions,
        goBackLink,
        hiddenToolbar,
        hiddenBackButton,
        hiddenRevertButton,
        hiddenSaveButton,
        hiddenDeleteButton,
        componentProps,
        children,
    } = props;
    const formApiRef = useFormApiContext();
    const { t, goBack, anyHistoryEntryExist, contentExpandsToAvailableHeight } =
        useBaseAppContext();
    const { modified, isSaveActionPresent, isDeleteActionPresent } = useFormContext();
    const backButtonDisabled = !anyHistoryEntryExist() && !goBackLink;
    const toolbarNodes: ReactElementWithPosition[] = [];
    !hiddenBackButton &&
        toolbarNodes.push({
            position: 0,
            element: toToolbarIcon('arrow_back', {
                title: t('form.goBack.title'),
                onClick: () => goBack(goBackLink),
                disabled: backButtonDisabled,
                sx: { mr: 1 },
            }),
        });
    toolbarNodes.push(...(toolbarElementsWithPositions ?? []));
    !hiddenRevertButton &&
        isSaveActionPresent &&
        toolbarNodes.push({
            position: 2,
            element: toToolbarIcon('undo', {
                title: t('form.revert.title'),
                onClick: () => formApiRef.current?.revert(),
                disabled: !modified,
            }),
        });
    !hiddenSaveButton &&
        isSaveActionPresent &&
        toolbarNodes.push({
            position: 2,
            element: toToolbarIcon('save', {
                title: t(id != null ? 'form.update.title' : 'form.create.title'),
                onClick: () => formApiRef.current?.save(),
            }),
        });
    !hiddenDeleteButton &&
        isDeleteActionPresent &&
        toolbarNodes.push({
            position: 2,
            element: toToolbarIcon('delete', {
                title: t('form.delete.title'),
                onClick: () => formApiRef.current?.delete(),
            }),
        });
    const outerBoxStyles = contentExpandsToAvailableHeight
        ? { display: 'flex', flexDirection: 'column', height: '100%' }
        : undefined;
    const sx2 = contentExpandsToAvailableHeight
        ? { display: 'flex', flexDirection: 'column', height: '100%' }
        : undefined;
    const innerBoxStyles = hiddenToolbar
        ? { mt: 1, ...sx2, ...componentProps?.sx }
        : { m: 2, mt: 3, ...sx2, ...componentProps?.sx };
    return (
        <Box sx={outerBoxStyles}>
            {!hiddenToolbar && (
                <Toolbar
                    title={title ?? resourceName}
                    subtitle={toolbarSubtitle}
                    elementsWithPositions={toolbarNodes}
                    upperToolbar
                    sx={{
                        position: 'sticky',
                        top: '64px',
                        zIndex: 10,
                    }}
                />
            )}
            <Box sx={innerBoxStyles}>{children}</Box>
        </Box>
    );
};

/**
 * Component de formulari per a la llibreria MUI.
 *
 * @param props - Propietats del component.
 * @returns Element JSX del formulari.
 */
export const MuiForm: React.FC<MuiFormProps> = (props) => {
    const { defaultMuiComponentProps } = useMuiBaseAppContext();
    const joinedProps = { ...defaultMuiComponentProps.form, ...props };
    const {
        id,
        title,
        resourceName,
        toolbarSubtitle,
        toolbarElementsWithPositions,
        goBackLink,
        hiddenToolbar,
        hiddenBackButton,
        hiddenRevertButton,
        hiddenSaveButton,
        hiddenDeleteButton,
        componentProps,
        children,
    } = joinedProps;
    return (
        <Form {...joinedProps}>
            <MuiFormContent
                id={id}
                title={title}
                resourceName={resourceName}
                toolbarSubtitle={toolbarSubtitle}
                toolbarElementsWithPositions={toolbarElementsWithPositions}
                goBackLink={goBackLink}
                hiddenToolbar={hiddenToolbar}
                hiddenBackButton={hiddenBackButton}
                hiddenRevertButton={hiddenRevertButton}
                hiddenSaveButton={hiddenSaveButton}
                hiddenDeleteButton={hiddenDeleteButton}
                componentProps={componentProps}>
                {children}
            </MuiFormContent>
        </Form>
    );
};
export default MuiForm;
