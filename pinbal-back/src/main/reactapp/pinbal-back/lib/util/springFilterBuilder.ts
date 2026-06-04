const filter = (options: any[]): any[] => {
    return options.filter((a) => {
        return (
            a != null &&
            a.length > 0 &&
            !a.includes('undefined') &&
            !(a.includes('null') && !(a.includes('is not null') || a.includes('is null')))
        );
    });
};

export const and = (...options: any[]): string => {
    return filter(options).join(' and ');
};

export const or = (...options: any[]): string => {
    const joinedValues = filter(options).join(' OR ');
    return joinedValues.includes(' OR ') ? `(${joinedValues})` : joinedValues;
};

export const not = (value: string): string => {
    return value.length > 0 ? `not(${value})` : '';
};

export const eq = (option: string, value: any): string => {
    return value === null ? `${option} is null` : `${option}:${value}`;
};

export const neq = (option: string, value: any): string => {
    return value === null ? `${option} is not null` : `${option}!${value}`;
};

export const gt = (option: string, value: any): string => {
    return `${option}>${value}`;
};

export const gte = (option: string, value: any): string => {
    return `${option}>:${value}`;
};

export const lt = (option: string, value: any): string => {
    return `${option}<${value}`;
};

export const lte = (option: string, value: any): string => {
    return `${option}<:${value}`;
};

export const inn = (option: string, ...values: any[]): string => {
    const joinedValues = filter(values).join(',');
    return joinedValues.length > 0 ? `${option} in (${joinedValues})` : '';
};

export const like = (option: string, value: string): string => {
    return `${option}~'%${value}%'`;
};

export const concat = (...options: any[]): string => {
    return options.length > 0 ? `concat(${filter(options).join(",' ',")})` : '';
};

export const exists = (value: string): string => {
    return value.length > 0 ? `exists(${value})` : '';
};

export const between = (option: string, paramStart: any, paramEnd: any) => {
    return and(gte(option, paramStart), lte(option, paramEnd));
};
