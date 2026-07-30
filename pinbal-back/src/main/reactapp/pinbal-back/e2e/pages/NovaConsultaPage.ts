import { Locator, Page } from '@playwright/test';

/**
 * Encapsula el flux de "Nova consulta" (rol delegat): el menú desplegable
 * `#btNovaConsulta` de la capçalera (`decorators/default.jsp`) que llista
 * tots els serveis amb permís (`sessionServeis`) i permet filtrar-los, i el
 * formulari `consultaForm.jsp` (amb els parcials `import/consultaSimpleForm.jsp`
 * / `import/consultaMultipleForm.jsp`) on s'envien consultes simples o
 * múltiples.
 *
 * Es reutilitza tant des de `e2e/global-setup.ts` (creació de dades reals
 * per a la resta de la suite) com des de
 * `e2e/tests/delegat/noves-consultes.spec.ts`.
 */
export class NovaConsultaPage {
    constructor(private readonly page: Page) {}

    // --- Menú "Nova consulta" (decorators/default.jsp) ---------------------

    /** Botó que obre el desplegable de serveis amb permís de consulta. */
    novaConsultaButton(): Locator {
        return this.page.locator('#btNovaConsulta');
    }

    /** Obre el desplegable "Nova consulta" (si no ho està ja). */
    async obrirMenu(): Promise<void> {
        const menu = this.page.locator('#novaConsultaMenu');
        if (!(await menu.isVisible().catch(() => false))) {
            await this.novaConsultaButton().click();
        }
    }

    /** Totes les entrades de servei del desplegable (visibles o no). */
    serveiItems(): Locator {
        return this.page.locator('#novaConsultaMenu li.nova-consulta-item');
    }

    /** Entrades de servei actualment visibles (després d'aplicar un filtre). */
    visibleServeiItems(): Locator {
        return this.page.locator('#novaConsultaMenu li.nova-consulta-item:visible');
    }

    /**
     * Escriu al camp de cerca de serveis del desplegable (filtre client-side
     * per codi/descripció, `decorators/default.jsp`). El filtre reacciona a
     * l'esdeveniment `keyup`, que `fill()` no dispara per si sol, així que el
     * simulem explícitament després d'establir el valor.
     */
    async filtrarServeis(text: string): Promise<void> {
        const input = this.page.locator('#filterServeis');
        await input.fill(text);
        await input.dispatchEvent('keyup');
    }

    /** Fa clic a l'entrada del servei indicat (pel seu codi) dins el desplegable ja obert. */
    async seleccionarServei(serveiCodi: string): Promise<void> {
        await this.serveiItems()
            .filter({ hasText: serveiCodi })
            .first()
            .locator('a')
            .click();
    }

    // --- Formulari de nova consulta (consultaForm.jsp) ----------------------

    /** Navega directament al formulari de nova consulta per a un servei. */
    async goto(serveiCodi: string): Promise<void> {
        await this.page.goto(`consulta/${encodeURIComponent(serveiCodi)}/new`);
    }

    /**
     * Un cop obert el formulari (vegeu {@link goto}), detecta quines
     * modalitats permet el servei i si té el camp de document del titular
     * actiu, sense necessitat de conèixer per endavant la configuració del
     * servei. Els dos parcials (simple/múltiple) sempre es renderitzen al DOM
     * quan el servei permet ambdues modalitats (només una pestanya està
     * visible), així que `count()` és fiable encara que la pestanya no
     * estigui activa.
     */
    async capacitats(): Promise<{
        formulariTrobat: boolean;
        teDocumentTitular: boolean;
        permetMultiple: boolean;
        teProcediment: boolean;
    }> {
        const formulariTrobat = (await this.page.locator('#consultaForm').count()) > 0;
        const teDocumentTitular = (await this.page.locator('#titularDocumentNum').count()) > 0;
        const permetMultiple = (await this.page.locator('#multipleFitxer').count()) > 0;
        const teProcediment = (await this.page.locator('#procedimentId option').count()) > 0;
        return { formulariTrobat, teDocumentTitular, permetMultiple, teProcediment };
    }

    /** Localitzador de les pestanyes "Simple"/"Múltiple" (només presents quan el servei permet ambdues). */
    tabs(): Locator {
        return this.page.locator('#tabs-simple-multiple');
    }

    /** Canvia a la pestanya de consulta simple, si el servei permet ambdues modalitats. */
    async anarATabSimple(): Promise<void> {
        const tabs = this.tabs();
        if (await tabs.isVisible().catch(() => false)) {
            await tabs.locator('a[href="#tab-simple"]').click();
        }
    }

    /** Canvia a la pestanya de consulta múltiple, si el servei permet ambdues modalitats. */
    async anarATabMultiple(): Promise<void> {
        const tabs = this.tabs();
        if (await tabs.isVisible().catch(() => false)) {
            await tabs.locator('a[href="#tab-multiple"]').click();
        }
    }

    /**
     * Selecciona el procediment indicat (o el primer disponible si no se
     * n'indica cap).
     *
     * Llança un error immediat i clar si el select no té cap opció, en lloc
     * de deixar que `selectOption()` esgoti tot el timeout d'acció esperant
     * una opció que mai apareixerà: `#procedimentId` es carrega amb
     * `procedimentService.findActiusAmbEntitatIServeiCodi(entitatId, serveiCodi)`
     * (`ConsultaController.omplirModelPerMostrarFormulari`), que filtra els
     * `ProcedimentServei` per ACL (`PermisosHelper.filterGrantedAll`); si
     * l'usuari actual no té cap permís ACL concedit sobre cap
     * `ProcedimentServei` d'aquest servei —encara que la relació
     * `pbl_procediment_servei` existeixi—, el select es renderitza buit.
     */
    async seleccionarProcediment(procedimentId?: string): Promise<void> {
        const select = this.page.locator('#procedimentId');
        const numOpcions = await select.locator('option').count();
        if (numOpcions === 0) {
            throw new Error(
                'El select de procediment (#procedimentId) no té cap opció per a l\'usuari actual: cap ' +
                    'ProcedimentServei d\'aquest servei té permís ACL concedit (vegeu ' +
                    'ProcedimentServiceImpl.findActiusAmbEntitatIServeiCodi). Comprova les dades de mostra ' +
                    '(pbl_procediment_servei i els permisos ACL associats) per a l\'entitat/usuari d\'aquest entorn.',
            );
        }
        if (procedimentId) {
            await select.selectOption(procedimentId, { force: true });
        } else {
            await select.selectOption({ index: 0 }, { force: true });
        }
    }

