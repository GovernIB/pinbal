import { dateFormatLocale, timeFormatLocale } from './dateFormat';
import { parseIsoDuration } from './durationFormat';
import { numberFormatCurrency, numberFormatField } from './numberFormat';

/*config: {
type,
currentLanguage,
currencyCode,
currencyDecimalPlaces,
currencyLocale,
decimalPlaces,
noSeconds,
formatterParams,
booleanTextTrue,
booleanTextFalse,
booleanNullAsFalse
}*/

export const processType = (field?: any, forcedType?: any) => {
    const processedType = forcedType ?? field?.type;
    if (processedType != null) {
        if (processedType === 'search') {
            const dataSourceHref = field?.dataSource?.href;
            return dataSourceHref?.includes('options{?quickFilter,')
                ? 'reference'
                : dataSourceHref == null || dataSourceHref?.includes('enum')
                  ? 'enum'
                  : 'reference';
        } else {
            return processedType;
        }
    } else {
        return 'text';
    }
};

export const isFieldNumericType = (field: any, forcedType?: any) => {
    const type = forcedType ?? field?.type;
    const isNumeric =
        type === 'number' ||
        type === 'decimal' ||
        type === 'currency' ||
        type === 'range' ||
        type === 'date' ||
        type === 'time' ||
        type === 'datetime-local' ||
        type === 'duration';
    return isNumeric;
};

export const formattedFieldValue = (value: any, field?: any, config?: any): string | undefined => {
    const processedType = processType(field, config?.type);
    if (processedType === 'date') {
        return value ? dateFormatLocale(value) : value;
    } else if (processedType === 'time') {
        return value ? timeFormatLocale(value, config?.noSeconds) : value;
    } else if (processedType === 'datetime-local') {
        return value
            ? dateFormatLocale(value, config?.noTime ? false : true, config?.noSeconds)
            : value;
    } else if (processedType === 'duration') {
        const duration = parseIsoDuration(value);
        if (duration != null) {
            const parts: string[] = [];
            duration.years > 0 && parts.push(duration.years + 'y');
            duration.months > 0 && parts.push(duration.months + 'm');
            duration.days > 0 && parts.push(duration.days + 'd');
            duration.hours > 0 && parts.push(duration.hours + 'h');
            duration.minutes > 0 && parts.push(duration.minutes + 'm');
            duration.seconds > 0 && parts.push(duration.seconds + 's');
            return parts.join(' ');
        } else {
            return value;
        }
    } else if (processedType === 'number' || processedType === 'decimal') {
        return value === 0 || value
            ? numberFormatField(value, field, config?.currentLanguage)
            : value;
    } else if (processedType === 'currency') {
        const currencyCode =
            typeof config?.currencyCode === 'function'
                ? config?.currencyCode(config?.formatterParams)
                : config?.currencyCode;
        const currencyDecimalPlaces =
            typeof config?.currencyDecimalPlaces === 'function'
                ? config?.currencyDecimalPlaces(config?.formatterParams)
                : config?.currencyDecimalPlaces;
        const decimalPlaces = config?.decimalPlaces;
        const currencyLocale =
            typeof config?.currencyLocale === 'function'
                ? config?.currencyLocale(config?.formatterParams)
                : config?.currencyLocale;
        return value === 0 || value
            ? numberFormatCurrency(
                  value,
                  currencyCode,
                  currencyDecimalPlaces ?? decimalPlaces,
                  currencyLocale ?? config?.currentLanguage
              )
            : value;
    } else if (processedType === 'reference') {
        return value?.description;
    } else if (processedType === 'enum') {
        return field?.options?.[value] ?? value;
    } else if (processedType === 'checkbox' && (value != null || config?.booleanNullAsFalse)) {
        return value ? (config?.booleanTextTrue ?? 'Si') : (config?.booleanTextFalse ?? 'No');
    } else {
        return value;
    }
};

export const processApiFields = (fields: any[]) => {
    return fields
        ?.filter((f) => f != null)
        .map((f) => {
            return f.name.endsWith('*')
                ? {
                      ...f,
                      name: f.name.slice(0, -1),
                      onChangeActive: true,
                  }
                : f;
        });
};
