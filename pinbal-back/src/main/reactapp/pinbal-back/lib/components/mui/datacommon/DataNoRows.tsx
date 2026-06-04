import React from 'react';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import Icon from '@mui/material/Icon';
import { useBaseAppContext } from '../../BaseAppContext';

type DataNoRowsProps = {
    icon?: string;
    message?: string;
};

const DataNoRows: React.FC<DataNoRowsProps> = (props) => {
    const { icon, message } = props;
    const { t } = useBaseAppContext();
    const ref = React.useRef<HTMLDivElement>(null);
    React.useEffect(() => {
        if (ref.current?.parentElement) {
            ref.current.parentElement.style.display = 'flex';
            ref.current.parentElement.style.alignItems = 'center';
        }
    }, []);
    return (
        <Box
            ref={ref}
            sx={{
                width: '100%',
                textAlign: 'center',
                p: 2,
            }}>
            <Icon fontSize="large" color="disabled">
                {icon ?? 'block'}
            </Icon>
            {/* Se usa component="div" per a evitar error per ARIA [role] incorrecte */}
            <Typography component="div" variant="h5" color="text.secondary">
                {message ?? t('datacommon.noRows')}
            </Typography>
        </Box>
    );
};

export default DataNoRows;
