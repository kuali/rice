package edu.samplu.krad.labs.lookups;

import edu.samplu.common.SmokeTestBase;
import org.junit.Test;
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public class DemoLabsLookupHeadingsSmokeTest extends SmokeTestBase {

    /**
     * /kr-krad/lookup?methodToCall=start&viewId=LabsLookup-HeadingsView&hideReturnLink=true
     */
    public static final String BOOKMARK_URL = "/kr-krad/lookup?methodToCall=start&viewId=LabsLookup-HeadingsView&hideReturnLink=true";

        
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickByLinkText("Lookups");
        waitAndClickByLinkText("Lookup Headings");
    }

    @Test
    public void testLabsLookupHeadingsBookmark() throws Exception {
        testLabsLookupHeadings();
        passed();
    }

    @Test
    public void testLabsLookupHeadingsNav() throws Exception {
        testLabsLookupHeadings();
        passed();
    }
    
    protected void testLabsLookupHeadings()throws Exception {
        assertElementPresentByXpath("//div[@class='uif-horizontalBoxGroup uif-header-upperGroup']/div[2]/span");
        assertElementPresentByXpath("//div[@class='uif-horizontalBoxGroup uif-header-lowerGroup']/div[2]/span");
        assertElementPresentByXpath("//div[@class='uif-horizontalBoxGroup uif-header-rightGroup']/div[2]/span");
    }//button[contains(text(), '" + buttonText + "')]
}
