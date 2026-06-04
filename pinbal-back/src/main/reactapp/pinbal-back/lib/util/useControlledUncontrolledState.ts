import React from 'react';

export const useControlledUncontrolledState = <S>(
    defaultValue: S | (() => S),
    controlledValue?: S,
    onChange?: (value: S) => void
): [S | undefined, (value: S) => void] => {
    const [isControlled] = React.useState(controlledValue !== undefined);
    const [value, setValue] = React.useState(defaultValue);
    const finalValue = isControlled ? controlledValue : value;
    React.useEffect(() => {
        if (
            (isControlled && controlledValue === undefined) ||
            (!isControlled && controlledValue !== undefined)
        ) {
            console.error(
                '[FONAR] A component state is trying to change from controlled to uncontrolled or vice ' +
                    'versa. Decide between using a controlled or uncontrolled state for the lifetime of the component. ' +
                    'A component state is considered controlled if its value prop is not undefined.'
            );
        }
    }, [controlledValue]);
    const finalSetter: (value: S) => void = React.useCallback(
        (...params) => {
            if (isControlled) {
                onChange?.(...params);
            } else {
                setValue(...params);
                onChange?.(...params);
            }
        },
        [isControlled, onChange]
    );
    return [finalValue, finalSetter];
};

export default useControlledUncontrolledState;
