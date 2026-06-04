const componentsCa = {
    app: {
        menu: {
            home: 'Inici',
        },
        auth: {
            logout: 'Tancar sessió',
        },
        error: {
            '404': 'No trobat',
            '503': 'Sense connexió',
        },
        offline: {
            message: 'Sense connexió amb el servidor',
        },
    },
    datacommon: {
        back: {
            label: 'Tornar enrere',
        },
        details: {
            label: 'Detalls',
        },
        create: {
            label: 'Crear',
        },
        update: {
            label: 'Modificar',
        },
        delete: {
            label: 'Esborrar',
            single: {
                label: 'Confirmació',
                confirm:
                    'Estau segur que voleu esborrar aquest element (aquesta acció no es pot desfer)?',
                success: 'Element esborrat',
                error: 'Error esborrant element',
            },
            multiple: {
                label: 'Confirmació',
                confirm:
                    'Estau segur que voleu esborrar els {{count}} elements seleccionats (aquesta acció no es pot desfer)?',
                success: '{{count}} elements esborrats',
                error: "No s'han pogut esborrar {{count}} elements",
            },
        },
        export: {
            label: 'Exportar',
        },
        refresh: {
            label: 'Refrescar',
        },
        quickfilter: {
            label: 'Filtre ràpid',
        },
        toolbar: {
            error: "S'ha produit un error",
        },
        noRows: 'Sense resultats',
        error: 'Error',
    },
    grid: {
        selection: {
            one: '1 fila seleccionada',
            multiple: '{{count}} files seleccionades',
        },
        edit: {
            save: 'Desar',
            cancel: 'Cancel·lar',
        },
        row: {
            single: 'fila',
            multiple: 'files',
        },
        footer: {
            pageInfo: '{{from}} a {{to}} de {{count}}',
            sizeAuto: 'Automàtic',
            pageSizeTitle: "Nombre d'elements per pàgina",
        },
        requestPending: 'Pendent de consultar',
    },
    form: {
        goBack: {
            title: 'Tornar enrere',
        },
        revert: {
            title: 'Desfer canvis',
            confirm: 'Estau segur que voleu desfer els canvis fets al formulari?',
        },
        create: {
            title: 'Crear',
            success: 'Element creat',
            error: 'Error creant element',
        },
        update: {
            title: 'Modificar',
            success: 'Element modificat',
            error: 'Error modificant element',
            wrong_resource_type:
                'No es possible desar els formularis amb tipus de recurs "{{resourceType}}"',
        },
        delete: {
            title: 'Esborrar',
            confirm:
                'Estau segur que voleu esborrar aquest element (aquesta acció no es pot desfer)?',
            success: 'Element esborrat',
            error: 'Error esborrant element',
        },
        field: {
            enum: {
                clear: 'Esborrar',
                noOptions: 'Sense opcions',
            },
            reference: {
                open: 'Obrir',
                close: 'Tancar',
                clear: 'Esborrar',
                loading: 'Carregant...',
                noOptions: 'Sense opcions',
                page: 'Mostrant {{size}} de {{totalElements}} elements',
                advanced: {
                    title: 'Seleccionar valor',
                },
            },
            checkboxSelect: {
                true: 'Si',
                false: 'No',
            },
        },
        dialog: {
            create: 'Crear',
            update: 'Modificar',
        },
        validate: {
            error: 'Error de validació',
            saveErrors: 'Hi ha errors de validació',
        },
        blocker:
            "Està segur de que vol sortir d'aquest formulari? És possible que es perdin els canvis que heu fet.",
    },
    actionreport: {
        action: {
            confirm: {
                title: 'Confirmació',
                message: "Estau segur que voleu executar l'acció {{action}}?",
            },
            success: 'Acció executada correctament',
            error: "Error executant l'acció",
        },
        report: {
            success: 'Informe generat correctament',
            error: "Error generant l'informe",
        },
    },
    copyToClipboard: {
        copy: 'Copiar',
        default: 'Contingut copiat al porta-retalls',
        error: 'Error copiant contingut al porta-retalls',
    },
    buttons: {
        answerRequired: {
            accept: 'Acceptar',
            cancel: 'Cancel·lar',
        },
        confirm: {
            accept: 'Acceptar',
            cancel: 'Cancel·lar',
        },
        form: {
            save: 'Desar',
            cancel: 'Cancel·lar',
        },
        action: {
            exec: 'Executar',
            cancel: 'Cancel·lar',
        },
        report: {
            generate: 'Generar',
            cancel: 'Cancel·lar',
        },
        misc: {
            close: 'Tancar',
            retry: 'Tornar a provar',
        },
    },
};

export default componentsCa;
