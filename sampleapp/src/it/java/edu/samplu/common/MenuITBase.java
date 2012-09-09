package edu.samplu.common;

/**
 * @deprecated Use WebDriverITBase for new tests.
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public abstract class MenuITBase extends UpgradedSeleniumITBase {
    @Override
    public String getTestUrl() {
        return PORTAL;
    }

    /**
     * Override to return menu click selector (e.g. "link=Main Menu")
     * @return selenium locator to click on
     */
    protected abstract String getMenuLinkLocator();

    /**
     * Override to return main menu click selector (e.g. "link=Agenda lookup")
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
        ITUtil.waitAndClick(selenium, getMenuLinkLocator(), message);
        waitForTitleToEqualKualiPortalIndex();
        ITUtil.waitAndClick(selenium, getLinkLocator(), message);
        waitForTitleToEqualKualiPortalIndex(message);
        selenium.selectFrame("iframeportlet");

        ITUtil.checkForIncidentReport(selenium, getLinkLocator(), message);
    }

    protected void gotoMenuLinkLocator() throws Exception {
        gotoMenuLinkLocator("");
    }
    /**
     * go to having clicked create new of the getLinkLocator()
     */
    protected void gotoCreateNew() throws Exception {
        gotoMenuLinkLocator();
        ITUtil.waitAndClick(selenium,getCreateNewLinkLocator());
        //        selenium.selectFrame("relative=up");
        ITUtil.checkForIncidentReport(selenium, getCreateNewLinkLocator());
    }
}
