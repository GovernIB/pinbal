import React from 'react';

export const useDebounce = (value: any, delay: number = 300, debounceEmpty?: boolean) => {
    const [debouncedValue, setDebouncedValue] = React.useState<any>(value);
    React.useEffect(() => {
        if (value || debounceEmpty) {
            const timeoutId = setTimeout(() => setDebouncedValue(value), delay);
            return () => clearTimeout(timeoutId);
        } else {
            setDebouncedValue(value);
        }
    }, [value, delay]);
    return debouncedValue;
};
