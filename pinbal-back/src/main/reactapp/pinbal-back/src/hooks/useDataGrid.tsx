import React from 'react';
import { useTranslation } from 'react-i18next';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import { useTheme } from '@mui/material/styles';
import {
    GridTreeDataGroupingCell,
    useGridApiRef,
    gridRowNodeSelector,
    type DataGridProProps,
    type GridEventListener,
} from '@mui/x-data-grid-pro';
import { MuiFilter } from 'reactlib';
import { usePinbalContext } from '../components/PinbalContext.ts';

export const useDatagridPageSizeOptionsProps = (
    innerScroll: boolean = true,
    autoHeightMinHeight?: string
) => {
    const { currentUser, currentUserGridPageSizeOptions } = usePinbalContext();
    const [autoPageSize, setAutoPageSize] = React.useState<boolean>();
    const handlePersistentStateChange = (state: any) => {
        !innerScroll && setAutoPageSize(state.autoPageSize);
    };
    return {
        defaultPaginationModel: {
            page: 0,
            pageSize: currentUser.numElementsPaginaDefecteAsInt ?? -1,
        },
        pageSizeOptions: currentUserGridPageSizeOptions,
        onPersistentStateChange: handlePersistentStateChange,
        ...(!innerScroll &&
            !autoPageSize && {
                autoHeight: true,
                sx: { minHeight: autoHeightMinHeight ?? '400px' },
            }),
    };
};

export const useDatagridFilterProps = (
    resourceName: string,
    code: string,
    springFilterBuilder: (data: any) => string | undefined,
    content: React.ReactElement,
    minHeight: string = '40px',
    initialData?: any,
) => {
    const [autoFindDisabled, setAutoFindDisabled] = React.useState<boolean>(true);
    const handleSpringFilterChange = (springFilter: string | undefined) => {
        const springFilterEmpty = springFilter == null || springFilter === '';
        autoFindDisabled && setAutoFindDisabled(!springFilterEmpty);
    };
    const filterComponent = (
        <MuiFilter
            resourceName={resourceName}
            code={code}
            persistentStateActive
            springFilterBuilder={springFilterBuilder}
            onSpringFilterChange={handleSpringFilterChange}
            componentProps={{ sx: { mb: 0, mt: 0 } }}
            commonFieldComponentProps={{ size: 'small' }}
            initialData={initialData}
        >
            {content}
        </MuiFilter>
    );
    return {
        loading: autoFindDisabled ? autoFindDisabled : undefined,
        autoFindDisabled,
        toolbarHideQuickFilter: true as true,
        toolbarAdditionalRow: filterComponent,
        toolbarAdditionalRowMinHeight: minHeight,
    };
};

export const useDatagridTreeData = (
    active: boolean,
    headerName: string,
    reorderingActive: boolean,
    defaultGroupingExpansionDepth?: number,
    groupingColDefProps?: any
) => {
    const { t } = useTranslation();
    const theme = useTheme();
    const datagridApiRef = useGridApiRef();
    const changeAllNodesExpansion = (expanded: boolean) => {
        const rowIds = Array.from(datagridApiRef.current?.getRowModels().keys() ?? []);
        rowIds?.forEach((id) => {
            const node = gridRowNodeSelector(datagridApiRef, id) as any;
            if (node?.children?.length) {
                if (expanded) {
                    datagridApiRef.current?.setRowChildrenExpansion(id, expanded);
                } else if (
                    defaultGroupingExpansionDepth == null ||
                    node.depth >= defaultGroupingExpansionDepth
                ) {
                    datagridApiRef.current?.setRowChildrenExpansion(id, expanded);
                }
            }
        });
    };
    const getTreeDataPath: DataGridProProps['getTreeDataPath'] = (row: any) => {
        return row.path?.map((p: any) => p.description) ?? [row.id];
    };
    const setTreeDataPath: DataGridProProps['setTreeDataPath'] = (path, row) => {
        return {
            ...row,
            path: path.map((p) => ({ id: -1, description: p })),
        };
    };
    const processRowUpdate: DataGridProProps['processRowUpdate'] = (newRow: any, oldRow: any) => {
        console.log('>>> reordenació 1', oldRow.id, oldRow.path, newRow.id, newRow.path);
    };
    const onRowOrderChange: GridEventListener<'rowOrderChange'> = (params) => {
        if (params.oldParent === params.newParent) {
            console.log('>>> reordenació 2', params);
        }
    };
    const reorderingProps = reorderingActive
        ? {
              rowReordering: true,
              setTreeDataPath,
              processRowUpdate,
              onRowOrderChange,
          }
        : {};
    return active
        ? {
              perspectives: ['TREE'],
              treeData: true as true,
              getTreeDataPath,
              datagridApiRef,
              groupingColDef: {
                  headerName,
                  renderHeader: (params: any) => {
                      return (
                          <div
                              style={{
                                  display: 'flex',
                                  flexGrow: 1,
                                  alignItems: 'center',
                                  justifyContent: 'space-between',
                              }}
                          >
                              <div>{params.colDef.headerName}</div>
                              <div>
                                  <IconButton
                                      size="small"
                                      onClick={() => changeAllNodesExpansion(false)}
                                      title={t('hook.useDataGrid.treeData.collapseAll')}
                                  >
                                      <Icon fontSize="inherit">unfold_less</Icon>
                                  </IconButton>
                                  <IconButton
                                      size="small"
                                      onClick={() => changeAllNodesExpansion(true)}
                                      title={t('hook.useDataGrid.treeData.expandAll')}
                                  >
                                      <Icon fontSize="inherit">unfold_more</Icon>
                                  </IconButton>
                              </div>
                          </div>
                      );
                  },
                  renderCell: (params: any) => {
                      return (
                          <div
                              style={{
                                  display: 'flex',
                                  color: params.rowNode.isAutoGenerated
                                      ? theme.palette.text.disabled
                                      : undefined,
                              }}
                          >
                              <GridTreeDataGroupingCell {...params} />
                          </div>
                      );
                  },
                  ...groupingColDefProps,
              },
              defaultGroupingExpansionDepth,
              ...reorderingProps,
              sx: {
                  '& [data-field="__tree_data_group__"] .MuiDataGrid-columnHeaderTitleContainerContent':
                      {
                          flexGrow: 1,
                      },
              },
          }
        : {
              paginationActive: true as true,
          };
};
