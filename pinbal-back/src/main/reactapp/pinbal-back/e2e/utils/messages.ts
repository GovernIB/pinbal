import { Locator, Page, expect } from '@playwright/test';

/**
 * Els missatges d'èxit/error/avís de l'aplicació es renderitzen a
 * #contingut-missatges (tag <pbl:missatges/>), tant en la càrrega inicial
 * d'una pàgina com, després de tancar una modal amb èxit, via una petició
 * AJAX a /index/missatges (webutilRefreshMissatges()).
 */

export function successMessage(page: Page): Locator {
    return page.locator('#contingut-missatges .alert-success');
}

export function errorMessage(page: Page): Locator {
    return page.locator('#contingut-missatges .alert-danger');
}

export async function expectSuccessMessage(page: Page): Promise<void> {
    await expect(successMessage(page).first()).toBeVisible({ timeout: 15_000 });
}
