import { expect, test } from '@playwright/test';
import { login, logout } from '../utils/auth';
import { credentials, requireCredentials } from '../utils/env';

test.describe('Autenticació', () => {
    test('un usuari pot iniciar sessió i el menú principal es mostra', async ({ page }) => {
        const creds = requireCredentials(credentials.delegat, 'delegat');

        await login(page, creds);

        await expect(page.locator('#menu_user')).toBeVisible();
    });

    test('un usuari pot tancar sessió i torna a la pantalla de login', async ({ page }) => {
        const creds = requireCredentials(credentials.delegat, 'delegat');

        await login(page, creds);
        await logout(page);

        await expect(page.locator('#username')).toBeVisible();
        await expect(page.locator('#menu_user')).toHaveCount(0);
    });

    test('credencials incorrectes no donen accés a l\'aplicació', async ({ page }) => {
        await page.goto('/');
        await expect(page.locator('#username')).toBeVisible({ timeout: 15_000 });

        await page.locator('#username').fill('usuari_e2e_inexistent');
        await page.locator('#password').fill('contrasenya-incorrecta');
        await page.locator('#kc-login').click();

        // Keycloak manté l'usuari al formulari de login i no arriba a #menu_user
        await expect(page.locator('#username')).toBeVisible({ timeout: 15_000 });
        await expect(page.locator('#menu_user')).toHaveCount(0);
    });

    test('accedir a una pàgina protegida sense sessió redirigeix al login', async ({ page }) => {
        await page.goto('/entitat');

        await expect(page.locator('#username')).toBeVisible({ timeout: 15_000 });
    });
});
