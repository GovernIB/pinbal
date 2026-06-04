const componentsEs = {
    app: {
        menu: {
            home: 'Inicio',
        },
        auth: {
            logout: 'Cerrar sesión',
        },
        error: {
            '404': 'No encontrado',
            '503': 'Sin connexión',
        },
        offline: {
            message: 'Sin conexión con el servidor',
        },
    },
    datacommon: {
        back: {
            label: 'Volver atrás',
        },
        details: {
            label: 'Detalles',
        },
        create: {
            label: 'Crear',
        },
        update: {
            label: 'Modificar',
        },
        delete: {
            label: 'Borrar',
            single: {
                label: 'Confirmación',
                confirm:
                    '¿Está seguro de que desea borrar este elemento (esta acción no se puede deshacer)?',
                success: 'Elemento borrado',
                error: 'Error borrando elemento',
            },
            multiple: {
                label: 'Confirmación',
                confirm:
                    '¿Está seguro de que desea borrar los {{count}} elementos seleccionados (esta acción no se puede deshacer)?',
                success: '{{count}} elementos borrados',
                error: 'No se han podido borrar {{count}} elementos',
            },
        },
        export: {
            label: 'Exportar',
        },
        refresh: {
            label: 'Refrescar',
        },
        quickfilter: {
            label: 'Filtro rápido',
        },
        toolbar: {
            error: 'Se ha producido un error',
        },
        noRows: 'Sin resultados',
        error: 'Error',
    },
    grid: {
        selection: {
            one: '1 fila seleccionada',
            multiple: '{{count}} filas seleccionadas',
        },
        edit: {
            save: 'Guardar',
            cancel: 'Cancelar',
        },
        row: {
            single: 'fila',
            multiple: 'filas',
        },
        footer: {
            pageInfo: '{{from}} a {{to}} de {{count}}',
            sizeAuto: 'Automático',
            pageSizeTitle: 'Número de elementos por página',
        },
        requestPending: 'Pendiente de consultar',
    },
    form: {
        goBack: {
            title: 'Ir atrás',
        },
        revert: {
            title: 'Deshacer cambios',
            confirm: '¿Está seguro de que desea deshacer los cambios hechos al formulario?',
        },
        create: {
            title: 'Crear',
            success: 'Elemento creado',
            error: 'Error creando elemento',
        },
        update: {
            title: 'Modificar',
            success: 'Elemento modificado',
            error: 'Error modificando elemento',
            wrong_resource_type:
                'No es posible guardar los formularios con tipo de recurso "{{resourceType}}"',
        },
        delete: {
            title: 'Borrar',
            confirm:
                '¿Está seguro de que desea borrar este elemento (esta acción no se puede deshacer)?',
            success: 'Elemento borrado',
            error: 'Error borrando elemento',
        },
        field: {
            enum: {
                clear: 'Borrar',
                noOptions: 'Sin opciones',
            },
            reference: {
                open: 'Abrir',
                close: 'Cerrar',
                clear: 'Borrar',
                loading: 'Cargando...',
                noOptions: 'Sin opciones',
                page: 'Mostrando {{size}} de {{totalElements}} elementos',
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
            error: 'Error de validación',
            saveErrors: 'Hay errores de validación',
        },
        blocker:
            'Esta seguro de que desea salir de este formulario? Es posible que se pierdan los cambios que ha hecho.',
    },
    actionreport: {
        action: {
            confirm: {
                title: 'Confirmación',
                message: '¿Está seguro de que desea ejecutar la acción {{action}}?',
            },
            success: 'Acción ejecutada correctamente',
            error: 'Error ejecutando la acción',
        },
        report: {
            success: 'Informe generado correctamente',
            error: 'Error generando el informe',
        },
    },
    copyToClipboard: {
        copy: 'Copiar',
        default: 'Contenido copiado al portapapeles',
        error: 'Error copiando contenido en el portapapeles',
    },
    buttons: {
        answerRequired: {
            accept: 'Aceptar',
            cancel: 'Cancelar',
        },
        confirm: {
            accept: 'Aceptar',
            cancel: 'Cancelar',
        },
        form: {
            save: 'Guardar',
            cancel: 'Cancelar',
        },
        action: {
            exec: 'Ejecutar',
            cancel: 'Cancelar',
        },
        report: {
            generate: 'Generar',
            cancel: 'Cancelar',
        },
        misc: {
            close: 'Cerrar',
            retry: 'Reintentar',
        },
    },
};

export default componentsEs;
