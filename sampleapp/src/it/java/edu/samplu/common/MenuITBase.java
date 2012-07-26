package edu.samplu.common;

import static org.junit.Assert.assertEquals;

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
    protected void gotoMenuLinkLocator() {
        assertEquals("Kuali Portal Index", selenium.getTitle());
        selenium.click(getMenuLinkLocator());
        selenium.waitForPageToLoad("30000");
        assertEquals("Kuali Portal Index", selenium.getTitle());
        selenium.click(getLinkLocator());
        selenium.waitForPageToLoad("30000");
        assertEquals("Kuali Portal Index", selenium.getTitle());
        selenium.selectFrame("iframeportlet");
    }

    /**
     * go to having clicked create new of the getLinkLocator()
     */
    protected void gotoCreateNew() {
        gotoMenuLinkLocator();
        selenium.click(getCreateNewLinkLocator());
        //        selenium.selectFrame("relative=up");
        selenium.waitForPageToLoad("30000");
    }
}
