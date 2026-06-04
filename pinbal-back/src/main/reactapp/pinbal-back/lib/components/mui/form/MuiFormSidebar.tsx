import React from 'react';
import Drawer from '@mui/material/Drawer';
import MuiToolbar from '@mui/material/Toolbar';
import IconButton from '@mui/material/IconButton';
import Icon from '@mui/material/Icon';
import Box from '@mui/material/Box';
import { useBaseAppContext } from '../../BaseAppContext';
import { useFormDialogButtons } from '../../AppButtons';
import { FormApi } from '../../form/FormContext';
import Toolbar from '../Toolbar';
import ToolbarButtons from '../ToolbarButtons';
import MuiForm from './MuiForm';

export type FormSidebarApi = {
    show: (id?: any, additionalData?: any) => Promise<any>;
    hide: () => void;
};

export type FormSidebarProps = React.PropsWithChildren & {
    resourceName: string;
    title?: string;
    resourceTitle?: string;
    autoClose?: true;
    drawerWidth?: number;
    apiRef: React.RefObject<FormSidebarApi | null>;
};

export const MuiFormSidebar: React.FC<FormSidebarProps> = (props) => {
    const {
        resourceName,
        title: titleProp,
        resourceTitle,
        autoClose,
        drawerWidth = 500,
        apiRef,
        children,
    } = props;
    const { t } = useBaseAppContext();
    const formDialogButtons = useFormDialogButtons();
    const drawerRef = React.useRef<any>(null);
    const formApiRef = React.useRef<FormApi | any>({});
    const [open, setOpen] = React.useState<boolean>(false);
    const [id, setId] = React.useState<any>();
    const [additionalData, setAdditionalData] = React.useState<any>();
    const [resolveFn, setResolveFn] = React.useState<(value?: any) => void>();
    const [rejectFn, setRejectFn] = React.useState<(value?: any) => void>();
    const title =
        titleProp ??
        (id != null ? t('form.dialog.update') : t('form.dialog.create')) +
            ' ' +
            (resourceTitle ?? resourceName);
    const show = (id?: any, additionalData?: any) => {
        setId(id);
        setAdditionalData(additionalData);
        setTimeout(() => setOpen(true), 0);
        return new Promise<string>((resolve, reject) => {
            setResolveFn(() => resolve);
            setRejectFn(() => reject);
        });
    };
    const hide = () => {
        rejectFn?.();
        setOpen(false);
    };
    if (apiRef) {
        apiRef.current = {
            show,
            hide,
        };
    }
    const windowClickHandler = (event: MouseEvent) => {
        if (open && autoClose) {
            const insideDrawer = event.x > window.innerWidth - drawerWidth && event.y > 64;
            const insideDialog = (event.target as any)?.closest('.MuiDialog-container') != null;
            const insideAutocompletePopper =
                (event.target as any)?.closest('.MuiAutocomplete-popper') != null;
            /*console.log('>>> target', event.target)
            console.log('>>> insideDrawer', insideDrawer)
            console.log('>>> insideDialog', insideDialog)
            console.log('>>> insideAutocompletePopper', insideAutocompletePopper)*/
            !insideDrawer && !insideDialog && !insideAutocompletePopper && setOpen(false);
        }
    };
    const buttonCloseHandler = (value?: any) => {
        if (value) {
            formApiRef.current
                .save()
                .then((value: any) => {
                    // S'ha fet click al botó desar i tot ha anat be
                    setOpen(false);
                    resolveFn?.(value);
                })
                .catch(() => {
                    // S'ha fet click al botó desar i s'han produit errors
                });
        } else {
            // S'ha fet click al botó cancel·lar
            hide();
        }
    };
    React.useEffect(() => {
        if (open) {
            window.addEventListener('click', windowClickHandler);
            return () => window.removeEventListener('click', windowClickHandler);
        }
    }, [open]);
    return (
        <Drawer
            anchor="right"
            open={open}
            onClose={() => setOpen((o) => !o)}
            hideBackdrop
            ref={drawerRef}
            sx={{
                width: drawerWidth,
                flexShrink: 0,
                [`& .MuiDrawer-paper`]: { width: drawerWidth, boxSizing: 'border-box' },
            }}>
            <MuiToolbar />
            <Toolbar
                title={title}
                elementsWithPositions={[
                    {
                        position: 0,
                        element: (
                            <IconButton size="small" onClick={() => setOpen(false)} sx={{ mr: 2 }}>
                                <Icon fontSize="small">close</Icon>
                            </IconButton>
                        ),
                    },
                ]}
                sx={{ position: 'sticky', top: 0, backgroundColor: 'white', zIndex: 10, px: 2 }}
            />
            <Box sx={{ px: 2, overflow: 'auto' }}>
                <MuiForm
                    resourceName={resourceName}
                    id={id}
                    additionalData={additionalData}
                    apiRef={formApiRef}
                    hiddenToolbar
                    componentProps={{ sx: { pb: 4 } }}>
                    {children}
                </MuiForm>
            </Box>
            <ToolbarButtons
                buttons={formDialogButtons}
                handleClose={buttonCloseHandler}
                bottomAligned
            />
        </Drawer>
    );
};

export default MuiFormSidebar;
