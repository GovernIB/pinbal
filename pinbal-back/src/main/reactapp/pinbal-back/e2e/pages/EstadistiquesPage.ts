import { Page, expect } from '@playwright/test';

/**
 * Pàgina d'estadístiques de consultes (`/estadistiques`, estadistiques.jsp).
 *
 * Particularitats rellevants pel rol administrador:
 *  - La primera vegada que s'hi entra (sense cap entitat prèviament
 *    seleccionada a sessió) es mostra un selector d'entitat en lloc del
 *    formulari de filtre; cal seleccionar-ne una (POST a
 *    `/estadistiques/canviEntitat`) abans de poder filtrar.
 *  - El càlcul de dades és sobre una taula pre-agregada
 *    (`pbl_explot_consulta_fet`/`pbl_explot_consulta_dim`), NO sobre les
 *    consultes reals: si no s'indica `dataFi` explícitament, el controlador
 *    fa servir "ahir" i, en no trobar cap snapshot per aquesta data,
 *    dispara en calent el job de generació de dades d'explotació (lent i
 *    depenent de dades reals). Per evitar-ho i obtenir sempre el mateix
 *    resultat determinista, els tests fixen `dataFi` a la data sembrada
 *    `01/06/2024` (vegeu pbl_explot_temps id 900601 a
 *    00_e2e_seed_data.yaml), que ja existeix i evita el job.
 */
export class EstadistiquesPage {
    constructor(private readonly page: Page) {}

    async goto(): Promise<void> {
        await this.page.goto('estadistiques');
    }

    /** Localitzador del selector d'entitat inicial (només visible si encara no hi ha entitat a sessió). */
    private selectorEntitatVisible(): Promise<boolean> {
        return this.page.locator('#entitatId').isVisible().catch(() => false);
    }

    /**
     * Si el selector d'entitat inicial és present, selecciona l'entitat pel
     * seu nom visible i confirma. Si ja hi havia una entitat seleccionada a
     * sessió (visita repetida), no fa res.
     */
    async seleccionarEntitatSiCal(nomEntitat: string): Promise<void> {
        if (await this.selectorEntitatVisible()) {
            await this.page.locator('#entitatId').selectOption({ label: nomEntitat }, { force: true });
            await this.page.getByRole('button', { name: /seleccionar/i }).click();
            await expect(this.page.locator('#form-filtre')).toBeVisible({ timeout: 15_000 });
        }
    }

    /** Torna a l'estat "sense entitat seleccionada" (per si cal canviar-ne l'entitat en un altre test). */
    async canviarEntitat(): Promise<void> {
        await this.page.goto('estadistiques/canviEntitat');
    }

    /**
     * Omple el formulari de filtre i el sotmet, esperant la navegació
     * resultant (és un POST/redirect complet de pàgina, no un DataTable).
     *
     * Els camps `dataInici`/`dataFi` (`<pbl:inputDate>`) inicialitzen el seu
     * propi bootstrap-datepicker amb un script inline propi de la tag
     * (diferent del `webutilDatepickerEval()` genèric que fan servir altres
     * formularis). `.fill()` només escriu el `value` de l'input i no
     * actualitza el model intern del datepicker (`$(el).data('datepicker')
     * .dates` queda buit); qualsevol interacció posterior que re-sincronitzi
     * el camp des d'aquell model buit (`Escape`, `blur` amb `forceParse`)
     * n'esborra el text escrit, i el valor arriba buit al servidor. Cal
     * `pressSequentially` (tecla a tecla, com un usuari real) perquè el
     * listener `keyup` del datepicker vagi construint el seu model alhora.
     *
     * IMPORTANT: `page.waitForLoadState('load')` NO és un listener d'un
     * esdeveniment futur -- comprova l'estat ACTUAL de la pàgina i, si ja
     * l'ha assolit (que sempre és el cas abans de clicar, ja que la pàgina
     * prèvia ja ha carregat), es resol IMMEDIATAMENT sense esperar la
     * navegació que el clic està a punt de disparar. Per això aquí NO val
     * `Promise.all([this.page.waitForLoadState('load'), click])` (sempre
     * guanyaria la primera promesa, ja resolta): cal `page.waitForEvent
     * ('load')`, que sí registra un listener real per al PRÒXIM esdeveniment.
     */
    async filtrar(opcions: { dataInici?: string; dataFi?: string; estat?: string } = {}): Promise<void> {
        const form = this.page.locator('#form-filtre');
        await expect(form).toBeVisible();
        if (opcions.dataInici !== undefined) {
            await form.locator('#dataInici').clear();
            await form.locator('#dataInici').pressSequentially(opcions.dataInici);
            await form.locator('#dataInici').press('Escape');
        }
        if (opcions.dataFi !== undefined) {
            await form.locator('#dataFi').clear();
            await form.locator('#dataFi').pressSequentially(opcions.dataFi);
            await form.locator('#dataFi').press('Escape');
        }
        if (opcions.estat !== undefined) {
            await form.locator('#estat').selectOption(opcions.estat, { force: true });
        }
        await Promise.all([
            this.page.waitForEvent('load'),
            form.locator('#filtrar').click(),
        ]);
    }

    /** Taula de resultats (import/estadistiquesTaula.jsp), si n'hi ha. */
    taula() {
        return this.page.locator('table#estadistiques');
    }

    /**
     * Llegeix els totals de la fila `<tfoot>` de la taula d'estadístiques:
     * [Recobriment Ok, Web Ok, Recobriment Error, Web Error, Total].
     * Retorna null si la taula no és present (p.ex. filtre sense resultats).
     */
    async totals(): Promise<{ recobrimentOk: number; webOk: number; recobrimentError: number; webError: number; total: number } | null> {
        const taula = this.taula();
        if ((await taula.count()) === 0 || !(await taula.isVisible())) {
            return null;
        }
        const cells = taula.locator('tfoot th');
        const texts = await cells.allInnerTexts();
        // [0]="" [1]="Total" [2]=recOk [3]=webOk [4]=recError [5]=webError [6]=grandTotal
        const num = (s: string) => Number((s || '0').trim() || '0');
        return {
            recobrimentOk: num(texts[2]),
            webOk: num(texts[3]),
            recobrimentError: num(texts[4]),
            webError: num(texts[5]),
            total: num(texts[6]),
        };
    }
}
