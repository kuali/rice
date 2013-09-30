package edu.samplu.krad.labs.lookups;


import org.junit.Test;

import edu.samplu.common.SmokeTestBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public class DemoLabsLookupResultsLimitSmokeTest extends SmokeTestBase {

    /**
     * /kr-krad/lookup?methodToCall=start&viewId=LabsLookup-ResultsLimitView&hideReturnLink=true
     */
    public static final String BOOKMARK_URL = "/kr-krad/lookup?methodToCall=start&viewId=LabsLookup-ResultsLimitView&hideReturnLink=true";
    
    @Override
    protected String getBookmarkUrl() {
        return BOOKMARK_URL;
    }

    @Override
    protected void navigate() throws Exception {
        waitAndClickByLinkText("Lookups");
        waitAndClickByLinkText("Lookup Results Limit");
    }

    @Test
    public void testLabsLookupResultsLimitBookmark() throws Exception {
        testLabsLookupResultsLimit();
        passed();
    }

    @Test
    public void testLabsLookupResultsLimitNav() throws Exception {
        testLabsLookupResultsLimit();
        passed();
    }
    
    protected void testLabsLookupResultsLimit()throws Exception {
        waitAndTypeByName("lookupCriteria[number]","a*");
        waitAndClickButtonByText("Search");
        Thread.sleep(3000);
        assertTextPresent("TRAVEL ACCOUNT 14");
        assertTextPresent("a14");
        assertElementPresentByXpath("//table[@class='table table-condensed table-bordered uif-tableCollectionLayout dataTable']/tbody/tr[2]");
        if(isElementPresentByXpath("//table[@class='table table-condensed table-bordered uif-tableCollectionLayout dataTable']/tbody/tr[3]"))
        {
            fail("More than 2 results available");
        }
        waitAndClickButtonByText("Clear Values");
        waitAndClickButtonByText("Search");
        Thread.sleep(3000);
        assertTextPresent("Travel Account 1");
        assertTextPresent("a1");
        assertElementPresentByXpath("//table[@class='table table-condensed table-bordered uif-tableCollectionLayout dataTable']/tbody/tr[2]");
        if(isElementPresentByXpath("//table[@class='table table-condensed table-bordered uif-tableCollectionLayout dataTable']/tbody/tr[3]"))
        {
            fail("More than 2 results available");
        }
    }
}
