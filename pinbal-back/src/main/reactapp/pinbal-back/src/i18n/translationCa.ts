const translationCa = {
    app: {
        loading: 'Iniciant PINBAL',
        noEntitat: 'Aquest usuari no te accés a cap entorn',
        menu: {
            home: 'Inici',
            consulta: 'Consultes',
        },
    },
    page: {
        home: {
            toolbar: {
                title: 'Benvinguts a PINBAL',
                subtitle: 'Aplicació per a la interoperabilitat entre les administracions baleares i el reste del estado',
            },
        },
        consulta: {
            grid: {
                title: 'Consultes',
                origen: {
                    recents: 'Recents',
                    historic: 'Històric',
                },
                column: {
                    scspPeticionId: 'ID petició',
                    creacioData: 'Data creació',
                    creacioUsuariNomCodi: 'Usuari',
                    funcionariNomAmbDocument: 'Funcionari',
                    procedimentCodiNom: 'Procediment',
                    serveiCodiNom: 'Servei',
                    estat: 'Estat',
                    dataEsperadaResposta: 'Data esperada resposta',
                    entitat: 'Entitat',
                },
                filter: {
                    entitatId: 'Entitat',
                    scspPeticionId: 'ID petició SCSP',
                    serveiCodiNom: 'Servei',
                    estat: 'Estat',
                    dataInici: 'Data inici',
                    dataFi: 'Data fi',
                    funcionariNomAmbDocument: 'Funcionari',
                    recobriment: 'Recobriment',
                    multiple: 'Múltiple',
                },
            },
            detall: {
                titol: 'Detall de la consulta',
                veure: 'Veure detall',
                tancar: 'Tancar',
                descarregarJustificant: 'Descarregar justificant',
                justificantNoDisponible: 'El justificant no està disponible',
                field: {
                    scspPeticionId: 'ID petició',
                    scspSolicitudId: 'ID sol·licitud',
                    estat: 'Estat',
                    creacioData: 'Data creació',
                    respostaData: 'Data resposta',
                    entitatNom: 'Entitat',
                    entitatCif: 'CIF entitat',
                    procedimentCodiNom: 'Procediment',
                    serveiCodiNom: 'Servei',
                    funcionariNomAmbDocument: 'Funcionari',
                    departamentNom: 'Departament',
                    titularDocumentTipus: 'Tipus document titular',
                    titularDocumentNum: 'Document titular',
                    titularNomComplet: 'Nom titular',
                    finalitat: 'Finalitat',
                    consentiment: 'Consentiment',
                    expedientId: 'Expedient',
                    justificantEstat: 'Estat justificant',
                },
            },
        },
    },
    component: {
        HeaderThemeSelector: {
            light: 'Clar',
            system: 'Sistema',
            dark: 'Fosc',
        },
        HeaderLanguageSelector: {
            languages: {
                ca: 'Català',
                es: 'Castellà',
            },
        },
        Offline: {
            message: 'Sense connexió amb el servidor',
            retry: 'Tornar a provar',
        },
        AclPermissionManager: {
            title: 'Permisos',
            resourceTitle: 'Permís',
        },
        RoleSelector: {
            role: {
                PBL_ADMIN: 'Administrador',
                PBL_REPRES: 'Representant',
                PBL_AUDIT: 'Auditor',
                PBL_SUPERAUD: 'Superauditor',
                PBL_DELEG: 'Delegat',
                tothom: 'Delegat',
            },
        },
        PermissionGrid: {
            popupTitle: 'Permís',
            tipus: 'Tipus',
            grantedAuthority: {
                user: 'Usuari',
                role: 'Rol',
            },
        },
        GridToolbarButton: {
            add: 'Afegir',
            refresh: 'Refrescar',
        },
        UserProfile: {
            perfil: "Perfil de l'usuari",
            auto: 'Automàtic',
            rols: 'Rols',
            seccioDades: "Dades de l'usuari",
            seccioConfig: 'Configuració',
            tema: {
                label: "Tema de l'aplicació",
                clar: 'Clar',
                obscur: 'Obscur',
                sistema: 'Sistema',
            },
        },
        FormDropzoneField: {
            arrosegar: 'Arrossega el fitxer aquí',
            amollar: 'Amolla el fitxer ara ...',
            validacio: "L'arxiu no és vàlid",
        },
        AccionsMassives: {
            labelBoto: 'Accions massives',
            selectAll: "Seleccionar tot",
            deselectAll: "Desmarcar tot",
        },
        ButtonDetailExpandColapse: {
            expandAll: "Expandir tots",
            collapseAll: "Contreure tots",
        },
        Dir3SearchInput: {
            search: "Cercar",
            dialog: {
                title: "Consulta d'administracions públiques a DIR3",
                netejar: "Netejar",
                noCif: "Sense CIF",
                noSir: "Sense SIR",
                viaValib: "Via Valib",
            }
        },
    },
    hook: {
        useDataGrid: {
            treeData: {
                collapseAll: 'Contreure tot',
                expandAll: 'Expandir tot',
            },
        },
    },
    comu: {
        netejarFiltre: 'Netejar filtre',
        filtrar: 'Filtrar',
        obrirFiltreAvançat: 'Obrir filtre avançat',
        tancarFiltreAvançat: 'Tancar filtre avançat',
    },
};

export default translationCa;
