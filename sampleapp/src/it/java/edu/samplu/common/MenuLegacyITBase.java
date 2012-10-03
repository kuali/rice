package edu.samplu.common;

import org.openqa.selenium.By;

/**
 * @deprecated use WebDriverITBase
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public abstract class MenuLegacyITBase extends WebDriverLegacyITBase {
    @Override
    public String getTestUrl() {
        return ITUtil.PORTAL;
    }

    /**
     * TODO when the UpgradedSelenium tests have been converted over, rename this to getMenuLinkText
     * Override to return menu click selector (e.g. "Main Menu")
     * @return selenium locator to click on
     */
    protected abstract String getMenuLinkLocator();

    /**
     * TODO when the UpgradedSelenium tests have been converted over, rename this to getLinkText
     * Override to return main menu click selector (e.g. "Agenda lookup")
     * @return selenium locator to click on
     */
    protected abstract String getLinkLocator();


    /**
     * Override to return main menu click selector (e.g. "//img[@alt='create new']")
     * @return selenium locator to click on
     */
    protected abstract String getCreateNewLinkLocator();

    /**
     * go to the getMenuLinkLocator() Menu and click the getLinkLocator()
     */
    protected void gotoMenuLinkLocator(String message) throws Exception {
        waitForTitleToEqualKualiPortalIndex();
        waitAndClickByLinkText(getMenuLinkLocator(), message);
        waitForTitleToEqualKualiPortalIndex();
        waitAndClickByLinkText(getLinkLocator(), message);
        waitForTitleToEqualKualiPortalIndex(message);
        selectFrame("iframeportlet");
        checkForIncidentReport(getLinkLocator(), message);
    }

    protected void gotoMenuLinkLocator() throws Exception {
        gotoMenuLinkLocator("");
    }
    /**
     * go to having clicked create new of the getLinkLocator()
     */
    protected void gotoCreateNew() throws Exception {
        gotoMenuLinkLocator();
        waitAndClick(By.xpath(getCreateNewLinkLocator()));
        //        selectFrame("relative=up");
        checkForIncidentReport(getCreateNewLinkLocator());
    }
}
