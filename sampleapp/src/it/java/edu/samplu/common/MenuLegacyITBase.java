package edu.samplu.common;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public abstract class MenuLegacyITBase extends WebDriverLegacyITBase {
    @Override
    public String getTestUrl() {
        return ITUtil.PORTAL;
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
        waitAndClick(getMenuLinkLocator(), message);
        waitForTitleToEqualKualiPortalIndex();
        waitAndClick(getLinkLocator(), message);
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
        waitAndClick(getCreateNewLinkLocator(), "");
        //        selectFrame("relative=up");
        checkForIncidentReport(getCreateNewLinkLocator());
    }
}
