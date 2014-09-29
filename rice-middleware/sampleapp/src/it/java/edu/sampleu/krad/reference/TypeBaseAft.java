package edu.sampleu.krad.reference;

import org.junit.Test;
import org.kuali.rice.testtools.selenium.WebDriverLegacyITBase;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

public abstract class TypeBaseAft extends WebDriverLegacyITBase {

    protected abstract String[][] getData();

    //Code for KRAD Test Package.
    protected void testEntityType() throws Exception {
        selectFrameIframePortlet();
        waitAndClickClearValues();

        //Search by "Both" Filter in Active Indicator
        clickSearch();
        assertTextPresent(getData());
        waitAndClickClearValues();

        //Search by "Yes" Filter in Active Indicator
        waitAndClickByXpath("//input[@name='lookupCriteria[active]' and @value='Y']");
        clickSearch();
        assertTextPresent(getData());
        waitAndClickClearValues();

        //Search by "No" Filter in Active Indicator
        waitAndClickByXpath("//input[@name='lookupCriteria[active]' and @value='N']");
        clickSearch();
        waitForTextPresent("No values match this search.");
        waitAndClickClearValues();

        //Search by Code Filter
        waitAndTypeByName("lookupCriteria[code]",getData()[0][0]);
        clickSearch();
        assertTextPresent(getData()[0]);
        waitAndClickClearValues();

        //Search by Name Filter
        waitAndTypeByName("lookupCriteria[name]",getData()[0][1]);
        clickSearch();
        assertTextPresent(getData()[0]);
        waitAndClickClearValues();
    }

    protected void clickSearch() throws InterruptedException {
        waitAndClickSearchByText();
        waitForProgressLoading();
    }

    @Test
    public void testTypeBookmark() throws Exception {
        testEntityType();
        passed();
    }

    @Test
    public void testTypeNav() throws Exception {
        testEntityType();
        passed();
    }

}
