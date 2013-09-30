package edu.samplu.krad.labs.lookups;

import edu.samplu.common.SmokeTestBase;
import org.junit.Test;
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public class DemoLabsLookupWithoutSearchButtonSmokeTest extends SmokeTestBase {

    /**
     * /kr-krad/lookup?methodToCall=start&viewId=LabsLookup-NoSearchButtonsView&hideReturnLink=true
     */
    public static final String BOOKMARK_URL = "/kr-krad/lookup?methodToCall=start&viewId=LabsLookup-NoSearchButtonsView&hideReturnLink=true";
        
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickByLinkText("Lookups");
        waitAndClickByLinkText("Lookup without Search Buttons");
    }

    @Test
    public void testLabsLookupWithoutSearchButtonBookmark() throws Exception {
        testLabsLookupWithoutSearchButton();
        passed();
    }

    @Test
    public void testLabsLookupWithoutSearchButtonNav() throws Exception {
        testLabsLookupWithoutSearchButton();
        passed();
    }
    
    protected void testLabsLookupWithoutSearchButton()throws Exception {
        if(isElementPresentByXpath("//button[contains(text(), 'Search')]"))
        {
            fail("Search Button Present");
        }
    }
}
