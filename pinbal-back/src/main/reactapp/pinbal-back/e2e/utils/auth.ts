import { Page, expect } from '@playwright/test';
import { Credentials } from './env';

/**
 * PINBAL delega tota l'autenticació a Keycloak (auth-method KEYCLOAK al
 * web.xml de pinbal-back): en navegar a qualsevol pàgina de l'aplicació
 * sense sessió, JBoss redirigeix el navegador a la pàgina de login de
 * Keycloak, que (amb el tema per defecte) exposa els camps #username /
 * #password i el botó #kc-login.
 *
 * Inicia sessió i espera que la pàgina principal de PINBAL s'hagi carregat
 * (identificada pel menú d'usuari #menu_user, sempre present un cop
 * autenticat, sigui quin sigui el rol).
 *
 * IMPORTANT: cada fixture de rol (`adminPage`/`delegatPage`/etc., vegeu
 * fixtures.ts) crea el seu propi `BrowserContext` aïllat abans de cridar
 * `login()`, precisament perquè aquesta funció NO intenta gestionar un
 * canvi d'identitat sobre una sessió ja existent (fer-ho requeriria passar
 * per `logout()`, que xoca amb un bug real de l'aplicació — vegeu el seu
 * comentari). Si `login()` es crida sobre una `page` que ja té sessió
 * d'un ALTRE usuari, el comportament no està definit; feis-la servir només
 * amb una `page`/context nova.
 */
export async function login(page: Page, { username, password }: Credentials): Promise<void> {
    await page.goto('');

    const usernameField = page.locator('#username');
    const alreadyLoggedIn = await Promise.race([
        usernameField.waitFor({ state: 'visible', timeout: 15_000 }).then(() => false),
        page.locator('#menu_user').waitFor({ state: 'visible', timeout: 15_000 }).then(() => true),
    ]).catch(() => false);

    if (!alreadyLoggedIn) {
        await usernameField.fill(username);
        await page.locator('#password').fill(password);
        // El clic dispara un POST real cap a un Keycloak extern (no un mock local) seguit
        // de la validació/creació de sessió a l'aplicació (EJB + BD): sota càrrega pot trigar
        // més que el actionTimeout per defecte (10s) configurat per a interaccions normals
        // dins l'aplicació, per això s'utilitza aquí un timeout més generós.
        await page.locator('#kc-login').click({ timeout: 30_000 });
    }

    await expect(page.locator('#menu_user')).toBeVisible({ timeout: 30_000 });
}

/**
 * `UsuariController.logout()` feia un logout incorrecte contra Keycloak i deixava l'adaptador amb
 * l'"state" OIDC corromput, provocant un "Bad Request" al següent login (vegeu BUGS_APLICACIO.md per
 * al detall del bug ja corregit). Useu aquesta funció només quan el test verifiqui explícitament el
 * comportament de tancar sessió, no com a mecanisme per canviar d'usuari dins un test (per això les
 * fixtures de rol no la fan servir).
 */

/** Tanca la sessió actual mitjançant l'opció "Desconnectar" del menú d'usuari. */
export async function logout(page: Page): Promise<void> {
    await page.locator('#menu_user').click();
    await page.locator('#menu_user_logout').click();
    await expect(page.locator('#username')).toBeVisible({ timeout: 15_000 });
}
