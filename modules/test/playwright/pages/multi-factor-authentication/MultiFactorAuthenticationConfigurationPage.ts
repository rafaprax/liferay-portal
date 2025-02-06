import { Locator, Page, expect} from "@playwright/test";
import { InstanceSettingsPage } from "../configuration-admin-web/InstanceSettingsPage";
import { reloadUntilVisible } from "../../utils/reloadUntilVisible";


export class MultiFactorAuthenticationConfigurationPage {
    readonly enabledCheckBox: Locator;
    readonly saveButton: Locator;
    readonly page: Page;
    readonly instanceSettingsPage: InstanceSettingsPage;
    readonly updateButton: Locator;
    readonly successMessage: Locator;

    constructor(page: Page) {
        this.page = page;
        this.enabledCheckBox = this.page.getByText('Enabled');
        this.saveButton = this.page.getByRole('button', {name: 'Save'});
        this.instanceSettingsPage = new InstanceSettingsPage(page);
        this.updateButton = this.page.getByRole('button', {name: 'Update'});
        this.successMessage = page.getByText(
			'Your request completed successfully'
		);
    }   

    async goTo() {
        await this.instanceSettingsPage.goToInstanceSetting('Multi-Factor Authentication', 'Multi-Factor Authentication and Email One-Time Password Configuration');
        
        await this.enabledCheckBox.waitFor();
    }

    async enable() {
        await this.enabledCheckBox.waitFor();

        await this.enabledCheckBox.check();

        if (await this.page.isVisible('button:has-text("Update")')) {
                await this.updateButton.click();
        }else{
            await this.saveButton.click();
        }

        await this.page.waitForTimeout(500);
    }

    async disable() {
        await this.enabledCheckBox.waitFor();

        await this.enabledCheckBox.uncheck();

        if (await this.page.isVisible('button:has-text("Update")')) {
                await this.updateButton.click();
        }else{
            await this.saveButton.click();
        }

        await this.page.waitForTimeout(500);
    }



}