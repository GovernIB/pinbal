import React from 'react';
import Avatar from '@mui/material/Avatar';
import Icon from '@mui/material/Icon';
import { useTheme } from '@mui/material/styles';

const stringToColor = (str: string) => {
    let hash = 0;
    let i;
    for (i = 0; i < str.length; i += 1) {
        hash = str.charCodeAt(i) + ((hash << 5) - hash);
    }
    let color = '#';
    for (i = 0; i < 3; i += 1) {
        const value = (hash >> (i * 8)) & 0xff;
        color += `00${value.toString(16)}`.slice(-2);
    }
    return color;
};

type TextAvatarProps = {
    text: string;
};
type IconAvatarProps = {
    icon: string;
    title?: string;
};

export const TextAvatar: React.FC<TextAvatarProps> = (props) => {
    const { text } = props;
    const theme = useTheme();
    const textSplit = text.split(' ');
    const avatarText0 = textSplit[0][0].toUpperCase();
    const avatarText1 =
        textSplit.length > 1 ? textSplit[1][0].toUpperCase() : textSplit[0][1].toUpperCase();
    const bgColor = stringToColor(text);
    const textColor = theme.palette.getContrastText(bgColor);
    return (
        <Avatar alt={text} title={text} sx={{ color: textColor, bgcolor: bgColor }}>
            {avatarText0 + avatarText1}
        </Avatar>
    );
};

export const IconAvatar: React.FC<IconAvatarProps> = (props) => {
    const { icon, title } = props;
    const theme = useTheme();
    const bgColor = stringToColor(icon);
    const textColor = theme.palette.getContrastText(bgColor);
    return (
        <Avatar alt={title} title={title} sx={{ color: textColor, bgcolor: bgColor }}>
            <Icon title={title}>{icon}</Icon>
        </Avatar>
    );
};
