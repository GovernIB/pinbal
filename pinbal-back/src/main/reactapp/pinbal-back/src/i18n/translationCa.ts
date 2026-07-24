const translationCa = {
    app: {
        loading: 'Iniciant PINBAL',
        noEntitat: 'Aquest usuari no te accés a cap entorn',
        menu: {
            home: 'Inici',
            consulta: 'Consultes',
            entitats: 'Entitats',
            serveis: 'Serveis',
            procediments: 'Procediments',
            organGestors: 'Òrgans gestors',
            configurar: 'Configurar',
            propietats: 'Propietats',
            caches: 'Cachés',
            avisos: 'Avisos',
            paramConfs: 'Paràmetres SCSP',
            emissorCerts: 'Emissors de certificats',
            clauPubliques: 'Claus públiques',
            clauPrivades: 'Claus privades',
        },
    },
    page: {
        organGestors: {
            grid: {
                title: 'Òrgans gestors',
                column: {
                    codi: 'Codi',
                    nom: 'Nom',
                    pare: 'Òrgan pare',
                    actiu: 'Actiu',
                    estat: 'Estat',
                },
                filter: {
                    codi: 'Codi',
                    nom: 'Nom',
                },
                syncDir3: {
                    button: 'Sincronitzar amb DIR3',
                    okTitle: 'Sincronització completada',
                    okMessage: "S'ha sincronitzat correctament amb DIR3.",
                    errorTitle: 'Error de sincronització',
                    errorMessage: "No s'ha pogut sincronitzar amb DIR3.",
                },
            },
        },
        procediments: {
            grid: {
                title: 'Procediments',
                column: {
                    codi: 'Codi',
                    nom: 'Nom',
                    departament: 'Departament',
                    organGestor: 'Òrgan gestor',
                    codiSia: 'Codi SIA',
                    actiu: 'Actiu',
                },
                filter: {
                    codi: 'Codi',
                    nom: 'Nom',
                    actiu: 'Actiu',
                },
            },
            form: {
                titleCreate: 'Nou procediment',
                titleUpdate: 'Procediment',
                notice:
                    "L'assignació de serveis, la graella de permisos per servei/usuari, el clonatge i " +
                    "l'assistent de migració de serveis es continuen gestionant des del manteniment de " +
                    "l'aplicació JSP.",
                field: {
                    codi: 'Codi',
                    nom: 'Nom',
                    departament: 'Departament',
                    organGestor: 'Òrgan gestor',
                    codiSia: 'Codi SIA',
                    valorCampAutomatizado: 'Automatitzat (SIA)',
                    valorCampClaseTramite: 'Classe de tràmit (SIA)',
                    actiu: 'Actiu',
                },
            },
        },
        serveis: {
            grid: {
                title: 'Serveis',
                column: {
                    codi: 'Codi',
                    descripcio: 'Descripció',
                    pinbalEntitatTipus: 'Tipus entitat',
                    pinbalRoleName: 'Rol',
                    actiu: 'Actiu',
                },
                filter: {
                    codi: 'Codi',
                    descripcio: 'Descripció',
                    actiu: 'Actiu',
                },
            },
            form: {
                title: 'Servei',
                notice:
                    "Només es pot modificar la configuració pròpia de PINBAL. Per donar d'alta un servei nou o " +
                    "canviar-ne la configuració SCSP (URLs, seguretat, camps específics...) useu el manteniment " +
                    "de serveis de l'aplicació JSP.",
                tabs: {
                    dades: 'Dades',
                    redireccions: 'Redireccions (bus)',
                },
                field: {
                    codi: 'Codi',
                    descripcio: 'Descripció',
                    actiu: 'Actiu',
                    pinbalEntitatTipus: "Tipus d'entitat proveïdora",
                    pinbalRoleName: 'Rol',
                    pinbalPermesDocumentTipusDni: 'Permet DNI',
                    pinbalPermesDocumentTipusNif: 'Permet NIF',
                    pinbalPermesDocumentTipusCif: 'Permet CIF',
                    pinbalPermesDocumentTipusNie: 'Permet NIE',
                    pinbalPermesDocumentTipusPas: 'Permet passaport',
                    pinbalDocumentObligatori: 'Document del titular obligatori',
                    maxPeticionsMinut: 'Màxim de peticions per minut',
                },
                redireccions: {
                    resourceTitle: 'Redirecció',
                    field: {
                        urlDesti: 'URL destí',
                        entitat: 'Entitat',
                    },
                },
            },
        },
        entitats: {
            grid: {
                title: 'Entitats',
                column: {
                    codi: 'Codi',
                    nom: 'Nom',
                    cif: 'CIF',
                    unitatArrel: 'Unitat arrel',
                    tipus: 'Tipus',
                    activa: 'Activa',
                },
            },
            form: {
                titleCreate: 'Nova entitat',
                titleUpdate: 'Entitat',
                tabs: {
                    dades: 'Dades',
                    serveis: 'Serveis',
                    usuaris: 'Usuaris',
                },
                field: {
                    codi: 'Codi',
                    nom: 'Nom',
                    cif: 'CIF',
                    unitatArrel: 'Unitat arrel (DIR3)',
                    tipus: 'Tipus',
                    activa: 'Activa',
                },
                serveis: {
                    resourceTitle: 'Servei',
                    field: {
                        serveiCodi: 'Codi del servei',
                    },
                },
                usuaris: {
                    resourceTitle: 'Usuari',
                    field: {
                        usuariCodi: 'Codi usuari',
                        departament: 'Departament',
                        principal: 'Principal',
                        representant: 'Representant',
                        delegat: 'Delegat',
                        auditor: 'Auditor',
                        aplicacio: 'Aplicació',
                        actiu: 'Actiu',
                    },
                },
            },
        },
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
                    titularNomComplet: 'Nom titular',
                    titularDocumentNum: 'Document titular',
                    estat: 'Estat',
                    dataEsperadaResposta: 'Data esperada resposta',
                    recobriment: 'Recobriment',
                    multiple: 'Consulta múltiple',
                    estatEnum: {
                        Error: 'Error',
                        Pendent: 'Pendent',
                        Processant: 'Processant',
                        Tramitada: 'Tramitada',
                    },
                },
                filter: {
                    entitat: 'Entitat',
                    scspPeticionId: 'ID petició SCSP',
                    procediment: 'Procediment',
                    serveiCodiNom: 'Servei',
                    estat: 'Estat',
                    dataInici: 'Data inici',
                    dataFi: 'Data fi',
                    titularNomComplet: 'Nom titular',
                    titularDocumentNum: 'Document titular',
                    funcionariNomAmbDocument: 'Funcionari',
                    recobriment: 'Recobriment',
                    multiple: 'Múltiple',
                },
                accions: {
                    descarregarJustificant: 'Descarregar justificant (PDF)',
                    descarregarZipXmls: 'Descarregar XMLs (ZIP)',
                },
            },
            detall: {
                titol: 'Detall de la consulta',
                veure: 'Veure detall',
                tancar: 'Tancar',
                descarregarJustificant: 'Descarregar justificant',
                justificantNoDisponible: 'El justificant no està disponible',
                tabs: {
                    dadesPeticio: 'Dades de petició',
                    altresDades: 'Altres dades',
                    resposta: 'Dades de la resposta',
                    justificants: 'Justificants',
                },
                peticio: {
                    veureXml: 'Veure XML',
                    xmlPeticioTitol: 'XML de la petició',
                    descarregarMissatges: 'Descarregar missatges (ZIP)',
                },
                resposta: {
                    veureXml: 'Veure XML',
                    xmlRespostaTitol: 'XML de la resposta',
                },
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
                justificants: {
                    reintentar: {
                        boto: 'Reintentar',
                        okTitol: 'Justificant regenerat',
                        okMissatge: "S'ha reintentat la generació del justificant.",
                        errorTitol: 'Error',
                        errorMissatge: "No s'ha pogut reintentar la generació del justificant.",
                    },
                    justificant: {
                        titol: 'Justificant de la consulta',
                        descarregar: 'Descarregar justificant',
                    },
                    zip: {
                        titol: 'Consulta múltiple',
                        descarregarJustificants: 'Descarregar tots els justificants (ZIP)',
                        descarregarXmls: 'Descarregar tots els XMLs (ZIP)',
                    },
                    error: 'Error en la generació o custòdia del justificant',
                },
            },
        },
        paramConfs: {
            grid: {
                title: 'Paràmetres de configuració SCSP',
                filter: {
                    nom: 'Nom',
                    valor: 'Valor',
                },
                column: {
                    nom: 'Nom',
                    valor: 'Valor',
                },
            },
            form: {
                titleCreate: 'Nou paràmetre',
                titleUpdate: 'Paràmetre de configuració',
            },
        },
        emissorCerts: {
            grid: {
                title: 'Emissors de certificats',
                filter: {
                    nom: 'Nom',
                    cif: 'CIF',
                },
                column: {
                    nom: 'Nom',
                    cif: 'CIF',
                    dataBaixa: 'Data de baixa',
                },
            },
            form: {
                titleCreate: 'Nou emissor de certificat',
                titleUpdate: 'Emissor de certificat',
            },
        },
        clauPubliques: {
            grid: {
                title: 'Claus públiques',
                filter: {
                    alies: 'Àlies',
                    nom: 'Nom',
                    numSerie: 'Número de sèrie',
                },
                column: {
                    alies: 'Àlies',
                    nom: 'Nom',
                    numSerie: 'Número de sèrie',
                    dataAlta: 'Data d\'alta',
                    dataBaixa: 'Data de baixa',
                },
            },
            form: {
                titleCreate: 'Nova clau pública',
                titleUpdate: 'Clau pública',
            },
        },
        clauPrivades: {
            grid: {
                title: 'Claus privades',
                filter: {
                    alies: 'Àlies',
                    nom: 'Nom',
                    numSerie: 'Número de sèrie',
                },
                column: {
                    alies: 'Àlies',
                    nom: 'Nom',
                    numSerie: 'Número de sèrie',
                    dataAlta: 'Data d\'alta',
                    dataBaixa: 'Data de baixa',
                    organisme: 'Organisme cessionari',
                    perEntitat: 'Clau per entitat',
                },
            },
            form: {
                titleCreate: 'Nova clau privada',
                titleUpdate: 'Clau privada',
            },
        },
        avisos: {
            grid: {
                title: 'Avisos',
                filter: {
                    assumpte: 'Assumpte',
                    avisNivell: 'Nivell',
                    actiu: 'Actiu',
                },
                column: {
                    assumpte: 'Assumpte',
                    avisNivell: 'Nivell',
                    dataInici: 'Data inici',
                    dataFinal: 'Data final',
                    actiu: 'Actiu',
                },
            },
            form: {
                titleCreate: 'Nou avís',
                titleUpdate: 'Avís',
            },
        },
        caches: {
            grid: {
                title: 'Cachés de l\'aplicació',
                column: {
                    codi: 'Codi',
                    localHeapSize: 'Mida',
                },
                buidar: 'Buidar',
                buidarTotes: {
                    button: 'Buidar totes',
                    okTitle: 'Cachés buidades',
                    okMessage: 'S\'han buidat totes les cachés correctament.',
                    errorTitle: 'Error',
                    errorMessage: 'No s\'han pogut buidar totes les cachés.',
                },
            },
        },
        propietats: {
            title: 'Propietats',
            find: 'Cercar',
            revert: 'Desfer canvis',
            empty: 'Aquest grup no té propietats',
            save: {
                success: 'Propietat actualitzada correctament.',
                error: 'No s\'ha pogut actualitzar la propietat.',
            },
            actions: {
                sync: {
                    button: 'Sincronitzar amb JBoss',
                    okTitle: 'Sincronització completada',
                    okMessage: 'S\'han actualitzat {{count}} propietats des de JBoss.',
                    errorTitle: 'Error de sincronització',
                    errorMessage: 'No s\'ha pogut sincronitzar amb JBoss.',
                },
                reiniciarTasques: {
                    button: 'Reiniciar tasques en segon pla',
                    okTitle: 'Tasques reiniciades',
                    okMessage: 'S\'han reiniciat les tasques en segon pla.',
                    errorTitle: 'Error',
                    errorMessage: 'No s\'han pogut reiniciar les tasques en segon pla.',
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
        clipboard: {
            copy: 'Copiar',
        },
        boto: {
            descarregar: 'Descarregar',
        },
        opcio: {
            carregant: 'Carregant...',
        },
    },
};

export default translationCa;
