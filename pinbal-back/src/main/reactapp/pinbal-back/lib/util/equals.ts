export const shallowEqual = (obj1: any, obj2: any) => {
    for (let key of Object.keys(obj1)) {
        const val1 = obj1[key];
        const val2 = obj2[key];
        if (Array.isArray(val1) && Array.isArray(val2)) {
            if (val1.length !== val2.length) return false;
            if (JSON.stringify(val1) !== JSON.stringify(val2)) {
                return false;
            }
            continue;
        }
        if (Array.isArray(val1) || Array.isArray(val2)) {
            return false;
        }
        if (typeof val1 === 'object' && typeof val2 === 'object') {
            const bothNull = val1 === null && val2 === null;
            const bothNotNull = val1 !== null && val2 !== null;
            if (!bothNull && !bothNotNull) return false;
            if (JSON.stringify(val1) !== JSON.stringify(val2)) {
                return false;
            }
            continue;
        }
        if (val1 !== val2) return false;
    }
    return true;
};

export const deepEqual = (a: any, b: any): boolean => {
    if (a === b) return true;
    if (typeof a !== 'object' || typeof b !== 'object' || a === null || b === null) {
        return false;
    }
    for (let key of Object.keys(a)) {
        if (!deepEqual(a[key], b[key])) return false;
    }
    return true;
};
