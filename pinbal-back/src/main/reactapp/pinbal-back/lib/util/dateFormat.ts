export const dateFormatLocale = (
    date: any,
    withTime?: boolean,
    noSeconds?: boolean,
    noFixedSize?: boolean
) => {
    if (date) {
        let dateFormated;
        if (noFixedSize) {
            dateFormated =
                Intl.DateTimeFormat().format(new Date(date)) + withTime
                    ? ' ' + timeFormatLocale(date)
                    : '';
        } else {
            const parts = Intl.DateTimeFormat().formatToParts(new Date(date));
            dateFormated = parts
                .map((p) => {
                    if (p.type === 'day' || p.type === 'month') {
                        return ('0' + p.value).slice(-2);
                    } else {
                        return p.value;
                    }
                })
                .join('');
        }
        return dateFormated + (withTime ? ' ' + timeFormatLocale(date, noSeconds) : '');
    }
};

const TIME_SEPARATOR = ':';
export const timeFormatLocale = (date: any, noSeconds?: boolean) => {
    if (date) {
        if (typeof date === 'string' && date.length == 8) {
            return noSeconds ? date.substring(0, 5) : date;
        } else if (typeof date === 'string' && date.length == 5) {
            return date;
        } else {
            const dateObj = new Date(date);
            const hourMinute =
                ('0' + dateObj.getHours()).slice(-2) +
                TIME_SEPARATOR +
                ('0' + dateObj.getMinutes()).slice(-2);
            if (noSeconds) {
                return hourMinute;
            } else {
                return hourMinute + TIME_SEPARATOR + ('0' + dateObj.getSeconds()).slice(-2);
            }
        }
    }
};

export const timestampToIsoDate = (timestamp: number): string => {
    const date = new Date(timestamp);
    const year = date.getFullYear();
    const month = ('' + (date.getMonth() + 1)).padStart(2, '0');
    const day = ('' + date.getDate()).padStart(2, '0');
    return year + '-' + month + '-' + day;
};

export const timestampToIsoDateTime = (timestamp: number): string => {
    return timestampToIsoDate(timestamp) + 'T' + timeFormatLocale(timestamp);
};

export const isoDateToDate = (isoDate: string): Date | undefined => {
    const isoDateParts = isoDate?.split('-');
    if (isoDateParts != null) {
        return new Date(
            parseInt(isoDateParts[0]),
            parseInt(isoDateParts[1]) - 1,
            parseInt(isoDateParts[2]),
            0,
            0,
            0
        );
    } else {
        return undefined;
    }
};

export const isoDateTimeToDate = (isoDateTime: string): Date | undefined => {
    if (isoDateTime.includes('T')) {
        const isoDateTimeParts = isoDateTime.split('T');
        const isoDateParts = isoDateTimeParts[0].split('-');
        const isoTimeParts = isoDateTimeParts[1].split(':');
        return new Date(
            parseInt(isoDateParts[0]),
            parseInt(isoDateParts[1]) - 1,
            parseInt(isoDateParts[2]),
            parseInt(isoTimeParts[0]),
            parseInt(isoTimeParts[1]),
            parseInt(isoTimeParts[2])
        );
    } else {
        return isoDateToDate(isoDateTime);
    }
};
