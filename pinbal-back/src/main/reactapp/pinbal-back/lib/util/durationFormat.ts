const isoDurationRegex =
    /(-)?P(?:([.,\d]+)Y)?(?:([.,\d]+)M)?(?:([.,\d]+)W)?(?:([.,\d]+)D)?T(?:([.,\d]+)H)?(?:([.,\d]+)M)?(?:([.,\d]+)S)?/;

export const parseIsoDuration = (isoDuration: string) => {
    const matches = isoDuration?.match(isoDurationRegex);
    if (matches != null) {
        const duration = {
            sign: matches?.[1] === undefined ? '+' : '-',
            years: matches?.[2] === undefined ? 0 : parseInt(matches[2]),
            months: matches?.[3] === undefined ? 0 : parseInt(matches[3]),
            weeks: matches?.[4] === undefined ? 0 : parseInt(matches[4]),
            days: matches?.[5] === undefined ? 0 : parseInt(matches[5]),
            hours: matches?.[6] === undefined ? 0 : parseInt(matches[6]),
            minutes: matches?.[7] === undefined ? 0 : parseInt(matches[7]),
            seconds: matches?.[8] === undefined ? 0 : parseInt(matches[8]),
        };
        processDuration(duration);
        return duration;
    } else {
        return null;
    }
};

const processDuration = (duration: any) => {
    processDurationField(duration, 'years', 'months', 12);
    processDurationField(duration, 'days', 'hours', 24);
    processDurationField(duration, 'hours', 'minutes', 60);
    processDurationField(duration, 'minutes', 'seconds', 60);
};

const processDurationField = (duration: any, bigField: string, smallField: string, num: number) => {
    const div = Math.floor(duration[smallField] / num);
    const remainder = duration[smallField] % num;
    if (div > 0) {
        duration[bigField] = duration[bigField] + div;
        duration[smallField] = remainder;
    }
};
