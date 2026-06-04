import React from 'react';
import { useBaseAppContext } from './BaseAppContext';

/**
 * Propietats del component FormPage.
 */
type FormPageProps = React.PropsWithChildren & {
    /** Indica que s'han de desactivar els marges */
    disableMargins?: boolean;
};

/**
 * Pàgina que conté un element de formulari.
 *
 * @param props - Propietats del component.
 * @returns Element JSX de la pàgina.
 */
export const FormPage: React.FC<FormPageProps> = (props) => {
    const { disableMargins = true, children } = props;
    const { setMarginsDisabled } = useBaseAppContext();
    React.useEffect(() => {
        setMarginsDisabled(disableMargins);
        return () => setMarginsDisabled(false);
    }, [disableMargins]);
    return children;
};

export default FormPage;
