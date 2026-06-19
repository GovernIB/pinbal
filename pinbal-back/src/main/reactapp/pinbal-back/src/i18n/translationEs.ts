const translationEs = {
    app: {
        loading: 'Iniciando PINBAL',
        noEntitat: 'Este usuario no tiene acceso a ningún entorno',
        menu: {
            home: 'Inicio',
            consulta: 'Consultas',
        },
    },
    page: {
        home: {
            toolbar: {
                title: 'Bienvenidos a PINBAL',
                subtitle: 'Aplicación para la interoperabilidad entre las administraciones baleares y el resto del estado',
            },
        },
        consulta: {
            grid: {
                title: 'Consultas',
                origen: {
                    recents: 'Recientes',
                    historic: 'Histórico',
                },
                column: {
                    scspPeticionId: 'ID petición',
                    creacioData: 'Fecha creación',
                    creacioUsuariNomCodi: 'Usuario',
                    funcionariNomAmbDocument: 'Funcionario',
                    procedimentCodiNom: 'Procedimiento',
                    serveiCodiNom: 'Servicio',
                    estat: 'Estado',
                    dataEsperadaResposta: 'Fecha esperada respuesta',
                    entitat: 'Entidad',
                },
                filter: {
                    entitatId: 'Entidad',
                    scspPeticionId: 'ID petición SCSP',
                    serveiCodiNom: 'Servicio',
                    estat: 'Estado',
                    dataInici: 'Fecha inicio',
                    dataFi: 'Fecha fin',
                    funcionariNomAmbDocument: 'Funcionario',
                    recobriment: 'Recobrimiento',
                    multiple: 'Múltiple',
                },
            },
            detall: {
                titol: 'Detalle de la consulta',
                veure: 'Ver detalle',
                tancar: 'Cerrar',
                descarregarJustificant: 'Descargar justificante',
                justificantNoDisponible: 'El justificante no está disponible',
                field: {
                    scspPeticionId: 'ID petición',
                    scspSolicitudId: 'ID solicitud',
                    estat: 'Estado',
                    creacioData: 'Fecha creación',
                    respostaData: 'Fecha respuesta',
                    entitatNom: 'Entidad',
                    entitatCif: 'CIF entidad',
                    procedimentCodiNom: 'Procedimiento',
                    serveiCodiNom: 'Servicio',
                    funcionariNomAmbDocument: 'Funcionario',
                    departamentNom: 'Departamento',
                    titularDocumentTipus: 'Tipo documento titular',
                    titularDocumentNum: 'Documento titular',
                    titularNomComplet: 'Nombre titular',
                    finalitat: 'Finalidad',
                    consentiment: 'Consentimiento',
                    expedientId: 'Expediente',
                    justificantEstat: 'Estado justificante',
                },
            },
        },
    },
    component: {
        HeaderThemeSelector: {
            light: 'Claro',
            system: 'Sistema',
            dark: 'Oscuro',
        },
        HeaderLanguageSelector: {
            languages: {
                ca: 'Catalán',
                es: 'Castellano',
            },
        },
        Offline: {
            message: 'Sin conexión con el servidor',
            retry: 'Volver a intentar',
        },
        AclPermissionManager: {
            title: 'Permisos',
            resourceTitle: 'Permiso',
        },
        RoleSelector: {
            role: {
                PBL_ADMIN: 'Administrador',
                PBL_REPRES: 'Representante',
                PBL_AUDIT: 'Auditor',
                PBL_SUPERAUD: 'Superauditor',
                PBL_DELEG: 'Delegado',
                tothom: 'Delegado',
            },
        },
        PermissionGrid: {
            popupTitle: 'Permiso',
            tipus: 'Tipo',
            grantedAuthority: {
                user: 'Usuario',
                role: 'Rol',
            },
        },
        GridToolbarButton: {
            add: 'Añadir',
            refresh: 'Refrescar',
        },
        UserProfile: {
            perfil: "Perfil del usuario",
            auto: "Automático",
            rols: "Roles",
            seccioDades: "Datos del usuario",
            seccioConfig: "Configuración",
            tema: {
                label: "Tema de la aplicación",
                clar: "Claro",
                obscur: "Oscuro",
                sistema: "Sistema",
            },
        },
        FormDropzoneField: {
            arrosegar:  "Arrastra el fichero aquí",
            amollar: "Suelta el fichero ahora ...",
            validacio: "El archivo no es válido",
        },
        AccionsMassives: {
            labelBoto: "Acciones masivas",
            selectAll: "Seleccionar todo",
            deselectAll: "Desmarcar todo",
        },
        ButtonDetailExpandColapse: {
            expandAll: "Expandir todo",
            collapseAll: "Contraer todo",
        },
        Dir3SearchInput: {
            search: "Buscar",
            dialog: {
                title: "Consulta de administraciones públicas a DIR3",
                netejar: "Limpiar",
                noCif: "Sin CIF",
                noSir: "Sin SIR",
                viaValib: "Vía VALIB",
            }
        },
    },
    hook: {
        useDataGrid: {
            treeData: {
                collapseAll: 'Contraer todo',
                expandAll: 'Expandir todo',
            },
        },
    },
    comu: {
        netejarFiltre: 'Limpiar filtro',
        filtrar: 'Filtrar',
        obrirFiltreAvançat: 'Abrir filtro avanzado',
        tancarFiltreAvançat: 'Cerrar filtro avanzado',
    },
};

export default translationEs;
