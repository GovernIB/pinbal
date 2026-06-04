const componentsEn = {
    app: {
        menu: {
            home: 'Home',
        },
        auth: {
            logout: 'Log out',
        },
        error: {
            '404': 'Not found',
            '503': 'No connection',
        },
        offline: {
            message: 'Server connection lost',
        },
    },
    datacommon: {
        back: {
            label: 'Go back',
        },
        details: {
            label: 'Details',
        },
        create: {
            label: 'Create',
        },
        update: {
            label: 'Update',
        },
        delete: {
            label: 'Delete',
            single: {
                label: 'Confirmation',
                confirm: "Are you sure you want to delete this item (this action can't be undone)?",
                success: 'Deleted items',
                error: 'Error deleting items',
            },
            multiple: {
                label: 'Confirmation',
                confirm:
                    "Are you sure you want to delete {{count}} selected items (this action can't be undone)?",
                success: '{{count}} items deleted',
                error: "{{count}} items couldn't be deleted",
            },
        },
        export: {
            label: 'Export',
        },
        refresh: {
            label: 'Refresh',
        },
        quickfilter: {
            label: 'Quick filter',
        },
        toolbar: {
            error: 'An error has occurred',
        },
        noRows: 'No data',
        error: 'Error',
    },
    grid: {
        selection: {
            one: '1 selected row ',
            multiple: '{{count}} selected rows',
        },
        edit: {
            save: 'Save',
            cancel: 'Cancel',
        },
        row: {
            single: 'row',
            multiple: 'rows',
        },
        footer: {
            pageInfo: '{{from}} to {{to}} of {{count}}',
            sizeAuto: 'Automatic',
            pageSizeTitle: 'Number of items per page',
        },
        requestPending: 'Searching...',
    },
    form: {
        goBack: {
            title: 'Go back',
        },
        revert: {
            title: 'Undo changes',
            confirm: 'Are you sure you want to revert the changes in the form?',
        },
        create: {
            title: 'Create',
            success: 'Element created',
            error: 'Error creating element',
        },
        update: {
            title: 'Update',
            success: 'Element updated',
            error: 'Error updating element',
            wrong_resource_type: 'Couldn\'t save forms with resource type "{{resourceType}}"',
        },
        delete: {
            title: 'Delete',
            confirm: "Are you sure you want to delete this item (this action can't be undone)?",
            success: 'Element deleted',
            error: 'Error deleting element',
        },
        field: {
            enum: {
                clear: 'Clear',
                noOptions: 'No options',
            },
            reference: {
                open: 'Open',
                close: 'Close',
                clear: 'Clear',
                loading: 'Loading...',
                noOptions: 'No options',
                page: 'Showing {{size}} out of {{totalElements}} elements',
                advanced: {
                    title: 'Select value',
                },
            },
            checkboxSelect: {
                true: 'Yes',
                false: 'No',
            },
        },
        dialog: {
            create: 'Create',
            update: 'Update',
        },
        validate: {
            error: 'Validation error',
            saveErrors: 'There are validation errors',
        },
        blocker: "Are you sure you want to exit this form? You may lose the changes you've made.",
    },
    actionreport: {
        action: {
            confirm: {
                title: 'Confirmation',
                message: 'Are you sure you want to execute the action {{action}}?',
            },
            success: 'Action successfully executed',
            error: 'Error executing action',
        },
        report: {
            success: 'Report successfully generated',
            error: 'Error generating report',
        },
    },
    copyToClipboard: {
        copy: 'Copy',
        default: 'Content copied to clipboard',
        error: 'Error copying content to clipboard',
    },
    buttons: {
        answerRequired: {
            accept: 'Accept',
            cancel: 'Cancel',
        },
        confirm: {
            accept: 'Accept',
            cancel: 'Cancel',
        },
        form: {
            save: 'Save',
            cancel: 'Cancel',
        },
        action: {
            exec: 'Execute',
            cancel: 'Cancel',
        },
        report: {
            generate: 'Generate',
            cancel: 'Cancel',
        },
        misc: {
            close: 'Close',
            retry: 'Retry',
        },
    },
};

export default componentsEn;
