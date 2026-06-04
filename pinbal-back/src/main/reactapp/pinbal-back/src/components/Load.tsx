import React from "react";
import {BasePage} from 'reactlib';
import {Box, CircularProgress} from '@mui/material';

export type LoadProps = React.PropsWithChildren & {
    value: any,
    noEffect?: boolean
};

const Load: React.FC<LoadProps> = (props:LoadProps) => {
    const { value, noEffect, children } = props;

    if(!!value) {
        return children
    }

    if(noEffect){
        return <></>
    }

    return <BasePage>
        <Box
            sx={{
                display: 'flex',
                justifyContent: 'center',
                alignItems: 'center',
            }}>
            <CircularProgress/>
        </Box>
    </BasePage>;
}

export default Load;