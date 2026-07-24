import React from 'react';
import { SimpleTreeView } from '@mui/x-tree-view/SimpleTreeView';
import { TreeItem } from '@mui/x-tree-view/TreeItem';
import { useResourceApiService } from 'reactlib';

const PropietatsGroupTreeItems: React.FC<{
    configGroups?: any[];
    parentCode?: string;
}> = ({ configGroups, parentCode }) => {
    const configGroupsFilterByParentCode = (parentCode?: string) => {
        return configGroups?.filter((g) => (parentCode ?? null) === (g.parentCode ?? null));
    };
    const filteredGroups = configGroupsFilterByParentCode(parentCode);

    return (
        <>
            {filteredGroups?.map((g) => (
                <TreeItem key={g.id} itemId={g.id} label={g.description}>
                    {configGroupsFilterByParentCode(g.id)?.length ? (
                        <PropietatsGroupTreeItems configGroups={configGroups} parentCode={g.id} />
                    ) : null}
                </TreeItem>
            ))}
        </>
    );
};

export const PropietatsGroups: React.FC<{
    quickFilter?: string;
    onChange: (group: any) => void;
}> = ({ quickFilter, onChange }) => {
    const { isReady: apiIsReady, find: apiFind } = useResourceApiService('configGroupResource');
    const [configGroups, setConfigGroups] = React.useState<any[]>();
    const [selectedGroupId, setSelectedGroupId] = React.useState<string>();
    const [selectedItems, setSelectedItems] = React.useState<string>('');

    React.useEffect(() => {
        if (apiIsReady) {
            const args = {
                filter: quickFilter?.length
                    ? "exists(configs.key~'%" +
                      quickFilter +
                      "%' or configs.value~'%" +
                      quickFilter +
                      "%') or exists(innerConfigs.configs.key~'%" +
                      quickFilter +
                      "%' or innerConfigs.configs.value~'%" +
                      quickFilter +
                      "%')"
                    : undefined,
                sorts: ['position,asc'],
                unpaged: true,
            };
            apiFind(args).then((response) => {
                const groups = response.rows;
                setConfigGroups(groups);
                if (groups.length) {
                    const isSelectedGroupIdInConfigGroups = groups.find((g: any) => g.id === selectedGroupId);
                    if (!isSelectedGroupIdInConfigGroups) {
                        const firstId = String(groups[0].id);
                        setSelectedGroupId(firstId);
                        setSelectedItems(firstId);
                    }
                }
            });
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [apiIsReady, quickFilter]);

    React.useEffect(() => {
        onChange?.(configGroups?.find((g) => g.id === selectedGroupId));
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [configGroups, selectedGroupId]);

    return (
        <SimpleTreeView
            selectedItems={selectedItems}
            onSelectedItemsChange={(_event, ids) => {
                setSelectedGroupId(ids ?? undefined);
                if (ids) {
                    setSelectedItems(ids);
                }
            }}
            sx={{
                '& .MuiTreeItem-content': {
                    minHeight: 40,
                    paddingY: 0.5,
                },
                '& .MuiTreeItem-label': {
                    fontSize: '14px',
                },
            }}
        >
            <PropietatsGroupTreeItems configGroups={configGroups} />
        </SimpleTreeView>
    );
};

export default PropietatsGroups;
