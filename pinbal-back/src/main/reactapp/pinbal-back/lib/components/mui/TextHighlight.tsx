import Typography from '@mui/material/Typography';

export const TextHighlight: React.FC<{ text: string; match?: string; ignoreCase?: boolean }> = (
    props
) => {
    const { text, match, ignoreCase } = props;
    if (!match) {
        return <Typography>{text}</Typography>;
    }
    const escapedMatch = match.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
    const flags = ignoreCase ? 'gi' : 'g';
    const pattern = new RegExp(`(${escapedMatch})`, flags);
    const parts = text.split(pattern);
    return (
        <Typography>
            {parts.map((part, index) =>
                pattern.test(part) ? (
                    <mark key={index}>{part}</mark>
                ) : (
                    <span key={index}>{part}</span>
                )
            )}
        </Typography>
    );
};
