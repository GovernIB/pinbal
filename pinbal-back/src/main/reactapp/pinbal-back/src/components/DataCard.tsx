import React from 'react';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import { useTheme } from '@mui/material/styles';
import { formattedFieldValue } from 'reactlib';

const DataCard: React.FC<
    { title: string; data: Record<string, string | React.ReactElement>[] } & any
> = (props) => {
    const { title, data, sx, ...otherProps } = props;
    const theme = useTheme();
    const bgColor =
        theme.palette.mode === 'light' ? theme.palette.grey[200] : theme.palette.grey[900];
    return (
        data && (
            <Card {...otherProps} sx={{ '& .MuiCardContent-root ': { padding: 0 }, ...sx }}>
                <CardContent>
                    <Table aria-label="simple table">
                        <TableHead>
                            <TableRow>
                                <TableCell
                                    colSpan={2}
                                    sx={{ fontSize: 18, backgroundColor: bgColor, py: 1.5 }}
                                >
                                    {title}
                                </TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {Object.entries(data).map(([key, value]) => (
                                <TableRow key={key}>
                                    <TableCell
                                        component="th"
                                        scope="row"
                                        sx={{
                                            borderRight: 1,
                                            borderColor: 'divider',
                                            fontWeight: 500,
                                            py: 1.5,
                                        }}
                                    >
                                        {Object.keys(value as any)[0]}
                                    </TableCell>
                                    <TableCell
                                        component="th"
                                        scope="row"
                                        sx={{
                                            py: 1.5,
                                        }}
                                    >
                                        {
                                            Object.values(value as any)[0] as
                                                | string
                                                | React.ReactElement
                                        }
                                    </TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </CardContent>
            </Card>
        )
    );
};

export type DataCardRow = {
    field: string;
    label?: string;
    labelRenderer?: () => React.ReactElement;
    valueRenderer?: (
        value: any,
        formattedValue: string | undefined,
        data: any
    ) => React.ReactElement;
    formatOptions?: any;
    alwaysVisible?: boolean;
};

// Funció d'ajuda per obtenir valors niuats (ex: "user.address.street")
const getNestedValue = (obj: any, path: string) => {
    if (!obj || !path) return undefined;
    return path.split('.').reduce((acc, part) => acc && acc[part], obj);
};

const FieldsDataCardItem: React.FC<{ row: DataCardRow; fields: any[]; data: any }> = (
    props
) => {
    const { row, fields, data } = props;
    const field = fields?.find((field) => field?.name === row.field);
    const label =
        row.labelRenderer != null ? row.labelRenderer() : (row.label ?? field?.label ?? row.field);

    const rawValue = getNestedValue(data, row.field);
    const formattedValue = formattedFieldValue(rawValue, field, row.formatOptions);

    const value =
        row.valueRenderer != null
            ? row.valueRenderer(rawValue, formattedValue, data)
            : formattedValue;

    if (!row.alwaysVisible && !value) {
        return null;
    }

    return (
        <TableRow>
            <TableCell
                component="th"
                scope="row"
                sx={{
                    borderRight: 1,
                    borderColor: 'divider',
                    fontWeight: 500,
                    py: 1.5,
                }}
            >
                {label}
            </TableCell>
            <TableCell
                component="th"
                scope="row"
                sx={{
                    py: 1.5,
                }}
            >
                {value}
            </TableCell>
        </TableRow>
    );
};

export const FieldsDataCard: React.FC<
    { title?: string; rows: DataCardRow[]; fields: any[]; data: any } & any
> = (props) => {
    const { title, rows, fields, data, sx, ...otherProps } = props;
    const theme = useTheme();
    const bgColor =
        theme.palette.mode === 'light' ? theme.palette.grey[200] : theme.palette.grey[900];

    return (
        data && (
            <Card {...otherProps} sx={{ '& .MuiCardContent-root ': { padding: 0 }, ...sx }}>
                <CardContent>
                    <Table aria-label="simple table">
                        {title && (
                            <TableHead>
                                <TableRow>
                                    <TableCell
                                        colSpan={2}
                                        sx={{ fontSize: 18, backgroundColor: bgColor, py: 1.5 }}
                                    >
                                        {title}
                                    </TableCell>
                                </TableRow>
                            </TableHead>
                        )}
                        <TableBody>
                            {rows.map((row: any, index: number) => (
                                <FieldsDataCardItem
                                    key={index}
                                    row={row}
                                    fields={fields}
                                    data={data}
                                />
                            ))}
                        </TableBody>
                    </Table>
                </CardContent>
            </Card>
        )
    );
};

export default DataCard;
