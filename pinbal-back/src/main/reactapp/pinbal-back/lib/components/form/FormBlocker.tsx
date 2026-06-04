import React from 'react';
import { useBaseAppContext } from '../BaseAppContext';

export const FormBlocker: React.FC<{ modified: boolean }> = (props) => {
    const { modified } = props;
    const { useBlocker, t } = useBaseAppContext();
    useBlocker?.(() => {
        if (modified) {
            return !confirm(t('form.blocker'));
        } else {
            return false;
        }
    });
    return null;
};
export default FormBlocker;
