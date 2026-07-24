const translationEs = {
    app: {
        loading: 'Iniciando PINBAL',
        noEntitat: 'Este usuario no tiene acceso a ningún entorno',
        menu: {
            home: 'Inicio',
            consulta: 'Consultas',
            entitats: 'Entidades',
            serveis: 'Servicios',
            procediments: 'Procedimientos',
            organGestors: 'Órganos gestores',
            configurar: 'Configurar',
            propietats: 'Propiedades',
            caches: 'Cachés',
            avisos: 'Avisos',
            paramConfs: 'Parámetros SCSP',
            emissorCerts: 'Emisores de certificados',
            clauPubliques: 'Claves públicas',
            clauPrivades: 'Claves privadas',
        },
    },
    page: {
        organGestors: {
            grid: {
                title: 'Órganos gestores',
                column: {
                    codi: 'Código',
                    nom: 'Nombre',
                    pare: 'Órgano padre',
                    actiu: 'Activo',
                    estat: 'Estado',
                },
                filter: {
                    codi: 'Código',
                    nom: 'Nombre',
                },
                syncDir3: {
                    button: 'Sincronizar con DIR3',
                    okTitle: 'Sincronización completada',
                    okMessage: 'Se ha sincronizado correctamente con DIR3.',
                    errorTitle: 'Error de sincronización',
                    errorMessage: 'No se ha podido sincronizar con DIR3.',
                },
            },
        },
        procediments: {
            grid: {
                title: 'Procedimientos',
                column: {
                    codi: 'Código',
                    nom: 'Nombre',
                    departament: 'Departamento',
                    organGestor: 'Órgano gestor',
                    codiSia: 'Código SIA',
                    actiu: 'Activo',
                },
                filter: {
                    codi: 'Código',
                    nom: 'Nombre',
                    actiu: 'Activo',
                },
            },
            form: {
                titleCreate: 'Nuevo procedimiento',
                titleUpdate: 'Procedimiento',
                notice:
                    'La asignación de servicios, la rejilla de permisos por servicio/usuario, la clonación y ' +
                    'el asistente de migración de servicios se siguen gestionando desde el mantenimiento de ' +
                    'la aplicación JSP.',
                field: {
                    codi: 'Código',
                    nom: 'Nombre',
                    departament: 'Departamento',
                    organGestor: 'Órgano gestor',
                    codiSia: 'Código SIA',
                    valorCampAutomatizado: 'Automatizado (SIA)',
                    valorCampClaseTramite: 'Clase de trámite (SIA)',
                    actiu: 'Activo',
                },
            },
        },
        serveis: {
            grid: {
                title: 'Servicios',
                column: {
                    codi: 'Código',
                    descripcio: 'Descripción',
                    pinbalEntitatTipus: 'Tipo entidad',
                    pinbalRoleName: 'Rol',
                    actiu: 'Activo',
                },
                filter: {
                    codi: 'Código',
                    descripcio: 'Descripción',
                    actiu: 'Activo',
                },
            },
            form: {
                title: 'Servicio',
                notice:
                    'Solo se puede modificar la configuración propia de PINBAL. Para dar de alta un servicio ' +
                    'nuevo o cambiar su configuración SCSP (URLs, seguridad, campos específicos...) usad el ' +
                    'mantenimiento de servicios de la aplicación JSP.',
                tabs: {
                    dades: 'Datos',
                    redireccions: 'Redirecciones (bus)',
                },
                field: {
                    codi: 'Código',
                    descripcio: 'Descripción',
                    actiu: 'Activo',
                    pinbalEntitatTipus: 'Tipo de entidad proveedora',
                    pinbalRoleName: 'Rol',
                    pinbalPermesDocumentTipusDni: 'Permite DNI',
                    pinbalPermesDocumentTipusNif: 'Permite NIF',
                    pinbalPermesDocumentTipusCif: 'Permite CIF',
                    pinbalPermesDocumentTipusNie: 'Permite NIE',
                    pinbalPermesDocumentTipusPas: 'Permite pasaporte',
                    pinbalDocumentObligatori: 'Documento del titular obligatorio',
                    maxPeticionsMinut: 'Máximo de peticiones por minuto',
                },
                redireccions: {
                    resourceTitle: 'Redirección',
                    field: {
                        urlDesti: 'URL destino',
                        entitat: 'Entidad',
                    },
                },
            },
        },
        entitats: {
            grid: {
                title: 'Entidades',
                column: {
                    codi: 'Código',
                    nom: 'Nombre',
                    cif: 'CIF',
                    unitatArrel: 'Unidad raíz',
                    tipus: 'Tipo',
                    activa: 'Activa',
                },
            },
            form: {
                titleCreate: 'Nueva entidad',
                titleUpdate: 'Entidad',
                tabs: {
                    dades: 'Datos',
                    serveis: 'Servicios',
                    usuaris: 'Usuarios',
                },
                field: {
                    codi: 'Código',
                    nom: 'Nombre',
                    cif: 'CIF',
                    unitatArrel: 'Unidad raíz (DIR3)',
                    tipus: 'Tipo',
                    activa: 'Activa',
                },
                serveis: {
                    resourceTitle: 'Servicio',
                    field: {
                        serveiCodi: 'Código del servicio',
                    },
                },
                usuaris: {
                    resourceTitle: 'Usuario',
                    field: {
                        usuariCodi: 'Código usuario',
                        departament: 'Departamento',
                        principal: 'Principal',
                        representant: 'Representante',
                        delegat: 'Delegado',
                        auditor: 'Auditor',
                        aplicacio: 'Aplicación',
                        actiu: 'Activo',
                    },
                },
            },
        },
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
                    titularNomComplet: 'Nombre titular',
                    titularDocumentNum: 'Documento titular',
                    estat: 'Estado',
                    dataEsperadaResposta: 'Fecha esperada respuesta',
                    recobriment: 'Recobrimiento',
                    multiple: 'Consulta múltiple',
                    estatEnum: {
                        Error: 'Error',
                        Pendent: 'Pendiente',
                        Processant: 'Procesando',
                        Tramitada: 'Tramitada',
                    },
                },
                filter: {
                    entitat: 'Entidad',
                    scspPeticionId: 'ID petición SCSP',
                    procediment: 'Procedimiento',
                    serveiCodiNom: 'Servicio',
                    estat: 'Estado',
                    dataInici: 'Fecha inicio',
                    dataFi: 'Fecha fin',
                    titularNomComplet: 'Nombre titular',
                    titularDocumentNum: 'Documento titular',
                    funcionariNomAmbDocument: 'Funcionario',
                    recobriment: 'Recobrimiento',
                    multiple: 'Múltiple',
                },
                accions: {
                    descarregarJustificant: 'Descargar justificante (PDF)',
                    descarregarZipXmls: 'Descargar XMLs (ZIP)',
                },
            },
            detall: {
                titol: 'Detalle de la consulta',
                veure: 'Ver detalle',
                tancar: 'Cerrar',
                descarregarJustificant: 'Descargar justificante',
                justificantNoDisponible: 'El justificante no está disponible',
                tabs: {
                    dadesPeticio: 'Datos de la petición',
                    altresDades: 'Otros datos',
                    resposta: 'Datos de la respuesta',
                    justificants: 'Justificantes',
                },
                peticio: {
                    veureXml: 'Ver XML',
                    xmlPeticioTitol: 'XML de la petición',
                    descarregarMissatges: 'Descargar mensajes (ZIP)',
                },
                resposta: {
                    veureXml: 'Ver XML',
                    xmlRespostaTitol: 'XML de la respuesta',
                },
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
                justificants: {
                    reintentar: {
                        boto: 'Reintentar',
                        okTitol: 'Justificante regenerado',
                        okMissatge: 'Se ha reintentado la generación del justificante.',
                        errorTitol: 'Error',
                        errorMissatge: 'No se ha podido reintentar la generación del justificante.',
                    },
                    justificant: {
                        titol: 'Justificante de la consulta',
                        descarregar: 'Descargar justificante',
                    },
                    zip: {
                        titol: 'Consulta múltiple',
                        descarregarJustificants: 'Descargar todos los justificantes (ZIP)',
                        descarregarXmls: 'Descargar todos los XMLs (ZIP)',
                    },
                    error: 'Error en la generación o custodia del justificante',
                },
            },
        },
        paramConfs: {
            grid: {
                title: 'Parámetros de configuración SCSP',
                filter: {
                    nom: 'Nombre',
                    valor: 'Valor',
                },
                column: {
                    nom: 'Nombre',
                    valor: 'Valor',
                },
            },
            form: {
                titleCreate: 'Nuevo parámetro',
                titleUpdate: 'Parámetro de configuración',
            },
        },
        emissorCerts: {
            grid: {
                title: 'Emisores de certificados',
                filter: {
                    nom: 'Nombre',
                    cif: 'CIF',
                },
                column: {
                    nom: 'Nombre',
                    cif: 'CIF',
                    dataBaixa: 'Fecha de baja',
                },
            },
            form: {
                titleCreate: 'Nuevo emisor de certificado',
                titleUpdate: 'Emisor de certificado',
            },
        },
        clauPubliques: {
            grid: {
                title: 'Claves públicas',
                filter: {
                    alies: 'Alias',
                    nom: 'Nombre',
                    numSerie: 'Número de serie',
                },
                column: {
                    alies: 'Alias',
                    nom: 'Nombre',
                    numSerie: 'Número de serie',
                    dataAlta: 'Fecha de alta',
                    dataBaixa: 'Fecha de baja',
                },
            },
            form: {
                titleCreate: 'Nueva clave pública',
                titleUpdate: 'Clave pública',
            },
        },
        clauPrivades: {
            grid: {
                title: 'Claves privadas',
                filter: {
                    alies: 'Alias',
                    nom: 'Nombre',
                    numSerie: 'Número de serie',
                },
                column: {
                    alies: 'Alias',
                    nom: 'Nombre',
                    numSerie: 'Número de serie',
                    dataAlta: 'Fecha de alta',
                    dataBaixa: 'Fecha de baja',
                    organisme: 'Organismo cesionario',
                    perEntitat: 'Clave por entidad',
                },
            },
            form: {
                titleCreate: 'Nueva clave privada',
                titleUpdate: 'Clave privada',
            },
        },
        avisos: {
            grid: {
                title: 'Avisos',
                filter: {
                    assumpte: 'Asunto',
                    avisNivell: 'Nivel',
                    actiu: 'Activo',
                },
                column: {
                    assumpte: 'Asunto',
                    avisNivell: 'Nivel',
                    dataInici: 'Fecha inicio',
                    dataFinal: 'Fecha final',
                    actiu: 'Activo',
                },
            },
            form: {
                titleCreate: 'Nuevo aviso',
                titleUpdate: 'Aviso',
            },
        },
        caches: {
            grid: {
                title: 'Cachés de la aplicación',
                column: {
                    codi: 'Código',
                    localHeapSize: 'Tamaño',
                },
                buidar: 'Vaciar',
                buidarTotes: {
                    button: 'Vaciar todas',
                    okTitle: 'Cachés vaciadas',
                    okMessage: 'Se han vaciado todas las cachés correctamente.',
                    errorTitle: 'Error',
                    errorMessage: 'No se han podido vaciar todas las cachés.',
                },
            },
        },
        propietats: {
            title: 'Propiedades',
            find: 'Buscar',
            revert: 'Deshacer cambios',
            empty: 'Este grupo no tiene propiedades',
            save: {
                success: 'Propiedad actualizada correctamente.',
                error: 'No se ha podido actualizar la propiedad.',
            },
            actions: {
                sync: {
                    button: 'Sincronizar con JBoss',
                    okTitle: 'Sincronización completada',
                    okMessage: 'Se han actualizado {{count}} propiedades desde JBoss.',
                    errorTitle: 'Error de sincronización',
                    errorMessage: 'No se ha podido sincronizar con JBoss.',
                },
                reiniciarTasques: {
                    button: 'Reiniciar tareas en segundo plano',
                    okTitle: 'Tareas reiniciadas',
                    okMessage: 'Se han reiniciado las tareas en segundo plano.',
                    errorTitle: 'Error',
                    errorMessage: 'No se han podido reiniciar las tareas en segundo plano.',
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
        clipboard: {
            copy: 'Copiar',
        },
        boto: {
            descarregar: 'Descargar',
        },
        opcio: {
            carregant: 'Cargando...',
        },
    },
};

export default translationEs;
