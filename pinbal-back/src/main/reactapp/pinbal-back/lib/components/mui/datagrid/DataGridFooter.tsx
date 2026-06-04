import React from 'react';
import Box from '@mui/material/Box';
import Icon from '@mui/material/Icon';
import IconButton from '@mui/material/IconButton';
import {
    GridFooterContainer,
    useGridApiContext,
    useGridSelector,
    gridPageSelector,
    gridPageSizeSelector,
    gridRowSelectionCountSelector,
    GridPaginationModel,
    GridRowSelectionModel,
} from '@mui/x-data-grid-pro';
import FormControl from '@mui/material/FormControl';
import Select from '@mui/material/Select';
import MenuItem from '@mui/material/MenuItem';
import Pagination from '@mui/material/Pagination';
import { useBaseAppContext } from '../../BaseAppContext';
import { DEFAULT_ROW_SELECTION } from './DataGridContext';

type DataGridFooterSelectionProps = {
    setRowSelectionModel: (rowSelectionModel: GridRowSelectionModel) => void;
};

type DataGridFooterPaginationProps = {
    paginationModel: GridPaginationModel;
    pageInfo: any;
    pageSizeOptions: number[];
    autoPageSize: boolean;
    setAutoPageSize: (value: boolean) => void;
};

type DataGridFooterProps = {
    selectionActive: boolean;
    paginationActive: boolean;
    setRowSelectionModel: (rowSelectionModel: GridRowSelectionModel) => void;
} & DataGridFooterPaginationProps;

const DataGridFooterSelection: React.FC<DataGridFooterSelectionProps> = (props) => {
    const { setRowSelectionModel } = props;
    const { t } = useBaseAppContext();
    const apiRef = useGridApiContext();
    const selectedRowCount = useGridSelector(apiRef, gridRowSelectionCountSelector);
    const handleClearClick = () => {
        setRowSelectionModel(DEFAULT_ROW_SELECTION);
    };
    const selectedMessage =
        selectedRowCount > 1
            ? t('grid.selection.multiple', { count: selectedRowCount })
            : t('grid.selection.one');
    return (
        <Box sx={{ ml: 2 }}>
            {selectedRowCount ? selectedMessage : null}
            {selectedRowCount ? (
                <IconButton onClick={handleClearClick} size="small">
                    <Icon fontSize="small">clear</Icon>
                </IconButton>
            ) : null}
        </Box>
    );
};

const GridFooterPagination: React.FC<DataGridFooterPaginationProps> = (props) => {
    const { paginationModel, pageInfo, pageSizeOptions, autoPageSize, setAutoPageSize } = props;
    const { t } = useBaseAppContext();
    const apiRef = useGridApiContext();
    const page = useGridSelector(apiRef, gridPageSelector);
    const pageSize = useGridSelector(apiRef, gridPageSizeSelector);
    if (pageInfo?.totalElements) {
        const pageCount =
            pageInfo?.totalElements && pageSize
                ? Math.ceil(pageInfo.totalElements / pageSize)
                : undefined;
        const pageRowCount =
            pageInfo?.totalElements <= pageSize
                ? pageInfo?.totalElements
                : page === (pageCount ?? 0) - 1
                  ? pageInfo?.totalElements % pageSize || pageSize
                  : pageSize;
        const firstElementIndex = page * pageSize + 1;
        const lastElement = Math.min(firstElementIndex + pageRowCount - 1, pageInfo?.totalElements);
        const boxStyle = { display: 'flex', justifContent: 'flex-end', alignItems: 'center' };
        const currentPageSize = paginationModel?.pageSize;
        const enableAutoPageSizeOption = pageSizeOptions != null && pageSizeOptions.includes(-1);
        return (
            <Box style={boxStyle}>
                {pageSizeOptions && (
                    <Box sx={{ mr: 4 }}>
                        <FormControl size="small">
                            <Select
                                value={!autoPageSize ? currentPageSize : -1}
                                title={t('grid.footer.pageSizeTitle')}
                                onChange={(event) => {
                                    if (event.target.value === -1) {
                                        setAutoPageSize(true);
                                    } else {
                                        setAutoPageSize(false);
                                        apiRef.current.setPaginationModel({
                                            page,
                                            pageSize: event.target.value as number,
                                        });
                                    }
                                }}>
                                {enableAutoPageSizeOption && (
                                    <MenuItem value={-1}>{t('grid.footer.sizeAuto')}</MenuItem>
                                )}
                                {pageSizeOptions
                                    .filter((o: number) => o !== -1)
                                    .map((o: number) => (
                                        <MenuItem value={o}>{o}</MenuItem>
                                    ))}
                            </Select>
                        </FormControl>
                    </Box>
                )}
                <Box>
                    {pageInfo != null
                        ? t('grid.footer.pageInfo', {
                              from: firstElementIndex,
                              to: lastElement,
                              count: pageInfo.totalElements,
                          })
                        : ''}
                </Box>
                <Pagination
                    color="primary"
                    count={pageSize ? pageCount : 0}
                    page={page + 1}
                    onChange={(_event, value) => apiRef.current.setPage(value - 1)}
                />
            </Box>
        );
    } else {
        return (
            <Pagination
                color="primary"
                count={0}
                onChange={(_event, value) => apiRef.current.setPage(value - 1)}
            />
        );
    }
};

const DataGridFooterNumRows: React.FC<{ pageInfo: any }> = (props) => {
    const { pageInfo } = props;
    const { t } = useBaseAppContext();
    return (
        pageInfo != null && (
            <Box sx={{ mr: 2 }}>
                {pageInfo.totalElements}{' '}
                {pageInfo.totalElements == 1 ? t('grid.row.single') : t('grid.row.multiple')}
            </Box>
        )
    );
};

const DataGridFooter: React.FC<DataGridFooterProps> = (props) => {
    const {
        selectionActive,
        paginationActive,
        paginationModel,
        pageInfo,
        pageSizeOptions,
        setRowSelectionModel,
        autoPageSize,
        setAutoPageSize,
    } = props;
    const showFooter = selectionActive || paginationActive;
    return showFooter ? (
        <GridFooterContainer>
            <DataGridFooterSelection setRowSelectionModel={setRowSelectionModel} />
            {paginationActive ? (
                <GridFooterPagination
                    paginationModel={paginationModel}
                    pageInfo={pageInfo}
                    pageSizeOptions={pageSizeOptions}
                    autoPageSize={autoPageSize}
                    setAutoPageSize={setAutoPageSize}
                />
            ) : (
                <DataGridFooterNumRows pageInfo={pageInfo} />
            )}
        </GridFooterContainer>
    ) : null;
};

export default DataGridFooter;
