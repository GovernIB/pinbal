import {Box, Tab, Tabs} from "@mui/material";
import {useEffect, useState} from "react";
import {StyledBadge} from "./StyledBadge.tsx";
import Load from "./Load.tsx";

type TabProps = {
    value: string;
    label: string;
    content: any;
    title?: string;
    badge?: string;
    badgeColor?: 'primary' | 'secondary' | 'default' | 'error' | 'info' | 'success' | 'warning' | string;
    disabled?: boolean;
    hidden?: boolean;
    showZero?: boolean;
    error?: boolean;
};

const TabPanel = (props:any) => {
    const { children, value, index, ...other } = props;

    return (
        <div
            role="tabpanel"
            hidden={value !== index}
            id={`simple-tabpanel-${index}`}
            aria-labelledby={`simple-tab-${index}`}
            style={{height: '100%'}}
            {...other}
        >
            {value === index && (
                <Box p={2} height={'100%'}>
                    {children}
                </Box>
            )}
        </div>
    );
}

const TabComponent = (props :any) => {
    const [valueDef, setValueDef] = useState<any>();
    const { tabs, headerAdditionalData, defaultValue, value = valueDef, onChange:setValue = setValueDef, ...other}=props;

    const handleChange = (_event :any, newValue :string) : void => {
        if (tabs.some((tab:TabProps)=>tab?.value==newValue)) {
            setValue(newValue);
        }
    };

    useEffect(() => {
        if (!value && defaultValue) {
            setValue(defaultValue)
        }else if (!tabs.some((tab:TabProps)=>tab?.value==value)) {
            setValue(tabs?.[0]?.value);
        }
    }, [tabs, value]);

    return <Load value={tabs?.length}>
    <Box sx={{display: 'flex', flexDirection: 'column', height: '100%'}}>
        <Box sx={{flexGrow: 1, display: 'flex', flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', width: '100%'}}>
            <Tabs
                value={value}
                onChange={handleChange}
                {...other}
                sx={{px: 1}}
            >
                {tabs.filter((tab:TabProps)=>!tab.hidden).map((tab:TabProps) => {
                    const {title, value: tabValue, label, disabled, showZero = false, content, badge = 0, badgeColor= 'primary', error = false} = tab;

                    const errorProps = error ?{
                        sx: {color: 'error.main'}
                    } :{}

                    return <Tab value={tabValue} disabled={disabled} title={title} content={content} key={"tab-" + tabValue} {...errorProps} label={
                        <StyledBadge badgeContent={badge} badgecolor={badgeColor} showZero={showZero}>{label}</StyledBadge>}/>
                })}
            </Tabs>
            {headerAdditionalData}
        </Box>
        {tabs.map((tab:TabProps) =>
            <TabPanel value={value} index={tab.value} key={"tab-panel-"+tab.value}>
                {!tab.hidden && tab.content}
            </TabPanel>
        )}
    </Box>
    </Load>
}

export default TabComponent;