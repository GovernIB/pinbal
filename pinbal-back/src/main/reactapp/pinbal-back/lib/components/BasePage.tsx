import React from 'react';
import { useBaseAppContext } from './BaseAppContext';

/**
 * Propietats del component BasePage.
 */
export type BasePageProps = React.PropsWithChildren & {
    /** Component toolbar de la pàgina */
    toolbar?: React.ReactElement;
    /** Indica que s'han de desactivar els marges */
    disableMargins?: boolean;
    /** Indica que s'ha d'expandir l'alçada de la pàgina al 100% */
    expandHeight?: boolean;
    /** Estils addicionals per l'element contenidor */
    style?: React.CSSProperties;
};

/**
 * Pàgina base de l'aplicació.
 *
 * @param props - Propietats del component.
 * @returns Element JSX de la pàgina.
 */
export const BasePage: React.FC<BasePageProps> = (props) => {
    const { toolbar, disableMargins = true, expandHeight = false, style, children } = props;
    const { setMarginsDisabled, setContentExpandsToAvailableHeight } = useBaseAppContext();
    React.useEffect(() => {
        setMarginsDisabled(disableMargins);
        return () => setMarginsDisabled(false);
    }, [disableMargins]);
    React.useEffect(() => {
        if (expandHeight) {
            setContentExpandsToAvailableHeight(true);
            return () => setContentExpandsToAvailableHeight(false);
        }
    }, [expandHeight]);
    const parentDivExpandHeightStyles: React.CSSProperties = expandHeight
        ? {
              display: 'flex',
              flexDirection: 'column',
              height: 'calc(100% - 64px - 1px)', // Els 64px son del toolbar de l'aplicació (el pixel ni idea)
          }
        : {};
    const marginsDivExpandHeightStyles: React.CSSProperties = expandHeight
        ? {
              flexGrow: 1,
          }
        : {};
    return (
        <div
            style={{
                ...parentDivExpandHeightStyles,
                ...style,
            }}>
            {toolbar}
            <div style={{ margin: '16px', minHeight: 0, ...marginsDivExpandHeightStyles }}>
                {children}
            </div>
        </div>
    );
};

export default BasePage;
