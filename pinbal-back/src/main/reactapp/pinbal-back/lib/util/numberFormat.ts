export const countDecimalPositions = (number: number) => {
    if (number !== undefined && number !== null && number % 1 != 0) {
        if (Math.floor(number.valueOf()) === number.valueOf()) return 0;
        var str = number.toString();
        if (str.startsWith('1e-')) {
            return str.split('-')[1] || 0;
        } else {
            return str.split('.')[1].length || 0;
        }
    } else {
        return 0;
    }
};

export const numberFormat = (number: number, formatOptions: any, locale?: string) => {
    return new Intl.NumberFormat(locale, { ...formatOptions, useGrouping: true }).format(number);
};

export const numberFormatEur = (number: number, formatOptions: any, locale?: string) => {
    const eurFormatOptions = {
        style: 'currency',
        currency: 'EUR',
        ...(formatOptions ?? {}),
    };
    return numberFormat(number, eurFormatOptions, locale);
};

export const numberFormatCurrency = (
    number: number,
    currency?: string,
    decimalPlaces?: number,
    locale?: string
) => {
    const formatOptions = {
        style: currency ? 'currency' : undefined,
        currency: currency ?? undefined,
        ...(decimalPlaces != null
            ? {
                  minimumFractionDigits: decimalPlaces,
                  maximumFractionDigits: decimalPlaces,
              }
            : {}),
    };
    return numberFormat(number, formatOptions, locale);
};

export const numberFormatCurrencyParts = (currency?: string, locale?: string) => {
    const formatOptions: any = {
        style: currency ? 'currency' : undefined,
        currency,
        useGrouping: true,
    };
    return new Intl.NumberFormat(locale, formatOptions).formatToParts(0);
};

export const numberFormatField = (number: number, field: any, locale: string) => {
    if (field) {
        const decimalPlaces = field.step ? countDecimalPositions(field.step) : undefined;
        const formatOptions =
            decimalPlaces != null
                ? {
                      minimumFractionDigits: decimalPlaces,
                      maximumFractionDigits: decimalPlaces,
                  }
                : {};
        return numberFormat(number, formatOptions, locale);
    } else {
        return numberFormat(number, locale);
    }
};

export const intWithFixedPositions = (number: number, positions: number) => {
    return ('0'.repeat(positions - 1) + number).slice(-positions);
};

export const getDecimalSeparator = (locale: string) => {
    return Intl.NumberFormat(locale === 'es' ? 'ca' : locale)
        .formatToParts(1.1)
        .find((p) => p.type === 'decimal')?.value;
};
export const getThousandSeparator = (locale: string) => {
    return Intl.NumberFormat(locale === 'es' ? 'ca' : locale)
        .formatToParts(10000.1)
        .find((p) => p?.type === 'group')?.value;
};