    /** Emplena els camps genèrics comuns a qualsevol consulta (simple o múltiple). */
    async emplenarDadesGeneriques(opts: { funcionariNom: string; departamentNom: string; finalitat: string }): Promise<void> {
        await this.page.locator('#funcionariNom').fill(opts.funcionariNom);
        await this.page.locator('#departamentNom').fill(opts.departamentNom);
        await this.page.locator('#finalitat').fill(opts.finalitat);
    }

    /**
     * Emplena les dades del titular (pestanya/formulari simple), si el servei
     * té el camp de document actiu. No fa res si el servei no en disposa.
     */
    async emplenarTitularDocument(tipus: string, num: string): Promise<boolean> {
        const documentNum = this.page.locator('#titularDocumentNum');
        if ((await documentNum.count()) === 0) {
            return false;
        }
        const documentTipus = this.page.locator('#titularDocumentTipus');
        if (await documentTipus.count()) {
            await documentTipus.selectOption(tipus, { force: true });
        }
        await documentNum.fill(num);
        return true;
    }

    /** Fa clic al botó d'enviament del formulari (serveix tant per a l'alta com per al reintentar). */
    async enviar(): Promise<void> {
        await this.page.locator('#consultaForm button[type="submit"]').click();
    }

    // --- Consulta múltiple: plantilla + fitxer ------------------------------

    /**
     * Descarrega la plantilla CSV real del servei (ja adaptada als seus camps
     * de dades específiques actius), hi afegeix una fila de dades emplenada
     * amb valors genèrics coneguts (document, nom, cognoms...) i puja el
     * fitxer resultant al camp `#multipleFitxer`.
     *
     * Les columnes de la plantilla es descriuen amb "paths" interns
     * (`DatosGenericos/Titular/Documentacion`, ...) fixats per
     * `PeticioMultiplePlantillaCsvView`; els camps de dades específiques del
     * servei (desconeguts a priori) es deixen en blanc.
     */
    async pujarFitxerMultiple(
        serveiCodi: string,
        files: Array<{
            documentTipus?: string;
            documentNum?: string;
            nom?: string;
            llinatge1?: string;
            llinatge2?: string;
            expedientId?: string;
        }>,
    ): Promise<void> {
        const url = `/consulta/${encodeURIComponent(serveiCodi)}/plantilla/CSV`;
        const response = await this.page.request.get(url);
        if (!response.ok()) {
            throw new Error(`No s'ha pogut descarregar la plantilla CSV (${response.status()}) per a ${serveiCodi}`);
        }
        const text = await response.text();
        const lines = text.split(/\r?\n/).filter((l) => l.length > 0);
        if (lines.length < 3) {
            throw new Error(`Plantilla CSV inesperada per a ${serveiCodi}: ${lines.length} línies`);
        }
        const [titolLine, labelsLine, pathsLine] = lines;
        const paths = pathsLine.split(',');

        const dataLines = files.map((fila) => paths.map((path) => valueForPath(path, fila)).join(','));

        const csvContent = [titolLine, labelsLine, pathsLine, ...dataLines].join('\n');
        await this.page.setInputFiles('#multipleFitxer', {
            name: `plantilla_${serveiCodi}.csv`,
            mimeType: 'text/csv',
            buffer: Buffer.from(csvContent, 'utf-8'),
        });
    }

    /** Bloc d'errors de validació del fitxer múltiple (`consultaForm.jsp`, `#errorsFitxer`), si n'hi ha. */
    errorsFitxer(): Locator {
        return this.page.locator('#errorsFitxer');
    }
}

/**
 * Tradueix un "path" de columna de la plantilla múltiple (vegeu
 * `PeticioMultiplePlantillaCsvView`) al valor genèric que hi volem posar.
 * Els camps de dades específiques del servei (paths no reconeguts) es
 * deixen en blanc: no en sabem la semàntica a priori.
 */
function valueForPath(
    path: string,
    fila: {
        documentTipus?: string;
        documentNum?: string;
        nom?: string;
        llinatge1?: string;
        llinatge2?: string;
        expedientId?: string;
    },
): string {
    if (path.endsWith('Solicitante/IdExpediente')) return fila.expedientId ?? '';
    if (path.endsWith('Titular/TipoDocumentacion')) return fila.documentTipus ?? 'NIF';
    if (path.endsWith('Titular/Documentacion')) return fila.documentNum ?? '';
    if (path.endsWith('Titular/NombreCompleto')) {
        return fila.nom && fila.llinatge1 ? `${fila.nom} ${fila.llinatge1}` : '';
    }
    if (path.endsWith('Titular/Nombre')) return fila.nom ?? '';
    if (path.endsWith('Titular/Apellido1')) return fila.llinatge1 ?? '';
    if (path.endsWith('Titular/Apellido2')) return fila.llinatge2 ?? '';
    // Camp de dades específiques del servei: es desconeix el seu significat, es deixa en blanc.
    return '';
}
