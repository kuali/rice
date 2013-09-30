package edu.samplu.krad.labs.lookups;

import edu.samplu.common.SmokeTestBase;
import org.junit.Test;
import org.openqa.selenium.By;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public class DemoLabsLookupDefaultSmokeTest extends SmokeTestBase {

    /**
     * /kr-krad/lookup?methodToCall=start&viewId=LabsLookup-DefaultView&hideReturnLink=true
     */
    public static final String BOOKMARK_URL = "/kr-krad/lookup?methodToCall=start&viewId=LabsLookup-DefaultView&hideReturnLink=true";
    
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickByLinkText("Lookups");
        waitAndClickByLinkText("Lookup Default");
    }

    @Test
    public void testLabsLookupDefaultBookmark() throws Exception {
        testLabsLookupDefault();
        passed();
    }

    @Test
    public void testLabsLookupDefaultNav() throws Exception {
        testLabsLookupDefault();
        passed();
    }
    
    protected void testLabsLookupDefault()throws Exception {
        waitAndTypeByName("lookupCriteria[number]","a1*");
        waitAndTypeByName("lookupCriteria[name]","Travel *");
        waitAndClickButtonByText("Search");
        Thread.sleep(3000);
        assertTextPresent("TRAVEL ACCOUNT 14");
        assertTextPresent("a14");
        waitAndClickButtonByText("Clear Values");
        waitAndClickButtonByText("Search");
        Thread.sleep(3000);
        assertTextPresent("Travel Account 1");
        assertTextPresent("a1");
    }
}
