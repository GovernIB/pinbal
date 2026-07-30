import { Locator, Page, expect } from '@playwright/test';

/**
 * Pantalla de "Propietats de configuració" (/config, secció administrador).
 *
 * A diferència de la resta de manteniments JSP (vegeu AdminMaintenancePage),
 * aquesta pantalla NO és un llistat amb DataTable + modal: cada propietat es
 * un mini-formulari inline (agrupades per pestanyes, una per grup de
 * configuració) que es desa via AJAX des del JS propi de config.jsp
 * (`$(".form-update-config").submit(...)`), sense recarregar la pàgina.
 *
 * Coses importants determinades llegint config.jsp / configGroup.jsp /
 * ConfigController / ConfigServiceImpl:
 *  - El `id` del input/checkbox de valor és sempre `config_<key>` (amb la
 *    clau tal qual, punts inclosos), i el `key` pot contenir punts, per això
 *    totes les cerques es fan amb selectors d'atribut `[id="..."]` en lloc
 *    d'ids CSS (que interpretarien el punt com a separador de classe).
 *  - El contenidor `#config_<key>_key` sempre mostra la clau tècnica en cru
 *    (`${config.key}`), independentment de si `config.descriptionKey` es pot
 *    resoldre com a missatge o no; s'utilitza doncs com a localitzador
 *    fiable de "aquesta és la propietat X" sense dependre de traduccions.
 *  - Només es mostra el botó de guardar (`button.btn-success`) quan
 *    `config.sourceProperty == 'DATABASE'`; en cas contrari l'input surt
 *    `disabled` i no hi ha manera de desar-lo — així és com la UI implementa
 *    "propietats editables vs no editables".
 *  - Els valors de tipus PASS es sobreescriuen sempre amb la cadena "*****"
 *    al servidor (`ConfigServiceImpl.processPropertyValues`), abans de
 *    muntar l'DTO que arriba a la vista: el valor real mai surt del servidor
 *    per a aquest tipus.
 *  - El missatge d'èxit/error apareix en un div `#config_<key>_msg` propi de
 *    cada propietat (NO al `#contingut-missatges` global) i s'autoelimina al
 *    cap d'uns 2.25s (èxit) / 4.25s (error) — cal comprovar-lo just després
 *    de rebre la resposta AJAX, no confiar en un `waitFor` llarg.
 */
export class PropietatsPage {
    constructor(private readonly page: Page) {}

    async goto(): Promise<void> {
        await this.page.goto('config');
        // Només hi ha un grup ("GENERAL") a les dades de prova e2e, i
        // config.jsp mostra automàticament la primera pestanya
        // ($('.a-config-group:first').tab('show')), per això no cal
        // seleccionar-la explícitament.
        await expect(this.page.locator('.tab-pane.active')).toBeVisible({ timeout: 15_000 });
    }

    /** Contenidor que mostra sempre la clau tècnica de la propietat (fiable independentment de traduccions). */
    keyLabel(key: string): Locator {
        return this.page.locator(`[id="config_${key}_key"]`);
    }

    /** Input (text/number/password) o checkbox (bool) del valor de la propietat. */
    valueInput(key: string): Locator {
        return this.page.locator(`[id="config_${key}"]`);
    }

    /** Formulari AJAX (un per propietat) que conté l'input donat. */
    private formFor(key: string): Locator {
        return this.page.locator('form.form-update-config').filter({ has: this.valueInput(key) });
    }

    /** Botó de guardar de la propietat; només present si la propietat és editable (source_property=DATABASE). */
    saveButton(key: string): Locator {
        return this.formFor(key).locator('button.btn-success');
    }

    /** Div de missatge d'èxit/error que apareix (i desapareix sol al cap de pocs segons) després de desar. */
    inlineMessage(key: string): Locator {
        return this.page.locator(`[id="config_${key}_msg"]`);
    }

    /** Fa clic al botó de guardar i espera la resposta AJAX de `/config/update`. */
    private async submitAndWait(key: string): Promise<void> {
        const responsePromise = this.page.waitForResponse(
            (resp) => resp.url().includes('/config/update') && resp.request().method() === 'POST',
            { timeout: 15_000 },
        );
        await this.saveButton(key).click();
        await responsePromise;
    }

    /** Desa un nou valor de text/number per a una propietat editable i n'espera el missatge de resultat. */
    async saveTextValue(key: string, value: string): Promise<void> {
        await this.valueInput(key).fill(value);
        await this.submitAndWait(key);
    }

    /** Marca/desmarca el checkbox d'una propietat booleana i en desa el nou valor. */
    async setBoolValue(key: string, checked: boolean): Promise<void> {
        const input = this.valueInput(key);
        if ((await input.isChecked()) !== checked) {
            await input.click();
        }
        await this.submitAndWait(key);
    }
}
