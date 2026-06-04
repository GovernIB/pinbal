import React from 'react';
import Box from '@mui/material/Box';
import { Detail, DetailProps } from '../../detail/Detail';
import { useBaseAppContext } from '../../BaseAppContext';
import { useMuiBaseAppContext } from '../MuiBaseAppContext';
import { ReactElementWithPosition } from '../../../util/reactNodePosition';
import { toToolbarIcon } from '../ToolbarIcon';
import { Toolbar } from '../Toolbar';

/**
 * Propietats del component MuiDetail (també conté les propietats del component Detail).
 */
export type MuiDetailProps = DetailProps & {
    /** Elements addicionals (amb la seva posició) per a la barra d'eines */
    toolbarElementsWithPositions?: ReactElementWithPosition[];
    /** Indica si la barra d'eines està oculta */
    hiddenToolbar?: true;
    /** Indica si el botó de retrocedir ha d'estar ocult */
    hiddenBackButton?: true;
    /** Propietats del component del detall */
    componentProps?: any;
};

const MuiDetailContent: React.FC<React.PropsWithChildren | any> = (props) => {
    const {
        title,
        resourceName,
        toolbarElementsWithPositions,
        goBackLink,
        hiddenToolbar,
        hiddenBackButton,
        componentProps,
        children,
    } = props;
    const { t, goBack, anyHistoryEntryExist, contentExpandsToAvailableHeight } =
        useBaseAppContext();
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
 * Component de detall per a la llibreria MUI.
 *
 * @param props - Propietats del component.
 * @returns Element JSX del detall.
 */
export const MuiDetail: React.FC<MuiDetailProps> = (props) => {
    const { defaultMuiComponentProps } = useMuiBaseAppContext();
    const {
        title,
        resourceName,
        toolbarElementsWithPositions,
        goBackLink,
        hiddenToolbar,
        hiddenBackButton,
        componentProps,
        children,
    } = { ...defaultMuiComponentProps.detail, ...props };
    return (
        <Detail {...props}>
            <MuiDetailContent
                title={title}
                resourceName={resourceName}
                toolbarElementsWithPositions={toolbarElementsWithPositions}
                goBackLink={goBackLink}
                hiddenToolbar={hiddenToolbar}
                hiddenBackButton={hiddenBackButton}
                componentProps={componentProps}>
                {children}
            </MuiDetailContent>
        </Detail>
    );
};
export default MuiDetail;
