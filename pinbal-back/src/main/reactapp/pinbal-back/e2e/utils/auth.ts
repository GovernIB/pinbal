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
 */
export async function login(page: Page, { username, password }: Credentials): Promise<void> {
    await page.goto('/');

    const usernameField = page.locator('#username');
    const alreadyLoggedIn = await Promise.race([
        usernameField.waitFor({ state: 'visible', timeout: 15_000 }).then(() => false),
        page.locator('#menu_user').waitFor({ state: 'visible', timeout: 15_000 }).then(() => true),
    ]).catch(() => false);

    if (!alreadyLoggedIn) {
        await usernameField.fill(username);
        await page.locator('#password').fill(password);
        await page.locator('#kc-login').click();
    }

    await expect(page.locator('#menu_user')).toBeVisible({ timeout: 15_000 });
}

/** Tanca la sessió actual mitjançant l'opció "Desconnectar" del menú d'usuari. */
export async function logout(page: Page): Promise<void> {
    await page.locator('#menu_user').click();
    await page.locator('#menu_user_logout').click();
    await expect(page.locator('#username')).toBeVisible({ timeout: 15_000 });
}
