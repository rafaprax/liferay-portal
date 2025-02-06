import { mergeTests, expect } from "@playwright/test";
import { loginTest } from "../../fixtures/loginTest";
import { MultiFactorAuthenticationConfigurationPage } from "../../pages/multi-factor-authentication/MultiFactorAuthenticationConfigurationPage";
import { TimeBasedOneTimePasswordConfigurationPage } from "../../pages/multi-factor-authentication/TimeBasedOneTimePasswordConfigurationPage";
import { AccountSettingsPage } from "../../pages/users-admin-web/AccountSettingsPage";
import { HomePage } from "../../pages/portal-web/HomePage";


export const test = mergeTests(
    loginTest()
)

test('LPD-48214 verify that qr code is visible', async ({page}) => {
    const multiFactorAuthPage = new MultiFactorAuthenticationConfigurationPage(page);

    const timeBasedOTPPage = new TimeBasedOneTimePasswordConfigurationPage(page);

    const accountSettingsPage = new AccountSettingsPage(page);

    const homePage = new HomePage(page);

    await multiFactorAuthPage.goTo();

    await multiFactorAuthPage.enable();

    await timeBasedOTPPage.goTo();

    await timeBasedOTPPage.enable();

    await homePage.goto();

    await accountSettingsPage.goToMultiFactorAuthenticationSettings();

    await page.waitForTimeout(500);

    await expect(await page.getByAltText('otp-configuration-qrcode')).toBeVisible();

    await timeBasedOTPPage.goTo();

    await timeBasedOTPPage.disable();

    await multiFactorAuthPage.goTo();

    await multiFactorAuthPage.disable();
});