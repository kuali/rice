package edu.samplu.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

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
        // TODO extract and generalize of reuse
        String contents = selenium.getHtmlSource();
        if (contents.contains("Incident Report")) {
            String chunk =  contents.substring(contents.indexOf("Incident Feedback"), contents.lastIndexOf("</div>") );
            String docId = chunk.substring(chunk.lastIndexOf("Document Id"), chunk.indexOf("View Id"));
            docId = docId.substring(0, docId.indexOf("</span>"));
            docId = docId.substring(docId.lastIndexOf(">") + 2, docId.length());

            String viewId = chunk.substring(chunk.lastIndexOf("View Id"), chunk.indexOf("Error Message"));
            viewId = viewId.substring(0, viewId.indexOf("</span>"));
            viewId = viewId.substring(viewId.lastIndexOf(">") + 2, viewId.length());

            String stackTrace = chunk.substring(chunk.lastIndexOf("(only in dev mode)"), chunk.length());
            stackTrace = stackTrace.substring(stackTrace.indexOf("<span id=\"") + 3, stackTrace.length());
            stackTrace = stackTrace.substring(stackTrace.indexOf("\">") + 2, stackTrace.indexOf("</span>"));

//            System.out.println(docId);
//            System.out.println(viewId);
//            System.out.println(stackTrace);
            fail(viewId.trim()  + " " + docId.trim() + " " + " " + stackTrace.trim());
        }
//        assertFalse(selenium.isElementPresent("//button[contains(.,'eport')]\""));
//        assertFalse(selenium.isElementPresent("//span[contains(.,'Incident')]\""));
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
