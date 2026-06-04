import React from 'react';
import Box from '@mui/material/Box';
import Tabs from '@mui/material/Tabs';
import Tab from '@mui/material/Tab';

interface TabPanelProps {
    children?: React.ReactNode;
    id: string;
    activeId: string;
}

interface TabConfig {
    id: string;
    label: string;
    content: React.ReactElement;
    hidden?: boolean;
    disabled?: boolean;
}

const a11yProps = (id: string) => {
    return {
        id: `simple-tab-${id}`,
        'aria-controls': `simple-tabpanel-${id}`,
    };
};

const CustomTabPanel = (props: TabPanelProps) => {
    const { children, id, activeId, ...other } = props;
    return (
        <div
            role="tabpanel"
            hidden={activeId !== id}
            id={`simple-tabpanel-${id}`}
            aria-labelledby={`simple-tab-${id}`}
            style={{ height: '100%', minHeight: 0 }}
            {...other}
        >
            {activeId === id && <Box sx={{ pt: 3, height: '100%' }}>{children}</Box>}
        </div>
    );
};

const CustomTabs: React.FC<{ tabs: TabConfig[] }> = ({ tabs }) => {
    const visibleTabs = tabs.filter((tab) => !tab.hidden);
    const [activeId, setActiveId] = React.useState<string>(visibleTabs[0]?.id);

    const handleChange = (_event: React.SyntheticEvent, newValue: string) => {
        setActiveId(newValue);
    };

    return (
        <Box sx={{ width: '100%', height: '100%', display: 'flex', flexDirection: 'column' }}>
            <Box sx={{ borderBottom: 1, borderColor: 'divider' }}>
                <Tabs value={activeId} onChange={handleChange} aria-label="basic tabs">
                    {visibleTabs.map((tab) => (
                        <Tab
                            key={tab.id}
                            value={tab.id}
                            label={tab.label}
                            disabled={tab?.disabled}
                            {...a11yProps(tab.id)}
                        />
                    ))}
                </Tabs>
            </Box>
            {visibleTabs.map((tab) => (
                <CustomTabPanel key={tab.id} id={tab.id} activeId={activeId}>
                    {tab.content}
                </CustomTabPanel>
            ))}
        </Box>
    );
};

export default CustomTabs;
